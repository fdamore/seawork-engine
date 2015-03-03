package org.uario.seaworkengine.utility;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;

public class UtilityCSV {

	private static final SimpleDateFormat formatDateOverview = new SimpleDateFormat("dd/MM/yyyy");

	private static final SimpleDateFormat formatTimeOverview = new SimpleDateFormat("dd/MM/yyyy hh:mm");

	private static final NumberFormat number_format = NumberFormat.getInstance(Locale.ITALIAN);

	public static StringBuilder downloadCSVPreprocessing(final List<Schedule> listSchedule, final IShiftCache shift_cache) {
		final StringBuilder builder = new StringBuilder();
		final String header = "nome;data;turno;\n";
		builder.append(header);

		for (final Schedule item : listSchedule) {
			String date = "";
			if (item.getDate_schedule() != null) {
				date = UtilityCSV.formatDateOverview.format(item.getDate_schedule());
			}

			String code_shift = "";
			final UserShift task = shift_cache.getUserShift(item.getShift());
			if (task != null) {
				code_shift = task.getCode();
			}

			final String line = "" + item.getName_user() + ";" + date + ";" + code_shift + ";\n";
			builder.append(line);
		}
		return builder;
	}

	public static StringBuilder downloadCSVProgram(final List<DetailInitialSchedule> listDetailProgram,
			final ITaskCache task_cache, final IShiftCache shift_cache) {
		final StringBuilder builder = new StringBuilder();
		final String header = "nome;data;tipoturno;turno;mansione;ore;ore_chiusura;ingresso;uscita\n";
		builder.append(header);

		for (final DetailInitialSchedule item : listDetailProgram) {
			String date = "";
			if (item.getDate_schedule() != null) {
				date = UtilityCSV.formatDateOverview.format(item.getDate_schedule());
			}

			String time_from = "";
			if (item.getTime_from() != null) {
				time_from = UtilityCSV.formatTimeOverview.format(item.getTime_from());
			}

			String time_to = "";
			if (item.getTime_to() != null) {
				time_to = UtilityCSV.formatTimeOverview.format(item.getTime_to());
			}

			String code_task = "";
			final UserTask task = task_cache.getUserTask(item.getTask());
			if (task != null) {
				code_task = task.getCode();
			}

			String code_shift = "";
			final UserShift shift_type = shift_cache.getUserShift(item.getShift_type());
			if (shift_type != null) {
				code_shift = shift_type.getCode();
			}

			Double time = item.getTime();
			if (time == null) {
				time = new Double(0.0);
			}
			final String time_info = UtilityCSV.number_format.format(time);

			Double time_vacation = item.getTime_vacation();
			if (time_vacation == null) {
				time_vacation = new Double(0.0);
			}
			final String time_vacation_info = UtilityCSV.number_format.format(time_vacation);

			final Integer shift_no = item.getShift();
			String shift_no_info = "";
			if (shift_no != null) {
				shift_no_info = shift_no.toString();
			}

			final String line = "" + item.getUser() + ";" + date + ";" + code_shift + ";" + shift_no_info + ";" + code_task + ";"
					+ time_info + ";" + time_vacation_info + ";" + time_from + ";" + time_to + ";\n";
			builder.append(line);
		}
		return builder;
	}

	public static StringBuilder downloadCSVReview(final List<DetailFinalSchedule> listDetailRevision,
			final ITaskCache task_cache, final IShiftCache shift_cache) {
		final StringBuilder builder = new StringBuilder();
		final String header = "nome;data;tipoturno;turno;mansione;ore;ore_chiusura;ingresso;uscita\n";
		builder.append(header);

		for (final DetailFinalSchedule item : listDetailRevision) {
			String date = "";
			if (item.getDate_schedule() != null) {
				date = UtilityCSV.formatDateOverview.format(item.getDate_schedule());
			}

			String time_from = "";
			if (item.getTime_from() != null) {
				time_from = UtilityCSV.formatTimeOverview.format(item.getTime_from());
			}

			String time_to = "";
			if (item.getTime_to() != null) {
				time_to = UtilityCSV.formatTimeOverview.format(item.getTime_to());
			}

			String code_task = "";
			final UserTask task = task_cache.getUserTask(item.getTask());
			if (task != null) {
				code_task = task.getCode();
			}

			String code_shift = "";
			final UserShift shift_type = shift_cache.getUserShift(item.getShift_type());
			if (shift_type != null) {
				code_shift = shift_type.getCode();
			}

			Double time = item.getTime();
			if (time == null) {
				time = new Double(0.0);
			}
			final String time_info = UtilityCSV.number_format.format(time);

			Double time_vacation = item.getTime_vacation();
			if (time_vacation == null) {
				time_vacation = new Double(0.0);
			}
			final String time_vacation_info = UtilityCSV.number_format.format(time_vacation);

			final Integer shift_no = item.getShift();
			String shift_no_info = "";
			if (shift_no != null) {
				shift_no_info = shift_no.toString();
			}

			final String line = "" + item.getUser() + ";" + date + ";" + code_shift + ";" + shift_no_info + ";" + code_task + ";"
					+ time_info + ";" + time_vacation_info + ";" + time_from + ";" + time_to + ";\n";
			builder.append(line);
		}
		return builder;
	}

}
