package r01f.aspects.dirtytrack;

import java.util.Collection;
import java.util.List;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableCollection;
import r01f.collections.dirtytrack.util.ChangesTrackedListMethods;

/**
 * Converts a type implementing List into a changes trackable List
 */
privileged public aspect ChangestTrackableListAspect 
	  			 extends ChangestTrackableCollectionAspectBase<ChangesTrackableCollection> {
/////////////////////////////////////////////////////////////////////////////////////////
//	COLLECTION'S MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressAjWarnings
    void around(List list,int index,Object value) : 
    			call(public * (@ConvertToDirtyStateTrackable List+).add(int,*))	// llamar al m�todo add de un tipo anotado con @ConvertToDirtyStateTrackable y que extiende de List
    		 && args(index,value)												// argumentos del metodo add
    		 && target(list) {													// el tipo anotado con @ConvertToDirtyStateTrackable y que extiende de List
    	ChangesTrackableCollection trck = (ChangesTrackableCollection)list;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		ChangesTrackedListMethods.add(index,value,
								 		  list,trck.getChangesTracker());
    	} else {
    		proceed(list,index,value);
    	}
    }
    @SuppressAjWarnings
    Object around(List list,int index,Object value) : 
    			call(public * (@ConvertToDirtyStateTrackable List+).set(int,*))	// llamar al m�todo set de un tipo anotado con @ConvertToDirtyStateTrackable y que extiende de List
    		 && args(index,value)												// argumentos del metodo set
    		 && target(list) {													// el tipo anotado con @ConvertToDirtyStateTrackable y que extiende de List
    	Object outVal = null;
    	ChangesTrackableCollection trck = (ChangesTrackableCollection)list;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		outVal = ChangesTrackedListMethods.set(index,value,
    											   list,trck.getChangesTracker());
    	} else {
    		outVal = proceed(list,index,value);
    	}
    	return outVal;
    }
    @SuppressAjWarnings
    boolean around(List list,int fromIndex,Collection otherCol) : 
    			call(public * (@ConvertToDirtyStateTrackable List+).addAll(int,Collection))		// llamar al m�todo addAll de un tipo anotado con @ConvertToDirtyStateTrackable y que extiende de List
    		 && args(fromIndex,otherCol)															// argumentos del metodo addAll
    		 && target(list) {																	// el tipo anotado con @ConvertToDirtyStateTrackable y que extiende de List
    	boolean outVal = true;
    	ChangesTrackableCollection trck = (ChangesTrackableCollection)list;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		outVal = ChangesTrackedListMethods.addAll(fromIndex,otherCol,
    											   	  list,trck.getChangesTracker());
    	} else {
    		outVal = proceed(list,fromIndex,otherCol);
    	}
    	return outVal;
    }
    @SuppressAjWarnings
    Object around(List list,int index) : 
    			call(public * (@ConvertToDirtyStateTrackable List+).remove(int))// llamar al m�todo remove de un tipo anotado con @ConvertToDirtyStateTrackable y que extiende de List
    		 && args(index)														// argumentos del metodo remove
    		 && target(list) {													// el tipo anotado con @ConvertToDirtyStateTrackable y que extiende de List
    	Object outVal = null;
    	ChangesTrackableCollection trck = (ChangesTrackableCollection)list;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		outVal = ChangesTrackedListMethods.remove(index,
    											   	  list,trck.getChangesTracker());
    	} else {
    		outVal = proceed(list,index);
    	}
    	return outVal;
    }
}
