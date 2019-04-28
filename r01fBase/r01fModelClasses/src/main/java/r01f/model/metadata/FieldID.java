package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.summary.Summary;

/**
 * Indexable field unique identifier
 * Beware that the {@link FieldID} might NOT be the same as the "real" field id at the index
 * in the case of fields with multiple "dimensions" like multi-language {@link Summary} fields where each
 * language {@link Summary} is stored in a separate field with an id like: metaDataId.{dimensionId}
 * For example, a multi-language {@link Summary} field with {@link FieldMetaDataID}=r01.summary
 * is stored in multiple lucene fields, one for each document: r01.summary.es, r01.summary.eu, etc
 */
@Immutable @GwtIncompatible
@MarshallType(as="indexableFieldId")
@NoArgsConstructor
public final class FieldID
	       extends OIDBaseMutable<String> {

	private static final long serialVersionUID = 8049806262539341650L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldID(final String oid) {
		super(oid);
	}
	public static FieldID from(final FieldIDToken... tokens) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < tokens.length; i++) {
			sb.append(tokens[i].getToken());
			if (i < tokens.length - 1) sb.append(".");
		}
		return FieldID.forId(sb.toString());
	}
	public static FieldID valueOf(final String s) {
		return FieldID.forId(s);
	}
	public static FieldID fromString(final String s) {
		return FieldID.forId(s);
	}
	public static FieldID forId(final String id) {
		return new FieldID(id);
	}
}
