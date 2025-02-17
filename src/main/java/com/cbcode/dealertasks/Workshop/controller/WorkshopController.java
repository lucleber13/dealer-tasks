package com.cbcode.dealertasks.Workshop.controller;

import com.cbcode.dealertasks.Workshop.service.WorkshopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workshop")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_WORKSHOP')")
public class WorkshopController {

    private final WorkshopService workshopService;

    public WorkshopController(WorkshopService workshopService) {
        this.workshopService = workshopService;
    }

    @PatchMapping(value = "/{id}/status", produces = "application/json")
    public ResponseEntity<?> updateWorkshopStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(workshopService.updateWorkshopStatus(id, status));
    }

}
