package r01f.model.services;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="coreServiceMethodExecResult",typeId="ok")
@Accessors(prefix="_")
public class COREServiceMethodExecOK<T>
	 extends COREServiceMethodExecResultBase<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The result 
	 */
	@MarshallField(as="methodExecResult",
				   whenXml=@MarshallFieldAsXml(collectionElementName="resultItem"))		// only when the result is a Collection (ie: find ops)
	@Getter @Setter protected T _methodExecResult;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public COREServiceMethodExecOK() {
		// by default
		super(COREServiceMethod.UNKNOWN);
	}
	public COREServiceMethodExecOK(final COREServiceMethod method) {
		super(method);
	}
	public COREServiceMethodExecOK(final COREServiceMethod method,
								   final T result) {
		super(method);
		_methodExecResult = result;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public T getOrThrow() throws COREServiceException {
		return _methodExecResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecError<T> asCOREServiceMethodExecError() {
		throw new ClassCastException();
	}
	@Override
	public COREServiceMethodExecOK<T> asCOREServiceMethodExecOK() {
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getDetailedMessage() {
		// info about the returned object
		String resultInfo = null;
		if (_methodExecResult != null) {
			if (CollectionUtils.isCollection(_methodExecResult.getClass())) {
				resultInfo = Strings.customized("Collection of {} objects",
												CollectionUtils.safeSize((Collection<?>)_methodExecResult));
			} else {
				resultInfo = Strings.customized("an object of type {}",
												_methodExecResult.getClass());
			}
		} else {
			resultInfo = "null";
		}
		// the debug info
		return Strings.customized("The execution of '{}' method was SUCCESSFUL returning {}",
						  		  _calledMethod,
						  		  resultInfo);	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return this.getDetailedMessage();
	}
}
