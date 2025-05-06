package com.binhbkfx02295.cshelpdesk.ticket_management.category.service;

import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.dto.CategoryDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.repository.CategoryRepository;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public APIResultSet<CategoryDTO> create(CategoryDTO categoryDTO) {
        Category category = toEntity(categoryDTO);
        try {
            if (categoryRepository.existsByCode(categoryDTO.getCode())) {
                return APIResultSet.badRequest("Mã danh mục đã tồn tại.");
            }
            Category saved = categoryRepository.save(category);
            return APIResultSet.ok("Tạo danh mục thành công.", categoryDTO);
        } catch (Exception e) {
            log.error("Lỗi khi tạo danh mục", e);
            return APIResultSet.internalError("Đã xảy ra lỗi khi tạo danh mục.");
        }
    }



    @Override
    public APIResultSet<CategoryDTO> update(int id, CategoryDTO categoryDTO) {
        Category category = toEntity(categoryDTO);
        try {
            Optional<Category> optional = categoryRepository.findById(id);
            if (optional.isEmpty()) {
                return APIResultSet.notFound("Không tìm thấy danh mục cần cập nhật.");
            }
            Category existing = optional.get();
            existing.setName(category.getName());
            existing.setCode(category.getCode());
            Category updated = categoryRepository.save(existing);
            return APIResultSet.ok("Cập nhật danh mục thành công.", toDTO(updated));
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật danh mục", e);
            return APIResultSet.internalError("Đã xảy ra lỗi khi cập nhật danh mục.");
        }
    }

    @Override
    public APIResultSet<Void> delete(int id) {
        try {
            categoryRepository.deleteById(id);
            return APIResultSet.ok("Xóa danh mục thành công.", null);
        } catch (Exception e) {
            log.error("Lỗi khi xóa danh mục", e);
            return APIResultSet.internalError("Không thể xóa danh mục. Có thể đang được liên kết với dữ liệu khác.");
        }
    }

    @Override
    public APIResultSet<List<CategoryDTO>> getAll() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return APIResultSet.ok("Lấy danh sách danh mục thành công.", categories.stream().map(this::toDTO).toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách danh mục", e);
            return APIResultSet.internalError("Đã xảy ra lỗi khi lấy danh sách danh mục.");
        }
    }

    private Category toEntity(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setCode(categoryDTO.getCode());
        category.setName(categoryDTO.getName());
        return category;
    }

    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setCode(category.getCode());
        dto.setName(category.getName());
        return dto;
    }
}
