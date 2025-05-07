package com.binhbkfx02295.cshelpdesk.ticket_management.ticket.controller;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.dto.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.service.EmployeeServiceImpl;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserServiceImpl;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketSearchCriteria;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.repository.TicketRepository;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class TicketControllerIntegrationTest {


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    private EmployeeServiceImpl employeeService;
    @Autowired
    private FacebookUserServiceImpl facebookUserService;
    @Autowired
    private TicketServiceImpl ticketService;

    @Autowired
    private final RestTemplate restTemplate = new RestTemplate();
    private TransactionStatus txStatus;
    private int ticketId;
    private TicketDetailDTO ticketDetailDTO;
    private static final HttpHeaders globalHeaders = new HttpHeaders();


    @LocalServerPort
    private int port;
    @Autowired private TicketRepository ticketRepository;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/ticket";
    }

    @BeforeAll
    void setUp() {
        ;
        globalHeaders.setContentType(MediaType.APPLICATION_JSON);
        txStatus = txManager.getTransaction(new DefaultTransactionDefinition());
        seedFacebookUser();
        seedEmployee();
        login();
        for (int i = 1; i <= 11; i++) {
            TicketDetailDTO ticketDTO = new TicketDetailDTO();
            ticketDTO.setAssignee("testacc");
            ticketDTO.setFacebookUser("test-989897796767");
            ticketDTO.setProgressStatus("pending");
            ticketDTO.setTitle(String.format("Ticket so %d", i));
            ticketService.createTicket(ticketDTO);
        }

        ticketDetailDTO = new TicketDetailDTO();
        ticketDetailDTO.setAssignee("testacc");
        ticketDetailDTO.setFacebookUser("test-989897796767");
        ticketDetailDTO.setProgressStatus("pending");


        txManager.commit(txStatus);

    }


    @AfterAll
    void tearDown() {
        // ✅ dùng globalHeaders chứa cookie để logout
        HttpEntity<Void> logoutRequest = new HttpEntity<>(null, globalHeaders);
        restTemplate.exchange("http://localhost:" + port + "/logout", HttpMethod.GET, logoutRequest, Void.class);
        // cleanup manually
        if (ticketId > 0) {
            ticketService.deleteById(ticketId);
        }
        facebookUserService.deleteById("test-989897796767");
        employeeService.deleteByUsername("testacc");
        ticketRepository.deleteAll();
        txManager.commit(txStatus);
    }

    @Test
    @Order(1)
    void testCreateTicket_success() throws Exception {
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(ticketDetailDTO), globalHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ticketId = objectMapper.readTree(response.getBody()).path("data").path("id").asInt();
        assertTrue(ticketId > 0);
    }

    @Test
    @Order(2)
    void testGetTicketById_success() throws Exception {
        HttpEntity<String> request = new HttpEntity<>(null, globalHeaders);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl() + "/" + ticketId, HttpMethod.GET, request ,String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode json = objectMapper.readTree(response.getBody());
        assertEquals(ticketId, json.path("data").path("id").asInt());
    }

    @Test
    @Order(4)
    void testUpdateTicketTitle_success() throws Exception {
        ticketDetailDTO.setTitle("Updated Title");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(ticketDetailDTO), headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl() + "/" + ticketId, HttpMethod.PUT, request, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode json = objectMapper.readTree(response.getBody());
        assertEquals("Updated Title", json.path("data").path("title").asText());
    }

    @Test
    @Order(3)
    void testSearchByFacebookId() throws Exception {
        // Chuẩn bị criteria
        TicketSearchCriteria criteria = new TicketSearchCriteria();
        criteria.setFacebookUserId("test-989897796767");
        ResponseEntity<String> response = postSearch(criteria);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode json = objectMapper.readTree(response.getBody());
        JsonNode content = json.path("data").path("content");

        assertTrue(content.isArray(), "Content should be an array");
    }

    @Test
    @Order(10)
    void testSearchByAssignee() throws Exception {
        TicketSearchCriteria criteria = new TicketSearchCriteria();
        criteria.setAssignee("testacc");
        ResponseEntity<String> response = postSearch(criteria);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode json = objectMapper.readTree(response.getBody());
        JsonNode content = json.path("data").path("content");

        assertTrue(content.isArray(), "Content should be an array");

        System.out.println("== Tickets found (assignee = testacc) ==");
        for (JsonNode ticket : content) {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ticket));
        }
    }

    @Test
    @Order(11)
    void testSearchByCategory() throws Exception {
        TicketSearchCriteria criteria = new TicketSearchCriteria();
        criteria.setCategory("category_code");
        ResponseEntity<String> response = postSearch(criteria);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(12)
    void testSearchByProgressStatus() throws Exception {
        TicketSearchCriteria criteria = new TicketSearchCriteria();
        criteria.setProgressStatus("pending");
        ResponseEntity<String> response = postSearch(criteria);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(13)
    void testSearchByNoteInTitle() throws Exception {
        TicketSearchCriteria criteria = new TicketSearchCriteria();
        criteria.setTitle("note keyword");
        ResponseEntity<String> response = postSearch(criteria);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(14)
    void testSearchByDate_Today() throws Exception {
        TicketSearchCriteria criteria = new TicketSearchCriteria();
        criteria.setFromDate(LocalDateTime.of(2025, 5, 6, 0, 0));
        criteria.setToDate(LocalDateTime.of(2025, 5, 6, 23, 59));
        ResponseEntity<String> response = postSearch(criteria);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(15)
    void testSearchByDate_Yesterday() throws Exception {
        TicketSearchCriteria criteria = new TicketSearchCriteria();
        criteria.setFromDate(LocalDateTime.of(2025, 5, 5, 0, 0));
        criteria.setToDate(LocalDateTime.of(2025, 5, 5, 23, 59));
        ResponseEntity<String> response = postSearch(criteria);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(16)
    void testSearchByDate_Last7Days() throws Exception {
        TicketSearchCriteria criteria = new TicketSearchCriteria();
        criteria.setFromDate(LocalDateTime.of(2025, 4, 30, 0, 0));
        criteria.setToDate(LocalDateTime.of(2025, 5, 6, 23, 59));
        ResponseEntity<String> response = postSearch(criteria);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(17)
    void testSearchByDate_Last30Days() throws Exception {
        TicketSearchCriteria criteria = new TicketSearchCriteria();
        criteria.setFromDate(LocalDateTime.of(2025, 4, 7, 0, 0));
        criteria.setToDate(LocalDateTime.of(2025, 5, 6, 23, 59));
        ResponseEntity<String> response = postSearch(criteria);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(18)
    void testSearchByDate_LastWeek() throws Exception {
        TicketSearchCriteria criteria = new TicketSearchCriteria();
        criteria.setFromDate(LocalDateTime.of(2025, 4, 28, 0, 0));
        criteria.setToDate(LocalDateTime.of(2025, 5, 4, 23, 59));
        ResponseEntity<String> response = postSearch(criteria);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    private void seedFacebookUser() {
        FacebookUserDetailDTO fb = new FacebookUserDetailDTO();
        fb.setFacebookId("test-989897796767");
        fb.setFacebookFirstName("Test");
        fb.setFacebookLastName("User");

        if (facebookUserService.existsById(fb.getFacebookId()).getHttpCode() != 200) {


            facebookUserService.save(fb);
        }
        APIResultSet<Void> result = facebookUserService.existsById("test-989897796767");
        log.info("Test 1 result: {}", result.getMessage());
    }

    private void seedEmployee() {
        //seed dữ lieuej thaajt truocws khi tesst
        Employee emp = new Employee();
        emp.setUsername("testacc");
        emp.setPassword("Abcd@1234");
        emp.setName("Test User");
        emp.setGroupId(1);
        if (employeeService.existsByUsername(emp.getUsername()).getHttpCode() != 200) {
            APIResultSet<Employee> result = employeeService.createUser(emp);
        }
    }

    public void login() {
        String loginUrl = "http://localhost:" + port + "/process-login";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", "testacc");
        form.add("password", "Abcd@1234");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);


        // Lấy header Set-Cookie
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        if (cookies != null) {
            globalHeaders.put(HttpHeaders.COOKIE, cookies);
        }

        assertEquals(HttpStatus.FOUND, response.getStatusCode()); // redirect 302
    }

    private ResponseEntity<String> postSearch(TicketSearchCriteria criteria) throws Exception {
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(criteria), globalHeaders);
        return restTemplate.postForEntity(baseUrl() + "/search", request, String.class);
    }

}