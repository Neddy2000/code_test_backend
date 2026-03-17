package uk.gov.hmcts.reform.dev.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.dto.UserDto;
import uk.gov.hmcts.reform.dev.entities.User;
import uk.gov.hmcts.reform.dev.exceptions.NotFoundException;
import uk.gov.hmcts.reform.dev.repositories.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(UserDto::fromEntity)
            .collect(Collectors.toList());
    }

    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return UserDto.fromEntity(user);
    }

    // Helper method for other services to get raw entity
    protected User getUserEntity(UUID id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }
}
