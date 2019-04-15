package r01f.html.css;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@RequiredArgsConstructor
abstract class SassProcessorBase<SELF_TYPE extends SassProcessorBase<SELF_TYPE>> {

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter protected SassOptions _sassOptions;
    
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Uses the compass framework in compilation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE withOptions(final SassOptions options) {
		_sassOptions = options;
    	return (SELF_TYPE)this;
	}
	/**
	 * Creates the default compass options
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE withDefaultOptions() {
		_sassOptions = SassOptions.DEFAULT_OPTIONS;
		return (SELF_TYPE)this;
	}
}
