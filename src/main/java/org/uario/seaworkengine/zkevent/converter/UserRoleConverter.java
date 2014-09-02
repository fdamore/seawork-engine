package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.utility.UserTag;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class UserRoleConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {
		String roles = (String) arg0;

		roles = roles.replace(UserTag.ROLE_BACKOFFICE, "UFFICIO");
		roles = roles.replace(UserTag.ROLE_OPERATIVE, "PREPOSTO");
		roles = roles.replace(UserTag.ROLE_USER, "UTENTE");
		roles = roles.replace(UserTag.ROLE_VIEWER, "VISUALIZZATORE");
		roles = roles.replace(UserTag.ROLE_SUPERVISOR, "AMMINISTRATORE");

		return roles;

	}

}
