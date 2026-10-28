package com.bookMyShow.booking_service.catalog.controller;

import com.bookMyShow.booking_service.catalog.dto.ShowRequest;
import com.bookMyShow.booking_service.catalog.dto.ShowResponse;
import com.bookMyShow.booking_service.catalog.dto.ShowUpdateRequest;
import com.bookMyShow.booking_service.catalog.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shows")
public class ShowController {
    private final ShowService showService;
    public ShowController(ShowService showService){
        this.showService = showService;
    }
    @GetMapping
    public ResponseEntity<Page<ShowResponse>> getAllShows(@PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(showService.getAllShows(pageable));
    }
    @PostMapping
    public ResponseEntity<ShowResponse> createShow(@RequestBody @Valid ShowRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(showService.createShow(request));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ShowResponse> updateShow(@RequestBody @Valid ShowUpdateRequest request,@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body(showService.updateShow(request,id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ShowResponse> softDeleteShow(@PathVariable String id){
        showService.softDeleteShow(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<ShowResponse> getShowById(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body(showService.getShowById(id));
    }
}
