package r01f.html.css;


public class ProcessorException
	 extends RuntimeException {

	private static final long serialVersionUID = 3526962880288381333L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public ProcessorException(final String msg) {
		super(msg);
	}
	public ProcessorException(final String msg,final Throwable other) {
		super(msg,other);
	}
	public ProcessorException(final Throwable other) {
		super(other);
	}
}
