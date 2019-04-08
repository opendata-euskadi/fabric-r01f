package r01f.resources;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.resources.ResourcesLoaderDef.ResourcesLoaderType;
import r01f.types.IsPath;
import r01f.types.Path;
import r01f.types.PathBase;
import r01f.types.PathFactory;

/**
 * A file {@link Path} alongside with the resources loader to use
 */
@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
public class ResourceToBeLoaded 
  implements IsPath {

	private static final long serialVersionUID = 1199580273135196069L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ResourcesLoaderType _resourcesLoaderType;
	@Getter private final Path _filePath;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static ResourceToBeLoaded classPathLoaded(final String filePath) {
		return new ResourceToBeLoaded(ResourcesLoaderType.CLASSPATH,
							  Path.from(filePath));
	}
	public static ResourceToBeLoaded classPathLoaded(final Path filePath) {
		return new ResourceToBeLoaded(ResourcesLoaderType.CLASSPATH,
							  Path.from(filePath));
	}
	public static ResourceToBeLoaded fileSystemLoaded(final String filePath) {
		return new ResourceToBeLoaded(ResourcesLoaderType.FILESYSTEM,
							  Path.from(filePath));
	}
	public static ResourceToBeLoaded fileSystemLoaded(final Path filePath) {
		return new ResourceToBeLoaded(ResourcesLoaderType.FILESYSTEM,
							  Path.from(filePath));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isLoadedFromClassPath() {
		return _resourcesLoaderType == ResourcesLoaderType.CLASSPATH;
	}
	public boolean isLoadedFromFileSystem() {
		return _resourcesLoaderType == ResourcesLoaderType.FILESYSTEM;
	}
	public String getFilePathAsString() {
		if (_filePath == null) return null;
		String outPathAsString = null;
		if (_resourcesLoaderType == ResourcesLoaderType.CLASSPATH) {
			outPathAsString = _filePath.asRelativeString();
		} else {
			outPathAsString = _filePath.asAbsoluteString();
		}
		return outPathAsString;
	}
	@Override
	public Collection<String> getPathElements() {
		return _filePath != null ? _filePath.getPathElements() : null;
	}
	@Override
	public String asString() {
		return _filePath != null ? _filePath.asString() : null;
	}
	@Override
	public String asRelativeString() {
		return _filePath != null ? _filePath.asRelativeString() : null;
	}
	@Override
	public String asAbsoluteString() {
		return _filePath != null ? _filePath.asAbsoluteString() : null;
	}
	@Override
	public <P extends IsPath> String asAbsoluteStringFrom(final P parentPath) {
		return _filePath != null ? _filePath.asAbsoluteStringFrom(parentPath) 
								 : parentPath.asAbsoluteString();
	}
	@Override
	public <P extends IsPath> String asRelativeStringFrom(final P parentPath) {
		return _filePath != null ? _filePath.asRelativeStringFrom(parentPath) 
								 : parentPath.asRelativeString();
	}
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> PathFactory<P> getPathFactory() {
		return (PathFactory<P>)(_filePath != null ? _filePath.getPathFactory() : null);
	}
	@Override
	public boolean hasPathElements() {
		return _filePath != null ? _filePath.hasPathElements() : false;
	} 
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> P withoutLastPathElement() {
		return (P)(_filePath != null ? _filePath.withoutLastPathElement() : null);
	}
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> P joinedWith(final Object... elements) {
		return (P)(_filePath != null ? _filePath.joinedWith(elements) : null);
	}
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> P prependedWith(final Object... elements) {
		return (P)(_filePath != null ? _filePath.prependedWith(elements) : null);
	}
	@Override
	public Iterator<String> getPathElementsIterator() {
		return _filePath != null ? _filePath.getPathElementsIterator() : null;
	}
	@Override
	public int getPathElementCount() {
		return _filePath != null ? _filePath.getPathElementCount() : 0;
	}
	@Override
	public String getLastPathElement() {
		return _filePath != null ? _filePath.getLastPathElement() : null;
	}
	@Override
	public String getFirstPathElement() {
		return _filePath != null ? _filePath.getFirstPathElement() : null;
	}
	@Override
	public String getPathElementAt(final int pos) {
		return _filePath != null ? _filePath.getPathElementAt(pos) : null;
	}
	@Override
	public List<String> getFirstNPathElements(final int num) {
		return _filePath != null ? _filePath.getFirstNPathElements(num) : null;
	}
	@Override
	public List<String> getPathElementsExceptLast() {
		return _filePath != null ? _filePath.getPathElementsExceptLast() : null;
	}
	@Override
	public List<String> getPathElementsFrom(final int pos) {
		return _filePath != null ? _filePath.getPathElementsFrom(pos) : null;
	}
	@Override
	public int getPathElementFirstPosition(final String pathElement) {
		return _filePath != null ? _filePath.getPathElementFirstPosition(pathElement) : -1;
	}
	@Override
	public <P extends PathBase<?>> List<String> getPathElementsAfter(final P prefix) {
		return _filePath != null ? _filePath.getPathElementsAfter(prefix) : null;
	}
	@Override
	public <P extends PathBase<?>> boolean startsWith(final P other) {
		return _filePath != null ? _filePath.startsWith(other) : false;
	}
	@Override
	public <P extends PathBase<?>> boolean endsWith(final P other) {
		return _filePath != null ? _filePath.endsWith(other) : false;
	}
	@Override
	public boolean containsPathElement(final String pathEl) {
		return _filePath != null ? _filePath.containsPathElement(pathEl) : false;
	}
	@Override
	public boolean containsAllPathElements(final String... pathElsToCheck) {
		return _filePath != null ? _filePath.containsAllPathElements(pathElsToCheck) : false;
	}
}
