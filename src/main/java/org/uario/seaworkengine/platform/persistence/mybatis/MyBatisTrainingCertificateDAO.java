/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.TrainingCertificate;
import org.uario.seaworkengine.platform.persistence.dao.TrainingCertificateDAO;

public class MyBatisTrainingCertificateDAO extends SqlSessionDaoSupport implements TrainingCertificateDAO {
	private static Logger logger = Logger.getLogger(MyBatisTrainingCertificateDAO.class);

	@Override
	public void createTrainingCertificate(final TrainingCertificate trainingCertificate) {
		MyBatisTrainingCertificateDAO.logger.info("createTrainingCertificate");

		this.getSqlSession().insert("trainingcertificate.createTrainingCertificate", trainingCertificate);

	}

	@Override
	public List<TrainingCertificate> loadTrainingCertificate(final Integer id, final String full_text_search,
			final Boolean expired, final Integer user_id) {
		MyBatisTrainingCertificateDAO.logger.info("loadTrainingCertificate");

		// set full text search
		String value_txt = null;
		if (full_text_search != null && !full_text_search.isEmpty()) {
			value_txt = full_text_search;
		}

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("full_text_search", value_txt);
		map.put("expired", expired);
		map.put("user_id", user_id);

		return this.getSqlSession().selectList("trainingcertificate.loadTrainingCertificate", map);

	}

	@Override
	public void removeTrainingCertificate(final Integer id) {
		MyBatisTrainingCertificateDAO.logger.info("removeTrainingCertificate " + id);

		this.getSqlSession().delete("trainingcertificate.removeTrainingCertificate", id);

	}

	@Override
	public void updateTrainingCertificate(final TrainingCertificate trainingCertificate) {
		MyBatisTrainingCertificateDAO.logger.info("updateTrainingCertificate " + trainingCertificate.getId());

		this.getSqlSession().update("trainingcertificate.updateTrainingCertificate", trainingCertificate);

	}

}
