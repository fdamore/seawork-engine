package org.uario.seaworkengine.zkevent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.TrainingCertificate;
import org.uario.seaworkengine.platform.persistence.dao.TrainingCertificateDAO;
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

public class UserDetailsComposerTrainingCertificate extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Wire
	private Datebox certificate_date;

	@Wire
	private Textbox description;

	@Wire
	private Datebox expiration_date;

	@Wire
	private Component grid_details;

	private final Logger logger = Logger.getLogger(UserDetailsComposerTrainingCertificate.class);

	private Person person_selected;

	// status ADD or MODIFY
	private boolean status_add = false;

	@Wire
	private Listbox sw_list;

	@Wire
	private Textbox title;

	// dao interface
	private TrainingCertificateDAO trainingCertificateDAO;

	@Listen("onClick = #sw_add")
	public void addItem() {

		this.status_add = true;

		this.certificate_date.setValue(null);
		this.expiration_date.setValue(null);
		this.title.setValue("");
		this.description.setValue(null);

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

		this.certificate_date.setValue(item.getCertificate_date());
		this.expiration_date.setValue(item.getExpiration_date());
		this.description.setValue(item.getDescription());
		this.title.setValue(item.getTitle());

	}

	@Listen("onClick = #ok_command")
	public void okCommand() {

		if (this.person_selected == null) {
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

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		final List<TrainingCertificate> list = this.trainingCertificateDAO.loadTrainingCertificate(null, null, null, null,
				this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<TrainingCertificate>(list));

		this.grid_details.setVisible(false);
	}

	private void setupItemWithValues(final TrainingCertificate item) {
		item.setCertificate_date(this.certificate_date.getValue());
		item.setExpiration_date(this.expiration_date.getValue());
		item.setDescription(this.description.getValue());
		item.setTitle(this.title.getValue());
		item.setUser_id(this.person_selected.getId());

	}

}
