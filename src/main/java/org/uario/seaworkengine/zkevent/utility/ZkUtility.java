package org.uario.seaworkengine.zkevent.utility;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;

public class ZkUtility {

	/**
	 * Check Processed
	 *
	 * @param val
	 * @return
	 */
	public static Boolean isUserProcessed(final Object val) {
		Integer id_shift = null;

		if (val instanceof DetailFinalSchedule) {

			if (((DetailFinalSchedule) val).getId() != null) {
				return Boolean.TRUE;
			}

			id_shift = ((DetailFinalSchedule) val).getShift_type();
		}

		if (val instanceof DetailInitialSchedule) {

			if (((DetailInitialSchedule) val).getId() != null) {
				return Boolean.TRUE;
			}

			id_shift = ((DetailInitialSchedule) val).getShift_type();

		}

		final IShiftCache shiftCache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);

		final UserShift shift = shiftCache.getUserShift(id_shift);

		if (shift == null) {
			return Boolean.FALSE;
		}

		if (shift.getBreak_shift() || shift.getForceable() || shift.getRecorded()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

}