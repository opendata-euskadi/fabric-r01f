package r01f.html.css;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.util.types.Strings;
import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.SupportedResourceType;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

/**
 * Wro4j (Web Resource Optimizer for Java - https://code.google.com/p/wro4j/) processor to pre-process Sass CSS files
 * with some goodies:
 * <ul>
 * 		<li>Sass @import options can be resolved to a remote file using http instead of local resolving</li>
 * 		<li>Supports compass framework</li> 
 * </ul>
 * It can be used with Wro4j (see documents at https://code.google.com/p/wro4j/) 
 */
@Slf4j
@SupportedResourceType(ResourceType.CSS)
@RequiredArgsConstructor
public class SassWro4jProcessor 
	 extends SassProcessorBase<SassWro4jProcessor>
  implements ResourcePreProcessor {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String ALIAS = "rubySassCss";
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Path where the ruby sass/compass gems can be found.
	 * This path can be:
	 * <ul>
	 * 		<li>If the gems are stored in a jar: classpath:[path_to_the_jar]/[gems_container_jar].jar![path_within_the_jar_to_the_gems]
	 * 			ie: classpath:gems.jar!gems</li>
	 * 		<li>If the gems are stored in the file system: [path_to_the_gems_containing_dir]
	 * 			ie: D:/develop/libs/ruby-gems/compass-gems/gems/</li>
	 * </ul>
	 */
	private final String _gemsHome;
	/**
	 * The real compiler
	 */
	private SassProcessor _sassProcessor;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new instance of the processor
	 * @return
	 */
	public static SassWro4jProcessor create(final String gemsHome) {
		return new SassWro4jProcessor(gemsHome);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void process(final Resource resource, 
						final Reader reader,final Writer writer) throws IOException {
		if (writer == null) throw new IOException("Writer null");
		
		SassProcessor sassProcessor = _getOrCreateSassProcessor(_gemsHome);
		try {
			String compiledCss = sassProcessor.process(reader);
			writer.write(compiledCss);
		} catch (final ProcessorException procEx) {
			final String resourceUri = resource == null ? Strings.EMPTY
														: resource.getUri();
			log.warn("Exception while applying {} processor on the {} resource, no processing applied...",
					 this.getClass().getSimpleName(),resourceUri,
					 procEx);
			WroRuntimeException wroEx = new WroRuntimeException(procEx.getMessage());
			wroEx.setStackTrace(procEx.getStackTrace());
			this.onException(wroEx);
		} finally {
			writer.flush();
		}
	}
	/**
	 * Invoked when a processing exception occurs. By default propagates the
	 * runtime exception.
	 */
	@SuppressWarnings("static-method")
	protected void onException(final WroRuntimeException wroEx) {
		throw wroEx;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private SassProcessor _getOrCreateSassProcessor(final String gemsHome) {
		if (_sassProcessor != null) _sassProcessor = SassProcessor.create(gemsHome)
																  .withOptions(_sassOptions);
		return _sassProcessor;
	}
}