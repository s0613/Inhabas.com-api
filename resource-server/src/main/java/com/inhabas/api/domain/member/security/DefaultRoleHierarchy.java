package com.inhabas.api.domain.member.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultRoleHierarchy implements Hierarchical {

    /* 기존 권한에 ROLE PREFIX 추가해야함. */
    private static final String ADMIN = "ROLE_ADMIN";
    private static final String CHIEF = "ROLE_CHIEF";
    private static final String EXECUTIVES = "ROLE_EXECUTIVES";
    private static final String SECRETARY = "ROLE_SECRETARY";
    private static final String BASIC = "ROLE_BASIC";
    private static final String DEACTIVATED = "ROLE_DEACTIVATED";
    private static final String NOT_APPROVED = "ROLE_NOT_APPROVED";


    @Override
    public RoleHierarchy getHierarchy() {

        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        Map<String, List<String>> roleHierarchyMap = new HashMap<>() {{
            put(
                    ADMIN,
                    Arrays.asList(CHIEF, EXECUTIVES, SECRETARY, BASIC, DEACTIVATED, NOT_APPROVED));
            put(
                    CHIEF,
                    Arrays.asList(EXECUTIVES, SECRETARY, BASIC, DEACTIVATED, NOT_APPROVED));
            put(
                    EXECUTIVES,
                    Arrays.asList(BASIC, DEACTIVATED, NOT_APPROVED));
            put(
                    SECRETARY,
                    Arrays.asList(BASIC, DEACTIVATED, NOT_APPROVED));
            put(
                    BASIC,
                    Arrays.asList(DEACTIVATED, NOT_APPROVED));
            put(
                    DEACTIVATED,
                    List.of(NOT_APPROVED));
        }};

        String roles = RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);
        roleHierarchy.setHierarchy(roles);

        return roleHierarchy;
    }
}
