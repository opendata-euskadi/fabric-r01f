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
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;



/**
 * Modela un item de seguridad.
 */
@Accessors(prefix="_")
public class XLNetsItemSeguridad
     extends XLNetsObjectBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  SENTENCIAS XPath
/////////////////////////////////////////////////////////////////////////////////////////
    private static final String UID = "parametro[@id='n38uidobjseguridad']/valor[1]/text()";
    private static final String CN = "parametro[@id='cn']/valor[1]/text()";
    private static final String PATH_EJECUTABLE = "parametro[@id='n38path-ejecutable']/valor[1]/text()";
    private static final String DESCRIPCION_ES = "parametro[@id='n38cadescripcion']/valor[1]/text()";
    private static final String DESCRIPCION_EU = "parametro[@id='n38eudescripcion']/valor[1]/text()";    
    private static final String URL = "parametro[@id='url']/valor[1]/text()";
    private static final String TIPO = "parametro[@id='n38tipo']/valor[1]/text()";
    private static final String SUBTIPO = "parametro[@id='n38subtipo']/valor[1]/text()";
    private static final String AUTORIZACIONES = "elemento[@subtipo='n38autorizacion']";
    
    private static final String ACCIONES = "parametro[@id='n38acciones']/valor"; 
    private static final String PERFILES = "parametro[@id='n38perfiles']/valor";
/////////////////////////////////////////////////////////////////////////////////////////
//  ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter private final String _UID;
    @Getter private final String _commonName;
    @Getter private final String _pathEjecutable;
    @Getter private final LanguageTexts _descripcion;
    @Getter private final String _tipo;
    @Getter private final String _subTipo;
    @Getter private final String[] _acciones;
    @Getter private final String[] _perfiles;
    @Getter private final XLNetsAutorizacion[] _autorizaciones;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public XLNetsItemSeguridad(Node itemSeguridadNode) {
        super();
        _UID = _extractValue(itemSeguridadNode,UID); 
        _commonName = _extractValue(itemSeguridadNode,CN); 
    	_pathEjecutable = _extractValue(itemSeguridadNode,PATH_EJECUTABLE); 
    	_descripcion = new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
		    							   .add(Language.SPANISH,_extractValue(itemSeguridadNode,DESCRIPCION_ES))
		    							   .add(Language.BASQUE,_extractValue(itemSeguridadNode,DESCRIPCION_EU));
    	_tipo = _extractValue(itemSeguridadNode,TIPO);
    	_subTipo = _extractValue(itemSeguridadNode,SUBTIPO);
    	_acciones = _extractMultipleValue(itemSeguridadNode,ACCIONES); 
    	_perfiles = _extractMultipleValue(itemSeguridadNode,PERFILES);
    	_autorizaciones = _autorizaciones(itemSeguridadNode);
    }
    /** 
     * Devuelve las autorizaciones asociadas con este tipo de objeto
     */
    private static XLNetsAutorizacion[] _autorizaciones(final Node node) {
        XLNetsAutorizacion[] outAuths = null;        
        NodeList nl = _executeXPathForNodeList(node,AUTORIZACIONES);
        if (nl != null) {
            outAuths = new XLNetsAutorizacion[nl.getLength()]; 
            for (int i=0; i<nl.getLength(); i++) outAuths[i] = new XLNetsAutorizacion(nl.item(i));
        }
        return outAuths;        
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
    	StringBuilder dbg = new StringBuilder();
    	if (Strings.isNOTNullOrEmpty(this.getUID()))            dbg.append("            UID: ").append(this.getUID()).append("\n");             
    	if (Strings.isNOTNullOrEmpty(this.getCommonName()))     dbg.append("             CN: ").append(this.getCommonName()).append("\n");      
    	if (this.getDescripcion() != null)                      dbg.append("    Descripcion: ").append(this.getDescripcion()).append("\n");     
    	if (Strings.isNOTNullOrEmpty(this.getTipo()))           dbg.append("           Tipo: ").append(this.getTipo()).append("\n");            
    	if (Strings.isNOTNullOrEmpty(this.getSubTipo()))        dbg.append("        SubTipo: ").append(this.getSubTipo()).append("\n");         
    	if (Strings.isNOTNullOrEmpty(this.getPathEjecutable())) dbg.append("Path-Ejecutable: ").append(this.getPathEjecutable()).append("\n");  
    	if (CollectionUtils.hasData(this.getAcciones()))        dbg.append("       Acciones: ").append(this.getAcciones()).append("\n");        
    	if (CollectionUtils.hasData(this.getPerfiles()))        dbg.append("       Perfiles: ").append(this.getPerfiles()).append("\n");        
    	if (CollectionUtils.hasData(this.getAutorizaciones()))  dbg.append(" Autorizaciones:\n").append(this.getAutorizaciones()).append("\n"); 
        return dbg.toString();
    }
}
