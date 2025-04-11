package ru.otus.hw.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

@Service
public class RoleCheckService {

    private final Map<UserRole, List<UserRole>> roleHierarchy = new HashMap<>();

    public RoleCheckService() {
        roleHierarchy.put(UserRole.ADMIN, List.of(UserRole.AUTHOR, UserRole.READER));
        roleHierarchy.put(UserRole.AUTHOR, List.of(UserRole.READER));
    }

    public boolean hasAccess(UserRole requiredRole) {
        List<UserRole> checkingRoles = getUserRoles();
        List<UserRole> allRoles = new ArrayList<>();
        List<UserRole> listedRoles = new ArrayList<>();

        Queue<UserRole> rolesToCheck = new LinkedList<>();
        rolesToCheck.addAll(checkingRoles);
        while (!rolesToCheck.isEmpty()) {
            UserRole role = rolesToCheck.poll();
            allRoles.add(role);
            listedRoles.add(role);
            if (roleHierarchy.containsKey(role)) {
                List<UserRole> roles = new ArrayList<>(roleHierarchy.get(role));
                roles.removeAll(listedRoles);
                rolesToCheck.addAll(roles);
            }
        }

        return allRoles.contains(requiredRole);
    }

    private List<UserRole> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<UserRole> checkingRoles = authentication.getAuthorities().stream()
                .map(grantedAuthority -> UserRole.getByAuthority(grantedAuthority.getAuthority()))
                .filter(Objects::nonNull)
                .toList();
        return checkingRoles;
    }
}
