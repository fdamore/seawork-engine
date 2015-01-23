package org.uario.seaworkengine.zkevent.converter;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Toolbarbutton;

public class DayShiftConverter implements TypeConverter {

	private static final String NO_DATA = "_";

	private static final String STANDARD_WORK = "_";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Schedule) || (arg0 == null)) {
			return arg0;
		}

		final IShiftCache shiftCache = (IShiftCache) SpringUtil
				.getBean(BeansTag.SHIFT_CACHE);

		final Schedule day_schedule = (Schedule) arg0;

		// set info for sunday
		Listcell item_cell = (Listcell) arg1.getParent();
		item_cell.setStyle(null);
		Date date_schedule = day_schedule.getDate_schedule();
		if (date_schedule != null) {
			Calendar my_calednar = DateUtils.toCalendar(date_schedule);
			if (my_calednar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				item_cell.setStyle("border-right-color: black");
			}
		}

		final Integer id_shift = day_schedule.getShift();
		if (id_shift == null) {
			return DayShiftConverter.NO_DATA;
		}

		final UserShift shift = shiftCache.getUserShift(id_shift);

		final UserShift standardWork = shiftCache.getStandardWorkShift();

		if (shift.equals(standardWork)) {
			return DayShiftConverter.STANDARD_WORK;
		} else {
			return shift.getCode();
		}

	}
}
