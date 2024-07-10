package cmiller.interview.checkout;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import cmiller.interview.ToolRentalService;

public class CheckoutRequest {
    private final String toolCode;
    private final int rentalDays;
    private final LocalDate checkOutDate;
    private final int discountPercent;

    private CheckoutRequest(Builder builder) {
	this.toolCode = builder.toolCode;
	this.rentalDays = builder.rentalDays;
	this.checkOutDate = builder.checkOutDate;
	this.discountPercent = builder.discountPercent;
    }

    public String getToolCode() {
	return toolCode;
    }

    public int getRentalDays() {
	return rentalDays;
    }

    public LocalDate getCheckOutDate() {
	return checkOutDate;
    }

    public int getDiscountPercent() {
	return discountPercent;
    }

    public static class Builder {
	private String toolCode;
	private int rentalDays;
	private LocalDate checkOutDate;
	private int discountPercent;

	public Builder toolCode(String toolCode) {
	    if (StringUtils.isBlank(toolCode)) {
		throw new IllegalArgumentException("CheckoutRequest.toolCode cannot be null/blank");
	    }
	    this.toolCode = toolCode;
	    return this;
	}

	public Builder rentalDays(int rentalDays) {
	    this.rentalDays = rentalDays;
	    return this;
	}

	public Builder checkOutDate(LocalDate checkOutDate) {
	    this.checkOutDate = checkOutDate;
	    return this;
	}

	public Builder discountPercent(int discountPercent) {
	    this.discountPercent = discountPercent;
	    return this;
	}

	/**
	 * Validation for required fields will be performed in the
	 * {@link ToolRentalService#checkout(CheckoutRequest) checkout implementation},
	 * per the API contract.
	 */
	public CheckoutRequest build() {
	    return new CheckoutRequest(this);
	}
    }

}