package org.uario.seaworkengine.zkevent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.uario.seaworkengine.model.Contestation;
import org.uario.seaworkengine.model.Employment;
import org.uario.seaworkengine.model.JobCost;
import org.uario.seaworkengine.model.MedicalExamination;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.TfrUser;
import org.uario.seaworkengine.model.TradeUnion;
import org.uario.seaworkengine.model.TrainingCertificate;
import org.uario.seaworkengine.model.UserCompensation;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.EmploymentDAO;
import org.uario.seaworkengine.platform.persistence.dao.IContestation;
import org.uario.seaworkengine.platform.persistence.dao.IJobCost;
import org.uario.seaworkengine.platform.persistence.dao.MedicalExaminationDAO;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.platform.persistence.dao.TfrDAO;
import org.uario.seaworkengine.platform.persistence.dao.TradeUnionDAO;
import org.uario.seaworkengine.platform.persistence.dao.TrainingCertificateDAO;
import org.uario.seaworkengine.platform.persistence.dao.UserCompensationDAO;
import org.uario.seaworkengine.platform.persistence.dao.excpetions.UserNameJustPresentExcpetion;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.CFGenerator;
import org.uario.seaworkengine.utility.DepartmentTag;
import org.uario.seaworkengine.utility.TaskColor;
import org.uario.seaworkengine.utility.UserReport;
import org.uario.seaworkengine.utility.UserStatusTag;
import org.uario.seaworkengine.utility.UserTag;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.utility.UtilityCSV;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
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
	private Button				cfgenerator;

	@Wire
	private Textbox				city_user;

	@Wire
	private Component			compensation_user_tab;

	public Comboitem			comuneSelected;

	@Wire
	private Component			contestations_user_tab;

	@Wire
	private Combobox			contractual_level_filter;

	@Wire
	private Label				count_users;

	@Wire
	private Textbox				country_user;

	@Wire
	private Textbox				current_position_user;

	@Wire
	private Checkbox			dailyemployee;

	@Wire
	private Intbox				daywork_w_user;

	@Wire
	private Combobox			department_user;

	List<String>				departmentItems		= new ArrayList<>();

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
	private Intbox				hourswork_w_user;

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
	private Component			medicalexamination_user_tab;

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
	private Textbox				search_qualifica;

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
	private Component			training_user_tab;

	@Wire
	private Label				user_contractual_level;

	@Wire
	private Button				user_csv;

	@Wire
	private Combobox			user_enable_filter;

	@Wire
	private Checkbox			user_enabled;

	@Wire
	private Label				user_status;

	@Wire
	private Combobox			user_status_filter;

	@Wire
	private Combobox			user_status_filter_period;

	@Wire
	private Datebox				user_status_from;

	@Wire
	private Datebox				user_status_to;

	@Wire
	private Combobox			user_task_code;

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
			person.setEducation(this.education_user.getValue());
			person.setNcfl(this.ncfl_user.getValue());
			person.setDepartment("");
			if (this.department_user.getSelectedItem() != null) {
				person.setDepartment(this.department_user.getSelectedItem().getValue().toString());
			}
			person.setCurrent_position(this.current_position_user.getValue());
			person.setNbudge(this.nbudje_user.getValue());
			person.setNpass(this.npass_user.getValue());
			person.setMarital_status(this.marital_status_user.getValue());
			person.setFamily_charge(this.family_charge_user.getValue());
			person.setDriving_license(this.driving_license_user.getValue());
			person.setDriving_license_emission(this.driving_license_emission_user.getValue());
			person.setDaywork_w(this.daywork_w_user.getValue());
			person.setHourswork_w(this.hourswork_w_user.getValue());

			if (this.birth_place_user.getSelectedItem() != null) {
				person.setBirth_place(this.birth_place_user.getSelectedItem().getValue().toString());
			}
			if (this.birth_province_user.getSelectedItem() != null) {
				person.setBirth_province(this.birth_province_user.getSelectedItem().getValue().toString());
			}

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

				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("La password inserita non è corretta ", "INFO", buttons, null, Messagebox.ERROR, null, null, params);

				return;

			}

			if (!this.email_editor_user.getValue().equals(this.email_editor_user_retype.getValue())) {

				final Map<String, String> params = new HashMap<>();
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

				final Map<String, String> params = new HashMap<>();
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

			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Mail cambiata ", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		}

		catch (final WrongValueException e) {

			final Map<String, String> params = new HashMap<>();
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

				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Le password devono essere uguali", "INFO", buttons, null, Messagebox.EXCLAMATION, null, null, params);
				return;
			}

			// change password
			this.personDao.changePassword(this.person_selected.getId(), this.person_selected.getEmail(), this.password_editor_user.getValue());

			// reset fields
			this.password_editor_user.setValue("");
			this.password_editor_user_retype.setValue("");

			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Password Aggiornata ", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		}

		catch (final WrongValueException e) {

			final Map<String, String> params = new HashMap<>();
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

		this.education_user.setValue(person_selected.getEducation());
		this.ncfl_user.setValue(person_selected.getNcfl());

		final ListModel<String> items = this.department_user.getModel();
		for (int i = 0; i < items.getSize(); i++) {
			if (items.getElementAt(i).toString().equals(person_selected.getDepartment())) {
				this.department_user.setSelectedItem(this.department_user.getItemAtIndex(i));
			}
		}

		this.current_position_user.setValue(person_selected.getCurrent_position());

		this.nbudje_user.setValue(person_selected.getNbudge());
		this.npass_user.setValue(person_selected.getNpass());
		this.marital_status_user.setValue(person_selected.getMarital_status());
		this.family_charge_user.setValue(person_selected.getFamily_charge());
		this.driving_license_user.setValue(person_selected.getDriving_license());
		this.driving_license_emission_user.setValue(person_selected.getDriving_license_emission());

		// set status
		this.user_status.setValue(person_selected.getStatus());

		// set contractual level
		if (person_selected.getContractual_level() != null) {
			this.user_contractual_level.setValue(person_selected.getContractual_level().toString());
		}

		// set users
		this.admin_user.setChecked(person_selected.isAdministrator());
		this.out_schedule_user.setChecked(person_selected.getOut_schedule());
		this.viewer_user.setChecked(person_selected.isViewer());
		this.backoffice_user.setChecked(person_selected.isBackoffice());
		this.operative_user.setChecked(person_selected.isOperative());

		// set info about time workable
		this.daywork_w_user.setValue(person_selected.getDaywork_w());
		this.hourswork_w_user.setValue(person_selected.getHourswork_w());

		if (person_selected.getSex()) {
			this.sex_user.setSelectedIndex(0);
		} else {
			this.sex_user.setSelectedIndex(1);
		}

		if (person_selected.getBirth_province() != null) {

			Boolean checked = false;

			final List<Comboitem> listItem = this.birth_province_user.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof String) {
					if (item.getValue().toString().equals(person_selected.getBirth_province())) {
						this.birth_province_user.setSelectedItem(item);
						checked = true;
						break;
					}
				}
			}
			if (!checked) {
				this.birth_province_user.setSelectedItem(null);
				this.birth_province_user.setValue(person_selected.getBirth_province());
			}

		}

		if (person_selected.getBirth_place() != null) {

			this.birth_place_user.setModel(new ListModelList<>(this.personDao.loadComuniByProvincia(person_selected.getBirth_province())));
			this.birth_place_user.setSelectedItem(this.getComboItem(this.birth_place_user, person_selected.getBirth_place()));
			this.birth_place_user.setValue(person_selected.getBirth_place());

		}

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

			Messagebox.show("Non è possibile eliminare questo utente.\nControlla che non ci siano azioni legate a questa angrafica.", "INFO", buttons,
					null, Messagebox.EXCLAMATION, null, null, params);

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

				final Employment item = (Employment) arg0.getData();

				UserDetailsComposer.this.person_selected.setStatus(item.getStatus());
				UserDetailsComposer.this.person_selected.setContractual_level(item.getContractual_level());

				UserDetailsComposer.this.personDao.updatePerson(UserDetailsComposer.this.person_selected);

				// set status
				UserDetailsComposer.this.user_status.setValue(item.getStatus());

				if (item.getContractual_level() != null) {
					// set contratctual level
					UserDetailsComposer.this.user_contractual_level.setValue(item.getContractual_level().toString());
				}

				// set user listbox
				UserDetailsComposer.this.setUserListBox();

			}
		});

		// create comboitem department
		UserDetailsComposer.this.departmentItems.add(DepartmentTag.ADMINISTRATION);
		UserDetailsComposer.this.departmentItems.add(DepartmentTag.OPERATIVE);
		UserDetailsComposer.this.department_user.setModel(new ListModelList<>(UserDetailsComposer.this.departmentItems));

		final List<String> userStatusItems = new ArrayList<>();

		// create comboitem user status
		userStatusItems.add(UserStatusTag.OPEN);
		userStatusItems.add(UserStatusTag.SUSPENDED);
		userStatusItems.add(UserStatusTag.FIRED);
		userStatusItems.add(UserStatusTag.RESIGNED);

		this.user_status_filter.setModel(new ListModelList<>(userStatusItems));
		this.user_status_filter_period.setModel(new ListModelList<>(userStatusItems));

	}

	@Listen("onClick = #person_review_list_download")
	public void downloadReviewUserCSV() {

		// TAKE DAO
		final EmploymentDAO employment_dao = (EmploymentDAO) SpringUtil.getBean(BeansTag.EMPLOYMENT_DAO);
		final TasksDAO task_dao = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);
		final TradeUnionDAO trade_union_dao = (TradeUnionDAO) SpringUtil.getBean(BeansTag.TRADE_UNION_DAO);
		final IContestation contestation_dao = (IContestation) SpringUtil.getBean(BeansTag.CONTESTATION_DAO);
		final TrainingCertificateDAO training_dao = (TrainingCertificateDAO) SpringUtil.getBean(BeansTag.TRAINING_CERTIFICATE_DAO);

		// take info
		final List<Person> list_users = this.personDao.listAllPersons();

		// take final builder
		final StringBuilder task = new StringBuilder();
		final StringBuilder employ = new StringBuilder();
		final StringBuilder trade = new StringBuilder();
		final StringBuilder contestazioni = new StringBuilder();
		final StringBuilder formazione = new StringBuilder();

		boolean add_header = true;

		for (final Person person : list_users) {

			final Integer id_user = person.getId();

			// get info

			final List<Employment> list_emply = employment_dao.loadEmploymentByUser(id_user);
			final List<UserTask> list_task = task_dao.loadTasksByUser(id_user);

			final List<TradeUnion> list_trade = trade_union_dao.loadTradeUnionsByUser(id_user);
			final List<Contestation> list_contestation = contestation_dao.loadUserContestation(id_user);
			final List<TrainingCertificate> list_training = training_dao.loadTrainingCertificate(null, null, null, id_user);

			// download total
			final StringBuilder builder = UtilityCSV.downloadCSV_user_task(person, list_task, add_header);
			final StringBuilder builder_1 = UtilityCSV.downloadCSV_user_raportolavorativo(person, list_emply, add_header);
			final StringBuilder builder_2 = UtilityCSV.downloadCSV_user_tradeunion(person, list_trade, add_header);
			final StringBuilder builder_3 = UtilityCSV.downloadCSV_user_contestazioni(person, list_contestation, add_header);
			final StringBuilder builder_4 = UtilityCSV.downloadCSV_user_formazione(person, list_training, add_header);

			add_header = false;

			// append info
			task.append(builder.toString());
			employ.append(builder_1.toString());
			trade.append(builder_2.toString());
			contestazioni.append(builder_3.toString());
			formazione.append(builder_4.toString());

		}

		FileOutputStream f_task = null;
		FileOutputStream f_emply = null;
		FileOutputStream f_trade = null;
		FileOutputStream f_contestazioni = null;
		FileOutputStream f_formazione = null;

		try {

			final File download = Files.createTempFile("data", "").toFile();

			final File task_file = Files.createTempFile("mansioni", ".csv").toFile();
			final File emplyment_file = Files.createTempFile("livelli_contratuali", ".csv").toFile();
			final File tradeunion_file = Files.createTempFile("sindacati", ".csv").toFile();
			final File contestazioni_file = Files.createTempFile("contestazioni", ".csv").toFile();
			final File formazione_file = Files.createTempFile("formazione", ".csv").toFile();

			final Collection<File> filesToArchive = new ArrayList<>();
			filesToArchive.add(task_file);
			filesToArchive.add(emplyment_file);
			filesToArchive.add(tradeunion_file);
			filesToArchive.add(contestazioni_file);
			filesToArchive.add(formazione_file);

			f_task = new FileOutputStream(task_file);
			f_emply = new FileOutputStream(emplyment_file);
			f_trade = new FileOutputStream(tradeunion_file);
			f_contestazioni = new FileOutputStream(contestazioni_file);
			f_formazione = new FileOutputStream(formazione_file);

			org.apache.commons.io.IOUtils.write(task.toString(), f_task, "UTF-8");
			org.apache.commons.io.IOUtils.write(employ.toString(), f_emply, "UTF-8");
			org.apache.commons.io.IOUtils.write(trade.toString(), f_trade, "UTF-8");
			org.apache.commons.io.IOUtils.write(contestazioni.toString(), f_contestazioni, "UTF-8");
			org.apache.commons.io.IOUtils.write(formazione.toString(), f_formazione, "UTF-8");

			Filedownload.save(download, "application/zip");

			try (final ArchiveOutputStream o = new ZipArchiveOutputStream(download)) {
				for (final File f : filesToArchive) {
					// maybe skip directories for formats like AR that don't store directories
					final ArchiveEntry entry = o.createArchiveEntry(f, f.getName());
					// potentially add more flags to entry
					o.putArchiveEntry(entry);
					if (f.isFile()) {
						try (InputStream i = Files.newInputStream(f.toPath())) {
							IOUtils.copy(i, o);
						}
					}
					o.closeArchiveEntry();
				}
				o.finish();
			}
		} catch (final FileNotFoundException e) {
			this.logger.error("Error in create data archive. " + e.getMessage());
		} catch (final IOException e) {
			this.logger.error("Error in create data archive. " + e.getMessage());
		} finally {

			if (f_task != null) {
				try {
					f_task.close();
				} catch (final IOException e) {

				}
			}

			if (f_emply != null) {
				try {
					f_emply.close();
				} catch (final IOException e) {

				}
			}

			if (f_trade != null) {
				try {
					f_trade.close();
				} catch (final IOException e) {

				}
			}

			if (f_contestazioni != null) {
				try {
					f_contestazioni.close();
				} catch (final IOException e) {

				}
			}

			if (f_formazione != null) {
				try {
					f_formazione.close();
				} catch (final IOException e) {

				}
			}

		}

	}

	/**
	 * Download total csv
	 */
	/**
	 *
	 */
	/**
	 *
	 */
	@Listen("onClick = #user_csv")
	public void downloadTotalInfoUserCSV() {

		if (this.person_selected == null) {
			return;
		}

		final Integer id_user = this.person_selected.getId();

		// TAKE DAO
		final EmploymentDAO employment_dao = (EmploymentDAO) SpringUtil.getBean(BeansTag.EMPLOYMENT_DAO);
		final TasksDAO task_dao = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);
		final IJobCost job_cost = (IJobCost) SpringUtil.getBean(BeansTag.JOB_COST_DAO);
		final TfrDAO tfr_dao = (TfrDAO) SpringUtil.getBean(BeansTag.TFR_DAO);
		final MedicalExaminationDAO medical_ex_dao = (MedicalExaminationDAO) SpringUtil.getBean(BeansTag.MEDICAL_EXAMINATION_DAO);
		final TradeUnionDAO trade_union_dao = (TradeUnionDAO) SpringUtil.getBean(BeansTag.TRADE_UNION_DAO);
		final IContestation contestation_dao = (IContestation) SpringUtil.getBean(BeansTag.CONTESTATION_DAO);
		final UserCompensationDAO compensation_dao = (UserCompensationDAO) SpringUtil.getBean(BeansTag.USER_COMPENSATION_DAO);
		final TrainingCertificateDAO training_dao = (TrainingCertificateDAO) SpringUtil.getBean(BeansTag.TRAINING_CERTIFICATE_DAO);

		// get info
		final Person person_info = this.personDao.loadPerson(id_user);
		final List<Employment> list_emply = employment_dao.loadEmploymentByUser(id_user);
		final List<UserTask> list_task = task_dao.loadTasksByUser(id_user);
		final List<JobCost> list_job_cost = job_cost.loadJobCostByUser(id_user);
		final List<TfrUser> list_tfr = tfr_dao.loadTFRByUser(id_user);
		final List<MedicalExamination> list_medical = medical_ex_dao.loadMedicalExaminationByUserId(id_user);
		final List<TradeUnion> list_trade = trade_union_dao.loadTradeUnionsByUser(id_user);
		final List<Contestation> list_contestation = contestation_dao.loadUserContestation(id_user);
		final List<UserCompensation> list_compensation = compensation_dao.loadAllUserCompensationByUserId(id_user);
		final List<TrainingCertificate> list_training = training_dao.loadTrainingCertificate(id_user, null, null, null);

		// download total
		final StringBuilder builder = UtilityCSV.downloadCSV_UserTotal(person_info, list_emply, list_task, list_job_cost, list_tfr, list_medical,
				list_trade, list_contestation, list_compensation, list_training);

		Filedownload.save(builder.toString(), "application/text", "user_total.csv");

	}

	@Listen("onClick = #personList_download")
	public void downloadUserCSV() {

		final List<UserReport> list_users = this.personDao.listUserReport();

		final StringBuilder builder = UtilityCSV.downloadCSV_UserReport(list_users);

		Filedownload.save(builder.toString(), "application/text", "lista_utenti.csv");

	}

	@Listen("onClick = #user_info_csv")
	public void downloadUserInfoCSV() {

		if ((this.person_selected == null) || (this.person_selected.getId() == null)) {
			return;
		}

		final StringBuilder builder = UtilityCSV.downloadCSV_userinfo(this.person_selected);

		Filedownload.save(builder.toString(), "application/text", "info_utente.csv");

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

	public Textbox getSearch_qualifica() {
		return this.search_qualifica;
	}

	public Combobox getUser_status_filter_period() {
		return this.user_status_filter_period;
	}

	@Listen("onSelect=#birth_province_user")
	public void loadComuni() {

		if ((this.birth_province_user.getSelectedItem() == null) || (this.birth_province_user.getSelectedItem().getValue() == null)) {
			return;
		}

		this.birth_place_user.setModel(
				new ListModelList<>(this.personDao.loadComuniByProvincia(this.birth_province_user.getSelectedItem().getValue().toString())));
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
		this.person_selected.setEducation(this.education_user.getValue());
		this.person_selected.setNcfl(this.ncfl_user.getValue());
		if (this.department_user.getSelectedItem() != null) {
			this.person_selected.setDepartment(this.department_user.getSelectedItem().getValue().toString());
		}
		this.person_selected.setCurrent_position(this.current_position_user.getValue());
		this.person_selected.setCountry(this.country_user.getValue());
		this.person_selected.setNbudge(this.nbudje_user.getValue());
		this.person_selected.setNpass(this.npass_user.getValue());
		this.person_selected.setMarital_status(this.marital_status_user.getValue());
		this.person_selected.setFamily_charge(this.family_charge_user.getValue());
		this.person_selected.setDriving_license(this.driving_license_user.getValue());
		this.person_selected.setDriving_license_emission(this.driving_license_emission_user.getValue());
		this.person_selected.setOut_schedule(this.out_schedule_user.isChecked());
		this.person_selected.setDaywork_w(this.daywork_w_user.getValue());
		this.person_selected.setHourswork_w(this.hourswork_w_user.getValue());

		String birthPlaceUser = "";
		if (this.birth_place_user.getSelectedItem() != null) {
			birthPlaceUser = this.birth_place_user.getSelectedItem().getValue().toString();
		} else if (this.birth_place_user.getValue() != null) {
			birthPlaceUser = this.birth_place_user.getValue().toString();
		}

		this.person_selected.setBirth_place(birthPlaceUser);

		String birthProvinceUser = "";

		if (this.birth_province_user.getSelectedItem() != null) {
			birthProvinceUser = this.birth_province_user.getSelectedItem().getValue().toString();
		} else if (this.birth_province_user.getValue() != null) {
			birthProvinceUser = this.birth_province_user.getValue().toString();
		}
		this.person_selected.setBirth_province(birthProvinceUser);

		if (this.sex_user.getSelectedItem() != null) {
			if (this.sex_user.getSelectedItem().getValue().toString().equals("M")) {
				this.person_selected.setSex(true);
			} else {
				this.person_selected.setSex(false);
			}
		}

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
		this.user_status_filter.setSelectedItem(null);
		this.contractual_level_filter.setSelectedItem(null);
		this.user_enable_filter.setSelectedItem(null);
		this.search_qualifica.setValue(null);
		this.user_task_code.setValue(null);

		this.user_status_filter_period.setValue(null);
		this.user_status_from.setValue(null);
		this.user_status_to.setValue(null);

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
		this.department_user.setSelectedItem(null);
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

		// set enable email (due because modify process)
		this.email_user.setDisabled(false);

		// enable command about credential
		this.row_email_user_retype.setVisible(true);
		this.row_password_user.setVisible(true);
		this.row_password_user_retype.setVisible(true);

		this.daywork_w_user.setValue(null);
		this.hourswork_w_user.setValue(null);
	}

	@Listen("onChange = #user_task_code")
	public void searchByUserTask() {
		if (this.user_task_code.getSelectedItem() == null) {
			return;
		}

		final UserTask item = this.user_task_code.getSelectedItem().getValue();

		final List<Person> list = this.personDao.listAllPersonByUserTask(item.getId());
		final ListModelList<Person> list_person = new ListModelList<>(list);

		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());

		this.full_text_search.setValue(null);
		this.select_specific_user.setSelectedItem(null);

	}

	@Listen("onOK = #search_qualifica")
	public void searchCurrentPosition() {

		final String item = this.search_qualifica.getValue();
		if ((item == null) || item.isEmpty()) {
			this.search_qualifica.setValue(null);
			this.setUserListBox();
		}

		final List<Person> list = this.personDao.listAllPersonByCurrentPosition(item);
		final ListModelList<Person> list_person = new ListModelList<>(list);
		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());

		this.full_text_search.setValue(null);
		this.select_specific_user.setSelectedItem(null);

	}

	@Listen("onChange = #user_status_filter")
	public void searchUserStatus() {
		if (this.user_status_filter.getSelectedItem() == null) {
			return;
		}

		final String item = this.user_status_filter.getSelectedItem().getValue().toString();

		final List<Person> listAllPersonByUserStatus = this.personDao.listAllPersonByUserStatus(item);
		final ListModelList<Person> list_person = new ListModelList<>(listAllPersonByUserStatus);

		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());

		this.full_text_search.setValue(null);
		this.select_specific_user.setSelectedItem(null);

	}

	@Listen("onChange = #user_status_filter_period, #user_status_from, #user_status_to;onOK = #user_status_filter_period, #user_status_from, #user_status_to")
	public void searchUserStatusPeriod() {
		if (this.user_status_filter_period.getSelectedItem() == null) {
			return;
		}

		final String item = this.user_status_filter_period.getSelectedItem().getValue().toString();

		final Date from = this.user_status_from.getValue();
		final Date to = this.user_status_to.getValue();
		final List<Person> listAllPersonByUserStatus = this.personDao.listAllPersonByUserStatus(item, from, to);
		final ListModelList<Person> list_person = new ListModelList<>(listAllPersonByUserStatus);

		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());
		this.full_text_search.setValue(null);
		this.select_specific_user.setSelectedItem(null);

	}

	private void selectAdmins() {

		final List<Person> list_person = this.personDao.usersAdmin();

		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());

	}

	@Listen("onChange=#contractual_level_filter")
	public void selectByContractLevel() {
		this.select_specific_user.setSelectedItem(null);
		this.user_status_filter.setSelectedItem(null);
		if (this.contractual_level_filter.getSelectedItem() != null) {

			final int itm_search = Integer.parseInt((String) this.contractual_level_filter.getSelectedItem().getValue());
			final List<Person> list_by_contract = this.personDao.listAllPersonByContractualLevel(itm_search);

			final ListModelList<Person> list_person = new ListModelList<>(list_by_contract);
			this.sw_list_user.setModel(new ListModelList<>(list_person));

			this.count_users.setValue("" + list_person.size());

			this.full_text_search.setValue(null);
			this.select_specific_user.setSelectedItem(null);
		}
	}

	@Listen("onChange=#user_enable_filter")
	public void selectByUserEnable() {

		this.select_specific_user.setSelectedItem(null);
		this.user_status_filter.setSelectedItem(null);

		if (this.user_enable_filter.getSelectedItem() == null) {

			return;

		}

		Boolean enable = Boolean.TRUE;
		final String value = this.user_enable_filter.getSelectedItem().getValue();
		if (value.equals("NON ATTIVO")) {
			enable = Boolean.FALSE;
		}

		final List<Person> list = this.personDao.listAllPersonByEnable(enable);

		final ListModelList<Person> list_person = new ListModelList<>(list);

		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());

		this.full_text_search.setValue(null);
		this.select_specific_user.setSelectedItem(null);

	}

	private void selectDailyEmployee() {
		final List<Person> list_person = this.personDao.listDailyEmployee();

		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());
	}

	private void selectOperatives() {
		final List<Person> list_person = this.personDao.listOperativePerson();

		this.sw_list_user.setModel(new ListModelList<>(list_person));
	}

	private void selectOutScheduleEmployee() {
		final List<Person> list_person = this.personDao.listOutScheduleEmployee();

		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());

	}

	private void selectPartTimeEmployee() {
		final List<Person> list_person = this.personDao.listAllPartTime();

		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());
	}

	private void selectProgrammerEmployee() {
		final List<Person> list_person = this.personDao.listProgrammerEmployee();

		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());

	}

	@Listen("onChange=#select_specific_user")
	public void selectSpecificUser() {
		this.full_text_search.setValue(null);
		this.user_status_filter.setSelectedItem(null);
		this.contractual_level_filter.setSelectedItem(null);
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

		this.sw_list_user.setModel(new ListModelList<>(list_person));
		this.count_users.setValue("" + list_person.size());

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

		// send event to show user fc
		final Component comp_medicalEx = Path.getComponent("//usermedicalexamination/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_medicalEx, person_selected);

		// send event to show user td
		final Component comp_td = Path.getComponent("//usertd/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_td, person_selected);

		// send event to show user status
		final Component comp_status = Path.getComponent("//userstatus/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_status, person_selected);

		// send event to show contestations
		final Component comp_cons = Path.getComponent("//cons/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_cons, person_selected);

		// send event to show job cost
		final Component comp_jc = Path.getComponent("//userjobcost/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_jc, person_selected);

		// send event to show job cost
		final Component comp_uc = Path.getComponent("//usercompensation/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_uc, person_selected);

		// send event to show user fc
		final Component comp_training = Path.getComponent("//usertrainingcertificate/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_training, person_selected);

	}

	/**
	 * Show users
	 */
	public void setInitialView() {

		this.full_text_search.setValue(null);

		this.select_specific_user.setSelectedItem(null);

		// select combo task
		final ConfigurationDAO configurationDAO = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
		final List<UserTask> list_task_user = configurationDAO.listAllStandardTask();
		final List<UserTask> list_task_absence = configurationDAO.listAllAbsenceTask();
		final List<UserTask> list_task_justificatory = configurationDAO.listAllJustificatoryTask();

		final List<UserTask> list = new ArrayList<>();
		list.addAll(list_task_user);
		list.addAll(list_task_justificatory);
		list.addAll(list_task_absence);

		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());
			if (task_item.getIsabsence()) {
				combo_item.setStyle("color: " + TaskColor.ANBSENCE_COLOR);
			} else if (task_item.getJustificatory()) {
				combo_item.setStyle("color: " + TaskColor.JUSTIFICATORY_COLOR);
			}
			this.user_task_code.appendChild(combo_item);

		}

		// set user listbox
		this.setUserListBox();

		final List<String> loadAllProvincia = this.personDao.loadAllProvincia();
		this.birth_province_user.setModel(new ListModelList<>(loadAllProvincia));

		// initial view
		this.grid_user_details.setVisible(false);

	}

	public void setSearch_qualifica(final Textbox search_qualifica) {
		this.search_qualifica = search_qualifica;
	}

	public void setUser_status_filter_period(final Combobox user_status_filter_period) {
		this.user_status_filter_period = user_status_filter_period;
	}

	/**
	 * Set user list box with initial events
	 */
	@Listen("onOK = #shows_rows, #full_text_search")
	public void setUserListBox() {

		this.select_specific_user.setSelectedItem(null);
		this.user_status_filter.setSelectedItem(null);
		this.contractual_level_filter.setSelectedItem(null);

		List<Person> personList = null;

		if ((this.full_text_search.getValue() != null) && !this.full_text_search.getValue().equals("")) {
			personList = this.personDao.listAllPersons(this.full_text_search.getValue());
		} else {
			personList = this.personDao.listAllPersons();
		}

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_user.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_user.setPageSize(10);
		}

		this.sw_list_user.setModel(new ListModelList<>(personList));
		this.count_users.setValue("" + personList.size());

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
		this.medicalexamination_user_tab.setVisible(false);
		this.tradeunion_user_tab.setVisible(false);
		this.contestations_user_tab.setVisible(false);
		this.compensation_user_tab.setVisible(false);
		this.userName.setVisible(false);
		this.jobcost_user_tab.setVisible(false);
		this.training_user_tab.setVisible(false);

		// generate qr code and total csv only in modify view
		this.qrcode_gen.setVisible(false);
		this.user_csv.setVisible(false);

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
		this.compensation_user_tab.setVisible(true);
		this.jobcost_user_tab.setVisible(true);
		this.medicalexamination_user_tab.setVisible(true);
		this.training_user_tab.setVisible(true);

		// generate qr code user csv only in modify view
		this.qrcode_gen.setVisible(true);
		this.user_csv.setVisible(true);

		// set detail to selection
		this.detail_user_tab.getTabbox().setSelectedTab(this.detail_user_tab);
	}
}
