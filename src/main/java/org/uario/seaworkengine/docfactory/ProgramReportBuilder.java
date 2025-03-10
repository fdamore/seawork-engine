package org.uario.seaworkengine.docfactory;

import java.awt.Color;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.zkevent.bean.RowSchedule;
import org.zkoss.spring.SpringUtil;

import net.sf.dynamicreports.jasper.builder.JasperConcatenatedReportBuilder;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.HyperLinkBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.grid.ColumnTitleGroupBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

public class ProgramReportBuilder {

	private static ConfigurationDAO configurationDAO = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);

	private static Integer firstShiftNumberOfPerson = 0;

	private static Integer fourthShiftNumberOfPerson = 0;

	private static boolean isInCounting = false;

	private static Logger		logger						= Logger.getLogger(ProgramReportBuilder.class);
	private static ISchedule	scheduleDAO					= (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);
	private static Integer		secondShiftNumberOfPerson	= 0;

	private static Integer thirdShiftNumberOfPerson = 0;

	private static void buildNoteShipReport_MeteoCondition(final JasperReportBuilder report) {
		final StyleBuilder textStyle = DynamicReports.stl.style().setBorder(DynamicReports.stl.pen1Point()).setPadding(2);

		final StyleBuilder columnStyle = DynamicReports.stl.style().setBorder(DynamicReports.stl.pen1Point())
				.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBackgroundColor(Color.lightGray);

		// create column

		final TextColumnBuilder<String> col1 = DynamicReports.col.column("Vento", "col1", DynamicReports.type.stringType());

		final TextColumnBuilder<String> col2 = DynamicReports.col.column("Pioggia", "col2", DynamicReports.type.stringType());

		final TextColumnBuilder<String> col3 = DynamicReports.col.column("Cielo", "col3", DynamicReports.type.stringType());

		final TextColumnBuilder<String> col4 = DynamicReports.col.column("Temperatura", "col4", DynamicReports.type.stringType());

		final ColumnTitleGroupBuilder meteoCondition = DynamicReports.grid.titleGroup("Condizioni Meteo", col1, col2, col3, col4);

		final VerticalListBuilder body = (VerticalListBuilder) ProgramReportBuilder.createBodyShipNote();

		report.setHighlightDetailEvenRows(false).setColumnTitleStyle(columnStyle).setColumnStyle(textStyle)
				.columnGrid(DynamicReports.grid.horizontalColumnGridList(meteoCondition)).columns(col1, col2, col3, col4);

		report.summary(body);

	}

	private static void buildShiftReport(final JasperReportBuilder report) {

		final StyleBuilder textStyle = DynamicReports.stl.style().setBorder(DynamicReports.stl.pen1Point()).setPadding(2);

		final StyleBuilder columnStyle = DynamicReports.stl.style().setBorder(DynamicReports.stl.pen1Point())
				.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBackgroundColor(Color.lightGray);

		// create column
		final TextColumnBuilder<String> name = DynamicReports.col.column("Nome", "name", DynamicReports.type.stringType());
		name.setWidth(150);
		final TextColumnBuilder<String> add = DynamicReports.col.column("Add", "add", DynamicReports.type.stringType());
		add.setWidth(40);
		final TextColumnBuilder<String> start = DynamicReports.col.column("Entrata", "start", DynamicReports.type.stringType());
		final TextColumnBuilder<String> end = DynamicReports.col.column("Uscita", "end", DynamicReports.type.stringType());

		final TextColumnBuilder<String> pnrStart = DynamicReports.col.column("Uscita", "pnrStart", DynamicReports.type.stringType());
		final TextColumnBuilder<String> pnrEnd = DynamicReports.col.column("Rientro", "pnrEnd", DynamicReports.type.stringType());
		final TextColumnBuilder<String> autorizedEntry = DynamicReports.col.column("Uscita/Entrata autorizzata", "autorizedEntry",
				DynamicReports.type.stringType());
		final TextColumnBuilder<String> nonAutorizedEntry = DynamicReports.col.column("Uscita/Entrata non autorizzata", "nonAutorizedEntry",
				DynamicReports.type.stringType());

		final TextColumnBuilder<String> priority1 = DynamicReports.col.column("Priorità Annullare", "priority1", DynamicReports.type.stringType());
		final TextColumnBuilder<String> priority2 = DynamicReports.col.column("Priorità Esubero", "priority2", DynamicReports.type.stringType());

		// create group of column
		final ColumnTitleGroupBuilder presenceInShift = DynamicReports.grid.titleGroup("Presenze in Turno", name, add)
				.setTitleStretchWithOverflow(true);
		final ColumnTitleGroupBuilder hours = DynamicReports.grid.titleGroup("Orario di", start, end);

		final ColumnTitleGroupBuilder pnr = DynamicReports.grid.titleGroup("PNR", pnrStart, pnrEnd);

		report.setHighlightDetailEvenRows(true).setColumnTitleStyle(columnStyle)
				.setColumnStyle(textStyle).columnGrid(DynamicReports.grid.horizontalColumnGridList(presenceInShift, hours, pnr, autorizedEntry,
						nonAutorizedEntry, priority1, priority2))
				.columns(name, add, start, end, pnrStart, pnrEnd, autorizedEntry, nonAutorizedEntry, priority1, priority2);

	}

	private static void buildShipReport(final JasperReportBuilder report) {

		final StyleBuilder textStyle = DynamicReports.stl.style().setBorder(DynamicReports.stl.pen1Point()).setPadding(2);

		final StyleBuilder columnStyle = DynamicReports.stl.style().setBorder(DynamicReports.stl.pen1Point())
				.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBackgroundColor(Color.lightGray);

		// create column

		final TextColumnBuilder<String> ship = DynamicReports.col.column("Nave", "ship", DynamicReports.type.stringType());
		ship.setWidth(180);

		final TextColumnBuilder<String> user = DynamicReports.col.column("Personale", "user", DynamicReports.type.stringType());
		user.setWidth(180);

		final TextColumnBuilder<String> co = DynamicReports.col.column("CO", "co", DynamicReports.type.stringType());

		final TextColumnBuilder<String> tw = DynamicReports.col.column("TW", "tw", DynamicReports.type.stringType());

		final TextColumnBuilder<String> hands = DynamicReports.col.column("Mani", "hands", DynamicReports.type.stringType());

		final TextColumnBuilder<String> cr = DynamicReports.col.column("CR", "cr", DynamicReports.type.stringType());

		final TextColumnBuilder<String> add = DynamicReports.col.column("Add.", "add", DynamicReports.type.stringType());

		final TextColumnBuilder<String> start = DynamicReports.col.column("Dalle", "start", DynamicReports.type.stringType());
		final TextColumnBuilder<String> end = DynamicReports.col.column("Alle", "end", DynamicReports.type.stringType());

		// create group of column
		final ColumnTitleGroupBuilder operation = DynamicReports.grid.titleGroup("Tipo Op.", co, tw).setTitleStretchWithOverflow(true);

		report.setHighlightDetailEvenRows(true).setColumnTitleStyle(columnStyle).setColumnStyle(textStyle)
				.columnGrid(DynamicReports.grid.horizontalColumnGridList(ship, operation, hands, cr, user, add, start, end))
				.columns(ship, co, tw, hands, cr, user, add, start, end);

	}

	/***
	 * Create body in ship note
	 **/
	private static ComponentBuilder<?, ?> createBodyShipNote() {
		final VerticalListBuilder itm = DynamicReports.cmp.verticalList();

		final StyleBuilder titleStyle = DynamicReports.stl.style().setBorder(DynamicReports.stl.pen1Point()).setBackgroundColor(Color.LIGHT_GRAY)
				.setHorizontalAlignment(HorizontalAlignment.CENTER);

		// MATERIALE CONSEGNATO
		final TextFieldBuilder<String> materialTitle = DynamicReports.cmp.text("Materiale Consegnato").setStyle(titleStyle);
		itm.add(materialTitle);

		final StyleBuilder cellStyle = DynamicReports.stl.style().setBorder(DynamicReports.stl.pen1Point())
				.setHorizontalAlignment(HorizontalAlignment.CENTER);

		final StringBuilder builder_gb = new StringBuilder();

		builder_gb.append(" _________________________________________________________________________________________________" + "\n");
		builder_gb.append(" _________________________________________________________________________________________________" + "\n");
		builder_gb.append(" _________________________________________________________________________________________________" + "\n");
		builder_gb.append(" _________________________________________________________________________________________________" + "\n");
		builder_gb.append(" _________________________________________________________________________________________________" + "\n");
		builder_gb.append(" _________________________________________________________________________________________________" + "\n");

		final TextFieldBuilder<String> material = DynamicReports.cmp.text(builder_gb.toString()).setStyle(cellStyle);

		itm.add(material);

		// OPERAZIONI
		final TextFieldBuilder<String> shipTitle = DynamicReports.cmp.text("Operazioni").setStyle(titleStyle);
		itm.add(shipTitle);

		final HorizontalListBuilder shipDetailContent = DynamicReports.cmp.horizontalList();

		final StringBuilder shipNote = new StringBuilder();

		shipNote.append("Nome Nave" + "\n");
		shipNote.append("Personale a Bordo" + "\n");
		shipNote.append("Primo Contenitore a Terra" + "\n");
		shipNote.append("Ultimo Contenitore a Terra" + "\n");
		shipNote.append("Personale a Terra" + "\n");

		final TextFieldBuilder<String> noteShip = DynamicReports.cmp.text(shipNote.toString());

		shipDetailContent.add(noteShip);

		final StringBuilder line = new StringBuilder();

		line.append("____________________" + "\n");
		line.append("____________________" + "\n");
		line.append("____________________" + "\n");
		line.append("____________________" + "\n");
		line.append("____________________" + "\n");

		final TextFieldBuilder<String> lines = DynamicReports.cmp.text(line.toString());

		shipDetailContent.add(lines);
		shipDetailContent.setStyle(cellStyle);

		noteShip.setStyle(DynamicReports.stl.style().setPadding(5));
		lines.setStyle(DynamicReports.stl.style().setPadding(5));

		final VerticalListBuilder shipDetailVertical = DynamicReports.cmp.verticalList();
		final HorizontalListBuilder shipDetailHorizontal = DynamicReports.cmp.horizontalList();
		shipDetailHorizontal.setStyle(cellStyle);

		shipDetailHorizontal.add(shipDetailContent);
		shipDetailHorizontal.add(shipDetailContent);

		shipDetailVertical.add(shipDetailHorizontal);
		shipDetailVertical.add(shipDetailHorizontal);
		shipDetailVertical.add(shipDetailHorizontal);

		itm.add(shipDetailVertical.setStyle(cellStyle));

		// NOTE
		final TextFieldBuilder<String> noteTitle = DynamicReports.cmp.text("Note").setStyle(titleStyle);
		itm.add(noteTitle);
		final StringBuilder workNote = new StringBuilder();

		workNote.append("\n" + " __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n");
		workNote.append(" __________________________________________________________________________________________________" + "\n" + "\n");

		final TextFieldBuilder<String> noteWork = DynamicReports.cmp.text(workNote.toString());

		itm.add(noteWork.setStyle(cellStyle));

		final StyleBuilder style = DynamicReports.stl.style().setFontSize(10).setHorizontalAlignment(HorizontalAlignment.CENTER);

		final TextFieldBuilder<String> text_title = DynamicReports.cmp.text("\n" + "\n" + "Firma  ________________________ ");
		text_title.setStyle(style);

		itm.add(text_title);

		return itm;

	}

	private static JRDataSource createDataSource_NoteShip_MeteoCondition() {

		final DRDataSource dataSource = new DRDataSource("col1", "col2", "col3", "col4");

		dataSource.add("Vento Leggero", "Pioggia Leggera", "Cielo Sereno", "Temp. Bassa");
		dataSource.add("Vento Medio", "Pioggia Media", "Cielo Coperto", "Temp. Media");
		dataSource.add("Vento Forte", "Pioggia Intensa", "Cielo Nuvoloso", "Temp. Alta");

		return dataSource;

	}

	/**
	 * @param list_rows
	 * @return
	 */
	private static JRDataSource createDataSource_Shift(final ArrayList<RowSchedule> list_rows, final Integer shiftNumber) {

		final DRDataSource dataSource = new DRDataSource("name", "add", "start", "end", "pnrStart", "pnrEnd", "autorizedEntry", "nonAutorizedEntry",
				"priority1", "priority2");

		for (final RowSchedule item : list_rows) {

			String name_user = item.getName_user();
			name_user = Utility.dottedName(name_user);

			if ((shiftNumber == 1) && (item.getItem_3() != null) && (item.getItem_3().getAnchor1() != null)) {

				if (item.getItem_3().getSchedule() != null) {

					ProgramReportBuilder.isInCounting = false;

					ProgramReportBuilder.updateDataSource_Shift(dataSource, shiftNumber, name_user, item);

					if (ProgramReportBuilder.isInCounting) {
						ProgramReportBuilder.firstShiftNumberOfPerson++;
					}

				}
			}

			if ((shiftNumber == 2) && (item.getItem_3() != null) && (item.getItem_3().getAnchor2() != null)) {

				if (item.getItem_3().getSchedule() != null) {

					ProgramReportBuilder.isInCounting = false;
					ProgramReportBuilder.updateDataSource_Shift(dataSource, shiftNumber, name_user, item);

					if (ProgramReportBuilder.isInCounting) {
						ProgramReportBuilder.secondShiftNumberOfPerson++;
					}
				}
			}

			if ((shiftNumber == 3) && (item.getItem_3() != null) && (item.getItem_3().getAnchor3() != null)) {

				if (item.getItem_3().getSchedule() != null) {

					ProgramReportBuilder.isInCounting = false;
					ProgramReportBuilder.updateDataSource_Shift(dataSource, shiftNumber, name_user, item);
					if (ProgramReportBuilder.isInCounting) {
						ProgramReportBuilder.thirdShiftNumberOfPerson++;
					}
				}
			}

			if ((shiftNumber == 4) && (item.getItem_3() != null) && (item.getItem_3().getAnchor4() != null)) {

				if (item.getItem_3().getSchedule() != null) {
					ProgramReportBuilder.isInCounting = false;
					ProgramReportBuilder.updateDataSource_Shift(dataSource, shiftNumber, name_user, item);
					if (ProgramReportBuilder.isInCounting) {
						ProgramReportBuilder.fourthShiftNumberOfPerson++;
					}
				}
			}

		}

		for (int i = 0; i < 4; i++) {
			dataSource.add("", "", "", "", "", "", "", "", "", "");
		}

		return dataSource;

	}

	/**
	 * @param list_rows
	 * @return
	 */
	private static JRDataSource createDataSource_Ship(final List<DetailScheduleShip> shipList, final Integer shiftNumber) {

		final DRDataSource dataSource = new DRDataSource("ship", "co", "tw", "hands", "cr", "user", "add", "start", "end");

		int shipId = -1;

		int shipNumberAdded = 0;

		for (final DetailScheduleShip item : shipList) {

			if (item.getShift().equals(shiftNumber) && (shipId != item.getId_ship())) {
				final String ship_name = item.getName();
				shipId = item.getId_ship();

				dataSource.add(ship_name, "", "", "", "", "", "", "", "");

				shipNumberAdded++;

			}

		}

		final int rowNum = 39 - shipNumberAdded;

		for (int i = 0; i < rowNum; i++) {
			dataSource.add("", "", "", "", "", "", "", "", "");
		}

		return dataSource;

	}

	private static JasperReportBuilder createNoteShipReport(final Date date, final Integer shiftNumber) {

		try {
			final JasperReportBuilder report = DynamicReports.report();

			report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT);

			report.setPageMargin(DynamicReports.margin(20));

			// crate title
			if (shiftNumber.equals(1)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, -1));

			} else if (shiftNumber.equals(2)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, -1));
			} else if (shiftNumber.equals(3)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, -1));
			} else if (shiftNumber.equals(4)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, -1));
			}

			final JRDataSource datasource = ProgramReportBuilder.createDataSource_NoteShip_MeteoCondition();

			report.setDataSource(datasource);

			ProgramReportBuilder.buildNoteShipReport_MeteoCondition(report);

			return report;

		} catch (final Exception e) {
			ProgramReportBuilder.logger.error("Error in creating report. " + e.getMessage());
			return null;
		}

	}

	/**
	 * Concatenate report
	 *
	 * @throws DRException
	 */
	public static JasperConcatenatedReportBuilder createReport(final ArrayList<RowSchedule> list_row, final List<DetailScheduleShip> shipList,
			final Date date) throws DRException {

		final StyleBuilder summaryStyle = DynamicReports.stl.style().setPadding(10);

		// TURNO 1
		final JasperReportBuilder report1Shift = ProgramReportBuilder.createShiftReport(list_row,
				list_row.get(0).getItem_3().getSchedule().getDate_schedule(), 1);
		report1Shift.summary(ProgramReportBuilder.createShiftFooterComponent()).setSummaryStyle(summaryStyle);
		final JasperReportBuilder report1Ship = ProgramReportBuilder.createShipReport(shipList, date, 1);

		final JasperReportBuilder noteReportShip1 = ProgramReportBuilder.createNoteShipReport(date, 1);

		// TURNO 2
		final JasperReportBuilder report2Shift = ProgramReportBuilder.createShiftReport(list_row,
				list_row.get(0).getItem_3().getSchedule().getDate_schedule(), 2);
		final JasperReportBuilder report2Ship = ProgramReportBuilder.createShipReport(shipList, date, 2);
		report2Shift.summary(ProgramReportBuilder.createShiftFooterComponent()).setSummaryStyle(summaryStyle);

		final JasperReportBuilder noteReportShip2 = ProgramReportBuilder.createNoteShipReport(date, 2);

		// TURNO 3
		final JasperReportBuilder report3Shift = ProgramReportBuilder.createShiftReport(list_row,
				list_row.get(0).getItem_3().getSchedule().getDate_schedule(), 3);
		final JasperReportBuilder report3Ship = ProgramReportBuilder.createShipReport(shipList, date, 3);
		report3Shift.summary(ProgramReportBuilder.createShiftFooterComponent()).setSummaryStyle(summaryStyle);

		final JasperReportBuilder noteReportShip3 = ProgramReportBuilder.createNoteShipReport(date, 3);

		// TURNO 4
		final JasperReportBuilder report4Shift = ProgramReportBuilder.createShiftReport(list_row,
				list_row.get(0).getItem_3().getSchedule().getDate_schedule(), 4);
		final JasperReportBuilder report4Ship = ProgramReportBuilder.createShipReport(shipList, date, 4);
		report4Shift.summary(ProgramReportBuilder.createShiftFooterComponent()).setSummaryStyle(summaryStyle);

		final JasperReportBuilder noteReportShip4 = ProgramReportBuilder.createNoteShipReport(date, 4);

		final JasperConcatenatedReportBuilder reportConcatenated = new JasperConcatenatedReportBuilder();
		reportConcatenated.concatenate(report1Shift, report2Shift, report3Shift, report4Shift, report1Ship, noteReportShip1, report2Ship,
				noteReportShip2, report3Ship, noteReportShip3, report4Ship, noteReportShip4);

		return reportConcatenated;

	}

	/**
	 * Create Footer for shift sheet
	 */
	private static ComponentBuilder<?, ?> createShiftFooterComponent() {
		final HorizontalListBuilder itm = DynamicReports.cmp.horizontalList();

		final StyleBuilder style = DynamicReports.stl.style().setFontSize(8);

		final StringBuilder builder_gb = new StringBuilder();

		builder_gb
				.append("Nel caso si verifichi che il compilatore montante e smontante è lo stesso, si dovrà apporre due firme. Se il dipendente ha un orario diverso da quello programmato equivalente al 1°-2°-3°-4°, il preposto deve segnare con una X se la diversità di orario è autorizzata o non autorizzata."
						+ "\n" + "\n");

		builder_gb
				.append("NOTE:_________________________________________________________________________________________________________________________________________________________________________"
						+ "\n" + "\n");
		builder_gb
				.append("______________________________________________________________________________________________________________________________________________________________________________"
						+ "\n" + "\n");

		builder_gb
				.append("Firma compilatore foglio rilevazione presenza montante  ____________________________________ Dalle: ____________________________ Alle: ____________________________"
						+ "\n" + "\n");
		builder_gb.append(
				"Firma compilatore foglio rilevazione presenza smontante ____________________________________ Dalle: ____________________________ Alle: ____________________________");

		final String ingo_gb = builder_gb.toString();

		final TextFieldBuilder<String> text_title = DynamicReports.cmp.text(ingo_gb);
		text_title.setStyle(style);

		itm.add(text_title);

		return itm;

	}

	private static JasperReportBuilder createShiftReport(final ArrayList<RowSchedule> list_row, final Date date, final Integer shiftNumber) {
		try {
			final JasperReportBuilder report = DynamicReports.report();

			report.setPageFormat(PageType.A4, PageOrientation.LANDSCAPE);

			report.setPageMargin(DynamicReports.margin(20));

			ProgramReportBuilder.firstShiftNumberOfPerson = 0;
			ProgramReportBuilder.secondShiftNumberOfPerson = 0;
			ProgramReportBuilder.thirdShiftNumberOfPerson = 0;
			ProgramReportBuilder.fourthShiftNumberOfPerson = 0;

			final JRDataSource datasource = ProgramReportBuilder.createDataSource_Shift(list_row, shiftNumber);

			// crate title
			if (shiftNumber.equals(1)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, ProgramReportBuilder.firstShiftNumberOfPerson));
			} else if (shiftNumber.equals(2)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, ProgramReportBuilder.secondShiftNumberOfPerson));
			} else if (shiftNumber.equals(3)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, ProgramReportBuilder.thirdShiftNumberOfPerson));
			} else if (shiftNumber.equals(4)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, ProgramReportBuilder.fourthShiftNumberOfPerson));
			}

			report.setDataSource(datasource);

			ProgramReportBuilder.buildShiftReport(report);

			return report;
		} catch (final Exception e) {
			ProgramReportBuilder.logger.error("Error in creating report. " + e.getMessage());
			return null;
		}
	}

	/**
	 * Create Footer for ship sheet
	 */
	private static ComponentBuilder<?, ?> createShipFooterComponent() {
		final HorizontalListBuilder itm = DynamicReports.cmp.horizontalList();

		final StyleBuilder style = DynamicReports.stl.style().setFontSize(10).setHorizontalAlignment(HorizontalAlignment.CENTER);

		final TextFieldBuilder<String> text_title = DynamicReports.cmp.text("Firma  ________________________ ");
		text_title.setStyle(style);

		itm.add(text_title);

		return itm;

	}

	private static JasperReportBuilder createShipReport(final List<DetailScheduleShip> shipList, final Date date, final Integer shiftNumber) {
		try {
			final JasperReportBuilder report = DynamicReports.report();

			report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT);

			report.setPageMargin(DynamicReports.margin(20));

			final JRDataSource datasource = ProgramReportBuilder.createDataSource_Ship(shipList, shiftNumber);

			// crate title
			if (shiftNumber.equals(1)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, -1));

			} else if (shiftNumber.equals(2)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, -1));
			} else if (shiftNumber.equals(3)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, -1));
			} else if (shiftNumber.equals(4)) {
				report.title(ProgramReportBuilder.createTitleComponent(shiftNumber, date, -1));
			}

			report.setDataSource(datasource);

			report.pageFooter(ProgramReportBuilder.createShipFooterComponent());

			ProgramReportBuilder.buildShipReport(report);

			return report;
		} catch (final Exception e) {
			ProgramReportBuilder.logger.error("Error in creating report. " + e.getMessage());
			return null;
		}
	}

	/**
	 * Create Title
	 */
	private static ComponentBuilder<?, ?> createTitleComponent(final Integer shiftNumber, final Date date, final Integer numberOfPerson) {

		final HorizontalListBuilder itm = DynamicReports.cmp.horizontalList();

		// BEGIN STYLE
		final StyleBuilder bold10CenteredStyle = DynamicReports.stl.style().setFontSize(10).setForegroundColor(Color.BLUE);
		bold10CenteredStyle.setAlignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP);
		final StyleBuilder rootStyle = DynamicReports.stl.style().setPadding(2);
		final StyleBuilder italicStyle = DynamicReports.stl.style(rootStyle).italic().setForegroundColor(Color.BLUE);
		final StyleBuilder centerTitleStyle = DynamicReports.stl.style().setFontSize(12).setBold(true).setUnderline(true)
				.setHorizontalAlignment(HorizontalAlignment.CENTER);
		final StyleBuilder dateStyle = DynamicReports.stl.style().setFontSize(12).setItalic(true).setHorizontalAlignment(HorizontalAlignment.CENTER);

		final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN);

		String title = "Foglio Rilevazione Presenze";
		// ship sheet
		if (numberOfPerson == -1) {
			title = "Foglio Lavorazione Nave";
		}

		final StringBuilder shiftString = new StringBuilder();
		shiftString.append("TURNO: ");
		shiftString.append(shiftNumber);

		final String shiftInfo = shiftString.toString();

		try {
			// CREATE QUALITY LOGO
			final URL urlQualityLogo = ProgramReportBuilder.class.getResource("/etc/reports/quality.jpg");
			final VerticalListBuilder qualityLogo = DynamicReports.cmp.verticalList();
			qualityLogo.add(DynamicReports.cmp.image(urlQualityLogo).setFixedHeight(60).setHorizontalAlignment(HorizontalAlignment.RIGHT));

			// FIND URL LOGO IMAGE
			final URL url = ProgramReportBuilder.class.getResource("/etc/reports/logo_report.png");

			// SET TITLE
			final VerticalListBuilder logo = DynamicReports.cmp.verticalList();
			final VerticalListBuilder center_title_container = DynamicReports.cmp.verticalList();

			center_title_container.setStyle(DynamicReports.stl.style().setHorizontalAlignment(HorizontalAlignment.CENTER));

			logo.add(DynamicReports.cmp.image(url).setFixedDimension(50, 50).setHorizontalAlignment(HorizontalAlignment.LEFT));

			final TextFieldBuilder<String> title_field = DynamicReports.cmp.text(title);
			title_field.setStyle(centerTitleStyle);
			center_title_container.add(title_field);

			final TextFieldBuilder<String> shiftInfo_field = DynamicReports.cmp.text(shiftInfo);
			shiftInfo_field.setStyle(dateStyle);
			center_title_container.add(shiftInfo_field);

			// link to company web site
			final String link_string = "http://www.seaworkservice.it";
			final HyperLinkBuilder link = DynamicReports.hyperLink(link_string);

			logo.add(DynamicReports.cmp.text(link_string).setStyle(italicStyle).setHyperLink(link).setHorizontalAlignment(HorizontalAlignment.LEFT));

			final TextFieldBuilder<String> data_title = DynamicReports.cmp.text(sdf.format(date));
			data_title.setStyle(dateStyle);
			center_title_container.add(data_title);

			final StyleBuilder vsty = DynamicReports.stl.style();
			vsty.setAlignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);
			itm.add(logo, center_title_container, qualityLogo);
			itm.setStyle(vsty);
			itm.newRow().add(DynamicReports.cmp.line()).newRow().add(DynamicReports.cmp.verticalGap(20));

			// ship sheet
			if (numberOfPerson != -1) {
				final HorizontalListBuilder numberOfPersonWorker = DynamicReports.cmp.horizontalList();
				numberOfPersonWorker.setStyle(DynamicReports.stl.style().setHorizontalAlignment(HorizontalAlignment.LEFT));
				final TextFieldBuilder<String> numberOfPersonField = DynamicReports.cmp.text("Totale persone: " + numberOfPerson);
				numberOfPersonWorker.add(numberOfPersonField);
				itm.newRow().add(numberOfPersonWorker);
				itm.newRow(3);
			}

		} catch (final Exception e) {

		}

		return itm;
	}

	/**
	 * Update data source of shift sheet
	 */
	private static void updateDataSource_Shift(final DRDataSource dataSource, final Integer shiftNumber, final String name_user,
			final RowSchedule item) {

		ProgramReportBuilder.isInCounting = false;

		final Schedule schedule = item.getItem_3().getSchedule();

		final List<DetailInitialSchedule> list_detail_schedule = ProgramReportBuilder.scheduleDAO
				.loadDetailInitialScheduleByIdSchedule(schedule.getId());

		if (list_detail_schedule != null) {

			for (final DetailInitialSchedule detailInitialSchedule : list_detail_schedule) {

				final Integer idTask = detailInitialSchedule.getTask();

				if ((idTask != null) && detailInitialSchedule.getShift().equals(shiftNumber)) {
					final UserTask task = ProgramReportBuilder.configurationDAO.loadTask(idTask);
					if ((task != null) && !task.getHiddentask() && !task.getIsabsence() && !task.getJustificatory()) {

						final String add = task.getCode();

						dataSource.add(name_user, add, "", "", "", "", "", "", "", "");

						ProgramReportBuilder.isInCounting = true;

					}
				}
			}
		}
	}

}
