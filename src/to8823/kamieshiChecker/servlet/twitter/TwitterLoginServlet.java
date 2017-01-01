package to8823.kamieshiChecker.servlet.twitter;

import java.io.IOException;
import java.util.logging.Logger;

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
import twitter4j.auth.RequestToken;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class TwitterLoginServlet extends HttpServlet {
	BlobstoreService bs = BlobstoreServiceFactory.getBlobstoreService();

	TwitterFactory twitterFactory = new TwitterFactory();

	private Logger log = Logger.getLogger(TwitterLoginServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		Twitter twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(PropConstants.CONSUMER_KEY, PropConstants.CONSUMER_SECRET);

		try {
			// リクエストトークンの生成
			RequestToken reqToken = twitter.getOAuthRequestToken();

			// RequestTokenとTwitterオブジェクトをセッションに保存
			HttpSession session = req.getSession();
			session.setAttribute(Constants.SESSION_REQUEST_TOKEN, reqToken);
			session.setAttribute(Constants.SESSION_TWITTER, twitter);

			// ローカルホストでの開発中はテストのアクセストークンを渡す
			if (req.getServerName().equals("localhost")) {
				AccessToken token = new AccessToken(PropConstants.TEST_ACCESS_TOKEN, PropConstants.TEST_ACCESS_TOKEN_SECRET);
				session.setAttribute(Constants.SESSION_ACCESS_TOKEN, token);
			}

			if (session.getAttribute(Constants.SESSION_ACCESS_TOKEN) != null) {
				resp.setContentType("text/html; charset=utf-8");
				resp.sendRedirect("/");
			} else {
				// 認証画面にリダイレクトするためのURLを生成
				String strUrl = reqToken.getAuthorizationURL();
				resp.sendRedirect(strUrl);
			}
		} catch (TwitterException e) {
			log.info(e.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
			resp.sendRedirect("/");
		}

	}
}
