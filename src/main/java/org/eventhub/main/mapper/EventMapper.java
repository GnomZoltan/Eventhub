package org.eventhub.main.mapper;

import org.eventhub.main.dto.EventResponse;
import org.eventhub.main.dto.EventRequest;
import org.eventhub.main.exception.NullDtoReferenceException;
import org.eventhub.main.exception.NullEntityReferenceException;
import org.eventhub.main.model.Event;
import org.eventhub.main.model.Photo;
import org.eventhub.main.model.State;
import org.eventhub.main.repository.PhotoRepository;
import org.eventhub.main.service.CategoryService;
import org.eventhub.main.service.PhotoService;
import org.eventhub.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class EventMapper {
    private final UserService userService;
    private final CategoryService categoryService;
    private final PhotoRepository photoRepository;
    private final CategoryMapper categoryMapper;
    private final PhotoMapper photoMapper;

    @Autowired
    public EventMapper(UserService userService, CategoryService categoryService,PhotoRepository photoRepository ,CategoryMapper categoryMapper, PhotoMapper photoMapper) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.photoRepository = photoRepository;
        this.categoryMapper = categoryMapper;
        this.photoMapper = photoMapper;
    }

    public EventResponse entityToResponse(Event event) {
        if (event == null) {
            throw new NullEntityReferenceException("Event can't be found");
        }
        EventResponse response = EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .maxParticipants(event.getMaxParticipants())
                .createdAt(event.getCreatedAt())
                .startAt(event.getStartAt())
                .expireAt(event.getExpireAt())
                .description(event.getDescription())
                .participantCount(event.getParticipantCount())
                .withOwner(event.isWithOwner())
                .location(event.getLocation())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .state(getState(event))
                .categoryResponses(event.getCategories()
                        .stream()
                        .map(categoryMapper::entityToResponse)
                        .collect(Collectors.toList()))
                .photoResponses(event.getPhotos()
                        .stream()
                        .map(photoMapper::entityToResponse)
                        .collect(Collectors.toList()))
                .ownerId(event.getOwner().getId())
                .build();

        if(response.getPhotoResponses().isEmpty()){
            Photo photo = photoRepository.findPhotoByPhotoName("eventDefaultImage");
            response.getPhotoResponses().add(photoMapper.entityToResponse(photo));
        }
        return response;
    }

    public Event requestToEntity(EventRequest eventRequest, Event event) {
        if(eventRequest == null){
            throw new NullDtoReferenceException("EventRequest can't be null");
        }
        if(event == null){
            throw new NullEntityReferenceException("Event can't be null");
        }
        event.setTitle(eventRequest.getTitle());
        event.setMaxParticipants(eventRequest.getMaxParticipants());
        event.setStartAt(eventRequest.getStartAt());
        event.setExpireAt(eventRequest.getExpireAt());
        event.setDescription(eventRequest.getDescription());
        event.setWithOwner(eventRequest.isWithOwner());
        event.setLocation(eventRequest.getLocation());
        event.setLatitude(eventRequest.getLatitude());
        event.setLongitude(eventRequest.getLongitude());
        event.setParticipantCount(eventRequest.getCurrentCount());
        event.setCategories(eventRequest.getCategoryRequests().stream()
                .map(categoryRequest -> categoryService.getByName(categoryRequest.getName()))
                .collect(Collectors.toList()));
        event.setOwner(userService.readByIdEntity(eventRequest.getOwnerId()));
        return event;
    }

    private State getState(Event event) {
        int dateComparisonResultStartAt = LocalDateTime.now().compareTo(event.getStartAt());
        int dateComparisonResultExpireAt = LocalDateTime.now().compareTo(event.getExpireAt());

        if (dateComparisonResultStartAt < 0) {return State.UPCOMING;}
        else if (dateComparisonResultExpireAt <= 0) {return State.LIVE;}
        else {return State.PAST;}
    }
}
