package com.bookMyShow.booking_service.catalog.controller;

import com.bookMyShow.booking_service.catalog.dto.VenueRequest;
import com.bookMyShow.booking_service.catalog.dto.VenueResponse;
import com.bookMyShow.booking_service.catalog.dto.VenueUpdateRequest;
import com.bookMyShow.booking_service.catalog.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venues")
public class VenueController {
    private  final VenueService venueService;
    public VenueController(VenueService venueService){
        this.venueService = venueService;
    }

    @GetMapping
    public ResponseEntity<Page<VenueResponse>> getAllVenue(@PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(venueService.getAllVenues(pageable));
    }
    @PostMapping
    public ResponseEntity<VenueResponse> createVenue(@RequestBody @Valid VenueRequest request){
       return ResponseEntity.status(HttpStatus.CREATED).body(venueService.createVenue(request));
    }
    @PatchMapping ("/{id}")
    public ResponseEntity<VenueResponse> updateVenue(@RequestBody @Valid VenueUpdateRequest request,@PathVariable String id){
       return ResponseEntity.status(HttpStatus.OK).body(venueService.updateVenue(request,id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteVenue(@PathVariable String id){
        venueService.softDeleteVenue(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> findVenueById(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body(venueService.findVenueById(id));
    }
}
