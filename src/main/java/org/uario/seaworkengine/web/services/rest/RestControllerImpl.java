package org.uario.seaworkengine.web.services.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.synchmodel.WorkerShift;

public class RestControllerImpl implements IWebServiceController {

	private IWebServiceController	webcontroller;

	@Override
	public List<UserShift> getUserShiftConfiguration() {
		return this.webcontroller.getUserShiftConfiguration();
	}

	@Override
	public List<UserTask> getUserTaskConfiguration() {
		return this.getUserTaskConfiguration();
	}

	public IWebServiceController getWebcontroller() {
		return this.webcontroller;
	}

	@Override
	@GET
	@Produces("application/xml")
	@Path("/selectInitialSchedule")
	public List<InitialSchedule> selectInitialSchedule() {
		return this.webcontroller.selectInitialSchedule();
	}

	public void setWebcontroller(final IWebServiceController webcontroller) {
		this.webcontroller = webcontroller;
	}

	@Override
	public boolean synchronizeWork(final List<WorkerShift> list_synch) {
		// TODO Auto-generated method stub
		return false;
	}

}
