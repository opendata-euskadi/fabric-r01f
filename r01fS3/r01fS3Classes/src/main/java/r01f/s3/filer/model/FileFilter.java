package r01f.s3.filer.model;

import r01f.types.Path;

public interface FileFilter {
	/**
	 * Accept only this filtered files
	 * @param path the path
	 * @return if is accepted
	 */
	public boolean accept(final Path path);
}
