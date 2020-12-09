package r01f.securitycontext;

import com.google.common.base.Optional;

import r01f.locale.Language;
import r01f.securitycontext.SecurityOIDs.UserOID;

public interface SecurityContextForUser {
	public UserOID getUserOid();
	public Optional<Language> getPrefLang();
}
