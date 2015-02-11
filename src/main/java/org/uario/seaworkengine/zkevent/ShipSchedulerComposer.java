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
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

public class ShipSchedulerComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long			serialVersionUID			= 1L;

	@Wire
	private Component					add_scheduleShips_command;

	@Wire
	private Tab							detail_scheduleShip_tab;

	DetailScheduleShip					detailScheduleShip_selected	= null;

	@Wire
	private Textbox						full_text_search;

	@Wire
	private Component					grid_scheduleShip_details;

	@Wire
	private Intbox						handswork;

	private List<DetailScheduleShip>	listDetailScheduleShip		= new ArrayList<DetailScheduleShip>();

	private final Logger				logger						= Logger.getLogger(UserDetailsComposer.class);

	@Wire
	private Intbox						menwork;

	@Wire
	private Component					modify_Scheduleships_command;

	@Wire
	private Textbox						note;

	@Wire
	private Textbox						operation;

	protected PersonDAO					personDao;

	@Wire
	private Popup						popup_ship;

	@Wire
	private Listbox						popup_shipDetail;

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
	private Datebox						shiftdate;

	@Wire
	private Datebox						ship_arrival;

	@Wire
	private Datebox						ship_departure;

	@Wire
	private Combobox					ship_name;

	@Wire
	private Doublebox					ship_volume;

	protected IShip						shipDao;

	protected IScheduleShip				shipSchedulerDao;

	@Wire
	private Intbox						shows_rows;

	@Wire
	private Listbox						sw_list_scheduleDetailShip;

	@Wire
	private Listbox						sw_list_scheduleShip;

	@Wire
	private Combobox					user;

	protected PersonDAO					userPrep;

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

			detailScheduleShip.setShift((Integer) this.shift.getSelectedItem().getValue());
			detailScheduleShip.setOperation(this.operation.getValue());
			detailScheduleShip.setIduser(userOperative.getId());
			detailScheduleShip.setHandswork(this.handswork.getValue());
			detailScheduleShip.setMenwork(this.menwork.getValue());

			detailScheduleShip.setShiftdate(this.shiftdate.getValue());

			detailScheduleShip.setFirstname(userOperative.getFirstname() + " " + userOperative.getLastname());
			this.listDetailScheduleShip.add(detailScheduleShip);

			final ListModelList<DetailScheduleShip> model = new ListModelList<DetailScheduleShip>(this.listDetailScheduleShip);
			model.setMultiple(false);
			this.sw_list_scheduleDetailShip.setModel(model);

			this.resetDataInfoTabDetail();
		}
	}

	@Listen("onClick = #add_scheduleShips_command")
	public void addShipCommand() {

		if (this.ship_name.getSelectedItem() == null || this.ship_volume.getValue() == null || this.ship_arrival.getValue() == null
				|| this.ship_departure.getValue() == null || this.ship_arrival.getValue().after(this.ship_departure.getValue())) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Controllare i valori inseriti in Informazioni Generali", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null,
					null, params);
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

			final ScheduleShip shipSchedule = new ScheduleShip();

			final String shipName = this.ship_name.getSelectedItem().getValue().toString();

			final int idShip = this.shipDao.listIdShipByName(shipName).get(0);

			shipSchedule.setIdship(idShip);
			shipSchedule.setVolume(this.ship_volume.getValue());
			shipSchedule.setNote(this.note.getValue());

			shipSchedule.setArrivaldate(this.ship_arrival.getValue());

			shipSchedule.setDeparturedate(this.ship_departure.getValue());

			try {
				this.shipSchedulerDao.createScheduleShip(shipSchedule);
			} catch (final Exception e) {
				final Map<String, String> params = new HashMap();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Nome nave ed ora arrivo già presenti", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
				return;
			}

			final ScheduleShip myScheduleShip = this.shipSchedulerDao.loadScheduleShipByIdShipAndArrivalDate(shipSchedule.getIdship(),
					shipSchedule.getArrivaldate());

			for (final DetailScheduleShip item : this.listDetailScheduleShip) {
				item.setIdscheduleship(myScheduleShip.getId());
				this.shipSchedulerDao.createDetailScheduleShip(item);
			}

			// reset data info
			this.resetDataInfoTabProgram();

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Programmazione aggiunta", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

			if (this.searchDateShift.getValue() != null) {
				// set ship ListBox
				this.setScheduleShipListBox(this.searchDateShift.getValue());
			} else {
				this.setScheduleShipListBox();
			}

			this.grid_scheduleShip_details.setVisible(false);
			this.add_scheduleShips_command.setVisible(false);
			this.modify_Scheduleships_command.setVisible(false);

		}
	}

	@Listen("onClick = #closeNoSave")
	public void closeNoSave() {
		this.resetDataInfoTabProgram();
	}

	@Listen("onClick = #sw_link_modifyscheduleship")
	public void defineModifyView() {

		if ((this.sw_list_scheduleShip.getSelectedItem() == null) || (this.sw_list_scheduleShip.getSelectedItem().getValue() == null)
				|| !(this.sw_list_scheduleShip.getSelectedItem().getValue() instanceof DetailScheduleShip)) {
			return;
		}

		// command
		this.add_scheduleShips_command.setVisible(false);
		this.modify_Scheduleships_command.setVisible(true);

		// configure tab
		this.grid_scheduleShip_details.setVisible(true);

		// set detail to selection
		this.detail_scheduleShip_tab.getTabbox().setSelectedTab(this.detail_scheduleShip_tab);

		this.detailScheduleShip_selected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		// take schedule ship
		this.scheduleShip_selected = this.shipSchedulerDao.loadScheduleShip(this.detailScheduleShip_selected.getIdscheduleship());

		// set label with name ship
		this.selecetedShipName.setLabel(this.scheduleShip_selected.getName());

		// general details
		this.defineScheduleShipDetailsView(this.scheduleShip_selected);

		final List<DetailScheduleShip> detailList = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

		for (final DetailScheduleShip detailScheduleShip : detailList) {
			final Person userOperative = this.personDao.loadPerson(detailScheduleShip.getIduser());
			if (userOperative != null) {
				detailScheduleShip.setFirstname(userOperative.getFirstname() + " " + userOperative.getLastname());
			}
		}

		this.listDetailScheduleShip.clear();
		this.listDetailScheduleShip = new ArrayList<DetailScheduleShip>(detailList);

		final ListModelList<DetailScheduleShip> detailModelList = new ListModelList<DetailScheduleShip>(detailList);

		this.sw_list_scheduleDetailShip.setModel(detailModelList);

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

		for (final Listitem itm : this.sw_list_scheduleDetailShip.getSelectedItems()) {
			final DetailScheduleShip detail_item = itm.getValue();
			this.listDetailScheduleShip.remove(detail_item);
		}

		final ListModelList<DetailScheduleShip> model = new ListModelList<DetailScheduleShip>(this.listDetailScheduleShip);
		model.setMultiple(false);
		this.sw_list_scheduleDetailShip.setModel(model);

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

				// add item in combobox shift
				final List<Integer> shiftList = new ArrayList<Integer>();
				shiftList.add(1);
				shiftList.add(2);
				shiftList.add(3);
				shiftList.add(4);

				final ListModel<Integer> modelComboBox_Shift = new ListModelList<Integer>(shiftList);

				ShipSchedulerComposer.this.shift.setModel(modelComboBox_Shift);

				// add item operative users in combobox user
				final ListModel<Person> modelComboBox_User = new ListModelList<Person>(ShipSchedulerComposer.this.personDao.listOperativePerson());
				ShipSchedulerComposer.this.user.setModel(modelComboBox_User);

				ShipSchedulerComposer.this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

				ShipSchedulerComposer.this.setInitialView();

			}

		});

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

	/**
	 * Reset data on ship detail program tab
	 */
	private void resetDataInfoTabDetail() {
		this.shift.setSelectedItem(null);
		this.operation.setValue("");
		this.user.setSelectedItem(null);
		this.handswork.setValue(null);
		this.menwork.setValue(null);
		this.shiftdate.setValue(null);
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

		this.listDetailScheduleShip.clear();
		this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

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

	@Listen("onClick = #sw_searchScheduleShip")
	public void searchScheduleShipByDate() {
		List<ScheduleShip> list_scheduleShip = null;

		final Date dateFrom = this.searchArrivalDateShipFrom.getValue();

		final Date dateTo = this.searchArrivalDateShipTo.getValue();

		if ((dateFrom != null && dateTo != null) && (dateTo.compareTo(dateFrom) >= 0)) {
			final Timestamp dateFromTS = new Timestamp(dateFrom.getTime());

			final Timestamp dateToTS = new Timestamp(dateTo.getTime());

			list_scheduleShip = this.shipSchedulerDao.loadScheduleShipInDate(dateFromTS, dateToTS);

			if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
				this.sw_list_scheduleShip.setPageSize(this.shows_rows.getValue());
			} else {
				this.sw_list_scheduleShip.setPageSize(10);
			}

			this.sw_list_scheduleShip.setModel(new ListModelList<ScheduleShip>(list_scheduleShip));
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

		this.full_text_search.setValue(null);

		// set ship listbox
		this.setScheduleShipListBox();

		// initial view
		this.grid_scheduleShip_details.setVisible(false);

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
		this.grid_scheduleShip_details.setVisible(true);

		// set detail to selection
		this.detail_scheduleShip_tab.getTabbox().setSelectedTab(this.detail_scheduleShip_tab);

	}

	@Listen("onClick = #shipName")
	public void showDetailShip() {
		this.detailScheduleShip_selected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		this.shipDao.loadShip(this.detailScheduleShip_selected.getId_ship());

		final List<Ship> detailShip = new ArrayList<Ship>();

		detailShip.add(this.shipDao.loadShip(this.detailScheduleShip_selected.getId_ship()));

		this.popup_shipDetail.setModel(new ListModelList<Ship>(detailShip));

		// popup_ship.open(ref);

	}
}
