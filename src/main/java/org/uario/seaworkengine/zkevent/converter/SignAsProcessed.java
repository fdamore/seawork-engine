package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.zkevent.utility.ZkUtility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class SignAsProcessed implements TypeConverter {
	
	@Override
	public Object coerceToBean(final Object val, final Component comp) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object coerceToUi(final Object val, final Component comp) {
		
		if (val == null) {
			return "";
		}
		
		final Boolean res = ZkUtility.isUserProcessed(val);
		
		if (res.booleanValue()) {
			return "PROCESSATO";
		} else {
			return "";
		}
	}

}
