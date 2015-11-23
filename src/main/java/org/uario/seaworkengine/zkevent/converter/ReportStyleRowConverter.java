package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.ReportItem;
import org.uario.seaworkengine.utility.ReportItemTag;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ReportStyleRowConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object val, final Component comp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {
		if (!(arg0 instanceof ReportItem) || (arg0 == null)) {
			return "0";
		}

		final ReportItem source = (ReportItem) arg0;

		if (source.getArgument() == null) {
			return "";
		}

		if (source.getArgument().equals(ReportItemTag.Containers) || source.getArgument().equals(ReportItemTag.ContainerRZ_TW_SWS)
				|| source.getArgument().equals(ReportItemTag.ContainerRZ_TW_MCT)) {
			return "background-color:#FFFDCF";
		} else if (source.getArgument().equals(ReportItemTag.Hands)) {
			return "background-color:#F0CE8F";
		} else if (source.getArgument().equals(ReportItemTag.HandsC_P)) {
			return "background-color:#F1886B";
		} else if (source.getArgument().equals(ReportItemTag.TaskHours) || source.getIsTaskROW()) {
			return "background-color:#F4E4B3";
		} else if (source.getArgument().equals(ReportItemTag.HandsOnDays) || source.getArgument().equals(ReportItemTag.MenOnHands)
				|| source.getArgument().equals(ReportItemTag.Productivity) || source.getArgument().equals(ReportItemTag.ContainersOnHours)) {
			return "background-color:#F4E4B3";
		} else if (source.getArgument().contains(ReportItemTag.HandsC_shift)) {
			return "background-color:#E9C6B2";
		} else if (source.getArgument().contains(ReportItemTag.HandsP_shift)) {
			return "background-color:#FFFDCF";
		} else if (source.getArgument().contains(ReportItemTag.CustomerComplaint)) {
			return "background-color:#F74537";
		} else if (source.getArgument().equals(ReportItemTag.WindyDay)) {
			return "background-color:#DEDEDE";
		} else if (source.getArgument().equals(ReportItemTag.ShipNumberComplete) || source.getArgument().equals(ReportItemTag.ShipNumberTwist)) {
			return "background-color:##FFFFFF";
		} else if (source.getIsService() != null) {
			final int i = source.getIsService() % 2;
			if (i == 0) {
				return "background-color:#D4A190";
			}
			return "background-color:#EDD6CE";
		} else if (source.getArgument().equals(ReportItemTag.ContainersOnMen) || source.getArgument().equals(ReportItemTag.ContainerOnHours)) {
			return "background-color:#FFD700";
		} else if (source.getArgument().equals(ReportItemTag.Service_TimeWorkTotal)) {
			return "background-color:#FFD700";
		}
		return "";

	}

}
