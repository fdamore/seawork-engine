package org.uario.seaworkengine.web.services;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.statistics.impl.MonitorData;
import org.uario.seaworkengine.web.services.handler.Badge;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;

public interface IWebServiceController {

	/**
	 * @param badge
	 */
	void createBadge(Badge badge);

	/**
	 * create detail final schedule
	 *
	 * @param detailFinalScheduleShip
	 */
	void createDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip);

	/**
	 * @param id_badge
	 */
	void deleteBadge(Integer id_badge);

	/**
	 * delete details final id
	 *
	 * @param id_detail_final
	 */
	void deleteDetailFinalScheduleShipById(Integer id_detail_final);

	/**
	 * Get CT Task
	 *
	 * @return
	 */
	UserTask getCTTask();

	/**
	 * @return
	 */
	UserTask getDelayOperation();

	/**
	 * Public string get note
	 *
	 * @param id_schedule
	 * @return
	 */
	String getDetailScheduleShipNote(Integer id_schedule);

	/**
	 * @return
	 */
	UserTask getEndOperationTask();

	/**
	 * Get Monitor data
	 *
	 * @param request
	 * @return
	 */
	List<MonitorData> getMonitorData(Date request);

	/**
	 * @return
	 */
	UserTask getOverflowTask();

	/**
	 * Public string get note
	 *
	 * @param id_schedule
	 * @return
	 */
	String getScheduleNote(Integer id_schedule);

	/**
	 * Get list of ship schedulated today
	 *
	 * @param date_request
	 * @return
	 */
	List<Ship> listShip(Date date_request);

	/**
	 * list detail schedule ship
	 *
	 * @param idDetailScheduleShip
	 * @return
	 */
	List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(Integer idDetailScheduleShip);

	/**
	 * @param id_schedule
	 * @return
	 */
	List<Badge> loadListBadge(Integer id_schedule);

	/**
	 * Get initial schedule for each person
	 *
	 * @param status_selection TODO
	 * @param date
	 *
	 * @return
	 */
	List<InitialSchedule> selectInitialSchedule(Date date_request, Integer status_selection);

	/**
	 * Return Detail Scheduler
	 *
	 * @param date_request
	 * @param shift        TODO
	 * @return
	 */
	List<DetailScheduleShip> selectInitialShipSchedule(Date date_request, Integer shift);

	/**
	 * @param date_request
	 * @param shift
	 * @param id_ship
	 * @return
	 */
	DetailScheduleShip selectInitialShipSchedule(Date date_request, Integer shift, Integer id_ship);

	/**
	 * Update scheduler ship for mobile
	 *
	 * @param detail_schedule_ship_id
	 * @param operation
	 * @param menwork
	 */
	void updateDetailScheduleShipForMobile(Integer detail_schedule_ship_id, String operation, Integer handswork);

	/**
	 * Public string get note
	 *
	 * @param id_schedule
	 * @return
	 */
	void updateDetailScheduleShipNote(Integer id_schedule, String note);

	/**
	 * Public string get note
	 *
	 * @param id_schedule
	 */
	void updateScheduleNote(Integer id_schedule, String note);

}
