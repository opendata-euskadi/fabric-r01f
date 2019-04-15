/*
 * Created on 26-jul-2004
 *
 * @author IE00165H
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.util.types.Strings;

/**
 * Nodo de la jerarquía organizativa a la que pertenece un usuario
 */
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class XLNetsOrgNode
  implements Serializable {
    private static final long serialVersionUID = -2309298283303521160L;
/////////////////////////////////////////////////////////////////////////////////////////
//  MIEMBROS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private XLNetsOrgNode _supraOrg;
    @Getter @Setter private Set<XLNetsOrgNode> _subOrgs;
    @Getter @Setter private String _oid = null;
    @Getter @Setter private LanguageTexts _name;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor
     * @param oid Identificador
     * @param nombreES Nombre en castellano
     * @param nombreEU Nombre en euskara
     * @param supraOrg Organización superior
     */
    public XLNetsOrgNode(final String newOid,
    					 final String newNombreES,final String newNombreEU,
    					 final XLNetsOrgNode newSupraOrg) {
        _oid = newOid;
        _name = new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
        							.add(Language.SPANISH,newNombreES)
        							.add(Language.BASQUE,newNombreEU);
        // Establecer la relacion entre el padre y el hijo
        if (newSupraOrg != null) {
            if (newSupraOrg.getSubOrgs() == null) newSupraOrg.setSubOrgs( new HashSet<XLNetsOrgNode>() );
            newSupraOrg.getSubOrgs().add(this);
            this.setSupraOrg( newSupraOrg );
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  GET & SET
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Comprueba si el nodo actual es supra-organizacion (padre) de otra
     * organizacion cuyo oid se pasa
     * @param orgOID: El oid de la supuesta organizacion hija
     * @return: True si es padre y false si no es así
     */
    public boolean isSupraOrgOf(String orgOID) {
        boolean outIsSupraOrg = false;
        if (_subOrgs != null) {
	        XLNetsOrgNode currNode = null;
	        // Llamadas recursivas
	        for (XLNetsOrgNode org : _subOrgs) {
	            if (org.isSupraOrgOf(orgOID)) {
	            	outIsSupraOrg = true;
	            	break;
	            }
	        }
        }
        return outIsSupraOrg; // No!
    }
    /**
     * Comprueba si el nodo actual es una sub-organizacion (hija) de otra
     * organización cuyo oid se pasa
     * @param orgOID: El oid de la supuesta organizacion padre
     * @return: True si es hija y false si no lo es
     */
    public boolean isSubOrgOf(String orgOID) {
    	boolean outIsSubOrg = false;
        if (_supraOrg != null) {
	        XLNetsOrgNode currNode = null;
	        // Mirar el padre
	        currNode = _supraOrg;
	        while(currNode != null) {
	            if (currNode.getOid().equals(orgOID)) {
	            	outIsSubOrg = true;
	            	break;
	            }
	            currNode = currNode.getSupraOrg();
	        }
        }
        return outIsSubOrg; // No!
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDEZ
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Comprueba si el objeto es valido
     * @return: true si el objeto es valido y false si no es asín
     */
    public boolean isValid() {
        if (Strings.isNullOrEmpty(_oid)) return false;
        if (_name == null || _name.getFor(Language.SPANISH) == null || _name.getFor(Language.BASQUE) == null) return false;
        if (_subOrgs != null) {
            for (XLNetsOrgNode org : _subOrgs) {
                if ( !org.isValid() ) return false;
            }
        }
        return true;
    }
}
