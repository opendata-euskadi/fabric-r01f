package r01f.model.search.query;

import java.lang.reflect.Array;
import java.util.Collection;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Usage
 * <pre class='brush:java'>
 *		ContainedInQueryClause<Integer> spectrum = ContainedInQueryClause.<Integer>forMetaData("myField")
 *																	  	 .within(new Integer[] {2,3});
 * </pre>
 * @param <T>
 */
@MarshallType(as="containedInClause") 
@GwtIncompatible
@Accessors(prefix="_")
@NoArgsConstructor
public class ContainedInQueryClause<T>
     extends QueryClauseBase {

	private static final long serialVersionUID = -2289516417263071932L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="spectrum",
				   whenXml=@MarshallFieldAsXml(collectionElementName="item"))
	@Getter @Setter private T[] _spectrum;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	ContainedInQueryClause(final FieldID fieldId,
						   final T[] spectrum) {
		super(fieldId);
		_spectrum = spectrum;
	}	
	public static <T> ContainedInQueryClauseStep1Builder<T> forField(final FieldID fieldId) {
		return new ContainedInQueryClauseStep1Builder<T>(fieldId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public QueryClauseType getClauseType() {
		return QueryClauseType.CONTAINED_IN;
	}	
	@Override @SuppressWarnings("unchecked")
	public <V> V getValue() {
		return (V)_spectrum;
	}
	@Override @SuppressWarnings("unchecked")
	public <V> Class<V> getValueType() {
		return (Class<V>)_spectrum.getClass().getComponentType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public void setSpectrumFrom(final Collection<T> spectrum) {	// this is needed in order to store the value type
		if (spectrum == null) throw new IllegalArgumentException();
		_spectrum = (T[])spectrum.toArray();
	}
	@SuppressWarnings("unchecked")
	public void setSpectrumFrom(final Collection<T> spectrum,
								final Class<T> spectrumElementType) {	// this is needed in order to store the value type
		if (spectrum == null) throw new IllegalArgumentException();
		T[] array = (T[])Array.newInstance(spectrumElementType,spectrum.size());
		_spectrum = spectrum.toArray(array);
	}
	public void setSpectrumFrom(final T[] spectrum) {
		_spectrum = spectrum;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ContainedInQueryClause)) return false;
		
		if (!super.equals(obj)) return false;	// checks field id
		
		ContainedInQueryClause<?> otherContained = (ContainedInQueryClause<?>)obj;
		return _spectrum != null ? otherContained.getSpectrum() != null ? _arrayEqs(_spectrum,otherContained.getSpectrum())
							    							 			: false
							  : true;		// both null
	}
	private boolean _arrayEqs(final Object[] a,final Object[] b) {
		if (a.length != b.length) return false;
		boolean outEqs = true;
		for (int i=0; i < a.length; i++) {
			Object aEl = a[i];
			Object bEl = b[i];
			if (!aEl.equals(bEl)) {
				outEqs = false;
				break;
			}
		}
		return outEqs;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getFieldId(),
								this.getSpectrum());	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ContainedInQueryClauseStep1Builder<T> {
		private final FieldID _fieldId;
		
		public ContainedInQueryClause<T> within(final T[] spectrum) {
			return new ContainedInQueryClause<T>(_fieldId,
											  	 spectrum);
		}
		@SuppressWarnings("unchecked")
		public ContainedInQueryClause<T> within(final Collection<T> spectrum) {
			return new ContainedInQueryClause<T>(_fieldId,
											  	 (T[])spectrum.toArray());
		}
	} 
}
