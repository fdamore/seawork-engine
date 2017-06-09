package org.uario.seaworkengine.zkevent;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.model.Complaint;
import org.uario.seaworkengine.model.Crane;
import org.uario.seaworkengine.model.Customer;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.ReportItem;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Service;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.TerminalProductivity;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShipCache;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ICustomerDAO;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.statistics.ReviewShipWorkAggregate;
import org.uario.seaworkengine.statistics.ShipOverview;
import org.uario.seaworkengine.statistics.ShipTotal;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ReportItemTag;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.utility.UtilityCSV;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Toolbarbutton;

public class ShipSchedulerComposer extends SelectorComposer<Component> {

	private final class StartingEvent implements EventListener<Event> {

		@Override
		public void onEvent(final Event arg0) throws Exception {

			final String att_print = Executions.getCurrent().getParameter(ShipSchedulerComposer.PRINT_PROGRAM);
			if (att_print != null) {
				ShipSchedulerComposer.this.mainSchedulerView(ShipSchedulerComposer.PRINT_PROGRAM);
			} else {
				ShipSchedulerComposer.this.mainSchedulerView(null);
			}

		}
	}

	private static final String								CAPTION_DETAIL_PROGRAM_SHIP		= "Dettagli di Programmazione Nave";

	private static final String								CAPTION_POPUP_SCHEDULE_SHIP		= "Programmazione Nave";

	private static final String								CAPTION_SHIP_PROGRAM_LABEL		= "Dettaglio di Programmazione Nave";

	private static final String								PRINT_PROGRAM					= "printProgram";

	private static String									rif_mct_empty					= "RIF_MCT_EMPTY";

	/**
	 *
	 */
	private static final long								serialVersionUID				= 1L;

	private static final String								SHIP_WORKED_NO					= "no";

	private static final String								SHIP_WORKED_YES					= "yes";

	@Wire
	private Button											add_finalDetailScheduleShip_command;

	@Wire
	private Component										add_scheduleShips_command;

	@Wire
	Toolbarbutton											add_scheduleShipsDetail_command;

	@Wire
	public Row												alertShiftDate;

	@Wire
	public Row												alertShiftDate_detail;

	@Wire
	private Listheader										arrivalDateColumn;

	@Wire
	private Label											avg_productivity;

	@Wire
	private Label											avgVolmueShipProgram;

	@Wire
	private Tabbox											bap_overview_tab;

	@Wire
	private Caption											captionDetailProgramShip;

	@Wire
	private Caption											captionPopupScheduleShip;

	@Wire
	private Caption											captionShipProgram;

	@Wire
	private Checkbox										check_last_shift;

	@Wire
	private Checkbox										check_last_shift_detail;

	@Wire
	private Checkbox										check_last_shiftReview;

	@Wire
	private Intbox											complaint_apr;

	@Wire
	private Intbox											complaint_aug;

	@Wire
	private Intbox											complaint_dec;

	@Wire
	private Intbox											complaint_feb;

	@Wire
	private Intbox											complaint_gen;

	@Wire
	private Intbox											complaint_jul;

	@Wire
	private Intbox											complaint_jun;

	@Wire
	private Intbox											complaint_mar;

	@Wire
	private Intbox											complaint_may;

	@Wire
	private Intbox											complaint_nov;

	@Wire
	private Intbox											complaint_oct;

	@Wire
	private Intbox											complaint_sep;

	private ConfigurationDAO								configurationDao;

	@Wire
	private org.zkoss.zul.Checkbox							crane_gtw_review;

	private List<Crane>										craneListModel;

	@Wire
	private Listheader										customerColumn;

	private HashMap<Integer, HashMap<Integer, ShipTotal>>	customerComplaint				= new HashMap<>();

	private ICustomerDAO									customerDAO;

	@Wire
	private Checkbox										datashift_period;

	@Wire
	private Component										datashift_period_compo;

	@Wire
	private Listheader										departureDateColumn;

	@Wire
	public Component										detail_div;

	@Wire
	private Comboitem										detail_item;

	@Wire
	private Tab												detail_scheduleShip_tab;

	private DetailScheduleShip								detailScheduleShipSelect;

	private DetailScheduleShip								detailScheduleShipSelected;

	@Wire
	private Component										detailShipProgram_download;

	@Wire
	private Component										filterCustomer;

	@Wire
	private Component										filterDateWork;

	@Wire
	private Component										filterMonth;

	@Wire
	private Component										filterRows;

	@Wire
	private Component										filterService;

	@Wire
	private Component										filterShift;

	@Wire
	private Component										filterShip;

	@Wire
	private Component										filterShipType;

	@Wire
	private Component										filterShipWorked;

	@Wire
	private Component										filterYear;

	@Wire
	private Datebox											first_down_detail;

	@Wire
	private Datebox											first_down_review;

	@Wire
	private Label											forecasting_avg_volume_month;

	@Wire
	private Label											forecasting_volume_month;

	private final SimpleDateFormat							format							= new SimpleDateFormat("dd-MM-yyyy");

	private final SimpleDateFormat							format_it_date					= new SimpleDateFormat("dd/MM/yyyy");

	private final SimpleDateFormat							format_month					= new SimpleDateFormat("MM");

	@Wire
	private Label											franchise_time_default;

	@Wire
	private Label											franchise_volume_default;

	@Wire
	private Label											franchise_volumeunde_tw_mct_default;

	@Wire
	private Label											franchise_volumeunderboard_default;

	@Wire
	private Label											franchise_volumeunderboard_sws_default;

	@Wire
	private Textbox											full_text_search;

	@Wire
	private Component										grid_scheduleShip_details;

	@Wire
	private Row												h_detail_period;
	@Wire
	private Row												h_program_period;
	@Wire
	private Listheader										handsColumn;
	@Wire
	public Intbox											handswork_Daily;
	@Wire
	private Intbox											handswork_program;
	@Wire
	public Label											handswork_program_Daily;
	@Wire
	private Label											hourReview;
	@Wire
	private Combobox										idCrane_review;

	@Wire
	private Label											infoShipNameAndShift;

	@Wire
	public Checkbox											initial_support_date;

	@Wire
	private Radio											invoice_no;
	@Wire
	private Radio											invoice_yes;
	@Wire
	private Intbox											invoicing_cycle_review;

	@Wire
	private Intbox											invoicing_cycle_search;

	@Wire
	private Datebox											last_down_detail;

	@Wire
	private Datebox											last_down_review;

	// used to collect details about programmed ship
	private List<DetailScheduleShip>						list_details_programmed_ship	= new ArrayList<>();

	// used to collect programmed ship
	private List<ScheduleShip>								list_programmed_ship			= new ArrayList<>();

	private List<ReviewShipWork>							list_review_work				= new ArrayList<>();

	private List<ReviewShipWorkAggregate>					list_review_work_aggregate		= new ArrayList<>();

	@Wire
	private Listbox											list_reviewDetailScheduleShip;

	@Wire
	private Listbox											list_ship_statistics;

	private List<DetailScheduleShip>						listDetailScheduleShip			= new ArrayList<>();

	// statistics - USED DOWNLOAD
	private List<ShipOverview>								listShipStatistics				= new ArrayList<>();

	@Wire
	private Listheader										mctColumn;

	@Wire
	private Component										menwork;

	@Wire
	private Intbox											menwork_activityh;

	@Wire
	private Component										menwork_activityhRow;

	@Wire
	public Intbox											menwork_Daily;

	@Wire
	private Intbox											menwork_program;

	@Wire
	public Label											menwork_program_Daily;

	@Wire
	private Label											menworkReview;

	@Wire
	private Label											messageUpdateRifMCT;

	@Wire
	private Button											modify_finalDetailScheduleShip_command;

	@Wire
	private Component										modify_Scheduleships_command;

	@Wire
	private Toolbarbutton									modify_scheduleShipsDetail_command;

	@Wire
	private Listheader										modifyColumnDetail;

	@Wire
	private Listheader										modifyColumnSchedule;

	@Wire
	private Listheader										modifyColumnScheduleShip;

	@Wire
	public Label											msgAlert;

	@Wire
	public Label											msgAlert_detail;

	@Wire
	private Textbox											note;

	@Wire
	private Textbox											note_review;

	@Wire
	public Textbox											note_schedule;

	@Wire
	public Textbox											notedetail;

	@Wire
	private Textbox											noteshipdetail;

	@Wire
	private Combobox										operation;

	@Wire
	public Combobox											operation_Daily;

	@Wire
	private Tabpanel										overview_statistics_ship;

	@Wire
	private Tab												overviewBap;

	@Wire
	private Tab												overviewBapAggregate;

	@Wire
	private Panel											panel_detail_program;

	@Wire
	private Component										panel_editor_review_details;

	@Wire
	private Datebox											person_down_review;

	private Person											person_logged;

	@Wire
	private Datebox											person_onboard_review;

	private PersonDAO										personDao;

	@Wire
	private Listheader										personsColumn;

	@Wire
	private Popup											popu_detail;

	@Wire
	public Popup											popup_detail;

	@Wire
	public Popup											popup_detail_Daily;

	@Wire
	public Popup											popup_review_detail;

	@Wire
	private Popup											popup_scheduleShip;

	@Wire
	private Popup											popup_ship;

	@Wire
	private Listbox											popup_shipDetail;

	@Wire
	private Button											print_program_videos;

	@Wire
	private Button											print_ShipScheduler;

	@Wire
	public Component										program_div;

	@Wire
	private Comboitem										program_item;

	@Wire
	private Component										program_ship_editor;

	@Wire
	private Combobox										rain_detail;

	@Wire
	private Combobox										rain_review;

	@Wire
	private Component										remove_select_year_detail;

	@Wire
	private Component										report_div;

	@Wire
	private Comboitem										report_review_ship_item;

	private ArrayList<ReportItem>							reportList						= new ArrayList<>();

	@Wire
	private Listbox											reportListboxContainer;

	@Wire
	private Component										review_div;

	@Wire
	private Component										reviewedTime;

	@Wire
	private Timebox											reviewTimeFrom;

	@Wire
	private Timebox											reviewTimeTo;

	@Wire
	private Checkbox										rif_customer_empty;

	@Wire
	private Textbox											rif_mct_review;

	@Wire
	private Label											rif_sws_review;

	@Wire
	private Listheader										rifSWSColumn;

	@Wire
	private Row												row_info_activity_ship;

	@Wire
	private Row												row_info_activity_ship_add;

	@Wire
	private Combobox										scheduler_type_selector;

	private ScheduleShip									scheduleShip_selected			= null;

	@Wire
	private Datebox											searchArrivalDateShipFrom;

	@Wire
	private Datebox											searchArrivalDateShipTo;

	@Wire
	private Datebox											searchDateShift;

	@Wire
	private A												selecetedShipName;

	@Wire
	private Combobox										select_customer;

	@Wire
	public Combobox											select_month_detail;

	@Wire
	public Combobox											select_shift;
	@Wire
	private Combobox										select_typeShip;
	@Wire
	private Combobox										select_workedShip;
	@Wire
	public Combobox											select_year_detail;
	@Wire
	private Combobox										selectCustomer;
	@Wire
	private Combobox										selectServiceDetail;
	@Wire
	private Combobox										servicetype;
	@Wire
	private Combobox										servicetype_schedule;
	@Wire
	private Label											serviceTypeDescriprion;

	@Wire
	private Combobox										shift;

	@Wire
	public Combobox											shift_Daily;

	@Wire
	private Datebox											shiftdate;

	@Wire
	public Datebox											shiftdate_Daily;

	@Wire
	private Component										shiftFilter;

	@Wire
	private Listheader										shiftNumberColumn;

	@Wire
	private Combobox										ship_activity;

	@Wire
	private Combobox										ship_activity_add;

	@Wire
	private Datebox											ship_arrival;

	@Wire
	public Datebox											ship_arrival_schedule;

	private IShipCache										ship_cache;

	@Wire
	private Textbox											ship_condition_search;

	@Wire
	private Combobox										ship_customer;

	@Wire
	private Combobox										ship_customer_add;

	@Wire
	private Datebox											ship_departure;

	@Wire
	public Datebox											ship_departure_schedule;

	@Wire
	private Timebox											ship_from;

	@Wire
	private Timebox											ship_from_detail;

	@Wire
	private Textbox											ship_line_search;

	@Wire
	private Combobox										ship_name;

	@Wire
	public Combobox											ship_name_schedule;

	@Wire
	private Textbox											ship_rif_mcf;

	@Wire
	private Label											ship_rif_sws;

	@Wire
	private Timebox											ship_to;

	@Wire
	private Timebox											ship_to_detail;

	@Wire
	private Textbox											ship_type_search;

	@Wire
	private Intbox											ship_volume;

	@Wire
	public Intbox											ship_volume_schedule;

	protected IShip											shipDao;

	@Wire
	private Listheader										shipNameColumun;

	@Wire
	private Label											shipNumberProgramShip_noWorked;

	@Wire
	private Label											shipNumberProgramShip_worked;

	@Wire
	private Textbox											shipRif_mcf;

	private IScheduleShip									shipSchedulerDao;

	@Wire
	private Intbox											shows_rows;

	@Wire
	private Combobox										sky_detail;

	@Wire
	private Combobox										sky_review;

	private IStatistics										statistic_dao;

	private IStatistics										statisticDAO;

	@Wire
	private Tab												statisticsShipTab;

	@Wire
	private Label											summary_detail_1;

	@Wire
	private Label											summary_detail_2;

	@Wire
	private Label											summary_detail_3;

	@Wire
	private Label											sumVolumeShipProgram;

	@Wire
	private Button											sw_addScheduleShipProgram;

	@Wire
	private Toolbarbutton									sw_link_reviewscheduleship;

	@Wire
	private Listbox											sw_list_reviewWork;

	@Wire
	private Listbox											sw_list_reviewWorkAggregate;

	@Wire
	private Listbox											sw_list_scheduleDetailShip;

	@Wire
	private Listbox											sw_list_scheduleShip;

	@Wire
	private Listbox											sw_list_scheduleShipProgram;

	private TasksDAO										taskDAO;
	@Wire
	private Combobox										temperature_detail;

	@Wire
	private Combobox										temperature_review;

	private final HashMap<Integer, TerminalProductivity>	terminalProductivityList		= new HashMap<>();

	@Wire
	private Listbox											terminalProductivy;

	@Wire
	private Textbox											text_search_rifMCT;

	@Wire
	private Intbox											text_search_rifSWS;

	@Wire
	private Listheader										textCol;

	@Wire
	private Doublebox										time_review;

	@Wire
	private Doublebox										time_review_franchise;

	@Wire
	private Label											TotalTimeWork;

	@Wire
	private Label											TotalVolume;

	@Wire
	private Label											TotalVolumeOnBoard;

	@Wire
	private Label											TotalVolumeOnBoard_sws;

	@Wire
	private Label											TotalVolumeTWMTC;

	@Wire
	private Doublebox										tp_apr;

	@Wire
	private Doublebox										tp_aug;

	@Wire
	private Doublebox										tp_dec;

	@Wire
	private Doublebox										tp_feb;

	@Wire
	private Doublebox										tp_gen;

	@Wire
	private Doublebox										tp_jul;

	@Wire
	private Doublebox										tp_jun;

	@Wire
	private Doublebox										tp_mar;

	@Wire
	private Doublebox										tp_may;

	@Wire
	private Doublebox										tp_nov;

	@Wire
	private Doublebox										tp_oct;

	@Wire
	private Doublebox										tp_sep;

	@Wire
	private Combobox										user;

	@Wire
	public Combobox											user_Daily;

	protected PersonDAO										userPrep;

	@Wire
	private Combobox										usersecond;

	@Wire
	private Combobox										usersecond_Daily;

	@Wire
	private Comboitem										verify_review_ship_item;

	@Wire
	private Intbox											volume_review;

	@Wire
	private Doublebox										volume_review_franchise;

	@Wire
	private Listheader										volumeColumn;

	@Wire
	private Listheader										volumeColumnScheduleShip;

	@Wire
	private Component										volumeGrid;

	@Wire
	private Listheader										volumeStatisticColumn;

	@Wire
	private Intbox											volumeunde_tw_mct_review;

	@Wire
	private Doublebox										volumeunde_tw_mct_review_franchise;

	@Wire
	private Intbox											volumeunderboard_review;

	@Wire
	private Doublebox										volumeunderboard_review_franchise;

	@Wire
	private Intbox											volumeunderboard_sws_review;

	@Wire
	private Doublebox										volumeunderboard_sws_review_franchise;

	@Wire
	private Combobox										wind_detail;

	@Wire
	private Combobox										wind_review;

	@Wire
	private Radiogroup										windydayGroup;

	@Wire
	private Radiogroup										workedGroup;

	@Wire
	private Label											working_cycle_review;

	@Listen("onClick = #add_finalDetailScheduleShip_command")
	public void addDetailFinalScheduleShip() throws ParseException {

		if ((this.sw_list_scheduleShip == null) || (this.sw_list_scheduleShip.getSelectedItem() == null)) {
			return;
		}

		final DetailFinalScheduleShip detailFinalScheduleShip = new DetailFinalScheduleShip();

		final DetailScheduleShip itm = this.sw_list_scheduleShip.getSelectedItem().getValue();
		detailFinalScheduleShip.setIddetailscheduleship(itm.getId());

		final Integer id_ship = itm.getId_ship();
		final Ship ship = this.shipDao.loadShip(id_ship);
		final Integer shift_no = itm.getShift();

		if ((ship != null) && (ship.getActivityh() != null) && ship.getActivityh().booleanValue()) {

			final Date date_from = this.reviewTimeFrom.getValue();
			Date date_to = this.reviewTimeTo.getValue();

			if (shift_no.equals(4) && this.check_last_shiftReview.isChecked()) {

				final Calendar cal_from = DateUtils.toCalendar(date_from);
				final Calendar cal_to = DateUtils.toCalendar(date_to);
				cal_to.set(Calendar.DAY_OF_YEAR, cal_from.get(Calendar.DAY_OF_YEAR));
				cal_to.add(Calendar.DAY_OF_YEAR, 1);
				date_to = cal_to.getTime();

			} else if (shift_no.equals(4) && !this.check_last_shiftReview.isChecked()) {
				final Calendar cal_from = DateUtils.toCalendar(date_from);
				final Calendar cal_to = DateUtils.toCalendar(date_to);
				cal_to.set(Calendar.DAY_OF_YEAR, cal_from.get(Calendar.DAY_OF_YEAR));
				date_to = cal_to.getTime();
			}

			detailFinalScheduleShip.setActivity_end(date_to);
			detailFinalScheduleShip.setActivity_start(date_from);

			this.calculateTimeReview();

			detailFinalScheduleShip.setMenwork_activityh(this.menwork_activityh.getValue());

		}

		// set crane
		Integer crn_val = null;
		if (this.idCrane_review.getSelectedItem() != null) {
			final Crane crane = this.idCrane_review.getSelectedItem().getValue();
			crn_val = crane.getId();
		}

		detailFinalScheduleShip.setId_crane(crn_val);

		detailFinalScheduleShip.setVolume(this.volume_review.getValue());
		detailFinalScheduleShip.setVolume_tw_mct(this.volumeunde_tw_mct_review.getValue());
		detailFinalScheduleShip.setVolumeunderboard(this.volumeunderboard_review.getValue());
		detailFinalScheduleShip.setVolumeunderboard_sws(this.volumeunderboard_sws_review.getValue());

		detailFinalScheduleShip.setNotedetail(this.note_review.getValue());
		detailFinalScheduleShip.setInvoicing_cycle(this.invoicing_cycle_review.getValue());

		// set time work
		final double time_worked = this.getDecimalValue();
		detailFinalScheduleShip.setTimework(time_worked);

		// set type crane
		final Boolean isgtw = this.crane_gtw_review.isChecked();
		detailFinalScheduleShip.setCrane_gtw(isgtw);

		// set franchise
		detailFinalScheduleShip.setFranchise_timework(this.time_review_franchise.getValue());
		detailFinalScheduleShip.setFranchise_volume(this.volume_review_franchise.getValue());
		detailFinalScheduleShip.setFranchise_volume_tw_mct(this.volumeunde_tw_mct_review_franchise.getValue());
		detailFinalScheduleShip.setFranchise_volumeunderboard(this.volumeunderboard_review_franchise.getValue());
		detailFinalScheduleShip.setFranchise_volumeunderboard_sws(this.volumeunderboard_sws_review_franchise.getValue());

		// insert new detail
		this.shipSchedulerDao.createDetailFinalScheduleShip(detailFinalScheduleShip);

		this.add_finalDetailScheduleShip_command.setVisible(true);
		this.modify_finalDetailScheduleShip_command.setVisible(false);

		final DetailScheduleShip detailSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (detailSelected == null) {
			return;
		}

		// set list review
		final List<DetailFinalScheduleShip> final_details = this.shipSchedulerDao
				.loadDetailFinalScheduleShipByIdDetailScheduleShip(detailSelected.getId());
		this.list_reviewDetailScheduleShip.setModel(new ListModelList<>(final_details));

	}

	@Listen("onClick = #addShipSchedule_command")
	public void addScheduleShipCommand() {
		if ((this.ship_name_schedule.getSelectedItem() == null) || (this.ship_arrival_schedule.getValue() == null)
				|| (this.ship_departure_schedule.getValue() == null)
				|| this.ship_arrival_schedule.getValue().after(this.ship_departure_schedule.getValue())) {

			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare i valori inseriti.", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			return;
		}

		final ScheduleShip ship_to_add = new ScheduleShip();

		final Ship ship = this.ship_name_schedule.getSelectedItem().getValue();

		final int idShip = ship.getId();

		ship_to_add.setIdship(idShip);
		ship_to_add.setVolume(this.ship_volume_schedule.getValue());

		if (this.ship_rif_mcf.getValue() != null) {
			ship_to_add.setRif_mct(this.ship_rif_mcf.getValue());
		}

		ship_to_add.setNote(this.note_schedule.getValue());

		Integer idService = null;
		if (this.servicetype_schedule.getSelectedItem() != null) {
			final Service serviceSelected = this.servicetype_schedule.getSelectedItem().getValue();
			idService = serviceSelected.getId();
		}

		ship_to_add.setId_service(idService);

		ship_to_add.setArrivaldate(this.ship_arrival_schedule.getValue());

		ship_to_add.setDeparturedate(this.ship_departure_schedule.getValue());

		// add customer
		if ((this.ship_customer_add.getSelectedItem() != null) && (this.ship_customer_add.getSelectedItem().getValue() != null)) {

			final Customer customer = this.ship_customer_add.getSelectedItem().getValue();

			ship_to_add.setCustomer_id(customer.getId());

		} else {

			ship_to_add.setCustomer_id(null);

		}

		// set ship activity
		if ((this.ship_activity_add.getSelectedItem() != null) && (this.ship_activity_add.getSelectedItem().getValue() != null)) {
			final Ship ship_activity = this.ship_activity_add.getSelectedItem().getValue();
			ship_to_add.setIdship_activity(ship_activity.getId());
		}

		this.shipSchedulerDao.createScheduleShip(ship_to_add);

		this.refreshProgram();

		this.program_ship_editor.setVisible(false);

	}

	@Listen("onClick = #add_scheduleShipsDetail_command")
	public void addScheduleShipsDetailCommand() {

		if (this.shift.getSelectedItem() == null) {

			return;
		}

		if (this.shiftdate.getValue() == null) {
			return;
		}

		// selected ship
		if (this.scheduleShip_selected == null) {
			return;
		}

		final DetailScheduleShip item = new DetailScheduleShip();

		if (this.user.getSelectedItem() != null) {
			final Person userOperative = (Person) this.user.getSelectedItem().getValue();
			item.setIduser(userOperative.getId());
		}

		if (this.usersecond.getSelectedItem() != null) {
			final Person secondUserOperative = (Person) this.usersecond.getSelectedItem().getValue();
			item.setIdseconduser(secondUserOperative.getId());
		}

		item.setNotedetail(this.noteshipdetail.getValue());

		// set shift_no
		final String shift_info = this.shift.getSelectedItem().getValue();
		final Integer shift_no = Integer.parseInt(shift_info);
		item.setShift(shift_no);

		item.setOperation(this.operation.getValue());

		item.setHandswork_program(this.handswork_program.getValue());
		item.setMenwork_program(this.menwork_program.getValue());
		item.setIdscheduleship(this.scheduleShip_selected.getId());
		item.setShiftdate(this.shiftdate.getValue());

		// have some mean only if activity ship
		item.setActivity_start(null);
		item.setActivity_end(null);

		// set general day info
		item.setWind(this.wind_detail.getValue());
		item.setRain(this.rain_detail.getValue());
		item.setSky(this.sky_detail.getValue());
		item.setFirst_down(this.first_down_detail.getValue());
		item.setLast_down(this.last_down_detail.getValue());
		item.setTemperature(this.temperature_detail.getValue());
		item.setWorked(true);

		// save info if ship is an activity
		if (this.scheduleShip_selected.getIdship_activity() != null) {

			final Date date_from = this.ship_from.getValue();
			Date date_to = this.ship_to.getValue();

			if ((date_from != null) && (date_to != null)) {

				final Integer shift = Integer.parseInt(this.shift.getValue().toString());

				if (shift.equals(4) && this.check_last_shift.isChecked()) {

					final Calendar cal_date_to = DateUtils.toCalendar(date_to);
					final Calendar cal_date_from = DateUtils.toCalendar(date_from);

					cal_date_to.set(Calendar.DAY_OF_YEAR, cal_date_from.get(Calendar.DAY_OF_YEAR));
					cal_date_to.add(Calendar.DAY_OF_YEAR, 1);
					date_to = cal_date_to.getTime();
				}

				item.setActivity_start(date_from);
				item.setActivity_end(date_to);
			}

		}

		this.shipSchedulerDao.createDetailScheduleShip(item);

		this.showDetailShipScheduleOnProgram();

	}

	/**
	 * Set avg productivity in satistics
	 *
	 * @param list_review_work_aggregate
	 * @return
	 */
	private Double calculateProductivityAVG(final List<ReviewShipWorkAggregate> list_review_work_aggregate) {
		this.avg_productivity.setValue(null);

		if (list_review_work_aggregate.size() == 0) {
			return 0.0;
		}

		Double productivityAVG = 0.0;

		for (final ReviewShipWorkAggregate item : list_review_work_aggregate) {
			if ((item.getVolume() != null) && (item.getTime_work() != null) && (item.getTime_work() != 0)) {
				productivityAVG += item.getVolume() / item.getTime_work();
			}

		}

		productivityAVG = productivityAVG / list_review_work_aggregate.size();

		return Utility.roundTwo(productivityAVG);

	}

	/**
	 * Set the label total volume value. (SHIPPRogram)
	 */
	private void calculateSummaryBAP() {

		Double sumVolume = 0.0;
		Double sumVolumeOnBoard = 0.0;
		Double sumVolumeOnBoard_sws = 0.0;
		Double sumVolumeMTC = 0.0;
		Double time_worked = 0.0;

		if ((this.list_review_work == null) || (this.list_review_work.size() == 0)) {
			return;
		}

		for (final ReviewShipWork itm_review : this.list_review_work) {

			if (itm_review.getVolume() != null) {
				sumVolume += itm_review.getVolumeLessFranchise();
			}

			if (itm_review.getVolumeunderboard() != null) {
				sumVolumeOnBoard += itm_review.getVolumeunderboardLessFranchise();
			}

			if (itm_review.getVolumeunderboard_sws() != null) {
				sumVolumeOnBoard_sws += itm_review.getVolumeunderboard_swsLessFranchise();
			}

			if (itm_review.getVolume_tw_mct() != null) {
				sumVolumeMTC += itm_review.getVolume_tw_mctLessFranchise();
			}

			if (itm_review.getTime_work() != null) {
				time_worked += itm_review.getTimeworkLessFranchise();
			}

		}

		// time work
		final String label_h = Utility.decimatToTime(time_worked);

		this.TotalVolume.setValue(sumVolume.toString());
		this.TotalVolumeOnBoard.setValue(sumVolumeOnBoard.toString());
		this.TotalVolumeOnBoard_sws.setValue(sumVolumeOnBoard_sws.toString());
		this.TotalVolumeTWMTC.setValue(sumVolumeMTC.toString());
		this.TotalTimeWork.setValue(label_h);
	}

	/**
	 * Define info
	 *
	 * @param date
	 * @param full_text_search
	 */
	private void calculateSummaryShipDetails(final List<DetailScheduleShip> list) {

		this.summary_detail_1.setValue("");
		this.summary_detail_2.setValue("");
		this.summary_detail_3.setValue("");

		Integer menworking_program = 0;
		Integer programmed_handswork = 0;
		Integer revised_handswork = 0;
		Integer menworking_review = 0;
		Integer worked_shift = 0;
		Integer not_worked_shift = 0;
		Integer deltaHands = 0;
		Integer deltaPersons = 0;
		Integer tot_turni_nl = 0;

		if ((list != null) && (list.size() != 0)) {

			final ArrayList<Integer> count_sws_work = new ArrayList<>();
			final ArrayList<Integer> count_sws_not_work = new ArrayList<>();
			final ArrayList<String> count_nl_shift = new ArrayList<>();

			for (final DetailScheduleShip itm_details : list) {

				// MEN WORK
				if (itm_details.getMenwork_program() != null) {
					menworking_program += itm_details.getMenwork_program();
				}

				// HANDS
				if (itm_details.getHandswork_program() != null) {
					programmed_handswork += itm_details.getHandswork_program();
				}

				if (itm_details.getHandswork() != null) {
					revised_handswork += itm_details.getHandswork();
				}

				// count ship
				if (!(itm_details.getId_ship() == null)) {

					final Ship ship = this.ship_cache.getShip(itm_details.getId_ship());

					if (ship.getNowork()) {

						if (!count_sws_not_work.contains(itm_details.getIdscheduleship())) {
							count_sws_not_work.add(itm_details.getIdscheduleship());
						}

						// count shift no work
						if ((itm_details.getWorked() != null) && itm_details.getWorked()) {

							final Date dt_itm = itm_details.getShiftdate();
							final Integer shift_itm = itm_details.getShift();
							if ((dt_itm != null) && (shift_itm != null)) {

								final String info_dt = Utility.convertToDateAndTime(dt_itm);
								final String itm = info_dt + "@" + shift_itm.toString();

								if (!count_nl_shift.contains(itm)) {
									count_nl_shift.add(itm);
								}

							}

						}

					} else {
						if (!count_sws_work.contains(itm_details.getIdscheduleship())) {
							count_sws_work.add(itm_details.getIdscheduleship());
						}
					}

					if (!ship.getActivityh()) {
						if (itm_details.getMenwork() != null) {
							menworking_review += itm_details.getMenwork();
						}
					}

				}

			}

			worked_shift = count_sws_work.size();
			not_worked_shift = count_sws_not_work.size();
			tot_turni_nl = count_nl_shift.size();

			deltaHands = revised_handswork - programmed_handswork;
			deltaPersons = menworking_review - menworking_program;

		}

		final String msg_1 = "Tot. Navi lav: " + worked_shift + ";  Tot. Navi non lav: " + not_worked_shift + "; Tot Turni NL: " + tot_turni_nl;
		final String msg_2 = "Tot. Mani P: " + programmed_handswork + ";  Tot. Mani C: " + revised_handswork + ";  Mani C-P: " + deltaHands;
		final String msg_3 = "Tot. Persone P: " + menworking_program + ";  Tot. Persone C: " + menworking_review + ";  Persone C-P: " + deltaPersons;

		this.summary_detail_1.setValue(msg_1);
		this.summary_detail_2.setValue(msg_2);
		this.summary_detail_3.setValue(msg_3);
	}

	/**
	 * Set the label total volume value.
	 */
	private void calculateSummaryShipProgram(final List<ScheduleShip> list, final Date date_from) {

		// set info
		this.shipNumberProgramShip_worked.setValue("");
		this.shipNumberProgramShip_noWorked.setValue("");
		this.sumVolumeShipProgram.setValue("");
		this.avgVolmueShipProgram.setValue("");

		this.forecasting_volume_month.setValue("");
		this.forecasting_avg_volume_month.setValue("");

		if ((list == null) || (list.size() == 0)) {
			return;
		}

		// define start and and date
		Date first_day = this.list_programmed_ship.get(0).getArrivaldate();
		Date last_day = this.list_programmed_ship.get(0).getDeparturedate();

		// define volume sum
		Integer sum_volume = 0;

		// define count
		Integer count_work_ship = 0;
		Integer count_not_work_ship = 0;

		for (final ScheduleShip itm_review : list) {

			Date arrival = itm_review.getArrivaldate();

			if (arrival == null) {
				continue;
			}

			arrival = DateUtils.truncate(arrival, Calendar.DATE);

			if ((date_from != null) && arrival.before(date_from)) {
				continue;
			}

			if (itm_review.getVolume() != null) {
				sum_volume += itm_review.getVolume();

			}

			// count ship
			if (!(itm_review.getIdship() == null)) {

				final Ship ship = this.ship_cache.getShip(itm_review.getIdship());
				if (!ship.getNowork()) {
					count_work_ship++;
				} else {
					count_not_work_ship++;
				}
			}

			// define min and max date
			if (arrival.before(first_day)) {
				first_day = arrival;
			}

			if (itm_review.getDeparturedate().after(last_day)) {
				last_day = itm_review.getDeparturedate();
			}

		}

		// calculate number of day
		final long days = Utility.getDayBetweenDate(first_day, last_day);

		// calculate daily avg
		Double avg = 0.0;
		if (days != 0) {
			avg = sum_volume.doubleValue() / days;
			avg = Utility.roundTwo(avg);
		}

		// ship number
		this.shipNumberProgramShip_worked.setValue("" + count_work_ship);
		this.shipNumberProgramShip_noWorked.setValue("" + count_not_work_ship);

		this.sumVolumeShipProgram.setValue(sum_volume.toString());
		this.avgVolmueShipProgram.setValue(avg.toString());

	}

	@Listen("onChange = #reviewTimeFrom,#reviewTimeTo; onOK = #reviewTimeFrom,#reviewTimeTo; onChanging = #reviewTimeFrom,#reviewTimeTo; onClick=#calculateTime;")
	public void calculateTimeReview() {

		final Date date_from = this.reviewTimeFrom.getValue();
		final Date date_to = this.reviewTimeTo.getValue();

		Double time = null;

		if ((date_from != null) && (date_to != null)) {
			time = (double) (date_to.getTime() - date_from.getTime());

			time = time / (1000 * 60 * 60);
		}

		this.time_review.setValue(Utility.roundTwo(time));

	}

	public void changeBehaviorForShify(final Integer shift_no, final Date date_shift, final Timebox ship_from, final Timebox ship_to,
			final Checkbox check_last_shift) {

		check_last_shift.setVisible(false);

		final Date truncDate = DateUtils.truncate(date_shift, Calendar.DATE);

		switch (shift_no.intValue()) {

			case 1: {

				// prepare period
				final Calendar cal_shift_1_time_from = DateUtils.toCalendar(truncDate);
				cal_shift_1_time_from.set(Calendar.HOUR_OF_DAY, 1);
				cal_shift_1_time_from.set(Calendar.MINUTE, 0);

				final Calendar cal_shift_1_time_to = DateUtils.toCalendar(truncDate);
				cal_shift_1_time_to.set(Calendar.HOUR_OF_DAY, 7);
				cal_shift_1_time_to.set(Calendar.MINUTE, 0);

				ship_from.setValue(cal_shift_1_time_from.getTime());
				ship_to.setValue(cal_shift_1_time_to.getTime());

				break;
			}

			case 2: {

				final Calendar cal_shift_2_time_from = DateUtils.toCalendar(truncDate);
				cal_shift_2_time_from.set(Calendar.HOUR_OF_DAY, 7);
				cal_shift_2_time_from.set(Calendar.MINUTE, 0);

				final Calendar cal_shift_2_time_to = DateUtils.toCalendar(truncDate);
				cal_shift_2_time_to.set(Calendar.HOUR_OF_DAY, 13);
				cal_shift_2_time_to.set(Calendar.MINUTE, 0);

				ship_from.setValue(cal_shift_2_time_from.getTime());
				ship_to.setValue(cal_shift_2_time_to.getTime());

				break;
			}

			case 3: {

				final Calendar cal_shift_3_time_from = DateUtils.toCalendar(truncDate);
				cal_shift_3_time_from.set(Calendar.HOUR_OF_DAY, 13);
				cal_shift_3_time_from.set(Calendar.MINUTE, 0);

				final Calendar cal_shift_3_time_to = DateUtils.toCalendar(truncDate);
				cal_shift_3_time_to.set(Calendar.HOUR_OF_DAY, 19);
				cal_shift_3_time_to.set(Calendar.MINUTE, 0);

				ship_from.setValue(cal_shift_3_time_from.getTime());
				ship_to.setValue(cal_shift_3_time_to.getTime());

				break;
			}

			case 4: {

				final Calendar cal_shift_4_time_from = DateUtils.toCalendar(truncDate);
				cal_shift_4_time_from.set(Calendar.HOUR_OF_DAY, 19);
				cal_shift_4_time_from.set(Calendar.MINUTE, 0);

				final Calendar cal_shift_4_time_to = DateUtils.toCalendar(truncDate);

				cal_shift_4_time_to.set(Calendar.HOUR_OF_DAY, 1);
				cal_shift_4_time_to.set(Calendar.MINUTE, 0);

				ship_from.setValue(cal_shift_4_time_from.getTime());
				ship_to.setValue(cal_shift_4_time_to.getTime());

				check_last_shift.setVisible(true);
				check_last_shift.setChecked(true);
				break;
			}

		}

	}

	@Listen("onChange = #shiftdate")
	public void checkShiftDate() {

		if ((ShipSchedulerComposer.this.shiftdate.getValue() != null)
				&& ((ShipSchedulerComposer.this.shiftdate.getValue().compareTo(ShipSchedulerComposer.this.scheduleShip_selected.getArrivaldate()) < 0)
						|| (ShipSchedulerComposer.this.shiftdate.getValue()
								.compareTo(ShipSchedulerComposer.this.scheduleShip_selected.getDeparturedate()) > 0))) {

			final String msg = "Attenzione: data arrivo nave " + this.format_it_date.format(this.scheduleShip_selected.getArrivaldate())
					+ ", data partenza nave " + this.format_it_date.format(this.scheduleShip_selected.getDeparturedate());
			this.msgAlert.setValue(msg);

			this.alertShiftDate.setVisible(true);

		} else {
			this.alertShiftDate.setVisible(false);

		}

	}

	@Listen("onChange = #shiftdate_Daily")
	public void checkShiftDate_detail() {
		if (this.sw_list_scheduleShip.getSelectedItem() != null) {
			this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();
		}

		if ((this.detailScheduleShipSelected != null) && (ShipSchedulerComposer.this.shiftdate_Daily.getValue() != null)
				&& ((ShipSchedulerComposer.this.shiftdate_Daily.getValue()
						.compareTo(ShipSchedulerComposer.this.detailScheduleShipSelected.getArrivaldate()) < 0)
						|| (ShipSchedulerComposer.this.shiftdate_Daily.getValue()
								.compareTo(ShipSchedulerComposer.this.detailScheduleShipSelected.getDeparturedate()) > 0))) {

			final String msg = "Attenzione: data arrivo nave " + this.format_it_date.format(this.detailScheduleShipSelected.getArrivaldate())
					+ ", data partenza nave " + this.format_it_date.format(this.detailScheduleShipSelected.getDeparturedate());

			this.msgAlert_detail.setValue(msg);

			this.alertShiftDate_detail.setVisible(true);
		} else {

			this.alertShiftDate_detail.setVisible(false);
		}

	}

	@Listen("onChange = #select_workedShip, #select_typeShip,#scheduler_type_selector, #searchArrivalDateShipFrom,#searchArrivalDateShipTo, #select_customer, #selectServiceDetail, #select_shift; onSelect = #bap_overview_tab; onOK=#searchArrivalDateShipFrom,#searchArrivalDateShipTo, #shows_rows, #full_text_search,#invoicing_cycle_search,#ship_type_search,#ship_line_search,#ship_condition_search; onClick = #remove_select_shift, #remove_searchDateShift, #remove_select_typeShip,#remove_select_workedShip,#remove_select_customer, #removeServiceFilterDetail;  onCheck = #datashift_period, #initial_support_date")
	public void defineSchedulerView() {

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		this.filterShift.setVisible(false);
		this.filterShip.setVisible(false);
		this.filterShipType.setVisible(false);
		this.filterShipWorked.setVisible(false);
		this.filterCustomer.setVisible(false);
		this.invoicing_cycle_search.setVisible(false);
		this.filterService.setVisible(false);
		this.text_search_rifSWS.setVisible(false);
		this.text_search_rifMCT.setVisible(false);

		this.filterDateWork.setVisible(true);
		this.remove_select_year_detail.setVisible(true);

		this.filterRows.setVisible(true);
		this.searchArrivalDateShipFrom.setVisible(true);
		this.searchArrivalDateShipTo.setVisible(true);
		this.filterYear.setVisible(true);
		this.filterMonth.setVisible(true);

		this.initial_support_date.setVisible(false);

		if (selected.equals(this.program_item)) {

			this.program_div.setVisible(true);
			this.detail_div.setVisible(false);
			this.review_div.setVisible(false);
			this.report_div.setVisible(false);

			this.filterCustomer.setVisible(true);
			this.filterService.setVisible(true);
			this.text_search_rifSWS.setVisible(true);
			this.text_search_rifMCT.setVisible(true);

			this.initial_support_date.setVisible(true);

			this.refreshProgram();

		} else if (selected.equals(this.detail_item)) {

			this.program_div.setVisible(false);
			this.detail_div.setVisible(true);
			this.review_div.setVisible(false);
			this.report_div.setVisible(false);

			this.filterShift.setVisible(true);
			this.filterShip.setVisible(true);
			this.filterShipType.setVisible(true);
			this.filterShipWorked.setVisible(true);
			this.filterCustomer.setVisible(true);
			this.filterService.setVisible(true);
			this.text_search_rifSWS.setVisible(true);
			this.text_search_rifMCT.setVisible(true);

			// force over data shift
			this.datashift_period_compo.setVisible(true);

			String rifMCT;
			if (this.rif_customer_empty.isChecked()) {
				rifMCT = ShipSchedulerComposer.rif_mct_empty;
			} else {
				rifMCT = this.text_search_rifMCT.getValue();
			}

			final Integer rif_sws = this.text_search_rifSWS.getValue();

			if ((rifMCT != null) && rifMCT.equals("")) {
				rifMCT = null;
			}

			if ((rifMCT != null) || (rif_sws != null)) {
				this.searchRifSWS_MCT();
			} else {
				this.refreshDetail();
			}

		} else if (selected.equals(this.verify_review_ship_item)) {

			this.program_div.setVisible(false);
			this.detail_div.setVisible(false);
			this.review_div.setVisible(true);
			this.report_div.setVisible(false);

			final Tab selectedTab = this.bap_overview_tab.getSelectedTab();

			this.text_search_rifSWS.setVisible(false);
			this.text_search_rifMCT.setVisible(false);
			this.invoicing_cycle_search.setVisible(false);
			this.filterCustomer.setVisible(false);
			this.filterService.setVisible(false);
			this.filterShift.setVisible(false);
			this.filterShip.setVisible(false);
			this.filterShipType.setVisible(false);
			this.filterShipWorked.setVisible(false);

			// force over data shift
			this.datashift_period_compo.setVisible(false);

			if (selectedTab == this.overviewBap) {
				this.text_search_rifSWS.setVisible(true);
				this.text_search_rifMCT.setVisible(true);
				this.invoicing_cycle_search.setVisible(true);
				this.filterService.setVisible(true);
				this.filterShift.setVisible(true);
				this.filterShip.setVisible(true);
			} else if (selectedTab == this.overviewBapAggregate) {
				this.text_search_rifSWS.setVisible(true);
				this.text_search_rifMCT.setVisible(true);
				this.invoicing_cycle_search.setVisible(true);
				this.filterService.setVisible(true);
			}

			this.refreshBAP();

		} else if (selected.equals(this.report_review_ship_item)) {

			this.program_div.setVisible(false);
			this.detail_div.setVisible(false);
			this.review_div.setVisible(false);
			this.report_div.setVisible(true);

			this.filterRows.setVisible(false);
			this.text_search_rifSWS.setVisible(false);
			this.text_search_rifMCT.setVisible(false);
			this.invoicing_cycle_search.setVisible(false);
			this.filterCustomer.setVisible(false);
			this.filterService.setVisible(false);
			this.filterShift.setVisible(false);
			this.filterShip.setVisible(false);
			this.filterShipType.setVisible(false);
			this.filterShipWorked.setVisible(false);

			this.filterDateWork.setVisible(false);
			this.remove_select_year_detail.setVisible(false);
			this.filterYear.setVisible(true);
			this.filterMonth.setVisible(false);
			
			// set invoice report
			this.invoice_yes.setChecked(Boolean.TRUE);

			this.refreshReport();
		}
	}

	private void definingPrintableVersion() {

		// set visibility print button
		this.print_ShipScheduler.setVisible(true);
		this.print_program_videos.setVisible(false);

		// set visibility download csv buttons
		this.detailShipProgram_download.setVisible(false);

		// set listbox widt
		this.sw_list_scheduleShip.setWidth("950px");
		this.sw_list_scheduleShipProgram.setWidth("950px");
		this.list_reviewDetailScheduleShip.setWidth("950px");
		this.sw_list_reviewWork.setWidth("950px");
		this.list_ship_statistics.setWidth("950px");
		this.bap_overview_tab.setWidth("950px");

		// ship program view
		this.sw_list_scheduleShip.setWidth("950px");

		// report view
		this.reportListboxContainer.setWidth("950px");

		// tab statistiche nave
		this.volumeStatisticColumn.setWidth("45px");

		this.sw_addScheduleShipProgram.setVisible(false);

		// set visibility of modify columns in listbox
		this.modifyColumnSchedule.setVisible(false);
		this.modifyColumnScheduleShip.setVisible(false);
		this.modifyColumnDetail.setVisible(false);

		this.detail_div.setVisible(false);
		this.program_div.setVisible(false);
		this.review_div.setVisible(false);
		this.report_div.setVisible(false);

		this.scheduler_type_selector.setSelectedItem(null);

		this.terminalProductivy.setVisible(false);
	}

	@Listen("onClick = #deleteDetailFinalScheduleShip")
	public void deleteDetailFinalScheduleShip() throws ParseException {

		if (this.list_reviewDetailScheduleShip.getSelectedItem() == null) {
			return;
		}

		final DetailFinalScheduleShip detailFinal = (DetailFinalScheduleShip) this.list_reviewDetailScheduleShip.getSelectedItem().getValue();

		this.shipSchedulerDao.deleteDetailFinalScheduleShipById(detailFinal.getId());

		this.showReviewShipPopup();

	}

	/**
	 * Delete info
	 *
	 */
	private void deleteDetailship() {
		if (this.detailScheduleShipSelected == null) {
			return;
		}

		this.shipSchedulerDao.deleteDetailScheduleShip(this.detailScheduleShipSelected.getId());

		this.refreshDetail();
	}

	@Listen("onClick = #sw_link_deleteDetailship")
	public void deleteDetailshipInListDetail() {

		this.detailScheduleShipSelected = this.sw_list_scheduleDetailShip.getSelectedItem().getValue();
		if ((this.detailScheduleShipSelected != null) && (this.scheduleShip_selected != null)) {

			this.shipSchedulerDao.deleteDetailScheduleShip(this.detailScheduleShipSelected.getId());

			this.listDetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

			this.sw_list_scheduleDetailShip.setModel(new ListModelList<>(this.listDetailScheduleShip));
		}

	}

	@Override
	public void doFinally() throws Exception {

		// set info about person logged
		this.person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// SHOW SHIFT CONFIGURATOR
		this.getSelf().addEventListener(ZkEventsTag.onShowShipScheduler, new StartingEvent());

		// set year in combobox
		final Integer todayYear = Utility.getYear(Calendar.getInstance().getTime());

		// define year to select in combo
		final ArrayList<String> years = new ArrayList<>();
		for (Integer i = 2014; i <= (todayYear + 2); i++) {
			years.add(i.toString());

		}

		ShipSchedulerComposer.this.configurationDao = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
		final List<Service> serviceList = this.configurationDao.selectService(null, null, null);

		this.servicetype.setModel(new ListModelList<>(serviceList));
		this.servicetype_schedule.setModel(new ListModelList<>(serviceList));

		// define info for search
		final ArrayList<Service> services_on_search = new ArrayList<>();
		final Service service_empty = new Service();
		service_empty.setId(-666);
		service_empty.setDescription("VUOTO");
		service_empty.setName("VUOTO");
		services_on_search.addAll(serviceList);
		services_on_search.add(service_empty);
		this.selectServiceDetail.setModel(new ListModelList<>(services_on_search));

		this.select_year_detail.setModel(new ListModelList<>(years));
		this.select_year_detail.setModel(new ListModelList<>(years));

		this.statisticDAO = (IStatistics) SpringUtil.getBean(BeansTag.STATISTICS);

		this.taskDAO = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

		// check for printable version
		final String att_print = Executions.getCurrent().getParameter(ShipSchedulerComposer.PRINT_PROGRAM);

		if (att_print != null) {

			Events.sendEvent(ZkEventsTag.onShowShipScheduler, this.getSelf(), null);

		}

		this.craneListModel = this.configurationDao.getCrane(null, null, null, null);
		this.idCrane_review.setModel(new ListModelList<>(this.craneListModel));

		// Set customer combobox in report view
		ShipSchedulerComposer.this.customerDAO = (ICustomerDAO) SpringUtil.getBean(BeansTag.CUSTOMER_DAO);
		final List<Customer> customerList = this.customerDAO.listAllCustomers();
		this.selectCustomer.setModel(new ListModelList<>(customerList));
		this.select_customer.setSelectedItem(null);

	}

	@Listen("onClick = #detailShipProgram_download")
	public void downloadCSV() {
		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected.equals(this.program_item)) {

			if (this.list_programmed_ship.size() != 0) {
				final StringBuilder builder = UtilityCSV.downloadCSV_ScheduleProgramShip(this.list_programmed_ship, this.customerDAO);

				Filedownload.save(builder.toString(), "application/text", "programmazione_navi.csv");
			}

		} else if (selected.equals(this.detail_item)) {

			if (this.list_details_programmed_ship.size() != 0) {
				final StringBuilder builder = UtilityCSV.downloadCSV_DetailProgramShip(this.list_details_programmed_ship, this.customerDAO);

				Filedownload.save(builder.toString(), "application/text", "dettaglio_giornaliero.csv");
			}

		} else if (selected.equals(this.verify_review_ship_item)) {

			if (this.overviewBap.isSelected() && (this.list_review_work.size() != 0)) {

				final StringBuilder builder = UtilityCSV.downloadCSVReviewShipWork(this.list_review_work);
				Filedownload.save(builder.toString(), "application/text", "bap.csv");

			} else if (this.overviewBapAggregate.isSelected() && (this.list_review_work_aggregate.size() != 0)) {

				final StringBuilder builder = UtilityCSV.downloadCSVReviewShipWorkAggregate(this.list_review_work_aggregate);
				Filedownload.save(builder.toString(), "application/text", "bap_aggregator.csv");

			} else if (this.statisticsShipTab.isSelected() && (this.listShipStatistics != null) && (this.listShipStatistics.size() != 0)) {

				final StringBuilder builder = UtilityCSV.downloadCSVShipStatistics(this.listShipStatistics);
				Filedownload.save(builder.toString(), "application/text", "bap_aggregator_users.csv");

			}

		} else if (selected.equals(this.report_review_ship_item)) {
			if (this.reportList.size() != 0) {
				final StringBuilder builder = UtilityCSV.downloadCSV_ReportShip(this.reportListboxContainer, this.shipDao);

				Filedownload.save(builder.toString(), "application/text", "report_lavorazione_navi.csv");
			}

		}
	}

	/**
	 * Forecasting ship program
	 *
	 * @param list
	 */
	private void forecastingForMonthShipProgram(final Date date_from, final Integer service_id) {

		// define forecasting
		final Calendar cal_period = Calendar.getInstance();
		cal_period.set(Calendar.DAY_OF_MONTH, cal_period.getActualMinimum(Calendar.DAY_OF_MONTH));
		final Date first_day_month = cal_period.getTime();

		cal_period.set(Calendar.DAY_OF_MONTH, cal_period.getActualMaximum(Calendar.DAY_OF_MONTH));
		final Date last_day_month = cal_period.getTime();

		final List<ScheduleShip> list_month = this.shipSchedulerDao.searchScheduleShip(first_day_month, last_day_month, null, null, null, service_id,
				null, null, null, null, null);

		Integer sum_current_volume_month = 0;

		// get current calendar
		final Calendar current_cal = Calendar.getInstance();

		// current month days
		final int month_days = current_cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		for (final ScheduleShip itm_review : list_month) {

			Date arrival = itm_review.getArrivaldate();

			if (arrival == null) {
				continue;
			}

			arrival = DateUtils.truncate(arrival, Calendar.DATE);

			if (arrival.before(date_from)) {
				continue;
			}

			if (itm_review.getVolume() != null) {

				// get info about volume on month until now
				if (itm_review.getDeparturedate() != null) {
					final Calendar cal = DateUtils.toCalendar(itm_review.getDeparturedate());

					if ((cal.get(Calendar.MONTH) == current_cal.get(Calendar.MONTH)) && !cal.after(current_cal)) {
						sum_current_volume_month += itm_review.getVolume();
					}
				}
			}

		}

		// define forecasting
		// get day of month
		final Integer day_of_month = current_cal.get(Calendar.DAY_OF_MONTH);
		// get current daily
		final Double avg_daily = sum_current_volume_month.doubleValue() / day_of_month.doubleValue();
		// get naive forecasting
		final Integer remain_days = month_days - day_of_month;
		Double month_forecasting = remain_days.doubleValue() * avg_daily;

		month_forecasting = Utility.roundTwo(month_forecasting);

		this.forecasting_volume_month.setValue(month_forecasting.toString());

		// Calculate avg on month
		Double avg_month_forecasting = month_forecasting / month_days;
		avg_month_forecasting = Utility.roundTwo(avg_month_forecasting);
		this.forecasting_avg_volume_month.setValue(avg_month_forecasting.toString());
	}
	
	/**
	 * Get decimal value
	 *
	 * @return
	 */
	private double getDecimalValue() {

		if (this.time_review.getValue() == null) {
			return 0;
		}

		else {
			return this.time_review.getValue();
		}

	}
	
	/**
	 * Return if data are defined by invoice or by work *
	 *
	 * @return
	 */
	private boolean getInvoce() {

		if (this.invoice_yes.isChecked()) {
			return true;
		}
		
		if (this.invoice_no.isChecked()) {
			return false;
		}
		
		// no check
		this.invoice_yes.setChecked(true);
		return true;

	}

	private Integer getSelectedShift() {
		Integer shiftSelected = null;
		if (this.select_shift.getSelectedItem() != null) {
			shiftSelected = this.select_shift.getSelectedIndex() + 1;
		}
		return shiftSelected;
	}

	/**
	 * Reset value popup
	 */
	@Listen("onClick = #add_details_panels_review")
	public void initPopupReviewDetail() {
		this.idCrane_review.setSelectedItem(null);

		this.volume_review.setValue(0);
		this.volumeunderboard_review.setValue(0);
		this.volumeunderboard_sws_review.setValue(0);
		this.volumeunde_tw_mct_review.setValue(0);

		// set franchise item
		this.time_review_franchise.setValue(0);
		this.volume_review_franchise.setValue(0);
		this.volumeunderboard_review_franchise.setValue(0);
		this.volumeunderboard_sws_review_franchise.setValue(0);
		this.volumeunde_tw_mct_review_franchise.setValue(0);

		this.note_review.setValue(null);

		this.calculateTimeReview();
		this.menwork_activityh.setValue(null);

		this.crane_gtw_review.setChecked(Boolean.FALSE);

		this.add_finalDetailScheduleShip_command.setVisible(true);
		this.modify_finalDetailScheduleShip_command.setVisible(false);

		this.volumeGrid.setVisible(true);

		if (this.sw_list_scheduleShip.getSelectedItem() != null) {
			final DetailScheduleShip itm = this.sw_list_scheduleShip.getSelectedItem().getValue();
			final Ship ship = this.shipDao.loadShip(itm.getId_ship());

			if ((ship != null) && (ship.getActivityh() != null) && ship.getActivityh().booleanValue()) {
				this.volumeGrid.setVisible(false);
			}
		}

	}

	private void initTerminalProductivity(final Integer year) {
		final List<TerminalProductivity> list = this.statisticDAO.loadTerminalProductivityYear(year);

		if (this.terminalProductivityList == null) {
			return;
		}

		this.terminalProductivityList.clear();

		for (final TerminalProductivity tp : list) {

			final Integer month = tp.getMonth_tp();

			this.terminalProductivityList.put(month, tp);
		}

	}

	private void mainSchedulerView(final String version) {
		if (ShipSchedulerComposer.this.person_logged.isViewerOnly()) {
			// set initial item if user is a viewer only user
			ShipSchedulerComposer.this.scheduler_type_selector.setSelectedItem(ShipSchedulerComposer.this.detail_item);
		}

		// get the DAOs
		ShipSchedulerComposer.this.shipSchedulerDao = (IScheduleShip) SpringUtil.getBean(BeansTag.SCHEDULE_SHIP_DAO);
		ShipSchedulerComposer.this.shipDao = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);
		ShipSchedulerComposer.this.personDao = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);
		ShipSchedulerComposer.this.customerDAO = (ICustomerDAO) SpringUtil.getBean(BeansTag.CUSTOMER_DAO);
		ShipSchedulerComposer.this.statistic_dao = (IStatistics) SpringUtil.getBean(BeansTag.STATISTICS);

		// get ship cache
		this.ship_cache = (IShipCache) SpringUtil.getBean(BeansTag.SHIP_CACHE);

		final List<Ship> all_ship = ShipSchedulerComposer.this.shipDao.listAllShip(null);

		// add item in combobox ship name
		if (all_ship.size() == 0) {
			Messagebox.show("Inserire almeno una nave prima di procedere alla programmazione!", "INFO", Messagebox.OK, Messagebox.INFORMATION);
		}

		ShipSchedulerComposer.this.ship_name.setModel(new ListModelList<>(all_ship));
		ShipSchedulerComposer.this.ship_name_schedule.setModel(new ListModelList<>(all_ship));
		ShipSchedulerComposer.this.ship_activity.setModel(new ListModelList<>(all_ship));
		ShipSchedulerComposer.this.ship_activity_add.setModel(new ListModelList<>(all_ship));

		// add item operative users in combobox user
		final List<Person> list_op_person = ShipSchedulerComposer.this.personDao.listOperativePerson();
		final ListModel<Person> modelComboBox_User = new ListModelList<>(list_op_person);
		ShipSchedulerComposer.this.user.setModel(modelComboBox_User);

		final ListModel<Person> modelComboBox_UserSecond = new ListModelList<>(list_op_person);
		ShipSchedulerComposer.this.usersecond.setModel(modelComboBox_UserSecond);

		final ListModel<Person> modelComboBox_UserDaily = new ListModelList<>(list_op_person);
		ShipSchedulerComposer.this.user_Daily.setModel(modelComboBox_UserDaily);

		final ListModel<Person> modelComboBox_UserSecondDaily = new ListModelList<>(list_op_person);
		ShipSchedulerComposer.this.usersecond_Daily.setModel(modelComboBox_UserSecondDaily);

		// set info about customer combo
		final List<Customer> list_customer = ShipSchedulerComposer.this.customerDAO.listAllCustomers();

		final ListModel<Customer> model_customer = new ListModelList<>(list_customer);
		ShipSchedulerComposer.this.ship_customer.setModel(model_customer);

		final ListModel<Customer> model_customer_add = new ListModelList<>(list_customer);
		ShipSchedulerComposer.this.ship_customer_add.setModel(model_customer_add);

		ShipSchedulerComposer.this.select_customer.setModel(new ListModelList<>(list_customer));

		ShipSchedulerComposer.this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

		if ((version == null) || !version.equals(ShipSchedulerComposer.PRINT_PROGRAM)) {

			ShipSchedulerComposer.this.scheduler_type_selector.setSelectedItem(ShipSchedulerComposer.this.detail_item);

			ShipSchedulerComposer.this.setInitialView();

		} else {

			this.definingPrintableVersion();

		}

	}

	@Listen("onClick = #modify_finalDetailScheduleShip_command")
	public void modify_finalDetailScheduleShip_command() throws ParseException {

		if (this.list_reviewDetailScheduleShip.getSelectedItem() == null) {
			return;
		}

		// set button command
		this.add_finalDetailScheduleShip_command.setVisible(false);
		this.modify_finalDetailScheduleShip_command.setVisible(true);

		final DetailFinalScheduleShip detailFinal = (DetailFinalScheduleShip) this.list_reviewDetailScheduleShip.getSelectedItem().getValue();

		Integer crn_val = null;
		if (this.idCrane_review.getSelectedItem() != null) {
			final Crane crane = this.idCrane_review.getSelectedItem().getValue();
			crn_val = crane.getId();
		}

		detailFinal.setId_crane(crn_val);

		detailFinal.setCrane_gtw(this.crane_gtw_review.isChecked());
		detailFinal.setVolume(this.volume_review.getValue());
		detailFinal.setVolumeunderboard(this.volumeunderboard_review.getValue());
		detailFinal.setVolumeunderboard_sws(this.volumeunderboard_sws_review.getValue());
		detailFinal.setVolume_tw_mct(this.volumeunde_tw_mct_review.getValue());
		detailFinal.setTimework(this.time_review.getValue());
		detailFinal.setNotedetail(this.note_review.getValue());
		detailFinal.setInvoicing_cycle(this.invoicing_cycle_review.getValue());

		// set franchise
		detailFinal.setFranchise_timework(this.time_review_franchise.getValue());
		detailFinal.setFranchise_volume(this.volume_review_franchise.getValue());
		detailFinal.setFranchise_volume_tw_mct(this.volumeunde_tw_mct_review_franchise.getValue());
		detailFinal.setFranchise_volumeunderboard(this.volumeunderboard_review_franchise.getValue());
		detailFinal.setFranchise_volumeunderboard_sws(this.volumeunderboard_sws_review_franchise.getValue());

		// set info about activity
		final DetailScheduleShip detailScheduleship = this.sw_list_scheduleShip.getSelectedItem().getValue();
		detailFinal.setActivity_end(null);
		detailFinal.setActivity_start(null);

		final Integer id_ship = detailScheduleship.getId_ship();
		final Integer shift_no = detailScheduleship.getShift();
		final Ship ship = this.shipDao.loadShip(id_ship);

		if ((ship != null) && (ship.getActivityh() != null) && ship.getActivityh().booleanValue()) {

			final Date date_from = this.reviewTimeFrom.getValue();
			Date date_to = this.reviewTimeTo.getValue();

			if (shift_no.equals(4) && this.check_last_shiftReview.isChecked()) {

				final Calendar cal_from = DateUtils.toCalendar(date_from);
				final Calendar cal_to = DateUtils.toCalendar(date_to);
				cal_to.set(Calendar.DAY_OF_YEAR, cal_from.get(Calendar.DAY_OF_YEAR));
				cal_to.add(Calendar.DAY_OF_YEAR, 1);
				date_to = cal_to.getTime();

			} else if (shift_no.equals(4) && !this.check_last_shiftReview.isChecked()) {
				final Calendar cal_from = DateUtils.toCalendar(date_from);
				final Calendar cal_to = DateUtils.toCalendar(date_to);
				cal_to.set(Calendar.DAY_OF_YEAR, cal_from.get(Calendar.DAY_OF_YEAR));
				date_to = cal_to.getTime();
			}

			detailFinal.setActivity_end(date_to);
			detailFinal.setActivity_start(date_from);

			detailFinal.setMenwork_activityh(this.menwork_activityh.getValue());

		}

		this.shipSchedulerDao.updateDetailFinalScheduleShip(detailFinal);

		// close editor
		final int id_itm = detailFinal.getIddetailscheduleship();
		final List<DetailFinalScheduleShip> final_details = this.shipSchedulerDao.loadDetailFinalScheduleShipByIdDetailScheduleShip(id_itm);
		this.list_reviewDetailScheduleShip.setModel(new ListModelList<>(final_details));
		this.panel_editor_review_details.setVisible(false);

	}

	@Listen("onClick = #modifyDetailFinalScheduleShip")
	public void modifyDetailFinalScheduleShip() {
		if ((this.sw_list_scheduleShip.getSelectedItem() == null) || (this.list_reviewDetailScheduleShip.getSelectedItem() == null)) {

			return;

		}

		final DetailFinalScheduleShip detailFinal = (DetailFinalScheduleShip) this.list_reviewDetailScheduleShip.getSelectedItem().getValue();

		this.rif_mct_review.setValue(null);

		final DetailScheduleShip detailScheduleShip = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (detailScheduleShip != null) {
			final ScheduleShip scheduleShip = this.shipSchedulerDao.loadScheduleShip(detailScheduleShip.getIdscheduleship());
			if ((scheduleShip != null) && (scheduleShip.getRif_mct() != null)) {
				this.rif_mct_review.setValue(scheduleShip.getRif_mct());
			}
		}

		final List<Crane> crane = this.configurationDao.getCrane(detailFinal.getId_crane(), null, null, null);
		if ((crane != null) && (this.craneListModel != null)) {
			for (final Crane item : this.craneListModel) {
				if (item.getId().equals(crane.get(0).getId())) {
					this.idCrane_review.setValue(item.getNumber().toString());
				}
			}
		}

		this.crane_gtw_review.setChecked(detailFinal.getCrane_gtw());
		this.volume_review.setValue(detailFinal.getVolume());
		this.volumeunderboard_review.setValue(detailFinal.getVolumeunderboard());
		this.volumeunderboard_sws_review.setValue(detailFinal.getVolumeunderboard_sws());
		this.volumeunde_tw_mct_review.setValue(detailFinal.getVolume_tw_mct());
		this.time_review.setValue(detailFinal.getTimework());
		this.note_review.setValue(detailFinal.getNotedetail());
		this.invoicing_cycle_review.setValue(detailFinal.getInvoicing_cycle());

		// set franchise
		this.time_review_franchise.setValue(detailFinal.getFranchise_timework());
		this.volume_review_franchise.setValue(detailFinal.getFranchise_volume());
		this.volumeunde_tw_mct_review_franchise.setValue(detailFinal.getFranchise_volume_tw_mct());
		this.volumeunderboard_sws_review_franchise.setValue(detailFinal.getFranchise_volumeunderboard_sws());
		this.volumeunderboard_review_franchise.setValue(detailFinal.getFranchise_volumeunderboard());

		this.add_finalDetailScheduleShip_command.setVisible(false);

		this.modify_finalDetailScheduleShip_command.setVisible(true);
		this.panel_editor_review_details.setVisible(true);

		// SET ACTIVIY

		this.reviewTimeFrom.setValue(null);
		this.reviewTimeTo.setValue(null);
		this.menwork_activityh.setValue(null);

		this.reviewedTime.setVisible(false);
		this.menwork_activityhRow.setVisible(false);
		this.menwork.setVisible(true);
		this.check_last_shiftReview.setChecked(false);
		this.check_last_shiftReview.setVisible(false);

		// define if ship activity fields need to be disabled
		final Integer id_ship = detailScheduleShip.getId_ship();
		final Ship ship = this.shipDao.loadShip(id_ship);

		if ((ship != null) && (ship.getActivityh() != null) && ship.getActivityh().booleanValue()) {

			this.check_last_shiftReview.setVisible(false);
			this.check_last_shiftReview.setChecked(false);

			final Date from = detailFinal.getActivity_start();
			final Date to = detailFinal.getActivity_end();

			this.reviewTimeFrom.setValue(from);
			this.reviewTimeTo.setValue(to);

			this.menwork_activityh.setValue(detailFinal.getMenwork_activityh());

			if (detailScheduleShip.getShift().equals(4)) {
				this.check_last_shiftReview.setVisible(true);
				if ((from != null) && (to != null)) {
					final Calendar cal_from = DateUtils.toCalendar(from);
					final Calendar cal_to = DateUtils.toCalendar(to);
					final int day_from = cal_from.get(Calendar.DAY_OF_YEAR);
					final int day_to = cal_to.get(Calendar.DAY_OF_YEAR);

					final int gap_day = day_to - day_from;
					if (gap_day == 1) {
						this.check_last_shiftReview.setChecked(true);
					}

				}
			}

			this.reviewedTime.setVisible(true);
			this.menwork_activityhRow.setVisible(true);
			this.menwork.setVisible(false);

		}

	}

	@Listen("onClick = #sw_link_modifyDetailship")
	public void modifyDetailShip() {

		this.detailScheduleShipSelected = this.sw_list_scheduleDetailShip.getSelectedItem().getValue();

		if (this.detailScheduleShipSelected == null) {
			return;
		}

		if (this.scheduleShip_selected == null) {
			return;
		}

		// define general value
		this.first_down_detail.setValue(this.detailScheduleShipSelected.getFirst_down());
		this.last_down_detail.setValue(this.detailScheduleShipSelected.getLast_down());

		this.rain_detail.setSelectedItem(null);
		for (final Comboitem itm : this.rain_detail.getItems()) {
			if (itm.getValue().equals(this.detailScheduleShipSelected.getRain())) {
				this.rain_detail.setSelectedItem(itm);
				break;
			}
		}

		this.sky_detail.setSelectedItem(null);
		for (final Comboitem itm : this.sky_detail.getItems()) {
			if (itm.getValue().equals(this.detailScheduleShipSelected.getSky())) {
				this.sky_detail.setSelectedItem(itm);
				break;
			}
		}

		this.wind_detail.setSelectedItem(null);
		for (final Comboitem itm : this.wind_detail.getItems()) {
			if (itm.getValue().equals(this.detailScheduleShipSelected.getWind())) {
				this.wind_detail.setSelectedItem(itm);
				break;
			}
		}

		this.temperature_detail.setSelectedItem(null);
		for (final Comboitem itm : this.temperature_detail.getItems()) {
			if (itm.getValue().equals(this.detailScheduleShipSelected.getTemperature())) {
				this.temperature_detail.setSelectedItem(itm);
				break;
			}
		}

		this.shiftdate.setValue(this.detailScheduleShipSelected.getShiftdate());

		// select first user
		this.user.setSelectedItem(null);

		Person person = this.personDao.loadPerson(this.detailScheduleShipSelected.getIduser());

		if (person != null) {
			final List<Comboitem> listItem = this.user.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Person) {
					final Person current_person = item.getValue();
					if (person.equals(current_person)) {
						this.user.setSelectedItem(item);
						break;
					}
				}

			}

		}

		// select second user
		this.usersecond.setSelectedItem(null);

		person = this.personDao.loadPerson(this.detailScheduleShipSelected.getIdseconduser());

		if (person != null) {
			final List<Comboitem> listItem = this.usersecond.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Person) {
					final Person current_person = item.getValue();
					if (person.equals(current_person)) {
						this.usersecond.setSelectedItem(item);
						break;
					}
				}

			}

		}

		// select shift
		final Integer shift_no = this.detailScheduleShipSelected.getShift();
		if (shift_no != null) {
			this.shift.setValue(shift_no.toString());
		} else {
			this.shift.setValue(null);
		}

		this.operation.setValue(this.detailScheduleShipSelected.getOperation());

		this.handswork_program.setValue(this.detailScheduleShipSelected.getHandswork_program());
		this.menwork_program.setValue(this.detailScheduleShipSelected.getMenwork_program());
		this.noteshipdetail.setValue(this.detailScheduleShipSelected.getNotedetail());

		// set activity info
		this.ship_from.setValue(null);
		this.ship_to.setValue(null);
		this.check_last_shift.setVisible(false);
		this.check_last_shift.setChecked(false);
		if (this.scheduleShip_selected.getIdship_activity() != null) {

			final Date date_from = this.detailScheduleShipSelected.getActivity_start();
			final Date date_to = this.detailScheduleShipSelected.getActivity_end();

			if (shift_no == 4) {
				this.check_last_shift.setVisible(true);
				this.check_last_shift.setChecked(false);

				final Calendar cal_date_from = DateUtils.toCalendar(date_from);
				final Calendar cal_date_to = DateUtils.toCalendar(date_to);

				final int day_from = cal_date_from.get(Calendar.DAY_OF_YEAR);
				final int day_to = cal_date_to.get(Calendar.DAY_OF_YEAR);

				if (day_from != day_to) {
					this.check_last_shift.setChecked(true);
				}

			}

			this.ship_from.setValue(date_from);
			this.ship_to.setValue(date_to);
		}

		// SHOW PANEL
		this.add_scheduleShipsDetail_command.setVisible(false);
		this.modify_scheduleShipsDetail_command.setVisible(true);

	}

	@Listen("onClick = #sw_link_modifyscheduleshipProgram")
	public void modifyScheduleshipProgram() {
		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected == null) {
			return;
		}

		if (this.scheduleShip_selected.getArrivaldate() == null) {
			return;
		}

		// get ship activity
		Ship ship_activity = null;
		if (this.scheduleShip_selected.getIdship_activity() != null) {
			ship_activity = this.shipDao.loadShip(this.scheduleShip_selected.getIdship_activity());
		}

		this.ship_rif_sws.setValue(this.scheduleShip_selected.getId().toString());

		// set ship name
		this.ship_name.setSelectedItem(null);
		if (this.scheduleShip_selected.getIdship() != null) {

			final Ship ship = this.shipDao.loadShip(this.scheduleShip_selected.getIdship());

			final List<Comboitem> listItem = this.ship_name.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Ship) {
					final Ship current_ship = item.getValue();
					if (ship.equals(current_ship)) {
						this.ship_name.setSelectedItem(item);
						break;
					}
				}

			}

		}

		// set customer
		this.ship_customer.setSelectedItem(null);
		if (this.scheduleShip_selected.getCustomer_id() != null) {
			final Customer customer = this.customerDAO.loadCustomer(this.scheduleShip_selected.getCustomer_id());
			if (customer != null) {

				final List<Comboitem> listItem = this.ship_customer.getItems();

				for (final Comboitem item : listItem) {

					if (item.getValue() instanceof Customer) {
						final Customer current_customer = item.getValue();
						if (customer.equals(current_customer)) {
							this.ship_customer.setSelectedItem(item);
							break;
						}
					}

				}
			}

		}

		// set activity ship
		this.ship_activity.setSelectedItem(null);
		this.row_info_activity_ship.setVisible(false);

		if (ship_activity != null) {

			final List<Comboitem> listItem = this.ship_activity.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Ship) {
					final Ship current_ship = item.getValue();
					if (ship_activity.equals(current_ship)) {
						this.ship_activity.setSelectedItem(item);
						this.row_info_activity_ship.setVisible(true);
						break;
					}
				}

			}

		}

		this.onChangeShipOnModifyShipProgram();

		this.ship_volume.setValue(this.scheduleShip_selected.getVolume());
		this.note.setValue(this.scheduleShip_selected.getNote());

		this.servicetype.setSelectedItem(null);
		final Integer idServiceSelected = this.scheduleShip_selected.getId_service();

		if (idServiceSelected != null) {
			final List<Service> service = this.configurationDao.selectService(idServiceSelected, null, null);
			if (service.size() != 0) {
				for (final Comboitem item : this.servicetype.getItems()) {
					if ((item.getValue() != null) && (item.getValue() instanceof Service)) {
						final Service current = item.getValue();
						if (service.get(0).getId().equals(current.getId())) {
							this.servicetype.setSelectedItem(item);
							break;
						}
					}
				}
			}
		}

		this.shipRif_mcf.setValue(this.scheduleShip_selected.getRif_mct());

		// set arrival date and time
		Date date = this.scheduleShip_selected.getArrivaldate();
		this.ship_arrival.setValue(date);

		date = this.scheduleShip_selected.getDeparturedate();

		if (date != null) {
			this.ship_departure.setValue(date);
		}

		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {

			this.listDetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

		}

		String msg = ShipSchedulerComposer.CAPTION_POPUP_SCHEDULE_SHIP + " - " + this.scheduleShip_selected.getName();
		if (ship_activity != null) {
			msg = msg + ":" + ship_activity.getName();
		}
		this.captionPopupScheduleShip.setLabel(msg);

	}

	@Listen("onClick = #modifyShipSchedule_command")
	public void modifyShipScheduleCommand() {

		if (this.sw_list_scheduleShipProgram.getSelectedItem() == null) {
			return;
		}

		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if ((this.ship_name.getSelectedItem() == null) || (this.ship_volume.getValue() == null) || (this.ship_arrival == null)
				|| (this.ship_departure.getValue() == null) || this.ship_arrival.getValue().after(this.ship_departure.getValue())) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare i valori inseriti..", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			return;
		}

		// ship selected
		final Ship ship = (Ship) this.ship_name.getSelectedItem().getValue();

		this.scheduleShip_selected.setIdship(ship.getId());
		this.scheduleShip_selected.setVolume(this.ship_volume.getValue());
		this.scheduleShip_selected.setNote(this.note.getValue());

		Integer idService = null;
		if (this.servicetype.getSelectedItem() != null) {
			final Service serviceSelected = this.servicetype.getSelectedItem().getValue();
			idService = serviceSelected.getId();
		}

		this.scheduleShip_selected.setId_service(idService);

		this.scheduleShip_selected.setArrivaldate(this.ship_arrival.getValue());

		this.scheduleShip_selected.setDeparturedate(this.ship_departure.getValue());

		this.scheduleShip_selected.setRif_mct(this.shipRif_mcf.getValue());

		// add customer //
		if ((this.ship_customer.getSelectedItem() != null) && (this.ship_customer.getSelectedItem().getValue() != null)) {

			final Customer customer = this.ship_customer.getSelectedItem().getValue();

			this.scheduleShip_selected.setCustomer_id(customer.getId());

		} else {

			this.scheduleShip_selected.setCustomer_id(null);

		}

		// set activity
		this.scheduleShip_selected.setIdship_activity(null);
		if (ship.getActivityh().booleanValue() && (this.ship_activity.getSelectedItem() != null)) {
			final Ship ship_activity_item = this.ship_activity.getSelectedItem().getValue();
			this.scheduleShip_selected.setIdship_activity(ship_activity_item.getId());
		}

		// update
		this.shipSchedulerDao.updateScheduleShip(this.scheduleShip_selected);

		this.popup_scheduleShip.close();

		// update ship program view
		this.refreshProgram();

	}

	@Listen("onChange = #ship_name_schedule")
	public void onChangeShipOnAddShipProgram() {

		this.row_info_activity_ship_add.setVisible(false);
		this.ship_activity_add.setSelectedItem(null);

		if (this.ship_name_schedule.getSelectedItem() == null) {
			return;
		}

		final Ship ship = this.ship_name_schedule.getSelectedItem().getValue();
		if (ship.getActivityh().booleanValue()) {
			this.row_info_activity_ship_add.setVisible(true);

		}

	}

	@Listen("onChange = #ship_name")
	public void onChangeShipOnModifyShipProgram() {

		this.row_info_activity_ship.setVisible(false);

		if (this.ship_name.getSelectedItem() == null) {
			return;
		}

		final Ship ship = this.ship_name.getSelectedItem().getValue();
		if (ship.getActivityh().booleanValue()) {
			this.row_info_activity_ship.setVisible(true);

		}

	}

	@Listen("onChange = #shift")
	public void onShiftChangeProgram() {

		if (this.shift.getSelectedItem() == null) {
			return;
		}

		if (this.shiftdate_Daily.getValue() == null) {
			return;
		}

		final String shift_info = this.shift.getSelectedItem().getValue();
		final Integer shift_no = Integer.parseInt(shift_info);

		this.changeBehaviorForShify(shift_no, this.shiftdate_Daily.getValue(), this.ship_from, this.ship_to, this.check_last_shift);

	}

	@Listen("onClick = #sw_link_modifyscheduleship")
	public void openModifyDetailDailyPopup() {

		this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (this.detailScheduleShipSelected == null) {
			return;
		}

		this.shiftdate_Daily.setValue(this.detailScheduleShipSelected.getShiftdate());

		// select first user
		Person person = this.personDao.loadPerson(this.detailScheduleShipSelected.getIduser());
		if (person != null) {
			final List<Comboitem> listItem = this.user_Daily.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Person) {
					final Person current_person = item.getValue();
					if (person.equals(current_person)) {
						this.user_Daily.setSelectedItem(item);
						break;
					}
				}

			}

		} else {
			this.user_Daily.setSelectedItem(null);
		}

		// select second user
		person = this.personDao.loadPerson(this.detailScheduleShipSelected.getIdseconduser());
		if (person != null) {
			final List<Comboitem> listItem = this.usersecond_Daily.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Person) {
					final Person current_person = item.getValue();
					if (person.equals(current_person)) {
						this.usersecond_Daily.setSelectedItem(item);
						break;
					}
				}

			}

		} else {
			this.usersecond_Daily.setSelectedItem(null);
		}

		// select shift
		final Integer shift = this.detailScheduleShipSelected.getShift();
		if (shift != null) {
			this.shift_Daily.setValue(shift.toString());
		} else {
			this.shift_Daily.setValue(null);
		}

		this.operation_Daily.setValue(this.detailScheduleShipSelected.getOperation());
		this.notedetail.setValue(this.detailScheduleShipSelected.getNotedetail());

		// SET ACTIVIY

		this.ship_from_detail.setValue(null);
		this.ship_to_detail.setValue(null);

		this.h_detail_period.setVisible(false);
		this.check_last_shift_detail.setChecked(false);
		this.check_last_shift_detail.setVisible(false);

		// define if ship activity fields need to be disabled
		final Integer id_ship = this.detailScheduleShipSelected.getId_ship();
		final Ship ship = this.shipDao.loadShip(id_ship);

		if ((ship != null) && (ship.getActivityh() != null) && ship.getActivityh().booleanValue()) {

			this.check_last_shift_detail.setVisible(false);
			this.check_last_shift_detail.setChecked(false);

			final Date from = this.detailScheduleShipSelected.getActivity_start();
			final Date to = this.detailScheduleShipSelected.getActivity_end();

			this.ship_from_detail.setValue(from);
			this.ship_to_detail.setValue(to);

			final Integer shiftNumber = this.detailScheduleShipSelected.getShift();

			if (shiftNumber.equals(4)) {
				this.check_last_shift_detail.setVisible(true);
				if ((from != null) && (to != null)) {
					final Calendar cal_from = DateUtils.toCalendar(from);
					final Calendar cal_to = DateUtils.toCalendar(to);
					final int day_from = cal_from.get(Calendar.DAY_OF_YEAR);
					final int day_to = cal_to.get(Calendar.DAY_OF_YEAR);

					final int gap_day = day_to - day_from;
					if (gap_day == 1) {
						this.check_last_shift_detail.setChecked(true);
					}

				}
			}

			this.h_detail_period.setVisible(true);

		}

		// set label
		String msg = ShipSchedulerComposer.CAPTION_SHIP_PROGRAM_LABEL + " - " + this.detailScheduleShipSelected.getName();

		final ScheduleShip scheduler_ship = this.shipSchedulerDao.loadScheduleShip(this.detailScheduleShipSelected.getIdscheduleship());
		final Integer ship_activity_id = scheduler_ship.getIdship_activity();

		if (ship_activity_id != null) {
			final Ship ship_activity = this.shipDao.loadShip(ship_activity_id);
			if (ship_activity != null) {
				msg = msg + ":" + ship_activity.getName();
			}

		}

		this.captionShipProgram.setLabel(msg);

	}

	/**
	 * Called on initial view definition
	 */
	public void refreshBAP() {

		// get date
		Date date_from = this.searchArrivalDateShipFrom.getValue();
		Date date_to = this.searchArrivalDateShipTo.getValue();

		if ((date_from != null) && (date_to != null) && date_from.after(date_to)) {
			return;
		}

		if ((date_from == null) && (date_to == null) && (this.searchDateShift.getValue() != null)) {
			// search by shiftDate
			date_from = this.searchDateShift.getValue();
			date_to = date_from;
		}

		// get text
		String text_search = this.full_text_search.getValue();
		if ((text_search != null) && text_search.equals("")) {
			text_search = null;
		}

		// get shift number
		Integer shiftNumber = null;
		if (this.select_shift.getSelectedItem() != null) {
			shiftNumber = Integer.parseInt(this.select_shift.getSelectedItem().getValue().toString());
		}

		// get rif_sws
		final Integer rif_sws = this.text_search_rifSWS.getValue();

		String rif_mct;
		if (this.rif_customer_empty.isChecked()) {
			rif_mct = ShipSchedulerComposer.rif_mct_empty;
		} else {
			rif_mct = this.text_search_rifMCT.getValue();
		}

		if (rif_mct.isEmpty()) {
			rif_mct = null;
		}

		// get invoice cycle
		final Integer invoicing = this.invoicing_cycle_search.getValue();

		// get id service
		Integer idServiceSelected = null;
		if (this.selectServiceDetail.getSelectedItem() != null) {
			final Service serviceSelected = this.selectServiceDetail.getSelectedItem().getValue();
			idServiceSelected = serviceSelected.getId();
		}

		final String ship_type = this.ship_type_search.getValue();
		final String ship_line = this.ship_line_search.getValue();
		final String ship_condition = this.ship_condition_search.getValue();

		if (this.overviewBap.isSelected()) {

			this.list_review_work = this.statistic_dao.loadReviewShipWork(date_from, date_to, text_search, rif_sws, rif_mct, shiftNumber, invoicing,
					idServiceSelected, ship_type, ship_line, ship_condition);

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.sw_list_reviewWork.setPageSize(this.shows_rows.getValue());
			} else {
				this.sw_list_reviewWork.setPageSize(10);
			}

			this.sw_list_reviewWork.setModel(new ListModelList<>(this.list_review_work));

			this.calculateSummaryBAP();

		} else if (this.overviewBapAggregate.isSelected()) {

			this.list_review_work_aggregate = this.statistic_dao.loadReviewShipWorkAggregate(date_from, date_to, rif_sws, rif_mct, invoicing,
					text_search, idServiceSelected, ship_type, ship_line, ship_condition);

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.sw_list_reviewWorkAggregate.setPageSize(this.shows_rows.getValue());
			} else {
				this.sw_list_reviewWorkAggregate.setPageSize(10);
			}

			this.sw_list_reviewWorkAggregate.setModel(new ListModelList<>(this.list_review_work_aggregate));

			final Double avgProd = this.calculateProductivityAVG(this.list_review_work_aggregate);

			this.avg_productivity.setValue(avgProd.toString());

		} else if (this.statisticsShipTab.isSelected()) {

			this.listShipStatistics = this.statisticDAO.overviewFinalScheduleByShip(text_search, date_from, date_to, ship_type, ship_line,
					ship_condition);

			this.list_ship_statistics.setModel(new ListModelList<>(this.listShipStatistics));

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.list_ship_statistics.setPageSize(this.shows_rows.getValue());
			}

		}

	}

	public void refreshBapView() {

		final Calendar calendar_from = Calendar.getInstance();
		final Calendar calendar_to = Calendar.getInstance();
		calendar_from.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMinimum(Calendar.DAY_OF_YEAR));
		calendar_to.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMaximum(Calendar.DAY_OF_YEAR));
		this.select_year_detail.setValue("" + calendar_from.get(Calendar.YEAR));
		this.select_month_detail.setSelectedItem(null);
		this.searchArrivalDateShipFrom.setValue(calendar_from.getTime());
		this.searchArrivalDateShipTo.setValue(calendar_to.getTime());

		this.full_text_search.setValue(null);
		this.text_search_rifSWS.setValue(null);
		this.text_search_rifMCT.setValue(null);
		this.invoicing_cycle_search.setValue(null);
		this.shows_rows.setValue(10);

		this.select_shift.setSelectedItem(null);

		this.refreshBAP();
	}

	/**
	 * Search info for detail
	 */
	public void refreshDetail() {

		final Date dateFrom = this.searchArrivalDateShipFrom.getValue();
		final Date dateTo = this.searchArrivalDateShipTo.getValue();

		if ((dateFrom != null) && (dateTo != null) && dateFrom.after(dateTo)) {
			return;
		}

		// get text
		String text_search = this.full_text_search.getValue();
		if ((text_search != null) && text_search.equals("")) {
			text_search = null;
		}

		final Integer no_shift = this.getSelectedShift();

		Integer idCustomer = null;
		if (this.select_customer.getSelectedItem() != null) {
			final Customer customerSelected = this.select_customer.getSelectedItem().getValue();
			idCustomer = customerSelected.getId();
		}

		// set ship type filter
		Boolean nowork = null;
		Boolean activityh = null;
		if (this.select_typeShip.getSelectedItem() != null) {
			final String selected = this.select_typeShip.getSelectedItem().getValue();
			if (selected.equals("activityh")) {
				activityh = true;
			} else if (selected.equals("nowork")) {
				nowork = true;
			} else if (selected.equals("noone")) {
				activityh = false;
				nowork = false;

			}
		}

		// set worked filter
		Boolean worked = null;
		if (this.select_workedShip.getSelectedItem() != null) {
			if (this.select_workedShip.getSelectedItem().getValue().equals(ShipSchedulerComposer.SHIP_WORKED_YES)) {
				worked = true;
			} else if (this.select_workedShip.getSelectedItem().getValue().equals(ShipSchedulerComposer.SHIP_WORKED_NO)) {
				worked = false;
			}
		}

		if (((this.text_search_rifMCT.getValue() == null) || this.text_search_rifMCT.getValue().equals(""))
				&& ((this.text_search_rifSWS.getValue() == null) || this.text_search_rifSWS.getValue().equals(""))) {

			Integer idServiceSelected = null;

			if (this.selectServiceDetail.getSelectedItem() != null) {
				final Service serviceSelected = this.selectServiceDetail.getSelectedItem().getValue();

				idServiceSelected = serviceSelected.getId();
			}

			// extract info for search
			final Date date_shift = this.searchDateShift.getValue();
			final String ship_type = this.ship_type_search.getValue();
			final String ship_line = this.ship_line_search.getValue();
			final String ship_condition = this.ship_condition_search.getValue();
			final Boolean period_on_dateshift = this.datashift_period.isChecked();

			this.list_details_programmed_ship = this.shipSchedulerDao.searchDetailScheduleShipByPeriodOrDateshift(dateFrom, dateTo, date_shift,
					period_on_dateshift, text_search, no_shift, idCustomer, nowork, activityh, worked, idServiceSelected, ship_type, ship_line,
					ship_condition);

			this.sw_list_scheduleShip.setModel(new ListModelList<>(this.list_details_programmed_ship));

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.sw_list_scheduleShip.setPageSize(this.shows_rows.getValue());
			} else {
				this.sw_list_scheduleShip.setPageSize(10);
			}

			this.calculateSummaryShipDetails(this.list_details_programmed_ship);
		} else {
			this.searchRifSWS_MCT();
		}
	}

	public void refreshDetailView() {

		final Calendar calendar_from = Calendar.getInstance();
		final Calendar calendar_to = Calendar.getInstance();

		calendar_from.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMinimum(Calendar.DAY_OF_YEAR));
		calendar_to.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMaximum(Calendar.DAY_OF_YEAR));
		this.select_year_detail.setValue("" + calendar_from.get(Calendar.YEAR));
		this.select_month_detail.setSelectedItem(null);

		this.invoicing_cycle_search.setVisible(false);

		// set null date shift box
		this.searchDateShift.setValue(null);

		this.searchArrivalDateShipFrom.setValue(calendar_from.getTime());
		this.searchArrivalDateShipTo.setValue(calendar_to.getTime());

		this.text_search_rifSWS.setValue(null);
		this.text_search_rifMCT.setValue(null);

		this.selectServiceDetail.setSelectedItem(null);
		this.select_customer.setSelectedItem(null);

		this.select_shift.setValue(null);

		this.select_typeShip.setValue(null);

		this.select_workedShip.setValue(null);

		this.full_text_search.setValue(null);

		this.refreshDetail();

	}

	/**
	 * Refresh info..
	 */
	public void refreshProgram() {

		Date dateFrom = this.searchArrivalDateShipFrom.getValue();
		Date dateTo = this.searchArrivalDateShipTo.getValue();

		if ((dateFrom != null) && (dateTo != null) && dateFrom.after(dateTo)) {
			return;
		}

		if (dateFrom != null) {
			dateFrom = DateUtils.truncate(dateFrom, Calendar.DATE);
		}

		if (dateTo != null) {
			dateTo = DateUtils.truncate(dateTo, Calendar.DATE);
		}

		// get text
		String text_search = this.full_text_search.getValue();
		if ((text_search != null) && text_search.equals("")) {
			text_search = null;
		}

		Integer id_customer = null;
		if (this.select_customer.getSelectedItem() != null) {
			final Customer c = this.select_customer.getSelectedItem().getValue();
			if (c != null) {
				id_customer = c.getId();
			}
		}

		// tale info about service id
		Integer id_service = null;

		if (this.selectServiceDetail.getSelectedItem() != null) {
			final Service serviceSelected = this.selectServiceDetail.getSelectedItem().getValue();
			id_service = serviceSelected.getId();
		}

		// take other info
		String rifMCT;
		if (this.rif_customer_empty.isChecked()) {
			rifMCT = ShipSchedulerComposer.rif_mct_empty;
		} else {
			rifMCT = this.text_search_rifMCT.getValue();
		}

		final Integer rif_SWS = this.text_search_rifSWS.getValue();
		final String ship_type = this.ship_type_search.getValue();
		final String ship_line = this.ship_line_search.getValue();
		final String ship_condition = this.ship_condition_search.getValue();
		final Boolean initial_support = this.initial_support_date.isChecked();

		this.list_programmed_ship = this.shipSchedulerDao.searchScheduleShip(dateFrom, dateTo, rif_SWS, rifMCT, id_customer, id_service, text_search,
				ship_type, ship_line, ship_condition, initial_support);

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_scheduleShipProgram.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_scheduleShipProgram.setPageSize(10);
		}

		this.sw_list_scheduleShipProgram.setModel(new ListModelList<>(this.list_programmed_ship));

		this.calculateSummaryShipProgram(this.list_programmed_ship, dateFrom);

		// set forecasting
		this.forecastingForMonthShipProgram(dateFrom, id_service);

	}

	@Listen("onClick = #byInvoce")
	public void refreshReport() {
		
		if (this.select_year_detail.getSelectedItem() == null) {
			final Calendar calendar_from = Calendar.getInstance();
			calendar_from.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMinimum(Calendar.DAY_OF_YEAR));
			this.select_year_detail.setValue("" + calendar_from.get(Calendar.YEAR));
		}

		final Integer year = Integer.parseInt((String) this.select_year_detail.getSelectedItem().getValue());

		this.initTerminalProductivity(year);

		this.reportListboxContainer.setModel(new ListModelList<>());

		this.reportList = new ArrayList<>();

		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);

		final Service serviceRZ = this.configurationDao.loadRZService();
		Integer idServiceRZ = null;
		if (serviceRZ != null) {
			idServiceRZ = serviceRZ.getId();
		}

		final List<ShipTotal> containerList = this.statisticDAO.getTotalInvoiceContainer(year, idServiceRZ, this.getInvoce());
		final List<ShipTotal> handMenList = this.statisticDAO.getTotalHandsMen(year, null, idServiceRZ, this.getInvoce());

		final ReportItem itemContainer = new ReportItem();
		itemContainer.setArgument(ReportItemTag.Containers);
		final ReportItem itemRZ_TW_SWS = new ReportItem();
		itemRZ_TW_SWS.setArgument(ReportItemTag.ContainerRZ_TW_SWS);
		final ReportItem itemRZ_TW_MCT = new ReportItem();
		itemRZ_TW_MCT.setArgument(ReportItemTag.ContainerRZ_TW_MCT);
		final ReportItem itemHands = new ReportItem();
		itemHands.setArgument(ReportItemTag.Hands);
		final ReportItem itemHandsOnDays = new ReportItem();
		itemHandsOnDays.setArgument(ReportItemTag.HandsOnDays);
		final ReportItem totalHoursRZ_PP_task = new ReportItem();
		final ReportItem itemMenOnHand = new ReportItem();
		itemMenOnHand.setArgument(ReportItemTag.MenOnHands);
		final ReportItem itemContainerOnHours = new ReportItem();
		itemContainerOnHours.setArgument(ReportItemTag.ContainersOnHours);
		final ReportItem itemProductivity = new ReportItem();
		itemProductivity.setArgument(ReportItemTag.Productivity);
		final ReportItem itemContainerOnMen = new ReportItem();
		itemContainerOnMen.setArgument(ReportItemTag.ContainersOnMen);
		final ReportItem itemWindyDay = new ReportItem();
		itemWindyDay.setArgument(ReportItemTag.WindyDay);
		final ReportItem itemShipNumberTW = new ReportItem();
		itemShipNumberTW.setArgument(ReportItemTag.ShipNumberTwist);
		final ReportItem itemShipNumberCM = new ReportItem();
		itemShipNumberCM.setArgument(ReportItemTag.ShipNumberComplete);

		cal.set(Calendar.MONTH, 1);
		final Integer dayInFeb = cal.getActualMaximum(Calendar.DATE);

		// CONTAINER FATTURATI
		Double totalContainer = 0.0;
		Integer numberOfContainerNotNull = 0;
		Double totalContainerRZ_TW_SWS = 0.0;
		Integer numberOfContainerRZ_TW_SWSNotNull = 0;
		Double totalContainerRZ_TW_MCT = 0.0;
		Integer numberOfContainerRZ_TW_MCTNotNull = 0;

		for (final ShipTotal shipTotal : containerList) {

			if ((shipTotal.getContainerInvoice() != null) && !shipTotal.getContainerInvoice().equals(0.0)) {
				totalContainer += shipTotal.getContainerInvoice();
				numberOfContainerNotNull++;
			}

			if ((shipTotal.getContainerRZ_TW_SWS() != null) && !shipTotal.getContainerRZ_TW_SWS().equals(0.0)) {
				totalContainerRZ_TW_SWS += shipTotal.getContainerRZ_TW_SWS();
				numberOfContainerRZ_TW_SWSNotNull++;
			}

			if ((shipTotal.getContainerRZ_TW_MCT() != null) && !shipTotal.getContainerRZ_TW_MCT().equals(0.0)) {
				totalContainerRZ_TW_MCT += shipTotal.getContainerRZ_TW_MCT();
				numberOfContainerRZ_TW_MCTNotNull++;
			}

			if (shipTotal.getMonthInvoice() == 1) {
				itemContainer.setGen(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setGen(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setGen(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 2) {
				itemContainer.setFeb(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setFeb(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setFeb(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 3) {
				itemContainer.setMar(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setMar(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setMar(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 4) {
				itemContainer.setApr(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setApr(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setApr(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 5) {
				itemContainer.setMay(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setMay(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setMay(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 6) {
				itemContainer.setJun(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setJun(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setJun(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 7) {
				itemContainer.setJul(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setJul(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setJul(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 8) {
				itemContainer.setAug(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setAug(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setAug(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 9) {
				itemContainer.setSep(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setSep(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setSep(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 10) {
				itemContainer.setOct(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setOct(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setOct(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 11) {
				itemContainer.setNov(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setNov(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setNov(shipTotal.getContainerRZ_TW_MCT());
			} else if (shipTotal.getMonthInvoice() == 12) {
				itemContainer.setDec(shipTotal.getContainerInvoice());
				itemRZ_TW_SWS.setDec(shipTotal.getContainerRZ_TW_SWS());
				itemRZ_TW_MCT.setDec(shipTotal.getContainerRZ_TW_MCT());
			}
		}

		// CONTAINER
		itemContainer.setTot(totalContainer);

		// MEDIA MENSILE CONTAINER
		if ((totalContainer != null) && (numberOfContainerNotNull != 0)) {
			itemContainer.setAvg(totalContainer / numberOfContainerNotNull);
		} else {
			itemContainer.setAvg(0.0);
		}

		// CONTAINER RZ_TW_SWS
		itemRZ_TW_SWS.setTot(totalContainerRZ_TW_SWS);

		// MEDIA MENSILE CONTAINER RZ_TW_SWS
		if ((totalContainerRZ_TW_SWS != null) && (numberOfContainerRZ_TW_SWSNotNull != 0)) {
			itemRZ_TW_SWS.setAvg(totalContainerRZ_TW_SWS / numberOfContainerRZ_TW_SWSNotNull);
		} else {
			itemRZ_TW_SWS.setAvg(0.0);
		}

		// CONTAINER RZ_TW_SWS
		itemRZ_TW_MCT.setTot(totalContainerRZ_TW_MCT);

		// MEDIA MENSILE CONTAINER RZ_TW_SWS
		if ((totalContainerRZ_TW_MCT != null) && (numberOfContainerRZ_TW_MCTNotNull != 0)) {
			itemRZ_TW_MCT.setAvg(totalContainerRZ_TW_MCT / numberOfContainerRZ_TW_MCTNotNull);
		} else {
			itemRZ_TW_MCT.setAvg(0.0);
		}

		Double totalProductivity = 0.0;
		Integer productivityNotNull = 0;

		if (this.terminalProductivityList != null) {
			for (int i = 1; i <= 12; i++) {
				if (this.terminalProductivityList.get(i) != null) {
					final Double productivity = this.terminalProductivityList.get(i).getProductivity();
					if ((productivity != null) && (productivity != 0)) {
						totalProductivity = totalProductivity + productivity;
						productivityNotNull++;
					}
					switch (i) {
						case 1:
							itemProductivity.setGen(productivity);
							break;
						case 2:
							itemProductivity.setFeb(productivity);
							break;
						case 3:
							itemProductivity.setMar(productivity);
							break;
						case 4:
							itemProductivity.setApr(productivity);
							break;
						case 5:
							itemProductivity.setMay(productivity);
							break;
						case 6:
							itemProductivity.setJun(productivity);
							break;
						case 7:
							itemProductivity.setJul(productivity);
							break;
						case 8:
							itemProductivity.setAug(productivity);
							break;
						case 9:
							itemProductivity.setSep(productivity);
							break;
						case 10:
							itemProductivity.setOct(productivity);
							break;
						case 11:
							itemProductivity.setNov(productivity);
							break;
						case 12:
							itemProductivity.setDec(productivity);
							break;
						default:
							break;
					}
				}
			}

		}

		if (productivityNotNull != 0.0) {
			itemProductivity.setAvg(totalProductivity / productivityNotNull);
		} else {
			itemProductivity.setAvg(0.0);
		}

		final List<UserTask> tasks = this.taskDAO.listAllTask();
		final List<ShipTotal> taskHoursList = this.statisticDAO.getTotalHoursByTask(year);

		final UserTask taskRZ = this.configurationDao.loadRZTask();
		final UserTask taskPP = this.configurationDao.loadPPTask();
		ReportItem itemRZ = new ReportItem();
		ReportItem itemPP = new ReportItem();
		final ReportItem itemTotalHoursTask = new ReportItem();
		itemTotalHoursTask.setArgument(ReportItemTag.TaskTotalHours);
		double totalHoursTask = 0.0;
		int hoursTaskNotNull = 0;
		final int indexTotalHoursTask = this.reportList.size();

		for (final UserTask userTask : tasks) {
			final ReportItem itemTaskHour = new ReportItem();
			itemTaskHour.setIsTaskROW(true);

			// TASK HOURS
			Double totalHours = 0.0;
			Integer numberOfHoursNotNull = 0;
			itemTaskHour.setArgument(userTask.getCode() + " - " + userTask.getDescription());

			for (final ShipTotal shipTotal : taskHoursList) {

				if (shipTotal.getTask_id().equals(userTask.getId())) {
					if ((shipTotal.getTask_hour() != null) && !shipTotal.getTask_hour().equals(0.0)) {
						totalHours += shipTotal.getTask_hour();
						numberOfHoursNotNull++;
					}
					if (shipTotal.getMonth_date() == 1) {
						itemTaskHour.setGen(shipTotal.getTask_hour());
						itemTotalHoursTask.setGen(itemTotalHoursTask.getGen() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 2) {
						itemTaskHour.setFeb(shipTotal.getTask_hour());
						itemTotalHoursTask.setFeb(itemTotalHoursTask.getFeb() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 3) {
						itemTaskHour.setMar(shipTotal.getTask_hour());
						itemTotalHoursTask.setMar(itemTotalHoursTask.getMar() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 4) {
						itemTaskHour.setApr(shipTotal.getTask_hour());
						itemTotalHoursTask.setApr(itemTotalHoursTask.getApr() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 5) {
						itemTaskHour.setMay(shipTotal.getTask_hour());
						itemTotalHoursTask.setMay(itemTotalHoursTask.getMay() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 6) {
						itemTaskHour.setJun(shipTotal.getTask_hour());
						itemTotalHoursTask.setJun(itemTotalHoursTask.getJun() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 7) {
						itemTaskHour.setJul(shipTotal.getTask_hour());
						itemTotalHoursTask.setJul(itemTotalHoursTask.getJul() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 8) {
						itemTaskHour.setAug(shipTotal.getTask_hour());
						itemTotalHoursTask.setAug(itemTotalHoursTask.getAug() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 9) {
						itemTaskHour.setSep(shipTotal.getTask_hour());
						itemTotalHoursTask.setSep(itemTotalHoursTask.getSep() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 10) {
						itemTaskHour.setOct(shipTotal.getTask_hour());
						itemTotalHoursTask.setOct(itemTotalHoursTask.getOct() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 11) {
						itemTaskHour.setNov(shipTotal.getTask_hour());
						itemTotalHoursTask.setNov(itemTotalHoursTask.getNov() + shipTotal.getTask_hour());
					} else if (shipTotal.getMonth_date() == 12) {
						itemTaskHour.setDec(shipTotal.getTask_hour());
						itemTotalHoursTask.setDec(itemTotalHoursTask.getDec() + shipTotal.getTask_hour());
					}
				}
			}

			// TASK HOURS
			itemTaskHour.setTot(totalHours);

			// MEDIA MENSILE ORE MANSIONI
			if ((totalHours != null) && (numberOfHoursNotNull != 0)) {
				itemTaskHour.setAvg(totalHours / numberOfHoursNotNull);
			} else {
				itemTaskHour.setAvg(0.0);
			}

			if ((taskRZ != null) && userTask.getId().equals(taskRZ.getId())) {
				itemRZ = itemTaskHour;
			}
			if ((taskPP != null) && userTask.getId().equals(taskPP.getId())) {
				itemPP = itemTaskHour;
			}

			this.reportList.add(itemTaskHour);
		}

		totalHoursTask = itemTotalHoursTask.getGen() + itemTotalHoursTask.getFeb() + itemTotalHoursTask.getMar() + itemTotalHoursTask.getApr()
				+ itemTotalHoursTask.getMay() + itemTotalHoursTask.getJun() + itemTotalHoursTask.getJul() + itemTotalHoursTask.getAug()
				+ itemTotalHoursTask.getSep() + itemTotalHoursTask.getOct() + itemTotalHoursTask.getNov() + itemTotalHoursTask.getDec();

		if (!itemTotalHoursTask.getGen().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getFeb().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getMar().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getApr().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getMay().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getJun().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getJul().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getAug().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getSep().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getOct().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getNov().equals(0.0)) {
			hoursTaskNotNull++;
		}
		if (!itemTotalHoursTask.getDec().equals(0.0)) {
			hoursTaskNotNull++;
		}

		if (hoursTaskNotNull != 0) {
			itemTotalHoursTask.setAvg(totalHoursTask / hoursTaskNotNull);
		} else {
			itemTotalHoursTask.setAvg(0.0);
		}
		itemTotalHoursTask.setTot(totalHoursTask);

		this.reportList.add(indexTotalHoursTask, itemTotalHoursTask);

		// TOTALE MANI - MANI SU GIORNO - UOMINI SU MANI - CONTAINER SU UOMO -
		// GIORNI DISAGIO VENTO
		Double totalHands = 0.0;
		Integer numberOfHandsNotNull = 0;

		Integer totalWindyDay = 0;
		Integer numberOfWindyDayNotNull = 0;

		Double totalMenOnHand = 0.0;
		Integer numberMenOnHandNotNull = 0;

		Double totalContainerOnMen = 0.0;
		Integer containerOnMenNotNull = 0;

		Double totalContainerOnHours = 0.0;
		Integer containerOnHoursNotNull = 0;

		for (final ShipTotal shipTotal : handMenList) {

			if ((shipTotal.getHandswork() != null) && !shipTotal.getHandswork().equals(0.0)) {
				totalHands += shipTotal.getHandswork();
				numberOfHandsNotNull++;
			}

			if ((shipTotal.getWindyday() != null) && !shipTotal.getWindyday().equals(0.0)) {
				totalWindyDay += shipTotal.getWindyday();
				numberOfWindyDayNotNull++;
			}

			if ((shipTotal.getHandswork() != null) && !shipTotal.getHandswork().equals(0.0)) {
				numberMenOnHandNotNull++;
			}

			if (shipTotal.getMonthInvoice() == 1) {
				itemHands.setGen(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setGen((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setGen(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setGen(shipTotal.getHandswork() / 31);
					if (itemRZ.getGen() != null) {
						sumHourRZ_PP = itemRZ.getGen();
					}
					if (itemPP.getGen() != null) {
						sumHourRZ_PP = sumHourRZ_PP + itemPP.getGen();
					}
					if ((sumHourRZ_PP != 0.0) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setGen(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getGen();
						if ((itemContainer.getGen() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setGen((itemContainer.getGen() + itemRZ_TW_SWS.getGen()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getGen();
							containerOnMenNotNull++;
							itemContainerOnHours.setGen((itemContainer.getGen() + itemRZ_TW_SWS.getGen()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getGen() != null) && !itemContainerOnHours.getGen().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getGen();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setGen(itemRZ_TW_SWS.getGen());
							if ((itemRZ_TW_SWS.getGen() != null) && !itemRZ_TW_SWS.getGen().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getGen();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setGen(0.0);
						}
					} else {
						itemMenOnHand.setGen(0.0);
					}
				} else {
					itemHandsOnDays.setGen(0.0);
				}

				totalHoursRZ_PP_task.setGen(sumHourRZ_PP);

			} else if (shipTotal.getMonthInvoice() == 2) {
				itemHands.setFeb(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setFeb((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setFeb(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setFeb(shipTotal.getHandswork() / dayInFeb);
					if (itemRZ.getFeb() != null) {
						sumHourRZ_PP = itemRZ.getFeb();
					}
					if (itemPP.getFeb() != null) {
						sumHourRZ_PP = sumHourRZ_PP + itemPP.getFeb();
					}

					if ((sumHourRZ_PP != null) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setFeb(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getFeb();
						if ((itemContainer.getFeb() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setFeb((itemContainer.getFeb() + itemRZ_TW_SWS.getFeb()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getFeb();
							containerOnMenNotNull++;
							itemContainerOnHours.setFeb((itemContainer.getFeb() + itemRZ_TW_SWS.getFeb()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getFeb() != null) && !itemContainerOnHours.getFeb().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getFeb();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setFeb(itemRZ_TW_SWS.getFeb());
							if ((itemRZ_TW_SWS.getFeb() != null) && !itemRZ_TW_SWS.getFeb().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getFeb();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setFeb(0.0);
						}
					} else {
						itemMenOnHand.setFeb(0.0);
					}
				} else {
					itemHandsOnDays.setFeb(0.0);
				}

				totalHoursRZ_PP_task.setFeb(sumHourRZ_PP);
			} else if (shipTotal.getMonthInvoice() == 3) {
				itemHands.setMar(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setMar((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setMar(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (itemRZ.getMar() != null) {
					sumHourRZ_PP = itemRZ.getMar();
				}
				if (itemPP.getMar() != null) {
					sumHourRZ_PP = sumHourRZ_PP + itemPP.getMar();
				}

				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setMar(shipTotal.getHandswork() / 31);
					if ((sumHourRZ_PP != null) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setMar(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getMar();
						if ((itemContainer.getMar() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setMar((itemContainer.getMar() + itemRZ_TW_SWS.getMar()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getMar();
							containerOnMenNotNull++;
							itemContainerOnHours.setMar((itemContainer.getMar() + itemRZ_TW_SWS.getMar()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getMar() != null) && !itemContainerOnHours.getMar().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getMar();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setMar(itemRZ_TW_SWS.getMar());
							if ((itemRZ_TW_SWS.getMar() != null) && !itemRZ_TW_SWS.getMar().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getMar();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setMar(0.0);
						}
					} else {
						itemMenOnHand.setMar(0.0);
					}
				} else {
					itemHandsOnDays.setMar(0.0);
				}

				totalHoursRZ_PP_task.setMar(sumHourRZ_PP);
			} else if (shipTotal.getMonthInvoice() == 4) {
				itemHands.setApr(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setApr((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setApr(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (itemRZ.getApr() != null) {
					sumHourRZ_PP = itemRZ.getApr();
				}
				if (itemPP.getApr() != null) {
					sumHourRZ_PP = sumHourRZ_PP + itemPP.getApr();
				}

				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setApr(shipTotal.getHandswork() / 30);
					if ((sumHourRZ_PP != null) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setApr(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getApr();
						if ((itemContainer.getApr() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setApr((itemContainer.getApr() + itemRZ_TW_SWS.getApr()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getApr();
							containerOnMenNotNull++;
							itemContainerOnHours.setApr((itemContainer.getApr() + itemRZ_TW_SWS.getApr()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getApr() != null) && !itemContainerOnHours.getApr().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getApr();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setApr(itemRZ_TW_SWS.getApr());
							if ((itemRZ_TW_SWS.getApr() != null) && !itemRZ_TW_SWS.getApr().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getApr();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setApr(0.0);
						}
					} else {
						itemMenOnHand.setApr(0.0);
					}
				} else {
					itemHandsOnDays.setApr(0.0);
				}

				totalHoursRZ_PP_task.setApr(sumHourRZ_PP);
			} else if (shipTotal.getMonthInvoice() == 5) {
				itemHands.setMay(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setMay((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setMay(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (itemRZ.getMay() != null) {
					sumHourRZ_PP = itemRZ.getMay();
				}
				if (itemPP.getMay() != null) {
					sumHourRZ_PP = sumHourRZ_PP + itemPP.getMay();
				}
				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setMay(shipTotal.getHandswork() / 31);
					if ((sumHourRZ_PP != null) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setMay(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getMay();
						if ((itemContainer.getMay() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setMay((itemContainer.getMay() + itemRZ_TW_SWS.getMay()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getMay();
							containerOnMenNotNull++;
							itemContainerOnHours.setMay((itemContainer.getMay() + itemRZ_TW_SWS.getMay()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getMay() != null) && !itemContainerOnHours.getMay().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getMay();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setMay(itemRZ_TW_SWS.getMay());
							if ((itemRZ_TW_SWS.getMay() != null) && !itemRZ_TW_SWS.getMay().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getMay();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setMay(0.0);
						}
					} else {
						itemMenOnHand.setMay(0.0);
					}
				} else {
					itemHandsOnDays.setMay(0.0);
				}

				totalHoursRZ_PP_task.setMay(sumHourRZ_PP);
			} else if (shipTotal.getMonthInvoice() == 6) {
				itemHands.setJun(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setJun((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setJun(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (itemRZ.getJun() != null) {
					sumHourRZ_PP = itemRZ.getJun();
				}
				if (itemPP.getJun() != null) {
					sumHourRZ_PP = sumHourRZ_PP + itemPP.getJun();
				}
				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setJun(shipTotal.getHandswork() / 30);
					if ((sumHourRZ_PP != null) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setJun(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getJun();
						if ((itemContainer.getJun() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setJun((itemContainer.getJun() + itemRZ_TW_SWS.getJun()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getJun();
							containerOnMenNotNull++;
							itemContainerOnHours.setJun((itemContainer.getJun() + itemRZ_TW_SWS.getJun()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getJun() != null) && !itemContainerOnHours.getJun().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getJun();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setJun(itemRZ_TW_SWS.getJun());
							if ((itemRZ_TW_SWS.getJun() != null) && !itemRZ_TW_SWS.getJun().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getJun();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setJun(0.0);
						}
					} else {
						itemMenOnHand.setJun(0.0);
					}
				} else {
					itemHandsOnDays.setJun(0.0);
				}

				totalHoursRZ_PP_task.setJun(sumHourRZ_PP);
			} else if (shipTotal.getMonthInvoice() == 7) {
				itemHands.setJul(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setJul((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setJul(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (itemRZ.getJul() != null) {
					sumHourRZ_PP = itemRZ.getJul();
				}
				if (itemPP.getJul() != null) {
					sumHourRZ_PP = sumHourRZ_PP + itemPP.getJul();
				}
				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setJul(shipTotal.getHandswork() / 31);
					if ((sumHourRZ_PP != null) && (sumHourRZ_PP != 0)) {
						itemMenOnHand.setJul(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getJul();
						if ((itemContainer.getJul() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setJul((itemContainer.getJul() + itemRZ_TW_SWS.getJul()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getJul();
							containerOnMenNotNull++;
							itemContainerOnHours.setJul((itemContainer.getJul() + itemRZ_TW_SWS.getJul()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getJul() != null) && !itemContainerOnHours.getJul().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getJul();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setJul(itemRZ_TW_SWS.getJul());
							if ((itemRZ_TW_SWS.getJul() != null) && !itemRZ_TW_SWS.getJul().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getJul();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setJul(0.0);
						}
					} else {
						itemMenOnHand.setJul(0.0);
					}
				} else {
					itemHandsOnDays.setJul(0.0);
				}

				totalHoursRZ_PP_task.setJul(sumHourRZ_PP);
			} else if (shipTotal.getMonthInvoice() == 8) {
				itemHands.setAug(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setAug((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setAug(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (itemRZ.getAug() != null) {
					sumHourRZ_PP = itemRZ.getAug();
				}
				if (itemPP.getAug() != null) {
					sumHourRZ_PP = sumHourRZ_PP + itemPP.getAug();
				}
				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setAug(shipTotal.getHandswork() / 31);
					if ((sumHourRZ_PP != null) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setAug(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getAug();
						if ((itemContainer.getAug() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setAug((itemContainer.getAug() + itemRZ_TW_SWS.getAug()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getAug();
							containerOnMenNotNull++;
							itemContainerOnHours.setAug((itemContainer.getAug() + itemRZ_TW_SWS.getAug()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getAug() != null) && !itemContainerOnHours.getAug().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getAug();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setAug(itemRZ_TW_SWS.getAug());
							if ((itemRZ_TW_SWS.getAug() != null) && !itemRZ_TW_SWS.getAug().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getAug();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setAug(0.0);
						}
					} else {
						itemMenOnHand.setAug(0.0);
					}
				} else {
					itemHandsOnDays.setAug(0.0);
				}
				totalHoursRZ_PP_task.setAug(sumHourRZ_PP);
			} else if (shipTotal.getMonthInvoice() == 9) {
				itemHands.setSep(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setSep((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setSep(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (itemRZ.getSep() != null) {
					sumHourRZ_PP = itemRZ.getSep();
				}
				if (itemPP.getSep() != null) {
					sumHourRZ_PP = sumHourRZ_PP + itemPP.getSep();
				}
				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setSep(shipTotal.getHandswork() / 30);
					if ((sumHourRZ_PP != null) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setSep(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getSep();
						if ((itemContainer.getSep() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setSep((itemContainer.getSep() + itemRZ_TW_SWS.getSep()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getSep();
							containerOnMenNotNull++;
							itemContainerOnHours.setSep((itemContainer.getSep() + itemRZ_TW_SWS.getSep()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getSep() != null) && !itemContainerOnHours.getSep().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getSep();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setSep(itemRZ_TW_SWS.getSep());
							if ((itemRZ_TW_SWS.getSep() != null) && !itemRZ_TW_SWS.getSep().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getSep();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setSep(0.0);
						}
					} else {
						itemMenOnHand.setSep(0.0);
					}
				} else {
					itemHandsOnDays.setSep(0.0);
				}

				totalHoursRZ_PP_task.setSep(sumHourRZ_PP);
			} else if (shipTotal.getMonthInvoice() == 10) {
				itemHands.setOct(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setOct((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setOct(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (itemRZ.getOct() != null) {
					sumHourRZ_PP = itemRZ.getOct();
				}
				if (itemPP.getOct() != null) {
					sumHourRZ_PP = sumHourRZ_PP + itemPP.getOct();
				}
				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setOct(shipTotal.getHandswork() / 31);
					if ((sumHourRZ_PP != null) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setOct(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getOct();
						if ((itemContainer.getOct() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setOct((itemContainer.getOct() + itemRZ_TW_SWS.getOct()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getOct();
							containerOnMenNotNull++;
							itemContainerOnHours.setOct((itemContainer.getOct() + itemRZ_TW_SWS.getOct()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getOct() != null) && !itemContainerOnHours.getOct().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getOct();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setOct(itemRZ_TW_SWS.getOct());
							if ((itemRZ_TW_SWS.getOct() != null) && !itemRZ_TW_SWS.getOct().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getOct();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setOct(0.0);
						}
					} else {
						itemMenOnHand.setOct(0.0);
					}
				} else {
					itemHandsOnDays.setOct(0.0);
				}

				totalHoursRZ_PP_task.setOct(sumHourRZ_PP);
			} else if (shipTotal.getMonthInvoice() == 11) {
				itemHands.setNov(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setNov((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setNov(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (itemRZ.getNov() != null) {
					sumHourRZ_PP = itemRZ.getNov();
				}
				if (itemPP.getNov() != null) {
					sumHourRZ_PP = sumHourRZ_PP + itemPP.getNov();
				}
				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setNov(shipTotal.getHandswork() / 31);
					if ((sumHourRZ_PP != null) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setNov(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getNov();
						if ((itemContainer.getNov() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setNov((itemContainer.getNov() + itemRZ_TW_SWS.getNov()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getNov();
							containerOnMenNotNull++;
							itemContainerOnHours.setNov((itemContainer.getNov() + itemRZ_TW_SWS.getNov()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getNov() != null) && !itemContainerOnHours.getNov().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getNov();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setNov(itemRZ_TW_SWS.getNov());
							if ((itemRZ_TW_SWS.getNov() != null) && !itemRZ_TW_SWS.getNov().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getNov();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setNov(0.0);
						}
					} else {
						itemMenOnHand.setNov(0.0);
					}
				} else {
					itemHandsOnDays.setNov(0.0);
				}

				totalHoursRZ_PP_task.setNov(sumHourRZ_PP);
			} else if (shipTotal.getMonthInvoice() == 12) {
				itemHands.setDec(shipTotal.getHandswork());
				if (shipTotal.getWindyday() != null) {
					itemWindyDay.setDec((double) shipTotal.getWindyday());
				} else {
					itemWindyDay.setDec(0.0);
				}
				Double sumHourRZ_PP = 0.0;
				if (itemRZ.getDec() != null) {
					sumHourRZ_PP = itemRZ.getDec();
				}
				if (itemPP.getDec() != null) {
					sumHourRZ_PP = sumHourRZ_PP + itemPP.getDec();
				}
				if (shipTotal.getHandswork() != null) {
					itemHandsOnDays.setDec(shipTotal.getHandswork() / 31);
					if ((sumHourRZ_PP != null) && (shipTotal.getHandswork() != 0)) {
						itemMenOnHand.setDec(sumHourRZ_PP / shipTotal.getHandswork() / 6);
						totalMenOnHand += itemMenOnHand.getDec();
						if ((itemContainer.getDec() != null) && (sumHourRZ_PP != 0)) {
							itemContainerOnMen.setDec((itemContainer.getDec() + itemRZ_TW_SWS.getDec()) / sumHourRZ_PP);
							totalContainerOnMen += itemContainerOnMen.getDec();
							containerOnMenNotNull++;
							itemContainerOnHours.setDec((itemContainer.getDec() + itemRZ_TW_SWS.getDec()) / shipTotal.getHandswork() / 6);
							if ((itemContainerOnHours.getDec() != null) && !itemContainerOnHours.getDec().equals(0)) {
								totalContainerOnHours += itemContainerOnHours.getDec();
								containerOnHoursNotNull++;
							}
						} else {
							itemContainerOnMen.setDec(itemRZ_TW_SWS.getDec());
							if ((itemRZ_TW_SWS.getDec() != null) && !itemRZ_TW_SWS.getDec().equals(0)) {
								totalContainerOnMen += itemContainerOnMen.getDec();
								containerOnMenNotNull++;
							}
							itemContainerOnHours.setDec(0.0);
						}
					} else {
						itemMenOnHand.setDec(0.0);
					}
				} else {
					itemHandsOnDays.setDec(0.0);
				}

				totalHoursRZ_PP_task.setDec(sumHourRZ_PP);
			}
		}

		itemHands.setTot(totalHands);
		itemHands.setAvg(totalHands / numberOfHandsNotNull);

		itemHandsOnDays.setAvg(totalHands / 365);

		if (numberMenOnHandNotNull != 0) {
			itemMenOnHand.setAvg(totalMenOnHand / numberMenOnHandNotNull);
		} else {
			itemMenOnHand.setAvg(0.0);
		}

		if (containerOnMenNotNull != 0) {
			itemContainerOnMen.setAvg(totalContainerOnMen / containerOnMenNotNull);
		} else {
			itemContainerOnMen.setAvg(0.0);
		}

		itemContainerOnMen.setTot(0.0);

		if (containerOnHoursNotNull != 0) {
			itemContainerOnHours.setAvg(totalContainerOnHours / containerOnHoursNotNull);
		} else {
			itemContainerOnHours.setAvg(0.0);
		}

		if (!numberOfWindyDayNotNull.equals(0)) {
			itemWindyDay.setAvg((double) (totalWindyDay / numberOfWindyDayNotNull));
		} else {
			itemWindyDay.setAvg(0.0);
		}
		itemWindyDay.setTot((double) totalWindyDay);

		this.reportList.add(0, itemContainer);
		this.reportList.add(1, itemRZ_TW_SWS);
		this.reportList.add(2, itemRZ_TW_MCT);
		this.reportList.add(3, itemHands);
		this.reportList.add(4, itemHandsOnDays);
		this.reportList.add(5, itemMenOnHand);
		this.reportList.add(6, itemContainerOnMen);
		this.reportList.add(7, itemContainerOnHours);
		this.reportList.add(8, itemProductivity);

		// Mani consuntivate
		Double totalHandsC = 0.0;
		Integer numberOfHandsNotNullC = 0;

		int indexRowHandsC = 9;

		final List<Integer> handsC = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			handsC.add(0);
		}

		for (int i = 1; i <= 4; i++) {
			final List<ShipTotal> list = this.statisticDAO.getTotalHandsMen(year, i, idServiceRZ, this.getInvoce());
			totalHandsC = 0.0;
			numberOfHandsNotNullC = 0;

			final ReportItem itemHandsC = new ReportItem();
			itemHandsC.setArgument(ReportItemTag.HandsC_shift + i);

			for (final ShipTotal shipTotal : list) {

				if ((shipTotal.getHandswork() != null) && !shipTotal.getHandswork().equals(0.0)) {
					totalHandsC += shipTotal.getHandswork();
					numberOfHandsNotNullC++;
				}

				if (shipTotal.getMonthInvoice() == 1) {
					itemHandsC.setGen(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(0, (int) (handsC.get(0) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 2) {
					itemHandsC.setFeb(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(1, (int) (handsC.get(1) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 3) {
					itemHandsC.setMar(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(2, (int) (handsC.get(2) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 4) {
					itemHandsC.setApr(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(3, (int) (handsC.get(3) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 5) {
					itemHandsC.setMay(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(4, (int) (handsC.get(4) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 6) {
					itemHandsC.setJun(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(5, (int) (handsC.get(5) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 7) {
					itemHandsC.setJul(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(6, (int) (handsC.get(6) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 8) {
					itemHandsC.setAug(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(7, (int) (handsC.get(7) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 9) {
					itemHandsC.setSep(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(8, (int) (handsC.get(8) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 10) {
					itemHandsC.setOct(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(9, (int) (handsC.get(9) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 11) {
					itemHandsC.setNov(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(10, (int) (handsC.get(10) + shipTotal.getHandswork()));
					}
				} else if (shipTotal.getMonthInvoice() == 12) {
					itemHandsC.setDec(shipTotal.getHandswork());
					if (shipTotal.getHandswork() != null) {
						handsC.set(11, (int) (handsC.get(11) + shipTotal.getHandswork()));
					}
				}

			}

			itemHandsC.setTot(totalHandsC);
			if ((numberOfHandsNotNullC != 0) && (totalHandsC != null)) {
				itemHandsC.setAvg(totalHandsC / numberOfHandsNotNullC);
			} else {
				itemHandsC.setAvg(null);
			}
			this.reportList.add(indexRowHandsC, itemHandsC);
			indexRowHandsC++;
		}

		// Mani programmate
		Double totalHandsP = 0.0;
		Integer numberOfHandsNotNullP = 0;

		int indexRowHandsP = 13;

		final List<Integer> handsP = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			handsP.add(0);
		}

		for (int i = 1; i <= 4; i++) {
			final List<ShipTotal> list = this.statisticDAO.getTotalHandsMen(year, i, idServiceRZ, this.getInvoce());
			totalHandsP = 0.0;
			numberOfHandsNotNullP = 0;

			final ReportItem itemHandsP = new ReportItem();
			itemHandsP.setArgument(ReportItemTag.HandsP_shift + i);

			for (final ShipTotal shipTotal : list) {

				if ((shipTotal.getHandswork_program() != null) && !shipTotal.getHandswork_program().equals(0.0)) {
					totalHandsP += shipTotal.getHandswork_program();
					numberOfHandsNotNullP++;
				}

				if (shipTotal.getMonthInvoice() == 1) {
					itemHandsP.setGen(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(0, (int) (handsP.get(0) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 2) {
					itemHandsP.setFeb(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(1, (int) (handsP.get(1) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 3) {
					itemHandsP.setMar(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(2, (int) (handsP.get(2) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 4) {
					itemHandsP.setApr(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(3, (int) (handsP.get(3) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 5) {
					itemHandsP.setMay(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(4, (int) (handsP.get(4) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 6) {
					itemHandsP.setJun(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(5, (int) (handsP.get(5) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 7) {
					itemHandsP.setJul(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(6, (int) (handsP.get(6) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 8) {
					itemHandsP.setAug(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(7, (int) (handsP.get(7) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 9) {
					itemHandsP.setSep(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(8, (int) (handsP.get(8) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 10) {
					itemHandsP.setOct(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(9, (int) (handsP.get(9) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 11) {
					itemHandsP.setNov(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(10, (int) (handsP.get(10) + shipTotal.getHandswork_program()));
					}
				} else if (shipTotal.getMonthInvoice() == 12) {
					itemHandsP.setDec(shipTotal.getHandswork_program());
					if (shipTotal.getHandswork_program() != null) {
						handsP.set(11, (int) (handsP.get(11) + shipTotal.getHandswork_program()));
					}
				}
			}

			itemHandsP.setTot(totalHandsP);
			if ((numberOfHandsNotNullP != 0) && (totalHandsP != null)) {
				itemHandsP.setAvg(totalHandsP / numberOfHandsNotNullP);
			} else {
				itemHandsP.setAvg(null);
			}
			this.reportList.add(indexRowHandsP, itemHandsP);
			indexRowHandsP++;
		}

		// Mani C-P
		final ReportItem itemHandsC_P = new ReportItem();
		itemHandsC_P.setArgument(ReportItemTag.HandsC_P);
		for (int i = 1; i <= 12; i++) {
			switch (i) {
				case 1:
					itemHandsC_P.setGen((double) handsC.get(0) - handsP.get(0));
					break;
				case 2:
					itemHandsC_P.setFeb((double) handsC.get(1) - handsP.get(1));
					break;
				case 3:
					itemHandsC_P.setMar((double) handsC.get(2) - handsP.get(2));
					break;
				case 4:
					itemHandsC_P.setApr((double) handsC.get(3) - handsP.get(3));
					break;
				case 5:
					itemHandsC_P.setMay((double) handsC.get(4) - handsP.get(4));
					break;
				case 6:
					itemHandsC_P.setJun((double) handsC.get(5) - handsP.get(5));
					break;
				case 7:
					itemHandsC_P.setJul((double) handsC.get(6) - handsP.get(6));
					break;
				case 8:
					itemHandsC_P.setAug((double) handsC.get(7) - handsP.get(7));
					break;
				case 9:
					itemHandsC_P.setSep((double) handsC.get(8) - handsP.get(8));
					break;
				case 10:
					itemHandsC_P.setOct((double) handsC.get(9) - handsP.get(9));
					break;
				case 11:
					itemHandsC_P.setNov((double) handsC.get(10) - handsP.get(10));
					break;
				case 12:
					itemHandsC_P.setDec((double) handsC.get(11) - handsP.get(11));
					break;
				default:
					break;
			}
		}

		itemHandsC_P.setTot(null);
		this.reportList.add(indexRowHandsP, itemHandsC_P);

		// RECLAMI CLIENTI
		final List<Customer> customerList = this.customerDAO.listAllCustomers();
		indexRowHandsP++;
		Integer totalComplaint = 0;
		Integer complainNotNull = 0;

		this.customerComplaint = new HashMap<>();

		if (customerList != null) {
			for (final Customer customer : customerList) {
				if (customer == null) {
					break;
				}
				final ReportItem itemComplaint = new ReportItem();
				itemComplaint.setArgument(ReportItemTag.CustomerComplaint + customer.getName());

				final HashMap<Integer, ShipTotal> complaintList = this.statisticDAO.countComplaintByCustomer(year, customer.getId());

				if ((complaintList.get(1) != null) && !complaintList.get(1).getNumberofcomplaint().equals(0)) {
					itemComplaint.setGen((double) complaintList.get(1).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getGen());
					complainNotNull++;
				} else {
					itemComplaint.setGen(null);
				}
				if ((complaintList.get(2) != null) && !complaintList.get(2).getNumberofcomplaint().equals(0)) {
					itemComplaint.setFeb((double) complaintList.get(2).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getFeb());
					complainNotNull++;
				} else {
					itemComplaint.setFeb(null);
				}
				if ((complaintList.get(3) != null) && !complaintList.get(3).getNumberofcomplaint().equals(0)) {
					itemComplaint.setMar((double) complaintList.get(3).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getMar());
					complainNotNull++;
				} else {
					itemComplaint.setMar(null);
				}
				if ((complaintList.get(4) != null) && !complaintList.get(4).getNumberofcomplaint().equals(0)) {
					itemComplaint.setApr((double) complaintList.get(4).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getApr());
					complainNotNull++;
				} else {
					itemComplaint.setApr(null);
				}
				if ((complaintList.get(5) != null) && !complaintList.get(5).getNumberofcomplaint().equals(0)) {
					itemComplaint.setMay((double) complaintList.get(5).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getMay());
					complainNotNull++;
				} else {
					itemComplaint.setMay(null);
				}
				if ((complaintList.get(6) != null) && !complaintList.get(6).getNumberofcomplaint().equals(0)) {
					itemComplaint.setJun((double) complaintList.get(6).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getJun());
					complainNotNull++;
				} else {
					itemComplaint.setJun(null);
				}
				if ((complaintList.get(7) != null) && !complaintList.get(7).getNumberofcomplaint().equals(0)) {
					itemComplaint.setJul((double) complaintList.get(7).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getJul());
					complainNotNull++;
				} else {
					itemComplaint.setJul(null);
				}
				if ((complaintList.get(8) != null) && !complaintList.get(8).getNumberofcomplaint().equals(0)) {
					itemComplaint.setAug((double) complaintList.get(8).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getAug());
					complainNotNull++;
				} else {
					itemComplaint.setAug(null);
				}
				if ((complaintList.get(9) != null) && !complaintList.get(9).getNumberofcomplaint().equals(0)) {
					itemComplaint.setSep((double) complaintList.get(9).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getSep());
					complainNotNull++;
				} else {
					itemComplaint.setSep(null);
				}
				if ((complaintList.get(10) != null) && !complaintList.get(10).getNumberofcomplaint().equals(0)) {
					itemComplaint.setOct((double) complaintList.get(10).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getOct());
					complainNotNull++;
				} else {
					itemComplaint.setOct(null);
				}
				if ((complaintList.get(11) != null) && !complaintList.get(11).getNumberofcomplaint().equals(0)) {
					itemComplaint.setNov((double) complaintList.get(11).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getNov());
					complainNotNull++;
				} else {
					itemComplaint.setNov(null);
				}
				if ((complaintList.get(12) != null) && !complaintList.get(12).getNumberofcomplaint().equals(0)) {
					itemComplaint.setDec((double) complaintList.get(12).getNumberofcomplaint());
					totalComplaint = (int) (totalComplaint + itemComplaint.getDec());
					complainNotNull++;
				} else {
					itemComplaint.setDec(null);
				}

				itemComplaint.setTot((double) totalComplaint);
				itemComplaint.setAvg(0.0);
				if (!complainNotNull.equals(0)) {
					itemComplaint.setAvg((double) totalComplaint / complainNotNull);
				}
				totalComplaint = 0;
				complainNotNull = 0;

				this.customerComplaint.put(customer.getId(), new HashMap<>(complaintList));

				this.reportList.add(indexRowHandsP, itemComplaint);
				indexRowHandsP++;
			}
		}

		this.reportList.add(indexRowHandsP, itemWindyDay);

		// NUMERO NAVI
		final List<ShipTotal> shipNumberTW = this.statistic_dao.getShipNumber("TW", year, this.getInvoce());
		Integer totalShipNumber = 0;
		Integer totalShipNumberNotNull = 0;
		if ((shipNumberTW != null) && (shipNumberTW.size() != 0)) {
			for (final ShipTotal shipTotal : shipNumberTW) {
				if (shipTotal != null) {
					if ((shipTotal.getShipnumber() != null) && !shipTotal.getShipnumber().equals(0)) {
						totalShipNumber = totalShipNumber + shipTotal.getShipnumber();
						totalShipNumberNotNull++;
					}
					if (shipTotal.getMonthInvoice().equals(1)) {
						itemShipNumberTW.setGen((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(2)) {
						itemShipNumberTW.setFeb((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(3)) {
						itemShipNumberTW.setMar((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(4)) {
						itemShipNumberTW.setApr((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(5)) {
						itemShipNumberTW.setMay((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(6)) {
						itemShipNumberTW.setJun((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(7)) {
						itemShipNumberTW.setJul((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(8)) {
						itemShipNumberTW.setAug((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(9)) {
						itemShipNumberTW.setSep((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(10)) {
						itemShipNumberTW.setOct((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(11)) {
						itemShipNumberTW.setNov((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(12)) {
						itemShipNumberTW.setDec((double) shipTotal.getShipnumber());
					}
				}
			}
		}
		itemShipNumberTW.setAvg(0.0);
		itemShipNumberTW.setTot((double) totalShipNumber);
		if (!totalShipNumberNotNull.equals(0)) {
			itemShipNumberTW.setAvg((double) (totalShipNumber / totalShipNumberNotNull));
		}

		indexRowHandsP++;
		this.reportList.add(indexRowHandsP, itemShipNumberTW);

		final List<ShipTotal> shipNumberComplete = this.statistic_dao.getShipNumber("COM", year, this.getInvoce());
		totalShipNumber = 0;
		totalShipNumberNotNull = 0;
		if ((shipNumberComplete != null) && (shipNumberComplete.size() != 0)) {
			for (final ShipTotal shipTotal : shipNumberComplete) {
				if (shipTotal != null) {
					if ((shipTotal.getShipnumber() != null) && !shipTotal.getShipnumber().equals(0)) {
						totalShipNumber = totalShipNumber + shipTotal.getShipnumber();
						totalShipNumberNotNull++;
					}
					if (shipTotal.getMonthInvoice().equals(1)) {
						itemShipNumberCM.setGen((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(2)) {
						itemShipNumberCM.setFeb((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(3)) {
						itemShipNumberCM.setMar((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(4)) {
						itemShipNumberCM.setApr((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(5)) {
						itemShipNumberCM.setMay((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(6)) {
						itemShipNumberCM.setJun((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(7)) {
						itemShipNumberCM.setJul((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(8)) {
						itemShipNumberCM.setAug((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(9)) {
						itemShipNumberCM.setSep((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(10)) {
						itemShipNumberCM.setOct((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(11)) {
						itemShipNumberCM.setNov((double) shipTotal.getShipnumber());
					} else if (shipTotal.getMonthInvoice().equals(12)) {
						itemShipNumberCM.setDec((double) shipTotal.getShipnumber());
					}
				}
			}
		}
		itemShipNumberCM.setAvg(0.0);
		itemShipNumberCM.setTot((double) totalShipNumber);
		if (!totalShipNumberNotNull.equals(0)) {
			itemShipNumberCM.setAvg((double) (totalShipNumber / totalShipNumberNotNull));
		}

		indexRowHandsP++;
		this.reportList.add(indexRowHandsP, itemShipNumberCM);
		int i = 0;

		// ITEM SERVICE
		final ReportItem totalServiceTimeWork = new ReportItem();
		totalServiceTimeWork.setArgument(ReportItemTag.Service_TimeWorkTotal);
		Double totalServiceTimeWorks = 0.0;
		Integer totaleServiceTimeWorkNotNull = 0;
		int indexTotalServiceTimeWorkRow = indexRowHandsP + 1;
		final List<Service> services = this.configurationDao.selectService(null, null, null);
		if ((services != null) && (services.size() != 0)) {
			for (final Service service : services) {
				final List<ShipTotal> listServiceTotal = this.statisticDAO.getTotalInvoiceContainer(year, service.getId(), this.getInvoce());
				final ReportItem itemServiceVolume = new ReportItem();
				itemServiceVolume.setArgument(ReportItemTag.Service_Container + service.getName());
				itemServiceVolume.setIsService(i);
				double totalVolume = 0.0;
				int volumeNotNull = 0;
				final ReportItem itemServiceTimeWork = new ReportItem();
				itemServiceTimeWork.setArgument(ReportItemTag.Service_TimeWork + service.getName());
				itemServiceTimeWork.setIsService(i);
				double totalTimeWork = 0.0;
				int timeWorkNotNull = 0;
				final ReportItem itemServiceCount = new ReportItem();
				itemServiceCount.setArgument(ReportItemTag.Service_NumberOfService + service.getName());
				itemServiceCount.setIsService(i);
				double totalNumberOfService = 0.0;
				int numberOfServiceWorkNotNull = 0;
				final ReportItem itemServiceNumberOfMan = new ReportItem();
				itemServiceNumberOfMan.setArgument(ReportItemTag.Service_NumberOfMan + service.getName());
				itemServiceNumberOfMan.setIsService(i);
				double totalNumberOfMan = 0.0;
				int numberOfManNotNull = 0;
				final ReportItem itemServiceHoursMan = new ReportItem();
				itemServiceHoursMan.setArgument(ReportItemTag.Service_HoursMan + service.getName());
				itemServiceHoursMan.setIsService(i);

				if ((listServiceTotal == null) || (listServiceTotal.size() == 0)) {
					continue;
				}

				for (final ShipTotal shipTotal : listServiceTotal) {

					if ((shipTotal.getContainerInvoice() != null) && !shipTotal.getContainerInvoice().equals(0)) {
						totalVolume = totalVolume + shipTotal.getContainerInvoice();
						volumeNotNull++;
					}
					if ((shipTotal.getTimework() != null) && !shipTotal.getTimework().equals(0)) {
						totalTimeWork = totalTimeWork + shipTotal.getTimework();
						timeWorkNotNull++;
					}
					if ((shipTotal.getCountService() != null) && !shipTotal.getCountService().equals(0)) {
						totalNumberOfService = totalNumberOfService + shipTotal.getCountService();
						numberOfServiceWorkNotNull++;
					}
					if ((shipTotal.getMenwork() != null) && !shipTotal.getMenwork().equals(0)) {
						totalNumberOfMan = totalNumberOfMan + shipTotal.getMenwork();
						numberOfManNotNull++;
					}

					if (shipTotal.getMonthInvoice().equals(1)) {
						itemServiceVolume.setGen(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setGen(shipTotal.getTimework());
						totalServiceTimeWork.setGen(totalServiceTimeWork.getGen() + itemServiceTimeWork.getGen());
						itemServiceCount.setGen((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setGen(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(2)) {
						itemServiceVolume.setFeb(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setFeb(shipTotal.getTimework());
						totalServiceTimeWork.setFeb(totalServiceTimeWork.getFeb() + itemServiceTimeWork.getFeb());
						itemServiceCount.setFeb((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setFeb(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(3)) {
						itemServiceVolume.setMar(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setMar(shipTotal.getTimework());
						totalServiceTimeWork.setMar(totalServiceTimeWork.getMar() + itemServiceTimeWork.getMar());
						itemServiceCount.setMar((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setMar(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(4)) {
						itemServiceVolume.setApr(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setApr(shipTotal.getTimework());
						totalServiceTimeWork.setApr(totalServiceTimeWork.getApr() + itemServiceTimeWork.getApr());
						itemServiceCount.setApr((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setApr(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(5)) {
						itemServiceVolume.setMay(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setMay(shipTotal.getTimework());
						totalServiceTimeWork.setMay(totalServiceTimeWork.getMay() + itemServiceTimeWork.getMay());
						itemServiceCount.setMay((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setMay(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(6)) {
						itemServiceVolume.setJun(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setJun(shipTotal.getTimework());
						totalServiceTimeWork.setJun(totalServiceTimeWork.getJun() + itemServiceTimeWork.getJun());
						itemServiceCount.setJun((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setJun(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(7)) {
						itemServiceVolume.setJul(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setJul(shipTotal.getTimework());
						totalServiceTimeWork.setJul(totalServiceTimeWork.getJul() + itemServiceTimeWork.getJul());
						itemServiceCount.setJul((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setJul(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(8)) {
						itemServiceVolume.setAug(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setAug(shipTotal.getTimework());
						totalServiceTimeWork.setAug(totalServiceTimeWork.getAug() + itemServiceTimeWork.getAug());
						itemServiceCount.setAug((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setAug(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(9)) {
						itemServiceVolume.setSep(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setSep(shipTotal.getTimework());
						totalServiceTimeWork.setSep(totalServiceTimeWork.getSep() + itemServiceTimeWork.getSep());
						itemServiceCount.setSep((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setSep(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(10)) {
						itemServiceVolume.setOct(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setOct(shipTotal.getTimework());
						totalServiceTimeWork.setOct(totalServiceTimeWork.getOct() + itemServiceTimeWork.getOct());
						itemServiceCount.setOct((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setOct(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(11)) {
						itemServiceVolume.setNov(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setNov(shipTotal.getTimework());
						totalServiceTimeWork.setNov(totalServiceTimeWork.getNov() + itemServiceTimeWork.getNov());
						itemServiceCount.setNov((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setNov(shipTotal.getMenwork());
					} else if (shipTotal.getMonthInvoice().equals(12)) {
						itemServiceVolume.setDec(shipTotal.getContainerInvoice());
						itemServiceTimeWork.setDec(shipTotal.getTimework());
						totalServiceTimeWork.setDec(totalServiceTimeWork.getDec() + itemServiceTimeWork.getDec());
						itemServiceCount.setDec((double) shipTotal.getCountService());
						itemServiceNumberOfMan.setDec(shipTotal.getMenwork());
					}
				}
				if (volumeNotNull != 0) {
					itemServiceVolume.setAvg(totalVolume / volumeNotNull);
				} else {
					itemServiceVolume.setAvg(0.0);
				}
				itemServiceVolume.setTot(totalVolume);
				totalVolume = 0;
				volumeNotNull = 0;

				if (timeWorkNotNull != 0) {
					itemServiceTimeWork.setAvg(totalTimeWork / timeWorkNotNull);
				} else {
					itemServiceTimeWork.setAvg(0.0);
				}
				itemServiceTimeWork.setTot(totalTimeWork);
				totalTimeWork = 0;
				timeWorkNotNull = 0;

				if (numberOfServiceWorkNotNull != 0) {
					itemServiceCount.setAvg(totalNumberOfService / numberOfServiceWorkNotNull);
				} else {
					itemServiceCount.setAvg(0.0);
				}
				itemServiceCount.setTot(totalNumberOfService);
				totalNumberOfService = 0;
				numberOfServiceWorkNotNull = 0;

				if (numberOfManNotNull != 0) {
					itemServiceNumberOfMan.setAvg(totalNumberOfMan / numberOfManNotNull);
				} else {
					itemServiceNumberOfMan.setAvg(0.0);
				}
				itemServiceNumberOfMan.setTot(totalNumberOfMan);
				totalNumberOfMan = 0;
				numberOfManNotNull = 0;

				indexRowHandsP++;
				this.reportList.add(indexRowHandsP, itemServiceVolume);
				indexRowHandsP++;
				this.reportList.add(indexRowHandsP, itemServiceTimeWork);
				indexRowHandsP++;
				this.reportList.add(indexRowHandsP, itemServiceCount);
				indexRowHandsP++;
				this.reportList.add(indexRowHandsP, itemServiceNumberOfMan);
				i++;
			}
		}

		totalServiceTimeWorks = totalServiceTimeWork.getGen() + totalServiceTimeWork.getFeb() + totalServiceTimeWork.getMar()
				+ totalServiceTimeWork.getApr() + totalServiceTimeWork.getMay() + totalServiceTimeWork.getJun() + totalServiceTimeWork.getJul()
				+ totalServiceTimeWork.getAug() + totalServiceTimeWork.getSep() + totalServiceTimeWork.getOct() + totalServiceTimeWork.getNov()
				+ totalServiceTimeWork.getDec();

		if (!totalServiceTimeWork.getGen().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getFeb().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getMar().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getApr().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getMay().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getJun().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getJul().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getAug().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getSep().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getOct().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getNov().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}
		if (!totalServiceTimeWork.getDec().equals(0.0)) {
			totaleServiceTimeWorkNotNull++;
		}

		if (totaleServiceTimeWorkNotNull != 0) {
			totalServiceTimeWork.setAvg(totalServiceTimeWorks / totaleServiceTimeWorkNotNull);
		} else {
			totalServiceTimeWork.setAvg(0.0);
		}
		totalServiceTimeWork.setTot(totalServiceTimeWorks);

		this.reportList.add(indexTotalServiceTimeWorkRow++, totalServiceTimeWork);

		this.reportListboxContainer.setModel(new ListModelList<>(this.reportList));
	}

	private void refreshReportView() {

		final Integer year = Calendar.getInstance().get(Calendar.YEAR);

		this.select_year_detail.setSelectedIndex(year - 2014);

	}

	@Listen("onClick = #refreshDetailView")
	public void refreshSchedulerView() {

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();
		final Calendar calendar_from = Calendar.getInstance();
		final Calendar calendar_to = Calendar.getInstance();

		calendar_from.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMinimum(Calendar.DAY_OF_YEAR));
		calendar_to.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMaximum(Calendar.DAY_OF_YEAR));
		this.select_year_detail.setValue("" + calendar_from.get(Calendar.YEAR));
		this.select_month_detail.setSelectedItem(null);

		this.searchArrivalDateShipFrom.setValue(calendar_from.getTime());
		this.searchArrivalDateShipTo.setValue(calendar_to.getTime());

		this.text_search_rifSWS.setValue(null);
		this.text_search_rifMCT.setValue(null);

		this.selectServiceDetail.setSelectedItem(null);
		this.select_customer.setSelectedItem(null);

		this.full_text_search.setValue(null);

		if (selected == null) {
			return;
		}

		if (selected.equals(this.program_item)) {

			this.program_div.setVisible(true);
			this.detail_div.setVisible(false);
			this.review_div.setVisible(false);
			this.report_div.setVisible(false);

			this.refreshProgram();

		} else if (selected.equals(this.detail_item)) {

			this.program_div.setVisible(false);
			this.detail_div.setVisible(true);
			this.review_div.setVisible(false);
			this.report_div.setVisible(false);

			this.refreshDetail();

		} else if (selected.equals(this.verify_review_ship_item)) {

			this.program_div.setVisible(false);
			this.detail_div.setVisible(false);
			this.review_div.setVisible(true);
			this.report_div.setVisible(false);

			this.refreshBAP();

		} else if (selected.equals(this.report_review_ship_item)) {

			this.program_div.setVisible(false);
			this.detail_div.setVisible(false);
			this.review_div.setVisible(false);
			this.report_div.setVisible(true);

			// set invoice report
			this.invoice_yes.setChecked(Boolean.TRUE);

			this.refreshReport();
		}
	}

	@Listen("onClick = #sw_link_deleteship")
	public void removeItem() {

		this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		// take schedule ship
		this.scheduleShip_selected = this.shipSchedulerDao.loadScheduleShip(this.detailScheduleShipSelected.getIdscheduleship());

		final Map<String, String> params = new HashMap<>();
		params.put("sclass", "mybutton Button");

		final Messagebox.Button[] buttons = new Messagebox.Button[2];
		buttons[0] = Messagebox.Button.OK;
		buttons[1] = Messagebox.Button.CANCEL;

		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.EXCLAMATION, null,
				new EventListener<ClickEvent>() {
					@Override
					public void onEvent(final ClickEvent e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							ShipSchedulerComposer.this.deleteDetailship();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				}, params);

	}

	@Listen("onClick = #sw_link_deleteshipProgram")
	public void removeScheduleShipProgram() {
		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {

			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");

			final Messagebox.Button[] buttons = new Messagebox.Button[2];
			buttons[0] = Messagebox.Button.OK;
			buttons[1] = Messagebox.Button.CANCEL;

			Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.EXCLAMATION, null,
					new EventListener<ClickEvent>() {
						@Override
						public void onEvent(final ClickEvent e) {
							if (Messagebox.ON_OK.equals(e.getName())) {
								ShipSchedulerComposer.this.shipSchedulerDao
										.deleteScheduleShip(ShipSchedulerComposer.this.scheduleShip_selected.getId());

								ShipSchedulerComposer.this.refreshProgram();
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {

							}
						}
					}, params);

		}
	}

	@Listen("onClick = #modify_scheduleShipsDetailDaily_command")
	public void saveModifyShipDetailDaily() {

		this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (this.detailScheduleShipSelected == null) {
			return;
		}

		final Date shiftDate = this.shiftdate_Daily.getValue();

		if (shiftDate == null) {
			return;
		}

		this.detailScheduleShipSelected.setShiftdate(shiftDate);

		final Integer shift_no = Integer.parseInt(this.shift_Daily.getValue().toString());
		this.detailScheduleShipSelected.setShift(shift_no);

		this.detailScheduleShipSelected.setOperation(this.operation_Daily.getValue().toString());

		if (this.user_Daily.getSelectedItem() != null) {
			this.detailScheduleShipSelected.setIduser(((Person) this.user_Daily.getSelectedItem().getValue()).getId());
		}

		if (this.usersecond_Daily.getSelectedItem() != null) {
			this.detailScheduleShipSelected.setIdseconduser(((Person) this.usersecond_Daily.getSelectedItem().getValue()).getId());
		}

		this.detailScheduleShipSelected.setNotedetail(this.notedetail.getValue());

		// set info about activity
		this.detailScheduleShipSelected.setActivity_end(null);
		this.detailScheduleShipSelected.setActivity_start(null);

		final Integer id_ship = this.detailScheduleShipSelected.getId_ship();
		final Ship ship = this.shipDao.loadShip(id_ship);

		if ((ship != null) && (ship.getActivityh() != null) && ship.getActivityh().booleanValue()) {

			final Date date_from = this.ship_from_detail.getValue();
			Date date_to = this.ship_to_detail.getValue();

			if (shift_no.equals(4) && this.check_last_shift_detail.isChecked()) {

				final Calendar cal_from = DateUtils.toCalendar(date_from);
				final Calendar cal_to = DateUtils.toCalendar(date_to);
				cal_to.set(Calendar.DAY_OF_YEAR, cal_from.get(Calendar.DAY_OF_YEAR));
				cal_to.add(Calendar.DAY_OF_YEAR, 1);
				date_to = cal_to.getTime();

			} else if (shift_no.equals(4) && !this.check_last_shift_detail.isChecked()) {
				final Calendar cal_from = DateUtils.toCalendar(date_from);
				final Calendar cal_to = DateUtils.toCalendar(date_to);
				cal_to.set(Calendar.DAY_OF_YEAR, cal_from.get(Calendar.DAY_OF_YEAR));
				date_to = cal_to.getTime();
			}

			this.detailScheduleShipSelected.setActivity_end(date_to);
			this.detailScheduleShipSelected.setActivity_start(date_from);

		}

		// update..
		this.shipSchedulerDao.updateDetailScheduleShip(this.detailScheduleShipSelected);

		// reset view
		this.refreshDetail();
		this.alertShiftDate_detail.setVisible(false);
		this.popup_detail_Daily.close();

	}

	@Listen("onClick = #save_review")
	public void saveReview() {

		if (this.sw_list_scheduleShip.getSelectedItem() != null) {
			this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();
		} else {
			this.detailScheduleShipSelected = this.detailScheduleShipSelect;
		}

		if (this.detailScheduleShipSelected == null) {
			return;
		}

		if (this.workedGroup.getSelectedIndex() == 0) {
			this.detailScheduleShipSelected.setWorked(true);
		} else {
			this.detailScheduleShipSelected.setWorked(false);
		}

		if (this.windydayGroup.getSelectedIndex() == 0) {
			this.detailScheduleShipSelected.setWindyday(true);
		} else {
			this.detailScheduleShipSelected.setWindyday(false);
		}

		this.detailScheduleShipSelected.setHandswork(this.handswork_Daily.getValue());
		this.detailScheduleShipSelected.setMenwork(this.menwork_Daily.getValue());

		this.detailScheduleShipSelected.setFirst_down(this.first_down_review.getValue());
		this.detailScheduleShipSelected.setLast_down(this.last_down_review.getValue());

		this.detailScheduleShipSelected.setPerson_down(this.person_down_review.getValue());
		this.detailScheduleShipSelected.setPerson_onboard(this.person_onboard_review.getValue());

		if (this.rain_review.getSelectedItem() != null) {
			this.detailScheduleShipSelected.setRain(this.rain_review.getSelectedItem().getValue().toString());
		} else {
			this.detailScheduleShipSelected.setRain(null);
		}

		if (this.wind_review.getSelectedItem() != null) {
			this.detailScheduleShipSelected.setWind(this.wind_review.getSelectedItem().getValue().toString());
		} else {
			this.detailScheduleShipSelected.setWind(null);
		}

		if (this.sky_review.getSelectedItem() != null) {
			this.detailScheduleShipSelected.setSky(this.sky_review.getSelectedItem().getValue().toString());
		} else {
			this.detailScheduleShipSelected.setSky(null);
		}

		if (this.temperature_review.getSelectedItem() != null) {
			this.detailScheduleShipSelected.setTemperature(this.temperature_review.getSelectedItem().getValue().toString());
		} else {
			this.detailScheduleShipSelected.setTemperature(null);
		}

		// update..
		this.shipSchedulerDao.updateDetailScheduleShip(this.detailScheduleShipSelected);

		// reset view
		this.refreshDetail();

		this.popup_review_detail.close();

	}

	@Listen("onOK = #text_search_rifSWS, #text_search_rifMCT; onCheck = #rif_customer_empty")
	public void searchRifSWS_MCT() {

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected.equals(this.program_item)) {
			this.defineSchedulerView();

		} else if (selected.equals(this.detail_item)) {

			String rifMCT;
			if (this.rif_customer_empty.isChecked()) {
				rifMCT = ShipSchedulerComposer.rif_mct_empty;
			} else {
				rifMCT = this.text_search_rifMCT.getValue();
			}

			final Integer rifSWS = this.text_search_rifSWS.getValue();

			if ((rifMCT != null) && rifMCT.isEmpty()) {
				rifMCT = null;
			}

			if ((rifMCT != null) || (rifSWS != null)) {

				this.list_details_programmed_ship = this.shipSchedulerDao.searchDetailScheduleShipRif_MCT_SWS(rifSWS, rifMCT);

				this.sw_list_scheduleShip.setModel(new ListModelList<>(this.list_details_programmed_ship));

				// calculate details
				this.calculateSummaryShipDetails(this.list_details_programmed_ship);

			} else {
				this.refreshSchedulerView();
			}

		} else if (selected.equals(this.verify_review_ship_item)) {

			this.refreshBAP();

		}

	}

	@Listen("onChange = #searchArrivalDateShipFrom, #searchArrivalDateShipTo; onOK = #searchArrivalDateShipFrom, #searchArrivalDateShipTo; onCheck = #datashift_period")
	public void selectDetailDatePeriod() {

		// the search must to be for date period
		this.searchDateShift.setValue(null);

		this.refreshDetail();
	}

	@Listen("onChange = #searchDateShift; onOK = #searchDateShift")
	public void selectDetailDateshift() {

		// the search must to be for date period
		this.searchArrivalDateShipFrom.setValue(null);
		this.searchArrivalDateShipTo.setValue(null);
		this.select_year_detail.setValue(null);
		this.select_month_detail.setValue(null);

		this.defineSchedulerView();
	}

	@Listen("onChange = #select_month_detail;onClick = #remove_select_month_detail")
	public void selectedMonthDetail() {

		if ((this.select_month_detail.getSelectedItem() == null) || (this.select_month_detail.getSelectedItem().getValue() == null)) {
			this.selectedYearDetail();
			return;
		}

		if ((this.select_year_detail.getSelectedItem() == null) || (this.select_year_detail.getSelectedItem().getValue() == null)) {
			final Integer todayYear = Utility.getYear(Calendar.getInstance().getTime());
			this.select_year_detail.setValue(todayYear.toString());
		}

		final String monthSelected = this.select_month_detail.getSelectedItem().getValue();
		final String yearSelected = this.select_year_detail.getSelectedItem().getValue();

		final Integer year = Integer.parseInt(yearSelected);
		final Integer month = Integer.parseInt(monthSelected);
		final Calendar calendar_from = Calendar.getInstance();
		final Calendar calendar_to = Calendar.getInstance();

		calendar_from.set(Calendar.YEAR, year);
		calendar_from.set(Calendar.MONTH, month - 1);
		calendar_from.set(Calendar.DAY_OF_MONTH, calendar_from.getActualMinimum(Calendar.DAY_OF_MONTH));

		calendar_to.set(Calendar.YEAR, year);
		calendar_to.set(Calendar.MONTH, month - 1);
		calendar_to.set(Calendar.DAY_OF_MONTH, calendar_from.getActualMaximum(Calendar.DAY_OF_MONTH));

		this.searchArrivalDateShipFrom.setValue(calendar_from.getTime());
		this.searchArrivalDateShipTo.setValue(calendar_to.getTime());

		this.defineSchedulerView();
	}

	@Listen("onChange =#select_year_detail; onClick = #remove_select_year_detail")
	public void selectedYearDetail() {

		if ((this.select_year_detail.getSelectedItem() == null) || (this.select_year_detail.getSelectedItem().getValue() == null)) {

			this.searchArrivalDateShipFrom.setValue(null);
			this.searchArrivalDateShipTo.setValue(null);

			this.defineSchedulerView();

			return;

		}

		final String yearSelected = this.select_year_detail.getSelectedItem().getValue();
		final Integer year = Integer.parseInt(yearSelected);

		if (this.select_month_detail.getSelectedItem() != null) {
			final String monthSelected = this.select_month_detail.getSelectedItem().getValue();
			final Integer month = Integer.parseInt(monthSelected);

			final Calendar calendar_from = Calendar.getInstance();
			final Calendar calendar_to = Calendar.getInstance();

			calendar_from.set(Calendar.YEAR, year);
			calendar_from.set(Calendar.MONTH, month - 1);
			calendar_from.set(Calendar.DAY_OF_MONTH, calendar_from.getActualMinimum(Calendar.DAY_OF_MONTH));

			calendar_to.set(Calendar.YEAR, year);
			calendar_to.set(Calendar.MONTH, month - 1);
			calendar_to.set(Calendar.DAY_OF_MONTH, calendar_from.getActualMaximum(Calendar.DAY_OF_MONTH));

			this.searchArrivalDateShipFrom.setValue(calendar_from.getTime());
			this.searchArrivalDateShipTo.setValue(calendar_to.getTime());

		} else {
			final Calendar calendar_from = Calendar.getInstance();
			final Calendar calendar_to = Calendar.getInstance();

			calendar_from.set(Calendar.YEAR, year);
			calendar_from.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMinimum(Calendar.DAY_OF_YEAR));
			calendar_from.set(Calendar.MONTH, calendar_from.getActualMinimum(Calendar.MONTH));

			calendar_to.set(Calendar.YEAR, year);
			calendar_to.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMaximum(Calendar.DAY_OF_YEAR));
			calendar_to.set(Calendar.MONTH, calendar_from.getActualMaximum(Calendar.MONTH));

			this.searchArrivalDateShipFrom.setValue(calendar_from.getTime());
			this.searchArrivalDateShipTo.setValue(calendar_to.getTime());

		}

		this.defineSchedulerView();

	}

	@Listen("onClick = #selectShipNoWork")
	public void selectShipNoWorkInCombobox() {
		final Ship shipNoWork = this.shipDao.getNoWorkShip();

		this.ship_name_schedule.setSelectedItem(null);

		if (shipNoWork != null) {
			final List<Comboitem> list = this.ship_name_schedule.getItems();

			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					final Ship ship = list.get(i).getValue();
					if (ship.getId() == shipNoWork.getId()) {
						this.ship_name_schedule.setSelectedIndex(i);
					}
				}
			}

		}

	}

	@Listen("onClick = #selectShipNoWorkPopup")
	public void selectShipNoWorkInComboboxPopup() {
		final Ship shipNoWork = this.shipDao.getNoWorkShip();

		this.ship_name.setSelectedItem(null);

		if (shipNoWork != null) {
			final List<Comboitem> list = this.ship_name.getItems();

			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					final Ship ship = list.get(i).getValue();
					if (ship.getId() == shipNoWork.getId()) {
						this.ship_name.setSelectedIndex(i);
					}
				}
			}

		}

	}

	/**
	 * Define initial view
	 */
	private void setInitialView() {

		// define initial view
		this.refreshSchedulerView();
		this.refreshDetailView();
		this.refreshBapView();
		this.refreshReportView();

		this.defineSchedulerView();

		// set second item in view selector
		this.scheduler_type_selector.setSelectedItem(this.detail_item);

		// set initial session
		this.program_div.setVisible(false);
		this.detail_div.setVisible(true);
		this.review_div.setVisible(false);
		this.report_div.setVisible(false);

	}

	@Listen("onChange = #shift_Daily")
	public void setVisibilityEndeAfterADay() {
		final Integer shift = Integer.parseInt(this.shift_Daily.getSelectedItem().getValue().toString());
		if (shift == 4) {
			this.check_last_shift_detail.setVisible(true);
		} else {
			this.check_last_shift_detail.setVisible(false);
		}

	}

	@Listen("onClick = #sw_addScheduleShipProgram")
	public void showAddScheduleShipView() {

		this.ship_name_schedule.setSelectedItem(null);
		this.ship_customer_add.setSelectedItem(null);

		this.row_info_activity_ship_add.setVisible(false);
		this.ship_activity_add.setSelectedItem(null);
		this.ship_volume_schedule.setValue(null);
		this.ship_rif_mcf.setValue(null);
		this.ship_arrival_schedule.setValue(null);
		this.ship_departure_schedule.setValue(null);
		this.note_schedule.setValue(null);

		this.servicetype_schedule.setSelectedItem(null);

		this.program_ship_editor.setVisible(true);
	}

	@Listen("onClick = #shipNameProgram, #shipNameDetail")
	public void showDetailShip() {

		Integer idShip = 0;
		if (this.sw_list_scheduleShip.getSelectedItem() != null) {
			this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();
			idShip = this.detailScheduleShipSelected.getId_ship();
		} else if (this.sw_list_scheduleShipProgram.getSelectedItem() != null) {
			this.scheduleShip_selected = this.sw_list_scheduleShipProgram.getSelectedItem().getValue();
			idShip = this.scheduleShip_selected.getIdship();
		}

		final List<Ship> detailShip = new ArrayList<>();

		detailShip.add(this.shipDao.loadShip(idShip));

		this.popup_shipDetail.setModel(new ListModelList<>(detailShip));

	}

	@Listen("onClick = #sw_link_addDetailScheduleShipProgram")
	public void showDetailShipScheduleOnProgram() {

		this.modify_scheduleShipsDetail_command.setVisible(false);
		this.add_scheduleShipsDetail_command.setVisible(true);
		this.alertShiftDate.setVisible(false);

		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {

			this.listDetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

			this.sw_list_scheduleDetailShip.setModel(new ListModelList<>(this.listDetailScheduleShip));
		}

		String msg = ShipSchedulerComposer.CAPTION_DETAIL_PROGRAM_SHIP + " - " + this.scheduleShip_selected.getName();

		if (this.scheduleShip_selected.getIdship_activity() != null) {
			final Ship ship = this.shipDao.loadShip(this.scheduleShip_selected.getIdship_activity());
			msg = msg + ":" + ship.getName();
		}

		this.captionDetailProgramShip.setLabel(msg);

		// set panel editor close
		this.panel_detail_program.setVisible(false);

	}

	@Listen("onClick = #show_add_panel_program")
	public void showPanelAdd() {

		this.shift_Daily.setSelectedIndex(0);
		this.operation_Daily.setSelectedItem(null);
		this.user_Daily.setSelectedItem(null);
		this.usersecond_Daily.setSelectedItem(null);
		this.handswork_Daily.setValue(0);
		this.menwork_Daily.setValue(0);
		this.handswork_program_Daily.setValue("0");
		this.menwork_program_Daily.setValue("0");
		this.shiftdate_Daily.setValue(new Date());
		this.notedetail.setValue("");

		// define if ship activity fields need to be disabled
		final Integer id_ship = this.scheduleShip_selected.getIdship();
		final Ship ship = this.shipDao.loadShip(id_ship);

		if ((ship != null) && (ship.getActivityh() != null) && ship.getActivityh().booleanValue()) {

			this.h_program_period.setVisible(true);

		} else {

			this.ship_from.setValue(null);
			this.ship_to.setValue(null);

			this.h_program_period.setVisible(false);

		}

		this.add_scheduleShipsDetail_command.setVisible(true);
		this.modify_scheduleShipsDetail_command.setVisible(false);

		this.panel_detail_program.setVisible(true);

	}

	@Listen("onClick = #sw_link_reviewscheduleship")
	public void showReviewShipPopup() throws ParseException {
		if (this.sw_list_scheduleShip.getSelectedItem() == null) {
			return;
		}

		final DetailScheduleShip detailSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (detailSelected == null) {
			return;
		}

		// set service info
		final Service serviceSelected = this.configurationDao.loadService(detailSelected.getServiceId());
		if (serviceSelected != null) {
			this.serviceTypeDescriprion.setValue(serviceSelected.getName() + " - " + serviceSelected.getDescription());
			this.franchise_time_default.setValue(serviceSelected.getFranchise_timework().toString());
			this.franchise_volume_default.setValue(serviceSelected.getFranchise_volume().toString());
			this.franchise_volumeunderboard_default.setValue(serviceSelected.getFranchise_volumeunderboard().toString());
			this.franchise_volumeunderboard_sws_default.setValue(serviceSelected.getFranchise_volumeunderboard_sws().toString());
			this.franchise_volumeunde_tw_mct_default.setValue(serviceSelected.getFranchise_volume_tw_mct().toString());
		} else {
			this.serviceTypeDescriprion.setValue("");
			this.franchise_time_default.setValue("");
			this.franchise_volume_default.setValue("");
			this.franchise_volumeunderboard_default.setValue("");
			this.franchise_volumeunderboard_sws_default.setValue("");
			this.franchise_volumeunde_tw_mct_default.setValue("");
		}

		// calculate menwork and hour by user reviewed view
		Integer idShip = detailSelected.getId_ship();

		final Ship ship = this.shipDao.loadShip(idShip);

		// define "fascia oraria"
		Boolean reviewshift = null;

		if (ship.getActivityh()) {
			final ScheduleShip scheduleShip = this.shipSchedulerDao.loadScheduleShip(detailSelected.getIdscheduleship());
			if ((scheduleShip != null) && (scheduleShip.getIdship_activity() != null)) {
				idShip = scheduleShip.getIdship_activity();
			}
			reviewshift = Boolean.TRUE;
		}

		// count worker
		this.menworkReview.setValue("");
		final List<DetailFinalSchedule> countWorker = this.statisticDAO.listDetailFinalSchedule(null, detailSelected.getShift(), null, null,
				detailSelected.getShiftdate(), detailSelected.getShiftdate(), reviewshift, idShip, null);

		final HashMap<Integer, Boolean> hash_counter = new HashMap<>();
		for (final DetailFinalSchedule dt_itm : countWorker) {

			if (hash_counter.containsKey(dt_itm.getId_user())) {
				continue;
			} else {
				hash_counter.put(dt_itm.getId_user(), Boolean.TRUE);
			}
		}

		if (countWorker != null) {
			this.menworkReview.setValue(hash_counter.size() + "");
		}

		final List<DetailFinalSchedule> listDetailRevision = this.statisticDAO.listDetailFinalSchedule(null, detailSelected.getShift(), null, null,
				detailSelected.getShiftdate(), detailSelected.getShiftdate(), reviewshift, idShip, null);
		double count_h = 0;

		for (final DetailFinalSchedule item : listDetailRevision) {
			if (item.getTime() != null) {
				count_h += item.getTime();
			}
		}

		this.hourReview.setValue("" + Utility.roundTwo(count_h));

		ScheduleShip scheduleShip = this.shipSchedulerDao.loadScheduleShip(detailSelected.getIdscheduleship());

		// select if worked or not
		if (detailSelected.getWorked() != null) {
			if (detailSelected.getWorked()) {
				this.workedGroup.setSelectedIndex(0);
			} else {
				this.workedGroup.setSelectedIndex(1);
			}
		} else {
			this.workedGroup.setSelectedIndex(0);
		}
		if ((detailSelected.getWindyday() != null) && detailSelected.getWindyday()) {
			this.windydayGroup.setSelectedIndex(0);
		} else {
			this.windydayGroup.setSelectedIndex(1);
		}

		this.handswork_Daily.setValue(detailSelected.getHandswork());
		this.menwork_Daily.setValue(detailSelected.getMenwork());

		final String hankswork_info = detailSelected.getHandswork_program() == null ? "" : "" + detailSelected.getHandswork_program();
		this.handswork_program_Daily.setValue(hankswork_info);

		final String menwork_info = detailSelected.getMenwork_program() == null ? "" : "" + detailSelected.getMenwork_program();
		this.menwork_program_Daily.setValue(menwork_info);

		// define general value
		this.first_down_review.setValue(detailSelected.getFirst_down());
		this.last_down_review.setValue(detailSelected.getLast_down());

		this.person_down_review.setValue(detailSelected.getPerson_down());
		this.person_onboard_review.setValue(detailSelected.getPerson_onboard());

		this.rain_review.setSelectedItem(null);
		for (final Comboitem itm : this.rain_review.getItems()) {
			if (itm.getValue().equals(detailSelected.getRain())) {
				this.rain_review.setSelectedItem(itm);
				break;
			}
		}

		this.sky_review.setSelectedItem(null);
		for (final Comboitem itm : this.sky_review.getItems()) {
			if (itm.getValue().equals(detailSelected.getSky())) {
				this.sky_review.setSelectedItem(itm);
				break;
			}
		}

		this.wind_review.setSelectedItem(null);
		for (final Comboitem itm : this.wind_review.getItems()) {
			if (itm.getValue().equals(detailSelected.getWind())) {
				this.wind_review.setSelectedItem(itm);
				break;
			}
		}

		this.temperature_review.setSelectedItem(null);
		for (final Comboitem itm : this.temperature_review.getItems()) {
			if (itm.getValue().equals(detailSelected.getTemperature())) {
				this.temperature_review.setSelectedItem(itm);
				break;
			}
		}

		// get ship activity name
		String shipActivity = "";

		this.reviewedTime.setVisible(false);
		this.menwork_activityhRow.setVisible(false);
		this.menwork.setVisible(true);

		this.reviewTimeFrom.setValue(null);
		this.reviewTimeTo.setValue(null);
		this.menwork_activityh.setValue(null);
		this.check_last_shiftReview.setVisible(false);
		this.check_last_shiftReview.setChecked(false);

		final Integer shiftNumber = detailSelected.getShift();
		final Date dateShift = detailSelected.getShiftdate();

		if (scheduleShip.getIdship_activity() != null) {
			final Ship ship_activity = this.shipDao.loadShip(scheduleShip.getIdship_activity());

			if (ship_activity != null) {
				// set name of ship activity
				shipActivity = ": " + ship_activity.getName();

				// set start end time
				if ((detailSelected.getActivity_start() != null) && (detailSelected.getActivity_end() != null)) {
					this.reviewTimeFrom.setValue(detailSelected.getActivity_start());
					this.reviewTimeTo.setValue(detailSelected.getActivity_end());

					if (shiftNumber.equals(4)) {
						this.check_last_shiftReview.setVisible(true);
						this.check_last_shiftReview.setChecked(false);

						final Calendar cal_date_from = DateUtils.toCalendar(detailSelected.getActivity_start());
						final Calendar cal_date_to = DateUtils.toCalendar(detailSelected.getActivity_end());

						final int day_from = cal_date_from.get(Calendar.DAY_OF_YEAR);
						final int day_to = cal_date_to.get(Calendar.DAY_OF_YEAR);

						if (day_from != day_to) {
							this.check_last_shiftReview.setChecked(true);
						}
					}

				} else {
					this.changeBehaviorForShify(shiftNumber, dateShift, this.reviewTimeFrom, this.reviewTimeTo, this.check_last_shiftReview);
				}

			}

			this.reviewedTime.setVisible(true);
			this.menwork_activityhRow.setVisible(true);
			this.menwork.setVisible(false);
		}

		// set ship name and alert
		this.messageUpdateRifMCT.setVisible(false);
		if (detailSelected.getShiftdate() != null) {

			this.infoShipNameAndShift.setValue(detailSelected.getName() + shipActivity + " - Turno " + detailSelected.getShift() + " - Data Turno: "
					+ this.format.format(detailSelected.getShiftdate()));
		} else {
			this.infoShipNameAndShift.setValue(detailSelected.getName() + shipActivity + " - Turno " + detailSelected.getShift());
		}

		// set working and invoicing cycle (default to same value)
		if (detailSelected.getShiftdate() != null) {
			final String val = this.format_month.format(detailSelected.getShiftdate());
			final int val_int = NumberFormat.getInstance().parse(val).intValue();
			this.invoicing_cycle_review.setValue(val_int);
			this.working_cycle_review.setValue(val);
		} else {
			this.invoicing_cycle_review.setValue(null);
			this.invoicing_cycle_review.setValue(null);
		}

		// set rifs
		if (detailSelected.getIdscheduleship() != null) {

			scheduleShip = this.shipSchedulerDao.loadScheduleShip(detailSelected.getIdscheduleship());

			if (scheduleShip != null) {

				this.rif_sws_review.setValue(scheduleShip.getId().toString());

				if (scheduleShip.getRif_mct() != null) {
					this.rif_mct_review.setValue(scheduleShip.getRif_mct());
				}

			} else {
				this.rif_mct_review.setValue(null);
				this.rif_sws_review.setValue(null);
			}

		}

		// set list review
		final int id_itm = detailSelected.getId();
		final List<DetailFinalScheduleShip> final_details = this.shipSchedulerDao.loadDetailFinalScheduleShipByIdDetailScheduleShip(id_itm);
		this.list_reviewDetailScheduleShip.setModel(new ListModelList<>(final_details));

		// set button
		this.panel_editor_review_details.setVisible(false);

	}

	@Listen("onOK=#complaint_gen,#complaint_feb,#complaint_mar,#complaint_apr,#complaint_may,#complaint_jun,#complaint_jul,#complaint_aug,#complaint_sep,#complaint_oct,#complaint_nov,#complaint_dec")
	public void updateCustomerComplaint() {
		if ((this.customerComplaint == null) || (this.select_year_detail.getSelectedItem() == null) || (this.selectCustomer.getSelectedItem() == null)
				|| (this.selectCustomer.getSelectedItem().getValue() == null)) {
			return;
		}

		final Customer customer = this.selectCustomer.getSelectedItem().getValue();
		final Integer year = Integer.parseInt((String) this.select_year_detail.getSelectedItem().getValue());
		if (this.complaint_gen.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_gen.getValue(), 1, year);
			this.complaint_gen.setValue(null);
		}
		if (this.complaint_feb.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_feb.getValue(), 2, year);
			this.complaint_feb.setValue(null);
		}
		if (this.complaint_mar.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_mar.getValue(), 3, year);
			this.complaint_mar.setValue(null);
		}
		if (this.complaint_apr.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_apr.getValue(), 4, year);
			this.complaint_apr.setValue(null);
		}
		if (this.complaint_may.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_may.getValue(), 5, year);
			this.complaint_may.setValue(null);
		}
		if (this.complaint_jun.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_jun.getValue(), 6, year);
			this.complaint_jun.setValue(null);
		}
		if (this.complaint_jul.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_jul.getValue(), 7, year);
			this.complaint_jul.setValue(null);
		}
		if (this.complaint_aug.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_aug.getValue(), 8, year);
			this.complaint_aug.setValue(null);
		}
		if (this.complaint_sep.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_sep.getValue(), 9, year);
			this.complaint_sep.setValue(null);
		}
		if (this.complaint_oct.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_oct.getValue(), 10, year);
			this.complaint_oct.setValue(null);
		}
		if (this.complaint_nov.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_nov.getValue(), 11, year);
			this.complaint_nov.setValue(null);
		}
		if (this.complaint_dec.getValue() != null) {
			this.updateCustomerComplaint(customer.getId(), this.complaint_dec.getValue(), 12, year);
			this.complaint_dec.setValue(null);
		}

		this.refreshReport();

	}

	private void updateCustomerComplaint(final Integer id_customer, final Integer numberOfComplaint, final Integer month, final Integer year) {

		Complaint complaint = this.statistic_dao.loadComplaint(id_customer, year, month);

		if (complaint == null) {
			complaint = new Complaint();
			complaint.setMonth_comp(month);
			complaint.setYear_comp(year);
			complaint.setNumberofcomplaint(numberOfComplaint);
			complaint.setId_customer(id_customer);
			this.statisticDAO.createComplaint(complaint);
		} else {
			complaint.setNumberofcomplaint(numberOfComplaint);
			this.statisticDAO.updateComplaint(complaint);
		}

	}

	@Listen("onClick = #modify_scheduleShipsDetail_command")
	public void updateDetail() {

		if (this.shift.getSelectedItem() == null) {
			return;
		}

		final Date shiftDate = this.shiftdate.getValue();

		if (shiftDate == null) {
			return;
		}

		if (this.scheduleShip_selected == null) {
			return;
		}

		this.detailScheduleShipSelected.setShiftdate(this.shiftdate.getValue());

		// set general day info
		this.detailScheduleShipSelected.setWind(this.wind_detail.getValue());
		this.detailScheduleShipSelected.setRain(this.rain_detail.getValue());
		this.detailScheduleShipSelected.setSky(this.sky_detail.getValue());
		this.detailScheduleShipSelected.setFirst_down(this.first_down_detail.getValue());
		this.detailScheduleShipSelected.setLast_down(this.last_down_detail.getValue());
		this.detailScheduleShipSelected.setTemperature(this.temperature_detail.getValue());

		final Integer shift = Integer.parseInt(this.shift.getValue().toString());
		this.detailScheduleShipSelected.setShift(shift);

		this.detailScheduleShipSelected.setOperation(this.operation.getValue().toString());

		if (this.user.getSelectedItem() != null) {
			final Person person = (Person) this.user.getSelectedItem().getValue();
			this.detailScheduleShipSelected.setIduser(person.getId());
		} else {
			this.detailScheduleShipSelected.setIduser(null);
		}

		if (this.usersecond.getSelectedItem() != null) {
			final Person person = (Person) this.usersecond.getSelectedItem().getValue();
			this.detailScheduleShipSelected.setIdseconduser(person.getId());
		} else {
			this.detailScheduleShipSelected.setIdseconduser(null);
		}

		this.detailScheduleShipSelected.setHandswork_program(this.handswork_program.getValue());
		this.detailScheduleShipSelected.setMenwork_program(this.menwork_program.getValue());
		this.detailScheduleShipSelected.setNotedetail(this.noteshipdetail.getValue());

		// SET ACTIVITY
		this.detailScheduleShipSelected.setActivity_end(null);
		this.detailScheduleShipSelected.setActivity_start(null);
		if (this.scheduleShip_selected.getIdship_activity() != null) {

			final Date date_from = this.ship_from.getValue();
			Date date_to = this.ship_to.getValue();

			if (shift.equals(4) && this.check_last_shift.isChecked()) {

				final Calendar cal_from = DateUtils.toCalendar(date_from);
				final Calendar cal_to = DateUtils.toCalendar(date_to);

				cal_to.set(Calendar.DAY_OF_YEAR, cal_from.get(Calendar.DAY_OF_YEAR));
				cal_to.add(Calendar.DAY_OF_YEAR, 1);
				date_to = cal_to.getTime();

			}

			this.detailScheduleShipSelected.setActivity_end(date_to);
			this.detailScheduleShipSelected.setActivity_start(date_from);
		}

		this.shipSchedulerDao.updateDetailScheduleShip(this.detailScheduleShipSelected);

		// refresh list detail in popup
		this.showDetailShipScheduleOnProgram();

	}

	@Listen("onClick = #updateRifMCT")
	public void updateRifMCT() {

		if (this.sw_list_scheduleShip.getSelectedItem() != null) {
			this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();
		} else {
			this.detailScheduleShipSelected = this.detailScheduleShipSelect;
		}

		if (this.detailScheduleShipSelected == null) {
			return;
		}

		this.detailScheduleShipSelect = this.detailScheduleShipSelected;
		if ((this.detailScheduleShipSelected != null) && (this.detailScheduleShipSelected.getIdscheduleship() != null)) {

			final ScheduleShip scheduleShip = this.shipSchedulerDao.loadScheduleShip(this.detailScheduleShipSelected.getIdscheduleship());

			if (scheduleShip != null) {
				this.shipSchedulerDao.updateRifMCT(scheduleShip.getId(), this.rif_mct_review.getValue());

				this.messageUpdateRifMCT.setVisible(true);
			}
		}

		this.refreshDetail();

	}

	@Listen("onOK=#tp_gen,#tp_feb,#tp_mar,#tp_apr,#tp_may,#tp_jun,#tp_jul,#tp_aug,#tp_sep,#tp_oct,#tp_nov,#tp_dec")
	public void updateTerminalProductivity() {
		if ((this.terminalProductivityList == null) || (this.select_year_detail.getSelectedItem() == null)) {
			return;
		}

		final Integer year = Integer.parseInt((String) this.select_year_detail.getSelectedItem().getValue());
		if (this.tp_gen.getValue() != null) {
			this.updateTerminalProductivity(this.tp_gen.getValue(), 1, year);
			this.tp_gen.setValue(null);
		}
		if (this.tp_feb.getValue() != null) {
			this.updateTerminalProductivity(this.tp_feb.getValue(), 2, year);
			this.tp_feb.setValue(null);
		}
		if (this.tp_mar.getValue() != null) {
			this.updateTerminalProductivity(this.tp_mar.getValue(), 3, year);
			this.tp_mar.setValue(null);
		}
		if (this.tp_apr.getValue() != null) {
			this.updateTerminalProductivity(this.tp_apr.getValue(), 4, year);
			this.tp_apr.setValue(null);
		}
		if (this.tp_may.getValue() != null) {
			this.updateTerminalProductivity(this.tp_may.getValue(), 5, year);
			this.tp_may.setValue(null);
		}
		if (this.tp_jun.getValue() != null) {
			this.updateTerminalProductivity(this.tp_jun.getValue(), 6, year);
			this.tp_jun.setValue(null);
		}
		if (this.tp_jul.getValue() != null) {
			this.updateTerminalProductivity(this.tp_jul.getValue(), 7, year);
			this.tp_jul.setValue(null);
		}
		if (this.tp_aug.getValue() != null) {
			this.updateTerminalProductivity(this.tp_aug.getValue(), 8, year);
			this.tp_aug.setValue(null);
		}
		if (this.tp_sep.getValue() != null) {
			this.updateTerminalProductivity(this.tp_sep.getValue(), 9, year);
			this.tp_sep.setValue(null);
		}
		if (this.tp_oct.getValue() != null) {
			this.updateTerminalProductivity(this.tp_oct.getValue(), 10, year);
			this.tp_oct.setValue(null);
		}
		if (this.tp_nov.getValue() != null) {
			this.updateTerminalProductivity(this.tp_nov.getValue(), 11, year);
			this.tp_nov.setValue(null);
		}
		if (this.tp_dec.getValue() != null) {
			this.updateTerminalProductivity(this.tp_dec.getValue(), 12, year);
			this.tp_dec.setValue(null);
		}

		this.refreshReport();

	}

	private void updateTerminalProductivity(final Double productivity, final Integer month, final Integer year) {
		if (this.terminalProductivityList.get(month) != null) {
			this.terminalProductivityList.get(month).setProductivity(productivity);
			this.statisticDAO.updateTerminalProductivity(this.terminalProductivityList.get(month));
		} else {
			final TerminalProductivity tp = new TerminalProductivity();
			tp.setMonth_tp(month);
			tp.setYear_tp(year);
			tp.setProductivity(productivity);
			this.statisticDAO.createTerminalProductivity(tp);
		}
	}

}
