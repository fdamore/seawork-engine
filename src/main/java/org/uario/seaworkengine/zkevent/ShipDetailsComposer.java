package org.uario.seaworkengine.zkevent;

import java.util.List;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.excpetions.UserNameJustPresentExcpetion;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
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
	private Component			condition_ship_tab;

	@Wire
	private Tab					detail_ship_tab;

	@Wire
	private Textbox				full_text_search;

	@Wire
	private Component			grid_ship_details;

	@Wire
	private Component			line_ship_tab;

	private final Logger		logger				= Logger.getLogger(UserDetailsComposer.class);

	@Wire
	private Component			modify_ships_command;

	@Wire
	private Component			name_ship_tab;

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

	@Wire
	private Component			twtype_ship_tab;

	@Wire
	private Component			type_ship_tab;

	@Listen("onClick = #add_ships_command")
	public void addShipCommand() throws UserNameJustPresentExcpetion {

		final Ship ship = new Ship();
		ship.setName(this.ship_name.getValue());
		ship.setLine(this.ship_line.getValue());
		ship.setShiptype(this.ship_type.getValue());
		ship.setShipcondition(this.ship_condition.getValue());
		ship.setTwtype(this.ship_twtype.getValue());

		this.shipDao.createShip(ship);

		// reset data info
		this.resetDataInfo();

		Messagebox.show("Aggiunto elemento", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		// set user ListBox
		this.setShipListBox();

		this.grid_ship_details.setVisible(false);
		this.add_ships_command.setVisible(false);
		this.modify_ships_command.setVisible(false);

	}

	@Listen("onClick = #sw_link_deleteship")
	public void defineDeleteView() {

		// take person
		this.ship_selected = this.sw_list_ship.getSelectedItem().getValue();

	}

	@Listen("onClick = #sw_link_modifyship")
	public void defineModifyView() {

		if ((this.sw_list_ship.getSelectedItem() == null) || (this.sw_list_ship.getSelectedItem().getValue() == null)
				|| !(this.sw_list_ship.getSelectedItem().getValue() instanceof Ship)) {
			return;
		}

		// take person
		this.ship_selected = this.sw_list_ship.getSelectedItem().getValue();

		// get the last person from database
		this.ship_selected = this.shipDao.loadShip(this.ship_selected.getId());

		// general details
		this.defineShipDetailsView(this.ship_selected);

	}

	/**
	 * define user details view for modify
	 *
	 * @param person_selected
	 */
	private void defineShipDetailsView(final Ship ship_selected) {
		this.ship_name.setValue(ship_selected.getName());
		this.ship_line.setValue(ship_selected.getLine());
		this.ship_type.setValue(ship_selected.getShiptype());
		this.ship_condition.setValue(ship_selected.getShipcondition());
		this.ship_twtype.setValue(ship_selected.getTwtype());

		// send mail to components
		this.sendShipToUserComponents(ship_selected);

	}

	private void deleteUserCommand() {

		try {
			if (this.ship_selected == null) {
				return;
			}

			this.shipDao.deleteShip(this.ship_selected.getId());

			// update list
			this.setShipListBox();

			Messagebox.show("Nave cancellata", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		} catch (final Exception e) {

			this.logger.error("Error removing user. " + e.getMessage());

			Messagebox.show("Non è possibile eliminare questa nave.\nControlla che non ci siano azioni legate a questa angrafica.", "INFO",
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

		this.getSelf().addEventListener(ZkEventsTag.onUpdateGeneralDetails, new EventListener<Event>() {

			@Override
			public void onEvent(final Event event) throws Exception {

			}

			/**
			 * @Override public void onEvent(final Event arg0) throws Exception
			 *           {
			 *
			 *           if (UserDetailsComposer.this.person_selected == null) {
			 *           return; }
			 *
			 *           final String status = (String) arg0.getData();
			 *
			 *           UserDetailsComposer.this.person_selected.setStatus(
			 *           status);
			 *
			 *           UserDetailsComposer.this.personDao.updatePerson(
			 *           UserDetailsComposer.this.person_selected);
			 *
			 *           // set status
			 *           UserDetailsComposer.this.user_status.setValue(status);
			 *
			 *           // set user listbox
			 *           UserDetailsComposer.this.setUserListBox();
			 *
			 *           }
			 **/
		});

	}

	@Listen("onClick = #modify_users_command")
	public void modifyCommand() {

		if (this.ship_selected == null) {
			return;
		}

		this.ship_name.setValue(this.ship_name.getValue());
		this.ship_line.setValue(this.ship_line.getValue());
		this.ship_type.setValue(this.ship_type.getValue());
		this.ship_condition.setValue(this.ship_condition.getValue());
		this.ship_twtype.setValue(this.ship_twtype.getValue());

		// update
		this.shipDao.updateShip(this.ship_selected);

		// update list
		this.setShipListBox();

		Messagebox.show("Dati Nave aggiornati", "INFO", Messagebox.OK, Messagebox.INFORMATION);

	}

	@Listen("onClick = #sw_refresh_list;")
	public void refreshListUser() {

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
					ShipDetailsComposer.this.deleteUserCommand();
				} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
					// Cancel is clicked
				}
			}
		});

	}

	/**
	 * Reset data on user grid
	 */
	private void resetDataInfo() {
		this.ship_name.setValue(null);
		this.ship_type.setValue(null);

		this.ship_line.setValue(null);

		this.ship_condition.setValue(null);
		this.ship_twtype.setValue(null);

	}

	/**
	 * Send person_selected to other component
	 *
	 * @param ship_selected
	 */
	private void sendShipToUserComponents(final Ship ship_selected) {

		// send event to show user task
		final Component comp = Path.getComponent("//usertask/panel_task");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp, ship_selected);

		// send event to show user task
		final Component comp_tfr = Path.getComponent("//usertfr/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_tfr, ship_selected);

		// send event to show user fc
		final Component comp_fc = Path.getComponent("//userfc/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_fc, ship_selected);

		// send event to show user td
		final Component comp_td = Path.getComponent("//usertd/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_td, ship_selected);

		// send event to show user status
		final Component comp_status = Path.getComponent("//userstatus/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_status, ship_selected);

		// send event to show contestations
		final Component comp_cons = Path.getComponent("//cons/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_cons, ship_selected);

		final Component comp_jc = Path.getComponent("//userjobcost/panel");
		Events.sendEvent(ZkEventsTag.onShowUsers, comp_jc, ship_selected);

	}

	/**
	 * Show users
	 */
	public void setInitialView() {

		this.full_text_search.setValue(null);

		// set user listbox
		this.setShipListBox();

		// initial view
		this.grid_ship_details.setVisible(false);

	}

	/**
	 * Set user list box with initial events
	 */
	@Listen("onOK = #shows_rows, #full_text_search")
	public void setShipListBox() {

		final List<Ship> list_ship = null;

		if ((this.full_text_search.getValue() != null) && !this.full_text_search.getValue().equals("")) {
			// list_person =
			// this.shipDao.listAllShip(this.full_text_search.getValue());
		} else {
			// list_person = this.shipDao.listAllShip();
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
		this.name_ship_tab.setVisible(false);
		this.line_ship_tab.setVisible(false);
		this.type_ship_tab.setVisible(false);
		this.condition_ship_tab.setVisible(false);
		this.twtype_ship_tab.setVisible(false);

		// set detail to selection
		this.detail_ship_tab.getTabbox().setSelectedTab(this.detail_ship_tab);

	}

	@Listen("onClick = #sw_adduser")
	public void showGridAddUser() {

		this.resetDataInfo();

	}

	@Listen("onClick = #sw_link_modifyship")
	public void showModifyUserPanel() {

		// command
		this.add_ships_command.setVisible(false);
		this.modify_ships_command.setVisible(true);

		// configure tab
		this.grid_ship_details.setVisible(true);
		this.name_ship_tab.setVisible(true);
		this.line_ship_tab.setVisible(true);
		this.type_ship_tab.setVisible(true);
		this.condition_ship_tab.setVisible(true);
		this.twtype_ship_tab.setVisible(true);

		// set detail to selection
		this.detail_ship_tab.getTabbox().setSelectedTab(this.detail_ship_tab);
	}

}
