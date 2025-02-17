package com.cbcode.dealertasks.Valet.controller;

import com.cbcode.dealertasks.Valet.service.ValetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/valet")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_VALET')")
public class ValetController {

    private final ValetService valetService;

    public ValetController(ValetService valetService) {
        this.valetService = valetService;
    }

    @PatchMapping(value = "/{id}/status", produces = "application/json")
    public ResponseEntity<?> updateValetStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(valetService.updateValetStatus(id, status));
    }
}
