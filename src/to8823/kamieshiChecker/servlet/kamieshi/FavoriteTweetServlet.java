package to8823.kamieshiChecker.servlet.kamieshi;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import to8823.kamieshiChecker.util.Constants;
import to8823.kamieshiChecker.util.PropConstants;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class FavoriteTweetServlet extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/json; charset=utf-8");
		PrintWriter out = resp.getWriter();

		TwitterFactory twitterFactory = new TwitterFactory();

		HttpSession session = req.getSession();

		Twitter twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(PropConstants.CONSUMER_KEY, PropConstants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken((AccessToken) session.getAttribute(Constants.SESSION_ACCESS_TOKEN));

		if (session.getAttribute(Constants.SESSION_ACCESS_TOKEN) != null) {
			String id = req.getParameter("id");
			try {
				twitter.createFavorite(Long.parseLong(id));
				out.print("{\"error\":0}");
			} catch (NumberFormatException e) {
				out.print("{\"error\":2}");
			} catch (TwitterException e) {
				if (e.getErrorCode() == 139) {
					out.print("{\"error\":3}");
				} else {
					out.print("{\"error\":9}");
					e.printStackTrace();
				}
			} catch (Exception e) {
				out.print("{\"error\":9}");
				e.printStackTrace();
			}
		} else {
			out.print("{\"error\":1}");
		}
	}
}
