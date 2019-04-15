/*
 * Created on 11-ago-2004
 * 
 * @author ie00165h
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.model;

import org.w3c.dom.Node;

import lombok.Getter;
import lombok.experimental.Accessors;



/**
 * Modela un tipo de objeto xlnets.
 */
@Accessors(prefix="_")
public class XLNetsFuncion 
     extends XLNetsObjectBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  SENTENCIAS XPath
/////////////////////////////////////////////////////////////////////////////////////////
    private static transient final String ITEM_SEGURIDAD = "/n38/elementos[@tipo='n38ItemObtenerAutorizacion']/elemento[@subtipo='n38itemSeguridad']";
/////////////////////////////////////////////////////////////////////////////////////////
//  ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter private final XLNetsItemSeguridad _itemSeguridad;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
    public XLNetsFuncion(final Node nodeFuncion) {
        super();
        _itemSeguridad = new XLNetsItemSeguridad(_executeXPathForNode(nodeFuncion,ITEM_SEGURIDAD));
    }   
/////////////////////////////////////////////////////////////////////////////////////////
//  OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        StringBuilder dbg = new StringBuilder();
        if (this.getItemSeguridad() != null) dbg.append(">>>Funcion______________________________\n")
        										.append(this.getItemSeguridad())
        										.append("\n");
        return dbg.toString();
    }
}
