package org.uario.seaworkengine.zkevent.converter;

import java.util.HashMap;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.utility.ZkSessionTag;
import org.uario.seaworkengine.zkevent.bean.RowDaySchedule;
import org.uario.seaworkengine.zkevent.bean.RowSchedule;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zkplus.databind.TypeConverter;

public class DottedNameConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (arg0 == null) {
			return "";
		}

		// standard definition
		if (arg0 instanceof String) {

			final String info_name = Utility.dottedName(arg0.toString());

			return info_name;

		}

		// if arg is not string, define an additional report
		return this.defineAdditionalBhavior(arg0);

	}

	/**
	 * Defin an additional report
	 *
	 * @param arg0
	 * @return
	 */
	private Object defineAdditionalBhavior(final Object arg0) {
		// get info for additional definition (stat) when required
		String name_user = null;
		Integer id_user = null;

		if (arg0 instanceof RowDaySchedule) {

			final RowDaySchedule row = (RowDaySchedule) arg0;
			name_user = row.getName_user();
			id_user = row.getUser();

		} else if (arg0 instanceof RowSchedule) {

			final RowSchedule row = (RowSchedule) arg0;

			name_user = row.getName_user();
			id_user = row.getUser();

		} else if (arg0 instanceof DetailFinalSchedule) {

			final DetailFinalSchedule row = (DetailFinalSchedule) arg0;

			name_user = row.getUser();
			id_user = row.getId_user();

		}

		if ((name_user == null) || (id_user == null)) {
			return "";
		}

		final String info_name = Utility.dottedName(name_user);

		// define info about sat
		final HashMap<Integer, Double> cache_sat = (HashMap<Integer, Double>) Executions.getCurrent().getSession()
				.getAttribute(ZkSessionTag.PersonCache);

		if (cache_sat == null) {
			return info_name;
		}

		else {

			final Double sat = cache_sat.get(id_user);

			if (sat == null) {
				return info_name;
			}

			// defining info
			final String info = this.definingSatLevel(sat);

			return info_name + "(" + info + ")";

		}
	}

	private String definingSatLevel(final Double sat) {

		if (sat <= -50) {
			return "-7";
		}

		if ((sat > -50) && (sat <= -30)) {
			return "-6";
		}
		if ((sat > -30) && (sat <= -24)) {
			return "-5";
		}

		if ((sat > -24) && (sat <= -18)) {
			return "-4";
		}

		if ((sat > -18) && (sat <= -12)) {
			return "-3";
		}

		if ((sat > -12) && (sat <= -2)) {
			return "-2";
		}

		if ((sat > -12) && (sat <= -6)) {
			return "-2";
		}

		if ((sat > -6) && (sat <= -0.001)) {
			return "-1";
		}

		if ((sat > 0.000001) && (sat <= 6)) {
			return "+5";
		}

		if ((sat > 6) && (sat <= 12)) {
			return "+4";
		}

		if ((sat > 12) && (sat <= 18)) {
			return "+3";
		}

		if ((sat > 18) && (sat <= 24)) {
			return "+2";
		}

		if (sat > 24) {
			return "+1";
		}

		return "0+";

	}
}
