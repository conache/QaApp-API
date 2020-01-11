package com.project.qa.service;

import javax.servlet.http.HttpServletRequest;

public interface ClientService {
    String findClientIdByName(HttpServletRequest request, String name);
}
