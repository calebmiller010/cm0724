package cmiller.interview.internal.checkout;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import cmiller.interview.checkout.RentalAgreement;
import cmiller.interview.common.Tool;

public class RentalAgreementImpl implements RentalAgreement {
    private final Tool tool;
    private final int rentalDays;
    private final LocalDate checkOutDate;
    private final LocalDate dueDate;
    private final long dailyRentalCharge;
    private final int chargeDays;
    private final long preDiscountCharge;
    private final int discountPercent;
    private final long discountAmount;
    private final long finalCharge;

    private RentalAgreementImpl(Builder builder) {
	this.tool = builder.tool;
	this.rentalDays = builder.rentalDays;
	this.checkOutDate = builder.checkOutDate;
	this.dueDate = builder.dueDate;
	this.dailyRentalCharge = builder.dailyRentalCharge;
	this.chargeDays = builder.chargeDays;
	this.preDiscountCharge = builder.preDiscountCharge;
	this.discountPercent = builder.discountPercent;
	this.discountAmount = builder.discountAmount;
	this.finalCharge = builder.finalCharge;
    }

    @Override
    public String prettyPrint() {
	StringBuilder builder = new StringBuilder();
	builder.append("Tool code: ").append(tool.getCode()).append('\n');
	builder.append("Tool type: ").append(tool.getType().asString()).append('\n');
	builder.append("Tool brand: ").append(tool.getBrand()).append('\n');
	builder.append("Rental days: ").append(rentalDays).append('\n');
	builder.append("Check-out date: ").append(prettyPrintDate(checkOutDate)).append('\n');
	builder.append("Due date: ").append(prettyPrintDate(dueDate)).append('\n');
	builder.append("Daily rental charge: ").append(prettyPrintDollar(dailyRentalCharge)).append('\n');
	builder.append("Charge days: ").append(chargeDays).append('\n');
	builder.append("Pre-discount charge: ").append(prettyPrintDollar(preDiscountCharge)).append('\n');
	builder.append("Discount percent: ").append("%s%%".formatted(discountPercent)).append('\n');
	builder.append("Discount amount: ").append(prettyPrintDollar(discountAmount)).append('\n');
	builder.append("Final charge: ").append(prettyPrintDollar(finalCharge)).append('\n');
	return builder.toString();
    }

    private static String prettyPrintDollar(long amount) {
	NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
	return currencyFormat.format(amount / 100.0);
    }

    private static String prettyPrintDate(LocalDate date) {
	return date.format(DateTimeFormatter.ofPattern("MM'/'dd'/'yy"));
    }

    @Override
    public Tool getTool() {
	return tool;
    }

    @Override
    public int getRentalDays() {
	return rentalDays;
    }

    @Override
    public LocalDate getCheckOutDate() {
	return checkOutDate;
    }

    @Override
    public LocalDate getDueDate() {
	return dueDate;
    }

    @Override
    public long getDailyRentalCharge() {
	return dailyRentalCharge;
    }

    @Override
    public int getChargeDays() {
	return chargeDays;
    }

    @Override
    public long getPreDiscountCharge() {
	return preDiscountCharge;
    }

    @Override
    public int getDiscountPercent() {
	return discountPercent;
    }

    @Override
    public long getDiscountAmount() {
	return discountAmount;
    }

    @Override
    public long getFinalCharge() {
	return finalCharge;
    }

    @Override
    public String toString() {
	return "RentalAgreementImpl [tool=" + tool + ", rentalDays=" + rentalDays + ", checkOutDate=" + checkOutDate
		+ ", dueDate=" + dueDate + ", dailyRentalCharge=" + dailyRentalCharge + ", chargeDays=" + chargeDays
		+ ", preDiscountCharge=" + preDiscountCharge + ", discountPercent=" + discountPercent
		+ ", discountAmount=" + discountAmount + ", finalCharge=" + finalCharge + "]";
    }

    public static class Builder {
	private Tool tool;
	private int rentalDays;
	private LocalDate checkOutDate;
	private LocalDate dueDate;
	private long dailyRentalCharge;
	private int chargeDays;
	private long preDiscountCharge;
	private int discountPercent;
	private long discountAmount;
	private long finalCharge;

	public Builder tool(Tool tool) {
	    this.tool = tool;
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

	public Builder dueDate(LocalDate dueDate) {
	    this.dueDate = dueDate;
	    return this;
	}

	public Builder dailyRentalCharge(long dailyRentalCharge) {
	    this.dailyRentalCharge = dailyRentalCharge;
	    return this;
	}

	public Builder chargeDays(int chargeDays) {
	    this.chargeDays = chargeDays;
	    return this;
	}

	public Builder preDiscountCharge(long preDiscountCharge) {
	    this.preDiscountCharge = preDiscountCharge;
	    return this;
	}

	public Builder discountPercent(int discountPercent) {
	    this.discountPercent = discountPercent;
	    return this;
	}

	public Builder discountAmount(long discountAmount) {
	    this.discountAmount = discountAmount;
	    return this;
	}

	public Builder finalCharge(long finalCharge) {
	    this.finalCharge = finalCharge;
	    return this;
	}

	public RentalAgreement build() {
	    return new RentalAgreementImpl(this);
	}
    }
}
