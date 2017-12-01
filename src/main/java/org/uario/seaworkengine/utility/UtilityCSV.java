package org.uario.seaworkengine.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.uario.seaworkengine.model.Contestation;
import org.uario.seaworkengine.model.Customer;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Employment;
import org.uario.seaworkengine.model.FiscalControl;
import org.uario.seaworkengine.model.JobCost;
import org.uario.seaworkengine.model.MedicalExamination;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.ReportItem;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.TfrUser;
import org.uario.seaworkengine.model.TradeUnion;
import org.uario.seaworkengine.model.TrainingCertificate;
import org.uario.seaworkengine.model.UserCompensation;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.IShipCache;
import org.uario.seaworkengine.platform.persistence.dao.ICustomerDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.statistics.ReviewShipWorkAggregate;
import org.uario.seaworkengine.statistics.ShipOverview;
import org.uario.seaworkengine.statistics.UserStatistics;
import org.uario.seaworkengine.zkevent.converter.CraneTypeConverter;
import org.uario.seaworkengine.zkevent.converter.ProductivityConverter;
import org.uario.seaworkengine.zkevent.converter.UserEnableConverter;
import org.uario.seaworkengine.zkevent.converter.UserRoleConverter;
import org.uario.seaworkengine.zkevent.utility.ZkUtility;
import org.zkoss.spring.SpringUtil;

public class UtilityCSV {

	private static final SimpleDateFormat	dayFormat			= new SimpleDateFormat("EEE", Locale.ITALIAN);

	private static final SimpleDateFormat	formatDateOverview	= new SimpleDateFormat("dd/MM/yyyy");

	private static final SimpleDateFormat	formatMonthOverview	= new SimpleDateFormat("MM/yyyy");

	private static final SimpleDateFormat	formatTime			= new SimpleDateFormat("HH:mm");

	private static final SimpleDateFormat	formatTimeOverview	= new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public static StringBuilder downloadCSV_DetailProgramShip(final List<DetailScheduleShip> modelListDetailScheduleShip,
			final ICustomerDAO customerDAO) {
		if (modelListDetailScheduleShip == null) {
			return null;
		}

		final StringBuilder builder = new StringBuilder();

		final String header = "Nome Nave;Cliente;Rif Cliente;Rif SWS;Data Turno;Turno;Operazione;Primo Preposto;Secondo Preposto;Lavorata;Mani P;Mani C;Persone P;Persone C;Tipo servizio;Data Inizio;Data Fine;\n";
		builder.append(header);

		for (final DetailScheduleShip item : modelListDetailScheduleShip) {

			String shipName = "";
			String customerName = "";
			String rif_mct = "";
			String rif_sws = "";
			String shiftDate = "";
			String shiftNumber = "";
			String operation = "";
			String firstUser = "";
			String secondUser = "";
			String worked = "NO";
			String hands_program = "";
			String hands_review = "";
			String persons_program = "";
			String persons_review = "";
			String serviceType = "";
			String startDate = "";
			String endDate = "";

			if (item.getName() != null) {
				shipName = item.getName();
			}

			if (item.getCustomer_id() != null) {
				final Customer customer = customerDAO.loadCustomer(item.getCustomer_id());
				if (customer != null) {
					customerName = customer.getName();
				}
			}

			if (item.getRif_mct() != null) {
				rif_mct = item.getRif_mct();
			}

			if (item.getIdscheduleship() != null) {
				rif_sws = item.getIdscheduleship().toString();
			}

			if (item.getShiftdate() != null) {
				shiftDate = Utility.getDataAsString_it(item.getShiftdate());
			}

			if (item.getShift() != null) {
				shiftNumber = item.getShift().toString();
			}

			if (item.getOperation() != null) {
				operation = item.getOperation();
			}

			if (item.getFirstOperativeName() != null) {
				firstUser = Utility.dottedName(item.getFirstOperativeName());
			}

			if (item.getSecondOperativeName() != null) {
				secondUser = Utility.dottedName(item.getSecondOperativeName());
			}

			if ((item.getWorked() != null) && item.getWorked()) {
				worked = "SI";
			}

			if (item.getHandswork_program() != null) {
				hands_program = item.getHandswork_program().toString();
			}

			if (item.getHandswork() != null) {
				hands_review = item.getHandswork().toString();
			}

			if (item.getMenwork() != null) {
				persons_review = item.getMenwork().toString();
			}

			if (item.getMenwork_program() != null) {
				persons_program = item.getMenwork_program().toString();
			}

			if (item.getServiceType() != null) {
				serviceType = item.getServiceType();
			}

			if (item.getArrivaldate() != null) {
				startDate = Utility.getDataAsString_it(item.getArrivaldate());
			}

			if (item.getDeparturedate() != null) {
				endDate = Utility.getDataAsString_it(item.getDeparturedate());
			}

			final String line = "" + shipName + ";" + customerName + ";" + rif_mct + ";" + rif_sws + ";" + shiftDate + ";" + shiftNumber + ";"
					+ operation + ";" + firstUser + ";" + secondUser + ";" + worked + ";" + hands_program + ";" + hands_review + ";" + persons_program
					+ ";" + persons_review + ";" + serviceType + ";" + startDate + ";" + endDate + ";\n";

			builder.append(line);

		}

		return builder;
	}

	public static StringBuilder downloadCSV_PersonList(final List<Person> personList) {
		if (personList == null) {
			return null;
		}

		final StringBuilder builder = new StringBuilder();

		final String header = "Id utente;Rapporto Lavorativo;Livello Contrattuale;Abilitato;Nome;Città;Telefono;Ruolo;Qualifica;\n";
		builder.append(header);

		for (final Person item : personList) {

			String idUser = "";
			String status = "";
			String contractualLevel = "";
			String enabled = "";
			String name = "";
			String city = "";
			String tel = "";
			String role = "";
			String currentPosition = "";

			if (item.getId() != null) {
				idUser = item.getId().toString();
			}

			if (item.getStatus() != null) {
				status = item.getStatus();
			}

			if (item.getContractual_level() != null) {
				contractualLevel = item.getContractual_level().toString();
			}

			if (item.getCurrent_position() != null) {
				enabled = UserEnableConverter.converterValue(item.isEnabled());
			}

			if ((item.getFirstname() != null) && (item.getLastname() != null)) {
				name = item.getLastname() + " " + item.getFirstname();
			}

			if (item.getCity() != null) {
				city = item.getCity();
			}

			if (item.getPhone() != null) {
				tel = item.getPhone();
			}

			if (item.getAuthorities() != null) {
				role = UserRoleConverter.convertRole(item.getAuthorities().toString());
			}

			if (item.getCurrent_position() != null) {
				currentPosition = item.getCurrent_position();
			}

			final String line = "" + idUser + ";" + status + ";" + contractualLevel + ";" + enabled + ";" + name + ";" + city + ";" + tel + ";" + role
					+ ";" + currentPosition + ";\n";
			builder.append(line);

		}

		return builder;
	}

	/**
	 * Report BILOTTA
	 *
	 * @param reportList
	 * @param shipDAO
	 * @return
	 */
	public static StringBuilder downloadCSV_ReportShip(final List<ReportItem> list) {
		if (list == null) {
			return null;
		}

		final StringBuilder builder = new StringBuilder();

		final String header = " ;Gennaio;Febbraio;Marzo;Aprile;Maggio;Giugno;Luglio;Agosto;Settembre;Ottobre;Novembre;Dicembre;Totale;Media";
		builder.append(header);

		for (final ReportItem itm : list) {

			final StringBuilder line = new StringBuilder();

			line.append(itm.getArgument());

			for (int i = 1; i <= 12; i++) {

				String info = "0";
				final Double itm_info = itm.getMonth(i);
				if (itm_info != null) {
					final String adding = String.format(Locale.ITALY, "%10.2f", itm_info);
					info = adding.trim();
				}

				line.append(";" + info);

			}

			final String total = String.format(Locale.ITALY, "%10.2f", itm.getTotalMonth());
			final String avg = String.format(Locale.ITALY, "%10.2f", itm.calculateAvg());

			line.append(";" + total.trim());
			line.append(";" + avg.trim());

			builder.append("\n" + line);

		}

		return builder;

	}

	public static StringBuilder downloadCSV_ScheduleProgramShip(final List<ScheduleShip> modelListScheduleShip, final ICustomerDAO customerDAO) {
		if (modelListScheduleShip == null) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();

		final String header = "Data Inizio Attività;Data Fine Attività;Nome Nave;Cliente;Volume Atteso;Rif MCT;Rif SWS;Tipo Servizio;Note Programmazione;\n";
		builder.append(header);

		for (final ScheduleShip item : modelListScheduleShip) {
			String startDate = "";
			String endDate = "";
			String shipName = "";
			String customerName = "";
			String volume = "";
			String mct = "";
			String sws = "";
			String serviceType = "";
			String note = "";

			if (item.getArrivaldate() != null) {
				startDate = Utility.getDataAsString_it(item.getArrivaldate());
			}

			if (item.getDeparturedate() != null) {
				endDate = Utility.getDataAsString_it(item.getDeparturedate());
			}

			if (item.getName() != null) {
				shipName = item.getName();
			}

			if (item.getCustomer_id() != null) {
				final Customer customer = customerDAO.loadCustomer(item.getCustomer_id());
				if (customer != null) {
					customerName = customer.getName();
				}
			}

			if (item.getVolume() != null) {
				volume = item.getVolume().toString();
			}

			if (item.getRif_mct() != null) {
				mct = item.getRif_mct().toString();
			}

			if (item.getId() != null) {
				sws = item.getId().toString();
			}

			if (item.getServiceName() != null) {
				serviceType = item.getServiceName();
			}

			if (item.getNote() != null) {
				note = item.getNote();
				note = note.replace("\n", " ");
			}

			final String line = "" + startDate + ";" + endDate + ";" + shipName + ";" + customerName + ";" + volume + ";" + mct + ";" + sws + ";"
					+ serviceType + ";" + note + ";\n";
			builder.append(line);

		}

		return builder;

	}

	/**
	 * @param list
	 * @return
	 */
	public static StringBuilder downloadCSV_user_compensazioni(final List<UserCompensation> list) {
		final StringBuilder builder = new StringBuilder();
		final String header = "Data;OreAssegnate;Note\n";
		builder.append(header);

		for (final UserCompensation itm : list) {
			final String data = "" + UtilityCSV.returnItalianDate(itm.getDate_submit());
			final String ore_assegnate = "" + itm.getTime_comp();
			final String note = "" + itm.getNote();

			final String line = data + ";" + ore_assegnate + ";" + note + ";\n";

			builder.append(line);

		}

		return builder;
	}

	/**
	 * @param list
	 * @return
	 */
	public static StringBuilder downloadCSV_user_contestazioni(final List<Contestation> list) {
		final StringBuilder builder = new StringBuilder();
		final String header = "Tipo;Ricorso;DataContestazione;Protocollo;DataSanzione;ProtocolloSanzione;SospesoDa;SospesoFinoA;MeseAnnoBP;Note\n";
		builder.append(header);

		for (final Contestation itm : list) {
			final String tipo = "" + itm.getTyp();
			final String ricorso = "" + itm.getRecall();
			final String data_contestazione = "" + UtilityCSV.returnItalianDate(itm.getDate_contestation());
			final String protocollo = "" + itm.getProt();
			final String data_sazione = "" + UtilityCSV.returnItalianDate(itm.getDate_penalty());
			final String protocollo_sanzione = "" + itm.getProt_penalty();
			final String sospeso_da = "" + UtilityCSV.returnItalianDate(itm.getStop_from());
			final String sospeso_a = "" + UtilityCSV.returnItalianDate(itm.getStop_to());
			final String mese_anno_bp = "" + UtilityCSV.returnItalianMonth(itm.getDate_bp());
			final String note = "" + itm.getNote();

			final String line = tipo + ";" + ricorso + ";" + data_contestazione + ";" + protocollo + ";" + data_sazione + ";" + protocollo_sanzione
					+ ";" + sospeso_da + ";" + sospeso_a + ";" + mese_anno_bp + ";" + note + ";\n";

			builder.append(line);

		}

		return builder;
	}

	/**
	 * @param list
	 * @return
	 */
	public static StringBuilder downloadCSV_user_cost(final List<JobCost> list) {
		final StringBuilder builder = new StringBuilder();
		final String header = "CentroDiCosto;DataInizio;DataFine;CostoOrarioAzienda;CostoOrarioEffettivo;LivelloContrattuale;Pagabase;Contingenza;Scatti;EDR;Totale;Premi;Note"
				+ "\n";
		builder.append(header);

		for (final JobCost itm : list) {
			final String centro_costo = "" + itm.getBillcenterDescription();
			final String data_inizio = "" + UtilityCSV.returnItalianDate(itm.getDate_from());
			final String data_fine = "" + UtilityCSV.returnItalianDate(itm.getDate_to());
			final String costo_azienda = itm.getBusiness_job_costITASTRING();
			final String costo_effettivo = itm.getFinal_job_costITASTRING();
			final String livello_contrattuale = "" + itm.getContractual_level();
			final String pagabase = itm.getBasicsalaryITASTRING();
			final String contigenza = itm.getContingencyITASTRING();
			final String scatti = itm.getShotsITASTRING();
			final String edr = itm.getEdrITASTRING();
			final String totale = itm.getTotalITASTRING();
			final String premi = itm.getAwardsITASTRING();
			final String note = "" + itm.getNote();

			final String line = centro_costo + ";" + data_inizio + ";" + data_fine + ";" + costo_azienda + ";" + costo_effettivo + ";"
					+ livello_contrattuale + ";" + pagabase + ";" + contigenza + ";" + scatti + ";" + edr + ";" + totale + ";" + premi + ";" + note
					+ "\n";

			builder.append(line);

		}

		return builder;
	}

	/**
	 * @param list
	 * @return
	 */
	public static StringBuilder downloadCSV_user_fiscalvisit(final List<FiscalControl> list) {

		final StringBuilder builder = new StringBuilder();
		final String header = "Richiesta;Controllo;SedeInps;Risultato;Comunicazione;MalattiaDa;MalattiaA;Note\n";
		builder.append(header);

		for (final FiscalControl itm : list) {

			final String richiesta = "" + UtilityCSV.returnItalianDate(itm.getRequest_date());
			final String controllo = "" + UtilityCSV.returnItalianDate(itm.getControl_date());
			final String sede_inps = "" + itm.getSede_inps();
			final String risultato = "" + itm.getResult();
			final String comunicazione = "" + itm.getResult_comunication_type();
			final String malattia_da = "" + UtilityCSV.returnItalianDate(itm.getSikness_from());
			final String malattia_a = "" + UtilityCSV.returnItalianDate(itm.getSikness_to());
			final String note = "" + itm.getNote();

			final String line = richiesta + ";" + controllo + ";" + sede_inps + ";" + risultato + ";" + comunicazione + ";" + malattia_da + ";"
					+ malattia_a + ";" + note + "\n";

			builder.append(line);

		}

		return builder;
	}

	/**
	 * @param list
	 * @return
	 */
	public static StringBuilder downloadCSV_user_formazione(final List<TrainingCertificate> list) {
		final StringBuilder builder = new StringBuilder();
		final String header = "Titolo;Descrizione;InternaEsterna;EnteFormatore;Mansione;Livello;DataConseguimento;DataScadenza;Tutor;Inizio Corso; Fine Corso; HTOT;Note\n";
		builder.append(header);

		for (final TrainingCertificate itm : list) {

			final String titolo = "" + itm.getTitle();
			final String descrizione = "" + itm.getDescription();
			final String interna_esterna = "" + itm.getTrainer_type();
			final String ente_formatore = "" + itm.getTrainer();
			final String mansione = "" + itm.getTraining_task();
			final String livello = "" + itm.getTraining_level();
			final String data_conseguimento = "" + UtilityCSV.returnItalianDate(itm.getCertificate_date());
			final String data_scadenza = "" + UtilityCSV.returnItalianDate(itm.getExpiration_date());
			final String tutor = itm.getTutor();
			final String inizio_corso = UtilityCSV.returnTimeFormat(itm.getStart_class());
			final String fine_corso = UtilityCSV.returnTimeFormat(itm.getEnd_class());
			final String htot = (itm.getDuration() == null) ? "" : itm.getDuration().toString();

			final String note = "" + itm.getNote();

			final String line = titolo + ";" + descrizione + ";" + interna_esterna + ";" + ente_formatore + ";" + mansione + ";" + livello + ";"
					+ data_conseguimento + ";" + data_scadenza + ";" + tutor + ";" + inizio_corso + ";" + fine_corso + ";" + htot + ";" + note + "\n";

			builder.append(line);

		}

		return builder;
	}

	/**
	 * @param list
	 * @return
	 */
	public static StringBuilder downloadCSV_user_medical(final List<MedicalExamination> list) {
		final StringBuilder builder = new StringBuilder();
		final String header = "DatVisita;DataProssimaVisita;Esito;Prescrizioni;Note\n";
		builder.append(header);

		for (final MedicalExamination itm : list) {

			final String data_visita = "" + UtilityCSV.returnItalianDate(itm.getDate_examination());
			final String data_prossima_visita = "" + UtilityCSV.returnItalianDate(itm.getNext_date_examination());
			final String esito = "" + itm.getResult_examination();
			final String prescrizioni = "" + itm.getPrescriptions();
			final String note = "" + itm.getNote_examination();

			final String line = data_visita + ";" + data_prossima_visita + ";" + esito + ";" + prescrizioni + ";" + note + "\n";

			builder.append(line);

		}

		return builder;
	}

	/**
	 * @param list
	 * @return
	 */
	public static StringBuilder downloadCSV_user_raporto(final List<Employment> list) {

		final StringBuilder builder = new StringBuilder();
		final String header = "LivelloContrattuale;DataFine;DataModifica;Status;Note;\n";
		builder.append(header);

		for (final Employment itm : list) {

			final String contractual_level = "" + itm.getContractual_level();
			final String date_end = "" + UtilityCSV.returnItalianDate(itm.getDate_end());
			final String date_modified = "" + UtilityCSV.returnItalianDate(itm.getDate_modified());
			final String note = "" + itm.getNote();
			final String status = "" + itm.getStatus();

			final String line = contractual_level + ";" + date_end + ";" + date_modified + ";" + status + ";" + note + "\n";

			builder.append(line);

		}

		return builder;

	}

	/**
	 * Task
	 *
	 * @param list_mytask
	 * @return
	 */
	public static StringBuilder downloadCSV_user_task(final List<UserTask> list) {

		final StringBuilder builder = new StringBuilder();
		final String header = "Codice;Descrizione;Predefinito;\n";
		builder.append(header);

		for (final UserTask itm : list) {

			final String code = "" + itm.getCode();
			final String description = "" + itm.getDescription();
			final String default_task = "" + itm.getTask_default();

			final String line = code + ";" + description + ";" + default_task + "\n";

			builder.append(line);

		}

		return builder;
	}

	/**
	 * @param list
	 * @return
	 */
	public static StringBuilder downloadCSV_user_tfr(final List<TfrUser> list) {
		final StringBuilder builder = new StringBuilder();
		final String header = "DestinazioneTfr;DataScelta\n";
		builder.append(header);

		for (final TfrUser itm : list) {

			final String destinazione = "" + itm.getTfr_destination();
			final String scelta = "" + UtilityCSV.returnItalianDate(itm.getTfr_selection_date());

			final String line = destinazione + ";" + scelta + "\n";

			builder.append(line);

		}

		return builder;

	}

	/**
	 * @param list
	 * @return
	 */
	public static StringBuilder downloadCSV_user_tradeunion(final List<TradeUnion> list) {

		final StringBuilder builder = new StringBuilder();
		final String header = "Nome;Iscrizione;Cancellazione;Note;\n";
		builder.append(header);

		for (final TradeUnion itm : list) {

			final String nome = "" + itm.getName();
			final String iscrizione = "" + UtilityCSV.returnItalianDate(itm.getRegistration());
			final String cancellazione = "" + UtilityCSV.returnItalianDate(itm.getCancellation());
			final String note = "" + itm.getNote();

			final String line = nome + ";" + iscrizione + ";" + cancellazione + ";" + note + "\n";

			builder.append(line);

		}

		return builder;

	}

	/**
	 * Download info about single user
	 *
	 * @param person
	 * @return
	 */
	public static StringBuilder downloadCSV_userinfo(final Person person) {
		final StringBuilder builder = new StringBuilder();

		final String header = "Tipo;Informazione\n";
		builder.append(header);

		final String row_1 = "Username;" + person.getUsername() + "\n";
		final String row_2 = "RapportoLavoro;" + person.getCurrent_position() + "\n";
		final String row_3 = "LivelloContrattuale;" + person.getContractual_level() + "\n";
		final String row_4 = "DipendenteGiornaliero;" + person.getDailyemployee() + "\n";
		final String row_5 = "Nome;" + person.getFirstname() + "\n";
		final String row_6 = "Cognome;" + person.getLastname() + "\n";
		final String row_7 = "DataNascita;" + UtilityCSV.returnItalianDate(person.getBirth_date()) + "\n";
		final String row_8 = "ProvinciaNascita;" + person.getBirth_province() + "\n";
		final String row_9 = "LuogoNascita;" + person.getBirth_place() + "\n";
		final String row_10 = "Sesso;" + person.getSex() + "\n";
		final String row_11 = "CodFiscale;" + person.getFiscal_code() + "\n";
		final String row_12 = "Matricola;" + person.getEmployee_identification() + "\n";
		final String row_13 = "CodPersonale;" + person.getPersonal_code() + "\n";
		final String row_14 = "Telefono;" + person.getPhone() + "\n";
		final String row_15 = "Città;" + person.getCity() + "\n";
		final String row_16 = "Paese;" + person.getCountry() + "\n";
		final String row_17 = "Provincia;" + person.getProvincia() + "\n";
		final String row_18 = "Indirizzo;" + person.getAddress() + "\n";
		final String row_19 = "CodPostale;" + person.getZip() + "\n";
		final String row_20 = "TitoloStudio;" + person.getEducation() + "\n";
		final String row_21 = "NCFL;" + person.getNcfl() + "\n";
		final String row_22 = "Dipartimento;" + person.getDepartment() + "\n";
		final String row_23 = "PosizioneCorrente;" + person.getCurrent_position() + "\n";
		final String row_24 = "NumBudge;" + person.getNbudge() + "\n";
		final String row_25 = "NumPass;" + person.getNpass() + "\n";
		final String row_26 = "StatoCivile;" + person.getMarital_status() + "\n";
		final String row_27 = "CarichiFamiliari;" + person.getFamily_charge() + "\n";
		final String row_28 = "TipoPatenti;" + person.getDriving_license() + "\n";
		final String row_29 = "DataPatente;" + UtilityCSV.returnItalianDate(person.getDriving_license_emission()) + "\n";
		final String row_30 = "GGSettimana;" + person.getDaywork_w() + "\n";
		final String row_31 = "HHSettimana;" + person.getHourswork_w() + "\n";
		final String row_32 = "PartTime;" + person.getPart_time() + "\n";

		builder.append(row_1);
		builder.append(row_2);
		builder.append(row_3);
		builder.append(row_4);
		builder.append(row_5);
		builder.append(row_6);
		builder.append(row_7);
		builder.append(row_8);
		builder.append(row_9);
		builder.append(row_10);
		builder.append(row_11);
		builder.append(row_12);
		builder.append(row_13);
		builder.append(row_14);
		builder.append(row_15);
		builder.append(row_16);
		builder.append(row_17);
		builder.append(row_18);
		builder.append(row_19);
		builder.append(row_20);
		builder.append(row_21);
		builder.append(row_22);
		builder.append(row_23);
		builder.append(row_24);
		builder.append(row_25);
		builder.append(row_26);
		builder.append(row_27);
		builder.append(row_28);
		builder.append(row_29);
		builder.append(row_30);
		builder.append(row_31);
		builder.append(row_32);

		return builder;
	}

	/**
	 * Download user report
	 *
	 * @param list_users
	 *
	 * @return
	 */
	public static StringBuilder downloadCSV_UserReport(final List<UserReport> list_users) {

		final StringBuilder builder = new StringBuilder();

		final String header = UserReport.HEADER + "\n";
		builder.append(header);

		for (final UserReport report : list_users) {

			final String itm = report.toCSVLine() + "\n";

			builder.append(itm);

		}

		return builder;

	}

	/**
	 * return total CSV
	 *
	 * @param person_info
	 * @param list_emply
	 * @param list_task
	 * @param list_job_cost
	 * @param list_tfr
	 * @param list_medical
	 * @param list_trade
	 * @param list_contestation
	 * @param list_compensation
	 * @return
	 */
	public static StringBuilder downloadCSV_UserTotal(final Person person_info, final List<Employment> list_emply, final List<UserTask> list_task,
			final List<JobCost> list_job_cost, final List<TfrUser> list_tfr, final List<MedicalExamination> list_medical,
			final List<TradeUnion> list_trade, final List<Contestation> list_contestation, final List<UserCompensation> list_compensation,
			final List<TrainingCertificate> list_training) {

		final StringBuilder builder = new StringBuilder();

		builder.append("INFORMAZIONI UTENTE");
		builder.append("\n");

		final StringBuilder user_info = UtilityCSV.downloadCSV_userinfo(person_info);
		builder.append(user_info);

		builder.append("\n");
		builder.append("RAPPORTO LAVORATIVO");
		builder.append("\n");

		final StringBuilder employ = UtilityCSV.downloadCSV_user_raporto(list_emply);
		builder.append(employ);

		builder.append("\n");
		builder.append("MANSIONI");
		builder.append("\n");

		final StringBuilder task = UtilityCSV.downloadCSV_user_task(list_task);
		builder.append(task);

		builder.append("\n");
		builder.append("COSTI ORARI");
		builder.append("\n");

		final StringBuilder job_cost = UtilityCSV.downloadCSV_user_cost(list_job_cost);
		builder.append(job_cost);

		builder.append("\n");
		builder.append("TFR");
		builder.append("\n");

		final StringBuilder tfr = UtilityCSV.downloadCSV_user_tfr(list_tfr);
		builder.append(tfr);

		builder.append("\n");
		builder.append("VISITE FISCALI");
		builder.append("\n");

		final StringBuilder medical = UtilityCSV.downloadCSV_user_medical(list_medical);
		builder.append(medical);

		builder.append("\n");
		builder.append("SINDACATI");
		builder.append("\n");

		final StringBuilder trade = UtilityCSV.downloadCSV_user_tradeunion(list_trade);
		builder.append(trade);

		builder.append("\n");
		builder.append("CONTESTAZIONI DISCIPLINARI");
		builder.append("\n");

		final StringBuilder contestation = UtilityCSV.downloadCSV_user_contestazioni(list_contestation);
		builder.append(contestation);

		builder.append("\n");
		builder.append("COMPENSAZIONI");
		builder.append("\n");

		final StringBuilder compensation = UtilityCSV.downloadCSV_user_compensazioni(list_compensation);
		builder.append(compensation);

		builder.append("\n");
		builder.append("COMPENSAZIONI");
		builder.append("\n");

		final StringBuilder training = UtilityCSV.downloadCSV_user_formazione(list_training);
		builder.append(training);

		return builder;

	}

	public static StringBuilder downloadCSVPreprocessing(final List<Schedule> listSchedule, final IShiftCache shift_cache) {
		final StringBuilder builder = new StringBuilder();
		final String header = "anno;mese;settimana;giorno;nome;matricola;data;turno;note\n";
		builder.append(header);

		for (final Schedule item : listSchedule) {
			String date = "";
			String year = "";
			String weekDate = "";
			String day = "";
			String mouth = "";
			if (item.getDate_schedule() != null) {
				weekDate = Utility.getWeekNumber(item.getDate_schedule()).toString();
				date = UtilityCSV.formatDateOverview.format(item.getDate_schedule());
				day = UtilityCSV.dayFormat.format(item.getDate_schedule());
				mouth = Utility.getMonthNumber(item.getDate_schedule()).toString();
				year = Utility.getYear(item.getDate_schedule()).toString();
			}

			String code_shift = "";
			final UserShift task = shift_cache.getUserShift(item.getShift());
			if (task != null) {
				code_shift = task.getCode();
			}

			String employee_identification = "";
			if ((item.getEmployee_identification() != null) && (item.getEmployee_identification().trim() != "")) {
				employee_identification = item.getEmployee_identification();
			}

			final String note = (item.getNote() == null) ? "" : item.getNote();

			final String line = "" + year + ";" + mouth + ";" + weekDate + ";" + day + ";" + item.getName_user() + ";" + employee_identification + ";"
					+ date + ";" + code_shift + ";" + note + "\n";
			builder.append(line);
		}
		return builder;
	}

	public static StringBuilder downloadCSVProgram(final List<DetailInitialSchedule> listDetailProgram, final TasksDAO taskDao,
			final IShiftCache shift_cache, final ISchedule scheduleDAO, final Boolean administrator) {
		final StringBuilder builder = new StringBuilder();
		String header = "anno;mese;settimana;giorno;nome;matricola;data;tipoturno;turno;mansione;ore (hh:mm);ore_chiusura (hh:mm);ingresso;uscita;nota\n";

		String holiday = "";
		String programmer = "";
		String controller = "";

		if (administrator) {
			header = "anno;mese;settimana;giorno;nome;matricola;data;festivo;tipoturno;turno;mansione;ore (hh:mm);ore_chiusura (hh:mm);ingresso;uscita;nota;programmatore;controllore\n";
		}

		builder.append(header);

		for (final DetailInitialSchedule item : listDetailProgram) {
			String date = "";
			String year = "";
			String weekDate = "";
			String mouth = "";
			String day = "";
			if (item.getDate_schedule() != null) {
				weekDate = Utility.getWeekNumber(item.getDate_schedule()).toString();
				date = UtilityCSV.formatDateOverview.format(item.getDate_schedule());
				day = UtilityCSV.dayFormat.format(item.getDate_schedule());
				mouth = Utility.getMonthNumber(item.getDate_schedule()).toString();
				year = Utility.getYear(item.getDate_schedule()).toString();
			}

			String nota = "";
			if (item.getNote() != null) {
				nota = item.getNote();
				nota = nota.replace("\n", " ");
			}

			if (administrator) {
				holiday = "NO";
				if (UtilityCSV.isHoliday(item.getDate_schedule())) {
					holiday = "SI";
				}

				if (item.getEditor() != null) {
					programmer = item.getEditor();
				}

				if (item.getController() != null) {
					controller = item.getController();
				}
			}

			String time_from = "";
			if (item.getTime_from() != null) {
				time_from = UtilityCSV.formatTimeOverview.format(item.getTime_from());
			}

			String time_to = "";
			if (item.getTime_to() != null) {
				time_to = UtilityCSV.formatTimeOverview.format(item.getTime_to());
			}

			String code_task = "";
			final UserTask task = taskDao.loadTask(item.getTask());

			final List<DetailInitialSchedule> listDetail = scheduleDAO.loadDetailInitialScheduleByIdSchedule(item.getId_schedule());

			if (task != null) {
				code_task = task.getCode();

				// search previous task
				if (task.getIsabsence() || task.getJustificatory()) {
					Long time = null;
					Integer minTimeIndex = null;

					for (int i = 0; i < listDetail.size(); i++) {
						final Integer idItemTask = listDetail.get(i).getTask();
						final UserTask itemtask = taskDao.loadTask(idItemTask);
						if (!item.getId().equals(listDetail.get(i).getId()) && !(itemtask.getIsabsence() || itemtask.getJustificatory())) {

							Long t;

							if (listDetail.get(i).getTime_to().getTime() > listDetail.get(i).getTime_from().getTime())

							{
								t = item.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
							} else {
								t = item.getTime_from().getTime() - listDetail.get(i).getTime_from().getTime();
							}

							if (((time == null) && (t >= 0)) || ((t >= 0) && (t < time))) {
								minTimeIndex = i;
								time = item.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
							}

						}
					}

					if (minTimeIndex != null) {
						final Integer taskIDPrev = listDetail.get(minTimeIndex).getTask();
						if (taskIDPrev != null) {
							final UserTask taskPrev = taskDao.loadTask(taskIDPrev);
							if (taskPrev != null) {
								code_task = taskPrev.getCode() + "-" + code_task;
							}
						}
					} else {
						// search following task
						for (int i = 0; i < listDetail.size(); i++) {
							final Integer idItemTask = listDetail.get(i).getTask();
							final UserTask itemtask = taskDao.loadTask(idItemTask);
							if (!item.getId().equals(listDetail.get(i).getId()) && !(itemtask.getIsabsence() || itemtask.getJustificatory())) {

								Long t;

								if (listDetail.get(i).getTime_to().getTime() > listDetail.get(i).getTime_from().getTime())

								{
									t = listDetail.get(i).getTime_from().getTime() - item.getTime_to().getTime();
								} else {
									t = listDetail.get(i).getTime_to().getTime() - item.getTime_to().getTime();
								}

								if (((time == null) && (t >= 0)) || ((t >= 0) && (t < time))) {
									minTimeIndex = i;
									time = item.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
								}

							}
						}
						if (minTimeIndex != null) {
							final Integer taskIDNext = listDetail.get(minTimeIndex).getTask();
							if (taskIDNext != null) {
								final UserTask taskNext = taskDao.loadTask(taskIDNext);
								if (taskIDNext != null) {
									code_task = taskNext.getCode() + "-" + code_task;
								}
							}
						}
					}
				}

			} else {
				final IShiftCache shiftCache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);

				final UserShift shift = shiftCache.getUserShift(item.getShift_type());

				// Absence Shift
				if (!shift.getPresence()) {
					code_task = item.getDefaultTask() + "-" + shift.getCode();
				}
			}

			String code_shift = "";
			final UserShift shift_type = shift_cache.getUserShift(item.getShift_type());
			if (shift_type != null) {
				code_shift = shift_type.getCode();
			}

			String time_info = "00:00";
			String time_vacation_info = "00:00";
			if ((item.getDateTo() != null) && (item.getDateFrom() != null)) {

				final Long milliseconds = item.getDateTo().getTime() - item.getDateFrom().getTime();

				if (!task.getIsabsence()) {

					time_info = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
							TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));
				} else {

					time_vacation_info = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
							TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));
				}
			}

			final Integer shift_no = item.getShift();
			String shift_no_info = "";
			if (shift_no != null) {
				shift_no_info = shift_no.toString();
			}

			String employee_identification = "";
			if ((item.getEmployee_identification() != null) && (item.getEmployee_identification().trim() != "")) {
				employee_identification = item.getEmployee_identification();
			}

			String line = "";

			if (administrator) {
				line = "" + year + ";" + mouth + ";" + weekDate + ";" + day + ";" + item.getUser() + ";" + employee_identification + ";" + date + ";"
						+ holiday + ";" + code_shift + ";" + shift_no_info + ";" + code_task + ";" + time_info + ";" + time_vacation_info + ";"
						+ time_from + ";" + time_to + ";" + nota + ";" + programmer + ";" + controller + ";\n";
			} else {

				line = "" + year + ";" + mouth + ";" + weekDate + ";" + day + ";" + item.getUser() + ";" + employee_identification + ";" + date + ";"
						+ code_shift + ";" + shift_no_info + ";" + code_task + ";" + time_info + ";" + time_vacation_info + ";" + time_from + ";"
						+ time_to + ";" + nota + ";\n";

			}

			builder.append(line);
		}
		return builder;
	}

	public static StringBuilder downloadCSVReview(final List<DetailFinalSchedule> listDetailRevision, final TasksDAO taskDao,
			final IShiftCache shift_cache, final IShipCache ship_cache, final ISchedule scheduleDAO, final Boolean administrator) {
		final StringBuilder builder = new StringBuilder();

		String header = "anno;mese;settimana;giorno;nome;matricola;data;tipoturno;turno;mansione;ore (hh:mm);ore_chiusura (hh:mm);nome nave;gru;postazione;rif_sws;ingresso;uscita;consuntiva fascia oraria;nota\n";

		String holiday = "";
		String programmer = "";
		String controller = "";

		if (administrator) {
			header = "processo;anno;mese;settimana;giorno;nome;matricola;conta;data;festivo;tipoturno;turno;contabilizzato;fattore;mansione;GG. Lav.;ore (hh:mm);ore_chiusura (hh:mm);nome nave;gru;postazione;rif_sws;ingresso;uscita;consuntiva fascia oraria;nota;programmatore;controllore\n";
		}
		builder.append(header);

		// adding working date control
		final HashMap<String, Boolean> date_working_yes = new HashMap<>();

		for (final DetailFinalSchedule item : listDetailRevision) {

			String processo = "";
			String contabilizzato = "";
			String date = "";
			String year = "";
			String mouth = "";
			String weekDate = "";
			String day = "";
			String dayWorked = "";
			String code_task = "";

			// GET CURRENT TASK
			final UserTask task = taskDao.loadTask(item.getTask());

			// get rif_sws
			final String rif_sws = (item.getRif_sws() == null) ? "" : item.getRif_sws().toString();

			if (item.getDate_schedule() != null) {
				weekDate = Utility.getWeekNumber(item.getDate_schedule()).toString();
				date = UtilityCSV.formatDateOverview.format(item.getDate_schedule());
				day = UtilityCSV.dayFormat.format(item.getDate_schedule());
				mouth = Utility.getMonthNumber(item.getDate_schedule()).toString();
				year = Utility.getYear(item.getDate_schedule()).toString();
			}

			String time_from = "";
			if (item.getTime_from() != null) {
				time_from = UtilityCSV.formatTimeOverview.format(item.getTime_from());
			}

			String time_to = "";
			if (item.getTime_to() != null) {
				time_to = UtilityCSV.formatTimeOverview.format(item.getTime_to());
			}

			String nota = "";
			if (item.getNote() != null) {
				nota = item.getNote();
				nota = nota.replace("\n", " ");
				nota = nota.replace("°", " ");

			}

			if (administrator) {
				holiday = "NO";
				if (UtilityCSV.isHoliday(item.getDate_schedule())) {
					holiday = "SI";
				}

				if (item.getEditor() != null) {
					programmer = item.getEditor();
				}

				if (item.getController() != null) {
					controller = item.getController();
				}

				// get info about process
				final Boolean check_process = ZkUtility.isUserProcessed(item);
				if (check_process.booleanValue()) {
					processo = "PROCESSATO";
				} else {
					processo = "";
				}

				contabilizzato = "NO";

				if ((task != null) && (task.getRecorded() != null) && task.getRecorded().booleanValue()) {
					contabilizzato = "SI";
				}
			}

			final List<DetailFinalSchedule> listDetail = scheduleDAO.loadDetailFinalScheduleByIdSchedule(item.getId_schedule());

			if (task != null) {
				code_task = task.getCode();

				// search previous task
				if (task.getIsabsence() || task.getJustificatory()) {
					Long time = null;
					Integer minTimeIndex = null;

					for (int i = 0; i < listDetail.size(); i++) {
						final Integer idItemTask = listDetail.get(i).getTask();
						final UserTask itemtask = taskDao.loadTask(idItemTask);
						if (!item.getId().equals(listDetail.get(i).getId()) && !(itemtask.getIsabsence() || itemtask.getJustificatory())) {

							Long t;

							if (listDetail.get(i).getTime_to().getTime() > listDetail.get(i).getTime_from().getTime())

							{
								t = item.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
							} else {
								t = item.getTime_from().getTime() - listDetail.get(i).getTime_from().getTime();
							}

							if (((time == null) && (t >= 0)) || ((t >= 0) && (t < time))) {
								minTimeIndex = i;
								time = item.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
							}

						}
					}

					if (minTimeIndex != null) {
						final Integer taskIDPrev = listDetail.get(minTimeIndex).getTask();
						if (taskIDPrev != null) {
							final UserTask taskPrev = taskDao.loadTask(taskIDPrev);
							if (taskPrev != null) {
								code_task = taskPrev.getCode() + "-" + code_task;
							}
						}
					} else {
						// search following task
						for (int i = 0; i < listDetail.size(); i++) {
							final Integer idItemTask = listDetail.get(i).getTask();
							final UserTask itemtask = taskDao.loadTask(idItemTask);
							if (!item.getId().equals(listDetail.get(i).getId()) && !(itemtask.getIsabsence() || itemtask.getJustificatory())) {

								Long t;

								if (listDetail.get(i).getTime_to().getTime() > listDetail.get(i).getTime_from().getTime())

								{
									t = listDetail.get(i).getTime_from().getTime() - item.getTime_to().getTime();
								} else {
									t = listDetail.get(i).getTime_to().getTime() - item.getTime_to().getTime();
								}

								if (((time == null) && (t >= 0)) || ((t >= 0) && (t < time))) {
									minTimeIndex = i;
									time = item.getTime_from().getTime() - listDetail.get(i).getTime_to().getTime();
								}

							}
						}
						if (minTimeIndex != null) {
							final Integer taskIDNext = listDetail.get(minTimeIndex).getTask();
							if (taskIDNext != null) {
								final UserTask taskNext = taskDao.loadTask(taskIDNext);
								if (taskIDNext != null) {
									code_task = taskNext.getCode() + "-" + code_task;
								}
							}
						}
					}
				}

			} else {
				final IShiftCache shiftCache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);

				final UserShift shift = shiftCache.getUserShift(item.getShift_type());

				// Absence Shift
				if (!shift.getPresence()) {
					code_task = item.getDefaultTask() + "-" + shift.getCode();
				}

			}

			String code_shift = "";
			final UserShift shift_type = shift_cache.getUserShift(item.getShift_type());
			if (shift_type != null) {
				code_shift = shift_type.getCode();
			}

			String nameShip = "";
			if (item.getId_ship() != null) {

				final Ship ship = ship_cache.getShip(item.getId_ship());
				if (ship != null) {
					nameShip = ship.getName();
				} else {
					nameShip = "NULL";
				}

			}

			if (Utility.isWorkingDay(item) && (item.getDate_schedule() != null)) {

				// define key
				final String key = item.getId_user() + "@" + Utility.convertToDateAndTime(item.getDate_schedule());

				final Boolean check = date_working_yes.get(key);
				if (check == null) {

					dayWorked = "Si";
					date_working_yes.put(key, Boolean.TRUE);

				}

			}

			String crane = "";
			if (item.getCrane() != null) {
				crane = "=\"" + item.getCrane() + "\"";
			}

			String board = "";
			if (item.getBoard() != null) {
				board = item.getBoard();
			}

			String time_info = "00:00";
			String time_vacation_info = "00:00";
			if ((item.getDateTo() != null) && (item.getDateFrom() != null)) {

				Long milliseconds;
				if (item.getDateTo().getTime() > item.getDateFrom().getTime()) {
					milliseconds = item.getDateTo().getTime() - item.getDateFrom().getTime();
				} else {
					milliseconds = item.getDateFrom().getTime() - item.getDateTo().getTime();
				}

				if (!task.getIsabsence()) {

					time_info = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
							TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));
				} else {

					time_vacation_info = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
							TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));
				}
			}

			final Integer shift_no = item.getShift();
			String shift_no_info = "";
			if (shift_no != null) {
				shift_no_info = shift_no.toString();
			}

			String employee_identification = "";
			if ((item.getEmployee_identification() != null) && (item.getEmployee_identification().trim() != "")) {
				employee_identification = item.getEmployee_identification();
			}

			String sign_info = "No";
			if (item.getSign_user().booleanValue()) {
				sign_info = "Si";
			}

			String reviewshift = "No";
			if ((item.getReviewshift() != null) && item.getReviewshift()) {
				reviewshift = "Si";
			}

			// define factor for users and shift type
			String factor_shift = "";
			if (shift_type.getRecorded()) {
				final Integer den = item.getDaywork_w();
				final Integer num = item.getHourswork_w();
				if ((den != null) && (den != 0) && (num != null)) {
					final double fact = num.doubleValue() / den.doubleValue();
					final double num_factor_shift = Utility.roundTwo(fact);
					factor_shift = String.format(Locale.ITALY, "%10.2f", num_factor_shift);
				}
			}

			String line = "";

			if (administrator) {
				line = "" + processo + ";" + year + ";" + mouth + ";" + weekDate + ";" + day + ";" + item.getUser() + ";" + employee_identification
						+ ";" + sign_info + ";" + date + ";" + holiday + ";" + code_shift + ";" + shift_no_info + ";" + contabilizzato + ";"
						+ factor_shift + ";" + code_task + ";" + dayWorked + ";" + time_info + ";" + time_vacation_info + ";" + nameShip + ";" + crane
						+ ";" + board + ";" + rif_sws + ";" + time_from + ";" + time_to + ";" + reviewshift + ";" + nota + ";" + programmer + ";"
						+ controller + ";\n";
			} else {
				line = "" + year + ";" + mouth + ";" + weekDate + ";" + day + ";" + item.getUser() + ";" + employee_identification + ";" + date + ";"
						+ code_shift + ";" + shift_no_info + ";" + code_task + ";" + dayWorked + ";" + time_info + ";" + time_vacation_info + ";"
						+ nameShip + ";" + crane + ";" + board + ";" + rif_sws + ";" + time_from + ";" + time_to + ";" + reviewshift + ";" + nota
						+ ";\n";

			}

			builder.append(line);
		}
		return builder;
	}

	public static StringBuilder downloadCSVReviewShipWork(final List<ReviewShipWork> reviewShipWorkList) {
		final StringBuilder builder = new StringBuilder();

		String header = "Settimana;Giorno;Data Turno;Nome Nave;Cliente;Rif SWS;Rif MCT;Turno;Lavorato;Conta Rif. SWS;Gru;H LAV;Volumi Netti;";
		header = header + "Volumi Netti Rizz. da Bordo (x Cliente);Volumi Netti Rizz. da Bordo (x SWS);Volumi Netti TW MTC;Periodo di Fatturazione;";
		header = header + "Cielo;Vento;Temperatura;Pioggia;Persone a Bordo;Primo Contenitore a Terra;Ultimo Contenitore a Terra;Persone a Terra;Note";
		header = header + "\n";

		builder.append(header);

		final CraneTypeConverter craneConverter = new CraneTypeConverter();

		for (final ReviewShipWork item : reviewShipWorkList) {

			String week = "";
			String day = "";
			String date = "";
			String shipName = "";
			String rif_sws = "";
			String rif_mct = "";
			String shift = "";
			String crane = "";
			String workedTime = "";
			String volume = "";
			String volumeOnBoard = "";
			String volumeOnBoard_sws = "";
			String volumeTW = "";
			String inovoice_cycle = "";
			String sky_item = "";
			String wind_item = "";
			String temperature_item = "";
			String rain = "";
			String person_onboard = "";
			String person_down = "";
			String date_first_down = "";
			String date_last_down = "";
			final String worked = (item.getWorkedIT() == null) ? "" : item.getWorkedIT();
			final String distinct_sws = (item.getDistinctSWS() == null) ? "" : item.getDistinctSWS();
			final String customer = (item.getCustomer() == null) ? "" : item.getCustomer();
			final String note = (item.getNotedetail() == null) ? "" : item.getNotedetail();

			if (item.getDate_request() != null) {
				week = Utility.getWeekNumber(item.getDate_request()).toString();
			}

			if (item.getDate_request() != null) {
				day = Utility.getDay(item.getDate_request()).toString();
			}

			if (item.getDate_request() != null) {
				date = Utility.getDataAsString_it(item.getDate_request());
			}

			if (item.getShipname() != null) {
				shipName = item.getShipname();
			}

			if (item.getRif_sws() != null) {
				rif_sws = item.getRif_sws().toString();
			}

			if (item.getRif_mct() != null) {
				rif_mct = item.getRif_mct().toString();
			}

			if (item.getShift() != null) {
				shift = item.getShift().toString();
			}

			if ((item.getCrane() != null) && (item.getCrane_gtw() != null)) {
				final Boolean crane_gtw = item.getCrane_gtw();
				final String craneId = item.getCrane();

				crane = (String) craneConverter.defineCraneString(crane_gtw, craneId);
			}

			if (item.getTimeworkLessFranchise() != null) {

				workedTime = Utility.decimatToTime(item.getTimeworkLessFranchise());

			}

			if (item.getVolumeLessFranchise() != null) {
				volume = item.getVolumeLessFranchise().toString();
			}

			if (item.getVolumeunderboardLessFranchise() != null) {
				volumeOnBoard = item.getVolumeunderboardLessFranchise().toString();
			}

			if (item.getVolumeunderboard_swsLessFranchise() != null) {
				volumeOnBoard_sws = item.getVolumeunderboard_swsLessFranchise().toString();
			}

			if (item.getVolume_tw_mctLessFranchise() != null) {
				volumeTW = item.getVolume_tw_mctLessFranchise().toString();
			}

			if (item.getInvoicing_cycle() != null) {
				inovoice_cycle = "" + item.getInvoicing_cycle();
			}

			if (item.getSky() != null) {
				sky_item = "" + item.getSky();
			}

			if (item.getWind() != null) {
				wind_item = "" + item.getWind();
			}

			if (item.getTemperature() != null) {
				temperature_item = "" + item.getTemperature();
			}

			if (item.getRain() != null) {
				rain = "" + item.getRain();
			}

			if (item.getFirst_down() != null) {
				date_first_down = Utility.convertToDateAndTime(item.getFirst_down());
			}

			if (item.getLast_down() != null) {
				date_last_down = Utility.convertToDateAndTime(item.getLast_down());
			}

			if (item.getPerson_onboard() != null) {
				person_onboard = "" + Utility.convertToDateAndTime(item.getPerson_onboard());
			}

			if (item.getPerson_down() != null) {
				person_down = "" + Utility.convertToDateAndTime(item.getPerson_down());
			}

			final String line = "" + week + ";" + day + ";" + date + ";" + shipName + ";" + customer + ";" + rif_sws + ";" + rif_mct + ";" + shift
					+ ";" + worked + ";" + distinct_sws + ";" + crane + ";" + workedTime + ";" + volume + ";" + volumeOnBoard + ";"
					+ volumeOnBoard_sws + ";" + volumeTW + ";" + inovoice_cycle + ";" + sky_item + ";" + wind_item + ";" + temperature_item + ";"
					+ rain + ";" + person_onboard + ";" + date_first_down + ";" + date_last_down + ";" + person_down + ";" + note + "\n";
			builder.append(line);

		}

		return builder;
	}

	public static StringBuilder downloadCSVReviewShipWorkAggregate(final List<ReviewShipWorkAggregate> reviewShipWorkList) {
		final StringBuilder builder = new StringBuilder();

		final String header = "Nome Nave;Tempo Netto Lavorato;Volumi Netti;Produttività;Volumi Netti Rizz. da Bordo (x Cliente);Volumi Netti Rizz. da Bordo (x SWS);Volumi Netti TW MTC;\n";
		builder.append(header);

		for (final ReviewShipWorkAggregate item : reviewShipWorkList) {

			String shipName = "";
			String workedTime = "";
			String volume = "";
			String productivity = "";
			String volumeOnBoard = "";
			String volumeOnBoard_sws = "";
			String volumeTW = "";

			if (item.getShipname() != null) {
				shipName = item.getShipname();
			}

			if (item.getTime_work() != null) {
				workedTime = Utility.decimatToTime(item.getTime_work());
			}

			if (item.getVolume() != null) {
				volume = item.getVolume().toString();
			}

			if ((item.getTime_work() != null) && (item.getVolume() != null)) {
				productivity = ProductivityConverter.getProductivity(item);
			}

			if (item.getVolumeunderboard() != null) {
				volumeOnBoard = item.getVolumeunderboard().toString();
			}

			if (item.getVolumeunderboard_sws() != null) {
				volumeOnBoard_sws = item.getVolumeunderboard_sws().toString();
			}

			if (item.getVolume_tw_mct() != null) {
				volumeTW = item.getVolume_tw_mct().toString();
			}

			final String line = shipName + ";" + workedTime + ";" + volume + ";" + productivity + ";" + volumeOnBoard + ";" + volumeOnBoard_sws + ";"
					+ volumeTW + ";\n";

			builder.append(line);

		}

		return builder;
	}

	public static StringBuilder downloadCSVShipStatistics(final List<ShipOverview> userStatisticsList) {
		final StringBuilder builder = new StringBuilder();

		final String header = "Nave;TempoLavorato;Saturazione;\n";
		builder.append(header);

		for (final ShipOverview item : userStatisticsList) {

			String name = "";
			String time = "";
			String saturation = "";

			name = item.getName();
			time = "" + Utility.roundTwo(item.getTime());
			saturation = "" + Utility.roundTwo(item.getTime_vacation());

			final String line = "" + name + ";" + time + ";" + saturation + ";\n";

			builder.append(line);

		}

		return builder;
	}

	/**
	 * Downbload info in PROGRAMMA LAVORO -> RIEPILOGO -> STATISTICHE UTENTE
	 *
	 * @param userStatisticsList
	 * @return
	 */
	public static StringBuilder downloadCSVStatistics(final List<UserStatistics> userStatisticsList) {
		final StringBuilder builder = new StringBuilder();

		final String header = "Nome;MATR;Tipo;Saturazione;Ore Lavorate;Lavoro Domenicale (N.Lav);Lavoro Domenicale (%);Festivi (N);Festivi (%);Turno 1 (Feriale);Turno 1 (D./F.);Turno 2 (Feriale);Turno 2 (D./F.);Turno 3 (Feriale);Turno 3 (D./F.);Turno 4 (Feriale);Turno 4 (D./F.);\n";
		builder.append(header);

		for (final UserStatistics item : userStatisticsList) {

			String name = "";
			String matr = "";
			String saturation = "";
			String type_sat = "";
			String work_current = "";

			String work_sunday_perc = "";
			String work_holiday_perc = "";
			String work_sunday = "";
			String work_holiday = "";

			String shift_perc_1 = "";
			String shift_perc_2 = "";
			String shift_perc_3 = "";
			String shift_perc_4 = "";

			String shift_perc_1_base = "";
			String shift_perc_2_base = "";
			String shift_perc_3_base = "";
			String shift_perc_4_base = "";

			if (item.getPerson() != null) {
				final Person p = item.getPerson();
				name = p.getLastname() + " " + p.getFirstname();
			}

			if (item.getPerson() != null) {
				final Person p = item.getPerson();
				matr = p.getEmployee_identification();
			}

			// get saturation info
			if (item.getSaturation() != null) {

				type_sat = Utility.getTypeSaturation(item.getSaturation());

				final Double sat = Math.abs(Utility.roundTwo(item.getSaturation()));
				final String sat_info = String.format(Locale.ITALY, "%10.2f", sat);

				saturation = "" + sat_info;

			}

			if (item.getWork_current() != null) {
				work_current = item.getWork_current();
			}

			if (item.getWork_sunday() != null) {
				work_sunday = item.getWork_sunday();
			}

			if (item.getWork_sunday_perc() != null) {
				work_sunday_perc = item.getWork_sunday_onlyperc();
			}

			if (item.getWork_holiday() != null) {
				work_holiday = item.getWork_holiday();
			}

			if (item.getWork_holiday_perc() != null) {
				work_holiday_perc = item.getWork_holiday_onlyperc();
			}

			if (item.getShift_perc_1() != null) {
				shift_perc_1 = item.getShift_perc_1_percholiday();
			}

			if (item.getShift_perc_2() != null) {
				shift_perc_2 = item.getShift_perc_2_percholiday();
			}

			if (item.getShift_perc_3() != null) {
				shift_perc_3 = item.getShift_perc_3_percholiday();
			}

			if (item.getShift_perc_4() != null) {
				shift_perc_4 = item.getShift_perc_4_percholiday();
			}

			if (item.getShift_perc_1_base() != null) {
				shift_perc_1_base = item.getShift_perc_1_base();
			}

			if (item.getShift_perc_2_base() != null) {
				shift_perc_2_base = item.getShift_perc_2_base();
			}

			if (item.getShift_perc_3_base() != null) {
				shift_perc_3_base = item.getShift_perc_3_base();
			}

			if (item.getShift_perc_4_base() != null) {
				shift_perc_4_base = item.getShift_perc_4_base();
			}

			String line = "" + name + ";" + matr + ";" + type_sat + ";" + saturation + ";" + work_current + ";" + work_sunday + ";" + work_sunday_perc
					+ ";" + work_holiday + ";" + work_holiday_perc + ";" + shift_perc_1_base + ";" + shift_perc_1 + ";" + shift_perc_2_base + ";"
					+ shift_perc_2 + ";" + shift_perc_3_base + ";" + shift_perc_3 + ";" + shift_perc_4_base + ";" + shift_perc_4 + ";\n";

			// replace for ITALIAN STYLE in decimal definition (VERY BUD, BUT VERY FAST)
			line = line.replace(".", ",");

			builder.append(line);

		}

		return builder;
	}

	private static Boolean isHoliday(final Date date) {

		final IBankHolidays bank_holiday = (IBankHolidays) SpringUtil.getBean(BeansTag.BANK_HOLIDAYS);

		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		final Calendar cal1 = Calendar.getInstance();

		final SimpleDateFormat format = new SimpleDateFormat("MM-dd");

		// count calendar
		for (final String item : bank_holiday.getDays()) {

			try {

				final Date date_item = format.parse(item);

				cal1.setTime(date_item);

				if ((cal.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH)) && (cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH))) {
					return true;
				}

			} catch (final ParseException e) {
				continue;
			}

		}

		if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
			return true;
		}

		return false;
	}

	private static String returnItalianDate(final Date itm) {
		if (itm == null) {
			return "";
		}

		return UtilityCSV.formatDateOverview.format(itm);
	}

	private static String returnItalianMonth(final Date itm) {
		if (itm == null) {
			return "";
		}

		return UtilityCSV.formatMonthOverview.format(itm);
	}

	private static String returnTimeFormat(final Date itm) {
		if (itm == null) {
			return "00:00";
		}

		return UtilityCSV.formatTime.format(itm);
	}

}
