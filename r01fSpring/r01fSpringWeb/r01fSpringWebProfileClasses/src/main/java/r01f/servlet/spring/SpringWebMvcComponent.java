package r01f.servlet.spring;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
@EnableWebMvc
public class SpringWebMvcComponent
          extends WebMvcConfigurerAdapter {
 //Just to enbale WebMVC
}
