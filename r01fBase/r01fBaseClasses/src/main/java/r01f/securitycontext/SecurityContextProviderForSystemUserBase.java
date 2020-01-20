package r01f.securitycontext;

import java.util.Date;

import javax.inject.Provider;

import r01f.guids.CommonOIDs.AuthenticatedActorID;
import r01f.guids.CommonOIDs.SecurityToken;

/**
 * Annotation to set that a client api is for the SYSTEM user: the one that have all privileges
 * used internally in batch processes or whatever internal operations
 * 
 * The only difference between the SYSTEM client API and a "normal" client api is that the SYSTEM one
 * is injected with a SYSTEM SecurityContext that ONLY legitimated providers can generate 
 * In order to ensure that the SecurityContext is a legitimate one:
 * 
 * 		1 - The the SYSTEM client api is injected with a SYSTEM SecurityContext that can only be created by legitimate SecurityContext provider
 * 			This SYSTEM SecurityContext contains a TOKEN signed with a private key that only both the legitimate provider and the CORE knows
 * 
 * 		3 - When a service interface's method call with a SYSTEM SecurityContext is made, the first layer at the CORE is 
 * 			in charge of ensuring the SecurityContext is legitimate; to do so, it usually checks the signed token 
 * 			using the secret shared with the legitimate SecurityContext provider 
 * 
 * Usage:
 * [0] - Create a type that provides SYSTEM SecurityContexts:
 * 		 <pre class='brush:java'>
 * 			public class MyMasterSecurityContextProvider
 * 				 extends SecurityContextProviderForSystemUserBase {
 * 				
 * 				public MyMasterSecurityContextProvider() {	// beware!! no-args constructor
 * 					super(new MySecurityContext());		// <-- this is a security context with all the privileges
 * 				}	
 * 		 	}
 * 		 </pre>
 * 
 * [1] - At the CLIENT bootstrap guice module:
 *		 a) Bind the SYSTEM SecurityContext provider as a SINGLETON (important!!)
 *			<pre class='brush:java'>
 *				binder.bind(SecurityContext.class)
 *					  .annotatedWith(SecurityContextProviderForSystemUser.class)
 *					  .to(MyMasterSecurityContextProvider.class)
 *					  .in(Singleton.class);
 *			</pre>
 *
 * 		 b) Create a ClientAPI provider like
 * 		 	<pre class='brush:java'>
 * 				@Provides					// provides a client api 
 * 				@ClientAPIForSystemUser		// for SYSTEM system usage
 * 				@Singleton		// BEWARE!!!
 *				private MyClientApi _provideMasterClientAPI(@SecurityContextProviderForSystemUser MyMasterSecurityContextProvider securityContextProvider,
 *														    @ModelObjectsMarshaller final Marshaller modelObjectsMarshaller,
 *			   							  				    @Named("appCode") final Map<Class,ServiceInterface> srvcIfaceMappings) {
 *					// This creates an ad-hoc client api that uses a 
 *					// legitimated SecurityContext provider 
 *					return new MyClientApi(securityContextProvider,
 *										   modelObjectsMarshaller,
 *										   srvcIfaceMappings);
 *			   	}
 * 		 	</pre>
 * [2] - When requesting the SYSTEM client api to the injector:
 * 		 <pre class='brush:java'>
 * 				MyClientApi masterApi = injector.getInstance(Key.get(MyClientApi.class,
 * 															 		 ClientAPIForSystemUser.class));
 * 		 </pre>
 *       or just inject the client api as:
 *       <pre class='brush:java'>
 *       		public class MyClientAPIInjectedType {
 *       			@Inject 
 *       			public MyClientAPIInjectedType(@ClientAPIForSystemUser final MyClientAPI clientApi) {
 *       				... 
 *       			}
 *       		}
 *       </pre>
 * Note that the ClientAPI is usually used at the client-side; at the SERVER / CORE side, 
 * usually only the SYSTEM SecurityContext is needed instead of the SYSTEM client api
 * ... to do so:
 * <pre class='brush:java'>
 * 		public class MyCOREService {
 * 			private final Provider<SecurityContext> _masterSecurityContextProvider;
 * 			private final MyService _service;
 * 
 * 			@Inject 
 * 			public MyCOREService(@SecurityContextProviderForSystemUser final Provider<SecurityContext> masterSecurityContextProvider,
 * 																	   final MyService service) {
 * 				_masterSecurityContextProvider = masterSecurityContextProvider;
 * 				_service = service;
 * 			}
 * 			
 * 			public void doSomethingInteresting() {
 * 				// [1] - Get a SYSTEM security context	
 * 				SecurityContext masterSecurityContext = _masterSecurityContextProvider.get();	
 * 				
 * 				// [2] - Now we're SYSTEM!
 * 				_service.doSomething(masterSecurityContext);
 * 			}
 * 		}
 * </pre>
 *  
 * @param <C>
 */
public abstract class SecurityContextProviderForSystemUserBase 
		   implements Provider<SecurityContext> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final SecurityContext _systemSecurityContext;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityContextProviderForSystemUserBase(final SecurityContext masterSecurityContext) {
		_systemSecurityContext = masterSecurityContext;
		
		// complete the token
		SecurityContextBase context = (SecurityContextBase)_systemSecurityContext;
		context.setSystemUser(true);	// ensure it's a master context
		context.setAuthenticatedActorId(AuthenticatedActorID.SYSTEM);
		context.setCreateDate(new Date());
		context.setSecurityToken(_createToken());
	}
	/**
	 * Creates a security context that's set a the token
	 * This method can be overridden to create a custom token
	 * @return
	 */
	protected SecurityToken _createToken() {
		// TODO load a SECRET shared with the CORE and sign some text
		return SecurityToken.forId("some hash");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public SecurityContext get() {
		if (_systemSecurityContext == null) throw new IllegalStateException();
		return _systemSecurityContext;
	}
}
