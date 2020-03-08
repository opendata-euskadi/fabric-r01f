Java coding standards
=================================================

Project names
-------------------------------------------------
Projects MUST ALWAYS start with the `{appCode}` as `{appCode}{Project}`

ie: if [appCode]=TCK01 a project might be named `TCK01ModelClasses`

There are some main conventions with [project] names:

	Model objects: {appCode}ModelClasses
	Client API   : {appCode}ClientAPIClasses
	CORE         : {appCode}COREClasses
	UI           : {appCode}UIWar
	REST services: {appCode}CORERESTServicesWar
	Config		 : {appCode}ConfigByEnv

Package names
-------------------------------------------------
Packages MUST start ALWAYS with the `[appCode]` as `{appCode}.{package}`

ie: if [appCode]=TCK01 a package might be `tck01.util`

Class names
-------------------------------------------------
Class names MUST start ALWAYS with the `[appCode]` as `{appCode}{Type}`

ie: if [appCode]=TCK01 the a class should be named like `TCK01MyType`


Bracing style:
-------------------------------------------------
Use this brace style:

	public void doSomenting() {
	}

**NOT** this:

	public void doSomenting() 
	{
		...
	}

Spacing:
-------------------------------------------------
Use **4 character tabs**

The `golden rule`: **PUT AN SPACE AFTER A RESERVED WORD**

Wrong:
	
	try {
		if(something) {
			for(int i=0; i<0; i++) { 
				...
			}
		}
	} catch(Throwable th) {
		...
	}

Right (beware the space char AFTER the reserved word):

	try {
		if (something) {
			for (int i=0; i<0; i++) { 
				...
			}
		}
	} catch (Throwable th) {
		...
	}

Comments:
-------------------------------------------------
Leave an space AFTER a line comments:

Wrong:

	//this is a line comment
	
Right: 

	// this is a line comment 
	
Use `separators` wisely:

	/////////////////////////////////////////////////////////////////////////////////////////
	//	SECTION NAME
	/////////////////////////////////////////////////////////////////////////////////////////
	
Indentation:
-------------------------------------------------
Use indentation wisely:

Wrong:

	public void myLongParamsMethod(final String name,final String surname,final Date birthDate,final Phone phone) {
		...
	}
	public void myOtherMethod() {
		this.myLongParamsMethod("Evo","Morales",Phone.of("67456732"),new Date());
	}

Right: try to group params with the same context 

	public void myLongParamsMethod(final String name,final String surname,
											final Date birthDate,
											final Phone phone) {
		...
	}
	public void myOtherMethod() {
		this.myLongParamsMethod("Evo","Morales",
									  birthDate,
									  Phone.of("67456732"));
	}

Wrong indentation of lambda expressions:
	
	textField.addChangeListener(changeEvent -> {
		String val = changeEvent.getValue();
		...
	});
	
Right indentation of lambda expression:

	textField.addChangeListener(changeEvent -> {
											String val = changeEvent.getValue();
											...
										});


Class definitions
-------------------------------------------------
Wrong:

	public class MyType extends MyTypeBase implements MyInterface1,MyInterface2 {
		...
	}
	
Right (beware the indentation):

	public class MyType
		 extends MyTypeBase
	  implements MyInterface1,
				 MyInterface2 {
		...
	}
	     class MyPackageProtectedType
	   extends MyTypeBase
	implements MyInterface {
			...
	}
	
Private and protected fields and methods
-------------------------------------------------
Private [fields] & [methods] MUST ALWAYS named starting with an hipen and
they MUST ALWAYS be refered to using JUST the name (never with `this.{field}`)

Wrong:

	public class MyType {
		private final String myStringField;
		
		private String myMethod(final String param) {
			this.myStringField = "Hey!";
		}
	}
	
Right:

	public class MyType {
		private final String _myStringField;
		
		private String _myMethod(final String param) {
			_myStringField = "Hey!";
		}
	}

Public methods
-------------------------------------------------
Public methods MUST ALWAYS be refered to with `this.{method}` as opposite to private ones
that MUST ALWAYS be refered to using the name (which starts with _)
... this way one can know if a method is public or private just with a glance

Wrong:

	public class MyType {		
		public String myPublicMethod() {
			myOtherPublicMethod();
		}
		public String myOtherPublicMethod() {
			myPrivateMethod();
		}
		private String myPrivateMethod() {
		}
	}
	
Right:

	public class MyType {		
		public String myPublicMethod() {
			this.myOtherPublicMethod();
		}
		public String myOtherPublicMethod() {
			_myPrivateMethod();
		}
		private String _myPrivateMethod() {
		}
	}	
	


Type's definition: fields, constructors & methods
-------------------------------------------------
A type's elements SHOULD ALWAYS be defined in this order:

	1.- Constants (static final fields)
	1.- Instance Fields (NEVER public)
	2.- Constructors
	3.- Public & private methods grouped by function
	
Use a separator like:

	/////////////////////////////////////////////////////////////////////////////////////////
	//	SECTION NAME
	/////////////////////////////////////////////////////////////////////////////////////////

Wrong:

	public class MyType {
		public MyType(final String fieldVal) {
			_myStringField = fieldVal;
		}
		
		public String myMethod() {
	
		}
	
		private final String _myStringField;
	}

Right:

	public class MyType {
	/////////////////////////////////////////////////////////////////////////////////////////
	//	SECTION NAME
	/////////////////////////////////////////////////////////////////////////////////////////
		private final String _myStringField;
		
	/////////////////////////////////////////////////////////////////////////////////////////
	//	CONSTRUCTORS
	/////////////////////////////////////////////////////////////////////////////////////////		
		public MyType(final String fieldVal) {
			_myStringField = fieldVal;
		}
	/////////////////////////////////////////////////////////////////////////////////////////
	//	METHODS
	/////////////////////////////////////////////////////////////////////////////////////////
		public String myMethod() {
	
		}
	}

Method params
-------------------------------------------------
If possible ALL method's params MUST be final

Wrong:
 
		private String myMethod(String param) {
			this.myStringField = "Hey!";
		}

Right:

		private String myMethod(final String param) {
			this.myStringField = "Hey!";
		}

Abstract types
-------------------------------------------------
Name abstract type as {typeName}Base

Wrong:
	
	public abstract class MyAbstractType {
		...
	}

Right:
	
	public abstract class MyTypeBase {
		...
	}
	
Accessors: use Lombok!
-------------------------------------------------
By genral means DO NOT write accessor methods by hand: use lombok

Wrong:

	public class MyType {
		private String _myField;
		
		public String getMyField() {
			return _myField;
		}
		public void setMyField(final String val) {
			_myField = val;
		}
	}

Right:
	
	@Accessors(prefix="_")
	public class MyType {
		@Getter @Setter private String _myField;
	} 

Use interfaces; avoid concrete implementations
-------------------------------------------------
When defining a field or a method NEVER use a concrete implementation, 
use an interface if possible

Wrong:

	public class MyType {
		private final ArrayList<String> _myCol;
		
		public MyType() {
			_myCol = new ArrayList<String>();
		}
	}
	
Right:

	public class MyType {
		private final Collection<String> _myCol;
		
		public MyType() {
			_myCol = new ArrayList<String>();
		}
	}

Use functional style programming if possible
-------------------------------------------------
Avoid traditional `for`, `while`... loops if possible: *use java's stream API* (_or Guava_)

Avoid checked exceptions
-------------------------------------------------
If possible DO NOT use or create checked exceptions

ie: DO NOT create exception types like

	public class MyException
		  extends Exception {
		...		
	}
	public class MyType {
		public void myMethod() throws MyException {
			...
		}
	}

If possible use UNCHECKED exceptions instead:

	public class MyUncheckedExceptions
		  extends RuntimeException {
		...
	}
	public class MyType {
		public void myMethod() {		// no throws clause
			...
		}
	}
	
... BUT it's better to avoid creating custom exception types and reuse common types like
`IllegalArgumentException`, `UnsupportedOperationException`, etc
