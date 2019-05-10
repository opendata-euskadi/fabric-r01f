package r01f.servlet.spring;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The SpringConfigDistpacherInitializerBase 
 *       web initilizer is based in this structure :
 * 
 *     SpringConfigDistpacherInitializerBase
 *            ----->  SpringRootConfigBootstrap
 *                  ---->     SpringWebMvcComponent
 *                                   ---> REST Services
 *
 *  @author  PCI
  * @param <T>
 */
@Accessors(prefix="_")
public abstract class SpringConfigDistpacherInitializerBase<T extends SpringRootConfigBootstrap, MVC extends SpringWebMvcComponent>
		extends AbstractAnnotationConfigDispatcherServletInitializer {
/////////////////////////////////////////////////////////////////////////////////////
// MEMBERS
////////////////////////////////////////////////////////////////////////////////////
	private @Getter Class<T> 	_bootsTrapClassType;
	private @Getter Class<MVC>  _mvcClassType;
/////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
////////////////////////////////////////////////////////////////////////////////////
	public SpringConfigDistpacherInitializerBase(final Class<T> classType, final  Class<MVC> mvvcComponent) {
		_bootsTrapClassType = classType;
		_mvcClassType = mvvcComponent;
	}
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { _bootsTrapClassType};
    }
    @Override
    protected Class<?>[] getServletConfigClasses() {
         return new Class[] { _mvcClassType};
    }
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}