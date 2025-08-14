package com.javaproject.Billing.System.service;

import com.javaproject.Billing.System.exception.ResourceNotFoundException;
import com.javaproject.Billing.System.model.*;
import com.javaproject.Billing.System.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public BillService(BillRepository billRepository,
                       BillItemRepository billItemRepository,
                       ProductRepository productRepository,
                       CustomerRepository customerRepository) {
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    public Bill createBill(Long customerId, List<BillItemRequest> items) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + customerId + " not found"));

        double subtotal = 0.0;
        List<BillItem> billItems = new ArrayList<>();

        for (BillItemRequest req : items) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + req.getProductId() + " not found"));

            int requestedQty = req.getQuantity();
            int availableQty = product.getQuantity();

            if (availableQty < requestedQty) {
                throw new ResourceNotFoundException("Insufficient stock for product: " + product.getName()
                        + ". Available: " + availableQty + ", Requested: " + requestedQty);
            }

            product.setQuantity(availableQty - requestedQty);
            productRepository.save(product);

            BillItem item = new BillItem();
            item.setProduct(product);
            item.setQuantity(requestedQty);
            item.setPrice(product.getPrice() * requestedQty);

            subtotal += item.getPrice();
            billItems.add(item);
        }

        double tax = subtotal * 0.15;
        double total = subtotal + tax;

        Bill bill = new Bill();
        bill.setCustomer(customer);
        bill.setDate(LocalDate.now());
        bill.setSubtotal(subtotal);
        bill.setTax(tax);
        bill.setTotal(total);
        bill.setBillItems(new ArrayList<>());

        Bill savedBill = billRepository.save(bill);

        for (BillItem item : billItems) {
            item.setBill(savedBill);
            billItemRepository.save(item);
            savedBill.getBillItems().add(item);
        }

        return billRepository.save(savedBill);
    }

    public List<Bill> getBillsByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + customerId + " not found"));
        return billRepository.findByCustomer(customer);
    }

    // ✅ Get bills by date
    public List<Bill> getBillsByDate(LocalDate date) {
        return billRepository.findByDate(date);
    }

    // ✅ Get bills by product name
    public List<Bill> getBillsByProductName(String productName) {
        return billRepository.findByProductName(productName);
    }
    public Bill getBillById(Long billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill with ID " + billId + " not found"));
    }

}
