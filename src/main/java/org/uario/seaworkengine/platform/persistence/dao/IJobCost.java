package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.BillCenter;
import org.uario.seaworkengine.model.JobCost;

public interface IJobCost {

	/**
	 * create billcenter
	 *
	 * @param billcenter
	 *
	 */
	public void createBillCenter(BillCenter billCenter);

	public void createJobCost(JobCost item);

	/**
	 * delete billcenter
	 *
	 * @param idBillCenter
	 *
	 */
	public void deleteBillCenter(Integer idBillCenter);

	/**
	 * list all billcenter
	 *
	 * @param idBillCenter
	 *
	 */
	public List<BillCenter> listAllBillCenter(String textsearch);

	/**
	 * load billcenter
	 *
	 * @param idBillCenter
	 *
	 */
	public void loadBillCenter(Integer idBillCenter);

	public List<JobCost> loadJobCostByBillCenter(String bill_center);

	public List<JobCost> loadJobCostByUser(Integer id_user);

	public void removeJobCost(Integer id);

	/**
	 * update billcenter
	 *
	 * @param billcenter
	 *
	 */
	public void updateBillCenter(BillCenter billCenter);

	public void updateJobCost(JobCost jobCost);

}
