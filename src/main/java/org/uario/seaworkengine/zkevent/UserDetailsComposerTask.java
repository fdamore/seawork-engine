package org.uario.seaworkengine.zkevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.TaskColor;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

public class UserDetailsComposerTask extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private ConfigurationDAO configurationDAO;

	@Wire
	private Component grid_task_details;

	private final Logger logger = Logger.getLogger(UserDetailsComposerTask.class);

	private Person person_selected;

	@Wire
	private Listbox sw_list_task;

	// task interface
	private TasksDAO taskDAO;

	@Wire
	private Combobox user_task_code;

	@Wire
	private Label user_task_description;

	@Listen("onClick = #add_tasks_command")
	public void addTaskUser() {

		if (this.user_task_code.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final UserTask task = this.user_task_code.getSelectedItem().getValue();

		// check for task assignment
		final Boolean check = this.taskDAO.isTaskAssigned(this.person_selected.getId(), task.getId());
		if (check.booleanValue()) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Mansione già presente", "INFO", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		this.taskDAO.assignTaskToUser(this.person_selected.getId(), task.getId());

		final Map<String, String> params = new HashMap();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

		Messagebox.show("Mansione aggiunta all'utente", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		// Refresh list task
		this.refreshListTaskUser();

	}

	@Listen("onChange = #user_task_code")
	public void changeTaskSelector() {

		if (this.user_task_code.getSelectedItem() == null) {
			return;
		}

		final UserTask task = this.user_task_code.getSelectedItem().getValue();

		this.user_task_description.setValue(task.getDescription());

	}

	private void deleteTaskUser() {

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final UserTask task = this.sw_list_task.getSelectedItem().getValue();

		this.taskDAO.deleteTaskToUser(this.person_selected.getId(), task.getId());

		final Map<String, String> params = new HashMap();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

		Messagebox.show("Mansione rimossa dall'elenco utente", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		// Refresh list task
		this.refreshListTaskUser();

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
				UserDetailsComposerTask.this.person_selected = (Person) arg0.getData();

				// get the person dao
				UserDetailsComposerTask.this.taskDAO = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);
				UserDetailsComposerTask.this.configurationDAO = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);

				UserDetailsComposerTask.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #sw_refresh_list_task")
	public void refreshListTaskUser() {

		if (this.person_selected == null) {
			return;
		}

		final List<UserTask> list = this.taskDAO.loadTasksByUser(this.person_selected.getId());
		this.sw_list_task.setModel(new ListModelList<UserTask>(list));

		this.grid_task_details.setVisible(false);
	}

	@Listen("onClick = #sw_link_deletetask")
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
							UserDetailsComposerTask.this.deleteTaskUser();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				}, params);

	}

	@Listen("onClick = #sw_setdefault")
	public void setDefault() {

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final UserTask task = this.sw_list_task.getSelectedItem().getValue();

		this.taskDAO.setAsDefault(this.person_selected.getId(), task.getId());

		// Refresh list task
		this.refreshListTaskUser();

	}

	/**
	 * Show users
	 */
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		// select combo task
		final List<UserTask> list_task_user = this.configurationDAO.listAllStandardTask();
		final List<UserTask> list_task_absence = this.configurationDAO.listAllAbsenceTask();
		final List<UserTask> list_task_justificatory = this.configurationDAO.listAllJustificatoryTask();

		final List<UserTask> list = new ArrayList<UserTask>();
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

		final List<UserTask> list_mytask = this.taskDAO.loadTasksByUser(this.person_selected.getId());
		this.sw_list_task.setModel(new ListModelList<UserTask>(list_mytask));

	}

}
