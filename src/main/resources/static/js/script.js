const currentSort = { field: null, direction: 'asc' };

const BASE = window.location.href.includes(`cshelpdesk.online`) ? "" : `http://localhost:8080`;
const API_EMPLOYEE = `${BASE}/api/employee-management`;
const API_PERMISSION = `${BASE}/api/employee-management/permission`;
const API_USERGROUP = `${BASE}/api/employee-management/user-group`;
const API_TICKET = `${BASE}/api/ticket`;
const API_CATEGORY = `${BASE}/api/category`;
const API_TAG = `${BASE}/api/tag`;
const API_ONLINE_STATUS = `${BASE}/api/employee-management/online-status`;
const API_PROGRESS_STATUS = `${BASE}/api/progress-status`;
const API_EMOTION = `${BASE}/api/emotion`;
const API_SATISFACTION = `${BASE}/api/satisfaction`;
const API_MESSAGE = `${BASE}/api/message`;
const API_FACEBOOK_USER = `${BASE}/api/facebookuser`
const USERS = []
const CATEGORIES = []
const PROGRESS_STATUS = [];
const EMOTIONS = [];
const SATISFACTIONS = [];
const GLOBAL_API_HEADERS = {
  "Content-Type": "application/json",
  "Accept": "application/json",
}
const HTTP_GET_METHOD = "GET";
const HTTP_POST_METHOD = "POST";
const HTTP_PUT_METHOD = "PUT";
const HTTP_DELETE_METHOD = "DELETE";
var refreshHandler = null;
var submitHandler = null;
var keyupHandler = null;
var debounceKeyup = null;
var deleteCustomerHandler = null;
$(document).ready(() => {
  // Simulate loading time
  //  setTimeout(() => {
  //    // Hide loading overlay
  //    $("#loadingOverlay").fadeOut()
  //    initHeader()
  //    initDashboard()
  //    initTicket()
  //    initTicketDetailModal();
  //
  //  }, 500)
  $("#loadingOverlay").fadeOut()
  initHeader();

  if (window.location.href.includes("dashboard") || window.location.href.includes("index")
  ) {
    initDashboard();
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(0).classList.add("active");
  } else if (window.location.href.includes("ticket")) {

    initTicket()
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(1).classList.add("active");
  } else if (window.location.href.includes("customer")) {
    initCustomer();
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(2).classList.add("active");

  } else if (window.location.href.includes("performance")) {
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(3).classList.add("active");
  } else if (window.location.href.includes("report")) {
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(4).classList.add("active");
  } else if (window.location.href.includes("setting")) {
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(5).classList.add("active");
  }
})

//customer/html
function initCustomer() {
  console.log("init customer page");
  initCustomerDeleteModal();
  initCustomerExport();
  initCustomerEditModal();
  bindItemClickEvents(); //TODO: delete after testing



  $(".select-all input[type=checkbox]").click(function (e) {
    $("#customer-list-body .item input[type=checkbox]").prop("checked", $(this).prop("checked"));
  });

  $(".search-common i").click(function (e) {
    performSearchCustomer(page = 0, size = $("#pageSize").val());
  });

  function initCustomerDeleteModal() {
    let button = document.getElementById("delete-checkbox");
    window.customerDeleteModal = new bootstrap.Modal(document.getElementById("customerDeleteModal"));
    //bind click open modal
    button.addEventListener("click", function () {
      window.customerDeleteModal.show();
    })
  }

  function initCustomerEditModal() {
    window.customerEditModal = new bootstrap.Modal(document.getElementById("customerEditModal"));
    const i = document.querySelectorAll("#customerEditModal .field-group i");
    i.forEach((item) => {
      item.addEventListener("click", function () {
        //get sibling ad set to 0;
        console.log(item.previousElementSibling);
        item.previousElementSibling.value = "";
      })
    })
  }

  function initCustomerExport() {
    $("#customer-export-excel").click(function (e) {
      console.log("click");
      const data = getCustomerSearchData();
      const url = `${API_FACEBOOK_USER}/export-excel?${buildQueryParam(data)}`;
      console.log(url);
      xhr = createXHR();
       xhr.responseType = "blob";
      handleResponse(xhr, function (blob) {
        successToast("Tải xuống thành công");
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = "Khách hàng.xlsx";
        link.click();
      })
      xhr.open(HTTP_GET_METHOD, url)
      xhr.send();
    });
  }


  function sendExportData() {
    data = getCustomerSearchData();
    xhr = createXHR();


  }

  function performSearchCustomer(page = 0, size = 10) {
    data = getCustomerSearchData();
    data.page = page;
    data.size = size;
    console.log(data)
    const container = document.getElementById("customer-list-body");
    const loading = $($("#loading-result"));
    const url = `${API_FACEBOOK_USER}/search?${buildQueryParam(data)}`;
    console.log(url)
    loading.show();

    xhr = createXHR();
    handleResponse(xhr, function (response) {
      response = JSON.parse(response);
      loading.hide();
      successToast(response.message);
      container.innerHTML = "";
      let data = response.data
      if (data.content != 0) {
        renderCustomerDetail(data.content, container)
        bindItemClickEvents()
        renderPagination(
          data.page,
          data.totalElements,
          data.size,
          performSearchCustomer
        )

      } else {
        let item = document.createElement("div");
        item.innerHTML = `
          <div id="no-ticket-result" class="text-center text-muted py-3" style="display: block;">
        <i class="bi bi-inbox me-1"></i> Không có kết quả phù hợp.
      </div>
          `;
        console.log(item);
        container.innerHTML = item.getHTML();
      }
    })
    xhr.open(HTTP_GET_METHOD, url)
    xhr.send();
  }

  function getCustomerSearchData() {
    const fieldName = $("#search-field").val();
    const keyword = $("#search-keyword").val() || null;
    return {
      facebookId: fieldName.includes("facebookId") ? keyword : null,
      facebookName: fieldName.includes("facebookName") ? keyword : null,
      realName: fieldName.includes("realName") ? keyword : null,
      phone: fieldName.includes("phone") ? keyword : null,
      email: fieldName.includes("email") ? keyword : null,
      zalo: fieldName.includes("zalo") ? keyword : null,
    }
  }

  function renderCustomerDetail(data, container) {
    const fragment = document.createDocumentFragment();
    data.forEach((customer) => {
      let item = document.createElement("div");
      item.className = "row item border-bottom align-items-center";
      item.setAttribute("data-id", customer.facebookId);
      item.innerHTML = `
      <div class="col selected d-flex justify-content-center align-items-center">
          <input type="checkbox">
      </div>
      <div class="col facebookId">${customer.facebookId || "- -"}</div>
      <div class="col facebookProfile">
        <img class="avt" src="${customer.facebookProfilePic || "- -"}">
        ${customer.facebookName || "- -"}</div>
      <div class="col">${sanitizeText(customer.facebookName) || "- -"}</div>
      <div class="col">${sanitizeText(customer.phone) || "- -"}</div>
      <div class="col">${sanitizeText(customer.email) || "- -"}</div>
      <div class="col">${sanitizeText(customer.zalo) || "- -"}</div>
      <div class="col options overflow-visible">
          <div class="customer-dropdown">
              <i class="bi bi-three-dots-vertical"></i>
              <ul class="dropdown-menu">
                  <li><a class="customer-view-detail dropdown-item" href="#"><i class="bi bi-eye me-2"></i></i>Xem chi tiết</a></li>
                  <li><a class="customer-edit dropdown-item" href="#"><i class="bi bi-pencil-square me-2"></i>Chỉnh sửa</a></li>
                  <li><a class="customer-delete dropdown-item" href="#"><i class="bi bi-trash3 me-2"></i>Xóa</a></li>
              </ul>
          </div>
      </div>
      `
      fragment.appendChild(item);
    })
    container.appendChild(fragment)
  }

  function renderCustomerItem(item, container) {
    console.log("rendering..");
    //TODO
    const $item = $(`
    <div class="row item border-bottom align-items-center" data-id="${item.facebookId || "- -"}">
        <div class="col selected d-flex justify-content-center align-items-center">
            <input type="checkbox">
        </div>
        <div class="col facebookId">${item.facebookId || "- -"}</div>
        <div class="col facebookProfile">
          <img class="avt" src="${item.facebookProfilePic || "- -"}">
          ${item.facebookName || "- -"}</div>
        <div class="col">${sanitizeText(item.facebookName) || "- -"}</div>
        <div class="col">${sanitizeText(item.phone) || "- -"}</div>
        <div class="col">${sanitizeText(item.email) || "- -"}</div>
        <div class="col">${sanitizeText(item.zalo) || "- -"}</div>
        <div class="col options overflow-visible">
            <div class="customer-dropdown">
                <i class="bi bi-three-dots-vertical"></i>
                <ul class="dropdown-menu">
                    <li><a class="customer-view-detail dropdown-item" href="#"><i class="bi bi-eye me-2"></i></i>Xem chi tiết</a></li>
                    <li><a class="customer-edit dropdown-item" href="#"><i class="bi bi-pencil-square me-2"></i>Chỉnh sửa</a></li>
                    <li><a class="customer-delete dropdown-item" href="#"><i class="bi bi-trash3 me-2"></i>Xóa</a></li>
                </ul>
            </div>
        </div>
    </div>
      `);
    container.append($item);
  }

  function bindItemClickEvents() {
    const facebookProfileCol = document.querySelectorAll("#customer-list-body .facebookProfile");
    console.log("binding..");

    facebookProfileCol.forEach((item) => {
      item.addEventListener("click", function () {
        const id = $(this).closest(".item").data("id");
        open(openCustomerViewDetailModal(id))
      });
    })
    $("#customer-list-body .item .dropdown-menu a").on("click", function (e) {
      e.preventDefault();
      const action = $(this).attr("class");
      const id = $(this).closest(".item").data("id");
      if (action.includes("customer-view-detail")) openCustomerViewDetailModal(id);
      else if (action.includes("customer-edit")) openCustomerEditModal(id);
      else if (action.includes("customer-delete")) openCustomerDeleteModal(id);
      $(this).closest(".dropdown-menu").hide();
    });

    // TODO: Toggle dropdown (nên dùng event delegation hoặc Bootstrap dropdown JS)
    $("#customer-list-body .item .options i").on("click", function () {
      console.log("click");
      $(this).siblings(".dropdown-menu").toggle();
    });

    const checkboxes = $("input[type=checkbox");
    const searchCommon = $(".search-common");
    const deleteButton = $(".delete-checkbox");
    const btnGroup = $(".customer-search-btn-group");
    checkboxes.on("change", function () {
      const anyChecked = checkboxes.is(":checked");
      if (anyChecked) {
        $(".page-list-body .item input:checked").closest(".item").addClass("selected");
        searchCommon.css("width", "0px"); // Show as flex
        btnGroup.hide(); // Show as flex
        deleteButton.show();
      } else {
        $(".page-list-body .item.selected").removeClass("selected");
        searchCommon.css("width", "auto"); // Hides (display: none)
        btnGroup.show(); // Hides (display: none)
        deleteButton.hide();
      }
    });
  }
  function openCustomerViewDetailModal(id) {
    //TODO
    console.log("pending..");
  }

  function openCustomerEditModal(id) {
    //TODO:
    window.customerEditModal.show();
    const modal = document.getElementById("customerEditModal");
    const submitBtn = document.getElementById("submit-edit");
    modal.setAttribute("data-facebookId", id);
    submitBtn.disabled = true;
    fetchCustomerDetail(id);
  }

  function fetchCustomerDetail(id) {
    let xhr = createXHR();
    console.log(id);
    handleResponse(xhr, function (response) {
      response = JSON.parse(response);
      populateCustomerEditModal(response);
    });
    xhr.open(HTTP_GET_METHOD, `${API_FACEBOOK_USER}?id=${id}`);
    xhr.send();
  }

  function populateCustomerEditModal(response) {
    let customer = response.data;
    if (window.customerEditModal == null) {
      return;
    }
    console.log(customer);
    window.customer = customer;
    const submitBtn = document.getElementById("submit-edit");
    const realName = document.getElementById("realName");
    const phone = document.getElementById("phone");
    const email = document.getElementById("email")
    const zalo = document.getElementById("zalo");
    let img = document.querySelector("#facebook-profile img");
    document.querySelector("#facebook-profile .facebookName").textContent = customer.facebookName;
    document.querySelector("#facebook-profile .facebookId").textContent = customer.facebookId;
    document.querySelector("#facebook-profile .createdAt").textContent = formatEpochTimestamp(customer.createdAt) || "- -";
    realName.value = customer.realName || "";
    phone.value = customer.phone || "";
    email.value = customer.email || "";
    zalo.value = customer.zalo || "";
    //add original data
    realName.setAttribute("data-original", customer.realName);
    phone.setAttribute("data-original", customer.phone);
    email.setAttribute("data-original", customer.email);
    zalo.setAttribute("data-original", customer.zalo);

    img.setAttribute("src", customer.facebookProfilePic);

    //bind refresh button
    const refreshBtn = document.getElementById("refresh-edit");
    refreshBtn.removeEventListener("click", refreshHandler)
    refreshHandler = function () {
      document.getElementById("realName").value = customer.facebookId || "";
      document.getElementById("phone").value = customer.phone || "";
      document.getElementById("email").value = customer.email || "";
      document.getElementById("zalo").value = customer.zalo || "";
    }
    refreshBtn.addEventListener("click", refreshHandler)

    //bind input button
    const inputList = document.querySelectorAll("#customerEditModal input");
    inputList.forEach(function (input) {
      input.removeEventListener("keyup", keyupHandler);
      keyupHandler = function () {

        //TODO: check if input changed
        clearTimeout(debounceKeyup);
        debounceKeyup = setTimeout(function () {
          console.log(input.value);
          originalValue = input.getAttribute("data-original").trim();
          if (input.value != "" && input.value != originalValue) {
            submitBtn.disabled = false;
          } else {
            submitBtn.disabled = true;
          }

        }, 500)
      }
      input.addEventListener("keyup", keyupHandler);
    })

    //bind submit button
    submitBtn.removeEventListener("click", sendEditCustomer)
    submitBtn.addEventListener("click", sendEditCustomer);
  }

  function sendEditCustomer() {
    let data = {
      facebookId: document.getElementById("customerEditModal").getAttribute("data-facebookId") || null,
      facebookName: document.getElementById("facebookName").value || null,
      realName: document.getElementById("realName").value || null,
      phone: document.getElementById("phone").value || null,
      email: document.getElementById("email").value || null,
      zalo: document.getElementById("zalo").value || null
    }
    console.log(data);
    let xhr = createXHR();
    handleResponse(xhr, function (response) {
      response = JSON.parse(response);
      let data = response.data;
      window.customerEditModal.hide();
      successToast(response.message);
      setTimeout(function () {
        openCustomerEditModal(data.facebookId);
      }, 300)
    })
    xhr.open(HTTP_PUT_METHOD, `${API_FACEBOOK_USER}`)
    xhr.setRequestHeader(
      "Content-type", "application/json"
    )
    xhr.send(JSON.stringify(data));
  }

  function openCustomerDeleteModal(id = null) {
    window.customerDeleteModal.show();
    const confirm = document.getElementById("confirmDeleteCustomerBtn");
    confirm.removeEventListener("click", deleteCustomerHandler);
    deleteCustomerHandler = function () {
      xhr = createXHR();
      handleResponse(xhr, function (response) {
        response = JSON.parse(response);
        successToast(response.message);
        window.customerDeleteModal.hide();
      })
      xhr.open(HTTP_DELETE_METHOD, `${API_FACEBOOK_USER}?id=${id}`)
      xhr.send();
    }
    confirm.addEventListener("click", deleteCustomerHandler)

  }

  function buildQueryParam(params) {
    const parts = [];
    for (let k in params) {
      if (params[k] != null && params[k] !== "") {
        parts.push(`${encodeURIComponent(k)}=${encodeURIComponent(params[k])}`);
      }
    }
    return parts.length > 0 ? `${parts.join("&")}` : "";
  }
}

//get json helper
function getAPI({ url, container, renderItem,
  noResultNode, loadingNode,
  onSuccess, onError }) {
  loadingNode?.show();
  console.log("calling api...", url);
  $.getJSON(url)
    .done(res => {
      console.log(res);
      const data = res.data.content;
      console.log(container);
      console.log(renderItem)
      if (container && renderItem) {
        console.log("ok...");
        container.empty();
        console.log(Array.isArray(data))
        if (Array.isArray(data) && data.length > 0) {
          data.forEach(item => renderItem(item, container));
          noResultNode?.hide();
        } else {
          noResultNode?.show();
        }
      }

      onSuccess?.(res);
    })
    .fail(err => onError?.(err))
    .always(() => loadingNode?.hide());
}

//ticket.html
function initTicket() {
  console.log("init ticket search ..");
  initTicketSearch();
  initTicketSortingByIndex();
  initTicketCreate();
  initTicketDetailModal();
}


function initHeader() {
  //init current date
  const now = new Date()
  $("#currentDate").text(formatDate(now))
  $("#lastUpdated").text(formatTime(now))
  loadDashboardEmployees()

  $('#btnEmployees').click(function () {
    $('#employeeSection').removeClass('d-none');
    $('#ticketSection').addClass('d-none');
    $(this).addClass('active');
    $('#btnTickets').removeClass('active');
    loadDashboardEmployees()
  });

  $('#btnTickets').click(function () {
    $('#ticketSection').removeClass('d-none');
    $('#employeeSection').addClass('d-none');
    $(this).addClass('active');
    $('#btnEmployees').removeClass('active');
    loadDashboardTickets()
  });

  //get API to get online status
  const statusText = $(this).text().trim();
  const statusValue = statusText.toLowerCase();
  var $indicator = $('.status-dropdown .status-indicator');
  var $statusText = $('.status-dropdown #currentStatusText');

  $.ajax({
    url: `${API_EMPLOYEE}/online-status`,
    method: 'GET',
    contentType: 'application/json',
    success: function (res) {
      // Cập nhật UI nếu thành công
      console.log(res);
      const statusValue = res.status;
      const statusText = toCapital(statusValue);
      $indicator.addClass(statusValue);

      $statusText.text(statusText);
    },
    error: function (res) {
      console.log(res.message);
      console.log();
    }
  })

  const userProfileModal = new bootstrap.Modal(document.getElementById("userProfileModal"));
  const settingModal = new bootstrap.Modal(document.getElementById("settingModal"));
  const confirmResetModal = new bootstrap.Modal(document.getElementById("confirmResetModal"));
  $("#user-profile-button").on("click", function (e) {
    e.preventDefault();
    userProfileModal.show();
  });


  $("#setting-button").on("click", function (e) {
    e.preventDefault();
    settingModal.show();
  });

  // Bấm nút "Đặt lại mật khẩu" => mở modal xác nhận
  $("#resetPasswordBtn").on("click", function () {
    $("#confirmResetModal").find("input").each(function (e) {
      $(e).val("");
    });
    confirmResetModal.show();
  });



  $("#confirmResetModal input").on("keyup", function () {
    console.log("Hello");
    setTimeout(() => validateChangePassword(), 1000);
  })

  // Xác nhận reset
  $("#confirmResetBtn").on("click", function () {
    console.log("Đã xác nhận đặt lại mật khẩu");
    body = JSON.stringify({
      password: $("#password").val(),
      newPassword: $("#new-password").val(),
    })
    console.log(body);
    $.ajax({
      url: `${API_EMPLOYEE}/me/password`,
      method: "PUT",
      data: body,
      contentType: "application/json",
      success: function (res) {
        successToast(res.message);
        $(location).attr('href', '/logout');
      },
      error: function (res) {
        errorToast(res.responseJSON.message);
      }

    })

    // Tắt modal xác nhận
    $("#confirmResetModal").modal("hide");
  });

  // Đổi trạng thái Online/Away
  $('.status-dropdown .dropdown-item').click(function (e) {
    const statusText = $(this).text().trim();
    const statusValue = statusText.toLowerCase();
    var $indicator = $('.status-dropdown .status-indicator');
    var $statusText = $('.status-dropdown #currentStatusText');
    // Gửi request đến backend để cập nhật trạng thái
    $.ajax({
      url: `${API_EMPLOYEE}/me/online-status`,
      method: 'PUT',
      contentType: 'application/json',
      data: JSON.stringify({ status: statusValue }),
      success: function (res) {
        successToast(res.message);
        if (statusValue === 'online') {
          $indicator.removeClass('away').addClass('online');
        } else if (statusValue === 'away') {
          $indicator.removeClass('online').addClass('away');
        }

        $statusText.text(statusText);
      },
      error: function (res) {
        errorToast(res.responseJSON.message);
      }
    });
  });

  // Đổi ngôn ngữ VI/EN
  $('.language-dropdown .dropdown-item').click(function (e) {
    e.preventDefault();
    var lang = $(this).text().trim();
    $('#currentLanguage').text(lang);
  });
}

// Initialize dashboard
function initDashboard() {
  console.log("init dashboard");
  initTicketDetailModal();
  //TODO:
  $("#refreshDashboardTicket").click(() => {
    refreshDashboardTicket()
  })

  // Search tickets
  //TODO:
  $("#ticketSearch").on("keyup", function () {
    const searchTerm = $(this).val().toLowerCase()
    filterTickets(searchTerm)
  })
}

// Refresh dashboard
function refreshDashboardTicket() {
  // Show loading animation on refresh button
  const refreshBtn = $("#refreshDashboardTicket")
  const originalContent = refreshBtn.html()
  refreshBtn.html('<i class="bi bi-arrow-repeat"></i> <span>Đang tải...</span>')
  refreshBtn.prop("disabled", true)



  // Simulate refresh delay
  //  setTimeout(() => {
  //    // Update last updated time
  //    const now = new Date()
  //    $("#lastUpdated").text(formatTime(now))
  //
  //    // Reload data
  //    loadDashboardTickets()
  //
  //    // Restore refresh button
  //    refreshBtn.html(originalContent)
  //    refreshBtn.prop("disabled", false)
  //  }, 500)
  // Update last updated time
  const now = new Date()
  $("#lastUpdated").text(formatTime(now))

  // Reload data
  loadDashboardTickets()

  // Restore refresh button
  refreshBtn.html(originalContent)
  refreshBtn.prop("disabled", false)



}

//Load employee2 list
function loadDashboardEmployees(sortField = null) {
  const employeeList = $("#employeeList2");
  employeeList.empty();

  //fetch employees;
  $.ajax({
    url: `${API_EMPLOYEE}/dashboard`,
    type: 'GET',
    dataType: 'json',
    success: function (data) {
      console.log(data);
      employees = data.data;
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

        const employeeRow = `
          <tr class="show">
            <td>${employee.name}</td>
            <td>${employee.userGroup.name}</td>
            <td>${employee.ticketCount || 0}/6</td>
            <td style="text-transform: capitalize;">
              <span class="status-indicator ${employee.statusLog[0].status}"></span>
              ${employee.statusLog[0].status}
            </td>
            ${employee.statusLog[0].status == "offline" ? "" : `<td class="time-elapse" data-timestamp="${employee.statusLog[0].from}">${startElapsedTimer(employee.statusLog[0].from)}</td>`}

          </tr>
        `;

        employeeList.append(employeeRow);
      });

      if (window.startElapsedTimerInterval) {
        clearInterval(window.startElapsedTimerInterval);
        console.log("cleared window.startElapsedTimerInterval");
      }
      window.startElapsedTimerInterval = setInterval(function () {
        $(".time-elapse").each(function () {
          const timestamp = $(this).attr("data-timestamp");
          $(this).text(startElapsedTimer(timestamp));
        })
      })
    },
    error: function (xhr, status, error) {
      console.error('Lỗi:', error);
    }
  })

}


// Load ticket list
function loadDashboardTickets() {
  $.ajax({
    url: `${API_TICKET}/dashboard`,
    method: "GET",
    success: function (res) {
      console.log(res);
      //populateDashboard
      showTicketListLoading($("#ticketList"));
      //      setTimeout(function () {
      //        populateDashboardTicket(res.data);
      //        populateDashboardTicketMetrics(res.data);
      //        hideTicketListLoading($("#ticketList"));
      //      }, 500)
      populateDashboardTicket(res.data);
      populateDashboardTicketMetrics(res.data);
      hideTicketListLoading($("#ticketList"));
    },
    error: function (res) {
      errorToast(res.responseJSON.message);
    }
  })
}


//Populate Dashboard tick
function populateDashboardTicket(tickets) {
  const ticketList = $("#ticketList")
  ticketList.empty()

  tickets.forEach((ticket, index) => {
    const card = $(`
        <div class="item mb-2" data-ticket-id="${ticket.id}">
            <div class="d-flex flex-row">
                <div class=" w-100 d-flex flex-column me-2">
                    <div class="messages mb-1"></div>
                    <div class="title mb-1">
                        <span class="ticket-id me-2">#${ticket.id}</span> - ${ticket.title || "Chưa có tiêu đề"}
                    </div>
                    <div class="user">
                        <span class="avatar me-2 text-center">
                        <img src="${ticket.facebookUser.facebookProfilePic}">
                        </span><i class="bi bi-messenger me-2"></i>${ticket.facebookUser.facebookName || "- -"} </span>

                    </div>
                </div>
                <div class="w-25 d-flex flex-column justify-content-between me-2">
                    <div class="mb-1">
                        <i class="bi bi-activity me-2"></i><span class="badge progress-status ${ticket.progressStatus.code}">${ticket.progressStatus.name}</span>
                    </div>
                    <div class="assignee mb-1"><i class="bi bi-person-check me-2"></i>${ticket.assignee?.name || "Chưa có"}</div>
                    <div class="">
                      <i class="bi bi-hourglass me-2"></i><span class="duration time-elapse" data-timestamp=${ticket.createdAt}>${startElapsedTimer(ticket.createAt)}</span>
                    </div>
                </div>
            </div>
        </div>
      `)
    ticketList.append(card);
    window.card = card;
    setTimeout(function () {
      card.addClass("visible");
    }, index * 50);


  })
  if (window.startElapsedTimerTicketInterval) {
    clearInterval(window.startElapsedTimerTicketInterval);
    console.log("cleared ticket timer interval")
  }
  window.startElapsedTimerTicketInterval = setInterval(function () {
    $(".time-elapse").each(function () {
      const timestamp = $(this).attr("data-timestamp");
      $(this).text(startElapsedTimer(timestamp));
    })
  })

  $("#ticketList .item").click(function () {
    loadTicketDetail($(this).data("ticket-id"));
  });

  $("input#ticketSearch").on("keyup", function (e) {
    if (window.searchTimeout) clearTimeout(window.searchTimeout);

    window.searchTimeout = setTimeout(() => {
      filterTickets();
    }, 500);

  });
}

//Dashboard
function filterTickets() {
  const keyword = $("#ticketSearch").val().toLowerCase().trim();
  const field = $("#searchField").val();

  $("#ticketList .item").each(function () {
    const $item = $(this);
    let text = "";

    switch (field) {
      case "title":
        text = $item.find(".title").text();
        break;
      case "facebook":
        text = $item.find(".user").text();
        break;
      case "assignee":
        text = $item.find(".assignee").text();
        break;
      case "status":
        text = $item.find(".progress-status").text();
        break;
      case "category":
        text = $item.find(".category").text(); // nếu có
        break;
      default:
        text = $item.text();
    }

    if (text.toLowerCase().includes(keyword)) {
      $item.addClass("visible").show();
    } else {
      $item.removeClass("visible").hide();
    }
  });
}


// Dashboard Load ticket metrics
function populateDashboardTicketMetrics(tickets) {
  // Count tickets by status
  const totalTickets = tickets.length
  const pendingTickets = tickets.filter((ticket) => ticket.progressStatus.id === 1).length
  const onHoldTickets = tickets.filter((ticket) => ticket.progressStatus.id === 2).length
  const resolvedTickets = tickets.filter((ticket) => ticket.progressStatus.id === 3).length
  const closedTickets = tickets.filter((ticket) => ticket.status === "closed").length

  // Update metrics
  $("#totalTickets").text(totalTickets)
  $("#inProgressTickets").text(pendingTickets)
  $("#onHoldTickets").text(onHoldTickets)
  $("#resolvedTickets").text(resolvedTickets)
  $("#closedTickets").text(closedTickets)
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
function populateTicketDetail(ticket) {
  console.log("populateTicketDetail", ticket);
  originalTicketData = JSON.parse(JSON.stringify(ticket));
  currentEditingTicketId = ticket.id;

  $("#editTicketId").val(ticket.id);
  $("#editTitle").val(ticket.title || "");
  $("#editFacebookUser").val(`${ticket.facebookUser.facebookId}`);
  $("#editAssignee").val(ticket.assignee?.name || "- -");
  $("#editCreatedAt").val(formatEpochTimestamp(ticket.createdAt));
  $("#editCategory").val(ticket.category?.name || "- -");
  $("#editProgressStatus").val(ticket.progressStatus?.name || "- -");
  $("#editEmotion").val(ticket.emotion?.name || "- -");
  $("#editSatisfaction").val(ticket.satisfaction?.name || "- -");
  $("#editNote").val(ticket.description || "- -");


  $("#editCategory").attr("data-category-code", ticket.category?.code || null);
  $("#editProgressStatus").attr("data-progress-code", ticket.progressStatus.code || null);
  $("#editEmotion").attr("data-emotion-code", ticket.emotion?.code || null);
  $("#editSatisfaction").attr("data-satisfaction-code", ticket.satisfaction?.code || null);
  $("#editAssignee").attr("data-username", ticket.assignee?.username || null);

  // Load Tags (nhiều tag)
  // if (ticket.tags && Array.isArray(ticket.tags)) {
  //   $("#editTags").val(ticket.tags);
  // } else {
  //   $("#editTags").val([]);
  // }
  disableEditButtons();
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


// Helper function
function enableEditButtons() {
  $("#saveEdit").prop("disabled", false);
  $("#cancelEdit").prop("disabled", false);
}

function disableEditButtons() {
  $("#saveEdit").prop("disabled", true);
  $("#cancelEdit").prop("disabled", true);
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

function fetch_online_status() {
  $.ajax({
    url: API_ONLINE_STATUS,
    type: 'GET',
    dataType: 'json',
    success: function (data) {
      console.log(data);
      employees = data.data;
      ONLINE_STATUS = {}
    },
    error: function (xhr, status, error) {
      console.error('Lỗi:', error);
    }
  })
}

function startElapsedTimer(startTimestamp) {
  const ms = Date.now() - startTimestamp;
  const totalSeconds = Math.floor(ms / 1000);
  const hours = String(Math.floor(totalSeconds / 3600)).padStart(2, '0');
  const minutes = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0');
  const seconds = String(totalSeconds % 60).padStart(2, '0');
  return `${hours}:${minutes}:${seconds}`;

}

function toCapital(str) {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}


function loadDropdownField(ul, input, loadFn, globalVarName, dataKey, labelKey, valueKey) {
  (async function () {
    await loadFn(); // Gọi hàm load dữ liệu
    ul.html("");
    addClearForDropdown(ul, input);
    const list = globalVarName;
    list.forEach(item => {
      const li = $(`<li data-${dataKey}="${item[valueKey]}">`);
      const a = $("<a>").addClass("dropdown-item").text(item[labelKey]);
      li.append(a);
      ul.append(li);
      li.on("click", function () {
        input.val(item[labelKey]);
        input.attr(`data-${dataKey}`, item[valueKey]);
        ul.removeClass("show");
      });
    });
    ul.toggleClass("show");
  })()
}

function addClearForDropdown(ul, input) {
  const clear = $(`
    <li>
      <a class="dropdown-item">Xóa</a>
    </li>
    <li><hr class="dropdown-divider"></li>
    `);
  clear.click(function () {
    input.val("");
    $.each(input[0].attributes, function () {
      if (this.name.startsWith("data-")) {
        input.removeAttr(this.name);
      }
    })
    ul.removeClass("show");
  });
  ul.append(clear);
}
function initTicketCreate() {

  // Sự kiện mở modal
  $("#form-create-ticket").click(function () {
    $("#createTicketModal").modal("show");
  });

  // Gửi dữ liệu ticket mới
  $("#submitCreateTicket").click(function () {

    const assignee = $("#create_assignee").attr("data-username")
      ? { username: $("#create_assignee").attr("data-username") } : null;

    const facebookUser = $("#create_facebookuser").val() ?
      { id: $("#create_facebookuser").val() } : null


    const category = $("#create_category").attr("data-category-code") ?
      { code: $("#create_category").attr("data-category-code") } : null

    const progressStatus = $("#create_progress-status").attr("data-progress-code") ?
      { code: $("#create_progress-status").attr("data-progress-code") } : null

    const emotion = $("#create_emotion").attr("data-emotion-code") ?
      { code: $("#create_emotion").attr("data-emotion-code") } : null

    const satisfaction = $("#create-satisfaction").attr("data-satisfaction-code") ?
      { code: $("#create-satisfaction").attr("data-satisfaction-code") } : null

    const ticketData = {
      title: $("#create_title").val(),
      assignee: assignee,
      facebookUser: facebookUser,
      category: category,
      progressStatus: progressStatus,
      emotion: emotion,
      satisfaction: satisfaction
    };

    console.log(ticketData);

    $.ajax({
      url: `${API_TICKET}`,
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify(ticketData),
      success: function (response) {
        successToast(response.message);
        $("#createTicketModal").modal("hide");
        // refresh danh sách
      },
      error: function (res) {
        errorToast(res.responseJSON.message)
      }
    });
  });


  $("#resetCreateTicket").click(function () {
    $("#createTicketForm")[0].reset();

    $("#createTicketForm input").each(function () {
      $(this).removeAttr("data-id");
    });

    $("#createTicketForm .dropdown-menu").removeClass("show");
  });

}

function initTicketDetailModal() {
  const i = $(".field-group i");
  console.log("initTicketDetailModal");
  window.fullModal = new bootstrap.Modal(document.getElementById("ticketFullDetailModal"));

  i.click(function () {
    const container = $(this).closest(".field-group"); //traverse up to get parent container
    const ul = $(container).find("ul"); //find parent's target ul
    const input = $(container).find("input");

    //now based on the container's className to call load
    if (container.attr("class").includes("assignee")) {
      loadDropdownField(
        ul,
        input,
        loadUsers,
        USERS,
        "username",
        "name",
        "username"
      );
    } else if (container.attr("class").includes("category")) {
      loadDropdownField(
        ul,
        input,
        loadCategories,
        CATEGORIES,
        "category-code",
        "name",
        "code"
      );
    } else if (container.attr("class").includes("progress-status")) {
      loadDropdownField(
        ul,
        input,
        loadProgressStatus,
        PROGRESS_STATUS,
        "progress-code",
        "name",
        "code"
      );
    } else if (container.attr("class").includes("emotion")) {
      loadDropdownField(
        ul,
        input,
        loadEmotions,
        EMOTIONS,
        "emotion-code",
        "name",
        "code"
      );
    } else if (container.attr("class").includes("satisfaction")) {
      loadDropdownField(
        ul,
        input,
        loadSatisfaction,
        SATISFACTIONS,
        "satisfaction-code",
        "name",
        "code"
      );
    }


  })

  $("#ticketInfoColumn").on("input change", "input, select, textarea", function () {
    enableEditButtons();
  });

  $("#cancelEdit").click(function () {
    if (!originalTicketData) return;

    $("#editTitle").val(originalTicketData.title);
    $("#editCategory").val(originalTicketData.category);
    $("#editStatus").val(originalTicketData.status);
    $("#editProcessingStatus").val(originalTicketData.progressStatus);
    $("#editAssignee").val(originalTicketData.employee?.name || "- -");
    $("#editDescription").val(originalTicketData.description || "- -");

    disableEditButtons();
  });

  // Save edit
  $("#saveEdit").click(function () {
    const category = $("#editCategory").attr("data-category-code") ?
      { code: $("#editCategory").attr("data-category-code") } : null

    const progressStatus = $("#editProgressStatus").attr("data-progress-code") ?
      { code: $("#editProgressStatus").attr("data-progress-code") } : null

    const ticketData = {
      title: $("#editTitle").val(),
      category: category,
      progressStatus: progressStatus,
    };

    console.log("Updated Ticket Data:", ticketData);

    $.ajax({
      url: `${API_TICKET}/${$("#editTicketId").val()}`,
      method: "PUT",
      contentType: "application/json",
      data: JSON.stringify(ticketData),
      success: function (response) {
        successToast(response.message);
        populateTicketDetail(response.data);
        // performTicketSearch(0, $('#pageSize').val());
        // refresh danh sách
      },
      error: function (res) {
        errorToast(res.responseJSON.message)
      }
    });

    disableEditButtons();
  });


}

function performTicketSearch(page, pageSize) {
  $(this).prop("disabled", true);
  loadTicketSearch(page, pageSize);
  $(this).prop("disabled", false);
}

function initTicketSearch() {
  //button submit
  $("#form-submit").click(function (e) {
    performTicketSearch(0, $('#pageSize').val());
  });

  // Nút Làm Mới
  $("#form-reset").click(function () {
    $("#form-ticket-search input").val(""); // reset tất cả input
    $("#dateRangeLabel input").val("Thời gian");
    resetDateField();
  });

  initDataExport()
  loadDatetimePickerField();
}

function loadDatetimePickerField() {
  const now = new Date();

  $("#dateRangeLabel-container").click(function (e) {
    console.log("clock");
    $(".fast-pick .dropdown-menu").toggleClass("show");
  });

  $(".date-range .dropdown-item").click(function () {
    const range = $(this).data("range");
    const today = new Date();

    let from, to;

    $("#customPickerContainer").hide();

    switch (range) {
      case "today": {
        from = new Date(today);
        from.setHours(0, 0, 0, 0);

        to = new Date(from);
        to.setDate(to.getDate() + 1);
        setDateRange(from, to, "Hôm nay");
        break;
      }

      case "yesterday": {
        from = new Date(today);
        from.setDate(from.getDate() - 1);
        from.setHours(0, 0, 0, 0);

        to = new Date(from);
        to.setDate(to.getDate() + 1);
        setDateRange(from, to, "Hôm qua");
        break;
      }

      case "this_week": {
        from = new Date(today);
        from.setDate(today.getDate() - today.getDay() + 1 + 1);
        from.setHours(0, 0, 0, 0);

        to = new Date(today);
        to.setDate(to.getDate() + 1);
        to.setHours(0, 0, 0, 0);
        setDateRange(from, to, "Tuần này");
        break;
      }

      case "last_7_days": {
        from = new Date(today);
        from.setDate(from.getDate() - 7);
        from.setHours(0, 0, 0, 0);

        to = new Date(today);
        to.setDate(to.getDate() + 1);
        to.setHours(0, 0, 0, 0);
        setDateRange(from, to, "7 Ngày");
        break;
      }

      case "this_month": {
        from = new Date(today.getFullYear(), today.getMonth(), 1);
        from.setHours(0, 0, 0, 0);

        to = new Date(today);
        to.setDate(to.getDate() + 1);
        to.setHours(0, 0, 0, 0);
        setDateRange(from, to, "Tháng này");
        break;
      }

      case "last_30_days": {
        from = new Date(today);
        from.setDate(from.getDate() - 30);
        from.setHours(0, 0, 0, 0);

        to = new Date(today);
        to.setDate(to.getDate() + 1);
        to.setHours(0, 0, 0, 0);
        setDateRange(from, to, "30 Ngày");
        break;
      }

      case "custom": {
        $("#customPickerContainer").show();
        break;
      }
    }

    $(".date-range .dropdown-menu").removeClass("show");
  });

  resetDateField();

  flatpickr("#customDateRange", {
    mode: "range",
    dateFormat: "Y-m-d",
    locale: "vn",
    onClose: function (selectedDates) {
      if (selectedDates.length === 2) {
        setDateRange(selectedDates[0], new Date(selectedDates[1].getTime() + 24 * 60 * 60 * 1000), "Tùy chỉnh");
      }
    }
  });
}

function resetDateField() {
  console.log("tự load");
  const today = new Date();
  const from = new Date(today);
  from.setHours(0, 0, 0, 0);
  const to = new Date(today);
  to.setDate(today.getDate() + 1);
  setDateRange(from, to, "Hôm nay");
}

async function loadUsers() {
  if (USERS.length === 0) {
    try {
      const res = await new Promise((resolve, reject) => {
        $.ajax({
          url: `${API_EMPLOYEE}/get-all-user`,
          method: 'GET',
          contentType: 'application/json',
          success: function (data) {
            console.log(data);
            resolve(data);
          },
          error: reject
        });
      });
      USERS.push(...res.data);
    } catch (err) {
      console.error("Lỗi khi tải user:", err.responseText || err);
    }
  }
}

async function loadProgressStatus() {
  if (PROGRESS_STATUS.length === 0) {
    try {
      const res = await new Promise((resolve, reject) => {
        $.ajax({
          url: `${API_PROGRESS_STATUS}`,
          method: 'GET',
          contentType: 'application/json',
          success: function (data) {
            console.log(data);
            resolve(data);
          },
          error: reject
        });
      });
      PROGRESS_STATUS.push(...res.data);
    } catch (err) {
      console.error("Lỗi khi tải PROGRESS_STATUS:", err.responseText || err);
    }
  }
}

async function loadCategories() {
  if (CATEGORIES.length === 0) {
    try {
      const res = await new Promise((resolve, reject) => {
        $.ajax({
          url: `${API_CATEGORY}`,
          method: 'GET',
          contentType: 'application/json',
          success: resolve,
          error: reject
        });
      });
      CATEGORIES.push(...res.data);
    } catch (err) {
      console.error("Lỗi khi tải Phana loaij:", err.responseText || err);
    }
  }
}

async function loadEmotions() {
  if (EMOTIONS.length === 0) {
    try {
      const res = await new Promise((resolve, reject) => {
        $.ajax({
          url: `${API_EMOTION}`,
          method: 'GET',
          contentType: 'application/json',
          success: function (data) {
            console.log(data);
            resolve(data);
          },
          error: reject
        });
      });
      EMOTIONS.push(...res.data);
    } catch (err) {
      console.error("Lỗi khi tải Phana loaij:", err.responseText || err);
    }
  }
}

async function loadSatisfaction() {
  if (SATISFACTIONS.length === 0) {
    try {
      const res = await new Promise((resolve, reject) => {
        $.ajax({
          url: `${API_SATISFACTION}`,
          method: 'GET',
          contentType: 'application/json',
          success: function (data) {
            console.log(data);
            resolve(data);
          },
          error: reject
        });
      });
      SATISFACTIONS.push(...res.data);
    } catch (err) {
      console.error("Lỗi khi tải Mức Hài Lòng:", err.responseText || err);
    }
  }
}

function _formatDate(date) {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
}

function setDateRange(from, to, label = '') {
  console.log("Set date range to ", label);
  $('#fromDate').val(_formatDate(from));
  $('#toDate').val(_formatDate(to));
  $('#dateRangeLabel').val(label);
  // $('#fromDate').attr("data-timestamp-from", Math.round(from.getTime()));
  // $('#toDate').attr("data-timestamp-to", Math.round(to.getTime()));
}

function populateTicketSearchResult(data) {
  const content = data.content;
  console.log(data)
  console.log(data.totalElements == 0)
  const container = $("#ticket-list-body");
  container.html("");

  if (data.totalElements == 0) {
    container.append($(`
      <div id="no-ticket-result" class="text-center text-muted py-3" style="display: block;">
        <i class="bi bi-inbox me-1"></i> Không có kết quả phù hợp.
      </div>
      `))
    return;
  }

  content.forEach(function (ticket) {
    const html = `
      <div class="row border-bottom item"
           data-ticket-id="${ticket.id}"
           data-facebookId="${ticket.facebookUser.facebookId}">
        <div class="col text-truncate" title="${ticket.id}">${ticket.id}</div>
        <div class="col text-truncate" title="${ticket.title || ""}">${ticket.title || "- -"}</div>
        <div class="col text-truncate" title="${ticket.assignee?.name || ticket.assignee?.username}">
          ${ticket.assignee?.name || ticket.assignee?.username || "- -"}
        </div>
        <div class="col text-truncate" title="${ticket.facebookUser.facebookId}">
          ${ticket.facebookUser.facebookId}
        </div>
        <div class="col text-truncate progress-status-${ticket.progressStatus.code}"
             title="${ticket.progressStatus.name}">
          ${ticket.progressStatus.name}
        </div>
        <div class="col text-truncate category-${ticket.category?.code || ''}"
             title="${ticket.category?.name || ''}">
          ${ticket.category?.name || ''}
        </div>
        <div class="col text-truncate" title="${formatEpochTimestamp(ticket.createdAt)}">
          ${ticket.progressStatus.code == "resolved" ? "- -" : formatEpochTimestamp(ticket.createdAt)}
        </div>
        <div class="col text-truncate emotion-${ticket.emotion?.code || ''}"
             title="${ticket.emotion?.name || ''}">
          ${ticket.emotion?.name || ''}
        </div>
        <div class="col text-truncate satisfaction-${ticket.satisfaction?.code || ''}"
             title="${ticket.satisfaction?.name || ''}">
          ${ticket.satisfaction?.name || ''}
        </div>
      </div>
    `;

    container.append(html);
    // initColumnResizeHandles();

    //paging



  });

  // Gán sự kiện click cho từng item
  $("#ticket-list-body .item").click(function () {
    const ticketId = $(this).attr("data-ticket-id");
    loadTicketDetail(ticketId);
  })
}

function loadTicketDetail(ticketId) {
  console.log("click ticket view ", ticketId);
  window.fullModal.show();

  $.ajax({
    url: `${API_TICKET}`,
    method: 'GET',
    data: { id: ticketId },
    success: function (response) {
      populateTicketDetail(response.data);
      loadTicketMessages(response.data.id);
      loadTicketHistory(response.data.facebookUser.facebookId);
    },
    error: function (res) {
      errorToast(rres.responseJSON.message);
    }
  });

  // Khởi tạo lại tooltip (nếu dùng Bootstrap tooltip)
  $('[data-bs-toggle="tooltip"]').tooltip?.();
}
function loadTicketSearch(page = null, pageSize = null) {
  console.log("button submit: page and pagesize", page, pageSize);
  console.log(`${API_TICKET}/search?page=${page}&size=${pageSize}&sort=createdAt,DESC`);
  //get ticketDetailDTO data
  const ticketSearchCriteria = {
    assignee: $("#ticket-search #assignee").attr("data-username") || null,          // assignee
    facebookId: $("#ticket-search #facebookuser").val() || null,
    title: $("#ticket-search #title").val() || null,
    progressStatus: $("#ticket-search #progress-status").attr("data-progress-status-code") || null,
    fromDate: toTimestamp($("#fromDate").val()),
    toDate: toTimestamp($("#toDate").val()),
    category: $("#ticket-search #category").attr("data-category-code") || null,
    emotion: $("#ticket-search #emotion").attr("data-emotion-code") || null,
    satisfaction: $("#ticket-search #satisfaction").attr("satisfaction") || null,
  }
  console.log("ticketSearchCriteria ", ticketSearchCriteria);

  //TODO: call API search
  $.ajax({
    url: `${API_TICKET}/search?page=${page}&size=${pageSize}&sort=createdAt,DESC`,
    method: "POST",
    contentType: "application/json",
    data: JSON.stringify(ticketSearchCriteria),
    beforeSend: function () {

    },
    success: function (res) {
      successToast(res.message);
      //TODO: populate list
      showTicketListLoading($("#ticket-list-body"));
      setTimeout(function () {
        hideTicketListLoading($("#ticket-list-body"));
        populateTicketSearchResult(res.data)
        renderPagination(res.data.page,
          res.data.totalElements,
          res.data.size,
          performTicketSearch);
      }, 300);
      // populateTicketSearchResult(res.data);
    },
    error: function (err) {
      errorToast(res.responseJSON.message);
    },
  })
}
function resizeColumnByContent(index) {
  const $headerRow = $(".ticket-list-header");
  const $bodyRows = $(".ticket-list-body .row");

  const $cells = $([$headerRow.children().eq(index)[0]]);
  $bodyRows.each(function () {
    const $cell = $(this).children().eq(index);
    if ($cell.length) $cells.push($cell[0]);
  });

  const $testSpan = $("<span>")
    .css({
      visibility: "hidden",
      whiteSpace: "nowrap",
      position: "absolute",
      font: $headerRow.children().eq(index).css("font")
    })
    .appendTo("body");

  let maxWidth = 0;
  $cells.each(function () {
    $testSpan.text($(this).text());
    const width = $testSpan[0].offsetWidth + 24; // trừ hao
    maxWidth = Math.max(maxWidth, width);
  });

  $testSpan.remove();

  $cells.each(function () {
    $(this).css({
      flex: "none",
      width: `${maxWidth}px`
    });
  });
}

function initColumnResizeHandles() {
  $('.ticket-list-header .resizable').each(function () {
    const $col = $(this);
    const $handle = $("<div>")
      .css({
        width: "5px",
        cursor: "col-resize",
        position: "absolute",
        right: "0",
        top: "0",
        bottom: "0",
        zIndex: "10"
      })
      .appendTo($col);

    // Kéo để resize
    $handle.on("mousedown", function (e) {
      e.preventDefault();
      const startX = e.pageX;
      const startWidth = $col.outerWidth();
      const index = $col.index();

      function onMouseMove(e) {
        const newWidth = startWidth + (e.pageX - startX);
        $(`.ticket-list-header .col:nth-child(${index + 1}),
           .ticket-list-body .row .col:nth-child(${index + 1})`).css({
          flex: "none",
          width: `${newWidth}px`
        });
      }

      function onMouseUp() {
        $(document).off("mousemove", onMouseMove);
        $(document).off("mouseup", onMouseUp);
      }

      $(document).on("mousemove", onMouseMove);
      $(document).on("mouseup", onMouseUp);
    });

    // Double click để auto resize theo nội dung
    $handle.on("dblclick", function (e) {
      e.preventDefault();
      const index = $col.index();
      resizeColumnByContent(index);
    });
  });
}


function renderPagination(currentPageZeroBased, totalElements, pageSize, callback) {
  console.log("rendering pagination...");
  console.log("currentPageZeroBased...", currentPageZeroBased);
  console.log("totalElements...", totalElements);
  console.log("pageSize...", pageSize);
  const totalPages = Math.ceil(totalElements / pageSize);
  const $pagination = $("#pagination-menu");
  $pagination.empty();

  if (totalPages <= 1) return; // Không cần hiển thị nếu chỉ có 1 trang

  const currentPage = currentPageZeroBased + 1; // 1-based để hiển thị

  const createPageItem = (page, label = null, active = false, disabled = false) => {
    const li = $(`
      <li class="page-item ${active ? 'active' : ''} ${disabled ? 'disabled' : ''}">
        <a class="page-link" href="#">${label || page}</a>
      </li>
    `);
    if (!disabled && !active) {
      li.click(function (e) {
        e.preventDefault();
        callback(page - 1, pageSize); // Truyền page 0-based
      });
    }
    return li;
  };

  // Prev button
  $pagination.append(createPageItem(currentPage - 1, "Prev", false, currentPage === 1));

  let startPage = Math.max(1, currentPage - 1);
  let endPage = Math.min(totalPages, startPage + 2);

  if (endPage - startPage < 2 && startPage > 1) {
    startPage = Math.max(1, endPage - 2);
  }

  // Nếu chưa phải trang 1 → hiển thị trang 1 + ...
  if (startPage > 1) {
    $pagination.append(createPageItem(1));
    if (startPage > 2) {
      $pagination.append(`<li class="page-item disabled"><span class="page-link">...</span></li>`);
    }
  }

  // Các trang chính
  for (let page = startPage; page <= endPage; page++) {
    $pagination.append(createPageItem(page, null, page === currentPage));
  }

  // Nếu còn nhiều trang sau
  if (endPage < totalPages - 1) {
    $pagination.append(`<li class="page-item disabled"><span class="page-link">...</span></li>`);
  }

  // Hiển thị trang cuối nếu chưa có
  if (endPage < totalPages) {
    $pagination.append(createPageItem(totalPages));
  }

  // Next button
  $pagination.append(createPageItem(currentPage + 1, "Next", false, currentPage === totalPages));
}

function initTicketSorting() {
  console.log("init ticket sorting");
  $(".page-list-header .col.resizable").click(function () {
    const $col = $(this);
    const sortField = $col.data("sort");
    let currentDirection = $col.data("sort-direction") || "none";

    // Reset các cột khác
    $(".page-list-header .col.resizable").not($col).data("sort-direction", "none");

    // Toggle hướng sắp xếp
    const newDirection = currentDirection === "asc" ? "desc" : "asc";
    $col.data("sort-direction", newDirection);

    // Gọi hàm reload list (ví dụ từ server hoặc sort tại client)
    sortTicketListByIndex(sortField, newDirection);
  });
}

function sortTicketListBy(field, direction) {
  const $rows = $("#ticket-list-body .row").get();

  $rows.sort((a, b) => {
    const aText = $(a).find(`.col[data-field="${field}"]`).text().trim();
    const bText = $(b).find(`.col[data-field="${field}"]`).text().trim();

    if (!isNaN(aText) && !isNaN(bText)) {
      return direction === "asc" ? aText - bText : bText - aText;
    } else {
      return direction === "asc"
        ? aText.localeCompare(bText)
        : bText.localeCompare(aText);
    }
  });

  console.log($rows);
  $("#ticket-list-body").html($rows);
}

function initTicketSortingByIndex() {
  $(".page-list-header .col.resizable").click(function () {
    const $col = $(this);
    const index = $col.index();
    let direction = $col.data("sort-direction") || "none";

    // Reset các cột khác
    $(".page-list-header .col.resizable").not($col).data("sort-direction", "none");

    // Toggle hướng
    direction = direction === "asc" ? "desc" : "asc";
    $col.data("sort-direction", direction);

    sortTicketListByIndex(index, direction);
  });
}


function sortTicketListByIndex(colIndex, direction) {
  const $rows = $("#ticket-list-body .row").get();

  $rows.sort((a, b) => {
    const aText = $(a).children(".col").eq(colIndex).text().trim();
    const bText = $(b).children(".col").eq(colIndex).text().trim();

    if (!isNaN(aText) && !isNaN(bText)) {
      return direction === "asc" ? aText - bText : bText - aText;
    } else {
      return direction === "asc"
        ? aText.localeCompare(bText)
        : bText.localeCompare(aText);
    }
  });

  $("#ticket-list-body").html($rows);
}

// Sự kiện mở modal
function initDataExport() {
  // Nút Xuất Excel (stub)
  $("#form-export-excel").click(function () {
    const data = {
      assignee: $("#ticket-search #assignee").attr("data-username") || null,          // assignee
      facebookId: $("#ticket-search #facebookuser").val() || null,
      title: $("#ticket-search #title").val() || null,
      progressStatus: $("#ticket-search #progress-status").attr("data-progress-status-code") || null,
      fromDate: toTimestamp($("#fromDate").val()),
      toDate: toTimestamp($("#toDate").val()),
      category: $("#ticket-search #category").attr("data-category-code") || null,
      emotion: $("#ticket-search #emotion").attr("data-emotion-code") || null,
      satisfaction: $("#ticket-search #satisfaction").attr("satisfaction") || null,
    };

    console.log(data);
    console.log(JSON.stringify(data))
    $.ajax({
      url: `${API_TICKET}/export-excel`, // Stub endpoint, bạn có thể cập nhật
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify(data),
      xhrFields: {
        responseType: 'blob' // để tải về file
      },
      success: function (blob) {
        successToast("tải thành công");
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = "tickets.xlsx";
        link.click();
      },
      error: function (res) {
        errorToast("Lỗi tải xuống excel");
      },
    });
  });
}

function toTimestamp(dateString) {
  return new Date(dateString).getTime();
}

function showTicketListLoading(container) {
  container.html(`
    <div class="row loading-row justify-content-center text-muted py-3">
      <div class="col-auto">
        <div class="spinner-border spinner-border-sm me-2 text-primary" role="status"></div>
        Đang tải dữ liệu...
      </div>
    </div>
  `);
}

function hideTicketListLoading(container) {
  container.find(".loading-row").remove();
}


function showToast(type, message) {
  const toastId = `toast-${Date.now()}`;

  const toastHtml = `
    <div id="${toastId}" class="toast align-items-center text-white bg-${type} border-0 mb-2" role="alert" aria-live="assertive" aria-atomic="true" data-bs-delay="3000">
      <div class="d-flex">
        <div class="toast-body">${message}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
      </div>
    </div>
  `;

  const $container = $("#toastContainer");
  $container.append(toastHtml);
  console.log($container)

  const toast = new bootstrap.Toast(document.getElementById(toastId));
  toast.show();

  $(`#${toastId}`).on('hidden.bs.toast', function () {
    $(this).remove();
  });
}

function successToast(message) {
  showToast("success", message || "🎉 Thành công!");
}

function errorToast(message) {
  showToast("danger", message || "❌ Có lỗi xảy ra!");
}

function validationToast(message) {
  showToast("warning", message || "⚠️ Dữ liệu không hợp lệ.");
}

// Load messages kiểu chat
function loadTicketMessages(ticketId) {
  $("#messageList").empty();
  $.ajax({
    url: `${API_MESSAGE}`,
    method: 'GET',
    data: { ticketId: ticketId },
    success: function (response) {
      console.log("get message Success:", response);
      const ticketMessages = response.data;
      ticketMessages.sort((a, b) => a.timestamp - b.timestamp);

      ticketMessages.forEach(msg => {
        const senderClass = msg.senderEmployee ? "staff" : "user";
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

    },
    error: (res) => errorToast(res.responseJSON.message)
  });
}


// Auto scroll to bottom
function scrollToBottomMessageList() {
  const messageList = document.getElementById('messageList');
  if (messageList) {
    messageList.scrollTop = messageList.scrollHeight;
  }
}



// Load ticket history
function loadTicketHistory(facebookId) {
  $("#historyList").empty();

  $.ajax({
    url: `${API_TICKET}/get-by-facebook-id`,
    method: 'GET',
    data: { id: facebookId },
    beforeSend: function () {
      console.log("loading screen.show")
      // $('#loadingScreen').show();
    },
    success: function (response) {
      const history = response.data;
      history.forEach(hist => {
        const item = `
          <li class="list-group-item d-flex flex-column">
            <strong>#${hist.id}</strong>
            <small>${hist.title || "Chưa có tiêu đề"}</small>
            <small>${formatEpochTimestamp(hist.createdAt)}</small>
          </li>
        `;
        $("#historyList").append(item);
      });
      scrollToBottomMessageList();

    },
    error: function (xhr, status, error) {
      alert("Đã xảy ra lỗi khi tải thông tin ticket.");
    },
    complete: function () {
      // $('#loadingScreen').hide();
      console.log("loading screen.off")
    }
  });
}


function validateChangePassword() {
  const pw = $("#password");
  const newPw = $("#new-password");
  const confirmPw = $("#confirm-password");
  const errPw = $("#error-password");
  const errNewPw = $("#error-new-password");
  const errConfirmPw = $("#error-confirm-password");
  pw.val() === "" ? errPw.removeClass("d-none") : errPw.addClass("d-none");
  newPw.val() === "" ? errNewPw.removeClass("d-none") : errNewPw.addClass("d-none");
  confirmPw.val() != newPw.val() ? errConfirmPw.removeClass("d-none") : errConfirmPw.addClass("d-none");

  const result = pw.val() === "" && newPw.val() === "" && confirmPw.val() != newPw.val();
  if (result == false) {
    $("#confirmResetBtn").prop("disabled", result);
  }
}




function createXHR() {
  return new XMLHttpRequest();
}

function handleResponse(xhr, callback, errorCallback = null) {
  xhr.onreadystatechange = function () {
    if (this.readyState == 4) {
      let response = this.response;
      if (this.status == 200) {
        callback(response);
      } else {
        if (errorCallback) {
          errorCallback(response);
        } else {
          errorToast(response.message);
        }
      }
    }
  }
}

function sanitizeText(text) {
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}