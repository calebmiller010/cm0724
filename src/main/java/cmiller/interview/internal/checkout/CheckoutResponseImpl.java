package cmiller.interview.internal.checkout;

import cmiller.interview.checkout.CheckoutResponse;
import cmiller.interview.checkout.RentalAgreement;

public class CheckoutResponseImpl implements CheckoutResponse {
    private final RentalAgreement rentalAgreement;

    public CheckoutResponseImpl(RentalAgreement rentalAgreement) {
	this.rentalAgreement = rentalAgreement;
    }

    @Override
    public RentalAgreement getRentalAgreement() {
	return rentalAgreement;
    }
}
