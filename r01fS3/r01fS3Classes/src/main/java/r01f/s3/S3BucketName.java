package r01f.s3;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;


@Immutable
@NoArgsConstructor
public class S3BucketName
	 extends OIDBaseMutable<String>
  implements OIDTyped<String>  {

	private static final long serialVersionUID = 4162366466990455545L;

	public S3BucketName(final String id) {
		super(id);
	}
	public static S3BucketName forId(final String idAsString) {
		return new S3BucketName(idAsString);
	}
}