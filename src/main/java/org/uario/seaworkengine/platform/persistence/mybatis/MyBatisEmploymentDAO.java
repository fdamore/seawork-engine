/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Employment;
import org.uario.seaworkengine.platform.persistence.dao.EmploymentDAO;

public class MyBatisEmploymentDAO extends SqlSessionDaoSupport implements EmploymentDAO {
	private static Logger	logger	= Logger.getLogger(MyBatisEmploymentDAO.class);

	@Override
	public void createEmploymentForUser(final Integer id_user, final Employment employment) {
		MyBatisEmploymentDAO.logger.info("createEmploymentForUser");

		this.getSqlSession().insert("employment.createItemForUser", employment);
	}

	@Override
	public List<Employment> loadEmploymentByUser(final Integer id_user) {
		MyBatisEmploymentDAO.logger.info("loadEmploymentByUser..");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_user", id_user.toString());

		final List<Employment> list_items = this.getSqlSession().selectList("employment.loadItemsByUser", map);
		return list_items;
	}

	@Override
	public void removeEmployment(final Integer id) {
		MyBatisEmploymentDAO.logger.info("removeEmployment");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_item", id.toString());

		this.getSqlSession().delete("employment.removeItem", map);

	}

	@Override
	public void updateEmployment(final Employment employment) {
		MyBatisEmploymentDAO.logger.info("updateEmployment");
		this.getSqlSession().update("employment.udapteItem", employment);

	}

}
