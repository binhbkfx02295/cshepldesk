package com.binhbkfx02295.cshelpdesk.facebookuser.controller;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserListDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserSearchCriteria;
import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.PaginationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facebookuser")
@Slf4j
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
    public ResponseEntity<APIResultSet<List<FacebookUserListDTO>>> getAll() {
        APIResultSet<List<FacebookUserListDTO>> resultSet = facebookUserService.getAll();
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResultSet<FacebookUserDetailDTO>> get(@PathVariable String id) {
        APIResultSet<FacebookUserDetailDTO> resultSet = facebookUserService.get(id);
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResultSet<FacebookUserDetailDTO>> createFacebookUser(@RequestBody FacebookUserDetailDTO user) {
        APIResultSet<FacebookUserDetailDTO> resultSet = facebookUserService.save(user);
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }

    @PostMapping
    public ResponseEntity<APIResultSet<FacebookUserDetailDTO>> updateFacebookUser(@RequestBody FacebookUserDetailDTO user) {
        APIResultSet<FacebookUserDetailDTO> resultSet = facebookUserService.update(user);
        return ResponseEntity.status(resultSet.getHttpCode()).body(resultSet);
    }

    @GetMapping("/search")
    public ResponseEntity<APIResultSet<PaginationResponse<FacebookUserDetailDTO>>> searchFacebookUser(
            @ModelAttribute FacebookUserSearchCriteria criteria,
            @PageableDefault(size = 10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info(criteria.toString());
        log.info(pageable.toString());
        APIResultSet<PaginationResponse<FacebookUserDetailDTO>> resultSet = facebookUserService.searchUsers(criteria, pageable);
        return APIResponseEntityHelper.from(resultSet);
    }

}
