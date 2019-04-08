package r01f.model.persistence;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.reflect.TypeToken;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.model.persistence.CountResultForEntityBuilder.CountResultBuilderForErrorExtErrorCodeStep;
import r01f.model.persistence.CountResultForEntityBuilder.CountResultBuilderForErrorStep;
import r01f.model.persistence.CountResultForEntityBuilder.CountResultBuilderForOKStep;
import r01f.patterns.IsBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.Strings;

/**
 * Builder type for {@link CountResult}-implementing types:
 * <ul>
 * 		<li>A successful COUNT operation result on a single entity: {@link CountOK}</li>
 * 		<li>An error on a COUNT operation execution on a single entity: {@link CountError}</li>
 * </ul>
 * If the operation execution was successful:
 * <pre class='brush:java'>
 * 		CountOK<MyEntity> opOK = CountResultBuilder.using(securityContext)
 * 											       .on(MyEntity.class)
 * 												   .loaded()
 * 														.entity(myEntityInstance);
 * 		CountOK<MyEntity> opOK = CountResultBuilder.using(securityContext)
 * 											       .on(MyEntity.class)
 * 												   .created()
 * 														.entity(myEntityInstance);
 * </pre>
 * If an error is raised while executing the persistence operation:
 * <pre class='brush:java'>
 * 		CountError<MyEntity> opError = CountResultBuilder.using(securityContext)
 * 													   .on(MyEntity.class)
 * 													   .notLoaded()
 * 													   .because(error)
 * 														 	.about(myEntityOid);
 * </pre>
 */
@GwtIncompatible
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CountResultBuilder 
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static CountResultBuilderEntityStep using(final SecurityContext securityContext) {
		return new CountResultBuilder() {/* nothing */}
						.new CountResultBuilderEntityStep(securityContext);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class CountResultBuilderEntityStep {
		private final SecurityContext _securityContext;
		
		public <T> CountResultBuilderOperationStep<T> on(final Class<T> entityType) {
			return new CountResultBuilderOperationStep<T>(_securityContext,
														 entityType);
		}
		@SuppressWarnings("unchecked")
		public <T> CountResultBuilderOperationStep<T> on(final TypeToken<T> typeRef) {
			return new CountResultBuilderOperationStep<T>(_securityContext,
														 (Class<T>)typeRef.getRawType());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Operation
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private abstract class CountResultBuilderOperationStepBase<T> {
		protected final SecurityContext _securityContext;
		protected final Class<T> _entityType;
		
		//  --------- ERROR
		public CountResultBuilderForErrorExtErrorCodeStep<T> badClientRequestData(final String reqOp,
																	  final String msg,final Object... vars) {
			CountError<T> err = new CountError<T>(_entityType,
												   reqOp,
												   Strings.customized(msg,vars),PersistenceErrorType.BAD_REQUEST_DATA);
			return new CountResultBuilderForErrorExtErrorCodeStep<T>(_securityContext,
														 			 err);
		}
		public CountResultBuilderForErrorStep<T> not(final String reqOp) {
			return new CountResultBuilderForErrorStep<T>(_securityContext,
														 _entityType,
														 reqOp);
		}
		// --------- SUCCESS
		public CountResultBuilderForOKStep<T> counted(final String reqOp) {
			return new CountResultBuilderForOKStep<T>(_securityContext,
													  _entityType,
													  reqOp);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public class CountResultBuilderOperationStep<T> 
		 extends CountResultBuilderOperationStepBase<T> {
		
		public CountResultBuilderOperationStep(final SecurityContext securityContext,
											   final Class<T> entityType) {
			super(securityContext,
				  entityType);
		}
	}
}