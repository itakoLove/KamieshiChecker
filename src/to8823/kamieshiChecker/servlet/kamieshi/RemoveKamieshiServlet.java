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
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class RemoveKamieshiServlet extends HttpServlet {
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

			try {
				// 登録されているかどうか確認する
				if (!Kamieshi.hasKamieshi(ds, twitter, screenName)) {
					out.print("{\"error\":2}");
				} else {
					Kamieshi kamieshi = Kamieshi.getKamieshi(ds, twitter, screenName);
					ds.delete(kamieshi.getKey());

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
