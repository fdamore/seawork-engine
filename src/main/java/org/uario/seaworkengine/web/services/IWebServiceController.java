package org.uario.seaworkengine.web.services;

import java.util.Date;
import java.util.List;

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

public interface IWebServiceController {

	/**
	 * Calculate user saturation
	 *
	 * @param date_request
	 *            TODO
	 *
	 * @return
	 */
	public List<UserStaturation> calculateUserSaturation(Date date_request);

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
	public void createBadge(Badge badge);

	/**
	 * create detail final schedule
	 *
	 * @param detailFinalScheduleShip
	 */
	public void createDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip);

	/**
	 * @param id_badge
	 */
	public void deleteBadge(Integer id_badge);

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
	 * Public string get note
	 *
	 * @param id_schedule
	 * @return
	 */
	public String getDetailScheduleShipNote(Integer id_schedule);

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
	public List<MonitorData> getMonitorData(Date request);

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
	public String getScheduleNote(Integer id_schedule);

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
	public List<Ship> listShip(Date date_request);

	/**
	 * list detail schedule ship
	 *
	 * @param idDetailScheduleShip
	 * @return
	 */
	public List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(Integer idDetailScheduleShip);

	/**
	 * @param id_schedule
	 * @return
	 */
	public List<Badge> loadListBadge(Integer id_schedule);

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
	public List<InitialSchedule> selectInitialSchedule(Date date_request);

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
	public boolean synchronizeWork(Date date_request, Integer no_shift, WorkerShift worker_shift);

	/**
	 * Update scheduler ship for mobile
	 *
	 * @param detail_schedule_ship_id
	 * @param operation
	 * @param menwork
	 */
	public void updateDetailScheduleShipForMobile(Integer detail_schedule_ship_id, String operation, Integer handswork, Integer menwork,
	        String temperature, String sky, String rain, String wind, Date first_down, Date last_down, Date person_down, Date person_onboard);

	/**
	 * Public string get note
	 *
	 * @param id_schedule
	 * @return
	 */
	public void updateDetailScheduleShipNote(Integer id_schedule, String note);

	/**
	 * Public string get note
	 *
	 * @param id_schedule
	 */
	public void updateScheduleNote(Integer id_schedule, String note);

}
