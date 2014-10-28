package org.uario.seaworkengine.web.services.webcontroller;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.FinalSchedule;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;

public class WebControllerImpl implements IWebServiceController {

	private PersonDAO	personDAO;
	private ISchedule	scheduleDAO;

	public PersonDAO getPersonDAO() {
		return this.personDAO;
	}

	public ISchedule getScheduleDAO() {
		return this.scheduleDAO;
	}

	@Override
	public List<InitialSchedule> selectInitialSchedul(final Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setFinalSchedule(final FinalSchedule schedule) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setPersonDAO(final PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public void setScheduleDAO(final ISchedule scheduleDAO) {
		this.scheduleDAO = scheduleDAO;
	}

}
