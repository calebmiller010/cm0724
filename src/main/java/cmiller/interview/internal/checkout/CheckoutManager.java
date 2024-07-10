package cmiller.interview.internal.checkout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import cmiller.interview.ToolRentalServiceException;
import cmiller.interview.ToolRentalServiceException.FailureReason;
import cmiller.interview.checkout.CheckoutRequest;
import cmiller.interview.checkout.CheckoutResponse;
import cmiller.interview.checkout.RentalAgreement;
import cmiller.interview.common.Tool;
import cmiller.interview.internal.agent.HolidaysAgent;
import cmiller.interview.internal.data.ChargeableDaysDO;
import cmiller.interview.internal.data.access.DataRetrievalService;
import cmiller.interview.internal.factory.ToolRentalServiceDependencyFactory;

public class CheckoutManager {
    private static final Logger LOGGER = Logger.getLogger(CheckoutManager.class.getName());

    private ToolRentalServiceDependencyFactory factory;
    private HolidaysAgent holidaysAgent;

    public CheckoutManager(ToolRentalServiceDependencyFactory factory) {
	this.factory = factory;
	this.holidaysAgent = factory.getHolidaysAgent();
    }

    public CheckoutResponse checkout(CheckoutRequest request) {
	validateRequest(request);

	String toolCode = request.getToolCode();

	DataRetrievalService dataRetrievalService = factory.getDataRetrievalService();
	Tool toolToRent = dataRetrievalService.getToolByCode(toolCode);

	if (toolToRent == null) {
	    throw new ToolRentalServiceException(FailureReason.TOOL_NOT_FOUND,
		    "The toolCode %s could not be found".formatted(toolCode));
	}

	ChargeableDaysDO chargeableDaysDO = dataRetrievalService.getChargeableDaysByToolType(toolToRent.getType());
	if (chargeableDaysDO == null) {
	    LOGGER.log(Level.SEVERE,
		    "Unable to find chargeable days information for the toolType: %s. It is expected that all tool"
			    + "types within our database have an associated chargeable days defined."
				    .formatted(toolToRent.getType()));
	    throw new ToolRentalServiceException(FailureReason.INTERNAL_ERROR, "An unexpected error occurred");
	}

	int rentalDays = request.getRentalDays();
	LocalDate checkOutDate = request.getCheckOutDate();
	LocalDate dueDate = checkOutDate.plusDays(rentalDays);

	int chargeDays = calculateChargeableDays(checkOutDate, dueDate, chargeableDaysDO);

	long dailyRentalCharge = chargeableDaysDO.getDailyCharge();
	long preDiscountCharge = calculatePreDiscountCharge(chargeDays, dailyRentalCharge);
	int discountPercent = request.getDiscountPercent();
	long discountAmount = calculateDiscountAmount(preDiscountCharge, discountPercent);
	long finalCharge = preDiscountCharge - discountAmount;

	//@formatter:off
	RentalAgreement rentalAgreement = new RentalAgreementImpl.Builder()
		.tool(toolToRent)
		.rentalDays(rentalDays)
		.checkOutDate(checkOutDate)
		.dueDate(dueDate)
		.dailyRentalCharge(dailyRentalCharge)
		.chargeDays(chargeDays)
		.preDiscountCharge(preDiscountCharge)
		.discountPercent(discountPercent)
		.discountAmount(discountAmount)
		.finalCharge(finalCharge)
		.build();
	//@formatter:on

	return new CheckoutResponseImpl(rentalAgreement);
    }

    private static long calculateDiscountAmount(long preDiscountChargeCents, int discountPercent) {
	BigDecimal preDiscountCharge = new BigDecimal(preDiscountChargeCents);
	BigDecimal discount = preDiscountCharge.multiply(new BigDecimal(discountPercent)).divide(new BigDecimal("100"));
	discount = discount.setScale(0, RoundingMode.HALF_UP);
	return discount.longValue();
    }

    private static long calculatePreDiscountCharge(int chargeDays, long dailyRentalChargeCents) {
	BigDecimal dailyRentalCharge = new BigDecimal(dailyRentalChargeCents);
	BigDecimal totalCharge = dailyRentalCharge.multiply(new BigDecimal(chargeDays));
	totalCharge = totalCharge.setScale(0, RoundingMode.HALF_UP);
	return totalCharge.longValue();
    }

    private int calculateChargeableDays(LocalDate checkOutDate, LocalDate dueDate, ChargeableDaysDO chargeableDaysDO) {
	int chargeableDays = 0;
	List<Predicate<LocalDate>> notChargeableDayConditions = buildNonChargeableDayConditions(chargeableDaysDO);

	// Based on the requirements, the first chargeable day is the day after
	// checkout
	LocalDate currentDate = checkOutDate.plusDays(1);

	// inclusive to the due date
	nextDayLoop: while (currentDate.isBefore(dueDate) || currentDate.isEqual(dueDate)) {
	    for (Predicate<LocalDate> condition : notChargeableDayConditions) {

		// if it's determined based on the Predicate, that the tool rental is not
		// chargeable for that day
		if (condition.test(currentDate)) {
		    currentDate = currentDate.plusDays(1);
		    continue nextDayLoop;
		}
	    }

	    chargeableDays++;
	    currentDate = currentDate.plusDays(1);
	}
	return chargeableDays;
    }

    /**
     * 
     * @return the List of conditions to check if a certain {@link LocalDate day}
     *         should not be charged. The list of conditions is made up, only of the
     *         specified conditions (weekday/weekend/holiday) that are "false",
     *         meaning that condition is "not chargeable" for the tool type.
     */
    private List<Predicate<LocalDate>> buildNonChargeableDayConditions(ChargeableDaysDO chargeableDaysDO) {
	List<Predicate<LocalDate>> conditions = new ArrayList<>();

	if (!chargeableDaysDO.isWeekendCharge()) {
	    conditions.add(CheckoutManager::isWeekend);
	}
	if (!chargeableDaysDO.isHolidayCharge()) {
	    conditions.add(this::isHoliday);
	}

	return conditions;
    }

    private static boolean isWeekend(LocalDate date) {
	return Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(date.getDayOfWeek());
    }

    private boolean isHoliday(LocalDate date) {
	List<LocalDate> holidays = holidaysAgent.getHolidaysForYear(date.getYear());
	return holidays.contains(date);
    }

    /**
     * throws an exception with {@link FailureReason#INVALID_INPUT} if one of the
     * conditions for an invalid request are met
     */
    private void validateRequest(CheckoutRequest request) {
	if (request.getRentalDays() < 1) {
	    throw new ToolRentalServiceException(FailureReason.INVALID_INPUT,
		    "rentalDays is required, and must be greater than 0");
	} else if (request.getDiscountPercent() < 0 || request.getDiscountPercent() >= 100) {
	    throw new ToolRentalServiceException(FailureReason.INVALID_INPUT,
		    "discountPercent must be between 0 and 100");
	} else if (request.getToolCode() == null) {
	    throw new ToolRentalServiceException(FailureReason.INVALID_INPUT,
		    "toolCode is required, and cannot be null");
	} else if (request.getCheckOutDate() == null) {
	    throw new ToolRentalServiceException(FailureReason.INVALID_INPUT,
		    "checkOutDate is required, and cannot be null");
	}
    }

}
