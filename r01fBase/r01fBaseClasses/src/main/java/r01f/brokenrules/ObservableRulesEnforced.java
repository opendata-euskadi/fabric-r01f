package r01f.brokenrules;

import java.util.Collection;

import r01f.patterns.reactive.Observable;

/**
 * Interface to be implemented by objects that can enforce business rules
 * @param <B>
 */
public interface ObservableRulesEnforced 
	     extends Observable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Notify registered observers that the model object has broken rules giving them chance
	 * to for example display an alert indicator
	 * @param brokenRules
	 */
	public void notifyObserversAboutBrokenRules(final Collection<BrokenRule> brokenRules);
}
