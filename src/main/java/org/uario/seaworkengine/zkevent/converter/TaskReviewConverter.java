package org.uario.seaworkengine.zkevent.converter;

import java.util.List;

import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class TaskReviewConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof DetailInitialSchedule) || (arg0 == null)) {
			return arg0;
		}

		final DetailInitialSchedule detailInitialSchedule = (DetailInitialSchedule) arg0;

		if (detailInitialSchedule.getTask() == null) {
			return arg0;
		}

		final ISchedule scheduleDAO = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);

		final List<DetailInitialSchedule> listDetail = scheduleDAO.loadDetailInitialScheduleByIdSchedule(detailInitialSchedule.getId_schedule());

		final TasksDAO taskCache = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

		final Integer id_task = detailInitialSchedule.getTask();

		final UserTask task = taskCache.loadTask(id_task);

		String taskCode = task.getCode();

		// search previous task
		if (task.getIsabsence() || task.getJustificatory()) {
			Long time = null;
			Integer minTimeIndex = null;

			for (int i = 0; i < listDetail.size(); i++) {
				if (!detailInitialSchedule.getId().equals(listDetail.get(i).getId())) {

					final Long t = detailInitialSchedule.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();

					if (((time == null) && (t >= 0)) || ((t >= 0) && ((t) < time))) {
						minTimeIndex = i;
						time = detailInitialSchedule.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
					}

				}
			}

			if (minTimeIndex != null) {
				final Integer taskIDPrev = listDetail.get(minTimeIndex).getTask();
				if (taskIDPrev != null) {
					final UserTask taskPrev = taskCache.loadTask(taskIDPrev);
					if (taskPrev != null) {
						taskCode = taskPrev.getCode() + "-" + taskCode;
					}
				}

			} else {
				// search following task
				for (int i = 0; i < listDetail.size(); i++) {
					if (!detailInitialSchedule.getId().equals(listDetail.get(i).getId())) {

						Long t;

						if (listDetail.get(i).getTime_to().getTime() > listDetail.get(i).getTime_from().getTime())

						{
							t = listDetail.get(i).getTime_from().getTime() - detailInitialSchedule.getTime_to().getTime();
						} else {
							t = listDetail.get(i).getTime_to().getTime() - detailInitialSchedule.getTime_to().getTime();
						}

						if (((time == null) && (t >= 0)) || ((t >= 0) && ((t) < time))) {
							minTimeIndex = i;
							time = detailInitialSchedule.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
						}

					}
				}
				if (minTimeIndex != null) {
					final Integer taskIDNext = listDetail.get(minTimeIndex).getTask();
					if (taskIDNext != null) {
						final UserTask taskNext = taskCache.loadTask(taskIDNext);
						if (taskIDNext != null) {
							taskCode = taskCode + "-" + taskNext.getCode();
						}
					}
				}
			}
		}

		return taskCode;

	}
}
