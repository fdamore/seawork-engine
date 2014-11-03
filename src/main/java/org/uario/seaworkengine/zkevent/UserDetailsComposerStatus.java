package org.uario.seaworkengine.zkevent;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Employment;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.EmploymentDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.uario.seaworkengine.zkevent.UserDetailsComposerCons.ContestationMessage;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class UserDetailsComposerStatus extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private ConfigurationDAO	configurationDao;

	@Wire
	private Datebox				date_modifiled;

	// DAO for access employment data
	private EmploymentDAO		employmentDao;

	@Wire
	private Component			grid_details;

	private final Logger		logger				= Logger.getLogger(UserDetailsComposerStatus.class);

	@Wire
	private Textbox				note;

	private Person				person_selected;

	@Wire
	private Combobox			status;

	// status ADD or MODIFY
	private boolean				status_add			= false;

	private String				status_upload		= "";

	@Wire
	private Listbox				sw_list;

	@Listen("onClick = #sw_add")
	public void addItem() {

		this.status_add = true;
		this.date_modifiled.setValue(Calendar.getInstance().getTime());
		this.note.setValue("");
		this.status.setValue(null);
		this.setStatusComboBox();

	}

	private void deleteItemToUser() {

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final Employment item = this.sw_list.getSelectedItem().getValue();

		this.employmentDao.removeEmployment(item.getId());

		Messagebox.show("Informazione rimossa", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		// Refresh list task
		this.setInitialView();

	}

	@Override
	public void doFinally() throws Exception {

		this.getSelf().addEventListener(ZkEventsTag.onShowUsers, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// get selected person
				if ((arg0.getData() == null) || !(arg0.getData() instanceof Person)) {
					return;
				}
				UserDetailsComposerStatus.this.person_selected = (Person) arg0.getData();

				// get DAO
				UserDetailsComposerStatus.this.employmentDao = (EmploymentDAO) SpringUtil.getBean(BeansTag.EMPLOYMENT_DAO);
				UserDetailsComposerStatus.this.configurationDao = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);

				UserDetailsComposerStatus.this.setInitialView();

			}
		});

		this.getSelf().addEventListener(ZkEventsTag.onUpdateGeneralDetails, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				if (UserDetailsComposerStatus.this.person_selected == null) {
					return;
				}

				final ContestationMessage data = (ContestationMessage) arg0.getData();

				if (data == null) {
					return;
				}

				final Employment item = new Employment();

				// setup item with values
				item.setNote(null);
				item.setStatus(data.getStatus());
				item.setDate_modified(data.getDate_modified());
				item.setId_user(UserDetailsComposerStatus.this.person_selected.getId());

				UserDetailsComposerStatus.this.employmentDao.createEmploymentForUser(UserDetailsComposerStatus.this.person_selected.getId(), item);

				UserDetailsComposerStatus.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #sw_link_edit")
	public void modifyItem() {

		this.status_add = false;

		this.setStatusComboBox();

		// get selected item
		final Employment item = this.sw_list.getSelectedItem().getValue();

		// set info about grid
		if (this.person_selected == null) {
			return;
		}

		if (item == null) {
			return;
		}

		this.note.setValue(item.getNote());

		// set status
		final String status_info = item.getStatus();
		final List<Comboitem> lists = this.status.getItems();
		for (final Comboitem item_combo : lists) {
			if (item_combo.getValue().equals(status_info)) {
				this.status.setSelectedItem(item_combo);
				break;
			}
		}

		this.date_modifiled.setValue(item.getDate_modified());

	}

	@Listen("onClick = #ok_command")
	public void okCommand() {

		if (this.person_selected == null) {
			return;
		}

		Employment item = null;

		if (this.status_add) {

			// add a status

			item = new Employment();

			// setup item with values
			if (!this.setupItemWithValues(item)) {
				return;
			}

			this.employmentDao.createEmploymentForUser(this.person_selected.getId(), item);

			Messagebox.show("Mansione aggiunta all'utente", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		} else {

			// modify a status

			// get selected item
			item = this.sw_list.getSelectedItem().getValue();
			if (item == null) {
				return;
			}

			// add values to the items
			if (!this.setupItemWithValues(item)) {
				return;
			}

			this.employmentDao.updateEmployment(item);
		}

		// ask user for update current status
		Date to_day = Calendar.getInstance().getTime();
		to_day = DateUtils.truncate(to_day, Calendar.DATE);
		final Date my_date = DateUtils.truncate(item.getDate_modified(), Calendar.DATE);

		if ((item != null) && (my_date.compareTo(to_day) >= 0)) {

			Messagebox.show("Vuoi cambiare lo status attuale?", "CONFERMA STATUS ATTUALE", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
					new org.zkoss.zk.ui.event.EventListener() {
				@Override
				public void onEvent(final Event e) {
					if (Messagebox.ON_OK.equals(e.getName())) {
						UserDetailsComposerStatus.this.onUpdateStatus();
					} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
						// Cancel is clicked
					}
				}
			});

			this.status_upload = item.getStatus();

		}

		// Refresh list task
		this.setInitialView();

	}

	private void onUpdateStatus() {

		if (this.status_upload == null) {
			return;
		}

		// send event to show user task
		final Component comp = Path.getComponent("//user/page_user_detail");
		Events.sendEvent(ZkEventsTag.onUpdateGeneralDetails, comp, this.status_upload);
	}

	@Listen("onClick = #sw_link_delete")
	public void removeItem() {
		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener() {
					@Override
					public void onEvent(final Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							UserDetailsComposerStatus.this.deleteItemToUser();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});

	}

	@Listen("onClick = #sw_link_set")
	public void setCurrentStatus() {

		final Employment item = this.sw_list.getSelectedItem().getValue();

		// send event to show user task
		final Component comp = Path.getComponent("//user/page_user_detail");
		Events.sendEvent(ZkEventsTag.onUpdateGeneralDetails, comp, item.getStatus());

		Messagebox.show("Status Utente Assegnato", "Info", Messagebox.OK, Messagebox.INFORMATION);
	}

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		final List<Employment> list = this.employmentDao.loadEmploymentByUser(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<Employment>(list));

		this.grid_details.setVisible(false);
	}

	private void setStatusComboBox() {
		final List<String> list = this.configurationDao.selectAllStatus();
		this.status.setModel(new ListModelList<String>(list));
	}

	private Boolean setupItemWithValues(final Employment item) {

		item.setNote(this.note.getValue());

		// set status
		if (this.status.getSelectedItem() != null) {
			final String status_val = this.status.getSelectedItem().getValue();
			item.setStatus(status_val);
		} else {
			Messagebox.show("Selezionare Status Utente!", "Info", Messagebox.OK, Messagebox.INFORMATION);
			return false;
		}

		if (this.date_modifiled.getValue() == null) {
			Messagebox.show("Selezionare una data!", "Info", Messagebox.OK, Messagebox.INFORMATION);
			return false;
		}

		item.setDate_modified(this.date_modifiled.getValue());
		return true;
	}

}
