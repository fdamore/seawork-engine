package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.Employment;

public interface EmploymentDAO {

	public void createEmploymentForUser(Integer id_user, Employment employment);

	public List<Employment> loadEmploymentByUser(Integer id_user);

	public void removeEmployment(Integer id);

	public void updateEmployment(Employment employment);

}
