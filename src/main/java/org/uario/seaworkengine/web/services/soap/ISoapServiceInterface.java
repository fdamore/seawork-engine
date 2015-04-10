package org.uario.seaworkengine.web.services.soap;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.WorkerShift;

@WebService(serviceName = "SoapControllerServices")
public interface ISoapServiceInterface {

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
	 * @param shift
	 *            TODO
	 * @param date
	 *
	 * @return
	 */
	public boolean synchronizeWork(@WebParam(name = "date_request") Date date_request, @WebParam(name = "list_synch") List<WorkerShift> list_synch);

}
