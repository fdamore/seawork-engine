package org.uario.seaworkengine.web.services;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.WorkerShift;

public interface IWebServiceController {

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
	public List<Ship> listShip(Date date_request);

	/**
	 * Return Detail Scheduler
	 *
	 * @param date_request
	 * @return
	 */
	public List<DetailScheduleShip> selectDetailScheduleShip(Date date_request);

	/**
	 * Return detail schedule shift
	 * 
	 * @param date_request
	 * @param no_shift
	 * @return
	 */
	public List<DetailScheduleShip> selectDetailScheduleShipByShift(Date date_request, Integer no_shift);

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
	 * @param shift
	 *            TODO
	 * @param date
	 *
	 * @return
	 */
	public boolean synchronizeWork(Date date_request, List<WorkerShift> list_synch);

}
