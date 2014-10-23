package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.JobCost;

public interface IJobCost {

	public void createJobCost(JobCost item);

	public List<JobCost> loadJobCostByBillCenter(String bill_center);

	public List<JobCost> loadJobCostByUser(Integer id_user);

	public void removeJobCost(Integer id);

	public void updateJobCost(JobCost jobCost);

}
