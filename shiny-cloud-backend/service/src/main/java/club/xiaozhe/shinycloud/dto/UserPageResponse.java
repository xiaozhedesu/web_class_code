package club.xiaozhe.shinycloud.dto;

import club.xiaozhe.shinycloud.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserPageResponse(
        List<UserResponse> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int size
) {
    public static UserPageResponse from(Page<User> userPage) {
        return new UserPageResponse(
                userPage.getContent().stream().map(UserResponse::from).toList(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.getNumber(),
                userPage.getSize()
        );
    }
}
