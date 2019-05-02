package r01f.s3.model;

import java.io.File;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.s3.S3BucketName;
import r01f.s3.S3ObjectKey;


@NoArgsConstructor
@Accessors(prefix="_")
public abstract class RequestBase {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter S3BucketName _bucketName;
	@Getter @Setter S3ObjectKey _key;
	@Getter @Setter File _file;
	@Getter @Setter OperationSettings _operationSettings;
}
