package org.uario.seaworkengine.zkevent;

import java.util.List;

import org.uario.seaworkengine.model.JobCost;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.IJobCost;
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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class UserDetailsComposerJobCost extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Combobox			bill_center;

	@Wire
	private Doublebox			business_job_cost;

	@Wire
	private Textbox				contractual_level;

	@Wire
	private Datebox				date_from;

	@Wire
	private Datebox				date_to;

	@Wire
	private Doublebox			final_job_cost;
	@Wire
	private Component			grid_details;
	// dao interface
	private IJobCost			jobCostDAO;
	private Person				person_selected;

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

		final JobCost item = this.sw_list.getSelectedItem().getValue();

		this.jobCostDAO.removeJobCost(item.getId());
		Messagebox.show("Centro di costo rimosso", "INFO", Messagebox.OK, Messagebox.INFORMATION);

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
				UserDetailsComposerJobCost.this.person_selected = (Person) arg0.getData();

				// get the dao
				UserDetailsComposerJobCost.this.jobCostDAO = (IJobCost) SpringUtil.getBean(BeansTag.JOB_COST_DAO);

				UserDetailsComposerJobCost.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #sw_link_edit")
	public void modifyItem() {

		this.status_add = false;

		// get selected item
		final JobCost item = this.sw_list.getSelectedItem().getValue();

		// set info about grid
		if (this.person_selected == null) {
			return;
		}

		if (item == null) {
			return;
		}

		this.business_job_cost.setValue(item.getBusiness_job_cost());
		this.final_job_cost.setValue(item.getFinal_job_cost());
		this.date_from.setValue(item.getDate_from());
		this.date_to.setValue(item.getDate_to());
		this.contractual_level.setValue(item.getContractual_level());

		this.bill_center.setValue(item.getBill_center());

		// set bill center type
		final String billCenter_type = item.getBill_center();
		if (billCenter_type != null) {
			for (final Comboitem itm : this.bill_center.getItems()) {
				if (itm.getValue().equals(billCenter_type)) {
					this.bill_center.setSelectedItem(itm);
				}

			}
		}

	}

	@Listen("onClick = #ok_command")
	public void okCommand() {

		if (this.person_selected == null) {
			return;
		}

		if (this.status_add) {

			final JobCost item = new JobCost();

			// setup item with values
			this.setupItemWithValues(item);

			this.jobCostDAO.createJobCost(item);
			// this.createFCForUser(this.person_selected.getId(), item);

			Messagebox.show("Costo orario aggiunto all'utente", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		} else {

			// get selected item
			final JobCost item = this.sw_list.getSelectedItem().getValue();
			if (item == null) {
				return;
			}

			// add values to the items
			this.setupItemWithValues(item);

			this.jobCostDAO.updateJobCost(item);
		}

		// Refresh list task
		this.setInitialView();

	}

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		final List<JobCost> list = this.jobCostDAO.loadJobCostByUser(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<JobCost>(list));

		this.grid_details.setVisible(false);
	}

	private void setupItemWithValues(final JobCost item) {

		item.setBusiness_job_cost((this.business_job_cost.getValue()));
		item.setFinal_job_cost(this.final_job_cost.getValue());
		item.setContractual_level(this.contractual_level.getValue());
		item.setDate_from(this.date_from.getValue());
		item.setDate_to(this.date_to.getValue());
		item.setId_user(this.person_selected.getId());

		// set result communication type
		if (this.bill_center.getSelectedItem() == null) {
			item.setBill_center(null);
		} else {
			final String itm = this.bill_center.getSelectedItem().getValue();
			item.setBill_center(itm);
		}

	}

}
