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
		grid_details.setVisible(false);
		saveCompensation.setVisible(false);
		closeModifyCompensation.setVisible(false);
		addCompensation.setVisible(true);

		date_submitUser.setValue(Calendar.getInstance().getTime());
		time_compUser.setValue(null);
		note_compUser.setValue(null);

		userCompensationInUpdate = null;
	}

	@Listen("onClick = #sw_link_delete")
	public void deleteCompensation() {
		if (sw_list.getSelectedItem() != null) {
			final UserCompensation userCompensation = sw_list.getSelectedItem().getValue();
			userCompensationDAO.deleteUserCompensation(userCompensation.getId());
			setInitialView();

		}
	}

	@Override
	public void doFinally() throws Exception {

		getSelf().addEventListener(ZkEventsTag.onShowUsers, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// get selected person
				if ((arg0.getData() == null) || !(arg0.getData() instanceof Person)) {
					return;
				}
				person_selected = (Person) arg0.getData();

				// get the dao
				userCompensationDAO = (UserCompensationDAO) SpringUtil.getBean(BeansTag.USER_COMPENSATION_DAO);

				// set year in combobox
				final Integer todayYear = Utility.getYear(Calendar.getInstance().getTime());
				final ArrayList<String> years = new ArrayList<String>();

				years.add("TUTTI");

				for (Integer i = 2014; i <= (todayYear + 2); i++) {
					years.add(i.toString());
				}

				select_year.setModel(new ListModelList<String>(years));

				UserDetailsCompensationComposer.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #sw_link_edit")
	public void modifyCompensation() {
		if (sw_list.getSelectedItem() != null) {
			userCompensationInUpdate = sw_list.getSelectedItem().getValue();
			date_submitUser.setValue(userCompensationInUpdate.getDate_submit());
			time_compUser.setValue(userCompensationInUpdate.getTime_comp());
			note_compUser.setValue(userCompensationInUpdate.getNote());

			saveCompensation.setVisible(true);
			closeModifyCompensation.setVisible(true);

		}
	}

	@Listen("onClick= #saveCompensation")
	public void saveCompensation() {
		if ((person_selected != null) && (date_submitUser.getValue() != null) && (time_compUser.getValue() != null)) {

			if (userCompensationInUpdate == null) {

				final UserCompensation userCompensation = new UserCompensation();
				userCompensation.setDate_submit(date_submitUser.getValue());
				userCompensation.setTime_comp(time_compUser.getValue());
				userCompensation.setId_user(person_selected.getId());
				userCompensation.setNote(note_compUser.getValue());

				userCompensationDAO.createUserCompensation(userCompensation);

			} else {
				userCompensationInUpdate.setDate_submit(date_submitUser.getValue());
				userCompensationInUpdate.setTime_comp(time_compUser.getValue());
				userCompensationDAO.updateUserCompensation(userCompensationInUpdate);
			}

			setInitialView();

		} else {
			Messagebox.show("Verificare valori inseriti.", "Errore", Messagebox.OK, Messagebox.EXCLAMATION);
		}
	}

	@Listen("onChange =#select_year")
	public void selectedYear() {
		if (select_year.getSelectedItem() != null) {

			final String yearSelected = select_year.getSelectedItem().getValue();

			if (!yearSelected.equals("TUTTI")) {

				final Integer year = Integer.parseInt(yearSelected);

				final List<UserCompensation> list = userCompensationDAO.loadAllUserCompensationByUserId(person_selected.getId(),
						year);

				if (list != null) {
					final ListModelList<UserCompensation> model = new ListModelList<UserCompensation>(list);

					sw_list.setModel(model);
				}

			} else {
				setInitialView();
			}
		}
	}

	protected void setInitialView() {
		grid_details.setVisible(false);
		saveCompensation.setVisible(false);
		addCompensation.setVisible(true);
		closeModifyCompensation.setVisible(false);

		date_submitUser.setValue(Calendar.getInstance().getTime());
		time_compUser.setValue(null);
		note_compUser.setValue(null);

		userCompensationInUpdate = null;

		select_year.setSelectedItem(null);

		final List<UserCompensation> list = userCompensationDAO.loadAllUserCompensationByUserId(person_selected.getId());

		if (list != null) {
			final ListModelList<UserCompensation> model = new ListModelList<UserCompensation>(list);

			sw_list.setModel(model);
		}

	}

}
