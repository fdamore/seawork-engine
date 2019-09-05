package org.uario.seaworkengine.zkevent;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.uario.seaworkengine.model.Employment;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.WorkerStatus;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.EmploymentDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.UtilityCSV;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class UserDetailsComposerStatus extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Checkbox			check_contractual_level;

	@Wire
	private Checkbox			check_date_end;

	private ConfigurationDAO	configurationDao;

	@Wire
	private Combobox			contractual_level;

	@Wire
	private Label				current_;

	@Wire
	private Datebox				date_end;

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

	@Wire
	private Listbox				sw_list;

	@Listen("onClick = #sw_add")
	public void addItem() {

		this.status.setValue(null);

		this.status_add = true;
		this.date_modifiled.setValue(Calendar.getInstance().getTime());

		this.date_end.setValue(null);
		this.date_end.setDisabled(true);

		this.note.setValue("");

		this.contractual_level.setValue(null);
		this.contractual_level.setDisabled(true);

		this.check_date_end.setChecked(false);
		this.check_contractual_level.setChecked(false);

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

		final Map<String, String> params = new HashMap<>();
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

				final Employment data = (Employment) arg0.getData();

				if (data == null) {
					return;
				}

				// send event to show user task
				final Component comp = Path.getComponent("//user/page_user_detail");

				Events.sendEvent(ZkEventsTag.onUpdateGeneralDetails, comp, data);

				UserDetailsComposerStatus.this.employmentDao.createEmploymentForUser(UserDetailsComposerStatus.this.person_selected.getId(), data);

				UserDetailsComposerStatus.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #user_status_csv")
	public void downloadCSV_user_raporto() {

		if ((this.person_selected == null) || (this.person_selected.getId() == null)) {
			return;
		}

		final List<Employment> list = this.employmentDao.loadEmploymentByUser(this.person_selected.getId());
		final StringBuilder builder = UtilityCSV.downloadCSV_user_raportolavorativo(null, list, true);
		Filedownload.save(builder.toString(), "application/text", "info_raporto.csv");

	}

	public Datebox getDate_end() {
		return this.date_end;
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
		this.status.setValue(status_info);

		this.date_modifiled.setValue(item.getDate_modified());
		this.date_end.setValue(item.getDate_end());

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

			final Map<String, String> params = new HashMap<>();
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

		if ((item != null) && (to_day.compareTo(my_date) >= 0)) {

			// send event to show user task
			final Component comp = Path.getComponent("//user/page_user_detail");

			Events.sendEvent(ZkEventsTag.onUpdateGeneralDetails, comp, item);

		}

		// Refresh list task
		this.setInitialView();

	}

	@Listen("onClick = #sw_link_delete")
	public void removeItem() {
		final Map<String, String> params = new HashMap<>();
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

		final Map<String, String> params = new HashMap<>();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

		Messagebox.show("Status utente assegnato", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

	}

	public void setDate_end(final Datebox date_end) {
		this.date_end = date_end;
	}

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		final List<Employment> list = this.employmentDao.loadEmploymentByUser(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<>(list));

		this.grid_details.setVisible(false);

		this.current_.setValue(this.person_selected.getStatus());
	}

	private void setStatusComboBox() {

		this.status.getItems().clear();

		final List<WorkerStatus> statusList = this.configurationDao.selectAllStatus();

		this.status.setModel(new ListModelList<>(statusList));

	}

	/**
	 * define metohod item for modify and add object
	 *
	 * @param item
	 * @return
	 */
	private Boolean setupItemWithValues(final Employment item) {

		item.setNote(this.note.getValue());
		item.setId_user(this.person_selected.getId());

		// set status
		if (this.status.getSelectedItem() != null) {
			final String status_val = this.status.getSelectedItem().getLabel();
			item.setStatus(status_val);
		} else {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Selezionare Status utente!", "INFO", buttons, null, Messagebox.ERROR, null, null, params);

			return false;
		}

		if (this.date_modifiled.getValue() == null) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Selezionare una data inizio!", "INFO", buttons, null, Messagebox.ERROR, null, null, params);
			return false;
		} else {
			item.setDate_modified(this.date_modifiled.getValue());
		}

		if (this.check_date_end.isChecked()) {
			if (this.date_end.getValue() != null) {
				if (this.date_end.getValue().before(this.date_modifiled.getValue())) {
					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");
					final Messagebox.Button[] buttons = new Messagebox.Button[1];
					buttons[0] = Messagebox.Button.OK;

					Messagebox.show("Attenzione alle date!", "ERROR", buttons, null, Messagebox.ERROR, null, null, params);
					return false;
				}
			}

			item.setDate_end(this.date_end.getValue());
		}

		if (this.check_contractual_level.isChecked()) {
			if (this.contractual_level.getSelectedItem() != null) {
				item.setContractual_level(Integer.parseInt(this.contractual_level.getSelectedItem().getValue().toString()));
			}
		}

		return true;
	}

}
