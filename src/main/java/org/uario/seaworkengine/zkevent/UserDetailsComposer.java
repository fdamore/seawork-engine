package org.uario.seaworkengine.zkevent;

import java.util.List;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.excpetions.UserNameJustPresentExcpetion;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.UserTag;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

public class UserDetailsComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Wire
	private Component add_users_command;

	@Wire
	private Textbox address_user;

	@Wire
	private Checkbox admin_user;

	@Wire
	private Checkbox backoffice_user;

	@Wire
	private Combobox billcenter_user;

	@Wire
	private Datebox birth_date_user;

	@Wire
	private Textbox birth_place_user;

	@Wire
	private Div box_deleteuser;

	@Wire
	private Textbox city_user;

	@Wire
	private Textbox country_user;

	@Wire
	private Textbox current_position_user;

	@Wire
	private Datebox date_fired_user;

	@Wire
	private Datebox date_modify_user;

	@Wire
	private Textbox department_user;

	@Wire
	private Datebox driving_license_emission_user;

	@Wire
	private Textbox driving_license_user;

	@Wire
	private Textbox education_user;

	@Wire
	private Textbox email_editor_user;

	@Wire
	private Textbox email_editor_user_retype;

	@Wire
	private Textbox email_user;

	@Wire
	private Textbox email_user_retype;

	@Wire
	private Textbox employee_identification_user;

	@Wire
	private Textbox employee_level_user;

	@Wire
	private Datebox employment_date_user;

	@Wire
	private Textbox family_charge_user;

	@Wire
	private Textbox firstname_user;

	@Wire
	private Textbox fiscalcode_user;

	@Wire
	private Component grid_user_details;

	@Wire
	private Textbox lastname_user;

	private final Logger logger = Logger.getLogger(UserDetailsComposer.class);

	@Wire
	private Textbox mailpassword_user;

	@Wire
	private Textbox marital_status_user;

	@Wire
	private Component modify_users_command;

	@Wire
	private Textbox nbudje_user;

	@Wire
	private Textbox ncfl_user;

	@Wire
	private Textbox npass_user;

	@Wire
	private Checkbox operative_user;

	@Wire
	private Div panel_modify;

	@Wire
	private Textbox password_editor_user;

	@Wire
	private Textbox password_editor_user_retype;

	@Wire
	private Textbox password_user;

	@Wire
	private Textbox password_user_retype;

	// the dao used for db interaction
	private PersonDAO personDao;

	@Wire
	private Textbox phone_user;

	@Wire
	private Textbox postalCode_user;

	@Wire
	private Textbox provincia_user;

	@Wire
	private Row row_email_user_retype;

	@Wire
	private Row row_password_user;

	@Wire
	private Row row_password_user_retype;

	@Wire
	private Listbox sw_list_user;

	@Wire
	private Checkbox user_enabled;

	@Wire
	private Combobox user_status;

	@Wire
	private Checkbox viewer_user;

	@Listen("onClick = #add_users_command")
	public void addUserCommand() throws UserNameJustPresentExcpetion {

		// check info mail
		final String mail = email_user.getValue();
		final String retype_mail = email_user_retype.getValue();
		if (!retype_mail.equals(mail)) {
			Messagebox.show("Ridigita la Mail!", "ERROR", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		// check info password
		final String retype_password = password_user_retype.getValue();
		final String password = password_user.getValue();
		if (retype_password.equals("") || !retype_password.equals(password)) {
			Messagebox.show("Ridigita la password!", "ERROR", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		// check mail single....
		final Object ob = personDao.loadUserByUsernameIfAny(mail);
		if ((ob != null) && (ob instanceof Person)) {
			Messagebox.show("Mail già presente", "ERROR", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		final Person person = new Person();
		person.setAddress(address_user.getValue());
		person.setCity(city_user.getValue());
		person.setEmail(email_user.getValue());
		person.setFirstname(firstname_user.getValue());
		person.setLastname(lastname_user.getValue());
		person.setPassword(password_user.getValue());
		person.setPhone(phone_user.getValue());
		person.setZip(postalCode_user.getValue());
		person.setCountry(country_user.getValue());
		person.setEnabled(user_enabled.isChecked());
		person.setEmployee_identification(employee_identification_user.getValue());
		person.setProvincia(provincia_user.getValue());
		person.setFiscal_code(fiscalcode_user.getValue());
		person.setBirth_date(birth_date_user.getValue());
		person.setBirth_place(birth_place_user.getValue());
		person.setEducation(education_user.getValue());
		person.setEmployment(employment_date_user.getValue());
		person.setDate_fired(date_fired_user.getValue());
		person.setDate_modify(date_modify_user.getValue());
		person.setNcfl(ncfl_user.getValue());
		person.setDepartment(department_user.getValue());
		person.setCurrent_position(current_position_user.getValue());
		person.setEmployee_level(employee_level_user.getValue());
		person.setBill_center(billcenter_user.getValue());

		person.setNbudge(nbudje_user.getValue());
		person.setNpass(npass_user.getValue());
		person.setMarital_status(marital_status_user.getValue());
		person.setFamily_charge(family_charge_user.getValue());
		person.setDriving_license(driving_license_user.getValue());
		person.setDriving_license_emission(driving_license_emission_user.getValue());

		// set authority
		person.setAuthority(UserTag.ROLE_USER);

		final boolean admin = admin_user.isChecked();

		if (admin) {
			person.setAuthority(UserTag.ROLE_SUPERVISOR);
		}

		// set enable true by default
		person.setEnabled(Boolean.TRUE);

		personDao.savePerson(person);

		// reset data info
		resetDataInfo();

		Messagebox.show("Aggiunto elemento", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		// set user ListBox
		setUserListBox();

		grid_user_details.setVisible(false);
		add_users_command.setVisible(false);
		modify_users_command.setVisible(false);

	}

	@Listen("onClick = #modify_mail_user")
	public void changeMailActionUser(final Event evt) {

		try {

			if ((sw_list_user.getSelectedItem() == null) || (sw_list_user.getSelectedItem().getValue() == null)
					|| !(sw_list_user.getSelectedItem().getValue() instanceof Person)) {
				return;
			}

			final Person person_selected = sw_list_user.getSelectedItem().getValue();

			// check over password
			final String hashing_password = Utility.encodeSHA256(mailpassword_user.getValue(), person_selected.getEmail());
			if (!hashing_password.equals(person_selected.getPassword())) {
				Messagebox.show("la password inserita non è corretta", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);

				// set fields
				mailpassword_user.setValue("");

				return;

			}

			if (!email_editor_user.getValue().equals(email_editor_user_retype.getValue())) {
				Messagebox.show("Le mail devono essere uguali", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);

				// set fields
				mailpassword_user.setValue("");

				return;
			}

			// change password
			personDao.changeMail(person_selected.getId(), mailpassword_user.getValue(), email_editor_user.getValue());

			person_selected.setEmail(email_editor_user.getValue());
			sw_list_user.getSelectedItem().setValue(person_selected);

			// set fields
			mailpassword_user.setValue("");

			email_user.setValue(person_selected.getEmail());

			Messagebox.show("Mail Cambiata", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		}

		catch (final WrongValueException e) {

			Messagebox.show("Dati digitati non corretti", "INFO", Messagebox.OK, Messagebox.ERROR);

		}

	}

	@Listen("onClick = #modify_mail_password")
	public void changePassword(final Event evt) {
		try {

			if ((sw_list_user.getSelectedItem() == null) || (sw_list_user.getSelectedItem().getValue() == null)
					|| !(sw_list_user.getSelectedItem().getValue() instanceof Person)) {
				return;
			}

			final Person person_selected = sw_list_user.getSelectedItem().getValue();

			// WARNING
			if (!password_editor_user.getValue().equals(password_editor_user_retype.getValue())) {
				Messagebox.show("Le Password devono essere uguali", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			// change password
			personDao.changePassword(person_selected.getEmail(), password_editor_user.getValue());

			// reset fields
			password_editor_user.setValue("");
			password_editor_user_retype.setValue("");

			Messagebox.show("Password Aggiornata", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		}

		catch (final WrongValueException e) {

			Messagebox.show("Errore nell'inserimento dei valori", "INFO", Messagebox.OK, Messagebox.ERROR);

		}

	}

	@Listen("onClick = #sw_link_modifyeuser")
	public void defineModifyView() {

		if ((sw_list_user.getSelectedItem() == null) || (sw_list_user.getSelectedItem().getValue() == null)
				|| !(sw_list_user.getSelectedItem().getValue() instanceof Person)) {
			return;
		}

		Person person_selected = sw_list_user.getSelectedItem().getValue();

		// get the last person from database
		person_selected = personDao.loadPerson(person_selected.getId());

		email_user.setValue(person_selected.getEmail());
		email_user.setDisabled(true);

		// disable command about credential
		row_email_user_retype.setVisible(false);
		row_password_user.setVisible(false);
		row_password_user_retype.setVisible(false);

		firstname_user.setValue(person_selected.getFirstname());
		lastname_user.setValue(person_selected.getLastname());
		city_user.setValue(person_selected.getCity());
		address_user.setValue(person_selected.getAddress());
		phone_user.setValue(person_selected.getPhone());
		postalCode_user.setValue(person_selected.getZip());
		user_enabled.setChecked(person_selected.isEnabled());
		country_user.setValue(person_selected.getCountry());
		provincia_user.setValue(person_selected.getProvincia());
		employee_identification_user.setValue(person_selected.getEmployee_identification());
		fiscalcode_user.setValue(person_selected.getFiscal_code());
		birth_date_user.setValue(person_selected.getBirth_date());
		birth_place_user.setValue(person_selected.getBirth_place());
		education_user.setValue(person_selected.getEducation());
		employment_date_user.setValue(person_selected.getEmployment());
		date_fired_user.setValue(person_selected.getDate_fired());
		date_modify_user.setValue(person_selected.getDate_modify());
		ncfl_user.setValue(person_selected.getNcfl());
		department_user.setValue(person_selected.getDepartment());
		current_position_user.setValue(person_selected.getCurrent_position());
		employee_level_user.setValue(person_selected.getEmployee_level());
		billcenter_user.setValue(person_selected.getBill_center());
		driving_license_emission_user.setValue(person_selected.getDriving_license_emission());
		nbudje_user.setValue(person_selected.getNbudge());

		// set status
		final String status = person_selected.getStatus();
		final List<Comboitem> lists = user_status.getItems();
		for (final Comboitem item : lists) {
			if (item.getValue().equals(status)) {
				user_status.setSelectedItem(item);
				break;
			}
		}

		// set users
		admin_user.setChecked(person_selected.isAdministrator());
		viewer_user.setChecked(person_selected.isViewer());
		backoffice_user.setChecked(person_selected.isBackoffice());
		operative_user.setChecked(person_selected.isOperative());

		// set panel modify
		panel_modify.setVisible(true);

	}

	@Listen("onClick = #deleteuser_command")
	public void deleteUserCommand() {

		try {
			if ((sw_list_user.getSelectedItem() == null) || (sw_list_user.getSelectedItem().getValue() == null)
					|| !(sw_list_user.getSelectedItem().getValue() instanceof Person)) {
				return;
			}

			final Person person_selected = sw_list_user.getSelectedItem().getValue();

			personDao.removePerson(person_selected.getId());

			// update list
			setUserListBox();

			box_deleteuser.setVisible(false);

			Messagebox.show("Utente cancellato", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		} catch (final Exception e) {

			logger.error("Error removing user. " + e.getMessage());

			Messagebox.show("Non è possibile eliminare questo utente.\nControlla che non ci siano azioni legate a questa angrafica.", "INFO",
					Messagebox.OK, Messagebox.ERROR);

		}

	}

	@Override
	public void doFinally() throws Exception {

		getSelf().addEventListener(ZkEventsTag.onShowUsers, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// get the person dao
				personDao = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);

				UserDetailsComposer.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #modify_users_command")
	public void modifyCommand() {

		if ((sw_list_user.getSelectedItem() == null) || (sw_list_user.getSelectedItem().getValue() == null)
				|| !(sw_list_user.getSelectedItem().getValue() instanceof Person)) {
			return;
		}

		final Person person_selected = sw_list_user.getSelectedItem().getValue();

		person_selected.setFirstname(firstname_user.getValue());
		person_selected.setLastname(lastname_user.getValue());
		person_selected.setCity(city_user.getValue());
		person_selected.setAddress(address_user.getValue());
		person_selected.setPhone(phone_user.getValue());
		person_selected.setZip(postalCode_user.getValue());
		person_selected.setEnabled(user_enabled.isChecked());
		person_selected.setEmployee_identification(employee_identification_user.getValue());
		person_selected.setProvincia(provincia_user.getValue());
		person_selected.setFiscal_code(fiscalcode_user.getValue());
		person_selected.setBirth_date(birth_date_user.getValue());
		person_selected.setBirth_place(birth_place_user.getValue());
		person_selected.setEducation(education_user.getValue());
		person_selected.setEmployment(employment_date_user.getValue());
		person_selected.setDate_fired(date_fired_user.getValue());
		person_selected.setDate_modify(date_modify_user.getValue());
		person_selected.setNcfl(ncfl_user.getValue());
		person_selected.setDepartment(department_user.getValue());
		person_selected.setCurrent_position(current_position_user.getValue());
		person_selected.setEmployee_level(employee_level_user.getValue());
		person_selected.setBill_center(billcenter_user.getValue());
		person_selected.setDriving_license_emission(driving_license_emission_user.getValue());
		person_selected.setCountry(country_user.getValue());

		// set status
		if (user_status.getSelectedItem() != null) {
			final String status_val = user_status.getSelectedItem().getValue();
			person_selected.setStatus(status_val);
		}

		// set authority
		String auth = UserTag.ROLE_USER;
		if (admin_user.isChecked()) {
			auth = UserTag.ROLE_SUPERVISOR;
		}

		if (operative_user.isChecked()) {
			if (!auth.equals(UserTag.ROLE_USER)) {
				auth = auth + "," + UserTag.ROLE_OPERATIVE;
			}
		}

		if (backoffice_user.isChecked()) {
			if (!auth.equals(UserTag.ROLE_USER)) {
				auth = auth + "," + UserTag.ROLE_BACKOFFICE;

			}
		}

		if (viewer_user.isChecked()) {
			if (!auth.equals(UserTag.ROLE_USER)) {
				auth = auth + "," + UserTag.ROLE_VIEWER;
			}
		}
		person_selected.setAuthority(auth);

		// update
		personDao.updatePerson(person_selected);

		// update list
		setUserListBox();

		grid_user_details.setVisible(false);

		Messagebox.show("Dati Utente aggiornati", "INFO", Messagebox.OK, Messagebox.INFORMATION);

	}

	@Listen("onClick = #sw_refresh_list")
	public void refreshList() {

		// set user listbox
		setUserListBox();
	}

	/**
	 * Reset data on user grid
	 */
	private void resetDataInfo() {
		email_user.setValue("info@seawork.com");
		email_user_retype.setValue("info@seawork.com");
		password_user.setValue("");
		password_user_retype.setValue("");
		firstname_user.setValue("NOME");
		lastname_user.setValue("COGNOME");
		city_user.setValue("");
		address_user.setValue("");
		phone_user.setValue("");
		postalCode_user.setValue("");
		employee_identification_user.setValue("");
		provincia_user.setValue("");
		user_enabled.setChecked(Boolean.TRUE);
		user_status.setValue(null);
		fiscalcode_user.setValue("");
		birth_date_user.setValue(null);
		birth_place_user.setValue("");
		education_user.setValue("");
		employment_date_user.setValue(null);
		date_fired_user.setValue(null);
		country_user.setValue("");

		date_modify_user.setValue(null);
		ncfl_user.setValue("");
		department_user.setValue("");
		current_position_user.setValue("");
		employee_level_user.setValue("");
		billcenter_user.setValue(null);
		driving_license_emission_user.setValue(null);

		// set roles
		admin_user.setChecked(Boolean.FALSE);
		operative_user.setChecked(Boolean.FALSE);
		backoffice_user.setChecked(Boolean.FALSE);
		viewer_user.setChecked(Boolean.FALSE);

		// set enable email (due becouse modify process)
		email_user.setDisabled(false);

		// enable command about credential
		row_email_user_retype.setVisible(true);
		row_password_user.setVisible(true);
		row_password_user_retype.setVisible(true);
	}

	@Listen("onClick = #sw_select_admins")
	public void selectAdmins() {

		final List<Person> list_person = personDao.usersAdmin();

		sw_list_user.setModel(new ListModelList<Person>(list_person));

	}

	/**
	 * Show users
	 */
	public void setInitialView() {

		// set user listbox
		setUserListBox();

		// initial view
		grid_user_details.setVisible(false);

	}

	/**
	 * Set user list box with initial events
	 */
	private void setUserListBox() {

		final List<Person> list_person = personDao.listAllPersons();

		sw_list_user.setModel(new ListModelList<Person>(list_person));
	}

	@Listen("onClick = #sw_adduser")
	public void showGridAddUser() {

		resetDataInfo();

		// set panel modify
		panel_modify.setVisible(false);

	}
}
