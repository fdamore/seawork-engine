package org.uario.seaworkengine.web.services.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.FinalSchedule;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;

public class RestControllerImpl implements IWebServiceController {

	private IWebServiceController	webcontroller;

	public IWebServiceController getWebcontroller() {
		return this.webcontroller;
	}

	@Override
	@GET
	@Produces("application/xml")
	@Path("/selectInitialSchedule")
	public List<InitialSchedule> selectInitialSchedule() {
		return this.webcontroller.selectInitialSchedule();
	}

	@Override
	@GET
	@Produces("application/xml")
	@Path("/setFinalSchedule/{schedule}/{shift}")
	public boolean setFinalSchedule(@PathParam("schedule") final FinalSchedule schedule, @PathParam("shift") final Integer shift) {
		return this.webcontroller.setFinalSchedule(schedule, null);
	}

	public void setWebcontroller(final IWebServiceController webcontroller) {
		this.webcontroller = webcontroller;
	}

}
