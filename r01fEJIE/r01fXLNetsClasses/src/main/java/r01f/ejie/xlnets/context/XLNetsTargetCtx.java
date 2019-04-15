/*
 * Created on 26-jul-2004
 */
package r01f.ejie.xlnets.context;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.ejie.xlnets.config.XLNetsTargetCfg;
import r01f.util.types.collections.CollectionUtils;

/**
 * Recurso al que se necesita autorizacion para acceder
 */
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class XLNetsTargetCtx
  implements Serializable {
	
	private static final long serialVersionUID = 7653522718638715222L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////        
	@Getter @Setter private XLNetsTargetCfg _targetCfg;		// Configuración de seguridad que se ha macheado en el servlet
    									        			// (del fichero de properties)
    @Getter @Setter private Map<String,XLNetsResourceCtx> _authorizedResources;	// Recursos a los que se tiene acceso   
    
/////////////////////////////////////////////////////////////////////////////////////////
//  GET & SET
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Devuelve un item autorizado en base a su OID
     * @param itemOID El oid del item autorizado
     * @return Un objeto con la info de autorizacion sobre el item
     */
    public XLNetsResourceCtx getAuthorizedResource(final String itemOID) {
        return CollectionUtils.hasData(_authorizedResources) ? _authorizedResources.get(itemOID)
        													 : null;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDEZ
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Comprueba si el objeto es valido
     * @return true si el objeto es valido y false si no es asín
     */
    public boolean isValid() {
        if (_targetCfg == null) return false;
        if (CollectionUtils.isNullOrEmpty(_authorizedResources)) return false;
        for (XLNetsResourceCtx res : _authorizedResources.values()) {
            if ( !res.isValid() ) return false;
        }
        return true;
    }
    
//    public String toXML() throws XOMarshallerException {           
//        return XOManager.getXML(XMLProperties.get("r01f","authMapPath"),this);
//    }
    
   
/////////////////////////////////////////////////////////////////////////////////////////
// 	INNER CLASS QUE REPRESENTA UN ITEM DEL RECURSO   
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Item perteneciente al recurso 
     */
    @Accessors(prefix="_")
    public class ResourceCtx {
        private XLNetsTargetCfg.ResourceCfg _itemCfg = null;
        private Map<String,XLNetsResourceCtx> _items = null;
        
        /**
         * Comprueba si se tiene autorizacion sobre el recurso
         * @return true si hay autorizacion o false si no la hay
         */
        public boolean hasAuthorization() {
            return (CollectionUtils.isNullOrEmpty(_items));
        }        
        /**
         * Comprueba si el objeto es valido
         * @return true si el objeto es valido y false si no es asín
         */
        public boolean isValid() {
            if (_itemCfg == null) return false;
            if (CollectionUtils.isNullOrEmpty(_items)) return false;
            for (XLNetsResourceCtx item : _items.values()) {
                if ( !item.isValid() ) return false;
            }
            return true;
        }    
    }  
}
