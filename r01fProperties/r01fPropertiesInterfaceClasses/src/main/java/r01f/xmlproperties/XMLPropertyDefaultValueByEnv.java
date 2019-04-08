package r01f.xmlproperties;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.Environment;
import r01f.internal.Env;

/**
 * Creates a holder type for a property's values by environment
 * <pre class="brush:java">
 * 		new XMLPropertyDefaultValueByEnv<T>()
 * 				.when(Environment.forId("dev").use(value)
 * 				.when(Environment.forId("test").use(otherValue)
 * 				.when(Environment.forId("prod").use(anotherValue)
 * 				.defaultValue(defaultValue)		// used when environment is NOT any of the above
 * </pre>
 * @param <T>
 */
public class XMLPropertyDefaultValueByEnv<T> 
  implements Serializable {

	private static final long serialVersionUID = -2266425009065246193L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS 
/////////////////////////////////////////////////////////////////////////////////////////
	private final Map<Environment,T> _envValues = Maps.newLinkedHashMapWithExpectedSize(4);	// des, int, test, prod
	
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the property value for the given env
	 * @param env
	 * @return
	 */
	public T getFor(final Environment env) {
		T outVal = _envValues.get(env);
		if (outVal == null) outVal = _envValues.get(Environment.NO_ENV);	// try the default value
		return outVal;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER 
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLPropertyDefaultValueByEnv<T> defaultValue(final T value) {
		return this.when(Environment.NO_ENV)
				   .use(value);
	}
	public XMLPropertyDefaultValueByEnvBuilderValStep when(final Env env) {
		return this.when(env.getEnv());
	}
	public XMLPropertyDefaultValueByEnvBuilderValStep when(final Environment env) {
		return new XMLPropertyDefaultValueByEnvBuilderValStep(env);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class XMLPropertyDefaultValueByEnvBuilderValStep {
		private final Environment _env;
		
		public XMLPropertyDefaultValueByEnv<T> use(final T value) {
			if (_env != null && value != null) XMLPropertyDefaultValueByEnv.this._envValues.put(_env,value);
			return XMLPropertyDefaultValueByEnv.this;
		}
	}
}
