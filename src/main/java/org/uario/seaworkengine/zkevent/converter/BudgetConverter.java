package org.uario.seaworkengine.zkevent.converter;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.uario.seaworkengine.mobile.model.Badge;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.Utility;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class BudgetConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if ((arg0 == null) || !(arg0 instanceof Integer)) {
			return "";
		}

		final Integer id_schedule = (Integer) arg0;

		final ISchedule schedule_dao = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);

		// set badge info
		final List<Badge> badgeList = schedule_dao.loadBadgeByScheduleId(id_schedule);
		if (CollectionUtils.isEmpty(badgeList)) {
			return "";
		}
		final String infob = Utility.getLabelListBadge(badgeList);
		return infob;

	}

}
