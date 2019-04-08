package r01f.filestore.api;

import r01f.file.FileProperties;
import r01f.types.Path;

public interface FileFilter {
	/**
	 * Accept only this filtered files
	 * @param path the path
	 * @return if is accepted
	 */
	public boolean accept(final Path path);
	/**
	 * Accept only this filtered files
	 * @param props
	 * @return
	 */
	public boolean accept(final FileProperties props);
}
