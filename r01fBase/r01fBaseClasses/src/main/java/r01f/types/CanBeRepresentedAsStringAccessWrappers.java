package r01f.types;

import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.FactoryFrom;
import r01f.patterns.Provider;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class CanBeRepresentedAsStringAccessWrappers {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Removes boilerplate code when get-accessing a {@link CanBeRepresentedAsString} var
	 * Without this wrapper:
	 * <pre class='brush:java'>
	 * 		MyOID oid = MyOID.forId("myOid");	// an OID is a CanBeRepresentedAsString instance
	 * 		String str = oid != null ? oid.asString()
	 * 								 : null;	// ugly uhh! 		
	 * </pre>
	 * Using this wrapper:
	 * <pre class='brush:java'>
	 * 		MyOID oid = MyOID.forId("myOid");	// an OID is a CanBeRepresentedAsString instance
	 * 		String str = CanBeRepresentedAsStringGetAccess.wrap(oid)
	 * 													  .asStringOrNull();
	 * </pre> 
	 * @param <S>
	 */
	@AllArgsConstructor
	public static class CanBeRepresentedAsStringGetAccess<S extends CanBeRepresentedAsString> {
		private S _canBeRepresentedAsString;
		
		public static <S extends CanBeRepresentedAsString> CanBeRepresentedAsStringGetAccess<S> wrap(final S s) {
			return new CanBeRepresentedAsStringGetAccess<S>(s);
		}
		public void replaceWrappedWith(final S s) {
			_canBeRepresentedAsString = s;
		}
		public String asStringOrNull() {
			return _canBeRepresentedAsString != null ? _canBeRepresentedAsString.asString()
													 : null;
		}
	}
	/**
	 * Removes boilerplate code when set-accessing a {@link CanBeRepresentedAsString} var
	 * Without this wrapper:
	 * <pre class='brush:java'>
	 * 		String oidStr = "myOid";	// or maybe null!
	 * 		MyOID oid = oidStr != null ? MyOID.forId(oidStr)
	 * 								   : null;		// ugly uhh!!!
	 * </pre>
	 * Using this wrapper:
	 * <pre class='brush:java'>
	 * 		String oidStr = "myOid";	// or maybe null!
	 * 		MyOID oid = CanBeRepresentedAsStringSetAccess.wrap(MyOID::forId)
	 * 													 .createFrom(oidStr);
	 * </pre>
	 * @param <S>
	 */
	@RequiredArgsConstructor
	public static class CanBeRepresentedAsStringSetAccess<S extends CanBeRepresentedAsString> {
		private final FactoryFrom<String,S> _canBeRepresentedAsStringFactory;

		public static <S extends CanBeRepresentedAsString> CanBeRepresentedAsStringSetAccess<S> using(final FactoryFrom<String,S> factory) {
			return new CanBeRepresentedAsStringSetAccess<S>(factory);
		}
		public S createFrom(final String s) {
			return s != null ? _canBeRepresentedAsStringFactory.from(s)
							 : null;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Wraps a {@link CanBeRepresentedAsString} instance
	 * Usage:
	 * <pre class='brush:java'>
	 * 		@Accessors(prefix="_")
	 * 		public class MyType {
	 * 			@Getter private MyOID _oid;		// OIDs are CanBeRepresentedAsString instances
	 * 
	 * 			private transient final CanBeRepresentedAsStringWrapper<MyOID> _wrapper = new CanBeRepresentedAsStringWrapper(MyType::getOid,MyType::setOid,	// provider & consumer
	 * 																														  MyOID::forId);					// factory
	 * 			...
	 * 			public String getOidAsString() {
	 * 				// instead of:
	 * 				//		return _oid != null ? _oid.asString() : null;
	 * 				return _wrapper.asStringOrNull(); 
	 *			}
	 *			public void setOidFromString(final String str) {
	 *				// instead of:
	 *				//		_oid = str != null ? MyOID.forId(str) : null;
	 *				_wrapper.setFrom(str);
	 *			}
	 * 		}
	 * </pre>
	 * @param <S>
	 */
	public static class CanBeRepresentedAsStringWrapper<S extends CanBeRepresentedAsString> {
		private transient final Provider<S> _getAccess;
		private transient final Consumer<S> _setAccess;
		private transient final FactoryFrom<String,S> _factoryFromString;
		
		public CanBeRepresentedAsStringWrapper(final Provider<S> getAccess,final Consumer<S> setAccess,
											   final FactoryFrom<String,S> factory) {
			_getAccess = getAccess;
			_setAccess = setAccess;
			_factoryFromString = factory;
		}
		public String asStringOrNull() {
			S instance = _getAccess.provideValue();
			return instance != null ? instance.asString()
									: null;
		}
		public void setFrom(final String str) {
			S instance = str != null ? _factoryFromString.from(str) : null;
			_setAccess.accept(instance);
		}
	}
}
