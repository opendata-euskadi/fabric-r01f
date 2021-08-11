package r01f.model.builders;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.model.PersistableModelObject;
import r01f.patterns.IsBuilderStep;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class BuilderBuildStep<T> 
  implements IsBuilderStep {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The {@link PersistableModelObject} instance that's being built
	 */
	@Getter protected final T _modelObject;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILD
/////////////////////////////////////////////////////////////////////////////////////////
	public T build() {
		return _modelObject;
	}
}
