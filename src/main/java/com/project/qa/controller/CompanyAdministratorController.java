package com.project.qa.controller;

import com.project.qa.model.CustomUser;
import com.project.qa.service.AdminService;
import com.project.qa.service.CompanyAdministratorService;
import com.project.qa.service.GroupService;
import com.project.qa.utils.CsvUtils;
import org.apache.http.HttpException;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/company")
public class CompanyAdministratorController {

    public final AdminService adminService;
    public final GroupService groupService;
    public final CompanyAdministratorService companyAdministratorService;

    @Autowired
    public CompanyAdministratorController(AdminService adminService, GroupService groupService, CompanyAdministratorService companyAdministratorService) {
        this.adminService = adminService;
        this.groupService = groupService;
        this.companyAdministratorService = companyAdministratorService;
    }

    @PostMapping(path = "/addUser")
    public String addUser(HttpServletRequest request, @RequestBody CustomUser customUser) throws HttpException {
        return adminService.addUser(request, customUser);
    }

    @PostMapping(path = "/addGroup")
    public void addGroup(HttpServletRequest request, @RequestBody String name) {
        companyAdministratorService.addGroup(request, name);
    }

    @DeleteMapping(path = "/deleteGroup")
    public void deleteGroup(HttpServletRequest request, @RequestParam String id) {
        companyAdministratorService.deleteGroupById(request, id);
    }

    @GetMapping(path = "/users")
    public Map<String, Object> findAllUsersByGroup(HttpServletRequest request, @RequestParam int page, @RequestParam int size) {
        return companyAdministratorService.findAllUsersByGroup(request, PageRequest.of(page, size));
    }

    @DeleteMapping(path = "/deleteUser")
    public Response deleteGroupWithUsers(HttpServletRequest request, @RequestParam String userId) {
        return companyAdministratorService.deleteUserFromGroup(request, userId);
    }

    @GetMapping(path = "/findUser")
    public UserRepresentation findUserById(HttpServletRequest request, @RequestParam String userId) {
        return companyAdministratorService.findUserById(request, userId);
    }

    @PutMapping(path = "/editUser")
    public void editUser(HttpServletRequest request, @RequestBody UserRepresentation userRepresentation) {
        companyAdministratorService.editUser(request, userRepresentation);
    }


    @PostMapping(value = "/uploadCsv", consumes = "text/csv")
    public void uploadSimple(@RequestBody InputStream body) throws IOException {
        companyAdministratorService.saveAllUsers(CsvUtils.read(UserRepresentation.class, body));
    }

    @PostMapping(value = "/uploadCsv", consumes = "multipart/form-data")
    public void uploadMultipart(@RequestParam("file") MultipartFile file) throws IOException {
        companyAdministratorService.saveAllUsers(CsvUtils.read(UserRepresentation.class, file.getInputStream()));
    }

}
