package r01f.types.contact;

import java.io.Serializable;

import lombok.experimental.Accessors;

/**
 * Base type for every {@link ContactInfo} media related object: {@link ContactMail}, {@link ContactPhone}, {@link ContactSocialNetwork}, etc
 * @param <SELF_TYPE>
 */
@Accessors(prefix="_")
public interface ContactMeanData
		 extends Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactInfoUsage getUsage();
	public void setUsage(final ContactInfoUsage usage);

	public String getUsageDetails();
	public void setUsageDetails(final String details);

	public boolean isDefault();
	public void setDefault(boolean def);

	public boolean isPrivate();
	public void setPrivate(final boolean priv);
}
