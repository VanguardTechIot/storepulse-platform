package com.vanguardtech.storepulse.platform.iam.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.iam.domain.exceptions.IamError;
import com.vanguardtech.storepulse.platform.iam.domain.model.queries.GetUserAccountByIdQuery;
import com.vanguardtech.storepulse.platform.iam.domain.services.UserQueryService;
import com.vanguardtech.storepulse.platform.iam.infrastructure.authorization.UserPrincipal;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.UserAccountResource;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform.UserAccountResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** User accounts as seen by their owners. */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UsersController {

    private final UserQueryService userQueryService;

    /** Account of the authenticated user, used by the clients to restore a stored session. */
    @GetMapping("/me")
    public UserAccountResource getAuthenticatedUser(@AuthenticationPrincipal UserPrincipal principal) {
        return userQueryService.handle(new GetUserAccountByIdQuery(principal.userId()))
                .map(UserAccountResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(IamError.USER_NOT_FOUND));
    }
}
