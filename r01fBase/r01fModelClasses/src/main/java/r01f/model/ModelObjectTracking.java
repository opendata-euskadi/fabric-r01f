package r01f.model;

import java.io.Serializable;
import java.util.Date;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityContext;

/**
 * Object's Tracking info (author/a, create date, update date, etc)
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="tracking")
@Accessors(prefix="_")
public class ModelObjectTracking 
  implements Serializable {
	
	private static final long serialVersionUID = 2286660970580116262L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Object create date
     */
	@MarshallField(as="createDate",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private Date _createDate =  new Date();//Not compatible in GWT :Calendar.getInstance().getTime();
    /**
     * Creator user code
     */
	@MarshallField(as="creatorUserCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private UserCode _creatorUserCode;
    /**
     * Object's create date
     */
	@MarshallField(as="lastUpdate",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private Date _lastUpdateDate =  new Date(); //Not compatible in GWT :Calendar.getInstance().getTime();
    /**
     * Last update user code
     */
	@MarshallField(as="lastUpdaterUserCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private UserCode _lastUpdatorUserCode;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ModelObjectTracking() {
		// default no-args constructor
	}
	public ModelObjectTracking(final UserCode creator,final Date createDate) {
		this(creator,createDate,
			 creator,createDate);
	}
	public ModelObjectTracking(final UserCode creator,final Date createDate,
							   final UserCode lastUpdator,final Date lastUpdateDate) {
		_createDate = createDate;
		_lastUpdateDate = lastUpdateDate;
		_creatorUserCode = creator;
		_lastUpdatorUserCode = lastUpdator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets that the provided user code has made an update just now
	 * @param userCode
	 * @return 
	 */
	public ModelObjectTracking setModifiedBy(final UserCode userCode) {
		this.setLastUpdatorUserCode(userCode);
		this.setLastUpdateDate(new Date());
		return this;
	}
	public ModelObjectTrackingMergePersistencePerformedOperationStep mergeWith(final ModelObjectTracking other) {
		return new ModelObjectTrackingMergePersistencePerformedOperationStep(other);
	}
	@RequiredArgsConstructor
	public class ModelObjectTrackingMergePersistencePerformedOperationStep {
		private final ModelObjectTracking _other;
		public void whenCreatedBy(final SecurityContext securityContext) {
			_merge(_other,
				   PersistencePerformedOperation.CREATED,
				   securityContext);
		}
		public void whenUpdatedBy(final SecurityContext securityContext) {
			_merge(_other,
				   PersistencePerformedOperation.UPDATED,
				   securityContext);
		}
		public ModelObjectTrackingMergesecurityContextStep when(final PersistencePerformedOperation op) {
			return new ModelObjectTrackingMergesecurityContextStep(_other,op);
		}
	}
	@RequiredArgsConstructor
	public class ModelObjectTrackingMergesecurityContextStep {
		private final ModelObjectTracking _other;
		private final PersistencePerformedOperation _op;
		
		public void by(final SecurityContext securityContext) {
			_merge(_other,
				   _op,
				   securityContext);
		}
	}
	private void _merge(final ModelObjectTracking other,						
						final PersistencePerformedOperation op,
						final SecurityContext securityContext) {
		if (other == null) return;
		if (op == PersistencePerformedOperation.CREATED) {
			_createDate = new Date();
			_lastUpdateDate = null;
			if (_creatorUserCode == null) {
				if (other.getCreatorUserCode() != null) {
					_creatorUserCode = other.getCreatorUserCode();
				} else if (securityContext.getUserCode() != null) {
					_creatorUserCode = securityContext.getUserCode();
				} else if (securityContext.getAppCode() != null) {
					_creatorUserCode = UserCode.forId(securityContext.getAppCode().asString());
				}
			}
			_lastUpdatorUserCode = null;
		}
		else if (op == PersistencePerformedOperation.UPDATED) {
			_lastUpdateDate = new Date();
			if (securityContext.getUserCode() != null) {
				_lastUpdatorUserCode = securityContext.getUserCode();
			} else if (other.getLastUpdatorUserCode() != null) {
				_lastUpdatorUserCode = other.getLastUpdatorUserCode();
			} else if (securityContext.getAppCode() != null) {
				_lastUpdatorUserCode = UserCode.forId(securityContext.getAppCode().asString());
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ModelObjectTracking)) return false;
		
		ModelObjectTracking other = (ModelObjectTracking)obj;
		return Objects.equal(this.getCreateDate(),other.getCreateDate())
			&& Objects.equal(this.getCreatorUserCode(),other.getCreatorUserCode())
			&& Objects.equal(this.getLastUpdateDate(),other.getLastUpdateDate())
			&& Objects.equal(this.getLastUpdatorUserCode(),other.getLastUpdatorUserCode());
		
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_createDate,_creatorUserCode,
								_lastUpdateDate,_lastUpdatorUserCode);
	}
}
