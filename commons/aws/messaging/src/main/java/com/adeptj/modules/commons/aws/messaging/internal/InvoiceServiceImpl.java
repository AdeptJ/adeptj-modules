package com.adeptj.modules.commons.aws.messaging.internal;

import com.adeptj.modules.commons.aws.messaging.InvoiceService;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Font.BOLD;
import static com.itextpdf.text.Font.FontFamily.HELVETICA;
import static com.itextpdf.text.Font.NORMAL;
import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;
import static org.osgi.framework.Constants.SERVICE_VENDOR;

/**
 * Generate payment invoice.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true, property = {SERVICE_DESCRIPTION + "=NCTE Invoice Service", SERVICE_VENDOR + "=NCTE"})
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private static final String NCTE_LOGO = "/ncte-logo.png";

    private static final String TEACHR_LOGO = "/teachr-logo.png";

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static final float FONT_SIZE = 12.0f;

    private static final Font FONT_HEADING_BOLD = new Font(HELVETICA, FONT_SIZE, BOLD);

    private static final Font COMMON_FONT = new Font(HELVETICA, FONT_SIZE, NORMAL);

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private static final float WIDTH_PERCENTAGE = 100.0f;

    private static final int TWO_COL = 2;

    private static final int ONE_COL = 1;

    //@Reference(target = "(osgi.unit.name=QCI)")
    // private JpaCrudRepository crudRepository;

    @Override
    public byte[] generateInvoice(String userId, String txnId) {
        LOGGER.info("Generating invoice for user: [{}] with txnId: [{}]", userId, txnId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            // Add invoice heading
            this.addInvoiceHeading(document);
            PdfPTable mainTable = this.mainTable();
            // Add Date and Invoice Number
            this.addDateAndInvoiceNumber(mainTable);
            // Add BilledBy Cell
            this.billedByCell(mainTable);
            // Add BilledByDetails Cell
            this.billedByDetailsCell(mainTable);
            // Add BilledTo Cell
            this.billedToCell(mainTable);
            // Add BilledToDetails Cell
            this.billedToDetailsCell(mainTable);
            // Add main Table
            document.add(mainTable);
            // Create 2 column Table for Description and Amount heading
            mainTable = new PdfPTable(TWO_COL);
            mainTable.setWidthPercentage(WIDTH_PERCENTAGE);
            // Add Description cell.
            this.descriptionCell(mainTable);
            // Add Amount cell.
            this.amountCell(mainTable);
            // Now add 2 column main Table to the document.
            document.add(mainTable);
            // Create 2 column Table for Description and Amount details
            mainTable = new PdfPTable(TWO_COL);
            mainTable.setWidthPercentage(WIDTH_PERCENTAGE);
            // Add Tax Details cell.
            this.taxDetailsCell(mainTable);
            // Add Amount Details cell.
            this.amountDetailsCell(mainTable);
            // Now add 2 column main Table to the document.
            document.add(mainTable);
            // Create 2 column Table for total amount
            mainTable = new PdfPTable(TWO_COL);
            mainTable.setWidthPercentage(WIDTH_PERCENTAGE);
            this.totalAmountCell(mainTable);
            this.finalAmountCell(mainTable);
            document.add(mainTable);
            mainTable = new PdfPTable(ONE_COL);
            mainTable.setWidthPercentage(WIDTH_PERCENTAGE);
            this.finalDescCell(mainTable);
            // Finally add the last table
            document.add(mainTable);
            document.close();
        } catch (Exception ex) {
            LOGGER.error("Exception while generating invoice!!", ex);
        }
        // Return the PDF bytes
        return outputStream.toByteArray();
    }

    private void addInvoiceHeading(Document document) throws DocumentException {
        Paragraph invoice = new Paragraph("Invoice", new Font(HELVETICA, 25.0f, BOLD));
        invoice.setAlignment(ALIGN_CENTER);
        document.add(invoice);
    }

    private PdfPTable mainTable() {
        PdfPTable mainTable = new PdfPTable(1);
        mainTable.setWidthPercentage(100.0f);
        mainTable.setSpacingBefore(20.0f);
        return mainTable;
    }

    private void addDateAndInvoiceNumber(PdfPTable mainTable) {
        Paragraph dateRowParagraph = new Paragraph("Date: " +
                new SimpleDateFormat(DATE_FORMAT).format(Date.from(Instant.now()))
                + "                                                               Invoice No: NCTE1935Z789054321786",
                FONT_HEADING_BOLD);
        dateRowParagraph.setAlignment(Element.ALIGN_LEFT);
        dateRowParagraph.setSpacingAfter(5.0f);
        PdfPCell dateRowCell = new PdfPCell();
        dateRowCell.addElement(dateRowParagraph);
        mainTable.addCell(dateRowCell);
    }

    private void billedByCell(PdfPTable mainTable) {
        PdfPCell billedByRowCell = new PdfPCell();
        billedByRowCell.addElement(new Paragraph("Billed By  ...", FONT_HEADING_BOLD));
        mainTable.addCell(billedByRowCell);
    }

    private void billedByDetailsCell(PdfPTable mainTable) throws BadElementException, IOException {
        PdfPCell billedByDetailsCell = new PdfPCell();
        Phrase billedByAddressPhrase = new Phrase();
        billedByAddressPhrase.add(new Chunk("National Council for Teacher Education\n", COMMON_FONT));
        Chunk billedByAddressChunk = new Chunk("\nAddress:\n"
                + "Hans Bhawan, Wing II, 1, Bahadur Shah Zafar Marg\n" +
                "New Delhi - 110012\n" + "Phone: (011-123456)\n" + "Fax: (011-123456)\n"
                + "Website: www.ncte-india.org.in\n", COMMON_FONT);
        billedByAddressPhrase.add(billedByAddressChunk);
        billedByAddressPhrase.add(new Chunk(this.getLogo(NCTE_LOGO), 500.0f, 130.0f));
        billedByDetailsCell.addElement(billedByAddressPhrase);
        mainTable.addCell(billedByDetailsCell);
    }

    private void billedToCell(PdfPTable mainTable) {
        PdfPCell billedToRowCell = new PdfPCell();
        billedToRowCell.addElement(new Paragraph("Billed To  ...", FONT_HEADING_BOLD));
        mainTable.addCell(billedToRowCell);
    }

    private void billedToDetailsCell(PdfPTable mainTable) throws BadElementException, IOException {
        PdfPCell billedToDetailsCell = new PdfPCell();
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
        billedToAddressPhrase.add(new Chunk(this.getLogo(TEACHR_LOGO), 500.0f, 130.0f));
        billedToDetailsCell.addElement(billedToAddressPhrase);
        mainTable.addCell(billedToDetailsCell);
    }

    private Image getLogo(String imagePath) throws IOException, BadElementException, IOException {
        Image logo = Image.getInstance(IOUtils.toByteArray(this.getClass().getResourceAsStream(imagePath)));
        logo.setRotationDegrees(180.0f);
        return logo;
    }

    private void descriptionCell(PdfPTable mainTable) {
        PdfPCell descCell = new PdfPCell();
        descCell.addElement(new Paragraph("Description", FONT_HEADING_BOLD));
        mainTable.addCell(descCell);
    }

    private void amountCell(PdfPTable mainTable) {
        PdfPCell amountCell = new PdfPCell();
        amountCell.addElement(new Paragraph("Amount", FONT_HEADING_BOLD));
        mainTable.addCell(amountCell);
    }

    private void taxDetailsCell(PdfPTable mainTable) {
        PdfPCell taxDetailsCell = new PdfPCell();
        taxDetailsCell.addElement(new Paragraph("TEI Ranking & Accreditation fee\n " +
                "GST @ 18%\n" +
                "NCTE Processing Fee", COMMON_FONT));
        mainTable.addCell(taxDetailsCell);
    }

    private void amountDetailsCell(PdfPTable mainTable) {
        PdfPCell amountDetailsCell = new PdfPCell();
        amountDetailsCell.addElement(new Paragraph("Rs. 1,30,000/-\n" +
                "Rs. 23,400/-\n" +
                "Rs. 20,000/-", COMMON_FONT));
        mainTable.addCell(amountDetailsCell);
    }

    private void totalAmountCell(PdfPTable mainTable) {
        PdfPCell totalAmountCell = new PdfPCell();
        totalAmountCell.addElement(new Paragraph("Total Amount to be paid", FONT_HEADING_BOLD));
        mainTable.addCell(totalAmountCell);
    }

    private void finalAmountCell(PdfPTable mainTable) {
        PdfPCell finalAmountCell = new PdfPCell();
        finalAmountCell.addElement(new Paragraph("Rs. 1,73,400/-", FONT_HEADING_BOLD));
        mainTable.addCell(finalAmountCell);
    }

    private void finalDescCell(PdfPTable mainTable) {
        PdfPCell finalDescCell = new PdfPCell();
        finalDescCell.addElement(new Paragraph("Grand Total(One Lakh Seventy Three Thousand Four Hundred Only/-)\n\n"
                + "This is a system generated invoice, requires no signature.",
                FONT_HEADING_BOLD));
        mainTable.addCell(finalDescCell);
    }
}
