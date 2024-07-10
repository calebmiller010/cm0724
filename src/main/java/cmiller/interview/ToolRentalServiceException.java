package cmiller.interview;

public class ToolRentalServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public enum FailureReason {
	TOOL_NOT_FOUND, INVALID_INPUT, INTERNAL_ERROR
    }

    private final FailureReason failureReason;

    public ToolRentalServiceException(FailureReason failureReason) {
	super("Checkout failed: " + failureReason);
	this.failureReason = failureReason;
    }

    public ToolRentalServiceException(FailureReason failureReason, String message) {
	super(message);
	this.failureReason = failureReason;
    }

    public ToolRentalServiceException(FailureReason failureReason, String message, Throwable cause) {
	super(message, cause);
	this.failureReason = failureReason;
    }

    public ToolRentalServiceException(FailureReason failureReason, Throwable cause) {
	super("Checkout failed due to: " + failureReason, cause);
	this.failureReason = failureReason;
    }

    public FailureReason getFailureReason() {
	return failureReason;
    }
}
