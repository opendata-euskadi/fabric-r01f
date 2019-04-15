/*
 * Created on 09-jul-2004
 *
 * @author IE00165H
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.config;

import java.io.Serializable;
import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;
import r01f.locale.LanguageTexts;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * URL's security config
 */
@Accessors(prefix="_")
public class XLNetsTargetCfg 
  implements Serializable {
	
    private static final long serialVersionUID = -3619681298463900886L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    @RequiredArgsConstructor
    public enum ResourceAccess 
     implements EnumWithCode<String,ResourceAccess>,
     			Serializable {
    	RESTRICT("restrict"),
    	ALLOW("allow");
    	
    	@Getter private final String _code;
    	@Getter private final Class<String> _codeType = String.class;
    	
    	private static transient EnumWithCodeWrapper<String,ResourceAccess> _enums = EnumWithCodeWrapper.wrapEnumWithCode(ResourceAccess.class);
		@Override
		public boolean isIn(final ResourceAccess... els) {
			return _enums.isIn(this,els);
		}
		@Override
		public boolean is(final ResourceAccess el) {
			return _enums.is(this,el);
		}
		public static ResourceAccess fromName(final String name) {
			return _enums.fromName(name);
		}
		public static ResourceAccess fromCode(final String code) {
			return _enums.fromCode(code);
		}
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private String _urlPathPattern = null;					// Expresion regular que machea el path de la url a la que se aplica la seguridad
    @Getter @Setter private ResourceAccess _kind = ResourceAccess.RESTRICT;	// Tipo restrictivo por defecto
    @Getter @Setter private Collection<ResourceCfg> _resources = null;		// Elementos de los que hay que verificar la seguridad

/////////////////////////////////////////////////////////////////////////////////////////
//  GET & SET
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_kind).append(" URLPath pattern=").append(_urlPathPattern);
        if (CollectionUtils.hasData(_resources)) {
            for (ResourceCfg resCfg : _resources) {
                sb.append("\n\t").append(resCfg.toString());
            }
        }
        return sb.toString();
    }
/////////////////////////////////////////////////////////////////////////////////////////  
//	  
/////////////////////////////////////////////////////////////////////////////////////////     
    public boolean containsAnyNonMandatoryResource() {
    	return CollectionUtils.hasData(_resources) 
					? FluentIterable.from(_resources)
    					  .firstMatch(NON_MANDATORY_RESOURCE_FILTER)
    					  .isPresent()
    				: false;
    }
    public boolean allResourcesAreMandatory() {
    	return !this.containsAnyNonMandatoryResource();
    }
    public Collection<ResourceCfg> getNonMandatoryResources() {
    	return FluentIterable.from(_resources)
    					  .filter(NON_MANDATORY_RESOURCE_FILTER)
    					  .toList();
    }
    public Collection<ResourceCfg> getMandatoryResources() {
    	return FluentIterable.from(_resources)
    					  .filter(Predicates.not(NON_MANDATORY_RESOURCE_FILTER))
    					  .toList();
    }
    private static final transient Predicate<ResourceCfg> NON_MANDATORY_RESOURCE_FILTER = new Predicate<ResourceCfg>() {
																									@Override
																									public boolean apply(final ResourceCfg resCfg) {
																										return !resCfg.isMandatory();
																									}
														    					  		 };
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    @RequiredArgsConstructor
    public enum ResourceItemType 
     implements EnumWithCode<String,ResourceItemType>,
    			Serializable {
    	FUNCTION("function"),
    	OBJECT("object");
    	
    	@Getter private final String _code;
    	@Getter private final Class<String> _codeType = String.class;
    	
    	private static transient EnumWithCodeWrapper<String,ResourceItemType> _enums = EnumWithCodeWrapper.wrapEnumWithCode(ResourceItemType.class);
		@Override
		public boolean isIn(final ResourceItemType... els) {
			return _enums.isIn(this,els);
		}
		@Override
		public boolean is(final ResourceItemType el) {
			return _enums.is(this,el);
		}
		public static ResourceItemType fromName(final String name) {
			return _enums.fromName(name);
		}
		public static ResourceItemType fromCode(final String code) {
			return _enums.fromCode(code);
		}
    }
    /**
     * elements' security config
     */
    @Accessors(prefix="_")
    @RequiredArgsConstructor
    public class ResourceCfg 
      implements Serializable {
        private static final long serialVersionUID = -5044952456280782506L;
        
        @Getter private final String _oid;
        @Getter private final ResourceItemType _type;		// function / object
        @Getter private final boolean _mandatory;
        @Getter private final LanguageTexts _name;
        
        @Override
        public String toString() {
            return Strings.customized("{} {} {}: {}",
            					      (_mandatory ? " mandatory":""),_type,_oid,_name);
        }
    }
}
