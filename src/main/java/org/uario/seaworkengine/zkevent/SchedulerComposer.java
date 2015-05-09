package org.uario.seaworkengine.zkevent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.docfactory.ProgramReportBuilder;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.LockTable;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.platform.persistence.dao.LockTableDAO;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.statistics.IStatProcedure;
import org.uario.seaworkengine.statistics.UserStatistics;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.BoardTag;
import org.uario.seaworkengine.utility.ShiftTag;
import org.uario.seaworkengine.utility.TableTag;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.utility.UtilityCSV;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.uario.seaworkengine.web.services.handler.Badge;
import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.uario.seaworkengine.zkevent.bean.RowDaySchedule;
import org.uario.seaworkengine.zkevent.bean.RowSchedule;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
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
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;

public class SchedulerComposer extends SelectorComposer<Component> {

	/**
	 * Break in week management - Substitution or not break in week
	 *
	 * @author francesco
	 *
	 */
	private final class BreakInWeekManagement implements EventListener<ClickEvent> {
		private final Date				date_scheduled;
		private final RowDaySchedule	row_item;
		private final List<Schedule>	scheduleListInWeek;
		private final UserShift			shift;

		private BreakInWeekManagement(final UserShift shift, final List<Schedule> scheduleListInWeek,
				final RowDaySchedule row_item, final Date date_scheduled) {
			this.shift = shift;
			this.scheduleListInWeek = scheduleListInWeek;
			this.row_item = row_item;
			this.date_scheduled = date_scheduled;
		}

		@Override
		public void onEvent(final ClickEvent e) {

			if (Messagebox.ON_OK.equals(e.getName())) {

				saveDayShiftProcedure(shift, row_item, date_scheduled, scheduleListInWeek);
			} else if (Messagebox.ON_NO.equals(e.getName())) {

				saveDayShiftProcedure(shift, row_item, date_scheduled, null);
			}

		}
	}

	/**
	 * Event on check foubl shift constraints (for task)
	 *
	 * @author francesco
	 *
	 */
	private final class CheckOnDoubleShiftBreaEvent implements EventListener<ClickEvent> {

		private CheckOnDoubleShiftBreaEvent() {

		}

		@Override
		public void onEvent(final ClickEvent e) {
			if (Messagebox.ON_OK.equals(e.getName())) {

				final Integer max_shift = statProcedure.getMaximumShift(currentSchedule.getDate_schedule(),
						currentSchedule.getUser());

				// save program... finalize
				saveProgramFinalStep();

				// check if reprogramming
				if (selectedShift.compareTo(max_shift) > 0) {

					final Map<String, String> params = new HashMap<String, String>();
					params.put("sclass", "mybutton Button");

					final Messagebox.Button[] buttons = new Messagebox.Button[2];
					buttons[0] = Messagebox.Button.OK;
					buttons[1] = Messagebox.Button.CANCEL;

					final String msg = "Hai violato il vincolo delle due ore di stacco nella programmazione del giorno successivo. Vuoi che il sistema riprogrammi automaticamente i turni del giorno successivo?";
					Messagebox.show(msg, "RIPROGRAMMAZIONE", buttons, null, Messagebox.EXCLAMATION, null,
							new ReAssignShiftAndTaskEvent(), params);

				}

				// close popup
				shift_definition_popup.close();

			} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
				// close popup
				shift_definition_popup.close();
			}
		}
	}

	/**
	 * Reassign break in current week
	 *
	 * @author francesco
	 *
	 */
	private final class ReassignBreakEvent implements EventListener<ClickEvent> {
		private final Date				date_scheduled;
		private final RowDaySchedule	row_item;

		private ReassignBreakEvent(final Date date_scheduled, final RowDaySchedule row_item) {
			this.date_scheduled = date_scheduled;
			this.row_item = row_item;
		}

		@Override
		public void onEvent(final ClickEvent e) {

			if (Messagebox.ON_OK.equals(e.getName())) {

				final Calendar cal_sunday = DateUtils.toCalendar(date_scheduled);

				cal_sunday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				cal_sunday.add(Calendar.DAY_OF_YEAR, -1);
				final Date sunday = DateUtils.truncate(cal_sunday.getTime(), Calendar.DATE);

				// check if Sunday is after current day
				final Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);

				// select date start
				Date date_start;

				if (sunday.after(today) || DateUtils.isSameDay(sunday, today)) {
					date_start = sunday;
				} else {
					date_start = today;
				}

				// set max random value
				int max_random_day = 7;
				final Calendar cal_date_start = DateUtils.toCalendar(date_start);
				final int day_of_week = cal_date_start.get(Calendar.DAY_OF_WEEK);
				if (day_of_week != Calendar.SUNDAY) {
					max_random_day = max_random_day - day_of_week - 1;
				}

				// only if you not assign waited work
				final Date date_break = statProcedure.getARandomDay(date_start, max_random_day);
				final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

				statProcedure.workAssignProcedure(shift_cache.getBreakShift(), date_break, row_item.getUser(),
						person_logged.getId());

				setupGlobalSchedulerGridForDay();

			}

		}
	}

	/**
	 * @author francesco
	 *
	 */
	private final class ReAssignShiftAndTaskEvent implements EventListener<ClickEvent> {
		@Override
		public void onEvent(final ClickEvent e) {

			if (Messagebox.ON_OK.equals(e.getName())) {

				final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

				if (currentSchedule == null) {
					return;
				}

				final Calendar next_cal = DateUtils.toCalendar(currentSchedule.getDate_schedule());
				next_cal.add(Calendar.DAY_OF_YEAR, 1);

				final Schedule next_schedule = scheduleDAO.loadSchedule(next_cal.getTime(), currentSchedule.getUser());

				statProcedure.reAssignShift(next_schedule, person_logged.getId());

				// refresh grid
				setupGlobalSchedulerGridForShift();

			}

		}
	}

	private static final String				ALL_ITEM						= "TUTTI";

	private static final int				DAY_REVIEW_IN_PROGRAM_SHIFT		= 1;

	private static final int				DAYS_BEFORE_TODAY_IN_PROGRAM	= -1;

	private static final int				DAYS_IN_GRID_PREPROCESSING		= 31;

	private static final int				DAYS_IN_GRID_PROGRAM			= 5;

	private static final int				DAYS_TO_SHOW_IN_REVIEW			= 2;

	// format
	private static final SimpleDateFormat	formatter_dd					= new SimpleDateFormat("dd");

	private static final SimpleDateFormat	formatter_ddmmm					= new SimpleDateFormat("EEEE dd MMM");

	private static final SimpleDateFormat	formatter_e						= new SimpleDateFormat("E");

	private static final SimpleDateFormat	formatter_last_p				= new SimpleDateFormat("dd-MM-yyyy 'alle' HH:mm");

	private static final SimpleDateFormat	formatter_MMdd					= new SimpleDateFormat("MM-dd");

	private static final SimpleDateFormat	formatter_scheduler_info		= new SimpleDateFormat("EEEE dd MMM");

	private static final String				PRINT_PROGRAM					= "printProgram";

	/**
	 *
	 */
	private static final long				serialVersionUID				= 1L;

	@Wire
	private Button							add_program_item;

	@Wire
	private Button							add_review_item;

	@Wire
	private Button							assign_program_review;

	private IBankHolidays					bank_holiday;

	@Wire
	private Combobox						board;

	@Wire
	private Button							cancel_day_definition;

	@Wire
	private Button							cancel_program;

	@Wire
	private Button							cancel_review;

	@Wire
	private Component						color_legend_review;

	@Wire
	private Component						color_program_legend;

	@Wire
	private Combobox						combo_user_dip;

	private ConfigurationDAO				configurationDAO;

	@Wire
	private A								controller_label;

	@Wire
	private A								controller_label_daydefinition;

	@Wire
	private A								controller_label_review;

	@Wire
	private Textbox							crane;

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

	@Wire
	private Component						day_shift_over;

	@Wire
	private Checkbox						day_shift_over_control;

	@Wire
	private Checkbox						day_shift_over_control_program;

	@Wire
	private Component						day_shift_over_program;

	@Wire
	private Component						define_program_body;

	@Wire
	private Component						div_force_shift;

	@Wire
	private Component						download_program_report;

	@Wire
	private A								editor_label;

	@Wire
	private A								editor_label_daydefinition;

	@Wire
	private A								editor_label_review;

	/**
	 * First date in grid
	 */
	private Date							firstDateInGrid;

	@Wire
	private Combobox						force_shift_combo;

	@Wire
	private Textbox							full_text_search;

	@Wire
	private Listbox							grid_scheduler;

	@Wire
	private Listbox							grid_scheduler_day;

	@Wire
	private Listbox							grid_scheduler_review;

	@Wire
	private Component						header_info;

	@Wire
	private Label							infoBadge;

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

	@Wire
	private A								label_statistic_task_popup;

	@Wire
	private Component						last_programmer_tag;

	@Wire
	private Label							lastProgrammer;

	// initial program and revision - USED IN POPUP
	private List<DetailInitialSchedule>		list_details_program;

	private List<DetailFinalSchedule>		list_details_review;

	@Wire
	private Listbox							list_overview_preprocessing;

	@Wire
	private Listbox							list_overview_program;

	@Wire
	private Listbox							list_overview_review;

	private ArrayList<RowSchedule>			list_rows_program;

	@Wire
	private Listbox							list_statistics;

	@Wire
	private Listbox							list_task_stat;

	@Wire
	private Listbox							listbox_program;

	@Wire
	private Listbox							listbox_review;

	// initial program and revision - USED DOWNLOAD
	private List<DetailInitialSchedule>		listDetailProgram				= null;

	// review program and revision - USED DOWNLOAD
	private List<DetailFinalSchedule>		listDetailRevision				= null;

	// review program and revision - USED DOWNLOAD
	private List<Schedule>					listSchedule					= null;

	// statistics - USED DOWNLOAD
	private List<UserStatistics>			listUserStatistics				= null;

	private LockTableDAO					lockTableDAO;

	private final Logger					logger							= Logger.getLogger(SchedulerComposer.class);

	@Wire
	private Label							loggerUserOnTable;

	private final String					messageTableLock				= "Utente connesso: ";

	private final String					messageTableUnLock				= "Nessun utente connesso.";

	private final String					messageTimeConnectionTableLock	= "Inizio connessione: ";

	@Wire
	private Textbox							note_preprocessing;

	@Wire
	private Textbox							note_program;

	@Wire
	private Textbox							note_review;

	@Wire
	private Button							ok_day_shift;

	@Wire
	private Button							ok_program;

	@Wire
	private Button							ok_review;

	@Wire
	private Label							overview_count_h;

	@Wire
	private Label							overview_count_h_c;

	@Wire
	private Div								overview_div;

	@Wire
	private Comboitem						overview_item;

	@Wire
	private Tabpanel						overview_preprocessing;

	@Wire
	private Tabpanel						overview_program;

	@Wire
	private Tabpanel						overview_review;
	@Wire
	private Tabbox							overview_tab;

	@Wire
	private Panel							panel_shift_period;
	private final String					partTimeMessage					= "Part Time";
	private Person							person_logged					= null;
	private PersonDAO						personDAO;

	private Person							personLock;

	@Wire
	private Div								preprocessing_div;
	@Wire
	private Comboitem						preprocessing_item;

	@Wire
	private Panel							preprocessing_panel;
	@Wire
	private Component						print_program_videos;
	@Wire
	private Component						print_scheduler;
	@Wire
	private Div								program_div;

	@Wire
	private Component						program_head_1_1;
	@Wire
	private Component						program_head_1_2;
	@Wire
	private Component						program_head_1_3;
	@Wire
	private Component						program_head_1_4;
	@Wire
	private Component						program_head_4_1;

	@Wire
	private Component						program_head_4_2;

	@Wire
	private Component						program_head_4_3;

	@Wire
	private Component						program_head_4_4;

	@Wire
	private Component						program_head_5_1;

	@Wire
	private Component						program_head_5_2;

	@Wire
	private Component						program_head_5_3;

	@Wire
	private Component						program_head_5_4;

	@Wire
	private Comboitem						program_item;

	@Wire
	private Panel							program_panel;

	@Wire
	private Listheader						program_panel_name;

	@Wire
	private Combobox						program_task;

	@Wire
	private Auxheader						program_tot_1_1;

	@Wire
	private Auxheader						program_tot_1_2;

	@Wire
	private Auxheader						program_tot_1_3;
	@Wire
	private Auxheader						program_tot_1_4;
	@Wire
	private Auxheader						program_tot_2_1;
	@Wire
	private Auxheader						program_tot_2_2;

	@Wire
	private Auxheader						program_tot_2_3;

	@Wire
	private Auxheader						program_tot_2_4;

	@Wire
	private Auxheader						program_tot_3_1;

	@Wire
	private Auxheader						program_tot_3_2;
	@Wire
	private Auxheader						program_tot_3_3;
	@Wire
	private Auxheader						program_tot_3_4;
	@Wire
	private Auxheader						program_tot_4_1;

	@Wire
	private Auxheader						program_tot_4_2;
	@Wire
	private Auxheader						program_tot_4_3;
	@Wire
	private Auxheader						program_tot_4_4;
	@Wire
	private Auxheader						program_tot_5_1;
	@Wire
	private Auxheader						program_tot_5_2;
	@Wire
	private Auxheader						program_tot_5_3;
	@Wire
	private Auxheader						program_tot_5_4;
	@Wire
	private Auxheader						programUser_tot_1_1;
	@Wire
	private Auxheader						programUser_tot_1_2;
	@Wire
	private Auxheader						programUser_tot_1_3;
	@Wire
	private Auxheader						programUser_tot_1_4;
	@Wire
	private Auxheader						programUser_tot_2_1;

	@Wire
	private Auxheader						programUser_tot_2_2;

	@Wire
	private Auxheader						programUser_tot_2_3;

	@Wire
	private Auxheader						programUser_tot_2_4;

	@Wire
	private Auxheader						programUser_tot_3_1;

	@Wire
	private Auxheader						programUser_tot_3_2;

	@Wire
	private Auxheader						programUser_tot_3_3;

	@Wire
	private Auxheader						programUser_tot_3_4;

	@Wire
	private Auxheader						programUser_tot_4_1;

	@Wire
	private Auxheader						programUser_tot_4_2;

	@Wire
	private Auxheader						programUser_tot_4_3;

	@Wire
	private Auxheader						programUser_tot_4_4;

	@Wire
	private Auxheader						programUser_tot_5_1;

	@Wire
	private Auxheader						programUser_tot_5_2;

	@Wire
	private Auxheader						programUser_tot_5_3;

	@Wire
	private Auxheader						programUser_tot_5_4;

	@Wire
	private Button							remove_program_item;

	@Wire
	private Button							remove_review_item;

	@Wire
	private Button							repogram_users;

	@Wire
	private Div								review_div;

	@Wire
	private Comboitem						review_item;

	@Wire
	private Panel							review_panel;

	@Wire
	private Listheader						review_panel_name;

	@Wire
	private Combobox						review_task;

	@Wire
	private Auxheader						review_tot_1_1;

	@Wire
	private Auxheader						review_tot_1_2;

	@Wire
	private Auxheader						review_tot_1_3;

	@Wire
	private Auxheader						review_tot_1_4;

	@Wire
	private Auxheader						review_tot_2_1;

	@Wire
	private Auxheader						review_tot_2_2;

	@Wire
	private Auxheader						review_tot_2_3;

	@Wire
	private Auxheader						review_tot_2_4;

	@Wire
	private Component						reviewSearchBox;

	@Wire
	private Auxheader						reviewUser_tot_1_1;

	@Wire
	private Auxheader						reviewUser_tot_1_2;

	@Wire
	private Auxheader						reviewUser_tot_1_3;

	@Wire
	private Auxheader						reviewUser_tot_1_4;

	@Wire
	private Auxheader						reviewUser_tot_2_1;

	@Wire
	private Auxheader						reviewUser_tot_2_2;

	@Wire
	private Auxheader						reviewUser_tot_2_3;

	@Wire
	private Auxheader						reviewUser_tot_2_4;

	@Wire
	private Label							saturation;

	@Wire
	private Label							saturation_month;
	private String							saturationStyle;

	private ISchedule						scheduleDAO;
	@Wire
	private A								scheduler_label;
	@Wire
	private A								scheduler_label_review;
	@Wire
	private Combobox						scheduler_type_selector;
	private IScheduleShip					scheduleShipDAO;

	@Wire
	private Combobox						select_shift_overview;

	@Wire
	private Combobox						select_shifttype_overview;

	@Wire
	public Combobox							select_year;

	// selected day
	private Integer							selectedDay;

	// selected shift
	private Integer							selectedShift;

	/**
	 * User selected to schedule
	 */
	private Integer							selectedUser;

	@Wire
	private Component						set_panel_shift_period;

	private IShiftCache						shift_cache;

	@Wire
	private Popup							shift_definition_popup;

	@Wire
	private Popup							shift_definition_popup_review;

	@Wire
	private Label							shift_description;

	@Wire
	private Label							shift_id;

	@Wire
	private Label							shift_perc_1;

	@Wire
	private Label							shift_perc_2;

	@Wire
	private Label							shift_perc_3;

	@Wire
	private Label							shift_perc_4;

	@Wire
	private Combobox						shift_period_combo;

	@Wire
	private Datebox							shift_period_from;

	@Wire
	private Label							shift_period_name;

	@Wire
	private Button							shift_period_ok;

	@Wire
	private Datebox							shift_period_to;

	@Wire
	private Popup							shift_popup;

	@Wire
	private Combobox						shifts_combo_select;

	@Wire
	private Combobox						ship;

	private IShip							shipDAO;

	@Wire
	public Combobox							shipInDay;

	private IScheduleShip					shipSchedulerDao;

	private Ship							shipSelected;

	@Wire
	private Intbox							shows_rows;

	private IStatistics						statisticDAO;

	@Wire
	private Tab								statisticsTab;

	private IStatProcedure					statProcedure;

	private final String					styleComboItemPopup				= "color: #F5290A;";

	@Wire
	private Button							switchButton;

	private final String					switchButtonValueClose			= "Chiudi";

	private final String					switchButtonValueOpen			= "Apri";

	@Wire
	private Checkbox						sync_schedule;

	protected ITaskCache					task_cache;

	@Wire
	private Label							task_description;

	@Wire
	private Label							task_id;

	@Wire
	private Popup							task_list_popup;

	@Wire
	private Popup							task_popup;

	@Wire
	public Combobox							taskComboBox;

	private TasksDAO						taskDAO;

	@Wire
	private Timebox							time_from;

	@Wire
	private Timebox							time_from_program;

	@Wire
	private Timebox							time_to;

	@Wire
	private Timebox							time_to_program;

	@Wire
	private Auxheader						total_program_day_1;

	@Wire
	private Auxheader						total_program_day_2;

	@Wire
	private Auxheader						total_program_day_3;

	@Wire
	private Auxheader						total_program_day_4;

	@Wire
	private Auxheader						total_program_day_5;

	@Wire
	private Auxheader						total_review_day_1;

	@Wire
	private Auxheader						total_review_day_2;

	@Wire
	public Label							totalHours_Program;

	@Wire
	public Label							totalHours_Review;

	@Wire
	private Auxheader						totalUser_program_day_1;

	@Wire
	private Auxheader						totalUser_program_day_2;

	@Wire
	private Auxheader						totalUser_program_day_3;

	@Wire
	private Auxheader						totalUser_program_day_4;

	@Wire
	private Auxheader						totalUser_program_day_5;

	@Wire
	private Auxheader						totalUser_review_day_1;

	@Wire
	private Auxheader						totalUser_review_day_2;

	@Wire
	private Label							updateStatisticTime;

	@Wire
	private Label							userDepartment;

	private LockTable						userLockTable;

	@Wire
	private Label							userRoles;

	@Wire
	private Label							work_current_month;

	@Wire
	private Label							work_current_week;

	@Wire
	private Label							work_current_year;

	@Wire
	private Label							work_holiday_perc;

	@Wire
	private Label							work_sunday_perc;

	@Wire
	private Label							working_series;

	@Listen("onClick= #add_program_item")
	public void addProgramItem() {

		if (!checkConnection()) {
			return;
		}

		if (list_details_program == null) {
			return;
		}

		if (selectedShift == null) {
			return;
		}

		if (program_task.getSelectedItem() == null) {
			// Messagebox.show("Assegnare una mansione all'utente selezionato, prima di procedere alla programmazione",
			// "INFO", Messagebox.OK,Messagebox.EXCLAMATION);
			return;
		}

		final UserTask task = program_task.getSelectedItem().getValue();
		if (task == null) {
			// Messagebox.show("Assegna una mansione", "INFO", Messagebox.OK,
			// Messagebox.EXCLAMATION);
			return;
		}

		double countHours = 0;

		if ((time_from_program.getValue() != null) && (time_to_program.getValue() != null)) {

			final Double time = getProgrammedTime();

			if (time == null) {
				// Messagebox.show("Definire il numero di ore da lavorare",
				// "INFO",
				// Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			// check about sum of time
			/*
			 * boolean check_sum = true; if (time > 6) { check_sum = false; } if
			 * (this.list_details_program.size() != 0) { int sum = time; for
			 * (final DetailInitialSchedule detail : this.list_details_program)
			 * { final int current_time = detail.getTime(); sum = sum +
			 * current_time; if (sum > 6) { check_sum = false; break; } } } if
			 * (!check_sum) { //
			 * Messagebox.show("Non si possono assegnare più di sei ore per turno"
			 * , // "INFO", Messagebox.OK, Messagebox.EXCLAMATION); return; }
			 */

			if (currentSchedule == null) {
				// save scheduler
				saveCurrentScheduler();
			}

			final DetailInitialSchedule new_item = new DetailInitialSchedule();
			new_item.setId_schedule(currentSchedule.getId());
			new_item.setShift(selectedShift);

			new_item.setTask(task.getId());

			// check if is absence task
			if (configurationDAO.loadTask(task.getId()).getIsabsence()) {
				new_item.setTime(0.0);
				new_item.setTime_vacation(time);
			} else {
				new_item.setTime(time);
				new_item.setTime_vacation(0.0);
			}

			countHours = time;

			final java.util.Date now_from = time_from_program.getValue();
			if (now_from != null) {

				final Calendar current_calendar = DateUtils.toCalendar(currentSchedule.getDate_schedule());
				final Calendar from_calendar = DateUtils.toCalendar(now_from);
				current_calendar.set(Calendar.HOUR_OF_DAY, from_calendar.get(Calendar.HOUR_OF_DAY));
				current_calendar.set(Calendar.MINUTE, from_calendar.get(Calendar.MINUTE));
				current_calendar.set(Calendar.SECOND, from_calendar.get(Calendar.SECOND));

				final java.sql.Timestamp t_from = new java.sql.Timestamp(current_calendar.getTimeInMillis());
				new_item.setTime_from(t_from);
			}

			final java.util.Date now_to = time_to_program.getValue();
			if (now_to != null) {
				final Calendar current_calendar = DateUtils.toCalendar(currentSchedule.getDate_schedule());
				final Calendar to_calendar = DateUtils.toCalendar(now_to);

				if ((selectedShift == 4) && (day_shift_over_control_program.isChecked())) {
					current_calendar.add(Calendar.DAY_OF_YEAR, 1);
				}

				current_calendar.set(Calendar.HOUR_OF_DAY, to_calendar.get(Calendar.HOUR_OF_DAY));
				current_calendar.set(Calendar.MINUTE, to_calendar.get(Calendar.MINUTE));
				current_calendar.set(Calendar.SECOND, to_calendar.get(Calendar.SECOND));

				final java.sql.Timestamp t_to = new java.sql.Timestamp(current_calendar.getTimeInMillis());
				new_item.setTime_to(t_to);
			}

			// check if hours interval is already present in list and if count
			// hours is more than 12
			final List<DetailInitialSchedule> list = (List<DetailInitialSchedule>) listbox_program.getModel();
			if (list != null) {

				for (final DetailInitialSchedule item : list) {

					Double timeVacation = 0.0;
					Double timeWorked = 0.0;

					if (item.getTime() != null) {
						timeWorked = item.getTime();
					}
					if (item.getTime_vacation() != null) {
						timeVacation = item.getTime_vacation();
					}

					countHours = countHours + timeWorked + timeVacation;

					if (countHours > 6) {
						return;
					}

					if (((new_item.getTime_from().compareTo(item.getTime_from()) >= 0) && (new_item.getTime_from().compareTo(
							item.getTime_to()) < 0))
							|| ((new_item.getTime_to().compareTo(item.getTime_from()) > 0) && (new_item.getTime_to().compareTo(
									item.getTime_to()) <= 0))
							|| ((new_item.getTime_from().compareTo(item.getTime_from()) <= 0) && (new_item.getTime_to()
									.compareTo(item.getTime_to()) >= 0))) {
						return;
					}

				}
			}

			if (countHours > 6) {
				return;
			}

			// update program list
			list_details_program.add(new_item);
			final ListModelList<DetailInitialSchedule> model = new ListModelList<DetailInitialSchedule>(list_details_program);
			model.setMultiple(true);
			listbox_program.setModel(model);

			setLabelTotalHoursProgram(model);
		}

	}

	@Listen("onClick= #add_review_item")
	public void addReviewItem() {

		if (!checkConnection()) {
			return;
		}

		if (list_details_review == null) {
			return;
		}

		if (selectedShift == null) {
			return;
		}

		if (review_task.getSelectedItem() == null) {
			return;
		}

		final UserTask task = review_task.getSelectedItem().getValue();
		if (task == null) {
			return;
		}

		final Double time = getRevisionTime();

		if (time == null) {
			return;
		}

		double countHours = 0;

		if (currentSchedule == null) {
			// save scheduler
			saveCurrentScheduler();
		}

		final DetailFinalSchedule new_item = new DetailFinalSchedule();
		new_item.setId_schedule(currentSchedule.getId());
		new_item.setShift(selectedShift);

		// set on board - under board
		if (board.getSelectedItem() != null) {
			if (board.getSelectedItem().getValue().equals(BoardTag.ON_BOARD)) {
				new_item.setBoard(BoardTag.ON_BOARD);
			} else if (board.getSelectedItem().getValue().equals(BoardTag.UNDER_BOARD)) {
				new_item.setBoard(BoardTag.UNDER_BOARD);
			}
		}

		// check if is absence task
		if (configurationDAO.loadTask(task.getId()).getIsabsence()) {
			new_item.setTime(0.0);
			new_item.setTime_vacation(time);
		} else {
			new_item.setTime(time);
			new_item.setTime_vacation(0.0);
		}

		countHours = time;

		new_item.setTask(task.getId());

		final java.util.Date now_from = time_from.getValue();
		if (now_from != null) {

			final Calendar current_calendar = DateUtils.toCalendar(currentSchedule.getDate_schedule());
			final Calendar from_calendar = DateUtils.toCalendar(now_from);
			current_calendar.set(Calendar.HOUR_OF_DAY, from_calendar.get(Calendar.HOUR_OF_DAY));
			current_calendar.set(Calendar.MINUTE, from_calendar.get(Calendar.MINUTE));
			current_calendar.set(Calendar.SECOND, from_calendar.get(Calendar.SECOND));

			final java.sql.Timestamp t_from = new java.sql.Timestamp(current_calendar.getTimeInMillis());
			new_item.setTime_from(t_from);
		}

		final java.util.Date now_to = time_to.getValue();
		if (now_to != null) {
			final Calendar current_calendar = DateUtils.toCalendar(currentSchedule.getDate_schedule());
			final Calendar to_calendar = DateUtils.toCalendar(now_to);

			if ((selectedShift == 4) && day_shift_over_control.isChecked()) {
				current_calendar.add(Calendar.DAY_OF_YEAR, 1);
			}

			current_calendar.set(Calendar.HOUR_OF_DAY, to_calendar.get(Calendar.HOUR_OF_DAY));
			current_calendar.set(Calendar.MINUTE, to_calendar.get(Calendar.MINUTE));
			current_calendar.set(Calendar.SECOND, to_calendar.get(Calendar.SECOND));

			final java.sql.Timestamp t_to = new java.sql.Timestamp(current_calendar.getTimeInMillis());
			new_item.setTime_to(t_to);
		}

		// set ship selected in new item
		if ((shipSelected != null) && (shipSelected.getId() != -1)) {
			new_item.setNameShip(shipSelected.getName());
			new_item.setId_ship(shipSelected.getId());
		}

		final String craneName = crane.getValue();
		if ((craneName != null) && (craneName.trim() != "")) {
			new_item.setCrane(craneName);
		}

		// check if hours interval is already present in list and if count
		// hours is more than 12
		final List<DetailFinalSchedule> list = (List<DetailFinalSchedule>) listbox_review.getModel();
		if (list != null) {

			for (final DetailFinalSchedule item : list) {

				countHours = countHours + item.getTime() + item.getTime_vacation();
				if (countHours > 6) {
					return;
				}

				if (((((new_item.getTime_from().compareTo(item.getTime_from()) >= 0) && (new_item.getTime_from().compareTo(
						item.getTime_to()) < 0)) || ((new_item.getTime_to().compareTo(item.getTime_from()) > 0) && (new_item
						.getTime_to().compareTo(item.getTime_to()) <= 0))))
						|| ((new_item.getTime_from().compareTo(item.getTime_from()) <= 0) && (new_item.getTime_to().compareTo(
								item.getTime_to()) >= 0))) {
					return;
				}
			}
		}

		if (countHours > 6) {
			return;
		}

		// update program list
		list_details_review.add(new_item);
		final ListModelList<DetailFinalSchedule> model = new ListModelList<DetailFinalSchedule>(list_details_review);
		model.setMultiple(true);
		listbox_review.setModel(model);

		setLabelTotalHoursReview(model);
		ship.setSelectedItem(null);
		shipInDay.setSelectedItem(null);
		shipSelected = null;
		crane.setValue("");

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
	private void assignShiftForDaySchedule(final UserShift shift, Date current_date_scheduled, final Integer user,
			final Schedule schedule, final Person editor, final String note) {

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
		scheduleDAO.saveOrUpdateSchedule(schedule);

		// assign
		statProcedure.workAssignProcedure(shift, current_date_scheduled, user, editor.getId());
	}

	/**
	 * Set the day with correct shift. Used on table... for single and multiple
	 * assign
	 *
	 * @param row_item
	 *            the row
	 * @param shift
	 *            the id shift to set
	 * @param offset
	 *            the day after selected day. for current day use 0
	 */
	private void assignShiftFromDaySchedule(final RowDaySchedule row_item, final UserShift shift, final int offset,
			final boolean override_break) {

		final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (person_logged == null) {
			return;
		}

		// getting information
		final Date current_date_scheduled = getDateScheduled(selectedDay + offset);
		final Integer user = row_item.getUser();

		Schedule schedule = row_item.getSchedule(selectedDay + offset);

		// check if is possible to override break
		if (!override_break && (schedule != null)) {
			if (schedule.getShift() != null) {
				final UserShift shift_target = shift_cache.getUserShift(schedule.getShift());
				final UserShift shift_rp_break = shift_cache.getBreakShift();
				final UserShift shift_rp_waited = shift_cache.getWaitedBreakShift();
				if ((shift_rp_break != null) && (shift_rp_waited != null)) {
					if (shift_target.equals(shift_rp_break) || shift_target.equals(shift_rp_waited)) {
						return;
					}
				}

			}
		}

		if (schedule == null) {
			schedule = new Schedule();
		}

		assignShiftForDaySchedule(shift, current_date_scheduled, user, schedule, person_logged, note_preprocessing.getValue());

	}

	@Listen("onClick= #shift_period_ok")
	public void assignShiftInPeriod() {

		if (!checkConnection()) {
			return;
		}

		if (shift_period_combo.getSelectedItem() == null) {
			return;
		}

		if ((shift_period_from.getValue() == null) || (shift_period_to.getValue() == null)) {
			return;
		}

		if (grid_scheduler_day.getSelectedItem() == null) {
			return;
		}

		if (!(shift_period_combo.getSelectedItem().getValue() instanceof UserShift)
				|| (shift_period_combo.getSelectedItem().getValue() == null)) {
			return;
		}

		// check over data from
		Calendar calendar_from = DateUtils.toCalendar(shift_period_from.getValue());
		calendar_from = DateUtils.truncate(calendar_from, Calendar.DATE);
		Calendar today = Calendar.getInstance();
		today = DateUtils.truncate(today, Calendar.DATE);
		if (calendar_from.before(today)) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Non puoi fare assegnazioni multiple che partono prima del giorno attuale.", "INFO", buttons, null,
					Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		// check user shift
		final UserShift shift = shift_period_combo.getSelectedItem().getValue();
		if (shift == null) {
			return;
		}

		if (shift.getBreak_shift().booleanValue() || shift.getWaitbreak_shift().booleanValue()) {

			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Non puoi usare il turno ti riposo o riposo atteso per assegnazioni multiple.", "INFO", buttons,
					null, Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		Calendar calendar_to = DateUtils.toCalendar(shift_period_to.getValue());
		calendar_to = DateUtils.truncate(calendar_to, Calendar.DATE);

		if (calendar_from.after(calendar_to)) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controlla le date inserite.", "INFO", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		final RowDaySchedule row_day_schedule = grid_scheduler_day.getSelectedItem().getValue();
		final Integer user = row_day_schedule.getUser();
		final Person editor = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Calendar index_calendar = Calendar.getInstance();
		index_calendar.setTimeInMillis(calendar_from.getTimeInMillis());
		index_calendar = DateUtils.truncate(index_calendar, Calendar.DATE);

		do {

			final Date date_index = index_calendar.getTime();

			// refresh info about just saved schedule
			final Schedule schedule = scheduleDAO.loadSchedule(date_index, user);

			// check if is possible to override break
			if ((schedule != null) && (schedule.getShift() != null)) {
				final UserShift shift_target = shift_cache.getUserShift(schedule.getShift());
				final UserShift shift_rp_break = shift_cache.getBreakShift();
				final UserShift shift_rp_waited = shift_cache.getWaitedBreakShift();
				if ((shift_rp_break != null) && (shift_rp_waited != null)) {
					if (shift_target.equals(shift_rp_break) || shift_target.equals(shift_rp_waited)) {
						index_calendar.add(Calendar.DAY_OF_YEAR, 1);
						continue;
					}
				}

			}

			// assign
			statProcedure.workAssignProcedure(shift, date_index, user, editor.getId());
			index_calendar.add(Calendar.DAY_OF_YEAR, 1);

		} while (!index_calendar.after(calendar_to));

		setupGlobalSchedulerGridForDay();

		// hide panel if all is ok
		panel_shift_period.setVisible(false);

	}

	@Listen("onChange = #date_init_scheduler_review;onOK = #date_init_scheduler_review")
	public void changeBehaviorReview() {
		// define command behavior for not supervisor
		final Person person_selected = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!person_selected.isAdministrator()) {

			if (date_init_scheduler_review.getValue() == null) {
				assign_program_review.setDisabled(true);
				ok_review.setDisabled(true);
				add_review_item.setDisabled(true);
				remove_review_item.setDisabled(true);
				cancel_review.setDisabled(true);
			} else {
				final Date date = DateUtils.truncate(date_init_scheduler_review.getValue(), Calendar.DATE);

				final Calendar calendar = Calendar.getInstance();

				calendar.add(Calendar.DAY_OF_YEAR, -1);
				final boolean check1 = DateUtils.isSameDay(date, calendar.getTime());

				calendar.add(Calendar.DAY_OF_YEAR, -1);
				final boolean check2 = DateUtils.isSameDay(date, calendar.getTime());

				calendar.add(Calendar.DAY_OF_YEAR, -1);
				final boolean check3 = DateUtils.isSameDay(date, calendar.getTime());

				if (check1 || check2 || check3) {
					assign_program_review.setDisabled(false);
					ok_review.setDisabled(false);
					add_review_item.setDisabled(false);
					remove_review_item.setDisabled(false);
					cancel_review.setDisabled(false);
				} else {
					assign_program_review.setDisabled(true);
					ok_review.setDisabled(true);
					add_review_item.setDisabled(true);
					remove_review_item.setDisabled(true);
					cancel_review.setDisabled(true);
				}
			}

		} else {
			assign_program_review.setDisabled(false);
			ok_review.setDisabled(false);
			add_review_item.setDisabled(false);
			remove_review_item.setDisabled(false);
			cancel_review.setDisabled(false);
		}
	}

	@Listen("onChange = #combo_user_dip")
	public void changeUserDipComboInUserStatistic() {

		refreshUserStatistics(full_text_search.getValue(), combo_user_dip.getValue());

	}

	private boolean checkConnection() {
		if (!checkIfUnLockTable()) {
			SchedulerComposer.this.disableWriteCancelButtons(true);
			if (userLockTable != null) {
				loggerUserOnTable.setValue(messageTableLock + personLock.getFirstname() + " " + personLock.getLastname() + " - "
						+ messageTimeConnectionTableLock + Utility.convertToDateAndTime(userLockTable.getTime_start()));
				switchButton.setLabel(switchButtonValueClose);

			} else {
				loggerUserOnTable.setValue(messageTableUnLock);
				switchButton.setLabel(switchButtonValueOpen);
			}
			return false;
		}
		return true;
	}

	/**
	 * Used to set open/close operation
	 *
	 * @return
	 */
	private void checkIfTableIsLockedAndSetButton() {
		loggerUserOnTable.setVisible(true);
		switchButton.setVisible(true);

		if (person_logged != null) {
			// check if user logged is a Programmer or Administrator
			if (person_logged.isBackoffice() || person_logged.isAdministrator()) {
				final Comboitem version_selected = scheduler_type_selector.getSelectedItem();
				LockTable lockTable = null;
				if ((version_selected == preprocessing_item) || (version_selected == program_item)) {
					lockTable = lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE);
				} else if (version_selected == SchedulerComposer.this.review_item) {
					lockTable = lockTableDAO.loadLockTableByTableType(TableTag.REVIEW_TABLE);
				}

				if (lockTable == null) {
					// table is unlock
					loggerUserOnTable.setValue(messageTableUnLock);
					switchButton.setVisible(true);
					switchButton.setLabel(switchButtonValueOpen);

					if (person_logged.isAdministrator()) {
						disableWriteCancelButtons(false);
					} else {
						disableWriteCancelButtons(true);
					}
					return;

				} else if (!person_logged.isAdministrator()) {
					// another user has lock table
					final Person user = personDAO.loadPerson(lockTable.getId_user());
					switchButton.setVisible(false);
					disableWriteCancelButtons(true);
					if (lockTable.getId_user().equals(person_logged.getId())) {
						switchButton.setVisible(true);
						switchButton.setLabel(switchButtonValueClose);
						disableWriteCancelButtons(false);
						loggerUserOnTable.setValue(messageTableLock + user.getFirstname() + " " + user.getLastname() + " - "
								+ messageTimeConnectionTableLock + Utility.convertToDateAndTime(lockTable.getTime_start()));
						return;
					}

					loggerUserOnTable.setValue(messageTableLock + user.getFirstname() + " " + user.getLastname() + " - "
							+ messageTimeConnectionTableLock + Utility.convertToDateAndTime(lockTable.getTime_start()));
					return;
				} else if (person_logged.isAdministrator()) {
					// another user has lock table, can unlock because is
					// administrator
					final Person user = personDAO.loadPerson(lockTable.getId_user());
					loggerUserOnTable.setValue(messageTableLock + user.getFirstname() + " " + user.getLastname() + " - "
							+ messageTimeConnectionTableLock + Utility.convertToDateAndTime(lockTable.getTime_start()));
					switchButton.setLabel(switchButtonValueClose);
					switchButton.setVisible(true);
					disableWriteCancelButtons(false);
					return;
				}
			}
			return;
		}
		return;
	}

	private boolean checkIfUnLockTable() {
		onChangeSelectedVersion();

		// set button switch
		checkIfTableIsLockedAndSetButton();

		final Comboitem version_selected = SchedulerComposer.this.scheduler_type_selector.getSelectedItem();
		userLockTable = null;
		if ((version_selected == SchedulerComposer.this.preprocessing_item)
				|| (version_selected == SchedulerComposer.this.program_item)) {
			userLockTable = SchedulerComposer.this.lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE);
		} else if (version_selected == SchedulerComposer.this.review_item) {
			userLockTable = SchedulerComposer.this.lockTableDAO.loadLockTableByTableType(TableTag.REVIEW_TABLE);
		}
		personLock = null;
		if (!person_logged.isAdministrator()
				&& ((((userLockTable != null) && !userLockTable.getId_user().equals(person_logged.getId())) || (userLockTable == null)))) {
			SchedulerComposer.this.disableWriteCancelButtons(true);
			if (userLockTable != null) {
				personLock = personDAO.loadPerson(userLockTable.getId_user());
			}

			return false;

		} else {

			return true;
		}

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
	 * Define anchor content on shift schedule
	 *
	 * @param program
	 * @param schedule
	 * @return
	 */
	private Double defineAnchorContentValue(final boolean program, final Schedule schedule) {
		Double time = null;
		if (program) {
			time = schedule.getProgram_time();
		} else {
			time = schedule.getRevision_time();
		}

		return time;

	}

	@Listen("onClick= #assign_program_review")
	public void defineReviewByProgram() {

		if (!checkConnection()) {
			return;
		}

		final Map<String, String> params = new HashMap<String, String>();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[2];
		buttons[0] = Messagebox.Button.OK;
		buttons[1] = Messagebox.Button.CANCEL;

		Messagebox.show("Stai assegnando i turni programmati al consuntivo. Sei sicuro di voler continuare?",
				"CONFERMA ASSEGNAZIONE", buttons, null, Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>() {
					@Override
					public void onEvent(final ClickEvent e) {
						if (Messagebox.ON_OK.equals(e.getName())) {

							SchedulerComposer.this.defineReviewByProgramProcedure();

						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {

						}
					}
				}, params);

		return;

	}

	/**
	 * Assign program to review: procedure
	 */
	private void defineReviewByProgramProcedure() {

		if ((grid_scheduler_review == null) || (grid_scheduler_review.getSelectedItems() == null)
				|| (grid_scheduler_review.getSelectedItems().size() == 0)) {
			return;
		}

		final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		for (final Listitem item : grid_scheduler_review.getSelectedItems()) {

			final RowSchedule item_row = item.getValue();

			if (item_row.getItem_1() == null) {
				continue;
			}

			final Schedule schedule = item_row.getItem_1().getSchedule();
			if ((schedule == null) || (schedule.getId() == null)) {
				continue;
			}

			final List<DetailInitialSchedule> list_init_detail = scheduleDAO.loadDetailInitialScheduleByIdSchedule(schedule
					.getId());

			scheduleDAO.removeAllDetailFinalScheduleBySchedule(schedule.getId());

			for (final DetailInitialSchedule init_item : list_init_detail) {

				final DetailFinalSchedule detail_schedule = new DetailFinalSchedule();

				detail_schedule.setDate_schedule(schedule.getDate_schedule());
				detail_schedule.setId_schedule(init_item.getId_schedule());
				detail_schedule.setShift(init_item.getShift());
				detail_schedule.setTask(init_item.getTask());
				detail_schedule.setTime(init_item.getTime());
				detail_schedule.setTime_vacation(init_item.getTime_vacation());

				Calendar to_day_calendar = DateUtils.toCalendar(schedule.getDate_schedule());
				to_day_calendar = DateUtils.truncate(to_day_calendar, Calendar.DATE);

				detail_schedule.setTime_to(init_item.getTime_to());
				detail_schedule.setTime_from(init_item.getTime_from());

				scheduleDAO.createDetailFinalSchedule(detail_schedule);
			}

			// set controller
			schedule.setController(person_logged.getId());
			scheduleDAO.saveOrUpdateSchedule(schedule);

		}

		// redefine info
		setupGlobalSchedulerGridForShiftReview();

	}

	/**
	 * define the view in function of the type of the view required
	 */
	@Listen("onChange = #scheduler_type_selector, #date_init_scheduler, #date_init_scheduler_review, #select_shift_overview,#select_shifttype_overview, #taskComboBox; onOK = #date_to_overview, #date_from_overview, #date_init_scheduler, #date_init_scheduler_review, #shows_rows, #full_text_search; onSelect = #overview_tab")
	public void defineSchedulerView() {

		setLastProgrammerLabel();

		setVisibilityDownloadReportButton();

		if (scheduler_type_selector.getSelectedItem() == null) {
			grid_scheduler.setVisible(false);
			grid_scheduler_review.setVisible(false);
			list_overview_program.setVisible(false);
			grid_scheduler_day.setVisible(false);
			return;
		} else {
			grid_scheduler.setVisible(true);
			grid_scheduler_review.setVisible(true);
			list_overview_program.setVisible(true);
			grid_scheduler_day.setVisible(true);
		}

		final Comboitem selected = scheduler_type_selector.getSelectedItem();

		// hide panel shift special
		panel_shift_period.setVisible(false);

		if (selected == preprocessing_item) {

			preprocessing_div.setVisible(true);
			program_div.setVisible(false);
			review_div.setVisible(false);
			overview_div.setVisible(false);

			// set initial structure for program
			setGridStructureForDay(SchedulerComposer.this.date_init_scheduler.getValue());
			setupGlobalSchedulerGridForDay();

			return;
		}

		if (selected == program_item) {

			preprocessing_div.setVisible(false);
			program_div.setVisible(true);
			review_div.setVisible(false);
			overview_div.setVisible(false);

			// set initial structure for program
			setGridStructureForShift();
			setupGlobalSchedulerGridForShift();

			return;
		}

		if (selected == review_item) {

			preprocessing_div.setVisible(false);
			program_div.setVisible(false);
			review_div.setVisible(true);
			overview_div.setVisible(false);

			// set initial structure for program
			setGridStructureForShiftReview(SchedulerComposer.this.date_init_scheduler_review.getValue());
			setupGlobalSchedulerGridForShiftReview();

			return;
		}

		if (selected == overview_item) {

			overview_div.setVisible(true);
			preprocessing_div.setVisible(false);
			program_div.setVisible(false);
			review_div.setVisible(false);

			if (statisticsTab.isSelected()) {
				reviewSearchBox.setVisible(false);
			} else {
				reviewSearchBox.setVisible(true);
			}

			// set overview list
			if (select_year.getSelectedItem() != null) {
				selectedYear();
			} else {
				setOverviewLists(date_from_overview.getValue(), date_to_overview.getValue());

			}

			return;
		}

	}

	/**
	 * set the scheduler views to date
	 *
	 * @param date
	 */
	private void defineSchedulerViewToDate(final Date date) {
		if (scheduler_type_selector.getSelectedItem() == null) {
			return;
		}

		final Comboitem selected = scheduler_type_selector.getSelectedItem();

		if (selected == preprocessing_item) {
			preprocessing_div.setVisible(true);
			program_div.setVisible(false);
			review_div.setVisible(false);

			SchedulerComposer.this.date_init_scheduler.setValue(date);

			// set initial structure for program
			setGridStructureForDay(date);
			setupGlobalSchedulerGridForDay();
		}

		if (selected == program_item) {
			preprocessing_div.setVisible(false);
			program_div.setVisible(true);
			review_div.setVisible(false);

			// set initial structure for program
			setGridStructureForShift();
			setupGlobalSchedulerGridForShift();
		}

		if (selected == review_item) {
			preprocessing_div.setVisible(false);
			program_div.setVisible(false);
			review_div.setVisible(true);

			SchedulerComposer.this.date_init_scheduler_review.setValue(date);

			// set initial structure for program
			setGridStructureForShiftReview(date);
			setupGlobalSchedulerGridForShiftReview();
		}
	}

	@Listen("onClick= #set_panel_shift_period")
	public void defineShiftPeriodPanel() {

		if (grid_scheduler_day.getSelectedItem() == null) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Seleziona un utente prima.", "ERROR", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		final RowDaySchedule row_day_schedule = grid_scheduler_day.getSelectedItem().getValue();

		// set label
		shift_period_name.setValue(row_day_schedule.getName_user());

		// show panel
		panel_shift_period.setVisible(true);
	}

	/**
	 * Define user availability
	 *
	 * @return
	 */
	private HashMap<Integer, String> defineUserAvailability(final Date date_picker) {

		// define info about day scheduled - define person available
		final List<Schedule> day_schedule_list = scheduleDAO.loadSchedule(date_picker);

		final HashMap<Integer, String> map_status = new HashMap<Integer, String>();
		for (final Schedule day_schedule : day_schedule_list) {
			final Integer id_user = day_schedule.getUser();
			if (map_status.containsKey(id_user)) {
				continue;
			}

			String status = ShiftTag.USER_WORKER_AVAILABLE;

			final Integer id_shift = day_schedule.getShift();
			final UserShift shift = shift_cache.getUserShift(id_shift);
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

	/**
	 * Define initial view in overview
	 */
	private void defineViewCurrentWorkInOverview() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		date_from_overview.setValue(DateUtils.truncate(calendar.getTime(), Calendar.DATE));

		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

		date_to_overview.setValue(calendar.getTime());
	}

	/**
	 * Defining printable version
	 */
	private void definingPrintableVersion() {
		// printable version
		print_scheduler.setVisible(true);

		// remove header info
		header_info.setVisible(false);

		// set width for A4 paper
		grid_scheduler_day.setWidth("950px");
		preprocessing_panel.setWidth("950px");

		grid_scheduler.setWidth("650px");
		program_panel.setWidth("650px");
		program_panel_name.setHflex("3");

		grid_scheduler_review.setWidth("650px");
		review_panel.setWidth("650px");
		review_panel_name.setHflex("3");

		overview_tab.setWidth("950px");

		preprocessing_div.setVisible(false);
		program_div.setVisible(false);
		review_div.setVisible(false);
		review_div.setVisible(false);

		// remove command
		color_program_legend.setVisible(false);
		color_legend_review.setVisible(false);
		repogram_users.setVisible(false);
		download_program_report.setVisible(false);
		set_panel_shift_period.setVisible(false);
		assign_program_review.setVisible(false);

		scheduler_type_selector.setSelectedItem(null);

		// show only today and tomorrow
		getSelf().getFellowIfAny("day_month_1").setVisible(false);
		getSelf().getFellowIfAny("day_month_4").setVisible(false);
		getSelf().getFellowIfAny("day_month_5").setVisible(false);

		totalUser_program_day_1.setVisible(false);
		totalUser_program_day_4.setVisible(false);
		totalUser_program_day_5.setVisible(false);

		total_program_day_1.setVisible(false);
		total_program_day_4.setVisible(false);
		total_program_day_5.setVisible(false);

		program_tot_1_1.setVisible(false);
		program_tot_1_2.setVisible(false);
		program_tot_1_3.setVisible(false);
		program_tot_1_4.setVisible(false);

		program_tot_4_1.setVisible(false);
		program_tot_4_2.setVisible(false);
		program_tot_4_3.setVisible(false);
		program_tot_4_4.setVisible(false);

		program_tot_5_1.setVisible(false);
		program_tot_5_2.setVisible(false);
		program_tot_5_3.setVisible(false);
		program_tot_5_4.setVisible(false);

		program_head_1_1.setVisible(false);
		program_head_1_2.setVisible(false);
		program_head_1_3.setVisible(false);
		program_head_1_4.setVisible(false);

		program_head_4_1.setVisible(false);
		program_head_4_2.setVisible(false);
		program_head_4_3.setVisible(false);
		program_head_4_4.setVisible(false);

		program_head_5_1.setVisible(false);
		program_head_5_2.setVisible(false);
		program_head_5_3.setVisible(false);
		program_head_5_4.setVisible(false);

	}

	/**
	 * Disable write command
	 *
	 * @param isDisabled
	 */
	private void disableWriteCancelButtons(final boolean isDisabled) {
		shift_period_ok.setDisabled(isDisabled);
		cancel_day_definition.setDisabled(isDisabled);
		ok_day_shift.setDisabled(isDisabled);
		ok_program.setDisabled(isDisabled);
		cancel_program.setDisabled(isDisabled);
		add_program_item.setDisabled(isDisabled);
		remove_program_item.setDisabled(isDisabled);
		ok_review.setDisabled(isDisabled);
		cancel_review.setDisabled(isDisabled);
		add_review_item.setDisabled(isDisabled);
		remove_review_item.setDisabled(isDisabled);
		assign_program_review.setDisabled(isDisabled);
		repogram_users.setDisabled(isDisabled);

		setVisibilityDownloadReportButton();
	}

	@Override
	public void doFinally() throws Exception {

		// set info about person logged
		person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// select initial value for initial date
		date_init_scheduler.setValue(Calendar.getInstance().getTime());

		// select initial value for initial date - review
		final Calendar calender_review = Calendar.getInstance();
		calender_review.add(Calendar.DATE, -1);
		date_init_scheduler_review.setValue(calender_review.getTime());

		// acquire dao
		scheduleDAO = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);
		scheduleShipDAO = (IScheduleShip) SpringUtil.getBean(BeansTag.SCHEDULE_SHIP_DAO);

		taskDAO = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);
		personDAO = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);

		configurationDAO = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
		bank_holiday = (IBankHolidays) SpringUtil.getBean(BeansTag.BANK_HOLIDAYS);

		statisticDAO = (IStatistics) SpringUtil.getBean(BeansTag.STATISTICS);
		statProcedure = (IStatProcedure) SpringUtil.getBean(BeansTag.STAT_PROCEDURE);

		lockTableDAO = (LockTableDAO) SpringUtil.getBean(BeansTag.LOCK_TABLE_DAO);

		shipDAO = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);

		shipSchedulerDao = (IScheduleShip) SpringUtil.getBean(BeansTag.SCHEDULE_SHIP_DAO);

		final ListModelList<Ship> shipList = new ListModelList<Ship>(shipDAO.loadAllShip());

		final List<UserTask> taskList = configurationDAO.loadTasks();
		final ListModelList<UserTask> taskModelList = new ListModelList<UserTask>(taskList);

		taskComboBox.setModel(taskModelList);

		// set value in board combo box
		final List<String> boardList = new ArrayList<String>();
		boardList.add("--");
		boardList.add(BoardTag.ON_BOARD);
		boardList.add(BoardTag.UNDER_BOARD);

		board.setModel(new ListModelList<String>(boardList));

		// set visibility of download_program_report button
		final Person personLogged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (personLogged.isAdministrator()) {
			download_program_report.setVisible(true);
		} else {
			setVisibilityDownloadReportButton();
		}

		if (shipList != null) {
			// add empty ship
			final Ship ship = new Ship();
			ship.setId(-1);
			ship.setName("--");
			shipList.add(0, ship);
			this.ship.setModel(shipList);

		}

		// set initial saturation label style
		saturationStyle = saturation.getStyle();

		// set year in combobox
		final Integer todayYear = Utility.getYear(Calendar.getInstance().getTime());
		final ArrayList<String> years = new ArrayList<String>();

		years.add(SchedulerComposer.ALL_ITEM);

		for (Integer i = 2014; i <= (todayYear + 2); i++) {
			years.add(i.toString());
		}

		select_year.setModel(new ListModelList<String>(years));

		getSelf().addEventListener(ZkEventsTag.onDayNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final RowDaySchedule row_schedule = grid_scheduler_day.getSelectedItem().getValue();

				if (row_schedule == null) {
					return;
				}

				final Integer id_user = row_schedule.getUser();
				// set name
				final String msg = row_schedule.getName_user();

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, grid_scheduler_day, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, grid_scheduler_day, msg);

				}

			}

		});

		getSelf().addEventListener(ZkEventsTag.onProgramNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final RowSchedule row_schedule = grid_scheduler.getSelectedItem().getValue();

				if (row_schedule == null) {
					return;
				}

				final Integer id_user = row_schedule.getUser();
				// set name
				final String msg = row_schedule.getName_user();

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, grid_scheduler, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, grid_scheduler, msg);

				}

			}

		});

		getSelf().addEventListener(ZkEventsTag.onReviewNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final RowSchedule row_schedule = grid_scheduler_review.getSelectedItem().getValue();

				if (row_schedule == null) {
					return;
				}

				final Integer id_user = row_schedule.getUser();
				// set name
				final String msg = row_schedule.getName_user();

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, grid_scheduler_review, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, grid_scheduler_review, msg);

				}

			}

		});

		getSelf().addEventListener(ZkEventsTag.onOverviewReviewNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailFinalSchedule detailFinalSchedule = list_overview_review.getSelectedItem().getValue();

				if (detailFinalSchedule == null) {
					return;
				}

				// set name
				final String msg = detailFinalSchedule.getUser();

				final Integer id_user = detailFinalSchedule.getId_user();

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, list_overview_review, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, list_overview_review, msg);

				}

			}

		});

		getSelf().addEventListener(ZkEventsTag.onOverviewReviewShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailFinalSchedule detailFinalSchedule = list_overview_review.getSelectedItem().getValue();

				if (detailFinalSchedule == null) {
					return;
				}

				final UserShift shift = configurationDAO.loadShiftById(detailFinalSchedule.getShift_type());

				if (shift != null) {
					shift_popup.open(review_div, "after_pointer");
					shift_id.setValue(shift.getCode());
					shift_description.setValue(shift.getDescription());
				}
			}
		});

		getSelf().addEventListener(ZkEventsTag.onOverviewReviewTaskClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailFinalSchedule detailFinalSchedule = list_overview_review.getSelectedItem().getValue();

				if (detailFinalSchedule == null) {
					return;
				}

				final UserTask task = configurationDAO.loadTask(detailFinalSchedule.getTask());

				if (task != null) {
					task_popup.open(review_div, "after_pointer");
					task_id.setValue(task.getCode());
					task_description.setValue(task.getDescription());
				}
			}
		});

		getSelf().addEventListener(ZkEventsTag.onOverviewProgramShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailInitialSchedule detailInitialSchedule = list_overview_program.getSelectedItem().getValue();

				if (detailInitialSchedule == null) {
					return;
				}

				final UserShift shift = configurationDAO.loadShiftById(detailInitialSchedule.getShift_type());

				if (shift != null) {
					shift_popup.open(review_div, "after_pointer");
					shift_id.setValue(shift.getCode());
					shift_description.setValue(shift.getDescription());
				}
			}
		});

		getSelf().addEventListener(ZkEventsTag.onOverviewProgramTaskClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailInitialSchedule detailInitialSchedule = list_overview_program.getSelectedItem().getValue();

				if (detailInitialSchedule == null) {
					return;
				}

				final UserTask task = configurationDAO.loadTask(detailInitialSchedule.getTask());

				if (task != null) {
					task_popup.open(review_div, "after_pointer");
					task_id.setValue(task.getCode());
					task_description.setValue(task.getDescription());
				}
			}
		});

		getSelf().addEventListener(ZkEventsTag.onOverviewPreprocessingShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final Schedule schedule = list_overview_preprocessing.getSelectedItem().getValue();

				if (schedule == null) {
					return;
				}

				final UserShift shift = configurationDAO.loadShiftById(schedule.getShift());

				if (shift != null) {
					shift_popup.open(review_div, "after_pointer");
					shift_id.setValue(shift.getCode());
					shift_description.setValue(shift.getDescription());
				}
			}
		});

		getSelf().addEventListener(ZkEventsTag.onOverviewProgramNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailInitialSchedule detailInitialSchedule = list_overview_program.getSelectedItem().getValue();

				if (detailInitialSchedule == null) {
					return;
				}

				final Integer id_user = detailInitialSchedule.getId_user();
				// set name
				final String msg = detailInitialSchedule.getUser();

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, list_overview_program, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, list_overview_program, msg);

				}

			}

		});

		getSelf().addEventListener(ZkEventsTag.onOverviewPreprocessingNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final Schedule schedule = list_overview_preprocessing.getSelectedItem().getValue();

				if (schedule == null) {
					return;
				}

				final Integer id_user = schedule.getUser();
				// set name
				final String msg = schedule.getName_user();

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, list_overview_preprocessing, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, list_overview_preprocessing, msg);

				}

			}

		});

		getSelf().addEventListener(ZkEventsTag.onShowScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				SchedulerComposer.this.mainSchedulerView(null);

			}
		});

		// SHOW SHIFT CONFIGURATOR
		getSelf().addEventListener(ZkEventsTag.onShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final String data_info = arg0.getData().toString();

				// configure shift
				SchedulerComposer.this.onShiftClickProgram(data_info);

			}

		});

		// SHOW SHIFT CONFIGURATOR
		getSelf().addEventListener(ZkEventsTag.onShiftClickReview, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final String data_info = arg0.getData().toString();

				// configure shift
				SchedulerComposer.this.onShiftClickReview(data_info);

			}

		});

		// SHOW DAY CONFIGURATOR
		getSelf().addEventListener(ZkEventsTag.onDayClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final String data_info = arg0.getData().toString();

				// configure shift
				SchedulerComposer.this.showPreprocessingPopup(data_info);

			}

		});

		final String att_print = Executions.getCurrent().getParameter(SchedulerComposer.PRINT_PROGRAM);
		if (att_print != null) {
			SchedulerComposer.this.mainSchedulerView(SchedulerComposer.PRINT_PROGRAM);
		}

	}

	@Listen("onClick = #overview_download")
	public void downdoadOverviewCSV() {
		// select list
		if (overview_review.isSelected() && (listDetailRevision != null)) {

			final StringBuilder builder = UtilityCSV.downloadCSVReview(listDetailRevision, task_cache, shift_cache);

			Filedownload.save(builder.toString(), "application/text", "revision.csv");
		} else if (listDetailProgram != null) {

			final StringBuilder builder = UtilityCSV.downloadCSVProgram(listDetailProgram, task_cache, shift_cache);

			Filedownload.save(builder.toString(), "application/text", "program.csv");

		}

		else if (listSchedule != null) {

			final StringBuilder builder = UtilityCSV.downloadCSVPreprocessing(listSchedule, shift_cache);

			Filedownload.save(builder.toString(), "application/text", "preprocessing.csv");

		} else if (listUserStatistics != null) {
			final StringBuilder builder = UtilityCSV.downloadCSVStatistics(listUserStatistics);

			Filedownload.save(builder.toString(), "application/text", "statistics.csv");
		}

	}

	@Listen("onClick= #download_program_report")
	public void downloadProgramReport() {

		if ((list_rows_program == null) || (list_rows_program.size() == 0)) {
			return;
		}

		ByteArrayOutputStream stream = null;
		ByteArrayInputStream decodedInput = null;

		try {

			// create invoice..
			stream = new ByteArrayOutputStream();

			final ArrayList<RowSchedule> final_list = new ArrayList<RowSchedule>();
			for (final RowSchedule item : list_rows_program) {

				final Schedule schedule = item.getItem_3().getSchedule();
				if (schedule == null) {
					continue;
				}

				final UserShift shift = shift_cache.getUserShift(schedule.getShift());

				if (!shift.getPresence().booleanValue()) {
					continue;
				}

				// check for worker people
				final Person person = personDAO.loadPerson(item.getUser());
				if ((person == null) || person.isInOffice()) {
					continue;
				}

				final_list.add(item);

			}

			final Calendar cal = Calendar.getInstance();
			cal.setTime(cal.getTime());
			cal.add(Calendar.DATE, 1);
			final Date tomorrowDate = DateUtils.truncate(cal.getTime(), Calendar.DATE);

			final List<DetailScheduleShip> list_DetailScheduleShip = shipSchedulerDao
					.loadDetailScheduleShipByShiftDateAndShipName(tomorrowDate, null, null);

			ProgramReportBuilder.createReport(final_list, list_DetailScheduleShip, tomorrowDate).toPdf(stream);

			decodedInput = new ByteArrayInputStream(stream.toByteArray());

			Filedownload.save(decodedInput, "application/pdf", "report.pdf");

		} catch (final Exception e) {

		} finally {
			try {
				if (stream != null) {
					stream.close();
				}

				if (decodedInput != null) {
					decodedInput.close();
				}
			} catch (final IOException e) {

			}

		}

	}

	@Listen("onChange = #force_shift_combo;")
	public void forceProgramShift() {
		if (force_shift_combo.getSelectedItem() == null) {
			return;
		}
		final UserShift my_shift = force_shift_combo.getSelectedItem().getValue();

		if (my_shift.getPresence()) {
			define_program_body.setVisible(true);
		} else {
			define_program_body.setVisible(false);
		}
	}

	private final Integer getCountWorkingDay(final List<Schedule> scheduleList) {
		Integer countWorkingDay = 0;

		for (int i = 0; i < scheduleList.size(); i++) {
			final Schedule schedule = scheduleList.get(i);
			if ((schedule != null) && (schedule.getShift() != null)) {
				final UserShift shift = configurationDAO.loadShiftById(schedule.getShift());
				if (shift != null) {
					if (shift.getPresence()) {
						countWorkingDay++;
					}
				} else {
					countWorkingDay++;
				}

			} else {
				countWorkingDay++;
			}
		}

		countWorkingDay += (10 - scheduleList.size());

		return countWorkingDay;
	}

	/**
	 * Get date scheduled
	 *
	 * @param day
	 * @return
	 */
	private final Date getDateScheduled(final Integer day) {
		final int to_add = day - 1;

		final Calendar calendar_day = Calendar.getInstance();
		calendar_day.setTime(firstDateInGrid);
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
	private final int getDayOfSchedule(final Schedule schedule) {

		final Date schedule_date = schedule.getDate_schedule();

		if (schedule_date == null) {
			// if not date scheduler, put it at first day
			schedule.setDate_schedule(firstDateInGrid);
			return 1;
		}

		final Date date_init_truncate = DateUtils.truncate(firstDateInGrid, Calendar.DATE);
		final Date schedule_date_truncate = DateUtils.truncate(schedule_date, Calendar.DATE);

		final long millis = schedule_date_truncate.getTime() - date_init_truncate.getTime();
		final double _day_avg = 86400000.0; // 1000 * 60 * 60 * 24
		final double day_elapsed = millis / _day_avg;

		return (int) (Math.round(day_elapsed) + 1);

	}

	/**
	 * Get a new Item Row from a schedule
	 *
	 * @param schedule
	 */
	private final ItemRowSchedule getItemRowSchedule(final RowSchedule currentRow, final Integer day_on_current_calendar,
			final Schedule schedule, final boolean program) {

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

		// get item row if null
		if (itemsRow == null) {
			itemsRow = new ItemRowSchedule(currentRow);
		}

		// set current schedule... in any case
		itemsRow.setSchedule(schedule);

		if (schedule.getId() != null) {
			if (schedule.getNo_shift() != null) {

				if (schedule.getNo_shift() == 1) {
					itemsRow.setAnchor1(defineAnchorContent(version_program, schedule));
					itemsRow.setAnchorValue1(defineAnchorContentValue(version_program, schedule));
				}

				if (schedule.getNo_shift() == 2) {
					itemsRow.setAnchor2(defineAnchorContent(version_program, schedule));
					itemsRow.setAnchorValue2(defineAnchorContentValue(version_program, schedule));
				}

				if (schedule.getNo_shift() == 3) {
					itemsRow.setAnchor3(defineAnchorContent(version_program, schedule));
					itemsRow.setAnchorValue3(defineAnchorContentValue(version_program, schedule));
				}

				if (schedule.getNo_shift() == 4) {
					itemsRow.setAnchor4(defineAnchorContent(version_program, schedule));
					itemsRow.setAnchorValue4(defineAnchorContentValue(version_program, schedule));
				}
			}

		}

		return itemsRow;
	}

	/**
	 * Get list of task to include in combo popup
	 *
	 * @param user
	 * @return
	 */
	private final List<UserTask> getListTaskForComboPopup(final Integer user) {
		final List<UserTask> list_task_user = taskDAO.loadTasksByUser(user);
		final List<UserTask> list_task_absence = configurationDAO.listAllAbsenceTask();

		final List<UserTask> list = new ArrayList<UserTask>();
		list.addAll(list_task_user);
		list.addAll(list_task_absence);
		Collections.sort(list);
		return list;
	};

	/**
	 * Programmed time in decimal
	 *
	 * @return
	 */
	private final Double getProgrammedTime() {

		final boolean check_day_over = day_shift_over_control_program.isChecked();

		final Date date_from = time_from_program.getValue();
		Date date_to = time_to_program.getValue();

		if ((selectedShift == 4) && check_day_over) {
			final Calendar calednar = DateUtils.toCalendar(date_to);
			calednar.add(Calendar.DAY_OF_YEAR, 1);
			date_to = calednar.getTime();
		}

		if ((date_from == null) || (date_to == null)) {
			return null;
		}

		final Date time_from_date = date_from;
		final Date time_to_date = date_to;
		if (time_from_date.after(time_to_date)) {
			return null;
		}

		final Long long_time = time_to_date.getTime() - time_from_date.getTime();

		final Double millis = long_time.doubleValue();

		final Double ret = millis / (1000 * 60 * 60);

		return ret;

	}

	/**
	 * Return decimal revision time
	 *
	 * @return
	 */
	private final Double getRevisionTime() {

		final boolean check_day_over = day_shift_over_control.isChecked();

		final Date date_from = time_from.getValue();
		Date date_to = time_to.getValue();

		if ((selectedShift == 4) && check_day_over) {
			final Calendar calednar = DateUtils.toCalendar(date_to);
			calednar.add(Calendar.DAY_OF_YEAR, 1);
			date_to = calednar.getTime();
		}

		if ((date_from == null) || (date_to == null)) {
			return null;
		}

		final Date time_from_date = date_from;
		final Date time_to_date = date_to;
		if (time_from_date.after(time_to_date)) {
			return null;
		}

		final Long long_time = time_to_date.getTime() - time_from_date.getTime();

		final Double millis = long_time.doubleValue();

		final Double ret = millis / (1000 * 60 * 60);

		return ret;
	}

	// using for check 10 day working constraint
	private final List<Schedule> getScheduleTenDayBefore(final Date dateBegin, final Integer idUser) {

		final Date my_pick_date = DateUtils.truncate(dateBegin, Calendar.DATE);

		// get begin date
		final Calendar calendar = DateUtils.toCalendar(my_pick_date);
		calendar.add(Calendar.DAY_OF_YEAR, -10);
		final Date start_date = calendar.getTime();

		return scheduleDAO.selectSchedulersForPreprocessingOnUserId(start_date, dateBegin, idUser);

	}

	/**
	 * Initialize method
	 *
	 * @param currentRow
	 * @param firstDateInGrid2
	 */
	private void initializeDateForDaySchedule(final RowDaySchedule currentRow, final Date firstDateInGrid2) {

		final Calendar date_init = DateUtils.toCalendar(firstDateInGrid2);

		currentRow.setItem1(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem2(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem3(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem4(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem5(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem6(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem7(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem8(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem9(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem10(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem11(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem12(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem13(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem14(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem15(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem16(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem17(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem18(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem19(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem20(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem21(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem22(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem23(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem24(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem25(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem26(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem27(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem28(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem29(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem30(new Schedule(date_init.getTime()));
		date_init.add(Calendar.DATE, 1);

		currentRow.setItem31(new Schedule(date_init.getTime()));

	}

	/**
	 * Main procedure to show
	 *
	 * @param view_version
	 *            (PRINTABLE....)
	 */
	private void mainSchedulerView(final String view_version) {

		// define shift combo
		final List<UserShift> shifts = SchedulerComposer.this.configurationDAO.loadShifts();
		shifts_combo_select.setModel(new ListModelList<UserShift>(shifts));

		// set combo in period shift composer
		shift_period_combo.setModel(new ListModelList<UserShift>(shifts));

		// set info in force combo
		force_shift_combo.setModel(new ListModelList<UserShift>(shifts));

		// set in overview
		select_shifttype_overview.setModel(new ListModelList<UserShift>(shifts));

		// get the shift cache
		shift_cache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);
		task_cache = (ITaskCache) SpringUtil.getBean(BeansTag.TASK_CACHE);

		if ((view_version != null) && view_version.equals(SchedulerComposer.PRINT_PROGRAM)) {

			definingPrintableVersion();

		} else {
			scheduler_type_selector.setSelectedItem(SchedulerComposer.this.preprocessing_item);
		}

		// set view about person viewer
		if (person_logged.isViewerOnly()) {
			scheduler_type_selector.setSelectedItem(program_item);
		}

		// check and set if table is locked
		checkIfTableIsLockedAndSetButton();

		final Comboitem version_selected = scheduler_type_selector.getSelectedItem();
		LockTable lockTable = null;
		if ((version_selected == preprocessing_item) || (version_selected == program_item)) {
			lockTable = lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE);
		} else if (version_selected == review_item) {
			lockTable = lockTableDAO.loadLockTableByTableType(TableTag.REVIEW_TABLE);
		}

		// check if you are admin or you are the user that have locked
		// the table
		if (!person_logged.isAdministrator()) {
			disableWriteCancelButtons(true);
		} else {
			disableWriteCancelButtons(false);
		}
		final int idLogged = person_logged.getId();

		if ((lockTable != null) && (lockTable.getId_user() == idLogged)) {
			SchedulerComposer.this.disableWriteCancelButtons(false);
		}

		defineViewCurrentWorkInOverview();

		defineSchedulerView();
	}

	@Listen("onChange = #scheduler_type_selector")
	public void onChangeSelectedVersion() {
		if (scheduler_type_selector.getSelectedItem() == null) {
			return;
		}
		final Comboitem selected = scheduler_type_selector.getSelectedItem();

		if (!selected.equals(overview_item)) {
			checkIfTableIsLockedAndSetButton();
			loggerUserOnTable.setVisible(true);
			// this.switchButton.setVisible(true);
		} else {
			loggerUserOnTable.setVisible(false);
			switchButton.setVisible(false);
		}

	}

	@Listen("onClick = #overview_month")
	public void onSelectMonthOverview() {

		select_year.setSelectedItem(null);

		defineViewCurrentWorkInOverview();

		defineSchedulerView();

	}

	/**
	 * Shift configurator
	 *
	 * @param data_info
	 */
	private void onShiftClickProgram(final String data_info) {

		if (grid_scheduler.getSelectedItem() == null) {
			return;
		}

		final RowSchedule row_scheduler = grid_scheduler.getSelectedItem().getValue();

		// for of shift --> shift_1_4
		final String[] info = data_info.split("_");
		if (info.length != 3) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Check Scheduler ZUL Strucutre. Contact Uario S.r.L.", "ERROR", buttons, null, Messagebox.ERROR,
					null, null, params);

			return;
		}

		// info check
		if (!NumberUtils.isNumber(info[1]) || !NumberUtils.isNumber(info[2])) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Check Scheduler ZUL Strucutre. Contact Uario S.r.L.", "ERROR", buttons, null, Messagebox.ERROR,
					null, null, params);
			return;
		}

		selectedDay = Integer.parseInt(info[1]);
		selectedShift = Integer.parseInt(info[2]);
		setDefaultValueTimeInPopupReview(selectedShift, time_from_program, time_to_program);
		final Integer user = row_scheduler.getUser();
		selectedUser = user;

		// show day over
		if (selectedShift != 4) {
			day_shift_over_program.setVisible(false);
		} else {
			day_shift_over_program.setVisible(true);
		}

		final Date date_schedule = getDateScheduled(SchedulerComposer.this.selectedDay);

		// take the right scheduler
		currentSchedule = scheduleDAO.loadSchedule(date_schedule, selectedUser);

		// set label
		if (personDAO.loadPerson(selectedUser).getPart_time()) {
			scheduler_label.setLabel(row_scheduler.getName_user() + " " + partTimeMessage + ". Giorno: "
					+ SchedulerComposer.formatter_scheduler_info.format(date_schedule) + ". Turno: "
					+ SchedulerComposer.this.selectedShift);

		} else {
			scheduler_label.setLabel(row_scheduler.getName_user() + ". Giorno: "
					+ SchedulerComposer.formatter_scheduler_info.format(date_schedule) + ". Turno: "
					+ SchedulerComposer.this.selectedShift);
		}

		// show programmer and controller
		editor_label.setLabel("");
		controller_label.setLabel("");

		if (currentSchedule != null) {

			if (currentSchedule.getEditor() != null) {
				final Person editor = personDAO.loadPerson(currentSchedule.getEditor());
				if (editor != null) {
					editor_label.setLabel("Programmatore: " + editor.getFirstname() + " " + editor.getLastname());
				}
			}

			if (currentSchedule.getController() != null) {
				final Person controller = personDAO.loadPerson(currentSchedule.getController());
				if (controller != null) {
					controller_label.setLabel("Controllore: " + controller.getFirstname() + " " + controller.getLastname());
				}
			}

		}

		// reset editor tools
		force_shift_combo.setValue(null);

		// set initial behavior for forceable
		div_force_shift.setVisible(false);
		define_program_body.setVisible(true);

		// set info abount standard work (if any exists)
		if (shift_cache.getStandardWorkShift() != null) {
			label_date_shift_program.setLabel(shift_cache.getStandardWorkShift().toString());
		} else {
			label_date_shift_program.setLabel(null);
		}

		// set label current shift
		if (currentSchedule != null) {

			final UserShift myshift = shift_cache.getUserShift(currentSchedule.getShift());

			if (myshift != null) {
				label_date_shift_program.setLabel(myshift.toString());

				// define fearceble behavior
				if (myshift.getForceable().booleanValue()) {
					div_force_shift.setVisible(true);
					define_program_body.setVisible(false);
				}

			}

			// set note_program
			SchedulerComposer.this.note_program.setValue(SchedulerComposer.this.currentSchedule.getNote());

			// set initial program and revision
			list_details_program = scheduleDAO.loadDetailInitialScheduleByIdScheduleAndShift(currentSchedule.getId(),
					selectedShift);

		} else {
			// if we haven't information about schedule
			note_program.setValue(null);
			listbox_program.getItems().clear();

			// set list program and revision
			list_details_program = new ArrayList<DetailInitialSchedule>();

		}

		// set model list program and revision
		final ListModelList<DetailInitialSchedule> model = new ListModelList<DetailInitialSchedule>(list_details_program);
		model.setMultiple(true);
		listbox_program.setModel(model);

		// set Label count hours added in listbox_progra
		setLabelTotalHoursProgram(model);

		// set combo task
		final List<UserTask> list = getListTaskForComboPopup(user);

		program_task.setSelectedItem(null);
		program_task.getChildren().clear();

		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());
			if (task_item.getIsabsence()) {
				combo_item.setStyle(styleComboItemPopup);
			}
			program_task.appendChild(combo_item);

			// set if default
			if (task_item.getTask_default()) {
				program_task.setSelectedItem(combo_item);
			}

		}

		// task
		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());

		}

		// open popup
		shift_definition_popup.open(program_div, "after_pointer");

	}

	/**
	 * Popup on review
	 *
	 * @param data_info
	 */
	private void onShiftClickReview(final String data_info) {

		final Date date_to_configure = date_init_scheduler_review.getValue();
		if (date_to_configure == null) {
			return;
		}

		// set null combobox all ship, ship in day and board
		ship.setSelectedItem(null);
		shipInDay.setSelectedItem(null);
		board.setSelectedItem(null);
		// set null selected ship
		shipSelected = null;
		// set null crane value
		crane.setValue("");

		// load ship in program for selected day and set model in combobox
		final List<Ship> listShipInDay = scheduleShipDAO.loadShipInDate(new Timestamp(date_to_configure.getTime()));
		shipInDay.setModel(new ListModelList<Ship>());

		if (listShipInDay != null) {
			// add empty ship
			final Ship ship = new Ship();
			ship.setId(-1);
			ship.setName("--");
			listShipInDay.add(0, ship);
			// set model in shipInDay combobox
			shipInDay.setModel(new ListModelList<Ship>(listShipInDay));
		}

		shift_definition_popup_review.open(review_div, "after_pointer");

		if (SchedulerComposer.this.grid_scheduler_review.getSelectedItem() == null) {
			return;
		}

		final RowSchedule row_scheduler = SchedulerComposer.this.grid_scheduler_review.getSelectedItem().getValue();

		// for of shift --> shift_1_4
		final String[] info = data_info.split("_");
		if (info.length != 3) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Check Scheduler ZUL Strucutre. Contact Uario S.r.L.", "ERROR", buttons, null, Messagebox.ERROR,
					null, null, params);

			return;
		}

		// info check
		if (!NumberUtils.isNumber(info[1]) || !NumberUtils.isNumber(info[2])) {

			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Check Scheduler ZUL Strucutre. Contact Uario S.r.L.", "ERROR", buttons, null, Messagebox.ERROR,
					null, null, params);

			return;
		}

		selectedDay = Integer.parseInt(info[1]);
		selectedShift = Integer.parseInt(info[2]);

		// set default value of time_from and time_to in popup
		setDefaultValueTimeInPopupReview(selectedShift, time_from, time_to);

		final Integer user = row_scheduler.getUser();
		selectedUser = user;

		// show day over
		if (selectedShift != 4) {
			day_shift_over.setVisible(false);
		} else {
			day_shift_over.setVisible(true);
		}

		final Date date_schedule = DateUtils.truncate(date_to_configure, Calendar.DATE);

		// take the right scheduler
		SchedulerComposer.this.currentSchedule = scheduleDAO.loadSchedule(date_schedule, selectedUser);

		// set label list badge
		setLabelListBadge(currentSchedule.getId());

		// set label
		if (personDAO.loadPerson(selectedUser).getPart_time()) {
			scheduler_label_review.setLabel(row_scheduler.getName_user() + " " + partTimeMessage + ". Giorno: "
					+ SchedulerComposer.formatter_scheduler_info.format(date_schedule) + ". Turno: "
					+ SchedulerComposer.this.selectedShift);

		} else {
			scheduler_label_review.setLabel(row_scheduler.getName_user() + ". Giorno: "
					+ SchedulerComposer.formatter_scheduler_info.format(date_schedule) + ". Turno: "
					+ SchedulerComposer.this.selectedShift);
		}

		// set label current shift
		if (currentSchedule != null) {
			final UserShift myshift = shift_cache.getUserShift(currentSchedule.getShift());
			if (myshift != null) {
				label_date_shift_review.setLabel(myshift.toString());
			} else {
				if (shift_cache.getStandardWorkShift() != null) {
					label_date_shift_review.setLabel(shift_cache.getStandardWorkShift().toString());
				} else {
					label_date_shift_review.setLabel(null);
				}
			}
		}

		// if any information about schedule...
		if (SchedulerComposer.this.currentSchedule != null) {

			// set note_program
			SchedulerComposer.this.note_review.setValue(SchedulerComposer.this.currentSchedule.getNote());

			// set initial program and revision
			list_details_review = scheduleDAO.loadDetailFinalScheduleByIdScheduleAndShift(currentSchedule.getId(), selectedShift);

		} else {
			// if we haven't information about schedule
			note_review.setValue(null);
			listbox_review.getItems().clear();

			// set list revision
			list_details_review = new ArrayList<DetailFinalSchedule>();

		}

		// set model list program and revision
		final ListModelList<DetailFinalSchedule> model = new ListModelList<DetailFinalSchedule>(list_details_review);
		model.setMultiple(true);
		listbox_review.setModel(model);

		setLabelTotalHoursReview(model);

		// set combo task
		final List<UserTask> list = getListTaskForComboPopup(user);

		review_task.setSelectedItem(null);
		review_task.getChildren().clear();

		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());
			if (task_item.getIsabsence()) {
				combo_item.setStyle(styleComboItemPopup);
			}
			review_task.appendChild(combo_item);

			// set if default
			if (task_item.getTask_default()) {
				review_task.setSelectedItem(combo_item);
			}

		}

		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());

		}

		// show programmer and controller
		editor_label_review.setLabel("");
		controller_label_review.setLabel("");

		if (currentSchedule != null) {

			if (currentSchedule.getEditor() != null) {
				final Person editor = personDAO.loadPerson(currentSchedule.getEditor());
				if (editor != null) {
					editor_label_review.setLabel("Programmatore: " + editor.getFirstname() + " " + editor.getLastname());
				}
			}

			if (currentSchedule.getController() != null) {
				final Person controller = personDAO.loadPerson(currentSchedule.getController());
				if (controller != null) {
					controller_label_review
							.setLabel("Controllore: " + controller.getFirstname() + " " + controller.getLastname());
				}
			}

			// check or unchek sync_mobile checkbox
			if (selectedShift.equals(1)) {
				sync_schedule.setChecked(currentSchedule.getSync_mobile_1());
			}
			if (selectedShift.equals(2)) {
				sync_schedule.setChecked(currentSchedule.getSync_mobile_2());
			}
			if (selectedShift.equals(3)) {
				sync_schedule.setChecked(currentSchedule.getSync_mobile_3());
			}
			if (selectedShift.equals(4)) {
				sync_schedule.setChecked(currentSchedule.getSync_mobile_4());
			}

		}

	}

	@Listen("onClick = #refresh_command")
	public void refreshCommand() {

		full_text_search.setValue(null);

		// refresh for overview
		date_from_overview.setValue(null);
		date_to_overview.setValue(null);
		select_shift_overview.setSelectedItem(item_all_shift_overview);

		// set selected item
		combo_user_dip.setSelectedItem(null);

		// if statistic tab is selected, fire refresh on user statistic
		if (statisticsTab.isSelected()) {

			refreshUserStatistics(full_text_search.getValue(), combo_user_dip.getValue());
		}

		defineSchedulerView();

	}

	/**
	 * Refresh statistic
	 *
	 * @param text
	 */
	private void refreshUserStatistics(final String text, final String department) {

		String department_select = null;
		if ((department != null) && !department.equals(SchedulerComposer.ALL_ITEM) && !department.equals("")) {
			department_select = department;
		}

		String text_select = null;
		if ((text != null) && !text.equals("")) {
			text_select = text;
		}

		// get worker user
		final List<Person> users_schedule = personDAO.listWorkerPersons(text_select, department_select);

		listUserStatistics = new ListModelList<UserStatistics>();

		for (final Person person : users_schedule) {
			final UserStatistics user = statProcedure.getUserStatistics(person);
			user.setPerson(person);
			listUserStatistics.add(user);
		}

		Collections.sort(listUserStatistics);

		list_statistics.setModel(new ListModelList<UserStatistics>(listUserStatistics));

		if ((shows_rows.getValue() != null) && (shows_rows.getValue() != 0)) {
			list_statistics.setPageSize(shows_rows.getValue());
		}

		final Calendar cal = Calendar.getInstance();

		updateStatisticTime.setValue("Statistica aggiornata a: " + Utility.convertToDateAndTime(cal.getTime()));
	}

	@Listen("onClick = #cancel_day_definition")
	public void removeDayConfiguration() {

		if (!checkConnection()) {
			return;
		}

		if ((selectedDay == null)) {
			return;
		}

		if (grid_scheduler_day == null) {
			return;
		}

		final Date dayScheduleDate = getDateScheduled(selectedDay);

		// check data start point
		if (dayScheduleDate == null) {
			return;
		}

		// get row item
		final RowDaySchedule row_item = grid_scheduler_day.getSelectedItem().getValue();

		// info to the users
		final UserShift shiftStandard = shift_cache.getStandardWorkShift();

		// check for day after tomorrow...remove means assign standard
		// work
		final Calendar tomorrow_cal = Calendar.getInstance();
		tomorrow_cal.add(Calendar.DATE, 1);
		final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// check if is a multiple removing
		final Date dayAfterConfig = day_after_config.getValue();

		if (dayAfterConfig != null) {
			// is multiple removing

			final Date to_day = DateUtils.truncate(dayAfterConfig, Calendar.DATE);

			if (dayScheduleDate.after(to_day)) {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Attenzione alla data inserita", "ERROR", buttons, null, Messagebox.EXCLAMATION, null, null,
						params);

				return;
			}

			final int count = (int) ((to_day.getTime() - dayScheduleDate.getTime()) / (1000 * 60 * 60 * 24));

			if ((selectedDay + count) > SchedulerComposer.DAYS_IN_GRID_PREPROCESSING) {

				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox
						.show("Non cancellare oltre i limiti della griglia corrente. Usa Imposta Speciale per azioni su intervalli che vanno otlre la griglia corrente.",
								"ERROR", buttons, null, Messagebox.EXCLAMATION, null, null, params);

				return;
			}

			// remove day schedule in interval date
			scheduleDAO.removeScheduleUser(row_item.getUser(), dayScheduleDate, dayAfterConfig);

			// check for info worker for tomorrow
			final Calendar calendar = DateUtils.toCalendar(dayScheduleDate);
			for (int i = 0; i <= count; i++) {

				if (i != 0) {
					calendar.add(Calendar.DAY_OF_YEAR, 1);
				}

				if (DateUtils.isSameDay(tomorrow_cal, calendar)) {
					final Schedule schedule = new Schedule();
					assignShiftForDaySchedule(shiftStandard, tomorrow_cal.getTime(), row_item.getUser(), schedule, person_logged,
							null);
					break;
				}

			}

		} else {

			// is a single removing
			scheduleDAO.removeScheduleUser(row_item.getUser(), dayScheduleDate, dayScheduleDate);

			// check for tomorrow
			if (DateUtils.isSameDay(tomorrow_cal.getTime(), dayScheduleDate)) {
				final Schedule schedule = new Schedule();
				assignShiftForDaySchedule(shiftStandard, tomorrow_cal.getTime(), row_item.getUser(), schedule, person_logged,
						null);
			}

			// check if a break shift is removing
			final Schedule current_schedule = row_item.getSchedule(selectedDay);

			if ((current_schedule != null) && (current_schedule.getShift() != null)) {

				final Integer shift_type_id = current_schedule.getShift();
				final UserShift shift_type = shift_cache.getUserShift(shift_type_id);

				if (shift_type.getBreak_shift().booleanValue() || shift_type.getWaitbreak_shift().booleanValue()) {

					// get local variable
					final Date date_schedule = current_schedule.getDate_schedule();

					final List<Schedule> list_sch = statProcedure.searchBreakInCurrentWeek(date_schedule,
							current_schedule.getUser());

					if (list_sch == null) {

						final Map<String, String> params = new HashMap<String, String>();
						params.put("sclass", "mybutton Button");
						final Messagebox.Button[] buttons = new Messagebox.Button[2];
						buttons[0] = Messagebox.Button.OK;
						buttons[1] = Messagebox.Button.NO;

						final String msg = "Non ci sono più riposi per questa settimana. Impostare automaticamente un riposo?";
						Messagebox.show(msg, "GESTIONE RIPOSI", buttons, null, Messagebox.EXCLAMATION, null,
								new ReassignBreakEvent(date_schedule, row_item), params);

					}

				}
			}

		}

		// refresh grid
		setupGlobalSchedulerGridForDay();

		day_definition_popup.close();
	}

	@Listen("onClick = #cancel_program")
	public void removeProgram() {
		if (!checkConnection()) {
			return;
		}

		if ((selectedDay == null) || (selectedShift == null) || (selectedUser == null)) {
			return;
		}

		if (list_details_program == null) {
			return;
		}

		if (currentSchedule != null) {
			scheduleDAO.removeAllDetailInitialScheduleByScheduleAndShift(currentSchedule.getId(), selectedShift);

			// refresh grid
			setupGlobalSchedulerGridForShift();
		}

		// Messagebox.show("Il programma è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		shift_definition_popup.close();
	}

	@Listen("onClick = #remove_program_item")
	public void removeProgramItem() {

		if (!checkConnection()) {
			return;
		}

		if (listbox_program == null) {
			return;
		}

		if ((list_details_program == null) || (list_details_program.size() == 0)) {
			return;
		}

		// remove....
		for (final Listitem itm : listbox_program.getSelectedItems()) {
			final DetailInitialSchedule detail_item = itm.getValue();
			list_details_program.remove(detail_item);
		}

		// set model list program and revision
		final ListModelList<DetailInitialSchedule> model = new ListModelList<DetailInitialSchedule>(list_details_program);
		model.setMultiple(true);
		listbox_program.setModel(model);

		setLabelTotalHoursProgram(model);

	}

	@Listen("onClick = #cancel_review")
	public void removeReview() {

		if (!checkConnection()) {
			return;
		}

		if ((selectedDay == null) || (selectedShift == null) || (selectedUser == null)) {
			return;
		}

		if (list_details_review == null) {
			return;
		}

		if (currentSchedule != null) {
			scheduleDAO.removeAllDetailFinalScheduleByScheduleAndShift(currentSchedule.getId(), selectedShift);

			scheduleDAO.updateMobileSynch(currentSchedule.getId(), false, selectedShift);

			// refresh grid
			setupGlobalSchedulerGridForShiftReview();

		}

		// Messagebox.show("Il consuntivo è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		shift_definition_popup_review.close();
	}

	@Listen("onClick = #remove_review_item")
	public void removeReviewItem() {

		if (!checkConnection()) {
			return;
		}

		if (listbox_review == null) {
			return;
		}

		if ((list_details_review == null) || (list_details_review.size() == 0)) {
			return;
		}

		// remove....
		for (final Listitem itm : listbox_review.getSelectedItems()) {
			final DetailFinalSchedule detail_item = itm.getValue();
			list_details_review.remove(detail_item);
		}

		// set model list program and revision
		final ListModelList<DetailFinalSchedule> model = new ListModelList<DetailFinalSchedule>(list_details_review);
		model.setMultiple(true);
		listbox_review.setModel(model);

		setLabelTotalHoursReview(model);

	}

	@Listen("onClick= #repogram_users")
	public void reprogramUser() {

		if (!checkConnection()) {
			return;
		}

		if ((grid_scheduler.getSelectedItems() == null) || (grid_scheduler.getSelectedItems().size() == 0)) {
			return;
		}

		// get date tomorrow
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		final Date date_tomorrow = DateUtils.truncate(cal.getTime(), Calendar.DATE);

		for (final Listitem item : grid_scheduler.getSelectedItems()) {

			final RowSchedule itm_row = item.getValue();
			if ((itm_row.getUser() == null) || (itm_row.getItem_3() == null)) {
				continue;
			}

			// get Person
			final Person person = personDAO.loadPerson(itm_row.getUser());

			Schedule schedule = null;

			if (itm_row.getItem_3().getSchedule() == null) {
				scheduleDAO.loadSchedule(date_tomorrow, person.getId());
			} else {
				schedule = itm_row.getItem_3().getSchedule();
			}

			if (schedule.getShift() == null) {
				continue;
			}

			final UserShift shift = shift_cache.getUserShift(schedule.getShift());
			if (!shift.getPresence().booleanValue()) {
				continue;
			}

			statProcedure.workAssignProcedure(shift, date_tomorrow, person.getId(), null);

		}

		// upload grid
		setupGlobalSchedulerGridForShift();

	}

	/**
	 * Save Current scheduler updating values from grid
	 */
	private void saveCurrentScheduler() {

		if (currentSchedule == null) {
			currentSchedule = new Schedule();
		}

		// set data scheduler
		final Date date_schedule = getDateScheduled(selectedDay);
		currentSchedule.setDate_schedule(date_schedule);

		// set editor
		final Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		currentSchedule.setEditor(person.getId());

		// set user
		currentSchedule.setUser(selectedUser);

		// if shift not assigned, assign default one
		if ((shift_cache.getStandardWorkShift() != null) && (currentSchedule.getShift() == null)) {
			currentSchedule.setShift(shift_cache.getStandardWorkShift().getId());
		}

		scheduleDAO.saveOrUpdateSchedule(currentSchedule);

		currentSchedule = scheduleDAO.loadSchedule(date_schedule, selectedUser);
	}

	@Listen("onClick = #ok_day_shift")
	public void saveDayScheduling() {
		if (!checkConnection()) {
			return;
		}

		if (grid_scheduler_day.getSelectedItem() == null) {
			return;
		}

		if (shifts_combo_select.getSelectedItem() == null) {
			return;
		}

		if (selectedDay == null) {
			return;
		}

		final RowDaySchedule row_item = grid_scheduler_day.getSelectedItem().getValue();

		if (!(shifts_combo_select.getSelectedItem().getValue() instanceof UserShift)
				|| (shifts_combo_select.getSelectedItem().getValue() == null)) {
			return;
		}

		// get shift
		final UserShift shift = shifts_combo_select.getSelectedItem().getValue();

		// get day schedule
		final Date date_scheduled = getDateScheduled(selectedDay);

		// check for 10 day of work constraint:
		if (shift.getPresence()) {
			final List<Schedule> scheduleList = getScheduleTenDayBefore(date_scheduled, row_item.getUser());
			Integer lenght;
			if (scheduleList == null) {
				lenght = 10;
			} else {
				lenght = getCountWorkingDay(scheduleList);
			}

			if (lenght == 10) {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.CANCEL;

				Messagebox.show("Serie lavorativa superiore a 10 giorni. Sicuro di voler assegnare un turno di lavoro?",
						"CONFERMA INSERIMENTO", buttons, null, Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>() {

							@Override
							public void onEvent(final ClickEvent e) {
								if (Messagebox.ON_OK.equals(e.getName())) {

									SchedulerComposer.this.saveShift(shift, date_scheduled, row_item);

								} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
									return;
								}
							}
						}, params);
			} else {
				saveShift(shift, date_scheduled, row_item);
			}

		} else {
			saveShift(shift, date_scheduled, row_item);
		}

	}

	/**
	 * used in "preventivo" to save shift
	 *
	 * @param shift
	 * @param row_item
	 * @param date_scheduled
	 * @param replace
	 * @param scheduleListInWeek
	 */
	private void saveDayShiftProcedure(final UserShift shift, final RowDaySchedule row_item, final Date date_scheduled,
			final List<Schedule> scheduleListInWeek) {

		if ((scheduleListInWeek != null) && (shift_cache.getDailyShift() != null) && (shift_cache.getStandardWorkShift() != null)) {

			// replace break shift with standard or, if user is a daily worker,
			// daily shift

			final Boolean isDailyWorker = personDAO.loadPerson(row_item.getUser()).getDailyemployee();

			final Integer id_standardShift = shift_cache.getStandardWorkShift().getId();
			final Integer id_dailyShift = shift_cache.getDailyShift().getId();

			final Date to_day = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);

			for (final Schedule schedule : scheduleListInWeek) {

				if (!schedule.getDate_schedule().after(to_day)) {
					continue;
				}

				final UserShift userShift = configurationDAO.loadShiftById(schedule.getShift());

				if (userShift.getBreak_shift() || userShift.getWaitbreak_shift() || userShift.getDisease_shift()
						|| userShift.getAccident_shift()) {

					if (isDailyWorker) {
						schedule.setShift(id_dailyShift);
						scheduleDAO.saveOrUpdateSchedule(schedule);
					} else {
						schedule.setShift(id_standardShift);
						scheduleDAO.saveOrUpdateSchedule(schedule);
					}
				}
			}

		}
		if (day_after_config.getValue() == null) {

			// set only current day
			assignShiftFromDaySchedule(row_item, shift, 0, true);

		} else {

			if (shift.getBreak_shift().booleanValue() || shift.getWaitbreak_shift().booleanValue()) {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Non puoi usare il turno di riposo programmato o atteso per assegnazioni multiple.", "ERROR",
						buttons, null, Messagebox.EXCLAMATION, null, null, params);
				return;
			}

			// set multiple day..... check date before...

			final Date to_day = DateUtils.truncate(day_after_config.getValue(), Calendar.DATE);

			if (date_scheduled.after(to_day)) {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Attenzione alla data inserita", "ERROR", buttons, null, Messagebox.EXCLAMATION, null, null,
						params);
				return;
			}

			final int count = (int) ((to_day.getTime() - date_scheduled.getTime()) / (1000 * 60 * 60 * 24));

			if ((selectedDay + count) > SchedulerComposer.DAYS_IN_GRID_PREPROCESSING) {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Non puoi programmare oltre i limiti della griglia corrente. Usa Imposta Speciale ", "ERROR",
						buttons, null, Messagebox.EXCLAMATION, null, null, params);

				return;
			}

			if (count == 0) {
				// single..
				assignShiftFromDaySchedule(row_item, shift, 0, true);
			}

			// check day - multiple
			for (int i = 0; i <= count; i++) {

				// set day with offest i
				assignShiftFromDaySchedule(row_item, shift, i, false);
			}

		}

		setupGlobalSchedulerGridForDay();

		day_definition_popup.close();

	}

	/**
	 * Save program
	 */
	@Listen("onClick = #ok_program")
	public void saveProgram() {

		if (!checkConnection()) {
			return;
		}

		if ((selectedDay == null) || (selectedShift == null) || (selectedUser == null)) {
			return;
		}

		if (list_details_program == null) {
			return;
		}

		if (currentSchedule == null) {
			// save scheduler
			saveCurrentScheduler();
		}

		if (div_force_shift.isVisible() && (force_shift_combo.getSelectedItem() != null)) {

			// assign shift ---- FORCE
			final UserShift my_shift = force_shift_combo.getSelectedItem().getValue();
			statProcedure.shiftAssign(my_shift, currentSchedule.getDate_schedule(), currentSchedule.getUser(),
					currentSchedule.getEditor());

			// refresh current schedule
			currentSchedule = scheduleDAO.loadSchedule(currentSchedule.getDate_schedule(), currentSchedule.getUser());
		}

		if (define_program_body.isVisible()) {

			// save note:
			final String note = note_program.getValue();
			currentSchedule.setNote(note);
			saveCurrentScheduler();

			// check about sum of time
			Double sum = 0.0;
			if (list_details_program.size() != 0) {
				for (final DetailInitialSchedule detail : list_details_program) {
					sum = sum + detail.getTime();
				}
			}

			// check max 12 h in a day
			final List<DetailInitialSchedule> list_detail_schedule = scheduleDAO
					.loadDetailInitialScheduleByIdSchedule(currentSchedule.getId());

			Double count = sum;
			for (final DetailInitialSchedule dt : list_detail_schedule) {
				if (dt.getShift() != selectedShift) {
					count = count + dt.getTime();
					if (count > 12) {
						break;
					}
				}

			}
			if (count > 12) {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Non si possono assegnare più di 12 ore al giorno", "ERROR", buttons, null,
						Messagebox.EXCLAMATION, null, null, params);

				return;

			}

			// check for 12h constraints
			if (sum != 0.0) {

				// check between different day
				final Integer min_shift = statProcedure.getMinimumShift(currentSchedule.getDate_schedule(),
						currentSchedule.getUser());
				final Integer max_shift = statProcedure.getMaximumShift(currentSchedule.getDate_schedule(),
						currentSchedule.getUser());

				boolean check_12_different_day = (selectedShift.compareTo(min_shift) < 0)
						|| (selectedShift.compareTo(max_shift) > 0);

				if (!check_12_different_day) {
					// check in same day
					final Integer minShiftInDay = statProcedure.getFirstShiftInDay(currentSchedule.getDate_schedule(),
							currentSchedule.getUser());
					final Integer maxShiftInDay = statProcedure.getLastShiftInDay(currentSchedule.getDate_schedule(),
							currentSchedule.getUser());
					if ((minShiftInDay != null) && (maxShiftInDay != null) && !minShiftInDay.equals(selectedShift)
							&& !maxShiftInDay.equals(selectedShift)) {
						if (!((selectedShift.equals(1) && minShiftInDay.equals(4))
								|| (selectedShift.equals(4) && minShiftInDay.equals(1))
								|| (selectedShift.equals(2) && minShiftInDay.equals(3))
								|| (selectedShift.equals(3) && minShiftInDay.equals(2))
								|| (selectedShift.equals(3) && minShiftInDay.equals(4)) || (selectedShift.equals(4) && minShiftInDay
								.equals(3)))) {
							check_12_different_day = true;
						}
					}
				}

				if (check_12_different_day) {

					final Map<String, String> params = new HashMap<String, String>();
					params.put("sclass", "mybutton Button");

					final Messagebox.Button[] buttons = new Messagebox.Button[2];
					buttons[0] = Messagebox.Button.OK;
					buttons[1] = Messagebox.Button.CANCEL;

					final String msg = "Stai assegnando un turno prima di 12 ore di stacco. Sei sicuro di voler continuare?";
					Messagebox.show(msg, "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.EXCLAMATION, null,
							new CheckOnDoubleShiftBreaEvent(), params);

					return;

				}

			}

			// final step saving program
			saveProgramFinalStep();
		}

		// close popup
		shift_definition_popup.close();

	}

	private void saveProgramFinalStep() {
		// save details
		scheduleDAO.saveListDetailInitialScheduler(currentSchedule.getId(), selectedShift, list_details_program);

		// refresh grid
		setupGlobalSchedulerGridForShift();
	}

	/**
	 * Save review
	 */
	@Listen("onClick = #ok_review")
	public void saveReview() {

		if (!checkConnection()) {
			return;
		}

		if ((selectedShift == null) || (selectedUser == null)) {
			return;
		}

		if (list_details_review == null) {
			return;
		}

		if (currentSchedule == null) {
			// save scheduler
			saveCurrentScheduler();
		}

		// save note and controller:
		final Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final String note = note_review.getValue();
		currentSchedule.setNote(note);
		currentSchedule.setController(person.getId());
		saveCurrentScheduler();

		// check about sum of time
		Double sum = 0.0;
		if (list_details_review.size() != 0) {
			for (final DetailFinalSchedule detail : list_details_review) {
				sum = sum + detail.getTime();
			}
		}

		// check max 12 h in a day
		final List<DetailFinalSchedule> list_detail_schedule = scheduleDAO.loadDetailFinalScheduleByIdSchedule(currentSchedule
				.getId());
		Double count = sum;
		for (final DetailFinalSchedule dt : list_detail_schedule) {
			if (dt.getShift() != selectedShift) {
				count = count + dt.getTime();
				if (count > 12) {
					break;
				}
			}

		}
		if (count > 12) {

			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Non si possono assegnare più di 12 ore al giorno", "ATTENZIONE", buttons, null,
					Messagebox.EXCLAMATION, null, null, params);
			return;

		}

		// save details
		scheduleDAO.saveListDetailFinalScheduler(currentSchedule.getId(), selectedShift, list_details_review);

		scheduleDAO.updateMobileSynch(currentSchedule.getId(), sync_schedule.isChecked(), selectedShift);

		// refresh grid
		setupGlobalSchedulerGridForShiftReview();

		// Messagebox.show("Il consuntivo è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		shift_definition_popup_review.close();

	}

	/**
	 * Save hift in "Preventivo"
	 *
	 * @param shift
	 * @param date_scheduled
	 * @param row_item
	 */
	private void saveShift(final UserShift shift, final Date date_scheduled, final RowDaySchedule row_item) {

		// check if break shift are setting
		if (shift.getBreak_shift() || shift.getWaitbreak_shift() || shift.getDisease_shift() || shift.getAccident_shift()) {

			final List<Schedule> scheduleListInWeek = statProcedure.searchBreakInCurrentWeek(date_scheduled, row_item.getUser());

			if ((scheduleListInWeek != null) && (scheduleListInWeek.size() > 0)) {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[3];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.NO;
				buttons[2] = Messagebox.Button.CANCEL;

				Messagebox.show("Sono presenti nella settimana altri turni di riposo. Sostituirli con turni di lavoro?",
						"CONFERMA CANCELLAZIONE TURNI DI RIPOSO", buttons, null, Messagebox.EXCLAMATION, null,
						new BreakInWeekManagement(shift, scheduleListInWeek, row_item, date_scheduled), params);
			} else {
				saveDayShiftProcedure(shift, row_item, date_scheduled, null);
			}

		} else {

			// save not break shift
			saveDayShiftProcedure(shift, row_item, date_scheduled, null);

			// check if in this week there is a break
			final List<Schedule> scheduleListInWeek = statProcedure.searchBreakInCurrentWeek(date_scheduled, row_item.getUser());
			if (scheduleListInWeek == null) {

				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.NO;

				final String msg = "Non ci sono più riposi per questa settimana. Impostare automaticamente un riposo?";
				Messagebox.show(msg, "GESTIONE RIPOSI", buttons, null, Messagebox.EXCLAMATION, null, new ReassignBreakEvent(
						date_scheduled, row_item), params);

			}
		}
	}

	@Listen("onClick= #overview_selector_select_all")
	public void selectAllShiftsInComboOverview() {

		select_shifttype_overview.setSelectedItem(null);

		// force refresh
		setOverviewLists(date_from_overview.getValue(), date_to_overview.getValue());

	}

	@Listen("onClick= #overview_selector_select_allTask")
	public void selectAllTaskInComboOverview() {
		taskComboBox.setSelectedItem(null);

		// force refresh
		setOverviewLists(date_from_overview.getValue(), date_to_overview.getValue());
	}

	@Listen("onClick= #preprocessing_select_rp")
	public void selectBreakShiftInCombo() {

		final UserShift break_shift = shift_cache.getBreakShift();

		if (break_shift == null) {
			return;
		}

		for (final Comboitem item : shifts_combo_select.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (break_shift.equals(current_shift_item)) {
					shifts_combo_select.setSelectedItem(item);
					break;
				}

			}
		}
	}

	@Listen("onClick= #overview_selector_select_rp")
	public void selectBreakShiftInComboOverview() {

		final UserShift bshift = shift_cache.getBreakShift();

		if (bshift == null) {
			return;
		}

		for (final Comboitem item : select_shifttype_overview.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (bshift.equals(current_shift_item)) {
					select_shifttype_overview.setSelectedItem(item);
					break;
				}

			}
		}

		// force refresh
		setOverviewLists(date_from_overview.getValue(), date_to_overview.getValue());
	}

	@Listen("onClick= #programpopup_select_rp")
	public void selectBreakShiftInComboShiftForceProgram() {

		final UserShift bshift = shift_cache.getBreakShift();

		if (bshift == null) {
			return;
		}

		for (final Comboitem item : force_shift_combo.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (bshift.equals(current_shift_item)) {
					force_shift_combo.setSelectedItem(item);
					break;
				}

			}
		}

		// some thing is changed
		forceProgramShift();
	}

	@Listen("onClick= #overview_selector_select_dl")
	public void selectDailyInComboOverview() {

		final UserShift daily = shift_cache.getDailyShift();

		if (daily == null) {
			return;
		}

		for (final Comboitem item : select_shifttype_overview.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (daily.equals(current_shift_item)) {
					select_shifttype_overview.setSelectedItem(item);
					break;
				}

			}
		}

		// force refresh
		setOverviewLists(date_from_overview.getValue(), date_to_overview.getValue());
	}

	@Listen("onClick= #shift_period_select_dl")
	public void selectDailyInComboShiftPeriodAssign() {

		final UserShift daily = shift_cache.getDailyShift();

		if (daily == null) {
			return;
		}

		for (final Comboitem item : shift_period_combo.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (daily.equals(current_shift_item)) {
					shift_period_combo.setSelectedItem(item);
					break;
				}

			}
		}
	}

	@Listen("onClick= #preprocessing_select_dl")
	public void selectDailySgiftInCombo() {

		final UserShift daily = shift_cache.getDailyShift();

		if (daily == null) {
			return;
		}

		for (final Comboitem item : shifts_combo_select.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (daily.equals(current_shift_item)) {
					shifts_combo_select.setSelectedItem(item);
					break;
				}

			}
		}
	}

	@Listen("onClick= #programpopup_select_dl")
	public void selectDailyShiftInComboShiftForceProgram() {

		final UserShift daily = shift_cache.getDailyShift();

		if (daily == null) {
			return;
		}

		for (final Comboitem item : force_shift_combo.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (daily.equals(current_shift_item)) {
					force_shift_combo.setSelectedItem(item);
					break;
				}

			}
		}

		// some thing is changed
		forceProgramShift();
	}

	@Listen("onChange =#select_year")
	public void selectedYear() {
		if (select_year.getSelectedItem() != null) {

			final String yearSelected = select_year.getSelectedItem().getValue();

			if (!yearSelected.equals(SchedulerComposer.ALL_ITEM)) {

				date_from_overview.setValue(null);
				date_to_overview.setValue(null);

				final Integer year = Integer.parseInt(yearSelected);
				Date date_from;
				Date date_to;

				final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

				final String dateFromString = "01/01/" + year;
				final String dateToString = "31/12/" + year;

				try {
					date_from = sdf.parse(dateFromString);
					date_to = sdf.parse(dateToString);
				} catch (final ParseException e) {
					return;
				}

				setOverviewLists(date_from, date_to);

			} else {
				date_from_overview.setValue(null);
				date_to_overview.setValue(null);
				setOverviewLists(null, null);
			}
		}
	}

	@Listen("onClick= #preprocessing_select_tl")
	public void selectStandardWorkInCombo() {

		final UserShift stw = shift_cache.getStandardWorkShift();

		if (stw == null) {
			return;
		}

		for (final Comboitem item : shifts_combo_select.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (stw.equals(current_shift_item)) {
					shifts_combo_select.setSelectedItem(item);
					break;
				}

			}
		}
	}

	@Listen("onClick= #programpopup_select_tl")
	public void selectStandardWorkInComboForceProgram() {

		final UserShift stw = shift_cache.getStandardWorkShift();

		if (stw == null) {
			return;
		}

		for (final Comboitem item : force_shift_combo.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (stw.equals(current_shift_item)) {
					force_shift_combo.setSelectedItem(item);
					break;
				}

			}
		}

		// some thing is changed
		forceProgramShift();
	}

	@Listen("onClick= #overview_selector_select_tl")
	public void selectStandardWorkInComboOverview() {

		final UserShift stw = shift_cache.getStandardWorkShift();

		if (stw == null) {
			return;
		}

		for (final Comboitem item : select_shifttype_overview.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (stw.equals(current_shift_item)) {
					select_shifttype_overview.setSelectedItem(item);
					break;
				}

			}
		}

		// force refresh
		setOverviewLists(date_from_overview.getValue(), date_to_overview.getValue());

	}

	@Listen("onClick= #shift_period_select_tl")
	public void selectStandardWorkInComboShiftPeriodAssign() {

		final UserShift stw = shift_cache.getStandardWorkShift();

		if (stw == null) {
			return;
		}

		for (final Comboitem item : shift_period_combo.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (stw.equals(current_shift_item)) {
					shift_period_combo.setSelectedItem(item);
					break;
				}

			}
		}
	}

	private void setCheckTotalHoursReview(final Double totalReviewDay1, final Double totalReviewDay2, final Auxheader t1,
			final Auxheader t2) {

		final String styleOK = "position:absolute;right:0;top:5;background-color:#629B58; padding: 0px 0px 0px 5px ; text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);border-radius: 25px;	min-height: 0;color: #FFF !important;&:hover {}; &[disabled] {}; &:focus {}; ";

		List<Component> children = t1.getChildren();

		for (int i = 0; i < children.size(); i++) {
			t1.removeChild(children.get(i));
		}

		children = t2.getChildren();

		for (int i = 0; i < children.size(); i++) {
			t2.removeChild(children.get(i));
		}

		if (totalReviewDay1.equals(totalReviewDay2)) {

			final A a1 = new A();
			final A a2 = new A();

			a1.setIconSclass("z-icon-check fa-2x");
			a1.setStyle(styleOK);
			a1.setHeight("25px");

			a2.setIconSclass("z-icon-check fa-2x");
			a2.setStyle(styleOK);

			// okButton.setDisabled(true);

			t1.appendChild(a1);
			t2.appendChild(a2);

			return;

		}

		final String style = "position:absolute;right:0;top:5;background-color:#D15B47; padding: 3px 12px; text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);border-radius: 25px;	min-height: 0;color: #FFF !important;";

		Double diff = 0.0;
		if (totalReviewDay1 < totalReviewDay2) {

			diff = totalReviewDay2 - totalReviewDay1;
			final String h = Utility.decimatToTime(diff);
			final Label lab1 = new Label("+" + h);
			final Label lab2 = new Label("-" + h);
			lab1.setStyle(style);
			lab2.setStyle(style);
			t1.appendChild(lab1);
			t2.appendChild(lab2);

		} else {
			diff = totalReviewDay1 - totalReviewDay2;
			final String h = Utility.decimatToTime(diff);
			final Label lab2 = new Label("+" + h);
			final Label lab1 = new Label("-" + h);
			lab2.setStyle(style);
			lab1.setStyle(style);
			t2.appendChild(lab2);
			t1.appendChild(lab1);
		}

	}

	private void setCheckTotalUserReview(final Integer totalUser_review_day_1, final Integer totalUser_review_day_2,
			final Auxheader t1, final Auxheader t2) {

		List<Component> children = t1.getChildren();

		for (int i = 0; i < children.size(); i++) {
			t1.removeChild(children.get(i));
		}

		children = t2.getChildren();

		for (int i = 0; i < children.size(); i++) {
			t2.removeChild(children.get(i));
		}

		final String styleOK = "position:absolute;right:0;top:5;background-color:#629B58; padding: 0px 0px 0px 5px ; text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);border-radius: 25px;	min-height: 0;color: #FFF !important;&:hover {}; &[disabled] {}; &:focus {}; ";

		if (totalUser_review_day_1 == totalUser_review_day_2) {

			final A a1 = new A();
			final A a2 = new A();

			a1.setIconSclass("z-icon-check fa-2x");
			a1.setStyle(styleOK);
			a1.setHeight("25px");

			a2.setIconSclass("z-icon-check fa-2x");
			a2.setStyle(styleOK);

			t1.appendChild(a1);
			t2.appendChild(a2);

			return;

		}

		final String style = "position:absolute;right:0;top:5;background-color:#D15B47; padding: 3px 12px; text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);border-radius: 25px;	min-height: 0;color: #FFF !important;";

		Integer diff = 0;
		if (totalUser_review_day_1 < totalUser_review_day_2) {

			diff = totalUser_review_day_2 - totalUser_review_day_1;
			final Label lab1 = new Label("-" + diff.toString());
			final Label lab2 = new Label("+" + diff.toString());
			lab1.setStyle(style);
			lab2.setStyle(style);
			t1.appendChild(lab1);
			t2.appendChild(lab2);

		} else {
			diff = totalUser_review_day_1 - totalUser_review_day_2;
			final Label lab1 = new Label("+" + diff.toString());
			final Label lab2 = new Label("-" + diff.toString());
			lab2.setStyle(style);
			lab1.setStyle(style);
			t2.appendChild(lab2);
			t1.appendChild(lab1);
		}

	}

	private void setDefaultValueTimeInPopupReview(final int shift, final Timebox timefrom, final Timebox timeto) {
		final Calendar cal = Calendar.getInstance();
		int to = 0;
		int from = 0;

		switch (shift) {
		case 1:
			to = 1;
			from = 7;
			break;
		case 2:
			to = 7;
			from = 13;
			break;
		case 3:
			to = 13;
			from = 19;
			break;
		case 4:
			to = 19;
			from = 1;
			break;
		default:
			break;
		}

		cal.set(Calendar.HOUR_OF_DAY, to);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		timefrom.setValue(cal.getTime());

		cal.set(Calendar.HOUR_OF_DAY, from);

		timeto.setValue(cal.getTime());
	}

	/**
	 *
	 * @param initial_date
	 */
	private void setGridStructureForDay(final Date initial_date) {

		if (initial_date == null) {
			return;
		}

		firstDateInGrid = DateUtils.truncate(initial_date, Calendar.DATE);

		// days
		for (int i = 0; i < SchedulerComposer.DAYS_IN_GRID_PREPROCESSING; i++) {

			final int index_day = i + 1;

			final Auxheader day_label = (Auxheader) getSelf().getFellowIfAny("day_label_" + index_day);
			final Listheader day_number = (Listheader) getSelf().getFellowIfAny("day_numb_" + index_day);
			if ((day_label == null) || (day_number == null)) {
				continue;
			}

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(firstDateInGrid);
			current_calendar.add(Calendar.DAY_OF_YEAR, i);

			final String day_n = SchedulerComposer.formatter_e.format(current_calendar.getTime());
			final String day_l = SchedulerComposer.formatter_dd.format(current_calendar.getTime());

			if (current_calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				day_number.setStyle("color:red;");
				day_label.setStyle("color:red;");
			} else {
				day_number.setStyle("color:black");
				day_label.setStyle("color:black");
			}

			// color bank holidays
			final String day_MMdd = SchedulerComposer.formatter_MMdd.format(current_calendar.getTime());
			if (bank_holiday.getDays().contains(day_MMdd)) {
				day_number.setStyle("color:red");
				day_label.setStyle("color:red");
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
		firstDateInGrid = DateUtils.truncate(calendar.getTime(), Calendar.DATE);

		// set seven days
		for (int i = 0; i < SchedulerComposer.DAYS_IN_GRID_PROGRAM; i++) {

			final int index_day = i + 1;

			final Auxheader month_head = (Auxheader) getSelf().getFellowIfAny("day_month_" + index_day);

			if (month_head == null) {
				continue;
			}

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(firstDateInGrid);
			current_calendar.add(Calendar.DAY_OF_YEAR, i);

			final String day_m = SchedulerComposer.formatter_ddmmm.format(current_calendar.getTime());

			if (current_calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				month_head.setStyle("color:red");
			} else {
				month_head.setStyle("color:black");
			}

			// color bank holidays
			final String day_MMdd = SchedulerComposer.formatter_MMdd.format(current_calendar.getTime());
			if (bank_holiday.getDays().contains(day_MMdd)) {
				month_head.setStyle("color:red");
			}

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
		firstDateInGrid = DateUtils.truncate(calendar.getTime(), Calendar.DATE);

		// set seven days
		for (int i = 0; i < SchedulerComposer.DAYS_TO_SHOW_IN_REVIEW; i++) {

			final int index_day = i + 1;

			final Auxheader month_head = (Auxheader) getSelf().getFellowIfAny("day_month_review_" + index_day);

			if (month_head == null) {
				continue;
			}

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(firstDateInGrid);

			// show the final same day in two different column
			// current_calendar.add(Calendar.DAY_OF_YEAR, i);

			final String day_m = SchedulerComposer.formatter_ddmmm.format(current_calendar.getTime());

			if (current_calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				month_head.setStyle("color:red");
			} else {
				month_head.setStyle("color:black");
			}

			// color bank holidays
			final String day_MMdd = SchedulerComposer.formatter_MMdd.format(current_calendar.getTime());
			if (bank_holiday.getDays().contains(day_MMdd)) {
				month_head.setStyle("color:red");
			}

			month_head.setLabel(day_m.toUpperCase());

		}

	}

	private void setLabelListBadge(final Integer idSchedule) {
		final List<Badge> badgeList = scheduleDAO.loadBadgeByScheduleId(idSchedule);

		String badgeInfo = "";

		if (badgeList != null) {
			for (final Badge badge : badgeList) {
				if (badge != null) {
					if (!badge.getEventType()) {
						badgeInfo = badgeInfo + " - E: " + Utility.getTimeFormat().format(badge.getEventTime());
					} else {
						badgeInfo = badgeInfo + " - U: " + Utility.getTimeFormat().format(badge.getEventTime());
					}
				}
			}
		}

		infoBadge.setValue(badgeInfo);

	}

	private void setLabelTotalHoursProgram(final ListModelList<DetailInitialSchedule> model) {
		if (model != null) {
			double total = 0;
			for (final DetailInitialSchedule detailInitialSchedule : model) {

				if (detailInitialSchedule.getTime() != null) {
					total += detailInitialSchedule.getTime();
				}
				if (detailInitialSchedule.getTime_vacation() != null) {
					total += detailInitialSchedule.getTime_vacation();
				}
			}
			totalHours_Program.setValue("Totale Ore Programmate: " + Utility.decimatToTime(total));
		}

	}

	private void setLabelTotalHoursReview(final ListModelList<DetailFinalSchedule> model) {
		if (model != null) {
			// set Label count hours added in listbox_progra
			double total = 0;
			for (final DetailFinalSchedule detailFinalSchedule : model) {
				if (detailFinalSchedule.getTime() != null) {
					total += detailFinalSchedule.getTime();
				}
				if (detailFinalSchedule.getTime_vacation() != null) {
					total += detailFinalSchedule.getTime_vacation();
				}
			}
			totalHours_Review.setValue("Totale Ore Consuntivate: " + Utility.decimatToTime(total));

		}

	}

	/**
	 * set last programmer label
	 */
	private void setLastProgrammerLabel() {

		final Comboitem version_selected = scheduler_type_selector.getSelectedItem();

		if ((version_selected == null) || (version_selected == overview_item)) {
			last_programmer_tag.setVisible(false);
			return;
		}

		last_programmer_tag.setVisible(true);

		if (version_selected == overview_item) {
			lastProgrammer.setValue("");
			return;
		}

		LockTable lockTable = null;

		if ((version_selected == preprocessing_item) || (version_selected == program_item)) {
			lockTable = lockTableDAO.loadLastLockTableByTableType(TableTag.PROGRAM_TABLE);
		} else if (version_selected == review_item) {
			lockTable = lockTableDAO.loadLastLockTableByTableType(TableTag.REVIEW_TABLE);
		}

		final Person person = personDAO.loadPerson(lockTable.getId_user());

		if (person != null) {

			lastProgrammer.setValue(person.getFirstname() + " " + person.getLastname() + " "
					+ SchedulerComposer.formatter_last_p.format(lockTable.getTime_to()));
		}

	}

	@Listen("onChange = #date_to_overview, #date_from_overview")
	public void setNullYearSelected() {
		select_year.setSelectedItem(null);
		defineSchedulerView();
	}

	/**
	 * Overview list
	 */
	private void setOverviewLists(final Date date_from_overview, final Date date_to_overview) {

		String full_text_search = null;
		Integer shift_number = null;
		Date date_from = null;
		Date date_to = null;

		UserTask taskSelected = null;
		if ((taskComboBox.getSelectedItem() != null) && (taskComboBox.getSelectedItem().getValue() instanceof UserTask)) {
			taskSelected = taskComboBox.getSelectedItem().getValue();
		}

		// select full_text searching
		if ((this.full_text_search.getValue() != null) && !this.full_text_search.getValue().equals("")) {
			full_text_search = this.full_text_search.getValue();
		}

		// select shift
		if (select_shift_overview.getSelectedItem() != null) {
			final String value = select_shift_overview.getValue();
			if (!value.equals(SchedulerComposer.ALL_ITEM)) {
				if (NumberUtils.isNumber(select_shift_overview.getValue())) {
					shift_number = Integer.parseInt(select_shift_overview.getValue());
				}

			}
		}

		// select date
		if (date_from_overview != null) {
			date_from = date_from_overview;
		}
		if (date_to_overview != null) {
			date_to = date_to_overview;
		}
		if ((date_from == null) && (date_to != null)) {
			date_from = date_to;
		}
		if ((date_from != null) && (date_to == null)) {
			date_to = date_from;
		}
		if ((date_from != null) && date_from.after(date_to)) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox
					.show("Controlla le date inserite", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		Integer shift_type = null;

		if ((select_shifttype_overview.getSelectedItem() != null)
				&& (select_shifttype_overview.getSelectedItem().getValue() instanceof UserShift)) {
			final UserShift shift = select_shifttype_overview.getSelectedItem().getValue();
			if (shift != null) {
				shift_type = shift.getId();
			}
		}

		// select list
		if (overview_review.isSelected()) {

			Integer idSelectedTask = null;
			if (taskSelected != null) {
				idSelectedTask = taskSelected.getId();
			}

			listDetailRevision = statisticDAO.listDetailFinalSchedule(full_text_search, shift_number, shift_type, idSelectedTask,
					date_from, date_to);

			double count_h = 0;
			double count_h_c = 0;

			for (final DetailFinalSchedule item : listDetailRevision) {

				if (item.getTime() != null) {
					count_h += item.getTime();
				}

				if (item.getTime_vacation() != null) {
					count_h_c += item.getTime_vacation();
				}

			}

			overview_count_h.setValue("" + Utility.roundTwo(count_h));
			overview_count_h_c.setValue("" + Utility.roundTwo(count_h_c));

			// set number of row showed
			list_overview_review.setModel(new ListModelList<DetailFinalSchedule>(listDetailRevision));
			if ((shows_rows.getValue() != null) && (shows_rows.getValue() != 0)) {
				list_overview_review.setPageSize(shows_rows.getValue());
			}
		} else if (overview_program.isSelected()) {

			Integer idSelectedTask = null;
			if (taskSelected != null) {
				idSelectedTask = taskSelected.getId();
			}

			listDetailProgram = statisticDAO.listDetailInitialSchedule(full_text_search, shift_number, shift_type,
					idSelectedTask, date_from, date_to);

			double count_h = 0;
			double count_h_c = 0;

			for (final DetailInitialSchedule item : listDetailProgram) {

				if (item.getTime() != null) {
					count_h += item.getTime();
				}

				if (item.getTime_vacation() != null) {
					count_h_c += item.getTime_vacation();
				}

			}

			overview_count_h.setValue("" + Utility.roundTwo(count_h));
			overview_count_h_c.setValue("" + Utility.roundTwo(count_h_c));

			// set number of row showed
			list_overview_program.setModel(new ListModelList<DetailInitialSchedule>(listDetailProgram));
			if ((shows_rows.getValue() != null) && (shows_rows.getValue() != 0)) {
				list_overview_program.setPageSize(shows_rows.getValue());
			}

		} else if (overview_preprocessing.isSelected()) {

			listSchedule = statisticDAO.listSchedule(full_text_search, shift_type, date_from, date_to);

			overview_count_h.setValue("");
			overview_count_h_c.setValue("");

			// set number of row showed
			list_overview_preprocessing.setModel(new ListModelList<Schedule>(listSchedule));
			if ((shows_rows.getValue() != null) && (shows_rows.getValue() != 0)) {
				list_overview_preprocessing.setPageSize(shows_rows.getValue());
			}

		}
	}

	@Listen("onChange = #ship")
	public void setShipComboBox() {
		if (ship.getSelectedItem() == null) {
			return;
		}
		if (ship.getSelectedItem().getValue() instanceof Ship) {
			final Ship ship = this.ship.getSelectedItem().getValue();
			if (ship != null) {
				if (ship.getId() != -1) {
					shipInDay.setSelectedItem(null);
				}
			}
			// update ship selected
			shipSelected = ship;
		}
	}

	@Listen("onChange = #shipInDay")
	public void setShipInDayComboBox() {
		if (shipInDay.getSelectedItem() != null) {
			if (shipInDay.getSelectedItem().getValue() instanceof Ship) {
				final Ship ship = shipInDay.getSelectedItem().getValue();
				if (ship != null) {
					if (ship.getId() != -1) {
						this.ship.setSelectedItem(null);
					}
				}
				// update ship selected
				shipSelected = ship;
			}
		}
	}

	@Listen("onClick = #go_today_preprocessing")
	public void setTodaySchedulerView() {
		final Calendar calendar = Calendar.getInstance();
		final Date today = calendar.getTime();

		defineSchedulerViewToDate(today);

	}

	/**
	 * @param info_visibility
	 *            if true set info scheduler for programming visible
	 */
	private void setupGlobalSchedulerGridForDay() {

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstDateInGrid);
		calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAYS_IN_GRID_PREPROCESSING);
		final Date final_date = calendar.getTime();

		List<Schedule> list = null;

		if ((full_text_search.getValue() == null) || full_text_search.getValue().equals("")) {
			list = scheduleDAO.selectSchedulersForPreprocessing(firstDateInGrid, final_date, null);
		} else {
			list = scheduleDAO.selectSchedulersForPreprocessing(firstDateInGrid, final_date, full_text_search.getValue());
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
				initializeDateForDaySchedule(currentRow, firstDateInGrid);

			}

			// set correct day
			final long day_on_current_calendar = getDayOfSchedule(schedule);

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
		if ((shows_rows.getValue() != null) && (shows_rows.getValue() != 0)) {
			grid_scheduler_day.setPageSize(shows_rows.getValue());
		}
		grid_scheduler_day.setModel(new ListModelList<RowDaySchedule>(list_row));

	}

	/**
	 * @param info_visibility
	 *            if true set info scheduler for programming visible
	 */
	private void setupGlobalSchedulerGridForShift() {

		// the day for define user availability for to morrow
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		// user availability and color
		final HashMap<Integer, String> map_status = defineUserAvailability(calendar.getTime());

		// setup final day for program
		final Calendar final_calendar = Calendar.getInstance();
		final_calendar.setTime(firstDateInGrid);
		final_calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAYS_IN_GRID_PROGRAM);
		final Date final_date_program = final_calendar.getTime();

		// setup initial day for program
		final Calendar initial_calendar = Calendar.getInstance();
		initial_calendar.setTime(firstDateInGrid);
		initial_calendar.add(Calendar.DAY_OF_YEAR, SchedulerComposer.DAY_REVIEW_IN_PROGRAM_SHIFT);
		final Date initial_date_program = initial_calendar.getTime();

		// take info about person
		String text_search_person = null;
		if ((full_text_search.getValue() == null) || full_text_search.getValue().equals("")) {
			text_search_person = null;
		} else {
			text_search_person = full_text_search.getValue();
		}
		final List<Schedule> list_program = scheduleDAO.selectAggregateSchedulersProgram(initial_date_program,
				final_date_program, text_search_person);

		list_rows_program = new ArrayList<RowSchedule>();
		RowSchedule currentRow = null;

		// create a map for define people scheduled
		final HashMap<Integer, RowSchedule> sign_scheduled = new HashMap<Integer, RowSchedule>();

		// set variable for count
		final Double[][] count_matrix = new Double[5][4];
		for (int i = 0; i < count_matrix.length; i++) {
			for (int j = 0; j < count_matrix[i].length; j++) {
				count_matrix[i][j] = 0.0;
			}
		}

		// set variable for count persons
		final Integer[][] count_matrixUsers = new Integer[5][4];
		for (int i = 0; i < count_matrixUsers.length; i++) {
			for (int j = 0; j < count_matrixUsers[i].length; j++) {
				count_matrixUsers[i][j] = 0;
			}
		}

		// set variable for count total person in day
		final Integer[] count_Day_Users = new Integer[5];
		for (int i = 0; i < count_Day_Users.length; i++) {
			count_Day_Users[i] = 0;
		}

		// under program
		for (int i = 0; i < list_program.size(); i++) {

			final Schedule schedule = list_program.get(i);

			// if the user is changed, add another row
			if ((currentRow == null) || (!currentRow.getUser().equals(schedule.getUser()))) {
				// set current row
				currentRow = new RowSchedule();
				currentRow.setUser(schedule.getUser());
				currentRow.setName_user(schedule.getName_user());
				list_rows_program.add(currentRow);

				// set user type for available
				if (map_status.containsKey(schedule.getUser())) {
					final String status = map_status.get(schedule.getUser());
					currentRow.setUser_status(status);
				}

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), currentRow);
			}

			// set correct day
			final int day_on_current_calendar = getDayOfSchedule(schedule);
			final ItemRowSchedule itemsRow = getItemRowSchedule(currentRow, day_on_current_calendar, schedule, true);

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
		final List<Schedule> list_revision = scheduleDAO.selectAggregateSchedulersRevision(firstDateInGrid, initial_date_program,
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
				list_rows_program.add(myrow);

				// set user type for available
				if (map_status.containsKey(schedule.getUser())) {
					final String status = map_status.get(schedule.getUser());
					myrow.setUser_status(status);
				}

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), myrow);
			}

			// set correct day
			final int day_on_current_calendar = getDayOfSchedule(schedule);
			final ItemRowSchedule itemsRow = getItemRowSchedule(myrow, day_on_current_calendar, schedule, true);

			if (day_on_current_calendar == 1) {
				myrow.setItem_1(itemsRow);
			}

		}

		// count review
		for (final RowSchedule itemrow_count : list_rows_program) {

			final Person user = personDAO.loadPerson(itemrow_count.getUser());

			if (!user.isInOffice()) {
				if (itemrow_count.getItem_1().getAnchorValue1() != 0) {
					count_matrixUsers[0][0]++;
				}

				if (itemrow_count.getItem_1().getAnchorValue2() != 0) {
					count_matrixUsers[0][1]++;
				}

				if (itemrow_count.getItem_1().getAnchorValue3() != 0) {
					count_matrixUsers[0][2]++;
				}

				if (itemrow_count.getItem_1().getAnchorValue4() != 0) {
					count_matrixUsers[0][3]++;
				}

				if (((itemrow_count.getItem_1().getAnchorValue1() != 0) || (itemrow_count.getItem_1().getAnchorValue2() != 0)
						|| (itemrow_count.getItem_1().getAnchorValue3() != 0) || (itemrow_count.getItem_1().getAnchorValue4() != 0))) {
					count_Day_Users[0]++;
				}

				count_matrix[0][0] = count_matrix[0][0] + itemrow_count.getItem_1().getAnchorValue1();
				count_matrix[0][1] = count_matrix[0][1] + itemrow_count.getItem_1().getAnchorValue2();
				count_matrix[0][2] = count_matrix[0][2] + itemrow_count.getItem_1().getAnchorValue3();
				count_matrix[0][3] = count_matrix[0][3] + itemrow_count.getItem_1().getAnchorValue4();

				if (itemrow_count.getItem_2().getAnchorValue1() != 0) {
					count_matrixUsers[1][0]++;
				}

				if (itemrow_count.getItem_2().getAnchorValue2() != 0) {
					count_matrixUsers[1][1]++;
				}

				if (itemrow_count.getItem_2().getAnchorValue3() != 0) {
					count_matrixUsers[1][2]++;
				}

				if (itemrow_count.getItem_2().getAnchorValue4() != 0) {
					count_matrixUsers[1][3]++;
				}

				if (((itemrow_count.getItem_2().getAnchorValue1() != 0) || (itemrow_count.getItem_2().getAnchorValue2() != 0)
						|| (itemrow_count.getItem_2().getAnchorValue3() != 0) || (itemrow_count.getItem_2().getAnchorValue4() != 0))) {
					count_Day_Users[1]++;
				}

				count_matrix[1][0] = count_matrix[1][0] + itemrow_count.getItem_2().getAnchorValue1();
				count_matrix[1][1] = count_matrix[1][1] + itemrow_count.getItem_2().getAnchorValue2();
				count_matrix[1][2] = count_matrix[1][2] + itemrow_count.getItem_2().getAnchorValue3();
				count_matrix[1][3] = count_matrix[1][3] + itemrow_count.getItem_2().getAnchorValue4();

				if (itemrow_count.getItem_3().getAnchorValue1() != 0) {
					count_matrixUsers[2][0]++;
				}

				if (itemrow_count.getItem_3().getAnchorValue2() != 0) {
					count_matrixUsers[2][1]++;
				}

				if (itemrow_count.getItem_3().getAnchorValue3() != 0) {
					count_matrixUsers[2][2]++;
				}

				if (itemrow_count.getItem_3().getAnchorValue4() != 0) {
					count_matrixUsers[2][3]++;
				}

				if (((itemrow_count.getItem_3().getAnchorValue1() != 0) || (itemrow_count.getItem_3().getAnchorValue2() != 0)
						|| (itemrow_count.getItem_3().getAnchorValue3() != 0) || (itemrow_count.getItem_3().getAnchorValue4() != 0))) {
					count_Day_Users[2]++;
				}

				count_matrix[2][0] = count_matrix[2][0] + itemrow_count.getItem_3().getAnchorValue1();
				count_matrix[2][1] = count_matrix[2][1] + itemrow_count.getItem_3().getAnchorValue2();
				count_matrix[2][2] = count_matrix[2][2] + itemrow_count.getItem_3().getAnchorValue3();
				count_matrix[2][3] = count_matrix[2][3] + itemrow_count.getItem_3().getAnchorValue4();

				if (itemrow_count.getItem_4().getAnchorValue1() != 0) {
					count_matrixUsers[3][0]++;
				}

				if (itemrow_count.getItem_4().getAnchorValue2() != 0) {
					count_matrixUsers[3][1]++;
				}

				if (itemrow_count.getItem_4().getAnchorValue3() != 0) {
					count_matrixUsers[3][2]++;
				}

				if (itemrow_count.getItem_4().getAnchorValue4() != 0) {
					count_matrixUsers[3][3]++;
				}

				if (((itemrow_count.getItem_4().getAnchorValue1() != 0) || (itemrow_count.getItem_4().getAnchorValue2() != 0)
						|| (itemrow_count.getItem_4().getAnchorValue3() != 0) || (itemrow_count.getItem_4().getAnchorValue4() != 0))) {
					count_Day_Users[3]++;
				}

				count_matrix[3][0] = count_matrix[3][0] + itemrow_count.getItem_4().getAnchorValue1();
				count_matrix[3][1] = count_matrix[3][1] + itemrow_count.getItem_4().getAnchorValue2();
				count_matrix[3][2] = count_matrix[3][2] + itemrow_count.getItem_4().getAnchorValue3();
				count_matrix[3][3] = count_matrix[3][3] + itemrow_count.getItem_4().getAnchorValue4();

				if (itemrow_count.getItem_5().getAnchorValue1() != 0) {
					count_matrixUsers[4][0]++;
				}

				if (itemrow_count.getItem_5().getAnchorValue2() != 0) {
					count_matrixUsers[4][1]++;
				}

				if (itemrow_count.getItem_5().getAnchorValue3() != 0) {
					count_matrixUsers[4][2]++;
				}

				if (itemrow_count.getItem_5().getAnchorValue4() != 0) {
					count_matrixUsers[4][3]++;
				}

				if (((itemrow_count.getItem_5().getAnchorValue1() != 0) || (itemrow_count.getItem_5().getAnchorValue2() != 0)
						|| (itemrow_count.getItem_5().getAnchorValue3() != 0) || (itemrow_count.getItem_5().getAnchorValue4() != 0))) {
					count_Day_Users[4]++;
				}

				count_matrix[4][0] = count_matrix[4][0] + itemrow_count.getItem_5().getAnchorValue1();
				count_matrix[4][1] = count_matrix[4][1] + itemrow_count.getItem_5().getAnchorValue2();
				count_matrix[4][2] = count_matrix[4][2] + itemrow_count.getItem_5().getAnchorValue3();
				count_matrix[4][3] = count_matrix[4][3] + itemrow_count.getItem_5().getAnchorValue4();

			}
		}

		// set sum review
		program_tot_1_1.setLabel(Utility.decimatToTime(count_matrix[0][0]));
		program_tot_1_2.setLabel(Utility.decimatToTime(count_matrix[0][1]));
		program_tot_1_3.setLabel(Utility.decimatToTime(count_matrix[0][2]));
		program_tot_1_4.setLabel(Utility.decimatToTime(count_matrix[0][3]));

		program_tot_2_1.setLabel(Utility.decimatToTime(count_matrix[1][0]));
		program_tot_2_2.setLabel(Utility.decimatToTime(count_matrix[1][1]));
		program_tot_2_3.setLabel(Utility.decimatToTime(count_matrix[1][2]));
		program_tot_2_4.setLabel(Utility.decimatToTime(count_matrix[1][3]));

		program_tot_3_1.setLabel(Utility.decimatToTime(count_matrix[2][0]));
		program_tot_3_2.setLabel(Utility.decimatToTime(count_matrix[2][1]));
		program_tot_3_3.setLabel(Utility.decimatToTime(count_matrix[2][2]));
		program_tot_3_4.setLabel(Utility.decimatToTime(count_matrix[2][3]));

		program_tot_4_1.setLabel(Utility.decimatToTime(count_matrix[3][0]));
		program_tot_4_2.setLabel(Utility.decimatToTime(count_matrix[3][1]));
		program_tot_4_3.setLabel(Utility.decimatToTime(count_matrix[3][2]));
		program_tot_4_4.setLabel(Utility.decimatToTime(count_matrix[3][3]));

		program_tot_5_1.setLabel(Utility.decimatToTime(count_matrix[4][0]));
		program_tot_5_2.setLabel(Utility.decimatToTime(count_matrix[4][1]));
		program_tot_5_3.setLabel(Utility.decimatToTime(count_matrix[4][2]));
		program_tot_5_4.setLabel(Utility.decimatToTime(count_matrix[4][3]));

		// set sum for persons
		programUser_tot_1_1.setLabel(count_matrixUsers[0][0].toString());
		programUser_tot_1_2.setLabel(count_matrixUsers[0][1].toString());
		programUser_tot_1_3.setLabel(count_matrixUsers[0][2].toString());
		programUser_tot_1_4.setLabel(count_matrixUsers[0][3].toString());

		programUser_tot_2_1.setLabel(count_matrixUsers[1][0].toString());
		programUser_tot_2_2.setLabel(count_matrixUsers[1][1].toString());
		programUser_tot_2_3.setLabel(count_matrixUsers[1][2].toString());
		programUser_tot_2_4.setLabel(count_matrixUsers[1][3].toString());

		programUser_tot_3_1.setLabel(count_matrixUsers[2][0].toString());
		programUser_tot_3_2.setLabel(count_matrixUsers[2][1].toString());
		programUser_tot_3_3.setLabel(count_matrixUsers[2][2].toString());
		programUser_tot_3_4.setLabel(count_matrixUsers[2][3].toString());

		programUser_tot_4_1.setLabel(count_matrixUsers[3][0].toString());
		programUser_tot_4_2.setLabel(count_matrixUsers[3][1].toString());
		programUser_tot_4_3.setLabel(count_matrixUsers[3][2].toString());
		programUser_tot_4_4.setLabel(count_matrixUsers[3][3].toString());

		programUser_tot_5_1.setLabel(count_matrixUsers[4][0].toString());
		programUser_tot_5_2.setLabel(count_matrixUsers[4][1].toString());
		programUser_tot_5_3.setLabel(count_matrixUsers[4][2].toString());
		programUser_tot_5_4.setLabel(count_matrixUsers[4][3].toString());

		final Double[] count_matrix_row = new Double[5];
		for (int i = 0; i < count_matrix_row.length; i++) {
			count_matrix_row[i] = 0.0;
		}

		// set sum row of matrix
		for (int i = 0; i < count_matrix.length; i++) {
			for (int j = 0; j < count_matrix[i].length; j++) {
				count_matrix_row[i] = count_matrix_row[i] + count_matrix[i][j];
			}
		}

		total_program_day_1.setLabel(Utility.decimatToTime(count_matrix_row[0]));
		total_program_day_2.setLabel(Utility.decimatToTime(count_matrix_row[1]));
		total_program_day_3.setLabel(Utility.decimatToTime(count_matrix_row[2]));
		total_program_day_4.setLabel(Utility.decimatToTime(count_matrix_row[3]));
		total_program_day_5.setLabel(Utility.decimatToTime(count_matrix_row[4]));

		totalUser_program_day_1.setLabel(count_Day_Users[0].toString());
		totalUser_program_day_2.setLabel(count_Day_Users[1].toString());
		totalUser_program_day_3.setLabel(count_Day_Users[2].toString());
		totalUser_program_day_4.setLabel(count_Day_Users[3].toString());
		totalUser_program_day_5.setLabel(count_Day_Users[4].toString());

		// get all user to schedule
		final List<Person> users_schedule = personDAO.listWorkerPersons(text_search_person, null);

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
			list_rows_program.add(addedRow);

		}

		// sort
		Collections.sort(list_rows_program);

		if ((shows_rows.getValue() != null) && (shows_rows.getValue() != 0)) {
			grid_scheduler.setPageSize(shows_rows.getValue());
		}
		final ListModelList<RowSchedule> model = new ListModelList<RowSchedule>(list_rows_program);
		model.setMultiple(true);
		grid_scheduler.setModel(model);

	}

	/**
	 * @param info_visibility
	 *            if true set info scheduler for programming visible
	 */
	private void setupGlobalSchedulerGridForShiftReview() {

		// user availability and color
		final HashMap<Integer, String> map_status = defineUserAvailability(firstDateInGrid);

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstDateInGrid);

		final Date ret = calendar.getTime();
		final Date date_schedule = DateUtils.truncate(ret, Calendar.DATE);

		// create a map for define people scheduled
		final HashMap<Integer, RowSchedule> sign_scheduled = new HashMap<Integer, RowSchedule>();

		// take info about person
		String text_search_person = null;
		if ((full_text_search.getValue() == null) || full_text_search.getValue().equals("")) {
			text_search_person = null;
		} else {
			text_search_person = full_text_search.getValue();
		}

		// get info on program
		final List<Schedule> list_revision = scheduleDAO.selectAggregateSchedulersRevision(date_schedule, text_search_person);

		final ArrayList<RowSchedule> list_row = new ArrayList<RowSchedule>();
		RowSchedule currentRow = null;

		// count review tot
		Double count_colum_1 = 0.0;
		Double count_colum_2 = 0.0;
		Double count_colum_3 = 0.0;
		Double count_colum_4 = 0.0;

		// count number of persons
		Integer countUsers_colum_1 = 0;
		Integer countUsers_colum_2 = 0;
		Integer countUsers_colum_3 = 0;
		Integer countUsers_colum_4 = 0;
		Integer countUsersTot = 0;

		for (int i = 0; i < list_revision.size(); i++) {

			final Schedule schedule = list_revision.get(i);

			// if the user is changed, add another row
			if ((currentRow == null) || (!currentRow.getUser().equals(schedule.getUser()))) {

				// set current row
				currentRow = new RowSchedule();
				currentRow.setUser(schedule.getUser());
				currentRow.setName_user(schedule.getName_user());
				list_row.add(currentRow);

				// set user type for available
				if (map_status.containsKey(schedule.getUser())) {
					final String status = map_status.get(schedule.getUser());
					currentRow.setUser_status(status);
				}

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), currentRow);
			}

			// set day 2
			final ItemRowSchedule itemsRow = getItemRowSchedule(currentRow, 2, schedule, false);

			currentRow.setItem_2(itemsRow);

		}

		// count review
		for (final RowSchedule itemrow_count : list_row) {

			final Person user = personDAO.loadPerson(itemrow_count.getUser());
			if (!user.isInOffice()) {

				if (itemrow_count.getItem_2().getAnchorValue1() != 0) {
					countUsers_colum_1++;
				}
				if (itemrow_count.getItem_2().getAnchorValue2() != 0) {
					countUsers_colum_2++;
				}
				if (itemrow_count.getItem_2().getAnchorValue3() != 0) {
					countUsers_colum_3++;
				}
				if (itemrow_count.getItem_2().getAnchorValue4() != 0) {
					countUsers_colum_4++;
				}

				if ((itemrow_count.getItem_2().getAnchorValue1() != 0) || (itemrow_count.getItem_2().getAnchorValue2() != 0)
						|| (itemrow_count.getItem_2().getAnchorValue3() != 0)
						|| (itemrow_count.getItem_2().getAnchorValue4() != 0)) {
					countUsersTot++;
				}

				count_colum_1 = count_colum_1 + itemrow_count.getItem_2().getAnchorValue1();
				count_colum_2 = count_colum_2 + itemrow_count.getItem_2().getAnchorValue2();
				count_colum_3 = count_colum_3 + itemrow_count.getItem_2().getAnchorValue3();
				count_colum_4 = count_colum_4 + itemrow_count.getItem_2().getAnchorValue4();
			}

		}

		// set info program
		review_tot_2_1.setLabel(Utility.decimatToTime(count_colum_1));
		review_tot_2_2.setLabel(Utility.decimatToTime(count_colum_2));
		review_tot_2_3.setLabel(Utility.decimatToTime(count_colum_3));
		review_tot_2_4.setLabel(Utility.decimatToTime(count_colum_4));

		final Double totalReviewDay1 = count_colum_1 + count_colum_2 + count_colum_3 + count_colum_4;
		total_review_day_2.setLabel(Utility.decimatToTime(count_colum_1 + count_colum_2 + count_colum_3 + count_colum_4));

		// set number of person in shift
		reviewUser_tot_2_1.setLabel(countUsers_colum_1.toString());
		reviewUser_tot_2_2.setLabel(countUsers_colum_2.toString());
		reviewUser_tot_2_3.setLabel(countUsers_colum_3.toString());
		reviewUser_tot_2_4.setLabel(countUsers_colum_4.toString());

		final Integer totalUserReviewDay2 = countUsersTot;
		totalUser_review_day_2.setLabel(countUsersTot.toString());

		// reset counter
		count_colum_1 = 0.0;
		count_colum_2 = 0.0;
		count_colum_3 = 0.0;
		count_colum_4 = 0.0;

		// reset counter person
		countUsers_colum_1 = 0;
		countUsers_colum_2 = 0;
		countUsers_colum_3 = 0;
		countUsers_colum_4 = 0;
		countUsersTot = 0;

		// get info on program
		final List<Schedule> list_program = scheduleDAO.selectAggregateSchedulersProgram(date_schedule, text_search_person);

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

				// set user type for available
				if (map_status.containsKey(schedule.getUser())) {
					final String status = map_status.get(schedule.getUser());
					myRow.setUser_status(status);
				}

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), myRow);
			}

			// set day 1
			final ItemRowSchedule itemsRow = getItemRowSchedule(myRow, 1, schedule, false);

			myRow.setItem_1(itemsRow);

		}

		// count review
		for (final RowSchedule itemrow_count : list_row) {

			final Person user = personDAO.loadPerson(itemrow_count.getUser());

			if (!user.isInOffice()) {
				if (itemrow_count.getItem_1().getAnchorValue1() != 0) {
					countUsers_colum_1++;
				}
				if (itemrow_count.getItem_1().getAnchorValue2() != 0) {
					countUsers_colum_2++;
				}
				if (itemrow_count.getItem_1().getAnchorValue3() != 0) {
					countUsers_colum_3++;
				}
				if (itemrow_count.getItem_1().getAnchorValue4() != 0) {
					countUsers_colum_4++;
				}

				if ((itemrow_count.getItem_1().getAnchorValue1() != 0) || (itemrow_count.getItem_1().getAnchorValue2() != 0)
						|| (itemrow_count.getItem_1().getAnchorValue3() != 0)
						|| (itemrow_count.getItem_1().getAnchorValue4() != 0)) {
					countUsersTot++;
				}

				count_colum_1 = count_colum_1 + itemrow_count.getItem_1().getAnchorValue1();
				count_colum_2 = count_colum_2 + itemrow_count.getItem_1().getAnchorValue2();
				count_colum_3 = count_colum_3 + itemrow_count.getItem_1().getAnchorValue3();
				count_colum_4 = count_colum_4 + itemrow_count.getItem_1().getAnchorValue4();

			}

		}

		// set info review
		review_tot_1_1.setLabel(Utility.decimatToTime(count_colum_1));
		review_tot_1_2.setLabel(Utility.decimatToTime(count_colum_2));
		review_tot_1_3.setLabel(Utility.decimatToTime(count_colum_3));
		review_tot_1_4.setLabel(Utility.decimatToTime(count_colum_4));

		final Double totalReviewDay2 = count_colum_1 + count_colum_2 + count_colum_3 + count_colum_4;
		total_review_day_1.setLabel(Utility.decimatToTime(count_colum_1 + count_colum_2 + count_colum_3 + count_colum_4));

		setCheckTotalHoursReview(totalReviewDay1, totalReviewDay2, total_review_day_1, total_review_day_2);

		// set number of person in shift
		reviewUser_tot_1_1.setLabel(countUsers_colum_1.toString());
		reviewUser_tot_1_2.setLabel(countUsers_colum_2.toString());
		reviewUser_tot_1_3.setLabel(countUsers_colum_3.toString());
		reviewUser_tot_1_4.setLabel(countUsers_colum_4.toString());

		final Integer totalUserReviewDay1 = countUsersTot;
		totalUser_review_day_1.setLabel(countUsersTot.toString());

		setCheckTotalUserReview(totalUserReviewDay1, totalUserReviewDay2, totalUser_review_day_1, totalUser_review_day_2);

		// get all user to schedule
		final List<Person> users_schedule = personDAO.listWorkerPersons(text_search_person, null);

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
			final ItemRowSchedule item_1 = new ItemRowSchedule(addedRow);
			final ItemRowSchedule item_2 = new ItemRowSchedule(addedRow);

			addedRow.setItem_1(item_1);
			addedRow.setItem_2(item_2);

			list_row.add(addedRow);

		}

		// sort
		Collections.sort(list_row);

		// set grid
		if ((shows_rows.getValue() != null) && (shows_rows.getValue() != 0)) {
			grid_scheduler_review.setPageSize(shows_rows.getValue());
		}
		final ListModelList<RowSchedule> model = new ListModelList<RowSchedule>(list_row);
		model.setMultiple(true);
		grid_scheduler_review.setModel(model);

	}

	// set visibility of download_program_report button
	private void setVisibilityDownloadReportButton() {
		final Person personLogged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!personLogged.isAdministrator()) {
			final Comboitem version_selected = SchedulerComposer.this.scheduler_type_selector.getSelectedItem();
			if (version_selected == SchedulerComposer.this.program_item) {
				if (lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE) == null) {
					download_program_report.setVisible(true);
				} else {
					download_program_report.setVisible(false);
				}
			}
		}

	}

	/**
	 * Define bheavior for day configuration
	 *
	 * @param data_info
	 */
	protected void showPreprocessingPopup(final String data_info) {

		day_after_config.setValue(null);

		// set info day;
		selectedDay = Integer.parseInt(data_info);
		if (selectedDay == null) {
			return;
		}

		final RowDaySchedule row_scheduler = grid_scheduler_day.getSelectedItem().getValue();
		selectedUser = row_scheduler.getUser();
		if (selectedUser == null) {
			return;
		}

		// get current day
		final Date current_day = getDateScheduled(selectedDay);

		if (!checkIfUnLockTable()) {
			SchedulerComposer.this.disableWriteCancelButtons(true);

			if (userLockTable != null) {
				loggerUserOnTable.setValue(messageTableLock + personLock.getFirstname() + " " + personLock.getLastname() + " - "
						+ messageTimeConnectionTableLock + Utility.convertToDateAndTime(userLockTable.getTime_start()));
				switchButton.setLabel(switchButtonValueClose);

			} else {
				loggerUserOnTable.setValue(messageTableUnLock);
				switchButton.setLabel(switchButtonValueOpen);
			}

		}

		if (!person_logged.isAdministrator()) {

			// set command enabling
			final Calendar today = Calendar.getInstance();
			final Calendar last_day_modify = DateUtils.truncate(today, Calendar.DATE);

			last_day_modify.add(Calendar.DAY_OF_YEAR, -3);

			// disable preprocessing
			if (current_day.before(last_day_modify.getTime())) {

				cancel_day_definition.setDisabled(true);
				ok_day_shift.setDisabled(true);

			}

		} else {
			cancel_day_definition.setDisabled(false);
			ok_day_shift.setDisabled(false);
		}

		// initialize message popup
		String msg = "" + SchedulerComposer.formatter_scheduler_info.format(current_day);

		// get user
		if (grid_scheduler_day.getSelectedItem() != null) {
			final RowDaySchedule row = grid_scheduler_day.getSelectedItem().getValue();
			final String name = row.getName_user();

			if (personDAO.loadPerson(selectedUser).getPart_time()) {
				msg = name + " " + partTimeMessage + ". " + msg;
			} else {
				msg = name + ". " + msg;
			}

		}

		// take the right scheduler
		SchedulerComposer.this.currentSchedule = scheduleDAO.loadSchedule(current_day, selectedUser);

		UserShift current_shift = null;
		if (currentSchedule != null) {
			final UserShift myshift = shift_cache.getUserShift(currentSchedule.getShift());
			if (myshift != null) {
				current_shift = myshift;
			}
		}

		// if current shift is null, show default work shift
		if (current_shift == null) {
			current_shift = shift_cache.getStandardWorkShift();
		}

		label_date_popup.setLabel(msg);
		if (current_shift != null) {
			label_date_shift_preprocessing.setLabel(current_shift.toString());
		} else {
			label_date_shift_preprocessing.setLabel(null);
		}

		shifts_combo_select.setSelectedItem(null);

		if (current_shift != null) {
			// set initial selected item
			for (final Comboitem item : shifts_combo_select.getItems()) {
				if (item.getValue() instanceof UserShift) {
					if (item.getValue().equals(current_shift)) {
						shifts_combo_select.setSelectedItem(item);
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
			note_preprocessing.setValue(null);
		}

		// show programmer and controller
		editor_label_daydefinition.setLabel("");
		controller_label_daydefinition.setLabel("");

		if (currentSchedule != null) {

			if (currentSchedule.getEditor() != null) {
				final Person editor = personDAO.loadPerson(currentSchedule.getEditor());
				if (editor != null) {
					editor_label_daydefinition.setLabel("Programmatore: " + editor.getFirstname() + " " + editor.getLastname());
				}
			}

			if (currentSchedule.getController() != null) {
				final Person controller = personDAO.loadPerson(currentSchedule.getController());
				if (controller != null) {
					controller_label_daydefinition.setLabel("Controllore: " + controller.getFirstname() + " "
							+ controller.getLastname());
				}
			}

		}

		day_definition_popup.open(grid_scheduler_day, "after_pointer");
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

		SchedulerComposer.this.label_statistic_popup.setLabel(title);

		final Person user = personDAO.loadPerson(id_user);

		final UserStatistics userStatistics = statProcedure.getUserStatistics(user);

		userRoles.setValue("");

		// set label user roles in statistic popup
		final String roles = user.getRolesDescription();
		if (roles != "") {
			userRoles.setValue(roles + ".");
		}

		// set department in statistic popup
		userDepartment.setValue("");
		final String department = user.getDepartment();
		if (department != null) {
			userDepartment.setValue(department);
		}

		final Double sat = userStatistics.getSaturation();
		saturation.setValue(sat.toString());
		// set saturation style
		if (sat < 0) {
			saturation.setStyle("color:red");
			saturation.setValue("REC " + Utility.roundTwo(Math.abs(sat)));

		} else {
			saturation.setStyle(saturationStyle);
			saturation.setValue("OT " + Utility.roundTwo(sat));
		}

		final Double sat_month = userStatistics.getSaturation_month();

		// set saturation month style
		if (sat_month == null) {
			saturation_month.setValue(" ");
		} else {

			if (sat_month < 0) {
				saturation_month.setStyle("color:red");
				saturation_month.setValue("REC " + Utility.roundTwo(Math.abs(sat_month)));
			} else {
				saturation_month.setStyle(saturation.getStyle());
				saturation_month.setValue("OT " + Utility.roundTwo(sat_month));
			}
		}

		work_sunday_perc.setValue(userStatistics.getWork_sunday_perc());
		work_holiday_perc.setValue(userStatistics.getWork_holiday_perc());
		work_current_week.setValue(userStatistics.getWork_current_week());
		work_current_month.setValue(userStatistics.getWork_current_month());
		work_current_year.setValue(userStatistics.getWork_current_year());
		working_series.setValue(userStatistics.getWorking_series());
		shift_perc_1.setValue(userStatistics.getShift_perc_1());
		shift_perc_2.setValue(userStatistics.getShift_perc_2());
		shift_perc_3.setValue(userStatistics.getShift_perc_3());
		shift_perc_4.setValue(userStatistics.getShift_perc_4());

		// show popup
		SchedulerComposer.this.day_name_popup.open(anchorComponent, "after_pointer");

	}

	/**
	 * Whow in popup list of tasks for a user
	 *
	 * @param id_user
	 * @param anchorComponent
	 * @param title
	 */
	private void showStatisticsTaskPopup(final Integer id_user, final Listbox anchorComponent, final String title) {

		SchedulerComposer.this.label_statistic_task_popup.setLabel(title);

		final List<UserTask> user_tasks = taskDAO.loadTasksByUser(id_user);
		list_task_stat.setModel(new ListModelList<UserTask>(user_tasks));

		SchedulerComposer.this.task_list_popup.open(anchorComponent, "after_pointer");

	}

	@Listen("onClick= #switchButton")
	public void switchButtonClick() {

		onChangeSelectedVersion();

		// set button switch
		checkIfTableIsLockedAndSetButton();

		Comboitem version_selected = SchedulerComposer.this.scheduler_type_selector.getSelectedItem();
		LockTable lockTable = null;
		if ((version_selected == SchedulerComposer.this.preprocessing_item)
				|| (version_selected == SchedulerComposer.this.program_item)) {
			lockTable = SchedulerComposer.this.lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE);
		} else if (version_selected == SchedulerComposer.this.review_item) {
			lockTable = SchedulerComposer.this.lockTableDAO.loadLockTableByTableType(TableTag.REVIEW_TABLE);
		}

		if (!person_logged.isAdministrator() && ((lockTable != null) && !lockTable.getId_user().equals(person_logged.getId()))) {
			// check and set if table is locked

			// check if you are admin or you are the user that have locked
			// the table
			if (!SchedulerComposer.this.person_logged.isAdministrator()) {
				SchedulerComposer.this.disableWriteCancelButtons(true);
			} else {
				SchedulerComposer.this.disableWriteCancelButtons(false);
			}
			final int idLogged = SchedulerComposer.this.person_logged.getId();
			if ((lockTable != null) && (lockTable.getId_user() == idLogged)) {
				SchedulerComposer.this.disableWriteCancelButtons(false);
			}

		} else {
			version_selected = SchedulerComposer.this.scheduler_type_selector.getSelectedItem();

			if (switchButton.getLabel().equals(switchButtonValueOpen)) {
				// lock table
				switchButton.setLabel(switchButtonValueClose);
				switchButton.setVisible(true);

				final LockTable myLockTable = new LockTable();
				myLockTable.setId_user(person_logged.getId());
				myLockTable.setTime_start(new Timestamp(Calendar.getInstance().getTime().getTime()));
				loggerUserOnTable.setValue(messageTableLock + person_logged.getFirstname() + " " + person_logged.getLastname()
						+ " - " + messageTimeConnectionTableLock + Utility.convertToDateAndTime(myLockTable.getTime_start()));
				if ((version_selected == SchedulerComposer.this.preprocessing_item)
						|| (version_selected == SchedulerComposer.this.program_item)) {
					myLockTable.setTable_type(TableTag.PROGRAM_TABLE);
				} else if (version_selected == SchedulerComposer.this.review_item) {
					myLockTable.setTable_type(TableTag.REVIEW_TABLE);
				}

				lockTableDAO.createLockTable(myLockTable);

				// disable all write and cancel buttons
				disableWriteCancelButtons(false);

			} else if (switchButton.getLabel().equals(switchButtonValueClose)) {
				switchButton.setVisible(true);
				loggerUserOnTable.setValue(messageTableUnLock);
				switchButton.setLabel(switchButtonValueOpen);
				// load locktable that is locked by you or unlock table because
				// you
				// are administrator
				lockTable = null;
				if ((version_selected == SchedulerComposer.this.preprocessing_item)
						|| (version_selected == SchedulerComposer.this.program_item)) {
					lockTable = lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE);
				} else if (version_selected == SchedulerComposer.this.review_item) {
					lockTable = lockTableDAO.loadLockTableByTableType(TableTag.REVIEW_TABLE);
				}

				if (lockTable != null) {
					lockTable.setTime_to(new Timestamp(Calendar.getInstance().getTime().getTime()));
					lockTable.setId_user_closer(person_logged.getId());
					lockTableDAO.updateLockTable(lockTable);
				}

				if (person_logged.isAdministrator()) {
					disableWriteCancelButtons(false);
				} else {
					disableWriteCancelButtons(true);
				}

			}
		}

	}
}
