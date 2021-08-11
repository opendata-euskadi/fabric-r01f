package r01f.model.builders.facets;

import lombok.experimental.Accessors;
import r01f.patterns.FactoryFrom;
import r01f.patterns.IsBuilder;
import r01f.patterns.IsBuilderStep;

/**
 * Base type for all content model object's builders
 */
@Accessors(prefix="_")
public abstract class FacetBuilderBase<NEXT_BUILDER,
									   T> 
		   implements IsBuilder,
		   			  IsBuilderStep { 
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Parent type containing this facet builder
	 */
	protected final FactoryFrom<T,NEXT_BUILDER> _nextBuilderFactory;
	/**
	 * Model object
	 */
	protected final T _modelObject;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FacetBuilderBase(final FactoryFrom<T,NEXT_BUILDER> nextBuilderFactory,
							final T modelObject) {
		_nextBuilderFactory = nextBuilderFactory;
		_modelObject = modelObject;
	}
	public FacetBuilderBase(final NEXT_BUILDER nextBuilder,
							final T modelObject) {
		_nextBuilderFactory = new FactoryFrom<T,NEXT_BUILDER>() {
										@Override
										public NEXT_BUILDER from(final T modelObj) {
											return nextBuilder;
										}
							  };
		_modelObject = modelObject;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected T getModelObject() {
		return _modelObject;
	}
	public NEXT_BUILDER next() {
		return _nextBuilderFactory.from(_modelObject);
	}
}
