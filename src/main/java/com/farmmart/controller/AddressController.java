package com.farmmart.controller;

import com.farmmart.model.Address;
import com.farmmart.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * replaces: route/address.route.js  +  controllers/address.controller.js
 */
@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepo;

    // POST /api/address/create
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Address address, Authentication auth) {
        address.setUserId(auth.getName());
        Address saved = addressRepo.save(address);
        return ResponseEntity.ok(Map.of("data", saved,
            "message", "Address Created", "error", false, "success", true));
    }

    // GET /api/address/get
    @GetMapping("/get")
    public ResponseEntity<?> getAll(Authentication auth) {
        return ResponseEntity.ok(Map.of(
            "data",    addressRepo.findByUserIdAndStatusTrue(auth.getName()),
            "error",   false,
            "success", true
        ));
    }

    // PUT /api/address/update
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String, Object> body, Authentication auth) {
        String id = (String) body.get("_id");
        if (id == null) return ResponseEntity.badRequest().body(Map.of("message", "Provide _id"));

        Address addr = addressRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        if (body.get("address_line") != null) addr.setAddressLine((String) body.get("address_line"));
        if (body.get("city")         != null) addr.setCity((String) body.get("city"));
        if (body.get("state")        != null) addr.setState((String) body.get("state"));
        if (body.get("pincode")      != null) addr.setPincode((String) body.get("pincode"));
        if (body.get("country")      != null) addr.setCountry((String) body.get("country"));
        if (body.get("mobile")       != null) addr.setMobile(Long.parseLong(body.get("mobile").toString()));

        addressRepo.save(addr);
        return ResponseEntity.ok(Map.of("message", "Updated successfully", "error", false, "success", true));
    }

    // DELETE /api/address/disable
    @DeleteMapping("/disable")
    public ResponseEntity<?> disable(@RequestBody Map<String, String> body) {
        if (body.get("_id") == null) return ResponseEntity.badRequest().body(Map.of("message", "Provide _id"));

        Address addr = addressRepo.findById(body.get("_id"))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
        addr.setStatus(false);
        addressRepo.save(addr);
        return ResponseEntity.ok(Map.of("message", "Address disabled", "error", false, "success", true));
    }
}
