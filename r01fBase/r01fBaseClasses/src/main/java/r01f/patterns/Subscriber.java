package r01f.patterns;

public interface Subscriber<T> 
	     extends OnSuccessSubscriber<T>,
				 OnErrorSubscriber<T> {
	// just a composite interfcace
}
