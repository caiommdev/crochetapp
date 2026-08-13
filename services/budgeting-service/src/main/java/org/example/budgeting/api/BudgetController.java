package org.example.budgeting.api;

import lombok.RequiredArgsConstructor;
import org.example.budgeting.application.BudgetService;
import org.example.budgeting.application.dtos.BudgetDto;
import org.example.budgeting.application.dtos.BudgetQuote;
import org.example.budgeting.application.dtos.CreateQuoteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetDto>> findAll() {
        return ResponseEntity.ok(budgetService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetDto> findById(@PathVariable UUID id) {
        return budgetService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        budgetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/quote")
    public ResponseEntity<BudgetQuote> createQuote(@RequestBody CreateQuoteRequest request) {
        return ResponseEntity.ok(budgetService.createQuote(request.productId(), request.materialIds()));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable UUID id) {
        budgetService.acceptBudget(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        budgetService.cancelBudget(id);
        return ResponseEntity.noContent().build();
    }
}
