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
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.uario.seaworkengine.zkevent.bean.ItemRowScheduler;
import org.uario.seaworkengine.zkevent.bean.RowScheduler;
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

		this.getSelf().addEventListener(ZkEventsTag.onShowScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// set combo task
				final List<UserShift> list = SchedulerComposer.this.configurationDAO.loadShifts();
				SchedulerComposer.this.program_task.setModel(new ListModelList<UserShift>(list));
				SchedulerComposer.this.revision_task.setModel(new ListModelList<UserShift>(list));

				// set initial structure
				SchedulerComposer.this.setGridStructure(SchedulerComposer.this.date_init_scheduler.getValue());
				SchedulerComposer.this.setupValuesGrid();
			}
		});

		this.getSelf().addEventListener(ZkEventsTag.onShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				if (SchedulerComposer.this.grid_scheduler.getSelectedItem() == null) {
					return;

				}

				// for of shift --> shift_1_4
				final String data_info = arg0.getData().toString();
				final String[] info = data_info.split("_");
				SchedulerComposer.this.selectedDay = info[1];
				SchedulerComposer.this.selectedShift = info[2];

				// take the right scheduler
				final Schedule scheduler = SchedulerComposer.this.getCurrentScheduler(SchedulerComposer.this.selectedDay,
						SchedulerComposer.this.selectedShift);

				if (scheduler == null) {
					return;
				}

				// set date
				if (NumberUtils.isNumber(SchedulerComposer.this.selectedDay)) {

					final Date current_day = SchedulerComposer.this.getDateScheduled(SchedulerComposer.this.selectedDay);

					// set label
					SchedulerComposer.this.scheduler_label.setLabel("Giorno: " + SchedulerComposer.this.formatter_scheduler_info.format(current_day)
							+ ". Turno: " + SchedulerComposer.this.selectedShift);

					if (scheduler.getFrom_time() == null) {
						SchedulerComposer.this.revision_time_in.setValue(current_day);
					} else {
						SchedulerComposer.this.revision_time_in.setValue(scheduler.getFrom_time());
					}

					if (scheduler.getTo_time() == null) {
						SchedulerComposer.this.revision_time_out.setValue(current_day);
					} else {
						SchedulerComposer.this.revision_time_out.setValue(scheduler.getTo_time());
					}

				}

				// set note
				SchedulerComposer.this.note.setValue(scheduler.getNote());

				// show info table
				SchedulerComposer.this.info_scheduler.setVisible(true);

			}
		});

	}

	/**
	 * Get a scheduler on the grid by day and shift
	 *
	 * @param selectedDay2
	 * @param selectedShift2
	 * @return
	 */
	private Schedule getCurrentScheduler(final String selectDay, final String selectShift) {

		return SchedulerComposer.this.grid_scheduler.getSelectedItem().getValue();
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

		return calendar_day.getTime();
	}

	@Listen("onClick = #refresh_command")
	public void refreshButton() {

		SchedulerComposer.this.date_init_scheduler.setValue(Calendar.getInstance().getTime());
		this.setGridStructure(this.date_init_scheduler.getValue());
		this.setupValuesGrid();
	}

	/**
	 * Save program
	 */
	@Listen("onClick = #ok_program")
	public void saveProgram() {

		if (this.currentSchedule == null) {
			return;
		}

		final String shift = this.selectedShift;
		final String day = this.selectedDay;

		// set task and time
		final Integer initial_time_task = this.program_time.getValue();

		Integer initial_shift = null;
		if (this.program_task.getSelectedItem() != null) {
			final UserShift initial_shift_ob = this.program_task.getSelectedItem().getValue();
			if (initial_shift_ob != null) {
				initial_shift = initial_shift_ob.getId();
			}
		}

		// save note
		this.currentSchedule.setNote(this.note.getValue());

		// set editor
		final Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		this.currentSchedule.setEditor(person.getId());

		// set data scheduler
		final Date date_schedule = this.getDateScheduled(day);
		this.currentSchedule.setDate_schedule(date_schedule);

		// save scheduler
		this.scheduleDAO.saveOrUpdate(this.currentSchedule);

		// refresh grid
		this.setupValuesGrid();

		Messagebox.show("Il programma è stato aggiornato", "INFO", Messagebox.OK, Messagebox.INFORMATION);

	}

	/**
	 * Save program
	 */
	@Listen("onClick = #ok_revision")
	public void saveRevision() {

		if (this.currentSchedule == null) {
			return;
		}

		// set time in and out
		this.currentSchedule.setFrom_time(this.revision_time_in.getValue());
		this.currentSchedule.setTo_time(this.revision_time_out.getValue());

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
	private List<RowScheduler> setupRowScheduler(final Date initial_date, final Date final_date) {

		final List<Schedule> list = this.scheduleDAO.selectSchedulers(initial_date, final_date);

		final ArrayList<RowScheduler> ret = new ArrayList<RowScheduler>();

		RowScheduler currentRow = null;

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
				currentRow = new RowScheduler();

				final ItemRowScheduler itemsRow = new ItemRowScheduler();
				if (schedule.getId() != null) {
					itemsRow.setAnchor1("" + schedule.getId());
					itemsRow.setAnchor2("" + schedule.getId());
					itemsRow.setAnchor3("" + schedule.getId());
					itemsRow.setAnchor4("" + schedule.getId());
				}

				currentRow.setItem_1(itemsRow);
				currentRow.setName_user(schedule.getName_user());
				currentRow.setUser(schedule.getUser());
				day++;

				ret.add(currentRow);
				break;
			}
			case 2: {
				final ItemRowScheduler itemsRow = new ItemRowScheduler();
				itemsRow.setAnchor1("" + schedule.getId());
				itemsRow.setAnchor2("" + schedule.getId());
				itemsRow.setAnchor3("" + schedule.getId());
				itemsRow.setAnchor4("" + schedule.getId());

				currentRow.setItem_2(itemsRow);
				day++;
				break;
			}
			case 3: {
				final ItemRowScheduler itemsRow = new ItemRowScheduler();
				itemsRow.setAnchor1("" + schedule.getId());
				itemsRow.setAnchor2("" + schedule.getId());
				itemsRow.setAnchor3("" + schedule.getId());
				itemsRow.setAnchor4("" + schedule.getId());

				currentRow.setItem_3(itemsRow);
				day++;
				break;
			}
			case 4: {
				final ItemRowScheduler itemsRow = new ItemRowScheduler();
				itemsRow.setAnchor1("" + schedule.getId());
				itemsRow.setAnchor2("" + schedule.getId());
				itemsRow.setAnchor3("" + schedule.getId());
				itemsRow.setAnchor4("" + schedule.getId());

				currentRow.setItem_4(itemsRow);
				day++;
				break;
			}
			case 5: {
				final ItemRowScheduler itemsRow = new ItemRowScheduler();
				itemsRow.setAnchor1("" + schedule.getId());
				itemsRow.setAnchor2("" + schedule.getId());
				itemsRow.setAnchor3("" + schedule.getId());
				itemsRow.setAnchor4("" + schedule.getId());

				currentRow.setItem_5(itemsRow);
				day++;
				break;
			}
			case 6: {
				final ItemRowScheduler itemsRow = new ItemRowScheduler();
				itemsRow.setAnchor1("" + schedule.getId());
				itemsRow.setAnchor2("" + schedule.getId());
				itemsRow.setAnchor3("" + schedule.getId());
				itemsRow.setAnchor4("" + schedule.getId());

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

		// TODO: setup row scheduler
		final List<RowScheduler> list_row = this.setupRowScheduler(initial_date, final_date);

		this.grid_scheduler.setModel(new ListModelList<RowScheduler>(list_row));

		// close info scheduler
		this.info_scheduler.setVisible(false);

	}
}
