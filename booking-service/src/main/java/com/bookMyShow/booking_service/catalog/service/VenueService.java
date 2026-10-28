package com.bookMyShow.booking_service.catalog.service;

import com.bookMyShow.booking_service.catalog.dto.VenueRequest;
import com.bookMyShow.booking_service.catalog.dto.VenueResponse;
import com.bookMyShow.booking_service.catalog.dto.VenueUpdateRequest;
import com.bookMyShow.booking_service.catalog.entity.Venue;
import com.bookMyShow.booking_service.catalog.repository.VenueRepository;
import com.bookMyShow.booking_service.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VenueService {
    private final VenueRepository venueRepository;
    public VenueService(VenueRepository venueRepository){
        this.venueRepository=venueRepository;
    }

    public Page<VenueResponse> getAllVenues(Pageable pageable){
        return venueRepository.findAll(pageable).map(this::mapToResponse);
    }
    public VenueResponse createVenue(VenueRequest request){
        Venue venue = Venue.builder()
                .name(request.name())
                .address(request.address())
                .city(request.city())
                .active(Boolean.TRUE).build();
        Venue savedVenue = venueRepository.save(venue);

        return mapToResponse(savedVenue);
    }

    public VenueResponse updateVenue(VenueUpdateRequest request,String id){
        Venue venue  = venueRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Venue not found with id "+id));
        if (request.name()!=null){
            venue.setName(request.name());
        }
        if (request.address()!=null){
            venue.setAddress(request.address());
        }
        if (request.city()!=null){
            venue.setCity(request.city());
        }
        if (request.active()!=null){
            venue.setActive(request.active());
        }

        return mapToResponse(venueRepository.save(venue));
    }

    public  VenueResponse findVenueById(String id){
        Venue venue  = venueRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Venue not found with id "+id));
        return mapToResponse(venue);

    }

    public void softDeleteVenue(String id){
        Venue venue  = venueRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Venue not found with id "+id));
        venue.setActive(Boolean.FALSE);
        venueRepository.save(venue);
    }



    private VenueResponse mapToResponse(Venue venue){
        return new VenueResponse(venue.getId(),
                venue.getName(),
                venue.getAddress(),
                venue.getCity(),
                venue.getActive());
    }
}
