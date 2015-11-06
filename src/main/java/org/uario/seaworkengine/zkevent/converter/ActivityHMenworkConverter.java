package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.mybatis.MyBatisShipDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ActivityHMenworkConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object val, final Component comp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object coerceToUi(final Object val, final Component comp) {
		if ((val == null) || !(val instanceof DetailScheduleShip)) {
			return "";
		}

		final DetailScheduleShip detail = (DetailScheduleShip) val;

		if (detail.getMenwork() == null) {
			return "";
		}

		final MyBatisShipDAO shipDao = (MyBatisShipDAO) SpringUtil.getBean(BeansTag.SHIP_DAO);
		final Ship ship = shipDao.loadShip(detail.getId_ship());

		if (ship.getActivityh()) {
			return "";
		}

		return detail.getMenwork().toString();
	}

}
