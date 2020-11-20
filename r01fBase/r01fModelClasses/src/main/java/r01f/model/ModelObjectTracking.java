package r01f.model;

import java.io.Serializable;
import java.util.Date;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityContext;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityOIDs.UserOID;

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
	@Getter @Setter private Date _createDate =  new Date();// Not compatible in GWT :Calendar.getInstance().getTime();
	/**
	 * Creator user oid
	 */
	@MarshallField(as="creatorUserOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private UserOID _creatorUserOid;
	/**
	 * Creator user code
	 */
	@MarshallField(as="creatorUserCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private LoginID _creatorUserCode;
	/**
	 * Object's create date
	 */
	@MarshallField(as="lastUpdate",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _lastUpdateDate =  new Date(); // Not compatible in GWT :Calendar.getInstance().getTime();
	/**
	 * Last update user oid
	 */
	@MarshallField(as="lastUpdaterUserOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private UserOID _lastUpdatorUserOid;
	/**
	 * Last update user code
	 */
	@MarshallField(as="lastUpdaterUserCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private LoginID _lastUpdatorUserCode;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ModelObjectTracking() {
		// default no-args constructor
	}
	public ModelObjectTracking(final ModelObjectTracking other) {
		_createDate = other.getCreateDate();
		_creatorUserOid = other.getCreatorUserOid();
		_creatorUserCode = other.getCreatorUserCode();
		_lastUpdateDate = other.getLastUpdateDate();
		_lastUpdatorUserOid = other.getLastUpdatorUserOid();
		_lastUpdatorUserCode = other.getLastUpdatorUserCode();
	}
	public ModelObjectTracking(final UserOID creatorOid,final LoginID creator,final Date createDate) {
		this(creatorOid,creator,createDate,
			 creatorOid,creator,createDate);
	}
	public ModelObjectTracking(final UserOID creatorOid,final LoginID creator,final Date createDate,
							   final UserOID updatorOid,final LoginID lastUpdator,final Date lastUpdateDate) {
		_createDate = createDate;
		_lastUpdateDate = lastUpdateDate;
		_creatorUserOid = creatorOid;
		_creatorUserCode = creator;
		_lastUpdatorUserOid = updatorOid;
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
	public ModelObjectTracking setModifiedBy(final UserOID userOid,final LoginID userCode) {
		this.setLastUpdatorUserOid(userOid);
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
			_lastUpdateDate = _createDate;
			if (_creatorUserOid == null) {
				_creatorUserOid = other.getCreatorUserOid() != null ? other.getCreatorUserOid()
																    : securityContext.isForUser() ? securityContext.asForUser()
																    											   .getUserOid()
																    							  : null;
			}
			if (_creatorUserCode == null) {
				_creatorUserCode = other.getCreatorUserCode() != null ? other.getCreatorUserCode()
																	  : securityContext.getLoginId();
			}
			_lastUpdatorUserCode = null;
		}
		else if (op == PersistencePerformedOperation.UPDATED) {
			_lastUpdateDate = new Date();
			_lastUpdatorUserOid = securityContext.isForUser() ? securityContext.asForUser()
																			   .getUserOid()
															  : null;
			_lastUpdatorUserCode = securityContext.getLoginId();
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
			&& Objects.equal(this.getCreatorUserOid(),other.getCreatorUserOid())
			&& Objects.equal(this.getCreatorUserCode(),other.getCreatorUserCode())
			&& Objects.equal(this.getLastUpdateDate(),other.getLastUpdateDate())
			&& Objects.equal(this.getLastUpdatorUserCode(),other.getLastUpdatorUserCode())
			&& Objects.equal(this.getLastUpdatorUserOid(),other.getLastUpdatorUserOid());

	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_createDate,_creatorUserCode,_creatorUserOid,
								_lastUpdateDate,_lastUpdatorUserCode,_lastUpdatorUserOid);
	}
}
