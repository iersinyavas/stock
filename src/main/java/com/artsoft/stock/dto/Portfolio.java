package com.artsoft.stock.dto;

import com.artsoft.stock.util.GeneralEnumeration.ShareEnum;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Portfolio {
    private Double balance;
    private Map<ShareEnum, Integer> haveShare = new HashMap<>();
}
