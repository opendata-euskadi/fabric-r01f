/*
 * Created on 11-ago-2004
 * 
 * @author ie00165h
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.util.types.Strings;

/**
 * Datos del puesto de usuario
 */
@NoArgsConstructor @AllArgsConstructor
@Accessors(prefix="_")
public class XLNetsPuesto
     extends XLNetsObjectBase {

/////////////////////////////////////////////////////////////////////////////////////////
//  MIEMBROS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private String _uidPuesto;
    @Getter @Setter private String _codigoTerritorial;
    @Getter @Setter private String _codigoEdificio;
    
  
/////////////////////////////////////////////////////////////////////////////////////////
//  OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
    	StringBuilder dbg = new StringBuilder();
    	if (Strings.isNOTNullOrEmpty(this.getUidPuesto()))            dbg.append("            Uid Puesto: ").append(this.getUidPuesto()).append("\n");             
    	if (Strings.isNOTNullOrEmpty(this.getCodigoTerritorial()))    dbg.append("    Codigo Territorial: ").append(this.getCodigoTerritorial()).append("\n");      
        if (Strings.isNOTNullOrEmpty(this.getCodigoEdificio()))    	  dbg.append("    Codigo Edificio: ").append(this.getCodigoEdificio()).append("\n");      
        return dbg.toString();
    }
}
