package org.uario.seaworkengine.zkevent;

import java.text.NumberFormat;
import java.util.List;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.IParams;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ParamsTag;
import org.uario.seaworkengine.utility.ShiftTag;
import org.uario.seaworkengine.utility.UserStatusTag;
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
	private Textbox				code_shift;

	@Wire
	private Textbox				code_status;

	@Wire
	private Textbox				code_task;

	private ConfigurationDAO	configurationDao;

	@Wire
	private Textbox				description_shift;

	@Wire
	private Textbox				description_status;

	@Wire
	private Textbox				description_task;

	@Wire
	private Textbox				docrepo;

	@Wire
	private Checkbox			forceable;

	@Wire
	private Div					grid_shift_details;

	@Wire
	private Div					grid_status_details;

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
	private Listbox				sw_list_status;

	@Wire
	private Listbox				sw_list_task;

	@Wire
	private Combobox			type_shift;

	@Wire
	private Combobox			typeofbreak;

	@Listen("onClick = #add_shifts_command")
	public void addShift() {

		String us_type = null;
		if (this.type_shift.getSelectedItem() != null) {
			us_type = this.type_shift.getSelectedItem().getValue();
		}

		final UserShift shift = new UserShift();
		shift.setCode(this.code_shift.getValue());
		shift.setDescription(this.description_shift.getValue());

		if (us_type == null) {
			Messagebox.show("Selezionare tipo di turno", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		if (us_type.equals(ShiftTag.ABSENCE_SHIFT)) {
			shift.setPresence(false);
		} else {
			shift.setPresence(true);
		}

		if (this.forceable.isChecked()) {
			shift.setForceable(true);
		} else {
			shift.setForceable(false);
		}

		final String typeOfBreakShift = this.typeofbreak.getSelectedItem().getValue().toString();

		shift.setBreak_shift(false);
		shift.setWaitbreak_shift(false);
		shift.setAccident_shift(false);
		shift.setDisease_shift(false);

		if (!typeOfBreakShift.equals("Non definito")) {
			if (typeOfBreakShift.equals("Riposo Programmato")) {
				shift.setBreak_shift(true);
				this.configurationDao.removeAllBreakShift();
			} else if (typeOfBreakShift.equals("Riposo Atteso")) {
				shift.setWaitbreak_shift(true);
				this.configurationDao.removeAllExpectedBreakShift();
			} else if (typeOfBreakShift.equals("Riposo Infortunio")) {
				shift.setAccident_shift(true);
				this.configurationDao.removeAllInjuryShift();
			} else if (typeOfBreakShift.equals("Riposo Malattia")) {
				shift.setDisease_shift(true);
				this.configurationDao.removeAllDiseaseShift();
			}
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

	@Listen("onClick = #add_status_command")
	public void addStatus() {

		final String status = this.description_status.getValue().toString();

		if (status == "") {
			Messagebox.show("Inserire uno status!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		if (this.configurationDao.selectAllStatus().contains(status)) {
			Messagebox.show("Status già presente!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		this.configurationDao.addStatus(status);

		this.refreshStatusList();
		this.resetStatusInfo();

		this.grid_status_details.setVisible(false);

	}

	@Listen("onClick = #sw_addstatus")
	public void addStatusDefine() {
		this.resetStatusInfo();
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

	private void deleteShift() {

		if (this.sw_list_shift.getSelectedItem() == null) {
			return;
		}

		final UserShift shift = this.sw_list_shift.getSelectedItem().getValue();

		this.configurationDao.removeShift(shift.getId());

		this.refreshShiftList();

		this.grid_shift_details.setVisible(false);

	}

	private void deleteStatus() {

		if (this.sw_list_status.getSelectedItem() == null) {
			return;
		}

		final String status = this.sw_list_status.getSelectedItem().getValue().toString();

		this.configurationDao.removeStatus(status);

		this.refreshStatusList();

		this.grid_status_details.setVisible(false);

	}

	private void deleteTask() {

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		final UserTask task = this.sw_list_task.getSelectedItem().getValue();

		this.configurationDao.removeTask(task.getId());

		this.refreshTaskList();

		this.grid_task_details.setVisible(false);

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

				// refresh status list
				Preferences.this.refreshStatusList();
				Preferences.this.resetStatusInfo();

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

		if (shift.getBreak_shift()) {
			this.typeofbreak.setSelectedIndex(1);
			this.type_shift.setDisabled(true);
			this.forceable.setDisabled(true);
		} else if (shift.getWaitBreak_shift()) {
			this.typeofbreak.setSelectedIndex(2);
			this.type_shift.setDisabled(true);
			this.forceable.setDisabled(true);
		} else if (shift.getAccident_shift()) {
			this.typeofbreak.setSelectedIndex(3);
			this.type_shift.setDisabled(true);
			this.forceable.setDisabled(true);
		} else if (shift.getDisease_shift()) {
			this.typeofbreak.setSelectedIndex(4);
			this.type_shift.setDisabled(true);
			this.forceable.setDisabled(true);
		} else {
			this.typeofbreak.setSelectedItem(null);
			this.type_shift.setDisabled(false);
			this.forceable.setDisabled(false);
		}

		this.code_shift.setValue(shift.getCode());
		this.description_shift.setValue(shift.getDescription());
		if (shift.getForceable()) {
			this.forceable.setChecked(true);
		} else {
			this.forceable.setChecked(false);
		}

		// set type shift
		String shft = null;

		if (shift.getPresence() != null) {
			if (!shift.getPresence()) {
				shft = ShiftTag.ABSENCE_SHIFT;
			} else {
				shft = ShiftTag.WORK_SHIFT;
			}
		}

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
			this.grid_shift_details.setVisible(false);
			return;
		}

		// define us_type
		final String us_type = this.type_shift.getSelectedItem().getValue();

		final UserShift shift = this.sw_list_shift.getSelectedItem().getValue();

		shift.setCode(this.code_shift.getValue());
		shift.setDescription(this.description_shift.getValue());
		if (us_type.equals(ShiftTag.ABSENCE_SHIFT)) {
			shift.setPresence(false);
		} else {
			shift.setPresence(true);
		}

		if (this.forceable.isChecked()) {
			shift.setForceable(true);
		} else {
			shift.setForceable(false);
		}

		shift.setBreak_shift(false);
		shift.setDisease_shift(false);
		shift.setAccident_shift(false);
		shift.setWaitbreak_shift(false);

		if (this.typeofbreak.getSelectedItem() != null) {
			final String typeOfBreak = this.typeofbreak.getSelectedItem().getValue();
			if (typeOfBreak.equals("Riposo Programmato")) {
				shift.setBreak_shift(true);
				this.configurationDao.setShiftAsBreak(shift.getId());
			} else if (typeOfBreak.equals("Riposo Atteso")) {
				shift.setWaitbreak_shift(true);
				this.configurationDao.setShiftAsExpectedBreak(shift.getId());
			} else if (typeOfBreak.equals("Riposo Infortunio")) {
				shift.setAccident_shift(true);
				this.configurationDao.setShiftAsInjury(shift.getId());
			} else if (typeOfBreak.equals("Riposo Malattia")) {
				shift.setDisease_shift(true);
				this.configurationDao.setShiftAsDisease(shift.getId());
			}

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

	@Listen("onClick = #sw_refresh_shiftlist")
	public void refreshShiftCommand() {
		this.refreshShiftList();

		this.resetShiftInfo();

		this.grid_shift_details.setVisible(false);
	}

	private void refreshShiftList() {
		// set info about task
		final List<UserShift> list = this.configurationDao.loadShifts();
		Preferences.this.sw_list_shift.setModel(new ListModelList<UserShift>(list));

	}

	@Listen("onClick = #sw_refresh_status_list")
	public void refreshStatusList() {
		final List<String> list = this.configurationDao.selectAllStatus();
		Preferences.this.sw_list_status.setModel(new ListModelList<String>(list));

	}

	@Listen("onClick = #sw_refresh_tasklist")
	public void refreshTaskCommand() {
		this.refreshTaskList();

		this.resetTaskInfo();

		this.grid_task_details.setVisible(false);
	}

	private void refreshTaskList() {
		// set info about task
		final List<UserTask> list = Preferences.this.configurationDao.loadTasks();
		Preferences.this.sw_list_task.setModel(new ListModelList<UserTask>(list));

	}

	@Listen("onClick = #sw_link_deleteshift")
	public void removeShift() {
		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener() {
					@Override
					public void onEvent(final Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							Preferences.this.deleteShift();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});

	}

	@Listen("onClick = #sw_link_deletestatus")
	public void removeStatus() {
		final String status = this.sw_list_status.getSelectedItem().getValue().toString();
		if (status.equals(UserStatusTag.FIRED) || status.equals(UserStatusTag.SUSPENDED) || status.equals(UserStatusTag.OPEN)) {
			Messagebox.show("Status di sistema, impossibile eliminare!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener() {
					@Override
					public void onEvent(final Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							Preferences.this.deleteStatus();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});

	}

	@Listen("onClick = #sw_link_deletetask")
	public void removeTask() {
		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener() {
					@Override
					public void onEvent(final Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							Preferences.this.deleteTask();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});

	}

	private void resetShiftInfo() {
		this.code_shift.setValue("");
		this.description_shift.setValue("");
		this.type_shift.setSelectedItem(null);
		this.typeofbreak.setSelectedIndex(0);
	}

	private void resetStatusInfo() {
		this.description_status.setValue("");

	}

	private void resetTaskInfo() {
		this.code_task.setValue("");
		this.description_task.setValue("");
	}

	@Listen("onSelect = #typeofbreak")
	public void setBreakShift() {
		if (!this.typeofbreak.getSelectedItem().getValue().toString().equals("Non definito")) {
			this.type_shift.setSelectedItem(this.type_shift.getItemAtIndex(1));
			this.type_shift.setDisabled(true);
			this.forceable.setChecked(false);
			this.forceable.setDisabled(true);
		} else {
			this.type_shift.setDisabled(false);
			this.forceable.setDisabled(false);
		}
	}

	/**
	 * Set repositiry
	 */
	private void setDocRepositiry() {
		final String repo_value = this.paramsDAO.getParam(ParamsTag.REPO_DOC);
		this.docrepo.setValue(repo_value);

	}

	@Listen("onSelect = #type_shift")
	public void setForceableChekbox() {
		final String typeShift = this.type_shift.getSelectedItem().getValue().toString();
		if (typeShift.equals(ShiftTag.WORK_SHIFT)) {
			this.forceable.setChecked(false);
			this.forceable.setDisabled(true);
		} else {
			this.forceable.setDisabled(false);
		}
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
