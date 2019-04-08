package r01f.model;

public interface HasParentModelObjectRef<PR extends ModelObjectRef<? extends ModelObject>> {
	public PR getParentRef();
}
