package cmiller.interview.internal.agent;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidaysAgent {

    // avoids redundant building out of this list for the same year
    private Map<Integer, List<LocalDate>> cache = new HashMap<>();

    public List<LocalDate> getHolidaysForYear(int year) {
	List<LocalDate> holidays = cache.get(year);

	if (holidays == null) {
	    holidays = createHolidayListForYear(year);
	    cache.put(year, holidays);
	}

	return holidays;
    }

    private List<LocalDate> createHolidayListForYear(int year) {
	//@formatter:off
	return Arrays.asList(
		// expand with other holidays in future
		fourthOfJuly(year), 
		laborDay(year)
	);
	//@formatter:on
    }

    private static LocalDate fourthOfJuly(int year) {
	LocalDate independenceDay = LocalDate.of(year, Month.JULY, 4);
	DayOfWeek dayOfWeek = independenceDay.getDayOfWeek();
	if (dayOfWeek == DayOfWeek.SATURDAY) {
	    return independenceDay.minusDays(1);
	} else if (dayOfWeek == DayOfWeek.SUNDAY) {
	    return independenceDay.plusDays(1);
	}
	return independenceDay;
    }

    private static LocalDate laborDay(int year) {
	return LocalDate.of(year, Month.SEPTEMBER, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
    }
}
