package org.uario.seaworkengine.zkevent;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.model.Customer;
import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.ICustomerDAO;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.statistics.ReviewShipWorkAggregate;
import org.uario.seaworkengine.statistics.ShipOverview;
import org.uario.seaworkengine.statistics.ShipTotal;
import org.uario.seaworkengine.utility.BeansTag;
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

	private static final String CAPTION_DETAIL_PROGRAM_SHIP = "Dettagli di Programmazione Nave";

	private static final String CAPTION_POPUP_SCHEDULE_SHIP = "Programmazione Nave";

	private static final String CAPTION_SHIP_PROGRAM_LABEL = "Dettaglio di Programmazione Nave";

	private static final String PRINT_PROGRAM = "printProgram";

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Wire
	private Button add_finalDetailScheduleShip_command;

	@Wire
	private Component add_scheduleShips_command;

	@Wire
	Toolbarbutton add_scheduleShipsDetail_command;

	@Wire
	public Row alertShiftDate;

	@Wire
	public Row alertShiftDate_detail;

	@Wire
	private Listheader arrivalDateColumn;

	@Wire
	private Label avgVolmueCurrentMonth;

	@Wire
	private Label avgVolmueShipProgram;

	@Wire
	private Tabbox bap_overview_tab;

	@Wire
	private Caption captionDetailProgramShip;

	@Wire
	private Caption captionPopupScheduleShip;

	@Wire
	private Caption captionShipProgram;

	@Wire
	private Checkbox check_last_shift;

	@Wire
	private Checkbox check_last_shift_detail;

	@Wire
	private Checkbox check_last_shiftReview;

	@Wire
	private org.zkoss.zul.Checkbox crane_gtw_review;

	@Wire
	private Intbox crane_review;

	@Wire
	private Listheader customerColumn;

	private ICustomerDAO customerDAO;

	@Wire
	public Component dailyDetailShip;

	@Wire
	private Datebox date_from_overview;

	@Wire
	private Datebox date_to_overview;

	@Wire
	private Listheader departureDateColumn;

	@Wire
	private Comboitem detail_item;

	@Wire
	private Tab detail_scheduleShip_tab;

	private DetailScheduleShip detailScheduleShipSelected;

	@Wire
	private Component detailShipProgram_download;

	@Wire
	private Datebox first_down_detail;

	@Wire
	private Datebox first_down_review;

	private final SimpleDateFormat format_it_date = new SimpleDateFormat("dd/MM/yyyy");

	private final SimpleDateFormat format_month = new SimpleDateFormat("MM");

	@Wire
	private Textbox full_text_search;

	@Wire
	private Textbox full_text_search_rifMCT;

	@Wire
	private Intbox full_text_search_rifSWS;

	@Wire
	private Textbox full_text_search_ship;

	@Wire
	private Component grid_scheduleShip;

	@Wire
	private Component grid_scheduleShip_details;

	@Wire
	private Row h_detail_period;

	@Wire
	private Row h_program_period;

	@Wire
	private Listheader handsColumn;

	@Wire
	private Intbox handswork;

	@Wire
	public Intbox handswork_Daily;

	@Wire
	private Label infoDetailShipProgram;

	@Wire
	private Label infoShipNameAndShift;

	@Wire
	private Intbox invoicing_cycle_review;

	@Wire
	private Intbox invoicing_cycle_search;

	@Wire
	private Datebox last_down_detail;

	@Wire
	private Datebox last_down_review;

	private List<ReviewShipWork> list_review_work = new ArrayList<ReviewShipWork>();

	private List<ReviewShipWorkAggregate> list_review_work_aggregate = new ArrayList<ReviewShipWorkAggregate>();

	@Wire
	private Listbox list_reviewDetailScheduleShip;

	@Wire
	private Listbox list_ship_statistics;

	private List<DetailScheduleShip> listDetailScheduleShip = new ArrayList<DetailScheduleShip>();

	// statistics - USED DOWNLOAD
	private List<ShipOverview> listShipStatistics = new ArrayList<ShipOverview>();

	@Wire
	private Listheader mctColumn;

	@Wire
	private Intbox menwork;

	@Wire
	public Intbox menwork_Daily;

	@Wire
	private Label messageUpdateRifMCT;

	private List<DetailScheduleShip> modelListDetailScheduleShip = new ArrayList<DetailScheduleShip>();

	private List<ScheduleShip> modelListScheduleShip = new ArrayList<ScheduleShip>();

	@Wire
	private Button modify_finalDetailScheduleShip_command;

	@Wire
	private Component modify_Scheduleships_command;

	@Wire
	private Toolbarbutton modify_scheduleShipsDetail_command;

	@Wire
	private Listheader modifyColumnDetail;

	@Wire
	private Listheader modifyColumnSchedule;

	@Wire
	private Listheader modifyColumnScheduleShip;

	@Wire
	public Label msgAlert;

	@Wire
	public Label msgAlert_detail;

	@Wire
	private Textbox note;

	@Wire
	private Textbox note_review;

	@Wire
	public Textbox note_schedule;

	@Wire
	public Textbox notedetail;

	@Wire
	private Textbox noteshipdetail;

	@Wire
	private Combobox operation;

	@Wire
	public Combobox operation_Daily;

	@Wire
	private Component overview_download;

	@Wire
	private Tabpanel overview_statistics_ship;

	@Wire
	private Tab overviewBap;

	@Wire
	private Tab overviewBapAggregate;

	@Wire
	private Panel panel_detail_program;

	@Wire
	private Component panel_editor_review_details;

	private Person person_logged;

	private PersonDAO personDao;

	@Wire
	private Listheader personsColumn;

	@Wire
	private Popup popu_detail;

	@Wire
	public Popup popup_detail;

	@Wire
	public Popup popup_detail_Daily;

	@Wire
	public Popup popup_review_detail;

	@Wire
	private Popup popup_scheduleShip;

	@Wire
	private Popup popup_ship;

	@Wire
	private Listbox popup_shipDetail;

	@Wire
	private Button print_program_videos;

	@Wire
	private Button print_ShipScheduler;

	@Wire
	private Comboitem program_item;

	@Wire
	private Combobox rain_detail;

	@Wire
	private Combobox rain_review;

	@Wire
	private Component reviewedTime;

	@Wire
	private Timebox reviewTimeFrom;

	@Wire
	private Timebox reviewTimeTo;

	@Wire
	private Component reviewWorkShip;

	@Wire
	private Textbox rif_mct_review;

	@Wire
	private Label rif_sws_review;

	@Wire
	private Listheader rifSWSColumn;

	@Wire
	private Row row_info_activity_ship;

	@Wire
	private Row row_info_activity_ship_add;

	@Wire
	private Combobox scheduler_type_selector;

	private ScheduleShip scheduleShip_selected = null;

	@Wire
	private Textbox search_rifMCT;

	@Wire
	private Intbox search_rifSWS;

	@Wire
	private Datebox searchArrivalDateShipFrom;

	@Wire
	private Datebox searchArrivalDateShipFrom_detail;

	@Wire
	private Datebox searchArrivalDateShipTo;

	@Wire
	private Datebox searchArrivalDateShipTo_detail;

	@Wire
	private Datebox searchDateShift;

	@Wire
	private Datebox searchWorkShip;

	@Wire
	private A selecetedShipName;

	@Wire
	private Combobox select_customer;

	@Wire
	public Combobox select_shift;

	@Wire
	public Combobox select_shiftBap;

	@Wire
	private Combobox select_typeShip;

	@Wire
	private Combobox select_workedShip;

	@Wire
	private Combobox select_year;

	@Wire
	private Combobox shift;

	@Wire
	public Combobox shift_Daily;

	@Wire
	private Datebox shiftdate;

	@Wire
	public Datebox shiftdate_Daily;

	@Wire
	private Listheader shiftNumberColumn;

	@Wire
	private Combobox ship_activity;

	@Wire
	private Combobox ship_activity_add;

	@Wire
	private Datebox ship_arrival;

	@Wire
	public Datebox ship_arrival_schedule;

	@Wire
	private Combobox ship_customer;

	@Wire
	private Combobox ship_customer_add;

	@Wire
	private Datebox ship_departure;

	@Wire
	public Datebox ship_departure_schedule;

	@Wire
	private Timebox ship_from;

	@Wire
	private Timebox ship_from_detail;

	@Wire
	private Combobox ship_name;

	@Wire
	public Combobox ship_name_schedule;

	@Wire
	private Textbox ship_rif_mcf;

	@Wire
	private Label ship_rif_sws;

	@Wire
	private Timebox ship_to;

	@Wire
	private Timebox ship_to_detail;

	@Wire
	private Intbox ship_volume;

	@Wire
	public Intbox ship_volume_schedule;

	protected IShip shipDao;

	@Wire
	private Listheader shipNameColumun;

	@Wire
	private Label shipNumberProgramShip;

	@Wire
	public Component shipProgram;

	@Wire
	private Component shipProgram_download;

	@Wire
	private Textbox shipRif_mcf;

	private IScheduleShip shipSchedulerDao;

	@Wire
	private Intbox shows_rows;

	@Wire
	private Intbox shows_rows_ship;

	@Wire
	private Combobox sky_detail;

	@Wire
	private Combobox sky_review;

	private IStatistics statistic_dao;

	private IStatistics statisticDAO;

	@Wire
	private Tab statisticsShipTab;

	@Wire
	private Label sumVolumeCurrentMonthShipProgram;

	@Wire
	private Label sumVolumeShipProgram;

	@Wire
	private Button sw_addScheduleShipProgram;

	@Wire
	private Toolbarbutton sw_link_reviewscheduleship;

	@Wire
	private Listbox sw_list_reviewWork;

	@Wire
	private Listbox sw_list_reviewWorkAggregate;

	@Wire
	private Listbox sw_list_scheduleDetailShip;

	@Wire
	private Listbox sw_list_scheduleShip;

	@Wire
	private Listbox sw_list_scheduleShipProgram;

	@Wire
	private Combobox temperature_detail;

	@Wire
	private Combobox temperature_review;

	@Wire
	private Doublebox time_review;

	@Wire
	private Label TotalTimeWork;

	@Wire
	private Label TotalVolume;

	@Wire
	private Label TotalVolumeOnBoard;

	@Wire
	private Label TotalVolumeOnBoard_sws;

	@Wire
	private Label TotalVolumeTWMTC;

	@Wire
	private Combobox user;

	@Wire
	public Combobox user_Daily;

	protected PersonDAO userPrep;

	@Wire
	private Combobox usersecond;

	@Wire
	private Combobox usersecond_Daily;

	@Wire
	private Comboitem verify_review_ship_item;

	@Wire
	private Intbox volume_review;

	@Wire
	private Listheader volumeColumn;

	@Wire
	private Listheader volumeColumnScheduleShip;

	@Wire
	private Listheader volumeStatisticColumn;

	@Wire
	private Intbox volumeunde_tw_mct_review;

	@Wire
	private Intbox volumeunderboard_review;

	@Wire
	private Intbox volumeunderboard_sws_review;

	@Wire
	private Combobox wind_detail;

	@Wire
	private Combobox wind_review;

	@Wire
	private Radiogroup workedGroup;

	@Wire
	private Label working_cycle_review;

	@Wire
	private Intbox working_cycle_search;

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

		}

		// set crane
		int crn_val = 1;
		if (this.crane_review.getValue() != null) {
			crn_val = this.crane_review.getValue();
		}
		detailFinalScheduleShip.setCrane(crn_val);

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

		this.shipSchedulerDao.createDetailFinalScheduleShip(detailFinalScheduleShip);

		this.add_finalDetailScheduleShip_command.setVisible(true);
		this.modify_finalDetailScheduleShip_command.setVisible(false);

		this.showReviewShipPopup();

	}

	@Listen("onClick = #addShipSchedule_command")
	public void addScheduleShipCommand() {
		if ((this.ship_name_schedule.getSelectedItem() == null) || (this.ship_arrival_schedule.getValue() == null)
				|| (this.ship_departure_schedule.getValue() == null)
				|| this.ship_arrival_schedule.getValue().after(this.ship_departure_schedule.getValue())) {

			final Map<String, String> params = new HashMap<String, String>();
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

		this.searchScheduleShip();

		this.grid_scheduleShip.setVisible(false);

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

		item.setHandswork(this.handswork.getValue());
		item.setMenwork(this.menwork.getValue());
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
	 * Set the label total volume value. (SHIPPRogram)
	 * */
	private void calculateTotaleVolumeBAP() {

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
				sumVolume += itm_review.getVolume();
			}

			if (itm_review.getVolumeunderboard() != null) {
				sumVolumeOnBoard += itm_review.getVolumeunderboard();
			}

			if (itm_review.getVolumeunderboard_sws() != null) {
				sumVolumeOnBoard_sws += itm_review.getVolumeunderboard_sws();
			}

			if (itm_review.getVolume_tw_mct() != null) {
				sumVolumeMTC += itm_review.getVolume_tw_mct();
			}

			if (itm_review.getTime_work() != null) {
				time_worked += itm_review.getTime_work();
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
	 * Set the label total volume value.
	 * */
	private void calculateTotaleVolumeShipProgram() {

		// set info
		this.shipNumberProgramShip.setValue("");
		this.sumVolumeShipProgram.setValue("");
		this.avgVolmueShipProgram.setValue("");
		this.sumVolumeCurrentMonthShipProgram.setValue("");
		this.avgVolmueCurrentMonth.setValue("");

		Integer sumVolume = 0;
		Integer sumVolumeMonth = 0;
		final ArrayList<Integer> ship_collection = new ArrayList<Integer>();

		if ((this.modelListScheduleShip == null) || (this.modelListScheduleShip.size() == 0)) {
			return;
		}

		if ((this.searchArrivalDateShipFrom.getValue() == null) || (this.searchArrivalDateShipTo.getValue() == null)) {
			return;
		}

		// current cal
		final Calendar current_cal = Calendar.getInstance();

		for (final ScheduleShip itm_review : this.modelListScheduleShip) {

			if (itm_review.getVolume() != null) {
				sumVolume += itm_review.getVolume();

				// get info about volume on month
				if (itm_review.getDeparturedate() != null) {
					final Calendar cal = DateUtils.toCalendar(itm_review.getDeparturedate());

					if (cal.get(Calendar.MONTH) == current_cal.get(Calendar.MONTH)) {
						sumVolumeMonth += itm_review.getVolume();
					}
				}

			}

			if ((itm_review.getIdship_activity() == null) && (itm_review.getIdship() != null) && !ship_collection.contains(itm_review.getIdship())) {
				ship_collection.add(itm_review.getIdship());
			}

		}

		// calculate number of day
		final Date first_day = DateUtils.truncate(this.searchArrivalDateShipFrom.getValue(), Calendar.DATE);
		final Date last_day = DateUtils.truncate(this.searchArrivalDateShipTo.getValue(), Calendar.DATE);
		final long gap = last_day.getTime() - first_day.getTime();
		final long days = TimeUnit.MILLISECONDS.toDays(gap) + 1;

		// calculate daily avg
		Double avg = 0.0;
		if (days != 0) {
			avg = sumVolume.doubleValue() / days;
			avg = Utility.roundTwo(avg);
		}

		// Calculate avg on month
		final int month_days = current_cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		Double avg_month = (double) sumVolumeMonth / (double) month_days;
		avg_month = Utility.roundTwo(avg_month);

		// ship number
		this.shipNumberProgramShip.setValue("" + ship_collection.size());

		this.sumVolumeShipProgram.setValue(sumVolume.toString());
		this.avgVolmueShipProgram.setValue(avg.toString());
		this.sumVolumeCurrentMonthShipProgram.setValue(sumVolumeMonth.toString());
		this.avgVolmueCurrentMonth.setValue(avg_month.toString());

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

		if (((ShipSchedulerComposer.this.shiftdate.getValue() != null) && ((ShipSchedulerComposer.this.shiftdate.getValue().compareTo(
				ShipSchedulerComposer.this.scheduleShip_selected.getArrivaldate()) < 0) || (ShipSchedulerComposer.this.shiftdate.getValue()
						.compareTo(ShipSchedulerComposer.this.scheduleShip_selected.getDeparturedate()) > 0)))) {

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

		if ((((this.detailScheduleShipSelected != null) && (ShipSchedulerComposer.this.shiftdate_Daily.getValue() != null)) && ((ShipSchedulerComposer.this.shiftdate_Daily
				.getValue().compareTo(ShipSchedulerComposer.this.detailScheduleShipSelected.getArrivaldate()) < 0) || (ShipSchedulerComposer.this.shiftdate_Daily
						.getValue().compareTo(ShipSchedulerComposer.this.detailScheduleShipSelected.getDeparturedate()) > 0)))) {

			final String msg = "Attenzione: data arrivo nave " + this.format_it_date.format(this.detailScheduleShipSelected.getArrivaldate())
					+ ", data partenza nave " + this.format_it_date.format(this.detailScheduleShipSelected.getDeparturedate());

			this.msgAlert_detail.setValue(msg);

			this.alertShiftDate_detail.setVisible(true);
		} else {

			this.alertShiftDate_detail.setVisible(false);
		}

	}

	@Listen("onClick= #closeShipSchedule_command")
	public void closeAddShipScheduleView() {

		this.grid_scheduleShip.setVisible(false);
	}

	@Listen("onClick = #currentMonthProgramShip")
	public void currentMonthProgramShip() {

		final Calendar current_cal = Calendar.getInstance();
		current_cal.set(Calendar.DAY_OF_MONTH, 1);
		this.searchArrivalDateShipFrom.setValue(current_cal.getTime());

		current_cal.set(Calendar.DAY_OF_MONTH, current_cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		this.searchArrivalDateShipTo.setValue(current_cal.getTime());

		this.searchScheduleShip();

	}

	@Listen("onChange = #scheduler_type_selector; onSelect = #bap_overview_tab")
	public void defineSchedulerView() {

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected.equals(this.program_item)) {

			this.grid_scheduleShip.setVisible(false);
			this.shipProgram.setVisible(true);
			this.dailyDetailShip.setVisible(false);
			this.reviewWorkShip.setVisible(false);
			this.searchScheduleShip();

		} else if (selected.equals(this.detail_item)) {

			this.grid_scheduleShip.setVisible(false);
			this.shipProgram.setVisible(false);
			this.dailyDetailShip.setVisible(true);
			this.reviewWorkShip.setVisible(false);
			this.refreshScheduleShipListBox();

		} else if (selected.equals(this.verify_review_ship_item)) {

			this.grid_scheduleShip.setVisible(false);
			this.shipProgram.setVisible(false);
			this.dailyDetailShip.setVisible(false);
			this.reviewWorkShip.setVisible(true);

			this.refreshOverviewBAP();
		}
	}

	@Listen("onOK = #shows_rows_ship")
	public void defineView() {

		this.refreshOverviewBAP();
	}

	private void definingPrintableVersion() {
		// set visibility print button
		this.print_ShipScheduler.setVisible(true);
		this.print_program_videos.setVisible(false);

		// set visibility download csv buttons
		this.overview_download.setVisible(false);
		this.shipProgram_download.setVisible(false);
		this.detailShipProgram_download.setVisible(false);

		// set listbox widt
		this.sw_list_scheduleShip.setWidth("950px");
		this.sw_list_scheduleShipProgram.setWidth("950px");
		this.list_reviewDetailScheduleShip.setWidth("950px");
		this.sw_list_reviewWork.setWidth("950px");
		this.list_ship_statistics.setWidth("950px");
		this.bap_overview_tab.setWidth("950px");

		// ship program view
		this.shiftNumberColumn.setWidth("50px");
		this.handsColumn.setWidth("50px");
		this.personsColumn.setWidth("70px");
		this.rifSWSColumn.setWidth("55px");
		this.mctColumn.setWidth("65px");
		this.customerColumn.setWidth("80px");
		this.arrivalDateColumn.setWidth("100px");
		this.departureDateColumn.setWidth("100px");
		this.shipNameColumun.setWidth("125px");
		this.volumeColumnScheduleShip.setWidth("90px");

		// tab statistiche nave
		this.volumeStatisticColumn.setWidth("45px");

		this.sw_addScheduleShipProgram.setVisible(false);

		// set visibility of modify columns in listbox
		this.modifyColumnSchedule.setVisible(false);
		this.modifyColumnScheduleShip.setVisible(false);
		this.modifyColumnDetail.setVisible(false);

		this.dailyDetailShip.setVisible(false);
		this.shipProgram.setVisible(false);
		this.reviewWorkShip.setVisible(false);

		this.scheduler_type_selector.setSelectedItem(null);
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

		this.refreshScheduleShipListBox();
	}

	@Listen("onClick = #sw_link_deleteDetailship")
	public void deleteDetailshipInListDetail() {

		this.detailScheduleShipSelected = this.sw_list_scheduleDetailShip.getSelectedItem().getValue();
		if ((this.detailScheduleShipSelected != null) && (this.scheduleShip_selected != null)) {

			this.shipSchedulerDao.deleteDetailScheduleShip(this.detailScheduleShipSelected.getId());

			this.listDetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

			this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>(this.listDetailScheduleShip));
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
		final ArrayList<String> years = new ArrayList<String>();
		years.add("TUTTI");

		for (Integer i = 2014; i <= (todayYear + 2); i++) {
			years.add(i.toString());
		}

		this.select_year.setModel(new ListModelList<String>(years));

		this.statisticDAO = (IStatistics) SpringUtil.getBean(BeansTag.STATISTICS);

		// check for printable version
		final String att_print = Executions.getCurrent().getParameter(ShipSchedulerComposer.PRINT_PROGRAM);

		if (att_print != null) {

			Events.sendEvent(ZkEventsTag.onShowShipScheduler, this.getSelf(), null);

		}

	}

	@Listen("onClick = #detailShipProgram_download")
	public void downloadCSV_DetailProgramShip() {

		if (this.modelListDetailScheduleShip.size() != 0) {
			final StringBuilder builder = UtilityCSV.downloadCSV_DetailProgramShip(this.modelListDetailScheduleShip, this.customerDAO);

			Filedownload.save(builder.toString(), "application/text", "dettaglio_giornaliero.csv");
		}

	}

	@Listen("onClick = #shipProgram_download")
	public void downloadCSV_ProgramShip() {

		if (this.modelListScheduleShip.size() != 0) {
			final StringBuilder builder = UtilityCSV.downloadCSV_ScheduleProgramShip(this.modelListScheduleShip, this.customerDAO);

			Filedownload.save(builder.toString(), "application/text", "programmazione_navi.csv");
		}

	}

	@Listen("onClick = #overview_download")
	public void downloadCSV_ReviewShipWork() {

		if (this.overviewBap.isSelected() && (this.list_review_work.size() != 0)) {

			final StringBuilder builder = UtilityCSV.downloadCSVReviewShipWork(this.list_review_work);
			Filedownload.save(builder.toString(), "application/text", "bap.csv");

		} else if (this.overviewBapAggregate.isSelected() && (this.list_review_work_aggregate.size() != 0)) {

			final StringBuilder builder = UtilityCSV.downloadCSVReviewShipWorkAggregate(this.list_review_work_aggregate);
			Filedownload.save(builder.toString(), "application/text", "bap_aggregator.csv");

		} else if (this.statisticsShipTab.isSelected() && ((this.listShipStatistics != null) && (this.listShipStatistics.size() != 0))) {

			final StringBuilder builder = UtilityCSV.downloadCSVShipStatistics(this.listShipStatistics);
			Filedownload.save(builder.toString(), "application/text", "bap_aggregator_users.csv");

		}

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

	private Integer getSelectedShift() {
		Integer shiftSelected = null;
		if ((this.select_shift.getSelectedItem() != null) && (this.select_shift.getSelectedIndex() != 0)) {
			shiftSelected = this.select_shift.getSelectedIndex();
		}
		return shiftSelected;
	}

	/**
	 * Reset value popup
	 */
	@Listen("onClick = #add_details_panels_review")
	public void initPopupReviewDetail() {
		this.crane_review.setValue(null);
		this.volume_review.setValue(null);
		this.volumeunderboard_review.setValue(null);
		this.volumeunderboard_sws_review.setValue(null);
		this.volumeunde_tw_mct_review.setValue(null);
		this.note_review.setValue(null);

		this.time_review.setValue(null);
		this.crane_gtw_review.setChecked(Boolean.FALSE);

		this.add_finalDetailScheduleShip_command.setVisible(true);
		this.modify_finalDetailScheduleShip_command.setVisible(false);

		// set initial value
		this.wind_review.setSelectedItem(null);
		this.rain_review.setSelectedItem(null);
		this.sky_review.setSelectedItem(null);
		this.first_down_review.setValue(null);
		this.last_down_review.setValue(null);
		this.temperature_review.setValue(null);

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

		final List<Ship> all_ship = ShipSchedulerComposer.this.shipDao.listAllShip(null);

		// add item in combobox ship name
		if (all_ship.size() == 0) {
			Messagebox.show("Inserire almeno una nave prima di procedere alla programmazione!", "INFO", Messagebox.OK, Messagebox.INFORMATION);
		}

		ShipSchedulerComposer.this.ship_name.setModel(new ListModelList<Ship>(all_ship));
		ShipSchedulerComposer.this.ship_name_schedule.setModel(new ListModelList<Ship>(all_ship));
		ShipSchedulerComposer.this.ship_activity.setModel((new ListModelList<Ship>(all_ship)));
		ShipSchedulerComposer.this.ship_activity_add.setModel((new ListModelList<Ship>(all_ship)));

		// add item operative users in combobox user
		final List<Person> list_op_person = ShipSchedulerComposer.this.personDao.listOperativePerson();
		final ListModel<Person> modelComboBox_User = new ListModelList<Person>(list_op_person);
		ShipSchedulerComposer.this.user.setModel(modelComboBox_User);

		final ListModel<Person> modelComboBox_UserSecond = new ListModelList<Person>(list_op_person);
		ShipSchedulerComposer.this.usersecond.setModel(modelComboBox_UserSecond);

		final ListModel<Person> modelComboBox_UserDaily = new ListModelList<Person>(list_op_person);
		ShipSchedulerComposer.this.user_Daily.setModel(modelComboBox_UserDaily);

		final ListModel<Person> modelComboBox_UserSecondDaily = new ListModelList<Person>(list_op_person);
		ShipSchedulerComposer.this.usersecond_Daily.setModel(modelComboBox_UserSecondDaily);

		// set info about customer combo
		final List<Customer> list_customer = ShipSchedulerComposer.this.customerDAO.listAllCustomers();
		final ListModel<Customer> model_customer = new ListModelList<Customer>(list_customer);
		ShipSchedulerComposer.this.ship_customer.setModel(model_customer);

		final ListModel<Customer> model_customer_add = new ListModelList<Customer>(list_customer);
		ShipSchedulerComposer.this.ship_customer_add.setModel(model_customer_add);

		final List<Customer> list = new ArrayList<Customer>(list_customer);

		final Customer allCustomer = new Customer();
		allCustomer.setName("TUTTI");
		allCustomer.setId(0);
		list.add(allCustomer);
		ShipSchedulerComposer.this.select_customer.setModel(new ListModelList<Customer>(list));

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

		int crn_val = 1;
		if (this.crane_review.getValue() != null) {
			crn_val = this.crane_review.getValue();
		}
		detailFinal.setCrane(crn_val);

		detailFinal.setCrane_gtw(this.crane_gtw_review.isChecked());
		detailFinal.setVolume(this.volume_review.getValue());
		detailFinal.setVolumeunderboard(this.volumeunderboard_review.getValue());
		detailFinal.setVolumeunderboard_sws(this.volumeunderboard_sws_review.getValue());
		detailFinal.setVolume_tw_mct(this.volumeunde_tw_mct_review.getValue());
		detailFinal.setTimework(this.time_review.getValue());
		detailFinal.setNotedetail(this.note_review.getValue());
		detailFinal.setInvoicing_cycle(this.invoicing_cycle_review.getValue());

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

		}

		this.shipSchedulerDao.updateDetailFinalScheduleShip(detailFinal);

		// close editor
		final int id_itm = detailFinal.getIddetailscheduleship();
		final List<DetailFinalScheduleShip> final_details = this.shipSchedulerDao.loadDetailFinalScheduleShipByIdDetailScheduleShip(id_itm);
		this.list_reviewDetailScheduleShip.setModel(new ListModelList<DetailFinalScheduleShip>(final_details));
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

		this.crane_review.setValue(detailFinal.getCrane());
		this.crane_gtw_review.setChecked(detailFinal.getCrane_gtw());
		this.volume_review.setValue(detailFinal.getVolume());
		this.volumeunderboard_review.setValue(detailFinal.getVolumeunderboard());
		this.volumeunderboard_sws_review.setValue(detailFinal.getVolumeunderboard_sws());
		this.volumeunde_tw_mct_review.setValue(detailFinal.getVolume_tw_mct());
		this.time_review.setValue(detailFinal.getTimework());
		this.note_review.setValue(detailFinal.getNotedetail());
		this.invoicing_cycle_review.setValue(detailFinal.getInvoicing_cycle());

		this.add_finalDetailScheduleShip_command.setVisible(false);

		this.modify_finalDetailScheduleShip_command.setVisible(true);
		this.panel_editor_review_details.setVisible(true);

		// SET ACTIVIY

		this.reviewTimeFrom.setValue(null);
		this.reviewTimeTo.setValue(null);

		this.reviewedTime.setVisible(false);
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

		this.handswork.setValue(this.detailScheduleShipSelected.getHandswork());
		this.menwork.setValue(this.detailScheduleShipSelected.getMenwork());
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
			final Map<String, String> params = new HashMap<String, String>();
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

		this.searchScheduleShip();

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

	@Listen("onClick = #refresh_command")
	public void refreshBapView() {

		this.searchWorkShip.setValue(Calendar.getInstance().getTime());
		this.select_year.setSelectedItem(null);
		this.date_from_overview.setValue(null);
		this.date_to_overview.setValue(null);

		this.full_text_search_ship.setValue(null);
		this.full_text_search_rifSWS.setValue(null);
		this.full_text_search_rifMCT.setValue(null);
		this.shows_rows_ship.setValue(10);

		this.select_shiftBap.setSelectedIndex(0);

		this.refreshOverviewBAP();
	}

	/**
	 * Called on initial view definition
	 */
	private void refreshOverviewBAP() {

		if ((this.date_from_overview.getValue() == null) && (this.date_to_overview.getValue() == null)) {

			this.searchReviewShipData();

		} else {

			this.searchReviewShipIntervalDate();

		}
	}

	/**
	 * Set ship list box with initial events
	 */
	@Listen("onOK = #shows_rows, #full_text_search;onChange = #select_shift, #select_customer, #select_typeShip, #select_workedShip")
	public void refreshScheduleShipListBox() {

		if (this.searchDateShift.getValue() != null) {
			this.searchDetailScheduleShipByDateShift();
		} else {
			this.searchDetailScheduleShipByDatePeriod();
		}

	}

	@Listen("onClick = #refreshScheduleView")
	public void refreshScheduleShipView() {

		final Calendar current_cal = Calendar.getInstance();
		current_cal.set(Calendar.DAY_OF_MONTH, 1);
		this.searchArrivalDateShipFrom.setValue(current_cal.getTime());

		current_cal.set(Calendar.DAY_OF_MONTH, current_cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		this.searchArrivalDateShipTo.setValue(current_cal.getTime());

		this.search_rifSWS.setValue(null);
		this.search_rifMCT.setValue(null);

		this.searchScheduleShip();
	}

	private void refreshShipStatistics(final String text) {

		String text_select = null;
		if ((text != null) && !text.equals("")) {
			text_select = text;
		}

		// select date period
		Date date_from = this.date_from_overview.getValue();
		Date date_to = this.date_to_overview.getValue();

		if ((date_from != null) && (date_to != null) && !date_from.after(date_to) && !DateUtils.isSameDay(date_from, date_to)) {

			// truncate
			date_from = DateUtils.truncate(date_from, Calendar.DATE);
			date_to = DateUtils.truncate(date_to, Calendar.DATE);
		}

		this.listShipStatistics = this.statisticDAO.overviewFinalScheduleByShip(text_select, date_from, date_to);

		this.list_ship_statistics.setModel(new ListModelList<ShipOverview>(this.listShipStatistics));

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.list_ship_statistics.setPageSize(this.shows_rows.getValue());
		}

	}

	@Listen("onClick = #sw_link_deleteship")
	public void removeItem() {

		this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		// take schedule ship
		this.scheduleShip_selected = this.shipSchedulerDao.loadScheduleShip(this.detailScheduleShipSelected.getIdscheduleship());

		final Map<String, String> params = new HashMap<String, String>();
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

			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");

			final Messagebox.Button[] buttons = new Messagebox.Button[2];
			buttons[0] = Messagebox.Button.OK;
			buttons[1] = Messagebox.Button.CANCEL;

			Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.EXCLAMATION, null,
					new EventListener<ClickEvent>() {
				@Override
				public void onEvent(final ClickEvent e) {
					if (Messagebox.ON_OK.equals(e.getName())) {
						ShipSchedulerComposer.this.shipSchedulerDao.deleteScheduleShip(ShipSchedulerComposer.this.scheduleShip_selected
								.getId());

						ShipSchedulerComposer.this.searchScheduleShip();
					} else if (Messagebox.ON_CANCEL.equals(e.getName())) {

					}
				}
			}, params);

		}
	}

	@Listen("onClick = #refreshDetailView")
	public void resfreshDetailView() {
		this.setInitialView();

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
		this.refreshScheduleShipListBox();
		this.alertShiftDate_detail.setVisible(false);
		this.popup_detail_Daily.close();

	}

	@Listen("onClick = #save_review")
	public void saveReview() {
		this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (this.detailScheduleShipSelected == null) {
			return;
		}

		if (this.workedGroup.getSelectedIndex() == 0) {
			this.detailScheduleShipSelected.setWorked(true);
		} else {
			this.detailScheduleShipSelected.setWorked(false);
		}

		this.detailScheduleShipSelected.setHandswork(this.handswork_Daily.getValue());
		this.detailScheduleShipSelected.setMenwork(this.menwork_Daily.getValue());

		final Integer id_ship = this.detailScheduleShipSelected.getId_ship();
		final Ship ship = this.shipDao.loadShip(id_ship);

		this.detailScheduleShipSelected.setFirst_down(this.first_down_review.getValue());
		this.detailScheduleShipSelected.setLast_down(this.last_down_review.getValue());

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
		this.refreshScheduleShipListBox();

		this.popup_review_detail.close();

	}

	@Listen("onChange = #searchArrivalDateShipFrom, #searchArrivalDateShipTo")
	public void searchByDate() {
		this.search_rifMCT.setValue(null);
		this.search_rifSWS.setValue(null);

		this.searchScheduleShip();
	}

	@Listen("onChange = #searchArrivalDateShipFrom_detail, #searchArrivalDateShipTo_detail; onOK = #searchArrivalDateShipFrom_detail, #searchArrivalDateShipTo_detail")
	public void searchDetailScheduleShipByDatePeriod() {

		// set null date shift box
		this.searchDateShift.setValue(null);

		final Date dateFrom = this.searchArrivalDateShipFrom_detail.getValue();
		final Date dateTo = this.searchArrivalDateShipTo_detail.getValue();

		if ((dateFrom == null) || (dateTo == null)) {
			return;
		}

		String text_search = this.full_text_search.getValue();

		if ((text_search != null) && text_search.equals("")) {
			text_search = null;
		}

		final Integer no_shift = this.getSelectedShift();

		Integer idCustomer = null;
		if ((this.select_customer.getSelectedItem() != null) && (this.select_customer.getSelectedItem().getLabel() != "TUTTI")) {
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
			}
		}

		// set worked filter
		Boolean worked = false;
		if (this.select_workedShip.getSelectedItem().getValue().equals("yes")) {
			worked = true;
		}

		this.modelListDetailScheduleShip = this.shipSchedulerDao.searchDetailScheduleShip(dateFrom, dateTo, text_search, no_shift, idCustomer,
				nowork, activityh, worked);

		this.sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(this.modelListDetailScheduleShip));

		this.setInfoShipProgram(dateFrom, dateTo, this.infoDetailShipProgram);
	}

	@Listen("onChange = #searchDateShift; onOK = #searchDateShift")
	public void searchDetailScheduleShipByDateShift() {

		this.searchArrivalDateShipFrom_detail.setValue(null);
		this.searchArrivalDateShipTo_detail.setValue(null);

		if (this.searchDateShift.getValue() == null) {
			return;
		}

		final Date date = this.searchDateShift.getValue();

		this.modelListDetailScheduleShip = null;

		final String _text = this.full_text_search.getValue();
		String text_search = this.full_text_search.getValue();

		if ((text_search != null) && text_search.equals("")) {
			text_search = null;
		}

		final Integer selectedShift = this.getSelectedShift();

		Integer idCustomer = null;
		if ((this.select_customer.getSelectedItem() != null) && (this.select_customer.getSelectedItem().getLabel() != "TUTTI")) {
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
			}
		}

		// set worked filter
		Boolean worked = false;
		if (this.select_workedShip.getSelectedItem() == null) {
			worked = null;
		} else if (this.select_workedShip.getSelectedItem().getValue().equals("yes")) {
			worked = true;
		} else if (this.select_workedShip.getSelectedItem().getValue().equals("no")) {
			worked = false;
		} else if (this.select_workedShip.getSelectedItem().getValue().equals("all")) {
			worked = null;
		}

		this.modelListDetailScheduleShip = this.shipSchedulerDao.searchDetailScheduleShip(date, text_search, selectedShift, idCustomer, nowork,
				activityh, worked);

		this.setInfoDetailShipProgram(date, _text);

		this.sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(this.modelListDetailScheduleShip));

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_scheduleShip.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_scheduleShip.setPageSize(10);
		}

	}

	@Listen("onOK = #full_text_search_rifMCT")
	public void searchMCT() {
		this.searchWorkShip.setValue(null);
		this.date_from_overview.setValue(null);
		this.date_to_overview.setValue(null);
		this.select_year.setSelectedItem(null);
		this.full_text_search_ship.setValue(null);
		this.full_text_search_rifSWS.setValue(null);

		this.refreshOverviewBAP();

	}

	/**
	 * Search info about data ship for review
	 */
	@Listen("onChange = #searchWorkShip; onOK = #searchWorkShip, #date_from_overview, #date_to_overview")
	public void searchReviewShipData() {

		this.select_year.setSelectedItem(null);
		this.date_from_overview.setValue(null);
		this.date_to_overview.setValue(null);

		final Date date_from = this.searchWorkShip.getValue();

		final String searchText = this.full_text_search_ship.getValue();

		if (this.overviewBap.isSelected()) {

			// not aggregate

			Integer shiftNumber = null;

			if (this.select_shiftBap.getSelectedItem() != null) {
				shiftNumber = this.select_shiftBap.getSelectedIndex();
				if (shiftNumber == 0) {
					shiftNumber = null;
				}
			}

			final Integer rif_sws = this.full_text_search_rifSWS.getValue();
			final String rif_mct = this.full_text_search_rifMCT.getValue();
			final Integer invoicing = this.invoicing_cycle_search.getValue();
			final Integer working = this.working_cycle_search.getValue();

			this.list_review_work = this.statistic_dao.loadReviewShipWork(date_from, null, searchText, rif_sws, rif_mct, shiftNumber, invoicing,
					working);

			if ((this.shows_rows_ship.getValue() != null) && (this.shows_rows_ship.getValue() != 0)) {
				this.sw_list_reviewWork.setPageSize(this.shows_rows_ship.getValue());
			} else {
				this.sw_list_reviewWork.setPageSize(10);
			}

			this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

			this.calculateTotaleVolumeBAP();

		} else if (this.overviewBapAggregate.isSelected()) {

			// aggregate

			this.list_review_work_aggregate = this.statistic_dao.loadReviewShipWorkAggregate(date_from, null, searchText);

			if ((this.shows_rows_ship.getValue() != null) && (this.shows_rows_ship.getValue() != 0)) {
				this.sw_list_reviewWorkAggregate.setPageSize(this.shows_rows_ship.getValue());
			} else {
				this.sw_list_reviewWorkAggregate.setPageSize(10);
			}

			this.sw_list_reviewWorkAggregate.setModel(new ListModelList<ReviewShipWorkAggregate>(this.list_review_work_aggregate));

		} else if (this.statisticsShipTab.isSelected()) {

			this.refreshShipStatistics(this.full_text_search_ship.getValue());

		}

	}

	/**
	 * Search info about data ship for review in interval date
	 */
	@Listen("onChange =  #date_from_overview, #date_to_overview; onOK= #date_from_overview, #date_to_overview")
	public void searchReviewShipIntervalDate() {

		final Date date_from = this.date_from_overview.getValue();
		final Date date_to = this.date_to_overview.getValue();

		if ((date_from == null) || (date_to == null)) {
			return;
		}

		this.searchWorkShip.setValue(null);
		this.select_year.setSelectedItem(null);

		final String text_search = this.full_text_search_ship.getValue();

		if (this.overviewBap.isSelected()) {

			Integer shiftNumber = null;

			if (this.select_shiftBap.getSelectedItem() != null) {
				shiftNumber = this.select_shiftBap.getSelectedIndex();
				if (shiftNumber == 0) {
					shiftNumber = null;
				}
			}

			final Integer rif_sws = this.full_text_search_rifSWS.getValue();
			final String rif_mct = this.full_text_search_rifMCT.getValue();
			final Integer invoicing = this.invoicing_cycle_search.getValue();
			final Integer working = this.working_cycle_search.getValue();

			this.list_review_work = this.statistic_dao.loadReviewShipWork(date_from, date_to, text_search, rif_sws, rif_mct, shiftNumber, invoicing,
					working);

			if ((this.shows_rows_ship.getValue() != null) && (this.shows_rows_ship.getValue() != 0)) {
				this.sw_list_reviewWork.setPageSize(this.shows_rows_ship.getValue());
			} else {
				this.sw_list_reviewWork.setPageSize(10);
			}

			this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

			this.calculateTotaleVolumeBAP();

		} else if (this.overviewBapAggregate.isSelected()) {

			// aggregate

			this.list_review_work_aggregate = this.statistic_dao.loadReviewShipWorkAggregate(date_from, date_to, text_search);

			if ((this.shows_rows_ship.getValue() != null) && (this.shows_rows_ship.getValue() != 0)) {
				this.sw_list_reviewWorkAggregate.setPageSize(this.shows_rows_ship.getValue());
			} else {
				this.sw_list_reviewWorkAggregate.setPageSize(10);
			}

			this.sw_list_reviewWorkAggregate.setModel(new ListModelList<ReviewShipWorkAggregate>(this.list_review_work_aggregate));
		} else if (this.statisticsShipTab.isSelected()) {

			this.refreshShipStatistics(this.full_text_search_ship.getValue());
		}

	}

	@Listen("onOK = #search_rifMCT")
	public void searchRifMCT() {
		this.searchArrivalDateShipFrom.setValue(null);
		this.searchArrivalDateShipTo.setValue(null);
		this.search_rifSWS.setValue(null);

		this.searchScheduleShip();
	}

	@Listen("onOK = #search_rifSWS")
	public void searchRifSWS() {
		this.searchArrivalDateShipFrom.setValue(null);
		this.searchArrivalDateShipTo.setValue(null);
		this.search_rifMCT.setValue(null);

		this.searchScheduleShip();
	}

	/**
	 * Search on Ship
	 */
	private void searchScheduleShip() {

		final Date dateFrom = this.searchArrivalDateShipFrom.getValue();
		final Date dateTo = this.searchArrivalDateShipTo.getValue();

		if (((dateFrom != null) && (dateTo != null)) || ((dateFrom == null) && (dateTo == null))) {
			this.modelListScheduleShip = this.shipSchedulerDao.searchScheduleShip(this.searchArrivalDateShipFrom.getValue(),
					this.searchArrivalDateShipTo.getValue(), this.search_rifSWS.getValue(), this.search_rifMCT.getValue());

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.sw_list_scheduleShipProgram.setPageSize(this.shows_rows.getValue());
			} else {
				this.sw_list_scheduleShipProgram.setPageSize(10);
			}

			this.sw_list_scheduleShipProgram.setModel(new ListModelList<ScheduleShip>(this.modelListScheduleShip));

		}

		this.calculateTotaleVolumeShipProgram();

	}

	@Listen("onOK = #full_text_search_rifSWS")
	public void searchSWS() {
		this.searchWorkShip.setValue(null);
		this.date_from_overview.setValue(null);
		this.date_to_overview.setValue(null);
		this.select_year.setSelectedItem(null);
		this.full_text_search_ship.setValue(null);
		this.full_text_search_rifMCT.setValue(null);

		this.refreshOverviewBAP();

	}

	@Listen("onOK = #full_text_search_ship, #invoicing_cycle_search, #working_cycle_search")
	public void searchText() {
		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_reviewWork.setPageSize(this.shows_rows_ship.getValue());
		} else {
			this.sw_list_reviewWork.setPageSize(10);
		}

		Integer shiftNumber = null;

		if (this.select_shiftBap.getSelectedItem() != null) {
			shiftNumber = this.select_shiftBap.getSelectedIndex();
			if (shiftNumber == 0) {
				shiftNumber = null;
			}
		}

		if (this.searchWorkShip.getValue() == null) {
			this.list_review_work = this.statistic_dao.loadReviewShipWork(this.date_from_overview.getValue(), this.date_to_overview.getValue(),
					this.full_text_search_ship.getValue(), this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(),
					shiftNumber, this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
		} else {
			this.list_review_work = this.statistic_dao.loadReviewShipWork(this.searchWorkShip.getValue(), null,
					this.full_text_search_ship.getValue(), this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(),
					shiftNumber, this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
		}

		this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

		this.calculateTotaleVolumeBAP();
	}

	@Listen("onChange = #select_shiftBap")
	public void selectBapByShift() {
		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_reviewWork.setPageSize(this.shows_rows_ship.getValue());
		} else {
			this.sw_list_reviewWork.setPageSize(10);
		}

		Integer shiftNumber = null;

		if (this.select_shiftBap.getSelectedItem() != null) {
			shiftNumber = this.select_shiftBap.getSelectedIndex();
			if (shiftNumber == 0) {
				shiftNumber = null;
			}
		}

		if (this.searchWorkShip.getValue() == null) {
			this.list_review_work = this.statistic_dao.loadReviewShipWork(this.date_from_overview.getValue(), this.date_to_overview.getValue(),
					this.full_text_search_ship.getValue(), this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(),
					shiftNumber, this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
		} else {
			this.list_review_work = this.statistic_dao.loadReviewShipWork(this.searchWorkShip.getValue(), null,
					this.full_text_search_ship.getValue(), this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(),
					shiftNumber, this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
		}

		this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

		this.calculateTotaleVolumeBAP();
	}

	@Listen("onChange =#select_year")
	public void selectedYear() {

		if ((this.select_year.getSelectedItem() == null) || (this.select_year.getSelectedItem().getValue() == null)) {
			return;
		}

		this.searchWorkShip.setValue(null);

		final String yearSelected = this.select_year.getSelectedItem().getValue();

		if (yearSelected.equals("TUTTI")) {

			this.date_from_overview.setValue(null);
			this.date_to_overview.setValue(null);

		} else {

			final Integer year = Integer.parseInt(yearSelected);
			final Calendar calendar_from = Calendar.getInstance();
			final Calendar calendar_to = Calendar.getInstance();

			calendar_from.set(Calendar.YEAR, year);
			calendar_from.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMinimum(Calendar.DAY_OF_YEAR));
			calendar_from.set(Calendar.MONTH, calendar_from.getActualMinimum(Calendar.MONTH));

			calendar_to.set(Calendar.YEAR, year);
			calendar_to.set(Calendar.DAY_OF_YEAR, calendar_from.getActualMaximum(Calendar.DAY_OF_YEAR));
			calendar_to.set(Calendar.MONTH, calendar_from.getActualMaximum(Calendar.MONTH));

			this.date_from_overview.setValue(calendar_from.getTime());
			this.date_to_overview.setValue(calendar_to.getTime());

		}

		this.refreshOverviewBAP();

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
	 * Define info
	 *
	 * @param date
	 * @param full_text_search
	 */
	private void setInfoDetailShipProgram(final Date date, final String full_text_search) {

		this.infoDetailShipProgram.setValue("");

		Integer volume = this.shipSchedulerDao.calculateVolumeByArrivalDateAndShipName(date, full_text_search, this.getSelectedShift());

		if (volume == null) {
			volume = 0;
		}

		Integer numberOfShip = this.shipSchedulerDao.calculateNumberOfShipByArrivalDateAndShipName(date, full_text_search, this.getSelectedShift());

		if (numberOfShip == null) {
			numberOfShip = 0;
		}

		Integer totalMenWork = 0;
		Integer totalHandsWork = 0;

		final ShipTotal shipTotal = this.shipSchedulerDao.calculateHandsWorkAndMensByArrivalDateAndShipName(date, full_text_search,
				this.getSelectedShift());

		if (shipTotal != null) {
			if (shipTotal.getTotalhands() != null) {
				totalHandsWork = shipTotal.getTotalhands();
			}

			if (shipTotal.getTotalmen() != null) {
				totalMenWork = shipTotal.getTotalmen();
			}
		}

		this.infoDetailShipProgram.setValue("Numero di Navi: " + numberOfShip + ". Totale volumi preventivati: " + volume + ". Totale Mani: "
				+ totalHandsWork + ". Totale Persone: " + totalMenWork);

	}

	/**
	 * Calculate info label
	 *
	 * @param date_from
	 * @param date_to
	 * @param infoLabel
	 */
	private void setInfoShipProgram(final Date date_from, final Date date_to, final Label infoLabel) {

		infoLabel.setValue("");

		if ((date_from == null) || (date_to == null)) {
			return;
		}

		final Timestamp dateFrom = new Timestamp(date_from.getTime());
		final Timestamp dateTo = new Timestamp(date_to.getTime());

		Integer volume = this.shipSchedulerDao.calculateVolumeInDate(dateFrom, dateTo, this.getSelectedShift());

		if (volume == null) {
			volume = 0;
		}

		Integer numberOfShip = this.shipSchedulerDao.calculateNumberOfShipInDate(dateFrom, dateTo, this.getSelectedShift());

		if (numberOfShip == null) {
			numberOfShip = 0;
		}

		final ShipTotal handMenWorkTotal = this.shipSchedulerDao.calculateHandsWorkInDate(dateFrom, dateTo, this.getSelectedShift());

		Integer totalHands = 0;
		Integer totalMen = 0;

		if (handMenWorkTotal != null) {
			if (handMenWorkTotal.getTotalmen() != null) {
				totalMen = handMenWorkTotal.getTotalmen();
			} else {
				totalMen = 0;
			}
			if (handMenWorkTotal.getTotalhands() != null) {
				totalHands = handMenWorkTotal.getTotalhands();
			} else {
				totalHands = 0;
			}
		}

		infoLabel.setValue("Numero di Navi: " + numberOfShip + ". Totale volumi preventivati: " + volume + ". Totale Mani: " + totalHands
				+ ". Totale Persone: " + totalMen);

	}

	/**
	 * Show ships
	 */
	public void setInitialView() {

		this.grid_scheduleShip.setVisible(false);
		this.shipProgram.setVisible(false);
		this.dailyDetailShip.setVisible(true);
		this.reviewWorkShip.setVisible(false);

		this.full_text_search.setValue(null);

		this.select_shift.setSelectedIndex(0);

		// set period in info date
		final Calendar current_cal = Calendar.getInstance();
		current_cal.set(Calendar.DAY_OF_MONTH, 1);
		this.searchArrivalDateShipFrom.setValue(current_cal.getTime());

		current_cal.set(Calendar.DAY_OF_MONTH, current_cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		this.searchArrivalDateShipTo.setValue(current_cal.getTime());

		// set initial query for review work
		final Calendar cal = Calendar.getInstance();
		this.searchWorkShip.setValue(cal.getTime());

		this.searchDateShift.setValue(DateUtils.truncate(cal.getTime(), Calendar.DATE));

		this.select_customer.setSelectedItem(null);

		this.select_typeShip.setSelectedItem(null);

		this.select_workedShip.setSelectedItem(null);

		// set ship listbox
		this.refreshScheduleShipListBox();

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

		this.grid_scheduleShip.setVisible(true);
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

		final List<Ship> detailShip = new ArrayList<Ship>();

		detailShip.add(this.shipDao.loadShip(idShip));

		this.popup_shipDetail.setModel(new ListModelList<Ship>(detailShip));

	}

	@Listen("onClick = #sw_link_addDetailScheduleShipProgram")
	public void showDetailShipScheduleOnProgram() {

		this.modify_scheduleShipsDetail_command.setVisible(false);
		this.add_scheduleShipsDetail_command.setVisible(true);
		this.alertShiftDate.setVisible(false);

		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {

			this.listDetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

			this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>(this.listDetailScheduleShip));
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

		this.shift.setSelectedIndex(0);
		this.operation.setValue("-");
		this.user.setSelectedItem(null);
		this.usersecond.setSelectedItem(null);
		this.handswork.setValue(0);
		this.menwork.setValue(0);

		this.shift_Daily.setSelectedIndex(0);
		this.operation_Daily.setSelectedItem(null);
		this.user_Daily.setSelectedItem(null);
		this.usersecond_Daily.setSelectedItem(null);
		this.handswork_Daily.setValue(0);
		this.menwork_Daily.setValue(0);
		this.shiftdate_Daily.setValue(new Date());
		this.notedetail.setValue("");
		this.noteshipdetail.setValue("");

		// define if ship activity fields need to be disabled
		final Integer id_ship = this.scheduleShip_selected.getIdship();
		final Ship ship = this.shipDao.loadShip(id_ship);

		if ((ship != null) && (ship.getActivityh() != null) && ship.getActivityh().booleanValue()) {

			final String info_shift = this.shift.getSelectedItem().getValue();
			final Integer shift_no = Integer.parseInt(info_shift);
			this.changeBehaviorForShify(shift_no, this.shiftdate_Daily.getValue(), this.ship_from, this.ship_to, this.check_last_shift);

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

		ScheduleShip scheduleShip = this.shipSchedulerDao.loadScheduleShip(detailSelected.getIdscheduleship());

		// select if worked or not
		if (detailSelected.getWorked()) {
			this.workedGroup.setSelectedIndex(0);
		} else {
			this.workedGroup.setSelectedIndex(1);
		}

		this.handswork_Daily.setValue(detailSelected.getHandswork());
		this.menwork_Daily.setValue(detailSelected.getMenwork());

		// define general value
		this.first_down_review.setValue(detailSelected.getFirst_down());
		this.last_down_review.setValue(detailSelected.getLast_down());

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

		this.reviewTimeFrom.setValue(null);
		this.reviewTimeTo.setValue(null);
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
		}

		// set ship name and alert
		this.messageUpdateRifMCT.setVisible(false);
		this.infoShipNameAndShift.setValue(detailSelected.getName() + shipActivity + " - Turno " + detailSelected.getShift());

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
		this.list_reviewDetailScheduleShip.setModel(new ListModelList<DetailFinalScheduleShip>(final_details));

		// set button
		this.panel_editor_review_details.setVisible(false);

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

		this.detailScheduleShipSelected.setHandswork(this.handswork.getValue());
		this.detailScheduleShipSelected.setMenwork(this.menwork.getValue());
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
			final DetailScheduleShip detailSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();
			if ((detailSelected != null) && (detailSelected.getIdscheduleship() != null)) {

				final ScheduleShip scheduleShip = this.shipSchedulerDao.loadScheduleShip(detailSelected.getIdscheduleship());

				if (scheduleShip != null) {
					this.shipSchedulerDao.updateRifMCT(scheduleShip.getId(), this.rif_mct_review.getValue());

					this.messageUpdateRifMCT.setVisible(true);
				}
			}
		}

	}
}
