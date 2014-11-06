package org.uario.seaworkengine.zkevent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.model.DaySchedule;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ShiftTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.uario.seaworkengine.zkevent.bean.RowDaySchedule;
import org.uario.seaworkengine.zkevent.bean.RowSchedule;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

public class SchedulerComposer extends SelectorComposer<Component> {

	private static final int				DAYS_BEFORE_TODAY_IN_PROGRAM	= -1;

	private static final int				DAYS_IN_GRID_PREPROCESSING		= 31;

	private static final int				DAYS_IN_GRID_PROGRAM			= 5;

	private static final int				DAYS_TO_SHOW_IN_REVIEW			= 2;

	// format
	private static final SimpleDateFormat	formatter_dd					= new SimpleDateFormat("dd");

	private static final SimpleDateFormat	formatter_ddmmm					= new SimpleDateFormat("dd/MMM");
	private static final SimpleDateFormat	formatter_e						= new SimpleDateFormat("E");
	private static final SimpleDateFormat	formatter_eeee					= new SimpleDateFormat("EEEE");
	private static final SimpleDateFormat	formatter_scheduler_info		= new SimpleDateFormat("EEEE dd MMM");
	/**
	 *
	 */
	private static final long				serialVersionUID				= 1L;
	private ConfigurationDAO				configurationDAO;
	private Schedule						currentSchedule;

	@Wire
	private Datebox							date_init_scheduler;

	@Wire
	private Datebox							date_init_scheduler_review;

	@Wire
	private Popup							day_definition_popup;

	@Wire
	private Spinner							days_after_config;

	/**
	 * First date in grid
	 */
	private Date							firstDateInGrid;

	@Wire
	private Listbox							grid_scheduler;

	@Wire
	private Listbox							grid_scheduler_day;

	@Wire
	private Listbox							grid_scheduler_review;

	@Wire
	private A								label_date_popup;

	// initial program and revision
	private List<DetailInitialSchedule>		list_details_program;
	private List<DetailFinalSchedule>		list_details_review;

	@Wire
	private Listbox							listbox_program;

	@Wire
	private Listbox							listbox_review;

	private final Logger					logger							= Logger.getLogger(SchedulerComposer.class);

	@Wire
	private Textbox							note;

	private PersonDAO						personDAO;

	@Wire
	private Div								preprocessing_div;

	@Wire
	private Comboitem						preprocessing_item;

	@Wire
	private Div								program_div;

	@Wire
	private Comboitem						program_item;

	@Wire
	private Combobox						program_task;

	@Wire
	private Intbox							program_time;

	@Wire
	private Div								review_div;

	@Wire
	private Comboitem						review_item;

	@Wire
	private Combobox						review_task;

	@Wire
	private Intbox							review_time;

	private ISchedule						scheduleDAO;

	@Wire
	private A								scheduler_label;

	@Wire
	private Combobox						scheduler_type_selector;

	// selected day
	private Integer							selectedDay;

	// selected shift
	private Integer							selectedShift;

	/**
	 * User selected to schedule
	 */
	private Integer							selectedUser;

	private IShiftCache						shift_cache;

	@Wire
	private Popup							shift_definition_popup;

	@Wire
	private Popup							shift_definition_popup_review;

	@Wire
	private Combobox						shift_popup;

	private TasksDAO						taskDAO;

	@Wire
	private Datebox							time_from;

	@Wire
	private Datebox							time_to;

	@Listen("onClick= #add_program_item")
	public void addProgramItem() {

		if (this.list_details_program == null) {
			return;
		}

		if (this.selectedShift == null) {
			return;
		}

		if (this.program_task.getSelectedItem() == null) {
			// Messagebox.show("Assegnare una mansione all'utente selezionato, prima di procedere alla programmazione",
			// "INFO", Messagebox.OK,Messagebox.EXCLAMATION);
			return;
		}

		final UserTask task = this.program_task.getSelectedItem().getValue();
		if (task == null) {
			// Messagebox.show("Assegna una mansione", "INFO", Messagebox.OK,
			// Messagebox.EXCLAMATION);
			return;
		}

		final Integer time = this.program_time.getValue();

		if (time == null) {
			// Messagebox.show("Definire il numero di ore da lavorare", "INFO",
			// Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		// check about sum of time
		boolean check_sum = true;
		if (time > 6) {
			check_sum = false;
		}
		if (this.list_details_program.size() != 0) {
			int sum = time;
			for (final DetailInitialSchedule detail : this.list_details_program) {
				final int current_time = detail.getTime();
				sum = sum + current_time;
				if (sum > 6) {
					check_sum = false;
					break;
				}
			}
		}
		if (!check_sum) {
			// Messagebox.show("Non si possono assegnare più di sei ore per turno",
			// "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentScheduler();
		}

		final DetailInitialSchedule new_item = new DetailInitialSchedule();
		new_item.setId_schedule(this.currentSchedule.getId());
		new_item.setShift(this.selectedShift);
		new_item.setTime(time);
		new_item.setTask(task.getId());

		// update program list
		this.list_details_program.add(new_item);
		final ListModelList<DetailInitialSchedule> model = new ListModelList<DetailInitialSchedule>(this.list_details_program);
		model.setMultiple(true);
		this.listbox_program.setModel(model);

	}

	@Listen("onClick= #add_review_item")
	public void addReviewItem() {

		if (this.list_details_review == null) {
			return;
		}

		if (this.selectedShift == null) {
			return;
		}

		if (this.review_task.getSelectedItem() == null) {
			// Messagebox.show("Assegnare una mansione all'utente selezionato, prima di procedere alla consuntivazione",
			// "INFO", Messagebox.OK,Messagebox.EXCLAMATION);
			return;
		}

		final UserTask task = this.review_task.getSelectedItem().getValue();
		if (task == null) {
			// Messagebox.show("Assegna una mansione", "INFO", Messagebox.OK,
			// Messagebox.EXCLAMATION);
			return;
		}

		final Integer time = this.review_time.getValue();

		if (time == null) {
			// Messagebox.show("Definire il numero di ore lavorate", "INFO",
			// Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		// check about sum of time
		boolean check_sum = true;
		if (time > 6) {
			check_sum = false;
		}
		if (this.list_details_review.size() != 0) {
			int sum = time;
			for (final DetailFinalSchedule detail : this.list_details_review) {
				final int current_time = detail.getTime();
				sum = sum + current_time;
				if (sum > 6) {
					check_sum = false;
					break;
				}
			}
		}
		if (!check_sum) {
			// Messagebox.show("Non si possono assegnare più di sei ore per turno",
			// "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentScheduler();
		}

		final DetailFinalSchedule new_item = new DetailFinalSchedule();
		new_item.setId_schedule(this.currentSchedule.getId());
		new_item.setShift(this.selectedShift);
		new_item.setTime(time);
		new_item.setTask(task.getId());
		new_item.setTime_from(this.time_from.getValue());
		new_item.setTime_to(this.time_to.getValue());

		// update program list
		this.list_details_review.add(new_item);
		final ListModelList<DetailFinalSchedule> model = new ListModelList<DetailFinalSchedule>(this.list_details_review);
		model.setMultiple(true);
		this.listbox_review.setModel(model);

	}

	/**
	 * Define bheavior for day configuration
	 *
	 * @param data_info
	 */
	protected void dayConfigurator(final String data_info) {

		this.days_after_config.setValue(null);

		// set info day;
		this.selectedDay = Integer.parseInt(data_info);
		if (this.selectedDay == null) {
			return;
		}
		final Date current_day = this.getDateScheduled(this.selectedDay);
		String msg = "" + SchedulerComposer.formatter_scheduler_info.format(current_day);

		// get user
		if (this.grid_scheduler_day.getSelectedItem() != null) {
			final RowDaySchedule row = this.grid_scheduler_day.getSelectedItem().getValue();
			final String name = row.getName_user();

			msg = name + ". " + msg;

		}

		this.label_date_popup.setLabel(msg);
		this.day_definition_popup.open(this.grid_scheduler_day, "after_pointer");
	}

	/**
	 * Define anchor content on shift schedule
	 *
	 * @param program
	 * @param schedule
	 * @return
	 */
	private String defineAnchorContent(final boolean program, final Schedule schedule) {
		Integer time = null;
		if (program) {
			time = schedule.getProgram_time();
		} else {
			time = schedule.getRevision_time();
		}

		if (time != null) {
			return "" + time;
		}

		return null;

	}

	@Listen("onChange = #scheduler_type_selector, #date_init_scheduler, #date_init_scheduler_review;onClick = #refresh_command, #refresh_command_review")
	public void defineSchedulerType() {

		if (this.scheduler_type_selector.getSelectedItem() == null) {
			return;
		}

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected == this.preprocessing_item) {
			this.preprocessing_div.setVisible(true);
			this.program_div.setVisible(false);
			this.review_div.setVisible(false);

			// set initial structure for program
			this.setGridStructureForDay(SchedulerComposer.this.date_init_scheduler.getValue());
			this.setupGlobalSchedulerGridForDay();
		}

		if (selected == this.program_item) {
			this.preprocessing_div.setVisible(false);
			this.program_div.setVisible(true);
			this.review_div.setVisible(false);

			// set initial structure for program
			this.setGridStructureForShift();
			this.setupGlobalSchedulerGridForShift();
		}

		if (selected == this.review_item) {
			this.preprocessing_div.setVisible(false);
			this.program_div.setVisible(false);
			this.review_div.setVisible(true);

			// set initial structure for program
			this.setGridStructureForShiftReview(SchedulerComposer.this.date_init_scheduler_review.getValue());
			this.setupGlobalSchedulerGridForShiftReview();
		}

	}

	/**
	 * Define user availability
	 *
	 * @return
	 */
	private HashMap<Integer, String> defineUserAvailability() {

		// the day for define user availability si to morrow
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		// define info about day scheduled - define person available
		final List<DaySchedule> day_schedule_list = this.scheduleDAO.loadDaySchedule(calendar.getTime());

		final HashMap<Integer, String> map_status = new HashMap<Integer, String>();
		for (final DaySchedule day_schedule : day_schedule_list) {
			final Integer id_user = day_schedule.getId_user();
			if (map_status.containsKey(id_user)) {
				continue;
			}

			String status = ShiftTag.USER_WORKER_AVAILABLE;

			final Integer id_shift = day_schedule.getShift();
			final UserShift shift = this.shift_cache.getUserShift(id_shift);
			final boolean forzable = shift.getForceable();
			final boolean presence = shift.getPresence();
			if (presence) {
				status = ShiftTag.USER_WORKER_AVAILABLE;
			} else if (!presence && forzable) {
				status = ShiftTag.USER_WORKER_FORZABLE;
			}

			else if (!presence && !forzable) {
				status = ShiftTag.USER_WORKER_NOT_AVAILABLE;
			}

			map_status.put(id_user, status);

		}
		return map_status;
	}

	@Override
	public void doFinally() throws Exception {

		// select initial value for initial date
		this.date_init_scheduler.setValue(Calendar.getInstance().getTime());
		this.date_init_scheduler_review.setValue(Calendar.getInstance().getTime());

		this.scheduleDAO = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);
		this.taskDAO = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);
		this.personDAO = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);
		this.configurationDAO = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);

		this.getSelf().addEventListener(ZkEventsTag.onShowScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// set preprocessing item in combo selection
				SchedulerComposer.this.scheduler_type_selector.setSelectedItem(SchedulerComposer.this.preprocessing_item);

				// define shift combo
				final List<UserShift> shifts = SchedulerComposer.this.configurationDAO.loadShifts();
				SchedulerComposer.this.shift_popup.setModel(new ListModelList<UserShift>(shifts));

				// get the shift cache
				SchedulerComposer.this.shift_cache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);

				// set initial structure for program
				SchedulerComposer.this.setGridStructureForDay(SchedulerComposer.this.date_init_scheduler.getValue());
				SchedulerComposer.this.setupGlobalSchedulerGridForDay();

			}
		});

		// SHOW SHIFT CONFIGURATOR
		this.getSelf().addEventListener(ZkEventsTag.onShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final String data_info = arg0.getData().toString();

				// configure shift
				SchedulerComposer.this.shiftConfigurator(data_info);

			}

		});

		// SHOW SHIFT CONFIGURATOR
		this.getSelf().addEventListener(ZkEventsTag.onShiftClickReview, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final String data_info = arg0.getData().toString();

				// configure shift
				SchedulerComposer.this.shiftConfiguratorReview(data_info);

			}

		});

		// SHOW DAY CONFIGURATOR
		this.getSelf().addEventListener(ZkEventsTag.onDayClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final String data_info = arg0.getData().toString();

				// configure shift
				SchedulerComposer.this.dayConfigurator(data_info);

			}

		});

	}

	/**
	 * Get date scheduled
	 *
	 * @param day
	 * @return
	 */
	private Date getDateScheduled(final Integer day) {
		final int to_add = day - 1;

		final Calendar calendar_day = Calendar.getInstance();
		calendar_day.setTime(this.firstDateInGrid);
		calendar_day.add(Calendar.DAY_OF_YEAR, to_add);

		final Date ret = calendar_day.getTime();
		return DateUtils.truncate(ret, Calendar.DATE);
	}

	/**
	 * return the day of this schedule on calendar
	 *
	 * @param initial_date
	 * @param schedule
	 * @return
	 */
	private int getDayOfSchedule(final Date schedule_date) {

		if (schedule_date == null) {
			// if not date scheduler, put it at first day
			return 1;
		}

		final Date date_init_truncate = DateUtils.truncate(this.firstDateInGrid, Calendar.DATE);
		final Date schedule_date_truncate = DateUtils.truncate(schedule_date, Calendar.DATE);

		final long millis = schedule_date_truncate.getTime() - date_init_truncate.getTime();
		final long day_elapsed = millis / (1000 * 60 * 60 * 24);

		return (int) (day_elapsed + 1);

	}

	/**
	 * Get a new Item Row from a schedule
	 *
	 * @param schedule
	 */
	private ItemRowSchedule getItemRowSchedule(final RowSchedule currentRow, final Integer day_on_current_calendar, final Schedule schedule,
			final boolean program) {

		ItemRowSchedule itemsRow = null;

		boolean version_program = false;
		final int day = day_on_current_calendar.intValue();

		if (!program) {
			if (day == 1) {
				version_program = true;
			}
		} else {

			if ((day == 1) || (day == 2)) {
				version_program = false;
			} else {
				version_program = true;
			}
		}

		if (day_on_current_calendar == 1) {
			itemsRow = currentRow.getItem_1();
		}

		if (day_on_current_calendar == 2) {
			itemsRow = currentRow.getItem_2();
		}

		if (day_on_current_calendar == 3) {
			itemsRow = currentRow.getItem_3();
		}

		if (day_on_current_calendar == 4) {
			itemsRow = currentRow.getItem_4();
		}

		if (day_on_current_calendar == 5) {
			itemsRow = currentRow.getItem_5();
		}

		if (schedule.getId() != null) {
			if (schedule.getShift() != null) {

				if (schedule.getShift() == 1) {
					itemsRow.setAnchor1(this.defineAnchorContent(version_program, schedule));
				}

				if (schedule.getShift() == 2) {
					itemsRow.setAnchor2(this.defineAnchorContent(version_program, schedule));
				}

				if (schedule.getShift() == 3) {
					itemsRow.setAnchor3(this.defineAnchorContent(version_program, schedule));
				}

				if (schedule.getShift() == 4) {
					itemsRow.setAnchor4(this.defineAnchorContent(version_program, schedule));
				}
			}

		}

		return itemsRow;
	}

	@Listen("onClick = #cancel_program")
	public void removeProgram() {
		if ((this.selectedDay == null) || (this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.list_details_program == null) {
			return;
		}

		this.scheduleDAO.removeAllDetailInitialScheduleByScheduleAndShift(this.currentSchedule.getId(), this.selectedShift);

		// refresh grid
		this.setupGlobalSchedulerGridForShift();

		// Messagebox.show("Il programma è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.shift_definition_popup.close();
	}

	@Listen("onClick = #remove_program_item")
	public void removeProgramItem() {

		if (this.listbox_program == null) {
			return;
		}

		if ((this.list_details_program == null) || (this.list_details_program.size() == 0)) {
			return;
		}

		// remove....
		for (final Listitem itm : this.listbox_program.getSelectedItems()) {
			final DetailInitialSchedule detail_item = itm.getValue();
			this.list_details_program.remove(detail_item);
		}

		// set model list program and revision
		final ListModelList<DetailInitialSchedule> model = new ListModelList<DetailInitialSchedule>(this.list_details_program);
		model.setMultiple(true);
		this.listbox_program.setModel(model);

	}

	@Listen("onClick = #cancel_review")
	public void removeReview() {
		if ((this.selectedDay == null) || (this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.list_details_review == null) {
			return;
		}

		this.scheduleDAO.removeAllDetailFinalScheduleByScheduleAndShift(this.currentSchedule.getId(), this.selectedShift);

		// refresh grid
		this.setupGlobalSchedulerGridForShiftReview();

		// Messagebox.show("Il consuntivo è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.shift_definition_popup_review.close();
	}

	@Listen("onClick = #remove_review_item")
	public void removeReviewItem() {

		if (this.listbox_review == null) {
			return;
		}

		if ((this.list_details_review == null) || (this.list_details_review.size() == 0)) {
			return;
		}

		// remove....
		for (final Listitem itm : this.listbox_review.getSelectedItems()) {
			final DetailFinalSchedule detail_item = itm.getValue();
			this.list_details_review.remove(detail_item);
		}

		// set model list program and revision
		final ListModelList<DetailFinalSchedule> model = new ListModelList<DetailFinalSchedule>(this.list_details_review);
		model.setMultiple(true);
		this.listbox_review.setModel(model);

	}

	/**
	 * Save Current scheduler updating values from grid
	 */
	private void saveCurrentScheduler() {

		if (this.currentSchedule == null) {
			this.currentSchedule = new Schedule();
		}

		// set data scheduler
		final Date date_schedule = this.getDateScheduled(this.selectedDay);
		this.currentSchedule.setDate_schedule(date_schedule);

		// set editor
		final Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		this.currentSchedule.setEditor(person.getId());

		// set user
		this.currentSchedule.setUser(this.selectedUser);

		this.scheduleDAO.saveOrUpdateSchedule(this.currentSchedule);

		this.currentSchedule = this.scheduleDAO.loadSchedule(date_schedule, this.selectedUser);
	}

	/**
	 * Save program
	 */
	@Listen("onClick = #ok_program")
	public void saveProgram() {

		if ((this.selectedDay == null) || (this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.list_details_program == null) {
			return;
		}

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentScheduler();
		}

		// check about sum of time

		int sum = 0;
		if (this.list_details_program.size() != 0) {
			for (final DetailInitialSchedule detail : this.list_details_program) {
				sum = sum + detail.getTime();
			}
		}
		if (sum < 6) {
			// Messagebox.show("Non si possono assegnare meno di sei ore per turno",
			// "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;

		}

		this.scheduleDAO.saveListDetailInitialScheduler(this.currentSchedule.getId(), this.selectedShift, this.list_details_program);

		// refresh grid
		this.setupGlobalSchedulerGridForShift();

		// Messagebox.show("Il programma è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.shift_definition_popup.close();

	}

	@Listen("onClick = #save_report")
	public void saveReport() {

		if ((this.selectedDay == null) || (this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.currentSchedule == null) {
			this.currentSchedule = new Schedule();
		}

		// save note
		this.currentSchedule.setNote(this.note.getValue());

		// save scheduler
		this.saveCurrentScheduler();

		// Messagebox.show("Il Report è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.shift_definition_popup.close();

	}

	/**
	 * Save review
	 */
	@Listen("onClick = #ok_review")
	public void saveReview() {

		if ((this.selectedDay == null) || (this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.list_details_review == null) {
			return;
		}

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentScheduler();
		}

		// check about sum of time

		int sum = 0;
		if (this.list_details_review.size() != 0) {
			for (final DetailFinalSchedule detail : this.list_details_review) {
				sum = sum + detail.getTime();
			}
		}
		if (sum < 6) {
			// Messagebox.show("Non si possono assegnare meno di sei ore per turno",
			// "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;

		}

		this.scheduleDAO.saveListDetailFinalScheduler(this.currentSchedule.getId(), this.selectedShift, this.list_details_review);

		// refresh grid
		this.setupGlobalSchedulerGridForShiftReview();

		// Messagebox.show("Il consuntivo è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.shift_definition_popup_review.close();

	}

	@Listen("onClick = #save_report_review")
	public void saveReviewReport() {

		if ((this.selectedDay == null) || (this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.currentSchedule == null) {
			this.currentSchedule = new Schedule();
		}

		// save note
		this.currentSchedule.setNote(this.note.getValue());

		// save scheduler
		this.saveCurrentScheduler();

		// Messagebox.show("Il Report è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.shift_definition_popup_review.close();

	}

	@Listen("onClick = #ok_day_shift")
	public void setDayShift() {
		if (this.grid_scheduler_day.getSelectedItem() == null) {
			return;
		}

		if (this.shift_popup.getSelectedItem() == null) {
			return;
		}

		if (this.selectedDay == null) {
			return;
		}

		final RowDaySchedule row_item = this.grid_scheduler_day.getSelectedItem().getValue();
		final UserShift shift = this.shift_popup.getSelectedItem().getValue();

		if (this.days_after_config.getValue() == null) {
			DaySchedule daySchedule = row_item.getDaySchedule(this.selectedDay);
			if (daySchedule == null) {
				daySchedule = new DaySchedule();
			}
			// reset day schedule
			final Date date_scheduled = this.getDateScheduled(this.selectedDay);

			daySchedule.setDate_scheduled(date_scheduled);
			daySchedule.setId_user(row_item.getUser());
			daySchedule.setShift(shift.getId());

			this.scheduleDAO.saveOrUpdateDaySchedule(daySchedule);
		} else {

			final int count = this.days_after_config.getValue().intValue();

			if ((this.selectedDay + count) > 31) {
				Messagebox.show("Non puoi schedulare oltre i limiti della griglia corrente", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			for (int i = 0; i < count; i++) {

				DaySchedule daySchedule = row_item.getDaySchedule(this.selectedDay + i);
				if (daySchedule == null) {
					daySchedule = new DaySchedule();
				}
				// reset day schedule
				final Date date_scheduled = this.getDateScheduled(this.selectedDay + i);

				daySchedule.setDate_scheduled(date_scheduled);
				daySchedule.setId_user(row_item.getUser());
				daySchedule.setShift(shift.getId());

				this.scheduleDAO.saveOrUpdateDaySchedule(daySchedule);

			}

		}

		this.setupGlobalSchedulerGridForDay();

		this.day_definition_popup.close();

	}

	/**
	 * initial structure
	 *
	 * @param initial_date
	 */
	private void setGridStructureForDay(final Date initial_date) {

		if (initial_date == null) {
			return;
		}

		this.firstDateInGrid = DateUtils.truncate(initial_date, Calendar.DATE);

		// set seven days
		for (int i = 0; i < SchedulerComposer.DAYS_IN_GRID_PREPROCESSING; i++) {

			final int index_day = i + 1;

			final Auxheader day_label = (Auxheader) this.getSelf().getFellowIfAny("day_label_" + index_day);
			final Listheader day_number = (Listheader) this.getSelf().getFellowIfAny("day_numb_" + index_day);
			if ((day_label == null) || (day_number == null)) {
				continue;
			}

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(this.firstDateInGrid);
			current_calendar.add(Calendar.DAY_OF_YEAR, i);

			final String day_n = SchedulerComposer.formatter_e.format(current_calendar.getTime());
			final String day_l = SchedulerComposer.formatter_dd.format(current_calendar.getTime());

			if (current_calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				day_number.setStyle("color:red");
				day_label.setStyle("color:red");
			} else {
				day_number.setStyle("color:black");
				day_label.setStyle("color:black");
			}

			day_number.setLabel(day_n.toUpperCase());
			day_label.setLabel(day_l.toUpperCase());

		}

	}

	/**
	 * initial structure
	 *
	 * @param initial_date
	 */
	private void setGridStructureForShift() {

		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAYS_BEFORE_TODAY_IN_PROGRAM);
		this.firstDateInGrid = DateUtils.truncate(calendar.getTime(), Calendar.DATE);

		// set seven days
		for (int i = 0; i < SchedulerComposer.DAYS_IN_GRID_PROGRAM; i++) {

			final int index_day = i + 1;

			final Auxheader month_head = (Auxheader) this.getSelf().getFellowIfAny("day_month_" + index_day);
			final Auxheader week_head = (Auxheader) this.getSelf().getFellowIfAny("day_week_" + index_day);
			if ((month_head == null) || (week_head == null)) {
				continue;
			}

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(this.firstDateInGrid);
			current_calendar.add(Calendar.DAY_OF_YEAR, i);

			final String day_w = SchedulerComposer.formatter_eeee.format(current_calendar.getTime());
			final String day_m = SchedulerComposer.formatter_ddmmm.format(current_calendar.getTime());

			if (current_calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				week_head.setStyle("color:red");
				month_head.setStyle("color:red");
			} else {
				week_head.setStyle("color:black");
				month_head.setStyle("color:black");
			}

			week_head.setLabel(day_w.toUpperCase());
			month_head.setLabel(day_m.toUpperCase());

		}

	}

	private void setGridStructureForShiftReview(final Date initial_date) {

		if (initial_date == null) {
			return;
		}

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtils.truncate(initial_date, Calendar.DATE));
		this.firstDateInGrid = DateUtils.truncate(calendar.getTime(), Calendar.DATE);

		// set seven days
		for (int i = 0; i < SchedulerComposer.DAYS_TO_SHOW_IN_REVIEW; i++) {

			final int index_day = i + 1;

			final Auxheader month_head = (Auxheader) this.getSelf().getFellowIfAny("day_month_review_" + index_day);
			final Auxheader week_head = (Auxheader) this.getSelf().getFellowIfAny("day_week_review_" + index_day);
			if ((month_head == null) || (week_head == null)) {
				continue;
			}

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(this.firstDateInGrid);

			// show the final same day in two different column
			// current_calendar.add(Calendar.DAY_OF_YEAR, i);

			final String day_w = SchedulerComposer.formatter_eeee.format(current_calendar.getTime());
			final String day_m = SchedulerComposer.formatter_ddmmm.format(current_calendar.getTime());

			if (current_calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				week_head.setStyle("color:red");
				month_head.setStyle("color:red");
			} else {
				week_head.setStyle("color:black");
				month_head.setStyle("color:black");
			}

			week_head.setLabel(day_w.toUpperCase());
			month_head.setLabel(day_m.toUpperCase());

		}

	}

	/**
	 * @param info_visibility
	 *            if true set info scheduler for programming visible
	 */
	private void setupGlobalSchedulerGridForDay() {

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.firstDateInGrid);
		calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAYS_IN_GRID_PREPROCESSING);
		final Date final_date = calendar.getTime();

		final List<DaySchedule> list = this.scheduleDAO.selectDaySchedulers(this.firstDateInGrid, final_date);

		final ArrayList<RowDaySchedule> list_row = new ArrayList<RowDaySchedule>();
		RowDaySchedule currentRow = null;

		for (int i = 0; i < list.size(); i++) {

			final DaySchedule schedule = list.get(i);

			// if the user is changed, add another row
			if ((currentRow == null) || (!currentRow.getUser().equals(schedule.getId_user()))) {
				// set current row
				currentRow = new RowDaySchedule();
				currentRow.setUser(schedule.getId_user());
				currentRow.setName_user(schedule.getName_user());
				list_row.add(currentRow);

				// set items for current row
				currentRow.setItem1(new DaySchedule());
				currentRow.setItem2(new DaySchedule());
				currentRow.setItem3(new DaySchedule());
				currentRow.setItem4(new DaySchedule());
				currentRow.setItem5(new DaySchedule());
				currentRow.setItem6(new DaySchedule());
				currentRow.setItem7(new DaySchedule());
				currentRow.setItem8(new DaySchedule());
				currentRow.setItem9(new DaySchedule());
				currentRow.setItem10(new DaySchedule());
				currentRow.setItem11(new DaySchedule());
				currentRow.setItem12(new DaySchedule());
				currentRow.setItem13(new DaySchedule());
				currentRow.setItem14(new DaySchedule());
				currentRow.setItem15(new DaySchedule());
				currentRow.setItem16(new DaySchedule());
				currentRow.setItem17(new DaySchedule());
				currentRow.setItem18(new DaySchedule());
				currentRow.setItem19(new DaySchedule());
				currentRow.setItem20(new DaySchedule());
				currentRow.setItem21(new DaySchedule());
				currentRow.setItem22(new DaySchedule());
				currentRow.setItem23(new DaySchedule());
				currentRow.setItem24(new DaySchedule());
				currentRow.setItem25(new DaySchedule());
				currentRow.setItem26(new DaySchedule());
				currentRow.setItem27(new DaySchedule());
				currentRow.setItem28(new DaySchedule());
				currentRow.setItem29(new DaySchedule());
				currentRow.setItem30(new DaySchedule());
				currentRow.setItem31(new DaySchedule());

			}

			// set correct day
			final int day_on_current_calendar = this.getDayOfSchedule(schedule.getDate_scheduled());

			if (day_on_current_calendar == 1) {
				currentRow.setItem1(schedule);
			}

			if (day_on_current_calendar == 2) {
				currentRow.setItem2(schedule);
			}

			if (day_on_current_calendar == 3) {
				currentRow.setItem3(schedule);
			}

			if (day_on_current_calendar == 4) {
				currentRow.setItem4(schedule);
			}

			if (day_on_current_calendar == 5) {
				currentRow.setItem5(schedule);
			}

			if (day_on_current_calendar == 6) {
				currentRow.setItem6(schedule);
			}

			if (day_on_current_calendar == 7) {
				currentRow.setItem7(schedule);
			}

			if (day_on_current_calendar == 8) {
				currentRow.setItem8(schedule);
			}

			if (day_on_current_calendar == 9) {
				currentRow.setItem9(schedule);
			}

			if (day_on_current_calendar == 10) {
				currentRow.setItem10(schedule);
			}

			if (day_on_current_calendar == 11) {
				currentRow.setItem11(schedule);
			}

			if (day_on_current_calendar == 12) {
				currentRow.setItem12(schedule);
			}

			if (day_on_current_calendar == 13) {
				currentRow.setItem13(schedule);
			}

			if (day_on_current_calendar == 14) {
				currentRow.setItem14(schedule);
			}

			if (day_on_current_calendar == 15) {
				currentRow.setItem15(schedule);
			}

			if (day_on_current_calendar == 16) {
				currentRow.setItem16(schedule);
			}

			if (day_on_current_calendar == 17) {
				currentRow.setItem17(schedule);
			}

			if (day_on_current_calendar == 18) {
				currentRow.setItem18(schedule);
			}

			if (day_on_current_calendar == 19) {
				currentRow.setItem19(schedule);
			}

			if (day_on_current_calendar == 20) {
				currentRow.setItem20(schedule);
			}

			if (day_on_current_calendar == 21) {
				currentRow.setItem21(schedule);
			}

			if (day_on_current_calendar == 22) {
				currentRow.setItem22(schedule);
			}

			if (day_on_current_calendar == 23) {
				currentRow.setItem23(schedule);
			}

			if (day_on_current_calendar == 24) {
				currentRow.setItem24(schedule);
			}

			if (day_on_current_calendar == 25) {
				currentRow.setItem25(schedule);
			}

			if (day_on_current_calendar == 26) {
				currentRow.setItem26(schedule);
			}

			if (day_on_current_calendar == 27) {
				currentRow.setItem27(schedule);
			}

			if (day_on_current_calendar == 28) {
				currentRow.setItem28(schedule);
			}

			if (day_on_current_calendar == 29) {
				currentRow.setItem29(schedule);
			}

			if (day_on_current_calendar == 30) {
				currentRow.setItem30(schedule);
			}

			if (day_on_current_calendar == 31) {
				currentRow.setItem31(schedule);
			}

		}

		this.grid_scheduler_day.setModel(new ListModelList<RowDaySchedule>(list_row));

	}

	/**
	 * @param info_visibility
	 *            if true set info scheduler for programming visible
	 */
	private void setupGlobalSchedulerGridForShift() {

		// user availability and color
		final HashMap<Integer, String> map_status = this.defineUserAvailability();

		// setup final period and get data...
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.firstDateInGrid);
		calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAYS_IN_GRID_PROGRAM);
		final Date final_date = calendar.getTime();

		// get info
		final List<Schedule> list = this.scheduleDAO.selectAggregateSchedulers(this.firstDateInGrid, final_date);

		final ArrayList<RowSchedule> list_row = new ArrayList<RowSchedule>();
		RowSchedule currentRow = null;

		// create a map for define people scheduled
		final HashMap<Integer, Object> sign_scheduled = new HashMap<Integer, Object>();

		for (int i = 0; i < list.size(); i++) {

			final Schedule schedule = list.get(i);

			// if the user is changed, add another row
			if ((currentRow == null) || (!currentRow.getUser().equals(schedule.getUser()))) {
				// set current row
				currentRow = new RowSchedule();
				currentRow.setUser(schedule.getUser());
				currentRow.setName_user(schedule.getName_user());
				list_row.add(currentRow);

				// set items for current row
				currentRow.setItem_1(new ItemRowSchedule(currentRow, schedule));
				currentRow.setItem_2(new ItemRowSchedule(currentRow, schedule));
				currentRow.setItem_3(new ItemRowSchedule(currentRow, schedule));
				currentRow.setItem_4(new ItemRowSchedule(currentRow, schedule));
				currentRow.setItem_5(new ItemRowSchedule(currentRow, schedule));

				// set user type for available
				if (map_status.containsKey(schedule.getUser())) {
					final String status = map_status.get(schedule.getUser());
					currentRow.setUser_status(status);
				}

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), new Object());
			}

			// set correct day
			final int day_on_current_calendar = this.getDayOfSchedule(schedule.getDate_schedule());
			final ItemRowSchedule itemsRow = this.getItemRowSchedule(currentRow, day_on_current_calendar, schedule, true);

			if (day_on_current_calendar == 1) {
				currentRow.setItem_1(itemsRow);
			}

			if (day_on_current_calendar == 2) {
				currentRow.setItem_2(itemsRow);
			}

			if (day_on_current_calendar == 3) {
				currentRow.setItem_3(itemsRow);
			}

			if (day_on_current_calendar == 4) {
				currentRow.setItem_4(itemsRow);
			}

			if (day_on_current_calendar == 5) {
				currentRow.setItem_5(itemsRow);
			}

		}

		// get all user to schedule
		final List<Person> users_schedule = this.personDAO.listAllPersons();

		for (final Person person : users_schedule) {
			if (sign_scheduled.containsKey(person.getId())) {
				continue;
			}

			final RowSchedule addedRow = new RowSchedule();
			addedRow.setUser(person.getId());
			addedRow.setName_user(person.getIndividualName());

			// set user type for available
			if (map_status.containsKey(person.getId())) {
				final String status = map_status.get(person.getId());
				addedRow.setUser_status(status);
			}

			// set items for current row
			addedRow.setItem_1(new ItemRowSchedule(addedRow));
			addedRow.setItem_2(new ItemRowSchedule(addedRow));
			addedRow.setItem_3(new ItemRowSchedule(addedRow));
			addedRow.setItem_4(new ItemRowSchedule(addedRow));
			addedRow.setItem_5(new ItemRowSchedule(addedRow));

			// add row
			list_row.add(addedRow);

		}

		// sort
		Collections.sort(list_row);

		this.grid_scheduler.setModel(new ListModelList<RowSchedule>(list_row));

	}

	/**
	 * @param info_visibility
	 *            if true set info scheduler for programming visible
	 */
	private void setupGlobalSchedulerGridForShiftReview() {

		// setup final period and get data...
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.firstDateInGrid);
		calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAYS_TO_SHOW_IN_REVIEW);
		final Date final_date = calendar.getTime();

		// get info
		final List<Schedule> list = this.scheduleDAO.selectAggregateSchedulers(this.firstDateInGrid);
		if ((list.size() > 0) && (list.get(0) != null)) {
			list.add(list.get(0));
		}

		final ArrayList<RowSchedule> list_row = new ArrayList<RowSchedule>();
		RowSchedule currentRow = null;

		// create a map for define people scheduled
		final HashMap<Integer, Object> sign_scheduled = new HashMap<Integer, Object>();

		for (int i = 0; i < list.size(); i++) {

			final Schedule schedule = list.get(i);

			// if the user is changed, add another row
			if ((currentRow == null) || (!currentRow.getUser().equals(schedule.getUser()))) {
				// set current row
				currentRow = new RowSchedule();
				currentRow.setUser(schedule.getUser());
				currentRow.setName_user(schedule.getName_user());
				list_row.add(currentRow);

				// set items for current row
				currentRow.setItem_1(new ItemRowSchedule(currentRow, schedule));
				currentRow.setItem_2(new ItemRowSchedule(currentRow, schedule));

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), new Object());
			}

			// set correct day
			final int day_on_current_calendar = this.getDayOfSchedule(schedule.getDate_schedule());
			final ItemRowSchedule itemsRow = this.getItemRowSchedule(currentRow, day_on_current_calendar, schedule, false);

			if (day_on_current_calendar == 1) {
				currentRow.setItem_1(itemsRow);
			}

			if (day_on_current_calendar == 2) {
				currentRow.setItem_2(itemsRow);
			}

		}

		// get all user to schedule
		final List<Person> users_schedule = this.personDAO.listAllPersons();

		for (final Person person : users_schedule) {
			if (sign_scheduled.containsKey(person.getId())) {
				continue;
			}

			final RowSchedule addedRow = new RowSchedule();
			addedRow.setUser(person.getId());
			addedRow.setName_user(person.getIndividualName());

			// set items for current row
			addedRow.setItem_1(new ItemRowSchedule(addedRow));
			addedRow.setItem_2(new ItemRowSchedule(addedRow));

			list_row.add(addedRow);

		}

		// sort
		Collections.sort(list_row);

		this.grid_scheduler_review.setModel(new ListModelList<RowSchedule>(list_row));

	}

	/**
	 * Shift configurator
	 *
	 * @param data_info
	 */
	private void shiftConfigurator(final String data_info) {

		this.shift_definition_popup.open(this.program_div, "after_pointer");

		if (SchedulerComposer.this.grid_scheduler.getSelectedItem() == null) {
			return;
		}

		final RowSchedule row_scheduler = SchedulerComposer.this.grid_scheduler.getSelectedItem().getValue();

		// for of shift --> shift_1_4
		final String[] info = data_info.split("_");
		if (info.length != 3) {
			Messagebox.show("Check Scheduler ZUL Strucutre. Contact Uario S.r.L.", "INFO", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		// info check
		if (!NumberUtils.isNumber(info[1]) || !NumberUtils.isNumber(info[2])) {

			Messagebox.show("Check Status Scheduler. Contact Uario S.r.L.", "INFO", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		SchedulerComposer.this.selectedDay = Integer.parseInt(info[1]);
		SchedulerComposer.this.selectedShift = Integer.parseInt(info[2]);
		this.selectedUser = row_scheduler.getUser();

		final Date date_schedule = SchedulerComposer.this.getDateScheduled(SchedulerComposer.this.selectedDay);

		// take the right scheduler
		SchedulerComposer.this.currentSchedule = this.scheduleDAO.loadSchedule(date_schedule, this.selectedUser);

		// set label
		SchedulerComposer.this.scheduler_label.setLabel(row_scheduler.getName_user() + ". Giorno: "
				+ SchedulerComposer.formatter_scheduler_info.format(date_schedule) + ". Turno: " + SchedulerComposer.this.selectedShift);

		// if any information about schedule...
		if (SchedulerComposer.this.currentSchedule != null) {

			// set note
			SchedulerComposer.this.note.setValue(SchedulerComposer.this.currentSchedule.getNote());

			// set initial program and revision
			this.list_details_program = this.scheduleDAO.loadDetailInitialScheduleByIdScheduleAndShift(this.currentSchedule.getId(),
					this.selectedShift);

		} else {
			// if we haven't information about schedule
			this.note.setValue(null);
			this.listbox_program.getItems().clear();

			// set list program and revision
			this.list_details_program = new ArrayList<DetailInitialSchedule>();

		}

		// set model list program and revision
		final ListModelList<DetailInitialSchedule> model = new ListModelList<DetailInitialSchedule>(this.list_details_program);
		model.setMultiple(true);
		this.listbox_program.setModel(model);

		// set combo task
		final List<UserTask> list = this.taskDAO.loadTasksByUser(row_scheduler.getUser());

		this.program_task.setSelectedItem(null);
		this.program_task.getChildren().clear();

		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());
			this.program_task.appendChild(combo_item);

			// set if default
			if (task_item.getTask_default()) {
				this.program_task.setSelectedItem(combo_item);
			}

		}

		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());

		}

	}

	/**
	 * Popup on review
	 *
	 * @param data_info
	 */
	protected void shiftConfiguratorReview(final String data_info) {

		this.shift_definition_popup_review.open(this.review_div, "after_pointer");

		if (SchedulerComposer.this.grid_scheduler_review.getSelectedItem() == null) {
			return;
		}

		final RowSchedule row_scheduler = SchedulerComposer.this.grid_scheduler_review.getSelectedItem().getValue();

		// for of shift --> shift_1_4
		final String[] info = data_info.split("_");
		if (info.length != 3) {
			Messagebox.show("Check Scheduler ZUL Strucutre. Contact Uario S.r.L.", "INFO", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		// info check
		if (!NumberUtils.isNumber(info[1]) || !NumberUtils.isNumber(info[2])) {

			Messagebox.show("Check Status Scheduler. Contact Uario S.r.L.", "INFO", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		SchedulerComposer.this.selectedDay = Integer.parseInt(info[1]);
		SchedulerComposer.this.selectedShift = Integer.parseInt(info[2]);
		this.selectedUser = row_scheduler.getUser();

		final Date date_schedule = SchedulerComposer.this.getDateScheduled(SchedulerComposer.this.selectedDay);

		// take the right scheduler
		SchedulerComposer.this.currentSchedule = this.scheduleDAO.loadSchedule(date_schedule, this.selectedUser);

		// set label
		SchedulerComposer.this.scheduler_label.setLabel(row_scheduler.getName_user() + ". Giorno: "
				+ SchedulerComposer.formatter_scheduler_info.format(date_schedule) + ". Turno: " + SchedulerComposer.this.selectedShift);

		// if any information about schedule...
		if (SchedulerComposer.this.currentSchedule != null) {

			// set note
			SchedulerComposer.this.note.setValue(SchedulerComposer.this.currentSchedule.getNote());

			// set initial program and revision
			this.list_details_review = this.scheduleDAO.loadDetailFinalScheduleByIdScheduleAndShift(this.currentSchedule.getId(), this.selectedShift);

		} else {
			// if we haven't information about schedule
			this.note.setValue(null);
			this.listbox_review.getItems().clear();

			// set list revision
			this.list_details_review = new ArrayList<DetailFinalSchedule>();

		}

		// set model list program and revision
		final ListModelList<DetailFinalSchedule> model = new ListModelList<DetailFinalSchedule>(this.list_details_review);
		model.setMultiple(true);
		this.listbox_review.setModel(model);

		// set combo task
		final List<UserTask> list = this.taskDAO.loadTasksByUser(row_scheduler.getUser());

		this.review_task.setSelectedItem(null);
		this.review_task.getChildren().clear();

		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());
			this.review_task.appendChild(combo_item);

			// set if default
			if (task_item.getTask_default()) {
				this.review_task.setSelectedItem(combo_item);
			}

		}

		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());

		}

	}
}
