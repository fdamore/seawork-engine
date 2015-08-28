package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.utility.BeansTag;
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

		if (!(arg0 instanceof Integer) || (arg0 == null)) {
			return arg0;
		}

		final ITaskCache taskCache = (ITaskCache) SpringUtil.getBean(BeansTag.TASK_CACHE);

		final Integer id_task = (Integer) arg0;

		final UserTask task = taskCache.getUserTask(id_task);

		if (task.getIsabsence()) {
			return "color:red";
		}
		return " ";

	}
}
