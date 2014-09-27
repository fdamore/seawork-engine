package org.uario.seaworkengine.zkevent;

import java.util.List;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

public class UserDetailsComposerTask extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private ConfigurationDAO	configurationDAO;

	@Wire
	private Component			grid_task_details;

	private final Logger		logger				= Logger.getLogger(UserDetailsComposerTask.class);

	private Person				person_selected;

	@Wire
	private Listbox				sw_list_task;

	// task interface
	private TasksDAO			taskDAO;

	@Wire
	private Combobox			user_task_code;

	@Wire
	private Label				user_task_description;

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
			Messagebox.show("Mansione già presente", "INFO", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}

		this.taskDAO.assignTaskToUser(this.person_selected.getId(), task.getId());

		Messagebox.show("Mansione aggiunta all'utente", "INFO", Messagebox.OK, Messagebox.INFORMATION);

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

	@Listen("onClick = #deletetask_command")
	public void deleteTaskUser() {

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final UserTask task = this.sw_list_task.getSelectedItem().getValue();

		this.taskDAO.deleteTaskToUser(this.person_selected.getId(), task.getId());

		Messagebox.show("Mansione rimossa dall'elenco utente", "INFO", Messagebox.OK, Messagebox.INFORMATION);

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
		final List<UserTask> list = this.configurationDAO.loadTasks();
		this.user_task_code.setModel(new ListModelList<UserTask>(list));

		final List<UserTask> list_mytask = this.taskDAO.loadTasksByUser(this.person_selected.getId());
		this.sw_list_task.setModel(new ListModelList<UserTask>(list_mytask));

	}

}
