package org.zerock.mallapi.dto;

import lombok.*;


/**
 *  장바구니 목록용 DTO
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
public class CartItemListDTO {

    private Long cino;

    private int qty;

    private String pname;

    private int price;

    private Long pno;

    private String imageFile;


    public CartItemListDTO(Long cino, int qty, String pname, int price, Long pno, String imageFile){
        this.cino = cino;
        this.qty = qty;
        this.pname = pname;
        this.price = price;
        this.pno = pno;
        this.imageFile = imageFile;
    }
}
