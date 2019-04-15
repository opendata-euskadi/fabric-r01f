/*
 * Created on 17-ago-2004
 * 
 * @author ie00165h
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.context;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.util.types.Strings;

/**
 * Autorizacion sobre un recurso
 */
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class XLNetsResourceAuthorization 
  implements Serializable {
    private static final long serialVersionUID = 7634405404633067669L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////        
    @Getter @Setter private String _oid = "auth-unknown";
    @Getter @Setter private LanguageTexts _name;
    @Getter @Setter private String[] _whatFor;
    @Getter @Setter private String _profileOid;	

    
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDEZ
/////////////////////////////////////////////////////////////////////////////////////////       
    /**
     * Comprueba si el objeto es valido
     * @return true si el objeto es valido y false si no es asín
     */
    public boolean isValid() {
        if (Strings.isNullOrEmpty(_oid)) return false;
        if (_name == null || _name.getFor(Language.SPANISH) == null || _name.getFor(Language.BASQUE) == null) return false;
        if (_profileOid == null) return false;
        return true;
    }
}
