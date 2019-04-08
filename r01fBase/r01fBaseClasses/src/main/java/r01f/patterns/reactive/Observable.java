package r01f.patterns.reactive;

public interface Observable {
	/**
	 * Adds an observer to the registered observers list
	 * @param observer
	 */
	public <O extends Observer> void addObserver(final O observer);
}
