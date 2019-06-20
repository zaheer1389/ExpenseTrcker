package com.expensemanager.report;

import java.io.File;
import java.io.FileOutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.expensemanager.dao.Expense;
import com.expensemanager.dao.PdfDao;
import com.expensemanager.repository.ExpenseRepository;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.HtmlUtilities;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

@Component
public class ReportGenerator {

	@Autowired
	ExpenseRepository expenseRepository;

	private Font catFont = new Font(FontFamily.HELVETICA, 18.0f, 1);
	private Format dateFormatSystem;
	private List<String> mColumns;
	private PdfDao mHeader;
	private int mIndex;
	private List<PdfDao> mValues;
	private Font smallBold = new Font(FontFamily.HELVETICA, HtmlUtilities.DEFAULT_FONT_SIZE, 1);
	private Font subFont = new Font(FontFamily.HELVETICA, 16.0f, 1);
	Font boldFont = new Font(FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
	Double openingBal;
	double closingBal, totalCR, totalDR;
	String fromDate, toDate;

	Document document;

	public ReportGenerator() {
		// TODO Auto-generated constructor stub
	}

	public File generateReport(String fromDate, String toDate) {
		this.fromDate = fromDate;
		this.toDate = toDate;

		try {
			String PATH = "/home/zaheer/expense_report.pdf";
			document = new Document(PageSize.A4, 20, 20, 20, 20);
			PdfWriter.getInstance(document, new FileOutputStream(PATH));
			document.open();
			processReport();
			document.close();
			return new File(PATH);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void processReport() {
		mColumns = new ArrayList();
		mColumns.add("Date");
		mColumns.add("Credit");
		mColumns.add("Debit");
		mColumns.add("Balance Amount");
		mColumns.add("Naration");

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date date = format.parse(fromDate);
			Date date2 = format.parse(toDate);

			Paragraph preface = new Paragraph("Salary Report from " + new SimpleDateFormat("MMM-yyyy").format(date)
					+ " to " + new SimpleDateFormat("MMM-yyyy").format(date2));
			preface.setAlignment(Element.ALIGN_CENTER);
			document.add(preface);

			int months = monthsBetweenDates(date, date2);

			System.err.println("Total months between two dates " + months);

			openingBal = expenseRepository.getOpeningBalance(date);
			if(openingBal == null){
				openingBal = 0.0;
			}
			System.out.println("Opening Balance :: " + openingBal);

			for (int i = 0; i < months; i++) {
				addMonth(i, date, date2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
		}
	}

	public void addMonth(int month, Date startDate, Date endDate) {
		try {
			// closingBal = openingBal;
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat format2 = new SimpleDateFormat("MMM-yyyy");

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(format.parse(fromDate));
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + month);
			Date updatedFromDate = calendar.getTime();

			Calendar c = Calendar.getInstance();
			c.setTime(calendar.getTime());
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date updatedToDate = c.getTime();

			List<Expense> expenses = expenseRepository.findBetweenDate(updatedFromDate, updatedToDate);

			if (expenses.size() == 0) {
				return;
			}

			Paragraph p = new Paragraph(
					"\nMonth : " + format2.format(calendar.getTime()) + "\nOpening Balance : " + openingBal, smallBold);
			p.setLeading(15);
			document.add(p);

			double totalCredit = 0.0d;
			double totalDebit = 0.0d;

			List<Expense> expns = expenses;
			mValues = new ArrayList();
			/*
			 * if (!SessionManager.getListOrder()) { Collections.reverse(trans);
			 * }
			 */
			for (Expense dao : expns) {
				PdfDao pdf = new PdfDao();
				pdf.setFirst(new SimpleDateFormat("dd-MM-yyyy").format(dao.getDate()));
				if (dao.getAmount() >= 0) {
					pdf.setSecond("" + dao.getAmount());
					pdf.setThird("0");
				} else {
					pdf.setSecond("0");
					pdf.setThird("" + dao.getAmount());
				}

				if (dao.getAmount() >= 0) {
					totalCredit += dao.getAmount();
				} else {
					totalDebit -= dao.getAmount();
				}
				if (totalCredit > totalDebit) {
					pdf.setFour(totalCredit - totalDebit + "/-Cr");
				} else if (totalDebit > totalCredit) {
					pdf.setFour(totalDebit - totalCredit + "/-Db");
				} else {
					pdf.setFour("0.00/-");
				}

				pdf.setFive(dao.getNarration());
				pdf.setCrDr(dao.getAmount() >= 0);

				mValues.add(pdf);

				// openingBal += dao.getCraditAmount() + dao.getDebitAmount();
			}

			createTable(document);

			// openingBal = closingBal;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int monthsBetweenDates(Date startDate, Date endDate) {

		Calendar start = Calendar.getInstance();
		start.setTime(startDate);

		Calendar end = Calendar.getInstance();
		end.setTime(endDate);

		int monthsBetween = 0;
		int dateDiff = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH);

		if (dateDiff < 0) {
			int borrrow = end.getActualMaximum(Calendar.DAY_OF_MONTH);
			dateDiff = (end.get(Calendar.DAY_OF_MONTH) + borrrow) - start.get(Calendar.DAY_OF_MONTH);
			monthsBetween--;

			if (dateDiff > 0) {
				monthsBetween++;
			}
		} else {
			monthsBetween++;
		}
		monthsBetween += end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
		monthsBetween += (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
		return monthsBetween;
	}

	private void createTable(Document document) throws BadElementException {
		try {
			totalCR = 0;
			totalDR = 0;
			int i;
			int len = mColumns.size();
			PdfPTable table = new PdfPTable(len);
			table.setSpacingBefore(10);
			table.setSpacingAfter(10);
			table.setTotalWidth(PageSize.A4.getWidth() - 40f);
			table.setLockedWidth(true);
			for (i = 0; i < len; i++) {
				PdfPCell c1 = new PdfPCell(new Phrase((String) mColumns.get(i), boldFont));
				c1.setHorizontalAlignment(1);
				c1.setUseAscender(true);
				c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				c1.setBackgroundColor(new BaseColor(157, 223, 237));
				c1.setFixedHeight(18);
				table.addCell(c1);
			}
			table.setHeaderRows(1);
			int j = 1;
			for (PdfDao dao : mValues) {
				if (dao.isCrDr()) {
					totalCR += Double.parseDouble(dao.getSecond());
				} else {
					totalDR += Double.parseDouble(dao.getThird());
				}
				for (i = 0; i < len; i++) {
					switch (i) {
					case 0:
						table.addCell("" + dao.getFirst());
						break;
					case 1:
						table.addCell("" + dao.getSecond());
						break;
					case 2:
						table.addCell("" + dao.getThird());
						break;
					case 3:
						table.addCell("" + dao.getFour());
						break;
					case 4:
						table.addCell("" + dao.getFive());
						break;
					case 5:
						table.addCell("" + dao.getSix());
						break;
					default:
						break;
					}
				}
				j++;
			}
			Paragraph p = new Paragraph();
			p.setIndentationLeft(20);
			p.setIndentationRight(20);
			p.add(table);
			document.add(p);
			closingBal = openingBal + totalCR + totalDR;
			Paragraph pp = new Paragraph(
					"Total Credit : " + totalCR + "\nTotal Debit : " + totalDR + "\nClosing Balance : " + closingBal,
					smallBold);
			pp.setLeading(15);
			document.add(pp);
			document.add(Chunk.NEWLINE);
			LineSeparator ls = new LineSeparator();
			document.add(new Chunk(ls));

			openingBal = closingBal;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
