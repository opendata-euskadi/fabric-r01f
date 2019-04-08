package r01f.patterns.reactive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * A base implementation for the {@link Observable} interface
 */
public abstract class ObservableBase 
	       implements Observable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private List<Observer> _observers = new ArrayList<Observer>();

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <O extends Observer> void addObserver(final O observer) {
		if (observer == null) throw new IllegalArgumentException();
		if (_observers.contains(observer)) return;
		_observers.add(observer);
	}
	public boolean hasObservers() {
		return _observers != null && _observers.size() > 0;
	}
	public boolean hasObserver(final Class<? extends Observer> observerType) {
		boolean outContainsObserver = false;
		if (_observers != null && _observers.size() > 0) {
			for (Observer obs : _observers) {
				if (obs.getClass() == observerType) {
					outContainsObserver = true;
					break;
				}
			}
		}
		return outContainsObserver;
	}
	public Collection<Observer> allObservers() {
		return _observers;
	}
	/**
	 * Returns observers of the provided type
	 * The default impl for this method is _filterObserversOfType() BUT it's not used here 
	 * because GWT does NOT supports isAssignableFrom
	 * @param observerType
	 * @return
	 */
	public abstract <O extends Observer> Collection<Observer> observersOfType(final Class<O> observerType);
	
	
	
	@GwtIncompatible("GWT does NOT supports isAssignableFrom")
	protected static <O extends Observer> Collection<Observer> _filterObserversOfType(final Collection<Observer> observers,
																					  final Class<O> observerType) {
		Collection<Observer> outObservers = null;
		if (observers != null && observers.size() > 0) {
			outObservers = Collections2.filter(observers,
											   new Predicate<Observer>() {
														@Override
														public boolean apply(final Observer obs) {
															return obs.getClass().isAssignableFrom(observerType);
														}
												});
		}
		return outObservers;
		
	}
}
