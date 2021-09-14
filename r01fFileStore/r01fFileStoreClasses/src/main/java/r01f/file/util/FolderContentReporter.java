package r01f.file.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.file.FileProperties;
import r01f.file.util.FolderContentReporter.FolderContentReportItem;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * A class that creates a report with a folder's contents
 * Usage:
 * <pre class='brush:java'>
 *		FolderContentReporter reporter = new FolderContentReporter(filerApi);
 * 		Collection<FolderContentReportItem> items = reporter.reportFrom(rootPath);
 * </pre>
 * To compute the differences between two reports:
 * <pre class='brush:java'>
 * 		FolderContentReportDiff diff = FolderContentReporter.diff(items1,items2);
 * </pre>
 */
@RequiredArgsConstructor
public class FolderContentReporter
	 extends FolderWalker<FolderContentReportItem> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final FileStoreFilerAPI _filerApi;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a file report (a collection of {@link FolderContentReportItem}s) 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public Set<FolderContentReportItem> reportFrom(final Path folder) throws IOException {
		Set<FolderContentReportItem> outProps = Sets.newHashSet();
		this.walk(folder,
				  outProps);
		return outProps;
	}
	public static FolderContentReportDiff diff(final Set<FolderContentReportItem> r1,final Set<FolderContentReportItem> r2) {
		Set<FolderContentReportItem> newOrUpdated = r1 != null && r2 != null ? Sets.difference(r2,r1)			// not in r1 (can be an update or a new element)
																			 : r2 != null ? Sets.newHashSet(r2)	// r1 is assumed to be null (all new)
																					 	  : r1 != null ? null	// r2 is assumed to be null
																					 			  	   : null;	// both null
																			 
		Set<FolderContentReportItem> deleted = r1 != null && r2 != null ? Sets.difference(r1,r2)				// still in r1 but NOT in r2
																		: r1 != null ? Sets.newHashSet(r1)		// r2 is assumed to be null (all deleted)
																					 : r2 != null ? null		// r1 is assumed to be null
																							 	  : null;		// both null
		Set<FolderContentReportItem> unchanged = r1 != null && r2 != null ? Sets.intersection(r1,r2)			// both in r1 and r2
																		  : null;
		return new FolderContentReportDiff(newOrUpdated,
										   deleted,
										   unchanged);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected FileProperties getFolderProperties(final Path folderPath) throws IOException {
		return _filerApi.getFolderProperties(folderPath);
	}
	@Override
	protected Collection<FileProperties> listFolderContents(final Path folderPath,
															final FileFilter filter) throws IOException {
		FileProperties[] outProps =  _filerApi.listFolderContents(folderPath,
																  filter,
																  false);
		return outProps != null ? Lists.newArrayList(outProps)
								: Lists.<FileProperties>newArrayList();
	}
	@Override
	protected void _handleFile(final FileProperties fileProps,final int depth,
							   final Collection<FolderContentReportItem> results) throws IOException {
		FolderContentReportItem item = FolderContentReportItem.from(fileProps);
		results.add(item);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="folderContentReportDiff")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class FolderContentReportDiff
			 implements Serializable,
			 			Debuggable {
		private static final long serialVersionUID = -6592740535253495605L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////		
		@MarshallField(as="newOrUpdated",
					   whenXml=@MarshallFieldAsXml(collectionElementName="newItem"))
		@Getter @Setter private Set<FolderContentReportItem> _newOrUpdated;
		
		@MarshallField(as="deleted",
					   whenXml=@MarshallFieldAsXml(collectionElementName="deletedItem"))
		@Getter @Setter private Set<FolderContentReportItem> _deleted;
		
		@MarshallField(as="untouched",
					   whenXml=@MarshallFieldAsXml(collectionElementName="untouchedItem"))
		@Getter @Setter private Set<FolderContentReportItem> _untouched;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
		public Set<Path> getNewOrUpadtedPaths() {
			if (CollectionUtils.isNullOrEmpty(_newOrUpdated)) return null;
			return FluentIterable.from(_newOrUpdated)
						.transform(new Function<FolderContentReportItem,Path>() {
											@Override
											public Path apply(final FolderContentReportItem item) {
												return item.getPath();
											}
								   })
						.toSet();
		}
		public Set<Path> getDeletedPaths() {
			if (CollectionUtils.isNullOrEmpty(_deleted)) return null;
			return FluentIterable.from(_deleted)
						.transform(new Function<FolderContentReportItem,Path>() {
											@Override
											public Path apply(final FolderContentReportItem item) {
												return item.getPath();
											}
								   })
						.toSet();
		}
		public Set<Path> getUntouchedPaths() {
			if (CollectionUtils.isNullOrEmpty(_untouched)) return null;
			return FluentIterable.from(_untouched)
						.transform(new Function<FolderContentReportItem,Path>() {
											@Override
											public Path apply(final FolderContentReportItem item) {
												return item.getPath();
											}
								   })
						.toSet();
		}
		
		@Override
		public CharSequence debugInfo() {
			return Strings.customized("{} new or updated / {} deleted / {} unchanged",
									  _newOrUpdated != null ? _newOrUpdated.size() : 0,
									  _deleted != null ? _deleted.size() : 0,
									  _untouched != null ? _untouched.size() : 0);
		}
	}
	@MarshallType(as="folderContentReportItem")
	@Accessors(prefix="_")
	public static class FolderContentReportItem 
			 implements Serializable,
			 			CanBeRepresentedAsString {

		private static final long serialVersionUID = 255494264159950729L;

		@MarshallField(as="type",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final FolderContentReportItemType _type;
		
		@MarshallField(as="path",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final Path _path;
		
		@MarshallField(as="size",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final long _size;
		
		@MarshallField(as="owner",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final LoginID _owner;
		
		@MarshallField(as="lastUpdated",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final long _lastUpdatedTimeStamp;
		
		@MarshallField(as="created",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final long _createdTimeStamp;
		
		public FolderContentReportItem(@MarshallFrom("type") final FolderContentReportItemType type,
									   @MarshallFrom("path") final Path path,
									   @MarshallFrom("size") final long size,
									   @MarshallFrom("owner") final LoginID owner,
									   @MarshallFrom("lastUpdated") final long lastUpdatedTS,@MarshallFrom("created") final long createdTS) {
			_type = type;
			_path = path;
			_size = size;
			_owner = owner;
			_lastUpdatedTimeStamp = lastUpdatedTS;
			_createdTimeStamp = createdTS;
		}
		
		public static FolderContentReportItem from(final FileProperties props) {
			return new FolderContentReportItem(FolderContentReportItemType.of(props),
											   props.getPath(),
											   props.getSize(),
											   props.getOwner(),
											   props.getModificationTimeStamp(),
											   props.getCreateTimeStamp());
		}
		@Override
		public String asString() {
			return Strings.customized("{},{},{},{},{},{}",
									  _type != null ? _type.name() : "unknown-type",
									  _path != null ? _path.asAbsoluteString() : "unknown-path",
									  _size,
									  _owner != null ? _owner : "unknown-owner",
									  _lastUpdatedTimeStamp,
									  _createdTimeStamp);
		}
		@Override
		public String toString() {
			return this.asString();
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (obj == null) return false;
			if (this == obj) return true;
			if (!(obj instanceof FolderContentReportItem)) return false;
			FolderContentReportItem other = (FolderContentReportItem)obj;
			
			boolean typeEq = this.getType() != null && other.getType() != null 
								?  this.getType() == other.getType()
								: this.getType() != null && other.getType() == null 
										? false
										: this.getType() == null && other.getType() != null
												? false
												: true;		// both eqs
			boolean pathEq = this.getPath() != null && other.getPath() != null
								? this.getPath().equals(other.getPath())
								: this.getPath() != null && other.getPath() == null
										? false
										: this.getPath() == null && other.getPath() != null
												? false
												: true;		// both null
			boolean sizeEq = this.getSize() == other.getSize();
			boolean userEq = this.getOwner() != null && other.getOwner() != null
								? this.getOwner().equals(other.getOwner())
								: this.getOwner() != null && other.getOwner() == null
										? false
										: this.getOwner() == null && other.getOwner() != null
												? false
												: true;		// both null
			boolean lastUpdEq = this.getLastUpdatedTimeStamp() == other.getLastUpdatedTimeStamp();
			boolean createEq = this.getCreatedTimeStamp() == other.getCreatedTimeStamp();
			return typeEq && pathEq && sizeEq && userEq && lastUpdEq && createEq;
		}
		@Override
		public int hashCode() {
			return Objects.hashCode(_type,_path,_size,_owner,_lastUpdatedTimeStamp,_createdTimeStamp);
		}
	}
	public static enum FolderContentReportItemType {
		D,	// directory
		F;	// folder
		
		public static FolderContentReportItemType of(final FileProperties props) {
			return props != null ? props.isFolder() ? D
								 					: props.isFile() ? F
								 									 : null
								 : null;
		}
	}
}
