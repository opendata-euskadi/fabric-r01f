package r01f.inject;

import java.lang.reflect.Method;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.reflection.ReflectionUtils;
import r01f.util.types.collections.CollectionUtils;

/**
 * GUICE {@link Matchers} extensions
 */
public class Matchers {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static ClassToTypeLiteralMatcherAdapter subclassesOf(final Class<?>... types) {
		return new ClassToTypeLiteralMatcherAdapter(types);
	}
	
	/**
	 * Adapts a Matcher<Class> to a Matcher<TypeLiteral>
	 * @see https://groups.google.com/forum/#!topic/google-guice/9IK1zQzWHLk
	 */
	@RequiredArgsConstructor @SuppressWarnings("rawtypes")
	public static class ClassToTypeLiteralMatcherAdapter 
	 		    extends AbstractMatcher<TypeLiteral> {
		
		private final Class<?>[] _types;

		@Override
		public boolean matches(final TypeLiteral typeLiteral) {
			boolean outMatches = false;
			for (Class<?> type : _types) {
				if (ReflectionUtils.isSubClassOf(typeLiteral.getRawType(),type)) {
					outMatches = true;
					break;
				}
			}
			return outMatches;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
    public static Matcher<Method> method(final String methodName,final Class<?>... methodParamTypes) {
        return new MethodMatcher(methodName,
        					     CollectionUtils.hasData(methodParamTypes) ? methodParamTypes
        					    		 								   : new Class<?>[] { /* empty */ });
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class MethodMatcher
	            extends AbstractMatcher<Method> {
	    private final String _methodName;
	    private final Class<?>[] _paramTypes;
	
	    @Override
	    public boolean matches(final Method method) {
	    	// do the method name matches?
	        boolean nameMatches = method.getName().equals(_methodName);
	        if (!nameMatches) return false;
	        
	        // do the param matches?
	        boolean paramsMatches = true;
	        if (CollectionUtils.hasData(method.getParameterTypes())) {
	        	if (_paramTypes.length != method.getParameterTypes().length) {
	        		paramsMatches = false;
	        	} else {
	        		Class<?>[] actualMethodParamTypes = method.getParameterTypes();
		        	for (int i=0; i < actualMethodParamTypes.length; i++) {
		        		Class<?> actualMethodParamType = actualMethodParamTypes[i];
		        		Class<?> methodParamType = _paramTypes[i];
		        		if (!actualMethodParamType.equals(methodParamType)) {
		        			paramsMatches = false;
		        			break;
		        		}
		        	}
	        	}
	        }
	        return paramsMatches;
	    }
	    @Override
	    public String toString() {
	        return "methodMatcher: " + _methodName + 
	        			(CollectionUtils.hasData(_paramTypes) ? "(" + CollectionUtils.toStringCommaSeparated(_paramTypes) + ")"
	        												  : "()");
	    }
	}
}
