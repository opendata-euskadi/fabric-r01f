package r01f.reflection.fluent;

/**
 * Entry point for the fluent api.
 * 
 * Usage example:
 * <pre>
 *   // Loads the class 'org.republic.Jedi'
 *   Class<?> jediType = Reflection.type("org.republic.Jedi").load();
 * 
 *   // Loads the class 'org.republic.Jedi' as 'org.republic.Person' (Jedi extends Person)
 *   Class<Person> jediType = Reflection.type("org.republic.Jedi").loadAs(Person.class);
 * 
 *   // Loads the class 'org.republic.Jedi' using a custom class loader
 *   Class<?> jediType = Reflection.type("org.republic.Jedi").withClassLoader(myClassLoader).load();
 * 
 *   // Gets the inner class 'Master' in the declaring class 'Jedi':
 *   Class<?> masterClass = Reflection.staticInnerClass("Master").in(Jedi.class).get();
 * 
 *   // Equivalent to call 'new Person()'
 *   Person p = Reflection.constructor().in(Person.class).newInstance();
 * 
 *   // Equivalent to call 'new Person("Yoda")'
 *   Person p = Reflection.constructor().withParameterTypes(String.class).in(Person.class).newInstance}("Yoda");
 * 
 *   // Retrieves the value of the field "name"
 *   String name = Reflection.field("name").ofType(String.class).in(person).get();
 * 
 *   // Sets the value of the field "name" to "Yoda"
 *   Reflection.field("name").ofType(String.class).in(person).set("Yoda");
 * 
 *   // Retrieves the value of the field "powers"
 *   List<String> powers = Reflection.field("powers").ofType(new TypeRef<List<String>>() {}).in(jedi).get();
 * 
 *   // Equivalent to call 'person.setName("Luke")'
 *   Reflection.method("setName").withParameterTypes(String.class).in(person).invoke("Luke");
 * 
 *   // Equivalent to call 'jedi.getPowers()'
 *   List<String> powers = Reflection.method("getPowers").withReturnType(new TypeRef<List<String>>() {}).in(person).invoke();
 * 
 *   // Retrieves the value of the static field "count" in Person.class
 *   int count = Reflection.staticField("count").ofType(int.class).in(Person.class).get();
 * 
 *   // Sets the value of the static field "count" to 3 in Person.class
 *   Reflection.staticField("count").ofType(int.class).in(Person.class).set(3);
 * 
 *   // Retrieves the value of the static field "commonPowers" in Jedi.class
 *   List<String> commmonPowers = Reflection.staticField("commonPowers").ofType(new TypeRef<List<String>>() {}).in(Jedi.class).get();
 * 
 *   // Equivalent to call 'person.concentrate()'
 *   Reflection.method("concentrate").in(person).invoke();
 * 
 *   // Equivalent to call 'person.getName()'
 *   String name = Reflection.method("getName").withReturnType(String.class).in(person).invoke();
 * 
 *   // Equivalent to call 'Jedi.setCommonPower("Jump")'
 *   Reflection.staticMethod("setCommonPower").withParameterTypes(String.class).in(Jedi.class).invoke("Jump");
 * 
 *   // Equivalent to call 'Jedi.addPadawan()'
 *   Reflection.staticMethod("addPadawan").in(Jedi.class).invoke();
 * 
 *   // Equivalent to call 'Jedi.commonPowerCount()'
 *   String name = Reflection.staticMethod("commonPowerCount").withReturnType(String.class).in(Jedi.class).invoke();
 * 
 *   // Equivalent to call 'Jedi.getCommonPowers()'
 *   List<String> powers = Reflection.staticMethod("getCommonPowers").withReturnType(new TypeRef<List<String>>() {}).in(Jedi.class).invoke();
 * 
 *   // Retrieves the value of the property "name"
 *   String name = Reflection.property("name").ofType(String.class).in(person).get();
 * 
 *   // Sets the value of the property "name" to "Yoda"
 *   Reflection.property("name").ofType(String.class).in(person).set("Yoda");
 * </pre>
 */

public final class FluentReflection {
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	private FluentReflection() {
		// default constructor
	}
///////////////////////////////////////////////////////////////////////////////
//	INTERFAZ PUBLICA
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Starting point of the fluent interface for loading a class dynamically.
	 * 
	 * @param name the name of the class to load.
	 * @return the starting point of the method chain.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	public static TypeReflection type(final String name) {
		return TypeReflection.startTypeAccess(name);
	}
	/**
	 * Starting point of the fluent interface for accessing static inner class
	 * via reflection.
	 * 
	 * @param innerClassName the name of the static inner class to access.
	 * @return the starting point of the method chain.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	public static StaticInnerClassReflection staticInnerClass(final String innerClassName) {
		return StaticInnerClassReflection.startStaticInnerClassAccess(innerClassName);
	}
	/**
	 * Starting point of the fluent interface for accessing fields via
	 * reflection.
	 * 
	 * @param fieldName the name of the field to access.
	 * @return the starting point of the method chain.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	public static FieldReflection field(final String fieldName) {
		return FieldReflection.startFieldAccess(fieldName);
	}
	/**
	 * Starting point of the fluent interface for accessing static fields via
	 * reflection.
	 * 
	 * @param fieldName the name of the static field to access.
	 * @return the starting point of the method chain.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	public static StaticFieldReflection staticField(final String fieldName) {
		return StaticFieldReflection.startStaticFieldAccess(fieldName);
	}
	/**
	 * Starting point of the fluent interface for invoking methods via
	 * reflection.
	 * 
	 * @param methodName the name of the method to invoke.
	 * @return the starting point of the method chain.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	public static MethodReflection method(final String methodName) {
		return MethodReflection.startMethodAccess(methodName);
	}
	/**
	 * Starting point of the fluent interface for invoking static methods via
	 * reflection.
	 * 
	 * @param name the name of the static method to invoke.
	 * @return the starting point of the static method chain.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	public static StaticMethodReflection staticMethod(final String name) {
		return StaticMethodReflection.startStaticMethodAccess(name);
	}
	/**
	 * Starting point of the fluent interface for invoking constructors via
	 * reflection.
	 * 
	 * @return the starting point of the method chain.
	 */
	public static ConstructorReflection constructor() {
		return ConstructorReflection.startConstructorAccess();
	}
	/**
	 * Starting point of the fluent interface for accessing properties via Bean
	 * Instrospection.
	 * 
	 * @param name the name of the property to access.
	 * @return the starting point of the method chain.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	public static PropertyReflection property(final String name) {
		return PropertyReflection.startPropertyAccess(name);
	}
}
