package r01f.util.types;

import java.text.DecimalFormat;

public class BinaryUnitsUtils {

//////////////////////////////////////////////////////////////////////////////
// CONSTANTS
//////////////////////////////////////////////////////////////////////////////
    /** Kilobytes */
    public static final int KB = 1024;

    /** Megabytes */
    public static final int MB = 1024 * KB;

    /** Gigabytes */
    public static final long GB = 1024 * MB;

    /** Terabytes */
    public static final long TB = 1024 * GB;
//////////////////////////////////////////////////////////////////////////////
// METHODS
//////////////////////////////////////////////////////////////////////////////
	public static String asHumanReadableSize(long size) {
	    if (size <  KB)                { return Strings.customized("{} byte",_floatForm(size) );};
        if (size >= KB && size < MB)   { return Strings.customized("{} KB",_floatForm((double)size / KB) );};
        if (size >= MB && size < GB)   { return Strings.customized("{} MB",_floatForm((double)size / MB) );};
        if (size >= GB && size < TB  ) { return Strings.customized("{} GB",_floatForm((double)size / GB) );};
        if (size >= TB )               { return Strings.customized("{} TB",_floatForm((double)size / TB) );};;
        return "???";
	}

// METHODS PRIVATE
//////////////////////////////////////////////////////////////////////////////
	private static String _floatForm (double d)   {
	       return new DecimalFormat("#.##").format(d);
	}
}
