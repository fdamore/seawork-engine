package org.uario.seaworkengine.zkevent;

import java.text.NumberFormat;
import java.util.List;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.IParams;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ParamsTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class Preferences extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Div					box_deleteshift;

	@Wire
	private Div					box_deletetask;

	@Wire
	private Textbox				code_shift;

	@Wire
	private Textbox				code_task;

	private ConfigurationDAO	configurationDao;

	@Wire
	private Textbox				description_shift;

	@Wire
	private Textbox				description_task;

	@Wire
	private Textbox				docrepo;

	@Wire
	private Checkbox			forceable;

	@Wire
	private Div					grid_shift_details;

	@Wire
	private Div					grid_task_details;

	@Wire
	private Label				label_allocated_meomry;

	@Wire
	private Label				label_free_meomry;

	@Wire
	private Label				label_max_meomry;

	private final NumberFormat	numberFormat		= NumberFormat.getInstance();

	private IParams				paramsDAO;

	private final Runtime		runtime				= Runtime.getRuntime();

	@Wire
	private Listbox				sw_list_shift;

	@Wire
	private Listbox				sw_list_task;

	@Wire
	private Combobox			type_shift;

	@Listen("onClick = #add_shifts_command")
	public void addShift() {

		String us_type = null;
		if (this.type_shift.getSelectedItem() != null) {
			us_type = this.type_shift.getSelectedItem().getValue();
		}

		final UserShift shift = new UserShift();
		shift.setCode(this.code_shift.getValue());
		shift.setDescription(this.description_shift.getValue());
		shift.setUs_type(us_type);
		if (this.forceable.isChecked()) {
			shift.setForceable(true);
		} else {
			shift.setForceable(false);
		}
		this.configurationDao.createShift(shift);

		this.refreshShiftList();
		this.resetShiftInfo();

		this.grid_shift_details.setVisible(false);

	}

	@Listen("onClick = #sw_addshift")
	public void addShiftDefine() {
		this.resetShiftInfo();
	}

	@Listen("onClick = #add_tasks_command")
	public void addTask() {

		final UserTask task = new UserTask();
		task.setCode(this.code_task.getValue());
		task.setDescription(this.description_task.getValue());
		this.configurationDao.createTask(task);

		this.refreshTaskList();

		this.resetTaskInfo();

		this.grid_task_details.setVisible(false);

	}

	@Listen("onClick = #sw_addtask")
	public void addTaskDefine() {
		this.resetTaskInfo();
	}

	@Listen("onClick = #deleteshift_command")
	public void deleteShift() {

		if (this.sw_list_shift.getSelectedItem() == null) {
			return;
		}

		final UserShift shift = this.sw_list_shift.getSelectedItem().getValue();

		this.configurationDao.removeShift(shift.getId());

		this.refreshShiftList();

		this.grid_shift_details.setVisible(false);
		this.box_deleteshift.setVisible(false);

	}

	@Listen("onClick = #deletetask_command")
	public void deleteTask() {

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		final UserTask task = this.sw_list_task.getSelectedItem().getValue();

		this.configurationDao.removeTask(task.getId());

		this.refreshTaskList();

		this.grid_task_details.setVisible(false);
		this.box_deletetask.setVisible(false);

	}

	@Override
	public void doFinally() throws Exception {

		this.getSelf().addEventListener(ZkEventsTag.onShowPreferences, new EventListener<Event>() {

			@Override
			public void onEvent(final Event event) throws Exception {

				// get the configuration dao
				Preferences.this.configurationDao = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
				Preferences.this.paramsDAO = (IParams) SpringUtil.getBean(BeansTag.PARAMS_DAO);

				// define memory info
				Preferences.this.showMemory();

				// define doc repository
				Preferences.this.setDocRepositiry();

				// refresh task list
				Preferences.this.refreshTaskList();
				Preferences.this.resetTaskInfo();

				// refresh shift list
				Preferences.this.refreshShiftList();
				Preferences.this.resetTaskInfo();

			}

		});
		this.showMemory();
	}

	@Listen("onClick = #free_memory")
	public void freeMemory() {
		System.gc();

		// recall..
		this.showMemory();
	}

	@Listen("onClick = #sw_link_modifyshift")
	public void modifyShift() {

		if (this.sw_list_shift.getSelectedItem() == null) {
			return;
		}

		final UserShift shift = this.sw_list_shift.getSelectedItem().getValue();

		this.code_shift.setValue(shift.getCode());
		this.description_shift.setValue(shift.getDescription());
		if (shift.getForceable()) {
			this.forceable.setChecked(true);
		} else {
			this.forceable.setChecked(false);
		}

		// set type shift
		final String shft = shift.getUs_type();
		if ((shft == null) || shft.equals("")) {
			this.type_shift.setSelectedItem(null);
		} else {
			final List<Comboitem> lists = this.type_shift.getItems();
			for (final Comboitem item : lists) {
				if (item.getValue().equals(shft)) {
					this.type_shift.setSelectedItem(item);
					break;
				}
			}
		}

	}

	@Listen("onClick = #modify_shifts_command")
	public void modifyShiftCommand() {

		if (this.type_shift.getSelectedItem() == null) {
			return;
		}

		// define us_type
		final String us_type = this.type_shift.getSelectedItem().getValue();

		final UserShift shift = this.sw_list_shift.getSelectedItem().getValue();

		shift.setCode(this.code_shift.getValue());
		shift.setDescription(this.description_shift.getValue());
		shift.setUs_type(us_type);
		if (this.forceable.isChecked()) {
			shift.setForceable(true);
		} else {
			shift.setForceable(false);
		}

		this.configurationDao.updateShift(shift);

		this.refreshShiftList();
		this.resetShiftInfo();

		this.grid_shift_details.setVisible(false);

	}

	@Listen("onClick = #sw_link_modifyetask")
	public void modifyTask() {

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		final UserTask task = this.sw_list_task.getSelectedItem().getValue();

		this.code_task.setValue(task.getCode());
		this.description_task.setValue(task.getDescription());

	}

	@Listen("onClick = #modify_tasks_command")
	public void modifyTaskCommand() {

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		final UserTask task = this.sw_list_task.getSelectedItem().getValue();

		task.setCode(this.code_task.getValue());
		task.setDescription(this.description_task.getValue());

		this.configurationDao.updateTask(task);

		this.refreshTaskList();

		this.resetTaskInfo();

		this.grid_task_details.setVisible(false);

	}

	@Listen("onClick = #sw_refresh_tasklist")
	public void refreshShiftCommand() {
		this.refreshTaskList();

		this.resetTaskInfo();

		this.grid_task_details.setVisible(false);
	}

	private void refreshShiftList() {
		// set info about task
		final List<UserShift> list = this.configurationDao.loadShifts();
		Preferences.this.sw_list_shift.setModel(new ListModelList<UserShift>(list));

		this.box_deleteshift.setVisible(false);
	}

	@Listen("onClick = #sw_refresh_shiftlist")
	public void refreshTaskCommand() {
		this.refreshShiftList();

		this.resetShiftInfo();

		this.grid_shift_details.setVisible(false);
	}

	private void refreshTaskList() {
		// set info about task
		final List<UserTask> list = Preferences.this.configurationDao.loadTasks();
		Preferences.this.sw_list_task.setModel(new ListModelList<UserTask>(list));

		this.box_deletetask.setVisible(false);
	}

	private void resetShiftInfo() {
		this.code_shift.setValue("");
		this.description_shift.setValue("");
		this.type_shift.setSelectedItem(null);
	}

	private void resetTaskInfo() {
		this.code_task.setValue("");
		this.description_task.setValue("");
	}

	/**
	 * Set repositiry
	 */
	private void setDocRepositiry() {
		final String repo_value = this.paramsDAO.getParam(ParamsTag.REPO_DOC);
		this.docrepo.setValue(repo_value);

	}

	/**
	 * Calculate memory
	 */
	private void showMemory() {
		final long maxMemory = this.runtime.maxMemory();
		final long freeMemory = this.runtime.freeMemory();
		final long allocatedMemory = this.runtime.totalMemory();

		// set value
		final String max_memory_info = this.numberFormat.format(maxMemory / 1024);
		final String free_memory_info = this.numberFormat.format(freeMemory / 1024);
		final String allocated_memory_info = this.numberFormat.format(allocatedMemory / 1024);

		this.label_max_meomry.setValue(max_memory_info);
		this.label_free_meomry.setValue(free_memory_info);
		this.label_allocated_meomry.setValue(allocated_memory_info);
	}

	@Listen("onClick = #update_doc_repo")
	public void updateDocRepository() {
		final String value = this.docrepo.getValue();
		this.paramsDAO.setParam(ParamsTag.REPO_DOC, value);

		Messagebox.show("Doc Repository Aggiornato con successo", "INFO", Messagebox.OK, Messagebox.INFORMATION);
	}

}
