package org.zerock.mallapi.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString(exclude = {"cart", "product"})
@Table(name="tbl_cart_item",
        indexes = {
                @Index(columnList ="cart_cno", name="idx_cartitem_cart"),
                @Index(columnList ="product_pno, cart_cno", name="idx_cartitem_pno_cart")}
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cino;

    @ManyToOne
    @JoinColumn(name = "product_pno")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_cno")
    private Cart cart;

    private int qty;

    // 카트 수량 갯수 변경 메소드
    public void changeQty(int qty) {
        this.qty = qty;
    }
}
