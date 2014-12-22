package org.uario.seaworkengine.zkevent;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.utility.BeansTag;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

public class MyProfileComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Textbox				address_user;

	@Wire
	private Div					box_deleteuser;

	@Wire
	private Textbox				city_user;

	@Wire
	private Textbox				email_editor_user;

	@Wire
	private Textbox				email_editor_user_retype;

	@Wire
	private Textbox				email_user;

	@Wire
	private Textbox				email_user_retype;

	@Wire
	private Textbox				firstname_user;

	@Wire
	private Component			grid_user_details;

	@Wire
	private Textbox				lastname_user;

	private final Logger		logger				= Logger.getLogger(MyProfileComposer.class);

	@Wire
	private Textbox				mailpassword_user;

	@Wire
	private Div					panel_modify;

	@Wire
	private Textbox				password_editor_user;

	@Wire
	private Textbox				password_editor_user_retype;

	@Wire
	private Textbox				password_user;

	@Wire
	private Textbox				password_user_retype;

	// the dao used for db interaction
	private PersonDAO			personDao;

	@Wire
	private Textbox				phone_user;

	@Wire
	private Textbox				postalCode_user;

	@Wire
	private Row					row_email_user_retype;

	@Wire
	private Row					row_password_user;

	@Wire
	private Row					row_password_user_retype;

	@Listen("onClick = #modify_mail_user")
	public void changeMailActionUser(final Event evt) {

		try {

			final Person person_selected = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			// check over password
			final String hashing_password = Utility.encodeSHA256(this.mailpassword_user.getValue(), person_selected.getEmail());
			if (!hashing_password.equals(person_selected.getPassword())) {

				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("La password inserita non è corretta", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

				// set fields
				this.mailpassword_user.setValue("");

				return;

			}

			if (!this.email_editor_user.getValue().equals(this.email_editor_user_retype.getValue())) {

				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Le mail devono essere uguali", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

				// set fields
				this.mailpassword_user.setValue("");

				return;
			}

			// change password
			this.personDao.changeMail(person_selected.getId(), this.mailpassword_user.getValue(), this.email_editor_user.getValue());

			person_selected.setEmail(this.email_editor_user.getValue());

			// set fields
			this.mailpassword_user.setValue("");

			this.email_user.setValue(person_selected.getEmail());

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Mail Cambiata", "INFO", buttons, null, Messagebox.EXCLAMATION, null, null, params);

		}

		catch (final WrongValueException e) {

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Dati digitati non corretti", "INFO", buttons, null, Messagebox.ERROR, null, null, params);

		}

	}

	@Listen("onClick = #modify_mail_password")
	public void changePassword(final Event evt) {
		try {

			final Person person_selected = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			// WARNING
			if (!this.password_editor_user.getValue().equals(this.password_editor_user_retype.getValue())) {

				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Le Password devono essere uguali", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

				// Messagebox.show("La password inserita non è corretta",
				// "Attenzione", 0, null, null, null, null, params);

				return;
			}

			// change password
			this.personDao.changePassword(person_selected.getEmail(), this.password_editor_user.getValue());

			// reset fields
			this.password_editor_user.setValue("");
			this.password_editor_user_retype.setValue("");

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Password Aggiornata", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		}

		catch (final WrongValueException e) {

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Errore nell'inserimento dei valori", "ATTENZIONE", buttons, null, Messagebox.ERROR, null, null, params);

		}

	}

	/**
	 * Define modify view
	 */
	private void defineModifyView() {

		final Person person_selected = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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

		// set panel modify
		this.panel_modify.setVisible(true);

	}

	@Override
	public void doFinally() throws Exception {

		this.getSelf().addEventListener(ZkEventsTag.onShowUsers, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// get the person dao
				MyProfileComposer.this.personDao = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);

				MyProfileComposer.this.defineModifyView();

			}
		});

	}

	@Listen("onClick = #modify_users_command")
	public void modifyCommand() {

		final Person person_selected = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		person_selected.setFirstname(this.firstname_user.getValue());
		person_selected.setLastname(this.lastname_user.getValue());
		person_selected.setCity(this.city_user.getValue());
		person_selected.setAddress(this.address_user.getValue());
		person_selected.setPhone(this.phone_user.getValue());
		person_selected.setZip(this.postalCode_user.getValue());

		this.personDao.updatePerson(person_selected);

		final Map<String, String> params = new HashMap();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

		Messagebox.show("Dati utente aggiornati", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

	}

}
