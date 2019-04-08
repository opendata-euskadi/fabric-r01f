package r01f.persistence.db;

import com.google.common.base.Function;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.model.ModelObjectTracking;
import r01f.model.PersistableModelObject;
import r01f.model.facets.HasCreationData;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasLastUpdateData;
import r01f.patterns.IsBuilder;
import r01f.securitycontext.SecurityContext;


/**
 * Transformer functions between {@link DBEntity} and {@link ModelObject}
 */
@Slf4j
public class DBEntityToModelObjectTransformerBuilder
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new transformer from a {@link DBEntity} to a {@link ModelObject}
	 * @param securityContext
	 * @param modelObjType
	 * @return
	 */
	public static <DB extends DBEntity,
				   M extends ModelObject> Function<DB,M> createFor(final SecurityContext securityContext,
						  				   						   final TransformsDBEntityIntoModelObject<DB,M> dbEntityToModelObjectTransformer) {
		return DBEntityToModelObjectTransformerBuilder.createFor(securityContext,
																 new Function<DB,M>() {
																		@Override
																		public M apply(final DB dbEntity) {
																			try {
																				// transform the dbentity to a model object
																				return dbEntityToModelObjectTransformer.dbEntityToModelObject(securityContext,
																																			  dbEntity);
																			} catch(Exception ex) {
																				log.error("DBEntityToModelObjectTransformerBuilder error :{}",ex.getMessage(),ex);
																			} catch(Throwable ex) {
																				log.error("DBEntityToModelObjectTransformerBuilder error :{}",ex.getMessage(),ex);
																			}																	
																			return null;//Return null if any exception happens, so must be applied a no null filtering!
																		}
																 });
	}
	/**
	 * Creates a new transformer from a {@link DBEntity} to a {@link ModelObject}
	 * @param securityContext
	 * @param transformer another transformer
	 * @return
	 */
	public static <DB extends DBEntity,
				   M extends ModelObject> Function<DB,M> createFor(final SecurityContext securityContext,
						  				   						 							 final Function<DB,M> transformer) {
		return new Function<DB,M>() {
						@SuppressWarnings("unchecked")
						@Override
						public M apply(final DB dbEntity) {
							try {
								// Transform to model object	
								M outModelObj = transformer.apply(dbEntity);	
								// ensure that the model object has the tacking info and entity version
								if (outModelObj != null) {
									if (outModelObj instanceof PersistableModelObject) {
										DBEntityToModelObjectTransformerBuilder.copyDBEntiyTrackingInfoAndEntityVersionToModelObject(dbEntity,
																																	 (PersistableModelObject<? extends OID>)outModelObj);
									}
								}
								return outModelObj;
							}catch(Exception ex) {
								log.error("DBEntityToModelObjectTransformerBuilder error :{}",ex.getMessage(),ex);
							}catch(Throwable ex) {
								log.error("DBEntityToModelObjectTransformerBuilder error :{}",ex.getMessage(),ex);
							}																	
							return null;//Return null if any exception happens, so must be applied a not null filtering!
							
						}
					};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATIC UTIL METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private static void copyDBEntiyTrackingInfoAndEntityVersionToModelObject(final DBEntity dbEntity,
																		     final PersistableModelObject<?> modelObject) {
		// do not forget!
		ModelObjectTracking trackingInfo = new ModelObjectTracking();
		
		if (dbEntity instanceof HasCreationData) {
			HasCreationData hasCreationData = (HasCreationData)dbEntity;
			if (hasCreationData.getCreateTimeStamp() != null) {
				trackingInfo.setCreateDate(hasCreationData.getCreateTimeStamp());
			} else if (modelObject.getTrackingInfo() != null 
					&& modelObject.getTrackingInfo().getCreateDate() != null) {
				trackingInfo.setCreateDate(modelObject.getTrackingInfo().getCreateDate());
			}
			if (hasCreationData.getCreatorUserCode() != null) {
				trackingInfo.setCreatorUserCode(hasCreationData.getCreatorUserCode());
			} else if (modelObject.getTrackingInfo() != null 
					&& modelObject.getTrackingInfo().getCreatorUserCode() != null) {
				trackingInfo.setCreatorUserCode(modelObject.getTrackingInfo().getCreatorUserCode());
			}
		}
		
		if (dbEntity instanceof HasLastUpdateData) {
			HasLastUpdateData hasUpdateData = (HasLastUpdateData)dbEntity;
			if (hasUpdateData.getLastUpdateTimeStamp() != null) {
				trackingInfo.setLastUpdateDate(hasUpdateData.getLastUpdateTimeStamp());
			} else if (modelObject.getTrackingInfo() != null 
					&& modelObject.getTrackingInfo().getLastUpdateDate() != null) {
				trackingInfo.setLastUpdateDate(modelObject.getTrackingInfo().getLastUpdateDate());
			}
			if (hasUpdateData.getLastUpdatorUserCode() != null) {
				trackingInfo.setLastUpdatorUserCode(hasUpdateData.getLastUpdatorUserCode());
			} else if (modelObject.getTrackingInfo() != null 
					&& modelObject.getTrackingInfo().getLastUpdatorUserCode() != null) {
				trackingInfo.setLastUpdatorUserCode(modelObject.getTrackingInfo().getLastUpdatorUserCode());
			}
		}
		
		modelObject.setTrackingInfo(trackingInfo);
		
		if (dbEntity instanceof HasEntityVersion) {
			HasEntityVersion hasEntityVersion = (HasEntityVersion)dbEntity;
			modelObject.setEntityVersion(hasEntityVersion.getEntityVersion());
		}
	}
}
