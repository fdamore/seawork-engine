package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
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

			final TasksDAO				task_dao			= (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

			final DetailFinalSchedule	detailFinalSchedule	= (DetailFinalSchedule) arg0;

			final UserTask				task				= task_dao.loadTask(detailFinalSchedule.getTask());

			if (task != null) {
				if (task.getIsabsence()) {
					return "color:" + TaskColor.ANBSENCE_COLOR;
				} else if (task.getJustificatory()) {
					return "color:" + TaskColor.JUSTIFICATORY_COLOR;
				}
			} else {
				final ConfigurationDAO	configuration	= (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);

				final UserShift			shift			= configuration.loadShiftById(detailFinalSchedule.getShift_type());

				// Absence Shift
				if (!shift.getPresence()) {
					return "color:" + TaskColor.ANBSENCE_COLOR;
				}

			}
		} else if (arg0 instanceof DetailInitialSchedule) {
			final TasksDAO				task_dao				= (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

			final DetailInitialSchedule	detailInitialSchedule	= (DetailInitialSchedule) arg0;

			final UserTask				task					= task_dao.loadTask(detailInitialSchedule.getTask());

			if (task != null) {
				if (task.getIsabsence()) {
					return "color:" + TaskColor.ANBSENCE_COLOR;
				} else if (task.getJustificatory()) {
					return "color:" + TaskColor.JUSTIFICATORY_COLOR;
				}
			} else {
				final ConfigurationDAO	configuration	= (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);

				final UserShift			shift			= configuration.loadShiftById(detailInitialSchedule.getShift_type());

				// Absence Shift
				if (!shift.getPresence()) {
					return "color:" + TaskColor.ANBSENCE_COLOR;
				}

			}
		}

		return " ";

	}
}
