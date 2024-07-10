package cmiller.interview.internal.factory;

import cmiller.interview.ToolRentalService;
import cmiller.interview.internal.ToolRentalServiceImpl;
import cmiller.interview.internal.agent.HolidaysAgent;
import cmiller.interview.internal.data.access.DataRetrievalService;
import cmiller.interview.internal.data.access.impl.InMemoryDataRetrievalService;

public class ToolRentalServiceDependencyFactory {
    public ToolRentalService getToolRentalServiceImpl() {
	return new ToolRentalServiceImpl(this);
    }

    public DataRetrievalService getDataRetrievalService() {
	return new InMemoryDataRetrievalService();
    }

    public HolidaysAgent getHolidaysAgent() {
	return new HolidaysAgent();
    }
}
