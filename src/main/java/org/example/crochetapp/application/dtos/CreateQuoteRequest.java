package org.example.crochetapp.application.dtos;

import java.util.List;
import java.util.UUID;

public record CreateQuoteRequest(
        UUID productId,
        List<UUID> materialIds
) {}
