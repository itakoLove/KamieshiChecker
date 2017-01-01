package to8823.kamieshiChecker.servlet.kamieshi;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import to8823.kamieshiChecker.entity.Kamieshi;
import to8823.kamieshiChecker.util.Constants;
import to8823.kamieshiChecker.util.PropConstants;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class AddKamieshiServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/json; charset=utf-8");
		PrintWriter out = resp.getWriter();

		TwitterFactory twitterFactory = new TwitterFactory();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		HttpSession session = req.getSession();

		Twitter twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(PropConstants.CONSUMER_KEY, PropConstants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken((AccessToken) session.getAttribute(Constants.SESSION_ACCESS_TOKEN));

		if (session.getAttribute(Constants.SESSION_ACCESS_TOKEN) != null) {
			String screenName = req.getParameter("screenName");
			boolean success = false;

			try {
				// Twitter上に存在するか確認する
				String[] search = {screenName};
				ResponseList<User> users = twitter.lookupUsers(search);
				for (User u: users) {
					if (u.getScreenName().equals(screenName)) {
						success = true;
					}
				}
			} catch (TwitterException e) {
				out.print("{\"error\":2}");
			}

			try {
				// すでに登録されているかどうか確認する
				if (Kamieshi.hasKamieshi(ds, twitter, screenName)) {
					out.print("{\"error\":3}");
				}
				else if (success) {
					Kamieshi kamieshi = Kamieshi.createKamieshi(ds, twitter, screenName);
					Kamieshi.save(ds, kamieshi);

					out.print("{\"error\":0}");
				}

			} catch (TwitterException e) {
				out.print("{\"error\":9}");
			}
		} else {
			out.print("{\"error\":1}");
		}
	}
}
