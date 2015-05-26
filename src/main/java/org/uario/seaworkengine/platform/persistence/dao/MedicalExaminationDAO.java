package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.MedicalExamination;

public interface MedicalExaminationDAO {

	public void createMedicalExamination(MedicalExamination medicalExamination);

	public MedicalExamination loadMedicalExamination(Integer idMedicalExamination);

	public List<MedicalExamination> loadMedicalExaminationByUserId(Integer id_user);

	public void removeMedicalExamination(Integer idMedicalExamination);

	public void updateMedicalExamination(MedicalExamination medicalExamination);
}
