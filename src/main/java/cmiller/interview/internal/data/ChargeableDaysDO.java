package cmiller.interview.internal.data;

import cmiller.interview.common.Tool;

public class ChargeableDaysDO {
    private final Tool.Type toolType;
    private final long dailyCharge;
    private final boolean weekendCharge;
    private final boolean holidayCharge;

    public ChargeableDaysDO(Tool.Type toolType, long dailyCharge, boolean weekdayCharge, boolean weekendCharge,
	    boolean holidayCharge) {
	this.toolType = toolType;
	this.dailyCharge = dailyCharge;
	this.weekendCharge = weekendCharge;
	this.holidayCharge = holidayCharge;
    }

    @Override
    public String toString() {
	return "ChargeableDaysDO [toolType=" + toolType + ", dailyCharge=" + dailyCharge + ", weekendCharge="
		+ weekendCharge + ", holidayCharge=" + holidayCharge + "]";
    }

    public Tool.Type getToolType() {
	return toolType;
    }

    public long getDailyCharge() {
	return dailyCharge;
    }

    public boolean isWeekendCharge() {
	return weekendCharge;
    }

    public boolean isHolidayCharge() {
	return holidayCharge;
    }
}
