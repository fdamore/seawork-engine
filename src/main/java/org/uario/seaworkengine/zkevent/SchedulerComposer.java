package org.uario.seaworkengine.zkevent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.model.DetailSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class SchedulerComposer extends SelectorComposer<Component> {

	private static final int		DAYS_IN_GRID				= 7;

	/**
	 *
	 */
	private static final long		serialVersionUID			= 1L;

	private ConfigurationDAO		configurationDAO;

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

	@Wire
	private Comboitem				program_item;

	@Wire
	private Combobox				program_task;

	@Wire
	private Intbox					program_time;

	@Wire
	private Combobox				review;

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

	// selected week
	private String					selectedDay;

	// selected shift
	private String					selectedShift;

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

		if ((this.selectedShift == null) || !NumberUtils.isNumber(this.selectedShift)) {
			return;
		}

		if (this.program_task.getSelectedItem() == null) {
			return;
		}

		final UserTask task = this.program_task.getSelectedItem().getValue();
		if (task == null) {
			Messagebox.show("Assegna una mansione", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		final Integer time = this.program_time.getValue();
		if (time == null) {
			Messagebox.show("Definisce numero di ore da lavorate", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentScheduler();
		}

		final DetailSchedule new_item = new DetailSchedule();
		new_item.setId_schedule(this.currentSchedule.getId());
		new_item.setShift(Integer.parseInt(this.selectedShift));
		new_item.setTime_initial(time);
		new_item.setTask_initial(task.getId());

		// update program list
		this.list_details_program.add(new_item);
		this.listbox_program.setModel(new ListModelList<DetailSchedule>(this.list_details_program));

	}

	@Listen("onChange = #date_init_scheduler")
	public void changeInitialDate() {

		this.info_scheduler.setVisible(false);
		this.setGridStructure(SchedulerComposer.this.date_init_scheduler.getValue());
		this.setupGlobalSchedulerGrid(false);

	}

	@Override
	public void doFinally() throws Exception {

		SchedulerComposer.this.date_init_scheduler.setValue(Calendar.getInstance().getTime());

		this.scheduleDAO = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);
		this.configurationDAO = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
		this.taskDAO = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

		this.getSelf().addEventListener(ZkEventsTag.onShowScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// set initial structure
				SchedulerComposer.this.setGridStructure(SchedulerComposer.this.date_init_scheduler.getValue());
				SchedulerComposer.this.setupGlobalSchedulerGrid(false);
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
	private Date getDateScheduled(final String day) {
		final Integer days = Integer.parseInt(day);
		final int to_add = days.intValue() - 1;

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
	private ItemRowSchedule getItemRowSchedule(final Schedule schedule) {

		final ItemRowSchedule itemsRow = new ItemRowSchedule();

		if (schedule.getId() != null) {
			itemsRow.setAnchor1("" + schedule.getId());
			itemsRow.setAnchor2("" + schedule.getId());
			itemsRow.setAnchor3("" + schedule.getId());
			itemsRow.setAnchor4("" + schedule.getId());
			itemsRow.setSchedule(schedule);
		}

		return itemsRow;
	}

	@Listen("onClick = #refresh_command")
	public void refreshButton() {

		SchedulerComposer.this.date_init_scheduler.setValue(Calendar.getInstance().getTime());
		this.setGridStructure(this.date_init_scheduler.getValue());
		this.setupGlobalSchedulerGrid(false);
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

		this.scheduleDAO.saveListDetailScheduler(this.currentSchedule.getId(), this.list_details_program);

		// refresh grid, but keep the info editor visible
		this.setupGlobalSchedulerGrid(true);

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
	private void setGridStructure(final Date initial_date) {
		if (initial_date == null) {
			return;
		}

		// set program version
		this.review.setSelectedItem(this.program_item);

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(initial_date);

		// set seven days
		for (int i = 0; i < SchedulerComposer.DAYS_IN_GRID; i++) {

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
	 * setup values for grid
	 */
	private void setupGlobalSchedulerGrid(final boolean info_visibility) {

		final Date initial_date = DateUtils.truncate(this.date_init_scheduler.getValue(), Calendar.DATE);
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAYS_IN_GRID);
		final Date final_date = calendar.getTime();

		final List<RowSchedule> list_row = this.setupRowsScheduler(initial_date, final_date);

		this.grid_scheduler.setModel(new ListModelList<RowSchedule>(list_row));

		// close info scheduler
		this.info_scheduler.setVisible(info_visibility);

	}

	/**
	 * setup RowScheduler
	 *
	 * @param initial_date
	 * @param final_date
	 * @return
	 */
	private List<RowSchedule> setupRowsScheduler(final Date initial_date, final Date final_date) {

		final List<Schedule> list = this.scheduleDAO.selectSchedulers(initial_date, final_date);

		final ArrayList<RowSchedule> ret = new ArrayList<RowSchedule>();

		RowSchedule currentRow = null;

		for (int i = 0; i < list.size(); i++) {

			final Schedule schedule = list.get(i);

			// if the user is changed, add another row
			if ((currentRow == null) || (!currentRow.getUser().equals(schedule.getUser()))) {
				// set current row
				currentRow = new RowSchedule();
				currentRow.setUser(schedule.getUser());
				currentRow.setName_user(schedule.getName_user());
				ret.add(currentRow);
			}

			// get day on calendar
			final int day_on_current_calendar = this.getDayOfSchedule(initial_date, schedule);

			for (int day = 1; day <= 7; day++) {

				ItemRowSchedule itemsRow;

				if (day != day_on_current_calendar) {
					itemsRow = new ItemRowSchedule();

				} else {
					itemsRow = this.getItemRowSchedule(schedule);
				}

				if (day == 1) {
					currentRow.setItem_1(itemsRow);
				}

				if (day == 2) {
					currentRow.setItem_2(itemsRow);
				}

				if (day == 3) {
					currentRow.setItem_3(itemsRow);
				}

				if (day == 4) {
					currentRow.setItem_4(itemsRow);
				}

				if (day == 5) {
					currentRow.setItem_5(itemsRow);
				}

				if (day == 6) {
					currentRow.setItem_6(itemsRow);
				}

				if (day == 7) {
					currentRow.setItem_7(itemsRow);
				}
			}

		}

		return ret;

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
		SchedulerComposer.this.selectedDay = info[1];
		SchedulerComposer.this.selectedShift = info[2];
		this.selectedUser = row_scheduler.getUser();

		// info check
		if (!NumberUtils.isNumber(SchedulerComposer.this.selectedDay) || !NumberUtils.isNumber(SchedulerComposer.this.selectedShift)) {

			Messagebox.show("Check Status Scheduler. Contact Uario S.r.L.", "INFO", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		final Date date_schedule = SchedulerComposer.this.getDateScheduled(SchedulerComposer.this.selectedDay);

		// take the right scheduler
		SchedulerComposer.this.currentSchedule = this.scheduleDAO.loadSchedule(date_schedule, this.selectedUser);

		// set label
		SchedulerComposer.this.scheduler_label.setLabel(row_scheduler.getName_user() + ". Giorno: "
				+ SchedulerComposer.this.formatter_scheduler_info.format(date_schedule) + ". Turno: " + SchedulerComposer.this.selectedShift);

		// if any information about schedule...
		if (SchedulerComposer.this.currentSchedule != null) {
			if (SchedulerComposer.this.currentSchedule.getFrom_time() == null) {
				SchedulerComposer.this.revision_time_in.setValue(date_schedule);
			} else {
				SchedulerComposer.this.revision_time_in.setValue(SchedulerComposer.this.currentSchedule.getFrom_time());
			}

			if (SchedulerComposer.this.currentSchedule.getTo_time() == null) {
				SchedulerComposer.this.revision_time_out.setValue(date_schedule);
			} else {
				SchedulerComposer.this.revision_time_out.setValue(SchedulerComposer.this.currentSchedule.getTo_time());
			}

			// set note
			SchedulerComposer.this.note.setValue(SchedulerComposer.this.currentSchedule.getNote());

			// set initial program and revision
			this.list_details_program = this.scheduleDAO.loadDetailScheduleByIdSchedule(this.currentSchedule.getId());
			this.listbox_program.setModel(new ListModelList<DetailSchedule>(this.list_details_program));

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

		}

		// set combo task
		final List<UserTask> list = this.taskDAO.loadTasksByUser(row_scheduler.getUser());
		SchedulerComposer.this.program_task.setModel(new ListModelList<UserTask>(list));
		SchedulerComposer.this.revision_task.setModel(new ListModelList<UserTask>(list));

		// show info table
		SchedulerComposer.this.info_scheduler.setVisible(true);
	}
}