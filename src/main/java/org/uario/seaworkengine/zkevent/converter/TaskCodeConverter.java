package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class TaskCodeConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Integer) || (arg0 == null)) {
			return arg0;
		}

		final TasksDAO dao = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

		final Integer id_task = (Integer) arg0;

		final UserTask task = dao.loadTask(id_task);

		if (task == null) {
			return "";
		}

		return task.getCode();

	}
}
