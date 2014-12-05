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
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.statistics.IStatProcedure;
import org.uario.seaworkengine.statistics.RateShift;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ShiftTag;
import org.uario.seaworkengine.utility.Utility;
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
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;

public class SchedulerComposer extends SelectorComposer<Component> {

	private static final String				ALL_SHIFT_IN_OVERVIEW			= "TUTTI";

	private static final int				DAY_REVIEW_IN_PROGRAM_SHIFT		= 1;

	private static final int				DAYS_BEFORE_TODAY_IN_PROGRAM	= -1;

	private static final int				DAYS_IN_GRID_PREPROCESSING		= 31;

	private static final int				DAYS_IN_GRID_PROGRAM			= 5;

	private static final int				DAYS_TO_SHOW_IN_REVIEW			= 2;

	private static final SimpleDateFormat	formatDateOverview				= new SimpleDateFormat("dd/MM/yyyy");

	// format
	private static final SimpleDateFormat	formatter_dd					= new SimpleDateFormat("dd");

	private static final SimpleDateFormat	formatter_ddmmm					= new SimpleDateFormat("dd/MMM");

	private static final SimpleDateFormat	formatter_e						= new SimpleDateFormat("E");

	private static final SimpleDateFormat	formatter_eeee					= new SimpleDateFormat("EEEE");

	private static final SimpleDateFormat	formatter_scheduler_info		= new SimpleDateFormat("EEEE dd MMM");
	private static final SimpleDateFormat	formatTimeOverview				= new SimpleDateFormat("dd/MM/yyyy hh:mm");

	private static final int				MAX_DAYS_WITHOUT_BREAK			= 10;

	/**
	 *
	 */
	private static final long				serialVersionUID				= 1L;

	private ConfigurationDAO				configurationDAO;
	private Schedule						currentSchedule;
	@Wire
	private Datebox							date_from_overview;
	@Wire
	private Datebox							date_init_scheduler;

	@Wire
	private Datebox							date_init_scheduler_review;

	@Wire
	private Datebox							date_to_overview;

	@Wire
	private Datebox							day_after_config;

	@Wire
	private Popup							day_definition_popup;

	@Wire
	private Popup							day_name_popup;

	/**
	 * First date in grid
	 */
	private Date							firstDateInGrid;

	@Wire
	private Textbox							full_text_search;

	@Wire
	private Listbox							grid_scheduler;

	@Wire
	private Listbox							grid_scheduler_day;

	@Wire
	private Listbox							grid_scheduler_review;

	@Wire
	private Comboitem						item_all_shift_overview;

	@Wire
	private A								label_date_popup;

	@Wire
	private A								label_date_shift_preprocessing;

	@Wire
	private A								label_date_shift_program;

	@Wire
	private A								label_date_shift_review;
	@Wire
	private A								label_statistic_popup;

	// initial program and revision - USED IN POPUP
	private List<DetailInitialSchedule>		list_details_program;

	private List<DetailFinalSchedule>		list_details_review;

	@Wire
	private Listbox							list_overview_program;

	@Wire
	private Listbox							list_overview_review;

	@Wire
	private Listbox							listbox_program;
	@Wire
	private Listbox							listbox_review;

	// initial program and revision - USED DOWNLOAD
	private List<DetailInitialSchedule>		listDetailProgram				= null;

	private List<DetailFinalSchedule>		listDetailRevision				= null;

	private final Logger					logger							= Logger.getLogger(SchedulerComposer.class);

	@Wire
	private Textbox							note_preprocessing;

	@Wire
	private Textbox							note_program;

	@Wire
	private Textbox							note_review;

	@Wire
	private Div								overview_div;

	@Wire
	private Comboitem						overview_item;

	@Wire
	private Tabpanel						overview_review;

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
	private Intbox							program_time_hours;

	@Wire
	private Intbox							program_time_minuts;

	@Wire
	private Div								review_div;

	@Wire
	private Comboitem						review_item;

	@Wire
	private Combobox						review_task;

	@Wire
	private Intbox							review_time;

	@Wire
	private Intbox							review_time_hours;

	@Wire
	private Intbox							review_time_minuts;

	private ISchedule						scheduleDAO;
	@Wire
	private A								scheduler_label;
	@Wire
	private A								scheduler_label_review;
	@Wire
	private Combobox						scheduler_type_selector;

	@Wire
	private Combobox						select_shift_overview;

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
	private Label							shift_perc_1;

	@Wire
	private Label							shift_perc_2;

	@Wire
	private Label							shift_perc_3;

	@Wire
	private Label							shift_perc_4;

	@Wire
	private Combobox						shift_popup;

	@Wire
	private Intbox							shows_rows;

	private IStatistics						statisticDAO;

	private IStatProcedure					statProcedure;

	protected ITaskCache					task_cache;

	private TasksDAO						taskDAO;

	@Wire
	private Timebox							time_from;

	@Wire
	private Timebox							time_to;

	@Wire
	private Label							work_current_month;

	@Wire
	private Label							work_current_week;

	@Wire
	private Label							work_current_year;

	@Wire
	private Label							work_sunday_perc;

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

		final Double time = this.getProgrammedTime();

		if (time == null) {
			// Messagebox.show("Definire il numero di ore da lavorare", "INFO",
			// Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		// check about sum of time
		/*
		 * boolean check_sum = true; if (time > 6) { check_sum = false; } if
		 * (this.list_details_program.size() != 0) { int sum = time; for (final
		 * DetailInitialSchedule detail : this.list_details_program) { final int
		 * current_time = detail.getTime(); sum = sum + current_time; if (sum >
		 * 6) { check_sum = false; break; } } } if (!check_sum) { //
		 * Messagebox.show("Non si possono assegnare più di sei ore per turno",
		 * // "INFO", Messagebox.OK, Messagebox.EXCLAMATION); return; }
		 */

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

		final Double time = this.getRevisionTime();

		if (time == null) {
			// Messagebox.show("Definire il numero di ore lavorate", "INFO",
			// Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		// check about sum of time
		/*
		 * boolean check_sum = true; if (time > 6) { check_sum = false; } if
		 * (this.list_details_review.size() != 0) { int sum = time; for (final
		 * DetailFinalSchedule detail : this.list_details_review) { final int
		 * current_time = detail.getTime(); sum = sum + current_time; if (sum >
		 * 6) { check_sum = false; break; } } } if (!check_sum) { //
		 * Messagebox.show("Non si possono assegnare più di sei ore per turno",
		 * // "INFO", Messagebox.OK, Messagebox.EXCLAMATION); return; }
		 */

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentScheduler();
		}

		final DetailFinalSchedule new_item = new DetailFinalSchedule();
		new_item.setId_schedule(this.currentSchedule.getId());
		new_item.setShift(this.selectedShift);
		new_item.setTime(time);
		new_item.setTask(task.getId());

		java.util.Date now = this.time_from.getValue();
		if (now != null) {

			final java.sql.Timestamp t_from = new java.sql.Timestamp(now.getTime());
			new_item.setTime_from(t_from);
			now = this.time_to.getValue();
			final java.sql.Timestamp t_to = new java.sql.Timestamp(now.getTime());
			new_item.setTime_to(t_to);
		}

		// update program list
		this.list_details_review.add(new_item);
		final ListModelList<DetailFinalSchedule> model = new ListModelList<DetailFinalSchedule>(this.list_details_review);
		model.setMultiple(true);
		this.listbox_review.setModel(model);

	}

	/**
	 * assign info scheduler
	 *
	 * @param shift
	 * @param current_date_scheduled
	 * @param user
	 * @param schedule
	 * @param editor
	 * @param note
	 */
	private void assignShiftForDaySchedule(final UserShift shift, Date current_date_scheduled, final Integer user, final Schedule schedule,
			final Person editor, final String note) {

		// truncate current date
		current_date_scheduled = DateUtils.truncate(current_date_scheduled, Calendar.DATE);

		// reset day schedule
		schedule.setDate_schedule(current_date_scheduled);
		schedule.setUser(user);
		schedule.setShift(shift.getId());
		// set editor
		schedule.setEditor(editor.getId());
		// set note
		schedule.setNote(note);

		// save schedule
		this.scheduleDAO.saveOrUpdateSchedule(schedule);

		// assign
		this.statProcedure.workAssignProcedure(shift, current_date_scheduled, user);
	}

	/**
	 * Set the day with correct shift.
	 *
	 * @param row_item
	 *            the row
	 * @param shift
	 *            the id shift to set
	 * @param offset
	 *            the day after selected day. for current day use 0
	 */
	private void assignShiftFromDaySchedule(final RowDaySchedule row_item, final UserShift shift, final int offset) {

		final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (person_logged == null) {
			return;
		}

		// getting information
		final Date current_date_scheduled = this.getDateScheduled(this.selectedDay + offset);
		final Integer user = row_item.getUser();

		Schedule schedule = row_item.getSchedule(this.selectedDay + offset);
		if (schedule == null) {
			schedule = new Schedule();
		}

		this.assignShiftForDaySchedule(shift, current_date_scheduled, user, schedule, person_logged, this.note_preprocessing.getValue());

	}

	/**
	 * Define anchor content on shift schedule
	 *
	 * @param program
	 * @param schedule
	 * @return
	 */
	private String defineAnchorContent(final boolean program, final Schedule schedule) {
		Double time = null;
		if (program) {
			time = schedule.getProgram_time();
		} else {
			time = schedule.getRevision_time();
		}

		if (time != null) {
			return Utility.decimatToTime(time);
		}

		return null;

	}

	/**
	 * define the view in function of the type of the view required
	 */
	@Listen("onChange = #scheduler_type_selector, #date_init_scheduler, #date_init_scheduler_review, #select_shift_overview, #date_to_overview, #date_from_overview;onOK = #shows_rows, #full_text_search; onSelect = #overview_tab")
	public void defineSchedulerView() {

		if (this.scheduler_type_selector.getSelectedItem() == null) {
			return;
		}

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected == this.preprocessing_item) {
			this.preprocessing_div.setVisible(true);
			this.program_div.setVisible(false);
			this.review_div.setVisible(false);
			this.overview_div.setVisible(false);

			// set initial structure for program
			this.setGridStructureForDay(SchedulerComposer.this.date_init_scheduler.getValue());
			this.setupGlobalSchedulerGridForDay();
			return;
		}

		if (selected == this.program_item) {
			this.preprocessing_div.setVisible(false);
			this.program_div.setVisible(true);
			this.review_div.setVisible(false);
			this.overview_div.setVisible(false);

			// set initial structure for program
			this.setGridStructureForShift();
			this.setupGlobalSchedulerGridForShift();
			return;
		}

		if (selected == this.review_item) {
			this.preprocessing_div.setVisible(false);
			this.program_div.setVisible(false);
			this.review_div.setVisible(true);
			this.overview_div.setVisible(false);

			// set initial structure for program
			this.setGridStructureForShiftReview(SchedulerComposer.this.date_init_scheduler_review.getValue());
			this.setupGlobalSchedulerGridForShiftReview();
			return;
		}

		if (selected == this.overview_item) {
			this.preprocessing_div.setVisible(false);
			this.program_div.setVisible(false);
			this.review_div.setVisible(false);
			this.overview_div.setVisible(true);

			// set overview list
			this.setOverviewLists();

			return;
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
		final List<Schedule> day_schedule_list = this.scheduleDAO.loadSchedule(calendar.getTime());

		final HashMap<Integer, String> map_status = new HashMap<Integer, String>();
		for (final Schedule day_schedule : day_schedule_list) {
			final Integer id_user = day_schedule.getUser();
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
		this.statisticDAO = (IStatistics) SpringUtil.getBean(BeansTag.STATISTICS);
		this.statProcedure = (IStatProcedure) SpringUtil.getBean(BeansTag.STAT_PROCEDURE);

		this.getSelf().addEventListener(ZkEventsTag.onDayNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final RowDaySchedule row_schedule = SchedulerComposer.this.grid_scheduler_day.getSelectedItem().getValue();

				if (row_schedule == null) {
					return;
				}

				final Integer id_user = row_schedule.getUser();
				// set name
				final String msg = row_schedule.getName_user();

				// show statistic popup
				SchedulerComposer.this.showStatisticsPopup(id_user, SchedulerComposer.this.grid_scheduler_day, msg);

			}

		});

		this.getSelf().addEventListener(ZkEventsTag.onProgramNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final RowSchedule row_schedule = SchedulerComposer.this.grid_scheduler.getSelectedItem().getValue();

				if (row_schedule == null) {
					return;
				}

				final Integer id_user = row_schedule.getUser();
				// set name
				final String msg = row_schedule.getName_user();

				// show statistic popup
				SchedulerComposer.this.showStatisticsPopup(id_user, SchedulerComposer.this.grid_scheduler, msg);

			}

		});

		this.getSelf().addEventListener(ZkEventsTag.onReviewNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final RowSchedule row_schedule = SchedulerComposer.this.grid_scheduler_review.getSelectedItem().getValue();

				if (row_schedule == null) {
					return;
				}

				final Integer id_user = row_schedule.getUser();
				// set name
				final String msg = row_schedule.getName_user();

				// show statistic popup
				SchedulerComposer.this.showStatisticsPopup(id_user, SchedulerComposer.this.grid_scheduler_review, msg);

			}

		});

		this.getSelf().addEventListener(ZkEventsTag.onShowScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// define shift combo
				final List<UserShift> shifts = SchedulerComposer.this.configurationDAO.loadShifts();
				SchedulerComposer.this.shift_popup.setModel(new ListModelList<UserShift>(shifts));

				// get the shift cache
				SchedulerComposer.this.shift_cache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);
				SchedulerComposer.this.task_cache = (ITaskCache) SpringUtil.getBean(BeansTag.TASK_CACHE);

				// set preprocessing item in combo selection
				SchedulerComposer.this.scheduler_type_selector.setSelectedItem(SchedulerComposer.this.preprocessing_item);

				SchedulerComposer.this.defineSchedulerView();

			}
		});

		// SHOW SHIFT CONFIGURATOR
		this.getSelf().addEventListener(ZkEventsTag.onShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final String data_info = arg0.getData().toString();

				// configure shift
				SchedulerComposer.this.onShiftClickProgram(data_info);

			}

		});

		// SHOW SHIFT CONFIGURATOR
		this.getSelf().addEventListener(ZkEventsTag.onShiftClickReview, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final String data_info = arg0.getData().toString();

				// configure shift
				SchedulerComposer.this.onShiftClickReview(data_info);

			}

		});

		// SHOW DAY CONFIGURATOR
		this.getSelf().addEventListener(ZkEventsTag.onDayClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final String data_info = arg0.getData().toString();

				// configure shift
				SchedulerComposer.this.onDayClick(data_info);

			}

		});

	}

	@Listen("onClick = #overview_download")
	public void downdoadOverviewCSV() {
		// select list
		if (this.overview_review.isSelected() && (this.listDetailRevision != null)) {

			final StringBuilder builder = new StringBuilder();
			final String header = "nome;data;turno;mansione;ore;ingresso;uscita\n";
			builder.append(header);

			for (final DetailFinalSchedule item : this.listDetailRevision) {
				String date = "";
				if (item.getDate_schedule() != null) {
					date = SchedulerComposer.formatDateOverview.format(item.getDate_schedule());
				}

				String time_from = "";
				if (item.getTime_from() != null) {
					time_from = SchedulerComposer.formatTimeOverview.format(item.getTime_from());
				}

				String time_to = "";
				if (item.getTime_to() != null) {
					time_to = SchedulerComposer.formatTimeOverview.format(item.getTime_to());
				}

				String code_task = "";
				final UserTask task = this.task_cache.getUserTask(item.getTask());
				if (task != null) {
					code_task = task.getCode();
				}

				final String line = "" + item.getUser() + ";" + date + ";" + item.getShift() + ";" + code_task + ";" + item.getTime() + ";"
						+ time_from + ";" + time_to + ";\n";
				builder.append(line);
			}

			Filedownload.save(builder.toString(), "application/text", "revision.csv");
		} else if (this.listDetailProgram != null) {

			final StringBuilder builder = new StringBuilder();
			final String header = "nome;data;turno;mansione;ore;\n";
			builder.append(header);

			for (final DetailInitialSchedule item : this.listDetailProgram) {
				String date = "";
				if (item.getDate_schedule() != null) {
					date = SchedulerComposer.formatDateOverview.format(item.getDate_schedule());
				}

				String code_task = "";
				final UserTask task = this.task_cache.getUserTask(item.getTask());
				if (task != null) {
					code_task = task.getCode();
				}

				final String line = "" + item.getUser() + ";" + date + ";" + item.getShift() + ";" + code_task + ";" + item.getTime() + ";\n";
				builder.append(line);
			}

			Filedownload.save(builder.toString(), "application/text", "program.csv");

		}

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

			if (day == 1) {
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
			if (schedule.getNo_shift() != null) {

				if (schedule.getNo_shift() == 1) {
					itemsRow.setAnchor1(this.defineAnchorContent(version_program, schedule));
				}

				if (schedule.getNo_shift() == 2) {
					itemsRow.setAnchor2(this.defineAnchorContent(version_program, schedule));
				}

				if (schedule.getNo_shift() == 3) {
					itemsRow.setAnchor3(this.defineAnchorContent(version_program, schedule));
				}

				if (schedule.getNo_shift() == 4) {
					itemsRow.setAnchor4(this.defineAnchorContent(version_program, schedule));
				}
			}

		}

		return itemsRow;
	}

	/**
	 * Return the last break day in week
	 *
	 * @param current_date_scheduled
	 * @param user
	 * @return get last break day in week
	 */
	private Date getMaxDateBreak(final Date current_date_scheduled, final Integer user) {
		final Calendar checkPeriod = Calendar.getInstance();
		checkPeriod.setTime(current_date_scheduled);
		checkPeriod.add(Calendar.DAY_OF_YEAR, -7);
		checkPeriod.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		final Date date_from = checkPeriod.getTime();
		checkPeriod.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		final Date date_to = checkPeriod.getTime();
		final Date date_break = this.statisticDAO.getDatesBreak(user, date_from, date_to);
		if (date_break != null) {
			final Calendar calendarDateBreak = Calendar.getInstance();
			calendarDateBreak.setTime(date_break);
			calendarDateBreak.add(Calendar.DAY_OF_YEAR, SchedulerComposer.MAX_DAYS_WITHOUT_BREAK);

			return calendarDateBreak.getTime();

		}

		return null;
	}

	/**
	 * Programmed time in decimal
	 *
	 * @return
	 */
	private Double getProgrammedTime() {

		Integer hours = this.program_time_hours.getValue();
		Integer minuts = this.program_time_minuts.getValue();

		if (hours == null) {
			hours = 0;
		}

		if (minuts == null) {
			minuts = 0;
		}

		final Double ret = hours + (((double) minuts) / 60);

		return ret;

	}

	/**
	 * Return decimal revision time
	 *
	 * @return
	 */
	private Double getRevisionTime() {

		final Integer hours = this.review_time_hours.getValue();
		final Integer minuts = this.review_time_minuts.getValue();

		if ((hours == null) || (minuts == null)) {
			return null;
		}

		final Double ret = hours + (((double) minuts) / 60);

		return Utility.roundTwo(ret);
	}

	@Listen("onClick = #go_today_preprocessing, #go_today_review")
	public void goToday_Preprocessing() {
		final Calendar calendar = Calendar.getInstance();
		final Date today = calendar.getTime();

		if (this.scheduler_type_selector.getSelectedItem() == null) {
			return;
		}

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected == this.preprocessing_item) {
			this.preprocessing_div.setVisible(true);
			this.program_div.setVisible(false);
			this.review_div.setVisible(false);

			SchedulerComposer.this.date_init_scheduler.setValue(today);

			// set initial structure for program
			this.setGridStructureForDay(today);
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

			SchedulerComposer.this.date_init_scheduler_review.setValue(today);

			// set initial structure for program
			this.setGridStructureForShiftReview(today);
			this.setupGlobalSchedulerGridForShiftReview();
		}

	}

	/**
	 * Define bheavior for day configuration
	 *
	 * @param data_info
	 */
	protected void onDayClick(final String data_info) {

		this.day_after_config.setValue(null);

		// set info day;
		this.selectedDay = Integer.parseInt(data_info);
		if (this.selectedDay == null) {
			return;
		}

		final RowDaySchedule row_scheduler = this.grid_scheduler_day.getSelectedItem().getValue();
		this.selectedUser = row_scheduler.getUser();
		if (this.selectedUser == null) {
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

		// take the right scheduler
		SchedulerComposer.this.currentSchedule = this.scheduleDAO.loadSchedule(current_day, this.selectedUser);

		UserShift current_shift = null;
		if (this.currentSchedule != null) {
			final UserShift myshift = this.shift_cache.getUserShift(this.currentSchedule.getShift());
			if (myshift != null) {
				current_shift = myshift;
			}
		}

		// if current shift is null, show default work shift
		if (current_shift == null) {
			current_shift = this.shift_cache.getStandardWorkShift();
		}

		this.label_date_popup.setLabel(msg);
		if (current_shift != null) {
			this.label_date_shift_preprocessing.setLabel(current_shift.toString());
		} else {
			this.label_date_shift_preprocessing.setLabel(null);
		}

		this.shift_popup.setSelectedItem(null);

		if (current_shift != null) {
			// set initial selected item
			for (final Comboitem item : this.shift_popup.getItems()) {
				if (item.getValue() instanceof UserShift) {
					if (item.getValue().equals(current_shift)) {
						this.shift_popup.setSelectedItem(item);
						break;
					}
				}
			}
		}

		// show note
		if (SchedulerComposer.this.currentSchedule != null) {
			// set note_program
			SchedulerComposer.this.note_preprocessing.setValue(SchedulerComposer.this.currentSchedule.getNote());
		} else {
			// if we haven't information about schedule
			this.note_preprocessing.setValue(null);
		}

		this.day_definition_popup.open(this.grid_scheduler_day, "after_pointer");
	}

	@Listen("onClick = #overview_month")
	public void onSelectMonthOverview() {

		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		this.date_from_overview.setValue(DateUtils.truncate(calendar.getTime(), Calendar.DATE));

		calendar.getMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));

		this.date_to_overview.setValue(calendar.getTime());

		this.defineSchedulerView();

	}

	/**
	 * Shift configurator
	 *
	 * @param data_info
	 */
	private void onShiftClickProgram(final String data_info) {

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

		// set label current shift
		if (this.currentSchedule != null) {
			final UserShift myshift = this.shift_cache.getUserShift(this.currentSchedule.getShift());
			if (myshift != null) {
				this.label_date_shift_program.setLabel(myshift.toString());
			} else {
				if (this.shift_cache.getStandardWorkShift() != null) {
					this.label_date_shift_program.setLabel(this.shift_cache.getStandardWorkShift().toString());
				} else {
					this.label_date_shift_program.setLabel(null);
				}
			}
		}

		// if any information about schedule...
		if (SchedulerComposer.this.currentSchedule != null) {

			// set note_program
			SchedulerComposer.this.note_program.setValue(SchedulerComposer.this.currentSchedule.getNote());

			// set initial program and revision
			this.list_details_program = this.scheduleDAO.loadDetailInitialScheduleByIdScheduleAndShift(this.currentSchedule.getId(),
					this.selectedShift);

		} else {
			// if we haven't information about schedule
			this.note_program.setValue(null);
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
	private void onShiftClickReview(final String data_info) {

		final Date date_to_configure = this.date_init_scheduler_review.getValue();
		if (date_to_configure == null) {
			return;
		}

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

		this.selectedDay = Integer.parseInt(info[1]);
		this.selectedShift = Integer.parseInt(info[2]);
		this.selectedUser = row_scheduler.getUser();

		final Date date_schedule = DateUtils.truncate(date_to_configure, Calendar.DATE);

		// take the right scheduler
		SchedulerComposer.this.currentSchedule = this.scheduleDAO.loadSchedule(date_schedule, this.selectedUser);

		// set label
		this.scheduler_label_review.setLabel(row_scheduler.getName_user() + ". Giorno: "
				+ SchedulerComposer.formatter_scheduler_info.format(date_schedule) + ". Turno: " + SchedulerComposer.this.selectedShift);

		// set label current shift
		if (this.currentSchedule != null) {
			final UserShift myshift = this.shift_cache.getUserShift(this.currentSchedule.getShift());
			if (myshift != null) {
				this.label_date_shift_review.setLabel(myshift.toString());
			} else {
				if (this.shift_cache.getStandardWorkShift() != null) {
					this.label_date_shift_review.setLabel(this.shift_cache.getStandardWorkShift().toString());
				} else {
					this.label_date_shift_review.setLabel(null);
				}
			}
		}

		// if any information about schedule...
		if (SchedulerComposer.this.currentSchedule != null) {

			// set note_program
			SchedulerComposer.this.note_review.setValue(SchedulerComposer.this.currentSchedule.getNote());

			// set initial program and revision
			this.list_details_review = this.scheduleDAO.loadDetailFinalScheduleByIdScheduleAndShift(this.currentSchedule.getId(), this.selectedShift);

		} else {
			// if we haven't information about schedule
			this.note_review.setValue(null);
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

	@Listen("onClick = #refresh_command")
	public void refreshCommand() {

		this.full_text_search.setValue(null);

		// refresh for overview
		this.date_from_overview.setValue(null);
		this.date_to_overview.setValue(null);
		this.select_shift_overview.setSelectedItem(this.item_all_shift_overview);

		this.defineSchedulerView();

	}

	@Listen("onClick = #cancel_day_definition")
	public void removeDayConfiguration() {

		if ((this.selectedDay == null)) {
			return;
		}

		if (this.grid_scheduler_day == null) {
			return;
		}

		// info to the row
		final RowDaySchedule row_item = this.grid_scheduler_day.getSelectedItem().getValue();
		final Date dayScheduleDate = this.getDateScheduled(this.selectedDay);

		// info to the users
		final UserShift shiftStandard = this.shift_cache.getStandardWorkShift();

		// check for day after tomorrow...remove means assign standard
		// work
		final Calendar tomorrow_cal = Calendar.getInstance();
		tomorrow_cal.add(Calendar.DATE, 1);
		final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// business metod
		if (dayScheduleDate != null) {

			final Date dayAfterConfig = this.day_after_config.getValue();

			if (dayAfterConfig != null) {

				final Date to_day = DateUtils.truncate(dayAfterConfig, Calendar.DATE);

				if (dayScheduleDate.after(to_day)) {
					Messagebox.show("Attenzione alla data inserita", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);
					return;
				}

				final int count = (int) ((to_day.getTime() - dayScheduleDate.getTime()) / (1000 * 60 * 60 * 24));

				if ((this.selectedDay + count) > SchedulerComposer.DAYS_IN_GRID_PREPROCESSING) {
					Messagebox
							.show("Non puoi programmare oltre i limiti della griglia corrente", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);
					return;
				}
				// remove day schedule in interval date
				this.scheduleDAO.removeScheduleUserSuspended(row_item.getUser(), dayScheduleDate, dayAfterConfig);

				// check for info worker for tomorrw
				final Calendar calendar = DateUtils.toCalendar(dayScheduleDate);
				for (int i = 0; i <= count; i++) {

					if (i != 0) {
						calendar.add(Calendar.DAY_OF_YEAR, 1);
					}

					if (DateUtils.isSameDay(tomorrow_cal, calendar)) {
						final Schedule schedule = new Schedule();
						this.assignShiftForDaySchedule(shiftStandard, tomorrow_cal.getTime(), row_item.getUser(), schedule, person_logged, null);
						break;
					}

				}

			} else {
				// Remove only current day schedule
				this.scheduleDAO.removeScheduleUserSuspended(row_item.getUser(), dayScheduleDate, dayScheduleDate);

				// check for tomorrw
				if (DateUtils.isSameDay(tomorrow_cal.getTime(), dayScheduleDate)) {
					final Schedule schedule = new Schedule();
					this.assignShiftForDaySchedule(shiftStandard, tomorrow_cal.getTime(), row_item.getUser(), schedule, person_logged, null);
				}

			}

			// refresh grid
			this.setupGlobalSchedulerGridForDay();
		} else {
			return;
		}

		// Messagebox.show("Il programma giornaliero è stato cancellato",
		// "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.day_definition_popup.close();
	}

	@Listen("onClick = #cancel_program")
	public void removeProgram() {
		if ((this.selectedDay == null) || (this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.list_details_program == null) {
			return;
		}

		if (this.currentSchedule != null) {
			this.scheduleDAO.removeAllDetailInitialScheduleByScheduleAndShift(this.currentSchedule.getId(), this.selectedShift);

			// refresh grid
			this.setupGlobalSchedulerGridForShift();
		}

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

		if (this.currentSchedule != null) {
			this.scheduleDAO.removeAllDetailFinalScheduleByScheduleAndShift(this.currentSchedule.getId(), this.selectedShift);

			// refresh grid
			this.setupGlobalSchedulerGridForShiftReview();

		}

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

		// if shift not assigned, assign default one
		if ((this.shift_cache.getStandardWorkShift() != null) && (this.currentSchedule.getShift() == null)) {
			this.currentSchedule.setShift(this.shift_cache.getStandardWorkShift().getId());
		}

		this.scheduleDAO.saveOrUpdateSchedule(this.currentSchedule);

		this.currentSchedule = this.scheduleDAO.loadSchedule(date_schedule, this.selectedUser);
	}

	@Listen("onClick = #ok_day_shift")
	public void saveDayScheduling() {
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

		// get day schedule
		final Date date_scheduled = this.getDateScheduled(this.selectedDay);

		if (this.day_after_config.getValue() == null) {

			// set only current day
			this.assignShiftFromDaySchedule(row_item, shift, 0);

		} else {

			// set multiple day..... check date before...

			final Date to_day = DateUtils.truncate(this.day_after_config.getValue(), Calendar.DATE);

			if (date_scheduled.after(to_day)) {
				Messagebox.show("Attenzione alla data inserita", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			final int count = (int) ((to_day.getTime() - date_scheduled.getTime()) / (1000 * 60 * 60 * 24));

			if ((this.selectedDay + count) > SchedulerComposer.DAYS_IN_GRID_PREPROCESSING) {
				Messagebox.show("Non puoi programmare oltre i limiti della griglia corrente", "ATTENZIONE", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			// check day
			for (int i = 0; i <= count; i++) {

				// set day with offest i
				this.assignShiftFromDaySchedule(row_item, shift, i);
			}

		}

		this.setupGlobalSchedulerGridForDay();

		this.day_definition_popup.close();

	}

	@Listen("onClick = #save_report, #save_report_review")
	public void saveNoteInSchedule() {

		if (this.currentSchedule == null) {
			this.currentSchedule = new Schedule();
		}

		// save note_program
		String note = "";

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected == this.program_item) {
			note = this.note_program.getValue();
			this.shift_definition_popup.close();
		}

		if (selected == this.review_item) {
			note = this.note_review.getValue();
			this.shift_definition_popup_review.close();
		}

		this.currentSchedule.setNote(note);

		// save scheduler
		this.saveCurrentScheduler();

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
		Double sum = 0.0;
		if (this.list_details_program.size() != 0) {
			for (final DetailInitialSchedule detail : this.list_details_program) {
				sum = sum + detail.getTime();
			}
		}

		// check max 12 h in a day
		final List<DetailInitialSchedule> list_detail_schedule = this.scheduleDAO.loadDetailInitialScheduleByIdSchedule(this.currentSchedule.getId());
		Double count = sum;
		for (final DetailInitialSchedule dt : list_detail_schedule) {
			count = count + dt.getTime();
			if (count > 12) {
				break;
			}
		}
		if (count > 12) {
			Messagebox.show("Non si possono assegnare più di 12 ore al giorno", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;

		}

		this.scheduleDAO.saveListDetailInitialScheduler(this.currentSchedule.getId(), this.selectedShift, this.list_details_program);

		// refresh grid
		this.setupGlobalSchedulerGridForShift();

		// Messagebox.show("Il programma è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.shift_definition_popup.close();

	}

	/**
	 * Save review
	 */
	@Listen("onClick = #ok_review")
	public void saveReview() {

		if ((this.selectedShift == null) || (this.selectedUser == null)) {
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
		Double sum = 0.0;
		if (this.list_details_review.size() != 0) {
			for (final DetailFinalSchedule detail : this.list_details_review) {
				sum = sum + detail.getTime();
			}
		}

		// check max 12 h in a day
		final List<DetailFinalSchedule> list_detail_schedule = this.scheduleDAO.loadDetailFinalScheduleByIdSchedule(this.currentSchedule.getId());
		Double count = sum;
		for (final DetailFinalSchedule dt : list_detail_schedule) {
			count = count + dt.getTime();
			if (count > 12) {
				break;
			}
		}
		if (count > 12) {
			Messagebox.show("Non si possono assegnare più di 12 ore al giorno", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;

		}

		this.scheduleDAO.saveListDetailFinalScheduler(this.currentSchedule.getId(), this.selectedShift, this.list_details_review);

		// refresh grid
		this.setupGlobalSchedulerGridForShiftReview();

		// Messagebox.show("Il consuntivo è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.shift_definition_popup_review.close();

	}

	/**
	 *
	 * @param initial_date
	 */
	private void setGridStructureForDay(final Date initial_date) {

		if (initial_date == null) {
			return;
		}

		this.firstDateInGrid = DateUtils.truncate(initial_date, Calendar.DATE);

		// days
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

	/**
	 * Set Grid header
	 *
	 * @param initial_date
	 */
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
	 * Overview list
	 */
	private void setOverviewLists() {

		String full_text_search = null;
		Integer shift_number = null;
		Date date_from = null;
		Date date_to = null;

		// select full_text searching
		if ((this.full_text_search.getValue() != null) && !this.full_text_search.getValue().equals("")) {
			full_text_search = this.full_text_search.getValue();
		}

		// select shift
		if (this.select_shift_overview.getSelectedItem() != null) {
			final String value = this.select_shift_overview.getValue();
			if (!value.equals(SchedulerComposer.ALL_SHIFT_IN_OVERVIEW)) {
				if (NumberUtils.isNumber(this.select_shift_overview.getValue())) {
					shift_number = Integer.parseInt(this.select_shift_overview.getValue());
				}

			}
		}

		// select date
		if (this.date_from_overview.getValue() != null) {
			date_from = this.date_from_overview.getValue();
		}
		if (this.date_to_overview.getValue() != null) {
			date_to = this.date_to_overview.getValue();
		}
		if ((date_from == null) && (date_to != null)) {
			date_from = date_to;
		}
		if ((date_from != null) && (date_to == null)) {
			date_to = date_from;
		}
		if ((date_from != null) && date_from.after(date_to)) {
			Messagebox.show("Controlla le date inserite", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		// select list
		if (this.overview_review.isSelected()) {

			this.listDetailRevision = this.statisticDAO.listDetailFinalSchedule(full_text_search, shift_number, date_from, date_to);

			// set number of row showed
			this.list_overview_review.setModel(new ListModelList<DetailFinalSchedule>(this.listDetailRevision));
			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.list_overview_review.setPageSize(this.shows_rows.getValue());
			}
		} else {

			this.listDetailProgram = this.statisticDAO.listDetailInitialSchedule(full_text_search, shift_number, date_from, date_to);

			// set number of row showed
			this.list_overview_program.setModel(new ListModelList<DetailInitialSchedule>(this.listDetailProgram));
			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.list_overview_program.setPageSize(this.shows_rows.getValue());
			}

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

		List<Schedule> list = null;

		if ((this.full_text_search.getValue() == null) || this.full_text_search.getValue().equals("")) {
			list = this.scheduleDAO.selectSchedulersForPreprocessing(this.firstDateInGrid, final_date, null);
		} else {
			list = this.scheduleDAO.selectSchedulersForPreprocessing(this.firstDateInGrid, final_date, this.full_text_search.getValue());
		}

		final ArrayList<RowDaySchedule> list_row = new ArrayList<RowDaySchedule>();
		RowDaySchedule currentRow = null;

		for (int i = 0; i < list.size(); i++) {

			final Schedule schedule = list.get(i);

			// if the user is changed, add another row
			if ((currentRow == null) || (!currentRow.getUser().equals(schedule.getUser()))) {
				// set current row
				currentRow = new RowDaySchedule();
				currentRow.setUser(schedule.getUser());
				currentRow.setName_user(schedule.getName_user());
				list_row.add(currentRow);

				// set items for current row
				currentRow.setItem1(new Schedule());
				currentRow.setItem2(new Schedule());
				currentRow.setItem3(new Schedule());
				currentRow.setItem4(new Schedule());
				currentRow.setItem5(new Schedule());
				currentRow.setItem6(new Schedule());
				currentRow.setItem7(new Schedule());
				currentRow.setItem8(new Schedule());
				currentRow.setItem9(new Schedule());
				currentRow.setItem10(new Schedule());
				currentRow.setItem11(new Schedule());
				currentRow.setItem12(new Schedule());
				currentRow.setItem13(new Schedule());
				currentRow.setItem14(new Schedule());
				currentRow.setItem15(new Schedule());
				currentRow.setItem16(new Schedule());
				currentRow.setItem17(new Schedule());
				currentRow.setItem18(new Schedule());
				currentRow.setItem19(new Schedule());
				currentRow.setItem20(new Schedule());
				currentRow.setItem21(new Schedule());
				currentRow.setItem22(new Schedule());
				currentRow.setItem23(new Schedule());
				currentRow.setItem24(new Schedule());
				currentRow.setItem25(new Schedule());
				currentRow.setItem26(new Schedule());
				currentRow.setItem27(new Schedule());
				currentRow.setItem28(new Schedule());
				currentRow.setItem29(new Schedule());
				currentRow.setItem30(new Schedule());
				currentRow.setItem31(new Schedule());

			}

			// set correct day
			final int day_on_current_calendar = this.getDayOfSchedule(schedule.getDate_schedule());

			if (day_on_current_calendar == 1) {
				currentRow.setItem1(schedule);
				continue;
			}

			if (day_on_current_calendar == 2) {
				currentRow.setItem2(schedule);
				continue;
			}

			if (day_on_current_calendar == 3) {
				currentRow.setItem3(schedule);
				continue;
			}

			if (day_on_current_calendar == 4) {
				currentRow.setItem4(schedule);
				continue;
			}

			if (day_on_current_calendar == 5) {
				currentRow.setItem5(schedule);
				continue;
			}

			if (day_on_current_calendar == 6) {
				currentRow.setItem6(schedule);
				continue;
			}

			if (day_on_current_calendar == 7) {
				currentRow.setItem7(schedule);
				continue;
			}

			if (day_on_current_calendar == 8) {
				currentRow.setItem8(schedule);
				continue;
			}

			if (day_on_current_calendar == 9) {
				currentRow.setItem9(schedule);
				continue;
			}

			if (day_on_current_calendar == 10) {
				currentRow.setItem10(schedule);
				continue;
			}

			if (day_on_current_calendar == 11) {
				currentRow.setItem11(schedule);
				continue;
			}

			if (day_on_current_calendar == 12) {
				currentRow.setItem12(schedule);
				continue;
			}

			if (day_on_current_calendar == 13) {
				currentRow.setItem13(schedule);
				continue;
			}

			if (day_on_current_calendar == 14) {
				currentRow.setItem14(schedule);
				continue;
			}

			if (day_on_current_calendar == 15) {
				currentRow.setItem15(schedule);
				continue;
			}

			if (day_on_current_calendar == 16) {
				currentRow.setItem16(schedule);
				continue;
			}

			if (day_on_current_calendar == 17) {
				currentRow.setItem17(schedule);
				continue;
			}

			if (day_on_current_calendar == 18) {
				currentRow.setItem18(schedule);
				continue;
			}

			if (day_on_current_calendar == 19) {
				currentRow.setItem19(schedule);
				continue;
			}

			if (day_on_current_calendar == 20) {
				currentRow.setItem20(schedule);
				continue;
			}

			if (day_on_current_calendar == 21) {
				currentRow.setItem21(schedule);
				continue;
			}

			if (day_on_current_calendar == 22) {
				currentRow.setItem22(schedule);
				continue;
			}

			if (day_on_current_calendar == 23) {
				currentRow.setItem23(schedule);
				continue;
			}

			if (day_on_current_calendar == 24) {
				currentRow.setItem24(schedule);
				continue;
			}

			if (day_on_current_calendar == 25) {
				currentRow.setItem25(schedule);
				continue;
			}

			if (day_on_current_calendar == 26) {
				currentRow.setItem26(schedule);
				continue;
			}

			if (day_on_current_calendar == 27) {
				currentRow.setItem27(schedule);
				continue;
			}

			if (day_on_current_calendar == 28) {
				currentRow.setItem28(schedule);
				continue;
			}

			if (day_on_current_calendar == 29) {
				currentRow.setItem29(schedule);
				continue;
			}

			if (day_on_current_calendar == 30) {
				currentRow.setItem30(schedule);
				continue;
			}

			if (day_on_current_calendar == 31) {
				currentRow.setItem31(schedule);
				continue;
			}

		}

		// set grid
		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.grid_scheduler_day.setPageSize(this.shows_rows.getValue());
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

		// setup final day for program
		final Calendar final_calendar = Calendar.getInstance();
		final_calendar.setTime(this.firstDateInGrid);
		final_calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAYS_IN_GRID_PROGRAM);
		final Date final_date_program = final_calendar.getTime();

		// setup initial day for program
		final Calendar initial_calendar = Calendar.getInstance();
		initial_calendar.setTime(this.firstDateInGrid);
		initial_calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAY_REVIEW_IN_PROGRAM_SHIFT);
		final Date initial_date_program = initial_calendar.getTime();

		// take info about person
		String text_search_person = null;
		if ((this.full_text_search.getValue() == null) || this.full_text_search.getValue().equals("")) {
			text_search_person = null;
		} else {
			text_search_person = this.full_text_search.getValue();
		}
		final List<Schedule> list_program = this.scheduleDAO.selectAggregateSchedulersProgram(initial_date_program, final_date_program,
				text_search_person);

		final ArrayList<RowSchedule> list_row = new ArrayList<RowSchedule>();
		RowSchedule currentRow = null;

		// create a map for define people scheduled
		final HashMap<Integer, RowSchedule> sign_scheduled = new HashMap<Integer, RowSchedule>();

		// under program
		for (int i = 0; i < list_program.size(); i++) {

			final Schedule schedule = list_program.get(i);

			// if the user is changed, add another row
			if ((currentRow == null) || (!currentRow.getUser().equals(schedule.getUser()))) {
				// set current row
				currentRow = new RowSchedule();
				currentRow.setUser(schedule.getUser());
				currentRow.setName_user(schedule.getName_user());
				list_row.add(currentRow);

				// set items for current row
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
				sign_scheduled.put(schedule.getUser(), currentRow);
			}

			// set correct day
			final int day_on_current_calendar = this.getDayOfSchedule(schedule.getDate_schedule());
			final ItemRowSchedule itemsRow = this.getItemRowSchedule(currentRow, day_on_current_calendar, schedule, true);

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

		// get info
		final List<Schedule> list_revision = this.scheduleDAO.selectAggregateSchedulersRevision(this.firstDateInGrid, initial_date_program,
				text_search_person);

		// under revision
		for (int i = 0; i < list_revision.size(); i++) {

			final Schedule schedule = list_revision.get(i);

			// get row
			RowSchedule myrow = sign_scheduled.get(schedule.getUser());

			// if the user is changed, add another row
			if ((myrow == null) || (!myrow.getUser().equals(schedule.getUser()))) {
				// set current row
				myrow = new RowSchedule();
				myrow.setUser(schedule.getUser());
				myrow.setName_user(schedule.getName_user());
				list_row.add(myrow);

				// set items for current row
				myrow.setItem_1(new ItemRowSchedule(myrow, schedule));

				// set user type for available
				if (map_status.containsKey(schedule.getUser())) {
					final String status = map_status.get(schedule.getUser());
					myrow.setUser_status(status);
				}

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), myrow);
			}

			// set correct day
			final int day_on_current_calendar = this.getDayOfSchedule(schedule.getDate_schedule());
			final ItemRowSchedule itemsRow = this.getItemRowSchedule(myrow, day_on_current_calendar, schedule, true);

			if (day_on_current_calendar == 1) {
				myrow.setItem_1(itemsRow);
			}

		}

		// get all user to schedule
		final List<Person> users_schedule = this.personDAO.listWorkerPersons(text_search_person);

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

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.grid_scheduler.setPageSize(this.shows_rows.getValue());
		}
		this.grid_scheduler.setModel(new ListModelList<RowSchedule>(list_row));

	}

	/**
	 * @param info_visibility
	 *            if true set info scheduler for programming visible
	 */
	private void setupGlobalSchedulerGridForShiftReview() {

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.firstDateInGrid);

		final Date ret = calendar.getTime();
		final Date date_schedule = DateUtils.truncate(ret, Calendar.DATE);

		// create a map for define people scheduled
		final HashMap<Integer, RowSchedule> sign_scheduled = new HashMap<Integer, RowSchedule>();

		// take info about person
		String text_search_person = null;
		if ((this.full_text_search.getValue() == null) || this.full_text_search.getValue().equals("")) {
			text_search_person = null;
		} else {
			text_search_person = this.full_text_search.getValue();
		}

		// get info on program
		final List<Schedule> list_revision = this.scheduleDAO.selectAggregateSchedulersRevision(date_schedule, text_search_person);

		final ArrayList<RowSchedule> list_row = new ArrayList<RowSchedule>();
		RowSchedule currentRow = null;

		for (int i = 0; i < list_revision.size(); i++) {

			final Schedule schedule = list_revision.get(i);

			// if the user is changed, add another row
			if ((currentRow == null) || (!currentRow.getUser().equals(schedule.getUser()))) {
				// set current row
				currentRow = new RowSchedule();
				currentRow.setUser(schedule.getUser());
				currentRow.setName_user(schedule.getName_user());
				list_row.add(currentRow);

				// set items for current row
				currentRow.setItem_2(new ItemRowSchedule(currentRow, schedule));

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), currentRow);
			}

			// set day 2
			final ItemRowSchedule itemsRow = this.getItemRowSchedule(currentRow, 2, schedule, false);
			currentRow.setItem_2(itemsRow);

		}

		// get info on program
		final List<Schedule> list_program = this.scheduleDAO.selectAggregateSchedulersProgram(date_schedule, text_search_person);

		for (int i = 0; i < list_program.size(); i++) {

			final Schedule schedule = list_program.get(i);

			// get row
			RowSchedule myRow = sign_scheduled.get(schedule.getUser());

			// if the user is changed, add another row
			if ((myRow == null) || (!myRow.getUser().equals(schedule.getUser()))) {
				// set current row
				myRow = new RowSchedule();
				myRow.setUser(schedule.getUser());
				myRow.setName_user(schedule.getName_user());
				list_row.add(myRow);

				// set items for current row
				myRow.setItem_1(new ItemRowSchedule(myRow, schedule));

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), myRow);
			}

			// set day 1
			final ItemRowSchedule itemsRow = this.getItemRowSchedule(myRow, 1, schedule, false);
			myRow.setItem_1(itemsRow);

		}

		// get all user to schedule
		final List<Person> users_schedule = this.personDAO.listWorkerPersons(text_search_person);

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

		// set grid
		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.grid_scheduler_review.setPageSize(this.shows_rows.getValue());
		}
		this.grid_scheduler_review.setModel(new ListModelList<RowSchedule>(list_row));

	}

	/**
	 * Show statistic popup
	 *
	 * @param id_user
	 * @param anchorComponent
	 * @param title
	 */
	private void showStatisticsPopup(final Integer id_user, final Component anchorComponent, final String title) {

		// reset old values
		SchedulerComposer.this.shift_perc_1.setValue("");
		SchedulerComposer.this.shift_perc_2.setValue("");
		SchedulerComposer.this.shift_perc_3.setValue("");
		SchedulerComposer.this.shift_perc_4.setValue("");

		final Double perc = SchedulerComposer.this.statisticDAO.getSundayWorkPercentage(id_user);

		String perc_info = "0%";
		if (perc != null) {
			perc_info = "" + Utility.roundTwo(perc) + "%";
		}

		// set perc
		SchedulerComposer.this.work_sunday_perc.setValue(perc_info);

		SchedulerComposer.this.label_statistic_popup.setLabel(title);

		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);

		final RateShift[] statistic = SchedulerComposer.this.statisticDAO.getAverageForShift(id_user, c.getTime());

		if (statistic != null) {
			for (final RateShift av : statistic) {

				if (av.getShift() == 1) {
					SchedulerComposer.this.shift_perc_1.setValue("" + Utility.roundTwo(av.getRate()) + "%");
				}
				if (av.getShift() == 2) {
					SchedulerComposer.this.shift_perc_2.setValue("" + Utility.roundTwo(av.getRate()) + "%");
				}
				if (av.getShift() == 3) {
					SchedulerComposer.this.shift_perc_3.setValue("" + Utility.roundTwo(av.getRate()) + "%");
				}
				if (av.getShift() == 4) {
					SchedulerComposer.this.shift_perc_4.setValue("" + Utility.roundTwo(av.getRate()) + "%");
				}
			}
		}

		// set info about week working
		final Calendar current = Calendar.getInstance();
		final Date date_to = current.getTime();
		current.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		Date date_from = current.getTime();

		Integer week_current_hours = this.statisticDAO.getTimeWorked(id_user, date_from, date_to);
		if (week_current_hours == null) {
			week_current_hours = 0;
		}

		this.work_current_week.setValue("" + week_current_hours);

		// set info about month working
		current.set(Calendar.DAY_OF_MONTH, current.getActualMinimum(Calendar.DAY_OF_MONTH));
		date_from = current.getTime();

		Integer month_current_hours = this.statisticDAO.getTimeWorked(id_user, date_from, date_to);
		if (month_current_hours == null) {
			month_current_hours = 0;
		}

		this.work_current_month.setValue("" + month_current_hours);

		// set info about year working
		current.set(Calendar.YEAR, current.get(Calendar.YEAR));
		current.set(Calendar.WEEK_OF_YEAR, 1);
		current.set(Calendar.DAY_OF_YEAR, 1);
		date_from = current.getTime();

		Integer year_current_hours = this.statisticDAO.getTimeWorked(id_user, date_from, date_to);
		if (year_current_hours == null) {
			year_current_hours = 0;
		}

		this.work_current_year.setValue("" + year_current_hours);

		SchedulerComposer.this.day_name_popup.open(anchorComponent, "after_pointer");
	}
}
