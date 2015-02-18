package org.uario.seaworkengine.zkevent;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
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
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class ShipSchedulerComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long			serialVersionUID			= 1L;

	@Wire
	private Component					add_scheduleShips_command;

	@Wire
	Toolbarbutton						add_scheduleShipsDetail_command;

	@Wire
	public Component					dailyDetailShip;

	@Wire
	private Tab							detail_scheduleShip_tab;

	private List<DetailScheduleShip>	detailList;

	DetailScheduleShip					detailScheduleShip_selected	= null;

	@Wire
	public DetailScheduleShip			detailScheduleShipSelected;

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
	private List<DetailScheduleShip>	listDetailScheduleShip		= new ArrayList<DetailScheduleShip>();
	private final Logger				logger						= Logger.getLogger(UserDetailsComposer.class);
	@Wire
	private Intbox						menwork;

	@Wire
	public Intbox						menwork_Daily;

	@Wire
	private Component					modify_Scheduleships_command;

	@Wire
	Toolbarbutton						modify_scheduleShipsDetail_command;

	@Wire
	private Textbox						note;

	@Wire
	public Textbox						note_schedule;

	@Wire
	private Textbox						operation;

	@Wire
	public Textbox						operation_Daily;

	protected PersonDAO					personDao;

	@Wire
	private Popup						popu_detail;

	@Wire
	public Popup						popup_detail_Daily;

	@Wire
	private Popup						popup_scheduleShip;

	@Wire
	private Popup						popup_ship;

	@Wire
	private Listbox						popup_shipDetail;

	@Wire
	private Combobox					scheduler_type_selector;

	ScheduleShip						scheduleShip_selected		= null;

	@Wire
	private Datebox						searchArrivalDateShipFrom;

	@Wire
	private Datebox						searchArrivalDateShipTo;

	@Wire
	private Datebox						searchDateShift;

	@Wire
	private A							selecetedShipName;

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

	/*
	 * @Listen("onClick = #add_scheduleShips_command") public void
	 * addShipCommand() {
	 *
	 * if (this.ship_name.getSelectedItem() == null ||
	 * this.ship_volume.getValue() == null || this.ship_arrival.getValue() ==
	 * null || this.ship_departure.getValue() == null ||
	 * this.ship_arrival.getValue().after(this.ship_departure.getValue())) {
	 * final Map<String, String> params = new HashMap(); params.put("sclass",
	 * "mybutton Button"); final Messagebox.Button[] buttons = new
	 * Messagebox.Button[1]; buttons[0] = Messagebox.Button.OK;
	 *
	 * Messagebox.show("Controllare i valori inseriti in Informazioni Generali",
	 * "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
	 * return; } else {
	 *
	 * if (this.listDetailScheduleShip.size() == 0) { final Map<String, String>
	 * params = new HashMap(); params.put("sclass", "mybutton Button"); final
	 * Messagebox.Button[] buttons = new Messagebox.Button[1]; buttons[0] =
	 * Messagebox.Button.OK;
	 *
	 * Messagebox.show("Inserire dettagli di programmazione", "ATTENZIONE",
	 * buttons, null, Messagebox.EXCLAMATION, null, null, params); return; }
	 *
	 * final ScheduleShip shipSchedule = new ScheduleShip();
	 *
	 * final String shipName =
	 * this.ship_name.getSelectedItem().getValue().toString();
	 *
	 * final int idShip = this.shipDao.listIdShipByName(shipName).get(0);
	 *
	 * shipSchedule.setIdship(idShip);
	 * shipSchedule.setVolume(this.ship_volume.getValue());
	 * shipSchedule.setNote(this.note.getValue());
	 *
	 * shipSchedule.setArrivaldate(this.ship_arrival.getValue());
	 *
	 * shipSchedule.setDeparturedate(this.ship_departure.getValue());
	 *
	 * try { this.shipSchedulerDao.createScheduleShip(shipSchedule); } catch
	 * (final Exception e) { final Map<String, String> params = new HashMap();
	 * params.put("sclass", "mybutton Button"); final Messagebox.Button[]
	 * buttons = new Messagebox.Button[1]; buttons[0] = Messagebox.Button.OK;
	 *
	 * Messagebox.show("Nome nave ed ora arrivo già presenti", "ATTENZIONE",
	 * buttons, null, Messagebox.EXCLAMATION, null, null, params); return; }
	 *
	 *
	 * this.resetDataInfoTabProgram();
	 *
	 * final Map<String, String> params = new HashMap(); params.put("sclass",
	 * "mybutton Button"); final Messagebox.Button[] buttons = new
	 * Messagebox.Button[1]; buttons[0] = Messagebox.Button.OK;
	 *
	 * Messagebox.show("Programmazione aggiunta", "INFO", buttons, null,
	 * Messagebox.INFORMATION, null, null, params);
	 *
	 * if (this.searchDateShift.getValue() != null) { // set ship ListBox
	 * this.setScheduleShipListBox(this.searchDateShift.getValue()); } else {
	 * this.setScheduleShipListBox(); }
	 *
	 * this.grid_scheduleShip_details.setVisible(false);
	 * this.add_scheduleShips_command.setVisible(false);
	 * this.modify_Scheduleships_command.setVisible(false);
	 *
	 * } }
	 */

	protected IScheduleShip				shipSchedulerDao;

	@Wire
	private Intbox						shows_rows;

	@Wire
	private Listbox						sw_list_scheduleDetailShip;

	@Wire
	private Listbox						sw_list_scheduleShip;

	@Wire
	private Listbox						sw_list_scheduleShipProgram;

	@Wire
	private Combobox					user;

	@Wire
	public Combobox						user_Daily;

	protected PersonDAO					userPrep;

	@Wire
	private Combobox					usersecond;

	@Wire
	private Combobox					usersecond_Daily;

	@Listen("onClick = #sw_link_addDetailScheduleShipProgram")
	public void addDetailScheduleShipProgram() {

		this.modify_scheduleShipsDetail_command.setVisible(false);
		this.add_scheduleShipsDetail_command.setVisible(true);

		this.resetDataInfoTabDetail();

		this.scheduleShip_selected = (ScheduleShip) this.sw_list_scheduleShipProgram.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {

			this.listDetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

			for (final DetailScheduleShip detailScheduleShip : this.listDetailScheduleShip) {
				final Person userOperative = this.personDao.loadPerson(detailScheduleShip.getIduser());
				if (userOperative != null) {
					detailScheduleShip.setFirstname(userOperative.getFirstname() + " " + userOperative.getLastname());
				}
				final Person userSecondOperative = this.personDao.loadPerson(detailScheduleShip.getIdseconduser());
				if (userSecondOperative != null) {
					detailScheduleShip.setFirstnameSecondUser(userSecondOperative.getFirstname() + " " + userSecondOperative.getLastname());
				}
			}

			this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>(this.listDetailScheduleShip));
		}

	}

	@Listen("onClick = #addShipSchedule_command")
	public void addScheduleShipCommand() {
		if (this.ship_name_schedule.getSelectedItem() == null || this.ship_volume_schedule.getValue() == null
				|| this.ship_arrival_schedule.getValue() == null || this.ship_departure_schedule.getValue() == null
				|| this.ship_arrival_schedule.getValue().after(this.ship_departure_schedule.getValue())) {
			final Map<String, String> params = new HashMap();
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

		if (this.shift.getSelectedItem() == null || this.operation.getValue() == null || this.user.getSelectedItem() == null
				|| this.handswork.getValue() == null || this.menwork.getValue() == null || this.shiftdate.getValue() == null) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare i valori inseriti", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

			return;
		} else {
			final DetailScheduleShip detailScheduleShip = new DetailScheduleShip();

			final Person userOperative = (Person) this.user.getSelectedItem().getValue();

			if (this.usersecond.getSelectedItem() != null) {
				final Person secondUserOperative = (Person) this.usersecond.getSelectedItem().getValue();
				detailScheduleShip.setIdseconduser(secondUserOperative.getId());
				detailScheduleShip.setFirstnameSecondUser(secondUserOperative.getFirstname() + " " + secondUserOperative.getLastname());
			}

			detailScheduleShip.setShift((Integer) this.shift.getSelectedItem().getValue());
			detailScheduleShip.setOperation(this.operation.getValue());
			detailScheduleShip.setIduser(userOperative.getId());
			detailScheduleShip.setHandswork(this.handswork.getValue());
			detailScheduleShip.setMenwork(this.menwork.getValue());
			detailScheduleShip.setIdscheduleship(this.scheduleShip_selected.getId());
			detailScheduleShip.setShiftdate(this.shiftdate.getValue());

			detailScheduleShip.setFirstname(userOperative.getFirstname() + " " + userOperative.getLastname());

			this.shipSchedulerDao.createDetailScheduleShip(detailScheduleShip);

			/**
			 * this.listDetailScheduleShip.add(detailScheduleShip);
			 *
			 * final ListModelList<DetailScheduleShip> model = new
			 * ListModelList<DetailScheduleShip>(this.listDetailScheduleShip);
			 * model.setMultiple(false);
			 * this.sw_list_scheduleDetailShip.setModel(model);
			 */
			this.addDetailScheduleShipProgram();

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
		}
	}

	@Listen("onChange = #scheduler_type_selector")
	public void defineSchedulerView() {
		final String selected = this.scheduler_type_selector.getSelectedItem().getValue().toString();
		if (selected.equals("PROGRAMMAZIONE")) {
			this.grid_scheduleShip.setVisible(false);
			this.shipProgram.setVisible(true);
			this.dailyDetailShip.setVisible(false);
			this.searchScheduleShipByDate();
		} else if (selected.equals("DETTAGLIO GIORNALIERO")) {
			this.grid_scheduleShip.setVisible(false);
			this.shipProgram.setVisible(false);
			this.dailyDetailShip.setVisible(true);
			this.searchDetailScheduleShipByDateShift();
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

	}

	private void deleteDetailship() {
		if (this.detailScheduleShip_selected == null) {
			return;
		}
		this.shipSchedulerDao.deleteDetailScheduleShip(this.detailScheduleShip_selected.getId());

		if (this.searchDateShift.getValue() == null) {
			this.setScheduleShipListBox();
		} else {
			this.searchDetailScheduleShipByDateShift();
		}

	}

	@Listen("onClick = #sw_link_deleteDetailship")
	public void deleteDetailshipInListDetail() {

		this.detailScheduleShipSelected = this.sw_list_scheduleDetailShip.getSelectedItem().getValue();
		if (this.detailScheduleShipSelected != null && this.scheduleShip_selected != null) {

			this.shipSchedulerDao.deleteDetailScheduleShip(this.detailScheduleShipSelected.getId());

			this.listDetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

			for (final DetailScheduleShip detailScheduleShip : this.listDetailScheduleShip) {
				final Person userOperative = this.personDao.loadPerson(detailScheduleShip.getIduser());
				if (userOperative != null) {
					detailScheduleShip.setFirstname(userOperative.getFirstname() + " " + userOperative.getLastname());
				}
			}

			this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>(this.listDetailScheduleShip));
		}

	}

	private void deleteScheduleShipCommand() {

		try {
			if (this.scheduleShip_selected == null) {
				return;
			}

			this.shipSchedulerDao.deleteScheduleShip(this.scheduleShip_selected.getId());

			// update list
			this.setScheduleShipListBox();

		} catch (final Exception e) {

			this.logger.error("Error removing programmation. " + e.getMessage());

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Non è possibile eliminare questa programmazione.\nControlla che non ci siano azioni legate a questa programmazione.",
					"ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

		}

	}

	@Override
	public void doFinally() throws Exception {

		// SHOW SHIFT CONFIGURATOR
		this.getSelf().addEventListener(ZkEventsTag.onShowShipScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {
				// get the DAOs
				ShipSchedulerComposer.this.shipSchedulerDao = (IScheduleShip) SpringUtil.getBean(BeansTag.SCHEDULE_SHIP_DAO);
				ShipSchedulerComposer.this.shipDao = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);
				ShipSchedulerComposer.this.personDao = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);

				final ListModel<Ship> modelComboBox_ShipName = new ListModelList<Ship>(ShipSchedulerComposer.this.shipDao.listAllShip(null));

				// add item in combobox ship name
				if (modelComboBox_ShipName.getSize() == 0) {
					Messagebox
					.show("Inserire almeno una nave prima di procedere alla programmazione!", "INFO", Messagebox.OK, Messagebox.INFORMATION);
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

				final ListModel<Person> modelComboBox_UserSecond = new ListModelList<Person>(ShipSchedulerComposer.this.personDao
						.listOperativePerson());
				ShipSchedulerComposer.this.usersecond.setModel(modelComboBox_UserSecond);

				final ListModel<Person> modelComboBox_UserDaily = new ListModelList<Person>(ShipSchedulerComposer.this.personDao
						.listOperativePerson());
				ShipSchedulerComposer.this.user_Daily.setModel(modelComboBox_UserDaily);

				final ListModel<Person> modelComboBox_UserSecondDaily = new ListModelList<Person>(ShipSchedulerComposer.this.personDao
						.listOperativePerson());
				ShipSchedulerComposer.this.usersecond_Daily.setModel(modelComboBox_UserSecondDaily);

				ShipSchedulerComposer.this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

				ShipSchedulerComposer.this.setInitialView();

				ShipSchedulerComposer.this.scheduler_type_selector.setSelectedIndex(1);

			}

		});

	}

	@Listen("onClick = #sw_link_modifyDetailship")
	public void modifyDetailShip() {
		this.detailScheduleShipSelected = this.sw_list_scheduleDetailShip.getSelectedItem().getValue();
		if (this.detailScheduleShipSelected != null && this.scheduleShip_selected != null) {
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
			if (this.ship_name.getSelectedItem() == null || this.ship_volume.getValue() == null || this.ship_arrival == null
					|| this.ship_departure.getValue() == null || this.ship_arrival.getValue().after(this.ship_departure.getValue())) {
				final Map<String, String> params = new HashMap();
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

	@Listen("onClick = #modify_Scheduleships_command")
	public void mofifyShipsCommand() {

		if (this.sw_list_scheduleShip.getSelectedItem().getValue() != null) {
			this.detailScheduleShip_selected = this.sw_list_scheduleShip.getSelectedItem().getValue();

			// take schedule ship
			this.scheduleShip_selected = this.shipSchedulerDao.loadScheduleShip(this.detailScheduleShip_selected.getIdscheduleship());

			if (this.scheduleShip_selected == null) {
				return;
			}

			if (this.ship_name.getSelectedItem() == null || this.ship_volume.getValue() == null || this.ship_arrival == null
					|| this.ship_departure.getValue() == null || this.ship_arrival.getValue().after(this.ship_departure.getValue())) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Controllare i valori inseriti..", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
				return;
			} else {

				if (this.listDetailScheduleShip.size() == 0) {
					final Map<String, String> params = new HashMap();
					params.put("sclass", "mybutton Button");
					final Messagebox.Button[] buttons = new Messagebox.Button[1];
					buttons[0] = Messagebox.Button.OK;

					Messagebox.show("Inserire dettagli di programmazione", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
					return;
				}

				final int idShip = ((Ship) this.ship_name.getSelectedItem().getValue()).getId();

				this.scheduleShip_selected.setIdship(idShip);
				this.scheduleShip_selected.setVolume(this.ship_volume.getValue());
				this.scheduleShip_selected.setNote(this.note.getValue());

				this.scheduleShip_selected.setArrivaldate(this.ship_arrival.getValue());

				this.scheduleShip_selected.setDeparturedate(this.ship_departure.getValue());

				// update
				this.shipSchedulerDao.updateScheduleShip(this.scheduleShip_selected);

				this.shipSchedulerDao.deteleDetailSchedueleShipByIdSchedule(this.scheduleShip_selected.getId());

				for (final DetailScheduleShip item : this.listDetailScheduleShip) {
					item.setIdscheduleship(this.scheduleShip_selected.getId());
					this.shipSchedulerDao.createDetailScheduleShip(item);
				}

				// update list
				if (this.searchDateShift.getValue() != null) {
					// set ship ListBox
					this.setScheduleShipListBox(this.searchDateShift.getValue());
				} else {
					this.setScheduleShipListBox();
				}

				this.grid_scheduleShip_details.setVisible(false);

				this.resetDataInfoTabProgram();

				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Dati programmazione aggiornati", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

			}
		}

	}

	@Listen("onClick = #sw_link_modifyscheduleship")
	public void openModifyDetailDailyPopup() {

		this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (this.detailScheduleShipSelected != null) {

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

			this.handswork_Daily.setValue(this.detailScheduleShipSelected.getHandswork());
			this.menwork_Daily.setValue(this.detailScheduleShipSelected.getMenwork());

		}

		// this.sw_list_scheduleDetailShip.setVisible(false);

		// final popup_detail

		/*
		 * if ((this.sw_list_scheduleShip.getSelectedItem() == null) ||
		 * (this.sw_list_scheduleShip.getSelectedItem().getValue() == null) ||
		 * !(this.sw_list_scheduleShip.getSelectedItem().getValue() instanceof
		 * DetailScheduleShip)) { return; }
		 *
		 * // command this.add_scheduleShips_command.setVisible(false);
		 * this.modify_Scheduleships_command.setVisible(true);
		 *
		 * // configure tab this.grid_scheduleShip_details.setVisible(true);
		 *
		 * // set detail to selection
		 * this.detail_scheduleShip_tab.getTabbox().setSelectedTab
		 * (this.detail_scheduleShip_tab);
		 *
		 * this.detailScheduleShip_selected =
		 * this.sw_list_scheduleShip.getSelectedItem().getValue();
		 *
		 * // take schedule ship this.scheduleShip_selected =
		 * this.shipSchedulerDao
		 * .loadScheduleShip(this.detailScheduleShip_selected
		 * .getIdscheduleship());
		 *
		 * // set label with name ship
		 * this.selecetedShipName.setLabel(this.scheduleShip_selected
		 * .getName());
		 *
		 * // general details
		 * this.defineScheduleShipDetailsView(this.scheduleShip_selected);
		 *
		 * final List<DetailScheduleShip> detailList =
		 * this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule
		 * (this.scheduleShip_selected.getId());
		 *
		 * for (final DetailScheduleShip detailScheduleShip : detailList) {
		 * final Person userOperative =
		 * this.personDao.loadPerson(detailScheduleShip.getIduser()); if
		 * (userOperative != null) {
		 * detailScheduleShip.setFirstname(userOperative.getFirstname() + " " +
		 * userOperative.getLastname()); } }
		 *
		 * this.listDetailScheduleShip.clear(); this.listDetailScheduleShip =
		 * new ArrayList<DetailScheduleShip>(detailList);
		 *
		 * final ListModelList<DetailScheduleShip> detailModelList = new
		 * ListModelList<DetailScheduleShip>(detailList);
		 *
		 * this.sw_list_scheduleDetailShip.setModel(detailModelList);
		 */
	}

	private void refreshListShip() {

		this.full_text_search.setValue(null);

		// set user listbox
		this.setScheduleShipListBox();
	}

	@Listen("onClick = #sw_link_deleteship")
	public void removeItem() {

		this.detailScheduleShip_selected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		// take schedule ship
		this.scheduleShip_selected = this.shipSchedulerDao.loadScheduleShip(this.detailScheduleShip_selected.getIdscheduleship());

		final Map<String, String> params = new HashMap();
		params.put("sclass", "mybutton Button");

		final Messagebox.Button[] buttons = new Messagebox.Button[2];
		buttons[0] = Messagebox.Button.OK;
		buttons[1] = Messagebox.Button.CANCEL;

		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.EXCLAMATION, null,
				new EventListener() {
			@Override
			public void onEvent(final Event e) {
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

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");

			final Messagebox.Button[] buttons = new Messagebox.Button[2];
			buttons[0] = Messagebox.Button.OK;
			buttons[1] = Messagebox.Button.CANCEL;

			Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.INFORMATION, null,
					new EventListener() {
				@Override
				public void onEvent(final Event e) {
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
		this.shift.setSelectedItem(null);
		this.operation.setValue("");
		this.user.setSelectedItem(null);
		this.usersecond.setSelectedItem(null);
		this.handswork.setValue(null);
		this.menwork.setValue(null);
		this.shiftdate.setValue(null);

		this.shift_Daily.setSelectedItem(null);
		this.operation_Daily.setValue("");
		this.user_Daily.setSelectedItem(null);
		this.handswork_Daily.setValue(null);
		this.menwork_Daily.setValue(null);
		this.shiftdate_Daily.setValue(null);
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

	@Listen("onClick = #modify_scheduleShipsDetailDaily_command")
	public void saveModifyShipDetailDaily() {

		this.detailScheduleShipSelected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (this.detailScheduleShipSelected != null) {
			if (this.shift_Daily.getSelectedItem() == null || this.operation_Daily.getValue() == null || this.user_Daily.getSelectedItem() == null
					|| this.handswork_Daily.getValue() == null || this.menwork_Daily.getValue() == null || this.shiftdate_Daily.getValue() == null) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Controllare i valori inseriti", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);

				return;
			} else {
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

				this.shipSchedulerDao.updateDetailScheduleShip(this.detailScheduleShipSelected);
				this.searchDetailScheduleShipByDateShift();

				this.popup_detail_Daily.close();
			}
		}

	}

	@Listen("onChange = #searchDateShift")
	public void searchDetailScheduleShipByDateShift() {
		if (this.searchDateShift.getValue() != null) {
			this.setScheduleShipListBox(this.searchDateShift.getValue());
		} else {

			final List<DetailScheduleShip> listDetailScheduleShipNullTimeShift = this.shipSchedulerDao.loadDetailScheduleWithShiftDateNull();

			if (listDetailScheduleShipNullTimeShift.size() != 0) {
				this.sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(listDetailScheduleShipNullTimeShift));
			}

		}

	}

	@Listen("onClick = #sw_searchScheduleShipProgram")
	public void searchScheduleShipByDate() {
		List<ScheduleShip> list_scheduleShip = null;

		final Date dateFrom = this.searchArrivalDateShipFrom.getValue();

		final Date dateTo = this.searchArrivalDateShipTo.getValue();

		if (dateFrom == null && dateTo == null) {

			list_scheduleShip = this.shipSchedulerDao.loadAllScheduleShip();

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.sw_list_scheduleShipProgram.setPageSize(this.shows_rows.getValue());
			} else {
				this.sw_list_scheduleShipProgram.setPageSize(10);
			}

			this.sw_list_scheduleShipProgram.setModel(new ListModelList<ScheduleShip>(list_scheduleShip));
		} else if ((dateFrom != null && dateTo != null) && (dateTo.compareTo(dateFrom) >= 0)) {
			final Timestamp dateFromTS = new Timestamp(dateFrom.getTime());

			final Timestamp dateToTS = new Timestamp(dateTo.getTime());

			list_scheduleShip = this.shipSchedulerDao.loadScheduleShipInDate(dateFromTS, dateToTS);

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.sw_list_scheduleShipProgram.setPageSize(this.shows_rows.getValue());
			} else {
				this.sw_list_scheduleShipProgram.setPageSize(10);
			}

			this.sw_list_scheduleShipProgram.setModel(new ListModelList<ScheduleShip>(list_scheduleShip));
		} else {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Verificare i valori inseriti.", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		}

	}

	/**
	 * Show ships
	 */
	public void setInitialView() {

		this.grid_scheduleShip.setVisible(false);
		this.shipProgram.setVisible(false);
		this.dailyDetailShip.setVisible(true);

		this.full_text_search.setValue(null);

		// set ship listbox
		this.setScheduleShipListBox();

		// initial view
		// this.grid_scheduleShip_details.setVisible(false);

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

	private void setScheduleShipListBox(final Date date) {
		List<DetailScheduleShip> list_DetailScheduleShip = null;

		if ((this.full_text_search.getValue() != null) && !this.full_text_search.getValue().equals("")) {

			list_DetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByShiftDateAndShipName(date, this.full_text_search.getValue());
		} else {
			list_DetailScheduleShip = this.shipSchedulerDao.loadDetailScheduleShipByShiftDateAndShipName(date, null);

		}

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_scheduleShip.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_scheduleShip.setPageSize(10);
		}

		this.sw_list_scheduleShip.setModel(new ListModelList<DetailScheduleShip>(list_DetailScheduleShip));

	}

	@Listen("onClick = #sw_addSchedulerShip")
	public void showAddScheduleShipPanel() {

		// command
		this.add_scheduleShips_command.setVisible(true);
		this.modify_Scheduleships_command.setVisible(false);

		// configure tab
		// this.grid_scheduleShip_details.setVisible(true);

		// set detail to selection
		this.detail_scheduleShip_tab.getTabbox().setSelectedTab(this.detail_scheduleShip_tab);

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
			idShip = this.scheduleShip_selected.getId();
		}

		final List<Ship> detailShip = new ArrayList<Ship>();

		detailShip.add(this.shipDao.loadShip(idShip));

		this.popup_shipDetail.setModel(new ListModelList<Ship>(detailShip));

		// popup_ship.open(ref);

	}

	@Listen("onClick = #modify_scheduleShipsDetail_command")
	public void updateDetail() {
		if (this.shift.getSelectedItem() == null || this.operation.getValue() == null || this.user.getSelectedItem() == null
				|| this.handswork.getValue() == null || this.menwork.getValue() == null || this.shiftdate.getValue() == null) {
			final Map<String, String> params = new HashMap();
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

			this.shipSchedulerDao.updateDetailScheduleShip(this.detailScheduleShipSelected);
			// refresh list detail in popup
			this.addDetailScheduleShipProgram();

		}
	}
}
