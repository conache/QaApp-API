package com.project.qa.utils;

import com.project.qa.enums.Roles;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.qa.enums.RequiredAction.UPDATE_PASSWORD;
import static com.project.qa.enums.RequiredAction.VERIFY_EMAIL;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.CollectionUtils.isEmpty;

public class UserUtils {

    public final static List<String> defaultRequiredActions = asList(VERIFY_EMAIL.name(), UPDATE_PASSWORD.toString());
    public final static String ROLE = "role";
    public final static String GROUP = "group";


    public static void addUserAttribute(UserRepresentation user, String attributeKey, List<String> attributeValue) {
        Map<String, List<String>> userAttributes = user.getAttributes();
        if (isEmpty(userAttributes)) {
            userAttributes = new HashMap<>();
        }
        userAttributes.put(attributeKey, attributeValue);
        user.setAttributes(userAttributes);
    }

    public static List<String> getUserAttribute(UserRepresentation user, String attributeKey) {
        Map<String, List<String>> userAttributes = user.getAttributes();
        if (isEmpty(userAttributes)) {
            throw new ResponseStatusException(NOT_FOUND, "User doesn't have attributes");
        }
        List<String> attributeValue = userAttributes.get(attributeKey);
        if (isEmpty(attributeValue)) {
            throw new ResponseStatusException(NOT_FOUND, "User attribute " + attributeKey + " not found");
        }
        return attributeValue;
    }
}
