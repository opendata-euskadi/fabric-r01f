/*
 * Created on 09-jul-2004
 *
 * @author IE00165H
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.config;

import java.io.Serializable;
import java.util.Collection;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.config.XLNetsTargetCfg.ResourceCfg;
import r01f.ejie.xlnets.config.XLNetsTargetCfg.ResourceItemType;
import r01f.exceptions.Throwables;
import r01f.guids.CommonOIDs.AppCode;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Modela la informacion de autorizacion de una aplicacion
 * Se carga desde un fichero de propiedades con la siguiente estructura:
 * <pre class='brush:xml'>
 * 	<xlNets>
 *     <authCfg useSession='true/false' override='true/false'>
 *     		<target id='theId' kind='restrict|allow'>
 *     			<uri>[Expresion regular para machear la uri que se solicita]</uri>
 *     			<resources>
 *     				<resource type='[itemType]' mandatory='true/false' oid='[itemOID]'>
 *     					<es>[Nombre en castellano]</es>
 *     					<eu>[Nombre en euskera]</eu>
 *     				</resource>
 *     				<resource type='[itemType]' mandatory='true/false' oid='[itemOID]'>
 *     					<es>[Nombre en castellano]</es>
 *     					<eu>[Nombre en euskera]</eu>
 *     				</resource>
 *     				....
 *     			</resources>
 *     		</target>
 *     		....
 *     </authCfg>
 * 	</xlNets>
 * </pre>
 */
@Accessors(prefix="_")
@Slf4j
public class XLNetsAppCfg
  implements Serializable {
    private static final long serialVersionUID = -4853237050490550353L;

/////////////////////////////////////////////////////////////////////////////////////////
//  MIEMBROS
/////////////////////////////////////////////////////////////////////////////////////////
    		private final XMLPropertiesForAppComponent _props;		// properties

    @Getter private final boolean _override;		// Indica si hay que permitir por narices...
    @Getter private final boolean _useSession;		// Indica si la información de autorizacion se guarda en session
    @Getter private final Url _loginUrl;			// url de login xlnets
    @Getter private final Collection<XLNetsTargetCfg> _targets;		// Recursos que se protegen.
    																// Cada recurso se indexa por una expresion regular que ha de machear la
    																// uri que se solicita
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR A PARTIR DEL CÓDIGO DE APLICACION
/////////////////////////////////////////////////////////////////////////////////////////
    public XLNetsAppCfg(final XMLPropertiesForAppComponent props) {
    	_props  = props;
    	AppCode theAppCode = _props.getAppCode();
        log.warn("Loading XLNets servlet filter auth config from {}.xlnets.properties.xml file",
        		  theAppCode);

        Url loginUrl = props.propertyAt("xlnets/loginUrl")
        					.asUrl("https://xlnets.servicios.jakina.ejgvdns/n38a/N38LoginInicioServlet");
        boolean override = false;
        boolean useSession = true;
        Collection<XLNetsTargetCfg> targets = Lists.newLinkedList();

        // ___________ auth _____________
        Node appAuthDefNode = props.propertyAt("xlnets/authCfg")
        						   .node();

        if (appAuthDefNode == null) {
            throw new IllegalStateException(Throwables.message("[XLNetsAuth]-NO se puede cargar la información de seguridad de la aplicación {} ya que no existe la configuración en el fichero {}.xlnets.properties.xml",
            		 					    				   theAppCode,theAppCode));
        }

        NamedNodeMap attrs = appAuthDefNode.getAttributes();
        Node currAttrNode = null;
        String currAttrValue = null;

        // Atributo override que indica si se ignora la configuración de XLNets
        if (attrs != null && (currAttrNode = attrs.getNamedItem("override")) != null) {
            currAttrValue = currAttrNode.getNodeValue().trim();
             override = Strings.isNOTNullOrEmpty(currAttrValue) && "true".equals(currAttrValue) ? true : false;
        }
        // Atributo useSession que indica si se utiliza la session http para guardar la informacion
        // de autorizacion una vez obtenida
        if (attrs != null && (currAttrNode = attrs.getNamedItem("useSession")) != null) {
            currAttrValue = currAttrNode.getNodeValue().trim();
            useSession = Strings.isNOTNullOrEmpty(currAttrValue) && "true".equals(currAttrValue) ? true : false;
        }

        // Provider de autorizacion y Targets a los que se aplica la autorización
        NodeList rl = appAuthDefNode.getChildNodes();
        if (rl != null) {
            Node node = null;
            for (int i=0; i<rl.getLength(); i++) {
                node = rl.item(i);
                // ________________ TARGET _________________
                if (node.getNodeName().equals("target")) {
                    XLNetsTargetCfg currTargetCfg = new XLNetsTargetCfg();  // Configuración de seguridad del target actual

                    attrs = node.getAttributes();
                    // Atributo id
                    //if (attrs != null && (currAttrNode = attrs.getNamedItem("id")) != null)
                    //    currTargetCfg.id = currAttrNode.getNodeValue().trim();

                    // Atributo kind
                    if (attrs != null && (currAttrNode = attrs.getNamedItem("kind")) != null) {
                        String kindStr = currAttrNode.getNodeValue();
                        currTargetCfg.setKind(XLNetsTargetCfg.ResourceAccess.fromCode(kindStr));
                        if (currTargetCfg.getKind() == null) log.warn("[XLNetsAuth]-El valor '{}' del atributo kind de la configuración del target de acceso NO es valido.\r\nLos únicos valores válidos son {}",
                            		 								  kindStr,XLNetsTargetCfg.ResourceAccess.values());
                    }

                    NodeList nl = node.getChildNodes();
                    if (nl != null) {
                        Node currNode = null;
                        for (int j=0; j<nl.getLength(); j++) {
                            currNode = nl.item(j);

                            if (currNode.getNodeName().equals("uri")) {
                                currTargetCfg.setUrlPathPattern(currNode.getFirstChild().getNodeValue().trim());
                            } else if (currNode.getNodeName().equals("resources")) {
                                // Los "hijos" de <resources> son los nombres de los recursos que hay que comprobar
                                NodeList il = currNode.getChildNodes();
                                if (il != null) {
                                    Node currResNode = null;
                                    NamedNodeMap currResAttrs = null;
                                    NodeList bl = null;
                                    String itemOID = null;
                                    boolean itemMandatory = false;
                                    ResourceItemType itemType = null;
                                    LanguageTexts itemName = null;

                                    for (int k=0; k<il.getLength(); k++) {
                                        currResNode = il.item(k);
                                        currResAttrs = currResNode.getAttributes();
                                        bl = currResNode.getChildNodes();
                                        if (currResNode.getNodeName().equals("resource")
                                         && bl != null && bl.getLength() > 0 && currResAttrs != null
                                         && currResAttrs.getNamedItem("oid") != null
                                         && currResAttrs.getNamedItem("type") != null) {
                                            // El oid, tipo y si el item es obligatorio o no
                                            itemOID = currResAttrs.getNamedItem("oid").getNodeValue();
                                            itemType = ResourceItemType.fromCode(currResAttrs.getNamedItem("type").getNodeValue());
                                            itemMandatory = (currResAttrs.getNamedItem("mandatory") == null ? false
                                            																: Boolean.valueOf(currResAttrs.getNamedItem("mandatory").getNodeValue()));
                                            itemName = new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL);
                                            // El nombre del item en diferentes idiomas
                                            for (int c=0; c<bl.getLength(); c++) {
                                            	Language lang = Languages.of(bl.item(c).getNodeName().toLowerCase());
                                            	if (lang != null &&  bl.item(c).getFirstChild() != null) {
                                                    itemName.add(lang,
                                                    			 bl.item(c).getFirstChild().getNodeValue().trim());
                                            	}
                                            }
                                            // Añadir la configuración del recurso a la configuracion del target
                                            if (itemType != null && itemName != null) {
                                                if (currTargetCfg.getResources() == null) currTargetCfg.setResources(Lists.<ResourceCfg>newLinkedList());
                                                XLNetsTargetCfg.ResourceCfg resCfg = currTargetCfg.new ResourceCfg(itemOID.trim(),itemType,
                                                																   itemMandatory,
                                                																   itemName);
                                                currTargetCfg.getResources().add(resCfg);
                                            }
                                        } else {
                                            //log.warn("Un item de seguridad no esta correctamente configurado!!!");
                                        }
                                    }
                                }
                            }
                        }
                    } // Fin del propiedades del target

                    // Meter en nuevo target en el mapa de configuración de targets de la aplicación,
                    // identificado por el patrón de la url
                    targets.add(currTargetCfg);

                } // Fin del recurso
            }
        } // Fin de la lista de recursos

        _loginUrl = loginUrl;
        _override = override;
        _useSession = useSession;
        _targets = targets;

        log.warn("XLNets servlet filter auth config loaded: {}",this.toString());
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Strings.customized("[XLNetsAuth]-Configuracion de seguridad de la aplicacion {}; useSession={}; override={}",
        				 			 _props.getAppCode(),_useSession,_override));
        sb.append("\nLoginUrl: ").append(_loginUrl.asString());
        if (CollectionUtils.hasData(_targets)) {
            for (XLNetsTargetCfg tgtCfg : _targets) {
                sb.append("\n").append( tgtCfg.toString() );
            }
        }
        return sb.toString();
    }
}
