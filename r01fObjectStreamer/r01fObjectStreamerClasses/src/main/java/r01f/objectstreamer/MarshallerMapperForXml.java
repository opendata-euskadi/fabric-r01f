package r01f.objectstreamer;

import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import r01f.guids.CommonOIDs.AppCode;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;

public class MarshallerMapperForXml
	 extends XmlMapper {

	private static final long serialVersionUID = -4318987020423327233L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static MarshallerMapperForXml forApps(final AppCode... appCodes) {
		return MarshallerMapperForXml.forApps(appCodes != null ? Sets.<AppCode>newLinkedHashSet(Lists.newArrayList(appCodes))
								  							   : Sets.<AppCode>newLinkedHashSet());
	}
	public static MarshallerMapperForXml forApps(final Set<AppCode> appCodes) {
		return new MarshallerMapperForXml(FluentIterable.from(appCodes)
												.transform(JavaPackage.APP_CODE_TO_JAVA_PACKAGE)
												.toSet());
	}
	public static MarshallerMapperForXml forJavaPackages(final Set<JavaPackage> javaPackages) {
		return new MarshallerMapperForXml(javaPackages);
	}
	public static MarshallerMapperForXml forJavaPackages(final JavaPackage... javaPackages) {
		return new MarshallerMapperForXml(javaPackages);
	}
	public MarshallerMapperForXml(final JavaPackage... javaPackages) {
		this(javaPackages != null ? Sets.<JavaPackage>newLinkedHashSet(Lists.newArrayList(javaPackages))
								  : Sets.<JavaPackage>newLinkedHashSet());
	}
	public MarshallerMapperForXml(final Set<JavaPackage> javaPackages) {
		this(javaPackages,
			  null);	// no custom modules
	}
	public MarshallerMapperForXml(final Set<JavaPackage> javaPackages,
								  final Set<? extends MarshallerModule> jacksonModules) {
		this(javaPackages, jacksonModules, null);
	}
	public MarshallerMapperForXml(final Set<JavaPackage> javaPackages,
								  final Set<? extends MarshallerModule> jacksonModules,
								  final Set<? extends SimpleModule> jacksonSimpleModules) {
		// WOODSTOX error in weblogic 10.3.6
		// =================================
		// Jackson XML performs better with woodstox (see: https://github.com/FasterXML/jackson-dataformat-xml/)
		// BUT it's does NOT work with woodstox version of  weblogic 10.3.6
		// To enable woodstox (JUST WOODTOX AND NOT STAX !!) : (
	    //   <wls:prefer-application-packages>
	    //		 <wls:package-name>com.ctc.wstx.*</wls:package-name>
       //   </wls:prefer-application-packages>
		//
		// [0] - Configure the xml input & output factories to use Woodstok and other xml-specific configs
		// ensure Woddstok parser is in use: https://github.com/FasterXML/jackson-dataformat-xml/issues/32
		super(new XmlFactory(new WstxInputFactory(),
							 new WstxOutputFactory()));

		// override default instance of XmlFactory(WstxInputFactory,WstxOutputFactory)
//		this.getFactory().setXMLInputFactory(new WstxInputFactory());
//		this.getFactory().setXMLOutputFactory(new WstxOutputFactory() {
//														@Override
//														public XMLStreamWriter createXMLStreamWriter(final Writer w) throws XMLStreamException {
//															XMLStreamWriter streamWriter = super.createXMLStreamWriter(w);
//															streamWriter.setPrefix("xlink","http://www.w3.org/1999/xlink");
//															streamWriter.setDefaultNamespace("http://www.w3.org/1999/xlink");
//															return streamWriter;
//														}
//											 });
		// Deserialize: use namespaces
		XMLInputFactory xmlInputFactory = this.getFactory().getXMLInputFactory();
		xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE,Boolean.TRUE);				// beware namespaces
		try {
			xmlInputFactory.setProperty(WstxInputProperties.P_RETURN_NULL_FOR_DEFAULT_NAMESPACE,Boolean.TRUE);
		} catch (IllegalArgumentException iae) {
			// Can't use this property with weblogic 3.10.6 woodstox jar because the jar is too old
		}

		// Serialize:
		XMLOutputFactory outputFactory = this.getFactory().getXMLOutputFactory();
//		outputFactory.setProperty(XMLOutputFactory2.P_AUTOMATIC_NS_PREFIX,"r01");					// the xmlns prefix automatically added to annotated fields (the prefix is generated like prefix{xx})
		outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES,Boolean.TRUE);			// do now fail when xmlns='' (namespace-reparing mode)



		// [1] - register the r01f module
		this.registerModule(new MarshallerModuleForXml(javaPackages));		// BEWARE!!! XML Module!!

		// [2] - register given modules
		if (CollectionUtils.hasData(jacksonModules)) {
			for (MarshallerModule jsonMod : jacksonModules) {
				if (!(jsonMod instanceof Module)) throw new IllegalArgumentException(String.format("% MUST be a subtype of %s to be a jackson module",
																								   jsonMod.getClass(),Module.class));
				this.registerModule((Module)jsonMod);
			}
		}
		if (CollectionUtils.hasData(jacksonSimpleModules)) {
			for (SimpleModule jsonMod : jacksonSimpleModules) {
				this.registerModule(jsonMod);
			}
		}

		// [3] - Global default config
		MarshallerObjectMappers.setDefaultConfig(this);
	}
	
}
