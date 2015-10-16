package org.uario.seaworkengine.zkevent.converter;

import java.util.Map;

import org.uario.seaworkengine.model.ReportItem;
import org.uario.seaworkengine.utility.ReportItemTag;
import org.uario.seaworkengine.utility.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Listcell;

public class RoundNumberReportItemConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof ReportItem) || (arg0 == null) || !(arg1 instanceof Listcell) || (arg1 == null)) {
			return "0";
		}

		final ReportItem source = (ReportItem) arg0;

		if (source.getArgument() == null) {
			return "";
		}

		final Listcell cell = (Listcell) arg1;

		final Map<String, Object> list = cell.getAttributes();

		final String month = (String) list.get("month");

		if (source.getArgument().equals(ReportItemTag.MonthAvg)) {
			if (month.equals(ReportItemTag.MonthAvg)) {
				return "";
			} else if (month.equals("dec")) {
				return ReportItemTag.MonthAvg;
			} else if (month.equals("tot")) {
				if (source.getTot() != null) {
					final Double value = Utility.roundTwo(source.getTot());
					return value.toString();
				}
			}
		}

		if (source.getArgument().equals(ReportItemTag.Containers) || source.getArgument().equals(ReportItemTag.Hands)) {
			Integer value = 0;
			if (month.equals("argument")) {
				return source.getArgument();
			}
			if (month.equals("gen")) {
				if (source.getGen() != null) {
					value = source.getGen().intValue();
				} else {
					return "0";
				}

			}
			if (month.equals("feb")) {
				if (source.getFeb() != null) {
					value = source.getFeb().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("mar")) {
				if (source.getMar() != null) {
					value = source.getMar().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("apr")) {
				if (source.getApr() != null) {
					value = source.getApr().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("may")) {
				if (source.getMay() != null) {
					value = source.getMay().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("jun")) {
				if (source.getJun() != null) {
					value = source.getJun().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("jul")) {
				if (source.getJul() != null) {
					value = source.getJul().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("aug")) {
				if (source.getAug() != null) {
					value = source.getAug().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("sep")) {
				if (source.getSep() != null) {
					value = source.getSep().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("oct")) {
				if (source.getOct() != null) {
					value = source.getOct().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("nov")) {
				if (source.getNov() != null) {
					value = source.getNov().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("dec")) {
				if (source.getDec() != null) {
					value = source.getDec().intValue();
				} else {
					return "0";
				}
			}
			if (month.equals("tot")) {
				if (source.getTot() != null) {
					value = source.getTot().intValue();
				} else {
					return "0";
				}
			}
			return value.toString();
		}

		if (source.getArgument().equals(ReportItemTag.HandsOnDays) || source.getArgument().equals(ReportItemTag.MenOnHands)
				|| source.getArgument().equals(ReportItemTag.ContainersOnMen)) {
			Double value = 0.0;

			if (month.equals("argument")) {
				return source.getArgument();
			}

			if (month.equals("gen")) {
				if (source.getGen() != null) {
					value = Utility.roundTwo(source.getGen());
				} else {
					return "0";
				}

			}
			if (month.equals("feb")) {
				if (source.getFeb() != null) {
					value = Utility.roundTwo(source.getFeb());
				} else {
					return "0";
				}
			}
			if (month.equals("mar")) {
				if (source.getMar() != null) {
					value = Utility.roundTwo(source.getMar());
				} else {
					return "0";
				}
			}
			if (month.equals("apr")) {
				if (source.getApr() != null) {
					value = Utility.roundTwo(source.getApr());
				} else {
					return "0";
				}
			}
			if (month.equals("may")) {
				if (source.getMay() != null) {
					value = Utility.roundTwo(source.getMay());
				} else {
					return "0";
				}
			}
			if (month.equals("jun")) {
				if (source.getJun() != null) {
					value = Utility.roundTwo(source.getJun());
				} else {
					return "0";
				}
			}
			if (month.equals("jul")) {
				if (source.getJul() != null) {
					value = Utility.roundTwo(source.getJul());
				} else {
					return "0";
				}
			}
			if (month.equals("aug")) {
				if (source.getAug() != null) {
					value = Utility.roundTwo(source.getAug());
				} else {
					return "0";
				}
			}
			if (month.equals("sep")) {
				if (source.getSep() != null) {
					value = Utility.roundTwo(source.getSep());
				} else {
					return "0";
				}
			}
			if (month.equals("oct")) {
				if (source.getOct() != null) {
					value = Utility.roundTwo(source.getOct());
				} else {
					return "0";
				}
			}
			if (month.equals("nov")) {
				if (source.getNov() != null) {
					value = Utility.roundTwo(source.getNov());
				} else {
					return "0";
				}
			}
			if (month.equals("dec")) {
				if (source.getDec() != null) {
					value = Utility.roundTwo(source.getDec());
				} else {
					return "0";
				}
			}
			if (month.equals("tot")) {
				return "";
			}
			return value.toString();
		}

		return "";
	}
}
