package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.JobCost;
import org.uario.seaworkengine.platform.persistence.dao.IJobCost;

public class MyBatisJobCostDAO extends SqlSessionDaoSupport implements IJobCost {

	private static Logger	logger	= Logger.getLogger(MyBatisJobCostDAO.class);

	@Override
	public void createJobCost(final JobCost item) {
		MyBatisJobCostDAO.logger.info("createJob_Cost");

		this.getSqlSession().insert("jobcost.createJobCost", item);

	}

	@Override
	public List<JobCost> loadJobCostByBillCenter(final String bill_center) {
		MyBatisJobCostDAO.logger.info("select job cost by bill center");

		final List<JobCost> jobCostList = this.getSqlSession().selectList("jobcost.selectJobCostByBillCenter", bill_center);
		return jobCostList;
	}

	@Override
	public List<JobCost> loadJobCostByUser(final Integer id_user) {
		MyBatisJobCostDAO.logger.info("select job cost by user");

		final List<JobCost> jobCostList = this.getSqlSession().selectList("jobcost.selectJobCostByUser", id_user);
		return jobCostList;
	}

	@Override
	public void removeJobCost(final Integer id) {
		MyBatisJobCostDAO.logger.info("remove job cost");

		this.getSqlSession().delete("jobcost.removeJobCost", id);
	}

	@Override
	public void updateJobCost(final JobCost jobCost) {
		MyBatisJobCostDAO.logger.info("update job cost");

		this.getSqlSession().update("jobcost.updateJobCost", jobCost);

	}

}
