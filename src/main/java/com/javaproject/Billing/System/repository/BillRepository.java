package com.javaproject.Billing.System.repository;

import com.javaproject.Billing.System.model.Bill;
import com.javaproject.Billing.System.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByCustomer(Customer customer);

    //  Get bills by date
    List<Bill> findByDate(LocalDate date);

    //  Get bills by product name using JOIN query
    @Query("SELECT b FROM Bill b JOIN b.billItems i WHERE i.product.name LIKE %:productName%")
    List<Bill> findByProductName(@Param("productName") String productName);
}
