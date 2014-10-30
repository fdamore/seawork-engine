package org.uario.seaworkengine.web.services.soap;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;

import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.FinalSchedule;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;

public class SoapControllerImpl implements ISoapServiceInterface {

	private IWebServiceController	webcontroller;

	public IWebServiceController getWebcontroller() {
		return this.webcontroller;
	}

	@Override
	public List<InitialSchedule> selectInitialSchedule(@WebParam(name = "date") final Date date) {
		return this.webcontroller.selectInitialSchedule(date);
	}

	@Override
	public boolean setFinalSchedule(@WebParam(name = "schedule") final FinalSchedule schedule, @WebParam(name = "shift") final Integer shift) {
		return this.setFinalSchedule(schedule, shift);
	}

	public void setWebcontroller(final IWebServiceController webcontroller) {
		this.webcontroller = webcontroller;
	}

}
