package org.uario.seaworkengine.zkevent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Customer;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.ICustomerDAO;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.statistics.impl.MonitorData;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

public class ShipDetailsComposer extends SelectorComposer<Component> {
	
	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;
	
	@Wire
	private Div					add_customer_div;

	@Wire
	private Component			add_ships_command;
	
	@Wire
	private Textbox				customer_name;
	
	@Wire
	private Textbox				customer_note;
	
	private Customer			customer_selected	= null;
	
	private ICustomerDAO		customerDAO;
	
	@Wire
	private Tab					detail_ship_tab;
	
	@Wire
	private Textbox				full_text_search;
	
	@Wire
	private Component			grid_ship_details;
	
	private Boolean				isInModify			= false;
	
	@Wire
	private Listbox				list_monitor;
	
	private final Logger		logger				= Logger.getLogger(UserDetailsComposer.class);
	
	@Wire
	private Component			modify_ships_command;

	@Wire
	private Datebox				monitor_date;
	
	@Wire
	private Textbox				note;
	
	@Wire
	private Checkbox			ship_activity;
	
	@Wire
	private Textbox				ship_condition;
	
	@Wire
	private Textbox				ship_line;
	
	@Wire
	private Textbox				ship_name;
	
	@Wire
	private Checkbox			ship_nowork;

	private Ship				ship_selected		= null;
	
	@Wire
	private Textbox				ship_twtype;
	
	@Wire
	private Textbox				ship_type;
	
	protected IShip				shipDao;
	
	@Wire
	private Intbox				shows_rows;
	
	private Boolean				status_modify		= Boolean.FALSE;
	
	@Wire
	private Listbox				sw_list_customer;
	
	@Wire
	private Listbox				sw_list_ship;
	
	@Listen("onClick = #sw_addcustomer_ok")
	public void addCustomerOK() {
		
		if (this.customer_name.getValue() == null) {
			return;
		}
		
		if (!this.status_modify.booleanValue()) {

			final Customer customer = new Customer();
			customer.setName(this.customer_name.getValue());
			customer.setNote(this.customer_note.getValue());
			this.customerDAO.createCustomer(customer);
		} else {
			if (this.customer_selected != null) {
				this.customer_selected.setName(this.customer_name.getValue());
				this.customer_selected.setNote(this.customer_note.getValue());
				this.customerDAO.updateCustomer(this.customer_selected);
			}
		}
		
		// set customer listbox
		this.setCustomerListBox();
		this.add_customer_div.setVisible(false);
		
	}
	
	@Listen("onClick = #add_ships_command")
	public void addShipCommand() {
		
		if (this.ship_name.getValue() == null || this.ship_name.getValue().equals("")) {
			
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;
			
			Messagebox.show("Devi inserire un nome nave", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null,
					null, params);
			return;
			
		}
		
		if (this.shipDao.verifyIfShipExistByName(this.ship_name.getValue(), null)) {
			
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;
			
			Messagebox.show("Nave già presente in anagrafica!", "INFO", buttons, null, Messagebox.EXCLAMATION, null,
					null, params);
			return;
			
		}
		
		final Ship ship = new Ship();
		ship.setName(this.ship_name.getValue());
		ship.setLine(this.ship_line.getValue());
		ship.setShiptype(this.ship_type.getValue());
		ship.setShipcondition(this.ship_condition.getValue());
		ship.setTwtype(this.ship_twtype.getValue());
		ship.setNowork(this.ship_nowork.isChecked());
		ship.setActivityh(this.ship_activity.isChecked());
		ship.setNote(this.note.getValue());
		
		if (!ship.getNowork()) {
			this.createShip(ship);
		} else if (ship.getNowork()) {
			
			final Ship noWorkShip = this.shipDao.getNoWorkShip();
			
			if (noWorkShip == null) {
				this.createShip(ship);
			} else {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				
				final Messagebox.Button[] buttons = new Messagebox.Button[2];
				buttons[0] = Messagebox.Button.OK;
				buttons[1] = Messagebox.Button.CANCEL;
				
				Messagebox.show("Nave No Lavoro già presente, proseguire?", "CONFERMA INSERIMENTO", buttons, null,
						Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>() {
							
							@Override
							public void onEvent(final ClickEvent e) {
								if (Messagebox.ON_OK.equals(e.getName())) {
									
									ShipDetailsComposer.this.shipDao.removeShipNoWork();
									ShipDetailsComposer.this.createShip(ship);
									
								} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
									return;
								}
							}
						}, params);
			}
			
		}
		
	}
	
	@Listen("onBlur = #ship_name")
	public void checkPresence() {
		if (this.ship_name.getValue() != null) {
			Integer shipId = null;
			if (this.isInModify && this.ship_selected != null) {
				shipId = this.ship_selected.getId();
			}
			if (this.shipDao.verifyIfShipExistByName(this.ship_name.getValue(), shipId)) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;
				
				Messagebox.show("Nave già presente in anagrafica!", "INFO", buttons, null, Messagebox.EXCLAMATION, null,
						null, params);
				
				this.ship_name.setValue("");
			}
			
		}
	}
	
	private void createShip(final Ship ship) {
		this.shipDao.createShip(ship);
		
		// reset data info
		this.resetDataInfo();
		
		// set ship ListBox
		this.setShipListBox();
		
		this.grid_ship_details.setVisible(false);
		this.add_ships_command.setVisible(false);
		this.modify_ships_command.setVisible(false);
	}
	
	@Listen("onClick = #sw_link_deleteship")
	public void defineDeleteView() {
		
		// take ship
		this.ship_selected = this.sw_list_ship.getSelectedItem().getValue();
		
	}
	
	@Listen("onClick = #sw_link_modifyship")
	public void defineModifyView() {
		
		this.isInModify = true;
		
		if (this.sw_list_ship.getSelectedItem() == null || this.sw_list_ship.getSelectedItem().getValue() == null
				|| !(this.sw_list_ship.getSelectedItem().getValue() instanceof Ship)) {
			return;
		}
		
		// take ship
		this.ship_selected = this.sw_list_ship.getSelectedItem().getValue();
		
		// get the last ship from database
		this.ship_selected = this.shipDao.loadShip(this.ship_selected.getId());
		
		// general details
		this.defineShipDetailsView(this.ship_selected);
		
		// command
		this.add_ships_command.setVisible(false);
		this.modify_ships_command.setVisible(true);
		
		// configure tab
		this.grid_ship_details.setVisible(true);
		
		// set detail to selection
		this.detail_ship_tab.getTabbox().setSelectedTab(this.detail_ship_tab);
		
	}
	
	private void defineShipDetailsView(final Ship ship_selected) {
		this.ship_name.setValue(ship_selected.getName());
		this.ship_line.setValue(ship_selected.getLine());
		this.ship_type.setValue(ship_selected.getShiptype());
		this.ship_condition.setValue(ship_selected.getShipcondition());
		this.ship_twtype.setValue(ship_selected.getTwtype());
		this.ship_activity.setChecked(ship_selected.getActivityh());
		this.ship_nowork.setChecked(ship_selected.getNowork());
		this.note.setValue(ship_selected.getNote());
		
	}
	
	@Listen("onClick = #sw_link_modifycustomer")
	public void deleteCustomerCommand() {
		
		if (this.sw_list_customer.getSelectedItem() == null
				|| this.sw_list_customer.getSelectedItem().getValue() == null) {
			return;
		}
		
		final Customer customer = this.sw_list_customer.getSelectedItem().getValue();
		
		this.customerDAO.deleteCustomer(customer.getId());
		
		// update list
		this.setCustomerListBox();
		
	}
	
	private void deleteShipCommand() {
		
		try {
			if (this.ship_selected == null) {
				return;
			}
			
			this.shipDao.deleteShip(this.ship_selected.getId());
			
			// update list
			this.setShipListBox();
			
		} catch (final Exception e) {
			
			this.logger.error("Error removing ship. " + e.getMessage());
			
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;
			
			Messagebox.show(
					"Non è possibile eliminare questa nave.\nControlla che non ci siano azioni legate a questa anagrafica.",
					"ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null, null, params);
			
		}
		
	}
	
	@Override
	public void doFinally() throws Exception {
		
		this.getSelf().addEventListener(ZkEventsTag.onShowShips, new EventListener<Event>() {
			
			@Override
			public void onEvent(final Event arg0) throws Exception {
				
				// update data monitor
				ShipDetailsComposer.this.updateDataMonitor();
				
				// get the DAOs
				ShipDetailsComposer.this.shipDao = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);
				
				// get the DAOs
				ShipDetailsComposer.this.customerDAO = (ICustomerDAO) SpringUtil.getBean(BeansTag.CUSTOMER_DAO);
				
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
			
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;
			
			Messagebox.show("Controllare i valori inseriti.", "ATTENZIONE", buttons, null, Messagebox.EXCLAMATION, null,
					null, params);
			
			this.isInModify = true;
			
		} else {
			
			this.ship_selected.setName(this.ship_name.getValue());
			this.ship_selected.setLine(this.ship_line.getValue());
			this.ship_selected.setShiptype(this.ship_type.getValue());
			this.ship_selected.setShipcondition(this.ship_condition.getValue());
			this.ship_selected.setTwtype(this.ship_twtype.getValue());
			this.ship_selected.setActivityh(this.ship_activity.isChecked());
			this.ship_selected.setNowork(this.ship_nowork.isChecked());
			this.ship_selected.setNote(this.note.getValue());
			
			if (!this.ship_nowork.isChecked() && !this.ship_activity.isChecked()) {
				this.shipDao.updateShip(this.ship_selected);
				
				this.showOkMessageBox();
				
			} else if (this.ship_nowork.isChecked()) {
				
				final Ship noWorkShip = this.shipDao.getNoWorkShip();
				if (noWorkShip == null || noWorkShip.getId() == this.ship_selected.getId()) {
					this.shipDao.updateShip(this.ship_selected);
					this.showOkMessageBox();
				} else {
					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");
					
					final Messagebox.Button[] buttons = new Messagebox.Button[2];
					buttons[0] = Messagebox.Button.OK;
					buttons[1] = Messagebox.Button.CANCEL;
					
					Messagebox.show("Nave No Lavoro già presente, proseguire?", "CONFERMA INSERIMENTO", buttons, null,
							Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>() {
								
								@Override
								public void onEvent(final ClickEvent e) {
									if (Messagebox.ON_OK.equals(e.getName())) {
										
										ShipDetailsComposer.this.shipDao.removeShipNoWork();
										ShipDetailsComposer.this.shipDao
												.updateShip(ShipDetailsComposer.this.ship_selected);
										
										ShipDetailsComposer.this.showOkMessageBox();
										
									} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
										ShipDetailsComposer.this.isInModify = true;
										return;
									}
								}
							}, params);
				}
				
			}
			
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
		final Map<String, String> params = new HashMap<>();
		params.put("sclass", "mybutton Button");
		
		final Messagebox.Button[] buttons = new Messagebox.Button[2];
		buttons[0] = Messagebox.Button.OK;
		buttons[1] = Messagebox.Button.CANCEL;
		
		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null,
				Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>() {
					@Override
					public void onEvent(final ClickEvent e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							ShipDetailsComposer.this.deleteShipCommand();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				}, params);
		
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
	
	@Listen("onOK = #sw_refresh_list_customer")
	public void setCustomerListBox() {
		
		final List<Customer> list_ship = this.customerDAO.listAllCustomers();
		
		this.sw_list_customer.setModel(new ListModelList<>(list_ship));
	}
	
	/**
	 * Show ships
	 */
	public void setInitialView() {
		
		this.full_text_search.setValue(null);
		
		// set ship listbox
		this.setShipListBox();
		
		// set customer listbox
		this.setCustomerListBox();
		
		// initial view
		this.grid_ship_details.setVisible(false);
		
	}
	
	/**
	 * Set ship list box with initial events
	 */
	@Listen("onOK = #shows_rows, #full_text_search")
	public void setShipListBox() {
		
		List<Ship> list_ship = null;
		
		if (this.full_text_search.getValue() != null && !this.full_text_search.getValue().equals("")) {
			list_ship = this.shipDao.listAllShip(this.full_text_search.getValue());
		} else {
			list_ship = this.shipDao.loadAllShip();
		}
		
		if (this.shows_rows.getValue() != null && this.shows_rows.getValue() != 0) {
			this.sw_list_ship.setPageSize(this.shows_rows.getValue());
		} else {
			this.sw_list_ship.setPageSize(10);
		}
		
		this.sw_list_ship.setModel(new ListModelList<>(list_ship));
	}

	@Listen("onClick = #show_modifycustomer")
	public void show_modifycustomer() {
		
		if (this.sw_list_customer.getSelectedItem() == null
				|| this.sw_list_customer.getSelectedItem().getValue() == null) {
			return;
		}
		
		final Customer customer = this.sw_list_customer.getSelectedItem().getValue();

		this.customer_name.setValue(customer.getName());
		this.customer_note.setValue(customer.getNote());

		this.customer_selected = customer;

		this.status_modify = Boolean.TRUE;
		this.add_customer_div.setVisible(true);
		
	}
	
	@Listen("onClick = #getActvityHShip")
	public void showActivityHShip() {
		
		final List<Ship> list_ship = this.shipDao.getActivityHShip();
		
		this.sw_list_ship.setModel(new ListModelList<>(list_ship));
	}
	
	@Listen("onClick = #sw_addcustomer")
	public void showAddCustomer() {
		
		this.customer_name.setValue(null);
		this.customer_note.setValue(null);
		
		this.status_modify = Boolean.FALSE;
		this.add_customer_div.setVisible(true);
		
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
		
		this.ship_activity.setChecked(false);
		this.ship_nowork.setChecked(false);
		
	}
	
	@Listen("onClick = #getNoWorkShip")
	public void showNoWorkShip() {
		final ListModelList<Ship> list_ship = new ListModelList<>();
		
		final Ship ship = this.shipDao.getNoWorkShip();
		
		if (ship != null) {
			list_ship.add(ship);
		}
		
		this.sw_list_ship.setModel(new ListModelList<>(list_ship));
	}
	
	private void showOkMessageBox() {
		// update list
		this.setShipListBox();
		
		this.grid_ship_details.setVisible(false);
		
		this.resetDataInfo();
		
		this.isInModify = false;
	}
	
	@Listen("onClick = #update_monitor; onChange = #monitor_date")
	public void updateDataMonitor() {
		
		if (this.monitor_date.getValue() == null) {
			return;
		}
		
		final IStatistics statistics = (IStatistics) SpringUtil.getBean(BeansTag.STATISTICS);
		final List<MonitorData> list = statistics.getMonitorData(this.monitor_date.getValue());
		
		this.list_monitor.setModel(new ListModelList<>(list));
	}
	
}
