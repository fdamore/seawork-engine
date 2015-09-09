package org.uario.seaworkengine.zkevent.converter;

import java.util.List;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.TaskColor;
import org.uario.seaworkengine.utility.Utility;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

public class DecimalToTimeVacationConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if ((!(arg0 instanceof DetailFinalSchedule) && !(arg0 instanceof DetailInitialSchedule)) || (arg0 == null)) {
			return arg0;
		}

		final Label lab = (Label) arg1;
		final Listcell lcell = (Listcell) lab.getParent();
		final Listitem litem = (Listitem) lcell.getParent();
		final List<Listcell> listCell = litem.getChildren();

		if (arg0 instanceof DetailFinalSchedule) {
			final DetailFinalSchedule ds = (DetailFinalSchedule) arg0;

			final Integer idTask = ds.getTask();
			final TasksDAO taskDAO = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

			final UserTask task = taskDAO.loadTask(idTask);

			if (task != null) {
				if (task.getIsabsence()) {
					for (final Listcell cell : listCell) {
						cell.setStyle("color:" + TaskColor.ANBSENCE_COLOR);
					}
					return Utility.decimatToTime(ds.getTime_vacation());
				} else if (task.getJustificatory()) {
					for (final Listcell cell : listCell) {
						cell.setStyle("color:" + TaskColor.JUSTIFICATORY_COLOR);
					}
					return Utility.decimatToTime(ds.getTime_vacation());
				}
				return Utility.decimatToTime(ds.getTime());
			}

			return arg0;

		} else {
			final DetailInitialSchedule ds = (DetailInitialSchedule) arg0;

			final Integer idTask = ds.getTask();
			final TasksDAO taskDAO = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

			final UserTask task = taskDAO.loadTask(idTask);

			if (task != null) {
				if (task.getIsabsence()) {
					for (final Listcell cell : listCell) {
						cell.setStyle("color:" + TaskColor.ANBSENCE_COLOR);
					}
					return Utility.decimatToTime(ds.getTime_vacation());
				} else if (task.getJustificatory()) {
					for (final Listcell cell : listCell) {
						cell.setStyle("color:" + TaskColor.JUSTIFICATORY_COLOR);
					}
					return Utility.decimatToTime(ds.getTime_vacation());
				}
				return Utility.decimatToTime(ds.getTime());
			}

			return arg0;

		}
	}
}
