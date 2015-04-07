/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.excpetions.UserNameJustPresentExcpetion;
import org.uario.seaworkengine.utility.UserStatusTag;
import org.uario.seaworkengine.utility.Utility;

public class MyBatisPersonDAO extends SqlSessionDaoSupport implements PersonDAO {
	private static Logger	logger	= Logger.getLogger(MyBatisPersonDAO.class);

	@Override
	public void changeMail(final Integer person_id, final String password, final String new_mail) {
		MyBatisPersonDAO.logger.info("Update mail fro person with id " + person_id);

		// define salt...
		final String hashing_password = Utility.encodeSHA256(password, new_mail);

		final Person person_data_handle = new Person();
		person_data_handle.setId(person_id);
		person_data_handle.setPassword(hashing_password);
		person_data_handle.setEmail(new_mail);

		this.getSqlSession().update("person.changeMail", person_data_handle);

	}

	@Override
	public void changePassword(final Integer person_id, final String email, final String new_password) {
		MyBatisPersonDAO.logger.info("Change Password for person: " + person_id);

		// define salt...
		final String hashing_password = Utility.encodeSHA256(new_password, email);

		final Person person_data_handle = new Person();
		person_data_handle.setPassword(hashing_password);
		person_data_handle.setId(person_id);

		this.getSqlSession().update("person.changePassword", person_data_handle);

	}

	@Override
	public List<Person> getSuspendendUsers() {
		MyBatisPersonDAO.logger.info("Get suspended users ");

		final String idSuspended = UserStatusTag.SUSPENDED;

		final List<Person> suspendedUsers = this.getSqlSession().selectList("person.selectSuspendedUsers", idSuspended);

		return suspendedUsers;
	}

	@Override
	public List<Person> listAllPartTime() {
		return this.getSqlSession().selectList("person.listAllPartTime");
	}

	@Override
	public List<Person> listAllPersons() {
		MyBatisPersonDAO.logger.info("Get all person..");
		final List<Person> list_person = this.getSqlSession().selectList("person.selectAllPerson");
		return list_person;

	}

	@Override
	public List<Person> listAllPersons(final String full_text_search) {
		MyBatisPersonDAO.logger.info("listAllPersons with like..");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("my_full_text_search", full_text_search);

		final List<Person> list_person = this.getSqlSession().selectList("person.selectAllPersonFulltextSearchLike", map);
		return list_person;
	}

	@Override
	public List<Person> listAllPersonsForMobile(final Date date) {
		MyBatisPersonDAO.logger.info("Get all person for mobile..");

		final Date date_scheudle = DateUtils.truncate(date, Calendar.DATE);

		final HashMap<String, Date> map = new HashMap<String, Date>();
		map.put("date_scheudle", date_scheudle);

		final List<Person> list_person = this.getSqlSession().selectList("person.selectWorkerPersonForMobile", map);
		return list_person;

	}

	@Override
	public List<Person> listDailyEmployee() {
		MyBatisPersonDAO.logger.info("Get all daily employee");
		return this.getSqlSession().selectList("person.listDailyEmployee");
	}

	@Override
	public List<Person> listOperativePerson() {
		MyBatisPersonDAO.logger.info("Get operative person");
		return this.getSqlSession().selectList("person.usersOperative");
	}

	@Override
	public List<Person> listOutScheduleEmployee() {
		MyBatisPersonDAO.logger.info("list Out Schedule Employee");
		return this.getSqlSession().selectList("person.listOutScheduleEmployee");
	}

	@Override
	public List<Person> listPersonInSchedule() {
		MyBatisPersonDAO.logger.info("Get all person in schedule");
		return this.getSqlSession().selectList("person.listPersonInSchedule");
	}

	@Override
	public List<Person> listProgrammerEmployee() {
		MyBatisPersonDAO.logger.info("list Programmer Employee");
		return this.getSqlSession().selectList("person.listProgrammerEmployee");
	}

	@Override
	public List<Person> listViewerEmployee() {
		MyBatisPersonDAO.logger.info("list Viewer Employee");
		return this.getSqlSession().selectList("person.listViewerEmployee");
	}

	@Override
	public List<Person> listWorkerPersons(final String full_text_search) {
		MyBatisPersonDAO.logger.info("list all worker persons...");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("my_full_text_search", full_text_search);

		return this.getSqlSession().selectList("person.selectWorkerPerson", map);
	}

	@Override
	public List<String> loadAllProvincia() {

		return this.getSqlSession().selectList("person.loadAllProvincia");
	}

	@Override
	public String loadCodComune(final String provincia, final String comune) {
		MyBatisPersonDAO.logger.info("Get cod comune by provincia e nome comune");
		final HashMap<String, String> map = new HashMap<String, String>();

		map.put("comune", comune);
		map.put("provincia", provincia);
		return this.getSqlSession().selectOne("person.loadCodComune", map);
	}

	@Override
	public List<String> loadComuniByProvincia(final String provincia) {
		return this.getSqlSession().selectList("person.loadComuniByProvincia", provincia);
	}

	@Override
	public Person loadPerson(final Integer id) {

		MyBatisPersonDAO.logger.info("Get person by id: " + id);
		final Person person = this.getSqlSession().selectOne("person.selectUserById", id);
		return person;

	}

	@Override
	public Person loadPerson(final String username, final String password) {

		MyBatisPersonDAO.logger.info("Get person with username " + username);
		final Person person = new Person();
		person.setEmail(username);

		final ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);
		final String encoded_pass = encoder.encodePassword(password, username);
		person.setPassword(encoded_pass);

		final Person ret = this.getSqlSession().selectOne("person.selectUsersByUsernameAndPassword", person);
		return ret;

	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		MyBatisPersonDAO.logger.info("Get person with username " + username);
		final Person person = this.getSqlSession().selectOne("person.selectUsersByUsername", username);

		if (person == null) {
			throw new UsernameNotFoundException("Person not found");
		}

		return person;

	}

	@Override
	public UserDetails loadUserByUsernameIfAny(final String username) {

		MyBatisPersonDAO.logger.info("Get person with username " + username);

		final Person person = this.getSqlSession().selectOne("person.selectUsersByUsername", username);

		return person;

	}

	@Override
	public void removePerson(final Integer personId) {

		MyBatisPersonDAO.logger.info("Remove person with id " + personId);
		this.getSqlSession().delete("person.deletePerson", personId);

	}

	@Override
	public void savePerson(final Person person) throws UserNameJustPresentExcpetion {

		MyBatisPersonDAO.logger.info("Insert Person " + person);

		final Object ob_person = this.loadUserByUsernameIfAny(person.getUsername());
		if (ob_person != null) {
			throw new UserNameJustPresentExcpetion();
		}

		// Encript password
		final String pass = person.getPassword();

		// encode.....
		final String encodedPass = Utility.encodeSHA256(pass, person.getUsername());
		person.setPassword(encodedPass);

		this.getSqlSession().insert("person.insertPerson", person);

	}

	@Override
	public void updatePerson(final Person person) {

		MyBatisPersonDAO.logger.info("Update person with id " + person.getId());
		this.getSqlSession().update("person.updatePerson", person);

	}

	@Override
	public List<Person> usersAdmin() {
		return this.getSqlSession().selectList("person.usersAdmin");

	}

}
