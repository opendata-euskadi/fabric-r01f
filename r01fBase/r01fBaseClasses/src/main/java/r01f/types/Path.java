package r01f.types;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Lists;

import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * path abstraction, simply using a String encapsulation
 */
@MarshallType(as="path")
@Accessors(prefix="_")
@Immutable
public class Path 
     extends PathBase<Path> {
	
	private static final long serialVersionUID = -4132364966392988245L;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	public Path() {
		super(Lists.newArrayList());
	}
	public Path(final Collection<String> pathEls) {
		super(pathEls);
	}
	public Path(final Object... obj) {
		super(obj);
	}
	public Path(final Object obj) {
		super(obj);
	}
	public <P extends IsPath> Path(final P otherPath) {
		super(otherPath);
	}
	public Path(final String... elements) {
		super(elements);
	}
	public static PathFactory<Path> PATH_FACTORY = new PathFactory<Path>() {
															@Override
															public Path createPathFrom(final Collection<String> elements) {
																return new Path(elements);
															}
												   };
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> PathFactory<P> getPathFactory() {
		return (PathFactory<P>)Path.PATH_FACTORY;
	}
///////////////////////////////////////////////////////////////////////////////
//	FACTORIES
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Factory from {@link String}
	 * @param path
	 * @return
	 */
	public static Path valueOf(final String path) {
		return Path.from(path);
	}
	/**
	 * Factory from path components
	 * @param elements 
	 * @return the {@link Path} object
	 */
	public static Path from(final String... elements) {
		if (elements == null || elements.length == 0) return null;
		return new Path(elements);
	}
	/**
	 * Factory from other {@link Path} object
	 * @param other 
	 * @return the new {@link Path} object
	 */
	public static <P extends IsPath> Path from(final P other) {
		if (other == null) return null;
		Path outPath = new Path(other);
		return outPath;
	}
	/**
	 * Factory from an {@link Object} (the path is composed translating the {@link Object} to {@link String})
	 * @param obj 
	 * @return the {@link Path} object
	 */
	public static Path from(final Object... obj) {
		if (obj == null) return null;
		return new Path(obj);
	}
	/**
	 * Factory from a {@link File} object 
	 * @param file
	 * @return
	 */
	@GwtIncompatible("GWT does not supports IO")
	public static Path from(final File file) {
		if (file == null) return null;
		return new Path(file.getAbsolutePath());
	}
///////////////////////////////////////////////////////////////////////////////
//  CLONE
///////////////////////////////////////////////////////////////////////////////
	@Override @GwtIncompatible("Cloneable interface is NOT implemented in GWT")
	protected Object clone() throws CloneNotSupportedException {
		return new Path(_pathElements);
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> P withoutLastPathElement() {
		return (P)PathBase.withoutLastPathElement(this.getPathFactory(),
											      this);
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> P joinedWith(final Object... elements) {
		return (P)PathBase.join(this.getPathFactory(),
								this,elements);
	}
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> P prependedWith(final Object... elements) {
		return (P)PathBase.prepend(this.getPathFactory(),
								   this,elements);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the folder path (all the path except the file)
	 * ... this assumes that the file name contains a DOT (.)
	 *     it this is NOT the case, the result is NOT accurate
	 * @return
	 */
	public Path getFolderPathAssumingFileWithExtension() {
		if (_pathElements.size() == 1 && this.isFilePath()) return null;
		if (_pathElements.size() == 1) return Path.from(_pathElements.get(0));
		if (this.isFilePath()) {
			return Path.from(this.getFirstNPathElements(_pathElements.size()-1)
							   		.toArray(new String[_pathElements.size()-1]));
		} else {
			return this;
		}
	}
	/**
	 * Returns the remaining path fragment begining where the given
	 * starting path ends
	 * ie: if path=/a/b/c/d
	 *     and startingPath = /a/b
	 *     ... this function will return /c/d
	 * @param startingPath
	 * @return
	 */
	public Path remainingPathFrom(final Path startingPath) {
		List<String> remainingPathEls = this.getPathElementsAfter(startingPath);
		return remainingPathEls != null ? new Path(remainingPathEls)
										: null;
	}
	/**
	 * Returns the url path AFTER the given prefix
	 * ie: if path=/foo/bar/baz.hml and prefix=/foo
	 * then, the returned path=/bar/baz.html
	 * If the path does NOT starts with the given prefix, it throws an IllegalStateException
	 * @param prefix
	 * @return
	 */
	public Path urlPathAfter(final Path prefix) {
		return this.remainingPathFrom(prefix);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MATCH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if the path matches the given regex
	 * Beware that the matching is done against the path.asAbsoluteString() result
	 * @param p
	 * @return
	 */
	@GwtIncompatible("regex not supported")
	public boolean matches(final Pattern p) {
		String asStr = this.asAbsoluteString();
		Matcher m = p.matcher(asStr);
		return m.find();
	}
}
