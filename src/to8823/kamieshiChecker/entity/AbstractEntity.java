package to8823.kamieshiChecker.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;

public abstract class AbstractEntity {
	protected Entity entity;

	protected AbstractEntity(Entity argEntity) {
		this.entity = argEntity;
	}

	public static void save(DatastoreService ds, AbstractEntity... argEntities) {
		List<Entity> entities = new ArrayList<Entity>();
		for (AbstractEntity entity: argEntities) {
			entities.add(entity.entity);
		}
		ds.put(entities);
	}
}
