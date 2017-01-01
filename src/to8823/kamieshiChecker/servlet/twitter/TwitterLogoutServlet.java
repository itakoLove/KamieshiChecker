package to8823.kamieshiChecker.servlet.twitter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import to8823.kamieshiChecker.util.Constants;

public class TwitterLogoutServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();

		session.removeAttribute(Constants.SESSION_ACCESS_TOKEN);

		resp.sendRedirect("/");
	}
}
