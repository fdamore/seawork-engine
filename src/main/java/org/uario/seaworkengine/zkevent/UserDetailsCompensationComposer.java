package org.uario.seaworkengine.zkevent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.UserCompensation;
import org.uario.seaworkengine.platform.persistence.dao.UserCompensationDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class UserDetailsCompensationComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID			= 1L;

	@Wire
	private Button				addCompensation;

	@Wire
	private Button				closeModifyCompensation;

	@Wire
	private Datebox				date_submitUser;

	@Wire
	private Component			grid_details;

	@Wire
	private Textbox				note_compUser;

	private Person				person_selected;

	@Wire
	private Button				saveCompensation;

	@Wire
	private Combobox			select_year;

	@Wire
	private Listbox				sw_list;

	@Wire
	private Doublebox			time_compUser;

	// dao interface
	private UserCompensationDAO	userCompensationDAO;

	private UserCompensation	userCompensationInUpdate	= null;

	@Listen("onClick = #closeModifyCompensation")
	public void closeModifyCompensation() {
		this.grid_details.setVisible(false);
		this.saveCompensation.setVisible(false);
		this.closeModifyCompensation.setVisible(false);
		this.addCompensation.setVisible(true);

		this.date_submitUser.setValue(null);
		this.time_compUser.setValue(null);

		this.userCompensationInUpdate = null;
	}

	@Listen("onClick = #sw_link_delete")
	public void deleteCompensation() {
		if (this.sw_list.getSelectedItem() != null) {
			final UserCompensation userCompensation = this.sw_list.getSelectedItem().getValue();
			this.userCompensationDAO.deleteUserCompensation(userCompensation.getId());
			this.setInitialView();

		}
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
				UserDetailsCompensationComposer.this.person_selected = (Person) arg0.getData();

				// get the dao
				UserDetailsCompensationComposer.this.userCompensationDAO = (UserCompensationDAO) SpringUtil.getBean(BeansTag.USER_COMPENSATION_DAO);

				// set year in combobox
				final Integer todayYear = Utility.getYear(Calendar.getInstance().getTime());
				final ArrayList<String> years = new ArrayList<String>();

				years.add("TUTTI");

				for (Integer i = 2014; i <= (todayYear + 2); i++) {
					years.add(i.toString());
				}

				UserDetailsCompensationComposer.this.select_year.setModel(new ListModelList<String>(years));

				UserDetailsCompensationComposer.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #sw_link_edit")
	public void modifyCompensation() {
		if (this.sw_list.getSelectedItem() != null) {
			this.userCompensationInUpdate = this.sw_list.getSelectedItem().getValue();
			this.date_submitUser.setValue(this.userCompensationInUpdate.getDate_submit());
			this.time_compUser.setValue(this.userCompensationInUpdate.getTime_comp());
			this.saveCompensation.setVisible(true);
			this.closeModifyCompensation.setVisible(true);
		}
	}

	@Listen("onClick= #saveCompensation")
	public void saveCompensation() {
		if ((this.person_selected != null) && (this.date_submitUser.getValue() != null) && (this.time_compUser.getValue() != null)) {

			if (this.userCompensationInUpdate == null) {

				final UserCompensation userCompensation = new UserCompensation();
				userCompensation.setDate_submit(this.date_submitUser.getValue());
				userCompensation.setTime_comp(this.time_compUser.getValue());
				userCompensation.setId_user(this.person_selected.getId());
				userCompensation.setNote(this.note_compUser.getValue());
				this.userCompensationDAO.createUserCompensation(userCompensation);

			} else {
				this.userCompensationInUpdate.setDate_submit(this.date_submitUser.getValue());
				this.userCompensationInUpdate.setTime_comp(this.time_compUser.getValue());
				this.userCompensationDAO.updateUserCompensation(this.userCompensationInUpdate);
			}

			this.setInitialView();

		} else {
			Messagebox.show("Verificare valori inseriti.", "Errore", Messagebox.OK, Messagebox.EXCLAMATION);
		}
	}

	@Listen("onChange =#select_year")
	public void selectedYear() {
		if (this.select_year.getSelectedItem() != null) {

			final String yearSelected = this.select_year.getSelectedItem().getValue();

			if (!yearSelected.equals("TUTTI")) {

				final Integer year = Integer.parseInt(yearSelected);

				final List<UserCompensation> list = this.userCompensationDAO.loadAllUserCompensationByUserId(this.person_selected.getId(), year);

				if (list != null) {
					final ListModelList<UserCompensation> model = new ListModelList<UserCompensation>(list);

					this.sw_list.setModel(model);
				}

			} else {
				this.setInitialView();
			}
		}
	}

	protected void setInitialView() {
		this.grid_details.setVisible(false);
		this.saveCompensation.setVisible(false);
		this.addCompensation.setVisible(true);
		this.closeModifyCompensation.setVisible(false);

		this.date_submitUser.setValue(null);
		this.time_compUser.setValue(null);

		this.userCompensationInUpdate = null;

		this.select_year.setSelectedItem(null);

		final List<UserCompensation> list = this.userCompensationDAO.loadAllUserCompensationByUserId(this.person_selected.getId());

		if (list != null) {
			final ListModelList<UserCompensation> model = new ListModelList<UserCompensation>(list);

			this.sw_list.setModel(model);
		}

	}

}
