package org.uario.seaworkengine.zkevent;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uario.seaworkengine.model.BillCenter;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.IJobCost;
import org.uario.seaworkengine.platform.persistence.dao.IParams;
import org.uario.seaworkengine.statistics.IBankHolidays;
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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class Preferences extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Wire
	private Component add_billcenter_command;

	private IBankHolidays bank_holiday;

	private BillCenter billCenterSelected;

	@Wire
	private Checkbox changeshift_task;

	@Wire
	private Textbox code_shift;

	@Wire
	private Textbox code_status;

	@Wire
	private Textbox code_task;

	private ConfigurationDAO configurationDao;

	@Wire
	private Checkbox delayoperation_task;

	@Wire
	private Textbox description_billcenter;

	@Wire
	private Textbox description_shift;

	@Wire
	private Textbox description_status;

	@Wire
	private Textbox description_task;

	@Wire
	private Textbox docrepo;

	@Wire
	private Checkbox endoperation_task;

	@Wire
	private Checkbox forceable;

	@Wire
	private Textbox full_text_search_BillCenter;

	@Wire
	private Textbox full_text_searchShift;

	@Wire
	private Textbox full_text_searchTask;

	@Wire
	private Component grid_billcenter_details;

	@Wire
	private Div grid_shift_details;

	@Wire
	private Div grid_status_details;

	@Wire
	private Div grid_task_details;

	@Wire
	private Checkbox hidden_task;

	@Wire
	private Checkbox isabsence_task;

	private IJobCost jobCostDao;

	@Wire
	private Checkbox justificatory_task;

	@Wire
	private Label label_allocated_meomry;

	@Wire
	private Label label_free_meomry;

	@Wire
	private Label label_max_meomry;

	@Wire
	private Listbox list_bankholiday;

	@Wire
	private Component modify_billcenter_command;

	private final NumberFormat numberFormat = NumberFormat.getInstance();

	@Wire
	private Checkbox overflow_task;

	private IParams paramsDAO;

	@Wire
	public Checkbox recorded_shift;

	@Wire
	private Checkbox recorded_task;

	private final Runtime runtime = Runtime.getRuntime();

	private int selectedOptionMobileTask;

	@Wire
	private Intbox shows_rows;

	@Wire
	private Intbox shows_rowsShift;

	@Wire
	private Intbox shows_rowsTask;

	@Wire
	private Listbox sw_list_billcenter;

	@Wire
	private Listbox sw_list_shift;

	@Wire
	private Listbox sw_list_status;

	@Wire
	private Listbox sw_list_task;

	@Wire
	private Combobox type_shift;

	@Wire
	private Combobox typeofbreak;

	@Listen("onClick = #add_billcenter_command")
	public void addBillCenterCommand() {
		if ((this.description_billcenter.getValue() != null) && (this.description_billcenter.getValue().trim() != "")) {
			final BillCenter billCenter = new BillCenter();
			billCenter.setDescription(this.description_billcenter.getValue());
			this.jobCostDao.createBillCenter(billCenter);
			this.refreshBillCenterList();
		}
	}

	@Listen("onClick = #add_shifts_command")
	public void addShift() {

		String us_type = null;
		if (this.type_shift.getSelectedItem() != null) {
			us_type = this.type_shift.getSelectedItem().getValue();
		}

		if (this.checkIfUserCodeIsPresent(this.configurationDao.loadAllShiftCode(), this.code_shift.getValue().toUpperCase())) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Codice turno già presente.", "Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);
		} else {

			final UserShift shift = new UserShift();
			shift.setCode(this.code_shift.getValue());
			shift.setDescription(this.description_shift.getValue());

			if (us_type == null) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Selezionare tipo di turno", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

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
			shift.setStandard_shift(false);
			shift.setDaily_shift(false);

			if (!typeOfBreakShift.equals("Non definito")) {
				if (typeOfBreakShift.equals("Riposo Programmato")) {
					shift.setBreak_shift(true);
					this.configurationDao.removeAllBreakShift();
				} else if (typeOfBreakShift.equals("Riposo Atteso")) {
					shift.setWaitbreak_shift(true);
					this.configurationDao.removeAllWaitBreakShift();
				} else if (typeOfBreakShift.equals("Riposo Infortunio")) {
					shift.setAccident_shift(true);
					this.configurationDao.removeAllAccidentShift();
				} else if (typeOfBreakShift.equals("Riposo Malattia")) {
					shift.setDisease_shift(true);
					this.configurationDao.removeAllDiseaseShift();
				} else if (typeOfBreakShift.equals("Turno Standard")) {
					shift.setStandard_shift(true);
					this.configurationDao.removeAllStandardShift();
				} else if (typeOfBreakShift.equals("Turno Giornaliero")) {
					shift.setDaily_shift(true);
					this.configurationDao.removeAllDailyShift();
				}
			}

			shift.setRecorded(this.recorded_shift.isChecked());

			this.configurationDao.createShift(shift);

			this.refreshShiftList();
			this.resetShiftInfo();

			this.grid_shift_details.setVisible(false);
		}

	}

	@Listen("onClick = #sw_addshift")
	public void addShiftDefine() {
		this.resetShiftInfo();
	}

	@Listen("onClick = #add_status_command")
	public void addStatus() {

		final String status = this.description_status.getValue().toString();

		if (status == "") {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Inserire uno status", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			return;
		}

		if (this.configurationDao.selectAllStatus().contains(status)) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Status già presente!", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
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

		// check if user code is present in table
		if (this.checkIfUserCodeIsPresent(this.configurationDao.loadAllTaskCode(), this.code_task.getValue().toUpperCase())) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Codice mansione già presente.", "Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);
		} else {

			String alertMessage = null;
			this.selectedOptionMobileTask = 0;

			final UserTask actualOverFlowTask = this.configurationDao.getOverflowTask();
			final UserTask actualEndOperationTask = this.configurationDao.getEndOperationTask();
			final UserTask actualDelayOperationTask = this.configurationDao.getDelayOperationTask();
			final UserTask actualChangeshiftTask = this.configurationDao.getChangeshiftTask();

			if (this.overflow_task.isChecked() && (actualOverFlowTask != null)) {
				alertMessage = "Mansione di Esubero per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 1;
			}

			if (this.endoperation_task.isChecked() && (actualEndOperationTask != null)) {
				alertMessage = "Mansione di Fine operazione per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 2;
			}

			if (this.delayoperation_task.isChecked() && (actualDelayOperationTask != null)) {
				alertMessage = "Mansione di Ritado per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 3;
			}

			if (this.changeshift_task.isChecked() && (actualChangeshiftTask != null)) {
				alertMessage = "Mansione di Cambio Turno per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 4;
			}

			if (alertMessage != null) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.CANCEL;

				Messagebox.show(alertMessage, "CONFERMA ASSEGNAZIONE", buttons, null, Messagebox.EXCLAMATION, null, new EventListener() {
					@Override
					public void onEvent(final Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							if (Preferences.this.selectedOptionMobileTask == 1) {
								Preferences.this.configurationDao.removeAllOverflowTasks();
							} else if (Preferences.this.selectedOptionMobileTask == 2) {
								Preferences.this.configurationDao.removeAllEndoperationTasks();
							} else if (Preferences.this.selectedOptionMobileTask == 3) {
								Preferences.this.configurationDao.removeAllDelayOperationTasks();
							} else if (Preferences.this.selectedOptionMobileTask == 4) {
								Preferences.this.configurationDao.removeAllChangeshiftTasks();
							}

							Preferences.this.createTask(task);

						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							return;
						}
					}
				}, params);
			} else {
				this.createTask(task);
			}

		}

	}

	@Listen("onClick = #sw_addtask")
	public void addTaskDefine() {
		this.resetTaskInfo();
	}

	private boolean checkIfUserCodeIsPresent(final List<String> codeList, String code) {
		if (code == null) {
			code = "";
		}

		for (final String string : codeList) {
			if (string.toUpperCase().equals(code)) {
				return true;
			}
		}

		return false;

	}

	@Listen("onClick = #closeBillCenterDetail")
	public void closeBillCenterDetail() {
		this.description_billcenter.setValue("");
	}

	private void createTask(final UserTask task) {
		task.setCode(this.code_task.getValue());
		task.setDescription(this.description_task.getValue());
		task.setIsabsence(this.isabsence_task.isChecked());
		task.setJustificatory(this.justificatory_task.isChecked());
		task.setOverflow(this.overflow_task.isChecked());
		task.setDelayoperation(this.delayoperation_task.isChecked());
		task.setEndoperation(this.endoperation_task.isChecked());
		task.setChangeshift(this.changeshift_task.isChecked());
		task.setRecorded(this.recorded_task.isChecked());
		task.setHiddentask(this.hidden_task.isChecked());

		this.configurationDao.createTask(task);

		this.refreshTaskList();

		this.resetTaskInfo();

		this.grid_task_details.setVisible(false);
	}

	@Listen("onClick = #sw_link_deleteBillCenter")
	public void deleteBillCenter() {
		if (this.sw_list_billcenter.getSelectedItem() != null) {
			this.billCenterSelected = this.sw_list_billcenter.getSelectedItem().getValue();
			this.jobCostDao.deleteBillCenter(this.billCenterSelected.getId());
			this.refreshBillCenterList();
		}

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
				Preferences.this.bank_holiday = (IBankHolidays) SpringUtil.getBean(BeansTag.BANK_HOLIDAYS);
				Preferences.this.jobCostDao = (IJobCost) SpringUtil.getBean(BeansTag.JOB_COST_DAO);

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

				// show bank holiday
				Preferences.this.showBankHolidays();

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

	@Listen("onClick = #sw_return_mobiletask")
	public void getMobileTask() {
		final List<UserTask> list_MobileUsertask = this.configurationDao.listSpecialTaskMobile();

		this.sw_list_task.setModel(new ListModelList<UserTask>(list_MobileUsertask));
	}

	@Listen("onClick = #sw_link_modifyBillCenter")
	public void modifyBillCenter() {

		if (this.sw_list_billcenter.getSelectedItem() != null) {
			this.billCenterSelected = this.sw_list_billcenter.getSelectedItem().getValue();
			this.description_billcenter.setValue(this.billCenterSelected.getDescription());
		}

	}

	@Listen("onClick = #modify_billcenter_command")
	public void modifyBillCenterCommand() {
		if ((this.billCenterSelected != null) && (this.description_billcenter.getValue() != null)
				&& (this.description_billcenter.getValue().trim() != "")) {
			this.billCenterSelected.setDescription(this.description_billcenter.getValue());
			this.jobCostDao.updateBillCenter(this.billCenterSelected);

			this.grid_billcenter_details.setVisible(false);
			this.add_billcenter_command.setVisible(false);
			this.modify_billcenter_command.setVisible(false);

			this.refreshBillCenterList();
		}
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
		} else if (shift.getWaitbreak_shift()) {
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
		} else if (shift.getStandard_shift()) {
			this.typeofbreak.setSelectedIndex(5);
			this.type_shift.setDisabled(true);
			this.forceable.setDisabled(true);
		} else if (shift.getDaily_shift()) {
			this.typeofbreak.setSelectedIndex(6);
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
				this.recorded_shift.setChecked(shift.getRecorded());
				this.recorded_shift.setDisabled(false);
			} else {
				shft = ShiftTag.WORK_SHIFT;
				this.recorded_shift.setChecked(false);
				this.recorded_shift.setDisabled(true);
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

		final List<String> listShiftCode = this.configurationDao.loadAllShiftCode();
		if (shift.getCode().toUpperCase().equals(this.code_shift.getValue().toUpperCase())) {
			listShiftCode.remove(shift.getCode());
		}

		if (this.checkIfUserCodeIsPresent(listShiftCode, this.code_shift.getValue().toUpperCase())) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Codice turno già presente.", "Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);
		} else {

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

			final Boolean isDefault = shift.isDefault();

			// if shift is a default shift, user cannot modify shift as a non
			// default shift
			if (((isDefault && (this.typeofbreak.getSelectedItem() != null)
					&& !(this.typeofbreak.getSelectedItem().getValue().equals("Non definito")))) || (!isDefault)) {
				shift.setBreak_shift(false);
				shift.setDisease_shift(false);
				shift.setAccident_shift(false);
				shift.setWaitbreak_shift(false);
				shift.setStandard_shift(false);
				shift.setDaily_shift(false);

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
						this.configurationDao.setShiftAsAccident(shift.getId());
					} else if (typeOfBreak.equals("Riposo Malattia")) {
						shift.setDisease_shift(true);
						this.configurationDao.setShiftAsDisease(shift.getId());
					} else if (typeOfBreak.equals("Turno Standard")) {
						shift.setStandard_shift(true);
						this.configurationDao.setShiftAsStandardShift(shift.getId());
					} else if (typeOfBreak.equals("Turno Giornaliero")) {
						shift.setDaily_shift(true);
						this.configurationDao.setShiftAsDailyShift(shift.getId());
					}

				}

				shift.setRecorded(this.recorded_shift.isChecked());

				this.configurationDao.updateShift(shift);

				this.refreshShiftList();
				this.resetShiftInfo();

				this.grid_shift_details.setVisible(false);

			} else {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show(
						"Non puoi modificare un turno di default associandogli un valore non definito, puoi solo modificare il tipo di default.",
						"Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			}

		}

	}

	@Listen("onClick = #sw_link_modifyetask")
	public void modifyTask() {

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		final UserTask task = this.sw_list_task.getSelectedItem().getValue();

		this.code_task.setValue(task.getCode());
		this.description_task.setValue(task.getDescription());
		this.isabsence_task.setChecked(task.getIsabsence());
		this.justificatory_task.setChecked(task.getJustificatory());
		this.overflow_task.setChecked(task.getOverflow());
		this.delayoperation_task.setChecked(task.getDelayoperation());
		this.endoperation_task.setChecked(task.getEndoperation());
		this.changeshift_task.setChecked(task.getChangeshift());
		this.recorded_task.setChecked(task.getRecorded());
		this.hidden_task.setChecked(task.getHiddentask());

	}

	@Listen("onClick = #modify_tasks_command")
	public void modifyTaskCommand() {

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		final UserTask task = this.sw_list_task.getSelectedItem().getValue();
		final List<String> listTaskCode = this.configurationDao.loadAllTaskCode();
		if (task.getCode().toUpperCase().equals(this.code_task.getValue().toUpperCase())) {
			listTaskCode.remove(task.getCode());
		}

		if (this.checkIfUserCodeIsPresent(listTaskCode, this.code_task.getValue().toUpperCase())) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Codice mansione già presente.", "Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);
		} else {

			String alertMessage = null;

			this.selectedOptionMobileTask = 0;

			final UserTask actualOverFlowTask = this.configurationDao.getOverflowTask();
			final UserTask actualEndOperationTask = this.configurationDao.getEndOperationTask();
			final UserTask actualDelayOperationTask = this.configurationDao.getDelayOperationTask();
			final UserTask actualChangeshiftTask = this.configurationDao.getChangeshiftTask();

			if (this.overflow_task.isChecked() && (actualOverFlowTask != null) && (!actualOverFlowTask.getId().equals(task.getId()))) {
				alertMessage = "Mansione di Esubero per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 1;
			}

			if (this.endoperation_task.isChecked() && (actualEndOperationTask != null) && (!actualEndOperationTask.getId().equals(task.getId()))) {
				alertMessage = "Mansione di Fine operazione per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 2;
			}

			if (this.delayoperation_task.isChecked() && (actualDelayOperationTask != null)
					&& (!actualDelayOperationTask.getId().equals(task.getId()))) {
				alertMessage = "Mansione di Ritado per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 3;
			}

			if (this.changeshift_task.isChecked() && (actualChangeshiftTask != null) && (!actualChangeshiftTask.getId().equals(task.getId()))) {
				alertMessage = "Mansione di Cambio Turno per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 4;
			}

			if (alertMessage != null) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.CANCEL;

				Messagebox.show(alertMessage, "CONFERMA ASSEGNAZIONE", buttons, null, Messagebox.EXCLAMATION, null, new EventListener() {
					@Override
					public void onEvent(final Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							if (Preferences.this.selectedOptionMobileTask == 1) {
								Preferences.this.configurationDao.removeAllOverflowTasks();
							} else if (Preferences.this.selectedOptionMobileTask == 2) {
								Preferences.this.configurationDao.removeAllEndoperationTasks();
							} else if (Preferences.this.selectedOptionMobileTask == 3) {
								Preferences.this.configurationDao.removeAllDelayOperationTasks();
							} else if (Preferences.this.selectedOptionMobileTask == 4) {
								Preferences.this.configurationDao.removeAllChangeshiftTasks();
							}

							Preferences.this.updateTask(task);

						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							return;
						}
					}
				}, params);
			} else {
				this.updateTask(task);
			}

		}

	}

	@Listen("onClick = #sw_refresh_billcenterlist, #billcenter")
	public void refreshBillCenterList() {

		this.description_billcenter.setValue("");

		this.full_text_search_BillCenter.setValue("");

		final List<BillCenter> listBillCenter = this.jobCostDao.listAllBillCenter(null);

		if (listBillCenter != null) {
			this.sw_list_billcenter.setModel(new ListModelList<BillCenter>(listBillCenter));
		}

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

		this.full_text_searchShift.setValue(null);

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

		this.full_text_searchTask.setValue(null);
	}

	@Listen("onClick = #sw_link_deleteshift")
	public void removeShift() {

		if (this.sw_list_shift.getSelectedItem() == null) {
			return;
		}

		final UserShift shift = this.sw_list_shift.getSelectedItem().getValue();

		if (!shift.isDefault()) {

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
								Preferences.this.deleteShift();
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								// Cancel is clicked
							}
						}
					}, params);

		} else {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Prima di rimuovere il turno, assegnare la specificità ad un altro turno.", "ATTENZIONE", buttons, null,
					Messagebox.EXCLAMATION, null, null, params);

		}

	}

	@Listen("onClick = #sw_link_deletestatus")
	public void removeStatus() {
		final String status = this.sw_list_status.getSelectedItem().getValue().toString();
		if (status.equals(UserStatusTag.FIRED) || status.equals(UserStatusTag.SUSPENDED) || status.equals(UserStatusTag.OPEN)) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Status di sistema, impossibile eliminare!", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		}

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
							Preferences.this.deleteStatus();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				}, params);

	}

	@Listen("onClick = #sw_link_deletetask")
	public void removeTask() {
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
							Preferences.this.deleteTask();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				}, params);

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
		this.isabsence_task.setChecked(false);
		this.justificatory_task.setChecked(false);
		this.overflow_task.setChecked(false);
		this.delayoperation_task.setChecked(false);
		this.endoperation_task.setChecked(false);
		this.changeshift_task.setChecked(false);
		this.recorded_task.setChecked(false);
		this.hidden_task.setChecked(false);

	}

	@Listen("onChange = #full_text_search_BillCenter; onOK = #full_text_search_BillCenter")
	public void searchBillCenterText() {
		if (this.full_text_search_BillCenter.getValue() != null) {
			final List<BillCenter> listBillCenter = this.jobCostDao.listAllBillCenter(this.full_text_search_BillCenter.getValue());

			if (listBillCenter != null) {
				this.sw_list_billcenter.setModel(new ListModelList<BillCenter>(listBillCenter));
			}
		}
	}

	@Listen("onSelect = #typeofbreak")
	public void setBreakShift() {
		if (this.typeofbreak.getSelectedItem().getValue().toString().equals("Turno Giornaliero")) {
			// job shift
			this.type_shift.setSelectedItem(this.type_shift.getItemAtIndex(0));
			this.type_shift.setDisabled(true);
			this.forceable.setChecked(false);
			this.forceable.setDisabled(true);
		} else if (this.typeofbreak.getSelectedItem().getValue().toString().equals("Turno Standard")) {
			// job shift
			this.type_shift.setSelectedItem(this.type_shift.getItemAtIndex(0));
			this.type_shift.setDisabled(true);
			this.forceable.setChecked(false);
			this.forceable.setDisabled(true);
		} else if (this.typeofbreak.getSelectedItem().getValue().toString().equals("Riposo Programmato")) {
			// absence shift, forceable
			this.type_shift.setSelectedItem(this.type_shift.getItemAtIndex(1));
			this.type_shift.setDisabled(true);
			this.forceable.setChecked(true);
			this.forceable.setDisabled(true);
		} else if (!this.typeofbreak.getSelectedItem().getValue().toString().equals("Non definito")) {
			// absence shift
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

	@Listen("onChange = #type_shift")
	public void setRecordedShiftCheckbox() {
		final Comboitem item = this.type_shift.getSelectedItem();

		if (item.getValue().toString().equals(ShiftTag.ABSENCE_SHIFT)) {
			this.recorded_shift.setDisabled(false);
		} else {
			this.recorded_shift.setChecked(false);
			this.recorded_shift.setDisabled(true);
		}
	}

	@Listen("onOK = #shows_rowsShift, #full_text_searchShift")
	public void setShiftListBox() {
		List<UserShift> list_usershift = null;

		if ((this.full_text_searchShift.getValue() != null) && !this.full_text_searchShift.getValue().equals("")) {
			list_usershift = this.configurationDao.listAllShifts(this.full_text_searchShift.getValue());
		} else {
			list_usershift = this.configurationDao.loadShifts();
		}

		if ((this.shows_rowsShift.getValue() != null) && (this.shows_rowsShift.getValue() != 0)) {
			this.sw_list_shift.setPageSize(this.shows_rowsShift.getValue());
		} else {
			this.sw_list_shift.setPageSize(10);
		}

		this.sw_list_shift.setModel(new ListModelList<UserShift>(list_usershift));
	}

	@Listen("onOK = #shows_rowsTask, #full_text_searchTask")
	public void setTaskListBox() {
		List<UserTask> list_usertask = null;

		if ((this.full_text_searchTask.getValue() != null) && !this.full_text_searchTask.getValue().equals("")) {
			list_usertask = this.configurationDao.listAllTasks(this.full_text_searchTask.getValue());
		} else {
			list_usertask = this.configurationDao.loadTasks();
		}

		if ((this.shows_rowsTask.getValue() != null) && (this.shows_rowsTask.getValue() != 0)) {
			this.sw_list_task.setPageSize(this.shows_rowsTask.getValue());
		} else {
			this.sw_list_task.setPageSize(10);
		}

		this.sw_list_task.setModel(new ListModelList<UserTask>(list_usertask));
	}

	@Listen("onClick = #sw_return_defaulttask")
	public void showAbsenceTask() {
		List<UserTask> list_usertask = null;

		list_usertask = this.configurationDao.listAllAbsenceTask();

		this.sw_list_task.setModel(new ListModelList<UserTask>(list_usertask));
	}

	/**
	 * Show Bank Holiday
	 */
	private void showBankHolidays() {
		final List<String> list_bnk = Preferences.this.bank_holiday.getDays();
		Preferences.this.list_bankholiday.setModel(new ListModelList<String>(list_bnk));

	}

	@Listen("onClick = #sw_return_defaultshift")
	public void showDefaultShift() {
		List<UserShift> list_usershift = null;

		list_usershift = this.configurationDao.listAllDefaultShift();

		this.sw_list_shift.setModel(new ListModelList<UserShift>(list_usershift));
	}

	@Listen("onClick = #sw_return_hiddentask")
	public void showHiddenTask() {
		final List<UserTask> list_MobileUsertask = this.configurationDao.listAllHiddenTask();

		this.sw_list_task.setModel(new ListModelList<UserTask>(list_MobileUsertask));
	}

	@Listen("onClick = #sw_return_justificatorytask")
	public void showJustificatoryTask() {
		List<UserTask> list_usertask = null;

		list_usertask = this.configurationDao.listAllJustificatoryTask();

		this.sw_list_task.setModel(new ListModelList<UserTask>(list_usertask));
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

	@Listen("onChange = #shows_rows; onOK = #shows_rows")
	public void showRows() {
		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_billcenter.setPageSize(this.shows_rows.getValue());
		}
	}

	@Listen("onClick = #update_doc_repo")
	public void updateDocRepository() {
		final String value = this.docrepo.getValue();
		this.paramsDAO.setParam(ParamsTag.REPO_DOC, value);

		final Map<String, String> params = new HashMap();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

		Messagebox.show("Doc Repository Aggiornato con successo", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

	}

	private void updateTask(final UserTask task) {
		task.setCode(this.code_task.getValue());
		task.setDescription(this.description_task.getValue());
		task.setIsabsence(this.isabsence_task.isChecked());
		task.setJustificatory(this.justificatory_task.isChecked());
		task.setEndoperation(this.endoperation_task.isChecked());
		task.setChangeshift(this.changeshift_task.isChecked());
		task.setOverflow(this.overflow_task.isChecked());
		task.setDelayoperation(this.delayoperation_task.isChecked());
		task.setRecorded(this.recorded_task.isChecked());
		task.setHiddentask(this.hidden_task.isChecked());

		this.configurationDao.updateTask(task);

		this.refreshTaskList();

		this.resetTaskInfo();

		this.grid_task_details.setVisible(false);
	}

}
