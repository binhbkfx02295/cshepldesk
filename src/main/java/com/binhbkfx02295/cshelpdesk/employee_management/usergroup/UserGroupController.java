package com.binhbkfx02295.cshelpdesk.employee_management.usergroup;

import com.binhbkfx02295.cshelpdesk.util.APIResponseEntityHelper;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-management/user-group")
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService userGroupService;

    @PostMapping
    public ResponseEntity<APIResultSet<UserGroupDTO>> createGroup(@RequestBody UserGroupDTO dto) {
        return APIResponseEntityHelper.from(userGroupService.createGroup(dto));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<APIResultSet<UserGroupDTO>> updateGroup(@PathVariable String groupId,
                                                                  @RequestBody UserGroupDTO dto) {
        return APIResponseEntityHelper.from(userGroupService.updateGroup(groupId, dto));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<APIResultSet<Void>> deleteGroup(@PathVariable String groupId) {
        return APIResponseEntityHelper.from(userGroupService.deleteGroup(groupId));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<APIResultSet<UserGroupDTO>> getGroupById(@PathVariable String groupId) {
        return APIResponseEntityHelper.from(userGroupService.getGroupById(groupId));
    }

    @GetMapping
    public ResponseEntity<APIResultSet<List<UserGroupDTO>>> getAllGroups() {
        return APIResponseEntityHelper.from(userGroupService.getAllGroups());
    }
}
