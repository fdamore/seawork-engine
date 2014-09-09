package org.uario.seaworkengine.zkevent.converter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ResultFCTypeConverter implements TypeConverter {

	private static final String	FAX			= "FAX";
	private static final String	FAX_INFO	= "FAX";

	private static final String	MAIL		= "MAIL";
	private static final String	MAIL_INFO	= "POSTA";

	private static final String	ONLINE		= "ONLINE";
	private static final String	ONLINE_INFO	= "ONLINE";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (arg0 == null) {
			return "";
		}

		final String shift = arg0.toString();

		if (shift.equals(ResultFCTypeConverter.FAX)) {
			return ResultFCTypeConverter.FAX_INFO;
		}

		if (shift.equals(ResultFCTypeConverter.MAIL)) {
			return ResultFCTypeConverter.MAIL_INFO;
		}

		if (shift.equals(ResultFCTypeConverter.ONLINE)) {
			return ResultFCTypeConverter.ONLINE_INFO;
		}

		return "";

	}
}
