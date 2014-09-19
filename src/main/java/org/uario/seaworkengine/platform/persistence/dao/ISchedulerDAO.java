package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.Scheduler;

public interface ISchedulerDAO {

	/**
	 * Save or update
	 *
	 * @param scheduler
	 */
	public void saveOrUpdate(Scheduler scheduler);

	/**
	 * Select all scheduler
	 *
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public List<Scheduler> selectSchedulers(Date date_from, Date date_to);

}
