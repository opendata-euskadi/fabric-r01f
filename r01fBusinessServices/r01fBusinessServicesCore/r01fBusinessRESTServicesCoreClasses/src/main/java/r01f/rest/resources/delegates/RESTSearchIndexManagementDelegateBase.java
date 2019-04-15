package r01f.rest.resources.delegates;

import java.net.URI;

import javax.ws.rs.core.Response;

import lombok.experimental.Accessors;
import r01f.persistence.index.IndexManagementCommand;
import r01f.rest.RESTOperationsResponseBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.IndexManagementServices;
import r01f.types.jobs.EnqueuedJob;

/**
 * Base type for REST services that encapsulates the common search index management ops
 */
@Accessors(prefix="_")
public abstract class RESTSearchIndexManagementDelegateBase 
           implements RESTDelegate {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final IndexManagementServices _indexManagementServices;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTSearchIndexManagementDelegateBase(final IndexManagementServices indexManagementServices) {
		_indexManagementServices = indexManagementServices;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  INDEX MANAGEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	public Response execCommand(final SecurityContext securityContext,final String resourcePath,
						 		final IndexManagementCommand command) {
		// Just delegate to the service implementation...
		EnqueuedJob outJob = null;
		switch(command.getAction()) {
		case CLOSE_INDEX:
			outJob = _indexManagementServices.closeIndex(securityContext);
			break;
		case OPEN_INDEX:
			outJob = _indexManagementServices.openIndex(securityContext);
			break;
		case OPTIMIZE_INDEX:
			outJob = _indexManagementServices.optimizeIndex(securityContext);
			break;
		case TRUNCATE_INDEX:
			outJob = _indexManagementServices.truncateIndex(securityContext);
			break;
		default:
			throw new IllegalArgumentException();
		}
		// return
		Response outResponse  = RESTOperationsResponseBuilder.searchIndex()
															 .at(URI.create(resourcePath))
															 .build(outJob);
		return outResponse;
	}
}
