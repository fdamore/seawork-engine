package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.Customer;
import org.uario.seaworkengine.platform.persistence.dao.ICustomerDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ConverterCustomerName implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Integer)) {
			return "";
		}

		final Integer		id			= (Integer) arg0;

		final ICustomerDAO	customers	= (ICustomerDAO) SpringUtil.getBean(BeansTag.CUSTOMER_DAO);

		final Customer		customer	= customers.loadCustomer(id);
		if (customer == null) {
			return "" + arg0;
		} else {
			return customer.getName();
		}

	}

}
