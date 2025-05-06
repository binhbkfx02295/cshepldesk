package com.binhbkfx02295.cshelpdesk.ticket_management.tag.service;

import com.binhbkfx02295.cshelpdesk.ticket_management.tag.dto.TagDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.entity.Tag;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.repository.TagRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public APIResultSet<TagDTO> create(TagDTO tagDTO) {
        try {
            if (tagRepository.existsByName(tagDTO.getName())) {
                return APIResultSet.badRequest("Hashtag đã tồn tại: " + tagDTO.getName());
            }
            Tag tag = Tag.builder()
                    .name(tagDTO.getName())
                    .build();
            Tag saved = tagRepository.save(tag);
            return APIResultSet.ok("Thêm hashtag thành công", toDTO(saved));
        } catch (Exception e) {
            log.error("Lỗi khi tạo hashtag", e);
            return APIResultSet.internalError("Lỗi khi tạo hashtag");
        }
    }

    @Override
    public APIResultSet<TagDTO> update(int id, TagDTO newTagDTO) {
        Optional<Tag> optional = tagRepository.findById(id);
        if (optional.isEmpty()) {
            return APIResultSet.notFound("Không tìm thấy hashtag ID: " + id);
        }
        try {
            Tag tag = optional.get();
            tag.setName(newTagDTO.getName());
            Tag updated = tagRepository.save(tag);
            return APIResultSet.ok("Cập nhật hashtag thành công", toDTO(updated));
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật hashtag", e);
            return APIResultSet.internalError("Lỗi khi cập nhật hashtag");
        }
    }

    @Override
    public APIResultSet<Void> delete(int id) {
        Optional<Tag> optional = tagRepository.findById(id);
        if (optional.isEmpty()) {
            return APIResultSet.notFound("Không tìm thấy hashtag để xóa ID: " + id);
        }
        try {
            Tag tag = optional.get();
            List<Ticket> tickets = tag.getTickets();
            tickets.forEach(ticket -> ticket.getTags().remove(tag));
            tagRepository.deleteById(id);
            return APIResultSet.ok("Đã xóa hashtag và các liên kết với ticket", null);
        } catch (Exception e) {
            log.error("Lỗi khi xóa hashtag", e);
            return APIResultSet.internalError("Lỗi khi xóa hashtag");
        }
    }

    @Override
    public APIResultSet<List<TagDTO>> search(String keyword) {
        try {
            List<Tag> result = tagRepository.findByNameContainingIgnoreCase(keyword);
            List<TagDTO> dtos = result.stream().map(this::toDTO).toList();
            return APIResultSet.ok("Tìm thấy " + dtos.size() + " hashtag", dtos);
        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm hashtag: {}", keyword, e);
            return APIResultSet.internalError("Không thể tìm kiếm hashtag");
        }
    }

    @Override
    public APIResultSet<List<TagDTO>> getAll() {
        try {
            List<TagDTO> allTags = tagRepository.findAll().stream()
                    .map(this::toDTO)
                    .toList();
            return APIResultSet.ok("Lấy tất cả hashtag thành công", allTags);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách hashtag", e);
            return APIResultSet.internalError("Không thể lấy danh sách hashtag");
        }
    }

    private TagDTO toDTO(Tag tag) {
        return new TagDTO(tag.getId(), tag.getName(), null);
    }
}
