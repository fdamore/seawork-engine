package org.uario.seaworkengine.zkevent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.TrainingCertificate;
import org.uario.seaworkengine.platform.persistence.dao.TrainingCertificateDAO;
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

public class UserDetailsComposerTrainingCertificate extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long		serialVersionUID	= 1L;

	@Wire
	private Datebox					certificate_date;

	@Wire
	private Textbox					description;

	@Wire
	private Datebox					expiration_date;

	@Wire
	private Textbox					full_text_search;

	@Wire
	private Component				grid_details;

	private final Logger			logger				= Logger.getLogger(UserDetailsComposerTrainingCertificate.class);

	@Wire
	private Textbox					note;

	private Person					person_selected;

	// status ADD or MODIFY
	private boolean					status_add			= false;

	@Wire
	private Listbox					sw_list;

	@Wire
	private Textbox					title;

	@Wire
	private Textbox					trainer;

	@Wire
	private Textbox					trainer_type;

	@Wire
	private Textbox					training_level;

	@Wire
	private Textbox					training_task;

	// dao interface
	private TrainingCertificateDAO	trainingCertificateDAO;

	@Listen("onClick = #sw_add")
	public void addItem() {

		this.status_add = true;

		this.certificate_date.setValue(null);
		this.expiration_date.setValue(null);
		this.title.setValue("");
		this.description.setValue(null);
		this.trainer_type.setValue(null);
		this.trainer.setValue(null);
		this.training_task.setValue(null);
		this.training_level.setValue(null);
		this.note.setValue(null);

	}

	private void deleteItemToUser() {

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final TrainingCertificate item = this.sw_list.getSelectedItem().getValue();

		this.trainingCertificateDAO.removeTrainingCertificate(item.getId());

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
				UserDetailsComposerTrainingCertificate.this.person_selected = (Person) arg0.getData();

				// get the dao
				UserDetailsComposerTrainingCertificate.this.trainingCertificateDAO = (TrainingCertificateDAO) SpringUtil
						.getBean(BeansTag.TRAINING_CERTIFICATE_DAO);

				UserDetailsComposerTrainingCertificate.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #user_csv")
	public void downloadCSV_user_csv() {

		if ((this.person_selected == null) || (this.person_selected.getId() == null)) {
			return;
		}

		final List<TrainingCertificate> list = this.trainingCertificateDAO.loadTrainingCertificate(null, null, null, this.person_selected.getId());
		final StringBuilder builder = UtilityCSV.downloadCSV_user_formazione(list);
		Filedownload.save(builder.toString(), "application/text", "info_formazione.csv");

	}

	@Listen("onClick = #sw_link_edit")
	public void modifyItem() {

		this.status_add = false;

		// get selected item
		final TrainingCertificate item = this.sw_list.getSelectedItem().getValue();

		// set info about grid
		if (this.person_selected == null) {
			return;
		}

		if (item == null) {
			return;
		}

		if ((this.certificate_date.getValue() != null) && (this.expiration_date.getValue() != null)
				&& this.certificate_date.getValue().after(this.expiration_date.getValue())) {
			return;
		}

		this.certificate_date.setValue(item.getCertificate_date());
		this.expiration_date.setValue(item.getExpiration_date());
		this.description.setValue(item.getDescription());
		this.title.setValue(item.getTitle());

		String trainerType = item.getTrainer_type();

		if ((trainerType != null) && trainerType.equals("--")) {
			trainerType = null;
		}

		this.trainer_type.setValue(trainerType);
		this.trainer.setValue(item.getTrainer());
		this.training_task.setValue(item.getTraining_task());
		this.training_level.setValue(item.getTraining_level());
		this.note.setValue(item.getNote());

	}

	@Listen("onClick = #ok_command")
	public void okCommand() {

		if (this.person_selected == null) {
			return;
		}

		if ((this.certificate_date.getValue() != null) && (this.expiration_date.getValue() != null)
				&& this.certificate_date.getValue().after(this.expiration_date.getValue())) {
			return;
		}

		if (this.status_add) {

			final TrainingCertificate item = new TrainingCertificate();

			// setup item with values
			this.setupItemWithValues(item);

			this.trainingCertificateDAO.createTrainingCertificate(item);

		} else {

			// get selected item
			final TrainingCertificate item = this.sw_list.getSelectedItem().getValue();
			if (item == null) {
				return;
			}

			// add values to the items
			this.setupItemWithValues(item);

			this.trainingCertificateDAO.updateTrainingCertificate(item);
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
							UserDetailsComposerTrainingCertificate.this.deleteItemToUser();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				}, params);

	}

	@Listen("onOK = #full_text_search")
	public void searchText() {
		if (this.person_selected == null) {
			return;
		}

		final List<TrainingCertificate> list = this.trainingCertificateDAO.loadTrainingCertificate(null, this.full_text_search.getValue(), null,
				this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<>(list));

		this.grid_details.setVisible(false);
	}

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		this.full_text_search.setValue(null);

		if (this.person_selected == null) {
			return;
		}

		final List<TrainingCertificate> list = this.trainingCertificateDAO.loadTrainingCertificate(null, this.full_text_search.getValue(), null,
				this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<>(list));

		this.grid_details.setVisible(false);
	}

	private void setupItemWithValues(final TrainingCertificate item) {

		item.setCertificate_date(this.certificate_date.getValue());
		item.setExpiration_date(this.expiration_date.getValue());
		item.setDescription(this.description.getValue());
		item.setTitle(this.title.getValue());
		item.setUser_id(this.person_selected.getId());

		String trainerType = this.trainer_type.getValue();

		if ((trainerType != null) && trainerType.equals("--")) {
			trainerType = null;
		}

		item.setTrainer_type(trainerType);
		item.setTrainer(this.trainer.getValue());
		item.setTraining_task(this.training_task.getValue());
		item.setTraining_level(this.training_level.getValue());
		item.setNote(this.note.getValue());

	}

	@Listen("onClick = #show_expirated")
	public void showExpiratedCertificates() {

		if (this.person_selected == null) {
			return;
		}

		final List<TrainingCertificate> list = this.trainingCertificateDAO.loadTrainingCertificate(null, this.full_text_search.getValue(), false,
				this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<>(list));

		this.grid_details.setVisible(false);
	}

	@Listen("onClick = #show_valid")
	public void showValidCertificates() {

		if (this.person_selected == null) {
			return;
		}

		final List<TrainingCertificate> list = this.trainingCertificateDAO.loadTrainingCertificate(null, this.full_text_search.getValue(), true,
				this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<>(list));

		this.grid_details.setVisible(false);
	}

}
