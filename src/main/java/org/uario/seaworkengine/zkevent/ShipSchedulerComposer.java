package org.uario.seaworkengine.zkevent;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
	private static final long			serialVersionUID		= 1L;

	@Wire
	private Component					add_scheduleShips_command;

	@Wire
	private Tab							detail_scheduleShip_tab;

	@Wire
	private Textbox						full_text_search;

	@Wire
	private Component					grid_scheduleShip_details;

	@Wire
	private Intbox						handswork;

	private List<DetailScheduleShip>	listDetailScheduleShip	= new ArrayList<DetailScheduleShip>();

	private final Logger				logger					= Logger.getLogger(UserDetailsComposer.class);

	@Wire
	private Component					modify_Scheduleships_command;

	@Wire
	private Textbox						note;

	@Wire
	private Textbox						operation;

	protected PersonDAO					personDao;

	@Wire
	private Popup						popup_detailscheduleship;

	@Wire
	private Listbox						popup_sw_list_scheduleDetailShip;

	ScheduleShip						scheduleShip_selected	= null;

	@Wire
	private Combobox					shift;

	@Wire
	private Datebox						ship_arrival;

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
				|| this.handswork.getValue() == null) {
			Messagebox.show("Controllare i valori inseriti.", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		} else {
			final DetailScheduleShip detailScheduleShip = new DetailScheduleShip();

			final Person userOperative = (Person) this.user.getSelectedItem().getValue();

			detailScheduleShip.setShift((Integer) this.shift.getSelectedItem().getValue());
			detailScheduleShip.setOperation(this.operation.getValue());
			detailScheduleShip.setIduser(userOperative.getId());
			detailScheduleShip.setHandswork(this.handswork.getValue());
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

		if (this.ship_name.getSelectedItem() == null || this.ship_volume.getValue() == null || this.note.getValue() == ""
				|| this.ship_arrival.getValue() == null) {
			Messagebox.show("Controllare i valori inseriti in Informazioni Generali.", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		} else {

			final ScheduleShip shipSchedule = new ScheduleShip();

			final String shipName = this.ship_name.getSelectedItem().getValue().toString();

			final int idShip = this.shipDao.listIdShipByName(shipName).get(0);

			shipSchedule.setIdship(idShip);
			shipSchedule.setVolume(this.ship_volume.getValue());
			shipSchedule.setNote(this.note.getValue());

			// set arrival date and time
			final Date arival = this.ship_arrival.getValue();

			final Date arrivale_truncate = DateUtils.truncate(arival, Calendar.MINUTE);
			final Timestamp timestamp = new Timestamp(arrivale_truncate.getTime());
			shipSchedule.setArrivaldate(timestamp);

			try {
				this.shipSchedulerDao.createScheduleShip(shipSchedule);
			} catch (final Exception e) {
				Messagebox.show("Nome nave ed ora arrivo già presenti", "INFO", Messagebox.OK, Messagebox.INFORMATION);
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

			Messagebox.show("Programmazione aggiunta", "INFO", Messagebox.OK, Messagebox.INFORMATION);

			// set ship ListBox
			this.setScheduleShipListBox();

			this.grid_scheduleShip_details.setVisible(false);
			this.add_scheduleShips_command.setVisible(false);
			this.modify_Scheduleships_command.setVisible(false);

		}
	}

	@Listen("onClick = #closeNoSave")
	public void closeNoSave() {
		this.resetDataInfoTabProgram();
	}

	@Listen("onClick = #sw_link_deleteship")
	public void defineDeleteView() {

		// take ship
		this.scheduleShip_selected = this.sw_list_scheduleShip.getSelectedItem().getValue();

	}

	@Listen("onClick = #sw_link_modifyscheduleship")
	public void defineModifyView() {

		if ((this.sw_list_scheduleShip.getSelectedItem() == null) || (this.sw_list_scheduleShip.getSelectedItem().getValue() == null)
				|| !(this.sw_list_scheduleShip.getSelectedItem().getValue() instanceof ScheduleShip)) {
			return;
		}

		// command
		this.add_scheduleShips_command.setVisible(false);
		this.modify_Scheduleships_command.setVisible(true);

		// configure tab
		this.grid_scheduleShip_details.setVisible(true);

		// set detail to selection
		this.detail_scheduleShip_tab.getTabbox().setSelectedTab(this.detail_scheduleShip_tab);

		// take schedule ship
		this.scheduleShip_selected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		// get the last ship from database
		this.scheduleShip_selected = this.shipSchedulerDao.loadScheduleShip(this.scheduleShip_selected.getId());

		// general details
		this.defineScheduleShipDetailsView(this.scheduleShip_selected);

		final List<DetailScheduleShip> detailList = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

		for (final DetailScheduleShip detailScheduleShip : detailList) {
			final Person userOperative = this.personDao.loadPerson(detailScheduleShip.getIduser());
			detailScheduleShip.setFirstname(userOperative.getFirstname() + " " + userOperative.getLastname());
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

		final List<Comboitem> listItem = this.ship_name.getItems();

		if (ship != null) {
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
		final Timestamp arrivalDate = scheduleShip_selected.getArrivaldate();
		this.ship_arrival.setValue(arrivalDate);

	}

	@Listen("onClick = #sw_link_deleteDetailship")
	public void deleteDetailship() {

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

			Messagebox.show("Non è possibile eliminare questa programmazione.\nControlla che non ci siano azioni legate a questa programmazione.",
					"INFO", Messagebox.OK, Messagebox.ERROR);

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
		if (this.scheduleShip_selected == null) {
			return;
		}

		if (this.ship_name.getSelectedItem() == null || this.ship_volume.getValue() == null || this.note.getValue() == ""
				|| this.ship_arrival == null) {
			Messagebox.show("Controllare i valori inseriti.", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		} else {

			final int idShip = ((Ship) this.ship_name.getSelectedItem().getValue()).getId();

			this.scheduleShip_selected.setIdship(idShip);
			this.scheduleShip_selected.setVolume(this.ship_volume.getValue());
			this.scheduleShip_selected.setNote(this.note.getValue());

			// set arrival date and time
			// set arrival date and time
			final Date arival = this.ship_arrival.getValue();

			final Date arrivale_truncate = DateUtils.truncate(arival, Calendar.MINUTE);
			final Timestamp timestamp = new Timestamp(arrivale_truncate.getTime());
			this.scheduleShip_selected.setArrivaldate(timestamp);

			// update
			this.shipSchedulerDao.updateScheduleShip(this.scheduleShip_selected);

			this.shipSchedulerDao.deteleDetailSchedueleShipByIdSchedule(this.scheduleShip_selected.getId());

			for (final DetailScheduleShip item : this.listDetailScheduleShip) {
				item.setIdscheduleship(this.scheduleShip_selected.getId());
				this.shipSchedulerDao.createDetailScheduleShip(item);
			}

			// update list
			this.setScheduleShipListBox();

			this.grid_scheduleShip_details.setVisible(false);

			this.resetDataInfoTabProgram();

			Messagebox.show("Dati Programmazione aggiornati", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		}

	}

	@Listen("onClick = #sw_refresh_list;")
	public void refreshListShip() {

		this.full_text_search.setValue(null);

		// set user listbox
		this.setScheduleShipListBox();
	}

	@Listen("onClick = #sw_link_deleteship")
	public void removeItem() {
		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					@Override
					public void onEvent(final Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							ShipSchedulerComposer.this.deleteScheduleShipCommand();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});

	}

	/**
	 * Reset data on ship detail program tab
	 */
	private void resetDataInfoTabDetail() {
		this.shift.setSelectedItem(null);
		this.operation.setValue("");
		this.user.setSelectedItem(null);
		this.handswork.setValue(null);
	}

	/**
	 * Reset data on ship program tab
	 */
	private void resetDataInfoTabProgram() {
		this.ship_name.setSelectedItem(null);
		this.ship_volume.setValue(null);
		this.note.setValue(null);
		this.ship_arrival.setValue(null);

		this.listDetailScheduleShip.clear();
		this.sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>());

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

		List<ScheduleShip> list_scheduleShip = null;

		if ((this.full_text_search.getValue() != null) && !this.full_text_search.getValue().equals("")) {
			list_scheduleShip = this.shipSchedulerDao.selectAllScheduleShipFulltextSearchLike(this.full_text_search.getValue());
		} else {
			list_scheduleShip = this.shipSchedulerDao.loadAllScheduleShip();
		}

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_scheduleShip.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_scheduleShip.setPageSize(10);
		}

		this.sw_list_scheduleShip.setModel(new ListModelList<ScheduleShip>(list_scheduleShip));
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

	@Listen("onClick = #sw_link_previewDetailship")
	public void showDetailScheduleShipInPopup() {

		this.scheduleShip_selected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		if (this.scheduleShip_selected != null) {
			final List<DetailScheduleShip> detailList = this.shipSchedulerDao.loadDetailScheduleShipByIdSchedule(this.scheduleShip_selected.getId());

			this.popup_sw_list_scheduleDetailShip.setModel(new ListModelList<DetailScheduleShip>(detailList));
			// show name of operative user
			for (final DetailScheduleShip detailScheduleShip : detailList) {
				final Person userOperative = this.personDao.loadPerson(detailScheduleShip.getIduser());
				detailScheduleShip.setFirstname(userOperative.getFirstname() + " " + userOperative.getLastname());
			}

		}

	}

	@Listen("onClick = #sw_shipArrivalToday")
	public void showScheduleShipTodayInArrival() {
		List<ScheduleShip> list_scheduleShip = null;

		Date date = new Date();
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		date = cal.getTime();

		final Timestamp today = new Timestamp(date.getTime());

		list_scheduleShip = this.shipSchedulerDao.loadScheduleShipByArrivalDate(today);

		for (final ScheduleShip scheduleShip : list_scheduleShip) {
			scheduleShip.setName(this.shipDao.loadShip(scheduleShip.getIdship()).getName());
		}

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_scheduleShip.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_scheduleShip.setPageSize(10);
		}

		this.sw_list_scheduleShip.setModel(new ListModelList<ScheduleShip>(list_scheduleShip));
	}

}
