package to8823.kamieshiChecker.servlet.kamieshi;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import to8823.kamieshiChecker.entity.CheckUser;
import to8823.kamieshiChecker.entity.Kamieshi;
import to8823.kamieshiChecker.util.Constants;
import to8823.kamieshiChecker.util.PropConstants;
import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class GetTweetListServlet extends HttpServlet {
	final int TWEET_PER_PAGE = 50;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/json; charset=utf-8");
		PrintWriter out = resp.getWriter();

		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setTweetModeExtended(true);

		TwitterFactory twitterFactory = new TwitterFactory(builder.build());
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		MemcacheService ms = MemcacheServiceFactory.getMemcacheService();

		HttpSession session = req.getSession();

		String continueKey = req.getParameter("key");
		String pageStr = req.getParameter("page");

		Twitter twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(PropConstants.CONSUMER_KEY, PropConstants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken((AccessToken) session.getAttribute(Constants.SESSION_ACCESS_TOKEN));

		if (session.getAttribute(Constants.SESSION_ACCESS_TOKEN) != null) {
			if (continueKey != null && !"".equals(continueKey)) {
				try {
					int page = Integer.parseInt(pageStr);
					String result = (String) ms.get(continueKey + ":" + page);

					if (result != null && !"".equals(result)) {
						out.print("{\"error\":0, \"tweets\":" + result + ", \"key\":\"" + continueKey + "\", \"page\":\"" + page + "\"}");
					} else {
						out.print("{\"error\":2}");
					}
				} catch(NumberFormatException e) {
					out.print("{\"error\":9}");
				}
			} else {
				try {
					CheckUser checkUser = CheckUser.getCheckUser(ds, twitter);

					List<Status> allStatuses = new ArrayList<Status>();

					List<Kamieshi> kamieshis = Kamieshi.getKamieshis(ds, twitter);
					for (Kamieshi kamieshi: kamieshis) {
						allStatuses.addAll(getTweet(twitter, kamieshi));
					}

					if (allStatuses.size() != 0) {
						// ツイートの投稿日の降順にソート
						allStatuses = sort(allStatuses);

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
						sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

						int pageSize = allStatuses.size() % TWEET_PER_PAGE == 0 ?
								allStatuses.size() / TWEET_PER_PAGE :
								allStatuses.size() / TWEET_PER_PAGE + 1;

						continueKey = getRandomString(8);

						for (int i = 0; i < pageSize; i++) {
							StringBuilder result = new StringBuilder();
							StringBuilder imageUrl;
							String message = "";

							result.append("[");

							for(int j = 0; (i * TWEET_PER_PAGE + j) < allStatuses.size() && j < TWEET_PER_PAGE; j++) {
								Status s = allStatuses.get(i * TWEET_PER_PAGE + j);

								message = s.getText();
								imageUrl = new StringBuilder();

								for (int k = 0; k < s.getMediaEntities().length; k++) {
									MediaEntity me = s.getMediaEntities()[k];
									message = message.replace(me.getURL(), "");

									imageUrl.append("\"").append(escape(me.getMediaURL())).append("\"");
									if (k + 1 < s.getMediaEntities().length) {
										imageUrl.append(",");
									}
								}

								message = message.replace("\n", " ");
								message = message.trim();
								if ("".equals(message)) {
									message = "　";
								}

								result.append("{");
								result.append("\"tweet_id\":\"" + s.getId() + "\",");
								result.append("\"screen_name\":\"" + escape(s.getUser().getScreenName()) + "\",");
								result.append("\"profile_name\":\"" + escape(s.getUser().getName()) + "\",");
								result.append("\"mini_profile_image\":\"" + escape(s.getUser().getMiniProfileImageURL()) + "\",");
								result.append("\"original_profile_image\":\"" + escape(s.getUser().getOriginalProfileImageURL()) + "\",");
								result.append("\"tweet_text\":\"" + escape(message) + "\",");
								result.append("\"tweet_create_date\":\"" + escape(sdf.format(s.getCreatedAt())) + "\",");
								result.append("\"tweet_image\":[" + imageUrl.toString() + "],");
								result.append("\"favorite_count\":" + s.getFavoriteCount() + ",");
								result.append("\"retweet_count\":" + s.getRetweetCount());
								result.append("},");
							}
							result.deleteCharAt(result.length()-1);
							result.append("]");

							ms.put(continueKey + ":" + i, result.toString());
						}

						out.print("{\"error\":0, \"tweets\":" + ms.get(continueKey + ":0") + ", \"key\":\"" + continueKey + "\", \"page\":\"0\"}");
					} else {
						out.print("{\"error\":2}");
					}

				} catch (TwitterException e) {
					if (429 == e.getStatusCode()){
						out.print("{\"error\":3}");
					} else {
						out.print("{\"error\":9}");
						e.printStackTrace();
					}
				}
			}

		} else {
			out.print("{\"error\":1}");
		}
	}

	private List<Status> getTweet(Twitter twitter, Kamieshi kamieshi)
		throws TwitterException {
		if (kamieshi.canSearch()) {
			Query q = new Query("from:" + kamieshi.getUserScreenName() + " -RT filter:images");
			QueryResult result = twitter.search(q);

			return result.getTweets();
		} else {
			User user = twitter.showUser("@" + kamieshi.getUserScreenName());
			List<Status> resultList = new ArrayList<Status>();

			Calendar fromCal = Calendar.getInstance();
			fromCal.add(Calendar.DAY_OF_MONTH, -3);

			Date fromDate = fromCal.getTime();

			int page = 1;
			boolean isFinish = false;

			while (true) {
				List<Status> tweetList = twitter.getUserTimeline(user.getId(), new Paging(page));

				for (Status s: tweetList) {
					if (fromDate.after(s.getCreatedAt())) {
						isFinish = true;
						break;
					}
					if (s.getMediaEntities().length != 0 && s.getText().indexOf("RT @") != 0) {
						resultList.add(s);
					}
				}

				if (isFinish) {
					break;
				} else {
					page++;
				}
			}

			return resultList;
		}
	}

	private List<Status> sort(List<Status> argList) {
		List<Status> resultList = new ArrayList<Status>();
		resultList.addAll(argList);

		boolean dirty = false;
		do {
			dirty = false;
			for (int i = 0; i < resultList.size() - 1; i++) {
				if (resultList.get(i).getCreatedAt().before(resultList.get(i + 1).getCreatedAt())) {
					Status s = resultList.get(i + 1);
					resultList.remove(i + 1);
					resultList.add(i, s);
					dirty = true;
				}
			}
		} while(dirty);

		return resultList;
	}

	private String getRandomString(int strLength) {
		final String seed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

		if (strLength <= 0) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strLength; i++) {
			sb.append(seed.charAt((int) Math.floor(Math.random() * seed.length())));
		}

		return sb.toString();
	}

	private String escape(String argStr) {
		return argStr
				.replace("\"", "\\\"")
				.replace("\\", "\\\\");
	}
}
