package com.xm.investment.recommendationservice.crypto.model;

import java.math.BigDecimal;

public record Statistics (Sample oldest,
                          Sample newest,
                          Sample min,
                          Sample max,
                          BigDecimal normalizedRange) { }
