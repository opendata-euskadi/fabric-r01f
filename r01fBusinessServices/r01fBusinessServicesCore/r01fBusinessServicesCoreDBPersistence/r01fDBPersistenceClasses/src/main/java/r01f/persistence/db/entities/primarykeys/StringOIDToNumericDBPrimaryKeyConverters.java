package r01f.persistence.db.entities.primarykeys;

import java.math.BigDecimal;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class StringOIDToNumericDBPrimaryKeyConverters {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class StringOIDToNumericDBPrimaryKeyConverterBase 
	  				  implements Converter {
	
		private static final long serialVersionUID = 1024711815523936318L;
	
		abstract Number _convertObjectValueToDataValue(final String objectValue);
		abstract String _convertDataValueToObjectValue(final Number dataValue);
		
		@Override
		public Object convertObjectValueToDataValue(final Object objectValue,
													final Session session) {
			return null;
		}
		@Override
		public Object convertDataValueToObjectValue(final Object dataValue,
													final Session session) {
			return null;
		}
		@Override
		public boolean isMutable() {
			return false;
		}
		@Override
		public void initialize(final DatabaseMapping mapping, 
							   final Session session) {
			// nothing
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static class StringOIDToIntegerDBPrimaryKeyConverter 
		 		extends StringOIDToNumericDBPrimaryKeyConverterBase {
		private static final long serialVersionUID = 2197283221725394412L;
		
		@Override
		Integer _convertObjectValueToDataValue(final String objectValue) {
			return Integer.parseInt(objectValue);
		}
		@Override
		String _convertDataValueToObjectValue(final Number dataValue) {
			return Integer.toString(dataValue.intValue());
		}
	}
	public static class StringOIDToLongDBPrimaryKeyConverter 
		 		extends StringOIDToNumericDBPrimaryKeyConverterBase {
		private static final long serialVersionUID = -1884047380706747392L;
		
		@Override
		Long _convertObjectValueToDataValue(final String objectValue) {
			return Long.parseLong(objectValue);
		}
		@Override
		String _convertDataValueToObjectValue(final Number dataValue) {
			return Long.toString(dataValue.longValue());
		}
	}
	public static class StringOIDToDoubleDBPrimaryKeyConverter 
		 		extends StringOIDToNumericDBPrimaryKeyConverterBase {
		private static final long serialVersionUID = 7331068275403476145L;
		
		@Override
		Double _convertObjectValueToDataValue(final String objectValue) {
			return Double.parseDouble(objectValue);
		}
		@Override
		String _convertDataValueToObjectValue(final Number dataValue) {
			return Double.toString(dataValue.doubleValue());
		}
	}
	public static class StringOIDToBigDecimalDBPrimaryKeyConverter 
		 		extends StringOIDToNumericDBPrimaryKeyConverterBase {
		private static final long serialVersionUID = 6976908549483694166L;
		
		@Override
		BigDecimal _convertObjectValueToDataValue(final String objectValue) {
			return new BigDecimal(objectValue);
		}
		@Override
		String _convertDataValueToObjectValue(final Number dataValue) {
			return dataValue.toString();
		}
	}
}
