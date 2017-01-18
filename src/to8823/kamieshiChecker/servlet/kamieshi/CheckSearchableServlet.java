package to8823.kamieshiChecker.servlet.kamieshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import to8823.kamieshiChecker.entity.CheckUser;
import to8823.kamieshiChecker.entity.Kamieshi;
import to8823.kamieshiChecker.util.FunctionUtil;
import to8823.kamieshiChecker.util.PropConstants;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class CheckSearchableServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException ,IOException {

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		TwitterFactory twitterFactory = new TwitterFactory();

		Twitter twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(PropConstants.CONSUMER_KEY, PropConstants.CONSUMER_SECRET);

		AccessToken token = new AccessToken(PropConstants.CRON_ACCESS_TOKEN, PropConstants.CRON_ACCESS_TOKEN_SECRET);
		twitter.setOAuthAccessToken(token);

		List<Kamieshi> kamieshis = new ArrayList<Kamieshi>();
		List<CheckUser> checkUsers = CheckUser.getAllCheckUser(ds);

		for (CheckUser checkUser: checkUsers) {
			kamieshis.addAll(Kamieshi.getCheckKamieshi(ds, checkUser.getKey()));
		}

		if (!kamieshis.isEmpty()) {
			kamieshis = sort(kamieshis);
			kamieshis = cut(kamieshis, 50);

			ThreadFactory factory = ThreadManager.currentRequestThreadFactory();
			ExecutorService executor = Executors.newCachedThreadPool(factory);
			for (Kamieshi kamieshi : kamieshis) {
				Runnable checkThread = ThreadManager.createThreadForCurrentRequest(
						new CheckSerchableThread(twitter, kamieshi));
				executor.execute(checkThread);
			}

			executor.shutdown();
			try {
				executor.awaitTermination(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Kamieshi.save(ds, kamieshis.toArray(new Kamieshi[0]));
		}
	}

	private List<Kamieshi> sort(List<Kamieshi> argKamieshis) {
		List<Kamieshi> resultList = new ArrayList<Kamieshi>();
		resultList.addAll(argKamieshis);

		boolean dirty = false;
		do {
			dirty = false;
			for (int i = 0; i < resultList.size() - 1; i++) {
				if (resultList.get(i).getCheckSearchableDate().after(resultList.get(i + 1).getCheckSearchableDate())) {
					Kamieshi s = resultList.get(i + 1);
					resultList.remove(i + 1);
					resultList.add(i, s);
					dirty = true;
				}
			}
		} while(dirty);

		return resultList;
	}

	private List<Kamieshi> cut(List<Kamieshi> argKamieshis, int size) {
		List<Kamieshi> resultList = new ArrayList<Kamieshi>();
		for (int i = 0 ; i < argKamieshis.size() && i < size; i++) {
			resultList.add(argKamieshis.get(i));
		}
		return resultList;
	}
}

class CheckSerchableThread implements Runnable {
	Twitter twitter;
	Kamieshi kamieshi;

	CheckSerchableThread(Twitter argTwitter, Kamieshi argKamieshi) {
		this.twitter = argTwitter;
		this.kamieshi = argKamieshi;
	}

	public void run() {
		try {
			kamieshi.setSearchable( FunctionUtil.searchableUser(twitter, kamieshi.getUserScreenName()) );
			kamieshi.setCheckSearchableDate(new Date());
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
}
