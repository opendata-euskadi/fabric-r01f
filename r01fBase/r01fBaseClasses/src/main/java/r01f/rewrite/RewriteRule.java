package r01f.rewrite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.util.types.Strings;

/**
 * Usage
 * <pre class='brush:java'>
 * 		String rewritten = RewriteRule.matching("/(read|write)/triplestore/(.+)")
 * 									  .rewriteTo("/$1/blazegraph/$2")
 * 									  .applyTo("/read/triplestore/sparql");
 * </pre>
 */
@Slf4j
public class RewriteRule { 
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern REPLACE_PATTERN = Pattern.compile("(?:\\$([0-9]+))");
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Pattern _matchingPattern;
	private final String _rewritePattern;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RewriteRule(final Pattern matchingPattern,
					   final String rewritePattern) {
		_matchingPattern = matchingPattern;
		_rewritePattern = rewritePattern;
	}
	public RewriteRule(final String matchingPattern,
					   final String rewritePattern) {
		_matchingPattern = Pattern.compile(matchingPattern);
		_rewritePattern = rewritePattern;
	}
	public static RewriteRuleBuilderToStep matching(final Pattern matchingPattern) {
		return new RewriteRuleBuilderToStep(matchingPattern);
	}
	public static RewriteRuleBuilderToStep matching(final String matchingPattern) {
		return new RewriteRuleBuilderToStep(Pattern.compile(matchingPattern));
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class RewriteRuleBuilderToStep {
		private final Pattern _theMatchingPattern;
		public RewriteRule rewriteTo(final String rewrite) {
			return new RewriteRule(_theMatchingPattern,
								   rewrite);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean matches(final String text) {
		return _matchingPattern.matcher(text)
					   		   .find();
	}
    public String applyTo(final String text) { 
        Matcher m = _matchingPattern.matcher(text);
        if (m.find()) {
        	log.trace("{} matches {}",
        			  text,_matchingPattern);
        	// find $x / replace with capturing groups in matching pattern
			Matcher rm = REPLACE_PATTERN.matcher(_rewritePattern);	// find $d in the rewrite patterns
			
			StringBuffer sb = new StringBuffer();
			
			int max = m.groupCount();	// max number of capturing groups at the matching pattern
			while (rm.find()) {
			    // Avoids throwing a NullPointerException in the case that 
				// there's NO a replacement defined in the map for the match
				Integer index = Integer.parseInt(rm.group(1));
				if (index > max) continue;		// bad group (the matching pattern does NOT have a capturing group with the required index)
				
			    String repString = m.group(index);
			    if (repString != null) rm.appendReplacement(sb,repString);		// replace $x at the rewrite pattern
			}
			rm.appendTail(sb);	// rest of the rewrite pattern
			
			// return
			String outRewritten = sb.toString();
			log.debug("{} rewritten to {} using {}",
					  text,outRewritten,_matchingPattern);
			return outRewritten;
        } else {
        	log.debug("{} does NOT match {}",
        			  text,_matchingPattern);
        	return text;
        }
    } 
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() { 
    	return Strings.customized("Rewrite {} to {}",
    							  _matchingPattern,_rewritePattern);
    }     
}

