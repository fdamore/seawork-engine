package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.cache.IShipCache;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ShipNameConverter implements TypeConverter {

	private static String	img	= "/img/star-full-icon.png";

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

		final IShipCache ship_cache = (IShipCache) SpringUtil.getBean(BeansTag.SHIP_CACHE);
		final Ship ship = ship_cache.getShip(id);
		if (ship == null) {
			return "" + arg0;
		} else {
			return ship.getName();
		}

	}

}
