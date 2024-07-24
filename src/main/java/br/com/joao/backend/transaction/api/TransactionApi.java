package br.com.joao.backend.transaction.api;

import java.math.BigDecimal;

public record TransactionApi(
        Integer type,
        String date,
        BigDecimal value,
        Long cpf,
        String card,
        String hour,
        String owner,
        String name
){}
