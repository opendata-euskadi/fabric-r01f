package r01f.patterns.reactive;

import java.util.ArrayList;
import java.util.List;


/**
 * Base implementation for the {@link Observable} pattern
 * @param <T>
 * @param <O>
 */
public abstract class TypeSafeObservableBase<T extends TypeSafeObservable<T,O>,
					    		     		 O extends TypeSafeObserver<O,T>>
		   implements TypeSafeObservable<T,O> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected List<TypeSafeObserver<O,T>> _observers = new ArrayList<TypeSafeObserver<O,T>>();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void addObserver(final O observer) {
		if (observer == null) throw new IllegalArgumentException();
		if (_observers.contains(observer)) return;
		_observers.add(observer);
	}
	public boolean hasObservers() {
		return _observers != null && _observers.size() > 0;
	}
}
