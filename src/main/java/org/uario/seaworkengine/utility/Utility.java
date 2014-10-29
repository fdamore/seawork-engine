package org.uario.seaworkengine.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.uario.seaworkengine.zkevent.bean.RowSchedule;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Toolbarbutton;

/**
 * Utility methods for GeoInt....
 *
 * @author francesco
 *
 */
public class Utility {

	private static String			COLOR_FORZABLE		= "orange";

	private static String			COLOR_NOT_AVAILABLE	= "red";

	private static String			COLOR_WORKER		= "blue";

	private static SimpleDateFormat	dateFormat			= new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat	dateFormat_it		= new SimpleDateFormat("dd-MM-yyyy");
	private static SimpleDateFormat	timeFormat			= new SimpleDateFormat("HH:mm");

	/**
	 * color scheduler shift
	 *
	 * @param arg1
	 * @param item_schedule
	 */
	public static void defineColorShiftConverter(final Component arg1, final ItemRowSchedule item_schedule) {
		final RowSchedule row = item_schedule.getRowSchedule();
		final String status = row.getUser_status();
		String color_stye = "background-color:" + Utility.COLOR_WORKER;

		if (status.equals(UserTag.USER_WORKER_AVAILABLE)) {
			color_stye = "background-color:" + Utility.COLOR_WORKER;
		}
		if (status.equals(UserTag.USER_WORKER_FORZABLE)) {
			color_stye = "background-color:" + Utility.COLOR_FORZABLE;
		}
		if (status.equals(UserTag.USER_WORKER_NOT_AVAILABLE)) {
			color_stye = "background-color:" + Utility.COLOR_NOT_AVAILABLE;
		}

		final Toolbarbutton toolbar = (Toolbarbutton) arg1;
		toolbar.setStyle("font-weight: bold;");

		final Listitem cell = (Listitem) arg1.getParent().getParent();
		cell.setStyle(color_stye);
	}

	/**
	 * Encode string on sha
	 *
	 * @param str
	 * @param salt
	 *            salting parameter
	 * @return
	 */
	public static String encodeSHA256(final String str, final String salt) {
		final String saltedPassword = str + "{" + salt + "}";

		final String digest = DigestUtils.sha256Hex(saltedPassword);
		return digest;
	}

	public static String getDataAsString(final Date date) {
		if (date == null) {
			return "";
		}
		return Utility.getDateFormat().format(date);
	}

	/**
	 * Return the default date format
	 *
	 * @return
	 */
	public static SimpleDateFormat getDateFormat() {
		return Utility.dateFormat;
	}

	/**
	 * Return the default date format
	 *
	 * @return
	 */
	public static SimpleDateFormat getDateFormatIT() {
		return Utility.dateFormat_it;
	}

	/**
	 * Return the default time format
	 *
	 * @return
	 */
	public static SimpleDateFormat getTimeFormat() {
		return Utility.timeFormat;
	}

	/**
	 * Round to second decimal digit
	 *
	 * @param x
	 * @return
	 */
	public static double roundOne(final double x) {
		final double temp = Math.pow(10, 1);
		return Math.round(x * temp) / temp;
	}

	/**
	 * Round to second decimal digit
	 *
	 * @param x
	 * @return
	 */
	public static double roundTwo(final Double x) {
		if (x == null) {
			return 0.0;
		}
		final double temp = Math.pow(10, 2);
		return Math.round(x.doubleValue() * temp) / temp;
	}
}
