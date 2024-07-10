package cmiller.interview.internal.data.access.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import cmiller.interview.common.Tool;
import cmiller.interview.internal.data.ChargeableDaysDO;
import cmiller.interview.internal.data.ToolDO;
import cmiller.interview.internal.data.access.DataRetrievalService;

public class InMemoryDataRetrievalService implements DataRetrievalService {

    private Map<String, Tool> toolByCode;
    private Map<Tool.Type, ChargeableDaysDO> chargeableDaysByType;

    public InMemoryDataRetrievalService() {
	populateDatabase();
    }

    private void populateDatabase() {
	this.toolByCode = Arrays
		.asList(createNewToolFromDataset("CHNS", "Chainsaw", "Stihl"),
			createNewToolFromDataset("LADW", "Ladder", "Werner"),
			createNewToolFromDataset("JAKD", "Jackhammer", "DeWalt"),
			createNewToolFromDataset("JAKR", "Jackhammer", "Ridgid"))
		.stream().collect(Collectors.toMap(Tool::getCode, Function.identity()));
	this.chargeableDaysByType = Arrays
		.asList(newChargeableDaysDO(Tool.Type.LADDER, 199, "Yes", "Yes", "No"),
			newChargeableDaysDO(Tool.Type.CHAINSAW, 149, "Yes", "No", "Yes"),
			newChargeableDaysDO(Tool.Type.JACKHAMMER, 299, "Yes", "No", "No"))
		.stream().collect(Collectors.toMap(ChargeableDaysDO::getToolType, Function.identity()));
    }

    private static ChargeableDaysDO newChargeableDaysDO(Tool.Type toolType, int dailyCharge, String weekdayCharge,
	    String weekendCharge, String holidayCharge) {
	return new ChargeableDaysDO(toolType, dailyCharge, weekdayCharge.equals("Yes"), weekendCharge.equals("Yes"),
		holidayCharge.equals("Yes"));
    }

    private static Tool createNewToolFromDataset(String code, String type, String brand) {
	return new ToolDO.Builder().code(code).type(Tool.Type.fromString(type)).brand(brand).build();
    }

    @Override
    public Tool getToolByCode(String toolCode) {
	return toolByCode.get(toolCode);
    }

    @Override
    public ChargeableDaysDO getChargeableDaysByToolType(Tool.Type toolType) {
	return chargeableDaysByType.get(toolType);
    }
}
