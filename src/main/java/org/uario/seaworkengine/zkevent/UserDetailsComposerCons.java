package org.uario.seaworkengine.zkevent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Contestation;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.platform.persistence.dao.IContestation;
import org.uario.seaworkengine.platform.persistence.dao.IParams;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ContestationTag;
import org.uario.seaworkengine.utility.ParamsTag;
import org.uario.seaworkengine.utility.UserStatusTag;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;

public class UserDetailsComposerCons extends SelectorComposer<Component> {

	/**
	 * used for internal doc preocess
	 *
	 * @author francesco
	 *
	 */
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

	/**
	 * Used to send a cons to "Rapporto Lavorativo"
	 *
	 * @author francesco
	 *
	 */
	public class ContestationMessage {

		private Date	date_modified;

		private String	status;

		public Date getDate_modified() {
			return this.date_modified;
		}

		public String getStatus() {
			return this.status;
		}

		public void setDate_modified(final Date date_modified) {
			this.date_modified = date_modified;
		}

		public void setStatus(final String item) {
			this.status = item;
		}

	}

	private static final String	ALL_ITEM				= "ANNULLA FILTRO";

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
	private Datebox				date_bp;

	@Wire
	private Datebox				date_contestation;

	@Wire
	private Datebox				date_penalty;

	@Wire
	private Textbox				description;

	@Wire
	private Button				docupload;

	@Wire
	private Component			grid_details;

	private final Logger		logger					= Logger.getLogger(UserDetailsComposerCons.class);

	@Wire
	private Textbox				note;

	private IParams				paramsDAO;

	private Person				person_selected;

	@Wire
	private Textbox				prot;

	@Wire
	private Textbox				prot_penalty;

	@Wire
	private Checkbox			recall;

	@Wire
	private Datebox				search_date_penalty;

	@Wire
	private Combobox			select_year;
	// status ADD or MODIFY
	private boolean				status_add				= false;

	private Date				status_date_modified	= null;

	private String				status_upload			= "";

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

		this.date_penalty.setValue(null);

		// date BP
		this.date_bp.setValue(null);

		this.stop_from.setDisabled(true);
		this.stop_to.setDisabled(true);

		this.recall.setChecked(Boolean.FALSE);

		this.note.setValue("");
		this.description.setValue("");

		this.prot.setValue(null);
		this.prot_penalty.setValue(null);

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

	private void deleteItemToUser() {

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final Contestation item = this.sw_list.getSelectedItem().getValue();

		this.contestationDAO.removeContestation(item.getId());

		final Map<String, String> params = new HashMap<>();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

		Messagebox.show("Informazione rimossa", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

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
				UserDetailsComposerCons.this.contestationDAO = (IContestation) SpringUtil
						.getBean(BeansTag.CONTESTATION_DAO);
				UserDetailsComposerCons.this.paramsDAO = (IParams) SpringUtil.getBean(BeansTag.PARAMS_DAO);

				UserDetailsComposerCons.this.setInitialView();

			}
		});

		// set year in combobox
		final Integer todayYear = Utility.getYear(Calendar.getInstance().getTime());
		final ArrayList<String> years = new ArrayList<>();

		years.add(UserDetailsComposerCons.ALL_ITEM);

		for (Integer i = 2000; i <= (todayYear + 2); i++) {
			years.add(i.toString());
		}

		this.select_year.setModel(new ListModelList<>(years));

	}

	@Listen("onClick = #sw_download_list, #current_document")
	public void downloadDocProcedure() throws FileNotFoundException {
		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		final Contestation item = this.sw_list.getSelectedItem().getValue();

		if (item.getFile_name() == null) {
			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Nessun documento associato", "ERROR", buttons, null, Messagebox.EXCLAMATION, null, null,
					params);
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
		this.description.setValue(item.getDescription());
		this.date_contestation.setValue(item.getDate_contestation());
		this.date_penalty.setValue(item.getDate_penalty());
		this.stop_from.setValue(item.getStop_from());
		this.stop_to.setValue(item.getStop_to());
		this.typ.setValue(item.getTyp());
		this.recall.setChecked(item.getRecall());
		this.date_bp.setValue(item.getDate_bp());
		this.prot.setValue(item.getProt());
		this.prot_penalty.setValue(item.getProt_penalty());

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

		final Contestation item;

		if (this.status_add) {

			// adding branch

			item = new Contestation();

			if (this.typ.getSelectedItem().getValue() == null) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Inserire un tipo di contestazione!", "ERROR", buttons, null, Messagebox.ERROR, null,
						null, params);
				return;
			}

			if (this.date_contestation.getValue() == null) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Data della contestazione mancante!", "ERROR", buttons, null, Messagebox.ERROR, null,
						null, params);
				return;
			}

			if (this.typ.getSelectedItem().getValue().equals(ContestationTag.SOSPENSIONE)) {

				if ((this.stop_to.getValue() == null) || (this.stop_from.getValue() == null)) {
					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");
					final Messagebox.Button[] buttons = new Messagebox.Button[1];
					buttons[0] = Messagebox.Button.OK;

					Messagebox.show("Intervallo date sospensione non completo!", "ERROR", buttons, null,
							Messagebox.ERROR, null, null, params);
					return;
				}

				if (this.stop_from.getValue().compareTo(this.stop_to.getValue()) > 0) {
					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");
					final Messagebox.Button[] buttons = new Messagebox.Button[1];
					buttons[0] = Messagebox.Button.OK;

					Messagebox.show("Intervallo date sospensione errato!", "ERROR", buttons, null, Messagebox.ERROR,
							null, null, params);
					return;
				}
			}

			// setup item with values
			this.setupItemWithValues(item);

			// set user_id
			item.setId_user(this.person_selected.getId());

			// create contestation
			this.contestationDAO.createContestation(item);

			final Map<String, String> params = new HashMap<>();
			params.put("sclass", "mybutton Button");
			final Messagebox.Button[] buttons = new Messagebox.Button[1];
			buttons[0] = Messagebox.Button.OK;

			Messagebox.show("Contestazione aggiunta all'utente", "INFO", buttons, null, Messagebox.INFORMATION, null,
					null, params);

		} else {

			// modify branch

			if (this.typ.getSelectedItem() == null) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Inserire un tipo di contestazione!", "ERROR", buttons, null, Messagebox.ERROR, null,
						null, params);

				return;
			}

			// get selected item
			item = this.sw_list.getSelectedItem().getValue();
			if (item == null) {
				return;
			}

			if (this.date_contestation.getValue() == null) {
				final Map<String, String> params = new HashMap<>();
				params.put("sclass", "mybutton Button");
				final Messagebox.Button[] buttons = new Messagebox.Button[1];
				buttons[0] = Messagebox.Button.OK;

				Messagebox.show("Data della contestazione mancante!", "ERROR", buttons, null, Messagebox.ERROR, null,
						null, params);
				return;
			}

			if (this.typ.getSelectedItem().getValue().equals(ContestationTag.SOSPENSIONE)) {

				if ((this.stop_to.getValue() == null) || (this.stop_from.getValue() == null)) {
					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");
					final Messagebox.Button[] buttons = new Messagebox.Button[1];
					buttons[0] = Messagebox.Button.OK;

					Messagebox.show("Intervallo date sospensione non completo!", "ERROR", buttons, null,
							Messagebox.ERROR, null, null, params);
					return;
				}

				if (this.stop_from.getValue().compareTo(this.stop_to.getValue()) > 0) {
					final Map<String, String> params = new HashMap<>();
					params.put("sclass", "mybutton Button");
					final Messagebox.Button[] buttons = new Messagebox.Button[1];
					buttons[0] = Messagebox.Button.OK;

					Messagebox.show("Intervallo date sospensione errato!", "ERROR", buttons, null, Messagebox.ERROR,
							null, null, params);
					return;
				}
			}

			// delete existing file if any and if required
			if ((item.getFile_name() != null) && (this.currentDoc != null)) {
				final String repo = this.paramsDAO.getParam(ParamsTag.REPO_DOC);
				final String global_file_name = repo + item.getFile_name();
				final File file = new File(global_file_name);
				file.delete();
			}

			// add values to the items
			this.setupItemWithValues(item);

			this.contestationDAO.updateContestation(item);
		}

		if (item.getTyp() != null) {
			if (item.getTyp().equals(ContestationTag.LICENZIAMENTO)
					|| item.getTyp().equals(ContestationTag.SOSPENSIONE)) {

				// ask user for update current status
				Date to_day = Calendar.getInstance().getTime();
				to_day = DateUtils.truncate(to_day, Calendar.DATE);
				final Date my_date = DateUtils.truncate(item.getDate_contestation(), Calendar.DATE);

				if ((item != null) && (my_date.compareTo(to_day) >= 0)) {

					this.onUpdateStatus();

					if (item.getTyp().equals(ContestationTag.LICENZIAMENTO)) {
						this.status_upload = UserStatusTag.FIRED;
						this.status_date_modified = item.getDate_contestation();
					}

					if (item.getTyp().equals(ContestationTag.SOSPENSIONE)) {
						this.status_upload = UserStatusTag.SUSPENDED;
						this.status_date_modified = item.getDate_contestation();
					}

				}

			}
		}

		// Refresh list task
		this.setInitialView();

	}

	private void onUpdateStatus() {

		// send event to show user task
		final Component comp = Path.getComponent("//user/page_user_detail");
		Events.sendEvent(ZkEventsTag.onUpdateGeneralDetails, comp, this.status_upload);

		// send info to "Rapporto Lavorativo"
		final ContestationMessage message = new ContestationMessage();
		message.setStatus(this.status_upload);
		message.setDate_modified(this.status_date_modified);
		final Component comp_status = Path.getComponent("//userstatus/panel");
		Events.sendEvent(ZkEventsTag.onUpdateGeneralDetails, comp_status, message);
	}

	@Listen("onClick = #sw_link_delete")
	public void removeItem() {
		final Map<String, String> params = new HashMap<>();
		params.put("sclass", "mybutton Button");

		final Messagebox.Button[] buttons = new Messagebox.Button[2];
		buttons[0] = Messagebox.Button.OK;
		buttons[1] = Messagebox.Button.CANCEL;

		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null,
				Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>() {
					@Override
					public void onEvent(final ClickEvent e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							UserDetailsComposerCons.this.deleteItemToUser();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				}, params);

	}

	@Listen("onOK = #search_date_penalty; onChange = #search_date_penalty")
	public void searchOnDatePenalty() {

		// set info about grid
		if (this.person_selected == null) {
			return;
		}

		final Date date = this.search_date_penalty.getValue();

		if (date != null) {

			final Integer id_user = this.person_selected.getId();
			final List<Contestation> list = this.contestationDAO.loadUserContestationByDatePenalty(id_user, date);

			this.sw_list.setModel(new ListModelList<>(list));

			this.grid_details.setVisible(false);

			// set null current contestaion doc
			this.currentDoc = null;

			// set link to current docuemnt
			this.current_document.setVisible(false);

			this.select_year.setValue(null);

		} else {

			this.setInitialView();

		}
	}

	@Listen("onChange =#select_year")
	public void selectedYear() {

		if (this.person_selected == null) {
			return;
		}

		if (this.select_year.getSelectedItem() == null) {
			return;
		}

		final String yearSelected = this.select_year.getSelectedItem().getValue();

		if (!yearSelected.equals(UserDetailsComposerCons.ALL_ITEM)) {

			final Integer year = Integer.parseInt(yearSelected);

			final List<Contestation> list = this.contestationDAO
					.loadUserContestationByYearPenalty(this.person_selected.getId(), year);
			this.sw_list.setModel(new ListModelList<>(list));

			this.grid_details.setVisible(false);

			// set null current contestaion doc
			this.currentDoc = null;

			// set link to current docuemnt
			this.current_document.setVisible(false);

			this.search_date_penalty.setValue(null);

		} else {

			this.setInitialView();

		}

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
		this.sw_list.setModel(new ListModelList<>(list));

		this.grid_details.setVisible(false);

		// set null current contestaion doc
		this.currentDoc = null;

		// set link to current docuemnt
		this.current_document.setVisible(false);

		this.search_date_penalty.setValue(null);

		this.select_year.setValue(null);
	}

	/**
	 * Set value
	 *
	 * @param item
	 * @throws FileNotFoundException
	 */
	private void setupItemWithValues(final Contestation item) throws IOException {

		item.setDate_contestation(this.date_contestation.getValue());
		item.setDate_penalty(this.date_penalty.getValue());
		item.setNote(this.note.getValue());
		item.setDescription(this.description.getValue());
		item.setStop_from(this.stop_from.getValue());
		item.setStop_to(this.stop_to.getValue());
		item.setTyp(this.typ.getValue());
		item.setRecall(this.recall.isChecked());
		item.setProt(this.prot.getValue());
		item.setProt_penalty(this.prot_penalty.getValue());

		// set bp (only month and year)
		if (this.date_bp.getValue() != null) {
			Calendar cal_db = DateUtils.toCalendar(this.date_bp.getValue());
			cal_db = DateUtils.truncate(cal_db, Calendar.MONTH);
			item.setDate_bp(cal_db.getTime());
		} else {
			item.setDate_bp(null);
		}

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
