package to8823.kamieshiChecker.servlet.twitter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import to8823.kamieshiChecker.util.Constants;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterCallbackServlet extends HttpServlet {
	private Logger log = Logger.getLogger(TwitterCallbackServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		HttpSession session = req.getSession();
		Twitter twitter = (Twitter) session.getAttribute(Constants.SESSION_TWITTER);
		String verifier = req.getParameter("oauth_verifier");
		AccessToken accessToken = null;

		try {
			// RequestTokenからAccessTokenを取得
			accessToken = twitter.getOAuthAccessToken((RequestToken) session.getAttribute(Constants.SESSION_REQUEST_TOKEN), verifier);
		} catch (TwitterException e) {
			log.warning(e.getMessage());
		}

		if (accessToken != null) {
			// AccessTokenをセッションに保持
			session.setAttribute(Constants.SESSION_ACCESS_TOKEN, accessToken);
			resp.sendRedirect("/login");
		} else {
			resp.setContentType("text/plain");
			resp.getWriter().println("AccessTokenがNullってます！");
		}
	}
}
