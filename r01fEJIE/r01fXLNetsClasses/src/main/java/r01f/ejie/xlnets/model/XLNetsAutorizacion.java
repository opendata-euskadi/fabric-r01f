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
public class XLNetsAutorizacion 
     extends XLNetsObjectBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTES
/////////////////////////////////////////////////////////////////////////////////////////
    private static transient final String CN = "parametro[@id='cn']/valor[1]/text()";
    private static transient final String DESCRIPCION_ES = "parametro[@id='n38cadescripcion']/valor[1]/text()";
    private static transient final String DESCRIPCION_EU = "parametro[@id='n38eudescripcion']/valor[1]/text()";    
    private static transient final String PERFIL = "parametro[@id='n38uidperfil']/valor[1]/text()";
    private static transient final String ACCIONES = "parametro[@id='n38acciones']/valor";
/////////////////////////////////////////////////////////////////////////////////////////
//  ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter private final String _commonName; 
    @Getter private final LanguageTexts _descripcion;
    @Getter private final String _profileOid;
    @Getter private final String[] _acciones;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
    public XLNetsAutorizacion(final Node authNode) {
        super();
        // Obtener los valores del nodo
        _commonName = _extractValue(authNode,CN);
        _descripcion = new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
		        						   .add(Language.SPANISH,_extractValue(authNode,DESCRIPCION_ES))
		    							   .add(Language.BASQUE,_extractValue(authNode,DESCRIPCION_EU));
        _profileOid = _extractValue(authNode,PERFIL); 
        _acciones = _extractMultipleValue(authNode,ACCIONES); 
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder("\tAutorizacion:\n");
    	if (Strings.isNOTNullOrEmpty(this.getCommonName())) sb.append("\t\t         CN: ").append(this.getCommonName()).append("\n");
    	if (this.getDescripcion() != null) 					sb.append("\t\tDescripcion: ").append(this.getDescripcion()).append("\n");
    	if (this.getProfileOid() != null) 					sb.append("\t\t     Perfil: ").append(this.getProfileOid()).append("\n");
    	if (CollectionUtils.hasData(this.getAcciones())) 	sb.append("\t\t   Acciones: ").append(this.getAcciones()).append("\n");
    	return sb.toString();
    }
}
