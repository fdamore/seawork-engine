package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.UserTask;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Image;

public class TaskDefaultConverter implements TypeConverter {

	private static String	img	= "/img/star-full-icon.png";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg1 instanceof Image)) {
			return "";
		}

		if (!(arg0 instanceof UserTask)) {
			return "";
		}

		final UserTask task = (UserTask) arg0;
		if (task.getTask_default().booleanValue()) {
			return img;
		} else {

			return "";
		}

	}

}
