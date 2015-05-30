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

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.model.Customer;
import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.ICustomerDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.statistics.ShipTotal;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.utility.UtilityCSV;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;
import org.zkoss.zul.Caption;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Toolbarbutton;

public class ShipSchedulerComposer extends SelectorComposer<Component> {

	private final class StartingEvent implements EventListener<Event> {
		@Override
		public void onEvent(final Event arg0) throws Exception {

			if (ShipSchedulerComposer.this.person_logged.isViewerOnly()) {
				// set initial item if user is a viewer only user
				ShipSchedulerComposer.this.scheduler_type_selector.setSelectedItem(ShipSchedulerComposer.this.detail_item);
			}

			// get the DAOs
			ShipSchedulerComposer.this.shipSchedulerDao = (IScheduleShip) SpringUtil.getBean(BeansTag.SCHEDULE_SHIP_DAO);
			ShipSchedulerComposer.this.shipDao = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);
			ShipSchedulerComposer.this.personDao = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);
			ShipSchedulerComposer.this.scheduleDao = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);
			ShipSchedulerComposer.this.customerDAO = (ICustomerDAO) SpringUtil.getBean(BeansTag.CUSTOMER_DAO);

			final List<Ship> all_ship = ShipSchedulerComposer.this.shipDao.listAllShip(null);
			final ListModel<Ship> modelComboBox_ShipName = new ListModelList<Ship>(all_ship);

			// add item in combobox ship name
			if (modelComboBox_ShipName.getSize() == 0) {
				Messagebox.show("Inserire almeno una nave prima di procedere alla programmazione!", "INFO", Messagebox.OK, Messagebox.INFORMATION);
			}

			ShipSchedulerComposer.this.ship_name.setModel(modelComboBox_ShipName);
			ShipSchedulerComposer.this.ship_name_schedule.setModel(modelComboBox_ShipName);
			ShipSchedulerComposer.this.ship_activity.setModel((modelComboBox_ShipName));

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

			final ListModel<Customer> model_customer_daily = new ListModelList<Customer>(list_customer);
			ShipSchedulerComposer.this.ship_customer_daily.setModel(model_customer_daily);

			ShipSchedulerComposer.this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

			ShipSchedulerComposer.this.scheduler_type_selector.setSelectedItem(ShipSchedulerComposer.this.detail_item);

			ShipSchedulerComposer.this.setInitialView();

		}
	}

	/**
	 *
	 */
	private static final long			serialVersionUID		= 1L;

	@Wire
	private Toolbarbutton				add_finalDetailScheduleShip_command;

	@Wire
	private Component					add_scheduleShips_command;

	@Wire
	Toolbarbutton						add_scheduleShipsDetail_command;

	@Wire
	public Row							alertShiftDate;

	@Wire
	public Row							alertShiftDate_detail;

	@Wire
	private Caption						captionDetailProgramShip;

	private String						captionDetailProgramShipLabel;

	@Wire
	private Caption						captionPopupScheduleShip;

	private String						captionPopupScheduleShipLabel;

	@Wire
	private Caption						captionShipProgram;

	private String						captionShipProgramLabel;

	@Wire
	private org.zkoss.zul.Checkbox		crane_gtw_review;

	@Wire
	private Intbox						crane_review;

	private ICustomerDAO				customerDAO;

	@Wire
	public Component					dailyDetailShip;

	@Wire
	private Datebox						date_from_overview;

	@Wire
	private Datebox						date_to_overview;

	@Wire
	private Comboitem					detail_item;

	@Wire
	private Tab							detail_scheduleShip_tab;

	private DetailScheduleShip			detailScheduleShipSelected;

	private final SimpleDateFormat		format_it_date			= new SimpleDateFormat("dd/MM/yyyy");

	private final SimpleDateFormat		format_month			= new SimpleDateFormat("MM");

	@Wire
	private Textbox						full_text_search;

	@Wire
	private Intbox						full_text_search_rifMCT;

	@Wire
	private Intbox						full_text_search_rifSWS;

	@Wire
	private Textbox						full_text_search_ship;

	@Wire
	private Component					grid_scheduleShip;

	@Wire
	private Component					grid_scheduleShip_details;

	@Wire
	private Row							h_program_period;

	@Wire
	private Intbox						handswork;

	@Wire
	public Intbox						handswork_Daily;

	@Wire
	private Label						infoDetailShipProgram;

	@Wire
	private Label						infoShipNameAndShift;

	@Wire
	private Label						infoShipProgram;

	@Wire
	private Intbox						invoicing_cycle_review;

	@Wire
	private Intbox						invoicing_cycle_search;

	private List<ReviewShipWork>		list_review_work		= new ArrayList<ReviewShipWork>();

	@Wire
	private Listbox						list_reviewDetailScheduleShip;

	private List<DetailScheduleShip>	listDetailScheduleShip	= new ArrayList<DetailScheduleShip>();

	private final Logger				logger					= Logger.getLogger(UserDetailsComposer.class);

	@Wire
	private Intbox						menwork;

	@Wire
	public Intbox						menwork_Daily;

	@Wire
	private Label						messageUpdateRifMCT;

	@Wire
	private Toolbarbutton				modify_finalDetailScheduleShip_command;

	@Wire
	private Component					modify_Scheduleships_command;

	@Wire
	private Toolbarbutton				modify_scheduleShipsDetail_command;

	@Wire
	public Label						msgAlert;

	@Wire
	public Label						msgAlert_detail;

	@Wire
	private Textbox						note;

	@Wire
	private Textbox						note_review;

	@Wire
	public Textbox						note_schedule;

	@Wire
	public Textbox						notedetail;

	@Wire
	private Textbox						noteshipdetail;

	@Wire
	private Combobox					operation;

	@Wire
	public Combobox						operation_Daily;

	@Wire
	private Panel						panel_detail_program;

	private Person						person_logged;

	private PersonDAO					personDao;

	@Wire
	private Popup						popu_detail;

	@Wire
	public Popup						popup_detail;

	@Wire
	public Popup						popup_detail_Daily;

	@Wire
	private Popup						popup_scheduleShip;

	@Wire
	private Popup						popup_ship;

	@Wire
	private Listbox						popup_shipDetail;

	@Wire
	private Comboitem					program_item;

	@Wire
	private Component					reviewWorkShip;

	@Wire
	private Intbox						rif_mct_review;

	@Wire
	private Label						rif_sws_review;

	@Wire
	private Row							row_info_activity_ship_program;

	public ISchedule					scheduleDao;

	@Wire
	private Combobox					scheduler_type_selector;

	private ScheduleShip				scheduleShip_selected	= null;

	@Wire
	private Datebox						searchArrivalDateShipFrom;

	@Wire
	private Datebox						searchArrivalDateShipFrom_detail;

	@Wire
	private Datebox						searchArrivalDateShipTo;

	@Wire
	private Datebox						searchArrivalDateShipTo_detail;

	@Wire
	private Datebox						searchDateShift;

	@Wire
	private Datebox						searchWorkShip;

	@Wire
	private A							selecetedShipName;

	@Wire
	public Combobox						select_shift;

	@Wire
	public Combobox						select_shiftBap;

	@Wire
	private Combobox					select_year;

	@Wire
	private Combobox					shift;

	@Wire
	public Combobox						shift_Daily;

	@Wire
	private Datebox						shiftdate;

	@Wire
	public Datebox						shiftdate_Daily;

	@Wire
	private Combobox					ship_activity;

	@Wire
	private Datebox						ship_arrival;

	@Wire
	public Datebox						ship_arrival_schedule;

	@Wire
	private Combobox					ship_customer;

	@Wire
	private Combobox					ship_customer_daily;

	@Wire
	private Datebox						ship_departure;

	@Wire
	public Datebox						ship_departure_schedule;

	@Wire
	private Timebox						ship_from;

	@Wire
	private Combobox					ship_name;

	@Wire
	public Combobox						ship_name_schedule;

	@Wire
	private Intbox						ship_rif_mcf;

	@Wire
	private Label						ship_rif_sws;

	@Wire
	private Timebox						ship_to;

	@Wire
	private Intbox						ship_volume;

	@Wire
	public Intbox						ship_volume_schedule;

	protected IShip						shipDao;

	@Wire
	public Component					shipProgram;

	@Wire
	private Intbox						shipRif_mcf;

	private IScheduleShip				shipSchedulerDao;

	@Wire
	private Intbox						shows_rows;

	@Wire
	private Intbox						shows_rows_ship;

	@Wire
	private Toolbarbutton				sw_link_reviewscheduleship;

	// @Wire
	// private Listbox sw_list_scheduleDetailShipProgram;

	@Wire
	private Listbox						sw_list_reviewWork;

	@Wire
	private Listbox						sw_list_scheduleDetailShip;

	@Wire
	private Listbox						sw_list_scheduleShip;

	@Wire
	private Listbox						sw_list_scheduleShipProgram;

	@Wire
	private Doublebox					time_review;

	@Wire
	private Label						TotalVolume;

	@Wire
	private Label						TotalVolumeOnBoard;

	@Wire
	private Label						TotalVolumeOnBoard_sws;

	@Wire
	private Label						TotalVolumeTWMTC;

	@Wire
	private Combobox					user;

	@Wire
	public Combobox						user_Daily;

	protected PersonDAO					userPrep;

	@Wire
	private Combobox					usersecond;

	@Wire
	private Combobox					usersecond_Daily;

	@Wire
	private Comboitem					verify_review_ship_item;

	@Wire
	private Intbox						volume_review;

	@Wire
	private Intbox						volumeunde_tw_mct_review;

	@Wire
	private Intbox						volumeunderboard_review;

	@Wire
	private Intbox						volumeunderboard_sws_review;

	@Wire
	private Label						working_cycle_review;

	@Wire
	private Intbox						working_cycle_search;

	@Listen("onClick = #add_finalDetailScheduleShip_command")
	public void addDetailFinalScheduleShip() throws ParseException {

		if ((this.sw_list_scheduleShip == null) || (this.sw_list_scheduleShip.getSelectedItem() == null)) {
			return;
		}

		final DetailFinalScheduleShip detailFinalScheduleShip = new DetailFinalScheduleShip();

		final DetailScheduleShip itm = this.sw_list_scheduleShip.getSelectedItem().getValue();
		detailFinalScheduleShip.setIddetailscheduleship(itm.getId());

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

	@Listen("onClick = #sw_link_addDetailScheduleShipProgram")
	public void addDetailScheduleShipProgram() {

		this.modify_scheduleShipsDetail_command.setVisible(false);
		this.add_scheduleShipsDetail_command.setVisible(true);

		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {

			this.listDetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

			this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>(this.listDetailScheduleShip));
		}

		this.captionDetailProgramShip.setLabel(this.captionDetailProgramShipLabel + " - " + this.scheduleShip_selected.getName());

		// set panel editor close
		this.panel_detail_program.setVisible(false);

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
		} else {

			final ScheduleShip shipSchedule = new ScheduleShip();

			final Ship ship = this.ship_name_schedule.getSelectedItem().getValue();

			final int idShip = ship.getId();

			shipSchedule.setIdship(idShip);
			shipSchedule.setVolume(this.ship_volume_schedule.getValue());

			if (this.ship_rif_mcf.getValue() != null) {
				shipSchedule.setRif_mct(this.ship_rif_mcf.getValue());
			}

			shipSchedule.setNote(this.note_schedule.getValue());

			shipSchedule.setArrivaldate(this.ship_arrival_schedule.getValue());

			shipSchedule.setDeparturedate(this.ship_departure_schedule.getValue());

			this.shipSchedulerDao.createScheduleShip(shipSchedule);

			this.searchScheduleShipByDate();

			this.resetDataInfoTabProgram();

		}
	}

	@Listen("onClick = #add_scheduleShipsDetail_command")
	public void addScheduleShipsDetailCommand() {

		if (this.shift.getSelectedItem() == null) {

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

		// customer
		if (this.ship_customer.getSelectedItem() != null) {

			final Customer customer = this.ship_customer.getSelectedItem().getValue();
			if (customer != null) {
				item.setCustomer_id(customer.getId());
			}

		}

		this.alertShiftDate.setVisible(false);

		this.shipSchedulerDao.createDetailScheduleShip(item);

		this.addDetailScheduleShipProgram();

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
		this.resetDataInfoTabProgram();

		this.grid_scheduleShip.setVisible(false);
	}

	@Listen("onClick = #closeNoSave")
	public void closeNoSave() {
		this.resetDataInfoTabProgram();
	}

	/**
	 * Define modify in program ship
	 */
	private void defineModifyDetailProgramShip() {
		if (this.detailScheduleShipSelected == null) {
			return;
		}

		if (this.scheduleShip_selected == null) {
			return;
		}

		// define if ship activity fields need to be disabled
		final Integer id_ship = this.scheduleShip_selected.getIdship();
		final Ship ship = this.shipDao.loadShip(id_ship);
		if ((ship != null) && (ship.getActivityh() != null) && ship.getActivityh().booleanValue()) {

			// TODO manage this
			this.ship_activity.setSelectedItem(null);
			this.ship_from.setValue(null);
			this.ship_to.setValue(null);

			this.row_info_activity_ship_program.setVisible(true);
			this.h_program_period.setVisible(true);

		} else {

			this.ship_activity.setSelectedItem(null);
			this.ship_from.setValue(null);
			this.ship_to.setValue(null);
			this.row_info_activity_ship_program.setVisible(false);
			this.h_program_period.setVisible(false);

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

		// set customer
		this.ship_customer.setSelectedItem(null);

		if (this.detailScheduleShipSelected.getCustomer_id() != null) {

			final Customer customer = this.customerDAO.loadCustomer(this.detailScheduleShipSelected.getCustomer_id());

			final List<Comboitem> listItem = this.ship_customer.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Customer) {
					final Customer current = item.getValue();
					if (customer.equals(current)) {
						this.ship_customer.setSelectedItem(item);
						break;
					}
				}

			}

		}

		// select shift
		final Integer shift = this.detailScheduleShipSelected.getShift();
		if (shift != null) {
			this.shift.setValue(shift.toString());
		} else {
			this.shift.setValue(null);
		}

		this.operation.setValue(this.detailScheduleShipSelected.getOperation());

		this.handswork.setValue(this.detailScheduleShipSelected.getHandswork());
		this.menwork.setValue(this.detailScheduleShipSelected.getMenwork());
		this.noteshipdetail.setValue(this.detailScheduleShipSelected.getNotedetail());

	}

	@Listen("onChange = #scheduler_type_selector")
	public void defineSchedulerView() {

		final Comboitem selected = this.scheduler_type_selector.getSelectedItem();

		if (selected.equals(this.program_item)) {

			this.grid_scheduleShip.setVisible(false);
			this.shipProgram.setVisible(true);
			this.dailyDetailShip.setVisible(false);
			this.reviewWorkShip.setVisible(false);
			this.searchScheduleShipByDate();

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
			this.setDateInSearch();
		}
	}

	private void defineScheduleShipDetailsView(final ScheduleShip scheduleShip_selected) {

		if (scheduleShip_selected.getArrivaldate() == null) {
			return;
		}

		final Ship ship = this.shipDao.loadShip(scheduleShip_selected.getIdship());

		if (ship != null) {
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

		} else {
			this.ship_name.setSelectedItem(null);
		}

		this.ship_volume.setValue(scheduleShip_selected.getVolume());
		this.note.setValue(scheduleShip_selected.getNote());

		this.shipRif_mcf.setValue(scheduleShip_selected.getRif_mct());

		// set arrival date and time
		Date date = scheduleShip_selected.getArrivaldate();
		this.ship_arrival.setValue(date);

		date = this.scheduleShip_selected.getDeparturedate();

		if (date != null) {
			this.ship_departure.setValue(date);
		}

		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {

			this.listDetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

		}

	}

	@Listen("onOK = #shows_rows_ship")
	public void defineView() {
		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_reviewWork.setPageSize(this.shows_rows_ship.getValue());
		} else {
			this.sw_list_reviewWork.setPageSize(10);
		}

		// this.setDateInSearch();
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

		this.captionShipProgramLabel = this.captionShipProgram.getLabel();

		this.captionPopupScheduleShipLabel = this.captionPopupScheduleShip.getLabel();

		this.captionDetailProgramShipLabel = this.captionDetailProgramShip.getLabel();

	}

	@Listen("onClick = #overview_download")
	public void downloadCSV_ReviewShipWork() {

		if (this.list_review_work.size() != 0) {
			final StringBuilder builder = UtilityCSV.downloadCSV_ReviewShipWork(this.list_review_work);

			Filedownload.save(builder.toString(), "application/text", "bap.csv");
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
	private void initPopupReviewDetail() {
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

		this.shipSchedulerDao.updateDetailFinalScheduleShip(detailFinal);

		this.showReviewShipPopup();

	}

	@Listen("onClick = #modifyDetailFinalScheduleShip")
	public void modifyDetailFinalScheduleShip() {
		if ((this.sw_list_scheduleShip.getSelectedItem() != null) && (this.list_reviewDetailScheduleShip.getSelectedItem() != null)) {

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

		}
	}

	@Listen("onClick = #sw_link_modifyDetailship")
	public void modifyDetailShip() {

		this.detailScheduleShipSelected = this.sw_list_scheduleDetailShip.getSelectedItem().getValue();

		if ((this.detailScheduleShipSelected != null) && (this.scheduleShip_selected != null)) {

			this.defineModifyDetailProgramShip();

			this.add_scheduleShipsDetail_command.setVisible(false);
			this.modify_scheduleShipsDetail_command.setVisible(true);

		}
	}

	@Listen("onClick = #sw_link_modifyscheduleshipProgram")
	public void modifyScheduleshipProgram() {
		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {

			this.ship_rif_sws.setValue(this.scheduleShip_selected.getId().toString());

			this.defineScheduleShipDetailsView(this.scheduleShip_selected);
		}

		this.captionPopupScheduleShip.setLabel(this.captionPopupScheduleShipLabel + " - " + this.scheduleShip_selected.getName());

	}

	@Listen("onClick = #modifyShipSchedule_command")
	public void modifyShipScheduleCommand() {
		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {
			if ((this.ship_name.getSelectedItem() == null) || (this.ship_volume.getValue() == null) || (this.ship_arrival == null)
					|| (this.ship_departure.getValue() == null) || this.ship_arrival.getValue().after(this.ship_departure.getValue())) {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Controllare i valori inseriti..", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
				return;
			} else {
				this.scheduleShip_selected.setIdship(((Ship) this.ship_name.getSelectedItem().getValue()).getId());
				this.scheduleShip_selected.setVolume(this.ship_volume.getValue());
				this.scheduleShip_selected.setNote(this.note.getValue());

				this.scheduleShip_selected.setArrivaldate(this.ship_arrival.getValue());

				this.scheduleShip_selected.setDeparturedate(this.ship_departure.getValue());

				this.scheduleShip_selected.setRif_mct(this.shipRif_mcf.getValue());

				// update
				this.shipSchedulerDao.updateScheduleShip(this.scheduleShip_selected);

				this.popup_scheduleShip.close();

				this.searchScheduleShipByDate();

			}
		}
	}

	@Listen("onClick = #sw_link_modifyscheduleship")
	public void openModifyDetailDailyPopup() {

		// TODO: define here behavior for customer

		this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (this.detailScheduleShipSelected == null) {
			return;
		}

		this.shiftdate_Daily.setValue(this.detailScheduleShipSelected.getShiftdate());

		this.captionShipProgram.setLabel(this.captionShipProgramLabel + " - " + this.detailScheduleShipSelected.getName());

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

		// set customer
		this.ship_customer_daily.setSelectedItem(null);

		if (this.detailScheduleShipSelected.getCustomer_id() != null) {

			final Customer customer = this.customerDAO.loadCustomer(this.detailScheduleShipSelected.getCustomer_id());

			final List<Comboitem> listItem = this.ship_customer_daily.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Customer) {
					final Customer current = item.getValue();
					if (customer.equals(current)) {
						this.ship_customer_daily.setSelectedItem(item);
						break;
					}
				}

			}

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
		this.handswork_Daily.setValue(this.detailScheduleShipSelected.getHandswork());
		this.menwork_Daily.setValue(this.detailScheduleShipSelected.getMenwork());

	}

	@Listen("onClick = #refresh_command")
	public void refreshBapView() {
		this.date_from_overview.setValue(null);
		this.date_to_overview.setValue(null);
		this.searchWorkShip.setValue(null);
		this.select_year.setSelectedItem(null);

		this.full_text_search_ship.setValue(null);
		this.full_text_search_rifSWS.setValue(null);
		this.full_text_search_rifMCT.setValue(null);
		this.shows_rows_ship.setValue(10);

		this.select_shiftBap.setSelectedIndex(0);

		this.searchWorkShip.setValue(Calendar.getInstance().getTime());
		this.searchReviewShipData();
	}

	/**
	 * Set ship list box with initial events
	 */
	@Listen("onOK = #shows_rows, #full_text_search;onChange = #select_shift")
	public void refreshScheduleShipListBox() {

		if (this.searchDateShift.getValue() != null) {
			this.searchDetailScheduleShipByDateShift();
		} else {
			this.searchDetailScheduleShipByDatePeriod();
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

						ShipSchedulerComposer.this.searchScheduleShipByDate();
					} else if (Messagebox.ON_CANCEL.equals(e.getName())) {

					}
				}
			}, params);

		}
	}

	/**
	 * Reset data on ship detail program tab
	 */
	private void resetDataInfoTabDetail() {

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
		this.ship_activity.setValue(null);
		this.ship_customer.setValue(null);
		this.ship_from.setValue(null);
		this.ship_to.setValue(null);

	}

	/**
	 * Reset data on ship program tab
	 */
	private void resetDataInfoTabProgram() {
		this.ship_name.setSelectedItem(null);
		this.ship_volume.setValue(null);
		this.note.setValue(null);
		this.ship_arrival.setValue(null);
		this.ship_departure.setValue(null);
		this.shipRif_mcf.setValue(null);

		this.ship_name_schedule.setSelectedItem(null);
		this.ship_volume_schedule.setValue(null);
		this.note_schedule.setValue(null);
		this.ship_arrival_schedule.setValue(null);
		this.ship_departure_schedule.setValue(null);
		this.ship_rif_mcf.setValue(null);

		this.listDetailScheduleShip.clear();
		this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

	}

	@Listen("onClick = #refreshDetailView")
	public void resfreshDetailView() {
		this.setInitialView();

	}

	@Listen("onClick = #modify_scheduleShipsDetailDaily_command")
	public void saveModifyShipDetailDaily() {

		// TODO: manage customer and save fascia oraria

		this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (this.detailScheduleShipSelected == null) {
			return;
		}

		this.detailScheduleShipSelected.setShiftdate(this.shiftdate_Daily.getValue());

		final Integer shift = Integer.parseInt(this.shift_Daily.getValue().toString());
		this.detailScheduleShipSelected.setShift(shift);
		this.detailScheduleShipSelected.setOperation(this.operation_Daily.getValue().toString());

		this.detailScheduleShipSelected.setIduser(((Person) this.user_Daily.getSelectedItem().getValue()).getId());

		if (this.usersecond_Daily.getSelectedItem() != null) {
			this.detailScheduleShipSelected.setIdseconduser(((Person) this.usersecond_Daily.getSelectedItem().getValue()).getId());
		}

		this.detailScheduleShipSelected.setHandswork(this.handswork_Daily.getValue());
		this.detailScheduleShipSelected.setMenwork(this.menwork_Daily.getValue());

		// set customer
		if (this.ship_customer_daily.getSelectedItem() != null) {

			final Customer customer = this.ship_customer_daily.getSelectedItem().getValue();
			this.detailScheduleShipSelected.setCustomer_id(customer.getId());

		} else {
			this.detailScheduleShipSelected.setCustomer_id(null);

		}

		this.detailScheduleShipSelected.setNotedetail(this.notedetail.getValue());

		// update..
		this.shipSchedulerDao.updateDetailScheduleShip(this.detailScheduleShipSelected);

		// reset view
		this.refreshScheduleShipListBox();
		this.alertShiftDate_detail.setVisible(false);
		this.popup_detail_Daily.close();

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

		final List<DetailScheduleShip> list = this.shipSchedulerDao.searchDetailScheduleShip(dateFrom, dateTo, text_search, no_shift);

		this.sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(list));

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

		List<DetailScheduleShip> list_DetailScheduleShip = null;

		final String _text = this.full_text_search.getValue();
		String text_search = this.full_text_search.getValue();

		if ((text_search != null) && text_search.equals("")) {
			text_search = null;
		}

		final Integer selectedShift = this.getSelectedShift();

		list_DetailScheduleShip = this.shipSchedulerDao.searchDetailScheduleShip(date, text_search, selectedShift);
		this.setInfoDetailShipProgram(date, _text, list_DetailScheduleShip);

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_scheduleShip.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_scheduleShip.setPageSize(10);
		}

		this.sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(list_DetailScheduleShip));

	}

	@Listen("onOK = #full_text_search_rifMCT")
	public void searchMCT() {
		this.searchWorkShip.setValue(null);
		this.date_from_overview.setValue(null);
		this.date_to_overview.setValue(null);
		this.select_year.setSelectedItem(null);
		this.full_text_search_ship.setValue(null);
		this.full_text_search_rifSWS.setValue(null);

		Integer shiftNumber = null;

		if (this.select_shiftBap.getSelectedItem() != null) {
			shiftNumber = this.select_shiftBap.getSelectedIndex();
			if (shiftNumber == 0) {
				shiftNumber = null;
			}
		}

		if (this.full_text_search_rifMCT.getValue() != null) {
			this.list_review_work = this.scheduleDao.loadReviewShipWork(null, null, null, null, this.full_text_search_rifMCT.getValue(), shiftNumber,
					this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
			this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

			this.setTotalVolumeLabel();
		} else {
			this.setDateInSearch();
		}

	}

	/**
	 * Search info about data ship for review
	 */
	@Listen("onChange = #searchWorkShip; onOK = #searchWorkShip, #date_from_overview, #date_to_overview")
	public void searchReviewShipData() {

		this.select_year.setSelectedItem(null);

		final Date date_from = this.searchWorkShip.getValue();

		final String searchText = this.full_text_search_ship.getValue();

		if (date_from != null) {
			this.date_from_overview.setValue(null);
			this.date_to_overview.setValue(null);

			Integer shiftNumber = null;

			if (this.select_shiftBap.getSelectedItem() != null) {
				shiftNumber = this.select_shiftBap.getSelectedIndex();
				if (shiftNumber == 0) {
					shiftNumber = null;
				}
			}

			this.list_review_work = this.scheduleDao.loadReviewShipWork(date_from, null, searchText, this.full_text_search_rifSWS.getValue(),
					this.full_text_search_rifMCT.getValue(), shiftNumber, this.invoicing_cycle_search.getValue(),
					this.working_cycle_search.getValue());
			this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

			this.setTotalVolumeLabel();

		}

	}

	/**
	 * Search info about data ship for review in interval date
	 */
	@Listen("onChange =  #date_from_overview, #date_to_overview; onOK= #date_from_overview, #date_to_overview")
	public void searchReviewShipIntervalDate() {
		this.searchWorkShip.setValue(null);
		this.select_year.setSelectedItem(null);

		Integer shiftNumber = null;

		if (this.select_shiftBap.getSelectedItem() != null) {
			shiftNumber = this.select_shiftBap.getSelectedIndex();
			if (shiftNumber == 0) {
				shiftNumber = null;
			}
		}

		if ((this.date_from_overview.getValue() != null) && (this.date_to_overview.getValue() != null)) {
			this.list_review_work = this.scheduleDao.loadReviewShipWork(this.date_from_overview.getValue(), this.date_to_overview.getValue(),
					this.full_text_search_ship.getValue(), this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(),
					shiftNumber, this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
			this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

			this.setTotalVolumeLabel();
		}
	}

	@Listen("onChange = #searchArrivalDateShipFrom, #searchArrivalDateShipTo")
	public void searchScheduleShipByDate() {
		List<ScheduleShip> list_scheduleShip = null;

		final Date dateFrom = this.searchArrivalDateShipFrom.getValue();
		final Date dateTo = this.searchArrivalDateShipTo.getValue();

		if ((dateFrom == null) && (dateTo == null)) {

			list_scheduleShip = this.shipSchedulerDao.loadAllScheduleShip();

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.sw_list_scheduleShipProgram.setPageSize(this.shows_rows.getValue());
			} else {
				this.sw_list_scheduleShipProgram.setPageSize(10);
			}

			this.sw_list_scheduleShipProgram.setModel(new ListModelList<ScheduleShip>(list_scheduleShip));

		} else if (((dateFrom != null) && (dateTo != null)) && (dateTo.compareTo(dateFrom) >= 0)) {
			final Timestamp dateFromTS = new Timestamp(dateFrom.getTime());

			final Timestamp dateToTS = new Timestamp(dateTo.getTime());

			list_scheduleShip = this.shipSchedulerDao.loadScheduleShipInDate(dateFromTS, dateToTS);

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.sw_list_scheduleShipProgram.setPageSize(this.shows_rows.getValue());
			} else {
				this.sw_list_scheduleShipProgram.setPageSize(10);
			}

			this.sw_list_scheduleShipProgram.setModel(new ListModelList<ScheduleShip>(list_scheduleShip));

		}

		// set label info program ship
		this.setInfoShipProgram(dateFrom, dateTo, this.infoShipProgram);
	}

	@Listen("onOK = #full_text_search_rifSWS")
	public void searchSWS() {
		this.searchWorkShip.setValue(null);
		this.date_from_overview.setValue(null);
		this.date_to_overview.setValue(null);
		this.select_year.setSelectedItem(null);
		this.full_text_search_ship.setValue(null);
		this.full_text_search_rifMCT.setValue(null);

		Integer shiftNumber = null;

		if (this.select_shiftBap.getSelectedItem() != null) {
			shiftNumber = this.select_shiftBap.getSelectedIndex();
			if (shiftNumber == 0) {
				shiftNumber = null;
			}
		}

		if (this.full_text_search_rifSWS.getValue() != null) {
			this.list_review_work = this.scheduleDao.loadReviewShipWork(null, null, null, this.full_text_search_rifSWS.getValue(), null, shiftNumber,
					this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
			this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

			this.setTotalVolumeLabel();
		} else {
			this.setDateInSearch();
		}

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
			this.list_review_work = this.scheduleDao.loadReviewShipWork(this.date_from_overview.getValue(), this.date_to_overview.getValue(),
					this.full_text_search_ship.getValue(), this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(),
					shiftNumber, this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
		} else {
			this.list_review_work = this.scheduleDao.loadReviewShipWork(this.searchWorkShip.getValue(), null, this.full_text_search_ship.getValue(),
					this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(), shiftNumber,
					this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
		}

		this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

		this.setTotalVolumeLabel();
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
			this.list_review_work = this.scheduleDao.loadReviewShipWork(this.date_from_overview.getValue(), this.date_to_overview.getValue(),
					this.full_text_search_ship.getValue(), this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(),
					shiftNumber, this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
		} else {
			this.list_review_work = this.scheduleDao.loadReviewShipWork(this.searchWorkShip.getValue(), null, this.full_text_search_ship.getValue(),
					this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(), shiftNumber,
					this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
		}

		this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

		this.setTotalVolumeLabel();
	}

	@Listen("onChange =#select_year")
	public void selectedYear() {

		this.searchWorkShip.setValue(null);
		this.date_from_overview.setValue(null);
		this.date_from_overview.setValue(null);

		if (this.select_year.getSelectedItem() != null) {

			final String yearSelected = this.select_year.getSelectedItem().getValue();

			if (!yearSelected.equals("TUTTI")) {

				this.searchWorkShip.setValue(null);
				this.date_from_overview.setValue(null);
				this.date_to_overview.setValue(null);

				final Integer year = Integer.parseInt(yearSelected);
				Date date_from;
				Date date_to;

				final String dateFromString = "01/01/" + year;
				final String dateToString = "31/12/" + year;

				try {
					date_from = this.format_it_date.parse(dateFromString);
					date_to = this.format_it_date.parse(dateToString);
				} catch (final ParseException e) {
					return;
				}

				Integer shiftNumber = null;

				if (this.select_shiftBap.getSelectedItem() != null) {
					shiftNumber = this.select_shiftBap.getSelectedIndex();
					if (shiftNumber == 0) {
						shiftNumber = null;
					}
				}

				this.list_review_work = this.scheduleDao.loadReviewShipWork(date_from, date_to, this.full_text_search_ship.getValue(),
						this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(), shiftNumber,
						this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());

				if (this.shows_rows_ship.getValue() != null) {
					this.sw_list_reviewWork.setPageSize(this.shows_rows_ship.getValue());
				} else {
					this.sw_list_reviewWork.setPageSize(10);
				}

				this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

				this.setTotalVolumeLabel();

			} else {

				Integer shiftNumber = null;

				if (this.select_shiftBap.getSelectedItem() != null) {
					shiftNumber = this.select_shiftBap.getSelectedIndex();
					if (shiftNumber == 0) {
						shiftNumber = null;
					}
				}

				this.list_review_work = this.scheduleDao.loadReviewShipWork(null, null, this.full_text_search_ship.getValue(),
						this.full_text_search_rifSWS.getValue(), this.full_text_search_rifMCT.getValue(), shiftNumber,
						this.invoicing_cycle_search.getValue(), this.working_cycle_search.getValue());
				this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(this.list_review_work));

				this.setTotalVolumeLabel();
			}
		}
	}

	@Listen("onChange = #ship_activity")
	public void selectShipForActivityProgram() {

		if (this.ship_activity.getSelectedItem() == null) {
			this.h_program_period.setVisible(false);
		} else {

			this.h_program_period.setVisible(true);

		}

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

	private void setDateInSearch() {
		if ((this.date_from_overview.getValue() == null) && (this.date_to_overview.getValue() == null) && (this.searchWorkShip.getValue() == null)
				&& (this.select_year.getSelectedItem() == null)) {
			this.searchWorkShip.setValue(Calendar.getInstance().getTime());
			this.searchReviewShipData();
		} else if ((this.date_to_overview.getValue() != null) && (this.date_from_overview.getValue() != null)) {
			this.searchWorkShip.setValue(null);
			this.searchReviewShipIntervalDate();
		} else if (this.searchWorkShip.getValue() != null) {
			this.searchReviewShipData();
		} else if (this.select_year.getSelectedItem() != null) {
			this.selectedYear();
		}
	}

	private void setInfoDetailShipProgram(final Date date, final String full_text_search, final List<DetailScheduleShip> list_DetailScheduleShip) {
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

		this.infoDetailShipProgram.setValue("Numero di Navi: " + numberOfShip + " - Totale volumi preventivati: " + volume + " - Totale Mani: "
				+ totalHandsWork + " - Totale Persone: " + totalMenWork);

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

		infoLabel.setValue("Numero di Navi: " + numberOfShip + " - Totale volumi preventivati: " + volume + " - Totale Mani: " + totalHands
				+ " - Totale Persone: " + totalMen);

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
		this.searchArrivalDateShipFrom_detail.setValue(null);
		this.searchArrivalDateShipTo_detail.setValue(null);

		// set initial query for review work
		final Calendar cal = Calendar.getInstance();

		this.searchWorkShip.setValue(cal.getTime());

		this.searchDateShift.setValue(DateUtils.truncate(cal.getTime(), Calendar.DATE));

		// set ship listbox
		this.refreshScheduleShipListBox();

	}

	/**
	 * Set the label total volume value.
	 * */
	private void setTotalVolumeLabel() {

		Double sumVolume = 0.0;
		Double sumVolumeOnBoard = 0.0;
		Double sumVolumeOnBoard_sws = 0.0;
		Double sumVolumeMTC = 0.0;

		for (final ReviewShipWork reviewShipWork : this.list_review_work) {
			if (reviewShipWork.getVolume() != null) {
				sumVolume += reviewShipWork.getVolume();
			}

			if (reviewShipWork.getVolumeunderboard() != null) {
				sumVolumeOnBoard += reviewShipWork.getVolumeunderboard();
			}

			if (reviewShipWork.getVolumeunderboard_sws() != null) {
				sumVolumeOnBoard_sws += reviewShipWork.getVolumeunderboard_sws();
			}

			if (reviewShipWork.getVolume_tw_mct() != null) {
				sumVolumeMTC += reviewShipWork.getVolume_tw_mct();
			}

		}

		this.TotalVolume.setValue(sumVolume.toString());
		this.TotalVolumeOnBoard.setValue(sumVolumeOnBoard.toString());
		this.TotalVolumeOnBoard_sws.setValue(sumVolumeOnBoard_sws.toString());
		this.TotalVolumeTWMTC.setValue(sumVolumeMTC.toString());
	}

	@Listen("onClick = #sw_addScheduleShipProgram")
	public void showAddScheduleShipView() {
		this.resetDataInfoTabProgram();

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

		// popup_ship.open(ref);

	}

	@Listen("onClick = #show_add_panel_program")
	public void showPanelAdd() {

		this.resetDataInfoTabDetail();

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

		// set ship name and alert
		this.messageUpdateRifMCT.setVisible(false);
		this.infoShipNameAndShift.setValue(detailSelected.getName() + " - Turno " + detailSelected.getShift());

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

			final ScheduleShip scheduleShip = this.shipSchedulerDao.loadScheduleShip(detailSelected.getIdscheduleship());

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
		final int idDetailScheduleShip = detailSelected.getId();
		final List<DetailFinalScheduleShip> final_details = this.shipSchedulerDao
				.loadDetailFinalScheduleShipByIdDetailScheduleShip(idDetailScheduleShip);

		this.list_reviewDetailScheduleShip.setModel(new ListModelList<DetailFinalScheduleShip>(final_details));

		// set button
		this.add_finalDetailScheduleShip_command.setVisible(true);
		this.modify_finalDetailScheduleShip_command.setVisible(false);

		// set grid popup values
		this.initPopupReviewDetail();

	}

	@Listen("onClick = #modify_scheduleShipsDetail_command")
	public void updateDetail() {
		if (this.shift.getSelectedItem() == null) {
			return;
		} else {

			this.detailScheduleShipSelected.setShiftdate(this.shiftdate.getValue());
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

			// customer
			if (this.ship_customer.getSelectedItem() != null) {

				final Customer customer = this.ship_customer.getSelectedItem().getValue();
				if (customer != null) {
					this.detailScheduleShipSelected.setCustomer_id(customer.getId());
				}

			}

			this.shipSchedulerDao.updateDetailScheduleShip(this.detailScheduleShipSelected);

			// refresh list detail in popup
			this.addDetailScheduleShipProgram();

		}
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
