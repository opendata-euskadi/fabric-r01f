package r01f.reflection;

import java.lang.annotation.Annotation;

import lombok.Getter;
import lombok.experimental.Accessors;


@Accessors(prefix="_")
public class FieldAnnotatedReflection<A extends Annotation> {
	@Getter private final FieldReflection _fieldReflection;
	@Getter private final A _annotationType;	
	
	public FieldAnnotatedReflection(final FieldReflection field,final A annotationType) {
		_fieldReflection = field;
		_annotationType = annotationType;
	}
}
