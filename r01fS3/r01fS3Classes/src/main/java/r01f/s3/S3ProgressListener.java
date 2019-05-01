package r01f.s3;

public interface S3ProgressListener {

/////////////////////////////////////////////////////////////////////////////////////
// METHODS TO IMPLEMENT
////////////////////////////////////////////////////////////////////////////////////
	public void progressChanged(final S3ProgressEvent progressEvent) ;

/////////////////////////////////////////////////////////////////////////////////////
// EVENTS
////////////////////////////////////////////////////////////////////////////////////
	public static enum S3ProgressEvent {
			 /**
		     * Event of the content length to be sent in a request.
		     */
		    REQUEST_CONTENT_LENGTH_EVENT,
		    /**
		     * Event of the content length received in a response.
		     */
		    RESPONSE_CONTENT_LENGTH_EVENT,

		    /**
		     * Used to indicate the number of bytes to be sent to AWS.
		     */
		    REQUEST_BYTE_TRANSFER_EVENT,
		    /**
		     * Used to indicate the number of bytes received from AWS.
		     */
		    RESPONSE_BYTE_TRANSFER_EVENT,
		    /**
		     * Used to indicate the number of bytes discarded after being received from AWS.
		     */
		    RESPONSE_BYTE_DISCARD_EVENT,

		    /* Generic request progress events */

		    /**
		     * Event indicating that the client has started sending the AWS API request.
		     * This type of event is guaranteed to be only fired once during a
		     * request-response cycle, even when the request is retried.
		     */
		    CLIENT_REQUEST_STARTED_EVENT,

		    /**
		     * Event indicating that the client has started sending the HTTP request.
		     * The request progress listener will be notified of multiple instances of
		     * this type of event if the request gets retried.
		     */
		    HTTP_REQUEST_STARTED_EVENT,

		    /**
		     * Event indicating that the client has finished sending the HTTP request.
		     * The request progress listener will be notified of multiple instances of
		     * this type of event if the request gets retried.
		     */
		    HTTP_REQUEST_COMPLETED_EVENT,

		    /**
		     * Event indicating that the HTTP request content is reset, which may or may not
		     * be caused by the retry of the request.
		     */
		    HTTP_REQUEST_CONTENT_RESET_EVENT,

		    /**
		     * Event indicating that a failed request is detected as retryable and is
		     * ready for the next retry.
		     */
		    CLIENT_REQUEST_RETRY_EVENT,

		    /**
		     * Event indicating that the client has started reading the HTTP response.
		     * The request progress listener will be notified of this event only if the
		     * client receives a successful service response (i.e. 2XX status code).
		     */
		    HTTP_RESPONSE_STARTED_EVENT,

		    /**
		     * Event indicating that the client has finished reading the HTTP response.
		     * The request progress listener will be notified of this event only if the
		     * client receives a successful service response (i.e. 2XX status code).
		     */
		    HTTP_RESPONSE_COMPLETED_EVENT,

		    /**
		     * Event indicating that the HTTP response content is reset.
		     */
		    HTTP_RESPONSE_CONTENT_RESET_EVENT,

		    /**
		     * Event indicating that the client has received a successful service
		     * response and has finished parsing the response data.
		     */
		    CLIENT_REQUEST_SUCCESS_EVENT,

		    /**
		     * Event indicating that a client request has failed (after retries have
		     * been conducted).
		     */
		    CLIENT_REQUEST_FAILED_EVENT,

		    //////////////////////////////////////////////////////////////////////////
		    // Transfer Event:
		    // Progress events that are used by S3 and Glacier client */
		    //////////////////////////////////////////////////////////////////////////
		    TRANSFER_PREPARING_EVENT,
		    TRANSFER_STARTED_EVENT,
		    TRANSFER_COMPLETED_EVENT,
		    TRANSFER_FAILED_EVENT,
		    TRANSFER_CANCELED_EVENT,
		    TRANSFER_PART_STARTED_EVENT,
		    TRANSFER_PART_COMPLETED_EVENT,
		    TRANSFER_PART_FAILED_EVENT;
		}
}
