package r01f.util.types;

import java.text.NumberFormat;

import com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
public class Currencies {
	/**
	 * Formats a money ammount
	 * @param money
	 * @return
	 */
	public static String formatMoney(final double money) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(money);
	}
}
