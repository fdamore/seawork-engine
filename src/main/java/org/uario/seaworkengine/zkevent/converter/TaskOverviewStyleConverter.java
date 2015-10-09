package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.TaskColor;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class TaskOverviewStyleConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if ((arg0 == null)) {
			return arg0;
		}

		if (arg0 instanceof DetailFinalSchedule) {
			final ITaskCache taskCache = (ITaskCache) SpringUtil.getBean(BeansTag.TASK_CACHE);

			final DetailFinalSchedule detailFinalSchedule = (DetailFinalSchedule) arg0;

			final UserTask task = taskCache.getUserTask(detailFinalSchedule.getTask());

			if (task != null) {
				if (task.getIsabsence()) {
					return "color:" + TaskColor.ANBSENCE_COLOR;
				} else if (task.getJustificatory()) {
					return "color:" + TaskColor.JUSTIFICATORY_COLOR;
				}
			} else {
				final IShiftCache shiftCache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);

				final UserShift shift = shiftCache.getUserShift(detailFinalSchedule.getShift_type());

				// Absence Shift
				if (!shift.getPresence()) {
					return "color:" + TaskColor.ANBSENCE_COLOR;
				}

			}
		} else if (arg0 instanceof DetailInitialSchedule) {
			final ITaskCache taskCache = (ITaskCache) SpringUtil.getBean(BeansTag.TASK_CACHE);

			final DetailInitialSchedule detailInitialSchedule = (DetailInitialSchedule) arg0;

			final UserTask task = taskCache.getUserTask(detailInitialSchedule.getTask());

			if (task != null) {
				if (task.getIsabsence()) {
					return "color:" + TaskColor.ANBSENCE_COLOR;
				} else if (task.getJustificatory()) {
					return "color:" + TaskColor.JUSTIFICATORY_COLOR;
				}
			} else {
				final IShiftCache shiftCache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);

				final UserShift shift = shiftCache.getUserShift(detailInitialSchedule.getShift_type());

				// Absence Shift
				if (!shift.getPresence()) {
					return "color:" + TaskColor.ANBSENCE_COLOR;
				}

			}
		}

		return " ";

	}
}
