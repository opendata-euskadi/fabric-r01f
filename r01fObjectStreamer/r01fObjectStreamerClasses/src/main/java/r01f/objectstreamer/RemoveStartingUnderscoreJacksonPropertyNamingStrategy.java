package r01f.objectstreamer;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RemoveStartingUnderscoreJacksonPropertyNamingStrategy 
     extends PropertyNamingStrategy {
	
	private static final long serialVersionUID = 8310912907941951750L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String nameForField(final MapperConfig<?> config,
							   final AnnotatedField field,
							   final String defaultName) {
		String outName = defaultName;
		if (defaultName.startsWith("_")) outName = defaultName.substring(1);
		return outName;
	}
}
