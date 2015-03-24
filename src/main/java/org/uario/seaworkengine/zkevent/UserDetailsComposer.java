package org.uario.seaworkengine.zkevent;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.excpetions.UserNameJustPresentExcpetion;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.CFGenerator;
import org.uario.seaworkengine.utility.UserStatusTag;
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
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
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
	private Datebox				birth_date_user;

	@Wire
	private Combobox			birth_place_user;

	@Wire
	private Combobox			birth_province_user;

	@Wire
	private Textbox				city_user;

	public Comboitem			comuneSelected;

	@Wire
	private Component			contestations_user_tab;

	@Wire
	private Textbox				country_user;

	@Wire
	private Textbox				current_position_user;

	@Wire
	private Checkbox			dailyemployee;

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
	private Textbox				family_charge_user;

	@Wire
	private Textbox				firstname_user;

	@Wire
	private Component			fiscalcheck_user_tab;

	@Wire
	private Textbox				fiscalcode_user;

	@Wire
	private Textbox				full_text_search;

	@Wire
	private Component			grid_user_details;

	@Wire
	private Component			jobcost_user_tab;

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
	private Checkbox			out_schedule_user;

	@Wire
	private Checkbox			partTime;

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

	Person						person_selected		= null;

	@Wire
	private Textbox				personal_code_user;

	// the dao used for db interaction
	private PersonDAO			personDao;

	@Wire
	private Textbox				phone_user;

	@Wire
	private Textbox				postalCode_user;

	@Wire
	private Textbox				provincia_user;

	@Wire
	private Component			qrcode_gen;

	@Wire
	private Row					row_email_user_retype;

	@Wire
	private Row					row_password_user;

	@Wire
	private Row					row_password_user_retype;

	@Wire
	private Combobox			select_specific_user;

	@Wire
	private Combobox			sex_user;

	@Wire
	private Intbox				shows_rows;

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
	private Label				user_status;

	@Wire
	private A					userName;

	@Wire
	private Checkbox			viewer_user;

	@Listen("onClick = #add_users_command")
	public void addUserCommand() throws UserNameJustPresentExcpetion {

		// check info mail
		String mail = this.email_user.getValue();

		if ((mail != null) && !mail.equals("")) {

			final String retype_mail = this.email_user_retype.getValue();
			if (!retype_mail.equals(mail)) {

				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Ridigita Username!", "ERROR", buttons, null, Messagebox.ERROR, null, null, params);
				return;
			}
		} else {
			mail = "" + Calendar.getInstance().getTimeInMillis();
		}

		// check mail single....
		final Object ob = this.personDao.loadUserByUsernameIfAny(mail);
		if ((ob != null) && (ob instanceof Person)) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Mail già presente!", "ERROR", buttons, null, Messagebox.ERROR, null, null, params);
			return;
		}

		final String password = this.password_user.getValue();

		if ((password != null) && !password.equals("")) {
			// check info password
			final String retype_password = this.password_user_retype.getValue();

			if (retype_password.equals("") || !retype_password.equals(password)) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Ridigita la password!", "ERROR", buttons, null, Messagebox.ERROR, null, null, params);
				return;
			}
		}

		if (!((this.email_user.getValue() == null) || (this.password_user.getValue() == null))) {
			final Person person = new Person();
			person.setAddress(this.address_user.getValue());
			person.setCity(this.city_user.getValue());
			person.setEmail(mail);
			person.setFirstname(this.firstname_user.getValue());
			person.setPersonal_code(this.personal_code_user.getValue());
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
			person.setDailyemployee(this.dailyemployee.isChecked());
			person.setPart_time(this.partTime.isChecked());
			if (this.birth_place_user.getSelectedItem() != null) {
				person.setBirth_place(this.birth_place_user.getSelectedItem().getValue().toString());
			}
			if (this.birth_province_user.getSelectedItem() != null) {
				person.setBirth_province(this.birth_province_user.getSelectedItem().getValue().toString());
			}
			person.setEducation(this.education_user.getValue());
			person.setNcfl(this.ncfl_user.getValue());
			person.setDepartment(this.department_user.getValue());
			person.setCurrent_position(this.current_position_user.getValue());
			person.setNbudge(this.nbudje_user.getValue());
			person.setNpass(this.npass_user.getValue());
			person.setMarital_status(this.marital_status_user.getValue());
			person.setFamily_charge(this.family_charge_user.getValue());
			person.setDriving_license(this.driving_license_user.getValue());
			person.setDriving_license_emission(this.driving_license_emission_user.getValue());

			if (this.sex_user.getSelectedItem() != null) {
				if (this.sex_user.getSelectedItem().toString() == "M") {
					person.setSex(true);
				} else {
					person.setSex(false);
				}
			} else {
				person.setSex(true);
			}

			person.setOut_schedule(this.out_schedule_user.isChecked());

			// add initial status
			person.setStatus(UserStatusTag.OPEN);

			// set authority
			String auth = UserTag.ROLE_USER;
			if (this.admin_user.isChecked()) {
				auth = UserTag.ROLE_SUPERVISOR;
			}

			if (this.operative_user.isChecked()) {
				if (!auth.equals(UserTag.ROLE_USER)) {
					auth = auth + "," + UserTag.ROLE_OPERATIVE;
				} else {
					auth = UserTag.ROLE_OPERATIVE;
				}

			}

			if (this.backoffice_user.isChecked()) {
				if (!auth.equals(UserTag.ROLE_USER)) {
					auth = auth + "," + UserTag.ROLE_BACKOFFICE;
				} else {
					auth = UserTag.ROLE_BACKOFFICE;
				}

			}

			if (this.viewer_user.isChecked()) {
				if (!auth.equals(UserTag.ROLE_USER)) {
					auth = auth + "," + UserTag.ROLE_VIEWER;
				} else {
					auth = UserTag.ROLE_VIEWER;
				}
			}

			person.setAuthority(auth);

			// set enable true by default
			person.setEnabled(Boolean.TRUE);

			this.personDao.savePerson(person);

			// reset data info
			this.resetDataInfo();

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Aggiunto elemento", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

			// set user ListBox
			this.setUserListBox();

			this.grid_user_details.setVisible(false);
			this.add_users_command.setVisible(false);
			this.modify_users_command.setVisible(false);
		} else {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare valori inseriti (email, password, sesso). ", "INFO", buttons, null, Messagebox.EXCLAMATION, null, null,
					params);

		}

	}

	@Listen("onClick=#cfgenerator")
	public void calculateFiscalCode() {

		if ((this.firstname_user.getValue() == null) || (this.lastname_user.getValue() == null) || (this.birth_place_user.getSelectedItem() == null)
				|| (this.birth_province_user.getSelectedItem() == null) || (this.birth_date_user.getValue() == null)
				|| (this.sex_user.getSelectedItem() == null)) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;
			Messagebox.show("Verificare valori inseriti.", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			return;
		}

		final String n = this.firstname_user.getValue().toString();
		final String c = this.lastname_user.getValue().toString();
		String cc = "";

		cc = this.birth_place_user.getSelectedItem().getValue().toString();
		final String prov = this.birth_province_user.getSelectedItem().getValue().toString();

		final Date data = this.birth_date_user.getValue();

		String s = null;
		if (this.sex_user.getSelectedIndex() == 0) {
			s = "M";
		} else {
			s = "F";
		}

		try {
			final SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
			final String dt = sd.format(data);
			final int g = Integer.parseInt(dt.substring(0, 2));
			final int m = Integer.parseInt(dt.substring(3, 5));
			final int a = Integer.parseInt(dt.substring(6));
			String month = "";
			switch (m) {
			case 1:
				month = "Gennaio";
				break;
			case 2:
				month = "Febbraio";
				break;
			case 3:
				month = "Marzo";
				break;
			case 4:
				month = "Aprile";
				break;
			case 5:
				month = "Maggio";
				break;
			case 6:
				month = "Giugno";
				break;
			case 7:
				month = "Luglio";
				break;
			case 8:
				month = "Agosto";
				break;
			case 9:
				month = "Settembre";
				break;
			case 10:
				month = "Ottobre";
				break;
			case 11:
				month = "Novembre";
				break;
			case 12:
				month = "Dicembre";
				break;
			default:
				break;
			}

			final CFGenerator cfg = new CFGenerator(n, c, cc, month, a, g, s, prov);
			final String cf = cfg.getCodiceFiscale();
			this.fiscalcode_user.setValue(cf);
		} catch (final Exception e) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Verificare valori inseriti.", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			return;
		}

	}

	@Listen("onClick = #modify_mail_user")
	public void changeMailActionUser(final Event evt) {

		try {

			if (this.person_selected == null) {
				return;
			}

			// check over password
			final String hashing_password = Utility.encodeSHA256(this.mailpassword_user.getValue(), this.person_selected.getEmail());
			if (!hashing_password.equals(this.person_selected.getPassword())) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("La password inserita non è corretta ", "INFO", buttons, null, Messagebox.ERROR, null, null, params);

				return;

			}

			if (!this.email_editor_user.getValue().equals(this.email_editor_user_retype.getValue())) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Le mail devono essere uguali ", "INFO", buttons, null, Messagebox.ERROR, null, null, params);

				// set fields
				this.mailpassword_user.setValue("");

				return;
			}

			// control about mail
			final UserDetails check = this.personDao.loadUserByUsernameIfAny(this.email_editor_user.getValue());
			if (check != null) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Mail già presente e usata da altro operatore", "INFO", buttons, null, Messagebox.ERROR, null, null, params);

				// set fields
				this.email_editor_user.setValue("");

				return;

			}

			// change password
			this.personDao.changeMail(this.person_selected.getId(), this.mailpassword_user.getValue(), this.email_editor_user.getValue());

			this.person_selected.setEmail(this.email_editor_user.getValue());
			this.sw_list_user.getSelectedItem().setValue(this.person_selected);

			// set fields
			this.mailpassword_user.setValue("");

			this.email_user.setValue(this.person_selected.getEmail());

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Mail cambiata ", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		}

		catch (final WrongValueException e) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Dati digitati non corretti!", "INFO", buttons, null, Messagebox.ERROR, null, null, params);

		}

	}

	@Listen("onClick = #modify_mail_password")
	public void changePassword(final Event evt) {
		try {

			if (this.person_selected == null) {
				return;
			}

			// WARNING
			if (!this.password_editor_user.getValue().equals(this.password_editor_user_retype.getValue())) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Le password devono essere uguali", "INFO", buttons, null, Messagebox.EXCLAMATION, null, null, params);
				return;
			}

			// change password
			this.personDao.changePassword(this.person_selected.getEmail(), this.password_editor_user.getValue());

			// reset fields
			this.password_editor_user.setValue("");
			this.password_editor_user_retype.setValue("");

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Password Aggiornata ", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		}

		catch (final WrongValueException e) {

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Errore nell'inserimento dei valori", "INFO", buttons, null, Messagebox.ERROR, null, null, params);

		}

	}

	@Listen("onClick = #sw_link_deleteuser")
	public void defineDeleteView() {

		// take person
		this.person_selected = this.sw_list_user.getSelectedItem().getValue();

	}

	@Listen("onClick = #sw_link_modifyeuser")
	public void defineModifyView() {

		this.birth_place_user.setSelectedItem(null);
		this.birth_place_user.setModel(new ListModelList<String>());
		this.birth_place_user.setValue("");
		this.birth_province_user.setSelectedItem(null);

		if ((this.sw_list_user.getSelectedItem() == null) || (this.sw_list_user.getSelectedItem().getValue() == null)
				|| !(this.sw_list_user.getSelectedItem().getValue() instanceof Person)) {
			return;
		}

		// take person
		this.person_selected = this.sw_list_user.getSelectedItem().getValue();

		// get the last person from database
		this.person_selected = this.personDao.loadPerson(this.person_selected.getId());

		// general details
		this.defineUserDetailsView(this.person_selected);

		this.userName.setLabel(this.firstname_user.getValue() + " " + this.lastname_user.getValue());
		this.userName.setVisible(true);

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
		this.personal_code_user.setValue(person_selected.getPersonal_code());
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
		this.dailyemployee.setChecked(person_selected.getDailyemployee());
		this.partTime.setChecked(person_selected.getPart_time());

		if (person_selected.getSex()) {
			this.sex_user.setSelectedIndex(0);
		} else {
			this.sex_user.setSelectedIndex(1);
		}

		if (person_selected.getBirth_province() != null) {

			final List<Comboitem> listItem = this.birth_province_user.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof String) {
					if (item.getValue().toString().equals(person_selected.getBirth_province())) {
						this.birth_province_user.setSelectedItem(item);
						break;
					}
				}

			}

		}

		if (person_selected.getBirth_place() != null) {

			this.birth_place_user.setModel(new ListModelList<String>(this.personDao.loadComuniByProvincia(person_selected.getBirth_province())));
			this.birth_place_user.setSelectedItem(this.getComboItem(this.birth_place_user, person_selected.getBirth_place()));
			this.birth_place_user.setValue(person_selected.getBirth_place());

		}

		this.education_user.setValue(person_selected.getEducation());
		this.ncfl_user.setValue(person_selected.getNcfl());
		this.department_user.setValue(person_selected.getDepartment());
		this.current_position_user.setValue(person_selected.getCurrent_position());

		this.nbudje_user.setValue(person_selected.getNbudge());
		this.npass_user.setValue(person_selected.getNpass());
		this.marital_status_user.setValue(person_selected.getMarital_status());
		this.family_charge_user.setValue(person_selected.getFamily_charge());
		this.driving_license_user.setValue(person_selected.getDriving_license());
		this.driving_license_emission_user.setValue(person_selected.getDriving_license_emission());

		// set status
		this.user_status.setValue(person_selected.getStatus());

		// set users
		this.admin_user.setChecked(person_selected.isAdministrator());
		this.out_schedule_user.setChecked(person_selected.getOut_schedule());
		this.viewer_user.setChecked(person_selected.isViewer());
		this.backoffice_user.setChecked(person_selected.isBackoffice());
		this.operative_user.setChecked(person_selected.isOperative());

		// send mail to components
		this.sendPersonToUserComponents(person_selected);

	}

	private void deleteUserCommand() {

		try {
			if (this.person_selected == null) {
				return;
			}

			this.personDao.removePerson(this.person_selected.getId());

			// update list
			this.setUserListBox();

		} catch (final Exception e) {

			this.logger.error("Error removing user. " + e.getMessage());

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Non è possibile eliminare questo utente.\nControlla che non ci siano azioni legate a questa angrafica.", "INFO",
					buttons, null, Messagebox.EXCLAMATION, null, null, params);

		}

	}

	@Override
	public void doFinally() throws Exception {

		this.getSelf().addEventListener(ZkEventsTag.onShowUsers, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// get the DAOs
				UserDetailsComposer.this.personDao = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);

				UserDetailsComposer.this.setInitialView();

			}
		});

		this.getSelf().addEventListener(ZkEventsTag.onUpdateGeneralDetails, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				if (UserDetailsComposer.this.person_selected == null) {
					return;
				}

				final String status = (String) arg0.getData();

				UserDetailsComposer.this.person_selected.setStatus(status);

				UserDetailsComposer.this.personDao.updatePerson(UserDetailsComposer.this.person_selected);

				// set status
				UserDetailsComposer.this.user_status.setValue(status);

				// set user listbox
				UserDetailsComposer.this.setUserListBox();

			}
		});

	}

	@Listen("onClick = #qrcode_gen")
	public void generateQrCode() {

		if ((this.person_selected == null) || (this.person_selected.getId() == null)) {
			return;
		}

		final ByteArrayOutputStream stream = Utility.QRCodeGen("" + this.person_selected.getId());

		Filedownload.save(stream.toByteArray(), "application/image", "qrcode.png");

	}

	private Comboitem getComboItem(final Combobox combo, final String value) {
		Comboitem item = null;

		for (int i = 0; i < combo.getItems().size(); i++) {
			if (combo.getItems().get(i) != null) {
				item = combo.getItems().get(i);
				if (value.equals(item.getValue().toString())) {
					break;
				}
			}
		}
		return item;
	}

	@Listen("onSelect=#birth_province_user")
	public void loadComuni() {

		if ((this.birth_province_user.getSelectedItem() == null) || (this.birth_province_user.getSelectedItem().getValue() == null)) {
			return;
		}

		this.birth_place_user.setModel(new ListModelList<String>(this.personDao.loadComuniByProvincia(this.birth_province_user.getSelectedItem()
				.getValue().toString())));
	}

	@Listen("onClick = #modify_users_command")
	public void modifyCommand() {

		if (this.person_selected == null) {
			return;
		}

		this.person_selected.setFirstname(this.firstname_user.getValue());
		this.person_selected.setPersonal_code(this.personal_code_user.getValue());
		this.person_selected.setLastname(this.lastname_user.getValue());
		this.person_selected.setCity(this.city_user.getValue());
		this.person_selected.setAddress(this.address_user.getValue());
		this.person_selected.setPhone(this.phone_user.getValue());
		this.person_selected.setZip(this.postalCode_user.getValue());
		this.person_selected.setEnabled(this.user_enabled.isChecked());
		this.person_selected.setEmployee_identification(this.employee_identification_user.getValue());
		this.person_selected.setProvincia(this.provincia_user.getValue());
		this.person_selected.setFiscal_code(this.fiscalcode_user.getValue());
		this.person_selected.setBirth_date(this.birth_date_user.getValue());
		this.person_selected.setDailyemployee(this.dailyemployee.isChecked());
		this.person_selected.setPart_time(this.partTime.isChecked());

		if (this.birth_place_user.getSelectedItem() != null) {
			this.person_selected.setBirth_place(this.birth_place_user.getSelectedItem().getValue().toString());
		}

		if (this.birth_province_user.getSelectedItem() != null) {
			this.person_selected.setBirth_province(this.birth_province_user.getSelectedItem().getValue().toString());
		}

		if (this.sex_user.getSelectedItem() != null) {
			if (this.sex_user.getSelectedItem().getValue().toString().equals("M")) {
				this.person_selected.setSex(true);
			} else {
				this.person_selected.setSex(false);
			}
		}

		this.person_selected.setEducation(this.education_user.getValue());
		this.person_selected.setNcfl(this.ncfl_user.getValue());
		this.person_selected.setDepartment(this.department_user.getValue());
		this.person_selected.setCurrent_position(this.current_position_user.getValue());
		this.person_selected.setCountry(this.country_user.getValue());
		this.person_selected.setNbudge(this.nbudje_user.getValue());
		this.person_selected.setNpass(this.npass_user.getValue());
		this.person_selected.setMarital_status(this.marital_status_user.getValue());
		this.person_selected.setFamily_charge(this.family_charge_user.getValue());
		this.person_selected.setDriving_license(this.driving_license_user.getValue());
		this.person_selected.setDriving_license_emission(this.driving_license_emission_user.getValue());
		this.person_selected.setOut_schedule(this.out_schedule_user.isChecked());

		// set authority
		String auth = UserTag.ROLE_USER;
		if (this.admin_user.isChecked()) {
			auth = UserTag.ROLE_SUPERVISOR;
		}

		if (this.operative_user.isChecked()) {
			if (!auth.equals(UserTag.ROLE_USER)) {
				auth = auth + "," + UserTag.ROLE_OPERATIVE;
			} else {
				auth = UserTag.ROLE_OPERATIVE;
			}

		}

		if (this.backoffice_user.isChecked()) {
			if (!auth.equals(UserTag.ROLE_USER)) {
				auth = auth + "," + UserTag.ROLE_BACKOFFICE;
			} else {
				auth = UserTag.ROLE_BACKOFFICE;
			}

		}

		if (this.viewer_user.isChecked()) {
			if (!auth.equals(UserTag.ROLE_USER)) {
				auth = auth + "," + UserTag.ROLE_VIEWER;
			} else {
				auth = UserTag.ROLE_VIEWER;
			}
		}

		this.person_selected.setAuthority(auth);

		// update
		this.personDao.updatePerson(this.person_selected);

		// update list
		this.setUserListBox();

		final Map<String, String> params = new HashMap();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

		Messagebox.show("Dati utente aggiornati.", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

	}

	@Listen("onClick = #sw_refresh_list;")
	public void refreshListUser() {

		this.full_text_search.setValue(null);
		this.select_specific_user.setSelectedItem(null);

		// set user listbox
		this.setUserListBox();
	}

	@Listen("onClick = #sw_link_deleteuser")
	public void removeItem() {

		final Map<String, String> params = new HashMap();
		params.put("sclass", "mybutton Button");

		final Messagebox.Button[] buttons = new Messagebox.Button[2];
		buttons[0] = Messagebox.Button.OK;
		buttons[1] = Messagebox.Button.CANCEL;

		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.EXCLAMATION, null,
				new EventListener() {
					@Override
					public void onEvent(final Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							UserDetailsComposer.this.deleteUserCommand();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				}, params);

	}

	/**
	 * Reset data on user grid
	 */
	private void resetDataInfo() {
		this.email_user.setValue(null);
		this.email_user_retype.setValue(null);
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
		this.birth_place_user.setSelectedItem(null);
		this.birth_place_user.setModel(new ListModelList<String>());
		this.birth_place_user.setValue("");
		this.dailyemployee.setChecked(Boolean.FALSE);
		this.partTime.setChecked(Boolean.FALSE);
		this.birth_province_user.setSelectedItem(null);
		this.education_user.setValue("");
		this.country_user.setValue("");
		this.ncfl_user.setValue("");
		this.department_user.setValue("");
		this.current_position_user.setValue("");
		this.personal_code_user.setValue("");
		this.sex_user.setSelectedItem(null);
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
		this.out_schedule_user.setChecked(Boolean.FALSE);

		// set enable email (due becouse modify process)
		this.email_user.setDisabled(false);

		// enable command about credential
		this.row_email_user_retype.setVisible(true);
		this.row_password_user.setVisible(true);
		this.row_password_user_retype.setVisible(true);
	}

	private void selectAdmins() {

		final List<Person> list_person = this.personDao.usersAdmin();

		this.sw_list_user.setModel(new ListModelList<Person>(list_person));

	}

	private void selectDailyEmployee() {
		final List<Person> list_person = this.personDao.listDailyEmployee();

		this.sw_list_user.setModel(new ListModelList<Person>(list_person));
	}

	private void selectOperatives() {
		final List<Person> list_person = this.personDao.listOperativePerson();

		this.sw_list_user.setModel(new ListModelList<Person>(list_person));
	}

	private void selectOutScheduleEmployee() {
		final List<Person> list_person = this.personDao.listOutScheduleEmployee();

		this.sw_list_user.setModel(new ListModelList<Person>(list_person));

	}

	private void selectPartTimeEmployee() {
		final List<Person> list_person = this.personDao.listAllPartTime();

		this.sw_list_user.setModel(new ListModelList<Person>(list_person));
	}

	private void selectProgrammerEmployee() {
		final List<Person> list_person = this.personDao.listProgrammerEmployee();

		this.sw_list_user.setModel(new ListModelList<Person>(list_person));

	}

	@Listen("onChange=#select_specific_user")
	public void selectSpecificUser() {
		final String selected = this.select_specific_user.getSelectedItem().getValue().toString();
		if (selected.equals("Amministratori di Sistema")) {
			this.selectAdmins();
		} else if (selected.equals("Preposti")) {
			this.selectOperatives();
		} else if (selected.equals("dailyemployee")) {
			this.selectDailyEmployee();
		} else if (selected.equals("partTimeEmployee")) {
			this.selectPartTimeEmployee();
		} else if (selected.equals("outSchedule")) {
			this.selectOutScheduleEmployee();
		} else if (selected.equals("programmer")) {
			this.selectProgrammerEmployee();
		} else if (selected.equals("viewer")) {
			this.selectViewerEmployee();
		} else {
			this.refreshListUser();
		}
	}

	private void selectViewerEmployee() {
		final List<Person> list_person = this.personDao.listViewerEmployee();

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

		final Component comp_jc = Path.getComponent("//userjobcost/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_jc, person_selected);

	}

	/**
	 * Show users
	 */
	public void setInitialView() {

		this.full_text_search.setValue(null);

		this.select_specific_user.setSelectedItem(null);

		// set user listbox
		this.setUserListBox();

		this.birth_province_user.setModel(new ListModelList<String>(this.personDao.loadAllProvincia()));

		// initial view
		this.grid_user_details.setVisible(false);

	}

	/**
	 * Set user list box with initial events
	 */
	@Listen("onOK = #shows_rows, #full_text_search")
	public void setUserListBox() {

		this.select_specific_user.setSelectedItem(null);

		List<Person> list_person = null;

		if ((this.full_text_search.getValue() != null) && !this.full_text_search.getValue().equals("")) {
			list_person = this.personDao.listAllPersons(this.full_text_search.getValue());
		} else {
			list_person = this.personDao.listAllPersons();
		}

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_user.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_user.setPageSize(10);
		}

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
		this.userName.setVisible(false);
		this.jobcost_user_tab.setVisible(false);

		// generate qr code only in modify view
		this.qrcode_gen.setVisible(false);

		this.resetDataInfo();

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
		this.jobcost_user_tab.setVisible(true);

		// generate qr code only in modify view
		this.qrcode_gen.setVisible(true);

		// set detail to selection
		this.detail_user_tab.getTabbox().setSelectedTab(this.detail_user_tab);
	}
}
