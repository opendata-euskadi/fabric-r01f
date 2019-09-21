package r01f.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.generics.ParameterizedTypeImpl;
import r01f.util.types.collections.CollectionUtils;



/**
 * Reflection utils
 */
@Slf4j
public class ReflectionUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
    private static final Map<String,Class<?>> FINAL_IMMUTABLE_CLASSES = new HashMap<String,Class<?>>(11);
	private static final Map<String, Class<?>> PRIMITIVE_CLASSES = new HashMap<String,Class<?>>(8);
	private static final Map<String, Class<?>> PRIMITIVE_ARRAY_CLASSES = new HashMap<String,Class<?>>(8);
    static {
        // immutable / final classes
        FINAL_IMMUTABLE_CLASSES.put(String.class.getName(),String.class);
        FINAL_IMMUTABLE_CLASSES.put(Byte.class.getName(),Byte.class);
        FINAL_IMMUTABLE_CLASSES.put(Short.class.getName(),Short.class);
        FINAL_IMMUTABLE_CLASSES.put(Integer.class.getName(),Integer.class);
        FINAL_IMMUTABLE_CLASSES.put(Long.class.getName(),Long.class);
        FINAL_IMMUTABLE_CLASSES.put(Float.class.getName(),Float.class);
        FINAL_IMMUTABLE_CLASSES.put(Double.class.getName(),Double.class);
        FINAL_IMMUTABLE_CLASSES.put(Character.class.getName(),Character.class);
        FINAL_IMMUTABLE_CLASSES.put(Boolean.class.getName(),Boolean.class);
        FINAL_IMMUTABLE_CLASSES.put(Date.class.getName(),Date.class);
        FINAL_IMMUTABLE_CLASSES.put(java.sql.Date.class.getName(),java.sql.Date.class);
        // Primitive types
		PRIMITIVE_CLASSES.put("boolean",boolean.class);
		PRIMITIVE_CLASSES.put("byte",byte.class);
		PRIMITIVE_CLASSES.put("short",short.class);
		PRIMITIVE_CLASSES.put("char",char.class);
		PRIMITIVE_CLASSES.put("int",int.class);
		PRIMITIVE_CLASSES.put("long",long.class);
		PRIMITIVE_CLASSES.put("float",float.class);
		PRIMITIVE_CLASSES.put("double",double.class);
		// primitive type array
		PRIMITIVE_ARRAY_CLASSES.put("boolean[]",boolean[].class);
		PRIMITIVE_ARRAY_CLASSES.put("byte[]",byte[].class);
		PRIMITIVE_ARRAY_CLASSES.put("short[]",short[].class);
		PRIMITIVE_ARRAY_CLASSES.put("char[]",char[].class);
		PRIMITIVE_ARRAY_CLASSES.put("int[]",int[].class);
		PRIMITIVE_ARRAY_CLASSES.put("long[]",long[].class);
		PRIMITIVE_ARRAY_CLASSES.put("float[]",float[].class);
		PRIMITIVE_ARRAY_CLASSES.put("double[]",double[].class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Checks if a type is a type definition (Class)
     * @param type the type
     * @return true if the type is a type definition
     */
    public static boolean isTypeDef(final Class<?> type) {
    	return type.isAssignableFrom(Class.class);
    }
    /**
     * Checks if a type is final or immutable: String, byte, short, integer, long, float, double, character o boolean
     * @param type
     * @return true if it's final, false otherwise 
     */
    public static boolean isFinalImmutable(final Class<?> type) {
        return type.isPrimitive() || FINAL_IMMUTABLE_CLASSES.containsValue(type);
    }
    /**
     * Checks if a type is abstract
     * @param type the type
     * @return true if the type is abstract
     */
    public static boolean isAbstract(final Class<?> type) {
    	return Modifier.isAbstract(type.getModifiers());
    }
    /**
     * Checks if a type is an interface
     * @param type the type
     * @return true if the type is an interface
     */
    public static boolean isInterface(final Class<?> type) {
    	return Modifier.isInterface(type.getModifiers());
    }
    /**
     * Checks if a type is instanciable (not a primitive type, an abstract type or an interface)
     * @param type the type
     * @return true if it's instanciable
     */
    public static boolean isInstanciable(final Class<?> type) {
    	return !type.isAnnotation() && !ReflectionUtils.isAbstract(type) && !ReflectionUtils.isInterface(type);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  HIERARCHY
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns a set with the type hierarchy for a type
     * @param type the type
     * @return a {@link Set} with the types in the hierarchy
     */
    public static Set<Class<?>> typeHierarchy(final Class<?> type) {
    	return ReflectionUtils.typeHierarchyBetween(type,Object.class);
    }
    /**
     * Returns a {@link Set} with the types in the hierarchy between a type and super-type
     * <pre>
     * IMPORTANT: if the ancestor type is NOT provided, all the hierarchy is returned 
     * </pre>
     * @param type the type
     * @param ancestorType the super-type
     * @return a {@link Set} with the types between the type and the ancestor type
     */
    public static Set<Class<?>> typeHierarchyBetween(final Class<?> type,final Class<?> ancestorType) {
    	Class<?> theAncestorType  = ancestorType != null ? ancestorType 
    													 : Object.class;
        Set<Class<?>> hierarchy = new LinkedHashSet<Class<?>>();
        if (!ReflectionUtils.isInterface(type)) {
	        Class<?> t = type;
	        do {
	            hierarchy.add(t);	            
	        	t = t.getSuperclass();
	        } while( !(t == theAncestorType || t == Object.class || t == null) );
	        if (type != ancestorType) hierarchy.add(t);		// beware that in the previous loop the last element is NOT added
        } else {
        	hierarchy.add(type);
        }
        return hierarchy;
    }
    /**
     * Gets the name of the type from the complete name including the package
     * @param classNameIncludingPackage 
     * @return 
     */
    public static String classNameFromClassNameIncludingPackage(final String classNameIncludingPackage) {
        if (classNameIncludingPackage == null || classNameIncludingPackage.length() == 0) return "";
        if (classNameIncludingPackage.indexOf('.') < 0) return classNameIncludingPackage;        
        Pattern p = Pattern.compile("^.*\\.([^.]+)$");
        Matcher m = p.matcher(classNameIncludingPackage);
        if (!m.matches()) return "";
        return m.group(1);
    }
    /**
     * Obtiene el nombre del paqueta a partir del nombre completo incluyendo el paquete
     * @param classNameIncludingPackage nombre de la classe completo: paquete.nombreClase
     * @return La parte del paquete
     */
    public static String packageFromClassName(final String classNameIncludingPackage) {
        if (classNameIncludingPackage == null || classNameIncludingPackage.length() == 0) return "";
        if (classNameIncludingPackage.indexOf('.') < 0) return "";
        Pattern p = Pattern.compile("^(.*)\\.[^.]+$");
        Matcher m = p.matcher(classNameIncludingPackage);
        if (!m.matches()) return "";
        return m.group(1);
    }
    /**
     * Obtiene el paquete y el nombre de la clase separados en un array de dos elementos
     * [0]-Paquete (null si no hay paquete)
     * [1]-Clase
     * @param classNameIncludingPackage
     * @return
     */
    public static String[] classAndPackageFromClassName(final String classNameIncludingPackage) {
        if (classNameIncludingPackage == null || classNameIncludingPackage.length() == 0) return new String[] {};
        if (classNameIncludingPackage.indexOf('.') < 0) return new String[] {null,classNameIncludingPackage};
        Pattern p = Pattern.compile("^(.*)\\.([^.]+)$");
        Matcher m = p.matcher(classNameIncludingPackage);
        if (!m.matches()) return new String[] {};
        return new String[] { m.group(1),m.group(2) };    	
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS PARA CHECKEAR LA JERARQUIA DE UN OBJETO
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Hace un cast del objeto al tipo que se pasa
	 * @param type el tipo al que hacer casting
	 * @param obj el objeto a hacer cast
	 * @return el objeto casteado
	 */
	public static <T> T cast(final Class<T> type,final Object obj) {
		return type.cast(obj);
	}
	@SuppressWarnings({ "cast","unchecked" })
	public static <T> T cast(final String typeName,final Object obj) {
		return ReflectionUtils.cast((Class<T>)ReflectionUtils.typeFromClassName(typeName),obj);
	}
	/**
	 * Returns true if the provided type is within the other provided ones
	 * @param types
	 * @return
	 */
	public static boolean isTypeWithin(final Class<?> type,
									   final Class<?>... types) {
		Preconditions.checkArgument(types != null && types.length > 0);
		boolean outIs = false;
		for (Class<?> currType : types) {
			if (currType.equals(type)) {
				outIs = true;
				break;
			}
		}
		return outIs;
	}
    /**
     * Comprueba si una clase o sus super-clases implementan un determinado interface
     * @param type el tipo 
     * @param theInterface el interfaz que se comprueba
     * @return true si se implementa y false en otro caso
     */
    public static boolean isImplementing(final Class<?> type,final Class<?> theInterface) {
    	return theInterface.isAssignableFrom(type); // not implemented in GWT
    }
    /**
     * Checks if a type is implementing any of the provided interfaces
     * @param type the type
     * @param theInterfaces the interfaces
     * @return true if one of the interfaces is implemented by type
     */
    public static boolean isImplementingAny(final Class<?> type,final Class<?>... theInterfaces) {
    	boolean outImplements = false;
    	for (Class<?> currIf : theInterfaces) {
    		if (ReflectionUtils.isImplementing(type,currIf)) {
    			outImplements = true;
    			break;
    		}
    	}
    	return outImplements;
    }
    /**
     * Checks if a type is implementing ALL the provided interfaces
     * @param type the type
     * @param theInterfaces the interfaces that must be implemented by the type
     * @return true if ALL the interfaces are implemented by type
     */
    public static boolean isImplementingAll(final Class<?> type,final Class<?>... theInterfaces) {
    	boolean outImplements = true;
    	for (Class<?> currIf : theInterfaces) {
    		if (!ReflectionUtils.isImplementing(type,currIf)) {
    			outImplements = false;
    			break;
    		}
    	}
    	return outImplements;
    }
    /**
     * Recorre la jerarquia de herencia para ver si una clase es subclase de
     * otra que se pasa como parametro
     * @param type La clase
     * @param superType La clase base
     * @return true si la clase es una subclase de la clase base
     */
    public static boolean isSubClassOf(final Class<?> type,final Class<?> superType) {
    	boolean isAssignable = superType.isAssignableFrom(type);
    	return isAssignable;
    }
    /**
     * Recorre la jerarquia de herencia para ver si un objeto es subclase de
     * otro que se pasa como parametro
     * @param theObj un objeto
     * @param theBaseObj el objeto base
     * @return true si el objeto es una subclase del objeto base
     */
    @SuppressWarnings("null")
	public static boolean isSubClassOf(final Object theObj,final Object theBaseObj) {
    	if (theObj == null && theBaseObj == null) return false;
        if (theObj == null && theBaseObj != null) return false;
        if (theObj != null && theBaseObj == null) return false;
        return ReflectionUtils.isSubClassOf(theObj.getClass(),theBaseObj.getClass());
    }
    /**
     * Comprueba si una clase es igual que la otra
     * @param type la clase
     * @param otherType la otra clase
     * @return true si son la misma clase
     */
    public static boolean isSameClassAs(final Class<?> type,final Class<?> otherType) {
        return type.equals(otherType);
    }
    /**
     * Comprueba si dos objetos son de la misma clase
     * @param theObj uno de los objetos
     * @param theOtherObj el otro objeto
     * @return true si son la misma clase
     */
    @SuppressWarnings("null")
	public static boolean isSameClassAs(final Object theObj,final Object theOtherObj) {
    	if (theObj == null && theOtherObj == null) return false;
        if (theObj == null && theOtherObj != null) return false;
        if (theObj != null && theOtherObj == null) return false;
        return ReflectionUtils.isSameClassAs(theObj.getClass(),theOtherObj.getClass());
    }  
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS DE DEFINICION DE CLASES
///////////////////////////////////////////////////////////////////////////////////////// 
    /**
     * Gets the type's {@link Class} object from the type's complete name (including package)
     * @param typeName 
     * @return the type's definition {@link Class} (NOT a type's instance) 
     * @throws ReflectionException if the type cannot be loaded
     */
	public static <T> Class<T> typeFromClassName(final String typeName) {
    	return _typeFromClassName(typeName,
    							  true);	// throw if cannot load
    }
    /**
     * Gets the type's {@link Class} object from the type's complete name (including package)
     * @param typeName 
     * @return the type's definition {@link Class} (NOT a type's instance) 
     * @throws ReflectionException if the type cannot be loaded
     */
	public static <T> Class<T> typeFromClassNameOrNull(final String typeName) {
    	return _typeFromClassName(typeName,
    							  false);	// do NOT throw if cannot load
    }
    /**
     * Gets the type's {@link Class} object from the type's complete name (including package)
     * @param typeName 
     * @param throwIfCannotLoad
     * @return the type's definition {@link Class} (NOT a type's instance) 
     * @throws ReflectionException if the type cannot be loaded
     */
    @SuppressWarnings("unchecked")
	private static <T> Class<T> _typeFromClassName(final String typeName,
												   final boolean throwIfCannotLoad) {
        if (typeName == null) throw new IllegalArgumentException("The typeName cannot be null to get it's type");
        Class<?> objType = null ;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        
        // Use default ClassLoader, Class.forName is problematic in EARs
        if (cl != null) {
            try  {
                objType = cl.loadClass(typeName);
            } catch(ClassNotFoundException cnfEx) {
                /* ignorar */
            }
            if (objType == null) {
            	try {
            		objType = Class.forName(typeName);
            	} catch(ClassNotFoundException cnfEx) {
            		if (throwIfCannotLoad) throw ReflectionException.of(cnfEx);	// wrap the exception
            	}
            }
        }
        if (objType == null && throwIfCannotLoad) throw ReflectionException.classNotFoundException(typeName);
        return (Class<T>)objType;
    }
    /**
     * Loads a type by it's name and returns it as a provided type
     * @param typeName the type to be loaded
     * @param type the type to be returned
     * @return
     */
    public static <T> Class<? extends T> typeFromClassName(final String typeName,final Class<T> type) {
    	Class<?> rawType = ReflectionUtils.typeFromClassName(typeName);
    	return rawType.asSubclass(type);
    }
	/**
	 * Returns a {@link Type} representing the provided type as a {@link String}
	 * <ul>
	 * 		<li>If the provided type is NOT a generic type, this returns a {@link Class} object</li>
	 * 		<li>If the provided type is a generic parameterized type this returns a {@link ParameterizedTypeImpl} object</li> 
	 * </ul>
	 * 
	 * @param typeName
	 * @return
	 * @throws ReflectionException
	 */
	public static Type getObjectType(final String typeName) throws ReflectionException {
		return _getObjectType(typeName,false);
	}
	private static Type _getObjectType(final String typeName,
							   		   final boolean isTypeArg) throws ReflectionException {
		try  {
			Type outType = null;
			
			// [1] Primitive types
			//			ie: long
			Class<?> clazz = PRIMITIVE_CLASSES.get(typeName);
			if (clazz != null) {
				if (!isTypeArg) {
					outType = clazz;	// the type is a primitive type... it cannot be a typeArg
				} else {
					throw new IllegalArgumentException(Throwables.message("A Type argument of a parameterized type can not be a primitive type, but it was {}",typeName));
				}
			}
			// [3] Primitive array types
			//			ie: long[]
			if (outType == null) {
				clazz = PRIMITIVE_ARRAY_CLASSES.get(typeName);
				if (clazz != null) outType = clazz;
			}
			
			// at this point, the type is NOT a primitive type neither a primitive array type
			int arrayLeftIdx = typeName.indexOf("[");
			int genericLeftIdx = typeName.indexOf("<");
			int genericRightIdx = typeName.lastIndexOf(">");
			
			// [3] Array of a not primitive type
			// 			ie: 	java.lang.String[]
			//					com.foo.FooNonGenericClass[]
			if (outType == null) {
				if (arrayLeftIdx >= 0											// it's an array 
				 && (genericLeftIdx < 0 || arrayLeftIdx < genericLeftIdx)) {	// AND it's not a generic type or it's an array of a generic type
					String arrayComponentType = typeName.substring(0,arrayLeftIdx);
					clazz = Class.forName(arrayComponentType);
					outType = Array.newInstance(clazz,0)
								   .getClass();
				}
			}
			// [4] Not an array neither a generic type: a normal type
			// 			ie: 	java.lang.String
			//					com.foo.FooNonGenericClass
			if (outType == null) {
				if (genericLeftIdx < 0) outType = Class.forName(typeName);
			}
			// [5] Generic Type
			//			ie: 	java.util.Collection<java.lang.Integer>
			//					java.util.Map<java.lang.String, 
			//								  com.foo.FooPair<java.lang.Integer,java.util.Date>>
			//					com.foo.FooGenericClass<java.lang.String, 
			//											java.lang.Integer>
			//					com.foo.FooGenericClass<int[], 
			//											com.prime.FooGenericClass<com.foo.FooNonGenericClass,java.lang.Boolean>>
			if (outType == null) {
				if (genericRightIdx < 0) throw new IllegalArgumentException(Throwables.message("The type {} is not a valid generic type",typeName));
				// 5.1 - Generic Type raw part
				String rawTypeName = typeName.substring(0,genericLeftIdx);
				Class<?> rawType = Class.forName(rawTypeName);
				
				// 5.2 - Generic Type args
				String strTypeArgs = typeName.substring(genericLeftIdx + 1,genericRightIdx);
				List<String> listTypeArgs = new ArrayList<String>();
				int startPos = 0;
				int seekPos = 0;
				while (true) {
					int commaPos = strTypeArgs.indexOf(",",seekPos);
					if (commaPos >= 0) {
						String term = strTypeArgs.substring(startPos,commaPos);
						int countLeftBrackets = StringUtils.countMatches(term,"<");
						int countRightBrackets = StringUtils.countMatches(term,">");
						if (countLeftBrackets == countRightBrackets) {
							listTypeArgs.add(term.trim());
							startPos = commaPos + 1;
						}
						seekPos = commaPos + 1;
					} else {
						listTypeArgs.add(strTypeArgs.substring(startPos).trim());
						break;
					}
				}
				if (listTypeArgs.isEmpty()) throw new IllegalArgumentException(Throwables.message("{} is not a valid generic type.",typeName));
		
				int size = listTypeArgs.size();
				Type[] objectTypes = new Type[size];
				for (int i = 0; i < size; i++) {
					objectTypes[i] = _getObjectType(listTypeArgs.get(i),
													true);	// recursive call for each type argument
				}
				outType = new ParameterizedTypeImpl(rawType,
													objectTypes,
													null);
			}
			// Devolver
			return outType;
		} catch (ClassNotFoundException cnfEx) {
			throw ReflectionException.of(cnfEx);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GENERICS   
// 	(ver http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#How do I retrieve an object's actual (dynamic) type?)
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if a type is generic
	 * @return
	 */
	public static boolean isGenericType(final Class<?> type) {
		return type.getTypeParameters() != null 
			&& type.getTypeParameters().length > 0;
	}
	/**
	 * Returns true if a type is generic 
	 * @param type
	 * @return
	 */
	public static boolean isGenericType(final Type type) {
		return !(type instanceof Class);
	}
	/**
	 * Returns true if a field is generic
	 * @param f 
	 * @return 
	 */
	public static boolean isGenericField(final Field f) {
		return !(f.getGenericType() instanceof Class);
	}
    /**
     * Returns the underlying type or null if it's a TypeVariable (remember type erasure)
     * @param type
     * @return 
     */
    public static Class<?> classOfType(final Type type) {
    	return TypeToken.of(type).getRawType();
    }
    /**
     * Returns the underlying type or null if it's a TypeVariable (remember type erasure)
     * @param type
     * @return
     */
    public static Class<?> typeOf(final Type type) {
    	return TypeToken.of(type).getRawType();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  TYPE ANNOTATIONS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns all type's annotations (super-type annotations are NOT returned)
     * @param type 
     * @return 
     */
    public static Annotation[] typeAnnotations(final Class<?> type) {
    	return type.getAnnotations();
    }
    /**
     * Returns all annotations on a types or it's super-types
     * @param type 
     * @return 
     */
    public static Annotation[] typeOrSuperTypeAnnotations(final Class<?> type) {
    	Set<Annotation> outAnnots = Sets.newHashSet();
    	Set<Class<?>> hierarchy = ReflectionUtils.typeHierarchy(type);
    	for (Class<?> t : hierarchy) {
    		Annotation[]  annots = ReflectionUtils.typeAnnotations(t);
    		if (annots != null) outAnnots.addAll(Arrays.asList(annots));
    	}
    	return CollectionUtils.toArray(outAnnots);	//,new TypeRef<Annotation>() {});
    }
    /**
     * Returns true if the given type has the given annotation (super-types are NOT checked)
     * @param type
     * @param annotationType
     * @return
     */
    public static <A extends Annotation> boolean typeHasAnnotation(final Class<?> type,final Class<A> annotationType) {
    	return ReflectionUtils.typeAnnotation(type,annotationType) != null;
    }
    /**
     * Returns a type's annotation (super types are NOT checked)
     * @param type 
     * @param annotationType 
     * @return null if type is NOT annotated
     */
    public static <A extends Annotation> A typeAnnotation(final Class<?> type,final Class<A> annotationType) {
    	return type.getAnnotation(annotationType);
    }
    /**
     * Returns true if the given type has the given annotation (super-types are checked)
     * @param type
     * @param annotationType
     * @return
     */
    public static <A extends Annotation> boolean typeOrSuperTypeHasAnnotation(final Class<?> type,final Class<A> annotationType) {
    	return ReflectionUtils.typeOrSuperTypeAnnotation(type,annotationType) != null;
    }
    /**
     * Returns a type's annotation (super-types are checked)
     * @param type 
     * @param annotationType 
     * @return null if type or it's super-types are NOT annotated
     */
    public static <A extends Annotation> A typeOrSuperTypeAnnotation(final Class<?> type,final Class<A> annotationType) {
    	A outAnnot = null;
    	Set<Class<?>> hierarchy = ReflectionUtils.typeHierarchy(type);
    	for (Class<?> t : hierarchy) {
    		A  annot = ReflectionUtils.typeAnnotation(t,annotationType);
    		if (annot != null) {
    			outAnnot = annot;
    			break;
    		}
    	}
    	return outAnnot;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  OBJECT BUILDING METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Class<?>[]  EMPTY_CLASS_ARRAY  = new Class<?>[0];
    /**
     * Returns an instance of a generic type from it's fully qualified name (name including package)
     * @param className
     * @return
     */
    public static <T> T createInstanceOf(final String className) {
        @SuppressWarnings("unchecked")     	
        T outObj = (T)ReflectionUtils.createInstanceOf(className,
        												EMPTY_CLASS_ARRAY,EMPTY_OBJECT_ARRAY,
        												true);  // force        
        return outObj;
    }
    /**
     * Returns an object's instance from the type's fully quallified name (type name including package)
     * @param className
     * @param constructorArgsTypes 
     * @param constructorArgs 
     * @return 
     */
    public static <T> T  createInstanceOf(final String className,
    									  final Class<?>[] constructorArgsTypes,final Object[] constructorArgs) {
    	@SuppressWarnings("unchecked")
        T outObj = (T)ReflectionUtils.createInstanceOf(className,
        										 		constructorArgsTypes,constructorArgs,
        										 		true);   // force 	
    	return outObj;
    }    
    /**
     * Returns an object's instance from the type's fully quallified name (type name including package)
     * @param className 
     * @param constructorArgsTypes 
     * @param constructorArgs 
     * @param force if the constructor accesibillity must be force (ie it's a private constructor)
     * @return 
     */
    public static <T> T createInstanceOf(final String className,
    									 final Class<?>[] constructorArgsTypes,final Object[] constructorArgs,
    									 final boolean force) {
        Class<?> objClassDef = ReflectionUtils.typeFromClassName(className);     // Obtener la definicion de la clase
    	@SuppressWarnings("unchecked")      
        T outObj = (T)ReflectionUtils.createInstanceOf(objClassDef,
        										 		constructorArgsTypes,constructorArgs,
        										 		force);
    	return outObj;
    }
    /**
     * Returns an object's instance using the default no-args constructor
     * @param type 
     * @return
     */
    public static <T> T  createInstanceOf(final Class<?> type) { 
    	@SuppressWarnings("unchecked")        	
        T outObj = (T)ReflectionUtils.createInstanceOf(type,
        										       EMPTY_CLASS_ARRAY,EMPTY_OBJECT_ARRAY,
        										       true);	// force         
        return outObj;
    }
    /**
     * Returns an object's instance
     * @param type 
     * @param constructorArgsTypes 
     * @param constructorArgs 
     * @return 
     */
    public static <T> T  createInstanceOf(final Class<?> type,
    									  final Class<?>[] constructorArgsTypes,final Object[] constructorArgs) {
    	@SuppressWarnings("unchecked")   	
    	T outObj = (T)ReflectionUtils.createInstanceOf(type,constructorArgsTypes,constructorArgs,true);
    	return outObj;
    }
    /**
     * Returns an object's instance
     * @param type 
     * @param constructorArgsTypes 
     * @param constructorArgs 
     * @param force if the constructor accesibillity must be force (ie it's a private constructor)
     * @return
     */
    public static <T> T  createInstanceOf(final Class<?> type,
    									  final Class<?>[] constructorArgsTypes,final Object[] constructorArgs,
    									  final boolean force) {
        if (type == null) return null;
        Object newObj = null;
        try {
	        Constructor<?> constructor = ReflectionUtils.getConstructor(type,
	        															constructorArgsTypes,
	        															force);
	        if (constructor != null) {
	        	newObj = constructor.newInstance(constructorArgs != null ? constructorArgs
                                               						  	 : new Object[] {});
	        } else {
	        	throw ReflectionException.noConstructorException(type,constructorArgsTypes);
	        }
        } catch(Throwable th) {
            throw ReflectionException.of(th);
        }
        @SuppressWarnings("unchecked")         
        T outObj = (T)newObj;
        return outObj;
    }
    /**
     * Checks if a type's instance can be created from a {@link String}
     * It uses a secuential approach: 
     * <ul>
     * 		<li>First it tries to use a single String param constructor</li>
     * 		<li>Second it tries to use a static valueOf(String) method</li>
     * </ul>
     * @param type
     * @return
     */
    public static boolean canBeCreatedFromString(final Class<?> type) {
    	try {
    		if (ReflectionUtils.isInstanciable(type)) {
		    	// [1] - Try to use the single String param constructor (if it exists)
		    	Constructor<?> constructor = ReflectionUtils.getConstructor(type,new Class<?>[] {String.class});
		    	if (constructor != null) return true;
		    	
		    	// [2] - Try to use a static valueOf method
	    		Method valueOfMethod = ReflectionUtils.staticMethodMatchingParamTypes(type,
	    									 										  "valueOf",
	    									 										  new Class<?>[] {String.class});
	    		if (valueOfMethod != null) return true;
    		} 
    	} catch(Throwable th) {
    		// ignored
    	}
    	return false;
    }
    /**
     * Creates a type's instance from a {@link String}
     * It uses a secuential approach: 
     * <ul>
     * 		<li>First it tries to use a single String param constructor</li>
     * 		<li>Second it tries to use a static valueOf(String) method</li>
     * </ul>
     * @param type
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T> T createInstanceFromString(final Class<?> type,
    									 		 final String value) {
    	Object newObj = null;
    	// [1] - Try to use the single String param constructor (if it exists)
    	try {
	    	Constructor<?> constructor = ReflectionUtils.getConstructor(type,new Class<?>[] {String.class});
	    	if (constructor != null) {
	        	newObj = constructor.newInstance(new Object[] {value});
	    	} 
    	} catch(Throwable th) {
    		th.printStackTrace(System.out);
    	}
    	// [2] - Try to use a static valueOf method
    	if (newObj == null) {
    		try {
	    		Method valueOfMethod = ReflectionUtils.staticMethod(type,
	    									 						"valueOf",
	    									 						new Class<?>[] {String.class});
	    		if (valueOfMethod != null)  {
	    			newObj = ReflectionUtils.invokeStaticMethod(type,
	    														valueOfMethod,
	    														new Object[] {value});
	    		} else {
	    			throw ReflectionException.instantiationException(type + " from a String because neither the single String param constructor exists, nor the static valueOf(String) method");
	    		}
	    	} catch(Throwable th) {
	    		th.printStackTrace(System.out);
	    	}
    	}
    	return (T)newObj;
    }


/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns a type constructor
     * @param type
     * @param constructorArgsTypes
     * @param force
     * @return
     */
    public static Constructor<?> getConstructor(final Class<?> type,
    											final Class<?>[] constructorArgsTypes) {
    	return ReflectionUtils.getConstructor(type,
    										  constructorArgsTypes,
    										  false);
	}
    /**
     * Returns a type constructor or null if the required constructor does NOT exists
     * @param type
     * @param constructorArgsTypes
     * @param force
     * @return
     */
    public static Constructor<?> getConstructor(final Class<?> type,
    											final Class<?>[] constructorArgsTypes,
    											final boolean force) {
        Constructor<?> constructor = null;
        try {
//            constructor = ConstructorUtils.getAccessibleConstructor(type,(constructorArgsTypes != null ? constructorArgsTypes
//                                                                                                       : new Class<?>[] {}));
//            if (constructor == null && force) {
                constructor = type.getDeclaredConstructor(constructorArgsTypes != null ? constructorArgsTypes
                                                                                       : new Class<?>[] {}); 	// Constructor
                if (force) ReflectionUtils.makeAccessible(constructor);      // Hacer accesible el constructor vacio
//            }
        } catch(NoSuchMethodException nsmEx) {
        	/* the constructor does NOT exists */
        }
         return constructor;
    }
    /**
     * Finds a constructor that suits the provided params given that they can not be in the 
     * correct order
     * @param type
     * @param providedParamTypes
     * @return
     */
    public static Collection<Constructor<?>> findSuitableConstructors(final Class<?> type,
    													  			  final Class<?>[] providedParamTypes) {
    	Collection<Constructor<?>> outConstructors = null;
    	Constructor<?>[] constructors = type.getDeclaredConstructors();
    	if (constructors != null && constructors.length > 0) {
    		for (Constructor<?> constructor : constructors) {
    			boolean isSuitable = false;
    			// Only constructors with the same number of parameters might be suitable
    			if (constructor.getParameterTypes().length == 0 
    			 && (providedParamTypes == null || providedParamTypes.length == 0)) {
    				isSuitable = true;
    			} else if (constructor.getParameterTypes().length == providedParamTypes.length) {
    				isSuitable = true;
    				for (Class<?> providedParamType : providedParamTypes) {  
    					boolean paramFound = false;
    					for (Class<?> constructorParamType : constructor.getParameterTypes()) {
    						if (ReflectionUtils.isSubClassOf(providedParamType,constructorParamType)) {
    							paramFound = true;
    							break;
    						}
    					}
    					if (!paramFound) {
    						isSuitable = false;
    						break;
    					}
    				}
    			}
    			if (isSuitable) {
					if (outConstructors == null) outConstructors = Lists.newArrayList();
					outConstructors.add(constructor);
    			}
    		}
    	}
    	return outConstructors;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Obtiene un array con la DEFINICION de todos los metodos de un objeto, recorriendo toda
     * la jerarquia de herencia
     * PROBLEMA:    class.getMethods()          devuelve solo metodos PUBLICOS
     *              class.getDeclaredMethods()  devuelve metodos publicos y privados declarados
     *                                          en la propia clase (ignora la herencia)
     * @param type   La definicion de la clase
     * @return un array de objetos {@link Method} con la definicion de los metodos
     */
    public static Method[] allMethods(final Class<?> type) {
        List<Method> methods = new ArrayList<Method>();
        for (Class<?> c = type; c != Object.class; c = c.getSuperclass()) {
            Method[] declaredMethods = c.getDeclaredMethods();
            if (declaredMethods != null)  methods.addAll(Arrays.asList(declaredMethods));
        }
        Method[] outMethods = new Method[methods.size()];
        return methods.toArray(outMethods);
    }
    /**
     * Devuelve un metodo ESTATICO de una clase
     * @param type La definici�n de la clase que contiene el metodo estatico
     * @param methodName nombre del metodo 
     * @param argsTypes tipos de los argumentos del metodo
     * @throws ReflectionException si ocurre algun error o el m�todo no existe
     */
    public static Method staticMethod(final Class<?> type,
    								  final String methodName,final Class<?>... argsTypes) {
    	Method method = ReflectionUtils.method(type,methodName,argsTypes);    
    	return method;
    }
    /**
     * Devuelve un metodo ESTATICO de una clase
     * @param type La definici�n de la clase que contiene el metodo estatico
     * @param methodName nombre del metodo 
     * @param argsTypes tipos de los argumentos del metodo
     * @throws ReflectionException si ocurre algun error
     */
    public static Method staticMethodMatchingParamTypes(final Class<?> type,
    								  					final String methodName,final Class<?>... argsTypes) {
    	Method method = ReflectionUtils.methodMatchingParamTypes(type,methodName,argsTypes);    
    	return method;
    }
    /**
     * Devuelve un metodo ESTATICO de una clase
     * @param type La definici�n de la clase que contiene el metodo estatico
     * @param methodName nombre del metodo 
     * @throws ReflectionException si ocurre algun error
     */
    public static Method staticMethodNotMatchingParamTypes(final Class<?> type,
    								  					   final String methodName) {
    	Method method = ReflectionUtils.methodNotMatchingParamTypes(type,methodName);    
    	return method;
    }
    /**
     * Busca el metodo que se pasa como parameto, recorriendo toda la jerarquia de herencia
     * PROBLEMA:    class.getMethods()          devuelve solo metodos PUBLICOS
     *              class.getDeclaredMethods()  devuelve metodos publicos y privados declarados
     * @param type el tipo
     * @param methodName El nombre del metodo
     * @param paramTypes Los tipos de los parametros
     * @return El metodo buscado 
     * @throws ReflectionException NoSuchMethodException si no se encuentra el metodo
     */
    public static Method method(final Class<?> type,
    							final String methodName,final Class<?>... paramTypes) {
    	Method outMethod = ReflectionUtils.methodMatchingParamTypes(type,
    															  	methodName,
    															  	paramTypes);
        // Segunda opci�n comprobar �nicamente el nombre... pasar de los parametros
        // (puede devolver un m�todo con distintos parametros a los deseados)
        if (outMethod == null) outMethod = ReflectionUtils.methodNotMatchingParamTypes(type,
        																			   methodName);
        if (outMethod == null) throw ReflectionException.noMethodException(type,
        																   methodName,paramTypes); 
        return outMethod;
    }
    /**
     * Busca el metodo que se pasa como parameto, recorriendo toda la jerarquia de herencia
     * PROBLEMA:    class.getMethods()          devuelve solo metodos PUBLICOS
     *              class.getDeclaredMethods()  devuelve metodos publicos y privados declarados
     * @param type el tipo
     * @param methodName El nombre del metodo
     * @param paramTypes Los tipos de los parametros
     * @return El metodo buscado o null si no se encuentra
     */
    public static Method methodMatchingParamTypes(final Class<?> type,
    								  			  final String methodName,final Class<?>... paramTypes) {
    	Method outMethod = null;
        for (Class<?> t = type; t != Object.class; t = t.getSuperclass()) {
        	if (t == null) break;
            try  {
                Method m = t.getDeclaredMethod(methodName,paramTypes != null ? paramTypes
                                                                             : new Class[] {});
                if (m != null) {
                	outMethod = m;
                	break;
                }
            } catch (NoSuchMethodException nsmEx) {
                /* Ignore */
            }
        }
        return outMethod;
    }
    /**
     * Busca el metodo que se pasa como parameto, recorriendo toda la jerarquia de herencia
     * PROBLEMA:    class.getMethods()          devuelve solo metodos PUBLICOS
     *              class.getDeclaredMethods()  devuelve metodos publicos y privados declarados
     * @param type el tipo
     * @param methodName El nombre del metodo
     * @return El metodo buscado o null si no se encuentra
     */
    public static Method methodNotMatchingParamTypes(final Class<?> type,
    												 final String methodName) {
    	Method outMethod = null;
        for (Class<?> t = type; t != Object.class; t = t.getSuperclass()) {
        	if (t == null) break;
        	Method[] methods = t.getDeclaredMethods();
        	for (Method m : methods) {
        		if (m.getName().equals(methodName)) {
        			outMethod = m;
        			break;
        		}
        	}
        }
        return outMethod;
    }
    /**
     * Invokes a method on a given object
     * @param obj the object on where the method is invoked (if it's null an static method is tried)
     * @param method 
     * @param argValues 
     * @return the method invocation return value
     * @throws ReflectionException 
     */
    @SuppressWarnings("unchecked")
	public static <T> T invokeMethod(final Object obj,final Method method,final Object... argValues) {
    	if (obj == null || method == null) throw new IllegalArgumentException(Throwables.message("Cannot invoke {} method on null object instance",
    																							 (method != null ? method : "null"),
    																							 (obj != null ? obj.getClass() : "null")));
        try {
        	ReflectionUtils.makeAccessible(method);	// Ensure the method is accessible
            return (T)method.invoke(obj,
            					    (argValues != null ? argValues : new Object[] {}));     // Invocar al metodo sobre el objeto
        } catch(Throwable th) {
        	throw ReflectionException.of(th); 
        }
    }
    /**
     * Invoca un metodo sobre un objeto
     * @param obj El objeto sobre el que se invoca el metodo (si es null se intenta llamar a un m�todo est�tico)
     * @param methodName nombre del metodo a invocar
     * @param argsTypes tipos de los argumentos a invocar
     * @param argsValues valores de los argumentos
     * @return El objeto devuelto tras la invocacion del metodo
     * @throws ReflectionException si ocurre algun error
     */
    @SuppressWarnings("unchecked")
	public static <T> T invokeMethod(final Object obj,
    								 final String methodName,final Class<?>[] argsTypes,final Object[] argsValues) {
    	if (obj == null || methodName == null) throw new IllegalArgumentException(Throwables.message("Cannot invoke {} method on null object instance",
    																							     (methodName != null ? methodName : "null"),
    																							     (obj != null ? obj.getClass() : "null")));
    	try {
           return (T)MethodUtils.invokeMethod(obj,methodName,argsValues,argsTypes);
    	} catch (Throwable th) { 
           throw ReflectionException.of(th);
    	} 
    }
    /**
     * Invokes an static method in a type
     * @param type 
     * @param methodName 
     * @param argsTypes 
     * @param argsValues 
     * @return the method-returned object
     * @throws ReflectionException 
     */
    @SuppressWarnings("unchecked")
	public static <T> T invokeStaticMethod(final Class<?> type,
    									   final String methodName,final Class<?>[] argsTypes,final Object[] argsValues) {
    	Method method = ReflectionUtils.method(type,methodName,argsTypes);
    	return (T)ReflectionUtils.invokeMethod(type,method,argsValues);
    }    
    /**
     * Invokes an static method in a type
     * @param 
     * @param method 
     * @param argsValues 
     * @return the method-returned object
     * @throws ReflectionException s
     */
    @SuppressWarnings("unchecked")
	public static <T> T invokeStaticMethod(final Class<?> type,
    									   final Method method,final Object... argsValues) {
    	if (type == null || method == null) throw new IllegalArgumentException(Throwables.message("Cannot invoke {} method on null object instance",
    																							  (method != null ? method : "null"),
    																							  (type != null ? type : "null")));
    	try {
           return (T)MethodUtils.invokeStaticMethod(type,
        		   								    method.getName(),
        		   								    argsValues,method.getParameterTypes());
    	} catch (Throwable th) { 
           throw ReflectionException.of(th);
    	} 
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELD-ACCESS METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns a {@link Map} with the DEFINITION of all object's fields going through
     * all object's hierarchy
     * BEWARE!!	The returned {@link Map} is ORDERED in a way that last fields are  
     * 			the ones at the base type
     * @param type 
     * @return a map of {@link Field} objects indexed by name
     * @throws ReflectionException 
     */
    public static Map<String,Field> allFieldsMap(final Class<?> type) {
        // BEWARE with the hierarcy: crawls over all hierarchy's type¡s fields to the Object type
        // (the root of all Java objects)
    	// PROBLEM:    class.getFields()         returns only PUBLIC fields
    	//             class.getDeclaredFields() returns public and private fields declared 
        //                                       at the type (ignoring inheritance hierarchy)
    	if (ReflectionUtils.isInterface(type)) return null;
        Map<String,Field> fields = Maps.newLinkedHashMap();		// order!!!
        for (Class<?> c = type; c != Object.class; c = c.getSuperclass()) {
            Field[] declaredFields = c.getDeclaredFields();
            if (declaredFields != null && declaredFields.length > 0) {
                for (int i=0; i < declaredFields.length; i++) {
                    fields.put(declaredFields[i].getName(),declaredFields[i]);
                }
            }
        }
        return fields;
    }
    /**
     * Returns a {@link Map} with the DEFINITION of all object's fields going through
     * all object's hierarchy
     * BEWARE!!	The returned {@link Map} is ORDERED in a way that last fields are  
     * 			the ones at the base type
     * @param type 
     * @param forceAccesible forces the field to be accessible 
     * @return a map of {@link Field} objects indexed by name
     * @throws ReflectionException
     */
    public static Map<String,Field> allFieldsMap(final Class<?> type,final boolean forceAccesible) {
    	Map<String,Field> outFields = ReflectionUtils.allFieldsMap(type);
    	if (outFields != null && outFields.size() > 0) {
    		for (Field f : outFields.values()) ReflectionUtils.makeAccessible(f);
    	}
    	return outFields;
    }
    /**
     * Returns collection with the DEFINITION of all object's fields going through
     * all object's hierarchy
     * BEWARE!!	The returned array is ORDERED in a way that last fields are  
     * 			the ones at the base type
     * @param type 
     * @throws ReflectionException 
     */
    public static Field[] allFields(final Class<?> type) {
        Map<String,Field> fields = ReflectionUtils.allFieldsMap(type);
        if (fields == null) return null;
        return fields.size() > 0 ? fields.values().toArray(new Field[fields.size()])
        						 : new Field[] {};
    }
    /**
     * Returns collection with the DEFINITION of all object's FINAL fields going through
     * all object's hierarchy
     * BEWARE!!	The returned array is ORDERED in a way that last fields are  
     * 			the ones at the base type
     * @param type 
     * @throws ReflectionException 
     */
    public static Field[] allFinalFields(final Class<?> type) {
    	Map<String,Field> finalFields = ReflectionUtils.allFinalFieldsMap(type);
    	return CollectionUtils.toArray(finalFields.values());
    }
    /**
     * Returns a {@link Map} with the DEFINITION of all object's FINAL fields going through
     * all object's hierarchy
     * BEWARE!!	The returned {@link Map} is ORDERED in a way that last fields are  
     * 			the ones at the base type
     * @param type 
     * @return a map of {@link Field} objects indexed by name
     * @throws ReflectionException
     */
    public static Map<String,Field> allFinalFieldsMap(final Class<?> type) {
    	Map<String,Field> allFields = ReflectionUtils.allFieldsMap(type);
    	Predicate<Field> filter = new Predicate<Field>() {
									@Override
									public boolean apply(final Field f) {
										return Modifier.isFinal(f.getModifiers());
									}
								  }; 
    	Map<String,Field> finalFields = Maps.filterValues(allFields,filter);
    	return finalFields;
    }
    /**
     * Gets an object's field DEFINITION looking at all the object's hierarchy 
     * @param type 
     * @param fieldName 
     * @return the field 
     * @throws ReflectionException if the field does NOT exists
     */
    public static Field field(final Class<?> type,final String fieldName) {
    	// PROBLEM:    class.getFields()         	returns ONLY public fields
    	//              class.getDeclaredFields() 	returns public fields and private fields in the type (ignoring the super-types)
    	Field outField = ReflectionUtils.fieldOrNull(type,fieldName);
        if (outField == null) throw ReflectionException.noFieldException(type,fieldName);
        return outField;
    }
    /**
     * Gets an object's field DEFINITION looking at all the object's hierarchy 
     * @param type 
     * @param fieldName 
     * @return the field or null if the field does NOT exists
     */
    public static Field fieldOrNull(final Class<?> type,final String fieldName) {
    	// PROBLEM:    class.getFields()         	returns ONLY public fields
    	//              class.getDeclaredFields() 	returns public fields and private fields in the type (ignoring the super-types)
    	Field outField = null;
        for (Class<?> c = type; c != Object.class; c = c.getSuperclass()) {
            try {
                outField = c.getDeclaredField(fieldName);
            	break;
            } catch (NoSuchFieldException nsfEx) {
                /* Ignore */
            }
        }
        return outField;
    }
    /**
     * Gets an object's field DEFINITION looking at all the object's hierarchy 
     * @param type 
     * @param fieldName 
     * @param forceAccesible 
     * @return the field 
     * @throws ReflectionException NoSuchFieldException if the field is NOT found
     */
    public static Field field(final Class<?> type,final String fieldName,
    						  final boolean forceAccesible) {
    	Field f = ReflectionUtils.field(type,fieldName);
        if (forceAccesible) ReflectionUtils.makeAccessible(f);
        return f;
    }
    /**
     * Gets an object's field DEFINITION looking at all the object's hierarchy 
     * @param type 
     * @param fieldName 
     * @param forceAccesible 
     * @return the field 
     * @throws ReflectionException if the field does NOT exists
     */
    public static Field fieldOrNull(final Class<?> type,final String fieldName,
    						  		final boolean forceAccesible) {
    	Field f = ReflectionUtils.fieldOrNull(type,fieldName);
        if (f != null && forceAccesible) ReflectionUtils.makeAccessible(f);
        return f;
    }
    /**
     * Returns all type's {@link Field}s
     * @param type 
     * @param fieldType  
     * @return 
     */
    public static Field[] fieldsOfType(final Class<?> type,final Class<?> fieldType) {
    	return ReflectionUtils.fieldsOfAnyType(type,fieldType);
    }
    /**
     * Returns all type's {@link Field}s
     * @param type 
     * @param fieldType 
     * @param forceAccesible 
     * @return 
     */
    public static Field[] fieldsOfType(final Class<?> type,final Class<?> fieldType,
    								   final boolean forceAccesible) {
    	Field[] fields = ReflectionUtils.fieldsOfType(type,fieldType);
    	if (fields != null && fields.length > 0) {
    		for (Field f : fields) ReflectionUtils.makeAccessible(f);
    	}
    	return fields;
    }
    /**
     * Returns all type's {@link Field}s with any of the given types
     * @param type 
     * @param fieldTypes 
     * @return 
     */
    public static Field[] fieldsOfAnyType(final Class<?> type,final Class<?>... fieldTypes) {
    	return ReflectionUtils.fieldsMatching(type,
    										  new Predicate<Field>() {
														@Override
														public boolean apply(final Field f) {
															boolean outApplies = false;
											        		for (Class<?> fieldType : fieldTypes) {
												        		if (ReflectionUtils.isSubClassOf(f.getType(),fieldType)) {
												        			outApplies = true;
												        			break;
												        		}
											        		}
											        		return outApplies;
														}
    										  });
    }
    /**
     * Returns a type's {@link Field}s
     * @param type 
     * @param fieldName  
     * @return 
     */
    public static Class<?> fieldType(final Class<?> type,final String fieldName) {
    	Field f = ReflectionUtils.field(type,fieldName,false);
    	return f.getType();
    }
    /**
     * Returns all fields matching a Predicate
     * @param type
     * @param predicate
     * @return
     */
    public static Field[] fieldsMatching(final Class<?> type,final Predicate<Field> predicate) {
    	List<Field> outFields = new ArrayList<Field>();
    	// PROBLEMA:    class.getFields()         devuelve solo miembros PUBLICOS
    	//              class.getDeclaredFields() devuelve miembros publicos y privados declarados
        //                                        en la propia clase (ignora la herencia)
        for (Class<?> c = type; c != Object.class; c = c.getSuperclass()) {
        	for (Field f : c.getDeclaredFields()) {
        		if (f.getName().startsWith("ajc$")) continue;	// Obviar campos inyectados por AspectJ
        		if (predicate.apply(f)) outFields.add(f);
        	}
        }
        return outFields != null && outFields.size() > 0 ? outFields.toArray(new Field[outFields.size()]) 
        												 : new Field[] {};
    }
    /**
     * Finds a {@link Field} suitable for the given object
     * @param containerObject 
     * @param memberInstance 
     * @return 
     */
    public static Field fieldFor(final Object containerObject,final Object memberInstance) {
    	if (containerObject == null || memberInstance == null) return null;
    	
    	Field outField = null;
		Field[] mapFields = ReflectionUtils.fieldsOfType(containerObject.getClass(),memberInstance.getClass(),true);		// fields del tipo del miembro 
		for (Field f : mapFields) {
			Object fValue = ReflectionUtils.fieldValue(containerObject,f,false);		
			if (fValue == memberInstance) {		// <-- key!!
				// wow!! now the Field is knows
				outField = f;
			}
		}
		return outField;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELD SETTERS
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Returns the setter method for a field
     * @param type 
     * @param fieldName 
     * @param memberType 
     * @return 
     */
    private static Method _fieldSetterMethod(final Class<?> type,
    									     final String fieldName,final Class<?> memberType) {
        String setter = null;
        Method outMethod = null;
        if (memberType == Boolean.class || memberType == boolean.class) {
            // FIX for all possible ways to name a boolean accessor
            // JavaDoc says:
            //      field: a      accessor: setA
            //      field: isA    accessor: setA
        	
            // Find the accessor method
            // The developer might have not name the field correctly
            // For example
            //      [OK] 	fieldName = myBoolean     -->    writeMethod = setMyBoolean     --> prop.getName = myBoolean 
            //      [OK]	fieldName = isMyBoolean   -->    writeMethod = setIsMyBoolean   --> prop.getName = isMyBoolean
            //      [ERROR] fieldName = isMyBoolean   -->    writeMethod = setMyBoolean     --> prop.getName = myBoolean        	
            try {
            	// Usual case: correctly named accessor
                setter = "set" + StringUtils.capitalize(fieldName);            	
                outMethod = ReflectionUtils.method(type,setter,memberType);
            } catch(ReflectionException nsmEx) {
                // Error: the developer has NOT named the setter method
            	//			ie: get[fieldName] (ie setMyBoolean when it should have been setIsMyBoolean)	
            	if (nsmEx.isNoMethodException()) {
		            setter = "set" + (fieldName.startsWith("is") ? StringUtils.capitalize(fieldName.substring(2))	// remove "is"
		                                                         : StringUtils.capitalize(fieldName));	
            	}
            }
        } else {
             setter = "set" + StringUtils.capitalize(fieldName);
        }
        // Return the method (it might have been obtained before)
        try {
        	if (outMethod == null && setter != null) outMethod = ReflectionUtils.method(type,setter,memberType);        	
        } catch(ReflectionException nsmEx) {
        	if (nsmEx.isNoMethodException()) outMethod = null;
        }
        return outMethod;
    }
    /**
     * Sets a member value by directly accessing the field (does not use field accessors -getters/setters-)
     * @param type 
     * @param obj 
     * @param fieldName 
     * @param value  
     * @return true if the field's value was setted
     * @throws ReflectionException
     */
    private static boolean _setFieldValueWithoutUsingAccessors(final Class<?> type,
    														   final Object obj,final String fieldName,
    														   final Object value) {
    	boolean outSetted = false;
        // Do not use accessors; use direct field access
        Field memberField = ReflectionUtils.fieldOrNull(type,fieldName,true);
        if (memberField != null) {
	        try {
				memberField.set( obj,value );
			} catch (IllegalArgumentException illArgEx) {
				throw ReflectionException.illegalArgumentException(memberField.getType(),
																   value.getClass()); 
			} catch (Throwable th) {
			    throw ReflectionException.of(th);		    				
			}  
	        outSetted = true;
        }
        return outSetted;
    }    
    /**
     * Sets a static {@link Field}'s value
     * @param type
     * @param fieldName
     * @param value 
     * @throws ReflectionException
     */
    public static void setStaticFieldValue(final Class<?> type,final String fieldName,
    									   final Object value) {
    	boolean setted = _setFieldValueWithoutUsingAccessors(type,null,fieldName,value);
    	if (!setted) throw ReflectionException.noFieldException(type,fieldName);
    }
    /**
     * Sets a static {@link Field}'s value
     * @param obj 
     * @param field 
     * @param value 
     * @param useAccessors true if using get/set accessor methods
     * @throws ReflectionException 
     */
    public static void setFieldValue(final Object obj,
    								 final Field field,final Object value,
    								 final boolean useAccessors) {
    	ReflectionUtils.setFieldValue(obj,field.getName(),value,useAccessors);
    }   
    /**
     * Sets a {@link Field}s value either using accessor methods or directly accessing the {@link Field} 
     * @param obj 
     * @param fieldName 
     * @param value
     * @throws ReflectionException 
     */
    public static void setFieldValue(final Object obj,
    							     final String fieldName,final Object value) {
    	ReflectionUtils.setFieldValue(obj,fieldName,value,true);
    }     
    /**
     * Sets the value of an object's field either using field direct access or using an accessor setter (set[fieldName])
     * @param obj the object
     * @param fieldName the field name
     * @param value the field value
     * @param useAccessor if accessors should be used
     * @throws ReflectionException 
     */
    public static void setFieldValue(final Object obj,
    								 final String fieldName,final Object value,
    								 final boolean useAccessor) {
        if (value == null) return;      // do not even try
        if (obj == null) throw new IllegalArgumentException(Throwables.message("Cannot set {} on a null object",fieldName));
        try {	      
	        if (useAccessor) {	        	
	            try {
            		// Use accessor methods
            		Method setter = _fieldSetterMethod(obj.getClass(),fieldName,value.getClass());
            		if (setter == null) throw ReflectionException.noMethodException(obj.getClass(),fieldName);
					setter.invoke(obj,value);
	            	// PropertyUtils.setProperty(obj,fieldName,value);
	            } catch (Throwable th) {
	            	_warnFieldAccessException(th,
	            							  obj,fieldName);
	                // ERROR!!! The field's setter method does NOT exists. Another non-ortodox way is attempted               
	                // Be careful when invoked on Maps or Lists... ALWAYS USE THE INTERFACE!!	                
	                Class<?> valueType = CollectionUtils.getCollectionType(value.getClass());
	                if (valueType == null) valueType = value.getClass();	// if this is NOT a collection is a "normal" type		                	
                	Method accessorMethod = _fieldSetterMethod(obj.getClass(),fieldName,valueType);	                	
	                if (accessorMethod != null) {
	                	ReflectionUtils.invokeMethod(obj,accessorMethod,value);
	                } else {
	                	// direct-field access...no accessors
                		ReflectionUtils.setFieldValue(obj,fieldName,value,false);	// try field direct access		                	
	                }
	            }
	        } else {
	            // Do not use setter accessor methods: use direct field access
	        	boolean setted = _setFieldValueWithoutUsingAccessors(obj.getClass(),obj,fieldName,value);
	        	if (!setted) {
	        		log.trace("The field {}'s setter method does NOT exists in type {} > " +
	        				  "Although it's value will be set directly to _{}, it's recommended to add a setter method (do not matter if the setter method is private",
	        				  fieldName,obj.getClass(),fieldName);
	        		setted = _setFieldValueWithoutUsingAccessors(obj.getClass(),obj,"_" + fieldName,value);
	        	}
		    	if (!setted) throw ReflectionException.noFieldException(obj.getClass(),fieldName); 
	        }
        } catch(Throwable th) {
        	log.error("Error trying to set a {} object's {} field with a {} value",
        			  obj.getClass(),fieldName,value.getClass());
        	throw ReflectionException.of(th);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELD GETTERS
/////////////////////////////////////////////////////////////////////////////////////////     
    /**
     * Gets a field's getter name
     * @param type 
     * @param fieldName 
     * @param memberType 
     * @return 
     */
    public static Method _fieldGetterMethod(final Class<?> type,
    									    final String fieldName,final Class<?> fieldType) {
    	// If the field name starts with "_" remove this char
    	String theFieldName = fieldName.startsWith("_") ? fieldName.substring(1) 
    													: fieldName;
    	
    	Method outMethod = null;
        String getter = null;
        if (fieldType != null 
        && (fieldType == Boolean.class || fieldType == boolean.class)) {
            // FIX for all possible ways to name a boolean accessor
            // JavaDoc says:
            //      field: a      accessor: getA
            //      field: isA    accessor: getA
        	
            // Find the accessor method
            // The developer might have not name the field correctly
            // For example
            //      [OK] 	fieldName = myBoolean     -->    readMethod = isMyBoolean      --> prop.getName = myBoolean 
            //      [OK]	fieldName = isMyBoolean   -->    writeMethod = isIsMyBoolean   --> prop.getName = isMyBoolean
            //      [ERROR] fieldName = isMyBoolean   -->    writeMethod = esMyBoolean    --> prop.getName = myBoolean
            try {
            	// Usual case
                getter = "is" + StringUtils.capitalize(theFieldName); 
                outMethod = ReflectionUtils.method(type,getter,fieldType);
            } catch(ReflectionException nsmEx) {
            	if (nsmEx.isNoMethodException()) {
	                // Error, el desarrollador NO ha nombrado el metodo de la forma
	                // estandar y ha utilizado get[fieldName] (ej: getMyBoolean cuando deber�a ser getIsMyBoolean() o isMyBoolean()
		            getter = "is" + (theFieldName.startsWith("is") ? StringUtils.capitalize(theFieldName.substring(2))	// quitar el is
		                                                           : StringUtils.capitalize(theFieldName));   
		            try {
		            	outMethod = ReflectionUtils.method(type,getter,fieldType);
		            } catch (ReflectionException nsmEx2) {
		            	if (nsmEx2.isNoMethodException()) {
		            		// ultima opcion... comienza por get
		            		getter = "get" + (theFieldName.startsWith("is") ? StringUtils.capitalize(theFieldName.substring(2))	// quitar el is
		                                                        		 	: StringUtils.capitalize(theFieldName));
		            	}
		            }
		            
            	}
            }
        } else {
            getter = "get" + StringUtils.capitalize(theFieldName);
        }
        // Devolver el metodo (puede ser que ya se haya obtenido antes)
        try {
        	if (outMethod == null && getter != null) outMethod = ReflectionUtils.method(type,getter,fieldType);        	
        } catch(ReflectionException nsmEx) {
        	if (log.isTraceEnabled()) log.trace("[R01F.ReflectionUtils WARN] ---->" + (fieldType != null ? fieldType.getName() : "unknown type") + " " + 
        															  				   type.getName() + "." + getter + " NOT FOUND!!!");
        	if (nsmEx.isNoFieldExcepton()) outMethod = null;
        }
        return outMethod;
    }
    /**
     * Devuelve el valor de un miembro SIN utilizar metodos get/set
     * @param obj el bean del que devolver el valor del campo
     * @param fieldName nombre del campo
     * @throws ReflectionException si se produce alguna excepci�n en el proceso
     */
	@SuppressWarnings("unchecked")
    private static <T> T _getFieldValueWithoutUsingAccessors(final Object obj,final String fieldName) {
        // No utilizar m�todos de acceso, directamente establecer el valor del miembro...
    	T outObj = null;
        Field memberField = ReflectionUtils.field(obj.getClass(),fieldName,true);
        try {
			outObj = (T)memberField.get( obj );
		} catch (Throwable th) {
		    throw ReflectionException.of(th); 
		}
        return outObj;
	}
    /**
     * Devuelve el valor de un miembro estatico
     * @param type definicion de la clase
     * @param fieldName nombre del campo
     * @return el valor del miembro est�tico
     * @throws ReflectionException si se produce alguna excepci�n en el proceso
     */
	@SuppressWarnings("unchecked")
    public static <T> T getStaticFieldValue(final Class<?> type,final String fieldName) {
        // No utilizar m�todos de acceso, directamente establecer el valor del miembro...
    	T outObj = null;
        Field memberField = ReflectionUtils.field(type,fieldName,true);
        try {
			outObj = (T)memberField.get( null );	// es un tield est�tico
		} catch (Throwable th) {
		    throw ReflectionException.of(th); 
		}
        return outObj;
    } 
    /**
     * Devuelve el valor de un miembro de una clase
     * @param obj el objeto cuyo miembro hay que devolver
     * @param field el miembro cuyo valor hay que devolver
     * @param useAccessor true si se utilizan metodos get/set
     * @return el valor del miembro
     * @throws ReflectionException si no se puede devolver el valor del miembro
     */
    public static <T> T fieldValue(final Object obj,final Field field,
    							   final boolean useAccessor) {    	
    	T outObj = ReflectionUtils.<T>fieldValue(obj,field.getName(),useAccessor,
    										  field.getType());
    	return outObj;
    }   
    /**
     * Devuelve el valor de un miembro de una clase
     * @param obj el objeto cuyo miembro hay que devolver
     * @param _field el miembro cuyo valor hay que devolver
     * @param useAccessor true si se utilizan metodos get/set
     * @return el valor del miembro
     * @throws ReflectionException si no se puede devolver el valor del miembro
     */    
    public static <T> T fieldValue(final Object obj,final String fieldName,
    							   final boolean useAccessor) {
    	@SuppressWarnings("unchecked")    	
    	T outObj = (T)ReflectionUtils.fieldValue(obj,fieldName,useAccessor,
    											 null);
    	return outObj;    	
    }
    /**
     * Obtiene el valor de un miembro en un objeto, bien accediendo directamente al miembro
     * o bien utilizando un accessor (get[fieldName])
     * @param obj El objeto
     * @param fieldName El nombre del miembro
     * @param useAccessor Si hay que utilizar accessors
     * @param fieldType type of returned field
     * @return El valor del miembro
     * @throws ReflectionException si se produce alguna excepcion en el proceso
     */
	public static <T> T fieldValue(final Object obj,final String fieldName,final boolean useAccessor,
								   final Class<?> fieldType) {
		if (fieldName == null) throw new IllegalArgumentException("The field name cannot be null");
        if (obj == null) throw new IllegalArgumentException(Throwables.message("The field {} value cannot be null!",fieldName));
        try {
        	Object outObj = null;
            if (useAccessor) {
            	try {    	 
            		// Utilizar m�todos accessor
            		Method getter = _fieldGetterMethod(obj.getClass(),fieldName,fieldType);
            		if (getter == null) throw ReflectionException.noMethodException(obj.getClass(),fieldName + " getter");
					outObj = getter.invoke(obj,(Object[])null);
            		//outObj = PropertyUtils.getProperty(obj,fieldName);
            	} catch(Throwable th) {
	            	_warnFieldAccessException(th,
	            							  obj,fieldName);
            		// Puede saltar la excepcion java.lang.NoClassDefFoundError: org/apache/commons/logging/LogFactory
            		// debido a que falta el JAR de apache commons logging
            		
	                // ERROR!!! NO existe el metodo setter para la propiedad. Se intenta de otra forma "no ortodoxa"               
	                // Invocarlo... cuidado con los mapas y listas...INVOCAR SIEMPRE CON EL INTERFAZ!!!	                
	                Class<?> valueType = fieldType != null ? CollectionUtils.getCollectionType(fieldType) : null;
	                if (valueType == null) valueType = fieldType;	// Si no es una colecci�n es un tipo "normal"		                	
                	Method accessorMethod = _fieldGetterMethod(obj.getClass(),fieldName,valueType);	                	
	                if (accessorMethod != null) {
	                	outObj = ReflectionUtils.invokeMethod(obj,accessorMethod);
	                } else {
	                	// acceder al miembro directamente... si accessors
                		outObj = ReflectionUtils.fieldValue(obj,fieldName,false,
                											null);	// intentarlo accediendo directamente al field		                	
	                }          		
            	}
            } else {
	            // No utilizar m�todos de acceso, directamente obtener el valor del miembro...
            	outObj = _getFieldValueWithoutUsingAccessors(obj,fieldName);
            }
            @SuppressWarnings("unchecked")            
            T theObj = (T)outObj;
            return theObj;
        } catch (Throwable th) {
            throw ReflectionException.of(th);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _warnFieldAccessException(final Throwable th,
										   		  final Object obj,final String fieldName) {
		if (log.isTraceEnabled()) {
    		StringBuilder methodsDbg = new StringBuilder();
    		Method[] methods = ReflectionUtils.allMethods(obj.getClass());
    		for (Method m : methods) {
    			methodsDbg.append("\t-").append(m.getName()).append("\r\n");
    		}
    		log.trace("\r\n**** WARNING: R01F > ReflectionUtils: This is not strictly an error; an attempt to set the {} value in type {} was made using accessor, but NO accessor war found... the property is going to be accessed directly getting the field value!\r\n" +
    				  "****                 The following is not strictly an error, but it's better if you correct it",
    				  fieldName,obj.getClass().getName());
    		log.trace(methodsDbg.toString());
    		if (log.isTraceEnabled()) th.printStackTrace(System.out);
		}
	}
///////////////////////////////////////////////////////////////////////////////
//	ANOTACIONES DE UN FIELD
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene todos los fields de una clase relacionados con su anotacion
	 * @param type el tipo
	 * @return un mapa de campos anotados indexados por la anotaci�n
	 */
	@SuppressWarnings({ "rawtypes","unchecked" })
	public static Map<Class<? extends Annotation>,FieldAnnotated<? extends Annotation>[]> fieldsAnnotated(final Class<?> type) {
		Map<Class<? extends Annotation>,List<FieldAnnotated<? extends Annotation>>> fieldsAnnotatedMap = new HashMap<Class<? extends Annotation>,List<FieldAnnotated<? extends Annotation>>>();
		Field[] fields = ReflectionUtils.allFields(type);
		if (fields != null && fields.length > 0) {
			for (Field f : fields) {
				Annotation[] annots = f.getAnnotations();
				if (annots != null) {
					for (Annotation annot : annots) {						
						List<FieldAnnotated<? extends Annotation>> fieldsAnnotated = fieldsAnnotatedMap.get(annot);
						if (fieldsAnnotated == null) {
							fieldsAnnotated = new ArrayList<FieldAnnotated<? extends Annotation>>();
							fieldsAnnotatedMap.put(annot.getClass(),fieldsAnnotated);
						}
						fieldsAnnotated.add(new FieldAnnotated(f,annot));
					}
				}
			}
		}
		Map<Class<? extends Annotation>,FieldAnnotated<? extends Annotation>[]> outFields = null;
		if (fieldsAnnotatedMap != null && fieldsAnnotatedMap.size() > 0) {
			outFields = new HashMap<Class<? extends Annotation>,FieldAnnotated<? extends Annotation>[]>(fieldsAnnotatedMap.size());
			for (Map.Entry<Class<? extends Annotation>,List<FieldAnnotated<? extends Annotation>>> me : fieldsAnnotatedMap.entrySet()) {
				outFields.put(me.getKey(),me.getValue().toArray(new FieldAnnotated[me.getValue().size()]));
			}			
		}
		return outFields;		
	}
	/**
	 * Returns a type's fields annotated with a given annotation
	 * @param type 
	 * @param annotationType 
	 * @return an array of {@link FieldAnnotated} that encapsulates the field and the annotation
	 */
	@SuppressWarnings({ "unchecked","rawtypes" })
	public static <A extends Annotation> FieldAnnotated<A>[] fieldsAnnotated(final Class<?> type,
										  		   						     final Class<A> annotationType) {		
		List<FieldAnnotated<? extends Annotation>> outList = new ArrayList<FieldAnnotated<? extends Annotation>>();
		Field[] fields = ReflectionUtils.allFields(type);
		if (fields != null && fields.length > 0) {
			for (Field f : fields) {			
				Annotation annot = f.getAnnotation(annotationType);
				if (annot != null) outList.add(new FieldAnnotated(f,annot));
			}
		}
		return outList != null && outList.size() > 0 ? outList.toArray(new FieldAnnotated[outList.size()])
													 : null;
	}
	/**
	 * Returns a type's fields annotated with a given annotation
	 * @param type 
	 * @param annotationTypes 
	 * @return an array of {@link FieldAnnotated} that encapsulates the field and the annotation
	 */
	public static FieldAnnotated<? extends Annotation>[] fieldsAnnotated(final Class<?> type,
										  		   	  					 final Class<? extends Annotation>... annotationTypes) {	
		Collection<FieldAnnotated<? extends Annotation>> outFieldsAnnotated = Lists.newArrayList();
		for (Class<? extends Annotation> ann : annotationTypes) {
			FieldAnnotated<? extends Annotation>[] fsAnn = ReflectionUtils.fieldsAnnotated(type,
																						   ann);
			if (CollectionUtils.hasData(fsAnn)) outFieldsAnnotated.addAll(Lists.newArrayList(fsAnn));
		}
		return outFieldsAnnotated.toArray(new FieldAnnotated<?>[outFieldsAnnotated.size()]);
	}
//	/**
//	 * Devuelve los fields de una clase anotados con varias anotaciones
//	 * @param type el tipo de la clase
//	 * @param annotationTypes las anotaciones que debe tener el tipo
//	 * @return un array de objetos {@link Field}
//	 */
//	public static Field[] fieldsAnnotatedWithAll(Class<?> type,
//									   		   	 Class<? extends Annotation>... annotationTypes) {
//		List<Field> outList = new ArrayList<Field>();
//		Field[] fields = ReflectionUtils.allFields(type);
//		if (fields != null && fields.length > 0) {
//			for (Field f : fields) {
//				boolean allAnnotations = true;
//				for (Class<? extends Annotation> annotationType : annotationTypes) {
//					Annotation annot = f.getAnnotation(annotationType);
//					if (annot == null) {
//						allAnnotations = false;
//						break;
//					}
//				}
//				if (allAnnotations) outList.add(f);
//			}
//		}
//		if (!CollectionUtils.isNullOrEmpty(outList)) return outList.toArray(new Field[outList.size()]);
//		return null;
//	}
	@Accessors(prefix="_")
	@AllArgsConstructor
	public static class FieldAnnotated<A extends Annotation> {
		@Getter private final Field _field;
		@Getter private final A _annotation;		
	}
///////////////////////////////////////////////////////////////////////////////
//	ACCESIBILIDAD DE CAMPOS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Establece el flag <code>accessible</code> en el <code>{@link AccessibleObject}</code>
	 * ignorando excepciones
	 * 
	 * @param o the given <code>AccessibleObject</code>.
	 * @param accesible el valor del flag
	 */
	public static void setAccessibleIgnoringExceptions(final AccessibleObject o,final boolean accessible) {
		try {
			ReflectionUtils.setAccessible(o,accessible);
		} catch (RuntimeException ignored) {
			/* ignore */
		}
	}
	/**
	 * Establece el flag <code>accessible</code> en el <code>{@link AccessibleObject}</code>
	 * 
	 * @param o el objeto <code>AccessibleObject</code>.
	 * @throws SecurityException si no se puede establecer el flag
	 */
	public static void makeAccessible(final AccessibleObject o) {
		ReflectionUtils.setAccessible(o,true);
	}
	/**
	 * Sets the <code>accessible</code> flag of the given <code>{@link AccessibleObject}</code> to the given <code>boolean</code> value.
	 * 
	 * @param o the given <code>AccessibleObject</code>.
	 * @param accessible the value to set the <code>accessible</code> flag to.
	 * @throws SecurityException if the request is denied.
	 */
	public static void setAccessible(final AccessibleObject o,final boolean accessible) {
		AccessController.doPrivileged(new SetAccessibleAction(o,accessible));
	}
	// ::::::------ INNER CLASS	
	private static class SetAccessibleAction implements PrivilegedAction<Void> {
		private final AccessibleObject _obj;
		private final boolean _accessible;

		SetAccessibleAction(final AccessibleObject o,final boolean accessible) {
			_obj = o;
			_accessible = accessible;
		}
		@Override
		public Void run() {
			_obj.setAccessible(_accessible);
			return null;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELD
/////////////////////////////////////////////////////////////////////////////////////////
	public static Class<?> fieldType(final Class<?> objectType,final Field field) {
	    return (Class<?>)_guessFieldType(objectType,field).type;
	}
	@AllArgsConstructor
	private static class TypeInfo {
	    Type type;
	    Type name;
	}
	private static TypeInfo _guessFieldType(final Class<?> clazz,final Field field) {
	    TypeInfo type = new TypeInfo(null,null);
	    
	    if (field.getGenericType() instanceof TypeVariable<?>) {
	    	// A generic field
	    	//		public class MyGenericType<T> {
	    	//			private T _myGenericField;
	    	//		}
	    	// Try to guess the generic class parameter type
			// see http://blog.vityuk.com/2011/03/java-generics-and-reflection.html
			// The java.lang.reflect.Type has some sub-clases:
			// 		java.lang.reflect.Type
			//			|-- java.lang.Class 						-> "normal" class
			//			|-- java.lang.reflect.ParameterizedType		-> class with a generic parameter (ie: String at List<String>)
			//			|-- java.lang.reflect.TypeVariable			-> Generic parameter of a class (ie: T at List<T>)
			//			|-- java.lang.reflect.WildcardType			-> wildcard type (ie: ? extends Number at List<? extends Number>
			//			|-- java.lang.reflect.GenericArrayType		-> Generic type of an array (ie: T en T[])
	        TypeVariable<?> genericTyp = (TypeVariable<?>)field.getGenericType();
	        Class<?> superClazz = clazz.getSuperclass();

	        if (clazz.getGenericSuperclass() instanceof ParameterizedType) {
	            ParameterizedType paramType = (ParameterizedType)clazz.getGenericSuperclass();
	            TypeVariable<?>[] superTypeParameters = superClazz.getTypeParameters();
	            if (!Object.class.equals(paramType)) {
	                if (field.getDeclaringClass().equals(superClazz)) {
	                    // this is the root class an starting point for this search
	                    type.name = genericTyp;
	                    type.type = null;
	                } else {
	                    type = _guessFieldType(superClazz,field);
	                }
	            }
	            if (type.type == null || type.type instanceof TypeVariable<?>) {
	                // lookup if type is not found or type needs a lookup in current concrete class
	                for (int j = 0; j < superClazz.getTypeParameters().length; ++j) {
	                    TypeVariable<?> superTypeParam = superTypeParameters[j];
	                    if (type.name.equals(superTypeParam)) {
	                        type.type = paramType.getActualTypeArguments()[j];
	                        Type[] typeParameters = clazz.getTypeParameters();
	                        if (typeParameters.length > 0) {
	                            for (Type typeParam : typeParameters) {
	                                TypeVariable<?> objectOfComparison = superTypeParam;
	                                if (type.type instanceof TypeVariable<?>) {
	                                    objectOfComparison = (TypeVariable<?>)type.type;
	                                }
	                                if (objectOfComparison.getName().equals(((TypeVariable<?>) typeParam).getName())) {
	                                    type.name = typeParam;
	                                    break;
	                                }
	                            }
	                        }
	                        break;
	                    }
	                }
	            }
	        }
	    } else {
	    	// Not generic type 
	        type.type = field.getGenericType();
	    }
	    return type;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS DE ACCESO A MIEMBROS EN BASE A UN PATH
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Funcion que se encarga de establecer el valor de un miembro en una jerarqu�a de objetos
     * El path al miembro se pasa como parametro en la variable memberPath que
     * tiene la siguiente estructura:
     *      obj.member.member.member...
     * NOTAS:
     *      - Se comprueba si alg�n miembro de la cadena est� creado o no, en cuyo caso se crea
     *      - NO se contemplan arrays o listas...
     * @param obj
     * @param memberPath
     * @param memberValue el valor del miembro final del path
     * @param useAccesors si hay que utilizar m�todos get/set
     * @throws ReflectionException Si se da alguna excepcion al acceder al miembro
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException Si el miembro solicitado en el path no existe
     */
    public static void setFieldValueUsingPath(final Object obj,final String memberPath,final Object memberValue,
    									      final boolean useAccesors) {
        if (obj == null || memberPath == null) throw new IllegalArgumentException("Either the object or the path are null");

        // Tokenizar el path e ir escarbando...
        StringTokenizer st = new StringTokenizer(memberPath,".");
        String currFieldName = null;
        Object currObj = obj;
        do {
            currFieldName = st.nextToken();    // Siguiente token en el path
            if (st.hasMoreTokens()) {
                // No es el elemento final... comprobar si el miembro padre est� creado y si no es as� hacerlo
                Object instance = ReflectionUtils.fieldValue(currObj,currFieldName,useAccesors,
                											 null);
                if (instance == null) {
                    Class<?> currInstanceClass = null;
                    if (useAccesors) {
                        currInstanceClass = ReflectionUtils.fieldType(currObj.getClass(),currFieldName);
                    } else {
                        currInstanceClass = ReflectionUtils.field(currObj.getClass(),currFieldName,true).getType();
                    }
                    if ( !currInstanceClass.isArray()
                      && !CollectionUtils.isCollection(currInstanceClass)
                      && !CollectionUtils.isMap(currInstanceClass)) {
                        instance = ReflectionUtils.createInstanceOf(currInstanceClass);
                    } else {
                    	throw new IllegalArgumentException("ReflectionUtils.setFieldValueUsingPath() method does NOT supports array, List or Map elements in the path");
                    }
                    ReflectionUtils.setFieldValue(currObj,currFieldName,instance,useAccesors);
                }
                currObj = instance;
            } else {
                // Es el �ltimo elemento.. establecer directamente el resultado
                ReflectionUtils.setFieldValue(currObj,currFieldName,
                                              memberValue,useAccesors);
            }
        } while(st.hasMoreTokens());
    }
    /**
     * Funcion que se encarga de obtener un valor de una jerarquia de objetos
     * El path al miembro se pasa como parametro en la variable memberPath que
     * tiene la siguiente estructura:
     *      obj.member.member.member...
     * Casos especiales:
     *      - Si un miembro de la jerarquia es un array o lista, la nomenclatura es:
     *          obj.member.member[2].member.... (entre corchetes va el �ndice en el array o lista)
     *      - Si un miembro de la jerarqu�a es un mapa, la nomenclatura es:
     *          obj.member.member(oid).member...(entre corchetes va una CADENA con la clave en el mapa)
     *          (obviamente, se restringe a mapas indexados por strings)
     *
     * @param obj El objeto en el cual se ha de obtener el miembro
     * @param memberPath El path del miembro ej: member.innnerMember.member...
     * @param useAccesors si hay que utilizar m�todos de acceso al usuario
     * @return El objeto solicitado
     * @throws ReflectionException Si se da alguna excepcion al acceder al miembro;
     *         ArrayIndexOutOfBoundsException Si un miembro del path es un array y el indice solicitado se sale de los l�mites
     *         IllegalAccessException Si no se puede acceder a un miembro especificado en el path
     *         NoSuchFieldException Si el miembro solicitado en el path no existe
     *         NumberFormatException Si el formato para un �ndice en un array o lista no es valido
     */
    public static Object fieldValueUsingPath(final Object obj,final String memberPath,
    									     final boolean useAccesors) {
        if (obj == null || memberPath == null) throw new IllegalArgumentException("El objeto o el path NO son v�lidos");

        // No utilizar accessors...Tokenizar el path e ir escarbando...
        StringTokenizer st = new StringTokenizer(memberPath,".");

        if (st.hasMoreTokens()) {
            try {
                Object currObj = obj;                       // El objeto actual mientras se escarba...
                Class<?> currObjClass = currObj.getClass(); // La clase del objeto actual...
                Field currObjField = null;                  // El miembro actual
                String[] currPathElem = null;               // El nombre del miembro
                do {
                   currPathElem = _parsePathElem(st.nextToken());
                   //log.debug(">>>>>" + currPathElem[0] + "-" +  currPathElem[1]);

                   // Obtener el miembro y su valor...
                   if (useAccesors) {
                       currObj = ReflectionUtils.fieldValue(currObj,currPathElem[0],useAccesors,
                    		   								null);
                       if (currObj != null) currObjClass = currObj.getClass();
                       // log.debug(">>>>>>" + currObjClass.getName());
                   } else {
                       try {
                           currObjField = ReflectionUtils.field(currObjClass,currPathElem[0],true);
                           if (currObjField != null) {
                               currObj = currObjField.get(currObj);
                               if (currObj != null) currObjClass = currObj.getClass();
                               // log.debug(">>>>>>" + currObjClass.getName());
                           }
                       } catch (IllegalAccessException illAccEx) {
                    	   throw ReflectionException.of(illAccEx);
                       }
                   }
                   // Una vez que se tiene el miembro, si se trata de un array, lista o mapa, obtener
                   // el valor adecuado...
                   if (currPathElem[1] != null) {
                       boolean found = false;
                       if (currObjClass.isArray()) {
                           // log.debug("\t->Array(" + Array.getLength(currObj));
                           currObj = Array.get(currObj,Integer.parseInt(currPathElem[1]));
                           if (currObj != null) currObjClass = currObj.getClass();
                           found = true;
                       } else {
                           Class<?>[] currObjInterfaces = currObjClass.getInterfaces();
                           if (currObjInterfaces != null) {
                               for (int i=0; i < currObjInterfaces.length; i++) {
                                   if (currObjInterfaces[i].equals(List.class)) {
                                       // log.debug("\t->Lista");
                                       List<?> list = (List<?>)currObj;
                                       currObj = list.get(Integer.parseInt(currPathElem[1]));
                                       if (currObj != null) currObjClass = currObj.getClass();
                                       found = true;
                                       break;
                                   } else if (currObjInterfaces[i].equals(Map.class)) {
                                       // log.debug("\t->Mapa");
                                       Map<?,?> map = (Map<?,?>)currObj;
                                       currObj = map.get(currPathElem[1]);
                                       if (currObj != null) currObjClass = currObj.getClass();
                                       found = true;
                                       break;
                                   }
                               } // del for
                           }
                       }
                       // Si no se ha encontrado... a cascarla..
                       if (!found) throw new IllegalArgumentException(Throwables.message("A path field type is thought to be an array, List or Map BUT the real type is {} '",currObjClass.getName()));
                   }
                } while (st.hasMoreTokens());

                // Devolver el objeto...
                return currObj;
            } catch (ArrayIndexOutOfBoundsException aiobEx) {
                throw new IllegalArgumentException("ArrayIndexOutOfBounds: The array element does NOT exists (" + memberPath + ")",
                								   aiobEx);
            }
        }
        return null;
    }
    /**
     * Obtiene el nombre del miembro y el index o clave en el caso
     * de que elemento del path sea una referencia a array, lista o mapa
     * @param pathElem El elemento del path
     * @return Un array de dos posiciones con Strings
     *              - En la primera posici�n va el nombre del miembro
     *              - En la segunda posici�n va el indice o clave si lo hay
     *                o null si no hay indice o clave
     */
    private static String[] _parsePathElem(final String pathElem) {
        String fieldName = pathElem;
        String ord = null;
        int p = -1;
        if ( (p = pathElem.indexOf("[")) > 0 ) {
           fieldName = pathElem.substring(0,p);
           ord = pathElem.substring(p+1,pathElem.length()-1);
        }
        // Componer el array de salida
        String[] out = new String[2];
        out[0] = fieldName;
        out[1] = ord;
        return out;
    }


/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS DE DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Compone la signatura de un metodo: clase.metodo(params..)
     * @param className El nombre de la clase
     * @param methodName El nombre del metodos
     * @param paramTypes La definicion de los tipos de los argumentos
     * @return La signatura del metodo: clase.metodo(params)
     */
    public static String composeMethodSignature(final String className,
    											final String methodName,final Class<?>[] paramTypes) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(className != null ? className : "[unknown class]");
    	sb.append(".");
    	sb.append(methodName != null ? methodName : "<constructor>");
    	sb.append("(");
        sb.append(ArrayFormatter.format(paramTypes));
        sb.append(")");
        return sb.toString();
    }
    /**
     * Compone la signatura de un metodo: clase.metodo(params..)
     * @param m La definicion del metodo
     * @return La signatura del metodo: clase.metodo(params...)
     */
    public static String composeMethodSignature(final Method m) {
        if (m == null) {
            return "<metodo nulo>";
        }
        StringBuilder outSignature = new StringBuilder();
        outSignature.append(m.getReturnType().getName());
        outSignature.append(" ");
        outSignature.append(m.getName());
        outSignature.append('(');
        Class<?>[] paramTypes = m.getParameterTypes();
        if (paramTypes != null) {
            for (int i=0; i < paramTypes.length; i++) {
                outSignature.append(paramTypes[i].getName());
                outSignature.append(" param");
                outSignature.append(i);
                if (i < paramTypes.length-1) {
                    outSignature.append(',');
                }
            }
        }
        outSignature.append(')');
        Class<?>[] thrownTypes = m.getExceptionTypes();
        if (thrownTypes != null) {
            outSignature.append(" throws ");
            for (int i=0; i < thrownTypes.length; i++) {
                outSignature.append(thrownTypes[i].getName());
                if (i < thrownTypes.length-1) {
                    outSignature.append(',');
                }
            }
        }
        outSignature.append(';');

        return outSignature.toString();
    }
    /**
     * Imprime informaci�n de debug sobre los metodos de una clase
     * @param type La definicion de la clase
     * @return una cadena con informacion de debug con la signatura de todos los metodos de la clase
     */
    public static String composeClassMethodsSignatures(final Class<?> type) {
        if (type == null) {
            return "<La definicion de la clase es nula>";
        }
        StringBuilder strDebug = new StringBuilder();
        Method[] methods = ReflectionUtils.allMethods(type);
        if (methods != null) {
            for (int i=0; i<methods.length; i++) {
                strDebug.append( methods[i].getDeclaringClass().getName() );
                strDebug.append(" > ");
                strDebug.append( ReflectionUtils.composeMethodSignature(methods[i]) );
            }
        } else {
            strDebug.append("<La clase ");
            strDebug.append(type);
            strDebug.append(" NO tiene m�todos definidos>");
        }
        return strDebug.toString();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS ELIMINADOS
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Devuelve si una clase es una clase gen�rica parametrizada
//	 * @param type el tipo 
//	 * @return false si el tipo no es la parametrizaci�n de una clase generica
//	 */
//	public static boolean isGenericTypeParameterized(final Class<?> type) {
//		boolean outIsParametrized = false;
//		if (!outIsParametrized) {
//			Type[] interfaces = type.getGenericInterfaces();
//			if (!CollectionUtils.isNullOrEmpty(interfaces)) {
//				for (Type i : interfaces) {
//					if (i instanceof ParameterizedType) {
//						outIsParametrized = true;
//						break;
//					}
//				}
//			}
//		}
//		if (!outIsParametrized) {
//			Type superType = type.getGenericSuperclass();
//			if (superType instanceof ParameterizedType) outIsParametrized = true;
//		}
//		return outIsParametrized;
//	}
//	/**
//	 * Devuelve los par�metros actuales (el tipo concreto) de una clase gen�rica parametrizada
//	 * IMPORTANTE:
//	 * <pre>
//	 * 		public class MyGenericType<T>		:-- NO es una clase generica parametrizada: NO se puede obtener informaci�n del tipo
//	 * 											    en tiempo de ejecuci�n debido al type-erasure
//	 * 		public class MyParametrizedGenericType extends MyGenericType<String>	:-- SI es una clase generica parametrizada
//	 * 		MyGenericType<String> 	:-- SI es una clase generica parametrizada
//	 * 		
//	 * </pre>  
//	 * @param type
//	 * @return
//	 */
//	public static Class<?>[] getGenericParameterizedTypeActualParams(Class<?> type) {
//		List<Class<?>> outTypes = null;
//		
//		// Parametros de las Superclase
//		Type superType = type.getGenericSuperclass();
//		if (superType instanceof ParameterizedType) {
//			ParameterizedType pt = (ParameterizedType)superType;
//			Type[] paramTypes = pt.getActualTypeArguments();
//			// Convertir a class (pero SOLO si es un tipo concreto)
//			if (!CollectionUtils.isNullOrEmpty(paramTypes)) {
//				if (outTypes == null) outTypes = new ArrayList<Class<?>>();
//				for (int i=0; i<paramTypes.length; i++) {
//					if (paramTypes[i] instanceof Class) outTypes.add( (Class<?>)paramTypes[i] );
//				}
//			}
//		}
//		// Parametros de los Interfaces
//		Type[] interfaces = type.getGenericInterfaces();
//		if (!CollectionUtils.isNullOrEmpty(interfaces)) {
//			for (Type intf : interfaces) {
//				if (intf instanceof ParameterizedType) {
//					ParameterizedType pt = (ParameterizedType)intf;
//					Type[] paramTypes = pt.getActualTypeArguments();
//					// Convertir a class (pero SOLO si es un tipo concreto)
//					if (!CollectionUtils.isNullOrEmpty(paramTypes)) {
//						if (outTypes == null) outTypes = new ArrayList<Class<?>>();
//						for (int i=0; i<paramTypes.length; i++) {
//							if (paramTypes[i] instanceof Class) outTypes.add( (Class<?>)paramTypes[i] );
//						}
//					}
//				}
//			}
//		}
//		return outTypes != null ? outTypes.toArray(new Class<?>[outTypes.size()])
//								: null;
//	}
//	/**
//	 * Intenta averiguar el tipo concreto de field generico de una clase
//	 * Por ejemplo, se puede tener la siguiente jerarqu�a de clases:
//	 * <pre class='brush:java'>
//	 * 		public abstract class BaseType<E extends Serializable> {
//	 * 			private E serializableField;
//	 * 		}
//	 *		public abstract class BaseSubType<T extends Number> 
//	 *					  extends BaseType<String> {
//	 *			private T numberField;
//	 *		}
//	 * 		public class ConcreteType
//	 * 			 extends BaseSubType<Integer> { 
//	 * 		}
//	 * </pre>
//	 * Si se tiene la clase ConcreteType y se quiere saber cual es el tipo concreto del miembro 'serializableField', 
//	 * basta con:
//	 * 		1. Obtener las clases que hay entre ConcreteType y la clase que define el miembro 'serializableField'
//	 * 		   (en el caso del ejemplo {ConcreteType,BaseSubType,BaseType}
//	 * 		2. Obtener los par�metros de cada clase de la jerarquia y ver si son asignables al miembro 'serializableField'
//	 * 		   (en el caso del ejemplo la clase BaseSubType tiene un par�metro <String> que es asignable a 'serializableField')  
//	 * @param type el tipo 
//	 * @param f el campo (no tiene por qu� estar definido en type sino que puede estar definido en una super-clase
//	 * @return el tipo concreto del campo
//	 */
//	public static Class<?> guessGenericFieldActualType(final Class<?> type,final Field f) {
//		// Si el field NO es generico... se puede devolver directamente su tipo
//		if (!ReflectionUtils.isGenericField(f)) return f.getType();	// El campo NO es generico
//		
//		// Obtener la jerarqu�a de clases entre el tipo y la clase donde est� definido el field
//		Set<Class<?>> hierarchy = ReflectionUtils.typeHierarchyBetween(type,f.getDeclaringClass());
//		return _guessGenericActualType(hierarchy,f.getType());
//	}
//	/**
//	 * Intenta obtener el tipo actual de un tipo gen�rico a partir de los par�metros de la clase
//	 * Por ejemplo:
//	 * <pre class='brush:java'>
//	 * 		public abstract class BaseType<E extends Serializable> {
//	 * 			public Map<String,E> mapField;
//	 * 		}
//	 * 		public abstract class BaseSubType 
//	 * 				      extends BaseType<String> {
//	 * 		} 
//	 *		public class ConcreteType
//	 *			 extends BaseSubType {
//	 *		}
//	 * </pre>
//	 * Si se quiere conocer cual es el tipo concreto de los elementos del mapa Map<String,E> hay que:
//	 * 		1. Obtener las clases que hay entre ConcreteType y la clase generica BaseType
//	 * 		   (en el caso del ejemplo {ConcreteType,BaseSubType,BaseType}
//	 * 		2. Obtener los par�metros de cada clase de la jerarquia y ver si son asignables a E
//	 * 		   (en el caso del ejemplo la clase BaseSubType tiene un par�metro <String> que es asignable a E)
//	 * @param typeHierarchy jerarqu�a de herencia de tipos desde la clase donde se encuentra el tipo
//	 * 						(es necesaria para intentar encontrar la parametrizaci�n)  
//	 * @param type el tipo del que se quiere conocer el tipo concreto
//	 * @return
//	 */
//	private static Class<?> _guessGenericActualType(final Set<Class<?>> typeHierarchy,
//													final Class<?> type) {
//		Class<?> outType = null;
//		for (Class<?> c : typeHierarchy) {
//			outType = _inferGenericActualTypeFromParameterizedType(c,type);
//			if (outType != null) break;
//		}
//		return outType;
//	}
//	private static Class<?> _inferGenericActualTypeFromParameterizedType(final Class<?> parameterizedType,
//																		 final Class<?> type) {
//		Class<?> outType = null;
//		Class<?>[] genericParameterizedTypeActualParams = ReflectionUtils.getGenericParameterizedTypeActualParams(parameterizedType);
//		if (!CollectionUtils.isNullOrEmpty(genericParameterizedTypeActualParams)) {
//			for (Class<?> param : genericParameterizedTypeActualParams) {
//				if (type.isAssignableFrom(param)) {
//					outType = param;
//					break;
//				}
//			}
//		}
//		return outType;
//	}
}
