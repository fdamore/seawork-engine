package org.uario.seaworkengine.zkevent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.TradeUnion;
import org.uario.seaworkengine.platform.persistence.dao.TradeUnionDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.UtilityCSV;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class UserDetailsComposerTD extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Datebox				cancellation;

	@Wire
	private Component			grid_details;

	private final Logger		logger				= Logger.getLogger(UserDetailsComposerTD.class);

	@Wire
	private Textbox				name;

	@Wire
	private Textbox				note;

	private Person				person_selected;

	@Wire
	private Datebox				registration;

	// status ADD or MODIFY
	private boolean				status_add			= false;

	@Wire
	private Listbox				sw_list;

	// dao interface
	private TradeUnionDAO		tradeUnionDAO;

	@Listen("onClick = #sw_add")
	public void addItem() {

		this.status_add = true;

	}

	private void deleteItemToUser() {

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final TradeUnion item = this.sw_list.getSelectedItem().getValue();

		this.tradeUnionDAO.removeTradeUnion(item.getId());

		final Map<String, String> params = new HashMap();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

		Messagebox.show("Info sindacato rimossa", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		// Refresh list task
		this.setInitialView();

	}

	@Override
	public void doFinally() throws Exception {

		this.getSelf().addEventListener(ZkEventsTag.onShowUsers, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// get selected person
				if ((arg0.getData() == null) || !(arg0.getData() instanceof Person)) {
					return;
				}
				UserDetailsComposerTD.this.person_selected = (Person) arg0.getData();

				// get the dao
				UserDetailsComposerTD.this.tradeUnionDAO = (TradeUnionDAO) SpringUtil.getBean(BeansTag.TRADE_UNION_DAO);

				UserDetailsComposerTD.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #user_csv")
	public void downloadCSV_user_csv() {

		if ((this.person_selected == null) || (this.person_selected.getId() == null)) {
			return;
		}

		final List<TradeUnion> list = this.tradeUnionDAO.loadTradeUnionsByUser(this.person_selected.getId());
		final StringBuilder builder = UtilityCSV.downloadCSV_user_tradeunion(null, list, true);
		Filedownload.save(builder.toString(), "application/text", "info_sindacati.csv");

	}

	@Listen("onClick = #sw_link_edit")
	public void modifyItem() {

		this.status_add = false;

		// get selected item
		final TradeUnion item = this.sw_list.getSelectedItem().getValue();

		// set info about grid
		if (this.person_selected == null) {
			return;
		}

		if (item == null) {
			return;
		}

		this.note.setValue(item.getNote());
		this.name.setValue(item.getName());
		this.registration.setValue(item.getRegistration());
		this.cancellation.setValue(item.getCancellation());

	}

	@Listen("onClick = #ok_command")
	public void okCommand() {

		if (this.person_selected == null) {
			return;
		}

		if (this.status_add) {

			final TradeUnion item = new TradeUnion();

			// setup item with values
			this.setupItemWithValues(item);

			this.tradeUnionDAO.createTradeUnionForUser(this.person_selected.getId(), item);

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Sindacato aggiunto all'utente", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		} else {

			// get selected item
			final TradeUnion item = this.sw_list.getSelectedItem().getValue();
			if (item == null) {
				return;
			}

			// add values to the items
			this.setupItemWithValues(item);

			this.tradeUnionDAO.updateTradeUnion(item);
		}

		// Refresh list task
		this.setInitialView();

	}

	@Listen("onClick = #sw_link_delete")
	public void removeItem() {
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
							UserDetailsComposerTD.this.deleteItemToUser();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				}, params);

	}

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		final List<TradeUnion> list = this.tradeUnionDAO.loadTradeUnionsByUser(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<>(list));

		this.grid_details.setVisible(false);
	}

	private void setupItemWithValues(final TradeUnion item) {
		item.setCancellation(this.cancellation.getValue());
		item.setName(this.name.getValue());
		item.setNote(this.note.getValue());
		item.setRegistration(this.registration.getValue());
	}

}
