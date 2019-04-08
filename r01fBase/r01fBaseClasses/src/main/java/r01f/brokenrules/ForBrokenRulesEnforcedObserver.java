package r01f.brokenrules;

import java.util.Collection;

import r01f.patterns.reactive.Observer;

/**
 * Inteface to be implemented by objects observing model objects that enforces business rules
 * @param <B>
 */
public interface ForBrokenRulesEnforcedObserver<B extends BrokenRule> 
		 extends Observer {
	/**
	 * The observer object is being notified by the observable object about the
	 * existence of a collection of broken rules
	 * If the provided collection is null all rules are verified by the observable object
	 * @param brokenRules
	 */
	public void onBrokenRules(final Collection<B> brokenRules);
}
