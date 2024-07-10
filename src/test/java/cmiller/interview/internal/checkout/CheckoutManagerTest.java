package cmiller.interview.internal.checkout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import cmiller.interview.ToolRentalServiceException;
import cmiller.interview.ToolRentalServiceException.FailureReason;
import cmiller.interview.checkout.CheckoutRequest;
import cmiller.interview.checkout.CheckoutResponse;
import cmiller.interview.checkout.RentalAgreement;
import cmiller.interview.common.Tool.Type;
import cmiller.interview.internal.agent.HolidaysAgent;
import cmiller.interview.internal.data.ChargeableDaysDO;
import cmiller.interview.internal.data.ToolDO;
import cmiller.interview.internal.data.access.DataRetrievalService;
import cmiller.interview.internal.factory.ToolRentalServiceDependencyFactory;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CheckoutManagerTest {
    private static final int DISCOUNT_PERCENTAGE_INPUT = 50;
    private static final int RENTAL_DAYS_INPUT = 3;
    private static final long DAILY_RENTAL_AMOUNT = 100;
    private static final String SOME_BRAND = "a brand";
    private static final String TOOL_CODE_INPUT = "TEST";
    private static final Type SOME_TOOL_TYPE = Type.CHAINSAW;
    private static final LocalDate CHECKOUT_DATE_INPUT = LocalDate.of(2020, Month.JANUARY, 1); // WEDNESDAY

    private final CheckoutRequest DEFAULT_CHECKOUT_REQUEST = new CheckoutRequest.Builder().toolCode(TOOL_CODE_INPUT)
	    .checkOutDate(CHECKOUT_DATE_INPUT).rentalDays(RENTAL_DAYS_INPUT).discountPercent(DISCOUNT_PERCENTAGE_INPUT)
	    .build();

    @Mock
    private ToolRentalServiceDependencyFactory factory;
    @Mock
    private HolidaysAgent mockHolidaysAgent;
    @Mock
    private DataRetrievalService mockDataRetrievalAgent;

    private CheckoutManager manager;

    @BeforeEach
    public void beforeEach() {
	when(factory.getHolidaysAgent()).thenReturn(mockHolidaysAgent);
	when(factory.getDataRetrievalService()).thenReturn(mockDataRetrievalAgent);

	when(mockHolidaysAgent.getHolidaysForYear(CHECKOUT_DATE_INPUT.getYear())).thenReturn(new ArrayList<>());
	when(mockDataRetrievalAgent.getToolByCode(TOOL_CODE_INPUT))
		.thenReturn(new ToolDO.Builder().code(TOOL_CODE_INPUT).brand(SOME_BRAND).type(SOME_TOOL_TYPE).build());
	when(mockDataRetrievalAgent.getChargeableDaysByToolType(SOME_TOOL_TYPE))
		.thenReturn(new ChargeableDaysDO(SOME_TOOL_TYPE, DAILY_RENTAL_AMOUNT, true, true, true));

	this.manager = new CheckoutManager(factory);
    }

    @Nested
    @DisplayName("ToolRentalServiceException test cases")
    class ExceptionTestCases {
	@Test
	public void invalidInputTests_noToolCode() {
	    CheckoutRequest request = new CheckoutRequest.Builder().checkOutDate(LocalDate.of(2024, Month.JULY, 20))
		    .rentalDays(1).build();

	    try {
		manager.checkout(request);
		fail("Expected exception was not thrown");
	    } catch (ToolRentalServiceException e) {
		assertThat(e.getFailureReason(), is(FailureReason.INVALID_INPUT));
	    }
	}

	@Test
	public void invalidInputTests_noCheckoutDate() {
	    CheckoutRequest request = new CheckoutRequest.Builder().toolCode("ABC").rentalDays(1).build();

	    try {
		manager.checkout(request);
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
		manager.checkout(request);
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
		manager.checkout(request);
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
		manager.checkout(request);
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
		manager.checkout(request);
		fail("Expected exception was not thrown");
	    } catch (ToolRentalServiceException e) {
		assertThat(e.getFailureReason(), is(FailureReason.TOOL_NOT_FOUND));
	    }
	}

	@Test
	public void toolTypeHadNoChargeableDaysData() {
	    when(mockDataRetrievalAgent.getChargeableDaysByToolType(SOME_TOOL_TYPE)).thenReturn(null);

	    try {
		manager.checkout(DEFAULT_CHECKOUT_REQUEST);
		fail("Expected exception was not thrown");
	    } catch (ToolRentalServiceException e) {
		assertThat(e.getFailureReason(), is(FailureReason.INTERNAL_ERROR));
	    }
	}
    }

    @Nested
    @DisplayName("Test cases resulting in a successful rental agreement")
    class SuccessTestCases {
	@Test
	public void testSuccess_RentalAgreementFields() {
	    CheckoutResponse response = manager.checkout(DEFAULT_CHECKOUT_REQUEST);

	    RentalAgreement rentalAgreement = response.getRentalAgreement();
	    assertThat(rentalAgreement.getTool().getBrand(), is(SOME_BRAND));
	    assertThat(rentalAgreement.getTool().getCode(), is(TOOL_CODE_INPUT));
	    assertThat(rentalAgreement.getTool().getType(), is(SOME_TOOL_TYPE));
	    assertThat(rentalAgreement.getCheckOutDate(), is(CHECKOUT_DATE_INPUT));
	    assertThat(rentalAgreement.getDueDate(), is(LocalDate.of(2020, Month.JANUARY, 4)));
	    assertThat(rentalAgreement.getDailyRentalCharge(), is(DAILY_RENTAL_AMOUNT));
//	    rentalAgreement.getChargeDays() - verified in ChargeableDaysTestCases
	    assertThat(rentalAgreement.getPreDiscountCharge(), is(300L));
	    assertThat(rentalAgreement.getDiscountPercent(), is(DISCOUNT_PERCENTAGE_INPUT));
	    assertThat(rentalAgreement.getDiscountAmount(), is(150L));
	    assertThat(rentalAgreement.getFinalCharge(), is(150L));
	}

	@Test
	public void testSuccess_RoundHalfUp() {
	    when(mockDataRetrievalAgent.getChargeableDaysByToolType(SOME_TOOL_TYPE))
		    .thenReturn(new ChargeableDaysDO(SOME_TOOL_TYPE, 99, true, true, true));

	    CheckoutResponse response = manager.checkout(DEFAULT_CHECKOUT_REQUEST);

	    RentalAgreement rentalAgreement = response.getRentalAgreement();
	    assertThat(rentalAgreement.getDailyRentalCharge(), is(99L));
	    assertThat(rentalAgreement.getPreDiscountCharge(), is(297L));
	    assertThat(rentalAgreement.getDiscountPercent(), is(DISCOUNT_PERCENTAGE_INPUT));
	    assertThat(rentalAgreement.getDiscountAmount(), is(149L));
	    assertThat(rentalAgreement.getFinalCharge(), is(148L));
	}

	@Nested
	@DisplayName("Test chargeable and non-chargeable days")
	class ChargeableDaysTestCases {
	    @Test
	    public void testSuccess_AllChargeableDays() {
		// still charge, even though there's a holiday
		when(mockHolidaysAgent.getHolidaysForYear(CHECKOUT_DATE_INPUT.getYear()))
			.thenReturn(Arrays.asList(CHECKOUT_DATE_INPUT.plusDays(1)));

		CheckoutResponse response = manager.checkout(DEFAULT_CHECKOUT_REQUEST);

		RentalAgreement rentalAgreement = response.getRentalAgreement();
		assertThat(rentalAgreement.getChargeDays(), is(RENTAL_DAYS_INPUT));
	    }

	    @Test
	    public void testSuccess_WeekendsNotCharged() {
		// don't charge weekends
		when(mockDataRetrievalAgent.getChargeableDaysByToolType(SOME_TOOL_TYPE))
			.thenReturn(new ChargeableDaysDO(SOME_TOOL_TYPE, DAILY_RENTAL_AMOUNT, true, false, true));

		CheckoutRequest request = DEFAULT_CHECKOUT_REQUEST;
		CheckoutResponse response = manager.checkout(request);

		// Wednesday, Jan 1 2020 -> 3 rentals days means potential chargeable days are:
		// Thursday Jan 2 2020 - Saturday Jan 4 2020
		// Jan 4 2020 is a weekend which is not chargeable = 2 chargeable days
		RentalAgreement rentalAgreement = response.getRentalAgreement();
		assertThat(rentalAgreement.getChargeDays(), is(2));
	    }

	    @Test
	    public void testSuccess_HolidaysNotCharged() {
		when(mockHolidaysAgent.getHolidaysForYear(CHECKOUT_DATE_INPUT.getYear()))
			.thenReturn(Arrays.asList(CHECKOUT_DATE_INPUT.plusDays(1)));

		when(mockDataRetrievalAgent.getChargeableDaysByToolType(SOME_TOOL_TYPE))
			.thenReturn(new ChargeableDaysDO(SOME_TOOL_TYPE, DAILY_RENTAL_AMOUNT, true, true, false));

		CheckoutResponse response = manager.checkout(DEFAULT_CHECKOUT_REQUEST);

		RentalAgreement rentalAgreement = response.getRentalAgreement();
		assertThat(rentalAgreement.getChargeDays(), is(2));
	    }
	}
    }
}
