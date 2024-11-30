package com.farmmart.controller;

import com.farmmart.model.CartProduct;
import com.farmmart.repository.CartProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * replaces: route/cart.route.js  +  controllers/cart.controller.js
 *
 * Authentication auth parameter — Spring auto-fills this from JwtAuthFilter.
 * auth.getName() returns the userId. This replaces: request.userId from Node middleware.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartProductRepository cartRepo;

    // POST /api/cart/create
    // replaces: addToCartItemController
    @PostMapping("/create")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, String> body,
                                       Authentication auth) {
        String userId    = auth.getName(); // replaces: request.userId
        String productId = body.get("productId");

        if (productId == null) {
            return ResponseEntity.status(402).body(
                Map.of("message", "Provide productId", "error", true, "success", false));
        }

        // replaces: CartProductModel.findOne({ userId, productId })
        if (cartRepo.findByUserIdAndProductId(userId, productId).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Item already in cart"));
        }

        CartProduct item = new CartProduct();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setQuantity(1);
        CartProduct saved = cartRepo.save(item);

        return ResponseEntity.ok(Map.of("data", saved,
            "message", "Item add successfully", "error", false, "success", true));
    }

    // GET /api/cart/get
    // replaces: getCartItemController
    @GetMapping("/get")
    public ResponseEntity<?> getCart(Authentication auth) {
        String userId = auth.getName();
        // NOTE: In Node you used .populate('productId') to get full product details.
        // In Spring, the cart items contain productId strings.
        // The frontend can fetch product details separately, or you can add a join here.
        return ResponseEntity.ok(Map.of("data", cartRepo.findByUserId(userId),
            "error", false, "success", true));
    }

    // PUT /api/cart/update-qty
    // replaces: updateCartItemQtyController
    @PutMapping("/update-qty")
    public ResponseEntity<?> updateQty(@RequestBody Map<String, Object> body,
                                       Authentication auth) {
        String id  = (String) body.get("_id");
        Object qtyObj = body.get("qty");

        if (id == null || qtyObj == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "provide _id, qty"));
        }

        int qty = Integer.parseInt(qtyObj.toString());

        CartProduct item = cartRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        item.setQuantity(qty);
        cartRepo.save(item);

        return ResponseEntity.ok(Map.of("message", "Update cart", "success", true, "error", false));
    }

    // DELETE /api/cart/delete-cart-item
    // replaces: deleteCartItemQtyController
    @DeleteMapping("/delete-cart-item")
    public ResponseEntity<?> deleteItem(@RequestBody Map<String, String> body,
                                        Authentication auth) {
        String userId = auth.getName();
        String id     = body.get("_id");

        if (id == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Provide _id", "error", true, "success", false));
        }

        cartRepo.deleteByIdAndUserId(id, userId);

        return ResponseEntity.ok(Map.of("message", "Item remove", "error", false, "success", true));
    }
}
