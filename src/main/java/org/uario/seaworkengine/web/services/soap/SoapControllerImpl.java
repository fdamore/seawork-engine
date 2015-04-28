package org.uario.seaworkengine.web.services.soap;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;

import org.uario.seaworkengine.model.DetailFinalScheduleShip;
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
	public void createDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip) {
		this.webcontroller.createDetailFinalScheduleShip(detailFinalScheduleShip);

	}

	@Override
	public void deleteDetailFinalScheduleShipById(final Integer id_detail_final) {
		this.webcontroller.deleteDetailFinalScheduleShipById(id_detail_final);
	}

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
	public List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(final Integer idDetailScheduleShip) {
		return this.webcontroller.loadDetailFinalScheduleShipByIdDetailScheduleShip(idDetailScheduleShip);
	}

	@Override
	public List<DetailScheduleShip> selectDetailScheduleShipByShiftDate(@WebParam(name = "date_request") final Date date_request) {
		return this.webcontroller.selectDetailScheduleShipByShiftDate(date_request);
	}

	@Override
	public List<InitialSchedule> selectInitialSchedule(@WebParam(name = "date_request") final Date date_request) {
		return this.webcontroller.selectInitialSchedule(date_request);
	}

	public void setWebcontroller(final IWebServiceController webcontroller) {
		this.webcontroller = webcontroller;
	}

	@Override
	public boolean synchronizeWork(@WebParam(name = "date_request") final Date date_request, @WebParam(name = "no_shift") final Integer no_shift,
			@WebParam(name = "worker_shift") final WorkerShift worker_shift) {

		return this.webcontroller.synchronizeWork(date_request, no_shift, worker_shift);

	}

	@Override
	public void updateDetailFinalScheduleShipForMobile(final DetailFinalScheduleShip detailFinalScheduleShip) {
		this.webcontroller.updateDetailFinalScheduleShipForMobile(detailFinalScheduleShip);

	}
}
