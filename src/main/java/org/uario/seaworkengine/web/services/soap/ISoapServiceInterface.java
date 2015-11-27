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
import org.uario.seaworkengine.statistics.impl.MonitorData;
import org.uario.seaworkengine.web.services.handler.Badge;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.UserStaturation;
import org.uario.seaworkengine.web.services.handler.WorkerShift;

@WebService(serviceName = "SoapControllerServices")
public interface ISoapServiceInterface {

	/**
	 * Calculate user saturation
	 * 
	 * @param date_request
	 *            TODO
	 *
	 * @return
	 */
	public List<UserStaturation> calculateUserSaturation(@WebParam(name = "date_request") Date date_request);

	/**
	 * Check for user abilitation
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public Boolean checkUser(String username, String password);

	/**
	 * @param badge
	 */
	public void createBadge(@WebParam(name = "badge") Badge badge);

	/**
	 * @param detailFinalScheduleShip
	 */
	public void createDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip);

	/**
	 * @param id_badge
	 */
	public void deleteBadge(@WebParam(name = "id_badge") Integer id_badge);

	/**
	 * delete details final id
	 *
	 * @param id_detail_final
	 */
	public void deleteDetailFinalScheduleShipById(Integer id_detail_final);

	/**
	 * Get CT Task
	 *
	 * @return
	 */
	public UserTask getCTTask();

	/**
	 * @return
	 */
	public UserTask getDelayOperation();

	/**
	 * @return
	 */
	public UserTask getEndOperationTask();

	/**
	 * Get Monitor data
	 *
	 * @param request
	 * @return
	 */
	public List<MonitorData> getMonitorData(@WebParam(name = "request") Date request);

	/**
	 * @return
	 */
	public UserTask getOverflowTask();

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
	public List<Badge> loadListBudge(@WebParam(name = "id_schedule") Integer id_schedule);

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
	 *            *
	 * @param shift
	 * @param date
	 *
	 * @return
	 */
	public boolean synchronizeWork(@WebParam(name = "date_request") Date date_request, @WebParam(name = "no_shift") Integer no_shift,
			@WebParam(name = "worker_shift") WorkerShift worker_shift);

	/**
	 * Update scheduler ship for mobile
	 *
	 * @param detail_schedule_ship_id
	 * @param operation
	 * @param menwork
	 */
	public void updateDetailScheduleShipForMobile(@WebParam(name = "detail_schedule_ship_id") Integer detail_schedule_ship_id,
			@WebParam(name = "operation") String operation, @WebParam(name = "handswork") Integer handswork,
			@WebParam(name = "menwork") final Integer menwork, @WebParam(name = "temperature") String temperature,
			@WebParam(name = "sky") String sky, @WebParam(name = "rain") String rain, @WebParam(name = "wind") String wind,
			@WebParam(name = "first_down") Date first_down, @WebParam(name = "last_down") Date last_down,
			@WebParam(name = "person_down") Date person_down, @WebParam(name = "person_onboard") Date person_onboard);

	/**
	 * Public string get note
	 *
	 * @param id_schedule
	 */
	public void updateScheduleNote(@WebParam(name = "id_schedule") Integer id_schedule, @WebParam(name = "note") String note);

}
