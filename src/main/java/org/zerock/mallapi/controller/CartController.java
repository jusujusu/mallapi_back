package org.zerock.mallapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zerock.mallapi.dto.CartItemDTO;
import org.zerock.mallapi.dto.CartItemListDTO;
import org.zerock.mallapi.service.CartService;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@Log4j2
public class CartController {

    private final CartService cartService;



    @PreAuthorize("#itemDTO.email == authentication.name") //cartItemDTO의 email과 login한 email이 같아야지만 처리
    @PostMapping("/change")
    public List<CartItemListDTO> changeCart(@RequestBody CartItemDTO itemDTO) {

        log.info("itemDTO : " + itemDTO);

        if (itemDTO.getQty() <= 0) {
            return cartService.remove(itemDTO.getCino());
        }
        return cartService.addOrdModify(itemDTO);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/items")
    public List<CartItemListDTO> getCartItems(Principal principal) {

        String email = principal.getName();

        log.info("--------------------------------------------");
        log.info("email: " + email );
        return cartService.getCartItems(email);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @DeleteMapping("/{cino}")
    public List<CartItemListDTO> removeFromCart(
            @PathVariable("cino") Long cino
    ){
        log.info("cart item no: " + cino);
        return cartService.remove(cino);
    }

}
