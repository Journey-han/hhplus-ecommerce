package io.hhplus.ecommerce.app.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.ecommerce.app.application.request.CartItemRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {

    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 6.1 카트에 상품 추가
     * @param userId
     * @param request
     * @param response
     */
    public void addItemToCart(Long userId, CartItemRequest request, HttpServletResponse response) {
        Cookie cartCookie = new Cookie("cart", request.toString());
        cartCookie.setMaxAge(60 * 60);
        response.addCookie(cartCookie);
    }


    /**
     * 6.2 카트 특정 상품 삭제
     * @param productId
     * @param userId
     * @param request
     * @param response
     */
    public void deleteItemFromCart(Long productId, Long userId, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("cart".equals(cookie.getName())) {
                    try {
                        String cartData = cookie.getValue();
                        List<CartItemRequest> cartItems = objectMapper.readValue(
                                cartData, new TypeReference<List<CartItemRequest>>() {});

                        cartItems.removeIf(item -> item.getProductId().equals(productId));

                        String updatedCartData = objectMapper.writeValueAsString(cartItems);
                        Cookie updatedCookie = new Cookie("cart", updatedCartData);
                        updatedCookie.setMaxAge(60 * 60);
                        updatedCookie.setPath("/");
                        response.addCookie(updatedCookie);
                    } catch (Exception e) {
                        throw new RuntimeException("카트 비우기 실패", e);
                    }
                }
            }
        }
    }


    /**
     * 6.3 카트 상품 전부 삭제
     * @param userId
     * @param request
     * @param response
     */
    public void deleteAllItemFromCart(Long userId, CartItemRequest request, HttpServletResponse response) {
        // 카트 쿠키를 만료시켜 삭제합니다.
        Cookie cartCookie = new Cookie("cart", null);
        cartCookie.setMaxAge(0);
        cartCookie.setPath("/");
        response.addCookie(cartCookie);
    }
}
