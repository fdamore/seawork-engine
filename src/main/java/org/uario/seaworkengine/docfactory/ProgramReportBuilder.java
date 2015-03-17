package org.uario.seaworkengine.docfactory;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.HyperLinkBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.uario.seaworkengine.zkevent.bean.RowSchedule;

public class ProgramReportBuilder {

	private static Logger	logger	= Logger.getLogger(ProgramReportBuilder.class);

	private static void build(final JasperReportBuilder report) {

		final StyleBuilder textStyle = DynamicReports.stl.style().setBorder(DynamicReports.stl.pen1Point());

		final TextColumnBuilder<String> name = DynamicReports.col.column("Nome", "name", DynamicReports.type.stringType());

		final TextColumnBuilder<String> shift1 = DynamicReports.col.column("Turno 1", "shift1", DynamicReports.type.stringType());
		final TextColumnBuilder<String> shift2 = DynamicReports.col.column("Turno 2", "shift2", DynamicReports.type.stringType());
		final TextColumnBuilder<String> shift3 = DynamicReports.col.column("Turno 3", "shift3", DynamicReports.type.stringType());
		final TextColumnBuilder<String> shift4 = DynamicReports.col.column("Turno 4", "shift4", DynamicReports.type.stringType());

		report.setColumnStyle(textStyle).columns(name, shift1, shift2, shift3, shift4)
				.columnGrid(DynamicReports.grid.horizontalColumnGridList(name, shift1, shift2, shift3, shift4));

	}

	/**
	 * @param list_rows
	 * @return
	 */
	private static JRDataSource createDataSource(final ArrayList<RowSchedule> list_rows) {

		final DRDataSource dataSource = new DRDataSource("name", "shift1", "shift2", "shift3", "shift4");

		for (final RowSchedule item : list_rows) {

			final String anchor1 = item.getItem_3().getAnchor1();
			final String anchor2 = item.getItem_3().getAnchor2();
			final String anchor3 = item.getItem_3().getAnchor3();
			final String anchor4 = item.getItem_3().getAnchor4();

			dataSource.add(item.getName_user(), anchor1, anchor2, anchor3, anchor4);

		}

		return dataSource;

	}

	/**
	 * Exception
	 *
	 * @param hotel
	 * @param booking
	 * @param list_rooms
	 * @param cc_security
	 * @return
	 */
	public static JasperReportBuilder createReport(final ArrayList<RowSchedule> list_row) {
		try {
			final JasperReportBuilder report = DynamicReports.report();

			report.setPageFormat(PageType.A4, PageOrientation.LANDSCAPE);
			report.setPageMargin(DynamicReports.margin(20));

			// crate title
			report.title(ProgramReportBuilder.createTitleComponent());

			ProgramReportBuilder.build(report);

			final JRDataSource datasource = ProgramReportBuilder.createDataSource(list_row);

			report.setDataSource(datasource);

			return report;
		} catch (final Exception e) {
			ProgramReportBuilder.logger.error("Error in creating report. " + e.getMessage());
			return null;
		}
	}

	/**
	 * Create Title
	 */
	private static ComponentBuilder<?, ?> createTitleComponent() {

		final HorizontalListBuilder itm = DynamicReports.cmp.horizontalList();

		// BEGIN STYLE
		final StyleBuilder bold10CenteredStyle = DynamicReports.stl.style().setFontSize(10).setForegroundColor(Color.BLUE);
		bold10CenteredStyle.setAlignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP);

		final StyleBuilder rootStyle = DynamicReports.stl.style().setPadding(2);
		final StyleBuilder italicStyle = DynamicReports.stl.style(rootStyle).italic().setForegroundColor(Color.BLUE);

		final StringBuilder builder_gb = new StringBuilder();
		builder_gb.append("Sea Work Service S.r.L." + "\n");
		builder_gb.append("Zona Interporto Porto di Gioia Tauro" + "\n");
		builder_gb.append("89026 San Ferdinando (RC)" + "\n");

		final String ingo_gb = builder_gb.toString();

		try {
			// FIND URL IMAGE

			final URL url = ProgramReportBuilder.class.getResource("/etc/reports/logo_report.png");

			// SET TITLE
			final VerticalListBuilder logo = DynamicReports.cmp.verticalList();

			logo.add(DynamicReports.cmp.image(url).setFixedDimension(200, 50).setHorizontalAlignment(HorizontalAlignment.LEFT));

			// link to company web site
			final String link_string = "http://www.uario.com";
			final HyperLinkBuilder link = DynamicReports.hyperLink(link_string);

			logo.add(DynamicReports.cmp.text(link_string).setStyle(italicStyle).setHyperLink(link).setHorizontalAlignment(HorizontalAlignment.LEFT));

			final TextFieldBuilder<String> text_title = DynamicReports.cmp.text(ingo_gb).setStyle(bold10CenteredStyle);

			final StyleBuilder vsty = DynamicReports.stl.style();
			vsty.setAlignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);
			itm.add(logo, text_title);
			itm.setStyle(vsty);
			itm.newRow().add(DynamicReports.cmp.line()).newRow().add(DynamicReports.cmp.verticalGap(20));

		} catch (final Exception e) {

		}

		return itm;
	}

	/**
	 * @param arg
	 * @throws DRException
	 * @throws IOException
	 */
	public static void main(final String[] args) throws DRException, IOException {

		final ArrayList<RowSchedule> value = new ArrayList<RowSchedule>();

		for (int i = 0; i < 10; i++) {
			final RowSchedule itm = new RowSchedule();
			itm.setUser(1);
			itm.setName_user("Nome di prova");

			final ItemRowSchedule item_3 = new ItemRowSchedule(itm);

			item_3.setAnchor1("Turno 1");
			item_3.setAnchor2("Turno 2");
			item_3.setAnchor3("Turno 3");
			item_3.setAnchor4("Turno 4");

			itm.setItem_3(item_3);

			value.add(itm);
		}

		final JasperReportBuilder report = ProgramReportBuilder.createReport(value);

		// final File file = new File("/home/francesco/Scrivania/file.pdf");

		report.show();

	}
}
