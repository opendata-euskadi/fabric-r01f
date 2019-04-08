package r01f.util.types;

public class Objects {
	/**
	 * Basic equals and type checkings
	 * @param o1
	 * @param o2
	 * @return
	 */
	@SuppressWarnings("null")
	public static boolean sameObjectOrSameType(final Object o1,final Object o2) {
		if (o1 == o2) return true;
		if (o1 == null && o2 == null) return true;
		if (o1 != null && o2 == null) return false;
		if (o1 == null && o2 != null) return false;
		return o1.getClass() == o2.getClass();
	}
	/**
	 * Checks if there's any not null object in the provided collection
	 * @param objects
	 * @return
	 */
	public static boolean isAnyNotNull(final Object... objects) {
		if (objects == null || objects.length == 0) throw new IllegalArgumentException();
		boolean outAnyNotNull = false;
		for (Object obj : objects) {
			if (obj != null) {
				outAnyNotNull = true;
				break;
			}
		}
		return outAnyNotNull;
	}
	/**
	 * Checks if all objects are all null in the provided collection
	 * @param objects
	 * @return
	 */
	public static boolean areAllNull(final Object... objects) {
		return !Objects.isAnyNotNull(objects);
	}
	/**
	 * Checks if all objects are all NOT null in the provided collection
	 * @param objects
	 * @return
	 */
	public static boolean areAllNOTNull(final Object... objects) {
		if (objects == null || objects.length == 0) throw new IllegalArgumentException();
		boolean areAllNotNull = true;
		for (Object obj : objects) {
			if (obj == null) {
				areAllNotNull = false;
				break;
			}
		}
		return areAllNotNull;
	}
}
