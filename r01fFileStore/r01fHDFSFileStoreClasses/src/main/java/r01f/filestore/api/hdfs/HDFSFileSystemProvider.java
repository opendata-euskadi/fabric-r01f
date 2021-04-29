package r01f.filestore.api.hdfs;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import lombok.extern.slf4j.Slf4j;
import r01f.types.TimeLapse;

@Slf4j
class HDFSFileSystemProvider {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Configuration class that stores the Hadoop config needed by FileSystem type.
	 * It loads the core-site and core-default.xml files using the class loader
	 * and keeps Hadoop configuration information such as fs.defaultFS, fs.default.name etc.
	 */
	private final Configuration _hdfsConf;
	 /**
	  * The credentials will be reloaded every X time (ie every 6 hours)
	  * This value is the period (time lapse) for credentials refreshing
	  */
	 private final long _credentialsRefreshPeriodMilis;
	 /**
	  * The last time the credentials were refreshed
	  */
	 private long _lastCredentialsRefreshTimeStamp = 0;
	 /**
	  * The HDFS file system
	  */
	 private FileSystem _fs;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	 HDFSFileSystemProvider(final Configuration conf) {
		 this(conf,
			  null);
	 }
	 HDFSFileSystemProvider(final Configuration conf,
			 				final TimeLapse credentialsRefreshPeriod) {
		_hdfsConf = conf;
		_credentialsRefreshPeriodMilis = credentialsRefreshPeriod != null ? credentialsRefreshPeriod.asMilis()
																		  : -1;
	 }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Configuration class that stores the Hadoop config needed by FileSystem type.
	 * It loads the core-site and core-default.xml files using the class loader
	 * and keeps Hadoop configuration information such as fs.defaultFS, fs.default.name etc.
	 */
	public Configuration getHDFSConfiguration() {
		return _fs.getConf();
	}
	/**
	 * Returns an instance of the HDFS FileSystem
	 * @return
	 */
	public FileSystem getHDFSFileSystem() throws IOException {
		_refreshCredentialsIfExpired();	// check if the credentials have to be refreshed because they've expired
		return _fs;
	}
	/**
	 * Refresh the HDFS FileSystem credentials
	 */
	public synchronized void refreshCredentials() throws IOException {
		log.warn("[hdfs filesystem provider] Refresh credentials!!!!!!");

		// create a new FileSystem
		_fs = FileSystem.get(_hdfsConf);

		// store the credentials
		_lastCredentialsRefreshTimeStamp = new Date().getTime();
	}
	private void _refreshCredentialsIfExpired() throws IOException {
		log.trace("[hdfs filesystem provider] The credentials refresh period in milis is: {}", _credentialsRefreshPeriodMilis);
		if (_fs != null && _credentialsRefreshPeriodMilis <= 0) {
			log.trace("[hdfs filesystem provider] The credentials WILL NOT be refreshed: the refresh timelapse was NOT set!");
			return;
		}
		// if the file system is null, just create it
		if (_fs == null) {
			this.refreshCredentials();
			return;
		}
		// check if the timelapse is over
		long currTimeStamp = new Date().getTime();
		long elapsed = currTimeStamp - _lastCredentialsRefreshTimeStamp;
		if (elapsed > _credentialsRefreshPeriodMilis) {
			log.warn("[hdfs filesystem provider] The credentials WILL be refreshed since the refresh period ({} milis) is over",
					 _credentialsRefreshPeriodMilis);
			this.refreshCredentials();
		} else if (log.isTraceEnabled()) {
			log.trace("[hdfs filesystem provider] The credentials WILL NOT be refreshed since the refresh period ({} milis) is NOT already over",
					 _credentialsRefreshPeriodMilis);
		}
	}
}
