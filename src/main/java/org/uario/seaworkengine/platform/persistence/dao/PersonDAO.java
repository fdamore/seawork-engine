package org.uario.seaworkengine.platform.persistence.dao;

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

	void changePassword(String username, String new_password);

	/**
	 * List all users suspended
	 *
	 * @return Return a list of User suspended
	 */
	public List<Person> getSuspendendUsers();

	/**
	 * List all person in the persistence engine
	 *
	 * @return
	 * @throws GenericExceptionInDAO
	 */

	public List<Person> listAllPersons();

	/**
	 * List all person in the persistence engine
	 *
	 * @return
	 * @throws GenericExceptionInDAO
	 */
	public List<Person> listAllPersons(String full_text_search);

	/**
	 * List all worker persons
	 * @param full_text_search TODO
	 *
	 * @return Return a list of worker persons
	 */
	public List<Person> listWorkerPersons(String full_text_search);

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
