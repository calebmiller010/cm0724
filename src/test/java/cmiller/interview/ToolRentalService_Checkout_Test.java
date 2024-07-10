package cmiller.interview;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import cmiller.interview.ToolRentalServiceException.FailureReason;
import cmiller.interview.checkout.CheckoutRequest;
import cmiller.interview.checkout.CheckoutResponse;
import cmiller.interview.checkout.RentalAgreement;
import cmiller.interview.common.Tool.Type;
import cmiller.interview.internal.data.access.impl.InMemoryDataRetrievalService;

/**
 * Functional tests for the {@link ToolRentalService#checkout(CheckoutRequest)
 * checkout} operation. These tests use the default in-memory sample data, from
 * {@link InMemoryDataRetrievalService}
 */
public class ToolRentalService_Checkout_Test {

    private static ToolRentalService toolRentalService;

    @BeforeAll
    public static void beforeAll() {
	toolRentalService = ToolRentalService.Factory.getService();
    }

    @Nested
    @DisplayName("ToolRentalServiceException test cases")
    // Although these are duplicate tests with the CheckoutManagerTest, we want
    // these in the functional tests because they ensure that the specifications on
    // the API contract is honored.
    class ExceptionTestCases {
	@Test
	public void invalidInputTests_noToolCode() {
	    CheckoutRequest request = new CheckoutRequest.Builder().checkOutDate(LocalDate.of(2024, Month.JULY, 20))
		    .rentalDays(1).build();

	    try {
		toolRentalService.checkout(request);
		fail("Expected exception was not thrown");
	    } catch (ToolRentalServiceException e) {
		assertThat(e.getFailureReason(), is(FailureReason.INVALID_INPUT));
	    }
	}

	@Test
	public void invalidInputTests_noCheckoutDate() {
	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode("ABC").rentalDays(1).build();

	    try {
		toolRentalService.checkout(request);
		fail("Expected exception was not thrown");
	    } catch (ToolRentalServiceException e) {
		assertThat(e.getFailureReason(), is(FailureReason.INVALID_INPUT));
	    }
	}

	@Test
	public void invalidInputTests_noRentalDays() {
	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode("ABC")
		    .checkOutDate(LocalDate.of(2024, Month.JULY, 20)).build();

	    try {
		toolRentalService.checkout(request);
		fail("Expected exception was not thrown");
	    } catch (ToolRentalServiceException e) {
		assertThat(e.getFailureReason(), is(FailureReason.INVALID_INPUT));
	    }
	}

	@ParameterizedTest
	@ValueSource(ints = { -100, -1, 0 })
	public void invalidInputTests_rentalDaysInvalid(int rentalDays) {
	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode("ABC")
		    .checkOutDate(LocalDate.of(2024, Month.JULY, 20)).rentalDays(rentalDays).build();

	    try {
		toolRentalService.checkout(request);
		fail("Expected exception was not thrown");
	    } catch (ToolRentalServiceException e) {
		assertThat(e.getFailureReason(), is(FailureReason.INVALID_INPUT));
	    }
	}

	@ParameterizedTest
	@ValueSource(ints = { -100, -1, 100, 101 })
	public void invalidInputTests_discountPercentInvalid(int discountPercent) {
	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode("ABC")
		    .checkOutDate(LocalDate.of(2024, Month.JULY, 20)).rentalDays(1).discountPercent(discountPercent)
		    .build();

	    try {
		toolRentalService.checkout(request);
		fail("Expected exception was not thrown");
	    } catch (ToolRentalServiceException e) {
		assertThat(e.getFailureReason(), is(FailureReason.INVALID_INPUT));
	    }
	}

	@Test
	public void toolCodeNotFoundTest() {
	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode("INVALID_TOOL_CODE").rentalDays(1)
		    .checkOutDate(LocalDate.of(2024, Month.JULY, 20)).build();

	    try {
		toolRentalService.checkout(request);
		fail("Expected exception was not thrown");
	    } catch (ToolRentalServiceException e) {
		assertThat(e.getFailureReason(), is(FailureReason.TOOL_NOT_FOUND));
	    }
	}
    }

    @Nested
    @DisplayName("Required test cases from the specification document provided")
    class RequiredTestCases {
	@Test
	public void test1() {
	    String toolCodeInput = "JAKR";
	    int chargeDaysInput = 5;
	    LocalDate checkOutDateInput = LocalDate.of(2015, Month.SEPTEMBER, 3);
	    int discountPercentage = 101;

	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode(toolCodeInput)
		    .checkOutDate(checkOutDateInput).rentalDays(chargeDaysInput).discountPercent(discountPercentage)
		    .build();

	    try {
		toolRentalService.checkout(request);
		fail("Expected exception was not thrown");
	    } catch (ToolRentalServiceException e) {
		assertThat(e.getFailureReason(), is(FailureReason.INVALID_INPUT));
	    }
	}

	/**
	 * Expected charge days explanation:<br>
	 * - Checked-out days that could be chargeable: Friday July 3 to Sunday July
	 * 5<br>
	 * - LADW -> Ladder; No holiday charge<br>
	 * July 4 is a Saturday, **so it gets observed on Friday July 3**<br>
	 * July 3 is a non-chargeable days (holiday)<br>
	 * July 4 and 5 are chargeable days<br>
	 * = 2 charge days
	 */
	@Test
	public void test2() {
	    String toolCodeInput = "LADW";
	    int chargeDaysInput = 3;
	    LocalDate checkOutDateInput = LocalDate.of(2020, Month.JULY, 2);
	    int discountPercentage = 10;

	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode(toolCodeInput)
		    .checkOutDate(checkOutDateInput).rentalDays(chargeDaysInput).discountPercent(discountPercentage)
		    .build();

	    CheckoutResponse response = toolRentalService.checkout(request);

	    RentalAgreement rentalAgreement = response.getRentalAgreement();

	    assertThat(rentalAgreement.getTool().getCode(), is(toolCodeInput));
	    assertThat(rentalAgreement.getTool().getBrand(), is("Werner"));
	    assertThat(rentalAgreement.getTool().getType(), is(Type.LADDER));
	    assertThat(rentalAgreement.getRentalDays(), is(chargeDaysInput));
	    assertThat(rentalAgreement.getCheckOutDate(), is(checkOutDateInput));
	    assertThat(rentalAgreement.getDueDate(), is(LocalDate.of(2020, Month.JULY, 5)));
	    assertThat(rentalAgreement.getDailyRentalCharge(), is(199L));
	    assertThat(rentalAgreement.getChargeDays(), is(2));
	    assertThat(rentalAgreement.getPreDiscountCharge(), is(398L));
	    assertThat(rentalAgreement.getDiscountPercent(), is(10));
	    assertThat(rentalAgreement.getDiscountAmount(), is(40L));
	    assertThat(rentalAgreement.getFinalCharge(), is(358L));
	}

	/**
	 * Expected charge days explanation:<br>
	 * - Checked-out days that could be chargeable: Friday July 3 - Tuesday July
	 * 7<br>
	 * - CHNS -> Chainsaw; No weekend charge<br>
	 * July 4 & 5 are non-chargeable days (weekend)<br>
	 * July 3 + 6,7 are chargeable days<br>
	 * = 3 charge days
	 */
	@Test
	public void test3() {
	    String toolCodeInput = "CHNS";
	    int chargeDaysInput = 5;
	    LocalDate checkOutDateInput = LocalDate.of(2015, Month.JULY, 2);
	    int discountPercentage = 25;

	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode(toolCodeInput)
		    .checkOutDate(checkOutDateInput).rentalDays(chargeDaysInput).discountPercent(discountPercentage)
		    .build();

	    CheckoutResponse response = toolRentalService.checkout(request);

	    RentalAgreement rentalAgreement = response.getRentalAgreement();

	    assertThat(rentalAgreement.getTool().getCode(), is(toolCodeInput));
	    assertThat(rentalAgreement.getTool().getBrand(), is("Stihl"));
	    assertThat(rentalAgreement.getTool().getType(), is(Type.CHAINSAW));
	    assertThat(rentalAgreement.getRentalDays(), is(chargeDaysInput));
	    assertThat(rentalAgreement.getCheckOutDate(), is(checkOutDateInput));
	    assertThat(rentalAgreement.getDueDate(), is(LocalDate.of(2015, Month.JULY, 7)));
	    assertThat(rentalAgreement.getDailyRentalCharge(), is(149L));
	    assertThat(rentalAgreement.getChargeDays(), is(3));
	    assertThat(rentalAgreement.getPreDiscountCharge(), is(447L));
	    assertThat(rentalAgreement.getDiscountPercent(), is(25));
	    assertThat(rentalAgreement.getDiscountAmount(), is(112L));
	    assertThat(rentalAgreement.getFinalCharge(), is(335L));
	}

	/**
	 * Expected charge days explanation:<br>
	 * - Checked-out days that could be chargeable: Friday September 4 - Wednesday
	 * September 9<br>
	 * - JAKD -> Jackhammer; No weekend or holiday charge<br>
	 * Labor day is Monday September 7<br>
	 * September 5 & 6 are non-chargeable days (weekend)<br>
	 * September 7 is a non-chargeable day (holiday)<br>
	 * September 4, 8, & 9 are chargeable days<br>
	 * = 3 charge days
	 */
	@Test
	public void test4() {
	    String toolCodeInput = "JAKD";
	    int chargeDaysInput = 6;
	    LocalDate checkOutDateInput = LocalDate.of(2015, Month.SEPTEMBER, 3);
	    int discountPercentage = 0;

	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode(toolCodeInput)
		    .checkOutDate(checkOutDateInput).rentalDays(chargeDaysInput).discountPercent(discountPercentage)
		    .build();

	    CheckoutResponse response = toolRentalService.checkout(request);

	    RentalAgreement rentalAgreement = response.getRentalAgreement();

	    assertThat(rentalAgreement.getTool().getCode(), is(toolCodeInput));
	    assertThat(rentalAgreement.getTool().getBrand(), is("DeWalt"));
	    assertThat(rentalAgreement.getTool().getType(), is(Type.JACKHAMMER));
	    assertThat(rentalAgreement.getRentalDays(), is(chargeDaysInput));
	    assertThat(rentalAgreement.getCheckOutDate(), is(checkOutDateInput));
	    assertThat(rentalAgreement.getDueDate(), is(LocalDate.of(2015, Month.SEPTEMBER, 9)));
	    assertThat(rentalAgreement.getDailyRentalCharge(), is(299L));
	    assertThat(rentalAgreement.getChargeDays(), is(3));
	    assertThat(rentalAgreement.getPreDiscountCharge(), is(897L));
	    assertThat(rentalAgreement.getDiscountPercent(), is(0));
	    assertThat(rentalAgreement.getDiscountAmount(), is(0L));
	    assertThat(rentalAgreement.getFinalCharge(), is(897L));
	}

	/**
	 * Expected charge days explanation:<br>
	 * - Checked-out days that could be chargeable: Friday July 3 - Saturday July
	 * 11<br>
	 * - JAKR -> Jackhammer; No weekend or holiday charge<br>
	 * Saturday July 4 gets observed on Friday July 3<br>
	 * July 3 is a non-chargeable day (holiday)<br>
	 * July 4, 5, & 11 are non-chargeable days (weekend)<br>
	 * July 6-10 are chargeable days<br>
	 * = 5 charge days
	 */
	@Test
	public void test5() {
	    String toolCodeInput = "JAKR";
	    int chargeDaysInput = 9;
	    LocalDate checkOutDateInput = LocalDate.of(2015, Month.JULY, 2);
	    int discountPercentage = 0;

	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode(toolCodeInput)
		    .checkOutDate(checkOutDateInput).rentalDays(chargeDaysInput).discountPercent(discountPercentage)
		    .build();

	    CheckoutResponse response = toolRentalService.checkout(request);

	    RentalAgreement rentalAgreement = response.getRentalAgreement();

	    assertThat(rentalAgreement.getTool().getCode(), is(toolCodeInput));
	    assertThat(rentalAgreement.getTool().getBrand(), is("Ridgid"));
	    assertThat(rentalAgreement.getTool().getType(), is(Type.JACKHAMMER));
	    assertThat(rentalAgreement.getRentalDays(), is(chargeDaysInput));
	    assertThat(rentalAgreement.getCheckOutDate(), is(checkOutDateInput));
	    assertThat(rentalAgreement.getDueDate(), is(LocalDate.of(2015, Month.JULY, 11)));
	    assertThat(rentalAgreement.getDailyRentalCharge(), is(299L));
	    assertThat(rentalAgreement.getChargeDays(), is(5));
	    assertThat(rentalAgreement.getPreDiscountCharge(), is(1495L));
	    assertThat(rentalAgreement.getDiscountPercent(), is(0));
	    assertThat(rentalAgreement.getDiscountAmount(), is(0L));
	    assertThat(rentalAgreement.getFinalCharge(), is(1495L));
	}

	/**
	 * Expected charge days explanation:<br>
	 * - Checked-out days that could be chargeable: Friday July 3 - Monday July
	 * 6<br>
	 * - JAKR -> Jackhammer; No weekend or holiday charge<br>
	 * Saturday July 4 gets observed on Friday July 3<br>
	 * July 3 is a non-chargeable day (holiday)<br>
	 * July 4 & 5 are non-chargeable days (weekend)<br>
	 * July 6 is a chargeable day<br>
	 * = 1 charge days
	 */
	@Test
	public void test6() {
	    String toolCodeInput = "JAKR";
	    int chargeDaysInput = 4;
	    LocalDate checkOutDateInput = LocalDate.of(2020, Month.JULY, 2);
	    int discountPercentage = 50;

	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode(toolCodeInput)
		    .checkOutDate(checkOutDateInput).rentalDays(chargeDaysInput).discountPercent(discountPercentage)
		    .build();

	    CheckoutResponse response = toolRentalService.checkout(request);

	    RentalAgreement rentalAgreement = response.getRentalAgreement();

	    assertThat(rentalAgreement.getTool().getCode(), is(toolCodeInput));
	    assertThat(rentalAgreement.getTool().getBrand(), is("Ridgid"));
	    assertThat(rentalAgreement.getTool().getType(), is(Type.JACKHAMMER));
	    assertThat(rentalAgreement.getRentalDays(), is(chargeDaysInput));
	    assertThat(rentalAgreement.getCheckOutDate(), is(checkOutDateInput));
	    assertThat(rentalAgreement.getDueDate(), is(LocalDate.of(2020, Month.JULY, 6)));
	    assertThat(rentalAgreement.getDailyRentalCharge(), is(299L));
	    assertThat(rentalAgreement.getChargeDays(), is(1));
	    assertThat(rentalAgreement.getPreDiscountCharge(), is(299L));
	    assertThat(rentalAgreement.getDiscountPercent(), is(50));
	    assertThat(rentalAgreement.getDiscountAmount(), is(150L));
	    assertThat(rentalAgreement.getFinalCharge(), is(149L));
	}

	@Test
	public void test_prettyPrint() {
	    String toolCodeInput = "JAKR";
	    int chargeDaysInput = 4;
	    LocalDate checkOutDateInput = LocalDate.of(2020, Month.JULY, 2);
	    int discountPercentage = 50;

	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode(toolCodeInput)
		    .checkOutDate(checkOutDateInput).rentalDays(chargeDaysInput).discountPercent(discountPercentage)
		    .build();

	    CheckoutResponse response = toolRentalService.checkout(request);

	    RentalAgreement rentalAgreement = response.getRentalAgreement();

	    String expectedOutput = new StringBuilder().append("Tool code: JAKR\n").append("Tool type: Jackhammer\n")
		    .append("Tool brand: Ridgid\n").append("Rental days: 4\n").append("Check-out date: 07/02/20\n")
		    .append("Due date: 07/06/20\n").append("Daily rental charge: $2.99\n").append("Charge days: 1\n")
		    .append("Pre-discount charge: $2.99\n").append("Discount percent: 50%\n")
		    .append("Discount amount: $1.50\n").append("Final charge: $1.49\n").toString();

	    assertThat(rentalAgreement.prettyPrint(), is(expectedOutput));
	}
    }
}
