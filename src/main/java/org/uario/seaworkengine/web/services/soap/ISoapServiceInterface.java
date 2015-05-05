package org.uario.seaworkengine.web.services.soap;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.WorkerShift;

@WebService(serviceName = "SoapControllerServices")
public interface ISoapServiceInterface {

	/**
	 * @param detailFinalScheduleShip
	 */
	public void createDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip);

	/**
	 * delete details final id
	 *
	 * @param id_detail_final
	 */
	public void deleteDetailFinalScheduleShipById(Integer id_detail_final);

	/**
	 * Public string get note
	 *
	 * @param id_schedule
	 * @return
	 */
	public String getScheduleNote(@WebParam(name = "id_schedule") Integer id_schedule);

	/**
	 * Get Configuration User shift
	 *
	 * @return
	 */
	public List<UserShift> getUserShiftConfiguration();

	/**
	 * Get Configuration User task
	 *
	 * @return
	 */
	public List<UserTask> getUserTaskConfiguration();

	/**
	 * Get list of ship schedulated today
	 *
	 * @param date_request
	 * @return
	 */
	public List<Ship> listShip(@WebParam(name = "date_request") Date date_request);

	/**
	 * list detail schedule ship
	 *
	 * @param idDetailScheduleShip
	 * @return
	 */
	public List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(
			@WebParam(name = "idDetailScheduleShip") Integer idDetailScheduleShip);

	/**
	 * @param id_schedule
	 * @return
	 */
	public List<Date> loadScheduleStartEndTime(@WebParam(name = "id_schedule") Integer id_schedule);

	/**
	 * Return Detail Scheduler
	 *
	 * @param date_request
	 * @return
	 */
	public List<DetailScheduleShip> selectDetailScheduleShipByShiftDate(Date date_request);

	/**
	 * Get initial schedule for each person
	 *
	 * @param date
	 * @return
	 */
	public List<InitialSchedule> selectInitialSchedule(@WebParam(name = "date_request") Date date_request);

	/**
	 * Transmit final scheduler
	 *
	 * @param no_shift
	 *            TODO
	 * @param shift
	 * @param date
	 *
	 * @return
	 */
	public boolean synchronizeWork(@WebParam(name = "date_request") Date date_request, @WebParam(name = "no_shift") Integer no_shift,
			@WebParam(name = "worker_shift") WorkerShift worker_shift);

	/**
	 * Update info about mobile: detail schedule ship
	 *
	 * @param detailFinalScheduleShip
	 */
	void updateDetailFinalScheduleShipForMobile(DetailFinalScheduleShip detailFinalScheduleShip);

	/**
	 * Update End Time
	 *
	 * @param id_schedule
	 * @param endTime
	 */
	public void updateEndTimeSchedule(@WebParam(name = "id_schedule") Integer id_schedule, @WebParam(name = "endTime") Date endTime);

	/**
	 * Public string get note
	 *
	 * @param id_schedule
	 */
	public void updateScheduleNote(@WebParam(name = "id_schedule") Integer id_schedule, @WebParam(name = "note") String note);

	/**
	 * Update start time
	 *
	 * @param id_schedule
	 * @param startTime
	 */
	public void updateStartTimeSchedule(@WebParam(name = "id_schedule") Integer id_schedule, @WebParam(name = "startTime") Date startTime);

}
