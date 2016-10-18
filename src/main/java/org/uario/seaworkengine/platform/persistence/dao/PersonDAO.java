package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.excpetions.UserNameJustPresentExcpetion;

public interface PersonDAO extends UserDetailsService {

	/**
	 * change mail...
	 *
	 * @param person
	 * @param password
	 * @param new_mail
	 *
	 */
	void changeMail(Integer person_id, String password, String new_mail);

	/**
	 * Changen password
	 *
	 * @param username
	 * @param new_password
	 */

	void changePassword(Integer person_id, String username, String new_password);

	/**
	 * List all users suspended
	 *
	 * @return Return a list of User suspended
	 */
	public List<Person> getSuspendendUsers();

	public List<Person> listAllPartTime();

	public List<Person> listAllPersonByContractualLevel(Integer contractual_level);

	/**
	 * List all person by enabling
	 *
	 * @param enable
	 * @return
	 */
	public List<Person> listAllPersonByEnable(Boolean enable);

	public List<Person> listAllPersonByUserStatus(String userStatus);

	/**
	 * @param userStatus
	 * @param from
	 * @param to
	 * @return
	 */
	public List<Person> listAllPersonByUserStatus(String userStatus, Date from, Date to);

	public List<Person> listAllPersons();

	/**
	 * List all person in the persistence engine
	 *
	 * @return
	 * @throws GenericExceptionInDAO
	 */
	public List<Person> listAllPersons(String full_text_search);

	/**
	 * @param date
	 *            TODO
	 * @return
	 */
	public List<Person> listAllPersonsForMobile(Date date);

	public List<Person> listDailyEmployee();

	public List<Person> listOperativePerson();

	public List<Person> listOutScheduleEmployee();

	/**
	 * List all person in the persistence engine
	 *
	 * @return
	 * @throws GenericExceptionInDAO
	 */

	public List<Person> listPersonInSchedule();

	public List<Person> listProgrammerEmployee();

	public List<Person> listViewerEmployee();

	/**
	 * List all worker persons
	 *
	 * @param full_text_search
	 *            TODO
	 *
	 * @return Return a list of worker persons
	 */
	public List<Person> listWorkerPersons(String full_text_search, String department);

	public List<String> loadAllProvincia();

	public String loadCodComune(String provincia, String comune);

	public List<String> loadComuniByProvincia(String provincia);

	/**
	 * Load a person
	 *
	 * @param id
	 *            in person
	 * @return Return a person if exists. If not return null
	 * @throws GenericExceptionInDAO
	 */
	public Person loadPerson(Integer id);

	/**
	 * Load a person
	 *
	 * @param id
	 *            in person
	 * @return Return a person if exists. If not return null
	 * @throws GenericExceptionInDAO
	 */
	public Person loadPerson(String username, String password);

	/**
	 * Load By User name and password.
	 *
	 * @param username
	 * @return person if any, elsewhere null
	 * @throws UsernameNotFoundException
	 */
	public UserDetails loadUserByUsernameIfAny(String username);

	/**
	 * remove a person
	 *
	 * @param personId
	 * @throws GenericExceptionInDAO
	 */

	public void removePerson(Integer personId);

	/**
	 * Save person in the persistence engine
	 *
	 * @param person
	 * @throws GenericExceptionInDAO
	 * @throws UserNameJustPresentExcpetion
	 */
	public void savePerson(Person person) throws UserNameJustPresentExcpetion;

	/**
	 * update person
	 *
	 * @param person
	 *            to update
	 * @throws PersonNotFound
	 * @throws GenericExceptionInDAO
	 * @throws UserNameJustPresentExcpetion
	 */
	public void updatePerson(Person person);

	/**
	 * List all person usersAdmin
	 *
	 * @return
	 * @throws GenericExceptionInDAO
	 */

	public List<Person> usersAdmin();

}
