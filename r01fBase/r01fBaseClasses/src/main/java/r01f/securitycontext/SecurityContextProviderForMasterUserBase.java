package r01f.securitycontext;

@Deprecated // see SecurityContextProviderForSystemUserBase
public abstract class SecurityContextProviderForMasterUserBase 
		   	  extends SecurityContextProviderForSystemUserBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityContextProviderForMasterUserBase(final SecurityContext sysSecurityContext) {
		super(sysSecurityContext);
	}
}
