package org.uario.seaworkengine.web.services.soap;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;

import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.WorkerShift;

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
	public List<Ship> listShip(@WebParam(name = "date_request") final Date date_request) {
		return this.webcontroller.listShip(date_request);
	}

	@Override
	public List<DetailScheduleShip> selectDetailScheduleShip(@WebParam(name = "date_request") final Date date_request) {
		return this.webcontroller.selectDetailScheduleShip(date_request);
	}

	@Override
	public List<DetailScheduleShip> selectDetailScheduleShipByShift(@WebParam(name = "date_request") final Date date_request,
			@WebParam(name = "no_shift") final Integer no_shift) {
		return this.webcontroller.selectDetailScheduleShipByShift(date_request, no_shift);
	}

	@Override
	public List<InitialSchedule> selectInitialSchedule(@WebParam(name = "date_request") final Date date_request) {
		return this.webcontroller.selectInitialSchedule(date_request);
	}

	public void setWebcontroller(final IWebServiceController webcontroller) {
		this.webcontroller = webcontroller;
	}

	@Override
	public boolean synchronizeWork(@WebParam(name = "date_request") final Date date_request,
			@WebParam(name = "list_synch") final List<WorkerShift> list_synch) {
		return this.webcontroller.synchronizeWork(date_request, list_synch);
	}
}
