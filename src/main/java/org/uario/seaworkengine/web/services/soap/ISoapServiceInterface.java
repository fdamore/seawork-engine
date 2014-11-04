package org.uario.seaworkengine.web.services.soap;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.uario.seaworkengine.web.services.handler.FinalSchedule;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;

@WebService(serviceName = "SoapControllerServices")
public interface ISoapServiceInterface {

	/**
	 * Get initial schedule for each person
	 *
	 * @param date
	 * @return
	 */
	public List<InitialSchedule> selectInitialSchedule(@WebParam(name = "date") String date);

	/**
	 * Transmit final scheduler
	 *
	 * @param shift
	 *
	 * @param date
	 *
	 * @return
	 */
	public boolean setFinalSchedule(@WebParam(name = "schedule") FinalSchedule schedule, @WebParam(name = "shift") Integer shift);

}
