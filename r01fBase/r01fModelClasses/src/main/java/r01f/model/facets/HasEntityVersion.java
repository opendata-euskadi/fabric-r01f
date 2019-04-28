package r01f.model.facets;


/**
 * Every persistable model object should implement this interface if
 * Optimistic Locking is being used and a long-type entity version field is 
 * used to check for db inconsistencies:
 * If optimistic locking is used, conflicts are unlike to happen is assumed
 * i.e. There are two web processes running in parallel, both processing the 
 * 		stock of an store item
 * 		... let's say that initially we have stock=100
 * 			----------[100]----------
 * 			|						|
 * 		  Load                    Load
 *          |-1						|-1
 *        [99]                     [99]
 *          |						|
 *        Save                    Save
 *          |---------[99]			|
 *          		  [99]----------| <---WTF!! the stock should have been 98
 *          									but it ends being 99: WRONG!!
 * To prevent this situation a last update timestamp or an incrementing version is used
 * Every time a process want to update an entity it MUST tell us what the version is so
 * if a conflict occurs it could be detected:
 * 
 * 			----------[100]----------
 * 			|	   (version=1)		|
 * 			|						|
 * 	      Load 				       Load 
 * 	   (version=1) 		       (version=1) 
 *          |-1						|-1
 *        [99]                     [99]
 *          |						|
 *        Save                      |
 *     (version=1)                  |
 *          |---------[99]			|
 *          	   (version=2)		|
 *          			|		   Save
 *          		CONFLICT!<--(Version=1)
 *          
 * As seen, to be able to detect conflicts:
 * 		- A version number (a timestamp) MUST be stored with the record
 * 		- The version number MUST be loaded alongside the record and stored at the processing client
 * 		- The version number MUST be send alongside the record in any update operation 
 * 		  so the received version could be compared with the provided one
 */
public interface HasEntityVersion
	     extends ModelObjectFacet {
	/**
	 * @return the entity version used to achieve the Optimistic Locking behavior
	 */
	public long getEntityVersion();
	/**
	 * @param version the entity version used to achieve the Optimistic Locking behavior
	 */
	public void setEntityVersion(long version);
}
