package org.uario.seaworkengine.zkevent;

import java.util.List;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.FiscalControl;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.FiscalControlDAO;
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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class UserDetailsComposerFC extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Datebox				control_date;

	// dao interface
	private FiscalControlDAO	fcDAO;

	@Wire
	private Component			grid_details;

	private final Logger		logger				= Logger.getLogger(UserDetailsComposerFC.class);

	@Wire
	private Textbox				note;

	private Person				person_selected;

	@Wire
	private Datebox				request_date;

	@Wire
	private Textbox				result;

	@Wire
	private Combobox			result_comunication_type;

	@Wire
	private Textbox				sede_inps;

	@Wire
	private Datebox				sikness_from;

	@Wire
	private Datebox				sikness_to;

	// status ADD or MODIFY
	private boolean				status_add			= false;

	@Wire
	private Listbox				sw_list;

	@Listen("onClick = #sw_add")
	public void addItem() {

		this.status_add = true;

	}

	@Listen("onClick = #delete_command")
	public void deleteItemToUser() {

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final FiscalControl item = this.sw_list.getSelectedItem().getValue();

		this.fcDAO.removeFiscalControl(item.getId());
		Messagebox.show("Visita Fiscale rimossa", "INFO", Messagebox.OK, Messagebox.INFORMATION);

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
				UserDetailsComposerFC.this.person_selected = (Person) arg0.getData();

				// get the dao
				UserDetailsComposerFC.this.fcDAO = (FiscalControlDAO) SpringUtil.getBean(BeansTag.FISCAL_CONTROL_DAO);

				UserDetailsComposerFC.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #sw_link_edit")
	public void modifyItem() {

		this.status_add = false;

		// get selected item
		final FiscalControl item = this.sw_list.getSelectedItem().getValue();

		// set info about grid
		if (this.person_selected == null) {
			return;
		}

		if (item == null) {
			return;
		}

		this.control_date.setValue(item.getControl_date());
		this.note.setValue(item.getNote());
		this.request_date.setValue(item.getRequest_date());
		this.result.setValue(item.getResult());

		// set result communication type
		final String com_type = item.getResult_comunication_type();
		if (com_type != null) {
			for (final Comboitem itm : this.result_comunication_type.getItems()) {
				if (itm.getValue().equals(com_type)) {
					this.result_comunication_type.setSelectedItem(itm);
				}

			}
		}

		this.sede_inps.setValue(item.getSede_inps());
		this.sikness_from.setValue(item.getSikness_from());
		this.sikness_to.setValue(item.getSikness_to());

	}

	@Listen("onClick = #ok_command")
	public void okCommand() {

		if (this.person_selected == null) {
			return;
		}

		if (this.status_add) {

			final FiscalControl item = new FiscalControl();

			// setup item with values
			this.setupItemWithValues(item);

			this.fcDAO.createFCForUser(this.person_selected.getId(), item);

			Messagebox.show("Mansione aggiunta all'utente", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		}
		else {

			// get selected item
			final FiscalControl item = this.sw_list.getSelectedItem().getValue();
			if (item == null) {
				return;
			}

			// add values to the items
			this.setupItemWithValues(item);

			this.fcDAO.updateFC(item);
		}

		// Refresh list task
		this.setInitialView();

	}

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		final List<FiscalControl> list = this.fcDAO.loadFiscalControlByUser(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<FiscalControl>(list));

		this.grid_details.setVisible(false);
	}

	private void setupItemWithValues(final FiscalControl item) {
		item.setControl_date(this.control_date.getValue());
		item.setNote(this.note.getValue());
		item.setRequest_date(this.request_date.getValue());
		item.setResult(this.result.getValue());

		// set result communication type
		if (this.result_comunication_type.getSelectedItem() == null) {
			item.setResult_comunication_type(null);
		}
		else {
			final String itm = this.result_comunication_type.getSelectedItem().getValue();
			item.setResult_comunication_type(itm);
		}

		item.setSede_inps(this.sede_inps.getValue());
		item.setSikness_from(this.sikness_from.getValue());
		item.setSikness_to(this.sikness_to.getValue());
	}

}
