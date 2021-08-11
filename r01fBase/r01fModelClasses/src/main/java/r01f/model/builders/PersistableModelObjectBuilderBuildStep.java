package r01f.model.builders;

import r01f.guids.OID;
import r01f.model.PersistableModelObject;

public class PersistableModelObjectBuilderBuildStep<M extends PersistableModelObject<? extends OID>> 
	 extends BuilderBuildStep<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistableModelObjectBuilderBuildStep(final M modelObject) {
		super(modelObject);
	}
}
