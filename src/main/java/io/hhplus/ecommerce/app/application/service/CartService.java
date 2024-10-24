package io.hhplus.ecommerce.app.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.ecommerce.app.application.request.CartItemRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addItemToCart(Long userId, CartItemRequest request, HttpServletResponse response) {
        Cookie cartCookie = new Cookie("cart", request.toString());
        cartCookie.setMaxAge(60 * 60);
        response.addCookie(cartCookie);
    }

    public void deleteAllItemFromCart(Long userId, CartItemRequest request, HttpServletResponse response) {
        // 카트 쿠키를 만료시켜 삭제합니다.
        Cookie cartCookie = new Cookie("cart", null);
        cartCookie.setMaxAge(0);
        cartCookie.setPath("/");
        response.addCookie(cartCookie);
    }

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
}
