package org.example.budgeting.api;

import java.util.List;
import java.util.UUID;

import org.example.budgeting.application.BudgetService;
import org.example.budgeting.application.dtos.BudgetDto;
import org.example.budgeting.application.dtos.BudgetQuote;
import org.example.budgeting.application.dtos.CreateQuoteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private static final Logger log = LoggerFactory.getLogger(BudgetController.class);

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetDto>> findAll() {
        log.info("HTTP list budgets recebido");
        return ResponseEntity.ok(budgetService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetDto> findById(@PathVariable UUID id) {
        log.info("HTTP get budget recebido budgetId={}", id);
        return budgetService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("HTTP get budget retornando 404 budgetId={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("HTTP delete budget recebido budgetId={}", id);
        budgetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/quote")
    public ResponseEntity<BudgetQuote> createQuote(@RequestBody CreateQuoteRequest request) {
        log.info("HTTP create budget quote recebido productId={} materialCount={}", request.productId(), request.materialIds().size());
        return ResponseEntity.ok(budgetService.createQuote(request.productId(), request.materialIds()));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable UUID id) {
        log.info("HTTP accept budget recebido budgetId={}", id);
        budgetService.acceptBudget(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        log.info("HTTP cancel budget recebido budgetId={}", id);
        budgetService.cancelBudget(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable UUID id) {
        log.info("HTTP complete budget recebido budgetId={}", id);
        budgetService.completeBudget(id);
        return ResponseEntity.noContent().build();
    }
}
