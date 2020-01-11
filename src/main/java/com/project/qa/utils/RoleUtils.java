package com.project.qa.utils;

import java.util.List;

import static com.project.qa.enums.Roles.ROLE_USER;
import static java.util.Collections.singletonList;

public class RoleUtils {
    public static final List<String> DEFAULT_ROLES = singletonList(ROLE_USER.name());
}
