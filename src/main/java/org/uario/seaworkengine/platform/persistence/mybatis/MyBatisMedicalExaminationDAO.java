/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.MedicalExamination;
import org.uario.seaworkengine.platform.persistence.dao.MedicalExaminationDAO;

public class MyBatisMedicalExaminationDAO extends SqlSessionDaoSupport implements MedicalExaminationDAO {
	private static Logger logger = Logger.getLogger(MyBatisMedicalExaminationDAO.class);

	@Override
	public void createMedicalExamination(final MedicalExamination medicalExamination) {
		MyBatisMedicalExaminationDAO.logger.info("createMedicalExamination " + medicalExamination.getId());

		this.getSqlSession().insert("medicalexamination.createMedicalExamination", medicalExamination);

	}

	@Override
	public MedicalExamination loadMedicalExamination(final Integer idMedicalExamination) {
		MyBatisMedicalExaminationDAO.logger.info("loadMedicalExamination " + idMedicalExamination);

		return this.getSqlSession().selectOne("medicalexamination.loadMedicalExamination", idMedicalExamination);

	}

	@Override
	public List<MedicalExamination> loadMedicalExaminationByUserId(final Integer id_user) {
		MyBatisMedicalExaminationDAO.logger.info("loadMedicalExaminationByUserId " + id_user);

		return this.getSqlSession().selectList("medicalexamination.loadMedicalExaminationByUserId", id_user);
	}

	@Override
	public void removeMedicalExamination(final Integer idMedicalExamination) {
		MyBatisMedicalExaminationDAO.logger.info("removeMedicalExamination " + idMedicalExamination);

		this.getSqlSession().delete("medicalexamination.removeMedicalExamination", idMedicalExamination);

	}

	@Override
	public void updateMedicalExamination(final MedicalExamination medicalExamination) {
		MyBatisMedicalExaminationDAO.logger.info("updateMedicalExamination " + medicalExamination.getId());

		this.getSqlSession().update("medicalexamination.updateMedicalExamination", medicalExamination);

	}

}
