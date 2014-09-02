package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.utility.UserStatusTag;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class UserStatusConverter implements TypeConverter {

	private static final String	APERTO			= "APERTO";
	private static final String	LICENZIATO		= "LICENZIATO";
	private static final String	NON_CONFERMATO	= "NON CONFERMATO";
	private static final String	RIAPERTO		= "RIAPERTO";
	private static final String	RIASSEGNATO		= "RIASSEGNATO";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {
		final String status = (String) arg0;
		if (status.equals(UserStatusTag.FIRED)) {
			return UserStatusConverter.LICENZIATO;
		}

		if (status.equals(UserStatusTag.NOT_CONFIRMED)) {
			return UserStatusConverter.NON_CONFERMATO;
		}

		if (status.equals(UserStatusTag.OPEN)) {
			return UserStatusConverter.APERTO;
		}

		if (status.equals(UserStatusTag.REOPEN)) {
			return UserStatusConverter.RIAPERTO;
		}

		if (status.equals(UserStatusTag.RESIGNED)) {
			return UserStatusConverter.RIASSEGNATO;
		}

		return "";

	}

}
