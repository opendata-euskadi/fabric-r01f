package r01f.model.facets.delegates;

import java.util.Date;

import r01f.facets.HasOID;
import r01f.facets.delegates.FacetDelegateBase;
import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.VersionIndependentOID;
import r01f.guids.VersionOID;
import r01f.model.VersionInfo;
import r01f.model.facets.Versionable;
import r01f.model.facets.Versionable.HasVersionableFacet;

/**
 * Encapsulates the {@link Versionable} behavior
 * @param <V>
 */
public class VersionableDelegate<V extends HasVersionableFacet & HasOID<? extends OIDForVersionableModelObject>>
	 extends FacetDelegateBase<V>
  implements Versionable {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VersionableDelegate(final V hasVersionable) {
		super(hasVersionable);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public VersionIndependentOID getVersionIndependentOid() {
		return _modelObject.getOid().getVersionIndependentOid();
	}
	@Override
	public VersionOID getVersionOid() {
		return _modelObject.getOid().getVersion();
	}
	@Override
	public Date getStartOfUseDate() {
		return _modelObject.getVersionInfo() != null ? _modelObject.getVersionInfo().getStartOfUseDate()
													 : null;
	}
	@Override
	public void setStartOfUseDate(final Date date) {
		_ensureVersionInfo();
		_modelObject.getVersionInfo().setStartOfUseDate(date);
	}
	@Override
	public Date getEndOfUseDate() {
		return _modelObject.getVersionInfo() != null ? _modelObject.getVersionInfo().getEndOfUseDate()
													 : null;
	}
	@Override
	public void setEndOfUseDate(final Date date) {
		_ensureVersionInfo();
		_modelObject.getVersionInfo().setEndOfUseDate(date);
	}
	@Override
	public boolean isActive() {
		VersionInfo versionInfo = _modelObject.getVersionInfo();
		return versionInfo != null ? versionInfo.isActive()
								   : false;
	}
	@Override
	public boolean isNotActive() {
		VersionInfo versionInfo = _modelObject.getVersionInfo();
		return versionInfo != null ? versionInfo.isNotActive()
								   : true;
	}
	@Override
	public boolean isDraft() {
		VersionInfo versionInfo = _modelObject.getVersionInfo();
		return versionInfo != null ? versionInfo.isDraft()
								   : true;
	}
	@Override
	public VersionOID getNextVersion() {
		return _modelObject.getVersionInfo() != null ? _modelObject.getVersionInfo().getNextVersionOid()
													 : null;
	}
	@Override
	public void setNextVersion(final VersionOID nextVersion)  {
		_ensureVersionInfo();
		_modelObject.getVersionInfo().setNextVersionOid(nextVersion);
	}
	@Override
	public void activate(final Date activationDate) {
		_ensureVersionInfo();
		_modelObject.getVersionInfo().activate(activationDate);
	}
	@Override
	public void overrideBy(final VersionOID otherVersion,
    					   final Date otherVersionStartOfUseDate) {
		_ensureVersionInfo();
		_modelObject.getVersionInfo().overrideBy(otherVersion,
												 otherVersionStartOfUseDate);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private void _ensureVersionInfo() {
		if (_modelObject.getVersionInfo() == null) {
			VersionInfo versionInfo = new VersionInfo();
			_modelObject.setVersionInfo(versionInfo);
		}
	}
}
