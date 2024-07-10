package cmiller.interview;

import cmiller.interview.checkin.CheckInRequest;
import cmiller.interview.checkin.CheckInResponse;
import cmiller.interview.checkout.CheckoutRequest;
import cmiller.interview.checkout.CheckoutResponse;
import cmiller.interview.checkout.RentalAgreement;
import cmiller.interview.internal.factory.ToolRentalServiceDependencyFactory;

public interface ToolRentalService {
    /**
     * Checkout a tool
     * 
     * @param the {@link CheckoutRequest request data} in order to process a
     *            checkout action
     * @return the {@link CheckoutResponse response} resulting from the checkout
     *         action, which will contain the {@link RentalAgreement rental
     *         agreement}
     * @throws ToolRentalServiceException if any of the following conditions are
     *                                    met:
     *                                    <ul>
     *                                    <li>{@link CheckoutRequest.Builder#toolCode(String)
     *                                    request.toolCode} is not provided</li>
     *                                    <li>The provided
     *                                    {@link CheckoutRequest.Builder#toolCode(String)
     *                                    request.toolCode} could not be found in
     *                                    the tool rental database</li>
     *                                    <li>{@link CheckoutRequest.Builder#checkOutDate(java.time.LocalDate)
     *                                    request.checkOutDate} is not provided</li>
     *                                    <li>{@link CheckoutRequest.Builder#rentalDays(int)
     *                                    request.rentalDays} is not provided, or
     *                                    the value provided is not a positive
     *                                    number</li>
     *                                    </ul>
     *                                    The returned exception will provide the
     *                                    {@link ToolRentalServiceException#getFailureReason()
     *                                    reason} for the failure
     */
    CheckoutResponse checkout(CheckoutRequest request) throws ToolRentalServiceException;

    CheckInResponse checkin(CheckInRequest request) throws ToolRentalServiceException;

    public class Factory {
	public static ToolRentalService getService() {
	    return new ToolRentalServiceDependencyFactory().getToolRentalServiceImpl();
	}
    }
}
