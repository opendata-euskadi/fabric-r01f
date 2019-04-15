package r01f.html.css;

import java.io.IOException;
import java.io.Reader;

import javax.script.ScriptException;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.io.util.StringPersistenceUtils;
import r01f.util.types.StringConverter;
import r01f.util.types.Strings;

/**
 * Process Sass styles supporting:
 * <ul>
 * 		<li>@import options resolved to a remote file using http instead of local resolving</li>
 * 		<li>Compass framework</li> 
 * </ul>
 * Standalone Usage:
 * <pre class='brush:java'>
 * SassProcessor sassProcessor = SassProcessor.create("D:/eclipse/libs/ruby-gems/compass-gems/gems/")	
 * 											  .withOptions(SassOptions.create()
 *											  						  .findFrameworkLocalResourcesAt(FRAMEWORK_HOME_DIR)
 *										  							  .webServerDirForStylesheets("/")
 *										  							  .webServerDirForScripts("/")
 *										  							  .webServerDirForImages("/")	
 *										  							  .build());
 * @Cleanup Reader sassSourceReader = ... a reader form some scss source ...
 * String compiledCss = sassProcessor.process(sassSourceReader);
 * System.out.println(writer.toString());
 * </pre>
 * It also can be used with Wro4j (see documents at https://code.google.com/p/wro4j/) 
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class SassProcessor
	 extends SassProcessorBase<SassProcessor> {
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
	 * 			ie: D:/eclipse/libs/ruby-gems/compass-gems/gems/</li>
	 * </ul>
	 */
	private final String _gemsHome;
	/**
	 * Ruby SaasEngine; this should be initialized ONLY once.
	 */
	private RubySassEngine _rubySASSEngine;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new instance of the processor
	 * @return
	 */
	public static SassProcessor create(final String gemsHome) {
		return new SassProcessor(gemsHome);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Transforms a sass content into css 
	 * Uses remote importing and Compass if they're initialized while creating an instance of {@link SassProcessor}
	 * @param sassCss
	 * @return
	 * @throws ScriptException
	 */
	public String process(final Reader sassSourceReader) throws ProcessorException {
		return this.process(_sassOptions,
							sassSourceReader);
	}
	/**
	 * Transforms a sass content into css 
	 * @param sassOptions sass and compass framework options 
	 * @param sassCss the Sass content to process.
	 */
	public String process(final SassOptions sassOptions,
						  final Reader sassSourceReader) throws ProcessorException {
		String outCompiledCss = null;
		try {
			String sassSource = StringPersistenceUtils.load(sassSourceReader);
			try {
				outCompiledCss = _getOrCreateRubySASSEngine(_gemsHome)
											.process(sassOptions,
													 sassSource);
				
			} catch (final ScriptException scriptEx) {
				ProcessorException procEx = null;
				if (scriptEx.getLineNumber() > 0 && scriptEx.getColumnNumber() > 0) {
					procEx = new ProcessorException(Strings.customized("Error while processing SASS compile ruby script at line {} column {}: {}",
														   			   scriptEx.getLineNumber(),scriptEx.getColumnNumber(),
																	   scriptEx.getMessage()),
												 scriptEx);
				} else {
					procEx = new ProcessorException(scriptEx);
				}
				throw procEx;
			} finally {
				sassSourceReader.close();
			}
		} catch (IOException ioEx) {
			throw new ProcessorException("IOException while reading the Sass source or writing to the CSS destination: " + ioEx.getMessage(),
										 ioEx);
		}
		return outCompiledCss;
	}
	/**
	 * A getter used for lazy loading.
	 */
	protected RubySassEngine _getOrCreateRubySASSEngine(final String gemsHome) {
		if (_rubySASSEngine == null) _rubySASSEngine = RubySassEngine.create(gemsHome);
		return _rubySASSEngine;
	}
}