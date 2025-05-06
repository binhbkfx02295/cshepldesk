package com.binhbkfx02295.cshelpdesk.employee_management.usergroup;

import com.binhbkfx02295.cshelpdesk.employee_management.permission.Permission;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.permission.PermissionRepository;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.repository.EmployeeRepository;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserGroupServiceImpl implements UserGroupService {

    private final UserGroupRepository userGroupRepository;
    private final PermissionRepository permissionRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public APIResultSet<UserGroupDTO> createGroup(UserGroupDTO groupDTO) {
        try {
            UserGroup group = new UserGroup();
            group.setName(groupDTO.getName());
            group.setPermissions(fetchPermissions(groupDTO.getPermissionIds()));
            group.setDescription(groupDTO.getDescription());
            UserGroup saved = userGroupRepository.save(group);
            log.info("✅ Đã tạo nhóm quyền: {}", saved.getName());
            return APIResultSet.ok("Tạo nhóm thành công", convertToDTO(saved));
        } catch (Exception e) {
            log.error("❌ Lỗi khi tạo nhóm quyền: {}", e.getMessage(), e);
            return APIResultSet.internalError("Tạo nhóm quyền thất bại: " + e.getMessage());
        }
    }

    @Override
    public APIResultSet<UserGroupDTO> updateGroup(String groupId, UserGroupDTO groupDTO) {
        try {
            int id = Integer.parseInt(groupId);
            UserGroup group = userGroupRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhóm có ID: " + groupId));

            group.setName(groupDTO.getName());
            group.setPermissions(fetchPermissions(groupDTO.getPermissionIds()));
            group.setDescription(groupDTO.getDescription());

            UserGroup updated = userGroupRepository.save(group);
            log.info("✅ Cập nhật nhóm quyền ID {} thành công", groupId);
            return APIResultSet.ok("Cập nhật nhóm thành công", convertToDTO(updated));
        } catch (NoSuchElementException e) {
            log.warn("⚠️ Không tìm thấy nhóm quyền ID {} để cập nhật", groupId);
            return APIResultSet.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Lỗi khi cập nhật nhóm quyền ID {}: {}", groupId, e.getMessage(), e);
            return APIResultSet.internalError("Cập nhật nhóm thất bại: " + e.getMessage());
        }
    }

    @Override
    public APIResultSet<Void> deleteGroup(String groupId) {
        try {
            int id = Integer.parseInt(groupId);
            Optional<UserGroup> optional = userGroupRepository.findById(id);

            if (optional.isEmpty()) {
                log.warn("⚠️ Không tìm thấy nhóm quyền ID {} để xoá", groupId);
                return APIResultSet.notFound("Không tìm thấy nhóm để xoá");
            }

            List<Employee> usersInGroup = employeeRepository.findByUserGroup_GroupId(id);
            for (Employee user : usersInGroup) {
                user.setUserGroup(null);
            }
            employeeRepository.saveAll(usersInGroup);
            userGroupRepository.deleteById(id);

            log.info("🗑️ Đã xoá nhóm quyền ID {} và gỡ liên kết khỏi {} người dùng", groupId, usersInGroup.size());
            return APIResultSet.ok("Xoá nhóm và gỡ liên kết người dùng thành công", null);

        } catch (Exception e) {
            log.error("❌ Lỗi khi xoá nhóm quyền ID {}: {}", groupId, e.getMessage(), e);
            return APIResultSet.internalError("Lỗi khi xoá nhóm: " + e.getMessage());
        }
    }

    @Override
    public APIResultSet<UserGroupDTO> getGroupById(String groupId) {
        try {
            int id = Integer.parseInt(groupId);
            UserGroup group = userGroupRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhóm quyền với ID: " + groupId));
            return APIResultSet.ok("Lấy nhóm thành công", convertToDTO(group));
        } catch (NoSuchElementException e) {
            log.warn("⚠️ Nhóm quyền ID {} không tồn tại", groupId);
            return APIResultSet.notFound(e.getMessage());
        }
    }

    @Override
    public APIResultSet<List<UserGroupDTO>> getAllGroups() {
        try {
            List<UserGroupDTO> result = new ArrayList<>();
            for (UserGroup group : userGroupRepository.findAll()) {
                result.add(convertToDTO(group));
            }
            log.info("📦 Đã lấy danh sách tất cả nhóm quyền, tổng số: {}", result.size());
            return APIResultSet.ok("Lấy danh sách nhóm thành công", result);
        } catch (Exception e) {
            log.error("❌ Lỗi khi lấy danh sách nhóm quyền: {}", e.getMessage(), e);
            return APIResultSet.internalError("Không thể lấy danh sách nhóm quyền: " + e.getMessage());
        }
    }

    private Set<Permission> fetchPermissions(Set<Integer> permissionIds) {
        return new HashSet<>(permissionRepository.findAllById(permissionIds));
    }

    private UserGroupDTO convertToDTO(UserGroup group) {
        UserGroupDTO dto = new UserGroupDTO();
        dto.setGroupId(group.getGroupId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setPermissionIds(
                new HashSet<>(group.getPermissions()).stream()
                        .map(Permission::getId)
                        .collect(Collectors.toSet())
        );
        return dto;
    }
}
