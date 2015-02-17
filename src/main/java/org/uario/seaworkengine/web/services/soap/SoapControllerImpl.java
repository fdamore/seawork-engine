package org.uario.seaworkengine.web.services.soap;

import java.util.List;

import javax.jws.WebParam;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.synchmodel.WorkerShift;

public class SoapControllerImpl implements ISoapServiceInterface {

	private IWebServiceController	webcontroller;

	@Override
	public List<UserShift> getUserShiftConfiguration() {
		return this.webcontroller.getUserShiftConfiguration();
	}

	@Override
	public List<UserTask> getUserTaskConfiguration() {
		return this.webcontroller.getUserTaskConfiguration();
	}

	public IWebServiceController getWebcontroller() {
		return this.webcontroller;
	}

	@Override
	public List<InitialSchedule> selectInitialSchedule() {
		return this.webcontroller.selectInitialSchedule();
	}

	public void setWebcontroller(final IWebServiceController webcontroller) {
		this.webcontroller = webcontroller;
	}

	@Override
	public boolean synchronizeWork(@WebParam(name = "list_synch") final List<WorkerShift> list_synch) {
		return this.webcontroller.synchronizeWork(list_synch);
	}
}
