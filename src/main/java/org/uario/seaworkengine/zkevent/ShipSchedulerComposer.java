package org.uario.seaworkengine.zkevent;

import java.sql.Timestamp;
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
import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.statistics.ShipTotal;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
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

			final ListModel<Ship> modelComboBox_ShipName = new ListModelList<Ship>(ShipSchedulerComposer.this.shipDao.listAllShip(null));

			// add item in combobox ship name
			if (modelComboBox_ShipName.getSize() == 0) {
				Messagebox.show("Inserire almeno una nave prima di procedere alla programmazione!", "INFO", Messagebox.OK, Messagebox.INFORMATION);
			}

			ShipSchedulerComposer.this.ship_name.setModel(modelComboBox_ShipName);
			ShipSchedulerComposer.this.ship_name_schedule.setModel(modelComboBox_ShipName);

			// add item in combobox shift
			final List<Integer> shiftList = new ArrayList<Integer>();
			shiftList.add(1);
			shiftList.add(2);
			shiftList.add(3);
			shiftList.add(4);

			final ListModel<Integer> modelComboBox_Shift = new ListModelList<Integer>(shiftList);

			ShipSchedulerComposer.this.shift.setModel(modelComboBox_Shift);

			ShipSchedulerComposer.this.shift_Daily.setModel(modelComboBox_Shift);

			// add item operative users in combobox user
			final ListModel<Person> modelComboBox_User = new ListModelList<Person>(ShipSchedulerComposer.this.personDao.listOperativePerson());
			ShipSchedulerComposer.this.user.setModel(modelComboBox_User);

			final ListModel<Person> modelComboBox_UserSecond = new ListModelList<Person>(ShipSchedulerComposer.this.personDao.listOperativePerson());
			ShipSchedulerComposer.this.usersecond.setModel(modelComboBox_UserSecond);

			final ListModel<Person> modelComboBox_UserDaily = new ListModelList<Person>(ShipSchedulerComposer.this.personDao.listOperativePerson());
			ShipSchedulerComposer.this.user_Daily.setModel(modelComboBox_UserDaily);

			final ListModel<Person> modelComboBox_UserSecondDaily = new ListModelList<Person>(
					ShipSchedulerComposer.this.personDao.listOperativePerson());
			ShipSchedulerComposer.this.usersecond_Daily.setModel(modelComboBox_UserSecondDaily);

			ShipSchedulerComposer.this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

			ShipSchedulerComposer.this.setInitialView();

			ShipSchedulerComposer.this.scheduler_type_selector.setSelectedItem(ShipSchedulerComposer.this.detail_item);

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
	private Intbox						crane_review;

	@Wire
	public Component					dailyDetailShip;

	@Wire
	private Comboitem					detail_item;

	@Wire
	private Tab							detail_scheduleShip_tab;

	private DetailScheduleShip			detailScheduleShipSelected;

	@Wire
	private Textbox						full_text_search;

	@Wire
	private Component					grid_scheduleShip;

	@Wire
	private Component					grid_scheduleShip_details;

	@Wire
	private Intbox						handswork;

	@Wire
	public Intbox						handswork_Daily;

	@Wire
	private Label						infoDetailShipProgram;
	@Wire
	private Label						infoShipProgram;

	@Wire
	private Listbox						list_reviewDetailScheduleShip;

	private List<DetailScheduleShip>	listDetailScheduleShip	= new ArrayList<DetailScheduleShip>();

	private final Logger				logger					= Logger.getLogger(UserDetailsComposer.class);

	@Wire
	private Intbox						menwork;

	@Wire
	public Intbox						menwork_Daily;

	@Wire
	private Toolbarbutton				modify_finalDetailScheduleShips_command;

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
	public Textbox						note_schedule;

	@Wire
	public Textbox						notedetail;

	@Wire
	private Textbox						noteshipdetail;

	@Wire
	private Textbox						operation;

	@Wire
	public Textbox						operation_Daily;

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
	private Combobox					shift;

	@Wire
	public Combobox						shift_Daily;

	@Wire
	private Datebox						shiftdate;

	@Wire
	public Datebox						shiftdate_Daily;

	@Wire
	private Datebox						ship_arrival;

	@Wire
	public Datebox						ship_arrival_schedule;

	@Wire
	private Datebox						ship_departure;

	@Wire
	public Datebox						ship_departure_schedule;

	@Wire
	private Combobox					ship_name;

	@Wire
	public Combobox						ship_name_schedule;

	@Wire
	private Intbox						ship_volume;

	@Wire
	public Intbox						ship_volume_schedule;

	protected IShip						shipDao;

	@Wire
	public Component					shipProgram;

	private IScheduleShip				shipSchedulerDao;

	@Wire
	private Intbox						shows_rows;

	@Wire
	private Toolbarbutton				sw_link_reviewscheduleship;

	@Wire
	private Listbox						sw_list_reviewWork;

	@Wire
	private Listbox						sw_list_scheduleDetailShip;

	@Wire
	private Listbox						sw_list_scheduleDetailShipProgram;

	@Wire
	private Listbox						sw_list_scheduleShip;

	@Wire
	private Listbox						sw_list_scheduleShipProgram;

	@Wire
	private Timebox						time_review;

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

	@Listen("onClick = #add_finalDetailScheduleShip_command")
	public void addDetailFinalScheduleShip() {

		if (this.sw_list_scheduleShip.getSelectedItem() != null) {
			final DetailFinalScheduleShip detailFinalScheduleShip = new DetailFinalScheduleShip();
			detailFinalScheduleShip.setIddetailscheduleship(((DetailScheduleShip) this.sw_list_scheduleShip.getSelectedItem().getValue()).getId());
			detailFinalScheduleShip.setCrane(this.crane_review.getValue());
			detailFinalScheduleShip.setTimework(this.time_review.getValue());
			detailFinalScheduleShip.setVolume(this.volume_review.getValue());

			this.shipSchedulerDao.createDetailFinalScheduleShip(detailFinalScheduleShip);

			this.reviewDetailScheduleShip();
		}
	}

	@Listen("onClick = #sw_link_addDetailScheduleShipProgram")
	public void addDetailScheduleShipProgram() {

		this.modify_scheduleShipsDetail_command.setVisible(false);
		this.add_scheduleShipsDetail_command.setVisible(true);

		this.resetDataInfoTabDetail();

		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {

			this.listDetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

			this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>(this.listDetailScheduleShip));
		}

	}

	@Listen("onClick = #addShipSchedule_command")
	public void addScheduleShipCommand() {
		if ((this.ship_name_schedule.getSelectedItem() == null) || (this.ship_volume_schedule.getValue() == null)
				|| (this.ship_arrival_schedule.getValue() == null) || (this.ship_departure_schedule.getValue() == null)
				|| this.ship_arrival_schedule.getValue().after(this.ship_departure_schedule.getValue())) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare i valori inseriti.", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			return;
		} else {
			final ScheduleShip shipSchedule = new ScheduleShip();

			final String shipName = this.ship_name_schedule.getSelectedItem().getValue().toString();

			final int idShip = this.shipDao.listIdShipByName(shipName).get(0);

			shipSchedule.setIdship(idShip);
			shipSchedule.setVolume(this.ship_volume_schedule.getValue());
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

		if ((this.shift.getSelectedItem() == null) || (this.operation.getValue() == null) || (this.user.getSelectedItem() == null)
				|| (this.handswork.getValue() == null) || (this.menwork.getValue() == null) || (this.shiftdate.getValue() == null)) {

			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare i valori inseriti", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		} else {

			final DetailScheduleShip detailScheduleShip = new DetailScheduleShip();

			final Person userOperative = (Person) ShipSchedulerComposer.this.user.getSelectedItem().getValue();
			detailScheduleShip.setIduser(userOperative.getId());

			if (ShipSchedulerComposer.this.usersecond.getSelectedItem() != null) {
				final Person secondUserOperative = (Person) ShipSchedulerComposer.this.usersecond.getSelectedItem().getValue();
				detailScheduleShip.setIdseconduser(secondUserOperative.getId());
			}

			detailScheduleShip.setNotedetail(ShipSchedulerComposer.this.noteshipdetail.getValue());
			detailScheduleShip.setShift((Integer) ShipSchedulerComposer.this.shift.getSelectedItem().getValue());
			detailScheduleShip.setOperation(ShipSchedulerComposer.this.operation.getValue());

			detailScheduleShip.setHandswork(ShipSchedulerComposer.this.handswork.getValue());
			detailScheduleShip.setMenwork(ShipSchedulerComposer.this.menwork.getValue());
			detailScheduleShip.setIdscheduleship(ShipSchedulerComposer.this.scheduleShip_selected.getId());
			detailScheduleShip.setShiftdate(ShipSchedulerComposer.this.shiftdate.getValue());

			this.alertShiftDate.setVisible(false);

			ShipSchedulerComposer.this.shipSchedulerDao.createDetailScheduleShip(detailScheduleShip);

			ShipSchedulerComposer.this.addDetailScheduleShipProgram();

		}
	}

	@Listen("onChange = #shiftdate")
	public void checkShiftDate() {

		if (((ShipSchedulerComposer.this.shiftdate.getValue() != null) && ((ShipSchedulerComposer.this.shiftdate.getValue().compareTo(
				ShipSchedulerComposer.this.scheduleShip_selected.getArrivaldate()) < 0) || (ShipSchedulerComposer.this.shiftdate.getValue()
				.compareTo(ShipSchedulerComposer.this.scheduleShip_selected.getDeparturedate()) > 0)))) {

			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			final String msg = "Attenzione: data arrivo nave " + sdf.format(this.scheduleShip_selected.getArrivaldate()) + ", data partenza nave "
					+ sdf.format(this.scheduleShip_selected.getDeparturedate());
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

			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			final String msg = "Attenzione: data arrivo nave " + sdf.format(this.detailScheduleShipSelected.getArrivaldate())
					+ ", data partenza nave " + sdf.format(this.detailScheduleShipSelected.getDeparturedate());

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

	private void defineModifyDetailProgramShip() {
		if (this.detailScheduleShipSelected != null) {
			this.shiftdate.setValue(this.detailScheduleShipSelected.getShiftdate());

			// select first user
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

			} else {
				this.user.setSelectedItem(null);
			}

			// select second user
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

			} else {
				this.usersecond.setSelectedItem(null);
			}

			// select shift
			final Integer shift = this.detailScheduleShipSelected.getShift();
			if (shift != null) {
				final List<Comboitem> listItem = this.shift.getItems();
				for (final Comboitem item : listItem) {
					if (item.getValue() instanceof Integer) {
						final Integer current_shift = item.getValue();
						if (shift.equals(current_shift)) {
							this.shift.setSelectedItem(item);
							break;
						}
					}

				}
			}

			this.operation.setValue(this.detailScheduleShipSelected.getOperation());

			this.handswork.setValue(this.detailScheduleShipSelected.getHandswork());
			this.menwork.setValue(this.detailScheduleShipSelected.getMenwork());
			this.noteshipdetail.setValue(this.detailScheduleShipSelected.getNotedetail());
		}
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
			this.searchDetailScheduleShipByDateShift();
		} else if (selected.equals(this.verify_review_ship_item)) {
			this.grid_scheduleShip.setVisible(false);
			this.shipProgram.setVisible(false);
			this.dailyDetailShip.setVisible(false);
			this.reviewWorkShip.setVisible(true);
			this.searchReviewShipData();
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

			this.sw_list_scheduleDetailShipProgram.setModel(new ListModelList<DetailScheduleShip>(this.listDetailScheduleShip));
		}

	}

	@Listen("onClick = #deleteDetailFinalScheduleShip")
	public void deleteDetailFinalScheduleShip() {
		if (this.sw_list_scheduleShip.getSelectedItem() != null && this.list_reviewDetailScheduleShip.getSelectedItem() != null) {
			final DetailFinalScheduleShip detailFinal = (DetailFinalScheduleShip) this.list_reviewDetailScheduleShip.getSelectedItem().getValue();
			this.shipSchedulerDao.deleteDetailFinalScheduleShipById(detailFinal.getId());

			this.reviewDetailScheduleShip();
		}
	}

	private void deleteDetailship() {
		if (this.detailScheduleShipSelected == null) {
			return;
		}
		this.shipSchedulerDao.deleteDetailScheduleShip(this.detailScheduleShipSelected.getId());

		if (this.searchDateShift.getValue() == null) {
			this.setScheduleShipListBox();
		} else {
			this.searchDetailScheduleShipByDateShift();
		}

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

	}

	private Integer getSelectedShift() {
		Integer shiftSelected = null;
		if ((this.select_shift.getSelectedItem() != null) && (this.select_shift.getSelectedIndex() != 0)) {
			shiftSelected = this.select_shift.getSelectedIndex();
		}
		return shiftSelected;
	}

	private void initPopupReviewDetail() {
		this.add_finalDetailScheduleShip_command.setVisible(true);
		this.modify_finalDetailScheduleShips_command.setVisible(false);
		this.crane_review.setValue(null);
		this.volume_review.setValue(null);
		this.time_review.setValue(null);
	}

	@Listen("onClick = #modifyDetailFinalScheduleShip")
	public void modifyDetailFinalScheduleShip() {
		if (this.sw_list_scheduleShip.getSelectedItem() != null && this.list_reviewDetailScheduleShip.getSelectedItem() != null) {
			final DetailFinalScheduleShip detailFinal = (DetailFinalScheduleShip) this.list_reviewDetailScheduleShip.getSelectedItem().getValue();
			this.crane_review.setValue(detailFinal.getCrane());
			this.volume_review.setValue(detailFinal.getVolume());
			this.time_review.setValue(detailFinal.getTimework());
			this.add_finalDetailScheduleShip_command.setVisible(false);
			this.modify_finalDetailScheduleShips_command.setVisible(true);
		}
	}

	@Listen("onClick = #modify_finalDetailScheduleShips_command")
	public void modifyDetailFinalScheduleShipCommand() {
		if (this.sw_list_scheduleShip.getSelectedItem() != null && this.list_reviewDetailScheduleShip.getSelectedItem() != null) {
			final DetailFinalScheduleShip detailFinal = (DetailFinalScheduleShip) this.list_reviewDetailScheduleShip.getSelectedItem().getValue();
			detailFinal.setCrane(this.crane_review.getValue());
			detailFinal.setVolume(this.volume_review.getValue());
			detailFinal.setTimework(this.time_review.getValue());

			this.shipSchedulerDao.updateDetailFinalScheduleShip(detailFinal);

			this.reviewDetailScheduleShip();

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
			this.defineScheduleShipDetailsView(this.scheduleShip_selected);
		}
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

				// update
				this.shipSchedulerDao.updateScheduleShip(this.scheduleShip_selected);

				this.popup_scheduleShip.close();

				this.searchScheduleShipByDate();

			}
		}
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
			final List<Comboitem> listItem = this.shift_Daily.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Integer) {
					final Integer current_shift = item.getValue();
					if (shift.equals(current_shift)) {
						this.shift_Daily.setSelectedItem(item);
						break;
					}
				}

			}
		}

		this.operation_Daily.setValue(this.detailScheduleShipSelected.getOperation());
		this.notedetail.setValue(this.detailScheduleShipSelected.getNotedetail());
		this.handswork_Daily.setValue(this.detailScheduleShipSelected.getHandswork());
		this.menwork_Daily.setValue(this.detailScheduleShipSelected.getMenwork());

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

			// sdfsd
		}
	}

	/**
	 * Reset data on ship detail program tab
	 */
	private void resetDataInfoTabDetail() {
		this.shift.setSelectedIndex(0);
		this.operation.setValue("-");
		this.user.setSelectedIndex(0);
		this.usersecond.setSelectedIndex(0);
		this.handswork.setValue(0);
		this.menwork.setValue(0);
		this.shiftdate.setValue(new Date());

		this.shift_Daily.setSelectedIndex(0);
		this.operation_Daily.setValue("-");
		this.user_Daily.setSelectedIndex(0);
		this.handswork_Daily.setValue(0);
		this.menwork_Daily.setValue(0);
		this.shiftdate_Daily.setValue(new Date());
		this.notedetail.setValue("");
		this.noteshipdetail.setValue("");

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

		this.ship_name_schedule.setSelectedItem(null);
		this.ship_volume_schedule.setValue(null);
		this.note_schedule.setValue(null);
		this.ship_arrival_schedule.setValue(null);
		this.ship_departure_schedule.setValue(null);

		this.listDetailScheduleShip.clear();
		this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

	}

	@Listen("onClick = #refreshDetailView")
	public void resfreshDetailView() {
		this.setInitialView();
	}

	@Listen("onClick = #sw_link_reviewscheduleship")
	public void reviewDetailScheduleShip() {
		if (this.sw_list_scheduleShip.getSelectedItem() != null) {
			final int idDetailScheduleShip = ((DetailScheduleShip) this.sw_list_scheduleShip.getSelectedItem().getValue()).getId();
			this.list_reviewDetailScheduleShip.setModel(new ListModelList<DetailFinalScheduleShip>(this.shipSchedulerDao
					.loadDetailFinalScheduleShipByIdDetailScheduleShip(idDetailScheduleShip)));

			this.initPopupReviewDetail();

		}
	}

	@Listen("onClick = #modify_scheduleShipsDetailDaily_command")
	public void saveModifyShipDetailDaily() {

		this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (this.detailScheduleShipSelected == null) {
			return;
		}

		this.detailScheduleShipSelected.setShiftdate(this.shiftdate_Daily.getValue());
		final Integer shift = Integer.parseInt(this.shift_Daily.getValue().toString());
		this.detailScheduleShipSelected.setShift(shift);
		this.detailScheduleShipSelected.setOperation(this.operation_Daily.getValue().toString());
		this.detailScheduleShipSelected.setIduser(((Person) this.user_Daily.getSelectedItem().getValue()).getId());
		this.detailScheduleShipSelected.setNotedetail(this.notedetail.getValue());
		if (this.usersecond_Daily.getSelectedItem() != null) {
			this.detailScheduleShipSelected.setIdseconduser(((Person) this.usersecond_Daily.getSelectedItem().getValue()).getId());
		}

		this.detailScheduleShipSelected.setHandswork(this.handswork_Daily.getValue());
		this.detailScheduleShipSelected.setMenwork(this.menwork_Daily.getValue());

		// update..
		this.shipSchedulerDao.updateDetailScheduleShip(this.detailScheduleShipSelected);

		// reset view
		this.searchDetailScheduleShipByDateShift();
		this.alertShiftDate_detail.setVisible(false);
		this.popup_detail_Daily.close();

	}

	@Listen("onChange = #searchArrivalDateShipFrom_detail, #searchArrivalDateShipTo_detail")
	public void searchDetailScheduleShipByDate() {
		this.searchDateShift.setValue(null);

		final Date dateFrom = this.searchArrivalDateShipFrom_detail.getValue();

		final Date dateTo = this.searchArrivalDateShipTo_detail.getValue();

		if (((dateFrom != null) && (dateTo != null)) && (dateTo.compareTo(dateFrom) >= 0)) {

			String text_search = this.full_text_search.getValue();

			if ((text_search != null) && text_search.equals("")) {
				text_search = null;
			}

			final Integer no_shift = this.getSelectedShift();
			final List<DetailScheduleShip> list = this.shipSchedulerDao.loadDetailScheduleShipByDateAndShipName(dateFrom, dateTo, text_search,
					no_shift);

			this.sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(list));

		} else {
			return;
		}

		this.setInfoShipProgram(new Timestamp(dateFrom.getTime()), new Timestamp(dateTo.getTime()), this.infoDetailShipProgram);
	}

	@Listen("onChange = #searchDateShift; onOK = #searchDateShift")
	public void searchDetailScheduleShipByDateShift() {

		if ((this.searchDateShift.getValue() == null) && (this.searchArrivalDateShipFrom_detail.getValue() != null)
				&& (this.searchArrivalDateShipTo_detail.getValue() != null)) {
			this.searchDetailScheduleShipByDate();
			return;
		}

		this.searchArrivalDateShipFrom_detail.setValue(null);
		this.searchArrivalDateShipTo_detail.setValue(null);

		if (this.searchDateShift.getValue() != null) {

			this.setScheduleShipListBox(this.searchDateShift.getValue());
		} else {

			final List<DetailScheduleShip> listDetailScheduleShipNullTimeShift = this.shipSchedulerDao.loadDetailScheduleWithShiftDateNull();

			if (listDetailScheduleShipNullTimeShift.size() != 0) {
				this.sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(listDetailScheduleShipNullTimeShift));
				this.infoDetailShipProgram.setValue("");
			}

		}

	}

	/**
	 * Search info about data ship for review
	 */
	@Listen("onChange = #searchWorkShip; onOK = #searchWorkShip")
	public void searchReviewShipData() {

		if (this.searchWorkShip.getValue() == null) {
			this.searchWorkShip.setValue(Calendar.getInstance().getTime());
		}

		final Date date_to_pick = this.searchWorkShip.getValue();

		final List<ReviewShipWork> list_review_work = this.scheduleDao.loadReviewShipWork(date_to_pick);
		this.sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(list_review_work));

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

			this.infoShipProgram.setValue("");

		} else if (((dateFrom != null) && (dateTo != null)) && (dateTo.compareTo(dateFrom) >= 0)) {
			final Timestamp dateFromTS = new Timestamp(dateFrom.getTime());

			final Timestamp dateToTS = new Timestamp(dateTo.getTime());

			list_scheduleShip = this.shipSchedulerDao.loadScheduleShipInDate(dateFromTS, dateToTS);

			// set label info program ship
			this.setInfoShipProgram(dateFromTS, dateToTS, this.infoShipProgram);

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.sw_list_scheduleShipProgram.setPageSize(this.shows_rows.getValue());
			} else {
				this.sw_list_scheduleShipProgram.setPageSize(10);
			}

			this.sw_list_scheduleShipProgram.setModel(new ListModelList<ScheduleShip>(list_scheduleShip));

		}
	}

	@Listen("onChange = #select_shift")
	public void selectDetailShipByShift() {
		if (this.searchDateShift.getValue() != null) {
			this.searchDetailScheduleShipByDateShift();
		} else {
			this.searchDetailScheduleShipByDate();
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

	private void setInfoShipProgram(final Timestamp dateFrom, final Timestamp dateTo, final Label infoShipProgram) {
		infoShipProgram.setValue("");

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

		infoShipProgram.setValue("Numero di Navi: " + numberOfShip + " - Totale volumi preventivati: " + volume + " - Totale Mani: " + totalHands
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
		this.searchWorkShip.setValue(Calendar.getInstance().getTime());

		// set ship listbox
		this.setScheduleShipListBox();

	}

	/**
	 * Set ship list box with initial events
	 */
	@Listen("onOK = #shows_rows, #full_text_search")
	public void setScheduleShipListBox() {
		final Calendar cal = Calendar.getInstance();
		final Date now = DateUtils.truncate(cal.getTime(), Calendar.DATE);

		this.searchDateShift.setValue(now);

		this.setScheduleShipListBox(now);
	}

	/**
	 * Search by shit date
	 *
	 * @param date
	 */
	private void setScheduleShipListBox(final Date date) {
		List<DetailScheduleShip> list_DetailScheduleShip = null;

		final String _text = this.full_text_search.getValue();
		final Integer selectedShift = this.getSelectedShift();

		if ((_text != null) && !_text.equals("")) {

			list_DetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByShiftDateAndShipName(date, _text, selectedShift);
			this.setInfoDetailShipProgram(date, _text, list_DetailScheduleShip);

		} else {
			list_DetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByShiftDateAndShipName(date, null, selectedShift);
			this.setInfoDetailShipProgram(date, _text, list_DetailScheduleShip);

		}

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_scheduleShip.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_scheduleShip.setPageSize(10);
		}

		this.sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(list_DetailScheduleShip));

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

	@Listen("onClick = #modify_scheduleShipsDetail_command")
	public void updateDetail() {
		if ((this.shift.getSelectedItem() == null) || (this.operation.getValue() == null) || (this.user.getSelectedItem() == null)
				|| (this.handswork.getValue() == null) || (this.menwork.getValue() == null) || (this.shiftdate.getValue() == null)) {

			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare i valori inseriti", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		} else {
			this.detailScheduleShipSelected.setShiftdate(this.shiftdate.getValue());
			final Integer shift = Integer.parseInt(this.shift.getValue().toString());
			this.detailScheduleShipSelected.setShift(shift);
			this.detailScheduleShipSelected.setOperation(this.operation.getValue().toString());
			this.detailScheduleShipSelected.setIduser(((Person) this.user.getSelectedItem().getValue()).getId());

			if (this.usersecond.getSelectedItem() != null) {
				this.detailScheduleShipSelected.setIdseconduser(((Person) this.usersecond.getSelectedItem().getValue()).getId());
			}

			this.detailScheduleShipSelected.setHandswork(this.handswork.getValue());
			this.detailScheduleShipSelected.setMenwork(this.menwork.getValue());
			this.detailScheduleShipSelected.setNotedetail(this.noteshipdetail.getValue());

			this.shipSchedulerDao.updateDetailScheduleShip(this.detailScheduleShipSelected);
			// refresh list detail in popup
			this.addDetailScheduleShipProgram();

		}
	}
}
