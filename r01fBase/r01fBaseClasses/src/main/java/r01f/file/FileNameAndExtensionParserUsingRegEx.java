package r01f.file;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;

import r01f.util.types.Strings;

@GwtIncompatible		// not gwt-compatible BUT can be emulated
public class FileNameAndExtensionParserUsingRegEx
  implements FileNameAndExtensionParser {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String[] parseFileNameAndExtension(final String fileName) {
		String[] outFileNameAndExtension = new String[2];
		if (Strings.isNOTNullOrEmpty(fileName)) {
			Pattern p = Pattern.compile("^(.*?)\\.?([^.]*?)$");		// FIXME it does not work when the filename contains two dots: ie: aaa.txt.aaaa.tmp
			Matcher m = p.matcher(fileName);
			if (m.matches()) {
				if (Strings.isNOTNullOrEmpty(m.group(1))) {
					outFileNameAndExtension[0] = m.group(1);
					outFileNameAndExtension[1] = m.group(2);
				} else {
					outFileNameAndExtension[0] = m.group(2);
				}
			}
		}
		return outFileNameAndExtension;
	}
}