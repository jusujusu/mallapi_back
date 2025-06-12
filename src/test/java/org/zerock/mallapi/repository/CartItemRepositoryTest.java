package org.zerock.mallapi.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mallapi.domain.Cart;
import org.zerock.mallapi.domain.CartItem;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.dto.CartItemListDTO;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
public class CartItemRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;


    //장바구니 아이템 추가 테스트
    @Test
    @Transactional
    @Commit
    public void testInsertByProduct() {

        log.info("test1-----------------------");

        //사용자가 전송하는 정보
        String email = "user5@aaa.com";
        Long pno = 1L;
        int qty = 3;

        //이메일, 상품번호로 장바구니 아이템 확인
        //있으면 수량 변경해서 저장, 없으면 추가
        CartItem cartItem = cartItemRepository.getItemOfPno(email, pno);

        //이미 사용자의 장바구니에 담겨있는 상품
        if (cartItem != null) {
            cartItem.changeQty(qty);
            cartItemRepository.save(cartItem);

            return;
        }

        //장바구니 아이템이 없었다면 장바구니부터 확인 필요
        //사용자가 장바구니를 만든적이 있는지 확인
        Optional<Cart> result = cartRepository.getCartOfMember(email);

        Cart cart = null;

        //사용자의 장바구니가 존재하지 않으면 장바구니 생성
        if (result.isEmpty()) {

            Member member = Member.builder().email(email).build();
            Cart tempCart = Cart.builder().owner(member).build();

            cart = cartRepository.save(tempCart);

        } else {        // 장바구니는 있으나 해당 상품의 장바구니 아이템은 없는 경우

            cart = result.get();

        }

        log.info("cart..................... :" + cart);

        if (cartItem == null) {

            Product product = Product.builder().pno(pno).build();
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .qty(qty)
                    .build();
        }

        //상품 아이템 저장
        cartItemRepository.save(cartItem);
    }


    //현재 사용자의 장바구니 아이템 목록 테스트
    @Test
    public void testListOfMember() {

        String email = "user5@aaa.com";

        List<CartItemListDTO> cartItemList = cartItemRepository.getItemsOfCartDTOByEmail(email);

        log.info("실행????");

        for (CartItemListDTO dto : cartItemList) {
            log.info("실행!!!");
            log.info(dto);
        }

    }


    //장바구니 아이템 수정 테스트
    @Test
    @Transactional
    @Commit
    public void testUpdateByCino() {

        Long cino = 4L;
        int qty = 5;

        Optional<CartItem> result = cartItemRepository.findById(cino);

        CartItem cartItem = result.orElseThrow();

        cartItem.changeQty(qty);

        cartItemRepository.save(cartItem);
    }


    //장바구니 아이템 삭제와 목록 조회
    @Test
    public void testDeleteThenList() {

        Long cino = 1L;

        //장바구니 번호
        Long cno = cartItemRepository.getCartFromItem(cino);

        //삭제
        cartItemRepository.deleteById(cino);

        //목록
        List<CartItemListDTO> cartItemList = cartItemRepository.getItemsOfCartDTOByCart(cno);

        for (CartItemListDTO dto : cartItemList) {
            log.info(dto);
        }
    }






}
