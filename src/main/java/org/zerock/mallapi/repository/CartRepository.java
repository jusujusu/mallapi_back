package org.zerock.mallapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.mallapi.domain.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    //이메일을 이용해서 카트 찾기
    @Query("select c from Cart c where c.owner.email = :email")
    public Optional<Cart> getCartOfMember(@Param("email") String email);

}
