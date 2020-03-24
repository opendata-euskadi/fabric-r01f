package r01f.guids;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class OIDForVersionableModelObjectBase
  		   implements OIDForVersionableModelObject {

	private static final long serialVersionUID = -6745951154429443045L;
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <O extends OID> boolean is(final O other) {
		return this.equals(other);
	}
	@Override
	public <O extends OID> boolean isNOT(final O other) {
		return !this.is(other);
	}
	@Override
	public String toString() {
		String outStr = null;
		if (this.getOid() != null && this.getVersion() != null) {
			outStr = Strings.customized("{}/{}",this.getOid(),this.getVersion());
		} else {
			throw new IllegalStateException(Strings.customized("A {} without oid (only oid or version is NOT allowed",this.getClass()));
		} 
		return outStr;
	}
	@Override @GwtIncompatible("gwt does NOT supports reflection")
	protected Object clone() throws CloneNotSupportedException {
		if (this.getOid() == null || this.getVersion() == null) throw new IllegalStateException(Strings.customized("The oid of type {} has NO state",this.getClass()));
		VersionIndependentOID clonedVersionIndependentOid = ObjectUtils.clone(this.getOid());
		VersionOID clonedVersionOid = ObjectUtils.clone(this.getVersion());
		Object outOid = _createVersionableOIDFromString((Class<? extends VersionIndependentOID>)clonedVersionIndependentOid.getClass(),(Class<? extends VersionOID>)clonedVersionOid.getClass(),
														clonedVersionIndependentOid,clonedVersionOid);
		return outOid;
	}
	@Override
	public int compareTo(final OID o) {
		if (o == null) return -1;
		if (this.getOid() == null) return 1;
		return this.toString().compareTo(o.toString());
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getOid(),this.getVersion());
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof OIDForVersionableModelObjectBase) {
			OIDForVersionableModelObjectBase versionable = (OIDForVersionableModelObjectBase)obj;
			boolean oidEquals = Objects.equal(versionable.getOid(),this.getOid()); 
			boolean versionEquals = Objects.equal(versionable.getVersion(),this.getVersion());
			return oidEquals && versionEquals;
		} 
		return false;
	}
	@Override
	public <O extends OID> boolean isContainedIn(final O... oids) {
		return oids != null ? this.isContainedIn(Arrays.asList(oids))
							: false;
	}
	@Override
	public <O extends OID> boolean isNOTContainedIn(final O... oids) {
		return !this.isContainedIn(oids);
	}
	@Override
	public <O extends OID> boolean isContainedIn(final Iterable<O> oids) {
    	boolean outIsContained = false;
    	if (oids != null) {
    		for (O oid : oids) {
    			if (oid.equals(this)) {
    				outIsContained = true;
    				break;
    			}
    		}
    	}
    	return outIsContained;
	}
	@Override
	public <O extends OID> boolean isNOTContainedIn(final Iterable<O> oids) {
    	return !this.isContainedIn(oids);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> O cast() {
		return (O)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return this.toString();
	}
	public String memoCode() {
		return this.toString();
	}
	@Override
	public boolean isValid() {
		return this.getOid() != null && this.getOid().isValid() 
			&& this.getVersion() != null && this.getVersion().isValid();
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
	@GwtIncompatible("gwt does NOT supports reflection") @SuppressWarnings("unchecked")
	private <O extends OIDForVersionableModelObject> O _createVersionableOIDFromString(final Class<? extends VersionIndependentOID> versionIndependentOidType,final Class<? extends VersionOID> versionOidType,
												   		  	  						   final VersionIndependentOID versionIndependentOid,final VersionOID versionOid) {				
		Preconditions.checkArgument(versionIndependentOid != null && versionOid != null,
									"an OIDForVersionableModelObject cannot have a null VersionIndependentOID nor a null VersionOID");
		// find the constructor
        Constructor<?> constructor = null;
        try {
            constructor = this.getClass()
            				  .getDeclaredConstructor(new Class<?>[] {versionIndependentOidType,versionOidType}); 	// Constructor
        } catch (NoSuchMethodException nsmEx) {
        	throw new IllegalArgumentException(Strings.customized("Type {} is NOT a valid {}: it does NOT have a {}({},{}) constructor",
        													 	  this.getClass(),OIDForVersionableModelObject.class.getSimpleName(),
        													 	  this.getClass().getSimpleName(),VersionIndependentOID.class,VersionOID.class));        	
        }
		// create the oid
        O outOid = null;
        try {
        	outOid = (O)constructor.newInstance(new Object[] {versionIndependentOid,versionOid});
        } catch (Throwable th) {
        	th.printStackTrace();
        	throw new IllegalStateException(Strings.customized("Could NOT create a {} instance using the {}({},{}) constructor: {}",
        												  	   this.getClass(),
        												  	   this.getClass().getSimpleName(),VersionIndependentOID.class,VersionOID.class,
        												  	   th.getMessage()),
        									th);
        }
		return outOid;
	}
}
