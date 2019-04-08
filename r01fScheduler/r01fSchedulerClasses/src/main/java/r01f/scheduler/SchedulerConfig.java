package r01f.scheduler;

import java.util.Map.Entry;
import java.util.Iterator;
import java.util.Properties;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.debug.Debuggable;
import r01f.scheduler.SchedulerIDs.SchedulerID;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class SchedulerConfig 
  implements ContainsConfigData,
  			 Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final SchedulerID _schedulerId;
	@Getter private final boolean _enabled;
	@Getter private final Properties _schedulerProperties;
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(_schedulerId).append(" enabled=").append(_enabled).append("\n");
		if (CollectionUtils.hasData(_schedulerProperties)) {
			for (Iterator<Entry<Object,Object>> entryIt = _schedulerProperties.entrySet().iterator(); entryIt.hasNext(); ) {
				Entry<Object,Object> entry = entryIt.next();
				sb.append("\t- ").append(entry.getKey()).append("=").append(entry.getValue());
				if (entryIt.hasNext()) sb.append("\n");
			}
		}
		return sb;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public SchedulerConfig(final SchedulerID id,
						   final boolean enabled,
						   final Properties schProps) {
		_enabled = enabled;
		_schedulerId = id;
		_schedulerProperties = schProps;
	}
	public static SchedulerConfig from(final String rootXPath,
									   final XMLPropertiesForAppComponent xmlProps) {
		boolean enabled = xmlProps.propertyAt(Strings.customized("{}/scheduler/@enabled",rootXPath))
								  .asBoolean(true);
		SchedulerID schedulerId = xmlProps.propertyAt(Strings.customized("{}/scheduler/@id",rootXPath))
										  .asOID(SchedulerID.class,
												 SchedulerID.forId("unknown"));
		Properties schProps = _propertiesFrom(xmlProps, 
											 Strings.customized("{}/scheduler[@id='{}']/quartz/",rootXPath, schedulerId.getId()));
		return new SchedulerConfig(schedulerId,
								   enabled,
								   schProps);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private static Properties _propertiesFrom(final XMLPropertiesForAppComponent props, final String rootXPath) {
        
		
		// QUARTZ CONFIG
        // =============================
        Properties outProps = new Properties();
        
        // Name & id used to distinguish between scheduler instances
        outProps.setProperty("org.quartz.scheduler.instanceName",
        					 props.propertyAt(Strings.customized("{}org.quartz.scheduler.instanceName",rootXPath))
        						   .asString("AB72QuartzScheduler"));
		outProps.setProperty("org.quartz.scheduler.instanceId",
							 props.propertyAt(Strings.customized("{}org.quartz.scheduler.instanceId",rootXPath))
							 	.asString("AUTO"));
		//Whether or not to skip running a quick web request to determine if there is an updated version of Quartz available for download. 
		outProps.setProperty("org.quartz.scheduler.skipUpdateCheck",
							 Boolean.toString(props.propertyAt(Strings.customized("{}org.quartz.scheduler.skipUpdateCheck",rootXPath))
								   .asBoolean(true)));
		
		// Time (ms) that the scheduler uses BEFORE quering for available triggers
		// Use only with XA transactions (the recommended value is > 5000 ms)
		// schedulerProperties.setProperty("org.quartz.scheduler.idleWaitTime","");
		
		// Time (ms) that the scheduler waits between retrying to access the job store
		outProps.setProperty("org.quartz.scheduler.dbFailureRetryInterval",
							 Long.toString(props.propertyAt(Strings.customized("{}org.quartz.scheduler.dbFailureRetryInterval",rootXPath))
												 .asLong(10000)));
		
		// Max number of triggers that the scheduler allows
		outProps.setProperty("org.quartz.scheduler.batchTriggerAcquisitionMaxCount",
							 Integer.toString(props.propertyAt(Strings.customized("{}org.quartz.scheduler.batchTriggerAcquisitionMaxCount",rootXPath))
											  		.asInteger(1)));
		
		// Set key / value at the context 
		// schedulerProperties.setProperty("org.quartz.context.key.SOME_KEY", "");
		

		// THREAD POOL CONFIG
		// ===============================
		// Thread pool name
		outProps.setProperty("org.quartz.threadPool.class",
							 props.propertyAt(Strings.customized("{}org.quartz.threadPool.class",rootXPath))
								   .asType(org.quartz.simpl.SimpleThreadPool.class)
								   .getName());
		// Number of threads (usually between 1 and 100)
		outProps.setProperty("org.quartz.threadPool.threadCount",
							 Integer.toString(props.propertyAt(Strings.customized("{}org.quartz.scheduler.batchTriggerAcquisitionMaxCount",rootXPath))
									 				.asInteger(10)));
		
		// Thread priority ( between Thread.MIN_PRIORITY (which is 1) and Thread.MAX_PRIORITY (which is 10)
		outProps.setProperty("org.quartz.threadPool.threadCount",
							 Integer.toString(props.propertyAt(Strings.customized("{}org.quartz.scheduler.batchTriggerAcquisitionMaxCount",rootXPath))
									 				.asInteger(1)));
		
		// true if the main scheduler thread is a daemon thread
		outProps.setProperty("org.quartz.scheduler.makeSchedulerThreadDaemon",
							 Boolean.toString(props.propertyAt(Strings.customized("{}org.quartz.scheduler.makeSchedulerThreadDaemon",rootXPath))
									 				.asBoolean(false)));
		
		// true if the thread pool's thread are daemon threads
		outProps.setProperty("org.quartz.threadPool.makeThreadsDaemons",
							 Boolean.toString(props.propertyAt(Strings.customized("{}quartz/org.quartz.threadPool.makeThreadsDaemons",rootXPath))
								  		  			.asBoolean(false)));

		// JOB STORE 
		// ==============================
		// if a trigger was set to be executed at a certain moment and the time is exceeded the trigger is considered failed
		outProps.setProperty("org.quartz.jobStore.misfireThreshold",
							 Long.toString(props.propertyAt(Strings.customized("{}org.quartz.jobStore.misfireThreshold",rootXPath))
									  			 .asLong(60)));
		//Job store 
		//How to store the scheduler data
		//Default to RAM storage
		String className =props.propertyAt(Strings.customized("{}org.quartz.jobStore.class",rootXPath))
									  			 .asString("org.quartz.simpl.RAMJobStore");
		outProps.setProperty("org.quartz.jobStore.class",
							 className);
		//JDBC-JobStore
		//See http://www.quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigDataSources.html
		if (className.equals("org.quartz.impl.jdbcjobstore.JobStoreTX")) {
			outProps.setProperty("org.quartz.jobStore.driverDelegateClass",
								 props.propertyAt(Strings.customized("{}org.quartz.jobStore.driverDelegateClass",rootXPath))
										  			  .asString("org.quartz.impl.jdbcjobstore.StdJDBCDelegate"));
			outProps.setProperty("org.quartz.jobStore.tablePrefix",
								 props.propertyAt(Strings.customized("{}org.quartz.jobStore.tablePrefix",rootXPath))
										  			 .asString(""));
			outProps.setProperty("org.quartz.jobStore.driverDelegateClass",
								 props.propertyAt(Strings.customized("{}org.quartz.jobStore.driverDelegateClass",rootXPath))
										  			  .asString("org.quartz.impl.jdbcjobstore.StdJDBCDelegate"));
			// true if you have multiple instances of Quartz that use the same set of database tables
			outProps.setProperty("org.quartz.jobStore.isClustered",
							 Boolean.toString(props.propertyAt(Strings.customized("{}org.quartz.jobStore.isClustered",rootXPath))
								  		  			.asBoolean(false)));
			//the frequency (in milliseconds) at which this instance "checks-in"* with the other instances of the cluster. 
			//Affects the quickness of detecting failed instances
			outProps.setProperty("org.quartz.jobStore.clusterCheckinInterval",
							 Long.toString(props.propertyAt(Strings.customized("{}org.quartz.jobStore.clusterCheckinInterval",rootXPath))
									  			 .asLong(15000)));	
			//Datasource conection (driver+user+pass+url or jndi) 
			String dataSource = props.propertyAt(Strings.customized("{}org.quartz.jobStore.dataSource",rootXPath))
										  			 .asString("myDS");
			outProps.setProperty("org.quartz.jobStore.dataSource",
								 props.propertyAt(Strings.customized("{}org.quartz.jobStore.dataSource",rootXPath))
										  			 .asString("myDS"));
			
			if (props.propertyAt(Strings.customized("{}org.quartz.dataSource.{}.driver",rootXPath, dataSource)).exist()) {
				outProps.setProperty(Strings.customized("org.quartz.dataSource.{}.driver", dataSource),
									 props.propertyAt(Strings.customized("{}org.quartz.dataSource.{}.driver",rootXPath,dataSource))
									 		.asString());
			}
			
			if (props.propertyAt(Strings.customized("{}org.quartz.dataSource."+dataSource+".URL",rootXPath)).exist()) {
				outProps.setProperty(Strings.customized("org.quartz.dataSource.{}.URL", dataSource),
									 props.propertyAt(Strings.customized("{}org.quartz.dataSource."+dataSource+".URL",rootXPath))
									 		.asString());
			}
			if (props.propertyAt(Strings.customized("{}org.quartz.dataSource."+dataSource+".user",rootXPath)).exist()) {
				outProps.setProperty(Strings.customized("org.quartz.dataSource.{}.user", dataSource),
									 props.propertyAt(Strings.customized("{}org.quartz.dataSource."+dataSource+".user",rootXPath))
									 		.asString());
			}
			if (props.propertyAt(Strings.customized("{}org.quartz.dataSource."+dataSource+".password",rootXPath)).exist()) {
				outProps.setProperty(Strings.customized("org.quartz.dataSource.{}.password",dataSource),
									 props.propertyAt(Strings.customized("{}org.quartz.dataSource."+dataSource+".password",rootXPath))
									 		.asString());
			}
			if (props.propertyAt(Strings.customized("{}org.quartz.dataSource."+dataSource+".jndiURL",rootXPath)).exist()) {
				outProps.setProperty(Strings.customized("org.quartz.dataSource.{}.jndiURL", dataSource),
									 props.propertyAt(Strings.customized("{}org.quartz.dataSource."+dataSource+".jndiURL",rootXPath))
									 		.asString());
			}
		
		}
		//Terracota jobstore
		//See http://www.quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigTerracottaJobStore.html
		else if (className.equals("org.terracotta.quartz.TerracottaJobStore")) {
			outProps.setProperty("org.quartz.jobStore.tcConfigUrl",
								 props.propertyAt(Strings.customized("{}org.quartz.jobStore.tcConfigUrl",rootXPath))
										  			 .asString("localhost:9510"));
		}
		return outProps;
	}
}
