package com.capitec.invoicetracking.service.impl;

import com.capitec.invoicetracking.domain.entity.BillingAccount;
import com.capitec.invoicetracking.domain.repository.BillingAccountRepository;
import com.capitec.invoicetracking.model.request.BillingAccountSearchRequest;
import com.capitec.invoicetracking.service.BillingAccountService;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BillingAccountServiceImpl implements BillingAccountService {
    private final BillingAccountRepository billingAccountRepository;

    public BillingAccountServiceImpl(BillingAccountRepository billingAccountRepository) {
        this.billingAccountRepository = billingAccountRepository;
    }

    @Override
    public Page<BillingAccount> searchAccountHolders(Pageable pageable, BillingAccountSearchRequest request) {
        return billingAccountRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request != null) {
                if (StringUtils.isNotBlank(request.getName())) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + escapeSqlLike(request.getName().toLowerCase()) + "%"));
                }
                if (StringUtils.isNotBlank(request.getPhone())) {
                    predicates.add(cb.like(cb.lower(root.get("phone")), "%" + escapeSqlLike(request.getPhone().toLowerCase()) + "%"));
                }
                if (StringUtils.isNotBlank(request.getEmail())) {
                    predicates.add(cb.like(cb.lower(root.get("email")), "%" + escapeSqlLike(request.getEmail().toLowerCase()) + "%"));
                }
                if (StringUtils.isNotBlank(request.getAddress())) {
                    predicates.add(cb.like(cb.lower(root.get("address")), "%" + escapeSqlLike(request.getAddress().toLowerCase()) + "%"));
                }
            }

            // If no predicates are added (null request or all fields blank), return all results (no where clause)
            if (predicates.isEmpty()) {
                return null;
            }

            if (query == null) {
                return null;
            }

            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageable);
    }

    private String escapeSqlLike(String term) {
        if (term == null) return null;
        return term.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

}
