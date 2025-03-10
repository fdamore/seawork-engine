package org.uario.seaworkengine.zkevent;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.uario.seaworkengine.model.BillCenter;
import org.uario.seaworkengine.model.JobCost;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.IJobCost;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
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
	private Doublebox			awards;

	@Wire
	private Doublebox			basicsalary;

	@Wire
	private Combobox			bill_center;

	@Wire
	private Doublebox			business_job_cost;

	@Wire
	private Doublebox			contingency;

	@Wire
	private Combobox			contractual_level;

	@Wire
	private Datebox				date_from;
	@Wire
	private Datebox				date_to;
	@Wire
	private Doublebox			edr;

	@Wire
	private Doublebox			final_job_cost;

	private final NumberFormat	format				= NumberFormat.getNumberInstance(Locale.ITALY);

	@Wire
	private Component			grid_details;

	// dao interface
	private IJobCost			jobCostDAO;

	@Wire
	private Textbox				note;

	private Person				person_selected;

	@Wire
	private Doublebox			shots;

	// status ADD or MODIFY
	private boolean				status_add			= false;

	@Wire
	private Listbox				sw_list;

	@Wire
	private Label				total;

	@Listen("onClick = #sw_add")
	public void addItem() {

		this.status_add = true;

		this.bill_center.setSelectedItem(null);
		this.business_job_cost.setValue(null);
		this.final_job_cost.setValue(null);
		this.date_from.setValue(null);
		this.date_to.setValue(null);
		this.contractual_level.setSelectedItem(null);
		this.basicsalary.setValue(null);
		this.contingency.setValue(null);
		this.shots.setValue(null);
		this.edr.setValue(null);
		this.note.setValue(null);
		this.awards.setValue(null);

		this.total.setValue(null);

	}

	private Boolean compareDate(final JobCost jc, final Date from, final Date to) {
		if ((jc.getDate_from() != null) && (jc.getDate_to() != null)) {
			if ((from.compareTo(jc.getDate_from()) >= 0) && (from.compareTo(jc.getDate_to()) <= 0)) {
				return false;
			}
			if ((to.compareTo(jc.getDate_from()) >= 0) && (to.compareTo(jc.getDate_to()) <= 0)) {
				return false;
			}
		}
		return true;
	}

	private Boolean controlDate(final Date from, final Date to) {
		if ((from == null) || (to == null)) {
			return false;
		}

		if (from.compareTo(to) > 0) {
			return false;
		}

		final List<JobCost> list = (List<JobCost>) this.sw_list.getModel();

		int control = 0;
		Integer idSelected = 0;

		if (this.sw_list.getSelectedItem() != null) {
			idSelected = ((JobCost) this.sw_list.getSelectedItem().getValue()).getId();
			control = 1;
		}

		for (final JobCost jc : list) {
			if (control == 1) {
				// edit mode
				if (jc.getId() != idSelected) {
					if (!this.compareDate(jc, from, to)) {
						return false;
					}
				}
			} else {
				// add mode
				if (!this.compareDate(jc, from, to)) {
					return false;
				}
			}
		}
		return true;

	}

	private void deleteItemToUser() {

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final JobCost item = this.sw_list.getSelectedItem().getValue();

		this.jobCostDAO.removeJobCost(item.getId());
		final Map<String, String> params = new HashMap();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

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

	@Listen("onClick = #user_cost")
	public void downloadCSV_user_cost() {

		if ((this.person_selected == null) || (this.person_selected.getId() == null)) {
			return;
		}

		final List<JobCost> list = this.jobCostDAO.loadJobCostByUser(this.person_selected.getId());
		final StringBuilder builder = UtilityCSV.downloadCSV_user_cost(list);
		Filedownload.save(builder.toString(), "application/text", "info_cost.csv");

	}

	private void insertItem() {
		if (UserDetailsComposerJobCost.this.person_selected == null) {
			return;
		}
		if (UserDetailsComposerJobCost.this.status_add) {

			final JobCost item = new JobCost();

			// setup item with values
			UserDetailsComposerJobCost.this.setupItemWithValues(item);

			UserDetailsComposerJobCost.this.jobCostDAO.createJobCost(item);
			// this.createFCForUser(this.person_selected.getId(),
			// item);

			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Costo orario aggiunto all'utente", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

		} else {

			// get selected item
			final JobCost item = UserDetailsComposerJobCost.this.sw_list.getSelectedItem().getValue();
			if (item == null) {
				return;
			}

			// add values to the items
			UserDetailsComposerJobCost.this.setupItemWithValues(item);

			UserDetailsComposerJobCost.this.jobCostDAO.updateJobCost(item);
		}

		// Refresh list task
		UserDetailsComposerJobCost.this.setInitialView();
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

		this.contractual_level.setSelectedItem(null);
		if (item.getContractual_level() != null) {
			this.contractual_level.setSelectedIndex(Integer.parseInt(item.getContractual_level()) - 1);
		}

		this.basicsalary.setValue(item.getBasicsalary());
		this.contingency.setValue(item.getContingency());
		this.shots.setValue(item.getShots());
		this.edr.setValue(item.getEdr());
		this.awards.setValue(item.getAwards());
		this.note.setValue(item.getNote());

		this.setTotalLabel();

		this.bill_center.setSelectedItem(null);
		if (item.getBill_center() != null) {
			final BillCenter billcenter = this.jobCostDAO.loadBillCenter(item.getBill_center());

			final String description = billcenter.getDescription();

			if (description != null) {
				for (final Comboitem itm : this.bill_center.getItems()) {
					if (itm.getLabel().equals(description)) {
						this.bill_center.setSelectedItem(itm);
					}

				}
			}

		}

	}

	@Listen("onClick = #ok_command")
	public void okCommand() {

		if (this.bill_center.getSelectedItem() == null) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Inserire centro di costo!", "ERROR", buttons, null, Messagebox.ERROR, null, null, params);

			return;
		}

		if (!this.controlDate(this.date_from.getValue(), this.date_to.getValue())) {
			final Map<String, String> params = new HashMap();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[2];
			buttons[0] = Messagebox.Button.OK;
			buttons[1] = Messagebox.Button.CANCEL;

			Messagebox.show("Data mancante, procedere?", "CONFERMA INSERIMENTO", buttons, null, Messagebox.EXCLAMATION, null, new EventListener() {
				@Override
				public void onEvent(final Event e) {
					if (Messagebox.ON_OK.equals(e.getName())) {

						UserDetailsComposerJobCost.this.insertItem();

					} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
						// Cancel is clicked
					}
				}
			}, params);

		} else {
			this.insertItem();
		}

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
							UserDetailsComposerJobCost.this.deleteItemToUser();
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

		final List<JobCost> list = this.jobCostDAO.loadJobCostByUser(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<>(list));

		final List<BillCenter> billCenterList = this.jobCostDAO.listAllBillCenter(null);
		if (billCenterList != null) {
			this.bill_center.setModel(new ListModelList<>(billCenterList));
		}

		this.grid_details.setVisible(false);
	}

	@Listen("onChange = #basicsalary, #contingency, #shots, #edr; onOK = #basicsalary, #contingency, #shots, #edr")
	public void setTotalLabel() {

		Double sum = 0.0;

		if (this.basicsalary.getValue() != null) {
			sum = this.basicsalary.getValue();
		}

		if (this.contingency.getValue() != null) {
			sum += this.contingency.getValue();
		}

		if (this.shots.getValue() != null) {
			sum += this.shots.getValue();
		}

		if (this.edr.getValue() != null) {
			sum += this.edr.getValue();
		}

		this.total.setValue(this.format.format(sum));

	}

	private void setupItemWithValues(final JobCost item) {

		item.setBusiness_job_cost((this.business_job_cost.getValue()));
		item.setFinal_job_cost(this.final_job_cost.getValue());
		if (this.contractual_level.getSelectedItem() != null) {
			item.setContractual_level(this.contractual_level.getSelectedItem().getValue().toString());
		}

		item.setDate_from(this.date_from.getValue());
		item.setDate_to(this.date_to.getValue());
		item.setId_user(this.person_selected.getId());

		item.setBasicsalary(this.basicsalary.getValue());
		item.setContingency(this.contingency.getValue());
		item.setShots(this.shots.getValue());
		item.setEdr(this.edr.getValue());
		item.setNote(this.note.getValue());
		item.setAwards(this.awards.getValue());

		// set result communication type
		if (this.bill_center.getSelectedItem() == null) {
			item.setBill_center(null);
		} else {
			final BillCenter billCenterSelected = this.bill_center.getSelectedItem().getValue();

			item.setBill_center(billCenterSelected.getId());

		}

	}

}
