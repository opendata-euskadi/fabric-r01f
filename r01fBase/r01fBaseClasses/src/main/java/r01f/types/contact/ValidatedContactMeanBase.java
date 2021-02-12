package r01f.types.contact;

import java.util.Collection;

import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.util.types.StringSplitter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;



@Immutable
@NoArgsConstructor @AllArgsConstructor
@Accessors(prefix="_")
abstract class ValidatedContactMeanBase
    implements ValidatedContactMean {

	private static final long serialVersionUID = 7691819813799837148L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _id;

/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return this.asString();
	}
	@Override
	public String asString() {
		return _id != null ? _id.toString() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static <I extends ValidatedContactMean> String asStringOrNull(final I id) {
		return id != null ? id.asString()
						  : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int hashCode() {
		return _id.hashCode();
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof ValidatedContactMeanBase) {
			ValidatedContactMeanBase id = (ValidatedContactMeanBase)obj;
			return id.getId().equals(_id);
		}
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Collection<String> _split(final String str) {
		if (Strings.isNullOrEmpty(str)) return null;
		Collection<String> s0 = Lists.newArrayList(str);
		Collection<String> s1 = _split(s0,' ');
		Collection<String> s2 = _split(s1,',');
		Collection<String> s3 = _split(s2,';');
		Collection<String> s4 = _split(s3,' ');
		Collection<String> s5 = _split(s4,'/');
		return FluentIterable.from(s5)
							 .filter(s -> Strings.isNOTNullOrEmpty(s))
							 .toList();
	}
	protected static Collection<String> _split(final Collection<String> strs,
									  		   final char separator) {
		if (CollectionUtils.isNullOrEmpty(strs)) return null;
		Collection<String> outStrs = Lists.newArrayList();
		for (String str : strs) {
			Collection<String> strSplitted = StringSplitter.using(Splitter.on(separator))
												 .at(str)
												 .toCollection();
			outStrs.addAll(strSplitted);
		}
		return outStrs;
	}
}
