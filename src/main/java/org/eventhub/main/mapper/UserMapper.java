package org.eventhub.main.mapper;

import org.eventhub.main.dto.UserResponse;
import org.eventhub.main.dto.UserRequest;
import org.eventhub.main.exception.NullDtoReferenceException;
import org.eventhub.main.exception.NullEntityReferenceException;
import org.eventhub.main.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserMapper {
    public UserResponse entityToResponse(User user) {
        if (user == null) {
            throw new NullEntityReferenceException("User can't be found");
        }

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .description(user.getDescription())
                .createdAt(user.getCreatedAt())
                .city(user.getCity())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .build();
    }

    public User requestToEntity(UserRequest userRequest, User user) {
        if(userRequest == null){
            throw new NullDtoReferenceException("UserRequest can't be null");
        }
        if(user == null){
            throw new NullEntityReferenceException("User can't be null");
        }

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setProfileImage(userRequest.getProfileImage());
        user.setDescription(userRequest.getDescription());
        user.setCreatedAt(LocalDateTime.now());
        user.setCity(userRequest.getCity());
        user.setBirthDate(userRequest.getBirthDate());
        user.setGender(userRequest.getGender());

        return user;
    }
}
