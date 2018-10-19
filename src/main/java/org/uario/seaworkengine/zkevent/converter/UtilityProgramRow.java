package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ProgramColorTag;
import org.uario.seaworkengine.utility.ShiftTag;
import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Toolbarbutton;

public class UtilityProgramRow {

	public static final String NO_DATA = "_";

	/**
	 * color scheduler shift
	 *
	 * @param item_schedule
	 * @param status        TODO
	 * @param arg1
	 */
	public static void defineRowBeahvior(final Component comp, final ItemRowSchedule item_schedule, final String status) {

		if (status == null) {
			return;
		}

		String color_stye = "background-color:" + ProgramColorTag.COLOR_WORKER;

		// define worker not available - disable
		if (status != null) {
			final Toolbarbutton button = (Toolbarbutton) comp;
			if (status.equals(ShiftTag.USER_WORKER_NOT_AVAILABLE)) {
				button.setDisabled(true);
			}
		}

		// define color
		final Listcell listcell = (Listcell) comp.getParent();

		if (status.equals(ShiftTag.USER_WORKER_AVAILABLE)) {
			color_stye = "background-color:" + ProgramColorTag.COLOR_WORKER;
		} else if (status.equals(ShiftTag.USER_WORKER_FORZABLE)) {
			color_stye = "background-color:" + ProgramColorTag.COLOR_FORZABLE;
		} else if (status.equals(ShiftTag.USER_WORKER_NOT_AVAILABLE)) {
			color_stye = "background-color:" + ProgramColorTag.COLOR_NOT_AVAILABLE;
		}

		// color DAILY
		if ((item_schedule != null) && (item_schedule.getSchedule() != null) && (item_schedule.getSchedule().getShift() != null)) {

			final ConfigurationDAO	configuration	= (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);

			final UserShift			dailyShift		= configuration.getDailyShift();
			if (dailyShift != null) {
				if (dailyShift.getId().equals(item_schedule.getSchedule().getShift())) {
					color_stye = "background-color:" + ProgramColorTag.DAILY_COLOR;
				}
			}

		}

		listcell.setStyle(color_stye);

	}

	/**
	 * Get current status
	 *
	 * @param item_schedule
	 * @return
	 */
	public static String getCurrentStatus(final ItemRowSchedule item_schedule) {
		// define status
		String		status	= ShiftTag.USER_WORKER_AVAILABLE;

		UserShift	myShift	= null;

		// define shift
		if (item_schedule.getSchedule() != null) {
			final Integer			shift			= item_schedule.getSchedule().getShift();
			final ConfigurationDAO	configuration	= (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
			myShift = configuration.loadShiftById(shift);

			if (!myShift.getPresence().booleanValue()) {
				status = ShiftTag.USER_WORKER_NOT_AVAILABLE;
			}
			if (myShift.getForceable().booleanValue()) {
				status = ShiftTag.USER_WORKER_FORZABLE;
			}
		}

		return status;

	}

	/**
	 * Get current user shift
	 *
	 * @param item_schedule
	 * @return
	 */
	public static UserShift getCurrentUserShift(final ItemRowSchedule item_schedule) {
		UserShift myShift = null;

		// define shift
		if (item_schedule.getSchedule() != null) {
			final Integer			shift			= item_schedule.getSchedule().getShift();
			final ConfigurationDAO	configuration	= (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
			myShift = configuration.loadShiftById(shift);

		}

		return myShift;
	}

}
