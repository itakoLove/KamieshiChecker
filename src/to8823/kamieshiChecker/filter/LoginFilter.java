package to8823.kamieshiChecker.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import to8823.kamieshiChecker.util.Constants;

public class LoginFilter implements Filter {
	HttpSession session;

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		session = ((HttpServletRequest)req).getSession();
		if (session.getAttribute(Constants.SESSION_ACCESS_TOKEN) == null) {
			HttpServletResponse httpRes = ((HttpServletResponse)res);
			httpRes.sendRedirect("/");
		} else {
			chain.doFilter(req, res);
		}
	}

	public void destroy() { }

	public void init(FilterConfig arg0) throws ServletException { }
}
