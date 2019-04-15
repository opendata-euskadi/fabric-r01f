package r01f.persistence.db;

import r01f.persistence.db.DBEntity;

public interface DBEntityFactory<DB extends DBEntity> {
	/**
	 * @return a new {@link DBEntity} factory
	 */
	public DB create();
}
