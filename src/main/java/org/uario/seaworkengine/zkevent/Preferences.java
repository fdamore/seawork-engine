package org.uario.seaworkengine.zkevent;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.model.BillCenter;
import org.uario.seaworkengine.model.Crane;
import org.uario.seaworkengine.model.LockTable;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Service;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.model.WorkerStatus;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.IJobCost;
import org.uario.seaworkengine.platform.persistence.dao.IParams;
import org.uario.seaworkengine.platform.persistence.dao.LockTableDAO;
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
import org.zkoss.zul.Doublebox;
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
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Component			add_billcenter_command;

	@Wire
	private Component			add_service_command;

	private IBankHolidays		bank_holiday;

	private BillCenter			billCenterSelected;

	@Wire
	private Checkbox			changeshift_task;

	@Wire
	private Textbox				code_shift;

	@Wire
	private Textbox				code_status;

	@Wire
	private Textbox				code_task;

	private ConfigurationDAO	configurationDao;

	@Wire
	private Checkbox			delayoperation_task;

	@Wire
	private Textbox				description_billcenter;

	@Wire
	private Textbox				description_crane;

	@Wire
	private Textbox				description_service;

	@Wire
	private Textbox				description_shift;

	@Wire
	private Textbox				description_status;

	@Wire
	private Textbox				description_task;

	@Wire
	private Textbox				docrepo;

	@Wire
	private Checkbox			endoperation_task;

	@Wire
	private Checkbox			forceable;

	@Wire
	private Doublebox			fr_timework;

	@Wire
	private Doublebox			fr_volume;

	@Wire
	private Doublebox			fr_volume_tw_mct;

	@Wire
	private Doublebox			fr_volumeunderboard;

	@Wire
	private Doublebox			fr_volumeunderboard_sws;

	@Wire
	private Textbox				full_text_search_BillCenter;

	@Wire
	private Intbox				full_text_search_Crane;

	@Wire
	private Textbox				full_text_search_Service;

	@Wire
	private Textbox				full_text_searchShift;

	@Wire
	private Textbox				full_text_searchTask;

	@Wire
	private Component			grid_billcenter_details;

	@Wire
	private Div					grid_crane_details;

	@Wire
	private Div					grid_service_details;

	@Wire
	private Div					grid_shift_details;

	@Wire
	private Div					grid_status_details;

	@Wire
	private Div					grid_task_details;

	@Wire
	private Checkbox			hidden_task;

	@Wire
	private Checkbox			internal_task;

	@Wire
	private Checkbox			isabsence_task;

	@Wire
	private Checkbox			isPP_task;

	@Wire
	private Checkbox			isRZ;

	@Wire
	private Checkbox			isRZ_task;

	private IJobCost			jobCostDao;

	@Wire
	private Checkbox			justificatory_task;

	@Wire
	private Label				label_allocated_meomry;

	@Wire
	private Label				label_free_meomry;

	@Wire
	private Label				label_max_meomry;

	@Wire
	private Listbox				list_bankholiday;

	@Wire
	private Listbox				listlocks;

	protected LockTableDAO		locktabelDao;

	@Wire
	private Component			modify_billcenter_command;

	@Wire
	private Component			modify_service_command;

	@Wire
	private Textbox				name_crane;

	@Wire
	private Textbox				name_service;

	@Wire
	private Textbox				note_status;

	@Wire
	private Intbox				number_crane;

	private final NumberFormat	numberFormat		= NumberFormat.getInstance();

	@Wire
	private Checkbox			overflow_task;

	private IParams				paramsDAO;

	@Wire
	public Checkbox				recorded_shift;

	@Wire
	private Checkbox			recorded_task;

	private final Runtime		runtime				= Runtime.getRuntime();

	private int					selectedOptionMobileTask;

	private Service				serviceSelected;

	@Wire
	private Intbox				shows_rows;

	@Wire
	private Intbox				shows_rows_crane;

	@Wire
	private Intbox				shows_rowsShift;

	@Wire
	private Intbox				shows_rowsTask;

	private boolean				status_add			= true;

	@Wire
	private Listbox				sw_list_billcenter;

	@Wire
	private Listbox				sw_list_crane;

	@Wire
	private Listbox				sw_list_service;

	@Wire
	private Listbox				sw_list_shift;

	@Wire
	private Listbox				sw_list_status;

	@Wire
	private Listbox				sw_list_task;

	private UserTask			taskAdded;

	private UserTask			taskUpdated;

	@Wire
	private Combobox			type_shift;

	@Wire
	private Combobox			typeofbreak;

	@Listen("onClick = #status_command")
	public void actionStatus() {

		final String status = this.description_status.getValue().toString();
		final String note = this.note_status.getValue().toString();

		if (status == "") {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Inserire uno status", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			return;
		}

		if (this.status_add) {

			if (this.configurationDao.selectAllStatus().contains(new WorkerStatus(status))) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Status già presente!", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
				return;
			}

			this.configurationDao.addStatus(status, note);
		} else {
			if ((this.sw_list_status.getSelectedItem() == null) || (this.sw_list_status.getSelectedItem().getValue() == null)) {
				return;
			}

			final WorkerStatus itm = this.sw_list_status.getSelectedItem().getValue();

			// check if you change the description
			if (!itm.getDescription().equals(status)) {
				if (this.configurationDao.selectAllStatus().contains(new WorkerStatus(status))) {
					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");
					final Messagebox.Button[] buttons = new Messagebox.Button[1];
					buttons[0] = Messagebox.Button.OK;

					Messagebox.show("Status già presente!", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
					return;
				}
			}

			itm.setDescription(status);
			itm.setNote(note);
			this.configurationDao.updateStatus(itm);
		}

		this.refreshStatusList();
		this.resetStatusInfo();

		this.grid_status_details.setVisible(false);

	}

	@Listen("onClick = #add_billcenter_command")
	public void addBillCenterCommand() {
		if ((this.description_billcenter.getValue() != null) && (this.description_billcenter.getValue().trim() != "")) {
			final BillCenter billCenter = new BillCenter();
			billCenter.setDescription(this.description_billcenter.getValue());
			this.jobCostDao.createBillCenter(billCenter);
			this.refreshBillCenterList();
		}
	}

	@Listen("onClick = #add_crane_command")
	public void addCrane() {

		final List<Crane> craneList = this.configurationDao.getCrane(null, this.number_crane.getValue(), null, null);

		if (craneList.size() == 0) {
			final Crane crane = new Crane();
			crane.setName(this.name_crane.getValue());
			crane.setDescription(this.description_crane.getValue());
			crane.setNumber(this.number_crane.getValue());
			this.configurationDao.createCrane(crane);

			this.refreshCraneList();

			this.resetCraneInfo();
		} else {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Numero gru già presente", "Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);
		}

	}

	@Listen("onClick = #add_service_command")
	public void addService() {

		final List<Service> serviceList = this.configurationDao.checkServiceExist(this.name_service.getValue());

		if (serviceList.size() == 0) {

			if (this.isRZ.isChecked()) {
				final Service rzService = this.configurationDao.loadRZService();
				if (rzService != null) {
					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");
					final Messagebox.Button[] buttons = new Messagebox.Button[2];
					buttons[0] = Messagebox.Button.OK;
					buttons[1] = Messagebox.Button.CANCEL;
					Messagebox.show("Il servizio Rizzaggio è già presente, procedere?", "CONFERMA ASSEGNAZIONE RIZZAGGIO", buttons, null,
							Messagebox.EXCLAMATION, null, new EventListener() {
								@Override
								public void onEvent(final Event e) {
									if (Messagebox.ON_OK.equals(e.getName())) {
										final Service rzService = Preferences.this.configurationDao.loadRZService();
										rzService.setIsRZ(false);
										Preferences.this.configurationDao.updateService(rzService);
										final Service service = new Service();
										service.setName(Preferences.this.name_service.getValue());
										service.setDescription(Preferences.this.description_service.getValue());
										service.setIsRZ(true);
										service.setFranchise_timework(Preferences.this.fr_timework.getValue());
										service.setFranchise_volume(Preferences.this.fr_volume.getValue());
										service.setFranchise_volume_tw_mct(Preferences.this.fr_volume_tw_mct.getValue());
										service.setFranchise_volumeunderboard(Preferences.this.fr_volumeunderboard.getValue());
										service.setFranchise_volumeunderboard_sws(Preferences.this.fr_volumeunderboard_sws.getValue());
										Preferences.this.configurationDao.addService(service);
										Preferences.this.refreshServiceList();
										Preferences.this.resetServiceInfo();
									} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
										// Cancel is clicked
									}
								}
							}, params);
				}
			} else {
				final Service service = new Service();
				service.setName(Preferences.this.name_service.getValue());
				service.setDescription(Preferences.this.description_service.getValue());
				service.setIsRZ(false);
				service.setFranchise_timework(this.fr_timework.getValue());
				service.setFranchise_volume(this.fr_volume.getValue());
				service.setFranchise_volume_tw_mct(this.fr_volume_tw_mct.getValue());
				service.setFranchise_volumeunderboard(this.fr_volumeunderboard.getValue());
				service.setFranchise_volumeunderboard_sws(this.fr_volumeunderboard_sws.getValue());
				Preferences.this.configurationDao.addService(service);
				Preferences.this.refreshServiceList();
				Preferences.this.resetServiceInfo();
			}

		} else {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Nome tipo di servizio già presente", "Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);
		}

	}

	@Listen("onClick = #add_shifts_command")
	public void addShift() {

		String us_type = null;
		if (this.type_shift.getSelectedItem() != null) {
			us_type = this.type_shift.getSelectedItem().getValue();
		}

		if (this.checkIfUserCodeIsPresent(this.configurationDao.loadAllShiftCode(), this.code_shift.getValue().toUpperCase())) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Codice turno già presente.", "Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);
		} else {

			final UserShift shift = new UserShift();
			shift.setCode(this.code_shift.getValue());
			shift.setDescription(this.description_shift.getValue());

			if (us_type == null) {
				final Map<String, String> params = new HashMap<>();
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

	@Listen("onClick = #sw_addstatus")
	public void addStatusDefine() {
		this.resetStatusInfo();

		// status add
		this.status_add = true;

	}

	@Listen("onClick = #add_tasks_command")
	public void addTask() {

		this.taskAdded = new UserTask();

		// check if user code is present in table
		if (this.checkIfUserCodeIsPresent(this.configurationDao.loadAllTaskCode(), this.code_task.getValue().toUpperCase())) {
			final Map<String, String> params = new HashMap<>();
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
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.CANCEL;

				Messagebox.show(alertMessage, "CONFERMA ASSEGNAZIONE", buttons, null, Messagebox.EXCLAMATION, null, new EventListener() {
					@Override
					public void onEvent(final Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							Preferences.this.checkRZ_PPTask_add();
							return;
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							return;
						}
					}
				}, params);
			} else {
				this.checkRZ_PPTask_add();
				return;
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

	public void checkRZ_PPTask_add() {

		UserTask userTask = null;

		if (this.isRZ_task.isChecked()) {
			userTask = this.configurationDao.loadRZTask();

			if (userTask != null) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.CANCEL;

				Messagebox.show("La mansione di Rizzaggio è già presente, riassegnare?", "CONFERMA ASSEGNAZIONE", buttons, null,
						Messagebox.EXCLAMATION, null, new EventListener() {
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
									final UserTask userTask = Preferences.this.configurationDao.loadRZTask();
									userTask.setIsRZ(false);
									userTask.setIsPP(false);
									Preferences.this.configurationDao.updateTask(userTask);
									Preferences.this.taskAdded.setIsRZ(true);
									Preferences.this.taskAdded.setIsPP(false);
									Preferences.this.createTask(Preferences.this.taskAdded);
									return;

								} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
									return;
								}
							}
						}, params);
				return;
			} else {
				if (Preferences.this.selectedOptionMobileTask == 1) {
					Preferences.this.configurationDao.removeAllOverflowTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 2) {
					Preferences.this.configurationDao.removeAllEndoperationTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 3) {
					Preferences.this.configurationDao.removeAllDelayOperationTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 4) {
					Preferences.this.configurationDao.removeAllChangeshiftTasks();
				}
				Preferences.this.taskAdded.setIsRZ(this.isRZ_task.isChecked());
				Preferences.this.taskAdded.setIsPP(this.isPP_task.isChecked());
				Preferences.this.createTask(Preferences.this.taskAdded);
				return;
			}

		} else if (this.isPP_task.isChecked()) {
			userTask = this.configurationDao.loadPPTask();

			if (userTask != null) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.CANCEL;

				Messagebox.show("La mansione di Preposto Piazzale è già presente, riassegnare?", "CONFERMA ASSEGNAZIONE", buttons, null,
						Messagebox.EXCLAMATION, null, new EventListener() {
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
									final UserTask userTask = Preferences.this.configurationDao.loadPPTask();
									userTask.setIsPP(false);
									userTask.setIsRZ(false);
									Preferences.this.configurationDao.updateTask(userTask);
									Preferences.this.taskAdded.setIsPP(true);
									Preferences.this.taskAdded.setIsRZ(false);
									Preferences.this.createTask(Preferences.this.taskAdded);
									return;

								} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
									return;
								}
							}
						}, params);
				return;
			} else {
				if (Preferences.this.selectedOptionMobileTask == 1) {
					Preferences.this.configurationDao.removeAllOverflowTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 2) {
					Preferences.this.configurationDao.removeAllEndoperationTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 3) {
					Preferences.this.configurationDao.removeAllDelayOperationTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 4) {
					Preferences.this.configurationDao.removeAllChangeshiftTasks();
				}
				Preferences.this.taskAdded.setIsRZ(this.isRZ_task.isChecked());
				Preferences.this.taskAdded.setIsPP(this.isPP_task.isChecked());
				Preferences.this.createTask(Preferences.this.taskAdded);
				return;
			}
		}

		if (Preferences.this.selectedOptionMobileTask == 1) {
			Preferences.this.configurationDao.removeAllOverflowTasks();
		} else if (Preferences.this.selectedOptionMobileTask == 2) {
			Preferences.this.configurationDao.removeAllEndoperationTasks();
		} else if (Preferences.this.selectedOptionMobileTask == 3) {
			Preferences.this.configurationDao.removeAllDelayOperationTasks();
		} else if (Preferences.this.selectedOptionMobileTask == 4) {
			Preferences.this.configurationDao.removeAllChangeshiftTasks();
		}
		this.taskAdded.setIsPP(false);
		this.taskAdded.setIsRZ(false);
		Preferences.this.createTask(Preferences.this.taskAdded);

	}

	public void checkRZ_PPTask_modify() {

		UserTask userTask = null;
		final UserTask userTaskRZ = this.configurationDao.loadRZTask();
		final UserTask userTaskPP = this.configurationDao.loadPPTask();

		if (this.isRZ_task.isChecked()) {
			userTask = this.configurationDao.loadRZTask();

			if ((userTask != null) && !userTask.getId().equals(this.taskUpdated.getId()) && this.taskUpdated.getIsPP()) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Impossibile procedere, riassegnare mansione di preposto piazzale.", "Error", buttons, null, Messagebox.EXCLAMATION,
						null, null, params);
				return;
			}

			if ((userTask != null) && !userTask.getId().equals(this.taskUpdated.getId())) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.CANCEL;

				Messagebox.show("La mansione di Rizzaggio è già presente, riassegnare?", "CONFERMA ASSEGNAZIONE", buttons, null,
						Messagebox.EXCLAMATION, null, new EventListener() {
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
									final UserTask userTask = Preferences.this.configurationDao.loadRZTask();
									userTask.setIsRZ(false);
									userTask.setIsPP(false);
									Preferences.this.configurationDao.updateTask(userTask);

									if (Preferences.this.selectedOptionMobileTask == 1) {
										Preferences.this.configurationDao.removeAllOverflowTasks();
									} else if (Preferences.this.selectedOptionMobileTask == 2) {
										Preferences.this.configurationDao.removeAllEndoperationTasks();
									} else if (Preferences.this.selectedOptionMobileTask == 3) {
										Preferences.this.configurationDao.removeAllDelayOperationTasks();
									} else if (Preferences.this.selectedOptionMobileTask == 4) {
										Preferences.this.configurationDao.removeAllChangeshiftTasks();
									}
									Preferences.this.taskUpdated.setIsRZ(true);
									Preferences.this.taskUpdated.setIsPP(false);
									Preferences.this.updateTask(Preferences.this.taskUpdated);

									return;

								} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
									return;
								}
							}
						}, params);
				return;
			} else {
				if (Preferences.this.selectedOptionMobileTask == 1) {
					Preferences.this.configurationDao.removeAllOverflowTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 2) {
					Preferences.this.configurationDao.removeAllEndoperationTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 3) {
					Preferences.this.configurationDao.removeAllDelayOperationTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 4) {
					Preferences.this.configurationDao.removeAllChangeshiftTasks();
				}
				this.taskUpdated.setIsRZ(this.isRZ_task.isChecked());
				this.taskUpdated.setIsPP(this.isPP_task.isChecked());
				Preferences.this.updateTask(this.taskUpdated);
				return;
			}

		} else if (!this.isRZ_task.isChecked() && (userTaskRZ != null) && userTaskRZ.getId().equals(this.taskUpdated.getId())) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Impossibile procedere, riassegnare mansione di rizzaggio.", "Error", buttons, null, Messagebox.EXCLAMATION, null, null,
					params);
			this.isRZ_task.setChecked(true);
			this.isPP_task.setChecked(false);
			return;
		} else if (this.isPP_task.isChecked()) {
			userTask = this.configurationDao.loadPPTask();

			if ((userTask != null) && !userTask.getId().equals(this.taskUpdated.getId()) && this.taskUpdated.getIsRZ()) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Impossibile procedere, riassegnare mansione di rizzaggio.", "Error", buttons, null, Messagebox.EXCLAMATION, null,
						null, params);

				return;
			}

			if ((userTask != null) && !userTask.getId().equals(this.taskUpdated.getId())) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.CANCEL;

				Messagebox.show("La mansione di Preposto Piazzale è già presente, riassegnare?", "CONFERMA ASSEGNAZIONE", buttons, null,
						Messagebox.EXCLAMATION, null, new EventListener() {
							@Override
							public void onEvent(final Event e) {
								if (Messagebox.ON_OK.equals(e.getName())) {
									final UserTask userTask = Preferences.this.configurationDao.loadPPTask();
									userTask.setIsRZ(false);
									userTask.setIsPP(false);
									Preferences.this.configurationDao.updateTask(userTask);

									if (Preferences.this.selectedOptionMobileTask == 1) {
										Preferences.this.configurationDao.removeAllOverflowTasks();
									} else if (Preferences.this.selectedOptionMobileTask == 2) {
										Preferences.this.configurationDao.removeAllEndoperationTasks();
									} else if (Preferences.this.selectedOptionMobileTask == 3) {
										Preferences.this.configurationDao.removeAllDelayOperationTasks();
									} else if (Preferences.this.selectedOptionMobileTask == 4) {
										Preferences.this.configurationDao.removeAllChangeshiftTasks();
									}
									Preferences.this.taskUpdated.setIsRZ(false);
									Preferences.this.taskUpdated.setIsPP(true);
									Preferences.this.updateTask(Preferences.this.taskUpdated);
									return;

								} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
									return;
								}
							}
						}, params);
				return;
			} else {
				if (Preferences.this.selectedOptionMobileTask == 1) {
					Preferences.this.configurationDao.removeAllOverflowTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 2) {
					Preferences.this.configurationDao.removeAllEndoperationTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 3) {
					Preferences.this.configurationDao.removeAllDelayOperationTasks();
				} else if (Preferences.this.selectedOptionMobileTask == 4) {
					Preferences.this.configurationDao.removeAllChangeshiftTasks();
				}
				this.taskUpdated.setIsRZ(this.isRZ_task.isChecked());
				this.taskUpdated.setIsPP(this.isPP_task.isChecked());
				this.updateTask(this.taskUpdated);
				return;
			}
		} else if (!this.isPP_task.isChecked() && (userTaskPP != null) && userTaskPP.getId().equals(this.taskUpdated.getId())) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Impossibile procedere, riassegnare mansione di preposto piazzale.", "Error", buttons, null, Messagebox.EXCLAMATION, null,
					null, params);
			this.isPP_task.setChecked(true);
			this.isRZ_task.setChecked(false);
			return;
		}

		if (Preferences.this.selectedOptionMobileTask == 1) {
			Preferences.this.configurationDao.removeAllOverflowTasks();
		} else if (Preferences.this.selectedOptionMobileTask == 2) {
			Preferences.this.configurationDao.removeAllEndoperationTasks();
		} else if (Preferences.this.selectedOptionMobileTask == 3) {
			Preferences.this.configurationDao.removeAllDelayOperationTasks();
		} else if (Preferences.this.selectedOptionMobileTask == 4) {
			Preferences.this.configurationDao.removeAllChangeshiftTasks();
		}
		this.taskUpdated.setIsRZ(this.isRZ_task.isChecked());
		this.taskUpdated.setIsPP(this.isPP_task.isChecked());
		this.updateTask(this.taskUpdated);

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
		task.setInternal(this.internal_task.isChecked());

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

	@Listen("onClick = #sw_link_deletecrane")
	public void deleteCrane() {
		if (this.sw_list_crane.getSelectedItem() != null) {
			final Crane craneSelected = this.sw_list_crane.getSelectedItem().getValue();
			this.configurationDao.removeCrane(craneSelected.getId());
			this.refreshCraneList();
		}

	}

	@Listen("onClick = #sw_link_deleteservice")
	public void deleteService() {
		if (this.sw_list_service.getSelectedItem() != null) {
			final Service serviceSelected = this.sw_list_service.getSelectedItem().getValue();

			if (!serviceSelected.getIsRZ()) {
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
									Preferences.this.configurationDao.removeService(serviceSelected.getId());
									Preferences.this.refreshServiceList();
								} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
									// Cancel is clicked
								}
							}
						}, params);
			} else {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Impossibile cancellare il servizio di Rizzaggio", "Error", buttons, null, Messagebox.EXCLAMATION, null, null,
						params);
			}

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
				Preferences.this.locktabelDao = (LockTableDAO) SpringUtil.getBean(BeansTag.LOCK_TABLE_DAO);

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

				// refresh service list
				Preferences.this.refreshServiceList();
				Preferences.this.resetServiceInfo();

				// refresh crane list
				Preferences.this.refreshCraneList();

				// show bank holiday
				Preferences.this.showBankHolidays();

				// define list block
				Preferences.this.showBlocks();

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

	public Listbox getListlocks() {
		return this.listlocks;
	}

	@Listen("onClick = #sw_return_mobiletask")
	public void getMobileTask() {
		final List<UserTask> list_MobileUsertask = this.configurationDao.listSpecialTaskMobile();

		this.sw_list_task.setModel(new ListModelList<>(list_MobileUsertask));
	}

	@Listen("onClick = #sw_return_RZ_PP")
	public void getRZandPPTask() {
		final List<UserTask> list = new ArrayList<>();

		final UserTask rz = this.configurationDao.loadRZTask();
		final UserTask pp = this.configurationDao.loadPPTask();
		list.add(rz);
		list.add(pp);

		this.sw_list_task.setModel(new ListModelList<>(list));
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

	@Listen("onClick = #modify_crane_command")
	public void modifyCraneCommand() {
		if ((this.sw_list_crane.getSelectedItem() == null) || (this.number_crane.getValue() == null)) {
			return;
		}

		final Crane craneSelected = this.sw_list_crane.getSelectedItem().getValue();

		final List<Crane> list = this.configurationDao.getCrane(null, this.number_crane.getValue(), null, null);

		if (list.size() != 0) {

			final Crane crane = list.get(0);

			if (crane.getId() != craneSelected.getId()) {

				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Numero gru già presente", "Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);

				return;
			}
		}

		craneSelected.setDescription(this.description_crane.getValue());
		craneSelected.setName(this.name_crane.getValue());
		craneSelected.setNumber(this.number_crane.getValue());

		this.configurationDao.updateCrane(craneSelected);

		this.refreshCraneList();

		this.resetCraneInfo();

		this.grid_crane_details.setVisible(false);

	}

	@Listen("onClick=#sw_link_modifyservice")
	public void modifyService() {
		if (this.sw_list_service.getSelectedItem() != null) {
			this.serviceSelected = this.sw_list_service.getSelectedItem().getValue();

			this.name_service.setValue(this.serviceSelected.getName());
			this.description_service.setValue(this.serviceSelected.getDescription());
			this.isRZ.setChecked(this.serviceSelected.getIsRZ());
			this.fr_timework.setValue(this.serviceSelected.getFranchise_timework());
			this.fr_volume.setValue(this.serviceSelected.getFranchise_volume());
			this.fr_volume_tw_mct.setValue(this.serviceSelected.getFranchise_volume_tw_mct());
			this.fr_volumeunderboard.setValue(this.serviceSelected.getFranchise_volumeunderboard());
			this.fr_volumeunderboard_sws.setValue(this.serviceSelected.getFranchise_volumeunderboard_sws());

			this.add_service_command.setVisible(false);
			this.modify_service_command.setVisible(true);
		}
	}

	@Listen("onClick=#modify_service_command")
	public void modifyServiceCommand() {
		if (this.serviceSelected == null) {
			return;
		}

		final Service service = this.configurationDao.loadRZService();

		if (this.isRZ.isChecked() && (service != null) && !service.getId().equals(this.serviceSelected.getId())) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[2];
			buttons[0] = Messagebox.Button.OK;
			buttons[1] = Messagebox.Button.CANCEL;
			Messagebox.show("Il servizio Rizzaggio è già presente, procedere?", "CONFERMA ASSEGNAZIONE RIZZAGGIO", buttons, null,
					Messagebox.EXCLAMATION, null, new EventListener() {
						@Override
						public void onEvent(final Event e) {
							if (Messagebox.ON_OK.equals(e.getName())) {
								final Service rzService = Preferences.this.configurationDao.loadRZService();
								rzService.setIsRZ(false);
								Preferences.this.configurationDao.updateService(rzService);
								Preferences.this.serviceSelected.setDescription(Preferences.this.description_service.getValue());
								Preferences.this.serviceSelected.setName(Preferences.this.name_service.getValue());
								Preferences.this.serviceSelected.setFranchise_timework(Preferences.this.fr_timework.getValue());
								Preferences.this.serviceSelected.setFranchise_volume(Preferences.this.fr_volume.getValue());
								Preferences.this.serviceSelected.setFranchise_volume_tw_mct(Preferences.this.fr_volume_tw_mct.getValue());
								Preferences.this.serviceSelected.setFranchise_volumeunderboard(Preferences.this.fr_volumeunderboard.getValue());
								Preferences.this.serviceSelected
										.setFranchise_volumeunderboard_sws(Preferences.this.fr_volumeunderboard_sws.getValue());
								Preferences.this.serviceSelected.setIsRZ(Preferences.this.isRZ.isChecked());
								Preferences.this.configurationDao.updateService(Preferences.this.serviceSelected);
								Preferences.this.resetServiceInfo();
								Preferences.this.refreshServiceList();
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								// Cancel is clicked
							}
						}
					}, params);
		} else if (!this.isRZ.isChecked() && (service != null) && service.getId().equals(this.serviceSelected.getId())) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Riassegnare il servizio di rizzaggio!", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			this.isRZ.setChecked(true);

			return;
		} else {

			this.serviceSelected.setDescription(this.description_service.getValue());
			this.serviceSelected.setName(this.name_service.getValue());
			this.serviceSelected.setIsRZ(this.isRZ.isChecked());
			this.serviceSelected.setFranchise_timework(this.fr_timework.getValue());
			this.serviceSelected.setFranchise_volume(this.fr_volume.getValue());
			this.serviceSelected.setFranchise_volume_tw_mct(this.fr_volume_tw_mct.getValue());
			this.serviceSelected.setFranchise_volumeunderboard(this.fr_volumeunderboard.getValue());
			this.serviceSelected.setFranchise_volumeunderboard_sws(this.fr_volumeunderboard_sws.getValue());
			this.configurationDao.updateService(this.serviceSelected);
			this.refreshServiceList();
			this.resetServiceInfo();
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
			final Map<String, String> params = new HashMap<>();
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
			if ((isDefault && (this.typeofbreak.getSelectedItem() != null) && !this.typeofbreak.getSelectedItem().getValue().equals("Non definito"))
					|| !isDefault) {
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
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show(
						"Non puoi modificare un turno di default associandogli un valore non definito, puoi solo modificare il tipo di default.",
						"Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			}

		}

	}

	@Listen("onClick = #sw_link_modifystatus")
	public void modifyStatus() {

		if (this.sw_list_status.getSelectedItem() == null) {
			return;
		}

		final WorkerStatus itm = this.sw_list_status.getSelectedItem().getValue();
		final String status = itm.getDescription();

		this.description_status.setValue(status);
		this.note_status.setValue(itm.getNote());

		if (status.equals(UserStatusTag.FIRED) || status.equals(UserStatusTag.SUSPENDED) || status.equals(UserStatusTag.OPEN)) {
			this.description_status.setDisabled(true);
		} else {
			this.description_status.setDisabled(false);
		}

		this.status_add = false;
		this.grid_status_details.setVisible(true);

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
		this.internal_task.setChecked(task.getInternal());
		this.isRZ_task.setChecked(task.getIsRZ());
		this.isPP_task.setChecked(task.getIsPP());

	}

	@Listen("onClick = #modify_tasks_command")
	public void modifyTaskCommand() {

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		this.taskUpdated = this.sw_list_task.getSelectedItem().getValue();
		final List<String> listTaskCode = this.configurationDao.loadAllTaskCode();
		if (this.taskUpdated.getCode().toUpperCase().equals(this.code_task.getValue().toUpperCase())) {
			listTaskCode.remove(this.taskUpdated.getCode());
		}

		if (this.checkIfUserCodeIsPresent(listTaskCode, this.code_task.getValue().toUpperCase())) {
			final Map<String, String> params = new HashMap<>();
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

			if (this.overflow_task.isChecked() && (actualOverFlowTask != null) && !actualOverFlowTask.getId().equals(this.taskUpdated.getId())) {
				alertMessage = "Mansione di Esubero per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 1;
			}

			if (this.endoperation_task.isChecked() && (actualEndOperationTask != null)
					&& !actualEndOperationTask.getId().equals(this.taskUpdated.getId())) {
				alertMessage = "Mansione di Fine operazione per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 2;
			}

			if (this.delayoperation_task.isChecked() && (actualDelayOperationTask != null)
					&& !actualDelayOperationTask.getId().equals(this.taskUpdated.getId())) {
				alertMessage = "Mansione di Ritado per app mobile già presente, continuare?";
				this.selectedOptionMobileTask = 3;
			}

			if (this.changeshift_task.isChecked() && (actualChangeshiftTask != null)
					&& !actualChangeshiftTask.getId().equals(this.taskUpdated.getId())) {
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
							Preferences.this.checkRZ_PPTask_modify();
							return;
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							return;
						}
					}
				}, params);
			} else {
				this.checkRZ_PPTask_modify();
			}

		}

	}

	@Listen("onClick = #sw_refresh_billcenterlist, #billcenter")
	public void refreshBillCenterList() {

		this.description_billcenter.setValue("");

		this.full_text_search_BillCenter.setValue("");

		final List<BillCenter> listBillCenter = this.jobCostDao.listAllBillCenter(null);

		if (listBillCenter != null) {
			this.sw_list_billcenter.setModel(new ListModelList<>(listBillCenter));
		}

	}

	@Listen("onClick = #sw_refresh_crane_list")
	public void refreshCraneList() {

		final List<Crane> list = this.configurationDao.getCrane(null, null, null, null);
		Preferences.this.sw_list_crane.setModel(new ListModelList<>(list));

		if ((this.shows_rows_crane.getValue() != null) && (this.shows_rows_crane.getValue() != 0)) {
			this.sw_list_crane.setPageSize(this.shows_rows_crane.getValue());
		} else {
			this.sw_list_crane.setPageSize(10);
			this.shows_rows_crane.setValue(10);
		}

	}

	@Listen("onClick = #sw_refresh_service_list")
	public void refreshServiceList() {

		this.full_text_search_Service.setValue(null);

		final List<Service> list = this.configurationDao.selectService(null, null, null);
		Preferences.this.sw_list_service.setModel(new ListModelList<>(list));

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
		Preferences.this.sw_list_shift.setModel(new ListModelList<>(list));

		this.full_text_searchShift.setValue(null);

	}

	@Listen("onClick = #sw_refresh_status_list")
	public void refreshStatusList() {
		final List<WorkerStatus> list = this.configurationDao.selectAllStatus();
		Preferences.this.sw_list_status.setModel(new ListModelList<>(list));

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
		Preferences.this.sw_list_task.setModel(new ListModelList<>(list));

		this.full_text_searchTask.setValue(null);
	}

	@Listen("onClick = #removeblock")
	public void removeBlock() {
		if (this.listlocks.getSelectedItem() == null) {
			return;
		}

		final LockTable itm = this.listlocks.getSelectedItem().getValue();
		if (itm == null) {
			return;
		}

		final Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		itm.setTime_to(new Timestamp(Calendar.getInstance().getTime().getTime()));
		itm.setId_user_closer(person.getId());
		this.locktabelDao.updateLockTable(itm);

		// reload list block
		this.showBlocks();

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
			final Map<String, String> params = new HashMap<>();
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

		if (this.sw_list_task.getSelectedItem() == null) {
			return;
		}

		final UserTask task = this.sw_list_task.getSelectedItem().getValue();

		if (!task.getIsPP() && !task.getIsRZ()) {
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
		} else {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Impossibile procedere, mansione speciale.", "Error", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			return;
		}
	}

	private void resetCraneInfo() {
		this.description_crane.setValue(null);
		this.name_crane.setValue(null);
		this.number_crane.setValue(null);
	}

	private void resetServiceInfo() {
		this.description_service.setValue(null);
		this.name_service.setValue(null);
		this.isRZ.setChecked(false);
		this.fr_timework.setValue(0.0);
		this.fr_volume.setValue(0.0);
		this.fr_volume_tw_mct.setValue(0.0);
		this.fr_volumeunderboard.setValue(0.0);
		this.fr_volumeunderboard_sws.setValue(0.0);
		this.grid_service_details.setVisible(false);
		this.add_service_command.setVisible(true);
		this.modify_service_command.setVisible(false);
	}

	private void resetShiftInfo() {
		this.code_shift.setValue("");
		this.description_shift.setValue("");
		this.type_shift.setSelectedItem(null);
		this.typeofbreak.setSelectedIndex(0);
	}

	private void resetStatusInfo() {
		this.description_status.setValue(null);
		this.description_status.setDisabled(false);
		this.note_status.setValue(null);

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
		this.internal_task.setChecked(false);
		this.isRZ_task.setChecked(false);
		this.isPP_task.setChecked(false);

	}

	@Listen("onChange = #full_text_search_BillCenter; onOK = #full_text_search_BillCenter")
	public void searchBillCenterText() {
		if (this.full_text_search_BillCenter.getValue() != null) {
			final List<BillCenter> listBillCenter = this.jobCostDao.listAllBillCenter(this.full_text_search_BillCenter.getValue());

			if (listBillCenter != null) {
				this.sw_list_billcenter.setModel(new ListModelList<>(listBillCenter));
			}
		}
	}

	@Listen("onChange = #full_text_search_Crane; onOK = #full_text_search_Crane")
	public void searchCrane() {
		if (this.full_text_search_Crane.getValue() != null) {
			final List<Crane> list = this.configurationDao.getCrane(null, this.full_text_search_Crane.getValue(), null, null);

			if (list != null) {
				this.sw_list_crane.setModel(new ListModelList<>(list));
			}
		} else {
			this.refreshCraneList();
		}
	}

	@Listen("onChange = #full_text_search_Service; onOK = #full_text_search_Service")
	public void searchService() {
		if (this.full_text_search_Service.getValue() != null) {
			final List<Service> list = this.configurationDao.selectService(null, this.full_text_search_Service.getValue(), null);

			if (list != null) {
				this.sw_list_service.setModel(new ListModelList<>(list));
			}
		}
	}

	@Listen("onClick = #selectRZ")
	public void selectRZService() {
		this.full_text_search_Service.setValue(null);

		final List<Service> list = new ArrayList<>();
		final Service service = this.configurationDao.loadRZService();
		list.add(service);

		Preferences.this.sw_list_service.setModel(new ListModelList<>(list));
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

	public void setListlocks(final Listbox listlocks) {
		this.listlocks = listlocks;
	}

	@Listen("onClick = #sw_link_modifycrane")
	public void setModifyCranePanel() {
		if (this.sw_list_crane.getSelectedItem() == null) {
			return;
		}

		final Crane craneSelected = this.sw_list_crane.getSelectedItem().getValue();
		if (craneSelected == null) {
			return;
		}

		this.name_crane.setValue(craneSelected.getName());
		this.description_crane.setValue(craneSelected.getDescription());
		this.number_crane.setValue(craneSelected.getNumber());
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

		this.sw_list_shift.setModel(new ListModelList<>(list_usershift));
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

		this.sw_list_task.setModel(new ListModelList<>(list_usertask));
	}

	@Listen("onClick = #sw_return_defaulttask")
	public void showAbsenceTask() {
		List<UserTask> list_usertask = null;

		list_usertask = this.configurationDao.listAllAbsenceTask();

		this.sw_list_task.setModel(new ListModelList<>(list_usertask));
	}

	/**
	 * Show Bank Holiday
	 */
	private void showBankHolidays() {
		final List<String> list_bnk = Preferences.this.bank_holiday.getDays();
		Preferences.this.list_bankholiday.setModel(new ListModelList<>(list_bnk));

	}

	/**
	 * Show Blocks
	 */
	@Listen("onClick = #refresh_listlocks")
	public void showBlocks() {

		final List<LockTable> list_bnk = this.locktabelDao.loadOpenLockTable();
		this.listlocks.setModel(new ListModelList<>(list_bnk));

	}

	@Listen("onClick = #sw_return_defaultshift")
	public void showDefaultShift() {
		List<UserShift> list_usershift = null;

		list_usershift = this.configurationDao.listAllDefaultShift();

		this.sw_list_shift.setModel(new ListModelList<>(list_usershift));
	}

	@Listen("onClick = #sw_return_hiddentask")
	public void showHiddenTask() {
		final List<UserTask> list_MobileUsertask = this.configurationDao.listAllHiddenTask();

		this.sw_list_task.setModel(new ListModelList<>(list_MobileUsertask));
	}

	@Listen("onClick = #sw_return_internaltask")
	public void showInternalTask() {
		final List<UserTask> list_InternalUsertask = this.configurationDao.loadInternalTask();

		this.sw_list_task.setModel(new ListModelList<>(list_InternalUsertask));
	}

	@Listen("onClick=#sw_return_recorded")
	public void showInvoiceTask() {
		final List<UserTask> list_InternalUsertask = this.configurationDao.loadRecordedTask();

		this.sw_list_task.setModel(new ListModelList<>(list_InternalUsertask));
	}

	@Listen("onClick = #sw_return_justificatorytask")
	public void showJustificatoryTask() {
		List<UserTask> list_usertask = null;

		list_usertask = this.configurationDao.listAllJustificatoryTask();

		this.sw_list_task.setModel(new ListModelList<>(list_usertask));
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

	@Listen("onClick = #sw_return_recordedshift")
	public void showRecordedShift() {

		this.sw_list_shift.setModel(new ListModelList<>(this.configurationDao.listRecordedShift()));
	}

	@Listen("onChange = #shows_rows; onOK = #shows_rows")
	public void showRows() {
		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_billcenter.setPageSize(this.shows_rows.getValue());
		}
	}

	@Listen("onOK=#shows_rows_crane")
	public void showRowsCraneList() {
		if ((this.shows_rows_crane.getValue() != null) && (this.shows_rows_crane.getValue() != 0)) {
			this.sw_list_crane.setPageSize(this.shows_rows_crane.getValue());
		} else {
			this.sw_list_crane.setPageSize(10);
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
		task.setInternal(this.internal_task.isChecked());

		this.configurationDao.updateTask(task);

		this.refreshTaskList();

		this.resetTaskInfo();

		this.grid_task_details.setVisible(false);
	}

}
