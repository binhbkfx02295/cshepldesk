let currentSort = { field: null, direction: 'asc' };
let ONLINE_STATUS = {
  class: {
    1: "online",
    2: "away",
    3: "offline"
  },
  text: {
    1: "Online",
    2: "Away",
    3: "Offline"
  },
}
$(document).ready(() => {
  // Simulate loading time
  setTimeout(() => {
    // Hide loading overlay
    $("#loadingOverlay").fadeOut()

    // Initialize dashboard
    initDashboard()
  }, 500)


  // Refresh dashboard
  $("#refreshDashboard").click(() => {
    refreshDashboard()
  })

  // Search tickets
  $("#ticketSearch").on("keyup", function () {
    const searchTerm = $(this).val().toLowerCase()
    filterTickets(searchTerm)
  })



  // Đổi trạng thái Online/Away
  $('.status-dropdown .dropdown-item').click(function (e) {
    e.preventDefault();
    var status = $(this).text().trim();
    var $indicator = $('.status-dropdown .status-indicator');
    var $statusText = $('.status-dropdown #currentStatusText');

    if (status === 'Online') {
      $indicator.removeClass('away').addClass('online');
    } else if (status === 'Away') {
      $indicator.removeClass('online').addClass('away');
    }

    $statusText.text(status);
  });

  // Đổi ngôn ngữ VI/EN
  $('.language-dropdown .dropdown-item').click(function (e) {
    e.preventDefault();
    var lang = $(this).text().trim();
    $('#currentLanguage').text(lang);
  });

})

// Initialize dashboard
function initDashboard() {


  // Set current date
  const now = new Date()
  $("#currentDate").text(formatDate(now))
  $("#lastUpdated").text(formatTime(now))

  // Load employee list
  loadEmployees()

  // Load ticket metrics
  loadTicketMetrics()

  // Load ticket list
  loadTickets()

  $('#btnEmployees').click(function () {
    $('#employeeSection').removeClass('d-none');
    $('#ticketSection').addClass('d-none');
    $(this).addClass('active');
    $('#btnTickets').removeClass('active');
  });

  $('#btnTickets').click(function () {
    $('#ticketSection').removeClass('d-none');
    $('#employeeSection').addClass('d-none');
    $(this).addClass('active');
    $('#btnEmployees').removeClass('active');
  });

}

// Refresh dashboard
function refreshDashboard() {
  // Show loading animation on refresh button
  const refreshBtn = $("#refreshDashboard")
  const originalContent = refreshBtn.html()
  refreshBtn.html('<i class="bi bi-arrow-repeat"></i> <span>Đang tải...</span>')
  refreshBtn.prop("disabled", true)



  // Simulate refresh delay
  setTimeout(() => {
    // Update last updated time
    const now = new Date()
    $("#lastUpdated").text(formatTime(now))

    // Reload data
    loadEmployees()
    loadTicketMetrics()
    loadTickets()



    // Restore refresh button
    refreshBtn.html(originalContent)
    refreshBtn.prop("disabled", false)
  }, 1000)



}

//Load employee2 list
function loadEmployees(sortField = null) {
  const employeeList = $("#employeeList2");
  employeeList.empty();

  // Update employee count
  $(".employee-count").text(employees.length);

  // Sort nếu cần
  if (sortField) {
    if (currentSort.field === sortField) {
      currentSort.direction = currentSort.direction === 'asc' ? 'desc' : 'asc';
    } else {
      currentSort.field = sortField;
      currentSort.direction = 'asc';
    }

    employees.sort((a, b) => {
      let valA = a[sortField];
      let valB = b[sortField];

      if (typeof valA === "string") valA = valA.toLowerCase();
      if (typeof valB === "string") valB = valB.toLowerCase();

      if (valA < valB) return currentSort.direction === 'asc' ? -1 : 1;
      if (valA > valB) return currentSort.direction === 'asc' ? 1 : -1;
      return 0;
    });
  }

  // Render
  employees.forEach((employee) => {
    const statusClass = ONLINE_STATUS.class[employee.status];
    const statusText = ONLINE_STATUS.text[employee.status];

    const employeeRow = `
      <tr class="show">
        <td>${employee.name}</td>
        <td>${employee.userGroup}</td>
        <td>${employee.ticketCount}</td>
        <td>
          <span class="status-indicator ${statusClass}"></span>
          ${statusText}
        </td>
        <td>${employee.statusTime}</td>
      </tr>
    `;

    employeeList.append(employeeRow);
  });
}


// Load ticket metrics
function loadTicketMetrics() {
  // Count tickets by status
  const totalTickets = tickets.length
  const pendingTickets = tickets.filter((ticket) => ticket.processingStatus === "pending").length
  const onHoldTickets = tickets.filter((ticket) => ticket.processingStatus === "on-hold").length
  const resolvedTickets = tickets.filter((ticket) => ticket.processingStatus === "resolved").length
  const closedTickets = tickets.filter((ticket) => ticket.status === "closed").length

  // Update metrics
  $("#totalTickets").text(totalTickets)
  $("#inProgressTickets").text(pendingTickets)
  $("#onHoldTickets").text(onHoldTickets)
  $("#resolvedTickets").text(resolvedTickets)
  $("#closedTickets").text(closedTickets)
}

// Load ticket list
function loadTickets() {
  const ticketList = $("#ticketList")
  ticketList.empty()

  tickets.forEach(ticket => {
    const card = `
        <div class="item col-md-6 col-lg-12 mb-3" data-ticket-id="${ticket.id}">
          <div class="d-flex flex-row">
            <div class="d-flex flex-column me-2">
              <div class="title"><span class="ticket-id me-2">#${ticket.id}</span> - ${ticket.title}</div>
              <div class="user"><span class="avatar me-2 text-center"><img src="${ticket.facebookUser.avtSrc}"></span>${ticket.facebookUser.firstName + " " + ticket.facebookUser.lastName} - <span class="badge progress-status ${PROGRESS_STATUS_CLASS[ticket.progressStatus]}">${PROGRESS_STATUS[ticket.progressStatus]}</span></div>
              <div class="created-at ">${formatEpochTimestamp(ticket.createdAt)}</div>
            </div>
          </div>
        </div>
    `
    ticketList.append(card);
  })

  // Add click event to ticket rows
  $("#ticketList .item").click(function () {
    const ticketId = $(this).data("ticket-id");
    console.log(ticketId);
    showTicketDetail(ticketId)
  })
}

// Filter tickets
function filterTickets(searchTerm) {
  $("#ticketList tr").each(function () {
    const rowText = $(this).text().toLowerCase()
    if (rowText.indexOf(searchTerm) > -1) {
      $(this).show()
    } else {
      $(this).hide()
    }
  })
}

// Show ticket detail
function showTicketDetail(ticketId) {
  const ticket = tickets.find(t => t.id === ticketId);
  if (!ticket) return;

  // Lưu dữ liệu gốc để revert
  originalTicketData = JSON.parse(JSON.stringify(ticket));
  currentEditingTicketId = ticketId;

  // Fill dữ liệu form
  $("#editTicketId").val(ticket.id);
  $("#editTitle").val(ticket.title);
  $("#editFacebookUser").val(`${ticket.facebookUser.firstName} ${ticket.facebookUser.lastName}`);
  $("#editAssignee").val(ticket.employee?.name || "Chưa có");
  $("#editCreatedAt").val(formatEpochTimestamp(ticket.createdAt));
  $("#editCategory").val(ticket.category);

  // Load Tags (nhiều tag)
  if (ticket.tags && Array.isArray(ticket.tags)) {
    $("#editTags").val(ticket.tags);
  } else {
    $("#editTags").val([]);
  }

  // Emotion và Satisfaction readonly
  $("#editEmotion").val(EMOTION[ticket.emotion] || "Không xác định");
  $("#editSatisfaction").val(SATISFACTION[ticket.satisfaction] || "Không xác định");

  $("#editNote").val(ticket.description || ""); // Dùng field description làm Note

  // Reset buttons
  disableEditButtons();

  // Load message
  loadTicketMessages(ticketId);

  // Load ticket history
  loadTicketHistory(ticket.facebookUser.id);

  // Show modal
  const fullModal = new bootstrap.Modal(document.getElementById("ticketFullDetailModal"));
  fullModal.show();

}


// Show ticket detail
function showTicketDetail2(ticketId) {
  const ticket = tickets.find((t) => t.id === ticketId)
  console.log(ticket);

  if (ticket) {
    // Populate modal with ticket details
    $("#detailId").text(ticket.id)
    $("#detailTitle").text(ticket.title)
    $("#detailDescription").text(ticket.description)
    $("#detailCreatedAt").text(formatEpochTimestamp(ticket.createdAt))
    $("#detailUpdatedAt").text(formatEpochTimestamp(ticket.updatedAt))

    const statusText = ticket.status === "open" ? "Mở" : "Đóng"
    $("#detailStatus").text(statusText)

    let processingStatusText = ""
    switch (ticket.processingStatus) {
      case "pending":
        processingStatusText = "Đang xử lý"
        break
      case "on-hold":
        processingStatusText = "Đang chờ"
        break
      case "resolved":
        processingStatusText = "Đã xử lý"
        break
    }
    $("#detailProcessingStatus").text(processingStatusText)

    $("#detailAssignee").text(ticket.assignee)
    $("#detailCategory").text(ticket.category)
    $("#detailEmotion").text(ticket.emotion || "Không có")
    $("#detailSatisfaction").text(ticket.satisfaction || "Chưa đánh giá")

    // Show modal
    const ticketModal = new bootstrap.Modal(document.getElementById("ticketDetailModal"))
    ticketModal.show()
  }
}

// Format date
function formatDate(date) {
  const day = date.getDate().toString().padStart(2, "0")
  const month = (date.getMonth() + 1).toString().padStart(2, "0")
  const year = date.getFullYear()

  return `${day}/${month}/${year}`
}

// Format time
function formatTime(date) {
  const hours = date.getHours().toString().padStart(2, "0")
  const minutes = date.getMinutes().toString().padStart(2, "0")

  return `${hours}:${minutes}`
}

// Format date and time
function formatDateTime(dateString) {
  const date = new Date(dateString)

  return `${formatDate(date)} ${formatTime(date)}`
}

// Khi form có thay đổi, enable nút
$("#ticketEditForm").on("input change", "input, select, textarea", function () {
  enableEditButtons();
});

// Cancel edit, revert data
$("#cancelEdit").click(function () {
  if (!originalTicketData) return;

  $("#editTitle").val(originalTicketData.title);
  $("#editCategory").val(originalTicketData.category);
  $("#editStatus").val(originalTicketData.status);
  $("#editProcessingStatus").val(originalTicketData.progressStatus);
  $("#editAssignee").val(originalTicketData.employee?.name || "");
  $("#editDescription").val(originalTicketData.description || "");

  disableEditButtons();
});

// Save edit
$("#saveEdit").click(function () {
  const updatedData = {
    id: currentEditingTicketId,
    title: $("#editTitle").val(),
    category: parseInt($("#editCategory").val()),
    status: $("#editStatus").val(),
    progressStatus: parseInt($("#editProcessingStatus").val()),
    assignee: $("#editAssignee").val(),
    description: $("#editDescription").val()
  };

  console.log("Updated Ticket Data:", updatedData);

  // TODO: Gửi dữ liệu cập nhật về server hoặc update local mock

  disableEditButtons();
});

// Helper function
function enableEditButtons() {
  $("#saveEdit").prop("disabled", false);
  $("#cancelEdit").prop("disabled", false);
}

function disableEditButtons() {
  $("#saveEdit").prop("disabled", true);
  $("#cancelEdit").prop("disabled", true);
}

// Load messages kiểu chat
function loadTicketMessages(ticketId) {
  $("#messageList").empty();

  const ticketMessages = messages.filter(m => m.ticketId === ticketId);
  ticketMessages.sort((a, b) => a.timestamp - b.timestamp);

  ticketMessages.forEach(msg => {
    const senderClass = msg.isSenderStaff ? "staff" : "user";
    const formattedTime = formatEpochTimestamp(msg.timestamp);

    const bubble = `
      <div class="d-flex mb-2 ${senderClass === "user" ? "justify-content-start" : "justify-content-end"}">
        <div class="chat-bubble ${senderClass}">
          <div class="message-text">${msg.text}</div>
          <div class="message-timestamp text-muted small mt-1" title="Timestamp: ${msg.timestamp}">${formattedTime}</div>
        </div>
      </div>
    `;
    $("#messageList").append(bubble);
  });

  scrollToBottomMessageList();
}

// Auto scroll to bottom
function scrollToBottomMessageList() {
  const messageList = document.getElementById('messageList');
  if (messageList) {
    messageList.scrollTop = messageList.scrollHeight;
  }
}



// Load ticket history
function loadTicketHistory(facebookUserId) {
  $("#historyList").empty();
  const history = tickets.filter(t => t.facebookUser.id === facebookUserId);

  history.forEach(hist => {
    const item = `
      <li class="list-group-item d-flex flex-column">
        <strong>#${hist.id}</strong>
        <small>${hist.title}</small>
        <small>${formatEpochTimestamp(hist.createdAt)}</small>
      </li>
    `;
    $("#historyList").append(item);
  });
}

function formatEpochTimestamp(epochMillis) {
  if (!epochMillis || isNaN(epochMillis)) {
    return "--:--:-- - --/--/----";
  }

  const date = new Date(epochMillis);

  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  const seconds = date.getSeconds().toString().padStart(2, "0");

  const day = date.getDate().toString().padStart(2, "0");
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const year = date.getFullYear();

  return `${hours}:${minutes}:${seconds} - ${day}/${month}/${year}`;
}

function toTime(epochMillis) {
  if (!epochMillis || isNaN(epochMillis)) {
    return "--:--:--";
  }

  const date = new Date(epochMillis);

  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  const seconds = date.getSeconds().toString().padStart(2, "0");

  return `${hours}:${minutes}:${seconds}`;
}