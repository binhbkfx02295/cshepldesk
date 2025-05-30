package com.binhbkfx02295.cshelpdesk.ticket_management.tag.service;

import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.dto.TagDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.entity.Tag;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.mapper.TagMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.tag.repository.TagRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
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
    private final MasterDataCache cache;
    private TagMapper mapper;

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
            cache.getAllTag().put(saved.getId(), saved);
            return APIResultSet.ok("Thêm hashtag thành công", mapper.toDTO(saved));
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
            if (cache.getAllTag().containsKey(updated)) {
                cache.getAllTag().put(updated.getId(), updated);
            }
            return APIResultSet.ok("Cập nhật hashtag thành công", mapper.toDTO(updated));
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
            cache.getAllTag().remove(id);
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
            List<TagDTO> dtos = result.stream().map(mapper::toDTO).toList();
            return APIResultSet.ok("Tìm thấy " + dtos.size() + " hashtag", dtos);
        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm hashtag: {}", keyword, e);
            return APIResultSet.internalError("Không thể tìm kiếm hashtag");
        }
    }

    @Override
    public APIResultSet<List<TagDTO>> getAll() {
        try {
            List<TagDTO> allTags = cache.getAllTag().values().stream()
                    .map(mapper::toDTO)
                    .toList();
            return APIResultSet.ok("Lấy tất cả hashtag thành công", allTags);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách hashtag", e);
            return APIResultSet.internalError("Không thể lấy danh sách hashtag");
        }
    }
}
