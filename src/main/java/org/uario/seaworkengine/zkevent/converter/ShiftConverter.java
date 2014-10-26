package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.DaySchedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ShiftConverter implements TypeConverter {

	private static final String	NO_DATA	= "_";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof DaySchedule) || (arg0 == null)) {
			return arg0;
		}

		final IShiftCache shiftCache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);

		final DaySchedule day_schedule = (DaySchedule) arg0;

		final Integer id_shift = day_schedule.getShift();
		if (id_shift == null) {
			return ShiftConverter.NO_DATA;
		}

		final UserShift shift = shiftCache.getUserShift(id_shift);

		return shift.getCode();

	}
}
