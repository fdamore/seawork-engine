package org.uario.seaworkengine.platform.persistence.mybatis;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.LockTable;
import org.uario.seaworkengine.platform.persistence.dao.LockTableDAO;

public class MyBatisLockTableDAO extends SqlSessionDaoSupport implements LockTableDAO {

	private static Logger	logger	= Logger.getLogger(MyBatisLockTableDAO.class);

	@Override
	public void createLockTable(final LockTable lockTable) {
		MyBatisLockTableDAO.logger.info("Insert LockTable ");
		this.getSqlSession().insert("locktable.createLockTable", lockTable);

	}

	@Override
	public LockTable loadLockTableById(final Integer idLockTable) {
		MyBatisLockTableDAO.logger.info("Load Lock Table By Id " + idLockTable);

		return this.getSqlSession().selectOne("locktable.loadLockTableById", idLockTable);
	}

	@Override
	public LockTable loadLockTableByTableType(final String tableType) {
		MyBatisLockTableDAO.logger.info("Load Lock Table By Table Type  " + tableType);

		return this.getSqlSession().selectOne("locktable.loadLockTableByTableType", tableType);
	}

	@Override
	public void removeLockTable(final Integer idLockTable) {
		MyBatisLockTableDAO.logger.info("Remove LockTable " + idLockTable);

		this.getSqlSession().delete("locktable.removeLockTable", idLockTable);

	}

	@Override
	public void updateLockTable(final LockTable lockTable) {
		MyBatisLockTableDAO.logger.info("Update LockTable");

		this.getSqlSession().update("locktable.updateLockTable", lockTable);

	}

}
