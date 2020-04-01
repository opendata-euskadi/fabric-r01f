package r01f.httpclient;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Collection;

import com.google.common.collect.Lists;

import r01f.patterns.Memoized;
import r01f.patterns.Supplier;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.types.Path;

/**
 * A list of Top Level Domains (see http://www.iana.org/domains/root/db and http://data.iana.org/TLD/tlds-alpha-by-domain.txt)
 */
public abstract class TLDs {
	private static final Memoized<Collection<String>> TLDS = Memoized.using(new Supplier<Collection<String>>() {
																				@Override
																				public Collection<String> supply() {
																					Collection<String> outList = Lists.newArrayListWithExpectedSize(1370);
																					Reader r = null;
																					BufferedReader br = null;
																					try {
																						ResourcesLoader resLoader = ResourcesLoaderBuilder.createDefaultResourcesLoader();
																						r = resLoader.getReader(Path.from("tlds.list"));
																						br = new BufferedReader(r);
																						String tld = null;
																						do {
																							tld = br.readLine();
																							if (tld != null) outList.add(tld);
																						} while (tld != null);
																						
																					} catch (Throwable th) {
																						th.printStackTrace(System.out);
																					} finally {
																						try {
																							if (r != null) r.close();
																							if (br != null) br.close();
																						} catch (Throwable th) {
																							/* ignored */
																						}
																					}
																					return outList;
																				}
																			});
	public static boolean existsTLD(final String tld) {
		return TLDS.get()
				   .contains(tld.toUpperCase());		// BEWARE!! this can be time-consuming since the list has 1400 items
	}
}
