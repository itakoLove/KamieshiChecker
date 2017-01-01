package to8823.kamieshiChecker.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class PropConstants {
	public static String CONSUMER_KEY;
	public static String CONSUMER_SECRET;
	public static String TEST_ACCESS_TOKEN;
	public static String TEST_ACCESS_TOKEN_SECRET;

	static {
		ResourceBundle bundle = null;
		try {
			bundle = ResourceBundle.getBundle("twitter");
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}

		CONSUMER_KEY = bundle.getString("consumerKey");
		CONSUMER_SECRET = bundle.getString("consumerSecret");
		TEST_ACCESS_TOKEN = bundle.getString("testAccessToken");
		TEST_ACCESS_TOKEN_SECRET = bundle.getString("testAccessTokenSecret");
	}
}
