package r01f.httpclient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class HttpTargetServerAuth {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final HttpTargetServerAuthType _type;
	@Getter protected final UserCode _user;		// target server auth user code
	@Getter protected final Password _password;	// target server auth password
	
}
