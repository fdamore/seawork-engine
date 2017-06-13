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
			return "containerStyleReport";
		} else if (source.getArgument().equals(ReportItemTag.TaskTotalHours) || source.getArgument().equals(ReportItemTag.TaskTotalHoursRZ_PP)) {
			return "taskTotalHoursStyleReport";
		} else if (source.getArgument().equals(ReportItemTag.Hands)) {
			return "handsStyleReport";
		} else if (source.getArgument().equals(ReportItemTag.HandsC_P)) {
			return "handsC_PStyleReport";
		} else if (source.getArgument().equals(ReportItemTag.TaskHours) || source.getIsTaskROW()) {
			return "taskHoursStyleReport";
		} else if (source.getArgument().equals(ReportItemTag.HandsOnDays) || source.getArgument().equals(ReportItemTag.MenOnHands)
				|| source.getArgument().equals(ReportItemTag.Productivity) || source.getArgument().equals(ReportItemTag.ContainersOnHours)) {
			return "handsOnDaysStyleReport";
		} else if (source.getArgument().contains(ReportItemTag.HandsC_shift)) {
			return "handsC_shiftStyleReport";
		} else if (source.getArgument().contains(ReportItemTag.HandsP_shift)) {
			return "handsP_shiftStyleReport";
		} else if (source.getArgument().contains(ReportItemTag.CustomerComplaint)) {
			return "customerComplaintStyleReport";
		} else if (source.getArgument().equals(ReportItemTag.WindyDay)) {
			return "windyDayStyleReport";
		} else if (source.getArgument().equals(ReportItemTag.ShipNumberComplete) || source.getArgument().equals(ReportItemTag.ShipNumberTwist)) {
			return "shipNumberStyleReport";
		} else if (source.getIsService() != null) {
			final int i = source.getIsService() % 2;
			if (i == 0) {
				return "isServiceStyleReport";
			}
			return "isServiceStyleReportd";
		} else if (source.getArgument().equals(ReportItemTag.ContainersOnMen) || source.getArgument().equals(ReportItemTag.ContainerOnHours)) {
			return "containersOnMenStyleReport";
		} else if (source.getArgument().equals(ReportItemTag.Service_TimeWorkTotal)) {
			return "service_TimeWorkTotalStyleReport";
		}
		return "";

	}

}
