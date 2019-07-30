package r01f.patterns;

public interface Subscriber<T> 
	     extends OnSuccessSubscriber<T>,
				 OnErrorSubscriber {
	// just a composite interfcace
}
