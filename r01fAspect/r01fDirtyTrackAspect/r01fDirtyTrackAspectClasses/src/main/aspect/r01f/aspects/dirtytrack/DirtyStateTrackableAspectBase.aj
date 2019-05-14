package r01f.aspects.dirtytrack;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.reflect.FieldSignature;

import r01f.aspects.core.dirtytrack.DirtyTrackingStatusImpl;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;

/**
 * Base aspect for the dirty tracking behavior
 * Here the pointcuts are defined and reused at the aspects
 * @param <D> type extending {@link DirtyStateTrackable}
 */
privileged public abstract aspect DirtyStateTrackableAspectBase<D extends DirtyStateTrackable> {
	
//	pointcut simpleMarshallerMappingsFromAnnotationsLoaderMethodExecution() : execution(* SimpleMarshallerMappingsFromAnnotationsLoader.*(..));
//	pointcut notInsimpleMarshallerMappingsFromAnnotationsLoaderFlow() : !cflow(simpleMarshallerMappingsFromAnnotationsLoaderMethodExecution()) && !within(r01f.aspects.dirtytrack.*);
	
/////////////////////////////////////////////////////////////////////////////////////////
//  Reusable PointCuts
/////////////////////////////////////////////////////////////////////////////////////////	
	/** 
	 * {@link DirtyStateTrackable} object creation
	 */
	pointcut newDirtyStateTrackableObjectCreation() : 
					   execution((DirtyStateTrackable+).new(..)) 	// DirtyStateTrackable implementing object constructor
					&& !within(r01f.types.dirtytrack..*);			// for types NOT in r01f.types.. package
	/**
	 * {@link DirtyStateTrackable} modification
	 * @param obj {@link DirtyStateTrackable} implementing object
	 * @param newVal the new value to be set into the member
	 */
	pointcut fieldSetInDirtyStateTrackableObj(D trck,Object newVal) : 
							(set(!static !final * DirtyStateTrackable+.*) 					// any non static non final DirtyStateTrackable-type member...
								&& !set(* *._tracking*)										// ... that it's NOT either _trackingStatus or _trackingMapChangesTracker members 
			 					&& !set(@NotDirtyStateTrackable * DirtyStateTrackable.*)	// ... nor annotated with NotDirtyStateTrackable
			 				)
							&& !within(r01f.types.dirtytrack..*)	// to be used on types NOT in package r01f.types..
							&& target(trck)							// the object containing the member
							&& args(newVal);						// the new member value
/////////////////////////////////////////////////////////////////////////////////////////
//	DirtyStateTrackable OBJECTS CREATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Advice after: this is executed AFTER a new {@link DirtyStateTrackable} object creation 
	 */
	after (D trck) returning : 
				newDirtyStateTrackableObjectCreation()
			 && this(trck) {
		trck.getTrackingStatus().setThisNew(true);	// new object...
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HOOK ON SIMPLE STATE MODIFICATIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Advice after: this is executed BEFORE setting a field
	 */
	before(D trck,Object newValue) : 
				fieldSetInDirtyStateTrackableObj(trck,newValue) {
		// Get the modified field
		FieldSignature fs = (FieldSignature)thisJoinPointStaticPart.getSignature();
		Field field = fs.getField();
		
		// run the aspect logic
		DirtyTrackingStatusImpl._beforeSetMember(trck,field,newValue); 
	}
	/**
	 * Advice after: this is executed AFTER setting a field
	 */
	after(D trck,Object newValue) : 
				fieldSetInDirtyStateTrackableObj(trck,newValue) {
		// Get the modified field
		FieldSignature fs = (FieldSignature)thisJoinPointStaticPart.getSignature();
		Field field = fs.getField();
		
		// run the aspect logic
		DirtyTrackingStatusImpl._afterSetMember(trck,field,newValue);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	Collections (Map / Collection) set
// 	When a Collection-type (Map/Collection) is set, the real collection is changed
// 	with another wrapping the original one; this wrapper is in charge of tracking Map / Collection
// 	changes and also tells the object containing the Map / Collection that the field has changed
//  so it can set it's dirty status acordingly
//	NOTE: Advices could be defined as: 
// 				Map around() : get(Map+ DirtyStateTrackable+.*) 
// 		  BUT it throws an ERROR when the collection is a type extending a Map / List / Set...
// 		  ie:
// 				public class MyObj extends HashMap.
//	
//
// BEWARE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// There are TWO ways to invoke a mutator method in a Map / Collection which is a field of a container object 
// 		public class Container {
// 			public Map _mapField;
// 		}
// CASE 1: From OUTSIDE the Collection-type field container object
// 			public class OtherObj {
// 				public void method() { 
// 					container.getMapField().put(..) <-- the pointcut is at the method calling put(..) in Map,
// 				}										... the pointcut is at OtherObj and NOT at the Collection-type field container object 
// 			}											
// CASE 2: From INSIDE the object containing the Collection-type field
// 			public class OtherObj {
// 				public void method() {
// 					container.putIntoMapField(..) <-- the putIntoMapField method belongs to the Collection-type field container object 
// 				}								 	  ... this method internally calls Map's put(..) method so the pointcut is at 
// 			} 											  the Collection-type field container object
// This means that a single pointcut cannot be set for the two cases
// 
// This implementation is complex so Collection-type fields access methods is captured and a wrapper is returned
// ... this wrapper tracks Collection-type changes
/////////////////////////////////////////////////////////////////////////////////////////
//    Object around(D trck) : 
//    			get(LanguageTexts+ D+.*)	// get((!DirtyStateTrackable && Map+) DirtyStateTrackable+.*)
//    		 && !within(r01f.types.dirtytrack..*)	// NOT a type at r01f.types
//    		 && target(trck) {						// the object containing the field
//		// Get the underlying map 
//    	Map theMap = (Map)proceed(trck);			// the underlying Map
//    	
//    	// Get the Map field being accessed
//		FieldSignature fs = (FieldSignature)thisJoinPointStaticPart.getSignature();
//		Field f = fs.getField();
//		System.out.println("----" + f);
//		
//		// Run the aspect 
//    	return DirtyTrackingStatusImpl._arroundMapFieldGet(trck,f,theMap);
//    }
    Object around(D trck) : 
    			get(Map+ D+.*)	// get((!DirtyStateTrackable && Map+) DirtyStateTrackable+.*)	// a Map field of a DirtyStateTrackable type
    		 && !within(r01f.types.dirtytrack..*)	// NOT a type at package r01f.types.dirtytrack..
    		 && target(trck) {						// the object containing the map
		// Get the map
    	Map theMap = (Map)proceed(trck);			// get the underlying Map
  
    	// Get the Map field being accessed
		FieldSignature fs = (FieldSignature)thisJoinPointStaticPart.getSignature();
		Field f = fs.getField();
		
		// Run the aspect
    	return DirtyTrackingStatusImpl._arroundMapFieldGet(trck,f,theMap);
    }
    Object around(D trck) : 
    			get(Collection+ D+.*) 				// a Collection field of a DirtyStateTrackable type
    		 && !within(r01f.types.dirtytrack..*)	// NOT a type at package r01f.types.dirtytrack..
    		 && target(trck) {						// the object containing the Collection				
		// Get the collection
    	Collection theCol = (Collection)proceed(trck);	// Get the underlying collection
    	
    	// Get the collection field being accessed
		FieldSignature fs = (FieldSignature)thisJoinPointStaticPart.getSignature();
		Field field = fs.getField();
		
		// Run the aspect
		return DirtyTrackingStatusImpl._arroundCollectionFieldGet(trck,field,theCol);
    }
    Object around(D trck) : 
    			get(List+ D+.*) 						// a List field of a DirtyStateTrackable type
    		 && !within(r01f.types.dirtytrack..*) 		// NOT a type at package r01f.types.dirtytrack..
    		 && target(trck) {							// the object containing the Collection			
		// Get the list
    	List theList = (List)proceed(trck);				// Get the underlying List
    	
    	// Get the collection field being accessed
		FieldSignature fs = (FieldSignature)thisJoinPointStaticPart.getSignature();
		Field field = fs.getField();
		
		// Run the aspect
		return DirtyTrackingStatusImpl._arroundCollectionFieldGet(trck,field,theList);
    }
    Object around(D trck) :
    			get(Set+ D+.*) 						// a List field of a DirtyStateTrackable type
    		 && !within(r01f.types.dirtytrack..*) 	// NOT a type at package r01f.types.dirtytrack..
    		 && target(trck) {						// the object containing the Collection	
		// Get the Set
    	Set theSet = (Set)proceed(trck);			// Get the underlying Set
    	
    	// Get the collection field being accessed
		FieldSignature fs = (FieldSignature)thisJoinPointStaticPart.getSignature();
		Field field = fs.getField();
    	
		// Run the aspect
		return DirtyTrackingStatusImpl._arroundCollectionFieldGet(trck,field,theSet);
    }
}
