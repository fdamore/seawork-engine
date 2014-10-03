package org.uario.seaworkengine.zkevent;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Employment;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.EmploymentDAO;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.excpetions.UserNameJustPresentExcpetion;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.UserTag;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

public class UserDetailsComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Component			add_users_command;

	@Wire
	private Textbox				address_user;

	@Wire
	private Checkbox			admin_user;

	@Wire
	private Checkbox			backoffice_user;

	@Wire
	private Combobox			billcenter_user;

	@Wire
	private Datebox				birth_date_user;

	@Wire
	private Textbox				birth_place_user;

	@Wire
	private Div					box_deleteuser;

	@Wire
	private Textbox				city_user;

	@Wire
	private Component			contestations_user_tab;

	@Wire
	private Textbox				country_user;

	@Wire
	private Textbox				current_position_user;

	@Wire
	private Datebox				date_fired_user;

	@Wire
	private Textbox				department_user;

	@Wire
	private Tab					detail_user_tab;

	@Wire
	private Textbox				driving_license_charge_user;

	@Wire
	private Datebox				driving_license_emission_user;

	@Wire
	private Textbox				driving_license_user;

	@Wire
	private Textbox				education_user;

	@Wire
	private Textbox				email_editor_user;

	@Wire
	private Textbox				email_editor_user_retype;

	@Wire
	private Textbox				email_user;

	@Wire
	private Textbox				email_user_retype;

	@Wire
	private Textbox				employee_identification_user;

	@Wire
	private Textbox				employee_level_user;

	@Wire
	private Datebox				employment_date_user;

	// DAO for access amployment data
	private EmploymentDAO		employmentDao;

	@Wire
	private Textbox				family_charge_user;

	@Wire
	private Textbox				firstname_user;

	@Wire
	private Component			fiscalcheck_user_tab;

	@Wire
	private Textbox				fiscalcode_user;

	@Wire
	private Component			grid_user_details;

	@Wire
	private Textbox				lastname_user;

	private final Logger		logger				= Logger.getLogger(UserDetailsComposer.class);

	@Wire
	private Component			mail_user_tab;

	@Wire
	private Textbox				mailpassword_user;

	@Wire
	private Textbox				marital_status_user;

	@Wire
	private Component			modify_users_command;

	@Wire
	private Textbox				nbudje_user;

	@Wire
	private Textbox				ncfl_user;

	@Wire
	private Textbox				npass_user;

	@Wire
	private Checkbox			operative_user;

	@Wire
	private Textbox				password_editor_user;

	@Wire
	private Textbox				password_editor_user_retype;

	@Wire
	private Textbox				password_user;

	@Wire
	private Textbox				password_user_retype;

	@Wire
	private Component			password_user_tab;

	// the dao used for db interaction
	private PersonDAO			personDao;

	@Wire
	private Textbox				phone_user;

	@Wire
	private Textbox				postalCode_user;

	@Wire
	private Textbox				provincia_user;

	@Wire
	private Row					row_email_user_retype;

	@Wire
	private Row					row_password_user;

	@Wire
	private Row					row_password_user_retype;

	// notify status chnaged
	private boolean				status_changed		= false;

	@Wire
	private Component			status_user_tab;

	@Wire
	private Listbox				sw_list_user;

	@Wire
	private Component			task_user_tab;

	@Wire
	private Component			tfr_user_tab;

	@Wire
	private Component			tradeunion_user_tab;

	@Wire
	private Checkbox			user_enabled;

	@Wire
	private Combobox			user_status;

	@Wire
	private Checkbox			viewer_user;

	@Listen("onClick = #add_users_command")
	public void addUserCommand() throws UserNameJustPresentExcpetion {

		// check info mail
		final String mail = this.email_user.getValue();
		final String retype_mail = this.email_user_retype.getValue();
		if (!retype_mail.equals(mail)) {
			Messagebox.show("Ridigita la Mail!", "ERROR", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		// check info password
		final String retype_password = this.password_user_retype.getValue();
		final String password = this.password_user.getValue();
		if (retype_password.equals("") || !retype_password.equals(password)) {
			Messagebox.show("Ridigita la password!", "ERROR", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		// check mail single....
		final Object ob = this.personDao.loadUserByUsernameIfAny(mail);
		if ((ob != null) && (ob instanceof Person)) {
			Messagebox.show("Mail già presente", "ERROR", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		final Person person = new Person();
		person.setAddress(this.address_user.getValue());
		person.setCity(this.city_user.getValue());
		person.setEmail(this.email_user.getValue());
		person.setFirstname(this.firstname_user.getValue());
		person.setLastname(this.lastname_user.getValue());
		person.setPassword(this.password_user.getValue());
		person.setPhone(this.phone_user.getValue());
		person.setZip(this.postalCode_user.getValue());
		person.setCountry(this.country_user.getValue());
		person.setEnabled(this.user_enabled.isChecked());
		person.setEmployee_identification(this.employee_identification_user.getValue());
		person.setProvincia(this.provincia_user.getValue());
		person.setFiscal_code(this.fiscalcode_user.getValue());
		person.setBirth_date(this.birth_date_user.getValue());
		person.setBirth_place(this.birth_place_user.getValue());
		person.setEducation(this.education_user.getValue());
		person.setEmployment(this.employment_date_user.getValue());
		person.setDate_fired(this.date_fired_user.getValue());
		person.setNcfl(this.ncfl_user.getValue());
		person.setDepartment(this.department_user.getValue());
		person.setCurrent_position(this.current_position_user.getValue());
		person.setEmployee_level(this.employee_level_user.getValue());
		person.setBill_center(this.billcenter_user.getValue());

		person.setNbudge(this.nbudje_user.getValue());
		person.setNpass(this.npass_user.getValue());
		person.setMarital_status(this.marital_status_user.getValue());
		person.setFamily_charge(this.family_charge_user.getValue());
		person.setDriving_license(this.driving_license_user.getValue());
		person.setDriving_license_emission(this.driving_license_emission_user.getValue());

		// set authority
		person.setAuthority(UserTag.ROLE_USER);

		final boolean admin = this.admin_user.isChecked();

		if (admin) {
			person.setAuthority(UserTag.ROLE_SUPERVISOR);
		}

		// set enable true by default
		person.setEnabled(Boolean.TRUE);

		this.personDao.savePerson(person);

		// reset data info
		this.resetDataInfo();

		Messagebox.show("Aggiunto elemento", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		// set user ListBox
		this.setUserListBox();

		this.grid_user_details.setVisible(false);
		this.add_users_command.setVisible(false);
		this.modify_users_command.setVisible(false);

	}

	@Listen("onChange = #user_status")
	public void change_user_status() {
		this.status_changed = true;

	}

	@Listen("onClick = #modify_mail_user")
	public void changeMailActionUser(final Event evt) {

		try {

			if ((this.sw_list_user.getSelectedItem() == null) || (this.sw_list_user.getSelectedItem().getValue() == null)
					|| !(this.sw_list_user.getSelectedItem().getValue() instanceof Person)) {
				return;
			}

			final Person person_selected = this.sw_list_user.getSelectedItem().getValue();

			// check over password
			final String hashing_password = Utility.encodeSHA256(this.mailpassword_user.getValue(), person_selected.getEmail());
			if (!hashing_password.equals(person_selected.getPassword())) {
				Messagebox.show("la password inserita non è corretta", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);

				// set fields
				this.mailpassword_user.setValue("");

				return;

			}

			if (!this.email_editor_user.getValue().equals(this.email_editor_user_retype.getValue())) {
				Messagebox.show("Le mail devono essere uguali", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);

				// set fields
				this.mailpassword_user.setValue("");

				return;
			}

			// change password
			this.personDao.changeMail(person_selected.getId(), this.mailpassword_user.getValue(), this.email_editor_user.getValue());

			person_selected.setEmail(this.email_editor_user.getValue());
			this.sw_list_user.getSelectedItem().setValue(person_selected);

			// set fields
			this.mailpassword_user.setValue("");

			this.email_user.setValue(person_selected.getEmail());

			Messagebox.show("Mail Cambiata", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		}

		catch (final WrongValueException e) {

			Messagebox.show("Dati digitati non corretti", "INFO", Messagebox.OK, Messagebox.ERROR);

		}

	}

	@Listen("onClick = #modify_mail_password")
	public void changePassword(final Event evt) {
		try {

			if ((this.sw_list_user.getSelectedItem() == null) || (this.sw_list_user.getSelectedItem().getValue() == null)
					|| !(this.sw_list_user.getSelectedItem().getValue() instanceof Person)) {
				return;
			}

			final Person person_selected = this.sw_list_user.getSelectedItem().getValue();

			// WARNING
			if (!this.password_editor_user.getValue().equals(this.password_editor_user_retype.getValue())) {
				Messagebox.show("Le Password devono essere uguali", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			// change password
			this.personDao.changePassword(person_selected.getEmail(), this.password_editor_user.getValue());

			// reset fields
			this.password_editor_user.setValue("");
			this.password_editor_user_retype.setValue("");

			Messagebox.show("Password Aggiornata", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		}

		catch (final WrongValueException e) {

			Messagebox.show("Errore nell'inserimento dei valori", "INFO", Messagebox.OK, Messagebox.ERROR);

		}

	}

	@Listen("onClick = #sw_link_modifyeuser")
	public void defineModifyView() {

		if ((this.sw_list_user.getSelectedItem() == null) || (this.sw_list_user.getSelectedItem().getValue() == null)
				|| !(this.sw_list_user.getSelectedItem().getValue() instanceof Person)) {
			return;
		}

		Person person_selected = this.sw_list_user.getSelectedItem().getValue();

		// get the last person from database
		person_selected = this.personDao.loadPerson(person_selected.getId());

		// general details
		this.defineUserDetailsView(person_selected);

	}

	/**
	 * define user details view for modify
	 *
	 * @param person_selected
	 */
	private void defineUserDetailsView(final Person person_selected) {
		this.email_user.setValue(person_selected.getEmail());
		this.email_user.setDisabled(true);

		// disable command about credential
		this.row_email_user_retype.setVisible(false);
		this.row_password_user.setVisible(false);
		this.row_password_user_retype.setVisible(false);

		this.firstname_user.setValue(person_selected.getFirstname());
		this.lastname_user.setValue(person_selected.getLastname());
		this.city_user.setValue(person_selected.getCity());
		this.address_user.setValue(person_selected.getAddress());
		this.phone_user.setValue(person_selected.getPhone());
		this.postalCode_user.setValue(person_selected.getZip());
		this.user_enabled.setChecked(person_selected.isEnabled());
		this.country_user.setValue(person_selected.getCountry());
		this.provincia_user.setValue(person_selected.getProvincia());
		this.employee_identification_user.setValue(person_selected.getEmployee_identification());
		this.fiscalcode_user.setValue(person_selected.getFiscal_code());
		this.birth_date_user.setValue(person_selected.getBirth_date());
		this.birth_place_user.setValue(person_selected.getBirth_place());
		this.education_user.setValue(person_selected.getEducation());
		this.employment_date_user.setValue(person_selected.getEmployment());
		this.date_fired_user.setValue(person_selected.getDate_fired());
		this.ncfl_user.setValue(person_selected.getNcfl());
		this.department_user.setValue(person_selected.getDepartment());
		this.current_position_user.setValue(person_selected.getCurrent_position());
		this.employee_level_user.setValue(person_selected.getEmployee_level());
		this.billcenter_user.setValue(person_selected.getBill_center());

		this.nbudje_user.setValue(person_selected.getNbudge());
		this.npass_user.setValue(person_selected.getNpass());
		this.marital_status_user.setValue(person_selected.getMarital_status());
		this.family_charge_user.setValue(person_selected.getFamily_charge());
		this.driving_license_user.setValue(person_selected.getDriving_license());
		this.driving_license_emission_user.setValue(person_selected.getDriving_license_emission());

		// set status
		final String status = person_selected.getStatus();
		final List<Comboitem> lists = this.user_status.getItems();
		for (final Comboitem item : lists) {
			if (item.getValue().equals(status)) {
				this.user_status.setSelectedItem(item);
				break;
			}
		}

		// set users
		this.admin_user.setChecked(person_selected.isAdministrator());
		this.viewer_user.setChecked(person_selected.isViewer());
		this.backoffice_user.setChecked(person_selected.isBackoffice());
		this.operative_user.setChecked(person_selected.isOperative());

		// send mail to components
		this.sendPersonToUserComponents(person_selected);

	}

	@Listen("onClick = #deleteuser_command")
	public void deleteUserCommand() {

		try {
			if ((this.sw_list_user.getSelectedItem() == null) || (this.sw_list_user.getSelectedItem().getValue() == null)
					|| !(this.sw_list_user.getSelectedItem().getValue() instanceof Person)) {
				return;
			}

			final Person person_selected = this.sw_list_user.getSelectedItem().getValue();

			this.personDao.removePerson(person_selected.getId());

			// update list
			this.setUserListBox();

			this.box_deleteuser.setVisible(false);

			Messagebox.show("Utente cancellato", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		} catch (final Exception e) {

			this.logger.error("Error removing user. " + e.getMessage());

			Messagebox.show("Non è possibile eliminare questo utente.\nControlla che non ci siano azioni legate a questa angrafica.", "INFO",
					Messagebox.OK, Messagebox.ERROR);

		}

	}

	@Override
	public void doFinally() throws Exception {

		this.getSelf().addEventListener(ZkEventsTag.onShowUsers, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// get the DAOs
				UserDetailsComposer.this.personDao = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);
				UserDetailsComposer.this.employmentDao = (EmploymentDAO) SpringUtil.getBean(BeansTag.EMPLOYMENT_DAO);

				// reset notify status changed
				UserDetailsComposer.this.status_changed = false;

				UserDetailsComposer.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #modify_users_command")
	public void modifyCommand() {

		if ((this.sw_list_user.getSelectedItem() == null) || (this.sw_list_user.getSelectedItem().getValue() == null)
				|| !(this.sw_list_user.getSelectedItem().getValue() instanceof Person)) {
			return;
		}

		final Person person_selected = this.sw_list_user.getSelectedItem().getValue();

		person_selected.setFirstname(this.firstname_user.getValue());
		person_selected.setLastname(this.lastname_user.getValue());
		person_selected.setCity(this.city_user.getValue());
		person_selected.setAddress(this.address_user.getValue());
		person_selected.setPhone(this.phone_user.getValue());
		person_selected.setZip(this.postalCode_user.getValue());
		person_selected.setEnabled(this.user_enabled.isChecked());
		person_selected.setEmployee_identification(this.employee_identification_user.getValue());
		person_selected.setProvincia(this.provincia_user.getValue());
		person_selected.setFiscal_code(this.fiscalcode_user.getValue());
		person_selected.setBirth_date(this.birth_date_user.getValue());
		person_selected.setBirth_place(this.birth_place_user.getValue());
		person_selected.setEducation(this.education_user.getValue());
		person_selected.setEmployment(this.employment_date_user.getValue());
		person_selected.setDate_fired(this.date_fired_user.getValue());
		person_selected.setNcfl(this.ncfl_user.getValue());
		person_selected.setDepartment(this.department_user.getValue());
		person_selected.setCurrent_position(this.current_position_user.getValue());
		person_selected.setEmployee_level(this.employee_level_user.getValue());
		person_selected.setBill_center(this.billcenter_user.getValue());
		person_selected.setCountry(this.country_user.getValue());
		person_selected.setNbudge(this.nbudje_user.getValue());
		person_selected.setNpass(this.npass_user.getValue());
		person_selected.setMarital_status(this.marital_status_user.getValue());
		person_selected.setFamily_charge(this.family_charge_user.getValue());
		person_selected.setDriving_license(this.driving_license_user.getValue());
		person_selected.setDriving_license_emission(this.driving_license_emission_user.getValue());

		// set status
		if (this.user_status.getSelectedItem() != null) {
			final String status_val = this.user_status.getSelectedItem().getValue();
			person_selected.setStatus(status_val);
		}

		// set authority
		String auth = UserTag.ROLE_USER;
		if (this.admin_user.isChecked()) {
			auth = UserTag.ROLE_SUPERVISOR;
		}

		if (this.operative_user.isChecked()) {
			if (!auth.equals(UserTag.ROLE_USER)) {
				auth = auth + "," + UserTag.ROLE_OPERATIVE;
			}
		}

		if (this.backoffice_user.isChecked()) {
			if (!auth.equals(UserTag.ROLE_USER)) {
				auth = auth + "," + UserTag.ROLE_BACKOFFICE;

			}
		}

		if (this.viewer_user.isChecked()) {
			if (!auth.equals(UserTag.ROLE_USER)) {
				auth = auth + "," + UserTag.ROLE_VIEWER;
			}
		}
		person_selected.setAuthority(auth);

		// update
		this.personDao.updatePerson(person_selected);

		// set status cnaged
		if (this.status_changed) {

			// get status
			String status_val = null;
			if (this.user_status.getSelectedItem() != null) {
				status_val = this.user_status.getSelectedItem().getValue();
			}

			final Employment employment = new Employment();
			employment.setId_user(person_selected.getId());
			employment.setDate_modified(Calendar.getInstance().getTime());
			employment.setStatus(status_val);

			this.employmentDao.createEmploymentForUser(person_selected.getId(), employment);

			// set status notify flag
			this.status_changed = false;
		}

		// update list
		this.setUserListBox();

		this.grid_user_details.setVisible(false);

		Messagebox.show("Dati Utente aggiornati", "INFO", Messagebox.OK, Messagebox.INFORMATION);

	}

	@Listen("onClick = #sw_refresh_list")
	public void refreshListUser() {

		// set user listbox
		this.setUserListBox();
	}

	/**
	 * Reset data on user grid
	 */
	private void resetDataInfo() {
		this.email_user.setValue("info@seawork.com");
		this.email_user_retype.setValue("info@seawork.com");
		this.password_user.setValue("");
		this.password_user_retype.setValue("");
		this.firstname_user.setValue("NOME");
		this.lastname_user.setValue("COGNOME");
		this.city_user.setValue("");
		this.address_user.setValue("");
		this.phone_user.setValue("");
		this.postalCode_user.setValue("");
		this.employee_identification_user.setValue("");
		this.provincia_user.setValue("");
		this.user_enabled.setChecked(Boolean.TRUE);
		this.user_status.setValue(null);
		this.fiscalcode_user.setValue("");
		this.birth_date_user.setValue(null);
		this.birth_place_user.setValue("");
		this.education_user.setValue("");
		this.employment_date_user.setValue(null);
		this.date_fired_user.setValue(null);
		this.country_user.setValue("");
		this.ncfl_user.setValue("");
		this.department_user.setValue("");
		this.current_position_user.setValue("");
		this.employee_level_user.setValue("");
		this.billcenter_user.setValue(null);

		this.nbudje_user.setValue("");

		this.npass_user.setValue("");
		this.marital_status_user.setValue("");
		this.family_charge_user.setValue("");
		this.driving_license_user.setValue("");
		this.driving_license_emission_user.setValue(null);

		// set roles
		this.admin_user.setChecked(Boolean.FALSE);
		this.operative_user.setChecked(Boolean.FALSE);
		this.backoffice_user.setChecked(Boolean.FALSE);
		this.viewer_user.setChecked(Boolean.FALSE);

		// set enable email (due becouse modify process)
		this.email_user.setDisabled(false);

		// enable command about credential
		this.row_email_user_retype.setVisible(true);
		this.row_password_user.setVisible(true);
		this.row_password_user_retype.setVisible(true);
	}

	@Listen("onClick = #sw_select_admins")
	public void selectAdmins() {

		final List<Person> list_person = this.personDao.usersAdmin();

		this.sw_list_user.setModel(new ListModelList<Person>(list_person));

	}

	/**
	 * Send person_selected to other component
	 *
	 * @param person_selected
	 */
	private void sendPersonToUserComponents(final Person person_selected) {

		// send event to show user task
		final Component comp = Path.getComponent("//usertask/panel_task");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp, person_selected);

		// send event to show user task
		final Component comp_tfr = Path.getComponent("//usertfr/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_tfr, person_selected);

		// send event to show user fc
		final Component comp_fc = Path.getComponent("//userfc/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_fc, person_selected);

		// send event to show user td
		final Component comp_td = Path.getComponent("//usertd/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_td, person_selected);

		// send event to show user status
		final Component comp_status = Path.getComponent("//userstatus/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_status, person_selected);

		// send event to show contestations
		final Component comp_cons = Path.getComponent("//cons/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_cons, person_selected);

	}

	/**
	 * Show users
	 */
	public void setInitialView() {

		// set user listbox
		this.setUserListBox();

		// initial view
		this.grid_user_details.setVisible(false);

	}

	/**
	 * Set user list box with initial events
	 */
	private void setUserListBox() {

		final List<Person> list_person = this.personDao.listAllPersons();

		this.sw_list_user.setModel(new ListModelList<Person>(list_person));
	}

	@Listen("onClick = #sw_adduser")
	public void showAddUserPanel() {

		// command
		this.add_users_command.setVisible(true);
		this.modify_users_command.setVisible(false);

		// configure tab
		this.grid_user_details.setVisible(true);
		this.mail_user_tab.setVisible(false);
		this.password_user_tab.setVisible(false);
		this.task_user_tab.setVisible(false);
		this.status_user_tab.setVisible(false);
		this.tfr_user_tab.setVisible(false);
		this.fiscalcheck_user_tab.setVisible(false);
		this.tradeunion_user_tab.setVisible(false);
		this.contestations_user_tab.setVisible(false);

		// set detail to selection
		this.detail_user_tab.getTabbox().setSelectedTab(this.detail_user_tab);

	}

	@Listen("onClick = #sw_adduser")
	public void showGridAddUser() {

		this.resetDataInfo();

	}

	@Listen("onClick = #sw_link_modifyeuser")
	public void showModifyUserPanel() {

		// command
		this.add_users_command.setVisible(false);
		this.modify_users_command.setVisible(true);

		// configure tab
		this.grid_user_details.setVisible(true);
		this.mail_user_tab.setVisible(true);
		this.password_user_tab.setVisible(true);
		this.task_user_tab.setVisible(true);
		this.status_user_tab.setVisible(true);
		this.tfr_user_tab.setVisible(true);
		this.fiscalcheck_user_tab.setVisible(true);
		this.tradeunion_user_tab.setVisible(true);
		this.contestations_user_tab.setVisible(true);

		// set detail to selection
		this.detail_user_tab.getTabbox().setSelectedTab(this.detail_user_tab);
	}
}
