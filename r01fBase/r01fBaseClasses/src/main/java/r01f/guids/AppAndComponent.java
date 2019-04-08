package r01f.guids;

import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.objectstreamer.annotations.MarshallType;



/**
 * AppCode and component in a single object
 * Normally the id is internally stored as a {@link String} like appCode.appComponent
 * The appCode and appComponent parts can be accessed individually:
 * <pre class='brush:java'>
 * 		AppAndComponent appAndComp = AppAndComponent.composedBy("myApp","myComp");
 * 		AppCode = appAndComp.getAppCode();
 * 		AppComponent = appAndComp.getComponent();
 * </pre>
 */
@MarshallType(as="appAndComponent")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public final class AppAndComponent 
           extends OIDBaseMutable<String> {
	
	private static final long serialVersionUID = -1130290632493385784L;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AppAndComponent(final String oid) {
		super(oid);
	}
	public AppAndComponent(final AppCode appCode,final AppComponent appComponent) {
		this(appCode.asString(),appComponent.asString());
	}
	public AppAndComponent(final String appCode,final String appComponent) {
		this(appCode + "." + appComponent);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public static AppAndComponent forId(final String id) {
		return new AppAndComponent(id);
	}
	public static AppAndComponent composedBy(final AppCode appCode,final AppComponent appComponent) {
		return AppAndComponent.composedBy(appCode.asString(),appComponent.asString());
	}
	public static AppAndComponent composedBy(final String appCode,final String appComponent) {
		return new AppAndComponent(appCode,appComponent);
	} 
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AppCode getAppCode() {
		List<String> parts = FluentIterable.from(Splitter.on(".").split(this.getId()))
									   	   .toList();
		return AppCode.forId(parts.get(0));
	}
	public AppComponent getAppComponent() {
		List<String> parts = FluentIterable.from(Splitter.on(".").split(this.getId()))
									   	   .toList();
		if (parts.size() == 2) {
			return AppComponent.forId(parts.get(1));
		} else if (parts.size() > 2) {
			StringBuilder comp = new StringBuilder();
			for (int i=1; i < parts.size(); i++) {
				comp.append(parts.get(i));
				if (i < parts.size()-1) comp.append(".");
			}
			return AppComponent.forId(comp.toString());
		} else {
			throw new IllegalStateException();
		}
	}
		
}
