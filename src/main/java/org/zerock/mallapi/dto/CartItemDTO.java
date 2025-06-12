package org.zerock.mallapi.dto;

import lombok.Data;


@Data
public class CartItemDTO {

    private String email;       // 사용자 email
    
    private Long pno;           // 상품 번호
    
    private int qty;            // 상품 수량
    
    private Long cino;          // 장바구니 아이템 번호
    
}