package com.binhbkfx02295.cshelpdesk.employee_management.permission;

import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public APIResultSet<PermissionDTO> createPermission(PermissionDTO dto) {
        try {
            Permission permission = new Permission();
            permission.setName(dto.getName());
            permission.setDescription(dto.getDescription());
            Permission saved = permissionRepository.save(permission);
            return APIResultSet.ok("Permission created successfully", convertToDTO(saved));
        } catch (Exception e) {
            return APIResultSet.internalError("Failed to create permission: " + e.getMessage());
        }
    }

    @Override
    public APIResultSet<PermissionDTO> updatePermission(String permissionId, PermissionDTO dto) {
        try {
            int id = Integer.parseInt(permissionId);
            Optional<Permission> optional = permissionRepository.findById(id);
            if (optional.isEmpty()) {
                return APIResultSet.notFound("Permission not found with ID: " + permissionId);
            }
            Permission existing = optional.get();
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            Permission updated = permissionRepository.save(existing);
            return APIResultSet.ok("Permission updated successfully", convertToDTO(updated));
        } catch (NumberFormatException e) {
            return APIResultSet.badRequest("Invalid permission ID format");
        } catch (Exception e) {
            return APIResultSet.internalError("Failed to update permission: " + e.getMessage());
        }
    }

    @Override
    public APIResultSet<Boolean> deletePermission(String permissionId) {
        try {
            int id = Integer.parseInt(permissionId);
            if (!permissionRepository.existsById(id)) {
                return APIResultSet.notFound("Permission not found with ID: " + permissionId);
            }
            permissionRepository.deleteById(id);
            return APIResultSet.ok("Permission deleted successfully", true);
        } catch (NumberFormatException e) {
            return APIResultSet.badRequest("Invalid permission ID format");
        } catch (Exception e) {
            return APIResultSet.internalError("Failed to delete permission: " + e.getMessage());
        }
    }

    @Override
    public APIResultSet<PermissionDTO> getPermissionById(String permissionId) {
        try {
            int id = Integer.parseInt(permissionId);
            Optional<Permission> optional = permissionRepository.findById(id);
            return optional.map(permission -> APIResultSet.ok("Permission found", convertToDTO(permission)))
                    .orElseGet(() -> APIResultSet.notFound("Permission not found with ID: " + permissionId));
        } catch (NumberFormatException e) {
            return APIResultSet.badRequest("Invalid permission ID format");
        } catch (Exception e) {
            return APIResultSet.internalError("Failed to get permission: " + e.getMessage());
        }
    }

    @Override
    public APIResultSet<List<PermissionDTO>> getAllPermissions() {
        try {
            List<PermissionDTO> list = permissionRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return APIResultSet.ok("Fetched all permissions", list);
        } catch (Exception e) {
            return APIResultSet.internalError("Failed to fetch permissions: " + e.getMessage());
        }
    }

    private PermissionDTO convertToDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        return dto;
    }
}
