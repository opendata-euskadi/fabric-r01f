package r01f.model.builders.facets;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.patterns.IsBuilder;

/**
 * Base type for all content model object's builders
 */
@Accessors(prefix="_")
public abstract class FacetBuilderBase<NEXT_BUILDER,
									   T> 
		   implements IsBuilder { 
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Parent type containing this facet builder
	 */
	@Setter(AccessLevel.PROTECTED) protected NEXT_BUILDER _nextBuilder;
	/**
	 * Model object
	 */
	protected final T _modelObject;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FacetBuilderBase(final T modelObject) {
		_modelObject = modelObject;
	}
	public FacetBuilderBase(final NEXT_BUILDER nextBuilder,
							final T modelObject) {
		_nextBuilder = nextBuilder;
		_modelObject = modelObject;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected T getModelObject() {
		return _modelObject;
	}
	public NEXT_BUILDER build() {
		return _nextBuilder;
	}
}
