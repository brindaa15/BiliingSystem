package com.javaproject.Billing.System.controller;

import com.javaproject.Billing.System.model.Bill;
import com.javaproject.Billing.System.model.BillItemRequest;
import com.javaproject.Billing.System.service.BillService;
import com.javaproject.Billing.System.util.PdfGenerator;

import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    //  Create a bill for a customer
    @PostMapping("/{customerId}")
    public Bill createBill(@PathVariable Long customerId, @RequestBody List<BillItemRequest> items) {
        return billService.createBill(customerId, items);
    }

    //  Get bills for a specific customer
    @GetMapping("/customer/{customerId}")
    public List<Bill> getBillsByCustomer(@PathVariable Long customerId) {
        return billService.getBillsByCustomerId(customerId);
    }

    //  Get bills created on a specific date
    @GetMapping("/date/{date}")
    public List<Bill> getBillsByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return billService.getBillsByDate(date);
    }

    //  Get bills containing a specific product
    @GetMapping("/product/{productName}")
    public List<Bill> getBillsByProduct(@PathVariable String productName) {
        return billService.getBillsByProductName(productName);
    }

    //  Generate PDF of a bill by ID
    @GetMapping("/pdf/{billId}")
    public ResponseEntity<InputStreamResource> generateBillPdf(@PathVariable Long billId) {
        Bill bill = billService.getBillById(billId); // Make sure this method exists in your service class

        if (bill == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayInputStream bis = PdfGenerator.generateBillPdf(bill);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=bill_" + billId + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
