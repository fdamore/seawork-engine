package org.uario.seaworkengine.zkevent.converter;

import java.util.concurrent.TimeUnit;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class HoursAndMinutesConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if ((arg0 == null) || !((arg0 instanceof DetailFinalSchedule) || (arg0 instanceof DetailInitialSchedule))) {
			return arg0;
		}

		if (arg0 instanceof DetailFinalSchedule) {
			final DetailFinalSchedule source = (DetailFinalSchedule) arg0;

			if ((source.getDateTo() == null) || (source.getDateFrom() == null)) {
				return source;
			}

			final TasksDAO taskDao = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

			final Integer id_task = source.getTask();

			final UserTask task = taskDao.loadTask(id_task);

			if (task.getIsabsence()) {
				return ("00:00");
			}

			final Long milliseconds;

			if (source.getDateTo().getTime() > source.getDateFrom().getTime()) {
				milliseconds = source.getDateTo().getTime() - source.getDateFrom().getTime();
			} else {
				milliseconds = source.getDateFrom().getTime() - source.getDateTo().getTime();
			}

			return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
					TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));

		} else {
			final DetailInitialSchedule source = (DetailInitialSchedule) arg0;

			if ((source.getDateTo() == null) || (source.getDateFrom() == null)) {
				return source;
			}

			final TasksDAO taskDao = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

			final Integer id_task = source.getTask();

			final UserTask task = taskDao.loadTask(id_task);

			if (task.getIsabsence()) {
				return ("00:00");
			}

			final Long milliseconds = source.getDateTo().getTime() - source.getDateFrom().getTime();

			return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
					TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));

		}

	}
}
