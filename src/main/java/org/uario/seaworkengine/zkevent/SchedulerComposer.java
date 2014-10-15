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
import org.uario.seaworkengine.model.Detail_Schedule;
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
	private List<Detail_Schedule>	list_details;

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

	@Listen("onChange = #date_init_scheduler")
	public void changeInitialDate() {

		this.info_scheduler.setVisible(false);
		this.setGridStructure(SchedulerComposer.this.date_init_scheduler.getValue());
		this.setupValuesGrid();

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
				SchedulerComposer.this.setupValuesGrid();
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
		this.setupValuesGrid();
	}

	/**
	 * Save Current scehduler updating values from grid
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

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentScheduler();
		}

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
	 * setup RowScheduler
	 *
	 * @param initial_date
	 * @param final_date
	 * @return
	 */
	private List<RowSchedule> setupRowScheduler(final Date initial_date, final Date final_date) {

		final List<Schedule> list = this.scheduleDAO.selectSchedulers(initial_date, final_date);

		final ArrayList<RowSchedule> ret = new ArrayList<RowSchedule>();

		RowSchedule currentRow = null;

		// The number of the day we are showing in the scheduler (in the first
		// implementation max 7 day in a row)
		int day = 1;

		for (int i = 0; i < list.size(); i++) {

			final Schedule schedule = list.get(i);

			// if the user is changed, add another row
			if (currentRow != null) {
				if (!currentRow.getUser().equals(schedule.getUser())) {
					day = 1;
				}
			}

			switch (day) {
			case 1: {
				currentRow = new RowSchedule();

				// set item row
				final ItemRowSchedule itemsRow = this.getItemRowSchedule(schedule);

				currentRow.setItem_1(itemsRow);
				currentRow.setName_user(schedule.getName_user());
				currentRow.setUser(schedule.getUser());
				day++;

				ret.add(currentRow);
				break;
			}
			case 2: {

				// set item row
				final ItemRowSchedule itemsRow = this.getItemRowSchedule(schedule);

				currentRow.setItem_2(itemsRow);
				day++;
				break;
			}
			case 3: {

				// set item row
				final ItemRowSchedule itemsRow = this.getItemRowSchedule(schedule);

				currentRow.setItem_3(itemsRow);
				day++;
				break;
			}
			case 4: {

				// set item row
				final ItemRowSchedule itemsRow = this.getItemRowSchedule(schedule);

				currentRow.setItem_4(itemsRow);
				day++;
				break;
			}
			case 5: {

				// set item row
				final ItemRowSchedule itemsRow = this.getItemRowSchedule(schedule);

				currentRow.setItem_5(itemsRow);
				day++;
				break;
			}
			case 6: {

				// set item row
				final ItemRowSchedule itemsRow = this.getItemRowSchedule(schedule);

				currentRow.setItem_2(itemsRow);
				day = 0;
				currentRow = null;
				break;
			}
			}

		}

		return ret;

	}

	/**
	 * setup values for grid
	 */
	private void setupValuesGrid() {

		final Date initial_date = DateUtils.truncate(this.date_init_scheduler.getValue(), Calendar.DATE);
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAYS_IN_GRID);
		final Date final_date = calendar.getTime();

		final List<RowSchedule> list_row = this.setupRowScheduler(initial_date, final_date);

		this.grid_scheduler.setModel(new ListModelList<RowSchedule>(list_row));

		// close info scheduler
		this.info_scheduler.setVisible(false);

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

			// set initial program and revision
			this.list_details = this.scheduleDAO.loadDetail_ScheduleByIdSchedule(this.currentSchedule.getId());
			this.listbox_program.setModel(new ListModelList<Detail_Schedule>(this.list_details));
			this.listbox_revision.setModel(new ListModelList<Detail_Schedule>(this.list_details));

		} else {
			// if we haven't information about schedule
			SchedulerComposer.this.revision_time_in.setValue(null);
			SchedulerComposer.this.revision_time_out.setValue(null);
		}

		// set combo task
		final List<UserTask> list = this.taskDAO.loadTasksByUser(row_scheduler.getUser());
		SchedulerComposer.this.program_task.setModel(new ListModelList<UserTask>(list));
		SchedulerComposer.this.revision_task.setModel(new ListModelList<UserTask>(list));

		// set note
		if (SchedulerComposer.this.currentSchedule != null) {
			SchedulerComposer.this.note.setValue(SchedulerComposer.this.currentSchedule.getNote());
		} else {
			SchedulerComposer.this.note.setValue(null);
		}

		// show info table
		SchedulerComposer.this.info_scheduler.setVisible(true);
	}
}