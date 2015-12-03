package org.uario.seaworkengine.zkevent;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
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
	private Combobox			contractual_level;

	@Wire
	private Datebox				date_modifiled;

	// DAO for access employment data
	private EmploymentDAO		employmentDao;

	@Wire
	private Component			grid_details;

	@Wire
	private Textbox				note;

	private Person				person_selected;

	@Wire
	private Combobox			status;

	// status ADD or MODIFY
	private boolean				status_add			= false;

	private String				status_upload		= "";

	private List<String>		statusList;

	@Wire
	private Listbox				sw_list;

	@Listen("onClick = #sw_add")
	public void addItem() {

		this.status_add = true;
		this.date_modifiled.setValue(Calendar.getInstance().getTime());
		this.note.setValue("");
		this.status.setValue(null);
		this.contractual_level.setValue(null);

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

		final Map<String, String> params = new HashMap<String, String>();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

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

				UserDetailsComposerStatus.this.setStatusComboBox();

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
		int i = 0;
		for (i = 0; i < this.statusList.size(); i++) {
			if (this.statusList.get(i).equals(status_info)) {
				this.status.setSelectedIndex(i);
				break;

			}
		}

		this.date_modifiled.setValue(item.getDate_modified());

		if (item.getContractual_level() != null) {
			this.contractual_level.setSelectedIndex(item.getContractual_level() - 1);
		}

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
			item.setId_user(this.person_selected.getId());

			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Status aggiunto all'utente", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

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
			item.setId_user(this.person_selected.getId());
		}

		// ask user for update current status
		Date to_day = Calendar.getInstance().getTime();
		to_day = DateUtils.truncate(to_day, Calendar.DATE);
		final Date my_date = DateUtils.truncate(item.getDate_modified(), Calendar.DATE);

		if ((item != null) && (my_date.compareTo(to_day) >= 0)) {

			// send event to show user task
			final Component comp = Path.getComponent("//user/page_user_detail");

			if (this.status_upload == null) {
				Events.sendEvent(ZkEventsTag.onUpdateGeneralDetails, comp, item);
			}

			this.status_upload = item.getStatus();

		}

		// Refresh list task
		this.setInitialView();

	}

	@Listen("onClick = #sw_link_delete")
	public void removeItem() {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("sclass", "mybutton Button");

		final Messagebox.Button[] buttons = new Messagebox.Button[2];
		buttons[0] = Messagebox.Button.OK;
		buttons[1] = Messagebox.Button.CANCEL;

		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.EXCLAMATION, null,
		        new EventListener() {
			        @Override
			        public void onEvent(final Event e) {
				        if (Messagebox.ON_OK.equals(e.getName())) {
					        UserDetailsComposerStatus.this.deleteItemToUser();
				        } else if (Messagebox.ON_CANCEL.equals(e.getName())) {
					        // Cancel is clicked
				        }
			        }
		        }, params);

	}

	@Listen("onClick = #sw_link_set")
	public void setCurrentStatus() {

		final Employment item = this.sw_list.getSelectedItem().getValue();

		// send event to show user task
		final Component comp = Path.getComponent("//user/page_user_detail");

		Events.sendEvent(ZkEventsTag.onUpdateGeneralDetails, comp, item);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

		Messagebox.show("Status utente assegnato", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

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

		if (this.statusList != null) {
			this.statusList.clear();

		}

		this.status.getItems().clear();

		this.statusList = this.configurationDao.selectAllStatus();

		for (int i = 0; i < this.statusList.size(); i++) {
			this.status.appendItem(this.statusList.get(i));
		}

		// this.status.setModel(new ListModelList<String>(this.statusList));

	}

	private Boolean setupItemWithValues(final Employment item) {

		item.setNote(this.note.getValue());
		item.setId_user(this.person_selected.getId());

		if (this.contractual_level.getSelectedItem() != null) {
			item.setContractual_level(Integer.parseInt(this.contractual_level.getSelectedItem().getValue().toString()));
		}

		// set status
		if (this.status.getSelectedItem() != null) {
			final String status_val = this.status.getSelectedItem().getLabel();
			item.setStatus(status_val);
		} else {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Selezionare Status utente!", "INFO", buttons, null, Messagebox.ERROR, null, null, params);

			return false;
		}

		if (this.date_modifiled.getValue() == null) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Selezionare una data!", "INFO", buttons, null, Messagebox.ERROR, null, null, params);
			return false;
		}

		item.setDate_modified(this.date_modifiled.getValue());
		return true;
	}

}
