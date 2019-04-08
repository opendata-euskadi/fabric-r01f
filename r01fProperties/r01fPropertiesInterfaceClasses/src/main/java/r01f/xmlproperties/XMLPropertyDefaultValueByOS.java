package r01f.xmlproperties;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.util.OSType;

/**
 * Creates a holder type for a property's values by environment
 * <pre class="brush:java">
 * 		new XMLPropertyDefaultValueByOS<T>()
 * 				.when(OSType.LINUX).use(value)
 * 				.when(OSType.WIN).use(otherValue)
 * 				.defaultValue(defaultValue)		// used when os is NOT any of the above
 * </pre>
 * @param <T>
 */
public class XMLPropertyDefaultValueByOS<T> 
  implements Serializable {

	private static final long serialVersionUID = -2266425009065246193L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS 
/////////////////////////////////////////////////////////////////////////////////////////
	private final Map<OSType,T> _envValues = Maps.newLinkedHashMapWithExpectedSize(2);	// linux win
	
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the property value for the given env
	 * @param env
	 * @return
	 */
	public T getFor(final OSType env) {
		T outVal = _envValues.get(env);
		if (outVal == null) outVal = _envValues.get(OSType.UNKNOWN);	// try the default value
		return outVal;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER 
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLPropertyDefaultValueByOS<T> defaultValue(final T value) {
		return this.when(OSType.UNKNOWN)
				   .use(value);
	}
	public XMLPropertyDefaultValueByOSBuilderValStep when(final OSType env) {
		return new XMLPropertyDefaultValueByOSBuilderValStep(env);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class XMLPropertyDefaultValueByOSBuilderValStep {
		private final OSType _os;
		
		public XMLPropertyDefaultValueByOS<T> use(final T value) {
			if (_os != null && value != null) XMLPropertyDefaultValueByOS.this._envValues.put(_os,value);
			return XMLPropertyDefaultValueByOS.this;
		}
	}
}
