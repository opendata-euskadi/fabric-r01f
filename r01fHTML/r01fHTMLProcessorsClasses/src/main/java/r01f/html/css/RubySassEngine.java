package r01f.html.css;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Saas engine implementation with the following goodies:
 * <ul>
 * 		<li>Sass compiler</li>
 * 		<li>Compass framework</li>
 * 		<li>Remote importer that allows the @import option to import remote scss files by http</li>
 * <ul>
 * This type is based on:
 * <ul>
 * 		<li>https://code.google.com/p/wro4j/source/browse/wro4j-extensions/src/main/java/ro/isdc/wro/extensions/processor/support/sass/RubySassEngine.java
 * 			where the inspiration for the sass compiler invocation from java was taken</li>
 * 		<li>http://stackoverflow.com/questions/6857988/import-sass-partial-over-http-instead-of-filesystem/6861985#6861985
 * 			where the http importer was found</li>
 * 		<li>https://github.com/joeellis/remote-sass
 * 			where an http importer was compiled as a ruby gem: RemoteImporter</li>
 * </ul>
 * 
 * In order to use sass/compass in java, jRuby is used.
 * There are two ways of using sass/compass GEMS with jRuby:
 * <ol>
 * 		<li>Packaging sass/compass GEMS as JAR files</li>
 * 		<li>Not packaging sass/compas GEMS as JAR files and tell jRuby to use them directly from the fileSystem</li>
 * </ol>
 * Using sass/compass GEMS packaged as JARS from a jRuby embeded in an app server was an issue when running in oracle-weblogic 10.3.6 (jrockit 6) -see http://stackoverflow.com/questions/21478221/use-gems-in-a-jruby-embeded-on-weblogic
 * In contrast, there're NO problems with Tomcat (java 7)... probabilly the problem is with jRuby and JDK version
 * 
 * Using sass/compass GEMS packaged as JARS in an embeded jRuby
 * ============================================================
 * This is done as explained at: http://blog.nicksieger.com/articles/2009/01/10/jruby-1-1-6-gems-in-a-jar/ 
 * (also see http://www.cfelements.com/2013/06/java-sass-compass-scss-solved.html and https://bitbucket.org/twic/jsass/src/f932ccc80b62e62a18e05a8a57490be8498c8a80/bin/package-gem.sh?at=default)
 * <ul>
 * 		<li>Download jruby-complete-xxx.jar from http://jruby.org/download (click at [Download the latest release], not at the direct links)</li>
 * 		<li>Download the GEM:
 * 			<ul>
 * 				<li>Open a command window a configure the java classpath (jruby-complete-xxx.jar is not needed in the classpath)</li>
 * 				<li>To download a ruby gem, the gem util must be used; see <pre>gem help install</pre> for a complete guide on gem installing
 * 					<ul>
 * 						<li>To download a gem and ALL it's dependencies:<pre>gem install -i ./[gem_name] [gem_name] --no-rdoc --no-ri</pre></li>
 * 						<li>To download a gem WITHOUT it's dependencies:<pre>gem install -i ./[gem_name] [gem_name] --ignore-dependencies --no-rdoc --no-ri</pre></li>
 *					</ul>
 *				</li>
 * 			</ul>
 * 		</li>
 * </ul>
 * 
 * Specifically to install compass for the pourpouse of this type:
 * <ol>
 * 		<li>Open a command line, set the java environment and move to the dir where the gems are going to be stored (ie: d:/eclipse/libs/ruby_gems/)</li>
 * 		<li>Download compass gems and ALL it's dependencies:<br />
 * 			<pre>java -jar jruby-complete-1.7.10.jar -S gem install -i ./compass-gems compass --no-rdoc --no-ri</pre>
 * 			This installs compass and also Sass and other dependencies as chunky_png or fssm
 * 			<pre>
 * 				Fetching: sass-3.2.13.gem (100%)
 *				Successfully installed sass-3.2.13
 *				Fetching: chunky_png-1.2.9.gem (100%)
 *				Successfully installed chunky_png-1.2.9
 *				Fetching: fssm-0.2.10.gem (100%)
 *				Successfully installed fssm-0.2.10
 *				Fetching: compass-0.12.2.gem (100%)
 *				Successfully installed compass-0.12.2
 *				4 gems installed
 * 			</pre>
 * 		</li>
 * 		<li>Download remote-sass gems WITHOUT dependencies (compass) since they've been installed previously
 * 			<pre>java -jar jruby-complete-1.7.10.jar -S gem install -i ./remotesass-gems remote-sass --ignore-dependencies --no-rdoc --no-ri</pre>
 * 			This installs remote-sass gem but NO dependency:
 * 			<pre>
 * 				Fetching: remote-sass-0.0.1.gem (100%)
 *				Successfully installed remote-sass-0.0.1
 *				1 gem installed
 * 			</pre>
 * 		</li>
 * </ol>
 * 
 * Once downloaded, in order to use the gems, a JAR file must be created BUT beaware NOT to name the jar file with the same name of the gem
 * Run the following java commands in a command line 
 * <ol>
 * 		<li><pre>jar cf compass-gems.jar -C compass-gems .</pre></li>
 * 		<li><pre>jar cf remotesass-gems.jar -C remotesass-gems .</pre></li>
 * </ol>
 * 
 * Now simply run the script to compile the sass/compass styles:
 * 	    require 'rubygems'
 *	    require 'sass/plugin'
 *	    require 'sass/engine'
 *	    source = '...the scss code....'
 *	    engine = Sass::Engine.new(source,{ :syntax => :scss, 
 *	    								   :compass => {:css_dir => '/styles',:js_dir => '/scripts',images_dir => '/images'},
 *										   :load_paths => {'d:/styles/framewors/} })
 *	    result = engine.render
 * 
 *      IMPORTANT!!!
 *      -----------
 *      Compass and other used libraries MUST be accessible in the filesystem (NOT inside JARs) since JRuby cannot
 *      do directory listings in a jar so in order for this Saas compiler to work the framework files and directories 
 *      MUST be accesible in a framework home dir] in the filesystem. To do so:
 *      <ol>
 *      	<li>Copy the [compass-gem]/gems/compass/frameworks/compass/stylesheets/ contents to the framework home dir so it contains:<br />
 *      		<pre>
 *      			[framework home dir]
 *      				|_ [compass]
 *      				|_ _compass.scss
 *      				|_ _lemonade.scss
 *      		</pre> 
 *      	</li>
 *      	<li>Set the JRuby working directory either:<br />
 *      		<ul>
 *      			<li>Setting the Sass engine :load_paths option in the compile script</li>
 *      			<li>Including <pre>Dir.chdir('[framework home dir]')</pre> in the ruby compile script</li>
 *      			<li>Setting 
 *      					<pre class='brush:java'>
 *      						_rubyEngine = new ScriptingContainer(LocalContextScope.CONCURRENT);
 *      						_rubyEngine.setCurrentDirectory([framework home dir])
 *      					</pre>
 *      				when initializing the ruby engine
 *      			</li>
 *      		</ul>
 *      	</li>
 *      </ol>
 *  
 * Using sass/compass GEMS packaged as JARS in an embeded jRuby
 * ============================================================
 * This is the recomended way of using sass/compass when running inside an application manager embeded jruby (i.e oracle-weblogic 10.3.6)
 * This time the GEMs are loaded by jRuby directly from the file system.
 * To make this work the paths where jRuby is going to look for the GEMs MUST be provided using ruby's LOAD_PATH
 * Supposing all the gems resides at: d:/ruby/gems, the paths will be:
 * <ul>
 * 		<li>Sass     : d:/ruby/gems/sass-3.2.13/lib</li>
 * 		<li>compass  : d:/ruby/gems/compass-0.12.2/lib</li>
 * 		<li>ChunkyPNG: d:/ruby/gems/chunky_png-1.2.9/lib</li>
 * 		<li>Fssm     : d:/ruby/gems/fssm-0.2.10/lib</li>
 * </ul>
 * There're two ways of telling Ruby the LOAD_PATH:
 * <ol>
 * 		<li>Providing the load path to the ScriptingContainer:
 * 			<pre class='brush:java'>
 * 				ScriptingContainer rubyEngine = new ScriptingContainer(LocalContextScope.CONCURRENT);
 * 				rubyEngine.setLoadPaths(Lists.newArrayList("d:/ruby/gems/sass-3.2.13/lib",
 * 														   "d:/ruby/gems/compass-0.12.2/lib",
 * 														   "d:/ruby/gems/chunky_png-1.2.9/lib",
 * 														   "d:/ruby/gems/fssm-0.2.10/lib"));
 * 			</pre>
 * 		</li>
 * 		<li>Setting the load path at the Ruby script:
 * 			<pre class='brush:ruby'>
 * 				sassGemDir = 'd:/ruby/gems/sass-3.2.13/lib'
 * 				compassGemDir = 'd:/ruby/gems/compass-0.12.2/lib'
 * 				chunkyPngGemDir = 'd:/ruby/gems/chunky_png-1.2.9/lib'
 * 				fssmGemDir = 'd:/ruby/gems/fssm-0.2.10/lib'
 * 				$LOAD_PATH.insert(0,sassGemDir,compassGemDir,chunkyPngGemDir,fssmGemDir)
 * 				... the rest of the ruby script ...
 * 			</pre>
 * 		</li>
 * </ol>
 * NOTE: many times only the second way -setting the load path at the ruby script- is the only way to make it work
 * 
 * IMPORTANT NOTE when running jRuby embedded in a Weblogic Server
 * ===============================================================
 * Weblogic brings it's own joda time library and so does jRuby.
 * In order to jRuby load it's own joda time library is important to add this to the EAR's weblogic-application.xml file
 * <pre class='brush:xml'>
 * 		<wls:prefer-application-packages>
 *   		<wls:package-name>org.joda.time.*</wls:package-name>
 * 		</wls:prefer-application-packages>
 * </pre>
 * and this to the WAR's weblogic.xml file:
 * <pre class='brush:xml'>
 *   	<wls:container-descriptor>
 *       	<wls:prefer-web-inf-classes>true</wls:prefer-web-inf-classes>
 *       	<wls:show-archived-real-path-enabled>true</wls:show-archived-real-path-enabled>
 *   	</wls:container-descriptor>
 * </pre>
 * 
 * 
 * How Sass imports (@imports) are resolved:
 * ========================================
 * <ul>
 *  	<li>By default Sass uses a FileSystem importer that looks for files in the local file system<br />
 *  		Reading the @import directive documentation at http://sass-lang.com/documentation/file.SASS_REFERENCE.html#import
 *  		there are some facts to be aware about the file system importer behavior:
 *  		<ul>
 *  			<li>The imported file extension is optional so for @import "foo" the fileSystem importer tries to load foo.scss, foo.sass and foo without extension
 *  				(this behavior seems NOT to be enforced by the fileSystem importer)</li>
 *  			<li>For partials, if @import "foo" is done, the fileSystem importer tries to load _foo.scss or _foo.sass
 *  				(this behavior seems to be enforced by the fileSystem importer)</li>
 *  		</ul>
 *  		So for an import like @import "foo", the fileSystem importer tries foo.scss, foo.sass, foo, _foo.scss, _foo.sass<br />
 *  		IMPORTANT: The directory where the fileSystem importer loads the files can be set with
 *  		<ol>
 *  			<li>(preferred) set the load_paths sass option</li>
 *  			<li>Set the ruby engine work dir: <pre class='brush:java'>rubyEngine.setCurrentDirectory(_frameworkHomeDir)</pre></li>
 *  			<li>At the beginning of ruby script change the dir with: <pre class='brush:ruby'>Dir.chdir(frameworkHomeDir)</pre></li>
 *  		</ol>
 *  	</li>
 *  	<li>The importers can be chained, so if one importer (ej: the fileSystem importer) does NOT load the file, the request is handed 
 *  		to the next importer in the chain<br />
 *  		The importers chain can be set:
 *  		<ul>
 *  			<li>using the :load_paths array  Sass engine option (http://sass-lang.com/documentation/file.SASS_REFERENCE.html#load_paths-option)
 *  				Note that this option can contain both a path for the fileSystem importer to look for the files and custom importers (subclasses of Sass::Importers::Base)
 *  				<pre>Sass.load_paths << Sass::Importers::MyCustomImporter.new()</pre>
 *  			</li>
 *  			<li>If the importer is a GEM, the load_paths option can be set at the gem init (look at remote-sass.rb file in remote-sass gem)</li>
 *  		</ul>
 *  		Here we're chaining the fileSystem importer with a custom remote (http) importer.<br />
 *  		When a resource can not be resolved by the fileSystem importer (remember that the fileSystem importer for an @import "foo" tries foo.scss, foo.sass, foo, _foo.scss and _foo.sass)
 *  		the resource is handed to the remote importer so it's given the oportunity to resolve @import "foo"<br />
 *  		IMPORTANT<br/>
 *  		The current implementation of remote importer DOES NOT have the same logic as the fileSystem importer, so for an @import "foo", 
 *  		the remote importer is ONLY asked to load "foo.scss", "foo.sass" and "foo", BUT NOT "_foo.scss", "_foo.sass"
 *  		This should be implemented in the remote importer to fully support partials.
 * 		</li>
 * </ul>
 */
@Slf4j
class RubySassEngine {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static Pattern GEM_NAME_PATTERN = Pattern.compile("([a-z_]+)-(.+)");
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The ruby engine... this should be initialized ONLY once
	 */
	private ScriptingContainer _rubyEngine;
	private final Map<String,String> _gems;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor from the path for the sass/compass gems
	 * This path can be:
	 * <ul>
	 * 		<li>If the gems are stored in a jar: classpath:[path_to_the_jar]/[gems_container_jar].jar![path_within_the_jar_to_the_gems]
	 * 			ie: classpath:gems.jar!gems</li>
	 * 		<li>If the gems are stored in the file system: [path_to_the_gems_containing_dir]
	 * 			ie: D:/eclipse/libs/ruby-gems/compass-gems/gems/</li>
	 * </ul>
	 * @param gemsHome the dir 
	 */
	public RubySassEngine(final String gemsHome) {
		_gems = _initGemsPaths(gemsHome);
	}
	public static RubySassEngine create(final String gemsHome) {
		return new RubySassEngine(gemsHome);
	}
	/**
	 * Init gems by inspecting the contents of the gemsHome dir
	 * @param gemsHome
	 * @return
	 */
	private static Map<String,String> _initGemsPaths(final String gemsHome) {
		String gemsRoot = gemsHome;		// classpath:/gems/  -- D:/eclipse/libs/ruby-gems/compass-gems/
		
		// Read the gemsHome dir contents to get the versions of the gems
		File gemsRootDir = new File(gemsRoot);
		if (!gemsRootDir.exists()) throw new IllegalArgumentException("The gems root dir " + gemsRoot + " does NOT exists!!");		
		File[] gems = gemsRootDir.listFiles(new FileFilter() {
													@Override
													public boolean accept(final File file) {
														return file.isDirectory();
													}
											});
		if (CollectionUtils.isNullOrEmpty(gems)) throw new IllegalArgumentException("The gems root dir " + gemsRoot + " does NOT contains all the required gems: sass, remoteSass, compass, chunkyPng and fssm");
	
		// Create a map with all gems paths indexed by gem name
		Map<String,String> outPaths = Maps.newHashMapWithExpectedSize(gems.length);
		for (File gem : gems) {
			Matcher matcher = GEM_NAME_PATTERN.matcher(gem.getName());
			String gemName = matcher.matches() ? matcher.group(1)
											   : null;
			if (gemName == null) {
				log.warn("ignoring gem dir " + gem.getName() + " at " + gemsRoot + " since it does NOT have the required name format ([a-z]+)_(.+))");
				continue;
			}
			String gemDirPath = new Path(gemsRoot,gem.getName(),"lib").asString();	// ie: gemsRoot + "sass-3.2.14/lib"
			log.warn("{} gem found at {}",gemName,gemDirPath);
			outPaths.put(gemName,gemDirPath);
		}
		// Check all the required gems existence
		if (!outPaths.containsKey("sass")) throw new IllegalArgumentException("The gems root dir " + gemsRoot + " does NOT contains the Sass gem!!");
		if (!outPaths.containsKey("remote_sass")) throw new IllegalArgumentException("The gems root dir " + gemsRoot + " does NOT contains the remote_sass gem!!");
		if (!outPaths.containsKey("compass")) throw new IllegalArgumentException("The gems root dir " + gemsRoot + " does NOT contains the compass gem!!");
		if (!outPaths.containsKey("chunky_png")) throw new IllegalArgumentException("The gems root dir " + gemsRoot + " does NOT contains the chunky_png gem!!");
		if (!outPaths.containsKey("fssm")) throw new IllegalArgumentException("The gems root dir " + gemsRoot + " does NOT contains the fssm gem!!");
		
		return outPaths;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PROCESS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Transforms a sass content into css 
	 * @param sassCss
	 * @return
	 * @throws ScriptException
	 */
	public String process(final String sassCss) throws ScriptException {
		return this.process(null,		// no sass options... the default options will be used
							sassCss);
	}
	/**
	 * Transforms a sass content into css 
	 * @param sassOptions sass framework options
	 * @param sassCss the Sass content to process.
	 */
	public String process(final SassOptions sassOptions,
						  final String sassCss) throws ScriptException {
		if (Strings.isNullOrEmpty(sassCss)) return Strings.EMPTY;
		
		SassOptions theSassOptions = sassOptions == null ? SassOptions.DEFAULT_OPTIONS	
														 : sassOptions;
		
		String scriptTemplate = _buildCompileScript(theSassOptions);		
		String theSassToBeCompiled = sassCss.replace("'","\"");
		String theScript = scriptTemplate.replace("${SOURCE_SASS}",
												  theSassToBeCompiled);
		log.info(theScript);
		System.out.println(theScript);
		ScriptingContainer rubyEngine = _getOrCreateRubyEngine();
		String compiledCSS = rubyEngine.runScriptlet(theScript)
								   	   .toString();
		return compiledCSS;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the previously created ruby engine or creates a new one if it's not been created
	 * @return
	 * @throws ScriptException
	 */
	private ScriptingContainer _getOrCreateRubyEngine() throws ScriptException {
		if (_rubyEngine == null) {
			// Create the scripting engine
			_rubyEngine = new ScriptingContainer(LocalContextScope.CONCURRENT);
			// Set the load path
			if (CollectionUtils.hasData(_gems)) _rubyEngine.setLoadPaths(Lists.newArrayList(_gems.values()));
			// _rubyEngine.setClassLoader(RubySassEngine.class.getClassLoader());
	
			// Another way of running a ruby script could be:
			// ScriptEngine _rubyEngine = new ScriptEngineManager().getEngineByName("jruby");
		}
		if (_rubyEngine == null) throw new ScriptException("Cannot create the jRuby engine. Check that the required libraries are available at runtime!");
		return _rubyEngine;
	}
  	private String _buildCompileScript(final SassOptions sassOptions) {
		// See [Using Sass in ruby code] at http://sass-lang.com/docs/yardoc/file.SASS_REFERENCE.html
		// The following ruby script is built:
  		//		# Set the LOAD_PATH
  		//		sassGemDir = 'd:/ruby/gems/sass-3.2.13/lib'
  		//		remoteSassGemDir = 'd:/ruby/gems/remote-sass-0.0.1/lib'
  		//		compassGemDir = 'd:/ruby/gems/compass-0.12.2/lib'
  		//		chunkyPngGemDir = 'd:/ruby/gems/chunky_png-1.2.9/lib' 
  		//		fssmGemDir = 'd:/ruby/gems/fssm-0.2.10/lib'
  		//		$LOAD_PATH.insert(0,sassGemDir,remoteSassGemDir,compassGemDir,chunkyPngGemDir,fssmGemDir)
  		//
  		//		# Call the engine
		//		require 'rubygems'
		//		require 'sass/plugin'
		//		require 'sass/engine'
		//		source = '...the scss code....'
		//		engine = Sass::Engine.new(source,{ :syntax => :scss, 
		//										   :compass => {:css_dir => '/styles',:js_dir => '/scripts',images_dir => '/images'},
	  	//										   :load_paths => {'d:/styles/framewors/} })
		//		result = engine.render
		
		final StringWriter raw = new StringWriter();
		final PrintWriter rubyScript = new PrintWriter(raw);
		
		// 1 - $LOAD_PATH
		String loadPath = _buildLoadPath();
		if (Strings.isNOTNullOrEmpty(loadPath)) rubyScript.println(loadPath);
		
		// 2 - Requires
		rubyScript.println(_buildRequiredRubyGems(sassOptions));
		
		// 3 - The source Sass styles to be compiled
		rubyScript.println("source = '${SOURCE_SASS}'");
		
		// 4 - The remote location where @imports NOT resolved locally are going to be looked for
		if (Strings.isNOTNullOrEmpty(sassOptions.getRemoteResourcesLocation())) {
			log.warn("@import will be tried ,o be resolved locally and if it's not possible, they'll be resolved remotelly at {}",sassOptions.getRemoteResourcesLocation());
			rubyScript.println(Strings.customized("RemoteSass.location = \"{}\"",
									  			  sassOptions.getRemoteResourcesLocation()));
		} else {
			log.warn("ALL @import will be resolved locally");
		}
		// 5 - Set the options > Sass Engine Options see http://sass-lang.com/documentation/file.SASS_REFERENCE.html#options
	    Map<String,String> rubyOptionsMap = _buildOptions(sassOptions);
	    
		// 6 - Sass engine invocation
		rubyScript.println("engine = Sass::Engine.new(source," + _buildRubyOptionsMapFor(rubyOptionsMap) + ")");
		rubyScript.println("result = engine.render");
		
		if (log.isDebugEnabled()) _debugRubyEnvironment(rubyScript);
		
		rubyScript.flush();
		
		// [4] - Return
		String outRubyScript = raw.toString();
		return outRubyScript;
  	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LOAD_PATH
/////////////////////////////////////////////////////////////////////////////////////////
  	/**
  	 * Builds the $LOAD_PATH 
  	 * @return
  	 */
  	private String _buildLoadPath() {
  		String outLoadPath = null;
  		if (CollectionUtils.hasData(_gems)) {
  			outLoadPath = Strings.customized("sassGemDir = '{}'\n" +
	    				  			 		 "remoteSassGemDir = '{}'\n" +
	    				  			 		 "compassGemDir = '{}'\n" +
	    				  			 		 "chunkyPngGemDir = '{}'\n" + 
	    				  			 		 "fssmGemDir = '{}'\n" + 
	     			  	  					 "$LOAD_PATH.insert(0,sassGemDir,remoteSassGemDir,compassGemDir,chunkyPngGemDir,fssmGemDir)\n\n",
	     			  	  					 _gems.get("sassGem"),
	     			  	  				 	 _gems.get("remoteSassGem"),
	     			  	  				 	 _gems.get("compassGem"),
	     			  	  				 	 _gems.get("chunkyPngGem"),
	     			  	  				 	 _gems.get("fssmGem"));
  		}
  		return outLoadPath;
  	}
/////////////////////////////////////////////////////////////////////////////////////////
//  REQUIRES
/////////////////////////////////////////////////////////////////////////////////////////
  	/**
  	 * Builds the ruby requires:
	 *		require 'rubygems'
	 *		require 'sass/plugin'
	 *		require 'sass/engine'
  	 * @return
  	 */
  	private static String _buildRequiredRubyGems(final SassOptions options) {  		
	    LinkedHashSet<String> rubyRequires = new LinkedHashSet<String>();
	    _addRequiredRubyGem("rubygems",rubyRequires);
	    _addRequiredRubyGem("sass/plugin",rubyRequires);
	    _addRequiredRubyGem("sass/engine",rubyRequires);
	    if (options != null && Strings.isNOTNullOrEmpty(options.getRemoteResourcesLocation())) _addRequiredRubyGem("remote-sass",rubyRequires);
	    if (options != null) _addRequiredRubyGem("compass",rubyRequires);
	    
		if (CollectionUtils.isNullOrEmpty(rubyRequires)) return Strings.EMPTY;
		
		StringBuilder outSb = new StringBuilder(rubyRequires.size()*10);
	    for (String requiredRubyGem : rubyRequires) {
	      outSb.append("require '").append(requiredRubyGem).append("'\n");
	    }
	    return outSb.toString();
  	}
	/**
	 * Adds a ruby require to the ruby script to be run by this RubySassEngine. It's safe to add the same require twice.
	 * @param requiredRubyGem The name of the require, e.g. bourbon, compass
	 * @param rubyRequires 
	 */
	private static void _addRequiredRubyGem(final String requiredRubyGem,
											final LinkedHashSet<String> rubyRequires) {
		if (Strings.isNOTNullOrEmpty(requiredRubyGem)) {
			rubyRequires.add(requiredRubyGem.trim());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OPTIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builds a {@link Map} with every sass/compass option indexed by name
	 * @param sassOptions
	 * @return
	 */
	private static Map<String,String> _buildOptions(final SassOptions sassOptions) {
	    Map<String,String> outOptions = new HashMap<String,String>();	
	    _addSassOption("syntax",":scss",		 
	    			   outOptions);	
	    _addSassOption("style",":compressed",	// see http://sass-lang.com/documentation/file.SASS_REFERENCE.html#output_style
	    			   outOptions);
	    if (sassOptions != null) {
			// Adds a Sass compiler option for the compass framework options:
			//		:compass => {:css_dir => '/styles',:js_dir => '/scripts',images_dir => '/images'}
			_addSassOption("compass",_buildCompassRuybOptionsMap(sassOptions),
						   outOptions);
	    }
	    if (sassOptions != null && Strings.isNOTNullOrEmpty(sassOptions.getFrameworksLocalHomeDir())) {
	    	// load_paths option is an array of filesystem paths or importers which should be searched for Sass templates imported with the @import directive
	    	// this is not strictly necessary if the working dir has been changed either during ruby engine inicialization or with a Dir.chdir('workDir') command
			log.warn("The framework home dir is {} so imported resources will be looked for there by the filesystem importer",
					 sassOptions.getFrameworksLocalHomeDir());
	    	_addSassOption("load_paths",Strings.customized("['{}']",
	    									   			   sassOptions.getFrameworksLocalHomeDir()),
	    				   outOptions);
	    } else {
			log.warn("The framework home dir is NOT set so framework resources will be locally imported by Sass FileSystem importer at {}",
					 new File(".").getAbsolutePath());	// the default working dir
	    }
	    return outOptions;
	}
  	/**
  	 * Builds the Sass engine options as:
  	 * 		{:syntax => :scss,:css_dir => '/styles',:js_dir => '/scripts',images_dir => '/images'}  
  	 * @param options
  	 * @return
  	 */
  	private static String _buildCompassRuybOptionsMap(final SassOptions options) {
  		Map<String,String> compassOptionsMap = new HashMap<String,String>(3);
  		compassOptionsMap.put("css_dir",Strings.quote(options.getWebServerDirForStylesheets()));
  		compassOptionsMap.put("js_dir",Strings.quote(options.getWebServerDirForScripts()));
  		compassOptionsMap.put("images_dir",Strings.quote(options.getWebServerDirForImages()));
  		
  		return _buildRubyOptionsMapFor(compassOptionsMap);
  	}
  	private static String _buildRubyOptionsMapFor(final Map<String,String> compassOptionsMap) {
  		String outOpts = "{}";
		StringBuilder optsSB = new StringBuilder(compassOptionsMap.size() * 15);
		optsSB.append("{");
		for (Iterator<Map.Entry<String,String>> optIt = compassOptionsMap.entrySet().iterator(); optIt.hasNext(); ) {
			Map.Entry<String,String> opt = optIt.next();
			String optName = opt.getKey();
			String optValue = opt.getValue();
			optsSB.append(":").append(optName).append(" => ").append(optValue);
			if (optIt.hasNext()) optsSB.append(",");
		}
		optsSB.append("}");
		outOpts = optsSB.toString();
  		return outOpts;
  	}
	/**
	 * Adds a sass compiler option
	 * This will be later translated to optName => optValue
	 * ... then compiled with other options in a map like { opt1Name => opt1Value, opt2Name => opt2Value, ...}
	 * ... and finally provided to the Sass engine
	 * @param optName
	 * @param optValue
	 * @param sassOptions
	 */
	private static void _addSassOption(final String optName,final String optValue,
								  	   final Map<String,String> sassOptions) {
		if (Strings.isNOTNullOrEmpty(optName) && Strings.isNOTNullOrEmpty(optValue)) {
			sassOptions.put(optName,optValue);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
  	/**
  	 * Simply debugs info
  	 * @param script
  	 */
  	private static void _debugRubyEnvironment(final PrintWriter script) {
		script.println("dir_contents = Dir.entries(Dir.pwd)    ");
		script.println("puts dir_contents   ");
		script.println("puts '--classpath--'   ");
		script.println("puts $:   ");
		script.println("puts '--classpath--'   ");
		script.println("puts '--working dir--'  ");
		script.println("puts Dir.pwd  ");
		script.println("puts '--working dir--'  ");
  	}
}
