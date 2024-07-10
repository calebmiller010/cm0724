package cmiller.interview.internal.data.access;

import cmiller.interview.common.Tool;
import cmiller.interview.internal.data.ChargeableDaysDO;
import cmiller.interview.internal.data.access.impl.InMemoryDataRetrievalService;

public interface DataRetrievalService {

    /**
     * @param toolCode the unique identifier of the tool
     * @return the tool, if found. Returns null if the tool was not found by the
     *         specified code.
     */
    public Tool getToolByCode(String toolCode);

    /**
     * @param toolType the type of the tool
     * @return the {@link ChargeableDaysDO chargeable days information}, if found.
     *         Returns null if it could not be found for the specified type.
     */
    public ChargeableDaysDO getChargeableDaysByToolType(Tool.Type toolType);

    public class Factory {
	public static DataRetrievalService getService() {
	    return new InMemoryDataRetrievalService();
	}
    }

}
