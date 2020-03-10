package r01f.cache;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.guids.OID;

/**
 * A cache key used 
 * @param <O>
 */
@Accessors(prefix="_")
public class CacheEntry<O extends OID,M> 
  implements Serializable {

	private static final long serialVersionUID = -3077860130764155886L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final O _oid;
	@Getter private final M _object;
	@Getter private final Date _cachedAt;
	@Getter private final Date _lastUpdatedAt;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS & BUILDERS                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public CacheEntry(final O oid,final M obj,
					  final Date lastUpdatedAt) {
		_oid = oid;
		_object = obj;
		_cachedAt = new Date();
		_lastUpdatedAt = lastUpdatedAt;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean needsReloadIfLastUpdatedAt(final Date lastUpdatedAt) {
		return lastUpdatedAt.after(_lastUpdatedAt);
	}
	public CacheEntry<O,M> reloadIfLastUpdatedAt(final Date lastUpdatedAt) {
		if (needsReloadIfLastUpdatedAt(lastUpdatedAt)) {
			return new CacheEntry<O,M>(_oid,_object,
										 lastUpdatedAt);
		}
		return this;
	}
}
