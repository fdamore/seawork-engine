package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Contestation;
import org.uario.seaworkengine.platform.persistence.dao.IContestation;

public class MyBatisContestationDAO extends SqlSessionDaoSupport implements IContestation {
	private static Logger	logger	= Logger.getLogger(MyBatisContestationDAO.class);

	@Override
	public void createContestation(final Contestation item) {
		MyBatisContestationDAO.logger.info("createContestation");

		this.getSqlSession().insert("contestation.createContestation", item);
	}

	@Override
	public Contestation loadContestation(final int id) {
		MyBatisContestationDAO.logger.info("loadContestationByContestationId = " + id);

		final Contestation contestation = this.getSqlSession().selectOne("contestation.loadContestation", id);
		return contestation;
	}

	@Override
	public List<Contestation> loadUserContestation(final int id_user) {
		MyBatisContestationDAO.logger.info("loadContestationByUserId =" + id_user);

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_user", id_user);

		final List<Contestation> list_items = this.getSqlSession().selectList("contestation.loadContestation", map);
		return list_items;
	}

	@Override
	public void removeContestation(final int id) {
		MyBatisContestationDAO.logger.info("removeContestation " + id);

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id", id);

		this.getSqlSession().delete("contestation.removeContestation", map);
	}

	@Override
	public void updateContestation(final Contestation contestation) {
		MyBatisContestationDAO.logger.info("updateContestation");
		this.getSqlSession().update("contestation.updateContestation", contestation);

	}

}
