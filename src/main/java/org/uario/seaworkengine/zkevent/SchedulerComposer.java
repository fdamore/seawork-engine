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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.docfactory.ProgramReportBuilder;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.LockTable;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserCompensation;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.IShipCache;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.platform.persistence.dao.LockTableDAO;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.platform.persistence.dao.UserCompensationDAO;
import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.statistics.IStatProcedure;
import org.uario.seaworkengine.statistics.UserStatistics;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.BoardTag;
import org.uario.seaworkengine.utility.ShiftTag;
import org.uario.seaworkengine.utility.TableTag;
import org.uario.seaworkengine.utility.TaskColor;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.utility.UtilityCSV;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.uario.seaworkengine.utility.ZkSessionTag;
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
import org.zkoss.zul.Doublebox;
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
import org.zkoss.zul.Toolbarbutton;

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

		private BreakInWeekManagement(final UserShift shift, final List<Schedule> scheduleListInWeek, final RowDaySchedule row_item,
				final Date date_scheduled) {
			this.shift = shift;
			this.scheduleListInWeek = scheduleListInWeek;
			this.row_item = row_item;
			this.date_scheduled = date_scheduled;
		}

		@Override
		public void onEvent(final ClickEvent e) {

			if (Messagebox.ON_OK.equals(e.getName())) {

				SchedulerComposer.this.saveDayShiftProcedure(this.shift, this.row_item, this.date_scheduled, this.scheduleListInWeek);
			} else if (Messagebox.ON_NO.equals(e.getName())) {

				SchedulerComposer.this.saveDayShiftProcedure(this.shift, this.row_item, this.date_scheduled, null);
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

				final Integer max_shift = SchedulerComposer.this.statProcedure
						.getMaximumShift(SchedulerComposer.this.currentSchedule.getDate_schedule(), SchedulerComposer.this.currentSchedule.getUser());

				// save program... finalize
				SchedulerComposer.this.saveProgramFinalStep();

				// check if reprogramming
				if (SchedulerComposer.this.selectedShift.compareTo(max_shift) > 0) {

					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");

					final Messagebox.Button[] buttons = new Messagebox.Button[2];
					buttons[0] = Messagebox.Button.OK;
					buttons[1] = Messagebox.Button.CANCEL;

					final String msg = "Hai violato il vincolo delle due ore di stacco nella programmazione del giorno successivo. Vuoi che il sistema riprogrammi automaticamente i turni del giorno successivo?";
					Messagebox.show(msg, "RIPROGRAMMAZIONE", buttons, null, Messagebox.EXCLAMATION, null, new ReAssignShiftAndTaskEvent(), params);

				}

				// close popup
				SchedulerComposer.this.shift_definition_popup.close();

			} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
				// close popup
				SchedulerComposer.this.shift_definition_popup.close();
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

			if (this.date_scheduled == null) {
				return;
			}

			if (Messagebox.ON_OK.equals(e.getName())) {

				final Calendar cal_sunday = DateUtils.toCalendar(this.date_scheduled);

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
					max_random_day = (max_random_day - day_of_week) + 1;
				}

				// only if you not assign waited work
				Date date_break = SchedulerComposer.this.statProcedure.getARandomDay(date_start, max_random_day);
				// check in naive way if date_break fall into date_schedule (NOT
				// TO BE)
				int check_exit = 1000;

				do {
					if (DateUtils.isSameDay(date_break, this.date_scheduled)) {

						date_break = SchedulerComposer.this.statProcedure.getARandomDay(date_start, max_random_day);
						check_exit--;
					} else {
						break;
					}

				} while (check_exit > 0);

				final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

				SchedulerComposer.this.statProcedure.workAssignProcedure(SchedulerComposer.this.shift_cache.getBreakShift(), date_break,
						this.row_item.getUser(), person_logged.getId());

				SchedulerComposer.this.setupGlobalSchedulerGridForDay();

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

				if (SchedulerComposer.this.currentSchedule == null) {
					return;
				}

				final Calendar next_cal = DateUtils.toCalendar(SchedulerComposer.this.currentSchedule.getDate_schedule());
				next_cal.add(Calendar.DAY_OF_YEAR, 1);

				final Schedule next_schedule = SchedulerComposer.this.scheduleDAO.loadSchedule(next_cal.getTime(),
						SchedulerComposer.this.currentSchedule.getUser());

				SchedulerComposer.this.statProcedure.reAssignShift(next_schedule, person_logged.getId());

				// refresh grid
				SchedulerComposer.this.setupGlobalSchedulerGridForShift();

			}

		}
	}

	private static final String				ALL_ITEM						= "ANNULLA FILTRO";

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

	private final static String				STATUS_COMP_EDITOR_ADD			= "STATUS_COMP_EDITOR_ADD";

	private final static String				STATUS_COMP_EDITOR_EDIT			= "STATUS_COMP_EDITOR_EDIT";

	private static Map<Integer, Double> sortByComparator(final Map<Integer, Double> unsortMap, final boolean order) {

		final List<Entry<Integer, Double>> list = new LinkedList<>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<Integer, Double>>() {
			@Override
			public int compare(final Entry<Integer, Double> o1, final Entry<Integer, Double> o2) {
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		final Map<Integer, Double> sortedMap = new LinkedHashMap<>();
		for (final Entry<Integer, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	@Wire
	private Button						add_program_item;

	@Wire
	private Button						add_review_item;

	@Wire
	private Label						alertMinHours;

	@Wire
	private Label						alertMinHoursReview;

	@Wire
	private Button						assign_program_review;

	private IBankHolidays				bank_holiday;

	@Wire
	private Combobox					board;

	// cache sat
	private HashMap<Integer, Double>	cache_sat						= null;

	@Wire
	private Button						cancel_day_definition;

	@Wire
	private Button						cancel_modify;

	@Wire
	private Button						cancel_modify_program;

	@Wire
	private Button						cancel_program;

	@Wire
	private Button						cancel_review;

	@Wire
	private Component					color_legend_review;

	@Wire
	private Component					color_program_legend;

	@Wire
	private Combobox					combo_user_dip;

	@Wire
	private Div							comp_editor;

	@Wire
	private A							compe_popup_year;

	@Wire
	private Popup						compensation_popup;

	private ConfigurationDAO			configurationDAO;

	@Wire
	private Checkbox					continue_shift;

	@Wire
	private Checkbox					continue_shiftProgram;

	@Wire
	private A							controller_label;

	@Wire
	private A							controller_label_daydefinition;

	@Wire
	private A							controller_label_review;

	@Wire
	private Textbox						crane;

	@Wire
	private Textbox						craneSelector;

	private Schedule					currentSchedule;

	@Wire
	private Datebox						date_from_overview;

	@Wire
	private Datebox						date_init_scheduler;

	@Wire
	private Datebox						date_init_scheduler_review;

	@Wire
	private Datebox						date_submitUser;

	@Wire
	private Datebox						date_to_overview;

	@Wire
	private Datebox						day_after_config;

	@Wire
	private Popup						day_definition_popup;

	@Wire
	private Popup						day_name_popup;

	@Wire
	private Component					day_shift_over;

	@Wire
	private Checkbox					day_shift_over_control;

	@Wire
	private Checkbox					day_shift_over_control_init;

	@Wire
	private Checkbox					day_shift_over_control_program;

	@Wire
	private Checkbox					day_shift_over_control_program_init;

	@Wire
	private Component					day_shift_over_program;

	@Wire
	private Combobox					dayWorking_filter;

	@Wire
	private Component					define_program_body;

	@Wire
	private Component					div_force_shift;

	@Wire
	private Component					download_program_report;

	@Wire
	private A							editor_label;

	@Wire
	private A							editor_label_daydefinition;

	@Wire
	private A							editor_label_review;

	@Wire
	private Label						errorMessageAddItem;

	@Wire
	private Label						errorMessageAddProgramItem;

	/**
	 * First date in grid
	 */
	private Date						firstDateInGrid;

	@Wire
	private Combobox					force_shift_combo;

	@Wire
	private Textbox						full_text_search;

	@Wire
	private Listbox						grid_scheduler;

	@Wire
	private Listbox						grid_scheduler_day;

	@Wire
	private Listbox						grid_scheduler_review;

	@Wire
	private Component					header_info;

	@Wire
	public Combobox						hoursInterval;

	@Wire
	private Label						infoBadge;

	@Wire
	private Comboitem					item_all_shift_overview;

	@Wire
	private A							label_date_popup;

	@Wire
	private A							label_date_shift_preprocessing;

	@Wire
	private A							label_date_shift_program;

	@Wire
	private A							label_date_shift_review;

	@Wire
	private A							label_statistic_popup;
	@Wire
	private A							label_statistic_task_popup;

	@Wire
	private Component					last_programmer_tag;

	@Wire
	private Label						lastProgrammer;

	// initial program and revision - USED IN POPUP
	private List<DetailInitialSchedule>	list_details_program;

	private List<DetailFinalSchedule>	list_details_review;

	@Wire
	private Listbox						list_overview_preprocessing;

	@Wire
	private Listbox						list_overview_program;

	@Wire
	private Listbox						list_overview_review;

	private ArrayList<RowDaySchedule>	list_rowDaySchedule				= new ArrayList<>();

	private ArrayList<RowSchedule>		list_rows_program;

	private ArrayList<RowSchedule>		list_rowSchedule;

	@Wire
	private Listbox						list_statistics;

	@Wire
	private Listbox						list_task_stat;

	@Wire
	private Listbox						listbox_program;

	@Wire
	private Listbox						listbox_review;

	// initial program and revision - USED DOWNLOAD
	private List<DetailInitialSchedule>	listDetailProgram				= null;

	// review program and revision - USED DOWNLOAD
	private List<DetailFinalSchedule>	listDetailRevision				= null;

	// review program and revision - USED DOWNLOAD
	private List<Schedule>				listSchedule					= null;

	// statistics - USED DOWNLOAD
	private List<UserStatistics>		listUserStatistics				= null;

	private LockTableDAO				lockTableDAO;

	@Wire
	private Label						loggerUserOnTable;

	private final String				messageTableLock				= "Utente connesso: ";

	private final String				messageTableUnLock				= "Nessun utente connesso.";

	private final String				messageTimeConnectionTableLock	= "Inizio connessione: ";

	private boolean						minHoursAlert					= false;

	@Wire
	private Textbox						note_compUser;

	@Wire
	private Textbox						note_preprocessing;

	@Wire
	private Textbox						note_program;

	@Wire
	private Textbox						note_review;

	@Wire
	private Button						ok_day_shift;

	@Wire
	private Button						ok_program;

	@Wire
	private Button						ok_review;

	@Wire
	private Label						overview_count_days;

	@Wire
	private Label						overview_count_h;

	@Wire
	private Label						overview_count_h_c;

	@Wire
	private Label						overview_count_h_stat;

	@Wire
	private Label						overview_count_worker;

	@Wire
	private Label						overview_count_worker_stat;

	@Wire
	private Div							overview_div;

	@Wire
	private Component					overview_download;
	@Wire
	private Comboitem					overview_item;

	@Wire
	private Tabpanel					overview_preprocessing;
	@Wire
	private Tabpanel					overview_program;
	@Wire
	private Tabpanel					overview_review;
	@Wire
	private Tabpanel					overview_statistics;

	@Wire
	private Label						overview_sum_saturation;

	@Wire
	private Tabbox						overview_tab;

	@Wire
	private Panel						panel_shift_period;

	private final String				partTimeMessage					= "Part Time";

	private Person						person_logged					= null;
	private PersonDAO					personDAO;
	private Person						personLock;
	@Wire
	private Div							preprocessing_div;

	@Wire
	private Comboitem					preprocessing_item;
	@Wire
	private Panel						preprocessing_panel;
	@Wire
	private Component					print_program_videos;
	@Wire
	private Component					print_scheduler;
	@Wire
	private Div							program_div;

	@Wire
	private Component					program_head_1_1;

	@Wire
	private Component					program_head_1_2;

	@Wire
	private Component					program_head_1_3;

	@Wire
	private Component					program_head_1_4;

	@Wire
	private Component					program_head_4_1;

	@Wire
	private Component					program_head_4_2;

	@Wire
	private Component					program_head_4_3;

	@Wire
	private Component					program_head_4_4;

	@Wire
	private Component					program_head_5_1;

	@Wire
	private Component					program_head_5_2;

	@Wire
	private Component					program_head_5_3;

	@Wire
	private Component					program_head_5_4;

	@Wire
	private Comboitem					program_item;

	@Wire
	private Panel						program_panel;
	@Wire
	private Listheader					program_panel_name;
	@Wire
	private Combobox					program_task;
	@Wire
	private Auxheader					program_tot_1_1;

	@Wire
	private Auxheader					program_tot_1_2;

	@Wire
	private Auxheader					program_tot_1_3;

	@Wire
	private Auxheader					program_tot_1_4;

	@Wire
	private Auxheader					program_tot_2_1;
	@Wire
	private Auxheader					program_tot_2_2;
	@Wire
	private Auxheader					program_tot_2_3;
	@Wire
	private Auxheader					program_tot_2_4;

	@Wire
	private Auxheader					program_tot_3_1;
	@Wire
	private Auxheader					program_tot_3_2;
	@Wire
	private Auxheader					program_tot_3_3;
	@Wire
	private Auxheader					program_tot_3_4;
	@Wire
	private Auxheader					program_tot_4_1;
	@Wire
	private Auxheader					program_tot_4_2;
	@Wire
	private Auxheader					program_tot_4_3;
	@Wire
	private Auxheader					program_tot_4_4;
	@Wire
	private Auxheader					program_tot_5_1;
	@Wire
	private Auxheader					program_tot_5_2;
	@Wire
	private Auxheader					program_tot_5_3;
	@Wire
	private Auxheader					program_tot_5_4;

	@Wire
	private Auxheader					programUser_tot_1_1;

	@Wire
	private Auxheader					programUser_tot_1_2;

	@Wire
	private Auxheader					programUser_tot_1_3;

	@Wire
	private Auxheader					programUser_tot_1_4;

	@Wire
	private Auxheader					programUser_tot_2_1;

	@Wire
	private Auxheader					programUser_tot_2_2;

	@Wire
	private Auxheader					programUser_tot_2_3;

	@Wire
	private Auxheader					programUser_tot_2_4;

	@Wire
	private Auxheader					programUser_tot_3_1;

	@Wire
	private Auxheader					programUser_tot_3_2;

	@Wire
	private Auxheader					programUser_tot_3_3;

	@Wire
	private Auxheader					programUser_tot_3_4;

	@Wire
	private Auxheader					programUser_tot_4_1;

	@Wire
	private Auxheader					programUser_tot_4_2;

	@Wire
	private Auxheader					programUser_tot_4_3;

	@Wire
	private Auxheader					programUser_tot_4_4;

	@Wire
	private Auxheader					programUser_tot_5_1;

	@Wire
	private Auxheader					programUser_tot_5_2;

	@Wire
	private Auxheader					programUser_tot_5_3;

	@Wire
	private Auxheader					programUser_tot_5_4;

	@Wire
	private Button						remove_program_item;

	@Wire
	private Button						remove_review_item;

	@Wire
	private Button						repogram_users;

	@Wire
	private Div							review_div;

	@Wire
	private Comboitem					review_item;

	@Wire
	private Panel						review_panel;

	@Wire
	private Listheader					review_panel_name;

	@Wire
	private Combobox					review_task;

	@Wire
	private Auxheader					review_tot_1_1;

	@Wire
	private Auxheader					review_tot_1_2;

	@Wire
	private Auxheader					review_tot_1_3;

	@Wire
	private Auxheader					review_tot_1_4;

	@Wire
	private Auxheader					review_tot_2_1;

	@Wire
	private Auxheader					review_tot_2_2;

	@Wire
	private Auxheader					review_tot_2_3;

	@Wire
	private Auxheader					review_tot_2_4;

	@Wire
	private Component					reviewSearchBox;

	@Wire
	private Checkbox					reviewShift;

	@Wire
	private Component					reviewShiftBox;

	@Wire
	private Tab							reviewTab;

	@Wire
	private Auxheader					reviewUser_tot_1_1;

	@Wire
	private Auxheader					reviewUser_tot_1_2;
	@Wire
	private Auxheader					reviewUser_tot_1_3;

	@Wire
	private Auxheader					reviewUser_tot_1_4;
	@Wire
	private Auxheader					reviewUser_tot_2_1;
	@Wire
	private Auxheader					reviewUser_tot_2_2;
	@Wire
	private Auxheader					reviewUser_tot_2_3;

	@Wire
	private Auxheader					reviewUser_tot_2_4;

	@Wire
	private Label						saturation;

	@Wire
	private Label						saturation_current_month;

	@Wire
	private Label						saturation_prec_month;

	@Wire
	private Button						save_note_preprocessing;

	@Wire
	private Button						save_note_program;

	@Wire
	private Button						save_note_review;

	@Wire
	private Button						save_program_item;

	@Wire
	private Button						save_review_item;

	private ISchedule					scheduleDAO;

	@Wire
	private A							scheduler_label;

	@Wire
	private A							scheduler_label_review;

	@Wire
	private Combobox					scheduler_type_selector;

	private IScheduleShip				scheduleShipDAO;

	@Wire
	public Combobox						select_month;

	@Wire
	private Combobox					select_shift_overview;

	@Wire
	private Combobox					select_shifttype_overview;

	@Wire
	public Combobox						select_week;

	@Wire
	public Combobox						select_year;

	// selected day
	private Integer						selectedDay;

	private DetailFinalSchedule			selectedItemReview;

	// selected shift
	private Integer						selectedShift;

	/**
	 * User selected to schedule
	 */
	private Integer						selectedUser;

	@Wire
	private Component					set_panel_shift_period;

	private IShiftCache					shift_cache;

	@Wire
	private Popup						shift_definition_popup;

	@Wire
	private Popup						shift_definition_popup_review;

	@Wire
	private Label						shift_description;

	@Wire
	private Label						shift_id;

	@Wire
	private Label						shift_perc_1;

	@Wire
	private Label						shift_perc_2;

	@Wire
	private Label						shift_perc_3;

	@Wire
	private Label						shift_perc_4;

	@Wire
	private Combobox					shift_period_combo;

	@Wire
	private Datebox						shift_period_from;

	@Wire
	private Label						shift_period_name;

	@Wire
	private Button						shift_period_ok;

	@Wire
	private Datebox						shift_period_to;

	@Wire
	private Popup						shift_popup;

	@Wire
	private Combobox					shifts_combo_select;

	@Wire
	private Combobox					ship;

	private IShipCache					ship_cache;

	private IShip						shipDAO;

	@Wire
	public Combobox						shipInDay;

	private IScheduleShip				shipSchedulerDao;

	private Ship						shipSelected;

	@Wire
	private Combobox					shipSelector;

	@Wire
	private Intbox						shows_rows;

	@Wire
	private Div							stat_update_command;

	private IStatistics					statisticDAO;

	@Wire
	private Tab							statisticsTab;

	private IStatProcedure				statProcedure;

	private String						status_comp_editor				= SchedulerComposer.STATUS_COMP_EDITOR_ADD;

	@Wire
	private Listbox						sw_compensation_list;

	@Wire
	private Toolbarbutton				sw_link_edit_program;

	@Wire
	private Toolbarbutton				sw_link_edit_review;

	@Wire
	private Button						switchButton;

	private final String				switchButtonValueClose			= "Chiudi";

	private final String				switchButtonValueOpen			= "Apri";

	@Wire
	private Checkbox					sync_schedule;

	protected ITaskCache				task_cache;

	@Wire
	private Label						task_cont;

	@Wire
	private Label						task_description;

	@Wire
	private Label						task_id;

	@Wire
	private Popup						task_list_popup;

	@Wire
	private Popup						task_popup;

	@Wire
	public Combobox						taskComboBox;

	private TasksDAO					taskDAO;

	@Wire
	private Doublebox					time_compUser;

	@Wire
	private Timebox						time_from;

	@Wire
	private Timebox						time_from_program;

	@Wire
	private Timebox						time_to;

	@Wire
	private Timebox						time_to_program;

	@Wire
	private Auxheader					total_program_day_1;

	@Wire
	private Auxheader					total_program_day_2;

	@Wire
	private Auxheader					total_program_day_3;

	@Wire
	private Auxheader					total_program_day_4;

	@Wire
	private Auxheader					total_program_day_5;

	@Wire
	private Auxheader					total_review_day_1;

	@Wire
	private Auxheader					total_review_day_2;

	@Wire
	public Label						totalHours_Program;

	@Wire
	public Label						totalHours_Review;

	@Wire
	private Auxheader					totalUser_program_day_1;

	@Wire
	private Auxheader					totalUser_program_day_2;

	@Wire
	private Auxheader					totalUser_program_day_3;

	@Wire
	private Auxheader					totalUser_program_day_4;

	@Wire
	private Auxheader					totalUser_program_day_5;

	@Wire
	private Auxheader					totalUser_review_day_1;

	@Wire
	private Auxheader					totalUser_review_day_2;

	@Wire
	private Label						updateStatisticTime;

	/**
	 * Userstatistic selected
	 */
	private UserStatistics				user_statistic_selected;

	private UserCompensationDAO			userCompensationDAO;

	@Wire
	private Label						userDepartment;

	private LockTable					userLockTable;

	@Wire
	private Label						userRoles;

	@Wire
	private Label						work_current_month;

	@Wire
	private Label						work_current_week;

	@Wire
	private Label						work_current_year;

	@Wire
	private Label						work_holiday_perc;

	@Wire
	private Label						work_sunday_perc;

	@Wire
	private Label						working_series;

	@Listen("onClick = #sw_link_add_comp")
	public void addCompenstion() {

		this.date_submitUser.setValue(Calendar.getInstance().getTime());
		this.time_compUser.setValue(null);
		this.note_compUser.setValue(null);

		this.status_comp_editor = SchedulerComposer.STATUS_COMP_EDITOR_ADD;

		this.comp_editor.setVisible(true);
	}

	@Listen("onClick= #add_program_item")
	public Boolean addProgramItem() {

		if (!this.checkConnection()) {
			return false;
		}

		if (this.list_details_program == null) {
			return false;
		}

		if (this.selectedShift == null) {
			return false;
		}

		if (this.program_task.getSelectedItem() == null) {
			// Messagebox.show("Assegnare una mansione all'utente selezionato,
			// prima di procedere alla programmazione",
			// "INFO", Messagebox.OK,Messagebox.EXCLAMATION);
			return false;
		}

		final UserTask task = this.program_task.getSelectedItem().getValue();
		if (task == null) {
			// Messagebox.show("Assegna una mansione", "INFO", Messagebox.OK,
			// Messagebox.EXCLAMATION);
			return false;
		}

		double countHours = 0;

		if ((this.time_from_program.getValue() != null) && (this.time_to_program.getValue() != null)) {

			final Double time = this.getProgrammedTime();

			if (time == null) {
				// Messagebox.show("Definire il numero di ore da lavorare",
				// "INFO",
				// Messagebox.OK, Messagebox.EXCLAMATION);
				return false;
			}

			// check about sum of time
			/*
			 * boolean check_sum = true; if (time > 6) { check_sum = false; } if
			 * (this.list_details_program.size() != 0) { int sum = time; for
			 * (final DetailInitialSchedule detail : this.list_details_program)
			 * { final int current_time = detail.getTime(); sum = sum +
			 * current_time; if (sum > 6) { check_sum = false; break; } } } if
			 * (!check_sum) { // Messagebox.show(
			 * "Non si possono assegnare più di sei ore per turno" , // "INFO",
			 * Messagebox.OK, Messagebox.EXCLAMATION); return; }
			 */

			if (this.currentSchedule == null) {
				// save scheduler
				this.saveCurrentSchedulerProgram();
			}

			final DetailInitialSchedule new_item = new DetailInitialSchedule();
			new_item.setId_schedule(this.currentSchedule.getId());
			new_item.setShift(this.selectedShift);
			new_item.setContinueshift(this.continue_shiftProgram.isChecked());
			new_item.setTask(task.getId());

			final UserTask t = this.configurationDAO.loadTask(task.getId());

			// check if is absence task
			if ((t != null) && t.getIsabsence()) {
				new_item.setTime(0.0);
				new_item.setTime_vacation(time);
			} else {
				new_item.setTime(time);
				new_item.setTime_vacation(0.0);
			}

			countHours = time;

			final java.util.Date now_from = this.time_from_program.getValue();
			if (now_from != null) {

				final Calendar current_calendar = DateUtils.toCalendar(this.currentSchedule.getDate_schedule());
				final Calendar from_calendar = DateUtils.toCalendar(now_from);

				if ((this.selectedShift == 4) && (this.day_shift_over_control_program_init.isChecked())) {
					current_calendar.add(Calendar.DAY_OF_YEAR, 1);
				}

				current_calendar.set(Calendar.HOUR_OF_DAY, from_calendar.get(Calendar.HOUR_OF_DAY));
				current_calendar.set(Calendar.MINUTE, from_calendar.get(Calendar.MINUTE));
				current_calendar.set(Calendar.SECOND, from_calendar.get(Calendar.SECOND));

				final java.sql.Timestamp t_from = new java.sql.Timestamp(current_calendar.getTimeInMillis());
				new_item.setTime_from(t_from);
			}

			final java.util.Date now_to = this.time_to_program.getValue();
			if (now_to != null) {
				final Calendar current_calendar = DateUtils.toCalendar(this.currentSchedule.getDate_schedule());
				final Calendar to_calendar = DateUtils.toCalendar(now_to);

				if ((this.selectedShift == 4) && (this.day_shift_over_control_program.isChecked())) {
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
			final List<DetailInitialSchedule> list = (List<DetailInitialSchedule>) this.listbox_program.getModel();
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

					if ((Utility.roundTwo(countHours) > 6.00)) {
						this.errorMessageAddProgramItem.setValue("Attenzione, totale ore maggiore di 6.");
						return false;
					}

					if (((new_item.getTime_from().compareTo(item.getTime_from()) >= 0) && (new_item.getTime_from().compareTo(item.getTime_to()) < 0))
							|| ((new_item.getTime_to().compareTo(item.getTime_from()) > 0)
									&& (new_item.getTime_to().compareTo(item.getTime_to()) <= 0))
							|| ((new_item.getTime_from().compareTo(item.getTime_from()) <= 0)
									&& (new_item.getTime_to().compareTo(item.getTime_to()) >= 0))) {
						this.errorMessageAddProgramItem.setValue("Attenzione, orario in sovrapposizione.");
						return false;
					}

				}
			}

			if ((Utility.roundTwo(countHours) > 6.00)) {
				this.errorMessageAddProgramItem.setValue("Attenzione, totale ore maggiore di 6.");
				return false;
			}

			// update program list
			this.list_details_program.add(new_item);
			final ListModelList<DetailInitialSchedule> model = new ListModelList<>(this.list_details_program);
			model.setMultiple(true);
			this.orderListDetailInitialScheduleByTimeFrom(model);
			this.listbox_program.setModel(model);

			this.setLabelTotalHoursProgram(model);
		}

		if ((countHours >= 6)) {
			this.minHoursAlert = false;
			this.alertMinHours.setVisible(false);
		}

		this.errorMessageAddProgramItem.setValue("");

		return true;

	}

	@Listen("onClick= #add_review_item")
	public Boolean addReviewItem() {

		this.errorMessageAddItem.setValue("");
		this.alertMinHoursReview.setVisible(false);

		if (!this.checkConnection()) {
			return false;
		}

		if (this.list_details_review == null) {
			return false;
		}

		if (this.selectedShift == null) {
			return false;
		}

		if (this.review_task.getSelectedItem() == null) {
			return false;
		}

		final UserTask task = this.review_task.getSelectedItem().getValue();
		if (task == null) {
			return false;
		}

		final Double time = this.getRevisionTime();

		if (time == null) {
			this.errorMessageAddItem.setValue("Attenzione, verificare intervallo orario.");
			this.alertMinHoursReview.setVisible(false);
			return false;
		}

		double countHours = 0;

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentSchedulerReview();
		}

		final DetailFinalSchedule new_item = new DetailFinalSchedule();
		new_item.setId_schedule(this.currentSchedule.getId());
		new_item.setShift(this.selectedShift);

		// set on board - under board
		if (this.board.getSelectedItem() != null) {
			if (this.board.getSelectedItem().getValue().equals(BoardTag.ON_BOARD)) {
				new_item.setBoard(BoardTag.ON_BOARD);
			} else if (this.board.getSelectedItem().getValue().equals(BoardTag.UNDER_BOARD)) {
				new_item.setBoard(BoardTag.UNDER_BOARD);
			}
		}

		final UserTask t = this.configurationDAO.loadTask(task.getId());

		// check if is absence task
		if ((t != null) && t.getIsabsence()) {
			new_item.setTime(0.0);
			new_item.setTime_vacation(time);
		} else {
			new_item.setTime(time);
			new_item.setTime_vacation(0.0);
		}

		// check if review shift
		new_item.setReviewshift(false);
		if (this.reviewShift.isChecked()) {
			new_item.setReviewshift(true);
		}

		new_item.setContinueshift(this.continue_shift.isChecked());

		countHours = time;

		new_item.setTask(task.getId());

		final java.util.Date now_from = this.time_from.getValue();
		if (now_from != null) {

			final Calendar current_calendar = DateUtils.toCalendar(this.currentSchedule.getDate_schedule());
			final Calendar from_calendar = DateUtils.toCalendar(now_from);

			if ((this.selectedShift == 4) && this.day_shift_over_control_init.isChecked()) {
				current_calendar.add(Calendar.DAY_OF_YEAR, 1);
			}

			current_calendar.set(Calendar.HOUR_OF_DAY, from_calendar.get(Calendar.HOUR_OF_DAY));
			current_calendar.set(Calendar.MINUTE, from_calendar.get(Calendar.MINUTE));
			current_calendar.set(Calendar.SECOND, from_calendar.get(Calendar.SECOND));

			final java.sql.Timestamp t_from = new java.sql.Timestamp(current_calendar.getTimeInMillis());
			new_item.setTime_from(t_from);
		}

		final java.util.Date now_to = this.time_to.getValue();
		if (now_to != null) {
			final Calendar current_calendar = DateUtils.toCalendar(this.currentSchedule.getDate_schedule());
			final Calendar to_calendar = DateUtils.toCalendar(now_to);

			if ((this.selectedShift == 4) && this.day_shift_over_control.isChecked()) {
				current_calendar.add(Calendar.DAY_OF_YEAR, 1);
			}

			current_calendar.set(Calendar.HOUR_OF_DAY, to_calendar.get(Calendar.HOUR_OF_DAY));
			current_calendar.set(Calendar.MINUTE, to_calendar.get(Calendar.MINUTE));
			current_calendar.set(Calendar.SECOND, to_calendar.get(Calendar.SECOND));

			final java.sql.Timestamp t_to = new java.sql.Timestamp(current_calendar.getTimeInMillis());
			new_item.setTime_to(t_to);
		}

		// set ship selected in new item
		if ((this.shipSelected != null) && (this.shipSelected.getId() != -1)) {

			new_item.setId_ship(this.shipSelected.getId());
		}

		final String craneName = this.crane.getValue();
		if ((craneName != null) && (craneName.trim() != "")) {
			new_item.setCrane(craneName);
		}

		// check if hours interval is already present in list and if count
		// hours is more than 12
		final List<DetailFinalSchedule> list = (List<DetailFinalSchedule>) this.listbox_review.getModel();
		if (list != null) {

			for (final DetailFinalSchedule item : list) {

				countHours = countHours + item.getTime() + item.getTime_vacation();

				if ((Utility.roundTwo(countHours) > 6.00)) {
					this.errorMessageAddItem.setValue("Attenzione, totale ore maggiore di 6.");
					this.alertMinHoursReview.setVisible(false);
					return false;
				}

				if (((((new_item.getTime_from().compareTo(item.getTime_from()) >= 0) && (new_item.getTime_from().compareTo(item.getTime_to()) < 0))
						|| ((new_item.getTime_to().compareTo(item.getTime_from()) > 0) && (new_item.getTime_to().compareTo(item.getTime_to()) <= 0))))
						|| ((new_item.getTime_from().compareTo(item.getTime_from()) <= 0)
								&& (new_item.getTime_to().compareTo(item.getTime_to()) >= 0))) {
					this.errorMessageAddItem.setValue("Attenzione, orario in sovrapposizione.");
					this.alertMinHoursReview.setVisible(false);
					return false;
				}
			}
		}

		if ((Utility.roundTwo(countHours) > 6.00)) {
			this.errorMessageAddItem.setValue("Attenzione, totale ore maggiore di 6.");
			this.alertMinHoursReview.setVisible(false);
			return false;
		}

		// update program list
		this.list_details_review.add(new_item);

		final ListModelList<DetailFinalSchedule> model = new ListModelList<>(this.list_details_review);
		model.setMultiple(true);
		this.orderListDetailFinalScheduleByTimeFrom(model);
		this.listbox_review.setModel(model);

		this.setLabelTotalHoursReview(model);
		this.ship.setSelectedItem(null);
		this.shipInDay.setSelectedItem(null);
		this.shipSelected = null;
		this.crane.setValue("");

		// check if time programmed if <= 6 hours

		if ((countHours >= 6)) {
			this.minHoursAlert = false;
			this.alertMinHoursReview.setVisible(false);
		}

		this.errorMessageAddItem.setValue("");
		this.alertMinHoursReview.setVisible(false);

		this.ship.setSelectedItem(null);
		this.shipInDay.setSelectedItem(null);
		this.crane.setValue("");
		this.board.setSelectedItem(null);
		this.continue_shift.setChecked(false);
		this.reviewShift.setChecked(false);

		return true;

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
		this.statProcedure.workAssignProcedure(shift, current_date_scheduled, user, editor.getId());
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
	private void assignShiftFromDaySchedule(final RowDaySchedule row_item, final UserShift shift, final int offset, final boolean override_break) {

		final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (person_logged == null) {
			return;
		}

		// getting information
		final Date current_date_scheduled = this.getDateScheduled(this.selectedDay + offset);
		final Integer user = row_item.getUser();

		Schedule schedule = row_item.getSchedule(this.selectedDay + offset);

		// check if is possible to override break
		if (!override_break && (schedule != null)) {
			if (schedule.getShift() != null) {
				final UserShift shift_target = this.shift_cache.getUserShift(schedule.getShift());
				final UserShift shift_rp_break = this.shift_cache.getBreakShift();
				final UserShift shift_rp_waited = this.shift_cache.getWaitedBreakShift();
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

		this.assignShiftForDaySchedule(shift, current_date_scheduled, user, schedule, person_logged, this.note_preprocessing.getValue());

	}

	@Listen("onClick= #shift_period_ok")
	public void assignShiftInPeriod() {

		if (!this.checkConnection()) {
			return;
		}

		if (this.shift_period_combo.getSelectedItem() == null) {
			return;
		}

		if ((this.shift_period_from.getValue() == null) || (this.shift_period_to.getValue() == null)) {
			return;
		}

		if (this.grid_scheduler_day.getSelectedItem() == null) {
			return;
		}

		if (!(this.shift_period_combo.getSelectedItem().getValue() instanceof UserShift)
				|| (this.shift_period_combo.getSelectedItem().getValue() == null)) {
			return;
		}

		// check over data from
		Calendar calendar_from = DateUtils.toCalendar(this.shift_period_from.getValue());
		calendar_from = DateUtils.truncate(calendar_from, Calendar.DATE);
		Calendar today = Calendar.getInstance();
		today = DateUtils.truncate(today, Calendar.DATE);
		if (calendar_from.before(today)) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Non puoi fare assegnazioni multiple che partono prima del giorno attuale.", "INFO", buttons, null,
					Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		// check user shift
		final UserShift shift = this.shift_period_combo.getSelectedItem().getValue();
		if (shift == null) {
			return;
		}

		if (shift.getBreak_shift().booleanValue() || shift.getWaitbreak_shift().booleanValue()) {

			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Non puoi usare il turno ti riposo o riposo atteso per assegnazioni multiple.", "INFO", buttons, null,
					Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		Calendar calendar_to = DateUtils.toCalendar(this.shift_period_to.getValue());
		calendar_to = DateUtils.truncate(calendar_to, Calendar.DATE);

		if (calendar_from.after(calendar_to)) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controlla le date inserite.", "INFO", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		final RowDaySchedule row_day_schedule = this.grid_scheduler_day.getSelectedItem().getValue();
		final Integer user = row_day_schedule.getUser();
		final Person editor = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Calendar index_calendar = Calendar.getInstance();
		index_calendar.setTimeInMillis(calendar_from.getTimeInMillis());
		index_calendar = DateUtils.truncate(index_calendar, Calendar.DATE);

		do {

			final Date date_index = index_calendar.getTime();

			// refresh info about just saved schedule
			final Schedule schedule = this.scheduleDAO.loadSchedule(date_index, user);

			// check if is possible to override break
			if ((schedule != null) && (schedule.getShift() != null)) {
				final UserShift shift_target = this.shift_cache.getUserShift(schedule.getShift());
				final UserShift shift_rp_break = this.shift_cache.getBreakShift();
				final UserShift shift_rp_waited = this.shift_cache.getWaitedBreakShift();
				if ((shift_rp_break != null) && (shift_rp_waited != null)) {
					if (shift_target.equals(shift_rp_break) || shift_target.equals(shift_rp_waited)) {
						index_calendar.add(Calendar.DAY_OF_YEAR, 1);
						continue;
					}
				}

			}

			// assign
			this.statProcedure.workAssignProcedure(shift, date_index, user, editor.getId());
			index_calendar.add(Calendar.DAY_OF_YEAR, 1);

		} while (!index_calendar.after(calendar_to));

		this.setupGlobalSchedulerGridForDay();

		// hide panel if all is ok
		this.panel_shift_period.setVisible(false);

	}

	@Listen("onClick = #calculate_sat")
	public void calculateSatOnPersons() {

		// update cache
		this.cache_sat = new HashMap<>();

		final List<Person> list_person = this.personDAO.listWorkerPersons(null, null);

		// set saturation month label
		final Calendar cal_saturation_month = Calendar.getInstance();
		cal_saturation_month.set(Calendar.DAY_OF_MONTH, cal_saturation_month.getActualMaximum(Calendar.DAY_OF_MONTH));

		final Date date_to_on_month = cal_saturation_month.getTime();
		cal_saturation_month.set(Calendar.DAY_OF_MONTH, cal_saturation_month.getMinimum(Calendar.DAY_OF_MONTH));
		final Date date_from_on_month = cal_saturation_month.getTime();

		for (final Person person : list_person) {

			final Double sat = this.statProcedure.calculeHourSaturation(person, date_from_on_month, date_to_on_month);
			this.cache_sat.put(person.getId(), sat);

		}

		// update info in session
		Executions.getCurrent().getSession().setAttribute(ZkSessionTag.PersonCache, this.cache_sat);

	}

	@Listen("onClick = #cancel_modify")
	public void cancelModify() {
		this.save_review_item.setVisible(false);
		this.cancel_modify.setVisible(false);
		this.add_review_item.setVisible(true);
		this.remove_review_item.setVisible(true);

		this.ship.setSelectedItem(null);
		this.shipInDay.setSelectedItem(null);
		this.crane.setValue("");
		this.board.setSelectedItem(null);

		this.errorMessageAddItem.setValue("");

	}

	@Listen("onClick = #cancel_modify_program")
	public void cancelModifyProgram() {
		this.errorMessageAddProgramItem.setValue("");
		this.save_program_item.setVisible(false);
		this.cancel_modify_program.setVisible(false);
		this.add_program_item.setVisible(true);
		this.remove_program_item.setVisible(true);
		this.continue_shiftProgram.setChecked(false);
	}

	@Listen("onChange = #date_init_scheduler_review;onOK = #date_init_scheduler_review")
	public void changeBehaviorReview() {
		// define command behavior for not supervisor
		final Person person_selected = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!person_selected.isAdministrator()) {

			if (this.date_init_scheduler_review.getValue() == null) {
				this.assign_program_review.setDisabled(true);
				this.ok_review.setDisabled(true);
				this.add_review_item.setDisabled(true);
				this.remove_review_item.setDisabled(true);
				this.cancel_review.setDisabled(true);
			} else {
				final Date date = DateUtils.truncate(this.date_init_scheduler_review.getValue(), Calendar.DATE);

				final Calendar calendar = Calendar.getInstance();

				calendar.add(Calendar.DAY_OF_YEAR, -1);
				final boolean check1 = DateUtils.isSameDay(date, calendar.getTime());

				calendar.add(Calendar.DAY_OF_YEAR, -1);
				final boolean check2 = DateUtils.isSameDay(date, calendar.getTime());

				calendar.add(Calendar.DAY_OF_YEAR, -1);
				final boolean check3 = DateUtils.isSameDay(date, calendar.getTime());

				if (check1 || check2 || check3) {
					this.assign_program_review.setDisabled(false);
					this.ok_review.setDisabled(false);
					this.add_review_item.setDisabled(false);
					this.remove_review_item.setDisabled(false);
					this.cancel_review.setDisabled(false);
				} else {
					this.assign_program_review.setDisabled(true);
					this.ok_review.setDisabled(true);
					this.add_review_item.setDisabled(true);
					this.remove_review_item.setDisabled(true);
					this.cancel_review.setDisabled(true);
				}
			}

		} else {
			this.assign_program_review.setDisabled(false);
			this.ok_review.setDisabled(false);
			this.add_review_item.setDisabled(false);
			this.remove_review_item.setDisabled(false);
			this.cancel_review.setDisabled(false);
		}
	}

	@Listen("onChange = #combo_user_dip;onClick = #remove_combo_user_dip")
	public void changeUserDipComboInUserStatistic() {

		this.refreshUserStatistics(this.full_text_search.getValue(), this.combo_user_dip.getValue());

	}

	private boolean checkConnection() {
		if (!this.checkIfUnLockTable()) {
			SchedulerComposer.this.disableWriteCancelButtons(true);
			if (this.userLockTable != null) {
				this.loggerUserOnTable.setValue(this.messageTableLock + this.personLock.getFirstname() + " " + this.personLock.getLastname() + " - "
						+ this.messageTimeConnectionTableLock + Utility.convertToDateAndTime(this.userLockTable.getTime_start()));
				this.switchButton.setLabel(this.switchButtonValueClose);

			} else {
				this.loggerUserOnTable.setValue(this.messageTableUnLock);
				this.switchButton.setLabel(this.switchButtonValueOpen);
			}
			return false;
		}
		return true;
	}

	/**
	 * Check for command availability in pre processing
	 *
	 * @param current_day
	 * @return
	 */
	private boolean checkForPreprocessingCommand(final Date current_day) {
		// set command enabling
		final Calendar today = Calendar.getInstance();
		final Calendar last_day_modify = DateUtils.truncate(today, Calendar.DATE);

		// before 3 days, you can not use some command
		last_day_modify.add(Calendar.DAY_OF_YEAR, -3);

		// disable preprocessing
		if (current_day.before(last_day_modify.getTime())) {

			return false;

		} else {
			return true;
		}
	}

	/**
	 * Used to set open/close operation
	 *
	 * @return
	 */
	private void checkIfTableIsLockedAndSetButton() {
		this.loggerUserOnTable.setVisible(true);
		this.switchButton.setVisible(true);

		if (this.person_logged != null) {
			// check if user logged is a Programmer or Administrator
			if (this.person_logged.isBackoffice() || this.person_logged.isAdministrator()) {
				final Comboitem version_selected = this.scheduler_type_selector.getSelectedItem();
				LockTable lockTable = null;
				if ((version_selected == this.preprocessing_item) || (version_selected == this.program_item)) {
					lockTable = this.lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE);
				} else if (version_selected == SchedulerComposer.this.review_item) {
					lockTable = this.lockTableDAO.loadLockTableByTableType(TableTag.REVIEW_TABLE);
				}

				if (lockTable == null) {
					// table is unlock
					this.loggerUserOnTable.setValue(this.messageTableUnLock);
					this.switchButton.setVisible(true);
					this.switchButton.setLabel(this.switchButtonValueOpen);

					if (this.person_logged.isAdministrator()) {
						this.disableWriteCancelButtons(false);
					} else {
						this.disableWriteCancelButtons(true);
					}
					return;

				} else if (!this.person_logged.isAdministrator()) {
					// another user has lock table
					final Person user = this.personDAO.loadPerson(lockTable.getId_user());
					this.switchButton.setVisible(false);
					this.disableWriteCancelButtons(true);
					if (lockTable.getId_user().equals(this.person_logged.getId())) {
						this.switchButton.setVisible(true);
						this.switchButton.setLabel(this.switchButtonValueClose);
						this.disableWriteCancelButtons(false);
						this.loggerUserOnTable.setValue(this.messageTableLock + user.getFirstname() + " " + user.getLastname() + " - "
								+ this.messageTimeConnectionTableLock + Utility.convertToDateAndTime(lockTable.getTime_start()));
						return;
					}

					this.loggerUserOnTable.setValue(this.messageTableLock + user.getFirstname() + " " + user.getLastname() + " - "
							+ this.messageTimeConnectionTableLock + Utility.convertToDateAndTime(lockTable.getTime_start()));
					return;
				} else if (this.person_logged.isAdministrator()) {
					// another user has lock table, can unlock because is
					// administrator
					final Person user = this.personDAO.loadPerson(lockTable.getId_user());
					this.loggerUserOnTable.setValue(this.messageTableLock + user.getFirstname() + " " + user.getLastname() + " - "
							+ this.messageTimeConnectionTableLock + Utility.convertToDateAndTime(lockTable.getTime_start()));
					this.switchButton.setLabel(this.switchButtonValueClose);
					this.switchButton.setVisible(true);
					this.disableWriteCancelButtons(false);
					return;
				}
			}
			return;
		}
		return;
	}

	private boolean checkIfUnLockTable() {
		this.onChangeSelectedVersion();

		// set button switch
		this.checkIfTableIsLockedAndSetButton();

		final Comboitem version_selected = SchedulerComposer.this.scheduler_type_selector.getSelectedItem();
		this.userLockTable = null;
		if ((version_selected == SchedulerComposer.this.preprocessing_item) || (version_selected == SchedulerComposer.this.program_item)) {
			this.userLockTable = SchedulerComposer.this.lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE);
		} else if (version_selected == SchedulerComposer.this.review_item) {
			this.userLockTable = SchedulerComposer.this.lockTableDAO.loadLockTableByTableType(TableTag.REVIEW_TABLE);
		}
		this.personLock = null;
		if (!this.person_logged.isAdministrator()
				&& ((((this.userLockTable != null) && !this.userLockTable.getId_user().equals(this.person_logged.getId()))
						|| (this.userLockTable == null)))) {
			SchedulerComposer.this.disableWriteCancelButtons(true);
			if (this.userLockTable != null) {
				this.personLock = this.personDAO.loadPerson(this.userLockTable.getId_user());
			}

			return false;

		} else {

			return true;
		}

	}

	@Listen("onClick = #sw_link_add_comp_command")
	public void commandCompenstion() {

		if (this.user_statistic_selected == null) {
			return;
		}

		final Integer user_id = this.user_statistic_selected.getPerson().getId();
		final Integer year = Calendar.getInstance().get(Calendar.YEAR);

		if (this.status_comp_editor.equals(SchedulerComposer.STATUS_COMP_EDITOR_EDIT)) {

			if (this.sw_compensation_list.getSelectedItem() == null) {
				return;
			}

			final UserCompensation mod_item = this.sw_compensation_list.getSelectedItem().getValue();
			mod_item.setDate_submit(this.date_submitUser.getValue());
			mod_item.setId_user(user_id);
			mod_item.setNote(this.note_compUser.getValue());
			mod_item.setTime_comp(this.time_compUser.getValue());

			this.userCompensationDAO.updateUserCompensation(mod_item);

		} else if (this.status_comp_editor.equals(SchedulerComposer.STATUS_COMP_EDITOR_ADD)) {

			final UserCompensation new_item = new UserCompensation();
			new_item.setDate_submit(this.date_submitUser.getValue());
			new_item.setId_user(user_id);
			new_item.setNote(this.note_compUser.getValue());
			new_item.setTime_comp(this.time_compUser.getValue());

			this.userCompensationDAO.createUserCompensation(new_item);

		}

		final List<UserCompensation> list = this.userCompensationDAO.loadAllUserCompensationByUserId(user_id, year);
		final ListModelList<UserCompensation> model = new ListModelList<>(list);
		this.sw_compensation_list.setModel(model);

		this.refreshUserStatistics(this.full_text_search.getValue(), this.combo_user_dip.getValue());

		this.comp_editor.setVisible(false);
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

		if (!this.checkConnection()) {
			return;
		}

		final Map<String, String> params = new HashMap<>();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[2];
		buttons[0] = Messagebox.Button.OK;
		buttons[1] = Messagebox.Button.CANCEL;

		Messagebox.show("Stai assegnando i turni programmati al consuntivo. Sei sicuro di voler continuare?", "CONFERMA ASSEGNAZIONE", buttons, null,
				Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>() {
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

		if ((this.grid_scheduler_review == null) || (this.grid_scheduler_review.getSelectedItems() == null)
				|| (this.grid_scheduler_review.getSelectedItems().size() == 0)) {
			return;
		}

		final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		for (final Listitem item : this.grid_scheduler_review.getSelectedItems()) {

			final RowSchedule item_row = item.getValue();

			if (item_row.getItem_1() == null) {
				continue;
			}

			final Schedule schedule = item_row.getItem_1().getSchedule();
			if ((schedule == null) || (schedule.getId() == null)) {
				continue;
			}

			final List<DetailInitialSchedule> list_init_detail = this.scheduleDAO.loadDetailInitialScheduleByIdSchedule(schedule.getId());

			this.scheduleDAO.removeAllDetailFinalScheduleBySchedule(schedule.getId());

			for (final DetailInitialSchedule init_item : list_init_detail) {

				final DetailFinalSchedule detail_schedule = new DetailFinalSchedule();

				detail_schedule.setDate_schedule(schedule.getDate_schedule());
				detail_schedule.setId_schedule(init_item.getId_schedule());
				detail_schedule.setShift(init_item.getShift());
				detail_schedule.setTask(init_item.getTask());
				detail_schedule.setTime(init_item.getTime());
				detail_schedule.setTime_vacation(init_item.getTime_vacation());
				detail_schedule.setContinueshift(init_item.getContinueshift());

				Calendar to_day_calendar = DateUtils.toCalendar(schedule.getDate_schedule());
				to_day_calendar = DateUtils.truncate(to_day_calendar, Calendar.DATE);

				detail_schedule.setTime_to(init_item.getTime_to());
				detail_schedule.setTime_from(init_item.getTime_from());

				this.scheduleDAO.createDetailFinalSchedule(detail_schedule);
			}

			// set controller
			schedule.setController(person_logged.getId());
			this.scheduleDAO.saveOrUpdateSchedule(schedule);

		}

		// redefine info
		this.setupGlobalSchedulerGridForShiftReview();

	}

	/**
	 * define the view in function of the type of the view required
	 */
	@Listen("onChange = #scheduler_type_selector, #date_init_scheduler, #date_init_scheduler_review, #select_shift_overview,#select_shifttype_overview, #taskComboBox, #hoursInterval, #shipSelector, #dayWorking_filter; onOK = #date_to_overview, #date_from_overview, #date_init_scheduler, #date_init_scheduler_review, #shows_rows, #full_text_search, #craneSelector; onSelect = #overview_tab; onClick = #calculate_sat")
	public void defineSchedulerView() {

		this.setLastProgrammerLabel();
		this.overview_download.setVisible(false);

		this.setVisibilityDownloadReportButton();

		if (this.scheduler_type_selector.getSelectedItem() == null) {
			this.grid_scheduler.setVisible(false);
			this.grid_scheduler_review.setVisible(false);
			this.list_overview_program.setVisible(false);
			this.grid_scheduler_day.setVisible(false);
			return;
		} else {
			this.grid_scheduler.setVisible(true);
			this.grid_scheduler_review.setVisible(true);
			this.list_overview_program.setVisible(true);
			this.grid_scheduler_day.setVisible(true);
		}

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		// hide panel shift special
		this.panel_shift_period.setVisible(false);

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

			this.overview_download.setVisible(true);
			this.overview_div.setVisible(true);
			this.preprocessing_div.setVisible(false);
			this.program_div.setVisible(false);
			this.review_div.setVisible(false);

			if (this.statisticsTab.isSelected()) {
				this.reviewSearchBox.setVisible(false);
			} else {
				this.reviewSearchBox.setVisible(true);
			}

			if (this.reviewTab.isSelected()) {
				this.reviewShiftBox.setVisible(true);
				this.continue_shift.setVisible(true);
			} else {
				this.reviewShiftBox.setVisible(false);
				this.continue_shift.setVisible(false);
			}

			// set overview list
			if (this.select_year.getSelectedItem() != null) {
				this.selectedYear();
			} else {
				this.setOverviewLists(this.date_from_overview.getValue(), this.date_to_overview.getValue());

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
		if (this.scheduler_type_selector.getSelectedItem() == null) {
			return;
		}

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected == this.preprocessing_item) {
			this.preprocessing_div.setVisible(true);
			this.program_div.setVisible(false);
			this.review_div.setVisible(false);

			SchedulerComposer.this.date_init_scheduler.setValue(date);

			// set initial structure for program
			this.setGridStructureForDay(date);
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

			SchedulerComposer.this.date_init_scheduler_review.setValue(date);

			// set initial structure for program
			this.setGridStructureForShiftReview(date);
			this.setupGlobalSchedulerGridForShiftReview();
		}
	}

	@Listen("onClick= #set_panel_shift_period")
	public void defineShiftPeriodPanel() {

		if (this.grid_scheduler_day.getSelectedItem() == null) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Seleziona un utente prima.", "ERROR", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		final RowDaySchedule row_day_schedule = this.grid_scheduler_day.getSelectedItem().getValue();

		// set label
		this.shift_period_name.setValue(row_day_schedule.getName_user());

		// show panel
		this.panel_shift_period.setVisible(true);
	}

	/**
	 * Define user availability
	 *
	 * @return
	 */
	private HashMap<Integer, String> defineUserAvailability(final Date date_picker) {

		// define info about day scheduled - define person available
		final List<Schedule> day_schedule_list = this.scheduleDAO.loadSchedule(date_picker);

		final HashMap<Integer, String> map_status = new HashMap<>();
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

	/**
	 * Define initial view in overview
	 */
	private void defineViewCurrentWorkInOverview() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		this.date_from_overview.setValue(DateUtils.truncate(calendar.getTime(), Calendar.DATE));

		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

		this.date_to_overview.setValue(calendar.getTime());
	}

	/**
	 * Defining printable version
	 */
	private void definingPrintableVersion() {
		// printable version
		this.print_scheduler.setVisible(true);

		// remove header info
		this.header_info.setVisible(false);

		// remove command update
		this.stat_update_command.setVisible(false);

		// set width for A4 paper
		this.grid_scheduler_day.setWidth("950px");
		this.preprocessing_panel.setWidth("950px");

		this.grid_scheduler.setWidth("650px");
		this.program_panel.setWidth("650px");
		this.program_panel_name.setHflex("3");

		this.grid_scheduler_review.setWidth("650px");
		this.review_panel.setWidth("650px");
		this.review_panel_name.setHflex("3");

		this.overview_tab.setWidth("950px");

		this.preprocessing_div.setVisible(false);
		this.program_div.setVisible(false);
		this.review_div.setVisible(false);
		this.review_div.setVisible(false);

		// remove command
		this.color_program_legend.setVisible(false);
		this.color_legend_review.setVisible(false);
		this.repogram_users.setVisible(false);
		this.download_program_report.setVisible(false);
		this.set_panel_shift_period.setVisible(false);
		this.assign_program_review.setVisible(false);

		this.scheduler_type_selector.setSelectedItem(null);

		// show only today and tomorrow
		this.getSelf().getFellowIfAny("day_month_1").setVisible(false);
		this.getSelf().getFellowIfAny("day_month_4").setVisible(false);
		this.getSelf().getFellowIfAny("day_month_5").setVisible(false);

		this.totalUser_program_day_1.setVisible(false);
		this.totalUser_program_day_4.setVisible(false);
		this.totalUser_program_day_5.setVisible(false);

		this.total_program_day_1.setVisible(false);
		this.total_program_day_4.setVisible(false);
		this.total_program_day_5.setVisible(false);

		this.program_tot_1_1.setVisible(false);
		this.program_tot_1_2.setVisible(false);
		this.program_tot_1_3.setVisible(false);
		this.program_tot_1_4.setVisible(false);

		this.program_tot_4_1.setVisible(false);
		this.program_tot_4_2.setVisible(false);
		this.program_tot_4_3.setVisible(false);
		this.program_tot_4_4.setVisible(false);

		this.program_tot_5_1.setVisible(false);
		this.program_tot_5_2.setVisible(false);
		this.program_tot_5_3.setVisible(false);
		this.program_tot_5_4.setVisible(false);

		this.program_head_1_1.setVisible(false);
		this.program_head_1_2.setVisible(false);
		this.program_head_1_3.setVisible(false);
		this.program_head_1_4.setVisible(false);

		this.program_head_4_1.setVisible(false);
		this.program_head_4_2.setVisible(false);
		this.program_head_4_3.setVisible(false);
		this.program_head_4_4.setVisible(false);

		this.program_head_5_1.setVisible(false);
		this.program_head_5_2.setVisible(false);
		this.program_head_5_3.setVisible(false);
		this.program_head_5_4.setVisible(false);

	}

	@Listen("onClick = #sw_link_delete_comp")
	public void deleteCompenstion() {

		if (this.sw_compensation_list.getSelectedItem() == null) {
			return;
		}

		if (this.user_statistic_selected == null) {
			return;
		}

		final Integer user_id = this.user_statistic_selected.getPerson().getId();
		final Integer year = Calendar.getInstance().get(Calendar.YEAR);

		final UserCompensation user_comp = this.sw_compensation_list.getSelectedItem().getValue();

		this.userCompensationDAO.deleteUserCompensation(user_comp.getId());

		final List<UserCompensation> list = this.userCompensationDAO.loadAllUserCompensationByUserId(user_id, year);
		final ListModelList<UserCompensation> model = new ListModelList<>(list);
		this.sw_compensation_list.setModel(model);

		this.refreshUserStatistics(this.full_text_search.getValue(), this.combo_user_dip.getValue());

	}

	/**
	 * Disable write command
	 *
	 * @param isDisabled
	 */
	private void disableWriteCancelButtons(final boolean isDisabled) {
		this.shift_period_ok.setDisabled(isDisabled);
		this.cancel_day_definition.setDisabled(isDisabled);
		this.ok_day_shift.setDisabled(isDisabled);
		this.ok_program.setDisabled(isDisabled);
		this.cancel_program.setDisabled(isDisabled);
		this.add_program_item.setDisabled(isDisabled);
		this.remove_program_item.setDisabled(isDisabled);
		this.ok_review.setDisabled(isDisabled);
		this.cancel_review.setDisabled(isDisabled);
		this.add_review_item.setDisabled(isDisabled);
		this.remove_review_item.setDisabled(isDisabled);
		this.assign_program_review.setDisabled(isDisabled);
		this.repogram_users.setDisabled(isDisabled);

		this.sw_link_edit_program.setDisabled(isDisabled);
		this.sw_link_edit_review.setDisabled(isDisabled);

		this.setVisibilityDownloadReportButton();
	}

	@Override
	public void doFinally() throws Exception {

		// set info about person logged
		this.person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// select initial value for initial date
		this.date_init_scheduler.setValue(Calendar.getInstance().getTime());

		// select initial value for initial date - review
		final Calendar calender_review = Calendar.getInstance();
		calender_review.add(Calendar.DATE, -1);
		this.date_init_scheduler_review.setValue(calender_review.getTime());

		// acquire dao
		this.scheduleDAO = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);
		this.scheduleShipDAO = (IScheduleShip) SpringUtil.getBean(BeansTag.SCHEDULE_SHIP_DAO);

		this.taskDAO = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);
		this.personDAO = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);

		this.configurationDAO = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
		this.bank_holiday = (IBankHolidays) SpringUtil.getBean(BeansTag.BANK_HOLIDAYS);

		this.statisticDAO = (IStatistics) SpringUtil.getBean(BeansTag.STATISTICS);
		this.statProcedure = (IStatProcedure) SpringUtil.getBean(BeansTag.STAT_PROCEDURE);

		this.lockTableDAO = (LockTableDAO) SpringUtil.getBean(BeansTag.LOCK_TABLE_DAO);

		this.shipDAO = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);

		this.shipSchedulerDao = (IScheduleShip) SpringUtil.getBean(BeansTag.SCHEDULE_SHIP_DAO);

		this.userCompensationDAO = (UserCompensationDAO) SpringUtil.getBean(BeansTag.USER_COMPENSATION_DAO);

		final ListModelList<Ship> shipList = new ListModelList<>(this.shipDAO.loadAllShip());

		final List<UserTask> taskList = new ArrayList<>();

		// add ship for "ship null" filter
		final UserTask taskNull = new UserTask();
		taskNull.setId(-1);
		taskNull.setCode(" ");
		taskNull.setDescription("Mansione non inserita");
		taskNull.setIsabsence(false);
		taskNull.setJustificatory(false);
		taskList.add(0, taskNull);

		// add ship for "all selected" filter
		final UserTask task = new UserTask();
		task.setId(-2);
		task.setCode(" ");
		task.setDescription("Tutte le mansioni");
		task.setIsabsence(false);
		task.setJustificatory(false);
		taskList.add(1, task);

		final List<UserTask> list_task_user = this.configurationDAO.listAllStandardTask();
		final List<UserTask> list_task_absence = this.configurationDAO.listAllAbsenceTask();
		final List<UserTask> list_task_justificatory = this.configurationDAO.listAllJustificatoryTask();

		taskList.addAll(list_task_user);
		taskList.addAll(list_task_justificatory);
		taskList.addAll(list_task_absence);

		for (final UserTask task_item : taskList) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());
			if (task_item.getIsabsence()) {
				combo_item.setStyle("color: " + TaskColor.ANBSENCE_COLOR);
			} else if (task_item.getJustificatory()) {
				combo_item.setStyle("color: " + TaskColor.JUSTIFICATORY_COLOR);
			}
			this.taskComboBox.appendChild(combo_item);

		}

		// set value in board combo box
		final List<String> boardList = new ArrayList<>();
		boardList.add("--");
		boardList.add(BoardTag.ON_BOARD);
		boardList.add(BoardTag.UNDER_BOARD);

		this.board.setModel(new ListModelList<>(boardList));

		// set visibility of download_program_report button
		final Person personLogged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (personLogged.isAdministrator()) {
			this.download_program_report.setVisible(true);
		} else {
			this.setVisibilityDownloadReportButton();
		}

		if (shipList != null) {

			// add ship for popup
			final Ship shipNullPopup = new Ship();
			shipNullPopup.setId(-1);
			shipNullPopup.setName("--");
			final ListModelList<Ship> shipListPopup = new ListModelList<>(shipList);
			shipListPopup.add(0, shipNullPopup);
			this.ship.setModel(shipListPopup);

			// add ship for "ship null" filter
			final Ship shipNull = new Ship();
			shipNull.setId(-1);
			shipNull.setName("Navi non inserite");
			shipList.add(0, shipNull);

			// add ship for "all selected" filter
			final Ship ship = new Ship();
			ship.setId(-2);
			ship.setName("Tutte le navi");
			shipList.add(1, ship);

			this.shipSelector.setModel(shipList);

		}

		// set year in combobox
		final Integer todayYear = Utility.getYear(Calendar.getInstance().getTime());
		final ArrayList<String> years = new ArrayList<>();

		years.add(SchedulerComposer.ALL_ITEM);

		for (Integer i = 2014; i <= (todayYear + 2); i++) {
			years.add(i.toString());
		}

		this.select_year.setModel(new ListModelList<>(years));

		// set months in combox
		final ArrayList<String> months = new ArrayList<>();
		months.add(SchedulerComposer.ALL_ITEM);
		for (Integer i = 1; i <= 12; i++) {
			months.add(i.toString());
		}

		this.select_month.setModel(new ListModelList<>(months));

		// set months in combox
		final ArrayList<String> weeks = new ArrayList<>();
		weeks.add(SchedulerComposer.ALL_ITEM);
		for (Integer i = 1; i <= 53; i++) {
			weeks.add(i.toString());
		}

		this.select_week.setModel(new ListModelList<>(weeks));

		this.getSelf().addEventListener(ZkEventsTag.onNameCompensationClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// Only admin can open this popup
				final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				if (!person_logged.isAdministrator()) {
					return;
				}

				if (SchedulerComposer.this.list_statistics.getSelectedItem() == null) {
					return;
				}

				SchedulerComposer.this.user_statistic_selected = SchedulerComposer.this.list_statistics.getSelectedItem().getValue();

				// get user id
				final Integer user_id = SchedulerComposer.this.user_statistic_selected.getPerson().getId();

				// get calendar for year
				final int year = Calendar.getInstance().get(Calendar.YEAR);

				final List<UserCompensation> list = SchedulerComposer.this.userCompensationDAO.loadAllUserCompensationByUserId(user_id, year);
				final ListModelList<UserCompensation> model = new ListModelList<>(list);
				SchedulerComposer.this.sw_compensation_list.setModel(model);

				// set year label
				SchedulerComposer.this.compe_popup_year.setLabel("" + Calendar.getInstance().get(Calendar.YEAR));

				SchedulerComposer.this.compensation_popup.open(SchedulerComposer.this.list_statistics, "after_pointer");

			}

		});

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

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, SchedulerComposer.this.grid_scheduler_day, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, SchedulerComposer.this.grid_scheduler_day, msg);

				}

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

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, SchedulerComposer.this.grid_scheduler, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, SchedulerComposer.this.grid_scheduler, msg);

				}

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

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, SchedulerComposer.this.grid_scheduler_review, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, SchedulerComposer.this.grid_scheduler_review, msg);

				}

			}

		});

		this.getSelf().addEventListener(ZkEventsTag.onOverviewReviewNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailFinalSchedule detailFinalSchedule = SchedulerComposer.this.list_overview_review.getSelectedItem().getValue();

				if (detailFinalSchedule == null) {
					return;
				}

				// set name
				final String msg = detailFinalSchedule.getUser();

				final Integer id_user = detailFinalSchedule.getId_user();

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, SchedulerComposer.this.list_overview_review, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, SchedulerComposer.this.list_overview_review, msg);

				}

			}

		});

		this.getSelf().addEventListener(ZkEventsTag.onOverviewReviewShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailFinalSchedule detailFinalSchedule = SchedulerComposer.this.list_overview_review.getSelectedItem().getValue();

				if (detailFinalSchedule == null) {
					return;
				}

				final UserShift shift = SchedulerComposer.this.configurationDAO.loadShiftById(detailFinalSchedule.getShift_type());

				if (shift != null) {
					SchedulerComposer.this.shift_popup.open(SchedulerComposer.this.review_div, "after_pointer");
					SchedulerComposer.this.shift_id.setValue(shift.getCode());
					SchedulerComposer.this.shift_description.setValue(shift.getDescription());
				}
			}
		});

		this.getSelf().addEventListener(ZkEventsTag.onOverviewReviewTaskClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailFinalSchedule detailFinalSchedule = SchedulerComposer.this.list_overview_review.getSelectedItem().getValue();

				if (detailFinalSchedule == null) {
					return;
				}

				UserTask task = SchedulerComposer.this.configurationDAO.loadTask(detailFinalSchedule.getTask());

				if (task != null) {
					SchedulerComposer.this.task_popup.open(SchedulerComposer.this.review_div, "after_pointer");
					SchedulerComposer.this.task_id.setValue(task.getCode());
					SchedulerComposer.this.task_description.setValue(task.getDescription());
				} else {

					final IShiftCache shiftCache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);
					final UserShift shift = shiftCache.getUserShift(detailFinalSchedule.getShift_type());

					// Absence Shift
					if (!shift.getPresence()) {
						final PersonDAO personDAO = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);

						final Person user = personDAO.loadPerson(detailFinalSchedule.getId_user());
						task = SchedulerComposer.this.taskDAO.getDefault(user.getId());

						SchedulerComposer.this.task_popup.open(SchedulerComposer.this.review_div, "after_pointer");
						SchedulerComposer.this.task_id.setValue(task.getCode());
						SchedulerComposer.this.task_description.setValue(task.getDescription());

					}

					if (task != null) {
						if (task.getRecorded().booleanValue()) {
							SchedulerComposer.this.task_cont.setValue("SI");
						} else {
							SchedulerComposer.this.task_cont.setValue("NO");

						}
					} else {
						SchedulerComposer.this.task_cont.setValue("");
					}
				}
			}
		});

		this.getSelf().addEventListener(ZkEventsTag.onOverviewProgramShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailInitialSchedule detailInitialSchedule = SchedulerComposer.this.list_overview_program.getSelectedItem().getValue();

				if (detailInitialSchedule == null) {
					return;
				}

				final UserShift shift = SchedulerComposer.this.configurationDAO.loadShiftById(detailInitialSchedule.getShift_type());

				if (shift != null) {
					SchedulerComposer.this.shift_popup.open(SchedulerComposer.this.review_div, "after_pointer");
					SchedulerComposer.this.shift_id.setValue(shift.getCode());
					SchedulerComposer.this.shift_description.setValue(shift.getDescription());
				}
			}
		});

		this.getSelf().addEventListener(ZkEventsTag.onOverviewProgramTaskClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailInitialSchedule detailInitialSchedule = SchedulerComposer.this.list_overview_program.getSelectedItem().getValue();

				if (detailInitialSchedule == null) {
					return;
				}

				final UserTask task = SchedulerComposer.this.configurationDAO.loadTask(detailInitialSchedule.getTask());

				if (task != null) {
					SchedulerComposer.this.task_popup.open(SchedulerComposer.this.review_div, "after_pointer");
					SchedulerComposer.this.task_id.setValue(task.getCode());
					SchedulerComposer.this.task_description.setValue(task.getDescription());
				} else {

					final IShiftCache shiftCache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);
					final UserShift shift = shiftCache.getUserShift(detailInitialSchedule.getShift_type());

					// Absence Shift
					if (!shift.getPresence()) {
						final PersonDAO personDAO = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);

						final Person user = personDAO.loadPerson(detailInitialSchedule.getId_user());
						final UserTask defaultTask = SchedulerComposer.this.taskDAO.getDefault(user.getId());

						SchedulerComposer.this.task_popup.open(SchedulerComposer.this.review_div, "after_pointer");
						SchedulerComposer.this.task_id.setValue(defaultTask.getCode());
						SchedulerComposer.this.task_description.setValue(defaultTask.getDescription());

					}
				}
			}
		});

		this.getSelf().addEventListener(ZkEventsTag.onOverviewPreprocessingShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final Schedule schedule = SchedulerComposer.this.list_overview_preprocessing.getSelectedItem().getValue();

				if (schedule == null) {
					return;
				}

				final UserShift shift = SchedulerComposer.this.configurationDAO.loadShiftById(schedule.getShift());

				if (shift != null) {
					SchedulerComposer.this.shift_popup.open(SchedulerComposer.this.review_div, "after_pointer");
					SchedulerComposer.this.shift_id.setValue(shift.getCode());
					SchedulerComposer.this.shift_description.setValue(shift.getDescription());
				}
			}
		});

		this.getSelf().addEventListener(ZkEventsTag.onOverviewProgramNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final DetailInitialSchedule detailInitialSchedule = SchedulerComposer.this.list_overview_program.getSelectedItem().getValue();

				if (detailInitialSchedule == null) {
					return;
				}

				final Integer id_user = detailInitialSchedule.getId_user();
				// set name
				final String msg = detailInitialSchedule.getUser();

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, SchedulerComposer.this.list_overview_program, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, SchedulerComposer.this.list_overview_program, msg);

				}

			}

		});

		this.getSelf().addEventListener(ZkEventsTag.onOverviewPreprocessingNameClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final Schedule schedule = SchedulerComposer.this.list_overview_preprocessing.getSelectedItem().getValue();

				if (schedule == null) {
					return;
				}

				final Integer id_user = schedule.getUser();
				// set name
				final String msg = schedule.getName_user();

				final String param = arg0.getData().toString();
				if ((param == null) || param.equals("left")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsPopup(id_user, SchedulerComposer.this.list_overview_preprocessing, msg);

				} else if ((param != null) && param.equals("right")) {

					// show statistic popup
					SchedulerComposer.this.showStatisticsTaskPopup(id_user, SchedulerComposer.this.list_overview_preprocessing, msg);

				}

			}

		});

		this.getSelf().addEventListener(ZkEventsTag.onShowScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				SchedulerComposer.this.mainSchedulerView(null);

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
		if (this.overview_review.isSelected() && (this.listDetailRevision != null)) {

			final StringBuilder builder = UtilityCSV.downloadCSVReview(this.listDetailRevision, this.taskDAO, this.shift_cache, this.ship_cache,
					this.scheduleDAO, this.person_logged.isAdministrator());

			Filedownload.save(builder.toString(), "application/text", "revision.csv");
		} else if (this.overview_program.isSelected() && (this.listDetailProgram != null)) {

			final StringBuilder builder = UtilityCSV.downloadCSVProgram(this.listDetailProgram, this.taskDAO, this.shift_cache, this.scheduleDAO,
					this.person_logged.isAdministrator());

			Filedownload.save(builder.toString(), "application/text", "program.csv");

		}

		else if (this.overview_preprocessing.isSelected() && (this.listSchedule != null)) {

			final StringBuilder builder = UtilityCSV.downloadCSVPreprocessing(this.listSchedule, this.shift_cache);

			Filedownload.save(builder.toString(), "application/text", "preprocessing.csv");

		} else if (this.overview_statistics.isSelected() && (this.listUserStatistics != null)) {
			final StringBuilder builder = UtilityCSV.downloadCSVStatistics(this.listUserStatistics);

			Filedownload.save(builder.toString(), "application/text", "statistics.csv");

		}

	}

	@Listen("onClick= #download_program_report")
	public void downloadProgramReport() {

		if ((this.list_rows_program == null) || (this.list_rows_program.size() == 0)) {
			return;
		}

		ByteArrayOutputStream stream = null;
		ByteArrayInputStream decodedInput = null;

		try {

			// create invoice..
			stream = new ByteArrayOutputStream();

			final ArrayList<RowSchedule> final_list = new ArrayList<>();
			for (final RowSchedule item : this.list_rows_program) {

				final Schedule schedule = item.getItem_3().getSchedule();
				if (schedule == null) {
					continue;
				}

				final UserShift shift = this.shift_cache.getUserShift(schedule.getShift());

				if (!shift.getPresence().booleanValue()) {
					continue;
				}

				// check for worker people
				final Person person = this.personDAO.loadPerson(item.getUser());
				if ((person == null)) {
					continue;
				}

				final_list.add(item);

			}

			final Calendar cal = Calendar.getInstance();
			cal.setTime(cal.getTime());
			cal.add(Calendar.DATE, 1);
			final Date tomorrowDate = DateUtils.truncate(cal.getTime(), Calendar.DATE);

			final List<DetailScheduleShip> list_DetailScheduleShip = this.shipSchedulerDao.searchDetailScheduleShipByDateshit(tomorrowDate, null,
					null, null, null, null, null, null);

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

	private ArrayList<DetailFinalSchedule> filterDetailFinalScheduleByWorkingDay(final Boolean workingDayFilter) {
		final ArrayList<DetailFinalSchedule> newList = new ArrayList<>();
		for (final DetailFinalSchedule item : this.listDetailRevision) {
			if (Utility.isWorkingDay(item).equals(workingDayFilter)) {
				newList.add(item);
			}
		}
		return newList;
	}

	@Listen("onChange = #force_shift_combo;")
	public void forceProgramShift() {
		if (this.force_shift_combo.getSelectedItem() == null) {
			return;
		}
		final UserShift my_shift = this.force_shift_combo.getSelectedItem().getValue();

		if (my_shift.getPresence()) {
			this.define_program_body.setVisible(true);
		} else {
			this.define_program_body.setVisible(false);
		}
	}

	private final Integer getCountWorkingDay(final List<Schedule> scheduleList) {
		Integer countWorkingDay = 0;

		for (int i = 0; i < scheduleList.size(); i++) {
			final Schedule schedule = scheduleList.get(i);
			if ((schedule != null) && (schedule.getShift() != null)) {
				final UserShift shift = this.configurationDAO.loadShiftById(schedule.getShift());
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
	};

	private ArrayList<Date> getDateByMonth(final Integer month, final Integer year) {

		final ArrayList<Date> date = new ArrayList<>();

		final Calendar cal = Calendar.getInstance();

		cal.set(year, month - 1, 1);

		date.add(cal.getTime());

		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

		date.add(cal.getTime());

		return date;
	}

	private ArrayList<Date> getDateByWeek(final Integer week, final Integer year) {
		final ArrayList<Date> date = new ArrayList<>();

		final Calendar cal = Calendar.getInstance(Locale.ITALIAN);

		cal.set(Calendar.YEAR, year);

		cal.set(Calendar.WEEK_OF_YEAR, week);

		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

		date.add(cal.getTime());

		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		date.add(cal.getTime());

		return date;
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
	private final int getDayOfSchedule(final Schedule schedule) {

		final Date schedule_date = schedule.getDate_schedule();

		if (schedule_date == null) {
			// if not date scheduler, put it at first day
			schedule.setDate_schedule(this.firstDateInGrid);
			return 1;
		}

		final Date date_init_truncate = DateUtils.truncate(this.firstDateInGrid, Calendar.DATE);
		final Date schedule_date_truncate = DateUtils.truncate(schedule_date, Calendar.DATE);

		final long millis = schedule_date_truncate.getTime() - date_init_truncate.getTime();
		final double _day_avg = 86400000.0; // 1000 * 60 * 60 * 24
		final double day_elapsed = millis / _day_avg;

		return (int) (Math.round(day_elapsed) + 1);

	}

	private double getHoursInProgramList() {
		final List<DetailInitialSchedule> list = (List<DetailInitialSchedule>) this.listbox_program.getModel();
		Double countHours = 0.0;
		if (list != null) {
			for (final DetailInitialSchedule item : list) {
				if (item.getTime_vacation() == null) {
					item.setTime_vacation(0.0);
				}
				countHours = countHours + item.getTime() + item.getTime_vacation();
			}
		}
		return countHours;
	}

	private double getHoursInReviewList() {
		final List<DetailFinalSchedule> list = (List<DetailFinalSchedule>) this.listbox_review.getModel();
		Double countHours = 0.0;
		if (list != null) {
			for (final DetailFinalSchedule item : list) {
				countHours = countHours + item.getTime() + item.getTime_vacation();
			}
		}
		return countHours;
	}

	/**
	 * Get a new Item Row from a schedule
	 *
	 * @param schedule
	 */
	private final ItemRowSchedule getItemRowSchedule(final RowSchedule currentRow, final Integer day_on_current_calendar, final Schedule schedule,
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

		// get item row if null
		if (itemsRow == null) {
			itemsRow = new ItemRowSchedule(currentRow);
		}

		// set current schedule... in any case
		itemsRow.setSchedule(schedule);

		if (schedule.getId() != null) {
			if (schedule.getNo_shift() != null) {

				if (schedule.getNo_shift() == 1) {
					itemsRow.setAnchor1(this.defineAnchorContent(version_program, schedule));
					itemsRow.setAnchorValue1(this.defineAnchorContentValue(version_program, schedule));
				}

				if (schedule.getNo_shift() == 2) {
					itemsRow.setAnchor2(this.defineAnchorContent(version_program, schedule));
					itemsRow.setAnchorValue2(this.defineAnchorContentValue(version_program, schedule));
				}

				if (schedule.getNo_shift() == 3) {
					itemsRow.setAnchor3(this.defineAnchorContent(version_program, schedule));
					itemsRow.setAnchorValue3(this.defineAnchorContentValue(version_program, schedule));
				}

				if (schedule.getNo_shift() == 4) {
					itemsRow.setAnchor4(this.defineAnchorContent(version_program, schedule));
					itemsRow.setAnchorValue4(this.defineAnchorContentValue(version_program, schedule));
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
		final List<UserTask> list_task_user = this.taskDAO.loadTasksByUser(user);
		final List<UserTask> list_task_absence = this.configurationDAO.listAllAbsenceTask();
		final List<UserTask> list_task_justificatory = this.configurationDAO.listAllJustificatoryTask();

		// remove default task user
		for (final UserTask userTask : list_task_user) {

			for (int i = 0; i < list_task_absence.size(); i++) {
				final UserTask task = list_task_absence.get(i);
				if (task.getId().equals(userTask.getId())) {
					list_task_absence.remove(task);
				}
			}

			for (int i = 0; i < list_task_justificatory.size(); i++) {
				final UserTask task = list_task_justificatory.get(i);
				if (task.getId().equals(userTask.getId())) {
					list_task_justificatory.remove(task);
				}
			}

		}

		final List<UserTask> list = new ArrayList<>();
		list.addAll(list_task_user);
		list.addAll(list_task_justificatory);
		list.addAll(list_task_absence);

		return list;
	}

	/**
	 * Programmed time in decimal
	 *
	 * @return
	 */
	private final Double getProgrammedTime() {

		final boolean check_day_over = this.day_shift_over_control_program.isChecked();
		final boolean check_day_over_init = this.day_shift_over_control_program_init.isChecked();

		Date date_from = this.time_from_program.getValue();
		Date date_to = this.time_to_program.getValue();

		if ((this.selectedShift == 4) && check_day_over) {
			final Calendar calednar = DateUtils.toCalendar(date_to);
			calednar.add(Calendar.DAY_OF_YEAR, 1);
			date_to = calednar.getTime();
		}

		if ((this.selectedShift == 4) && check_day_over_init) {
			final Calendar calednar = DateUtils.toCalendar(date_from);
			calednar.add(Calendar.DAY_OF_YEAR, 1);
			date_from = calednar.getTime();
		}

		if ((date_from == null) || (date_to == null)) {
			return null;
		}

		final Date time_from_date = date_from;
		final Date time_to_date = date_to;
		if (time_from_date.after(time_to_date)) {
			return null;
		}

		final Double ret = this.getTimeDifference(date_from, date_to);

		return ret;

	}

	/**
	 * Return decimal revision time
	 *
	 * @return
	 */
	private final Double getRevisionTime() {

		final boolean check_day_over = this.day_shift_over_control.isChecked();
		final boolean check_day_over_init = this.day_shift_over_control_init.isChecked();

		Date date_from = this.time_from.getValue();
		Date date_to = this.time_to.getValue();

		if ((this.selectedShift == 4) && check_day_over) {
			final Calendar calednar = DateUtils.toCalendar(date_to);
			calednar.add(Calendar.DAY_OF_YEAR, 1);
			date_to = calednar.getTime();
		}

		if ((this.selectedShift == 4) && check_day_over_init) {
			final Calendar calednar = DateUtils.toCalendar(date_from);
			calednar.add(Calendar.DAY_OF_YEAR, 1);
			date_from = calednar.getTime();
		}

		if ((date_from == null) || (date_to == null)) {
			return null;
		}

		final Double ret = this.getTimeDifference(date_from, date_to);

		return ret;
	}

	public Button getSave_note_preprocessing() {
		return this.save_note_preprocessing;
	}

	public Button getSave_note_program() {
		return this.save_note_program;
	}

	public Button getSave_note_review() {
		return this.save_note_review;
	}

	// using for check 10 day working constraint
	private final List<Schedule> getScheduleTenDayBefore(final Date dateBegin, final Integer idUser) {

		final Date my_pick_date = DateUtils.truncate(dateBegin, Calendar.DATE);

		// get begin date
		final Calendar calendar = DateUtils.toCalendar(my_pick_date);
		calendar.add(Calendar.DAY_OF_YEAR, -10);
		final Date start_date = calendar.getTime();

		return this.scheduleDAO.selectSchedulersForPreprocessingOnUserId(start_date, dateBegin, idUser);

	}

	public Label getTask_cont() {
		return this.task_cont;
	}

	/**
	 * Get Time differences
	 *
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	private Double getTimeDifference(final Date date_from, final Date date_to) {
		final Date time_from_date = DateUtils.truncate(date_from, Calendar.MINUTE);
		final Date time_to_date = DateUtils.truncate(date_to, Calendar.MINUTE);
		if (time_from_date.after(time_to_date)) {
			return null;
		}

		final Long long_time = time_to_date.getTime() - time_from_date.getTime();

		final Double millis = long_time.doubleValue();

		final Double ret = millis / (1000.0 * 60.0 * 60.0);

		return Utility.roundSix(ret);
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
		this.shifts_combo_select.setModel(new ListModelList<>(shifts));

		// set combo in period shift composer
		this.shift_period_combo.setModel(new ListModelList<>(shifts));

		// set info in force combo
		this.force_shift_combo.setModel(new ListModelList<>(shifts));

		// set in overview
		this.select_shifttype_overview.setModel(new ListModelList<>(shifts));

		// get the caches
		this.shift_cache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);
		this.task_cache = (ITaskCache) SpringUtil.getBean(BeansTag.TASK_CACHE);
		this.ship_cache = (IShipCache) SpringUtil.getBean(BeansTag.SHIP_CACHE);

		if ((view_version != null) && view_version.equals(SchedulerComposer.PRINT_PROGRAM)) {

			this.definingPrintableVersion();

		} else {
			this.scheduler_type_selector.setSelectedItem(SchedulerComposer.this.preprocessing_item);
		}

		// set view about person viewer
		if (this.person_logged.isViewerOnly()) {
			this.scheduler_type_selector.setSelectedItem(this.program_item);
		}

		// check and set if table is locked
		this.checkIfTableIsLockedAndSetButton();

		final Comboitem version_selected = this.scheduler_type_selector.getSelectedItem();
		LockTable lockTable = null;
		if ((version_selected == this.preprocessing_item) || (version_selected == this.program_item)) {
			lockTable = this.lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE);
		} else if (version_selected == this.review_item) {
			lockTable = this.lockTableDAO.loadLockTableByTableType(TableTag.REVIEW_TABLE);
		}

		// check if you are admin or you are the user that have locked
		// the table
		if (!this.person_logged.isAdministrator()) {
			this.disableWriteCancelButtons(true);
		} else {
			this.disableWriteCancelButtons(false);
		}
		final int idLogged = this.person_logged.getId();

		if ((lockTable != null) && (lockTable.getId_user() == idLogged)) {
			SchedulerComposer.this.disableWriteCancelButtons(false);
		}

		this.defineViewCurrentWorkInOverview();

		this.defineSchedulerView();
	}

	@Listen("onClick = #sw_link_edit_comp")
	public void modCompenstion() {

		if (this.sw_compensation_list.getSelectedItem() == null) {
			return;
		}

		final UserCompensation user_comp = this.sw_compensation_list.getSelectedItem().getValue();

		this.date_submitUser.setValue(user_comp.getDate_submit());
		this.time_compUser.setValue(user_comp.getTime_comp());
		this.note_compUser.setValue(user_comp.getNote());

		this.status_comp_editor = SchedulerComposer.STATUS_COMP_EDITOR_EDIT;

		this.comp_editor.setVisible(true);
	}

	@Listen("onChange = #scheduler_type_selector")
	public void onChangeSelectedVersion() {
		if (this.scheduler_type_selector.getSelectedItem() == null) {
			return;
		}
		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (!selected.equals(this.overview_item)) {
			this.checkIfTableIsLockedAndSetButton();
			this.loggerUserOnTable.setVisible(true);
			// this.switchButton.setVisible(true);
		} else {
			this.loggerUserOnTable.setVisible(false);
			this.switchButton.setVisible(false);
		}

	}

	@Listen("onClick = #overview_month")
	public void onSelectMonthOverview() {

		this.select_year.setSelectedItem(null);
		this.select_month.setSelectedItem(null);
		this.select_week.setSelectedItem(null);

		this.defineViewCurrentWorkInOverview();

		this.defineSchedulerView();

	}

	/**
	 * Shift configurator
	 *
	 * @param data_info
	 */
	private void onShiftClickProgram(final String data_info) {

		if (this.grid_scheduler.getSelectedItem() == null) {
			return;
		}

		final RowSchedule row_scheduler = this.grid_scheduler.getSelectedItem().getValue();

		// for of shift --> shift_1_4
		final String[] info = data_info.split("_");
		if (info.length != 3) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Check Scheduler ZUL Strucutre. Contact Uario S.r.L.", "ERROR", buttons, null, Messagebox.ERROR, null, null, params);

			return;
		}

		// info check
		if (!NumberUtils.isNumber(info[1]) || !NumberUtils.isNumber(info[2])) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Check Scheduler ZUL Strucutre. Contact Uario S.r.L.", "ERROR", buttons, null, Messagebox.ERROR, null, null, params);
			return;
		}

		this.selectedDay = Integer.parseInt(info[1]);
		this.selectedShift = Integer.parseInt(info[2]);
		this.setDefaultValueTimeInPopupReview(this.selectedShift, this.time_from_program, this.time_to_program);
		final Integer user = row_scheduler.getUser();
		this.selectedUser = user;

		// show day over
		if (this.selectedShift != 4) {
			this.day_shift_over_program.setVisible(false);
		} else {
			this.day_shift_over_program.setVisible(true);
		}

		final Date date_schedule = this.getDateScheduled(SchedulerComposer.this.selectedDay);

		// take the right scheduler
		this.currentSchedule = this.scheduleDAO.loadSchedule(date_schedule, this.selectedUser);

		// set label
		if (this.personDAO.loadPerson(this.selectedUser).getPart_time()) {

			final String msg = row_scheduler.getName_user() + " " + this.partTimeMessage + ". Giorno: "
					+ SchedulerComposer.formatter_scheduler_info.format(date_schedule) + ". Turno: " + SchedulerComposer.this.selectedShift;

			this.scheduler_label.setLabel(msg);

		} else {

			final String msg = row_scheduler.getName_user() + ". Giorno: " + SchedulerComposer.formatter_scheduler_info.format(date_schedule)
					+ ". Turno: " + SchedulerComposer.this.selectedShift;

			this.scheduler_label.setLabel(msg);
		}

		// show programmer and controller
		this.editor_label.setLabel("");
		this.controller_label.setLabel("");

		if (this.currentSchedule != null) {

			if (this.currentSchedule.getEditor() != null) {
				final Person editor = this.personDAO.loadPerson(this.currentSchedule.getEditor());
				if (editor != null) {
					this.editor_label.setLabel("Programmatore: " + editor.getFirstname() + " " + editor.getLastname());
				}
			}

			if (this.currentSchedule.getController() != null) {
				final Person controller = this.personDAO.loadPerson(this.currentSchedule.getController());
				if (controller != null) {
					this.controller_label.setLabel("Controllore: " + controller.getFirstname() + " " + controller.getLastname());
				}
			}

		}

		// reset editor tools
		this.force_shift_combo.setValue(null);

		// set initial behavior for forceable
		this.div_force_shift.setVisible(false);
		this.define_program_body.setVisible(true);

		// set info abount standard work (if any exists)
		if (this.shift_cache.getStandardWorkShift() != null) {
			this.label_date_shift_program.setLabel(this.shift_cache.getStandardWorkShift().toString());
		} else {
			this.label_date_shift_program.setLabel(null);
		}

		// set label current shift
		if (this.currentSchedule != null) {

			final UserShift myshift = this.shift_cache.getUserShift(this.currentSchedule.getShift());

			if (myshift != null) {
				this.label_date_shift_program.setLabel(myshift.toString());

				// define fearceble behavior
				if (myshift.getForceable().booleanValue()) {
					this.div_force_shift.setVisible(true);
					this.define_program_body.setVisible(false);
				}

			}

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
			this.list_details_program = new ArrayList<>();

		}

		// set model list program and revision
		final ListModelList<DetailInitialSchedule> model = new ListModelList<>(this.list_details_program);
		model.setMultiple(true);
		this.listbox_program.setModel(model);

		// set Label count hours added in listbox_progra
		this.setLabelTotalHoursProgram(model);

		// set combo task
		final List<UserTask> list = this.getListTaskForComboPopup(user);

		this.program_task.setSelectedItem(null);
		this.program_task.getChildren().clear();

		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());
			if (task_item.getIsabsence()) {
				combo_item.setStyle("color: " + TaskColor.ANBSENCE_COLOR);
			} else if (task_item.getJustificatory()) {
				combo_item.setStyle("color: " + TaskColor.JUSTIFICATORY_COLOR);
			}
			this.program_task.appendChild(combo_item);

			// set if default
			if (task_item.getTask_default()) {
				this.program_task.setSelectedItem(combo_item);
			}

		}

		// task
		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());

		}

		// open popup
		this.minHoursAlert = false;
		this.alertMinHours.setVisible(false);
		this.errorMessageAddProgramItem.setValue("");

		this.continue_shiftProgram.setChecked(false);

		this.shift_definition_popup.open(this.program_div, "after_pointer");

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

		// set null combobox all ship, ship in day and board
		this.ship.setSelectedItem(null);
		this.shipInDay.setSelectedItem(null);
		this.board.setSelectedItem(null);
		// set null selected ship
		this.shipSelected = null;
		// set null crane value
		this.crane.setValue("");

		// load ship in program for selected day and set model in combobox
		final List<Ship> listShipInDay = this.scheduleShipDAO.loadShipInDate(new Timestamp(date_to_configure.getTime()));
		this.shipInDay.setModel(new ListModelList<Ship>());

		if (listShipInDay != null) {
			// add empty ship
			final Ship ship = new Ship();
			ship.setId(-1);
			ship.setName("--");
			listShipInDay.add(0, ship);
			// set model in shipInDay combobox
			this.shipInDay.setModel(new ListModelList<>(listShipInDay));
		}

		this.minHoursAlert = false;
		this.alertMinHoursReview.setVisible(false);
		this.errorMessageAddItem.setValue("");
		this.shift_definition_popup_review.open(this.review_div, "after_pointer");
		this.reviewShift.setChecked(false);
		this.continue_shift.setChecked(false);

		if (SchedulerComposer.this.grid_scheduler_review.getSelectedItem() == null) {
			return;
		}

		final RowSchedule row_scheduler = SchedulerComposer.this.grid_scheduler_review.getSelectedItem().getValue();

		// for of shift --> shift_1_4
		final String[] info = data_info.split("_");
		if (info.length != 3) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Check Scheduler ZUL Strucutre. Contact Uario S.r.L.", "ERROR", buttons, null, Messagebox.ERROR, null, null, params);

			return;
		}

		// info check
		if (!NumberUtils.isNumber(info[1]) || !NumberUtils.isNumber(info[2])) {

			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Check Scheduler ZUL Strucutre. Contact Uario S.r.L.", "ERROR", buttons, null, Messagebox.ERROR, null, null, params);

			return;
		}

		this.selectedDay = Integer.parseInt(info[1]);
		this.selectedShift = Integer.parseInt(info[2]);

		// set default value of time_from and time_to in popup
		this.setDefaultValueTimeInPopupReview(this.selectedShift, this.time_from, this.time_to);

		final Integer user = row_scheduler.getUser();
		this.selectedUser = user;

		// show day over
		if (this.selectedShift != 4) {
			this.day_shift_over.setVisible(false);
		} else {
			this.day_shift_over.setVisible(true);
		}

		final Date date_schedule = DateUtils.truncate(date_to_configure, Calendar.DATE);

		// take the right scheduler
		SchedulerComposer.this.currentSchedule = this.scheduleDAO.loadSchedule(date_schedule, this.selectedUser);

		if (this.currentSchedule != null) {
			// set label list badge
			this.setLabelListBadge(this.currentSchedule.getId());
		}

		// set label
		if (this.personDAO.loadPerson(this.selectedUser).getPart_time()) {
			this.scheduler_label_review.setLabel(row_scheduler.getName_user() + " " + this.partTimeMessage + ". Giorno: "
					+ SchedulerComposer.formatter_scheduler_info.format(date_schedule) + ". Turno: " + SchedulerComposer.this.selectedShift);

		} else {
			this.scheduler_label_review.setLabel(row_scheduler.getName_user() + ". Giorno: "
					+ SchedulerComposer.formatter_scheduler_info.format(date_schedule) + ". Turno: " + SchedulerComposer.this.selectedShift);
		}

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
			this.continue_shift.setChecked(false);
			this.listbox_review.getItems().clear();

			// set list revision
			this.list_details_review = new ArrayList<>();

		}

		// set model list program and revision
		final ListModelList<DetailFinalSchedule> model = new ListModelList<>(this.list_details_review);
		model.setMultiple(true);
		this.listbox_review.setModel(model);

		this.setLabelTotalHoursReview(model);

		// set combo task
		final List<UserTask> list = this.getListTaskForComboPopup(user);

		this.review_task.setSelectedItem(null);
		this.review_task.getChildren().clear();

		for (final UserTask task_item : list) {
			final Comboitem combo_item = new Comboitem();
			combo_item.setValue(task_item);
			combo_item.setLabel(task_item.toString());
			if (task_item.getIsabsence()) {
				combo_item.setStyle("color: " + TaskColor.ANBSENCE_COLOR);
			} else if (task_item.getJustificatory()) {
				combo_item.setStyle("color: " + TaskColor.JUSTIFICATORY_COLOR);
			}
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

		// show programmer and controller
		this.editor_label_review.setLabel("");
		this.controller_label_review.setLabel("");

		if (this.currentSchedule != null) {

			if (this.currentSchedule.getEditor() != null) {
				final Person editor = this.personDAO.loadPerson(this.currentSchedule.getEditor());
				if (editor != null) {
					this.editor_label_review.setLabel("Programmatore: " + editor.getFirstname() + " " + editor.getLastname());
				}
			}

			if (this.currentSchedule.getController() != null) {
				final Person controller = this.personDAO.loadPerson(this.currentSchedule.getController());
				if (controller != null) {
					this.controller_label_review.setLabel("Controllore: " + controller.getFirstname() + " " + controller.getLastname());
				}
			}

			// check or unchek sync_mobile checkbox
			if (this.selectedShift.equals(1)) {
				this.sync_schedule.setChecked(this.currentSchedule.getSync_mobile_1());
			}
			if (this.selectedShift.equals(2)) {
				this.sync_schedule.setChecked(this.currentSchedule.getSync_mobile_2());
			}
			if (this.selectedShift.equals(3)) {
				this.sync_schedule.setChecked(this.currentSchedule.getSync_mobile_3());
			}
			if (this.selectedShift.equals(4)) {
				this.sync_schedule.setChecked(this.currentSchedule.getSync_mobile_4());
			}

		}

	}

	private void orderListDetailFinalScheduleByTimeFrom(final List<DetailFinalSchedule> list) {
		list.sort(new Comparator<DetailFinalSchedule>() {

			@Override
			public int compare(final DetailFinalSchedule o1, final DetailFinalSchedule o2) {
				if ((o1 == null) || (o2 == null)) {
					return -1;
				}

				if ((o1.getTime_from() != null) && (o2.getTime_from() != null)) {
					return o1.getTime_from().compareTo(o2.getTime_from());
				}

				return -1;
			}
		});
	}

	private void orderListDetailInitialScheduleByTimeFrom(final List<DetailInitialSchedule> list) {
		list.sort(new Comparator<DetailInitialSchedule>() {

			@Override
			public int compare(final DetailInitialSchedule o1, final DetailInitialSchedule o2) {
				if ((o1 == null) || (o2 == null)) {
					return -1;
				}

				if ((o1.getTime_from() != null) && (o2.getTime_from() != null)) {
					return o1.getTime_from().compareTo(o2.getTime_from());
				}

				return -1;
			}
		});
	}

	@Listen("onClick = #refresh_command")
	public void refreshCommand() {

		this.select_shift_overview.setSelectedItem(this.item_all_shift_overview);
		this.resetFilter();
		// refresh for overview
		this.full_text_search.setValue(null);
		this.date_from_overview.setValue(null);
		this.date_to_overview.setValue(null);

		// set selected item
		this.combo_user_dip.setSelectedItem(null);

		this.defineSchedulerView();

	}

	/**
	 * Refresh statistic
	 *
	 * @param text
	 */
	private void refreshUserStatistics(final String text, final String department) {

		// select date period
		Date date_from = this.date_from_overview.getValue();
		Date date_to = this.date_to_overview.getValue();

		// data checking
		if ((date_from == null) || (date_to == null)) {
			return;
		}
		if (date_from.after(date_to)) {
			return;
		}

		// truncate
		date_from = DateUtils.truncate(date_from, Calendar.DATE);
		date_to = DateUtils.truncate(date_to, Calendar.DATE);

		String department_select = null;
		if ((department != null) && !department.equals("")) {
			department_select = department;
		}

		String text_select = null;
		if ((text != null) && !text.equals("")) {
			text_select = text;
		}

		// get worker user
		final List<Person> users_schedule = this.personDAO.listWorkerPersons(text_select, department_select);

		// init list
		this.listUserStatistics = new ListModelList<>();

		final Date date_to_stat = new Date(date_to.getTime());

		for (final Person person : users_schedule) {

			UserStatistics user = null;

			user = this.statProcedure.getUserStatistics(person, date_from, date_to_stat, false);

			user.setPerson(person);

			this.listUserStatistics.add(user);
		}

		Collections.sort(this.listUserStatistics);

		this.list_statistics.setModel(new ListModelList<>(this.listUserStatistics));

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.list_statistics.setPageSize(this.shows_rows.getValue());
		}

		// define msg
		final Calendar cal = Calendar.getInstance();
		final String tstamp = Utility.convertToDateAndTime(cal.getTime());
		final String msg = "Statistica aggiornata a: " + tstamp + " riferito al periodo " + Utility.getDataAsString_it(date_from) + " - "
				+ Utility.getDataAsString_it(date_to);
		this.updateStatisticTime.setValue(msg);

		// defining counter
		double count_h = 0;
		double sum_saturation = 0;

		for (final UserStatistics statistic : this.listUserStatistics) {

			// check department
			if (department != null) {
				if (!department.equals(statistic.getDepartment())) {
					continue;
				}
			}

			if (statistic.getWork_current() != null) {

				try {

					count_h += Double.parseDouble(statistic.getWork_current());

				} catch (final Exception e) {
				}

			}

			if (statistic.getSaturation() != null) {
				sum_saturation = sum_saturation + statistic.getSaturation();
			}

		}

		this.overview_count_h_stat.setValue("" + Utility.roundTwo(count_h));
		this.overview_sum_saturation.setValue("" + Utility.roundTwo(sum_saturation));
		this.overview_count_worker_stat.setValue("" + this.listUserStatistics.size());

	}

	@Listen("onClick = #cancel_day_definition")
	public void removeDayConfiguration() {

		if (!this.checkConnection()) {
			return;
		}

		if ((this.selectedDay == null)) {
			return;
		}

		if (this.grid_scheduler_day == null) {
			return;
		}

		final Date dayScheduleDate = this.getDateScheduled(this.selectedDay);

		// check data start point
		if (dayScheduleDate == null) {
			return;
		}

		// get row item
		final RowDaySchedule row_item = this.grid_scheduler_day.getSelectedItem().getValue();

		// info to the users
		final UserShift shiftStandard = this.shift_cache.getStandardWorkShift();

		// check for day after tomorrow...remove means assign standard
		// work
		final Calendar tomorrow_cal = Calendar.getInstance();
		tomorrow_cal.add(Calendar.DATE, 1);
		final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// check if is a multiple removing
		final Date dayAfterConfig = this.day_after_config.getValue();

		if (dayAfterConfig != null) {
			// is multiple removing

			final Date to_day = DateUtils.truncate(dayAfterConfig, Calendar.DATE);

			if (dayScheduleDate.after(to_day)) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Attenzione alla data inserita", "ERROR", buttons, null, Messagebox.EXCLAMATION, null, null, params);

				return;
			}

			final int count = (int) ((to_day.getTime() - dayScheduleDate.getTime()) / (1000 * 60 * 60 * 24));

			if ((this.selectedDay + count) > SchedulerComposer.DAYS_IN_GRID_PREPROCESSING) {

				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show(
						"Non cancellare oltre i limiti della griglia corrente. Usa Imposta Speciale per azioni su intervalli che vanno otlre la griglia corrente.",
						"ERROR", buttons, null, Messagebox.EXCLAMATION, null, null, params);

				return;
			}

			// remove day schedule in interval date
			this.scheduleDAO.removeScheduleUser(row_item.getUser(), dayScheduleDate, dayAfterConfig);

			// check for info worker for tomorrow
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

			// is a single removing
			this.scheduleDAO.removeScheduleUser(row_item.getUser(), dayScheduleDate, dayScheduleDate);

			// check for tomorrow
			if (DateUtils.isSameDay(tomorrow_cal.getTime(), dayScheduleDate)) {
				final Schedule schedule = new Schedule();
				this.assignShiftForDaySchedule(shiftStandard, tomorrow_cal.getTime(), row_item.getUser(), schedule, person_logged, null);
			}

			// check if a break shift is removing
			final Schedule current_schedule = row_item.getSchedule(this.selectedDay);

			if ((current_schedule != null) && (current_schedule.getShift() != null)) {

				final Integer shift_type_id = current_schedule.getShift();
				final UserShift shift_type = this.shift_cache.getUserShift(shift_type_id);

				if (shift_type.getBreak_shift().booleanValue() || shift_type.getWaitbreak_shift().booleanValue()) {

					// get local variable
					final Date date_schedule = current_schedule.getDate_schedule();

					// check if the command is available
					final boolean check = this.checkForPreprocessingCommand(date_schedule);
					if (check) {

						final List<Schedule> list_break = this.statProcedure.searchBreakInCurrentWeek(date_schedule, current_schedule.getUser());

						if (list_break == null) {

							// check for sunday
							final Calendar check_sunday_cal = DateUtils.toCalendar(date_schedule);
							if (DateUtils.isSameDay(Calendar.getInstance(), check_sunday_cal)
									&& (check_sunday_cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {

								final Map<String, String> params = new HashMap<>();
								params.put("sclass", "mybutton Button");
								final Messagebox.Button[] buttons = new Messagebox.Button[1];
								buttons[0] = Messagebox.Button.OK;

								final String msg = "Non ci sono più riposi per questa settimana. Programma con attenzione la settimana successiva.";
								Messagebox.show(msg, "GESTIONE RIPOSI", buttons, null, Messagebox.EXCLAMATION, null, null, params);

							} else {

								final Map<String, String> params = new HashMap<>();
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
			}

		}

		// refresh grid
		this.setupGlobalSchedulerGridForDay();

		this.day_definition_popup.close();
	}

	@Listen("onClick = #cancel_program")
	public void removeProgram() {
		if (!this.checkConnection()) {
			return;
		}

		this.errorMessageAddProgramItem.setValue(null);

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

		this.errorMessageAddProgramItem.setValue("");

		if (!this.checkConnection()) {
			return;
		}

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
		final ListModelList<DetailInitialSchedule> model = new ListModelList<>(this.list_details_program);
		model.setMultiple(true);
		this.listbox_program.setModel(model);

		this.setLabelTotalHoursProgram(model);

	}

	@Listen("onClick = #cancel_review")
	public void removeReview() {

		this.errorMessageAddItem.setValue("");
		this.alertMinHoursReview.setVisible(false);

		if (!this.checkConnection()) {
			return;
		}

		if ((this.selectedDay == null) || (this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.list_details_review == null) {
			return;
		}

		if (this.currentSchedule != null) {
			this.scheduleDAO.removeAllDetailFinalScheduleByScheduleAndShift(this.currentSchedule.getId(), this.selectedShift);

			this.scheduleDAO.updateMobileSynch(this.currentSchedule.getId(), false, this.selectedShift);

			// refresh grid
			this.setupGlobalSchedulerGridForShiftReview();

		}

		// Messagebox.show("Il consuntivo è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.shift_definition_popup_review.close();
	}

	@Listen("onClick = #remove_review_item")
	public void removeReviewItem() {

		this.errorMessageAddItem.setValue("");
		this.alertMinHoursReview.setVisible(false);

		if (!this.checkConnection()) {
			return;
		}

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
		final ListModelList<DetailFinalSchedule> model = new ListModelList<>(this.list_details_review);
		model.setMultiple(true);
		this.listbox_review.setModel(model);

		this.setLabelTotalHoursReview(model);

	}

	@Listen("onClick= #repogram_users")
	public void reprogramUser() {

		if (!this.checkConnection()) {
			return;
		}

		if ((this.grid_scheduler.getSelectedItems() == null) || (this.grid_scheduler.getSelectedItems().size() == 0)) {
			return;
		}

		// get date tomorrow
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		final Date date_tomorrow = DateUtils.truncate(cal.getTime(), Calendar.DATE);

		for (final Listitem item : this.grid_scheduler.getSelectedItems()) {

			final RowSchedule itm_row = item.getValue();
			if ((itm_row.getUser() == null) || (itm_row.getItem_3() == null)) {
				continue;
			}

			// get Person
			final Person person = this.personDAO.loadPerson(itm_row.getUser());

			Schedule schedule = null;

			if (itm_row.getItem_3().getSchedule() == null) {
				this.scheduleDAO.loadSchedule(date_tomorrow, person.getId());
			} else {
				schedule = itm_row.getItem_3().getSchedule();
			}

			if (schedule.getShift() == null) {
				continue;
			}

			final UserShift shift = this.shift_cache.getUserShift(schedule.getShift());
			if (!shift.getPresence().booleanValue()) {
				continue;
			}

			this.statProcedure.workAssignProcedure(shift, date_tomorrow, person.getId(), null);

		}

		// upload grid
		this.setupGlobalSchedulerGridForShift();

	}

	private void resetFilter() {

		this.select_shift_overview.setSelectedItem(null);
		this.select_shifttype_overview.setSelectedItem(null);
		this.taskComboBox.setSelectedItem(null);
		this.hoursInterval.setSelectedItem(null);
		this.shipSelector.setSelectedItem(null);
		this.craneSelector.setValue(null);

	}

	@Listen("onClick= #save_note_preprocessing")
	public void save_note_preprocessing() {

		if ((this.note_preprocessing == null) || this.note_preprocessing.getValue().isEmpty()) {
			return;
		}

		this.updateNoteOnPopup(this.note_preprocessing.getValue());
	}

	@Listen("onClick= #save_note_program")
	public void save_note_program() {

		if ((this.note_program == null) || this.note_program.getValue().isEmpty()) {
			return;
		}

		this.updateNoteOnPopup(this.note_program.getValue());
	}

	@Listen("onClick= #save_note_review")
	public void save_note_review() {

		if ((this.note_review == null) || this.note_review.getValue().isEmpty()) {
			return;
		}

		this.updateNoteOnPopup(this.note_review.getValue());
	}

	/**
	 * Save Current scheduler updating values from grid
	 */
	private void saveCurrentSchedulerProgram() {

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

	/**
	 * Save Current scheduler updating values from grid
	 */
	private void saveCurrentSchedulerReview() {

		if (this.currentSchedule == null) {
			this.currentSchedule = new Schedule();
		}

		// set data scheduler
		final Date date_schedule = this.getDateScheduled(this.selectedDay);
		this.currentSchedule.setDate_schedule(date_schedule);

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
		if (!this.checkConnection()) {
			return;
		}

		if (this.grid_scheduler_day.getSelectedItem() == null) {
			return;
		}

		if (this.shifts_combo_select.getSelectedItem() == null) {
			return;
		}

		if (this.selectedDay == null) {
			return;
		}

		final RowDaySchedule row_item = this.grid_scheduler_day.getSelectedItem().getValue();

		if (!(this.shifts_combo_select.getSelectedItem().getValue() instanceof UserShift)
				|| (this.shifts_combo_select.getSelectedItem().getValue() == null)) {
			return;
		}

		// get shift
		final UserShift shift = this.shifts_combo_select.getSelectedItem().getValue();

		// get day schedule
		final Date date_scheduled = this.getDateScheduled(this.selectedDay);

		// check for 10 day of work constraint:
		if (shift.getPresence()) {
			final List<Schedule> scheduleList = this.getScheduleTenDayBefore(date_scheduled, row_item.getUser());
			Integer lenght;
			if (scheduleList == null) {
				lenght = 10;
			} else {
				lenght = this.getCountWorkingDay(scheduleList);
			}

			if (lenght == 10) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");

				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.CANCEL;

				Messagebox.show("Serie lavorativa superiore a 10 giorni. Sicuro di voler assegnare un turno di lavoro?", "CONFERMA INSERIMENTO",
						buttons, null, Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>() {

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
				this.saveShift(shift, date_scheduled, row_item);
			}

		} else {
			this.saveShift(shift, date_scheduled, row_item);
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

		if ((scheduleListInWeek != null) && (this.shift_cache.getDailyShift() != null) && (this.shift_cache.getStandardWorkShift() != null)) {

			// replace break shift with standard or, if user is a daily worker,
			// daily shift

			final Boolean isDailyWorker = this.personDAO.loadPerson(row_item.getUser()).getDailyemployee();

			final Integer id_standardShift = this.shift_cache.getStandardWorkShift().getId();
			final Integer id_dailyShift = this.shift_cache.getDailyShift().getId();

			final Date to_day = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);

			for (final Schedule schedule : scheduleListInWeek) {

				if (!schedule.getDate_schedule().after(to_day)) {
					continue;
				}

				final UserShift userShift = this.configurationDAO.loadShiftById(schedule.getShift());

				if (userShift.getBreak_shift() || userShift.getWaitbreak_shift() || userShift.getDisease_shift() || userShift.getAccident_shift()) {

					if (isDailyWorker) {
						schedule.setShift(id_dailyShift);
						this.scheduleDAO.saveOrUpdateSchedule(schedule);
					} else {
						schedule.setShift(id_standardShift);
						this.scheduleDAO.saveOrUpdateSchedule(schedule);
					}
				}
			}

		}
		if (this.day_after_config.getValue() == null) {

			// set only current day
			this.assignShiftFromDaySchedule(row_item, shift, 0, true);

		} else {

			if (shift.getBreak_shift().booleanValue() || shift.getWaitbreak_shift().booleanValue()) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Non puoi usare il turno di riposo programmato o atteso per assegnazioni multiple.", "ERROR", buttons, null,
						Messagebox.EXCLAMATION, null, null, params);
				return;
			}

			// set multiple day..... check date before...

			final Date to_day = DateUtils.truncate(this.day_after_config.getValue(), Calendar.DATE);

			if (date_scheduled.after(to_day)) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Attenzione alla data inserita", "ERROR", buttons, null, Messagebox.EXCLAMATION, null, null, params);
				return;
			}

			final int count = (int) ((to_day.getTime() - date_scheduled.getTime()) / (1000 * 60 * 60 * 24));

			if ((this.selectedDay + count) > SchedulerComposer.DAYS_IN_GRID_PREPROCESSING) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Non puoi programmare oltre i limiti della griglia corrente. Usa Imposta Speciale ", "ERROR", buttons, null,
						Messagebox.EXCLAMATION, null, null, params);

				return;
			}

			if (count == 0) {
				// single..
				this.assignShiftFromDaySchedule(row_item, shift, 0, true);
			}

			// check day - multiple
			for (int i = 0; i <= count; i++) {

				// set day with offest i
				this.assignShiftFromDaySchedule(row_item, shift, i, false);
			}

		}

		this.setupGlobalSchedulerGridForDay();

		this.day_definition_popup.close();

	}

	/**
	 * Save program
	 */
	@Listen("onClick = #ok_program")
	public void saveProgram() {

		if (!this.checkConnection()) {
			return;
		}

		this.errorMessageAddProgramItem.setValue("");

		// check if time programmed if <= 6 hours
		final Double timeProgrammed = this.getHoursInProgramList();

		if ((timeProgrammed < 6) && !this.minHoursAlert) {
			this.minHoursAlert = true;
			this.alertMinHours.setVisible(true);
			return;
		}

		if ((this.selectedDay == null) || (this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.list_details_program == null) {
			return;
		}

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentSchedulerProgram();
		}

		if (this.div_force_shift.isVisible() && (this.force_shift_combo.getSelectedItem() != null)) {

			// assign shift ---- FORCE
			final UserShift my_shift = this.force_shift_combo.getSelectedItem().getValue();
			this.statProcedure.shiftAssign(my_shift, this.currentSchedule.getDate_schedule(), this.currentSchedule.getUser(),
					this.currentSchedule.getEditor());

			// refresh current schedule
			this.currentSchedule = this.scheduleDAO.loadSchedule(this.currentSchedule.getDate_schedule(), this.currentSchedule.getUser());
		}

		if (this.define_program_body.isVisible()) {

			// save note:
			final String note = this.note_program.getValue();
			this.currentSchedule.setNote(note);
			this.saveCurrentSchedulerProgram();

			// check about sum of time
			Double sum = 0.0;
			if (this.list_details_program.size() != 0) {
				for (final DetailInitialSchedule detail : this.list_details_program) {
					sum = sum + detail.getTime();
				}
			}

			// check max 12 h in a day
			final List<DetailInitialSchedule> list_detail_schedule = this.scheduleDAO
					.loadDetailInitialScheduleByIdSchedule(this.currentSchedule.getId());

			Double count = sum;
			for (final DetailInitialSchedule dt : list_detail_schedule) {
				if (dt.getShift() != this.selectedShift) {
					count = count + dt.getTime();

					if (Utility.roundTwo(count) > 12) {
						break;
					}
				}

			}

			if (Utility.roundTwo(count) > 12) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Non si possono assegnare più di 12 ore al giorno", "ERROR", buttons, null, Messagebox.EXCLAMATION, null, null,
						params);

				return;

			}

			// check for 12h constraints
			if (sum != 0.0) {

				// check between different day
				final Integer min_shift = this.statProcedure.getMinimumShift(this.currentSchedule.getDate_schedule(), this.currentSchedule.getUser());
				final Integer max_shift = this.statProcedure.getMaximumShift(this.currentSchedule.getDate_schedule(), this.currentSchedule.getUser());

				boolean check_12_different_day = (this.selectedShift.compareTo(min_shift) < 0) || (this.selectedShift.compareTo(max_shift) > 0);

				if (!check_12_different_day) {
					// check in same day
					final Integer minShiftInDay = this.statProcedure.getFirstShiftInDay(this.currentSchedule.getDate_schedule(),
							this.currentSchedule.getUser());
					final Integer maxShiftInDay = this.statProcedure.getLastShiftInDay(this.currentSchedule.getDate_schedule(),
							this.currentSchedule.getUser());
					if ((minShiftInDay != null) && (maxShiftInDay != null) && !minShiftInDay.equals(this.selectedShift)
							&& !maxShiftInDay.equals(this.selectedShift)) {
						if (!((this.selectedShift.equals(1) && minShiftInDay.equals(4)) || (this.selectedShift.equals(4) && minShiftInDay.equals(1))
								|| (this.selectedShift.equals(2) && minShiftInDay.equals(3))
								|| (this.selectedShift.equals(3) && minShiftInDay.equals(2))
								|| (this.selectedShift.equals(3) && minShiftInDay.equals(4))
								|| (this.selectedShift.equals(4) && minShiftInDay.equals(3)))) {
							check_12_different_day = true;
						}
					}
				}

				if (check_12_different_day) {

					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");

					final Messagebox.Button[] buttons = new Messagebox.Button[2];
					buttons[0] = Messagebox.Button.OK;
					buttons[1] = Messagebox.Button.CANCEL;

					final String msg = "Stai assegnando un turno prima di 12 ore di stacco. Sei sicuro di voler continuare?";
					Messagebox.show(msg, "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.EXCLAMATION, null, new CheckOnDoubleShiftBreaEvent(),
							params);

					return;

				}

			}

			// final step saving program
			this.saveProgramFinalStep();
		}

		// close popup
		this.shift_definition_popup.close();

	}

	private void saveProgramFinalStep() {
		// save details
		this.scheduleDAO.saveListDetailInitialScheduler(this.currentSchedule.getId(), this.selectedShift, this.list_details_program);

		// refresh grid
		this.setupGlobalSchedulerGridForShift();
	}

	@Listen("onClick = #save_program_item")
	public void saveProgramItem() {

		this.errorMessageAddProgramItem.setValue("");

		final Integer selectedIndex = this.listbox_program.getSelectedIndex();

		this.save_program_item.setVisible(false);
		this.cancel_modify_program.setVisible(false);
		this.add_program_item.setVisible(true);
		this.remove_program_item.setVisible(true);

		if (this.listbox_program.getSelectedItem() == null) {
			return;
		}

		final DetailInitialSchedule itm = this.listbox_program.getSelectedItem().getValue();

		this.removeProgramItem();

		if (!this.addProgramItem()) {
			this.list_details_program.add(itm);
			final ListModelList<DetailInitialSchedule> model = new ListModelList<>(this.list_details_program);
			this.orderListDetailInitialScheduleByTimeFrom(model);
			this.listbox_program.setModel(model);

			this.listbox_program.setSelectedIndex(selectedIndex);

			this.save_program_item.setVisible(true);
			this.cancel_modify_program.setVisible(true);
			this.add_program_item.setVisible(false);
			this.remove_program_item.setVisible(false);

		}

		this.continue_shiftProgram.setChecked(false);

	}

	/**
	 * Save review
	 */
	@Listen("onClick = #ok_review")
	public void saveReview() {

		this.errorMessageAddItem.setValue("");
		this.alertMinHoursReview.setVisible(false);

		if (!this.checkConnection()) {
			return;
		}

		// check if time programmed if <= 6 hours
		final Double timeReviewed = this.getHoursInReviewList();

		if ((timeReviewed < 6) && !this.minHoursAlert) {
			this.minHoursAlert = true;
			this.alertMinHoursReview.setVisible(true);
			return;
		}

		if ((this.selectedShift == null) || (this.selectedUser == null)) {
			return;
		}

		if (this.list_details_review == null) {
			return;
		}

		if (this.currentSchedule == null) {
			// save scheduler
			this.saveCurrentSchedulerReview();
		}

		// save note and controller:
		final Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final String note = this.note_review.getValue();
		this.currentSchedule.setNote(note);
		this.currentSchedule.setController(person.getId());
		this.saveCurrentSchedulerReview();

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
			if (dt.getShift() != this.selectedShift) {
				count = count + dt.getTime();

				if (Utility.roundTwo(count) > 12) {
					break;
				}
			}

		}

		if (Utility.roundTwo(count) > 12) {

			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Non si possono assegnare più di 12 ore al giorno", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null,
					params);
			return;

		}

		// save details
		this.scheduleDAO.saveListDetailFinalScheduler(this.currentSchedule.getId(), this.selectedShift, this.list_details_review);

		this.scheduleDAO.updateMobileSynch(this.currentSchedule.getId(), this.sync_schedule.isChecked(), this.selectedShift);

		// refresh grid
		this.setupGlobalSchedulerGridForShiftReview();

		// Messagebox.show("Il consuntivo è stato aggiornato", "INFO",
		// Messagebox.OK, Messagebox.INFORMATION);
		this.shift_definition_popup_review.close();

	}

	@Listen("onClick = #save_review_item")
	public void saveReviewItem() {

		final Integer selectedIndex = this.listbox_review.getSelectedIndex();

		this.save_review_item.setVisible(false);
		this.cancel_modify.setVisible(false);
		this.add_review_item.setVisible(true);
		this.remove_review_item.setVisible(true);

		this.removeReviewItem();
		if (!this.addReviewItem()) {

			this.list_details_review.add(this.selectedItemReview);
			final ListModelList<DetailFinalSchedule> model = new ListModelList<>(this.list_details_review);
			this.orderListDetailFinalScheduleByTimeFrom(model);
			this.listbox_review.setModel(model);
			this.listbox_review.setSelectedIndex(selectedIndex);
			this.save_review_item.setVisible(true);
			this.cancel_modify.setVisible(true);
			this.add_review_item.setVisible(false);
			this.remove_review_item.setVisible(false);
		} else {
			this.ship.setSelectedItem(null);
			this.shipInDay.setSelectedItem(null);
			this.crane.setValue("");
			this.board.setSelectedItem(null);
		}

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

			// check if the command is available
			final boolean check = this.checkForPreprocessingCommand(date_scheduled);
			if (check) {

				final List<Schedule> scheduleListInWeek = this.statProcedure.searchBreakInCurrentWeek(date_scheduled, row_item.getUser());

				if ((scheduleListInWeek != null) && (scheduleListInWeek.size() > 0)) {
					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");

					final Messagebox.Button[] buttons = new Messagebox.Button[3];
					buttons[0] = Messagebox.Button.OK;
					buttons[1] = Messagebox.Button.NO;
					buttons[2] = Messagebox.Button.CANCEL;

					Messagebox.show(
							"Sono presenti nella settimana altri turni di riposo. Sostituirli con turni di lavoro? \nATTENZIONE: Non verranno modificati i giorni precedenti.",
							"CONFERMA CANCELLAZIONE TURNI DI RIPOSO", buttons, null, Messagebox.EXCLAMATION, null,
							new BreakInWeekManagement(shift, scheduleListInWeek, row_item, date_scheduled), params);
				} else {
					this.saveDayShiftProcedure(shift, row_item, date_scheduled, null);
				}
			} else {
				this.saveDayShiftProcedure(shift, row_item, date_scheduled, null);
			}

		} else {

			// save not break shift
			this.saveDayShiftProcedure(shift, row_item, date_scheduled, null);

			// check if the command is available
			final boolean check = this.checkForPreprocessingCommand(date_scheduled);
			if (check) {

				// check if in this week there is a break
				final List<Schedule> list_break = this.statProcedure.searchBreakInCurrentWeek(date_scheduled, row_item.getUser());

				if (list_break == null) {

					// check for sunday
					final Calendar check_sunday_cal = DateUtils.toCalendar(date_scheduled);
					if (DateUtils.isSameDay(Calendar.getInstance(), check_sunday_cal)
							&& (check_sunday_cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {

						final Map<String, String> params = new HashMap<>();
						params.put("sclass", "mybutton Button");
						final Messagebox.Button[] buttons = new Messagebox.Button[1];
						buttons[0] = Messagebox.Button.OK;

						final String msg = "Non ci sono più riposi per questa settimana. Programma con attenzione la settimana successiva.";
						Messagebox.show(msg, "GESTIONE RIPOSI", buttons, null, Messagebox.EXCLAMATION, null, null, params);

					} else {

						final Map<String, String> params = new HashMap<>();
						params.put("sclass", "mybutton Button");

						final Messagebox.Button[] buttons = new Messagebox.Button[2];
						buttons[0] = Messagebox.Button.OK;
						buttons[1] = Messagebox.Button.NO;

						final String msg = "Non ci sono più riposi per questa settimana. Impostare automaticamente un riposo?";
						Messagebox.show(msg, "GESTIONE RIPOSI", buttons, null, Messagebox.EXCLAMATION, null,
								new ReassignBreakEvent(date_scheduled, row_item), params);
					}

				}
			}
		}
	}

	@Listen("onClick= #overview_selector_select_all")
	public void selectAllShiftsInComboOverview() {

		this.select_shifttype_overview.setSelectedItem(null);

		// force refresh
		this.setOverviewLists(this.date_from_overview.getValue(), this.date_to_overview.getValue());

	}

	@Listen("onClick= #overview_selector_select_allTask")
	public void selectAllTaskInComboOverview() {
		this.taskComboBox.setSelectedItem(null);

		// force refresh
		this.setOverviewLists(this.date_from_overview.getValue(), this.date_to_overview.getValue());
	}

	@Listen("onClick= #preprocessing_select_rp")
	public void selectBreakShiftInCombo() {

		final UserShift break_shift = this.shift_cache.getBreakShift();

		if (break_shift == null) {
			return;
		}

		for (final Comboitem item : this.shifts_combo_select.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (break_shift.equals(current_shift_item)) {
					this.shifts_combo_select.setSelectedItem(item);
					break;
				}

			}
		}
	}

	@Listen("onClick= #overview_selector_select_rp")
	public void selectBreakShiftInComboOverview() {

		final UserShift bshift = this.shift_cache.getBreakShift();

		if (bshift == null) {
			return;
		}

		for (final Comboitem item : this.select_shifttype_overview.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (bshift.equals(current_shift_item)) {
					this.select_shifttype_overview.setSelectedItem(item);
					break;
				}

			}
		}

		// force refresh
		this.setOverviewLists(this.date_from_overview.getValue(), this.date_to_overview.getValue());
	}

	@Listen("onClick= #programpopup_select_rp")
	public void selectBreakShiftInComboShiftForceProgram() {

		final UserShift bshift = this.shift_cache.getBreakShift();

		if (bshift == null) {
			return;
		}

		for (final Comboitem item : this.force_shift_combo.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (bshift.equals(current_shift_item)) {
					this.force_shift_combo.setSelectedItem(item);
					break;
				}

			}
		}

		// some thing is changed
		this.forceProgramShift();
	}

	@Listen("onClick= #overview_selector_select_dl")
	public void selectDailyInComboOverview() {

		final UserShift daily = this.shift_cache.getDailyShift();

		if (daily == null) {
			return;
		}

		for (final Comboitem item : this.select_shifttype_overview.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (daily.equals(current_shift_item)) {
					this.select_shifttype_overview.setSelectedItem(item);
					break;
				}

			}
		}

		// force refresh
		this.setOverviewLists(this.date_from_overview.getValue(), this.date_to_overview.getValue());
	}

	@Listen("onClick= #shift_period_select_dl")
	public void selectDailyInComboShiftPeriodAssign() {

		final UserShift daily = this.shift_cache.getDailyShift();

		if (daily == null) {
			return;
		}

		for (final Comboitem item : this.shift_period_combo.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (daily.equals(current_shift_item)) {
					this.shift_period_combo.setSelectedItem(item);
					break;
				}

			}
		}
	}

	@Listen("onClick= #preprocessing_select_dl")
	public void selectDailySgiftInCombo() {

		final UserShift daily = this.shift_cache.getDailyShift();

		if (daily == null) {
			return;
		}

		for (final Comboitem item : this.shifts_combo_select.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (daily.equals(current_shift_item)) {
					this.shifts_combo_select.setSelectedItem(item);
					break;
				}

			}
		}
	}

	@Listen("onClick= #programpopup_select_dl")
	public void selectDailyShiftInComboShiftForceProgram() {

		final UserShift daily = this.shift_cache.getDailyShift();

		if (daily == null) {
			return;
		}

		for (final Comboitem item : this.force_shift_combo.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (daily.equals(current_shift_item)) {
					this.force_shift_combo.setSelectedItem(item);
					break;
				}

			}
		}

		// some thing is changed
		this.forceProgramShift();
	}

	@Listen("onChange =#select_year")
	public void selectedYear() {

		if (this.select_year.getSelectedItem() != null) {

			final String yearSelected = this.select_year.getSelectedItem().getValue();

			if (!yearSelected.equals(SchedulerComposer.ALL_ITEM)) {

				this.date_from_overview.setValue(null);
				this.date_to_overview.setValue(null);

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

				this.date_from_overview.setValue(date_from);
				this.date_to_overview.setValue(date_to);

				this.setOverviewLists(date_from, date_to);

			} else {
				this.date_from_overview.setValue(null);
				this.date_to_overview.setValue(null);
				this.setOverviewLists(null, null);
			}
		}
	}

	@Listen("onChange =#select_month,#select_week")
	public void selectMonthWeek() {
		if ((this.select_year.getSelectedItem() == null) || this.select_year.getSelectedItem().getValue().equals(SchedulerComposer.ALL_ITEM)) {
			final Calendar cal = Calendar.getInstance();
			this.select_year.setValue(cal.get(Calendar.YEAR) + "");
		}
		this.selectedYear();
	}

	@Listen("onClick = #selectShipNoWork")
	public void selectShipNoWorkInCombobox() {
		final Ship shipNoWork = this.shipDAO.getNoWorkShip();

		this.ship.setSelectedItem(null);
		this.shipInDay.setSelectedItem(null);

		if (shipNoWork != null) {
			final List<Comboitem> list = this.ship.getItems();

			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					final Ship ship = list.get(i).getValue();
					if (ship.getId() == shipNoWork.getId()) {
						this.ship.setSelectedIndex(i);
						this.shipSelected = ship;
					}
				}
			}

		}

	}

	@Listen("onClick= #preprocessing_select_tl")
	public void selectStandardWorkInCombo() {

		final UserShift stw = this.shift_cache.getStandardWorkShift();

		if (stw == null) {
			return;
		}

		for (final Comboitem item : this.shifts_combo_select.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (stw.equals(current_shift_item)) {
					this.shifts_combo_select.setSelectedItem(item);
					break;
				}

			}
		}
	}

	@Listen("onClick= #programpopup_select_tl")
	public void selectStandardWorkInComboForceProgram() {

		final UserShift stw = this.shift_cache.getStandardWorkShift();

		if (stw == null) {
			return;
		}

		for (final Comboitem item : this.force_shift_combo.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (stw.equals(current_shift_item)) {
					this.force_shift_combo.setSelectedItem(item);
					break;
				}

			}
		}

		// some thing is changed
		this.forceProgramShift();
	}

	@Listen("onClick= #overview_selector_select_tl")
	public void selectStandardWorkInComboOverview() {

		final UserShift stw = this.shift_cache.getStandardWorkShift();

		if (stw == null) {
			return;
		}

		for (final Comboitem item : this.select_shifttype_overview.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (stw.equals(current_shift_item)) {
					this.select_shifttype_overview.setSelectedItem(item);
					break;
				}

			}
		}

		// force refresh
		this.setOverviewLists(this.date_from_overview.getValue(), this.date_to_overview.getValue());

	}

	@Listen("onClick= #shift_period_select_tl")
	public void selectStandardWorkInComboShiftPeriodAssign() {

		final UserShift stw = this.shift_cache.getStandardWorkShift();

		if (stw == null) {
			return;
		}

		for (final Comboitem item : this.shift_period_combo.getItems()) {
			if ((item.getValue() != null) && (item.getValue() instanceof UserShift)) {
				final UserShift current_shift_item = item.getValue();
				if (stw.equals(current_shift_item)) {
					this.shift_period_combo.setSelectedItem(item);
					break;
				}

			}
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

	@Listen("onClick = #sw_link_edit_review")
	public void setEditValueInPopupReview() {

		this.errorMessageAddItem.setValue("");

		this.alertMinHoursReview.setVisible(false);

		this.save_review_item.setVisible(true);
		this.cancel_modify.setVisible(true);
		this.add_review_item.setVisible(false);
		this.remove_review_item.setVisible(false);

		final DetailFinalSchedule detail = this.listbox_review.getSelectedItem().getValue();

		if (detail != null) {
			final UserTask task = this.configurationDAO.loadTask(detail.getTask());

			List<Comboitem> items = this.review_task.getItems();

			for (int i = 0; i < items.size(); i++) {
				if (items.get(i).getLabel().equals(task.getCode() + " - " + task.getDescription())) {
					this.review_task.setSelectedIndex(i);
				}
			}

			Date dateTo = detail.getTime_to();
			Date dateFrom = detail.getTime_from();

			if ((dateTo != null) && (dateFrom != null)) {

				// for to fix old incorrect insert in db
				if (dateTo.before(dateFrom)) {

					final Date dateA = (Date) dateTo.clone();

					dateTo = (Date) dateFrom.clone();

					dateFrom = dateA;

				}

				final Calendar cal = Calendar.getInstance();
				cal.setTime(dateTo);
				final int dayTo = cal.get(Calendar.DAY_OF_MONTH);

				cal.setTime(dateFrom);
				final int dayFrom = cal.get(Calendar.DAY_OF_MONTH);

				this.day_shift_over_control.setChecked(false);
				this.day_shift_over_control_init.setChecked(false);

				final Date currentDate = this.currentSchedule.getDate_schedule();
				final Calendar calend = Calendar.getInstance();
				calend.setTime(currentDate);
				final int currentDay = calend.get(Calendar.DAY_OF_MONTH);

				if ((this.selectedShift == 4)) {
					if (currentDay != dayTo) {
						this.day_shift_over_control.setChecked(true);
						final Calendar calednar = DateUtils.toCalendar(dateTo);
						calednar.add(Calendar.DAY_OF_YEAR, -1);
						dateTo = calednar.getTime();
					}
					if (currentDay != dayFrom) {
						this.day_shift_over_control_init.setChecked(true);
						final Calendar calednar = DateUtils.toCalendar(dateFrom);
						calednar.add(Calendar.DAY_OF_YEAR, -1);
						dateFrom = calednar.getTime();
					}
				}

				this.time_from.setValue(dateFrom);
				this.time_to.setValue(dateTo);
			}

			this.ship.setSelectedItem(null);
			this.shipInDay.setSelectedItem(null);

			items = this.ship.getItems();
			for (int i = 0; i < items.size(); i++) {
				final Ship shipItem = items.get(i).getValue();
				if ((shipItem != null) && (detail.getId_ship() != null)) {
					if (shipItem.getId() == detail.getId_ship()) {
						this.ship.setSelectedIndex(i);
						this.shipSelected = shipItem;
						break;
					}
				}
			}

			this.crane.setValue(detail.getCrane());

			this.board.setValue(detail.getBoard());

			this.reviewShift.setChecked(false);
			if (detail.getReviewshift() != null) {
				this.reviewShift.setChecked(detail.getReviewshift());
			}

			this.continue_shift.setChecked(detail.getContinueshift());

		}
	}

	@Listen("onClick = #sw_link_edit_program")
	public void setEditValueInProgramPopup() {

		this.add_program_item.setVisible(false);
		this.remove_program_item.setVisible(false);
		this.save_program_item.setVisible(true);
		this.cancel_modify_program.setVisible(true);

		final DetailInitialSchedule detail = this.listbox_program.getSelectedItem().getValue();

		if (detail != null) {
			final UserTask task = this.configurationDAO.loadTask(detail.getTask());

			final List<Comboitem> items = this.program_task.getItems();

			for (int i = 0; i < items.size(); i++) {
				if (items.get(i).getLabel().equals(task.getCode() + " - " + task.getDescription())) {
					this.program_task.setSelectedIndex(i);
				}
			}

			Date dateTo = detail.getTime_to();
			Date dateFrom = detail.getTime_from();

			final Calendar cal = Calendar.getInstance();
			cal.setTime(dateTo);
			final int dayTo = cal.get(Calendar.DAY_OF_MONTH);

			cal.setTime(dateFrom);
			final int dayFrom = cal.get(Calendar.DAY_OF_MONTH);

			this.day_shift_over_control_program.setChecked(false);
			this.day_shift_over_control_program_init.setChecked(false);

			final Date currentDate = this.currentSchedule.getDate_schedule();
			final Calendar calend = Calendar.getInstance();
			calend.setTime(currentDate);
			final int currentDay = calend.get(Calendar.DAY_OF_MONTH);

			if ((this.selectedShift == 4)) {
				if (currentDay != dayTo) {
					this.day_shift_over_control_program.setChecked(true);
					final Calendar calednar = DateUtils.toCalendar(dateTo);
					calednar.add(Calendar.DAY_OF_YEAR, -1);
					dateTo = calednar.getTime();
				}
				if (currentDay != dayFrom) {
					this.day_shift_over_control_program_init.setChecked(true);
					final Calendar calednar = DateUtils.toCalendar(dateFrom);
					calednar.add(Calendar.DAY_OF_YEAR, -1);
					dateFrom = calednar.getTime();
				}
			}

			this.time_from_program.setValue(dateFrom);
			this.time_to_program.setValue(dateTo);
			this.continue_shiftProgram.setChecked(detail.getContinueshift());

		}

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
				day_number.setStyle("color:red;");
				day_label.setStyle("color:red;");
			} else {
				day_number.setStyle("color:black");
				day_label.setStyle("color:black");
			}

			// color bank holidays
			final String day_MMdd = SchedulerComposer.formatter_MMdd.format(current_calendar.getTime());
			if (this.bank_holiday.getDays().contains(day_MMdd)) {
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
		this.firstDateInGrid = DateUtils.truncate(calendar.getTime(), Calendar.DATE);

		// set seven days
		for (int i = 0; i < SchedulerComposer.DAYS_IN_GRID_PROGRAM; i++) {

			final int index_day = i + 1;

			final Auxheader month_head = (Auxheader) this.getSelf().getFellowIfAny("day_month_" + index_day);

			if (month_head == null) {
				continue;
			}

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(this.firstDateInGrid);
			current_calendar.add(Calendar.DAY_OF_YEAR, i);

			final String day_m = SchedulerComposer.formatter_ddmmm.format(current_calendar.getTime());

			if (current_calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				month_head.setStyle("color:red");
			} else {
				month_head.setStyle("color:black");
			}

			// color bank holidays
			final String day_MMdd = SchedulerComposer.formatter_MMdd.format(current_calendar.getTime());
			if (this.bank_holiday.getDays().contains(day_MMdd)) {
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
		this.firstDateInGrid = DateUtils.truncate(calendar.getTime(), Calendar.DATE);

		// set seven days
		for (int i = 0; i < SchedulerComposer.DAYS_TO_SHOW_IN_REVIEW; i++) {

			final int index_day = i + 1;

			final Auxheader month_head = (Auxheader) this.getSelf().getFellowIfAny("day_month_review_" + index_day);

			if (month_head == null) {
				continue;
			}

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(this.firstDateInGrid);

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
			if (this.bank_holiday.getDays().contains(day_MMdd)) {
				month_head.setStyle("color:red");
			}

			month_head.setLabel(day_m.toUpperCase());

		}

	}

	private void setLabelListBadge(final Integer idSchedule) {
		final List<Badge> badgeList = this.scheduleDAO.loadBadgeByScheduleId(idSchedule);

		String badgeInfo = "";

		if (badgeList != null) {
			for (final Badge badge : badgeList) {
				if (badge != null) {
					if (!badge.getEventType()) {
						badgeInfo = badgeInfo + " - U: " + Utility.getTimeFormat().format(badge.getEventTime());
					} else {
						badgeInfo = badgeInfo + " - E: " + Utility.getTimeFormat().format(badge.getEventTime());
					}
				}
			}
		}

		this.infoBadge.setValue(badgeInfo);

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
			this.totalHours_Program.setValue("Totale Ore Programmate: " + Utility.decimatToTime(total));
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
			this.totalHours_Review.setValue("Totale Ore Consuntivate: " + Utility.decimatToTime(total));

		}

	}

	/**
	 * set last programmer label
	 */
	private void setLastProgrammerLabel() {

		final Comboitem version_selected = this.scheduler_type_selector.getSelectedItem();

		if ((version_selected == null) || (version_selected == this.overview_item)) {
			this.last_programmer_tag.setVisible(false);
			return;
		}

		this.last_programmer_tag.setVisible(true);

		if (version_selected == this.overview_item) {
			this.lastProgrammer.setValue("");
			return;
		}

		LockTable lockTable = null;

		if ((version_selected == this.preprocessing_item) || (version_selected == this.program_item)) {
			lockTable = this.lockTableDAO.loadLastLockTableByTableType(TableTag.PROGRAM_TABLE);
		} else if (version_selected == this.review_item) {
			lockTable = this.lockTableDAO.loadLastLockTableByTableType(TableTag.REVIEW_TABLE);
		}

		final Person person = this.personDAO.loadPerson(lockTable.getId_user());

		if (person != null) {

			this.lastProgrammer.setValue(
					person.getFirstname() + " " + person.getLastname() + " " + SchedulerComposer.formatter_last_p.format(lockTable.getTime_to()));
		}

	}

	@Listen("onChange = #date_to_overview, #date_from_overview")
	public void setNullYearSelected() {
		this.select_year.setSelectedItem(null);
		this.defineSchedulerView();
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
		if ((this.taskComboBox.getSelectedItem() != null) && (this.taskComboBox.getSelectedItem().getValue() instanceof UserTask)) {
			taskSelected = this.taskComboBox.getSelectedItem().getValue();
		}

		// select full_text searching
		if ((this.full_text_search.getValue() != null) && !this.full_text_search.getValue().equals("")) {
			full_text_search = this.full_text_search.getValue();
		}

		// select shift
		if (this.select_shift_overview.getSelectedItem() != null) {
			final String value = this.select_shift_overview.getValue();
			if (!value.equals(SchedulerComposer.ALL_ITEM)) {
				if (NumberUtils.isNumber(this.select_shift_overview.getValue())) {
					shift_number = Integer.parseInt(this.select_shift_overview.getValue());
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
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controlla le date inserite", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		}

		// set datefrom and dateto if month filter or week filter is used
		if ((this.select_year.getSelectedItem() != null) && !this.select_year.getSelectedItem().getValue().equals(SchedulerComposer.ALL_ITEM)) {
			final String year = this.select_year.getSelectedItem().getValue();

			final Integer y = Integer.parseInt(year);

			ArrayList<Date> dates = null;

			if ((this.select_month.getSelectedItem() != null) && !this.select_month.getSelectedItem().getValue().equals(SchedulerComposer.ALL_ITEM)) {
				final String month = this.select_month.getSelectedItem().getValue();
				final Integer m = Integer.parseInt(month);

				dates = this.getDateByMonth(m, y);

			}

			if ((this.select_week.getSelectedItem() != null) && !this.select_week.getSelectedItem().getValue().equals(SchedulerComposer.ALL_ITEM)) {
				final String week = this.select_week.getSelectedItem().getValue();

				final Integer w = Integer.parseInt(week);

				dates = this.getDateByWeek(w, y);
			}

			if (dates != null) {
				date_from = dates.get(0);

				this.date_from_overview.setValue(date_from);

				date_to = dates.get(1);

				this.date_to_overview.setValue(date_to);
			}
		}

		Integer shift_type = null;

		if ((this.select_shifttype_overview.getSelectedItem() != null)
				&& (this.select_shifttype_overview.getSelectedItem().getValue() instanceof UserShift)) {
			final UserShift shift = this.select_shifttype_overview.getSelectedItem().getValue();
			if (shift != null) {
				shift_type = shift.getId();
			}
		}

		// select list
		if (this.overview_review.isSelected()) {

			Integer idSelectedTask = null;
			if (taskSelected != null) {
				idSelectedTask = taskSelected.getId();

				if (idSelectedTask == -2) {
					// filter selected is "all ships"
					idSelectedTask = null;
				}
			}

			Boolean reviewshift = null;
			if (this.hoursInterval.getSelectedItem() != null) {
				if (this.hoursInterval.getSelectedItem().getValue().equals("true")) {
					reviewshift = true;
				} else if (this.hoursInterval.getSelectedItem().getValue().equals("false")) {
				}
			}

			Integer idShip = null;

			if (this.shipSelector.getSelectedItem() != null) {
				final Ship ship = this.shipSelector.getSelectedItem().getValue();
				idShip = ship.getId();
				if (idShip == -2) {
					// filter selected is "all ships"
					idShip = null;
				}

			}

			// get idCrane. If idCrane is empty, set to null for mapping filter
			String idCrane = this.craneSelector.getValue();
			if ((idCrane != null) && idCrane.isEmpty()) {
				idCrane = null;
			}

			this.listDetailRevision = this.statisticDAO.listDetailFinalSchedule(full_text_search, shift_number, shift_type, idSelectedTask, date_from,
					date_to, reviewshift, idShip, idCrane);
			List<DetailFinalSchedule> countWorkerList = this.statisticDAO.countWorkerInOverviewFinalSchedule(full_text_search, shift_number,
					shift_type, idSelectedTask, date_from, date_to, reviewshift, idShip, idCrane);

			if (this.dayWorking_filter.getSelectedIndex() == 1) {
				this.listDetailRevision = this.filterDetailFinalScheduleByWorkingDay(true);
				countWorkerList = this.filterDetailFinalScheduleByWorkingDay(true);
			} else if (this.dayWorking_filter.getSelectedIndex() == 2) {
				this.listDetailRevision = this.filterDetailFinalScheduleByWorkingDay(false);
				countWorkerList = this.filterDetailFinalScheduleByWorkingDay(false);
			}

			if (countWorkerList != null) {
				this.overview_count_worker.setValue(countWorkerList.size() + "");
			} else {
				this.overview_count_worker.setValue("0");
			}

			double count_h = 0;
			double count_h_c = 0;

			final ArrayList<Date> count_day = new ArrayList<>();

			for (final DetailFinalSchedule item : this.listDetailRevision) {

				if (item.getTime() != null) {
					count_h += item.getTime();
				}

				if (item.getTime_vacation() != null) {
					count_h_c += item.getTime_vacation();
				}

				final Boolean is_working_day = Utility.isWorkingDay(item);

				// count day
				if (is_working_day) {

					final Date itm_date = item.getDate_schedule();

					if (!count_day.contains(itm_date)) {
						count_day.add(itm_date);
					}
				}

			}

			this.overview_count_h.setValue("" + Utility.roundTwo(count_h));
			this.overview_count_h_c.setValue("" + Utility.roundTwo(count_h_c));
			this.overview_count_days.setValue("" + count_day.size());

			// set number of row showed
			this.list_overview_review.setModel(new ListModelList<>(this.listDetailRevision));
			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.list_overview_review.setPageSize(this.shows_rows.getValue());
			}

		} else if (this.overview_program.isSelected()) {

			Integer idSelectedTask = null;
			if (taskSelected != null) {
				idSelectedTask = taskSelected.getId();

				if (idSelectedTask == -2) {
					// filter selected is "all ships"
					idSelectedTask = null;
				}

			}

			this.listDetailProgram = this.statisticDAO.listDetailInitialSchedule(full_text_search, shift_number, shift_type, idSelectedTask,
					date_from, date_to);

			double count_h = 0;
			double count_h_c = 0;

			final HashMap<Integer, Boolean> user_count = new HashMap<>();

			for (final DetailInitialSchedule item : this.listDetailProgram) {

				if (item.getTime() != null) {
					count_h += item.getTime();
				}

				if (item.getTime_vacation() != null) {
					count_h_c += item.getTime_vacation();
				}

				if (!user_count.containsKey(item.getId_user())) {
					user_count.put(item.getId_user(), Boolean.TRUE);
				}

			}

			this.overview_count_h.setValue("" + Utility.roundTwo(count_h));
			this.overview_count_h_c.setValue("" + Utility.roundTwo(count_h_c));
			this.overview_count_worker.setValue("" + user_count.size());

			// set number of row showed
			this.list_overview_program.setModel(new ListModelList<>(this.listDetailProgram));
			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.list_overview_program.setPageSize(this.shows_rows.getValue());
			}

		} else if (this.overview_preprocessing.isSelected()) {

			this.listSchedule = this.statisticDAO.listSchedule(full_text_search, shift_type, date_from, date_to);

			this.overview_count_h.setValue("");
			this.overview_count_h_c.setValue("");
			this.overview_count_worker.setValue("");

			// set number of row showed
			this.list_overview_preprocessing.setModel(new ListModelList<>(this.listSchedule));
			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.list_overview_preprocessing.setPageSize(this.shows_rows.getValue());
			}

		} else if (this.overview_statistics.isSelected()) {

			this.refreshUserStatistics(full_text_search, null);

		}

	}

	public void setSave_note_preprocessing(final Button save_note_preprocessing) {
		this.save_note_preprocessing = save_note_preprocessing;
	}

	public void setSave_note_program(final Button save_note_program) {
		this.save_note_program = save_note_program;
	}

	public void setSave_note_review(final Button save_note_review) {
		this.save_note_review = save_note_review;
	}

	@Listen("onClick = #sw_link_edit_review")
	public void setSelectedItemInListboxReview() {
		this.selectedItemReview = this.listbox_review.getSelectedItem().getValue();
	}

	@Listen("onChange = #ship")
	public void setShipComboBox() {
		if (this.ship.getSelectedItem() == null) {
			return;
		}
		if (this.ship.getSelectedItem().getValue() instanceof Ship) {
			final Ship ship = this.ship.getSelectedItem().getValue();
			if (ship != null) {
				if (ship.getId() != -1) {
					this.shipInDay.setSelectedItem(null);
				}
			}
			// update ship selected
			this.shipSelected = ship;
		}
	}

	@Listen("onChange = #shipInDay")
	public void setShipInDayComboBox() {
		if (this.shipInDay.getSelectedItem() != null) {
			if (this.shipInDay.getSelectedItem().getValue() instanceof Ship) {
				final Ship ship = this.shipInDay.getSelectedItem().getValue();
				if (ship != null) {
					if (ship.getId() != -1) {
						this.ship.setSelectedItem(null);
					}
				}
				// update ship selected
				this.shipSelected = ship;
			}
		}
	}

	public void setTask_cont(final Label task_cont) {
		this.task_cont = task_cont;
	}

	@Listen("onClick = #go_today_preprocessing")
	public void setTodaySchedulerView() {
		final Calendar calendar = Calendar.getInstance();
		final Date today = calendar.getTime();

		this.defineSchedulerViewToDate(today);

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

		this.list_rowDaySchedule = new ArrayList<>();
		RowDaySchedule currentRow = null;

		for (int i = 0; i < list.size(); i++) {

			final Schedule schedule = list.get(i);

			// if the user is changed, add another row
			if ((currentRow == null) || (!currentRow.getUser().equals(schedule.getUser()))) {
				// set current row
				currentRow = new RowDaySchedule();
				currentRow.setUser(schedule.getUser());
				currentRow.setName_user(schedule.getName_user());
				this.list_rowDaySchedule.add(currentRow);

				// set items for current row
				this.initializeDateForDaySchedule(currentRow, this.firstDateInGrid);

			}

			// set correct day
			final long day_on_current_calendar = this.getDayOfSchedule(schedule);

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
		this.grid_scheduler_day.setModel(new ListModelList<>(this.list_rowDaySchedule));

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
		final HashMap<Integer, String> map_status = this.defineUserAvailability(calendar.getTime());

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

		this.list_rows_program = new ArrayList<>();
		RowSchedule currentRow = null;

		// create a map for define people scheduled
		final HashMap<Integer, RowSchedule> sign_scheduled = new HashMap<>();

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
				this.list_rows_program.add(currentRow);

				// set user type for available
				if (map_status.containsKey(schedule.getUser())) {
					final String status = map_status.get(schedule.getUser());
					currentRow.setUser_status(status);
				}

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), currentRow);
			}

			// set correct day
			final int day_on_current_calendar = this.getDayOfSchedule(schedule);
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
				this.list_rows_program.add(myrow);

				// set user type for available
				if (map_status.containsKey(schedule.getUser())) {
					final String status = map_status.get(schedule.getUser());
					myrow.setUser_status(status);
				}

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), myrow);
			}

			// set correct day
			final int day_on_current_calendar = this.getDayOfSchedule(schedule);
			final ItemRowSchedule itemsRow = this.getItemRowSchedule(myrow, day_on_current_calendar, schedule, true);

			if (day_on_current_calendar == 1) {
				myrow.setItem_1(itemsRow);
			}

		}

		// count review
		for (final RowSchedule itemrow_count : this.list_rows_program) {

			final Person user = this.personDAO.loadPerson(itemrow_count.getUser());

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
		this.program_tot_1_1.setLabel(Utility.decimatToTime(count_matrix[0][0]));
		this.program_tot_1_2.setLabel(Utility.decimatToTime(count_matrix[0][1]));
		this.program_tot_1_3.setLabel(Utility.decimatToTime(count_matrix[0][2]));
		this.program_tot_1_4.setLabel(Utility.decimatToTime(count_matrix[0][3]));

		this.program_tot_2_1.setLabel(Utility.decimatToTime(count_matrix[1][0]));
		this.program_tot_2_2.setLabel(Utility.decimatToTime(count_matrix[1][1]));
		this.program_tot_2_3.setLabel(Utility.decimatToTime(count_matrix[1][2]));
		this.program_tot_2_4.setLabel(Utility.decimatToTime(count_matrix[1][3]));

		this.program_tot_3_1.setLabel(Utility.decimatToTime(count_matrix[2][0]));
		this.program_tot_3_2.setLabel(Utility.decimatToTime(count_matrix[2][1]));
		this.program_tot_3_3.setLabel(Utility.decimatToTime(count_matrix[2][2]));
		this.program_tot_3_4.setLabel(Utility.decimatToTime(count_matrix[2][3]));

		this.program_tot_4_1.setLabel(Utility.decimatToTime(count_matrix[3][0]));
		this.program_tot_4_2.setLabel(Utility.decimatToTime(count_matrix[3][1]));
		this.program_tot_4_3.setLabel(Utility.decimatToTime(count_matrix[3][2]));
		this.program_tot_4_4.setLabel(Utility.decimatToTime(count_matrix[3][3]));

		this.program_tot_5_1.setLabel(Utility.decimatToTime(count_matrix[4][0]));
		this.program_tot_5_2.setLabel(Utility.decimatToTime(count_matrix[4][1]));
		this.program_tot_5_3.setLabel(Utility.decimatToTime(count_matrix[4][2]));
		this.program_tot_5_4.setLabel(Utility.decimatToTime(count_matrix[4][3]));

		// set sum for persons
		this.programUser_tot_1_1.setLabel(count_matrixUsers[0][0].toString());
		this.programUser_tot_1_2.setLabel(count_matrixUsers[0][1].toString());
		this.programUser_tot_1_3.setLabel(count_matrixUsers[0][2].toString());
		this.programUser_tot_1_4.setLabel(count_matrixUsers[0][3].toString());

		this.programUser_tot_2_1.setLabel(count_matrixUsers[1][0].toString());
		this.programUser_tot_2_2.setLabel(count_matrixUsers[1][1].toString());
		this.programUser_tot_2_3.setLabel(count_matrixUsers[1][2].toString());
		this.programUser_tot_2_4.setLabel(count_matrixUsers[1][3].toString());

		this.programUser_tot_3_1.setLabel(count_matrixUsers[2][0].toString());
		this.programUser_tot_3_2.setLabel(count_matrixUsers[2][1].toString());
		this.programUser_tot_3_3.setLabel(count_matrixUsers[2][2].toString());
		this.programUser_tot_3_4.setLabel(count_matrixUsers[2][3].toString());

		this.programUser_tot_4_1.setLabel(count_matrixUsers[3][0].toString());
		this.programUser_tot_4_2.setLabel(count_matrixUsers[3][1].toString());
		this.programUser_tot_4_3.setLabel(count_matrixUsers[3][2].toString());
		this.programUser_tot_4_4.setLabel(count_matrixUsers[3][3].toString());

		this.programUser_tot_5_1.setLabel(count_matrixUsers[4][0].toString());
		this.programUser_tot_5_2.setLabel(count_matrixUsers[4][1].toString());
		this.programUser_tot_5_3.setLabel(count_matrixUsers[4][2].toString());
		this.programUser_tot_5_4.setLabel(count_matrixUsers[4][3].toString());

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

		this.total_program_day_1.setLabel(Utility.decimatToTime(count_matrix_row[0]));
		this.total_program_day_2.setLabel(Utility.decimatToTime(count_matrix_row[1]));
		this.total_program_day_3.setLabel(Utility.decimatToTime(count_matrix_row[2]));
		this.total_program_day_4.setLabel(Utility.decimatToTime(count_matrix_row[3]));
		this.total_program_day_5.setLabel(Utility.decimatToTime(count_matrix_row[4]));

		this.totalUser_program_day_1.setLabel(count_Day_Users[0].toString());
		this.totalUser_program_day_2.setLabel(count_Day_Users[1].toString());
		this.totalUser_program_day_3.setLabel(count_Day_Users[2].toString());
		this.totalUser_program_day_4.setLabel(count_Day_Users[3].toString());
		this.totalUser_program_day_5.setLabel(count_Day_Users[4].toString());

		// get all user to schedule
		final List<Person> users_schedule = this.personDAO.listWorkerPersons(text_search_person, null);

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
			this.list_rows_program.add(addedRow);

		}

		// sort
		Collections.sort(this.list_rows_program);

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.grid_scheduler.setPageSize(this.shows_rows.getValue());
		}
		final ListModelList<RowSchedule> model = new ListModelList<>(this.list_rows_program);
		model.setMultiple(true);
		this.grid_scheduler.setModel(model);

	}

	/**
	 * @param info_visibility
	 *            if true set info scheduler for programming visible
	 */
	private void setupGlobalSchedulerGridForShiftReview() {

		// user availability and color
		final HashMap<Integer, String> map_status = this.defineUserAvailability(this.firstDateInGrid);

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.firstDateInGrid);

		final Date ret = calendar.getTime();
		final Date date_schedule = DateUtils.truncate(ret, Calendar.DATE);

		// create a map for define people scheduled
		final HashMap<Integer, RowSchedule> sign_scheduled = new HashMap<>();

		// take info about person
		String text_search_person = null;
		if ((this.full_text_search.getValue() == null) || this.full_text_search.getValue().equals("")) {
			text_search_person = null;
		} else {
			text_search_person = this.full_text_search.getValue();
		}

		// get info on program
		final List<Schedule> list_revision = this.scheduleDAO.selectAggregateSchedulersRevision(date_schedule, text_search_person);

		this.list_rowSchedule = new ArrayList<>();
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
				this.list_rowSchedule.add(currentRow);

				// set user type for available
				if (map_status.containsKey(schedule.getUser())) {
					final String status = map_status.get(schedule.getUser());
					currentRow.setUser_status(status);
				}

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), currentRow);
			}

			// set day 2
			final ItemRowSchedule itemsRow = this.getItemRowSchedule(currentRow, 2, schedule, false);

			currentRow.setItem_2(itemsRow);

		}

		// count review
		for (final RowSchedule itemrow_count : this.list_rowSchedule) {

			final Person user = this.personDAO.loadPerson(itemrow_count.getUser());
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
						|| (itemrow_count.getItem_2().getAnchorValue3() != 0) || (itemrow_count.getItem_2().getAnchorValue4() != 0)) {
					countUsersTot++;
				}

				count_colum_1 = count_colum_1 + itemrow_count.getItem_2().getAnchorValue1();
				count_colum_2 = count_colum_2 + itemrow_count.getItem_2().getAnchorValue2();
				count_colum_3 = count_colum_3 + itemrow_count.getItem_2().getAnchorValue3();
				count_colum_4 = count_colum_4 + itemrow_count.getItem_2().getAnchorValue4();
			}

		}

		// set info program
		this.review_tot_2_1.setLabel(Utility.decimatToTime(count_colum_1));
		this.review_tot_2_2.setLabel(Utility.decimatToTime(count_colum_2));
		this.review_tot_2_3.setLabel(Utility.decimatToTime(count_colum_3));
		this.review_tot_2_4.setLabel(Utility.decimatToTime(count_colum_4));

		this.total_review_day_2.setLabel(Utility.decimatToTime(count_colum_1 + count_colum_2 + count_colum_3 + count_colum_4));

		// set number of person in shift
		this.reviewUser_tot_2_1.setLabel(countUsers_colum_1.toString());
		this.reviewUser_tot_2_2.setLabel(countUsers_colum_2.toString());
		this.reviewUser_tot_2_3.setLabel(countUsers_colum_3.toString());
		this.reviewUser_tot_2_4.setLabel(countUsers_colum_4.toString());

		this.totalUser_review_day_2.setLabel(countUsersTot.toString());

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
				this.list_rowSchedule.add(myRow);

				// set user type for available
				if (map_status.containsKey(schedule.getUser())) {
					final String status = map_status.get(schedule.getUser());
					myRow.setUser_status(status);
				}

				// sign person scheduled
				sign_scheduled.put(schedule.getUser(), myRow);
			}

			// set day 1
			final ItemRowSchedule itemsRow = this.getItemRowSchedule(myRow, 1, schedule, false);

			myRow.setItem_1(itemsRow);

		}

		// count review
		for (final RowSchedule itemrow_count : this.list_rowSchedule) {

			final Person user = this.personDAO.loadPerson(itemrow_count.getUser());

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
						|| (itemrow_count.getItem_1().getAnchorValue3() != 0) || (itemrow_count.getItem_1().getAnchorValue4() != 0)) {
					countUsersTot++;
				}

				count_colum_1 = count_colum_1 + itemrow_count.getItem_1().getAnchorValue1();
				count_colum_2 = count_colum_2 + itemrow_count.getItem_1().getAnchorValue2();
				count_colum_3 = count_colum_3 + itemrow_count.getItem_1().getAnchorValue3();
				count_colum_4 = count_colum_4 + itemrow_count.getItem_1().getAnchorValue4();

			}

		}

		// set info review
		this.review_tot_1_1.setLabel(Utility.decimatToTime(count_colum_1));
		this.review_tot_1_2.setLabel(Utility.decimatToTime(count_colum_2));
		this.review_tot_1_3.setLabel(Utility.decimatToTime(count_colum_3));
		this.review_tot_1_4.setLabel(Utility.decimatToTime(count_colum_4));

		this.total_review_day_1.setLabel(Utility.decimatToTime(count_colum_1 + count_colum_2 + count_colum_3 + count_colum_4));

		// set number of person in shift
		this.reviewUser_tot_1_1.setLabel(countUsers_colum_1.toString());
		this.reviewUser_tot_1_2.setLabel(countUsers_colum_2.toString());
		this.reviewUser_tot_1_3.setLabel(countUsers_colum_3.toString());
		this.reviewUser_tot_1_4.setLabel(countUsers_colum_4.toString());

		this.totalUser_review_day_1.setLabel(countUsersTot.toString());

		// get all user to schedule
		final List<Person> users_schedule = this.personDAO.listWorkerPersons(text_search_person, null);

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

			this.list_rowSchedule.add(addedRow);

		}

		// sort
		Collections.sort(this.list_rowSchedule);

		// set grid
		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.grid_scheduler_review.setPageSize(this.shows_rows.getValue());
		}
		final ListModelList<RowSchedule> model = new ListModelList<>(this.list_rowSchedule);
		model.setMultiple(true);
		this.grid_scheduler_review.setModel(model);

	}

	// set visibility of download_program_report button
	private void setVisibilityDownloadReportButton() {
		final Person personLogged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!personLogged.isAdministrator()) {
			final Comboitem version_selected = SchedulerComposer.this.scheduler_type_selector.getSelectedItem();
			if (version_selected == SchedulerComposer.this.program_item) {
				if (this.lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE) == null) {
					this.download_program_report.setVisible(true);
				} else {
					this.download_program_report.setVisible(false);
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

		// get current day
		final Date current_day = this.getDateScheduled(this.selectedDay);

		if (!this.checkIfUnLockTable()) {
			SchedulerComposer.this.disableWriteCancelButtons(true);

			if (this.userLockTable != null) {
				this.loggerUserOnTable.setValue(this.messageTableLock + this.personLock.getFirstname() + " " + this.personLock.getLastname() + " - "
						+ this.messageTimeConnectionTableLock + Utility.convertToDateAndTime(this.userLockTable.getTime_start()));
				this.switchButton.setLabel(this.switchButtonValueClose);

			} else {
				this.loggerUserOnTable.setValue(this.messageTableUnLock);
				this.switchButton.setLabel(this.switchButtonValueOpen);
			}

		}

		if (!this.person_logged.isAdministrator()) {

			final boolean check = this.checkForPreprocessingCommand(current_day);

			if (!check) {

				this.cancel_day_definition.setDisabled(true);
				this.ok_day_shift.setDisabled(true);
			}

		} else {
			this.cancel_day_definition.setDisabled(false);
			this.ok_day_shift.setDisabled(false);
		}

		// initialize message popup
		String msg = "" + SchedulerComposer.formatter_scheduler_info.format(current_day);

		// get user
		if (this.grid_scheduler_day.getSelectedItem() != null) {
			final RowDaySchedule row = this.grid_scheduler_day.getSelectedItem().getValue();
			final String name = row.getName_user();

			if (this.personDAO.loadPerson(this.selectedUser).getPart_time()) {
				msg = name + " " + this.partTimeMessage + ". " + msg;
			} else {
				msg = name + ". " + msg;
			}

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

		this.shifts_combo_select.setSelectedItem(null);

		if (current_shift != null) {
			// set initial selected item
			for (final Comboitem item : this.shifts_combo_select.getItems()) {
				if (item.getValue() instanceof UserShift) {
					if (item.getValue().equals(current_shift)) {
						this.shifts_combo_select.setSelectedItem(item);
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

		// show programmer and controller
		this.editor_label_daydefinition.setLabel("");
		this.controller_label_daydefinition.setLabel("");

		if (this.currentSchedule != null) {

			if (this.currentSchedule.getEditor() != null) {
				final Person editor = this.personDAO.loadPerson(this.currentSchedule.getEditor());
				if (editor != null) {
					this.editor_label_daydefinition.setLabel("Programmatore: " + editor.getFirstname() + " " + editor.getLastname());
				}
			}

			if (this.currentSchedule.getController() != null) {
				final Person controller = this.personDAO.loadPerson(this.currentSchedule.getController());
				if (controller != null) {
					this.controller_label_daydefinition.setLabel("Controllore: " + controller.getFirstname() + " " + controller.getLastname());
				}
			}

		}

		this.day_definition_popup.open(this.grid_scheduler_day, "after_pointer");
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

		final Person user = this.personDAO.loadPerson(id_user);

		// select date period
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
		final Date date_from = cal.getTime();

		cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
		final Date date_to = cal.getTime();

		final UserStatistics userStatistics = this.statProcedure.getUserStatistics(user, date_from, date_to, true);

		this.userRoles.setValue("");

		// set label user roles in statistic popup
		final String roles = user.getRolesDescription();
		if (roles != "") {
			this.userRoles.setValue(roles + ".");
		}

		// set department in statistic popup
		this.userDepartment.setValue("");
		final String department = user.getDepartment();
		if (department != null) {
			this.userDepartment.setValue(department);
		}

		final Double sat = userStatistics.getSaturation();
		this.saturation.setValue(sat.toString());
		// set saturation style
		if (sat < 0) {
			this.saturation.setStyle("color:red");
			this.saturation.setValue("REC " + Utility.roundTwo(Math.abs(sat)));

		} else {
			this.saturation.setStyle("color:green");
			this.saturation.setValue("OT " + Utility.roundTwo(sat));
		}

		// set saturation current month style
		final Double sat_month = userStatistics.getSaturation_month();
		if (sat_month == null) {
			this.saturation_current_month.setValue(" ");
		} else {

			if (sat_month < 0) {
				this.saturation_current_month.setStyle("color:red");
				this.saturation_current_month.setValue("REC " + Utility.roundTwo(Math.abs(sat_month)));
			} else {
				this.saturation_current_month.setStyle("color:green");
				this.saturation_current_month.setValue("OT " + Utility.roundTwo(sat_month));
			}
		}

		// set saturation prec month style
		final Double sat_prec = userStatistics.getSaturation_prec_month();
		if (sat_prec == null) {
			this.saturation_prec_month.setValue(" ");
		} else {

			if (sat_prec < 0) {
				this.saturation_prec_month.setStyle("color:red");
				this.saturation_prec_month.setValue("REC " + Utility.roundTwo(Math.abs(sat_prec)));
			} else {
				this.saturation_prec_month.setStyle("color:green");
				this.saturation_prec_month.setValue("OT " + Utility.roundTwo(sat_prec));
			}
		}

		this.work_sunday_perc.setValue(userStatistics.getWork_sunday_perc());
		this.work_holiday_perc.setValue(userStatistics.getWork_holiday_perc());
		this.work_current_week.setValue(userStatistics.getWork_current_week());
		this.work_current_month.setValue(userStatistics.getWork_current_month());
		this.work_current_year.setValue(userStatistics.getWork_current_year());
		this.working_series.setValue(userStatistics.getWorking_series());
		this.shift_perc_1.setValue(userStatistics.getShift_perc_1());
		this.shift_perc_2.setValue(userStatistics.getShift_perc_2());
		this.shift_perc_3.setValue(userStatistics.getShift_perc_3());
		this.shift_perc_4.setValue(userStatistics.getShift_perc_4());

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

		final List<UserTask> user_tasks = this.taskDAO.loadTasksByUser(id_user);
		this.list_task_stat.setModel(new ListModelList<>(user_tasks));

		SchedulerComposer.this.task_list_popup.open(anchorComponent, "after_pointer");

	}

	@Listen("onClick=#sortByStatistic")
	public void sortByStatistic() {
		this.defineSchedulerView();
		this.calculateSatOnPersons();

		HashMap<Integer, Double> cache_sat = (HashMap<Integer, Double>) Executions.getCurrent().getSession().getAttribute(ZkSessionTag.PersonCache);

		if (cache_sat == null) {
			return;
		}

		// order hashmap
		cache_sat = (HashMap<Integer, Double>) SchedulerComposer.sortByComparator(cache_sat, true);

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected == this.preprocessing_item) {

			final ArrayList<RowDaySchedule> sortedList = new ArrayList<>();
			final Iterator it = cache_sat.entrySet().iterator();
			while (it.hasNext()) {
				final Map.Entry pair = (Map.Entry) it.next();
				final Integer idPerson = (Integer) pair.getKey();
				for (final RowDaySchedule rowDaySchedule : this.list_rowDaySchedule) {
					if (rowDaySchedule.getUser().equals(idPerson)) {
						sortedList.add(rowDaySchedule);
						break;
					}
				}
			}
			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.grid_scheduler_day.setPageSize(this.shows_rows.getValue());
			}
			this.grid_scheduler_day.setModel(new ListModelList<>(sortedList));

		} else if (selected == this.program_item) {
			final ArrayList<RowSchedule> sortedList = new ArrayList<>();
			final Iterator it = cache_sat.entrySet().iterator();
			while (it.hasNext()) {
				final Map.Entry pair = (Map.Entry) it.next();
				final Integer idPerson = (Integer) pair.getKey();
				for (final RowSchedule detail : this.list_rows_program) {
					if (detail.getUser().equals(idPerson)) {
						sortedList.add(detail);
						break;
					}
				}
			}
			this.grid_scheduler.setModel(new ListModelList<>(sortedList));
		} else if (selected == this.review_item) {
			final ArrayList<RowSchedule> sortedList = new ArrayList<>();
			final Iterator it = cache_sat.entrySet().iterator();
			while (it.hasNext()) {
				final Map.Entry pair = (Map.Entry) it.next();
				final Integer idPerson = (Integer) pair.getKey();
				for (final RowSchedule detail : this.list_rowSchedule) {
					if (detail.getUser().equals(idPerson)) {
						sortedList.add(detail);
						break;
					}
				}
			}
			this.grid_scheduler_review.setModel(new ListModelList<>(sortedList));

		} else if (selected == this.overview_item) {
			if (this.overview_review.isSelected()) {
				final ArrayList<DetailFinalSchedule> sortedList = new ArrayList<>();
				final Iterator it = cache_sat.entrySet().iterator();
				while (it.hasNext()) {
					final Map.Entry pair = (Map.Entry) it.next();
					final Integer idPerson = (Integer) pair.getKey();
					for (final DetailFinalSchedule detail : this.listDetailRevision) {
						if (detail.getId_user().equals(idPerson)) {
							sortedList.add(detail);
						}
					}
				}
				this.list_overview_review.setModel(new ListModelList<>(sortedList));
			} else if (this.overview_program.isSelected()) {
				final ArrayList<DetailInitialSchedule> sortedList = new ArrayList<>();
				final Iterator it = cache_sat.entrySet().iterator();
				while (it.hasNext()) {
					final Map.Entry pair = (Map.Entry) it.next();
					final Integer idPerson = (Integer) pair.getKey();
					for (final DetailInitialSchedule detail : this.listDetailProgram) {
						if (detail.getId_user().equals(idPerson)) {
							sortedList.add(detail);
						}
					}
				}
				this.list_overview_program.setModel(new ListModelList<>(sortedList));
			} else if (this.overview_preprocessing.isSelected()) {
				final ArrayList<Schedule> sortedList = new ArrayList<>();
				final Iterator it = cache_sat.entrySet().iterator();
				while (it.hasNext()) {
					final Map.Entry pair = (Map.Entry) it.next();
					final Integer idPerson = (Integer) pair.getKey();
					for (final Schedule detail : this.listSchedule) {
						if (detail.getUser().equals(idPerson)) {
							sortedList.add(detail);
						}
					}
				}
				this.list_overview_preprocessing.setModel(new ListModelList<>(sortedList));
			} else if (this.overview_statistics.isSelected()) {
				final ArrayList<UserStatistics> sortedList = new ArrayList<>();
				final Iterator it = cache_sat.entrySet().iterator();
				while (it.hasNext()) {
					final Map.Entry pair = (Map.Entry) it.next();
					final Integer idPerson = (Integer) pair.getKey();
					for (final UserStatistics detail : this.listUserStatistics) {
						final Person person = detail.getPerson();
						if ((person != null) && person.getId().equals(idPerson)) {
							sortedList.add(detail);
							break;
						}
					}
				}
				this.list_statistics.setModel(new ListModelList<>(sortedList));
			}

		}
	}

	@Listen("onClick= #switchButton")
	public void switchButtonClick() {

		this.onChangeSelectedVersion();

		// set button switch
		this.checkIfTableIsLockedAndSetButton();

		Comboitem version_selected = SchedulerComposer.this.scheduler_type_selector.getSelectedItem();
		LockTable lockTable = null;
		if ((version_selected == SchedulerComposer.this.preprocessing_item) || (version_selected == SchedulerComposer.this.program_item)) {
			lockTable = SchedulerComposer.this.lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE);
		} else if (version_selected == SchedulerComposer.this.review_item) {
			lockTable = SchedulerComposer.this.lockTableDAO.loadLockTableByTableType(TableTag.REVIEW_TABLE);
		}

		if (!this.person_logged.isAdministrator() && ((lockTable != null) && !lockTable.getId_user().equals(this.person_logged.getId()))) {
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

			if (this.switchButton.getLabel().equals(this.switchButtonValueOpen)) {
				// lock table
				this.switchButton.setLabel(this.switchButtonValueClose);
				this.switchButton.setVisible(true);

				final LockTable myLockTable = new LockTable();
				myLockTable.setId_user(this.person_logged.getId());
				myLockTable.setTime_start(new Timestamp(Calendar.getInstance().getTime().getTime()));
				this.loggerUserOnTable.setValue(this.messageTableLock + this.person_logged.getFirstname() + " " + this.person_logged.getLastname()
						+ " - " + this.messageTimeConnectionTableLock + Utility.convertToDateAndTime(myLockTable.getTime_start()));
				if ((version_selected == SchedulerComposer.this.preprocessing_item) || (version_selected == SchedulerComposer.this.program_item)) {
					myLockTable.setTable_type(TableTag.PROGRAM_TABLE);
				} else if (version_selected == SchedulerComposer.this.review_item) {
					myLockTable.setTable_type(TableTag.REVIEW_TABLE);
				}

				this.lockTableDAO.createLockTable(myLockTable);

				// disable all write and cancel buttons
				this.disableWriteCancelButtons(false);

			} else if (this.switchButton.getLabel().equals(this.switchButtonValueClose)) {
				this.switchButton.setVisible(true);
				this.loggerUserOnTable.setValue(this.messageTableUnLock);
				this.switchButton.setLabel(this.switchButtonValueOpen);
				// load locktable that is locked by you or unlock table because
				// you
				// are administrator
				lockTable = null;
				if ((version_selected == SchedulerComposer.this.preprocessing_item) || (version_selected == SchedulerComposer.this.program_item)) {
					lockTable = this.lockTableDAO.loadLockTableByTableType(TableTag.PROGRAM_TABLE);
				} else if (version_selected == SchedulerComposer.this.review_item) {
					lockTable = this.lockTableDAO.loadLockTableByTableType(TableTag.REVIEW_TABLE);
				}

				if (lockTable != null) {
					lockTable.setTime_to(new Timestamp(Calendar.getInstance().getTime().getTime()));
					lockTable.setId_user_closer(this.person_logged.getId());
					this.lockTableDAO.updateLockTable(lockTable);
				}

				if (this.person_logged.isAdministrator()) {
					this.disableWriteCancelButtons(false);
				} else {
					this.disableWriteCancelButtons(true);
				}

			}
		}

	}

	/**
	 * Update only the note without any information about human interaction
	 */
	private void updateNoteOnPopup(final String note) {

		if (this.currentSchedule == null) {
			return;
		}

		this.scheduleDAO.updateScheduleNote(this.currentSchedule.getId(), note);

	}

}
