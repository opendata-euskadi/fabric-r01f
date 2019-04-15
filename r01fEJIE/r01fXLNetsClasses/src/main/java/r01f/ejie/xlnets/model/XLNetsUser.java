/*
 * Created on 26-jul-2004
 *
 * @author IE00165H
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.types.contact.ContactInfoBuilder;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.contact.ContactPhone;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.PersonBuilder;
import r01f.types.contact.PersonWithContactInfo;
import r01f.types.contact.PersonWithContactInfoBuilder;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xml.XMLUtils;

/**
 * Usuario
 */
@Slf4j
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class XLNetsUser implements Serializable {
    private static final long serialVersionUID = 7643780567483567591L;
/////////////////////////////////////////////////////////////////////////////////////////
//  MIEMBROS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private String _oid = "user-unknown";
    @Getter @Setter private boolean _loginApp;
    @Getter @Setter private String _login;
    @Getter @Setter private String _persona;
    @Getter @Setter private String _puesto;
    @Getter @Setter private String _name = "user-name-unknown";
    @Getter @Setter private String _surname = "user-surname-unknown";
    @Getter @Setter private String _displayName = "user-display-name-unknown";
    @Getter @Setter private String _dni;
    @Getter @Setter private String _home;
    @Getter @Setter private String _mail;
    @Getter @Setter private String _telephone;
    @Getter @Setter private Language _language;
    @Getter @Setter private String _ip;
    @Getter @Setter private Map<String,String> _attributes = null;		// Atributos del usuario (dni, login, puesto)
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Builds a list of items {@link PersonWithContactInfo} from n38ItemObtenerPersonas objects
     * @param nodeList	xml node list
     * @return	collection
     */
    public static Collection<PersonWithContactInfo> contactListFrom(final NodeList nodeList) {
    	return FluentIterable.from(XLNetsUser.createFrom(nodeList))
    						 .transform(new Function<XLNetsUser,PersonWithContactInfo>() {
	    							 		@Override
											public PersonWithContactInfo apply(final XLNetsUser user) {
	    							 			return toPersonWithContactInfo(user);
											}
    						 			})
							 .toList();
    }

    /**
     * Builds a list of items {@link XLNetsUser} from n38ItemObtenerPersonas objects
     * @param Document response from N38 api
     * @see {@link N38API#n38ItemObtenerPersonas(String)}
     * @return	collection
     */
    public static Collection<XLNetsUser> from(final Document doc) {

    	try {
			return FluentIterable.from(XLNetsUser.createFrom(XMLUtils.nodeListByXPath(doc,
														   "/n38/elementos/elemento")))
								.toList();
		} catch (XPathExpressionException e) {
			e.printStackTrace(System.out);
            log.error("Error getting nodelist by xpath to process XLNets response: {}",e.getMessage(),e);
			return Lists.newArrayList();
		}
    }
    public static Collection<XLNetsUser> createFrom(final NodeList nodeList) {
		Collection<XLNetsUser> outUsers = FluentIterable.from(XMLUtils.nodeListIterableFrom(nodeList))
												.transform(new Function<Node,XLNetsUser>() {
																	@Override
																	public XLNetsUser apply(final Node node) {
																		return XLNetsUser.from(node);
																	}
														   })
												.toList();
		return outUsers;
    }

    /**
     * Creates a {@link XLNetsUser} from the xml node data retrieved
     * @param node	node with n38ItemObtenerPersonas format
     * @see {@link N38API#n38ItemObtenerPersonas(String)}
     * @return XLNetsUser with data or null
     */
    public static XLNetsUser from(final Node node) {
		XLNetsUser user = new XLNetsUser();
		user.setDni(_itemValueOrNull(node, "dni"));
		user.setName(_itemValueOrNull(node,"givenname"));
		user.setSurname(_itemValueOrNull(node,"sn"));
		user.setDisplayName(_itemValueOrNull(node,"displayname"));
		user.setTelephone(_itemValueOrNull(node,"telephonenumber"));
		user.setMail(_itemValueOrNull(node,"mail"));
		user.setPersona(_itemValueOrNull(node,"uid"));
		user.setPuesto(_itemValueOrNull(node,"n38puestouid"));
		user.setLogin(_itemValueOrNull(node, "n38login"));
		String idioma = _itemValueOrNull(node, "n38idioma");
		if (idioma != null) {
			user.setLanguage(idioma.equals("3") ? Language.SPANISH : Language.BASQUE);
		}
		return user;
    }

    /**
	 * Returns the text content of an item
	 * @param node	the node
	 * @return	text content or null
	 */
	private static String _itemValueOrNull(Node elementNode, final String propertyId) {
		final String XPATH_TEMPLATE= "parametro[@id='{}']/valor[1]";
		Node outNode=null;
		try {
			outNode = XMLUtils.nodeByXPath(elementNode,
								 Strings.customized(XPATH_TEMPLATE, propertyId));
		} catch (XPathExpressionException e) {
			e.printStackTrace(System.out);
            log.error("Error getting nodelist by xpath {} to process XLNets response: {}",
            		Strings.customized(XPATH_TEMPLATE, propertyId),
            		e.getMessage(),
            		e);
		}
		if (outNode == null) return null;
		return outNode.getTextContent();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET & SET
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Devuelve un atributo del usuario (dni, login, puesto, etc)
     * @param attrName: El nombre del atributo
     * @return: El atributo (String)
     */
    public String getAttribute(final String attrName) {
        if (attrName == null) return null;
        return CollectionUtils.hasData(_attributes) ? _attributes.get(attrName)
        											: null;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDEZ
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Comprueba si el objeto es valido
     * @return: true si el objeto es valido y false si no es as�n
     */
    public boolean isValid() {
        if (Strings.isNullOrEmpty(_login)) return false;
        if (Strings.isNullOrEmpty(_oid)) return false;
        return true;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		StringBuilder dbg = new StringBuilder();
		if (Strings.isNOTNullOrEmpty(_dni)) 	dbg.append("DNI: ").append(_dni).append("\n");
		if (Strings.isNOTNullOrEmpty(_name)) 	        dbg.append("    name: ").append(_name).append("\n");
		if (Strings.isNOTNullOrEmpty(_surname)) 	    dbg.append(" surname: ").append(_surname).append("\n");
		if (Strings.isNOTNullOrEmpty(_displayName))	dbg.append(" displayName: ").append(_displayName).append("\n");
		if (Strings.isNOTNullOrEmpty(_mail)) 	    dbg.append("        mail: ").append(_mail).append("\n");
		if (Strings.isNOTNullOrEmpty(_telephone)) 	dbg.append("   telephone: ").append(_telephone).append("\n");
		if (Strings.isNOTNullOrEmpty(_login)) 	    dbg.append("       Login: ").append(_login).append("\n");
		if (Strings.isNOTNullOrEmpty(_persona)) 	dbg.append("     Persona: ").append(_persona).append("\n");
		if (Strings.isNOTNullOrEmpty(_puesto)) 		dbg.append("      Puesto: ").append(_puesto).append("\n");
		if (_language != null) 						dbg.append("      Idioma: ").append(_language).append("\n");
		if (Strings.isNOTNullOrEmpty(_ip)) 			dbg.append("          IP: ").append(_ip).append("\n");
													dbg.append("   Login App: ").append(_loginApp).append("\n");
		if (Strings.isNOTNullOrEmpty(_home)) 		dbg.append("        Home: ").append(_home).append("\n");
	    return dbg.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static PersonWithContactInfo toPersonWithContactInfo(XLNetsUser xlNetsUser) {
		if (xlNetsUser == null) return null;
		return PersonWithContactInfoBuilder.create()
											   .forPerson(PersonBuilder.createPersonWithId(NIFPersonID.forId(xlNetsUser._dni))
																								.withName(xlNetsUser.getName())
																								.withSurname(xlNetsUser.getSurname())
																								.noSalutation()
																								.noPreferredLanguage()
																								.noDetails()
																								.build())
											   .withContactInfo(ContactInfoBuilder.createVisible()
																.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
																			               .withNumber(xlNetsUser.getTelephone()))
																.addMail(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK)
																			               .mailTo(xlNetsUser.getMail()))
																.noWeb()
																.noSocialNetwork()
											   .build())
										   .build();
	}
}
