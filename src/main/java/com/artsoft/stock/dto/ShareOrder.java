package com.artsoft.stock.dto;

import com.artsoft.stock.util.GeneralEnumeration.ShareEnum;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderOperationStatus;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShareOrder {

    private Integer id;
    private ShareEnum shareEnum;
    private Integer lot;
    private Double price;
    private ShareOrderStatus shareOrderStatus;
    private ShareOrderOperationStatus shareOrderOperationStatus;
    private Customer customer;

}
