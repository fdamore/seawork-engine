package org.uario.seaworkengine.web.services;

import java.util.Date;
import java.util.List;

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
