package r01f.securitycontext;

import java.util.Date;

import javax.inject.Provider;

import r01f.guids.CommonOIDs.AuthenticatedActorID;
import r01f.guids.CommonOIDs.SecurityToken;

/**
 * Annotation to set that a client api is for the MASTER user: the one that have all privileges
 * used internally in batch processes or whatever internal operations
 * 
 * The only difference between the MASTER client API and a "normal" client api is that the MASTER one
 * is injected with a MASTER SecurityContext that ONLY legitimated providers can generate 
 * In order to ensure that the SecurityContext is a legitimate one:
 * 
 * 		1 - The the MASTER client api is injected with a MASTER SecurityContext that can only be created by legitimate SecurityContext provider
 * 			This MASTER SecurityContext contains a TOKEN signed with a private key that only both the legitimate provider and the CORE knows
 * 
 * 		3 - When a service interface's method call with a MASTER SecurityContext is made, the first layer at the CORE is 
 * 			in charge of ensuring the SecurityContext is legitimate; to do so, it usually checks the signed token 
 * 			using the secret shared with the legitimate SecurityContext provider 
 * 
 * Usage:
 * [0] - Create a type that provides MASTER SecurityContexts:
 * 		 <pre class='brush:java'>
 * 			public class MyMasterSecurityContextProvider
 * 				 extends SecurityContextProviderForMasterUserBase {
 * 				
 * 				public MyMasterSecurityContextProvider() {	// beware!! no-args constructor
 * 					super(new MySecurityContext());
 * 				}	
 * 		 }
 * 		 </pre>
 * 
 * [1] - At the CLIENT bootstrap guice module:
 *		 a) Bind the MASTER SecurityContext provider as a SINGLETON (important!!)
 *			<pre class='brush:java'>
 *				binder.bind(SecurityContext.class)
 *					  .annotatedWith(SecurityContextProviderForMasterUser.class)
 *					  .to(MyMasterSecurityContextProvider.class)
 *					  .in(Singleton.class);
 *			</pre>
 *
 * 		 b) Create a ClientAPI provider like
 * 		 	<pre class='brush:java'>
 * 				@Provides					// provides a client api 
 * 				@ClientAPIForMasterUser		// for MASTER system usage
 * 				@Singleton		// BEWARE!!!
 *				private MyClientApi _provideMasterClientAPI(@SecurityContextProviderForMasterUser MyMasterSecurityContextProvider securityContextProvider,
 *														    @ModelObjectsMarshaller final Marshaller modelObjectsMarshaller,
 *			   							  				    @Named("appCode") final Map<Class,ServiceInterface> srvcIfaceMappings) {
 *					// This creates an ad-hoc client api that uses a 
 *					// legitimated SecurityContext provider 
 *					return new MyClientApi(securityContextProvider,
 *										   modelObjectsMarshaller,
 *										   srvcIfaceMappings);
 *			   	}
 * 		 	</pre>
 * [2] - When requesting the MASTER client api to the injector:
 * 		 <pre class='brush:java'>
 * 				MyClientApi masterApi = injector.getInstance(Key.get(MyClientApi.class,
 * 															 		 ClientAPIForMasterUser.class));
 * 		 </pre>
 *       or inject the client api like:
 *       <pre class='brush:java'>
 *       		public class MyInjectedType {
 *       			@Inject
 *       			public MyInjectedType(@ClientAPIForMasterUser MyClientAPI masterApi) {
 *       				...
 *       			}
 *       		}
 *       </pre>
 *       or just request the client api as:
 *       <pre class='brush:java'>
 *       		public class MyClientAPIInjectedType {
 *       			@Inject 
 *       			public MyClientAPIInjectedType(@ClientAPIForMasterUser final MyClientAPI clientApi) {
 *       				... 
 *       			}
 *       		}
 *       </pre>
 * Note that the ClientAPI is usually used at the client-side; at the SERVER / CORE side, 
 * usually only the MASTER SecurityContext is needed instead of the MASTER client api
 * ... to do so:
 * <pre class='brush:java'>
 * 		public class MyCOREService {
 * 			private final Provider<SecurityContext> _masterSecurityContextProvider;
 * 			private final MyService _service;
 * 
 * 			@Inject 
 * 			public MyCOREService(@SecurityContextProviderForMasterUser final Provider<SecurityContext> masterSecurityContextProvider,
 * 																	   final MyService service) {
 * 				_masterSecurityContextProvider = masterSecurityContextProvider;
 * 				_service = service;
 * 			}
 * 			
 * 			public void doSomethingInteresting() {
 * 				// [1] - Get a MASTER security context	
 * 				SecurityContext masterSecurityContext = _masterSecurityContextProvider.get();	
 * 				
 * 				// [2] - Now we're MASTER!
 * 				_service.doSomething(masterSecurityContext);
 * 			}
 * 		}
 * </pre>
 *  
 * @param <C>
 */
public abstract class SecurityContextProviderForMasterUserBase 
		   implements Provider<SecurityContext> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final SecurityContext _masterSecurityContext;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityContextProviderForMasterUserBase(final SecurityContext masterSecurityContext) {
		_masterSecurityContext = masterSecurityContext;
		
		// complete the token
		SecurityContextBase context = (SecurityContextBase)_masterSecurityContext;
		context.setMasterUser(true);	// ensure it's a master context
		context.setAuthenticatedActorId(AuthenticatedActorID.MASTER);
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
		if (_masterSecurityContext == null) throw new IllegalStateException();
		return _masterSecurityContext;
	}
}
