package com.project.qa.utils;

import javax.ws.rs.core.Response;

public class KeycloakUtils {

    public static String getEntityId(Response response) {
        return response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
    }
}
