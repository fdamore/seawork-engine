package org.uario.seaworkengine.zkevent.converter;

import java.util.List;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class TaskOverviewConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		try {
			if (!(arg0 instanceof DetailFinalSchedule) || (arg0 == null)) {
				return arg0;
			}

			final DetailFinalSchedule detailFinalSchedule = (DetailFinalSchedule) arg0;

			final TasksDAO taskDAO = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

			if (detailFinalSchedule.getTask() == null) {

				final IShiftCache shiftCache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);

				final UserShift shift = shiftCache.getUserShift(detailFinalSchedule.getShift_type());

				// Absence Shift
				if (!shift.getPresence()) {
					return detailFinalSchedule.getDefaultTask() + "-" + shift.getCode();
				}
			}

			final ISchedule scheduleDAO = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);

			final List<DetailFinalSchedule> listDetail = scheduleDAO.loadDetailFinalScheduleByIdSchedule(detailFinalSchedule.getId_schedule());

			final Integer id_task = detailFinalSchedule.getTask();

			final UserTask task = taskDAO.loadTask(id_task);

			if (task == null) {
				return "";
			}

			String taskCode = task.getCode();

			// search previous task
			if (task.getIsabsence() || task.getJustificatory()) {
				Long time = null;
				Integer minTimeIndex = null;

				for (int i = 0; i < listDetail.size(); i++) {
					final Integer idItemTask = listDetail.get(i).getTask();
					final UserTask itemtask = taskDAO.loadTask(idItemTask);
					if (!detailFinalSchedule.getId().equals(listDetail.get(i).getId()) && !(itemtask.getIsabsence() || itemtask.getJustificatory())) {

						Long t;

						if (listDetail.get(i).getTime_to().getTime() > listDetail.get(i).getTime_from().getTime())

						{
							t = detailFinalSchedule.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
						} else {
							t = detailFinalSchedule.getTime_from().getTime() - listDetail.get(i).getTime_from().getTime();
						}

						if (((time == null) && (t >= 0)) || ((t >= 0) && ((t) < time))) {
							minTimeIndex = i;
							time = detailFinalSchedule.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
						}

					}
				}

				if (minTimeIndex != null) {
					final Integer taskIDPrev = listDetail.get(minTimeIndex).getTask();
					if (taskIDPrev != null) {
						final UserTask taskPrev = taskDAO.loadTask(taskIDPrev);
						if (taskPrev != null) {
							taskCode = taskPrev.getCode() + "-" + taskCode;
						}
					}
				} else {
					// search following task
					for (int i = 0; i < listDetail.size(); i++) {
						final Integer idItemTask = listDetail.get(i).getTask();
						final UserTask itemtask = taskDAO.loadTask(idItemTask);
						if (!detailFinalSchedule.getId().equals(listDetail.get(i).getId())
								&& !(itemtask.getIsabsence() || itemtask.getJustificatory())) {

							Long t;

							if (listDetail.get(i).getTime_to().getTime() > listDetail.get(i).getTime_from().getTime())

							{
								t = listDetail.get(i).getTime_from().getTime() - detailFinalSchedule.getTime_to().getTime();
							} else {
								t = listDetail.get(i).getTime_to().getTime() - detailFinalSchedule.getTime_to().getTime();
							}

							if (((time == null) && (t >= 0)) || ((t >= 0) && ((t) < time))) {
								minTimeIndex = i;
								time = detailFinalSchedule.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
							}

						}
					}
					if (minTimeIndex != null) {
						final Integer taskIDNext = listDetail.get(minTimeIndex).getTask();
						if (taskIDNext != null) {
							final UserTask taskNext = taskDAO.loadTask(taskIDNext);
							if (taskIDNext != null) {
								taskCode = taskNext.getCode() + "-" + taskCode;
							}
						}
					}
				}
			}

			return taskCode;
		} catch (final Exception e) {
			return "";
		}

	}
}
