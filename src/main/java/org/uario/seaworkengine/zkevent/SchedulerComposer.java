package org.uario.seaworkengine.zkevent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.model.DetailSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
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
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class SchedulerComposer extends SelectorComposer<Component> {

	private static final int		DAYS_IN_GRID_PROGRAM				= 7;

	/**
	 *
	 */
	private static final long		serialVersionUID			= 1L;

	private Schedule				currentSchedule;

	@Wire
	private Datebox					date_init_scheduler;

	// format
	private final SimpleDateFormat	formatter_ddmmm				= new SimpleDateFormat("dd/MMM");
	private final SimpleDateFormat	formatter_eeee				= new SimpleDateFormat("EEEE");
	private final SimpleDateFormat	formatter_scheduler_info	= new SimpleDateFormat("EEEE dd MMM");

	@Wire
	private Listbox					grid_scheduler;

	@Wire
	private Div						info_scheduler;

	// initial program and revision
	private List<DetailSchedule>	list_details_program;

	@Wire
	private Listbox					listbox_program;

	@Wire
	private Listbox					listbox_revision;

	private final Logger			logger						= Logger.getLogger(SchedulerComposer.class);

	@Wire
	private Textbox					note;

	private PersonDAO				personDAO;

	@Wire
	private Div						preprocessing_div;

	@Wire
	private Comboitem				preprocessing_item;

	@Wire
	private Div						program_div;

	@Wire
	private Comboitem				program_item;

	@Wire
	private Combobox				program_task;

	@Wire
	private Intbox					program_time;

	@Wire
	private Combobox				revision_task;

	@Wire
	private Intbox					revision_time;

	@Wire
	private Datebox					revision_time_in;

	@Wire
	private Datebox					revision_time_out;

	private ISchedule				scheduleDAO;

	@Wire
	private A						scheduler_label;

	@Wire
	private Combobox				scheduler_type_selector;

	// selected week
	private Integer					selectedDay;

	// selected shift
	private Integer					selectedShift;

	/**
	 * User selected to schedule
	 */
	private Integer					selectedUser;

	private TasksDAO				taskDAO;

	@Listen("onClick= #add_program_item")
	public void addProgramItem() {

		if (this.list_details_program == null) {
			return;
		}

		if (this.selectedShift == null) {
			return;
		}

		if (this.program_task.getSelectedItem() == null) {
			Messagebox.show("Assegnare una mansione all'utente selezionato, prima di procedere alla programmazione", "INFO", Messagebox.OK,
					Messagebox.EXCLAMATION);
			return;
		}

		final UserTask task = this.program_task.getSelectedItem().getValue();
		if (task == null) {
			Messagebox.show("Assegna una mansione", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		final Integer time = this.program_time.getValue();

		if (time == null) {
			Messagebox.show("Definire il numero di ore da lavorare", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		// check about sum of time
		boolean check_sum = true;
		if (time > 6) {
			check_sum = false;
		}
		if (this.list_details_program.size() != 0) {
			int sum = time;
			for (final DetailSchedule detail : this.list_details_program) {
				final int current_time = detail.getTime_initial();
				sum = sum + current_time;
				if (sum > 6) {
					check_sum = false;
					break;
				}
			}
		}
		if (!check_sum) {
			Messagebox.show("Non si possono assegnare più di sei ore per turno", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentScheduler();
		}

		final DetailSchedule new_item = new DetailSchedule();
		new_item.setId_schedule(this.currentSchedule.getId());
		new_item.setShift(this.selectedShift);
		new_item.setTime_initial(time);
		new_item.setTask_initial(task.getId());

		// update program list
		this.list_details_program.add(new_item);
		final ListModelList<DetailSchedule> model = new ListModelList<DetailSchedule>(this.list_details_program);
		model.setMultiple(true);
		this.listbox_program.setModel(model);

	}

	@Listen("onChange = #date_init_scheduler")
	public void changeInitialDate() {

		this.info_scheduler.setVisible(false);
		this.setGridStructureForShift(SchedulerComposer.this.date_init_scheduler.getValue());
		this.setupGlobalSchedulerGridForShift(false);

	}

	private String defineAnchorContent(final boolean program, final Schedule schedule) {
		Integer time = null;
		if (program) {
			time = schedule.getProgram_time();
		}

		if (time != null) {
			return "" + time;
		}

		return null;

	}

	@Listen("onChange = #scheduler_type_selector")
	public void defineSchedulerType() {

		if (this.scheduler_type_selector.getSelectedItem() == null) {
			return;
		}

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected == this.preprocessing_item) {
			this.preprocessing_div.setVisible(true);
			this.program_div.setVisible(false);

			// set initial structure for program
			SchedulerComposer.this.setGridStructureForDay(SchedulerComposer.this.date_init_scheduler.getValue());
			SchedulerComposer.this.setupGlobalSchedulerGridForDay(false);
		}

		if (selected == this.program_item) {
			this.preprocessing_div.setVisible(false);
			this.program_div.setVisible(true);

			// set initial structure for program
			SchedulerComposer.this.setGridStructureForShift(SchedulerComposer.this.date_init_scheduler.getValue());
			SchedulerComposer.this.setupGlobalSchedulerGridForShift(false);
		}

	}

	@Override
	public void doFinally() throws Exception {

		SchedulerComposer.this.date_init_scheduler.setValue(Calendar.getInstance().getTime());

		this.scheduleDAO = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);
		this.taskDAO = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);
		this.personDAO = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);

		this.getSelf().addEventListener(ZkEventsTag.onShowScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// set preprocessing item in combo selection
				SchedulerComposer.this.scheduler_type_selector.setSelectedItem(SchedulerComposer.this.preprocessing_item);

				// set initial structure for program
				SchedulerComposer.this.setGridStructureForDay(SchedulerComposer.this.date_init_scheduler.getValue());
				SchedulerComposer.this.setupGlobalSchedulerGridForDay(false);

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
		calendar_day.setTime(SchedulerComposer.this.date_init_scheduler.getValue());
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
	private int getDayOfSchedule(final Date initial_date, final Schedule schedule) {
		if (schedule == null) {
			this.logger.error("Schedule null not permitted");
			throw new IllegalArgumentException("Schedule null not permitted");
		}
		if (schedule.getDate_schedule() == null) {
			// if not date scheduler, put it at first day
			return 1;
		}

		final Date date_init_truncate = DateUtils.truncate(initial_date, Calendar.DATE);
		final Date schedule_date_truncate = DateUtils.truncate(schedule.getDate_schedule(), Calendar.DATE);

		final long millis = schedule_date_truncate.getTime() - date_init_truncate.getTime();
		final long day_elapsed = millis / (1000 * 60 * 60 * 24);

		return (int) (day_elapsed + 1);

	}

	/**
	 * Get a new Item Row from a schedule
	 *
	 * @param schedule
	 */
	private ItemRowSchedule getItemRowSchedule(final RowSchedule currentRow, final Integer day_on_current_calendar, final Schedule schedule) {

		ItemRowSchedule itemsRow = null;

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

		if (day_on_current_calendar == 6) {
			itemsRow = currentRow.getItem_5();
		}

		if (day_on_current_calendar == 7) {
			itemsRow = currentRow.getItem_7();
		}

		if (schedule.getId() != null) {
			if (schedule.getShift() != null) {

				if (schedule.getShift() == 1) {
					itemsRow.setAnchor1(this.defineAnchorContent(true, schedule));
				}

				if (schedule.getShift() == 2) {
					itemsRow.setAnchor2(this.defineAnchorContent(true, schedule));
				}

				if (schedule.getShift() == 3) {
					itemsRow.setAnchor3(this.defineAnchorContent(true, schedule));
				}

				if (schedule.getShift() == 4) {
					itemsRow.setAnchor4(this.defineAnchorContent(true, schedule));
				}
			}

		}

		return itemsRow;
	}

	@Listen("onClick = #refresh_command")
	public void refreshButton() {

		SchedulerComposer.this.date_init_scheduler.setValue(Calendar.getInstance().getTime());
		this.setGridStructureForShift(this.date_init_scheduler.getValue());
		this.setupGlobalSchedulerGridForShift(false);
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
			final DetailSchedule detail_item = itm.getValue();
			this.list_details_program.remove(detail_item);
		}

		// set model list program and revision
		final ListModelList<DetailSchedule> model = new ListModelList<DetailSchedule>(this.list_details_program);
		model.setMultiple(true);
		this.listbox_program.setModel(model);

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

		this.scheduleDAO.saveOrUpdate(this.currentSchedule);

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
			for (final DetailSchedule detail : this.list_details_program) {
				sum = sum + detail.getTime_initial();
			}
		}
		if (sum < 6) {
			Messagebox.show("Non si possono assegnare meno di sei ore per turno", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;

		}

		this.scheduleDAO.saveListDetailScheduler(this.currentSchedule.getId(), this.selectedShift, this.list_details_program);

		// refresh grid, but keep the info editor visible
		this.setupGlobalSchedulerGridForShift(true);

		Messagebox.show("Il programma è stato aggiornato", "INFO", Messagebox.OK, Messagebox.INFORMATION);

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
		this.currentSchedule.setFrom_time(this.revision_time_in.getValue());
		this.currentSchedule.setTo_time(this.revision_time_out.getValue());

		// save scheduler
		this.saveCurrentScheduler();

		Messagebox.show("Il Report è stato aggiornato", "INFO", Messagebox.OK, Messagebox.INFORMATION);

	}

	/**
	 * Save program
	 */
	@Listen("onClick = #ok_revision")
	public void saveRevision() {

		if ((this.selectedDay == null) || (this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.currentSchedule == null) {

			// save scheduler
			this.saveCurrentScheduler();
		}

		Messagebox.show("Il consuntivo è stato aggiornato", "INFO", Messagebox.OK, Messagebox.INFORMATION);

	}

	/**
	 * initial structure
	 *
	 * @param initial_date
	 */
	private void setGridStructureForDay(final Date initial_date) {

	}

	/**
	 * initial structure
	 *
	 * @param initial_date
	 */
	private void setGridStructureForShift(final Date initial_date) {
		if (initial_date == null) {
			return;
		}

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(initial_date);

		// set seven days
		for (int i = 0; i < SchedulerComposer.DAYS_IN_GRID_PROGRAM; i++) {

			final int index_day = i + 1;

			final Auxheader month_head = (Auxheader) this.getSelf().getFellowIfAny("day_month_" + index_day);
			final Auxheader week_head = (Auxheader) this.getSelf().getFellowIfAny("day_week_" + index_day);
			if ((month_head == null) || (week_head == null)) {
				continue;
			}

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(calendar.getTime());
			current_calendar.add(Calendar.DAY_OF_YEAR, i);

			final String day_w = this.formatter_eeee.format(current_calendar.getTime());
			final String day_m = this.formatter_ddmmm.format(current_calendar.getTime());

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
	private void setupGlobalSchedulerGridForDay(final boolean info_visibility) {

	}

	/**
	 * @param info_visibility
	 *            if true set info scheduler for programming visible
	 */
	private void setupGlobalSchedulerGridForShift(final boolean info_visibility) {

		final Date initial_date = DateUtils.truncate(this.date_init_scheduler.getValue(), Calendar.DATE);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(initial_date);
		calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAYS_IN_GRID_PROGRAM);
		final Date final_date = calendar.getTime();

		final List<Schedule> list = this.scheduleDAO.selectSchedulers(initial_date, final_date);

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
				currentRow.setItem_1(new ItemRowSchedule(schedule));
				currentRow.setItem_2(new ItemRowSchedule(schedule));
				currentRow.setItem_3(new ItemRowSchedule(schedule));
				currentRow.setItem_4(new ItemRowSchedule(schedule));
				currentRow.setItem_5(new ItemRowSchedule(schedule));
				currentRow.setItem_6(new ItemRowSchedule(schedule));
				currentRow.setItem_7(new ItemRowSchedule(schedule));

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), new Object());
			}

			// set correct day
			final int day_on_current_calendar = this.getDayOfSchedule(initial_date, schedule);
			final ItemRowSchedule itemsRow = this.getItemRowSchedule(currentRow, day_on_current_calendar, schedule);

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

			if (day_on_current_calendar == 6) {
				currentRow.setItem_6(itemsRow);
			}

			if (day_on_current_calendar == 7) {
				currentRow.setItem_7(itemsRow);
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
			addedRow.setItem_1(new ItemRowSchedule());
			addedRow.setItem_2(new ItemRowSchedule());
			addedRow.setItem_3(new ItemRowSchedule());
			addedRow.setItem_4(new ItemRowSchedule());
			addedRow.setItem_5(new ItemRowSchedule());
			addedRow.setItem_6(new ItemRowSchedule());
			addedRow.setItem_7(new ItemRowSchedule());

			list_row.add(addedRow);

		}

		this.grid_scheduler.setModel(new ListModelList<RowSchedule>(list_row));

		// close info scheduler
		this.info_scheduler.setVisible(info_visibility);

	}

	/**
	 * Shift configurator
	 *
	 * @param data_info
	 */
	private void shiftConfigurator(final String data_info) {

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
				+ SchedulerComposer.this.formatter_scheduler_info.format(date_schedule) + ". Turno: " + SchedulerComposer.this.selectedShift);

		// if any information about schedule...
		if (SchedulerComposer.this.currentSchedule != null) {
			if (SchedulerComposer.this.currentSchedule.getFrom_time() == null) {
				SchedulerComposer.this.revision_time_in.setValue(null);
			} else {
				SchedulerComposer.this.revision_time_in.setValue(SchedulerComposer.this.currentSchedule.getFrom_time());
			}

			if (SchedulerComposer.this.currentSchedule.getTo_time() == null) {
				SchedulerComposer.this.revision_time_out.setValue(null);
			} else {
				SchedulerComposer.this.revision_time_out.setValue(SchedulerComposer.this.currentSchedule.getTo_time());
			}

			// set note
			SchedulerComposer.this.note.setValue(SchedulerComposer.this.currentSchedule.getNote());

			// set initial program and revision
			this.list_details_program = this.scheduleDAO.loadDetailScheduleByIdScheduleAndShift(this.currentSchedule.getId(), this.selectedShift);

			// TODO: set revision list
			// this.listbox_revision.setModel(new
			// ListModelList<Detail_Schedule>(this.list_details));

		} else {
			// if we haven't information about schedule
			SchedulerComposer.this.revision_time_in.setValue(null);
			SchedulerComposer.this.revision_time_out.setValue(null);
			this.note.setValue(null);
			this.listbox_program.getItems().clear();
			this.listbox_revision.getItems().clear();

			// set list program and revision
			this.list_details_program = new ArrayList<DetailSchedule>();

		}

		// set model list program and revision
		final ListModelList<DetailSchedule> model = new ListModelList<DetailSchedule>(this.list_details_program);
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

		this.revision_task.setSelectedItem(null);
		this.revision_task.getChildren().clear();

		this.revision_task.getItems().clear();
		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());
			this.revision_task.appendChild(combo_item);

			// set if default
			if (task_item.getTask_default()) {
				this.revision_task.setSelectedItem(combo_item);
			}

		}

		// show info table
		SchedulerComposer.this.info_scheduler.setVisible(true);
	}
}
