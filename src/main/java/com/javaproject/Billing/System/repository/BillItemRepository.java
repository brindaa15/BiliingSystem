package com.javaproject.Billing.System.repository;

import com.javaproject.Billing.System.model.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillItemRepository extends JpaRepository<BillItem, Long> {
}
