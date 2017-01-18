package to8823.kamieshiChecker.servlet.kamieshi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import to8823.kamieshiChecker.entity.Kamieshi;
import to8823.kamieshiChecker.util.Constants;
import to8823.kamieshiChecker.util.FunctionUtil;
import to8823.kamieshiChecker.util.PropConstants;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class GetKamieshiListServlet extends HttpServlet {
	private final int MAX_THREAD_SIZE = 50;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/json; charset=utf-8");
		PrintWriter out = resp.getWriter();

		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setTweetModeExtended(true);

		TwitterFactory twitterFactory = new TwitterFactory(builder.build());
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		HttpSession session = req.getSession();

		Twitter twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(PropConstants.CONSUMER_KEY, PropConstants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken((AccessToken) session.getAttribute(Constants.SESSION_ACCESS_TOKEN));

		if (session.getAttribute(Constants.SESSION_ACCESS_TOKEN) != null) {
			try {
				List<Kamieshi> kamieshiList = Kamieshi.getKamieshis(ds, twitter);
				Map<String, User> userMap = new HashMap<String, User>();

				ThreadFactory factory = ThreadManager.currentRequestThreadFactory();
				ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_SIZE, factory);

				for (Kamieshi kamieshi: kamieshiList) {
					Runnable getTweetThread = ThreadManager.createThreadForCurrentRequest(
							new GetKamieshiThread(twitter, kamieshi, userMap));
					executor.execute(getTweetThread);
				}

				executor.shutdown();
				executor.awaitTermination(60, TimeUnit.SECONDS);

				StringBuilder result = new StringBuilder();

				result.append("[");
				for (Kamieshi kamieshi: kamieshiList) {
					String screenName = kamieshi.getUserScreenName();
					String profileName = "　";
					String miniProfileUrl = "";

					if (userMap.containsKey(screenName)) {
						profileName = userMap.get(screenName).getName();
						miniProfileUrl = userMap.get(screenName).getMiniProfileImageURL();
					}

					result.append("{");
					result.append("\"screen_name\":\"" + FunctionUtil.escape(screenName) + "\",");
					result.append("\"profile_name\":\"" + FunctionUtil.escape(profileName) + "\",");
					result.append("\"mini_profile_image\":\"" + FunctionUtil.escape(miniProfileUrl) + "\"");
					result.append("},");
				}
				result.deleteCharAt(result.length()-1);
				result.append("]");

				out.print("{\"error\":0, \"kamieshi_list\":" + result.toString() + "}");
			} catch (TwitterException e) {
				if (429 == e.getStatusCode()){
					out.print("{\"error\":2}");
				} else {
					out.print("{\"error\":9}");
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				out.print("{\"error\":9}");
			}
		} else {
			out.print("{\"error\":1}");
		}
	}
}

class GetKamieshiThread implements Runnable {
	Twitter twitter;
	Kamieshi kamieshi;

	Map<String, User> userMap;

	GetKamieshiThread(Twitter argTwitter, Kamieshi argKamieshi,
			Map<String, User> argUserMap) {
		this.twitter = argTwitter;
		this.kamieshi = argKamieshi;
		this.userMap = argUserMap;
	}

	public void run() {
		try {
			User user = twitter.showUser("@" + kamieshi.getUserScreenName());
			userMap.put(kamieshi.getUserScreenName(), user);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

}