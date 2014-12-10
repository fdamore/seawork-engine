package org.uario.seaworkengine.zkevent;

import java.util.List;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Ship;
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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

public class ShipDetailsComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Component			add_ships_command;

	@Wire
	private Tab					detail_ship_tab;

	@Wire
	private Textbox				full_text_search;

	@Wire
	private Component			grid_ship_details;

	private final Logger		logger				= Logger.getLogger(UserDetailsComposer.class);

	@Wire
	private Component			modify_ships_command;

	@Wire
	private Textbox				ship_condition;

	@Wire
	private Textbox				ship_line;

	@Wire
	private Textbox				ship_name;

	Ship						ship_selected		= null;

	@Wire
	private Textbox				ship_twtype;

	@Wire
	private Textbox				ship_type;

	protected IShip				shipDao;

	@Wire
	private Intbox				shows_rows;

	@Wire
	private Listbox				sw_list_ship;

	@Listen("onClick = #add_ships_command")
	public void addShipCommand() {

		final Ship ship = new Ship();
		ship.setName(this.ship_name.getValue());
		ship.setLine(this.ship_line.getValue());
		ship.setShiptype(this.ship_type.getValue());
		ship.setShipcondition(this.ship_condition.getValue());
		ship.setTwtype(this.ship_twtype.getValue());

		if (ship.getName() == "" || ship.getLine() == "" || ship.getShiptype() == "" || ship.getShipcondition() == "" || ship.getTwtype() == "") {
			Messagebox.show("Controllare i valori inseriti.", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
		} else {
			this.shipDao.createShip(ship);

			// reset data info
			this.resetDataInfo();

			Messagebox.show("Nave aggiunta", "INFO", Messagebox.OK, Messagebox.INFORMATION);

			// set ship ListBox
			this.setShipListBox();

			this.grid_ship_details.setVisible(false);
			this.add_ships_command.setVisible(false);
			this.modify_ships_command.setVisible(false);
		}

	}

	@Listen("onClick = #sw_link_deleteship")
	public void defineDeleteView() {

		// take ship
		this.ship_selected = this.sw_list_ship.getSelectedItem().getValue();

	}

	@Listen("onClick = #sw_link_modifyship")
	public void defineModifyView() {

		if ((this.sw_list_ship.getSelectedItem() == null) || (this.sw_list_ship.getSelectedItem().getValue() == null)
				|| !(this.sw_list_ship.getSelectedItem().getValue() instanceof Ship)) {
			return;
		}

		// take ship
		this.ship_selected = this.sw_list_ship.getSelectedItem().getValue();

		// get the last ship from database
		this.ship_selected = this.shipDao.loadShip(this.ship_selected.getId());

		// general details
		this.defineShipDetailsView(this.ship_selected);

	}

	private void defineShipDetailsView(final Ship ship_selected) {
		this.ship_name.setValue(ship_selected.getName());
		this.ship_line.setValue(ship_selected.getLine());
		this.ship_type.setValue(ship_selected.getShiptype());
		this.ship_condition.setValue(ship_selected.getShipcondition());
		this.ship_twtype.setValue(ship_selected.getTwtype());

	}

	private void deleteShipCommand() {

		try {
			if (this.ship_selected == null) {
				return;
			}

			this.shipDao.deleteShip(this.ship_selected.getId());

			// update list
			this.setShipListBox();

			Messagebox.show("Nave cancellata", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		} catch (final Exception e) {

			this.logger.error("Error removing ship. " + e.getMessage());

			Messagebox.show("Non è possibile eliminare questa nave.\nControlla che non ci siano azioni legate a questa anagrafica.", "INFO",
					Messagebox.OK, Messagebox.ERROR);

		}

	}

	@Override
	public void doFinally() throws Exception {

		this.getSelf().addEventListener(ZkEventsTag.onShowShips, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// get the DAOs
				ShipDetailsComposer.this.shipDao = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);

				ShipDetailsComposer.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #modify_ships_command")
	public void mofifyShipsCommand() {
		if (this.ship_selected == null) {
			return;
		}

		if (this.ship_name.getValue() == "" || this.ship_line.getValue() == "" || this.ship_type.getValue() == ""
				|| this.ship_condition.getValue() == "" || this.ship_twtype.getValue() == "") {
			Messagebox.show("Controllare i valori inseriti.", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
		} else {
			this.ship_selected.setName(this.ship_name.getValue());
			this.ship_selected.setLine(this.ship_line.getValue());
			this.ship_selected.setShiptype(this.ship_type.getValue());
			this.ship_selected.setShipcondition(this.ship_condition.getValue());
			this.ship_selected.setTwtype(this.ship_twtype.getValue());

			// update
			this.shipDao.updateShip(this.ship_selected);

			// update list
			this.setShipListBox();

			this.grid_ship_details.setVisible(false);

			this.resetDataInfo();

			Messagebox.show("Dati Navi aggiornati", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		}

	}

	@Listen("onClick = #sw_refresh_list;")
	public void refreshListShip() {

		this.full_text_search.setValue(null);

		// set user listbox
		this.setShipListBox();
	}

	@Listen("onClick = #sw_link_deleteship")
	public void removeItem() {
		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
			@Override
			public void onEvent(final Event e) {
				if (Messagebox.ON_OK.equals(e.getName())) {
					ShipDetailsComposer.this.deleteShipCommand();
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
		this.ship_name.setValue(null);
		this.ship_type.setValue(null);

		this.ship_line.setValue(null);

		this.ship_condition.setValue(null);
		this.ship_twtype.setValue(null);

	}

	/**
	 * Show ships
	 */
	public void setInitialView() {

		this.full_text_search.setValue(null);

		// set ship listbox
		this.setShipListBox();

		// initial view
		this.grid_ship_details.setVisible(false);

	}

	/**
	 * Set ship list box with initial events
	 */
	@Listen("onOK = #shows_rows, #full_text_search")
	public void setShipListBox() {

		List<Ship> list_ship = null;

		if ((this.full_text_search.getValue() != null) && !this.full_text_search.getValue().equals("")) {
			list_ship = this.shipDao.listAllShip(this.full_text_search.getValue());
		} else {
			list_ship = this.shipDao.loadAllShip();
		}

		if ((this.shows_rows.getValue() != null) && (this.shows_rows.getValue() != 0)) {
			this.sw_list_ship.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_ship.setPageSize(10);
		}

		this.sw_list_ship.setModel(new ListModelList<Ship>(list_ship));
	}

	@Listen("onClick = #sw_addship")
	public void showAddShipPanel() {

		// command
		this.add_ships_command.setVisible(true);
		this.modify_ships_command.setVisible(false);

		// configure tab
		this.grid_ship_details.setVisible(true);

		// set detail to selection
		this.detail_ship_tab.getTabbox().setSelectedTab(this.detail_ship_tab);

	}

	@Listen("onClick = #sw_link_modifyship")
	public void showModifyShipPanel() {

		// command
		this.add_ships_command.setVisible(false);
		this.modify_ships_command.setVisible(true);

		// configure tab
		this.grid_ship_details.setVisible(true);

		// set detail to selection
		this.detail_ship_tab.getTabbox().setSelectedTab(this.detail_ship_tab);
	}

}
