package cmiller.interview.checkout;

import java.time.LocalDate;

import cmiller.interview.common.Tool;

public interface RentalAgreement {
    /**
     * Pretty print each of the fields according to the requirements.
     * 
     * @return the pretty-printed fields of the Rental Agreement as String
     */
    String prettyPrint();

    /**
     * @return the tool that was rented. Guaranteed to not return {@code null}.
     */
    Tool getTool();

    /**
     * @return the number of days that the tool was rented. Guaranteed to be &gt 0.
     */
    int getRentalDays();

    /**
     * @return the date the tool was checked out. Guaranteed to not return
     *         {@code null}.
     */
    LocalDate getCheckOutDate();

    /**
     * @return the date the tool is due to be brought back. Guaranteed to not return
     *         {@code null}.
     */
    LocalDate getDueDate();

    /**
     * @return the amount per day the tool costs, in cents. Guaranteed to not be
     *         negative.
     */
    long getDailyRentalCharge();

    /**
     * @return the number of chargeable days, beginning from the day after checkout,
     *         and including the due date. Excludes "no charge" days, if applicable,
     *         based on the {@link Tool#getType() tool type}.
     */
    int getChargeDays();

    /**
     * @return the amount charged for the rental of the tool, in cents, before any
     *         discounts are taken. Guaranteed to not be negative.
     */
    long getPreDiscountCharge();

    /**
     * @return the discount percent offered on the tool. Guaranteed to be in the
     *         range 0 - 100.
     */
    int getDiscountPercent();

    /**
     * @return the discount amount offered on the tool, in cents. Guaranteed to not
     *         be negative.
     */
    long getDiscountAmount();

    /**
     * @return the final amount charged for rental of the tool, in cents. Guaranteed
     *         to not be negative.
     */
    long getFinalCharge();
}
