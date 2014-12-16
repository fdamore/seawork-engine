package org.uario.seaworkengine.statistics;

import java.util.Date;

import org.uario.seaworkengine.model.UserShift;

public interface IStatProcedure {

	/**
	 * Assign shift... remove all details
	 * 
	 * @param shift
	 * @param current_date_scheduled
	 * @param user
	 * @param editor
	 */
	public void shiftAssign(UserShift shift, Date current_date_scheduled, Integer user, Integer editor);

	/**
	 * workAssign Procedure
	 *
	 * @param shift
	 * @param current_date_scheduled
	 * @param user
	 * @param editor
	 *            TODO
	 */
	public abstract void workAssignProcedure(UserShift shift, Date current_date_scheduled, Integer user, Integer editor);

	public abstract Integer getMinimumShift(final Date date_calendar_schedule);

}