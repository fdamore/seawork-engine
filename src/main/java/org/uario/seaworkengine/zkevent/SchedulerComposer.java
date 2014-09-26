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
import org.uario.seaworkengine.model.Scheduler;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedulerDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
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

	@Wire
	private A						scheduler_label;

	private ISchedulerDAO			schedulerDAO;

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

		this.schedulerDAO = (ISchedulerDAO) SpringUtil.getBean(BeansTag.SCHEDULER_DAO);
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
				final Scheduler scheduler = SchedulerComposer.this.getCurrentScheduler(SchedulerComposer.this.selectedDay,
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

					if (scheduler.getFrom_ts() == null) {
						SchedulerComposer.this.revision_time_in.setValue(current_day);
					} else {
						SchedulerComposer.this.revision_time_in.setValue(scheduler.getFrom_ts());
					}

					if (scheduler.getTo_ts() == null) {
						SchedulerComposer.this.revision_time_out.setValue(current_day);
					} else {
						SchedulerComposer.this.revision_time_out.setValue(scheduler.getTo_ts());
					}

				}

				// set note
				SchedulerComposer.this.note.setValue(scheduler.getNote());

				// set task and hours working
				if (NumberUtils.isNumber(SchedulerComposer.this.selectedShift)) {
					final Integer shift = Integer.parseInt(SchedulerComposer.this.selectedShift);
					if (shift.intValue() == 1) {

						SchedulerComposer.this.program_time.setValue(scheduler.getInitial_time_1());
						SchedulerComposer.this.revision_time.setValue(scheduler.getFinal_time_1());

						SchedulerComposer.this.program_task.setSelectedItem(SchedulerComposer.this.selectItemShift(
								SchedulerComposer.this.program_task, scheduler.getInitial_shift_1()));
						SchedulerComposer.this.revision_task.setSelectedItem(SchedulerComposer.this.selectItemShift(
								SchedulerComposer.this.revision_task, scheduler.getFinal_shift_1()));

					}
					if (shift.intValue() == 2) {

						SchedulerComposer.this.program_time.setValue(scheduler.getInitial_time_2());
						SchedulerComposer.this.revision_time.setValue(scheduler.getFinal_time_2());

						SchedulerComposer.this.program_task.setSelectedItem(SchedulerComposer.this.selectItemShift(
								SchedulerComposer.this.program_task, scheduler.getInitial_shift_2()));
						SchedulerComposer.this.revision_task.setSelectedItem(SchedulerComposer.this.selectItemShift(
								SchedulerComposer.this.revision_task, scheduler.getFinal_shift_2()));

					}
					if (shift.intValue() == 3) {

						SchedulerComposer.this.program_time.setValue(scheduler.getInitial_time_3());
						SchedulerComposer.this.revision_time.setValue(scheduler.getFinal_time_3());

						SchedulerComposer.this.program_task.setSelectedItem(SchedulerComposer.this.selectItemShift(
								SchedulerComposer.this.program_task, scheduler.getInitial_shift_3()));
						SchedulerComposer.this.revision_task.setSelectedItem(SchedulerComposer.this.selectItemShift(
								SchedulerComposer.this.revision_task, scheduler.getFinal_shift_3()));

					}
					if (shift.intValue() == 4) {

						SchedulerComposer.this.program_time.setValue(scheduler.getInitial_time_4());
						SchedulerComposer.this.revision_time.setValue(scheduler.getFinal_time_4());

						SchedulerComposer.this.program_task.setSelectedItem(SchedulerComposer.this.selectItemShift(
								SchedulerComposer.this.program_task, scheduler.getInitial_shift_4()));
						SchedulerComposer.this.revision_task.setSelectedItem(SchedulerComposer.this.selectItemShift(
								SchedulerComposer.this.revision_task, scheduler.getFinal_shift_4()));

					}
				}

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
	private Scheduler getCurrentScheduler(final String selectedDay2, final String selectedShift2) {
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

		if (this.grid_scheduler.getSelectedItem() == null) {
			return;
		}

		final String shift = this.selectedShift;
		final String day = this.selectedDay;

		final Scheduler scheduler = this.getCurrentScheduler(day, shift);
		if (scheduler == null) {
			return;
		}

		// set task and time
		final Integer initial_time_task = this.program_time.getValue();

		Integer initial_shift = null;
		if (this.program_task.getSelectedItem() != null) {
			final UserShift initial_shift_ob = this.program_task.getSelectedItem().getValue();
			if (initial_shift_ob != null) {
				initial_shift = initial_shift_ob.getId();
			}
		}

		if (shift.equals("1")) {
			scheduler.setInitial_shift_1(initial_shift);
			scheduler.setInitial_time_1(initial_time_task);
		}

		if (shift.equals("2")) {
			scheduler.setInitial_shift_2(initial_shift);
			scheduler.setInitial_time_2(initial_time_task);
		}

		if (shift.equals("3")) {
			scheduler.setInitial_shift_3(initial_shift);
			scheduler.setInitial_time_3(initial_time_task);
		}
		if (shift.equals("4")) {
			scheduler.setInitial_shift_4(initial_shift);
			scheduler.setInitial_time_4(initial_time_task);
		}

		// save note
		scheduler.setNote(this.note.getValue());

		// set editor
		final Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		scheduler.setEditor(person.getId());

		// set data scheduler
		final Date date_scheduler = this.getDateScheduled(day);
		scheduler.setDate_scheduler(date_scheduler);

		// save scheduler
		this.schedulerDAO.saveOrUpdate(scheduler);

		// refresh grid
		this.setupValuesGrid();

		Messagebox.show("Il programma è stato aggiornato", "INFO", Messagebox.OK, Messagebox.INFORMATION);

	}

	/**
	 * Save program
	 */
	@Listen("onClick = #ok_revision")
	public void saveRevision() {

		if (this.grid_scheduler.getSelectedItem() == null) {
			return;
		}

		final String shift = this.selectedShift;
		final String day = this.selectedDay;

		final Scheduler scheduler = this.getCurrentScheduler(day, shift);
		if (scheduler == null) {
			return;
		}

		// set time in and out
		scheduler.setFrom_ts(this.revision_time_in.getValue());
		scheduler.setTo_ts(this.revision_time_out.getValue());

	}

	/**
	 * return selected comboitem
	 *
	 * @param targetCombo
	 * @param shift
	 * @return
	 */
	private Comboitem selectItemShift(final Combobox targetCombo, final Integer shift) {

		for (final Comboitem item : targetCombo.getItems()) {
			final UserShift shift_item = item.getValue();
			if (shift_item.getId().equals(shift)) {
				return item;
			}
		}

		return null;

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
		for (int i = 0; i < DAYS_IN_GRID; i++) {

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
	private List<Scheduler> setupRowScheduler(final Date initial_date, final Date final_date) {

		// get list data list
		final List<Scheduler> list = this.schedulerDAO.selectSchedulers(initial_date, final_date);

		// set grid
		final List<RowScheduler> rows = new ArrayList<RowScheduler>();

		RowScheduler current_scheduled = null;
		int index = 0;

		for (int i = 0; i < list.size(); i++) {

			final Scheduler scheduler = list.get(i);

			if (current_scheduled == null) {

				final RowScheduler row = new RowScheduler();
				row.setName_scheduled(scheduler.getName_scheduled());
				row.setEmployee_identification(scheduler.getEmployee_identification());
				row.setScheduled(scheduler.getScheduled());
				row.setScheduler_1(scheduler);

				// asign scheduled
				current_scheduled = row;
				index = 2;

			} else if (scheduler.getScheduled().equals(current_scheduled.getScheduled())) {

				if (index == 2) {
					current_scheduled.setScheduler_2(scheduler);
				}

				if (index == 3) {
					current_scheduled.setScheduler_3(scheduler);
				}

				if (index == 4) {
					current_scheduled.setScheduler_4(scheduler);
				}

				if (index == 5) {
					current_scheduled.setScheduler_5(scheduler);
				}

				if (index == 6) {
					current_scheduled.setScheduler_6(scheduler);
				}

				if (index == 7) {
					current_scheduled.setScheduler_7(scheduler);
				}

				// asign scheduled
				index = index++;

			} else if (!(scheduler.equals(current_scheduled.getScheduled()))) {

				final RowScheduler row = new RowScheduler();
				row.setName_scheduled(scheduler.getName_scheduled());
				row.setEmployee_identification(scheduler.getEmployee_identification());
				row.setScheduled(scheduler.getScheduled());
				row.setScheduler_1(scheduler);

				// asign scheduled
				current_scheduled = row;
				index = 1;

			}

		}
		return list;
	}

	/**
	 * setup values for grid
	 */
	private void setupValuesGrid() {

		final Date initial_date = DateUtils.truncate(this.date_init_scheduler.getValue(), Calendar.DATE);
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, DAYS_IN_GRID);
		final Date final_date = calendar.getTime();

		final List<Scheduler> list = this.schedulerDAO.selectSchedulers(initial_date, final_date);

		// TODO: setup row scheduler
		// final List<Scheduler> list = setupRowScheduler(initial_date,
		// final_date);

		this.grid_scheduler.setModel(new ListModelList<Scheduler>(list));

		// close info scheduler
		this.info_scheduler.setVisible(false);

	}
}
