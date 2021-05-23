package r01f.http.loadbalance;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.Url;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class LoadBalancerIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static interface LoadBalancerID
				    extends OIDTyped<String> {
		// just a marker interface
	}
	@Immutable
	public static abstract class LoadBalancerIDBase
	              		 extends OIDBaseMutable<String> 	// usually this should extend OIDBaseImmutable BUT it MUST have a default no-args constructor to be serializable
					  implements LoadBalancerID {

		private static final long serialVersionUID = 4449032738491944411L;

		public LoadBalancerIDBase() {
			/* default no args constructor for serialization purposes */
		}
		public LoadBalancerIDBase(final String id) {
			super(id);
		}
		@Override
		public boolean isValid() {
			return super.isValid() && this.getId().length() <= OID.OID_LENGTH;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * backend server id
	 */
	@Immutable
	@MarshallType(as="loadBalancedServiceId")
	@NoArgsConstructor
	public static class LoadBalancedServiceID 
	            extends LoadBalancerIDBase {

		private static final long serialVersionUID = -5495716286961699820L;
		
		public LoadBalancedServiceID(final String oid) {
			super(oid);
		}
		public static LoadBalancedServiceID named(final String id) {
			return new LoadBalancedServiceID(id);
		}		
		public static LoadBalancedServiceID forId(final String id) {
			return new LoadBalancedServiceID(id);
		}
		public static LoadBalancedServiceID valueOf(final String id) {
			return new LoadBalancedServiceID(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * backend server id
	 */
	@Immutable
	@MarshallType(as="backendServerId")
	@NoArgsConstructor
	public static class LoadBalancedBackEndServerID 
	            extends LoadBalancerIDBase {

		private static final long serialVersionUID = -1529074365497610822L;
		public LoadBalancedBackEndServerID(final String oid) {
			super(oid);
		}
		public static LoadBalancedBackEndServerID forId(final String id) {
			return new LoadBalancedBackEndServerID(id);
		}
		public static LoadBalancedBackEndServerID valueOf(final String id) {
			return new LoadBalancedBackEndServerID(id);
		}
		public static LoadBalancedBackEndServerID from(final Url url) {
			return new LoadBalancedBackEndServerID(url.asString());
		}
	}
}
