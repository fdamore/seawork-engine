package org.uario.seaworkengine.web.services.soap;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;

import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.statistics.impl.MonitorData;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.Badge;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.WorkerShift;

public class SoapControllerImpl implements ISoapServiceInterface {

	private IWebServiceController webcontroller;

	@Override
	public void createBadge(final Badge badge) {
		this.webcontroller.createBadge(badge);

	}

	@Override
	public void createDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip) {
		this.webcontroller.createDetailFinalScheduleShip(detailFinalScheduleShip);

	}

	@Override
	public void deleteBadge(final Integer id_badge) {
		this.webcontroller.deleteBadge(id_badge);

	}

	@Override
	public void deleteDetailFinalScheduleShipById(final Integer id_detail_final) {
		this.webcontroller.deleteDetailFinalScheduleShipById(id_detail_final);
	}

	@Override
	public UserTask getCTTask() {

		return this.webcontroller.getCTTask();

	}

	@Override
	public UserTask getDelayOperation() {
		return this.webcontroller.getDelayOperation();
	}

	@Override
	public UserTask getEndOperationTask() {
		return this.webcontroller.getEndOperationTask();
	}

	@Override
	public List<MonitorData> getMonitorData(final Date request) {
		return this.webcontroller.getMonitorData(request);
	}

	@Override
	public UserTask getOverflowTask() {
		return this.webcontroller.getOverflowTask();
	}

	@Override
	public String getScheduleNote(final Integer id_schedule) {
		return this.webcontroller.getScheduleNote(id_schedule);
	}

	@Override
	public List<UserShift> getUserShiftConfiguration() {
		return this.webcontroller.getUserShiftConfiguration();
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
	public List<Badge> loadListBudge(final Integer id_schedule) {
		return this.webcontroller.loadListBadge(id_schedule);
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
	public void updateDetailScheduleShipForMobile(@WebParam(name = "detail_schedule_ship_id") final Integer detail_schedule_ship_id,
			@WebParam(name = "operation") final String operation, @WebParam(name = "handswork") final Integer handswork,
			@WebParam(name = "temperature") final String temperature, @WebParam(name = "sky") final String sky,
			@WebParam(name = "rain") final String rain, @WebParam(name = "wind") final String wind,
			@WebParam(name = "first_down") final Date first_down, @WebParam(name = "last_down") final Date last_down) {

		this.webcontroller.updateDetailScheduleShipForMobile(detail_schedule_ship_id, operation, handswork, temperature, sky, rain, wind, first_down,
				last_down);

	}

	@Override
	public void updateScheduleNote(final Integer id_schedule, final String note) {
		this.webcontroller.updateScheduleNote(id_schedule, note);

	}

}
