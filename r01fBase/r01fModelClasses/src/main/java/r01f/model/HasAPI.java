package r01f.model;

/**
 * Interface for model object extensions that contains an API 
 */
public interface HasAPI {
	/**
	 * @return the api casted
	 */
	public <A extends API> A getApiAs(final Class<A> apiType);
}
