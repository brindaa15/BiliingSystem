package com.javaproject.Billing.System.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.javaproject.Billing.System.model.Bill;
import com.javaproject.Billing.System.model.BillItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

public class PdfGenerator {

    public static ByteArrayInputStream generateBillPdf(Bill bill) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Customer Bill", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Customer Info
            document.add(new Paragraph("Customer Name: " + bill.getCustomer().getName()));
            document.add(new Paragraph("Email: " + bill.getCustomer().getEmail()));
            document.add(new Paragraph("Phone: " + bill.getCustomer().getPhone()));
            document.add(new Paragraph("Date: " + bill.getDate()));
            document.add(Chunk.NEWLINE);

            // Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 2, 2, 2});

            Stream.of("Product", "Quantity", "Unit Price", "Total Price").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setPhrase(new Phrase(headerTitle));
                table.addCell(header);
            });

            List<BillItem> items = bill.getBillItems();
            for (BillItem item : items) {
                table.addCell(item.getProduct().getName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.valueOf(item.getProduct().getPrice()));
                table.addCell(String.valueOf(item.getPrice()));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Subtotal: ₹" + bill.getSubtotal()));
            document.add(new Paragraph("Tax (15%): ₹" + bill.getTax()));
            document.add(new Paragraph("Total: ₹" + bill.getTotal()));

            document.close();
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
