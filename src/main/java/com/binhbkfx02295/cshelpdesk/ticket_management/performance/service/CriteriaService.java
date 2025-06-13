package com.binhbkfx02295.cshelpdesk.ticket_management.performance.service;


import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.CriteriaDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.dto.CriteriaDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.performance.model.Criteria;

import java.util.List;
public interface CriteriaService {

    public static final String FIND_SUCCESS = "Lấy criteria thành công";

    public static final String FIND_ALL_SUCCESS = "Lấy danh sách criteria thành công";

    public static final String EXISTS_TRUE = "Truy vấn tồn tại";

    public static final String EXISTS_FALSE = "Truy vấn không tồn tại";

    public static final String DELETE_OK = "Xóa criteria thành công";

    public static final String UPDATE_OK = "Cập nhật criteria thành công";

    public static final String CREATE_SUCCESS = "Khởi tạo criteria thành công";


    APIResultSet<List<CriteriaDTO>> findAll();

    APIResultSet<CriteriaDetailDTO> findById(Long id);

    APIResultSet<CriteriaDetailDTO> create(CriteriaDetailDTO dto);

    APIResultSet<CriteriaDetailDTO> update(Long id, CriteriaDetailDTO dto);

    APIResultSet<Void> delete(Long id);

    APIResultSet<Void> existsById(Long id);
}
