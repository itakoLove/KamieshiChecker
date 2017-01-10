package to8823.kamieshiChecker.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class FunctionUtil {
	public static boolean searchableUser(Twitter twitter, String id)
			throws TwitterException {
		Calendar fromCal = Calendar.getInstance();
		fromCal.set(Calendar.DAY_OF_MONTH, -7);
		Date fromDate = fromCal.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Query q = new Query("from:" + id + " -RT");
		q.setSince(sdf.format(fromDate));
		QueryResult searchResult = twitter.search(q);

		if (searchResult.getTweets().size() != 0) {
			return true;
		} else {
			User user = twitter.showUser("@" + id);

			// 鍵付きユーザの場合はTRUEとする
			if (user.isProtected()){
				return true;
			} else {
				List<Status> statusList = twitter.getUserTimeline(user.getId());

				if (!statusList.isEmpty() &&
						fromDate.before( statusList.get(0).getCreatedAt() )) {
					return false;
				} else {
					return true;
				}
			}
		}
	}
}
