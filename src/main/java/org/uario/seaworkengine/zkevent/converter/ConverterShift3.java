package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.Scheduler;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ConverterShift3 implements TypeConverter {

	private static final String	NO_DATA	= "X";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Scheduler) || (arg0 == null)) {
			return arg0;
		}

		final Scheduler shift = (Scheduler) arg0;

		if ((shift.getInitial_shift_3() == null) && (shift.getInitial_time_3() == null)) {
			return ConverterShift3.NO_DATA;
		}

		if (shift.getInitial_time_3() != null) {
			return "" + shift.getInitial_time_3();
		}

		if (shift.getInitial_shift_3() != null) {

			// get shift cache
			final IShiftCache shift_cache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);

			final Integer id = shift.getInitial_shift_3();
			final UserShift item = shift_cache.getUserShift(id);
			if (item != null) {
				return item.getCode();
			}
			else {
				return "" + shift.getInitial_shift_3();
			}

		}

		return ConverterShift3.NO_DATA;

	}
}
