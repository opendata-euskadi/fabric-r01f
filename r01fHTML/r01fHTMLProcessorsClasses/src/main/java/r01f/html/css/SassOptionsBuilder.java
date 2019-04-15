package r01f.html.css;


public interface SassOptionsBuilder {
	/**
	 * Sets the directory where framework resources are looked for locally by the filesystem importer
	 * @param frameworkDir
	 * @return
	 */
	public SassOptionsBuilder findFrameworkLocalResourcesAt(final String frameworkDir);
	/**
	 * Sets the remote (http/https) location where to look for resources not found locally
	 * @param remoteResourcesLocation
	 */
	public SassOptionsBuilder findRemoteResourcesAt(final String remoteResourcesLocation);
	/**
	 * Sets the compass stylesheets default dir
	 * @param cssDir
	 * @return
	 */
	public SassOptionsBuilder webServerDirForStylesheets(final String cssDir);
	/**
	 * Sets the compass scripts default dir
	 * @param jsDir
	 * @return
	 */
	public SassOptionsBuilder webServerDirForScripts(final String jsDir);
	/**
	 * Sets the compass images dir
	 * @param imgDir
	 * @return
	 */
	public SassOptionsBuilder webServerDirForImages(final String imgDir);
	/**
	 * Builds the {@link SassOptions}
	 * @return
	 */
	public SassOptions build();
}
