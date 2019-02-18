package r01f.types;

public interface HasPath<P extends IsPath> {
	public P getPath();
	public void setPath(final P path);
}
