package r01f.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a field that will be the key when an object is putted into a map
 * when marshalling / unmarshalling
 * <pre class='brush:java'>
 * 		@TypeMarshall(as="myType")
 * 		public class MyType {
 * 			@MarshallField(as="key",
 * 						   whenXml=@MarshallXmlField(attr=true))
 * 			@OidField
 * 			@Getter @Setter private String _keyField;
 *
 * 			@MarshallField(as="value")
 * 			@Getter @Setter private String _valueField;
 * 		}
 * </pre>
 * When an instance of this type is converted from Java to XML the output result is:
 * <pre class='brush:xml'>
 * 		<myType key='theKey'>theValue</myType>
 * </pre>
 * Now imagine that you have a type that contains a Map of MyTypes, something like:
 * <pre class='brush:java'>
 * 		@TypeMarshall(as="myContainerType")
 * 		public class MyContainerType {
 * 			@MarshallField(as="container")
 * 			@Getter @Setter private Map<String,MyType> _containerMap;
 * 		}
 * </pre>
 * If an instance of MyContainerType is to be serialized to XML there is no problem with Map's key values:
 * <pre class='brush:xml'>
 * 		<myContainerType>
 * 			<container>
 * 				<myType key='theKey1'>theValue1</myType>
 * 				<myType key='theKey2'>theValue2</myType>
 * 			</container>
 * 		</myContainerType>
 * </pre>
 * BUT when converting this xml back to java objects instances, there is a problem knowing which of
 * MyType's fields is the one acting as OID. This is where @OidField annotation comes to play
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OidField {
	/* nothing */
}
