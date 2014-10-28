package org.uario.seaworkengine.web.services.rest;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.FinalSchedule;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;

/**
 * User: Francesco Marasco Date: 28/06/12 Time: 16.45 Implements soap services
 */

@WebService(serviceName = "ControllerServices")
public class RestControllerImpl implements IWebServiceController {

	IWebServiceController	webcontroller;

	public IWebServiceController getWebcontroller() {
		return this.webcontroller;
	}

	@Override
	public List<InitialSchedule> selectInitialSchedul(final Date date) {
		return this.webcontroller.selectInitialSchedul(date);
	}

	@Override
	public boolean setFinalSchedule(final FinalSchedule schedule) {
		return this.webcontroller.setFinalSchedule(schedule);
	}

	public void setWebcontroller(final IWebServiceController webcontroller) {
		this.webcontroller = webcontroller;
	}

}
