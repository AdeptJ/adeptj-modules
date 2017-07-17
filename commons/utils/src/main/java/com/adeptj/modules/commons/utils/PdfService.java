package com.adeptj.modules.commons.utils;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;

/**
 * @author Rakesh.Kumar, AdeptJ
 */
public class PdfService {

    private static final String OUTPUT_FILE = "/Users/rakesh.kumar/NCTE1935Z789054321786.pdf";

    private static final String NCTE_LOGO = "/Users/rakesh.kumar/ncte-logo.png";

    private static final String TEACHR_LOGO = "/Users/rakesh.kumar/teachr-logo.png";

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static final Font FONT_HEADING_BOLD = new Font(Font.FontFamily.HELVETICA, 12.0f, Font.BOLD);

    private static final Font COMMON_FONT = new Font(Font.FontFamily.HELVETICA, 12.0f, Font.NORMAL);

    public static void main(String[] args) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(new File(OUTPUT_FILE)));
        document.open();

        Paragraph invoice = new Paragraph("Invoice", new Font(Font.FontFamily.HELVETICA, 25.0f, Font.BOLD));
        invoice.setAlignment(Element.ALIGN_CENTER);
        document.add(invoice);


        PdfPTable mainTable = new PdfPTable(1);
        mainTable.setWidthPercentage(100.0f);
        mainTable.setSpacingBefore(20.0f);

        Paragraph dateRowParagraph = new Paragraph("Date: " +
                new SimpleDateFormat(DATE_FORMAT).format(Date.from(Instant.now()))
        + "                                                               Invoice No: " +
                "NCTE1935Z789054321786",
                FONT_HEADING_BOLD);
        dateRowParagraph.setAlignment(Element.ALIGN_LEFT);
        dateRowParagraph.setSpacingAfter(5.0f);

        PdfPCell dateRowCell = new PdfPCell();


        dateRowCell.addElement(dateRowParagraph);


        mainTable.addCell(dateRowCell);

        PdfPCell billedByRowCell = new PdfPCell();

        billedByRowCell.addElement(new Paragraph("Billed By  ...", FONT_HEADING_BOLD));

        mainTable.addCell(billedByRowCell);


        PdfPCell billedByDetailsRowCell = new PdfPCell();


        Phrase billedByAddressPhrase = new Phrase();

        billedByAddressPhrase.add(new Chunk("National Council for Teacher Education\n", COMMON_FONT));

        Chunk billedByAddressChunk = new Chunk("\nAddress:\n"
                + "Hans Bhawan, Wing II, 1, Bahadur Shah Zafar Marg\n" +
                "New Delhi - 110012\n" + "Phone: (011-123456)\n" + "Fax: (011-123456)\n"
                + "Website: www.ncte-india.org.in\n", COMMON_FONT);

        billedByAddressPhrase.add(billedByAddressChunk);

        Image ncteLogo = Image.getInstance(NCTE_LOGO);
        ncteLogo.setRotationDegrees(180.0f);

        billedByAddressPhrase.add(new Chunk(ncteLogo, 500.0f, 110.0f));

        billedByDetailsRowCell.addElement(billedByAddressPhrase);


        mainTable.addCell(billedByDetailsRowCell);


        // TODO: Working here

        PdfPCell billedToRowCell = new PdfPCell();
        billedToRowCell.addElement(new Paragraph("Billed To  ...", FONT_HEADING_BOLD));
        mainTable.addCell(billedToRowCell);

        PdfPCell billedToDetailsRowCell = new PdfPCell();


        Phrase billedToAddressPhrase = new Phrase();

        billedToAddressPhrase.add(new Chunk("Applicant Type: Teacher Education Institution\n", COMMON_FONT));



        Chunk billedToAddressChunk = new Chunk("\nPrincipal's name: Jane Doe\n"
                + "Address: XYZ road, ABC \n"
                + "City: XYZ\n"
                + "Postal Code: - 110012\n"
                + "State: Delhi\n"
                + "Phone no: 9811111111\n",
                COMMON_FONT);

        billedToAddressPhrase.add(billedToAddressChunk);

        Image teachrLogo = Image.getInstance(TEACHR_LOGO);
        teachrLogo.setRotationDegrees(180.0f);

        billedToAddressPhrase.add(new Chunk(teachrLogo, 500.0f, 130.0f));

        billedToDetailsRowCell.addElement(billedToAddressPhrase);


        mainTable.addCell(billedToDetailsRowCell);


        // TODO: Till here


        document.add(mainTable);

//        mainTable = new PdfPTable(2);
//        mainTable.setWidthPercentage(100f);
//
//        PdfPCell descCell = new PdfPCell();
//        descCell.addElement(new Paragraph("Description", FONT_HEADING_BOLD));
//
//        mainTable.addCell(descCell);
//
//        PdfPCell amountCell = new PdfPCell();
//        amountCell.addElement(new Paragraph("Amount", FONT_HEADING_BOLD));
//
//        mainTable.addCell(amountCell);
//
//        document.add(mainTable);

//        mainTable = new PdfPTable(2);
//        mainTable.setWidthPercentage(100f);
//
//        PdfPCell taxDetailsCell = new PdfPCell();
//        taxDetailsCell.addElement(new Paragraph("TEI Ranking & Accreditation fee\n " +
//                "GST @ 18%\n" +
//                "NCTE Processing Fee", COMMON_FONT));
//
//        mainTable.addCell(taxDetailsCell);
//
//        PdfPCell amountDetailsCell = new PdfPCell();
//        amountDetailsCell.addElement(new Paragraph("Rs. 1,30,000/-\n" +
//                "Rs. 23,400/-\n" +
//                "Rs. 20,000/-", COMMON_FONT));
//
//        mainTable.addCell(amountDetailsCell);
//
//
//        document.add(mainTable);


        mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(100f);

        PdfPCell totalAmountCell = new PdfPCell();
        totalAmountCell.addElement(new Paragraph("Total Amount to be paid", FONT_HEADING_BOLD));

        mainTable.addCell(totalAmountCell);

        PdfPCell finalAmountCell = new PdfPCell();
        finalAmountCell.addElement(new Paragraph("Rs. 1,73,400/-", FONT_HEADING_BOLD));

        mainTable.addCell(finalAmountCell);

        document.add(mainTable);

        mainTable = new PdfPTable(1);
        mainTable.setWidthPercentage(100f);

        PdfPCell finalDescCell = new PdfPCell();
        finalDescCell.addElement(new Paragraph("Grand Total(One Lakh Seventy Three Thousand Four Hundred Only/-)\n\n"
                + "This is a system generated invoice, requires no signature.",
                FONT_HEADING_BOLD));

        mainTable.addCell(finalDescCell);

        document.add(mainTable);


        document.close();

        System.out.println("Done!!");
    }
}
