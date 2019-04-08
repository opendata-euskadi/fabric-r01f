package r01f.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.debug.Debuggable;
import r01f.guids.VersionOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;



/**
 * Version info
 */
@MarshallType(as="versionInfo")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
@NoArgsConstructor
public class VersionInfo 
  implements Serializable,
  			 Debuggable {

	private static final long serialVersionUID = 5774104114793348710L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Version start of use date (the date when this version is set to be active)
	 */
	@MarshallField(as="startOfUseDate",
				   dateFormat=@MarshallDateFormat(use=DateFormat.TIMESTAMP),
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _startOfUseDate;
	/**
	 * Version end of use date (the date when another version was created and this one becomes obsolete)
	 */
	@MarshallField(as="endOfUseDate",
				   dateFormat=@MarshallDateFormat(use=DateFormat.TIMESTAMP),
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _endOfUseDate;
	/**
	 * Next version identifier (if this version is not the current version)
	 */
	@MarshallField(as="nextVersionOid")		// BEWARE cannot be an xml attribute since it's defined as an abstract/interface type
	@Getter @Setter private VersionOID _nextVersionOid;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static VersionInfo create() {
		return new VersionInfo();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isActive() {
		return this.getStartOfUseDate() != null 
			&& this.getEndOfUseDate() == null;
	}
	public boolean isNotActive() {
		return this.getStartOfUseDate() != null 
			&& this.getEndOfUseDate() != null;
	}
	public boolean isDraft() {
		return this.getStartOfUseDate() == null 
			&& this.getEndOfUseDate() == null;
	}
	public void activate(final Date activationDate) {
		_startOfUseDate = activationDate;
		_nextVersionOid = null;
	}
	public void overrideBy(final VersionOID otherVersion,
    					   final Date otherVersionStartOfUseDate) {
    	_nextVersionOid = otherVersion;
    	_endOfUseDate = otherVersionStartOfUseDate;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
    public VersionInfo startedToBeUsedAt(final Date startOfUseDate) {
    	this.activate(startOfUseDate);
    	return this;
    }
    public VersionInfo overridenBy(final VersionOID otherVersion) {
    	_nextVersionOid = otherVersion;
    	_endOfUseDate = new Date();
    	return this;
    }
    public VersionInfo overridenBy(final VersionOID otherVersion,
    										  final Date endOfUseDate) {
    	this.overrideBy(otherVersion,
    					endOfUseDate);
    	return this;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public StringBuffer debugInfo() {
        StringBuffer sb = new StringBuffer(72);
        sb.append("\r\n\tStartOfUseDate : ");sb.append(_startOfUseDate);
        sb.append("\r\n\t  EndOfUseDate : ");sb.append(_endOfUseDate);
        sb.append("\r\n\tNextVersionOid : ");sb.append(_nextVersionOid);
        sb.append("\r\n");
        return sb;
    }
    
}
