/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.TfrUser;
import org.uario.seaworkengine.platform.persistence.dao.TfrDAO;

public class MyBatisTfrDAO extends SqlSessionDaoSupport implements TfrDAO {
	private static Logger logger = Logger.getLogger(MyBatisTfrDAO.class);

	@Override
	public void createTGRForUser(final Integer id_user, final TfrUser tfr) {
		MyBatisTfrDAO.logger.info("createTGRForUser");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", id_user.toString());
		map.put("tfr_destination", tfr.getTfr_destination());
		map.put("tfr_selection_date", tfr.getTfr_selection_date());
		map.put("note", tfr.getNote());

		this.getSqlSession().insert("tfr.createTGRForUser", map);

	}

	@Override
	public List<TfrUser> loadTFRByUser(final Integer id_user) {
		MyBatisTfrDAO.logger.info("loadTFRByUser..");

		final HashMap<String, String> map = new HashMap<>();
		map.put("id_user", id_user.toString());

		final List<TfrUser> list_tfrs = this.getSqlSession().selectList("tfr.loadTFRByUser", map);
		return list_tfrs;
	}

	@Override
	public void removeTFR(final Integer id_tfr) {
		MyBatisTfrDAO.logger.info("removeTFR");

		final HashMap<String, String> map = new HashMap<>();
		map.put("id_tfr", id_tfr.toString());

		this.getSqlSession().delete("tfr.removeTFR", map);

	}

	@Override
	public void updateTfr(final TfrUser tfr) {
		MyBatisTfrDAO.logger.info("removeTFR");

		this.getSqlSession().delete("tfr.udapteTfr", tfr);

	}

}
