package org.uario.seaworkengine.zkevent;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;

public class ShipSchedulerComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID		= 1L;

	@Wire
	private Component			add_scheduleShips_command;

	@Wire
	private Tab					detail_scheduleShip_tab;

	@Wire
	private Textbox				full_text_search;

	@Wire
	private Component			grid_scheduleShip_details;

	private final Logger		logger					= Logger.getLogger(UserDetailsComposer.class);

	@Wire
	private Component			modify_Scheduleships_command;

	@Wire
	private Textbox				note;

	ScheduleShip				scheduleShip_selected	= null;

	@Wire
	private Datebox				ship_arrivalDate;

	@Wire
	private Timebox				ship_arrivalTime;

	@Wire
	private Combobox			ship_name;

	@Wire
	private Doublebox			ship_volume;

	protected IShip				shipDao;
	protected IScheduleShip		shipSchedulerDao;

	@Wire
	private Intbox				shows_rows;

	@Wire
	private Listbox				sw_list_scheduleShip;

	@Listen("onClick = #add_scheduleShips_command")
	public void addShipCommand() {

		if (this.ship_name.getSelectedItem() == null || this.ship_volume.getValue() == null || this.note.getValue() == ""
				|| this.ship_arrivalTime.getValue() == null || this.ship_arrivalDate.getValue() == null) {
			Messagebox.show("Controllare i valori inseriti.", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		} else {
			final ScheduleShip shipSchedule = new ScheduleShip();

			final String shipName = this.ship_name.getSelectedItem().getValue().toString();

			final int idShip = this.shipDao.listIdShipByName(shipName).get(0);

			shipSchedule.setIdship(idShip);
			shipSchedule.setVolume(this.ship_volume.getValue());
			shipSchedule.setNote(this.note.getValue());

			// set arrival date and time

			final Timestamp arrivalDate = new Timestamp(0);
			arrivalDate.setTime(this.ship_arrivalTime.getValue().getTime());
			arrivalDate.setDate(this.ship_arrivalDate.getValue().getDate());
			arrivalDate.setMonth(this.ship_arrivalDate.getValue().getMonth());
			arrivalDate.setYear(this.ship_arrivalDate.getValue().getYear());
			shipSchedule.setArrivaldate(arrivalDate);

			this.shipSchedulerDao.createScheduleShip(shipSchedule);

			// reset data info
			this.resetDataInfo();

			Messagebox.show("Programmazione aggiunta", "INFO", Messagebox.OK, Messagebox.INFORMATION);

			// set ship ListBox
			this.setScheduleShipListBox();

			this.grid_scheduleShip_details.setVisible(false);
			this.add_scheduleShips_command.setVisible(false);
			this.modify_Scheduleships_command.setVisible(false);

		}
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

		// take ship
		this.scheduleShip_selected = this.sw_list_scheduleShip.getSelectedItem().getValue();

		// get the last ship from database
		this.scheduleShip_selected = this.shipSchedulerDao.loadScheduleShip(this.scheduleShip_selected.getId());

		// general details
		this.defineScheduleShipDetailsView(this.scheduleShip_selected);

	}

	private void defineScheduleShipDetailsView(final ScheduleShip scheduleShip_selected) {

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
		this.ship_arrivalDate.setValue(arrivalDate);
		this.ship_arrivalTime.setValue(arrivalDate);

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

				final ListModel<Ship> modelComboBox = new ListModelList<Ship>(ShipSchedulerComposer.this.shipDao.listAllShip(null));

				if (modelComboBox.getSize() == 0) {
					Messagebox
					.show("Inserire almeno una nave prima di procedere alla programmazione!", "INFO", Messagebox.OK, Messagebox.INFORMATION);
				}

				ShipSchedulerComposer.this.ship_name.setModel(modelComboBox);

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
				|| this.ship_arrivalTime.getValue() == null || this.ship_arrivalDate.getValue() == null) {
			Messagebox.show("Controllare i valori inseriti.", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		} else {

			final int idShip = ((Ship) this.ship_name.getSelectedItem().getValue()).getId();

			this.scheduleShip_selected.setIdship(idShip);
			this.scheduleShip_selected.setVolume(this.ship_volume.getValue());
			this.scheduleShip_selected.setNote(this.note.getValue());

			// set arrival date and time
			final Timestamp arrivalDate = new Timestamp(0);
			arrivalDate.setTime(this.ship_arrivalTime.getValue().getTime());
			arrivalDate.setDate(this.ship_arrivalDate.getValue().getDate());
			arrivalDate.setMonth(this.ship_arrivalDate.getValue().getMonth());
			arrivalDate.setYear(this.ship_arrivalDate.getValue().getYear());

			this.scheduleShip_selected.setArrivaldate(arrivalDate);

			// update
			this.shipSchedulerDao.updateScheduleShip(this.scheduleShip_selected);

			// update list
			this.setScheduleShipListBox();

			this.grid_scheduleShip_details.setVisible(false);

			this.resetDataInfo();

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
	 * Reset data on ship grid
	 */
	private void resetDataInfo() {
		this.ship_name.setSelectedItem(null);
		this.ship_volume.setValue(null);
		this.note.setValue(null);
		this.ship_arrivalDate.setValue(null);
		this.ship_arrivalTime.setValue(null);
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

}
