package org.uario.seaworkengine.web.services;

import java.util.List;

import org.uario.seaworkengine.web.services.handler.FinalSchedule;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;

public interface IWebServiceController {

	/**
	 * Get initial schedule for each person
	 *
	 * @param date
	 * @return
	 */
	public List<InitialSchedule> selectInitialSchedule();

	/**
	 * Transmit final scheduler
	 *
	 * @param shift
	 *            TODO
	 * @param date
	 *
	 * @return
	 */
	public boolean setFinalSchedule(FinalSchedule schedule, Integer shift);

}
