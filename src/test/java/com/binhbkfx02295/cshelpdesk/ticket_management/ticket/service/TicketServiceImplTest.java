package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeService;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserService;
import com.binhbkfx02295.cshelpdesk.ticket_management.category.entity.Category;
import com.binhbkfx02295.cshelpdesk.ticket_management.emotion.entity.Emotion;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.dto.NoteDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.note.repository.NoteRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.entity.ProgressStatus;
import com.binhbkfx02295.cshelpdesk.ticket_management.satisfaction.entity.Satisfaction;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.mapper.TicketMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TicketServiceImplTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private TicketMapper mapper;
    @Mock private NoteRepository noteRepository;
    @Mock private FacebookUserService facebookUserService;
    @Mock private EmployeeService userService;
    @Mock private MasterDataCache cache;

    @InjectMocks private TicketServiceImpl ticketService;

    private static TicketDetailDTO validDTO;
    private static TicketDetailDTO validResultDTO;
    private static TicketDetailDTO wrongDTO;
    private static Employee existedEmployee;

    @BeforeAll
    public static void setUp() {
        validDTO = new TicketDetailDTO();
        validDTO.setAssignee("binhbk");
        validDTO.setFacebookUser("Abcd");
        validDTO.setProgressStatus("pending");
        validDTO.setCategory("complaint");
        validDTO.setEmotion(1);
        validDTO.setSatisfaction(5);

        wrongDTO = new TicketDetailDTO();

        existedEmployee = new Employee();
        existedEmployee.setUsername("binhbk");
        FacebookUser existedFacebookUser = new FacebookUser();
        existedFacebookUser.setFacebookId("Abcd");

        validResultDTO = new TicketDetailDTO();


    }

    @BeforeAll
    public static void tearDown() {

    }

    @Order(1)
    @Test void testCreateTicket_NullAssignee_failed() {
        APIResultSet<TicketDetailDTO> result = ticketService.createTicket(wrongDTO);

        assertEquals(400, result.getHttpCode());
    }
    @Order(2)
    @Test void testCreateTicket_NullFacebookUser_failed() {
        wrongDTO.setAssignee("binhbk");
        when(cache.getEmployee("binhbk")).thenReturn(existedEmployee);

        APIResultSet<TicketDetailDTO> result = ticketService.createTicket(wrongDTO);


        assertEquals(400, result.getHttpCode());
    }
    @Order(3)
    @Test void testCreateTicket_invalidAssignee_failed() {
        wrongDTO.setAssignee("binhbk111");
        APIResultSet<TicketDetailDTO> result = ticketService.createTicket(wrongDTO);


        assertEquals(400, result.getHttpCode());
    }

    @Order(4)
    @Test void testCreateTicket_invalidFacebookUser_failed() {
        wrongDTO.setAssignee("binhbk");

        wrongDTO.setFacebookUser("MEo Con");

        when(cache.getEmployee("binhbk")).thenReturn(existedEmployee);
        when(facebookUserService.existsById("MEo Con")).thenReturn(APIResultSet.badRequest());
        APIResultSet<TicketDetailDTO> result = ticketService.createTicket(wrongDTO);

        assertEquals(400, result.getHttpCode());
    }

    @Order(5)
    @Test void testCreateTicketSuccess() {

        when(cache.getEmployee(any())).thenReturn(new Employee());
        when(cache.getProgress(any())).thenReturn(new ProgressStatus());
        when(cache.getCategory(anyString())).thenReturn(new Category());
        when(cache.getEmotion(anyInt())).thenReturn(new Emotion());
        when(cache.getSatisfaction(anyInt())).thenReturn(new Satisfaction());
        when(facebookUserService.existsById("Abcd")).thenReturn(APIResultSet.ok());
        when(ticketRepository.save(any())).thenReturn(new Ticket());

        APIResultSet<TicketDetailDTO> result = ticketService.createTicket(validDTO);
        assertEquals(200, result.getHttpCode());

    }


    @Test void testGetTicketById_sucess() {
        Ticket ticket = new Ticket();
        when(ticketRepository.findByIdWithDetails(anyInt())).thenReturn(Optional.of(ticket));
        when(mapper.toDetailDTO(ticket)).thenReturn(validResultDTO);

        APIResultSet<TicketDetailDTO> result = ticketService.getTicketById(1);
        assertEquals(200, result.getHttpCode());
        assertEquals(validResultDTO, result.getData());
    }

    @Test void testFindTicketsByFacebookUserId_success() {
        when(ticketRepository.findAllByFacebookUser_FacebookId(anyString())).thenReturn(List.of());
        APIResultSet<?> result = ticketService.findAllByFacebookUserId("abc");
        assertEquals(200, result.getHttpCode());
    }



    @Test void testAddNoteTicketNotFound() {
        when(ticketRepository.findById(anyInt())).thenReturn(Optional.empty());
        APIResultSet<Void> result = ticketService.addNoteToTicket(1, new NoteDTO());
        assertEquals(404, result.getHttpCode());
    }

    @Test void testAddNoteSuccess() {
        Ticket ticket = new Ticket();
        when(ticketRepository.findById(anyInt())).thenReturn(Optional.of(ticket));
        when(ticketRepository.existsById(anyInt())).thenReturn(true);
        when(noteRepository.save(any())).thenReturn(new com.binhbkfx02295.cshelpdesk.ticket_management.note.entity.Note());
        NoteDTO dto = new NoteDTO();
        dto.setTicketId(1);
        APIResultSet<Void> result = ticketService.addNoteToTicket(1, dto);
        assertEquals(200, result.getHttpCode());
    }

    @Test void testRemoveNoteSuccess() {
        when(noteRepository.existsByIdAndTicket_Id(anyInt(), anyInt())).thenReturn(true);
        APIResultSet<Void> result = ticketService.deleteNoteFromTicket(1, 1);
        assertEquals(200, result.getHttpCode());
    }


}