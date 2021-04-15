package r01f.filestore.api.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.types.Path;

/**
 * Utils for creating HDFS Configuration
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HDFSConfigurationBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static Configuration hdfsConfigurationFor(final Path hdfsHomeDirPath,final Path configHomePath) {
		if (hdfsHomeDirPath != null) System.setProperty("hadoop.home.dir",hdfsHomeDirPath.asAbsoluteString());	
		
		Path coreSiteXmlFilePath = configHomePath.joinedWith("core-site.xml");
		Path hdfsSiteXmlFilePath = configHomePath.joinedWith("hdfs-site.xml");
		
		Configuration conf = new Configuration();
		conf.addResource(coreSiteXmlFilePath.asRelativeString());
		conf.addResource(hdfsSiteXmlFilePath.asRelativeString());
		conf.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem");
		conf.set("fs.file.impl","org.apache.hadoop.fs.LocalFileSystem");
		
		// log
		log.warn("[hadoop config]: hadoop.home.dir={}",hdfsHomeDirPath);
		log.warn("[hadoop config]: core-site.xml={}",coreSiteXmlFilePath.asRelativeString());
		log.warn("[hadoop config]: hdfs-site.xml={}",hdfsSiteXmlFilePath.asRelativeString());

		// return
		return conf;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AUTH
/////////////////////////////////////////////////////////////////////////////////////////
	public static void hdfsNoCredentialsAuth(final Configuration config,
											 final String hdfsUser) {
		log.warn("[hadoop config]: USER={}",hdfsUser);
		
		// set some env dependent vars
		if (hdfsUser != null) System.setProperty("HADOOP_USER_NAME",hdfsUser);
		
		// create hdfs auth object
		UserGroupInformation noCredentialsAuth = UserGroupInformation.createRemoteUser(hdfsUser);
		UserGroupInformation.setLoginUser(noCredentialsAuth);
		
		// config: set the auth 
		UserGroupInformation.setConfiguration(config);
	}
	public static void hdfsKerberosAuth(final Configuration config,
										final String hdfsUser,final String realm,final String domain,final Path ticketPath) throws IOException {
		log.warn("[hadoop config]: USER={}",hdfsUser);
		log.warn("[hadoop config]: KERBEROS REALM={}",realm);
		log.warn("[hadoop config]: KERBEROS DOMAIN={}",domain);
		log.warn("[hadoop config]: KERBEROS TICKET PATH={}",ticketPath.asAbsoluteString());
		
		// set some env dependent vars
		System.setProperty("java.security.krb5.realm", realm.toUpperCase());
		System.setProperty("java.security.krb5.kdc",domain);
		
		// create the hdfs auth object
		UserGroupInformation.loginUserFromKeytab(hdfsUser,ticketPath.asAbsoluteString());
		
		// config: set the auth
		UserGroupInformation.setConfiguration(config);
		config.set("hadoop.security.authentication","kerberos");
		config.set("dfs.namenode.kerberos.principal.pattern", "nn/*@" + realm.toUpperCase());
	}
}
