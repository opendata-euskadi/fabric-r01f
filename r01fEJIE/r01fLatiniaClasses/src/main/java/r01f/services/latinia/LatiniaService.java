package r01f.services.latinia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.HandlerRegistry;

import org.w3c.dom.Document;

import com.ejie.w91d.client.W91DSendSms;
import com.ejie.w91d.client.W91DSendSmsWebServiceImplService_Impl;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.model.latinia.LatiniaRequest;
import r01f.model.latinia.LatiniaRequestMessage;
import r01f.model.latinia.LatiniaResponse;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.patterns.Factory;
import r01f.patterns.Memoized;
import r01f.services.EJIESoapMessageHandler;
import r01f.util.types.Strings;
import r01f.xml.XMLUtils;

/**
 * Encapsulates latinia message sending
 * Usage_
 * <pre class='brush:java'>
 *		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
 *																			.notUsingCache()
 *																			.forComponent(AppComponent.forId("test"));
 *		LatiniaServiceAPIData latiniaApiData = LatiniaServiceAPIData.createFrom(props,
 *																				"test");
 *		
 *		LatiniaService latiniaService = new LatiniaService(latiniaApiData);
 * </pre>
 * 
 * Using guice:
 * <pre class='brush:java'>
 *		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
 *																			.notUsingCache()
 *																			.forComponent(AppComponent.forId("test"));
 *		LatiniaServiceAPIData latiniaApiData = LatiniaServiceAPIData.createFrom(props,
 *																				"test");
 *		
 *		Injector injector = Guice.createInjector(new LatiniaServiceGuiceModule(latiniaApiData));
 *
 *		LatiniaService latiniaService = injector.getInstance(LatiniaService.class);
 * </pre>
 * 
 * To build a message:
 * <pre class='brush:java'>
 *	    private static LatiniaRequestMessage _createMockMessage() {
 *	    	LatiniaRequestMessage latiniaMsg = new LatiniaRequestMessage();
 *	    	latiniaMsg.setAcknowledge("S");
 *	    	latiniaMsg.setMessageContent("TEST MESSAGE!!!");
 *	    	latiniaMsg.setReceiverNumbers("688671967");
 *	    	return latiniaMsg;
 *	    }
 * </pre>
 *
 * For all this to work a properties file with the following config MUST be provided:
 * <pre class='xml'>
 * 	<latinia>
 *		<wsURL>http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/W91dSendSms?WSDL</wsURL>
 *		<authentication>
 *		  <enterprise>
 *		    		<login>INNOVUS</login>
 *		    		<user>innovus.superusuario</user>
 *		    		<password>MARKSTAT</password>
 *		  </enterprise>
 *		  <clientApp>
 *		    		<productId>X47B</productId>
 *		    		<contractId>2066</contractId>
 *		    		<password>X47N</password>
 *		  </clientApp>
 *		</authentication>
 *	</latinia>
 * </pre>
 *
 * NOTE:
 * Latinia session Token:
 * 		<authenticationLatinia>
 * 			<loginEnterprise>INNOVUS</loginEnterprise>
 * 		    <userLatinia>innovus.superusuario</userLatinia>
 * 		    <passwordLatinia>MARKSTAT</passwordLatinia>
 * 		    <refProduct>X47B</refProduct>
 * 		    <idContract>xxxx</idContract>
 * 		    <password>X47B</password>
 * 		</authenticationLatinia>
 *
 * SOAP Message example:
 * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:w91d="http://w91d">
 * 	<soapenv:Header>
 *		<authenticationLatinia>
 *           <userLatinia>innovus.superusuario</userLatinia>
 *           <passwordLatinia>MARKSTAT</passwordLatinia>
 *           <refProduct>X47B</refProduct>
 *           <loginEnterprise>INNOVUS</loginEnterprise>
 *           <idContract>2066</idContract>
 *           <password>X47B</password>
 *		</authenticationLatinia>
 * 	</soapenv:Header>
 * 	<soapenv:Body>
 * 		<StringInput xmlns="http://w91d">
 *		<![CDATA[<?xml version="1.0" encoding="UTF-8" standalone="no"?>
 *		<PETICION>
 *		<LATINIA>
 *		<MENSAJES>
 *		<MENSAJE_INFO ACUSE="S">
 *		<TEXTO>Hola con prioridad BAJA pero BAJA</TEXTO>
 *		<GSM_DEST>616178858</GSM_DEST>
 *		</MENSAJE_INFO>
 *		</MENSAJES>
 *		</LATINIA>
 *		</PETICION>]]>
 *		</StringInput>
 *	</soapenv:Body>
 * </soapenv:Envelope>
 *
 */
@Singleton
@Slf4j
public class LatiniaService {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final LatiniaServiceAPIData _apiData;
	private final LatiniaAuthTokenProvider _authTokenProvider;
	private final Memoized<Document> _authToken = new Memoized<Document>() {
														@Override
														protected Document supply() {
															return _authTokenProvider.createAuthTokenDocument();
														}
												  };
	
	private final Marshaller _latiniaObjsMarshaller;

	private final Factory<W91DSendSms> _wsClientFactory = new Factory<W91DSendSms>() {
																	@Override @SuppressWarnings("deprecation")
																	public W91DSendSms create() {
																		log.debug("[Latinia] > creating the latinia ws client to URL {}",_apiData.getWebServiceUrl());
																		W91DSendSms sendSmsService = null;
																		try {
																			// [1] - Create the auth token
																			Map<String,String> authTokenMap = new HashMap<String,String>();
																			authTokenMap.put("sessionToken",XMLUtils.asStringLinearized(_authToken.get())); //Linarize xml, strip whitespaces and newlines from latinia auth token

																			// [2] - Create the client

																			//SE CAMBIA EL M�TODO "_apiData.getWebServiceUrl().asString()" POR "_apiData.getWebServiceUrl().getUrl()"
																			//DEBIDO A PROBLEMAS CON LA LLAMADA A LATINIA EN PRODUCCI�N

																			W91DSendSmsWebServiceImplService_Impl ws = new W91DSendSmsWebServiceImplService_Impl(_apiData.getWebServiceUrl().getUrl());
																			sendSmsService = ws.getW91dSendSms();
																			HandlerRegistry registry = ws.getHandlerRegistry();
																			Object port = ws.getPorts().next();

																			List<HandlerInfo> handlerList = new ArrayList<HandlerInfo>();
																			handlerList.add(new HandlerInfo(EJIESoapMessageHandler.class,
																											authTokenMap,
																											null));	// ?
																			registry.setHandlerChain((QName)port,
																									 handlerList);

																		} catch (Throwable th) {
																			log.error("[Latinia] > Error while creating the {} service: {}",W91DSendSms.class,th.getMessage(),th);
																		}
																		return sendSmsService;
																	}
															  };
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public LatiniaService(final LatiniaServiceAPIData apiData,
						  final Marshaller marshaller) {
		_apiData = apiData;
		_authTokenProvider = new LatiniaAuthTokenProvider(apiData);
		_latiniaObjsMarshaller = marshaller != null ? marshaller : MarshallerBuilder.build();
	}
	/**
	 * This constructor crashes in execution time because of the exception:
	 *   r01f.services.latinia.LatiniaService has more than one constructor annotated with @Inject. Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor that is not private.
     *   at r01f.services.latinia.LatiniaService.class(LatiniaService.java:130)
	 */
	 public LatiniaService(final LatiniaServiceAPIData apiData) {
		this(apiData,
			 MarshallerBuilder.build());
	 }
/////////////////////////////////////////////////////////////////////////////////////////
//	API
/////////////////////////////////////////////////////////////////////////////////////////
	public String getLatiniaRequestMessageAsXml(final LatiniaRequestMessage msg) {
		LatiniaRequest request = new LatiniaRequest();
		request.addMessage(msg);

//		StringBuilder requestXml = new StringBuilder("<![CDATA[");
//		requestXml.append(_apiData.getLatiniaObjsMarshaller().xmlFromBean(request));
//		requestXml.append("]]>");
		StringBuilder requestXml = new StringBuilder(_latiniaObjsMarshaller.forWriting().toXml(request));
		return requestXml.toString();
	}
	public LatiniaResponse sendNotification(final LatiniaRequestMessage msg) {
		log.debug("[Latinia] > Send message");

		// [1] - Create a ws client using the factory
		W91DSendSms sendSmsService = _wsClientFactory.create();
		if (sendSmsService == null) throw new IllegalStateException(Throwables.message("Could NOT create a {} instance!",W91DSendSms.class));


		// [2] - Send the request
		LatiniaResponse response = null;
		try {
			String requestXml = this.getLatiniaRequestMessageAsXml(msg);
			log.info("[Latinia] > request XML: {} ",requestXml);

			final String responseXml = sendSmsService.sendSms(requestXml);
			if (!Strings.isNullOrEmpty(responseXml)) {
				String theResponseXml = responseXml.replaceAll("PETICION","RESPUESTA");
				log.info("[Latinia] > response XML: {}",responseXml);
				response = _latiniaObjsMarshaller.forReading().fromXml(theResponseXml,
															  		   LatiniaResponse.class);
			} else {
				throw new IllegalStateException("Latinia WS returned a null response!");
			}

		} catch (Throwable th) {
			log.error("[Latinia] > Error while calling ws at {}: {}",_apiData.getWebServiceUrl(),th.getMessage(),th);
		}
		return response;
	}
}
