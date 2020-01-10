package com.project.qa.utils;

import java.util.List;

import static com.project.qa.enums.RequiredAction.UPDATE_PASSWORD;
import static com.project.qa.enums.RequiredAction.VERIFY_EMAIL;
import static java.util.Arrays.asList;

public class UserUtils {

    public final static List<String> defaultRequiredActions = asList(VERIFY_EMAIL.name(), UPDATE_PASSWORD.toString());
}
