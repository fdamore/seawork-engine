package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.utility.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class PersonToDottedNameConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Person)) {
			return arg0;
		}

		final Person person = (Person) arg0;

		if (arg0 == null) {
			return "";
		}

		final String name = person.getLastname() + " " + person.getFirstname();

		return Utility.dottedName(name);

	}

}
