package org.zerock.mallapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.mallapi.domain.Cart;
import org.zerock.mallapi.domain.CartItem;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.dto.CartItemDTO;
import org.zerock.mallapi.dto.CartItemListDTO;
import org.zerock.mallapi.repository.CartItemRepository;
import org.zerock.mallapi.repository.CartRepository;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;


    @Override
    public List<CartItemListDTO> addOrdModify(CartItemDTO cartItemDTO) {

        String email = cartItemDTO.getEmail();
        Long pno = cartItemDTO.getPno();
        int qty = cartItemDTO.getQty();
        Long cino = cartItemDTO.getCino();

        //기존에 담겨 있는 상품에 대한 처리
        if (cino != null) {     // 장바구니 아이템 번호가 있어서 수량만 변경하는 경우

            Optional<CartItem> cartItemResult = cartItemRepository.findById(cino);

            CartItem cartItem = cartItemResult.orElseThrow();

            cartItem.changeQty(qty);

            cartItemRepository.save(cartItem);

            return getCartItems(email);
        }

        //장바구니 아이템 번호cino가 없는 경우
        //사용자의 카트
        Cart cart = getCart(email);

        CartItem cartItem = null;

        //이미 동일한 상품이 담긴적이 있을 수 있으므로
        cartItemRepository.getItemOfPno(email, pno);

        if (cartItem == null) {

            Product product = Product.builder().pno(pno).build();
            cartItem = CartItem.builder().product(product).cart(cart).qty(qty).build();

        } else {
            cartItem.changeQty(qty);
        }

        cartItemRepository.save(cartItem);

        return getCartItems(email);
    }


    private Cart getCart(String email) {

        //해당 email의 장바구니(Cart)가 있는지 확인 있으면 반환
        // 없으면 Cart 객체 생성하고 추가 반환

        Cart cart =null;

        Optional<Cart> result=cartRepository.getCartOfMember(email);

        if(result.isEmpty()) {

            log.info("Cart of the member is not exist!!");

            Member member= Member.builder().email(email).build();

            Cart tempCart=Cart.builder().owner(member).build();

            cart = cartRepository.save(tempCart);

        }else{
            cart = result.get();
        }

        return cart;
    }

    @Override
    public List<CartItemListDTO> getCartItems(String email) {
        return cartItemRepository.getItemsOfCartDTOByEmail(email);
    }

    @Override
    public List<CartItemListDTO> remove(Long cino) {

        //cartItem의 상위인 Cart를 알아야 함

        Long cno = cartItemRepository.getCartFromItem(cino);

        log.info("cart no: " + cno);

        cartItemRepository.deleteById(cino);

        return cartItemRepository.getItemsOfCartDTOByCart(cno);
    }
}
