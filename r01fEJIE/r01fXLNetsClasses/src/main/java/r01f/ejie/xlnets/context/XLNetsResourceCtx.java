/*
 * Created on 26-jul-2004
 *
 * @author IE00165H
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.ejie.xlnets.model.XLNetsAutorizacion;
import r01f.ejie.xlnets.model.XLNetsFuncion;
import r01f.ejie.xlnets.model.XLNetsItemSeguridad;
import r01f.ejie.xlnets.model.XLNetsTipoObjeto;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


/**
 * Authorized resource item
 */
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class XLNetsResourceCtx 
  implements Serializable {
    private static final long serialVersionUID = -1471890948119134239L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private String _parentOid = "rctx-unknown"; 
    @Getter @Setter private String _oid = "rctx-unknown";       
    @Getter @Setter private LanguageTexts _name = null;			
    @Getter @Setter private String _type = null;      			
    @Getter @Setter private String _subtype = "rctx-unknown"; 	
    @Getter @Setter private Map<String,XLNetsResourceAuthorization> _authorizations = null;		
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public XLNetsResourceCtx(final XLNetsFuncion func) {
        this(func.getItemSeguridad());
    }
    public XLNetsResourceCtx(final XLNetsTipoObjeto tipoObj) {
    	this(tipoObj.getItemSeguridad());
    }
    public XLNetsResourceCtx(final XLNetsItemSeguridad itemSeg) {
        this(null,									// parent oid
        	itemSeg.getUID(),						// oid
			itemSeg.getDescripcion(),				// name
			itemSeg.getTipo(),itemSeg.getSubTipo(),	// tipo / subtipo
			_obtainItemAuths(itemSeg));				// autorizaciones
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS
/////////////////////////////////////////////////////////////////////////////////////////
//    public String toXML() throws XOMarshallerException {
//        return XOManager.getXML(XMLProperties.get("r01f","authMapPath"),this);
//    }
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Comprueba si el objeto es valido
     * @return true si el objeto es valido y false si no es asín
     */
    public boolean isValid() {
        if (Strings.isNullOrEmpty(_oid)) return false;
        if (_name == null || _name.getFor(Language.SPANISH) == null || _name.getFor(Language.BASQUE) == null) return false;
        if (CollectionUtils.isNullOrEmpty(_authorizations)) return false;
        for (XLNetsResourceAuthorization auth : _authorizations.values()) {
            if ( !auth.isValid() ) return false;
        }
        return true;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Obtiene las autorizaciones del recurso
     * @param itemSeg item de seguridad
     * @return un mapa con objetos {@link R01FResourceAuthorization}
     */
    private static Map<String,XLNetsResourceAuthorization> _obtainItemAuths(final XLNetsItemSeguridad itemSeg) {
        Map<String,XLNetsResourceAuthorization> outAuths = null;
        
        // Cada una de las autorizaciones del recurso
        if (CollectionUtils.hasData(itemSeg.getAutorizaciones())) {
            outAuths = new HashMap<String,XLNetsResourceAuthorization>(itemSeg.getAutorizaciones().length);
            for (XLNetsAutorizacion auth : itemSeg.getAutorizaciones()) {
            	XLNetsResourceAuthorization currAuth = new XLNetsResourceAuthorization(auth.getCommonName(),
                        								 							   auth.getDescripcion(),
                        								 							   auth.getAcciones(),
                        								 							   auth.getProfileOid());
                outAuths.put(currAuth.getOid(),currAuth);
            }
        }
        return outAuths;
    }

}
