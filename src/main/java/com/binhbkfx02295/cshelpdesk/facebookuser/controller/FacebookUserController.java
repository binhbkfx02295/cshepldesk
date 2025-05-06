package com.binhbkfx02295.cshelpdesk.facebookuser.controller;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facebookuser")
public class FacebookUserController {

    FacebookUserServiceImpl facebookUserService;

    @Autowired
    public FacebookUserController(FacebookUserServiceImpl service) {
        facebookUserService = service;
    }

    @GetMapping("/ping")
    public String ping() {
        return "it works";
    }

    //get all
    @GetMapping()
    public ResponseEntity<APIResultSet<List<FacebookUserDTO>>> getAll() {
        APIResultSet<List<FacebookUserDTO>> resultSet = facebookUserService.getAll();
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResultSet<FacebookUserDTO>> get(@PathVariable String id) {
        APIResultSet<FacebookUserDTO> resultSet = facebookUserService.get(id);
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }

    @PutMapping
    public ResponseEntity<APIResultSet<FacebookUserDTO>> createFacebookUser(@RequestBody FacebookUser user) {
        APIResultSet<FacebookUserDTO> resultSet = facebookUserService.save(user);
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }

    @PostMapping
    public ResponseEntity<APIResultSet<FacebookUserDTO>> updateFacebookUser(@RequestBody FacebookUser user) {
        APIResultSet<FacebookUserDTO> resultSet = facebookUserService.update(user);
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }

}
