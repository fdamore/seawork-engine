package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ShipNameConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Integer)) {
			return "";
		}

		final Integer id = (Integer) arg0;

		final IShip shipDao = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);
		final Ship ship = shipDao.loadShip(id);
		if (ship == null) {
			return "" + arg0;
		} else {
			return ship.getName();
		}

	}

}
