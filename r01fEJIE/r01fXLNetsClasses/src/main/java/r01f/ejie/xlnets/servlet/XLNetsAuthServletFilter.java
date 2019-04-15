package r01f.ejie.xlnets.servlet;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import r01f.xmlproperties.XMLPropertiesComponent;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

public class XLNetsAuthServletFilter
     extends XLNetsAuthServletFilterBase {
/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public XLNetsAuthServletFilter(@XMLPropertiesComponent("xlNets") final XMLPropertiesForAppComponent props) {
		super(props);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _attachBusinessModelObjectToLocalThreadIfNeeed(final HttpServletRequest request) {
	  // Do nothing. The default XLNetsAuthServletFilter has no business model object to attach into a local thread.
	  // If needed to attach a business model  into a local thread, extend XLNetsAuthServletFilterBase and implement this method
	}
	@Override
	protected void _doFinallyAfterFilter() {
		// Do nothing.
	}
}
