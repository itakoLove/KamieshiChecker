package to8823.kamieshiChecker.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class Kamieshi extends AbstractEntity {
	public static final String KIND_NAME = "Kamieshi";

	private static final String PROP_USER_SCREEN_NAME = "USER_SCREEN_NAME";
	private static final String PROP_CREATE_DATE = "CREATE_DATE";
	private static final String PROP_LAST_TWEET_ID = "LAST_TWEET_ID";

	protected Kamieshi(Entity argEntity) {
		super(argEntity);
	}

	public Date getCreateDate() {
		return (Date) entity.getProperty(PROP_CREATE_DATE);
	}

	private void setCreateDate(Date argDate) {
		entity.setProperty(PROP_CREATE_DATE, argDate);
	}

	public String getUserScreenName() {
		return (String) entity.getProperty(PROP_USER_SCREEN_NAME);
	}

	private void setUserScreenName(String argUserScreenName) {
		entity.setProperty(PROP_USER_SCREEN_NAME, argUserScreenName);
	}

	public long getLastTweetId() {
		return (Long) entity.getProperty(PROP_LAST_TWEET_ID);
	}

	public void setLastTweetId(long argId) {
		entity.setUnindexedProperty(PROP_LAST_TWEET_ID, argId);
	}

	public static boolean hasKamieshi(final DatastoreService ds, final Twitter twitter, String argUserScreenName)
			throws TwitterException {
		Query q = new Query(KIND_NAME);

		q.setAncestor(KeyFactory.createKey(CheckUser.KIND_NAME, twitter.getId()));
		q.setFilter(Query.FilterOperator.EQUAL.of(PROP_USER_SCREEN_NAME, argUserScreenName));
		q.setKeysOnly();

		PreparedQuery pq = ds.prepare(q);
		if (pq.countEntities(FetchOptions.Builder.withDefaults()) != 0) {
			return true;
		} else {
			return false;
		}
	}

	public static Kamieshi createKamieshi(final DatastoreService ds, final Twitter twitter, String argUserScreenName)
			throws TwitterException {
		if (twitter.getOAuthAccessToken() != null) {
			Entity entity = new Entity(KIND_NAME, KeyFactory.createKey(CheckUser.KIND_NAME, twitter.getId()));

			Kamieshi kamieshi = new Kamieshi(entity);

			kamieshi.setUserScreenName(argUserScreenName);
			kamieshi.setCreateDate(new Date());

			return kamieshi;
		} else {
			throw new TwitterException("ログインしていません");
		}
	}

	public static List<Kamieshi> getKamieshis(final DatastoreService ds, final Twitter twitter)
			throws TwitterException{
		if (twitter.getOAuthAccessToken() != null) {
			Query q = new Query(KIND_NAME);

			q.setAncestor(KeyFactory.createKey(CheckUser.KIND_NAME, twitter.getId()));
			q.addSort(PROP_CREATE_DATE);

			PreparedQuery pq = ds.prepare(q);

			List<Kamieshi> list = new ArrayList<Kamieshi>();
			for (Entity e : pq.asIterable()) {
				list.add(new Kamieshi(e));
			}

			return list;
		} else {
			throw new TwitterException("ログインしていません");
		}
	}

}
