package r01f.cache;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class CacheKeyForLangDependentObjectBase<O extends OID>
  implements Serializable {

	private static final long serialVersionUID = 3656153007909422466L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final O _oid;
	@Getter private final Language _lang;
/////////////////////////////////////////////////////////////////////////////////////////
//	TO STRING / EQUALS / HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Strings.customized("{}:{}",_lang,_oid);
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof CacheKeyForLangDependentObjectBase)) return false;
		CacheKeyForLangDependentObjectBase<?> otherKey = (CacheKeyForLangDependentObjectBase<?>)obj;
		return _oid.is(otherKey.getOid())
			&& _lang.is(otherKey.getLang());
	}
	@Override
	public int hashCode() {
		return Objects.hash(_oid,_lang);
	}

}
