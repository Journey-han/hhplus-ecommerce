package io.hhplus.ecommerce.app.api;

import io.hhplus.ecommerce.app.application.service.CartService;
import io.hhplus.ecommerce.app.application.request.CartItemRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> addItemToCart(@RequestBody CartItemRequest request, @PathVariable Long userId, HttpServletResponse response) {
        cartService.addItemToCart(userId, request, response);
        return ResponseEntity.ok("카트 상품 추가 성공");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteAllItemFromCart(@RequestParam CartItemRequest request, @PathVariable Long userId, HttpServletResponse response) {
       cartService.deleteAllItemFromCart(userId, request, response);
        return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }

    @DeleteMapping("/{productId}/{userId}")
    public ResponseEntity<String> deleteItemFromCart(
            @PathVariable Long productId,
            @PathVariable Long userId,
            HttpServletRequest request,
            HttpServletResponse response) {

        cartService.deleteItemFromCart(productId, userId, request, response);
        return ResponseEntity.status(HttpStatus.OK).body("카트 상품 삭제 성공");


        }
}
