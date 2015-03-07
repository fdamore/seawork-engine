package org.uario.seaworkengine.docfactory;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

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
import org.uario.seaworkengine.zkevent.bean.RowSchedule;

public class ProgramReportBuilder {

	private static Logger	logger	= Logger.getLogger(ProgramReportBuilder.class);

	private static void build(final JasperReportBuilder report) {

		final StyleBuilder textStyle = DynamicReports.stl.style().setBorder(DynamicReports.stl.pen1Point());

		final TextColumnBuilder<String> itemColumn = DynamicReports.col.column("Item", "item", DynamicReports.type.stringType());

		final TextColumnBuilder<Integer> quantityColumn = DynamicReports.col.column("Quantity", "quantity", DynamicReports.type.integerType());

		final TextColumnBuilder<BigDecimal> unitPriceColumn = DynamicReports.col.column("Unit price", "unitprice",
				DynamicReports.type.bigDecimalType());

		final TextColumnBuilder<Date> orderDateColumn = DynamicReports.col.column("Order date", "orderdate", DynamicReports.type.dateType());

		final TextColumnBuilder<Date> orderDateFColumn = DynamicReports.col.column("Order date", "orderdate",
				DynamicReports.type.dateYearToFractionType());

		final TextColumnBuilder<Date> orderYearColumn = DynamicReports.col.column("Order year", "orderdate", DynamicReports.type.dateYearType());

		final TextColumnBuilder<Date> orderMonthColumn = DynamicReports.col.column("Order month", "orderdate", DynamicReports.type.dateMonthType());

		final TextColumnBuilder<Date> orderDayColumn = DynamicReports.col.column("Order day", "orderdate", DynamicReports.type.dateDayType());

		report.setColumnStyle(textStyle)
		.columns(itemColumn, quantityColumn, unitPriceColumn, orderDateColumn, orderDateFColumn, orderYearColumn, orderMonthColumn,
				orderDayColumn)
				.columnGrid(
						DynamicReports.grid.verticalColumnGridList(itemColumn,
								DynamicReports.grid.horizontalColumnGridList(quantityColumn, unitPriceColumn)),

								DynamicReports.grid.verticalColumnGridList(

										orderDateColumn,

										DynamicReports.grid.horizontalColumnGridList(orderDateFColumn, orderYearColumn),

										DynamicReports.grid.horizontalColumnGridList(orderMonthColumn, orderDayColumn)));

	}

	private static JRDataSource createDataSource() {

		final DRDataSource dataSource = new DRDataSource("item", "orderdate", "quantity", "unitprice");

		dataSource.add("Notebook", new Date(), 1, new BigDecimal(500));

		dataSource.add("Book", new Date(), 7, new BigDecimal(300));

		dataSource.add("PDA", new Date(), 2, new BigDecimal(250));

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

			final JRDataSource datasource = ProgramReportBuilder.createDataSource();
			// report.setDataSource(list_row);
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
			itm.setName_user("prova");
			value.add(itm);
		}

		final JasperReportBuilder report = ProgramReportBuilder.createReport(value);

		// final File file = new File("/home/francesco/Scrivania/file.pdf");

		report.show();

	}

}
