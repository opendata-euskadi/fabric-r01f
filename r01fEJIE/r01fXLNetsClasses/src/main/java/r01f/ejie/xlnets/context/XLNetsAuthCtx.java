/*
 * Created on 09-jul-2004
 *
 * @author IE00165H (Alex Lara Garatxana)
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.context;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrgDivisionID;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrgDivisionServiceID;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrganizationID;
import r01f.ejie.xlnets.model.XLNetsSession;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.WorkPlaceCode;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.types.contact.EMail;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


/**
 * Contiene todo el contexto de seguridad que construye el filtro de autorizaci�n
 * y que se pasa al recurso protegido.
 * En este objeto se almacenan los siguientes datos:
 * <pre>
 * 		- Atributos del contexto de autorizacion
 * 		- Perfiles del usuario
 * 		- Informaci�n de autorizaci�n al recurso
 * </pre>
 */
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
@Slf4j
public class XLNetsAuthCtx
  implements Serializable {

	private static final long serialVersionUID = 5697699783433808308L;

	private static final String UNKNOWN = "unknown";
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private String _sessionUID;
    @Getter @Setter private UserCode _userCode;
    @Getter @Setter private WorkPlaceCode _workPlaceCode;
    @Getter @Setter private String _userName;
    @Getter @Setter private String _userSurname;
    @Getter @Setter private String _userDisplayName;
    @Getter @Setter private EMail  _userMail;
    @Getter @Setter private String _userDni;
    @Getter @Setter private String _userTelephone;
    @Getter @Setter private GeoCountyID _workplaceCountyId;
    @Getter @Setter private String _workplaceBuildingCode;



    @Getter @Setter private XLNetsOrganization _org;
    @Getter @Setter private XLNetsOrgDivision _orgDivision;
    @Getter @Setter private XLNetsOrgDivisionService _orgDivisionService;
    @Getter @Setter private Collection<String> _userProfiles;
    @Getter @Setter private Map<String,XLNetsTargetCtx> _authorizedTargets = null;	// Targets a los que se ha comprobado el acceso
    																				// indexados por el patron de la URI
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public XLNetsAuthCtx(final XLNetsSession xlnetsSessionToken) {
        this.setSessionUID(xlnetsSessionToken.getSessionUID());

        if (xlnetsSessionToken.getUser() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getUser().getPersona())) {
        	this.setUserCode(UserCode.forId(xlnetsSessionToken.getUser().getPersona()));
        }
        if (xlnetsSessionToken.getUser() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getUser().getPuesto())) {
        	this.setWorkPlaceCode(WorkPlaceCode.forId(xlnetsSessionToken.getUser().getPuesto()));
        }
        if (xlnetsSessionToken.getUser() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getUser().getName())) {
        	this.setUserName(xlnetsSessionToken.getUser().getName());
        }
        if (xlnetsSessionToken.getUser() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getUser().getSurname())) {
        	this.setUserSurname(xlnetsSessionToken.getUser().getSurname());
        }
        if (xlnetsSessionToken.getUser() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getUser().getDisplayName())) {
        	this.setUserDisplayName(xlnetsSessionToken.getUser().getDisplayName());
        }
        if (xlnetsSessionToken.getUser() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getUser().getMail())) {
        	this.setUserMail(EMail.create(xlnetsSessionToken.getUser().getMail()));
        }
        if (xlnetsSessionToken.getUser() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getUser().getTelephone())) {
        	this.setUserTelephone(xlnetsSessionToken.getUser().getTelephone());
        }
        if (xlnetsSessionToken.getUser() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getUser().getDni())) {
        	this.setUserDni(xlnetsSessionToken.getUser().getDni());
        }
        if (xlnetsSessionToken.getWorkplace() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getWorkplace().getCodigoTerritorial())
         && !xlnetsSessionToken.getWorkplace().getCodigoTerritorial().equals(UNKNOWN)) {
        	GeoCountyID countyId = null;
        	if (xlnetsSessionToken.getWorkplace().getCodigoTerritorial().equals("0")
        	 || xlnetsSessionToken.getWorkplace().getCodigoTerritorial().equals("1")) {
        		countyId = GeoCountyID.forId(1);		// araba
        	} else if (xlnetsSessionToken.getWorkplace().getCodigoTerritorial().equals("2")) {
        		countyId = GeoCountyID.forId(20);		// gipuzkoa
        	} else if (xlnetsSessionToken.getWorkplace().getCodigoTerritorial().equals("3")) {
        		countyId = GeoCountyID.forId(48);		// bizkaia
        	} else {
        		throw new IllegalArgumentException(xlnetsSessionToken.getWorkplace().getCodigoTerritorial() + " is NOT a valid county code!!");
        	}
        	this.setWorkplaceCountyId(countyId);
        }
        if (xlnetsSessionToken.getWorkplace() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getWorkplace().getCodigoEdificio())) {
        	this.setWorkplaceBuildingCode(xlnetsSessionToken.getWorkplace().getCodigoEdificio());
        }

        if (xlnetsSessionToken.getOrganizacion() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getOrganizacion().getOid())) {
        	this.setOrg(new XLNetsOrganization(XLNetsOrganizationID.forId(xlnetsSessionToken.getOrganizacion().getOid()),
							        		   new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_DEFAULT_VALUE,UNKNOWN)
							        			     .add(Language.SPANISH,xlnetsSessionToken.getOrganizacion().getName().get(Language.SPANISH))
							        				 .add(Language.BASQUE,xlnetsSessionToken.getOrganizacion().getName().get(Language.BASQUE))));
        }
        if (xlnetsSessionToken.getGrupo() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getGrupo().getOid())) {
        	this.setOrgDivision(new XLNetsOrgDivision ( XLNetsOrgDivisionID.forId(xlnetsSessionToken.getGrupo().getOid()),
									        			new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_DEFAULT_VALUE,UNKNOWN)
										    			     .add(Language.SPANISH,xlnetsSessionToken.getGrupo().getName().get(Language.SPANISH))
										    				 .add(Language.BASQUE,xlnetsSessionToken.getGrupo().getName().get(Language.BASQUE))));
        }
        if (xlnetsSessionToken.getUnidad() != null
         && Strings.isNOTNullOrEmpty(xlnetsSessionToken.getUnidad().getOid())) {
        	this.setOrgDivisionService(new XLNetsOrgDivisionService(XLNetsOrgDivisionServiceID.forId(xlnetsSessionToken.getUnidad().getOid()),
												        			new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_DEFAULT_VALUE,UNKNOWN)
													    			     .add(Language.SPANISH,xlnetsSessionToken.getUnidad().getName().get(Language.SPANISH))
													    				 .add(Language.BASQUE,xlnetsSessionToken.getUnidad().getName().get(Language.BASQUE))));
		}
        _userProfiles = xlnetsSessionToken.getPerfiles();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  GET & SET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la configuracion de seguridad del destino en base a la URI
	 * que se est� solicitando, para lo cual intenta "casar" esta URI con las
	 * configuraciones de seguridad del usuario y que se han obtenido del fichero
	 * de propiedades y cargado en el filtro de seguridad
	 * @param uriPattern El part�n de la uri para el destino
	 * @return La configuraci�n de seguridad si la uri verifica alguno de los
	 * 		   patrones configurados en el fichero properties.
	 * 		   Null si la uri no "casa" con ninguno de los patrones del
	 *         fichero properties
	 */
	public XLNetsTargetCtx getTargetAuth(final String uriPattern) {
		log.warn(".....getTargetAuth for {} ", uriPattern);
		if (CollectionUtils.hasData(_authorizedTargets)) {
			log.debug(">>>>>>>>>>>>>>>>>  Current _authorizedTargets are :  ");
			for (Iterator<String> it = _authorizedTargets.keySet().iterator(); it.hasNext(); ) {
				String storedUriPattern = it.next();
				log.warn("Authorized {}", storedUriPattern);
			}
			return _authorizedTargets.get(uriPattern);
		} else {
		    log.warn(">>>>>>>>>>>>>>>>>  There si NO Current authorizedTargets ");
			return null;
		}
	}
	/**
	 * Devuelve true o false en funcion de si el usuario tiene o no el perfil solicitado
	 * @param profile El perfil solicitado
	 * @return True si el usuario tiene el perfil o false si no es asi
	 */
	public boolean hasProfile(final String profileOID) {
        return CollectionUtils.hasData(_userProfiles) ? _userProfiles.contains(profileOID)
        										  	  : false;
    }
	/**
	 * Devuelve los perfiles de un c�digo de aplicacion
	 * @param appCode
	 * @return
	 */
	public Collection<String> profilesOf(final AppCode appCode) {
		Collection<String> outProfiles = Lists.newArrayList();
		if (CollectionUtils.hasData(_userProfiles)) {
			outProfiles = FluentIterable.from(_userProfiles)
										.filter(new Predicate<String>() {
													@Override
													public boolean apply(final String profile) {
														return profile.startsWith(appCode.asString().toUpperCase());
													}
												})
										.toList();
		}
		return outProfiles;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS DE COMPROBACI�N DE LA VALIDEZ
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Comprueba si el objeto es valido
     * @return true si el objeto es valido y false si no es as�n
     */
    public boolean isValid() {
        // Cuidado!!! Si override = true es posible que no haya profiles ni recursos
        if (CollectionUtils.isNullOrEmpty(_authorizedTargets)) return false;
        for (XLNetsTargetCtx target : _authorizedTargets.values()) {
            if ( !target.isValid() ) return false;
        }
        return true;
    }
   /////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Strings.customized("[XLNetsAuth]-Configuracion de contexto seguridad para {} {} sesssion id {}",
        				 			 _userName,_userCode,_sessionUID));
        if (CollectionUtils.hasData(_authorizedTargets)) {
            for (XLNetsTargetCtx tgtCfg : _authorizedTargets.values()) {
                sb.append("\n").append( tgtCfg.getTargetCfg().getUrlPathPattern().toString() );
            }
        }
        return sb.toString();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//INNER CLASSES
/////////////////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")  @AllArgsConstructor
    public static class XLNetsOrganization
    		 implements Serializable {
		private static final long serialVersionUID = 3467056861336966665L;

		@Getter @Setter private XLNetsOrganizationID _orgOid;
        @Getter @Setter private LanguageTexts _description;
    }

    @Accessors(prefix="_")  @AllArgsConstructor
    public static class XLNetsOrgDivision
    		 implements Serializable {
		private static final long serialVersionUID = -6986853911718525860L;

		@Getter @Setter private XLNetsOrgDivisionID _divisionOid;
        @Getter @Setter private LanguageTexts _description;
    }

    @Accessors(prefix="_")  @AllArgsConstructor
    public static class XLNetsOrgDivisionService
    	     implements Serializable {
		private static final long serialVersionUID = 1921545049810035066L;

		@Getter @Setter private XLNetsOrgDivisionServiceID  _divisionServiceOid;
        @Getter @Setter private LanguageTexts _description;
    }
}
