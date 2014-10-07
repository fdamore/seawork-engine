package org.uario.seaworkengine.zkevent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Contestation;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.IContestation;
import org.uario.seaworkengine.platform.persistence.dao.IParams;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ContestationTag;
import org.uario.seaworkengine.utility.ParamsTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class UserDetailsComposerCons extends SelectorComposer<Component> {

	private class ContestationDoc {
		/**
		 * The file to add
		 */
		private byte[]	file_doc;

		private String	format;

		public byte[] getFile_doc() {
			return this.file_doc;
		}

		public String getFormat() {
			return this.format;
		}

		public void setFile_doc(final byte[] file_doc) {
			this.file_doc = file_doc;
		}

		public void setFormat(final String format) {
			this.format = format;
		}

	}

	private final static String	BUTTON_FINAL_SCLASS		= "btn-danger";

	private final static String	BUTTON_INITIAL_SCLASS	= "btn-success";

	/**
	 *
	 */
	private static final long	serialVersionUID		= 1L;

	// dao interface
	private IContestation		contestationDAO;

	@Wire
	private Component			current_document;

	private ContestationDoc		currentDoc				= null;

	@Wire
	private Datebox				date_contestation;

	@Wire
	private Button				docupload;

	@Wire
	private Component			grid_details;

	private final Logger		logger					= Logger.getLogger(UserDetailsComposerCons.class);

	@Wire
	private Textbox				note;

	private IParams				paramsDAO;

	private Person				person_selected;

	// status ADD or MODIFY
	private boolean				status_add				= false;

	@Wire
	private Datebox				stop_from;

	@Wire
	private Datebox				stop_to;

	@Wire
	private Listbox				sw_list;

	@Wire
	private Combobox			typ;

	@Listen("onClick = #sw_add")
	public void addItem() {

		this.typ.setValue(ContestationTag.NESSUNA);

		this.stop_from.setValue(null);
		this.stop_to.setValue(null);
		this.date_contestation.setValue(Calendar.getInstance().getTime());

		this.stop_from.setDisabled(true);
		this.stop_to.setDisabled(true);

		this.note.setValue("");

		this.status_add = true;

		// set null current contestaion doc
		this.currentDoc = null;

		// set link to current document
		this.current_document.setVisible(false);

		// set initial sclass
		this.docupload.setSclass(UserDetailsComposerCons.BUTTON_INITIAL_SCLASS);

	}

	@Listen("onChange = #typ")
	public void changeTypContestation() {

		if (this.typ.getSelectedItem() == null) {
			return;
		}

		// set date box
		this.setDateBoxs();

	}

	@Listen("onClick = #delete_command")
	public void deleteItemToUser() {

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final Contestation item = this.sw_list.getSelectedItem().getValue();

		this.contestationDAO.removeContestation(item.getId());

		Messagebox.show("Informazione rimossa", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		// remove doc
		if (item.getFile_name() != null) {
			final String repo = this.paramsDAO.getParam(ParamsTag.REPO_DOC);
			final String global_file_name = repo + item.getFile_name();
			final File file = new File(global_file_name);
			file.delete();

		}

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
				UserDetailsComposerCons.this.person_selected = (Person) arg0.getData();

				// get the dao
				UserDetailsComposerCons.this.contestationDAO = (IContestation) SpringUtil.getBean(BeansTag.CONTESTATION_DAO);
				UserDetailsComposerCons.this.paramsDAO = (IParams) SpringUtil.getBean(BeansTag.PARAMS_DAO);

				UserDetailsComposerCons.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #sw_download_list, #current_document")
	public void downloadDocProcedure() throws FileNotFoundException {
		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		final Contestation item = this.sw_list.getSelectedItem().getValue();

		if (item.getFile_name() == null) {
			Messagebox.show("Nessun documento associato", "INFO", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		// download
		final String repo = this.paramsDAO.getParam(ParamsTag.REPO_DOC);
		final String global_file_name = repo + item.getFile_name();
		final File file = new File(global_file_name);
		if (file.exists()) {
			Filedownload.save(file, null);
		}
	}

	@Listen("onClick = #sw_link_edit")
	public void modifyItem() {

		this.status_add = false;

		// get selected item
		final Contestation item = this.sw_list.getSelectedItem().getValue();

		// set info about grid
		if (this.person_selected == null) {
			return;
		}

		if (item == null) {
			return;
		}

		this.note.setValue(item.getNote());
		this.date_contestation.setValue(item.getDate_contestation());
		this.stop_from.setValue(item.getStop_from());
		this.stop_to.setValue(item.getStop_to());
		this.typ.setValue(item.getTyp());

		// set null current contestaion doc
		this.currentDoc = null;

		// set link to current document
		if (item.getFile_name() == null) {
			this.current_document.setVisible(false);
		} else {
			this.current_document.setVisible(true);

		}

		// set initial sclass
		this.docupload.setSclass(UserDetailsComposerCons.BUTTON_INITIAL_SCLASS);

		// set date box
		this.setDateBoxs();

	}

	@Listen("onClick = #ok_command")
	public void okCommand() throws IOException {

		if (this.person_selected == null) {
			return;
		}

		if (this.status_add) {

			// adding branch

			final Contestation item = new Contestation();

			if (this.typ.getSelectedItem().getValue() == null) {
				Messagebox.show("Inserire un tipo di contestazione!");
				return;
			}

			if (this.date_contestation.getValue() == null) {
				Messagebox.show("Data della contestazione mancante!");
				return;
			}

			if (this.typ.getSelectedItem().getValue().equals(ContestationTag.SOSPENSIONE)) {

				if ((this.stop_to.getValue() == null) || (this.stop_from.getValue() == null)) {
					Messagebox.show("Intervallo date sospensione non completo!");
					return;
				}

				if (this.stop_from.getValue().compareTo(this.stop_to.getValue()) > 0) {
					Messagebox.show("Intervallo date sospensione errato!");
					return;
				}
			}

			// setup item with values
			this.setupItemWithValues(item);

			// set user_id
			item.setId_user(this.person_selected.getId());

			// create contestation
			this.contestationDAO.createContestation(item);

			Messagebox.show("Contestazione aggiunta all'utente", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		} else {

			// modify branch

			if (this.typ.getSelectedItem() == null) {
				Messagebox.show("Inserire un tipo di contestazione!");
				return;
			}

			// get selected item
			final Contestation item = this.sw_list.getSelectedItem().getValue();
			if (item == null) {
				return;
			}

			if (this.date_contestation.getValue() == null) {
				Messagebox.show("Data della contestazione mancante!");
				return;
			}

			if (this.typ.getSelectedItem().getValue().equals(ContestationTag.SOSPENSIONE)) {

				if ((this.stop_to.getValue() == null) || (this.stop_from.getValue() == null)) {
					Messagebox.show("Intervallo date sospensione non completo!");
					return;
				}

				if (this.stop_from.getValue().compareTo(this.stop_to.getValue()) > 0) {
					Messagebox.show("Intervallo date sospensione errato!");
					return;
				}
			}

			// delete existing file if any and if required
			if ((item.getFile_name() != null) && (this.currentDoc != null)) {
				// TODO: delete old file
			}

			// add values to the items
			this.setupItemWithValues(item);

			this.contestationDAO.updateContestation(item);
		}

		// Refresh list task
		this.setInitialView();

	}

	/**
	 * set datebox in view
	 */
	private void setDateBoxs() {
		final String info = this.typ.getSelectedItem().getValue();
		if (info.equals(ContestationTag.SOSPENSIONE)) {

			this.stop_from.setDisabled(false);
			this.stop_to.setDisabled(false);

		} else {
			this.stop_from.setDisabled(true);
			this.stop_to.setDisabled(true);
			this.stop_from.setValue(null);
			this.stop_to.setValue(null);

		}
	}

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		final List<Contestation> list = this.contestationDAO.loadUserContestation(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<Contestation>(list));

		this.grid_details.setVisible(false);

		// set null current contestaion doc
		this.currentDoc = null;

		// set link to current docuemnt
		this.current_document.setVisible(false);
	}

	/**
	 * Set value
	 *
	 * @param item
	 * @throws FileNotFoundException
	 */
	private void setupItemWithValues(final Contestation item) throws IOException {
		item.setDate_contestation(this.date_contestation.getValue());
		item.setNote(this.note.getValue());
		item.setStop_from(this.stop_from.getValue());
		item.setStop_to(this.stop_to.getValue());
		item.setTyp(this.typ.getValue());

		// set file name
		if (this.currentDoc != null) {
			final String repo = this.paramsDAO.getParam(ParamsTag.REPO_DOC);
			final String filename = "" + System.currentTimeMillis() + "." + this.currentDoc.getFormat();

			final String global_file_name = repo + filename;

			FileOutputStream stream = null;
			try {

				stream = new FileOutputStream(global_file_name);
				IOUtils.write(this.currentDoc.getFile_doc(), stream);

			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (final IOException ignore) {

					}
				}
			}

			item.setFile_name(filename);
		}

	}

	@Listen("onUpload = #docupload")
	public void uploadFile(final UploadEvent evt) throws IOException {

		Reader reader = null;
		InputStream stream = null;

		final String format = evt.getMedia().getFormat();
		byte[] byteDoc = null;

		try {

			if (!evt.getMedia().isBinary()) {
				if (evt.getMedia().inMemory()) {

					final String info = evt.getMedia().getStringData();
					byteDoc = info.getBytes();

				} else {
					reader = evt.getMedia().getReaderData();
					byteDoc = IOUtils.toByteArray(reader);

				}
			} else {
				if (evt.getMedia().inMemory()) {

					byteDoc = evt.getMedia().getByteData();

				} else {

					stream = evt.getMedia().getStreamData();
					byteDoc = IOUtils.toByteArray(stream);

				}
			}

			this.currentDoc = new ContestationDoc();
			this.currentDoc.setFile_doc(byteDoc);
			this.currentDoc.setFormat(format);

			// set initial sclass
			this.docupload.setSclass(UserDetailsComposerCons.BUTTON_FINAL_SCLASS);

		} finally {

			try {

				if (reader != null) {
					reader.close();
				}

				if (stream != null) {
					stream.close();
				}

			}

			catch (final IOException ignore) {

			}

		}
	}
}
