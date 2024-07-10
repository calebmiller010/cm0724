package cmiller.interview.checkout;

import cmiller.interview.ToolRentalService;

public interface CheckoutResponse {

    /**
     * Note: this response wrapper around Rental Agreement allows for additional
     * information (that's not part of the Rental Agreement) to be returned in the
     * future without breaking the API contract between versions.
     */

    /**
     * @return the Rental Agreement resulting from the
     *         {@link ToolRentalService#checkout(CheckoutRequest) checkout action}
     */
    RentalAgreement getRentalAgreement();
}
