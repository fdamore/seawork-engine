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
import org.zkoss.zul.Doublebox;
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
import org.zkoss.zul.Toolbarbutton;

public class ShipSchedulerComposer extends SelectorComposer<Component> {

	private final class StartingEvent implements EventListener<Event> {
		@Override
		public void onEvent(final Event arg0) throws Exception {

			if (person_logged.isViewerOnly()) {
				// set initial item if user is a viewer only user
				scheduler_type_selector.setSelectedItem(detail_item);
			}

			// get the DAOs
			shipSchedulerDao = (IScheduleShip) SpringUtil.getBean(BeansTag.SCHEDULE_SHIP_DAO);
			shipDao = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);
			personDao = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);
			scheduleDao = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);

			final ListModel<Ship> modelComboBox_ShipName = new ListModelList<Ship>(shipDao.listAllShip(null));

			// add item in combobox ship name
			if (modelComboBox_ShipName.getSize() == 0) {
				Messagebox.show("Inserire almeno una nave prima di procedere alla programmazione!", "INFO", Messagebox.OK,
						Messagebox.INFORMATION);
			}

			ship_name.setModel(modelComboBox_ShipName);
			ship_name_schedule.setModel(modelComboBox_ShipName);

			// add item in combobox shift
			final List<Integer> shiftList = new ArrayList<Integer>();
			shiftList.add(1);
			shiftList.add(2);
			shiftList.add(3);
			shiftList.add(4);

			final ListModel<Integer> modelComboBox_Shift = new ListModelList<Integer>(shiftList);

			shift.setModel(modelComboBox_Shift);

			shift_Daily.setModel(modelComboBox_Shift);

			// add item operative users in combobox user
			final ListModel<Person> modelComboBox_User = new ListModelList<Person>(personDao.listOperativePerson());
			user.setModel(modelComboBox_User);

			final ListModel<Person> modelComboBox_UserSecond = new ListModelList<Person>(personDao.listOperativePerson());
			usersecond.setModel(modelComboBox_UserSecond);

			final ListModel<Person> modelComboBox_UserDaily = new ListModelList<Person>(personDao.listOperativePerson());
			user_Daily.setModel(modelComboBox_UserDaily);

			final ListModel<Person> modelComboBox_UserSecondDaily = new ListModelList<Person>(personDao.listOperativePerson());
			usersecond_Daily.setModel(modelComboBox_UserSecondDaily);

			sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

			setInitialView();

			scheduler_type_selector.setSelectedItem(detail_item);

		}
	}

	/**
	 *
	 */
	private static final long			serialVersionUID			= 1L;

	@Wire
	private Component					add_scheduleShips_command;

	@Wire
	Toolbarbutton						add_scheduleShipsDetail_command;

	@Wire
	public Row							alertShiftDate;

	@Wire
	public Row							alertShiftDate_detail;

	@Wire
	private Textbox						cranedetail;

	@Wire
	public Component					dailyDetailShip;

	@Wire
	private Comboitem					detail_item;

	@Wire
	private Tab							detail_scheduleShip_tab;

	DetailScheduleShip					detailScheduleShip_selected	= null;

	@Wire
	public DetailScheduleShip			detailScheduleShipSelected;

	@Wire
	private Intbox						finalvolumedetail;

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
	private List<DetailScheduleShip>	listDetailScheduleShip		= new ArrayList<DetailScheduleShip>();
	private final Logger				logger						= Logger.getLogger(UserDetailsComposer.class);

	@Wire
	private Intbox						menwork;

	@Wire
	public Intbox						menwork_Daily;

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

	private ScheduleShip				scheduleShip_selected		= null;

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
	private Doublebox					timeworkdetail;

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

	@Listen("onClick = #sw_link_addDetailScheduleShipProgram")
	public void addDetailScheduleShipProgram() {

		modify_scheduleShipsDetail_command.setVisible(false);
		add_scheduleShipsDetail_command.setVisible(true);

		resetDataInfoTabDetail();

		scheduleShip_selected = (ScheduleShip) sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (scheduleShip_selected != null) {

			listDetailScheduleShip = shipSchedulerDao.loadDetailScheduleShipByIdSchedule(scheduleShip_selected.getId());

			sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>(listDetailScheduleShip));
		}

	}

	@Listen("onClick = #addShipSchedule_command")
	public void addScheduleShipCommand() {
		if ((ship_name_schedule.getSelectedItem() == null) || (ship_volume_schedule.getValue() == null)
				|| (ship_arrival_schedule.getValue() == null) || (ship_departure_schedule.getValue() == null)
				|| ship_arrival_schedule.getValue().after(ship_departure_schedule.getValue())) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare i valori inseriti.", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null,
					params);
			return;
		} else {
			final ScheduleShip shipSchedule = new ScheduleShip();

			final String shipName = ship_name_schedule.getSelectedItem().getValue().toString();

			final int idShip = shipDao.listIdShipByName(shipName).get(0);

			shipSchedule.setIdship(idShip);
			shipSchedule.setVolume(ship_volume_schedule.getValue());
			shipSchedule.setNote(note_schedule.getValue());

			shipSchedule.setArrivaldate(ship_arrival_schedule.getValue());

			shipSchedule.setDeparturedate(ship_departure_schedule.getValue());

			shipSchedulerDao.createScheduleShip(shipSchedule);

			searchScheduleShipByDate();

			resetDataInfoTabProgram();

		}
	}

	@Listen("onClick = #add_scheduleShipsDetail_command")
	public void addScheduleShipsDetailCommand() {

		if ((shift.getSelectedItem() == null) || (operation.getValue() == null) || (user.getSelectedItem() == null)
				|| (handswork.getValue() == null) || (menwork.getValue() == null) || (shiftdate.getValue() == null)) {

			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare i valori inseriti", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null,
					params);

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

			alertShiftDate.setVisible(false);

			ShipSchedulerComposer.this.shipSchedulerDao.createDetailScheduleShip(detailScheduleShip);

			ShipSchedulerComposer.this.addDetailScheduleShipProgram();

		}
	}

	@Listen("onChange = #shiftdate")
	public void checkShiftDate() {

		if (((ShipSchedulerComposer.this.shiftdate.getValue() != null) && ((ShipSchedulerComposer.this.shiftdate.getValue()
				.compareTo(ShipSchedulerComposer.this.scheduleShip_selected.getArrivaldate()) < 0) || (ShipSchedulerComposer.this.shiftdate
				.getValue().compareTo(ShipSchedulerComposer.this.scheduleShip_selected.getDeparturedate()) > 0)))) {

			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			final String msg = "Attenzione: data arrivo nave " + sdf.format(scheduleShip_selected.getArrivaldate())
					+ ", data partenza nave " + sdf.format(scheduleShip_selected.getDeparturedate());
			msgAlert.setValue(msg);

			alertShiftDate.setVisible(true);

		} else {
			alertShiftDate.setVisible(false);

		}

	}

	@Listen("onChange = #shiftdate_Daily")
	public void checkShiftDate_detail() {
		if (sw_list_scheduleShip.getSelectedItem() != null) {
			detailScheduleShipSelected = sw_list_scheduleShip.getSelectedItem().getValue();
		}

		if ((((detailScheduleShipSelected != null) && (ShipSchedulerComposer.this.shiftdate_Daily.getValue() != null)) && ((ShipSchedulerComposer.this.shiftdate_Daily
				.getValue().compareTo(ShipSchedulerComposer.this.detailScheduleShipSelected.getArrivaldate()) < 0) || (ShipSchedulerComposer.this.shiftdate_Daily
				.getValue().compareTo(ShipSchedulerComposer.this.detailScheduleShipSelected.getDeparturedate()) > 0)))) {

			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			final String msg = "Attenzione: data arrivo nave " + sdf.format(detailScheduleShipSelected.getArrivaldate())
					+ ", data partenza nave " + sdf.format(detailScheduleShipSelected.getDeparturedate());

			msgAlert_detail.setValue(msg);

			alertShiftDate_detail.setVisible(true);
		} else {

			alertShiftDate_detail.setVisible(false);
		}

	}

	@Listen("onClick= #closeShipSchedule_command")
	public void closeAddShipScheduleView() {
		resetDataInfoTabProgram();

		grid_scheduleShip.setVisible(false);
	}

	@Listen("onClick = #closeNoSave")
	public void closeNoSave() {
		resetDataInfoTabProgram();
	}

	private void defineModifyDetailProgramShip() {
		if (detailScheduleShipSelected != null) {
			shiftdate.setValue(detailScheduleShipSelected.getShiftdate());

			// select first user
			Person person = personDao.loadPerson(detailScheduleShipSelected.getIduser());
			if (person != null) {
				final List<Comboitem> listItem = user.getItems();
				for (final Comboitem item : listItem) {
					if (item.getValue() instanceof Person) {
						final Person current_person = item.getValue();
						if (person.equals(current_person)) {
							user.setSelectedItem(item);
							break;
						}
					}

				}

			} else {
				user.setSelectedItem(null);
			}

			// select second user
			person = personDao.loadPerson(detailScheduleShipSelected.getIdseconduser());
			if (person != null) {
				final List<Comboitem> listItem = usersecond.getItems();
				for (final Comboitem item : listItem) {
					if (item.getValue() instanceof Person) {
						final Person current_person = item.getValue();
						if (person.equals(current_person)) {
							usersecond.setSelectedItem(item);
							break;
						}
					}

				}

			} else {
				usersecond.setSelectedItem(null);
			}

			// select shift
			final Integer shift = detailScheduleShipSelected.getShift();
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

			operation.setValue(detailScheduleShipSelected.getOperation());

			handswork.setValue(detailScheduleShipSelected.getHandswork());
			menwork.setValue(detailScheduleShipSelected.getMenwork());
			noteshipdetail.setValue(detailScheduleShipSelected.getNotedetail());
		}
	}

	@Listen("onChange = #scheduler_type_selector")
	public void defineSchedulerView() {

		final Comboitem selected = scheduler_type_selector.getSelectedItem();

		if (selected.equals(program_item)) {
			grid_scheduleShip.setVisible(false);
			shipProgram.setVisible(true);
			dailyDetailShip.setVisible(false);
			reviewWorkShip.setVisible(false);
			searchScheduleShipByDate();
		} else if (selected.equals(detail_item)) {
			grid_scheduleShip.setVisible(false);
			shipProgram.setVisible(false);
			dailyDetailShip.setVisible(true);
			reviewWorkShip.setVisible(false);
			searchDetailScheduleShipByDateShift();
		} else if (selected.equals(verify_review_ship_item)) {
			grid_scheduleShip.setVisible(false);
			shipProgram.setVisible(false);
			dailyDetailShip.setVisible(false);
			reviewWorkShip.setVisible(true);
			searchReviewShipData();
		}
	}

	private void defineScheduleShipDetailsView(final ScheduleShip scheduleShip_selected) {

		if (scheduleShip_selected.getArrivaldate() == null) {
			return;
		}

		final Ship ship = shipDao.loadShip(scheduleShip_selected.getIdship());

		if (ship != null) {
			final List<Comboitem> listItem = ship_name.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Ship) {
					final Ship current_ship = item.getValue();
					if (ship.equals(current_ship)) {
						ship_name.setSelectedItem(item);
						break;
					}
				}

			}

		} else {
			ship_name.setSelectedItem(null);
		}

		ship_volume.setValue(scheduleShip_selected.getVolume());
		note.setValue(scheduleShip_selected.getNote());

		// set arrival date and time
		Date date = scheduleShip_selected.getArrivaldate();
		ship_arrival.setValue(date);

		date = this.scheduleShip_selected.getDeparturedate();

		if (date != null) {
			ship_departure.setValue(date);
		}

		this.scheduleShip_selected = (ScheduleShip) sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {

			listDetailScheduleShip = shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

			sw_list_scheduleDetailShipProgram.setModel(new ListModelList<DetailScheduleShip>(listDetailScheduleShip));
		}

	}

	private void deleteDetailship() {
		if (detailScheduleShip_selected == null) {
			return;
		}
		shipSchedulerDao.deleteDetailScheduleShip(detailScheduleShip_selected.getId());

		if (searchDateShift.getValue() == null) {
			this.setScheduleShipListBox();
		} else {
			searchDetailScheduleShipByDateShift();
		}

	}

	@Listen("onClick = #sw_link_deleteDetailship")
	public void deleteDetailshipInListDetail() {

		detailScheduleShipSelected = sw_list_scheduleDetailShip.getSelectedItem().getValue();
		if ((detailScheduleShipSelected != null) && (scheduleShip_selected != null)) {

			shipSchedulerDao.deleteDetailScheduleShip(detailScheduleShipSelected.getId());

			listDetailScheduleShip = shipSchedulerDao.loadDetailScheduleShipByIdSchedule(scheduleShip_selected.getId());

			sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>(listDetailScheduleShip));
		}

	}

	@Override
	public void doFinally() throws Exception {

		// set info about person logged
		person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// SHOW SHIFT CONFIGURATOR
		getSelf().addEventListener(ZkEventsTag.onShowShipScheduler, new StartingEvent());

	}

	private Integer getSelectedShift() {
		Integer shiftSelected = null;
		if ((select_shift.getSelectedItem() != null) && (select_shift.getSelectedIndex() != 0)) {
			shiftSelected = select_shift.getSelectedIndex();
		}
		return shiftSelected;
	}

	@Listen("onClick = #sw_link_modifyDetailship")
	public void modifyDetailShip() {
		detailScheduleShipSelected = sw_list_scheduleDetailShip.getSelectedItem().getValue();
		if ((detailScheduleShipSelected != null) && (scheduleShip_selected != null)) {
			defineModifyDetailProgramShip();
			add_scheduleShipsDetail_command.setVisible(false);
			modify_scheduleShipsDetail_command.setVisible(true);

		}
	}

	@Listen("onClick = #sw_link_modifyscheduleshipProgram")
	public void modifyScheduleshipProgram() {
		scheduleShip_selected = (ScheduleShip) sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (scheduleShip_selected != null) {
			defineScheduleShipDetailsView(scheduleShip_selected);
		}
	}

	@Listen("onClick = #modifyShipSchedule_command")
	public void modifyShipScheduleCommand() {
		scheduleShip_selected = (ScheduleShip) sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (scheduleShip_selected != null) {
			if ((ship_name.getSelectedItem() == null) || (ship_volume.getValue() == null) || (ship_arrival == null)
					|| (ship_departure.getValue() == null) || ship_arrival.getValue().after(ship_departure.getValue())) {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Controllare i valori inseriti..", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null,
						null, params);
				return;
			} else {
				scheduleShip_selected.setIdship(((Ship) ship_name.getSelectedItem().getValue()).getId());
				scheduleShip_selected.setVolume(ship_volume.getValue());
				scheduleShip_selected.setNote(note.getValue());

				scheduleShip_selected.setArrivaldate(ship_arrival.getValue());

				scheduleShip_selected.setDeparturedate(ship_departure.getValue());

				// update
				shipSchedulerDao.updateScheduleShip(scheduleShip_selected);

				popup_scheduleShip.close();

				searchScheduleShipByDate();

			}
		}
	}

	@Listen("onClick = #modify_Scheduleships_command")
	public void mofifyShipsCommand() {

		if (sw_list_scheduleShip.getSelectedItem().getValue() != null) {
			detailScheduleShip_selected = sw_list_scheduleShip.getSelectedItem().getValue();

			// take schedule ship
			scheduleShip_selected = shipSchedulerDao.loadScheduleShip(detailScheduleShip_selected.getIdscheduleship());

			if (scheduleShip_selected == null) {
				return;
			}

			if ((ship_name.getSelectedItem() == null) || (ship_volume.getValue() == null) || (ship_arrival == null)
					|| (ship_departure.getValue() == null) || ship_arrival.getValue().after(ship_departure.getValue())) {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Controllare i valori inseriti..", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null,
						null, params);
				return;
			} else {

				if (listDetailScheduleShip.size() == 0) {
					final Map<String, String> params = new HashMap<String, String>();
					params.put("sclass", "mybutton Button");
					final Messagebox.Button[] buttons = new Messagebox.Button[1];
					buttons[0] = Messagebox.Button.OK;

					Messagebox.show("Inserire dettagli di programmazione", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION,
							null, null, params);
					return;
				}

				final int idShip = ((Ship) ship_name.getSelectedItem().getValue()).getId();

				scheduleShip_selected.setIdship(idShip);
				scheduleShip_selected.setVolume(ship_volume.getValue());
				scheduleShip_selected.setNote(note.getValue());

				scheduleShip_selected.setArrivaldate(ship_arrival.getValue());

				scheduleShip_selected.setDeparturedate(ship_departure.getValue());

				// update
				shipSchedulerDao.updateScheduleShip(scheduleShip_selected);

				shipSchedulerDao.deteleDetailSchedueleShipByIdSchedule(scheduleShip_selected.getId());

				for (final DetailScheduleShip item : listDetailScheduleShip) {
					item.setIdscheduleship(scheduleShip_selected.getId());
					shipSchedulerDao.createDetailScheduleShip(item);
				}

				// update list
				if (searchDateShift.getValue() != null) {
					// set ship ListBox
					this.setScheduleShipListBox(searchDateShift.getValue());
				} else {
					this.setScheduleShipListBox();
				}

				grid_scheduleShip_details.setVisible(false);

				resetDataInfoTabProgram();

				final Map<String, String> params = new HashMap<String, String>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Dati programmazione aggiornati", "INFO", buttons, null, Messagebox.INFORMATION, null, null,
						params);

			}
		}

	}

	@Listen("onClick = #sw_link_modifyscheduleship")
	public void openModifyDetailDailyPopup() {

		detailScheduleShipSelected = sw_list_scheduleShip.getSelectedItem().getValue();

		if (detailScheduleShipSelected == null) {
			return;
		}

		shiftdate_Daily.setValue(detailScheduleShipSelected.getShiftdate());

		// select first user
		Person person = personDao.loadPerson(detailScheduleShipSelected.getIduser());
		if (person != null) {
			final List<Comboitem> listItem = user_Daily.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Person) {
					final Person current_person = item.getValue();
					if (person.equals(current_person)) {
						user_Daily.setSelectedItem(item);
						break;
					}
				}

			}

		} else {
			user_Daily.setSelectedItem(null);
		}

		// select second user
		person = personDao.loadPerson(detailScheduleShipSelected.getIdseconduser());
		if (person != null) {
			final List<Comboitem> listItem = usersecond_Daily.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Person) {
					final Person current_person = item.getValue();
					if (person.equals(current_person)) {
						usersecond_Daily.setSelectedItem(item);
						break;
					}
				}

			}

		} else {
			usersecond_Daily.setSelectedItem(null);
		}

		// select shift
		final Integer shift = detailScheduleShipSelected.getShift();
		if (shift != null) {
			final List<Comboitem> listItem = shift_Daily.getItems();
			for (final Comboitem item : listItem) {
				if (item.getValue() instanceof Integer) {
					final Integer current_shift = item.getValue();
					if (shift.equals(current_shift)) {
						shift_Daily.setSelectedItem(item);
						break;
					}
				}

			}
		}

		operation_Daily.setValue(detailScheduleShipSelected.getOperation());
		notedetail.setValue(detailScheduleShipSelected.getNotedetail());
		handswork_Daily.setValue(detailScheduleShipSelected.getHandswork());
		menwork_Daily.setValue(detailScheduleShipSelected.getMenwork());

		// set info about final work
		cranedetail.setValue(detailScheduleShipSelected.getCrane());
		finalvolumedetail.setValue(detailScheduleShipSelected.getVolume() == null ? null : detailScheduleShipSelected.getVolume()
				.intValue());
		timeworkdetail.setValue(detailScheduleShipSelected.getTimework());

	}

	@Listen("onClick = #sw_link_deleteship")
	public void removeItem() {

		detailScheduleShip_selected = sw_list_scheduleShip.getSelectedItem().getValue();

		// take schedule ship
		scheduleShip_selected = shipSchedulerDao.loadScheduleShip(detailScheduleShip_selected.getIdscheduleship());

		final Map<String, String> params = new HashMap<String, String>();
		params.put("sclass", "mybutton Button");

		final Messagebox.Button[] buttons = new Messagebox.Button[2];
		buttons[0] = Messagebox.Button.OK;
		buttons[1] = Messagebox.Button.CANCEL;

		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.EXCLAMATION,
				null, new EventListener<ClickEvent>() {
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
		scheduleShip_selected = (ScheduleShip) sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (scheduleShip_selected != null) {

			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");

			final Messagebox.Button[] buttons = new Messagebox.Button[2];
			buttons[0] = Messagebox.Button.OK;
			buttons[1] = Messagebox.Button.CANCEL;

			Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null,
					Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>() {
						@Override
						public void onEvent(final ClickEvent e) {
							if (Messagebox.ON_OK.equals(e.getName())) {
								shipSchedulerDao.deleteScheduleShip(scheduleShip_selected.getId());

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
		shift.setSelectedItem(null);
		operation.setValue("");
		user.setSelectedItem(null);
		usersecond.setSelectedItem(null);
		handswork.setValue(null);
		menwork.setValue(null);
		shiftdate.setValue(null);

		shift_Daily.setSelectedItem(null);
		operation_Daily.setValue("");
		user_Daily.setSelectedItem(null);
		handswork_Daily.setValue(null);
		menwork_Daily.setValue(null);
		shiftdate_Daily.setValue(null);
		notedetail.setValue("");
		noteshipdetail.setValue("");

		cranedetail.setValue("");
		timeworkdetail.setValue(null);
		finalvolumedetail.setValue(null);
	}

	/**
	 * Reset data on ship program tab
	 */
	private void resetDataInfoTabProgram() {
		ship_name.setSelectedItem(null);
		ship_volume.setValue(null);
		note.setValue(null);
		ship_arrival.setValue(null);
		ship_departure.setValue(null);

		ship_name_schedule.setSelectedItem(null);
		ship_volume_schedule.setValue(null);
		note_schedule.setValue(null);
		ship_arrival_schedule.setValue(null);
		ship_departure_schedule.setValue(null);

		listDetailScheduleShip.clear();
		sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

	}

	@Listen("onClick = #refreshDetailView")
	public void resfreshDetailView() {
		setInitialView();
	}

	@Listen("onClick = #modify_scheduleShipsDetailDaily_command")
	public void saveModifyShipDetailDaily() {

		detailScheduleShipSelected = sw_list_scheduleShip.getSelectedItem().getValue();

		if (detailScheduleShipSelected == null) {
			return;
		}

		detailScheduleShipSelected.setShiftdate(shiftdate_Daily.getValue());
		final Integer shift = Integer.parseInt(shift_Daily.getValue().toString());
		detailScheduleShipSelected.setShift(shift);
		detailScheduleShipSelected.setOperation(operation_Daily.getValue().toString());
		detailScheduleShipSelected.setIduser(((Person) user_Daily.getSelectedItem().getValue()).getId());
		detailScheduleShipSelected.setNotedetail(notedetail.getValue());
		if (usersecond_Daily.getSelectedItem() != null) {
			detailScheduleShipSelected.setIdseconduser(((Person) usersecond_Daily.getSelectedItem().getValue()).getId());
		}

		detailScheduleShipSelected.setHandswork(handswork_Daily.getValue());
		detailScheduleShipSelected.setMenwork(menwork_Daily.getValue());

		detailScheduleShipSelected.setCrane(cranedetail.getValue());
		detailScheduleShipSelected.setVolume(finalvolumedetail.getValue() == null ? null : finalvolumedetail.getValue()
				.doubleValue());
		detailScheduleShipSelected.setTimework(timeworkdetail.getValue());

		// update..
		shipSchedulerDao.updateDetailScheduleShip(detailScheduleShipSelected);

		// reset view
		searchDetailScheduleShipByDateShift();
		alertShiftDate_detail.setVisible(false);
		popup_detail_Daily.close();

	}

	@Listen("onChange = #searchArrivalDateShipFrom_detail, #searchArrivalDateShipTo_detail")
	public void searchDetailScheduleShipByDate() {
		searchDateShift.setValue(null);

		final Date dateFrom = searchArrivalDateShipFrom_detail.getValue();

		final Date dateTo = searchArrivalDateShipTo_detail.getValue();

		if (((dateFrom != null) && (dateTo != null)) && (dateTo.compareTo(dateFrom) >= 0)) {

			String text_search = full_text_search.getValue();

			if ((text_search != null) && text_search.equals("")) {
				text_search = null;
			}

			final Integer no_shift = getSelectedShift();
			final List<DetailScheduleShip> list = shipSchedulerDao.loadDetailScheduleShipByDateAndShipName(dateFrom, dateTo,
					text_search, no_shift);

			sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(list));

		} else {
			return;
		}

		setInfoShipProgram(new Timestamp(dateFrom.getTime()), new Timestamp(dateTo.getTime()), infoDetailShipProgram);
	}

	@Listen("onChange = #searchDateShift; onOK = #searchDateShift")
	public void searchDetailScheduleShipByDateShift() {

		if ((searchDateShift.getValue() == null) && (searchArrivalDateShipFrom_detail.getValue() != null)
				&& (searchArrivalDateShipTo_detail.getValue() != null)) {
			searchDetailScheduleShipByDate();
			return;
		}

		searchArrivalDateShipFrom_detail.setValue(null);
		searchArrivalDateShipTo_detail.setValue(null);

		if (searchDateShift.getValue() != null) {

			this.setScheduleShipListBox(searchDateShift.getValue());
		} else {

			final List<DetailScheduleShip> listDetailScheduleShipNullTimeShift = shipSchedulerDao
					.loadDetailScheduleWithShiftDateNull();

			if (listDetailScheduleShipNullTimeShift.size() != 0) {
				sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(listDetailScheduleShipNullTimeShift));
				infoDetailShipProgram.setValue("");
			}

		}

	}

	/**
	 * Search info about data ship for review
	 */
	@Listen("onChange = #searchWorkShip; onOK = #searchWorkShip")
	public void searchReviewShipData() {

		if (searchWorkShip.getValue() == null) {
			searchWorkShip.setValue(Calendar.getInstance().getTime());
		}

		final Date date_to_pick = searchWorkShip.getValue();

		final List<ReviewShipWork> list_review_work = scheduleDao.loadReviewShipWork(date_to_pick);
		sw_list_reviewWork.setModel(new ListModelList<ReviewShipWork>(list_review_work));

	}

	@Listen("onChange = #searchArrivalDateShipFrom, #searchArrivalDateShipTo")
	public void searchScheduleShipByDate() {
		List<ScheduleShip> list_scheduleShip = null;

		final Date dateFrom = searchArrivalDateShipFrom.getValue();

		final Date dateTo = searchArrivalDateShipTo.getValue();

		if ((dateFrom == null) && (dateTo == null)) {

			list_scheduleShip = shipSchedulerDao.loadAllScheduleShip();

			if ((shows_rows.getValue() != null) && (shows_rows.getValue() != 0)) {
				sw_list_scheduleShipProgram.setPageSize(shows_rows.getValue());
			} else {
				sw_list_scheduleShipProgram.setPageSize(10);
			}

			sw_list_scheduleShipProgram.setModel(new ListModelList<ScheduleShip>(list_scheduleShip));

			infoShipProgram.setValue("");

		} else if (((dateFrom != null) && (dateTo != null)) && (dateTo.compareTo(dateFrom) >= 0)) {
			final Timestamp dateFromTS = new Timestamp(dateFrom.getTime());

			final Timestamp dateToTS = new Timestamp(dateTo.getTime());

			list_scheduleShip = shipSchedulerDao.loadScheduleShipInDate(dateFromTS, dateToTS);

			// set label info program ship
			setInfoShipProgram(dateFromTS, dateToTS, infoShipProgram);

			if ((shows_rows.getValue() != null) && (shows_rows.getValue() != 0)) {
				sw_list_scheduleShipProgram.setPageSize(shows_rows.getValue());
			} else {
				sw_list_scheduleShipProgram.setPageSize(10);
			}

			sw_list_scheduleShipProgram.setModel(new ListModelList<ScheduleShip>(list_scheduleShip));

		}
	}

	@Listen("onChange = #select_shift")
	public void selectDetailShipByShift() {
		if (searchDateShift.getValue() != null) {
			searchDetailScheduleShipByDateShift();
		} else {
			searchDetailScheduleShipByDate();
		}
	}

	private void setInfoDetailShipProgram(final Date date, final String full_text_search,
			final List<DetailScheduleShip> list_DetailScheduleShip) {
		infoDetailShipProgram.setValue("");

		Integer volume = shipSchedulerDao.calculateVolumeByArrivalDateAndShipName(date, full_text_search, getSelectedShift());

		if (volume == null) {
			volume = 0;
		}

		Integer numberOfShip = shipSchedulerDao.calculateNumberOfShipByArrivalDateAndShipName(date, full_text_search,
				getSelectedShift());

		if (numberOfShip == null) {
			numberOfShip = 0;
		}

		Integer totalMenWork = 0;
		Integer totalHandsWork = 0;

		final ShipTotal shipTotal = shipSchedulerDao.calculateHandsWorkAndMensByArrivalDateAndShipName(date, full_text_search,
				getSelectedShift());

		if (shipTotal != null) {
			if (shipTotal.getTotalhands() != null) {
				totalHandsWork = shipTotal.getTotalhands();
			}

			if (shipTotal.getTotalmen() != null) {
				totalMenWork = shipTotal.getTotalmen();
			}
		}

		infoDetailShipProgram.setValue("Numero di Navi: " + numberOfShip + " - Totale volumi preventivati: " + volume
				+ " - Totale Mani: " + totalHandsWork + " - Totale Persone: " + totalMenWork);

	}

	private void setInfoShipProgram(final Timestamp dateFrom, final Timestamp dateTo, final Label infoShipProgram) {
		infoShipProgram.setValue("");

		Integer volume = shipSchedulerDao.calculateVolumeInDate(dateFrom, dateTo, getSelectedShift());

		if (volume == null) {
			volume = 0;
		}

		Integer numberOfShip = shipSchedulerDao.calculateNumberOfShipInDate(dateFrom, dateTo, getSelectedShift());

		if (numberOfShip == null) {
			numberOfShip = 0;
		}

		final ShipTotal handMenWorkTotal = shipSchedulerDao.calculateHandsWorkInDate(dateFrom, dateTo, getSelectedShift());

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

		infoShipProgram.setValue("Numero di Navi: " + numberOfShip + " - Totale volumi preventivati: " + volume
				+ " - Totale Mani: " + totalHands + " - Totale Persone: " + totalMen);

	}

	/**
	 * Show ships
	 */
	public void setInitialView() {

		grid_scheduleShip.setVisible(false);
		shipProgram.setVisible(false);
		dailyDetailShip.setVisible(true);
		reviewWorkShip.setVisible(false);

		full_text_search.setValue(null);

		select_shift.setSelectedIndex(0);
		searchArrivalDateShipFrom_detail.setValue(null);
		searchArrivalDateShipTo_detail.setValue(null);

		// set initial query for review work
		searchWorkShip.setValue(Calendar.getInstance().getTime());

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

		searchDateShift.setValue(now);

		this.setScheduleShipListBox(now);
	}

	/**
	 * Search by shit date
	 *
	 * @param date
	 */
	private void setScheduleShipListBox(final Date date) {
		List<DetailScheduleShip> list_DetailScheduleShip = null;

		final String _text = full_text_search.getValue();
		final Integer selectedShift = getSelectedShift();

		if ((_text != null) && !_text.equals("")) {

			list_DetailScheduleShip = shipSchedulerDao.loadDetailScheduleShipByShiftDateAndShipName(date, _text, selectedShift);
			setInfoDetailShipProgram(date, _text, list_DetailScheduleShip);

		} else {
			list_DetailScheduleShip = shipSchedulerDao.loadDetailScheduleShipByShiftDateAndShipName(date, null, selectedShift);
			setInfoDetailShipProgram(date, _text, list_DetailScheduleShip);

		}

		if ((shows_rows.getValue() != null) && (shows_rows.getValue() != 0)) {
			sw_list_scheduleShip.setPageSize(shows_rows.getValue());
		} else {
			sw_list_scheduleShip.setPageSize(10);
		}

		sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(list_DetailScheduleShip));

	}

	@Listen("onClick = #sw_addScheduleShipProgram")
	public void showAddScheduleShipView() {
		resetDataInfoTabProgram();

		grid_scheduleShip.setVisible(true);
	}

	@Listen("onClick = #shipNameProgram, #shipNameDetail")
	public void showDetailShip() {

		Integer idShip = 0;
		if (sw_list_scheduleShip.getSelectedItem() != null) {
			detailScheduleShipSelected = sw_list_scheduleShip.getSelectedItem().getValue();
			idShip = detailScheduleShipSelected.getId_ship();
		} else if (sw_list_scheduleShipProgram.getSelectedItem() != null) {
			scheduleShip_selected = sw_list_scheduleShipProgram.getSelectedItem().getValue();
			idShip = scheduleShip_selected.getIdship();
		}

		final List<Ship> detailShip = new ArrayList<Ship>();

		detailShip.add(shipDao.loadShip(idShip));

		popup_shipDetail.setModel(new ListModelList<Ship>(detailShip));

		// popup_ship.open(ref);

	}

	@Listen("onClick = #modify_scheduleShipsDetail_command")
	public void updateDetail() {
		if ((shift.getSelectedItem() == null) || (operation.getValue() == null) || (user.getSelectedItem() == null)
				|| (handswork.getValue() == null) || (menwork.getValue() == null) || (shiftdate.getValue() == null)) {

			final Map<String, String> params = new HashMap<String, String>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare i valori inseriti", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null,
					params);

			return;
		} else {
			detailScheduleShipSelected.setShiftdate(shiftdate.getValue());
			final Integer shift = Integer.parseInt(this.shift.getValue().toString());
			detailScheduleShipSelected.setShift(shift);
			detailScheduleShipSelected.setOperation(operation.getValue().toString());
			detailScheduleShipSelected.setIduser(((Person) user.getSelectedItem().getValue()).getId());

			if (usersecond.getSelectedItem() != null) {
				detailScheduleShipSelected.setIdseconduser(((Person) usersecond.getSelectedItem().getValue()).getId());
			}

			detailScheduleShipSelected.setHandswork(handswork.getValue());
			detailScheduleShipSelected.setMenwork(menwork.getValue());
			detailScheduleShipSelected.setNotedetail(noteshipdetail.getValue());

			shipSchedulerDao.updateDetailScheduleShip(detailScheduleShipSelected);
			// refresh list detail in popup
			addDetailScheduleShipProgram();

		}
	}
}
