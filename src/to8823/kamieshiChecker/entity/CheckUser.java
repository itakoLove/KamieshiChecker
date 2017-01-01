package to8823.kamieshiChecker.entity;

import java.util.Date;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class CheckUser extends AbstractEntity {
	public static final String KIND_NAME = "CheckUser";

	private static final String PROP_DO_NOT_SHOW_OLD_IMAGE_FLG = "DO_NOT_SHOW_OLD_IMAGE_FLG";
	private static final String PROP_CREATE_DATE = "CREATE_DATE";

	protected CheckUser(Entity argEntity) {
		super(argEntity);
	}

	public boolean doNotShowOldImageFlg() {
		return (Boolean) entity.getProperty(PROP_DO_NOT_SHOW_OLD_IMAGE_FLG);
	}

	public void setDoNotShowOldImageFlg(boolean argFlg) {
		entity.setUnindexedProperty(PROP_DO_NOT_SHOW_OLD_IMAGE_FLG, argFlg);
	}

	public Date getCreateDate() {
		return (Date) entity.getProperty(PROP_CREATE_DATE);
	}

	public void setCreateDate(Date date) {
		entity.setProperty(PROP_CREATE_DATE, date);
	}

	public static CheckUser getCheckUser(final DatastoreService ds, final Twitter twitter)
			throws TwitterException {
		CheckUser checkUser = null;
		Key key = KeyFactory.createKey(KIND_NAME, twitter.getId());

		try {
			checkUser = new CheckUser(ds.get(key));
		} catch(EntityNotFoundException e) {
			checkUser = new CheckUser(new Entity(KIND_NAME, twitter.getId()));

			checkUser.setCreateDate(new Date());
			checkUser.setDoNotShowOldImageFlg(false);

			ds.put(checkUser.entity);
		}

		return checkUser;
	}

}
