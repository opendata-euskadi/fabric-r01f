package r01f.services.ids;

import java.util.List;

import com.google.common.base.Splitter;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.Lists;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServiceIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//  BASE
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static abstract class AppCodeAndModuleBase<A,M> {
		@MarshallField(as="appCode",whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final A _appCode;
		
		@MarshallField(as="module",whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final M _module;
		
		public String asString() {
			return this.toString();
		}
		@Override
		public String toString() {
			return Strings.customized("{}.{}",_appCode,_module);
		}
		@Override
		public int hashCode() {
			return this.toString().hashCode();
		}
		@Override
		public boolean equals(final Object obj) {
			if (obj == null) return false;
			if (obj == this) return true;
			if (obj instanceof AppCodeAndModuleBase) {
				AppCodeAndModuleBase<?,?> otherAppCodeAndModule = (AppCodeAndModuleBase<?,?>)obj;
				return otherAppCodeAndModule.toString().equals(this.toString());		
			} else if (obj instanceof String) {
				return obj.toString().equals(this.toString());
			} 
			return super.equals(obj);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CLIENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * AppCode
	 */
	@MarshallType(as="clientApiAppCode")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class ClientApiAppCode 
	                  extends AppCode {
		private static final long serialVersionUID = 7093516452073951301L;
		public ClientApiAppCode(final String oid) {
			super(oid);
		}
		public static ClientApiAppCode forId(final String id) {
			return new ClientApiAppCode(id);
		}
		public static ClientApiAppCode valueOf(final String id) {
			return ClientApiAppCode.forId(id);
		}
		public AppCode asAppCode() {
			return AppCode.forId(this.getId());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * AppCode
	 */
	@MarshallType(as="coreAppCode")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class CoreAppCode 
	                  extends AppCode {
		private static final long serialVersionUID = 7498970290219115981L;
		public CoreAppCode(final String oid) {
			super(oid);
		}
		public static CoreAppCode of(final AppCode appCode) {
			return new CoreAppCode(appCode.asString());
		}
		public static CoreAppCode forId(final String id) {
			return new CoreAppCode(id);
		}
		public static CoreAppCode valueOf(final String id) {
			return CoreAppCode.forId(id);
		}
		public AppCode asAppCode() {
			return AppCode.forId(this.getId());
		}
	}
	/**
	 * AppCode component
	 */
	@MarshallType(as="coreModule")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class CoreModule 
	                  extends AppComponent {
		private static final long serialVersionUID = -8935025566081906908L;
		public CoreModule(final String oid) {
			super(oid);
		}
		public static CoreModule of(final AppComponent comp) {
			return new CoreModule(comp.asString());
		}
		public static CoreModule forId(final String id) {
			return new CoreModule(id);
		}
		public static CoreModule valueOf(final String id) {
			return CoreModule.forId(id);
		}
		public AppComponent asAppComponent() {
			return AppComponent.forId(this.getId());
		}
		public static final CoreModule SERVICES = CoreModule.forId("services");
		public static final CoreModule DBPERSISTENCE = CoreModule.forId("dbpersistence");
		public static final CoreModule FSPERSISTENCE = CoreModule.forId("fspersistence");
		public static final CoreModule SEARCHPERSISTENCE = CoreModule.forId("searchpersistence");
		public static final CoreModule RENDER = CoreModule.forId("render");
		public static final CoreModule NOTIFIER = CoreModule.forId("notifier");
		public static final CoreModule BUSINESS = CoreModule.forId("business");
		public static final CoreModule SECURITY = CoreModule.forId("security");
	}
	@MarshallType(as="coreAppAndModule")
	@EqualsAndHashCode(callSuper=true)
	@Accessors(prefix="_")
	public static final class CoreAppAndModule 
				      extends AppCodeAndModuleBase<CoreAppCode,CoreModule> {
		public CoreAppAndModule(@MarshallFrom("appCode") final CoreAppCode api,@MarshallFrom("module") final CoreModule module) {
			super(api,module);
		}
		public static CoreAppAndModule of(final CoreAppCode appCode,final CoreModule module) {
			return new CoreAppAndModule(appCode,module);
		}
		public static CoreAppAndModule of(final String appCodeAndModule) {
			List<String> parts = Lists.newArrayList(Splitter.on(".")
									 						.split(appCodeAndModule));
			if (parts.size() == 2) {
				return CoreAppAndModule.of(CoreAppCode.forId(parts.get(0)),
										   CoreModule.forId(parts.get(1)));
			} else if (parts.size() > 2) {
				StringBuilder comp = new StringBuilder();
				for (int i=1; i < parts.size(); i++) {
					comp.append(parts.get(i));
					if (i < parts.size()-1) comp.append(".");
				}
				return CoreAppAndModule.of(CoreAppCode.forId(parts.get(0)),CoreModule.forId(comp.toString()));
			} else {
				throw new IllegalStateException();
			}
		}
	}
}
