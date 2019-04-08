package r01f.service;

/**
 * Interface for service objects that can be enabled/disabled
 */
public interface ServiceCanBeDisabled {
	public boolean isEnabled();
	public boolean isDisabled();
	public void setEnabled();
	public void setDisabled();
}
