package org.uario.seaworkengine.statistics.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.statistics.IStatProcedure;
import org.uario.seaworkengine.statistics.RateShift;

public class StatProceduresImpl implements IStatProcedure {

	private ISchedule	myScheduleDAO;

	private TasksDAO	myTaskDAO;

	private IStatistics	statisticDAO;

	public ISchedule getMyScheduleDAO() {
		return this.myScheduleDAO;
	}

	public TasksDAO getMyTaskDAO() {
		return this.myTaskDAO;
	}

	/**
	 * Get the right shift number for a given day
	 *
	 * @param current_date_scheduled
	 * @param user
	 * @return
	 */
	private Integer getShiftNoForDay(final Date current_date_scheduled, final Integer user) {

		// get info from last shift
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(current_date_scheduled);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		final Integer last_shift = this.myScheduleDAO.getLastShift(calendar.getTime());

		final RateShift[] averages = this.statisticDAO.getAverageForShift(user, current_date_scheduled);

		// get a shift - 12 after last shift
		Integer my_shift = -1;
		if ((averages == null) || (averages.length == 0)) {

			int min = 1;
			if (((last_shift != null) && (last_shift == 3))) {
				min = 2;
			} else if (((last_shift != null) && (last_shift == 4))) {
				min = 3;
			}

			my_shift = min + (int) (Math.random() * 4);

		} else {
			if ((last_shift == null) || (last_shift == 1) || (last_shift == 2)) {
				my_shift = averages[0].getShift();
			}

			else {

				// set current shift
				my_shift = averages[0].getShift();

				final int cutoff = last_shift - 2;

				for (int i = 0; i < averages.length; i++) {

					final RateShift current_shift = averages[i];
					if (current_shift.getShift() > cutoff) {
						my_shift = averages[i].getShift();
						break;
					}

				}

			}
		}
		return my_shift;
	}

	public IStatistics getStatisticDAO() {
		return this.statisticDAO;
	}

	public void setMyScheduleDAO(final ISchedule myScheduleDAO) {
		this.myScheduleDAO = myScheduleDAO;
	}

	public void setMyTaskDAO(final TasksDAO myTaskDAO) {
		this.myTaskDAO = myTaskDAO;
	}

	public void setStatisticDAO(final IStatistics statisticDAO) {
		this.statisticDAO = statisticDAO;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.uario.seaworkengine.statistics.IStatProcedure#workAssignProcedure
	 * (org.uario.seaworkengine.model.UserShift, java.util.Date,
	 * java.lang.Integer)
	 */
	@Override
	public void workAssignProcedure(final UserShift shift, final Date current_date_scheduled, final Integer user) {

		final Date truncDate = DateUtils.truncate(current_date_scheduled, Calendar.DATE);

		// refresh info about just saved schedule
		final Schedule schedule = this.myScheduleDAO.loadSchedule(truncDate, user);

		// if the shift is an absence, delete all details
		if (!shift.getPresence().booleanValue()) {
			this.myScheduleDAO.removeAllDetailInitialScheduleBySchedule(schedule.getId());
		} else {

			// check if there is any default task (MANSIONE STANDARD)
			final UserTask task_default = this.myTaskDAO.getDefault(user);
			if (task_default == null) {
				return;
			}

			// get a shift for a day
			final Integer my_no_shift = this.getShiftNoForDay(truncDate, user);

			final List<DetailInitialSchedule> details = new ArrayList<DetailInitialSchedule>();

			final DetailInitialSchedule item = new DetailInitialSchedule();
			item.setId_schedule(schedule.getId());
			item.setShift(my_no_shift);
			item.setTask(task_default.getId());
			item.setTime(6.0);
			details.add(item);

			// remove all detail in any shift
			this.myScheduleDAO.removeAllDetailInitialScheduleBySchedule(schedule.getId());

			// create detail
			this.myScheduleDAO.createDetailInitialSchedule(item);

		}

	}
}
