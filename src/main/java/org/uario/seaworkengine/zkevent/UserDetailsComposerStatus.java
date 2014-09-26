package org.uario.seaworkengine.zkevent;

import java.util.List;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Employment;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.EmploymentDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class UserDetailsComposerStatus extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Datebox				date_modifiled;

	// DAO for access employment data
	private EmploymentDAO		employmentDao;

	@Wire
	private Component			grid_details;

	private final Logger		logger				= Logger.getLogger(UserDetailsComposerStatus.class);

	@Wire
	private Textbox				note;

	private Person				person_selected;

	@Wire
	private Textbox				status;

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

		final Employment item = this.sw_list.getSelectedItem().getValue();

		this.employmentDao.removeEmployment(item.getId());

		Messagebox.show("Inforomazione rimossa", "INFO", Messagebox.OK, Messagebox.INFORMATION);

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
				UserDetailsComposerStatus.this.person_selected = (Person) arg0.getData();

				// get DAO
				UserDetailsComposerStatus.this.employmentDao = (EmploymentDAO) SpringUtil.getBean(BeansTag.EMPLOYMENT_DAO);

				UserDetailsComposerStatus.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #sw_link_edit")
	public void modifyItem() {

		this.status_add = false;

		// get selected item
		final Employment item = this.sw_list.getSelectedItem().getValue();

		// set info about grid
		if (this.person_selected == null) {
			return;
		}

		if (item == null) {
			return;
		}

		this.note.setValue(item.getNote());
		this.status.setValue(item.getStatus());
		this.date_modifiled.setValue(item.getDate_modified());

	}

	@Listen("onClick = #ok_command")
	public void okCommand() {

		if (this.person_selected == null) {
			return;
		}

		if (this.status_add) {

			final Employment item = new Employment();

			// setup item with values
			this.setupItemWithValues(item);

			this.employmentDao.createEmploymentForUser(this.person_selected.getId(), item);

			Messagebox.show("Mansione aggiunta all'utente", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		} else {

			// get selected item
			final Employment item = this.sw_list.getSelectedItem().getValue();
			if (item == null) {
				return;
			}

			// add values to the items
			this.setupItemWithValues(item);

			this.employmentDao.updateEmployment(item);
		}

		// Refresh list task
		this.setInitialView();

	}

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		final List<Employment> list = this.employmentDao.loadEmploymentByUser(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<Employment>(list));

		this.grid_details.setVisible(false);
	}

	private void setupItemWithValues(final Employment item) {

		item.setNote(this.note.getValue());
		item.setStatus(this.status.getValue());
		item.setDate_modified(this.date_modifiled.getValue());

	}

}
