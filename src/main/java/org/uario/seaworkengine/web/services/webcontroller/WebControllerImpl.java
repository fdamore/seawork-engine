package org.uario.seaworkengine.web.services.webcontroller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
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
	public List<InitialSchedule> selectInitialSchedule(final Date date) {

		final List<InitialSchedule> ret = new ArrayList<InitialSchedule>();

		final List<Person> list = this.personDAO.listAllPersons();
		for (final Person person : list) {

			final InitialSchedule item = new InitialSchedule();

			final Schedule schedule = this.scheduleDAO.loadSchedule(date, person.getId());
			final List<DetailInitialSchedule> details = this.scheduleDAO.loadDetailInitialScheduleByIdSchedule(schedule.getId());

			// set current object
			item.setPerson(person);
			item.setDetail_chedule(details);
			ret.add(item);

		}

		return ret;
	}

	@Override
	public boolean setFinalSchedule(final FinalSchedule final_schedule, final Integer shift) {

		try {
			this.scheduleDAO.saveListDetailFinalScheduler(final_schedule.getSchedule().getId(), shift, final_schedule.getDetail_schedule());
			return true;
		}
		catch (final Exception e) {
			return false;
		}
	}

	public void setPersonDAO(final PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public void setScheduleDAO(final ISchedule scheduleDAO) {
		this.scheduleDAO = scheduleDAO;
	}

}
