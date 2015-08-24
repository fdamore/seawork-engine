package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.TrainingCertificate;

public interface TrainingCertificateDAO {

	public void createTrainingCertificate(TrainingCertificate trainingCertificate);

	public List<TrainingCertificate> loadTrainingCertificate(Integer id, String title, String description, Boolean expired, Integer user_id);

	public void removeTrainingCertificate(Integer id);

	public void updateTrainingCertificate(TrainingCertificate trainingCertificate);
}
