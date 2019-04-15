/*
 * Created on 11-ago-2004
 * 
 * @author ie00165h
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.model;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.Getter;
import lombok.experimental.Accessors;



/**
 * Modela un tipo de objeto xlnets.
 */
@Accessors(prefix="_")
public class XLNetsTipoObjeto 
     extends XLNetsObjectBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  SENTENCIAS XPath
/////////////////////////////////////////////////////////////////////////////////////////
    private static transient final String ITEM_SEGURIDAD = "/n38/elementos[@tipo='n38ItemSeguridad']/elemento[@subtipo='n38itemSeguridad']";
    private static transient final String INSTANCES = "/n38/elementos[@tipo='n38ItemSeguridad']/elemento[@subtipo='n38itemSeguridad']/elemento[@subtipo='n38itemSeguridad']";
/////////////////////////////////////////////////////////////////////////////////////////
//  ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter private final XLNetsItemSeguridad _itemSeguridad;
    @Getter private final XLNetsItemSeguridad[] _instances;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////    
    public XLNetsTipoObjeto(final Node nodeTipoObjeto) {
        super();
        _itemSeguridad = _itemSeguridad(nodeTipoObjeto);
        _instances = _instances(nodeTipoObjeto);
    }   
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS PRIVADOS
/////////////////////////////////////////////////////////////////////////////////////////
    /** 
     * Devuelve los items de seguridad de las instancias
     */
    private static XLNetsItemSeguridad[] _instances(final Node nodeTipoObjeto) {
        XLNetsItemSeguridad[] outInstances = null;        
        NodeList nl = _executeXPathForNodeList(nodeTipoObjeto,INSTANCES); 
        if (nl != null) {
            outInstances = new XLNetsItemSeguridad[nl.getLength()]; 
            for (int i=0; i<nl.getLength(); i++) outInstances[i] = _itemSeguridad(nl.item(i));
        }
        return outInstances;
    }
    /** 
     * Devuelve el uid del item de seguridad
     */
    private static XLNetsItemSeguridad _itemSeguridad(final Node nodeTipoObjeto) { 	
        return new XLNetsItemSeguridad(_executeXPathForNode(nodeTipoObjeto,ITEM_SEGURIDAD)); 
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        sb.append(">>>Tipo de objeto______________________________\n");
        sb.append(this.getItemSeguridad()).append("\n");
        sb.append(">>>Instancias\r\n");
        XLNetsItemSeguridad[] instancias = this.getInstances();
        if (instancias != null) {
            for (int i=0; i<instancias.length;i++) {
                sb.append("________________________________________________\n");
                sb.append(instancias[i]);
            }
        }
        return sb.toString();
    }
}
