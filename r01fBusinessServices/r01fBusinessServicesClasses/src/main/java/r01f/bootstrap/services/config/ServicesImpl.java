package r01f.bootstrap.services.config;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import r01f.util.enums.Enums;
import r01f.util.enums.Enums.EnumWrapper;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

public enum ServicesImpl {
	Default,		// Priority 0 (highest)
	Bean,			// Priority 1
	REST,			// Priority 2
	EJB,			// Priority 3
	Servlet,		// Priority 4 (it's NOT a full-fledged service since it's NOT consumed using a client api; it's called from a web browser so it has NO associated client-proxy)
	Mock,			// Priority 5 (lower)
	NULL;			// used at ServicesCore annotation
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static EnumWrapper<ServicesImpl> WRAPPER = Enums.of(ServicesImpl.class);
	
	public static ServicesImpl fromName(final String name) {
		return WRAPPER.fromName(name);
	}
	public static ServicesImpl fromNameOrNull(final String name) {
		return WRAPPER.fromName(name);
	}
	public static Collection<ServicesImpl> fromNames(final String... names) {
		if (CollectionUtils.isNullOrEmpty(names)) return null;
		Collection<ServicesImpl> outImpls = Lists.newArrayListWithExpectedSize(names.length);
		for (String name : names) {
			if (Strings.isNullOrEmpty(name)) continue;
			outImpls.add(ServicesImpl.fromName(name));
		}
		return CollectionUtils.hasData(outImpls) ? outImpls : null;
	}
	public static Set<ServicesImpl> asSet() {
		return Sets.newHashSet(ServicesImpl.values());
	}
	public boolean is(final ServicesImpl other) {
		return WRAPPER.is(this,other);
	}
	public boolean isIn(final ServicesImpl... other) {
		return WRAPPER.isIn(this,other);
	}
	public boolean isNOT(final ServicesImpl other) {
		return !WRAPPER.is(this,other);
	}

}
