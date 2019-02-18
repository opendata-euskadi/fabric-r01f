package r01f.types.contact;

import r01f.types.geo.GeoPosition;

public interface HasGeoPosition {
	public GeoPosition getGeoPosition();
	public void setGeoPosition(final GeoPosition position);
}
