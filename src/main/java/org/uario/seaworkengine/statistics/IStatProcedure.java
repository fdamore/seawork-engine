package org.uario.seaworkengine.statistics;

import java.util.Date;

import org.uario.seaworkengine.model.UserShift;

public interface IStatProcedure {

	/**
	 * workAssign Procedure
	 *
	 * @param shift
	 * @param current_date_scheduled
	 * @param user
	 * @param editor TODO
	 */
	public abstract void workAssignProcedure(UserShift shift, Date current_date_scheduled, Integer user, Integer editor);

}