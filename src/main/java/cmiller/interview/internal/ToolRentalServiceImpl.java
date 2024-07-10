package cmiller.interview.internal;

import cmiller.interview.ToolRentalService;
import cmiller.interview.ToolRentalServiceException;
import cmiller.interview.checkin.CheckInRequest;
import cmiller.interview.checkin.CheckInResponse;
import cmiller.interview.checkout.CheckoutRequest;
import cmiller.interview.checkout.CheckoutResponse;
import cmiller.interview.internal.checkin.CheckInManager;
import cmiller.interview.internal.checkout.CheckoutManager;
import cmiller.interview.internal.factory.ToolRentalServiceDependencyFactory;

/**
 * Implementation of {@link ToolRentalService} to route the service call to its
 * dedicated workflow
 */
public class ToolRentalServiceImpl implements ToolRentalService {
    private ToolRentalServiceDependencyFactory factory;

    public ToolRentalServiceImpl(ToolRentalServiceDependencyFactory factory) {
	this.factory = factory;
    }

    @Override
    public CheckoutResponse checkout(CheckoutRequest request) throws ToolRentalServiceException {
	return new CheckoutManager(factory).checkout(request);
    }

    @Override
    public CheckInResponse checkin(CheckInRequest request) throws ToolRentalServiceException {
	return new CheckInManager(factory).checkIn(request);
    }
}