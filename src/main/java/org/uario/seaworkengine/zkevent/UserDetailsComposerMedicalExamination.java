package org.uario.seaworkengine.zkevent;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.MedicalExamination;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.MedicalExaminationDAO;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class UserDetailsComposerMedicalExamination extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Wire
	private Datebox date_examination;

	@Wire
	private Component grid_details;

	@Wire
	private Label infoMedicalExamination;

	private final Logger logger = Logger.getLogger(UserDetailsComposerMedicalExamination.class);

	// dao interface
	private MedicalExaminationDAO medicalExaminationDAO;

	@Wire
	private Datebox next_date_examination;

	@Wire
	private Textbox note_examination;

	private Person person_selected;

	@Wire
	private Textbox prescriptions;

	@Wire
	private Textbox result_examination;

	// status ADD or MODIFY
	private boolean status_add = false;

	@Wire
	private Listbox sw_list;

	@Listen("onClick = #sw_add")
	public void addItem() {

		this.status_add = true;

		this.date_examination.setValue(null);
		this.next_date_examination.setValue(null);
		this.prescriptions.setValue("");
		this.result_examination.setValue(null);

		this.note_examination.setValue(null);

	}

	private void deleteItemToUser() {

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final MedicalExamination item = this.sw_list.getSelectedItem().getValue();

		this.medicalExaminationDAO.removeMedicalExamination(item.getId());

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
				UserDetailsComposerMedicalExamination.this.person_selected = (Person) arg0.getData();

				// get the dao
				UserDetailsComposerMedicalExamination.this.medicalExaminationDAO = (MedicalExaminationDAO) SpringUtil
						.getBean(BeansTag.MEDICAL_EXAMINATION_DAO);

				UserDetailsComposerMedicalExamination.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #sw_link_edit")
	public void modifyItem() {

		this.status_add = false;

		// get selected item
		final MedicalExamination item = this.sw_list.getSelectedItem().getValue();

		// set info about grid
		if (this.person_selected == null) {
			return;
		}

		if (item == null) {
			return;
		}

		this.date_examination.setValue(item.getDate_examination());
		this.next_date_examination.setValue(item.getNext_date_examination());
		this.note_examination.setValue(item.getNote_examination());

		this.result_examination.setValue(item.getResult_examination());

		this.prescriptions.setValue(item.getPrescriptions());

	}

	@Listen("onClick = #ok_command")
	public void okCommand() {

		if (this.person_selected == null) {
			return;
		}

		if (this.status_add) {

			final MedicalExamination item = new MedicalExamination();

			// setup item with values
			this.setupItemWithValues(item);

			this.medicalExaminationDAO.createMedicalExamination(item);

		} else {

			// get selected item
			final MedicalExamination item = this.sw_list.getSelectedItem().getValue();
			if (item == null) {
				return;
			}

			// add values to the items
			this.setupItemWithValues(item);

			this.medicalExaminationDAO.updateMedicalExamination(item);
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
					UserDetailsComposerMedicalExamination.this.deleteItemToUser();
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

		final List<MedicalExamination> list = this.medicalExaminationDAO.loadMedicalExaminationByUserId(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<MedicalExamination>(list));

		this.grid_details.setVisible(false);
	}

	@Listen("onChange=#date_examination; onOK=#date_examination")
	public void setNextDateExamination() {
		if (this.date_examination.getValue() == null) {
			return;
		}
		final Calendar cal = Calendar.getInstance();
		cal.setTime(this.date_examination.getValue());

		cal.add(Calendar.YEAR, 1);

		this.next_date_examination.setValue(cal.getTime());

	}

	private void setupItemWithValues(final MedicalExamination item) {
		item.setDate_examination(this.date_examination.getValue());
		item.setNext_date_examination(this.next_date_examination.getValue());
		item.setNote_examination(this.note_examination.getValue());

		item.setResult_examination(this.result_examination.getValue());

		item.setPrescriptions(this.prescriptions.getValue());
		item.setIduser(this.person_selected.getId());

	}

}
