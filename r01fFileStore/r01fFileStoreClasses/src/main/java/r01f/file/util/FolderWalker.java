package r01f.file.util;

import java.io.IOException;
import java.util.Collection;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

/**
 * Abstract class that walks through a folder hierarchy and provides
 * subclasses with convenient hooks to add specific behavior.
 * <p>
 * This class operates with a {@link FileFilter} and maximum depth to
 * limit the files and direcories visited.
 * Commons IO supplies many common filter implementations in the 
 * <a href="filefilter/package-summary.html"> filefilter</a> package.
 * </p>
 * <p>
 * The following sections describe:
 * </p>
 * <ul>
 *	  <li><a href="#example">1. Example Implementation</a> - example
 *		  <code>FileCleaner</code> implementation.</li>
 *	  <li><a href="#filter">2. Filter Example</a> - using 
 *		  {@link FileFilter}(s) with <code>FolderWalker</code>.</li>
 *	  <li><a href="#cancel">3. Cancellation</a> - how to implement cancellation
 *		  behaviour.</li>
 * </ul>
 *
 * <a name="example"></a>
 * <h3>1. Example Implementation</h3>
 *
 * <pre>
 *	public class FileProcessor 
 *		 extends FolderWalker<T> {
 *	
 *		public FileProcessor() {
 *			super();
 *		}
 *		public Collection<T> process(final Path startFolder) {
 *			Collection<T> results = new ArrayList();
 *			this.walk(startFolder,
 *						results);
 *			return results;
 *		}
 *		protected boolean _handleFolder(final FileProperties folder,final int depth,
 *										  final Collection<T> results) {
 *			return true;
 *		}
 *		protected void _handleFile(final FileProperties file,final int depth,
 *									 final Collection<T> results) {
 *			// process the file
 *			// ... do something with the file
 *			
 *			// put the file into the results collection
 *			T result = ...
 *			results.add(result);
 *		}
 *	}
 * </pre>
 *
 * <a name="filter"></a>
 * <h3>2. Filter Example</h3>
 *
 * Choosing which directories and files to process:
 * <p>
 * The first option is to visit all directories and files.
 * This is achieved via the no-args constructor.
 * </p>
 * <p>
 * The second constructor option is to supply a single {@link FileFilter}
 * that describes the files and directories to visit. 
 * Care must be taken with this option as the same filter is used for both directories
 * and files.
 * </p>
 * <p>
 * Example:
 * </p>
 * <pre>
 *	 public class FooFolderWalker 
 *		  extends FolderWalker {
 *		 public FooFolderWalker(FileFilter filter) {
 *			 super(filter,
 *			 		 -1);		// any depth
 *		 }
 *	 }
 *	 // Use the filter to construct a FolderWalker implementation
 *	 FooFolderWalker walker = new FooFolderWalker(fooFilter);
 * </pre>
 * <p>
 *
 * <a name="cancel"></a>
 * <h3>3. Cancellation</h3>
 *
 * The FolderWalker contains some of the logic required for cancel processing.
 * Subclasses must complete the implementation.
 * <p>
 * What <code>FolderWalker</code> does provide for cancellation is:
 * </p>
 * <ul>
 *	<li>{@link FolderWalkerCancelException} which can be thrown in any of the
 *		<i>lifecycle</i> methods to stop processing.</li>
 *	<li>The <code>walk()</code> method traps thrown {@link FolderWalkerCancelException}
 *		and calls the <code>handleCancelled()</code> method, providing
 *		a place for custom cancel processing.</li>
 * </ul>
 * <p>
 * Implementations need to provide:
 * <ul>
 *	<li>The decision logic on whether to cancel processing or not.</li>
 *	<li>Constructing and throwing a {@link FolderWalkerCancelException}.</li>
 *	<li>Custom cancel processing in the <code>_handleCancelled()</code> method.
 * </ul>
 * <p>
 * Two possible scenarios are envisaged for cancellation:
 * </p>
 * <ul>
 *	<li><a href="#external">3.1 External / Mult-threaded</a> - cancellation being
 *		decided/initiated by an external process.</li>
 *	<li><a href="#internal">3.2 Internal</a> - cancellation being decided/initiated 
 *		from within a FolderWalker implementation.</li>
 * </ul>
 * <p>
 * The following sections provide example implementations for these two different
 * scenarios.
 * </p>
 * 
 * <a name="external"></a>
 * <h4>3.1 External / Multi-threaded</h4>
 *
 * This example provides a public <code>cancel()</code> method that can be
 * called by another thread to stop the processing. A typical example use-case
 * would be a cancel button on a GUI. Calling this method sets a
 * <a href="http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html#36930">
 * volatile</a> flag to ensure it will work properly in a multi-threaded environment.
 * The flag is returned by the <code>handleIsCancelled()</code> method, which
 * will cause the walk to stop immediately. The <code>handleCancelled()</code>
 * method will be the next, and last, callback method received once cancellation
 * has occurred.
 *
 * <pre>
 *	public class FooFolderWalker 
 *		   extends FolderWalker {
 *	
 *		private volatile boolean _cancelled = false;
 *		
 *		public void cancel() {
 *			_cancelled = true;
 *		}
 *		private void _handleIsCancelled(final FileProperties file,final int depth,
 *										  final Collection<T> results) {
 *			return _cancelled;
 *		}
 *		protected void _handleCancelled(final FileProperties startFolder, 
 *										  final Collection<T> results,
 *										  final FolderWalkerCancelException cancel) {
 *			// implement processing required when a cancellation occurs
 *		}
 *	}
 * </pre>
 *
 * <a name="internal"></a>
 * <h4>3.2 Internal</h4>
 *
 * This shows an example of how internal cancellation processing could be implemented.
 * <b>Note</b> the decision logic and throwing a {@link FolderWalkerCancelException} could be implemented
 * in any of the <i>lifecycle</i> methods. 
 *
 * <pre>
 *  public class BarFolderWalker
 *	   extends FolderWalker {
 *
 *	protected boolean _handleFolder(final FileProperties folder,final int depth, 
 *									  final Collection<T> results) throws IOException {
 *		// cancel if hidden folder
 *		if (folder.isHidden()) {
 *			throw new FolderWalkerCancelException(file, depth);
 *		}
 *		return true;
 *	}
 *	protected void _handleFile(final FileProperties file,final int depth,
 *					  			 final Collection<T> results) throws IOException {
 *		// cancel if read-only file
 *		if (!file.canWrite()) {
 *			throw new FolderWalkerCancelException(file,depth);
 *		}
 *		results.add(file);
 *	}
 *	protected void _handleCancelled(final FileProperties startFolder,
 *									  final Collection<T> results,
 *									  final FolderWalkerCancelException cancel) {
 *		// implement processing required when a cancellation occurs
 *	}
 *  }
 * </pre>
 */
public abstract class FolderWalker<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The file filter to use to filter files and directories.
	 */
	protected final FileFilter _filter;
	/**
	 * The limit on the folder depth to walk.
	 */
	protected final int _depthLimit;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	protected FolderWalker() {
		this(null,	// no filter
			 -1);	// no depth limit
	}
	protected FolderWalker(final FileFilter filter) {
		this(filter,
			 -1);	// no depth limit
	}
	protected FolderWalker(final FileFilter filter,
						   final int depthLimit) {
		_filter = filter;
		_depthLimit = depthLimit;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a folder's properties
	 * @param folderPath
	 * @return
	 */
	protected abstract FileProperties getFolderProperties(final Path folderPath) throws IOException;
	/**
	 * Returns a folder's contents
	 * @param folderPath
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	protected abstract Collection<FileProperties> listFolderContents(final Path folderPath,
																	 final FileFilter filter) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Internal method that walks the folder hierarchy in a depth-first manner.
	 * <p>
	 * Users of this class do not need to call this method. This method will
	 * be called automatically by another (public) method on the specific subclass.
	 * </p>
	 * <p>
	 * Writers of subclasses should call this method to start the folder walk.
	 * Once called, this method will emit events as it walks the hierarchy.
	 * The event methods have the prefix <code>handle</code>.
	 * </p>
	 * @param startFolderPath  the folder to start from, not null
	 * @param results  the collection of result objects, may be updated
	 * @throws NullPointerException if the start folder is null
	 * @throws IOException if an I/O Error occurs
	 */
	protected final void walk(final Path startFolderPath,
							  final Collection<T> results) throws IOException {
		if (startFolderPath == null) throw new NullPointerException("Start Folder is null");
		FileProperties starFolderProps = this.getFolderProperties(startFolderPath);
		try {
			// start
			_handleStart(starFolderProps,
						 results);
			// walk
			_walk(starFolderProps,0,
				  results);
			// end
			_handleEnd(results);
		} catch (FolderWalkerCancelException cancel) {
			_handleCancelled(starFolderProps,
							 results, 
							 cancel);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Main recursive method to examine the folder hierarchy.
	 * @param folderProps  the folder to examine, not null
	 * @param depth  the folder level (starting folder = 0)
	 * @param results  the collection of result objects, may be updated
	 * @throws IOException if an I/O Error occurs
	 */
	private void _walk(final FileProperties folderProps,final int depth, 
					   final Collection<T> results) throws IOException {
		_checkIfCancelled(folderProps,depth,
						  results);
		if (_hasToHandleFolder(folderProps,depth,
						  	   results)) {
			_handleFolderStart(folderProps,depth,
							   results);
			int childDepth = depth + 1;
			if (_depthLimit < 0 || childDepth <= _depthLimit) {
				_checkIfCancelled(folderProps,depth,
								  results);
				
				Collection<FileProperties> folderChildren = this.listFolderContents(folderProps.getPath(),
															   	 		   	   		_filter);	// not recursive
				// give an opportunity to filter folder contents
				folderChildren = _filterFolderContents(folderProps,depth,
													   folderChildren);
				if (CollectionUtils.isNullOrEmpty(folderChildren)) {
					_handleRestricted(folderProps,childDepth,
									  results);
				} else {
					for (FileProperties childProps : folderChildren) {
						if (childProps.isFolder()) {	// is folder?
							_walk(childProps,childDepth,
								  results);
						} else {						// ... it's a file
							_checkIfCancelled(childProps,childDepth,
											  results);
							_handleFile(childProps,childDepth,
										results);
							_checkIfCancelled(childProps,childDepth,
											  results);
						}
					}
				}
			}
			_handleFolderEnd(folderProps,depth, 
							 results);
		}
		_checkIfCancelled(folderProps, depth, results);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Overridable callback method invoked at the start of processing.
	 * @param startFolderProps  the folder to start from
	 * @param results  the collection of result objects, may be updated
	 * @throws IOException if an I/O Error occurs
	 */
	protected void _handleStart(final FileProperties startFolderProps, 	
								final Collection<T> results) throws IOException {
		// do nothing - overridable by subclass
	}
	/**
	 * Overridable callback method invoked to determine if a folder should be processed.
	 * <p>
	 * This method returns a boolean to indicate if the folder should be examined or not.
	 * If you return false, the entire folder and any subdirectories will be skipped.
	 * Note that this functionality is in addition to the filtering by file filter.
	 * <p>
	 * @param folderProps  the current folder being processed
	 * @param depth  the current folder level (starting folder = 0)
	 * @param results  the collection of result objects, may be updated
	 * @return true to process this folder, false to skip this folder
	 * @throws IOException if an I/O Error occurs
	 */
	protected boolean _hasToHandleFolder(final FileProperties folderProps,final int depth, 
										 final Collection<T> results) throws IOException {
		// do nothing - overridable by subclass
		return true;  // process folder
	}
	/**
	 * Overridable callback method invoked at the start of processing each folder.
	 * @param folderProps  the current folder being processed
	 * @param depth  the current folder level (starting folder = 0)
	 * @param results  the collection of result objects, may be updated
	 * @throws IOException if an I/O Error occurs
	 */
	protected void _handleFolderStart(final FileProperties folderProps,final int depth,
									  final Collection<T> results) throws IOException {
		// do nothing - overridable by subclass
	}
	/**
	 * Overridable callback method invoked with the contents of each folder.
	 * @param folderProps  the current folder being processed
	 * @param depth  the current folder level (starting folder = 0)
	 * @param files the files (possibly filtered) in the folder
	 * @return the filtered list of files
	 * @throws IOException if an I/O Error occurs
	 * @since 2.0
	 */
	@SuppressWarnings("static-method")
	protected Collection<FileProperties> _filterFolderContents(final FileProperties folderProps,final int depth,
											  		 		   final Collection<FileProperties> files) throws IOException {
		return files;	// returns the files unchanged
	}
	/**
	 * Overridable callback method invoked for each (non-folder) file.
	 * @param fileProps  the current file being processed
	 * @param depth  the current folder level (starting folder = 0)
	 * @param results  the collection of result objects, may be updated
	 * @throws IOException if an I/O Error occurs
	 */
	protected void _handleFile(final FileProperties fileProps,final int depth,
							   final Collection<T> results) throws IOException {
		// do nothing - overridable by subclass
	}
	/**
	 * Overridable callback method invoked for each restricted folder.
	 * @param folderProps  the restricted folder
	 * @param depth  the current folder level (starting folder = 0)
	 * @param results  the collection of result objects, may be updated
	 * @throws IOException if an I/O Error occurs
	 */
	protected void _handleRestricted(final FileProperties folderProps,final int depth,
									 final Collection<T> results) throws IOException  {
		// do nothing - overridable by subclass
	}
	/**
	 * Overridable callback method invoked at the end of processing each folder.
	 * @param folderProps  the folder being processed
	 * @param depth  the current folder level (starting folder = 0)
	 * @param results  the collection of result objects, may be updated
	 * @throws IOException if an I/O Error occurs
	 */
	protected void _handleFolderEnd(final FileProperties folderProps,final int depth,
									final Collection<T> results) throws IOException {
		// do nothing - overridable by subclass
	}
	/**
	 * Overridable callback method invoked at the end of processing.
	 * @param results  the collection of result objects, may be updated
	 * @throws IOException if an I/O Error occurs
	 */
	protected void _handleEnd(final Collection<T> results) throws IOException {
		// do nothing - overridable by subclass
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks whether the walk has been cancelled by calling {@link #handleIsCancelled},
	 * throwing a <code>FolderWalkerCancelException</code> if it has.
	 * <p>
	 * Writers of subclasses should NOT normally call this method as it is called
	 * automatically by the walk of the tree. 
	 * However, sometimes a single method, typically {@link #handleFile}, may take a long time to run. 
	 * In that case, you may wish to check for cancellation by calling this method.
	 * </p>
	 * @param folderProps  the current file being processed
	 * @param depth  the current file level (starting folder = 0)
	 * @param results  the collection of result objects, may be updated
	 * @throws IOException if an I/O Error occurs
	 */
	protected final void _checkIfCancelled(final FileProperties folderProps,final int depth,
										   final Collection<T> results) throws IOException {
		if (_handleIsCancelled(folderProps,
							   depth,
							   results)) {
			throw new FolderWalkerCancelException(folderProps.getPath(),depth);
		}
	}
	/**
	 * Overridable callback method invoked to determine if the entire walk
	 * operation should be immediately cancelled.
	 * <p>
	 * This method should be implemented by those subclasses that want to
	 * provide a public <code>cancel()</code> method available from another
	 * thread. The design pattern for the subclass should be as follows:
	 * <pre>
	 *	   public class FooFolderWalker 
	 *			extends FolderWalker {
	 *			
	 *		   private volatile boolean _cancelled = false;
	 *		   
	 *		   public void cancel() {
	 *			   _cancelled = true;
	 *		   }
	 *		   private void _handleIsCancelled(final FileProperties file,final int depth,
	 *		   								 final Collection<T> results) {
	 *			   return _cancelled;
	 *		   }
	 *		   protected void _handleCancelled(final FileProperties startFolder,
	 *					 						 final Collection<T> results, 
	 *					 						 final FolderWalkerCancelException cancel) {
	 *			   // implement processing required when a cancellation occurs
	 *		   }
	 *	   }
	 * </pre>
	 * <p>
	 * If this method returns true, then the folder walk is immediately
	 * cancelled. The next callback method will be {@link #handleCancelled}.
	 * </p>
	 * @param folderProps  the file or folder being processed
	 * @param depth  the current folder level (starting folder = 0)
	 * @param results  the collection of result objects, may be updated
	 * @return true if the walk has been cancelled
	 * @throws IOException if an I/O Error occurs
	 */
	protected boolean _handleIsCancelled(final FileProperties folderProps,final int depth,
										 final Collection<T> results) throws IOException {
		// do nothing - overridable by subclass
		return false;  // not cancelled
	}
	/**
	 * Overridable callback method invoked when the operation is cancelled.
	 * The file being processed when the cancellation occurred can be
	 * obtained from the exception.
	 * @param startFolderProps  the folder that the walk started from
	 * @param results  the collection of result objects, may be updated
	 * @param cancel  the exception throw to cancel further processing
	 * containing details at the point of cancellation. 
	 * @throws IOException if an I/O Error occurs
	 */
	protected void _handleCancelled(final FileProperties startFolderProps,
									final Collection<T> results,
									final FolderWalkerCancelException cancel) throws IOException {
		// re-throw exception - overridable by subclass
		throw cancel;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Accessors(prefix="_")
	public static class FolderWalkerCancelException
	 			extends IOException {

		private static final long serialVersionUID = 1347339620135041008L;
		
		@Getter private final Path _path;
		@Getter private final int _depth;

		public FolderWalkerCancelException(final Path path,final int depth) {
			this("Operation Cancelled",
				 path,depth);
		}
		public FolderWalkerCancelException(final String message,
										   final Path path,final int depth) {
			super(message);
			_path = path;
			_depth = depth;
		}
	}
}
