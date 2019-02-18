package r01f.objectstreamer;

import java.util.Set;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;

import r01f.guids.CommonOIDs.AppCode;
import r01f.objectstreamer.annotationintrospector.MarshallerAnnotationIntrospector;

/**
 * see https://spin.atomicobject.com/2016/07/01/custom-serializer-jackson/
 */
public class MarshallerModuleForJson 
	 extends MarshallerModuleBase {

	private static final long serialVersionUID = 526390669822422298L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String NAME = "r01.marshallerJsonModule";
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerModuleForJson(final Set<AppCode> appCodes) { 
		super(NAME,VersionUtil.versionFor(MarshallerModuleForJson.class),
			  appCodes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setupModule(final Module.SetupContext context) {
        super.setupModule(context);

        // Append after other introspectors (instead of before) since
        // explicit annotations should have precedence
        context.appendAnnotationIntrospector(new MarshallerAnnotationIntrospector(_appCodes,
        																		  context));
	}
}
