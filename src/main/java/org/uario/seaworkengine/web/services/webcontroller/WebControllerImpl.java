package org.uario.seaworkengine.web.services.webcontroller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.FinalSchedule;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;

public class WebControllerImpl implements IWebServiceController {

	private PersonDAO	personDAO;

	private ISchedule	scheduleDAO;

	private IShiftCache	shiftCache;

	private ITaskCache	taskCache;

	public PersonDAO getPersonDAO() {
		return this.personDAO;
	}

	public ISchedule getScheduleDAO() {
		return this.scheduleDAO;
	}

	public IShiftCache getShiftCache() {
		return this.shiftCache;
	}

	public ITaskCache getTaskCache() {
		return this.taskCache;
	}

	@Override
	public List<UserShift> getUserShiftConfiguration() {
		return new ArrayList<UserShift>(this.shiftCache.getHash().values());

	}

	@Override
	public List<UserTask> getUserTaskConfiguration() {
		return new ArrayList<UserTask>(this.taskCache.getHash().values());
	}

	@Override
	public List<InitialSchedule> selectInitialSchedule() {

		final List<InitialSchedule> ret = new ArrayList<InitialSchedule>();

		final Date date_schedule = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);

		final List<Person> list = this.personDAO.listAllPersonsForMobile(date_schedule);

		for (final Person person : list) {

			final InitialSchedule item = new InitialSchedule();

			final Schedule schedule = this.scheduleDAO.loadSchedule(date_schedule, person.getId());
			if (schedule == null) {
				continue;
			}

			List<DetailInitialSchedule> details = null;
			if ((schedule.getSync_mobile() == null) || !schedule.getSync_mobile()) {
				details = this.scheduleDAO.loadDetailInitialScheduleForMobileByIdSchedule(schedule.getId());
			} else {
				details = this.scheduleDAO.loadDetailFinalScheduleForMobileByIdSchedule(schedule.getId());
			}

			if ((details == null) || (details.size() == 0)) {
				continue;
			}

			// set current object
			item.setPerson(person);
			item.setDetail_schedule(details);
			item.setSchedule(schedule);

			ret.add(item);

		}

		return ret;
	}

	@Override
	public boolean setFinalSchedule(final FinalSchedule final_schedule, final Integer shift) {

		try {
			this.scheduleDAO.saveListDetailFinalScheduler(final_schedule.getSchedule().getId(), shift, final_schedule.getDetail_schedule());
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public void setPersonDAO(final PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public void setScheduleDAO(final ISchedule scheduleDAO) {
		this.scheduleDAO = scheduleDAO;
	}

	public void setShiftCache(final IShiftCache shiftCache) {
		this.shiftCache = shiftCache;
	}

	public void setTaskCache(final ITaskCache taskCache) {
		this.taskCache = taskCache;
	}

}
