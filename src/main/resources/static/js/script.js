const currentSort = { field: null, direction: 'asc' };

const BASE = window.location.href.includes(`cshelpdesk.online`) ? "" : `http://localhost:8080`;
const API_EMPLOYEE = `${BASE}/api/employee-management`;
const API_PERMISSION = `${BASE}/api/employee-management/permission`;
const API_USERGROUP = `${BASE}/api/employee-management/user-group`;
const API_TICKET = `${BASE}/api/ticket`;
const API_CATEGORY = `${BASE}/api/category`;
const API_TAG = `${BASE}/api/tag`;
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
window.customerViewDetailModal = null;
$(document).ready(() => {
  $("#loadingOverlay").fadeOut()
  initHeader();

  if (window.location.href.includes("today-staff") || window.location.href.includes("index")
  ) {
    initTodayStaff();
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(0).classList.add("active");
  } else if (window.location.href.includes("today-ticket")) {
    initTodayTicket()
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(1).classList.add("active");
  } else if (window.location.href.includes("ticket")) {

    initTicket()
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(2).classList.add("active");
  } else if (window.location.href.includes("customer")) {
    initCustomer();
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(3).classList.add("active");

  } else if (window.location.href.includes("performance")) {
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(4).classList.add("active");
  } else if (window.location.href.includes("report")) {
    initReport();
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(5).classList.add("active");
  } else if (window.location.href.includes("setting")) {
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(6).classList.add("active");
  }
})

// today-staff.html
function initTodayStaff() {
  loadDashboardEmployees()

  //Load employee2 list
  function loadDashboardEmployees() {
    const employeeList = document.getElementById("employeeList2");
    const url = `${API_EMPLOYEE}/dashboard`
    const countElem = document.querySelector(".employee-count");
    const callback = function (response) {
      showLoadingElement(employeeList);
      populateData(response.data, employeeList, renderDashboardEmployeeItem);
      // Update employee count
      countElem.innerText = response.data.length;

      //add event interval
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
    }

    openAPIxhr(HTTP_GET_METHOD, url, callback);

  }
}

function renderDashboardEmployeeItem(employee) {
  console.log("..rendering items");
  const tr = document.createElement("tr");
  tr.classList.add("show");
  tr.innerHTML = `
            <td>${sanitizeText(employee.name)}</td>
            <td>${sanitizeText(employee.userGroup.name)}</td>
            <td>${employee.ticketCount || 0}/6</td>
            <td style="text-transform: capitalize;">
              <span class="status-indicator ${employee.statusLog.status}"></span>
              ${sanitizeText(employee.statusLog.status)}
            </td>
            ${employee.statusLog.status == "offline" ? "" : `<td class="time-elapse" data-timestamp="${employee.statusLog.from}">${startElapsedTimer(employee.statusLog.from)}</td>`}
        `;
  console.log(tr);
  return tr;
}
function populateData(data, container, renderItemCallback) {
  container.innerHTML = "";
  const fragment = document.createDocumentFragment();
  console.log(data);
  data.forEach((item) => {
    fragment.append(renderItemCallback(item));
  })
  container.append(fragment);
}

// today-ticket.html
function initTodayTicket() {
  initTicketDetailModal();
  refreshDashboardTicket();
  //TODO:
  $("#refreshDashboardTicket").click(() => {
    refreshDashboardTicket();
  })

  // Search tickets
  //TODO:
  bounded = null
  $("#ticketSearch").on("keyup", function () {
    clearTimeout(bounded);
    bounded = setTimeout(() => {
      const searchTerm = $(this).val().toLowerCase();
      console.log("...filtering search team: ", searchTerm);
      filterTickets(searchTerm)
    }, 500);
  })



}

function refreshTodayTicketMetrics() {
  console.log("init ticket metrics")
}
//customer/html
function initCustomer() {
  console.log("init customer page");
  initCustomerDeleteModal();
  initCustomerExport();
  initCustomerViewModal();
  bindItemClickEvents(); //TODO: delete after testing
  window.customerViewDetailModal = new bootstrap.Modal(document.getElementById("customerDetailModal"));

  document.querySelector(".select-all input[type=checkbox]").addEventListener("click", function (e) {
    $("#customer-list-body .item input[type=checkbox]").prop("checked", $(this).prop("checked"));
  });

  $(".search-common i").click(function (e) {
    performSearchCustomer(page = 0, size = $("#pageSize").val());
  });

  function initCustomerDeleteModal() {
    const button = document.getElementById("delete-checkbox");
    const selectBtn = document.querySelector(".select-all input[type=checkbox");
    const confirmBtn = document.getElementById("confirmDeleteCustomerBtn");
    window.customerDeleteModal = new bootstrap.Modal(document.getElementById("customerDeleteModal"));
    //bind click open modal
    button.addEventListener("click", function () {
      window.customerDeleteModal.show();
      confirmBtn.removeEventListener("click", deleteCustomerHandler);
      deleteCustomerHandler = function () {
        console.log("hehe");
        //get array of ids
        const checkboxes = document.querySelectorAll("input[type=checkbox]");
        arr = []
        checkboxes.forEach(checkbox => {
          if (checkbox != confirmBtn) {
            console.log(checkbox);
            console.log(checkbox.closest(".row[data-id]"))
            console.log("============")
            value = checkbox.closest(".row[data-id]")?.getAttribute("data-id");
            if (checkbox.checked == true && value != "" && value != null) {
              arr.push(value);
            }
          }
        })
        console.log("arr ", arr);
        openAPIxhr(HTTP_DELETE_METHOD, `${API_FACEBOOK_USER}/delete-all`, function () {
          console.log("clean up");
          window.customerDeleteModal.hide();
          setTimeout(function () {
            checkboxes.forEach(checkbox => {
              checkbox.checked = false;
              checkbox.dispatchEvent(new Event("change", { bubbles: true }));
            })
            performSearchCustomer();
          }, 500)
        }
          , null, arr);
      }
      confirmBtn.addEventListener("click", deleteCustomerHandler);
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
    const _url = `${API_FACEBOOK_USER}/search?${buildQueryParam(data)}`;
    console.log(_url)
    loading.show();
    openAPIxhr(HTTP_GET_METHOD, _url, function (response) {
      parseCustomerSearchResult(response, loading, container)
    });
  }

  function parseCustomerSearchResult(response, loading, container) {
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
      <div class="col">${sanitizeText(customer.realName) || "- -"}</div>
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

  function initCustomerViewModal() {
    const editBtn = document.getElementById("edit");
    const editedBtn = document.getElementById("edited");
    const cancelBtn = document.getElementById("cancel-edit");
    const submitBtn = document.getElementById("submit-edited");
    const inputList = document.querySelectorAll(".detail-info input");
    editBtn.addEventListener("click", function () {
      editedBtn.classList.remove("d-none");
      this.classList.add("d-none");
      const fieldNameList = document.querySelectorAll(".field-value");
      fieldNameList.forEach(item => {
        item.classList.add("d-none");
        console.log(item);
        input = item.nextElementSibling;
        input.classList.remove("d-none");
        input.value = item.textContent;
      })
    })

    cancelBtn.addEventListener("click", function () {
      editBtn.classList.remove("d-none");
      editedBtn.classList.add("d-none");
      const fieldNameList = document.querySelectorAll(".field-value");
      fieldNameList.forEach(item => {
        item.classList.remove("d-none");
        item.nextElementSibling.classList.add("d-none");
      })

    })

    submitBtn.addEventListener("click", function () {
      //get data
      data = {
        facebookId: document.getElementById("facebookId").innerText,
        realName: document.getElementById("realName").value,
        phone: document.getElementById("phone").value,
        email: document.getElementById("email").value,
        zalo: document.getElementById("zalo").value
      }
      openAPIxhr(HTTP_PUT_METHOD, `${API_FACEBOOK_USER}`, function (response) {
        window.customerViewDetailModal.hide();
        setTimeout(function () {
          openCustomerViewDetailModal(data.facebookId);
          cancelBtn.click();
        }, 500);
      }, null, data);
    })

    inputList.forEach(item => {
      item.addEventListener("keyup", function () {
        let original = item.getAttribute("data-original");
        console.log(original, item.value, item.value != "" && item.value != original);
        if (item.value != "" && item.value != original) {
          submitBtn.disabled = false;
        } else {
          submitBtn.disabled = true;
        }
      })
    })

  }

  function createTicketHistoryItem(ticket) {
    const div = document.createElement("div");
    div.innerHTML = `
    <div class="item">
        <div class="d-flex flex-row">
            <div class="i-badge me-3">
                <i class="bi bi-ticket-perforated"></i>
            </div>
            <div class="">
                <div>
                    <div class="title mb-2">${sanitizeText(ticket.title) || "Chưa có tiêu đề"}<span class="ticketId"> -
                            #ID: <span
                                id="ticketId">${ticket.id}</span></span></div>
                    <div class="assignee mb-1">Admin - <span class="category">${sanitizeText(ticket.category?.name) || "Chưa phân loại"}</span></div>
                    <div class="createdAt mb-1">${formatEpochTimestamp(ticket.createdAt)}</div>
                </div>
            </div>
            <div class="ms-auto text-start d-flex flex-column justify-content-evenly">
                <div class=""><i class="bi bi-activity me-3"></i><span class="progressStatus ${ticket.progressStatus.code}">${sanitizeText(ticket.progressStatus.name)}</span></div>
                <div class="emotion"><i class="bi bi-emoji-smile me-3"></i>${sanitizeText(ticket.emotion?.name) || "- -"}</div>
                <div class="satisfaction"><i class="bi bi-stars me-3"></i>${sanitizeText(ticket.satisfaction?.name) || "- -"}</div>
            </div>
        </div>
    </div>
    `
    return div.firstElementChild;
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
        openCustomerViewDetailModal(id);
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
    window.customerViewDetailModal.show();
    //fetch customer ticket history
    fetchCustomerTicketHistory(id)
    fetchCustomerDetail(id)

  }

  function fetchCustomerTicketHistory(id) {
    const container = document.querySelector("#customerDetailModal .data-table");
    container.innerHTML = `
      <div id="loading-result" class="fs-5 loading-row justify-content-center text-muted py-3 text-center"
          style="display: block">
          <div class="col-auto">
              <div class="spinner-border spinner-border-sm me-2 text-primary" role="status"></div>
              Đang tải dữ liệu...
          </div>
      </div>
    `;
    //fetch
    container.getAttribute("data-page");
    data = {
      facebookId: id,
      page: container.getAttribute("data-page") || 0,
      size: 10
    }
    url = `${API_TICKET}/search?${buildQueryParam(data)}`;
    setTimeout(function () {
      openAPIxhr(HTTP_GET_METHOD, url, function (response) {
        parseCustomerTicketHistoryResult(response, container)
      })
    }, 1000);

  }
  function parseCustomerTicketHistoryResult(res, container) {
    console.log(res);
    console.log(container);
    if (res.data.totalElements != 0) {
      const fragment = document.createDocumentFragment();
      res.data.content.forEach(ticket => {
        fragment.appendChild(createTicketHistoryItem(ticket));

      })
      container.innerHTML = ``;
      container.appendChild(fragment);
    } else {
      showNoTicketHistory(container);
    }
  }
  function showNoTicketHistory(container) {
    container.innerHTML = `
    <div class="text-center">
      <img src="/img/no-ticket-yet.svg" width="130" height="130">
      <div class="text-muted">Hiện chưa có ticket.</div>
    </div>
    `
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
    console.log(id);
    const container = document.querySelector("#customerDetailModal .detail-info");
    setTimeout(function () {
      openAPIxhr(HTTP_GET_METHOD, `${API_FACEBOOK_USER}?id=${id}`, function (response) {
        console.log("ok..");
        populateCustomerEditModal(response, container);
      })
    }, 700)

  }

  function populateCustomerEditModal(response, container) {

    console.log("Hey", response);
    customer = response.data;
    window.customer = customer;
    const realName = document.getElementById("realName");
    const phone = document.getElementById("phone");
    const email = document.getElementById("email")
    const zalo = document.getElementById("zalo");
    document.querySelector("#facebookName").textContent = customer.facebookName || "- -";
    document.querySelector("#facebookId").textContent = customer.facebookId || "- -";
    document.querySelector("#facebookProfilePic").src = customer.facebookProfilePic || "/img/facebookuser-profile-placeholder.jpg";
    realName.value = customer.realName || "";
    realName.previousElementSibling.textContent = customer.realName || "- -";
    phone.value = customer.phone || "";
    phone.previousElementSibling.textContent = customer.phone || "- -";
    email.value = customer.email || "";
    email.previousElementSibling.textContent = customer.email || "- -";
    zalo.value = customer.zalo || "";
    zalo.previousElementSibling.textContent = customer.zalo || "- -";
    //add original data
    realName.setAttribute("data-original", customer.realName);
    phone.setAttribute("data-original", customer.phone);
    email.setAttribute("data-original", customer.email);
    zalo.setAttribute("data-original", customer.zalo);


    container.parentElement.previousElementSibling.classList.add("d-none");
    container.parentElement.classList.remove("d-none");
    console.log(container);
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
        successToast(response.message);
        window.customerDeleteModal.hide();
      })
      xhr.open(HTTP_DELETE_METHOD, `${API_FACEBOOK_USER}?id=${id}`)
      xhr.send();
    }
    confirm.addEventListener("click", deleteCustomerHandler)

  }


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


  // fetch_online_status()

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


// Refresh dashboard
function refreshDashboardTicket() {
  // Show loading animation on refresh button
  const refreshBtn = $("#refreshDashboardTicket")
  const originalContent = refreshBtn.html();
  const url = `${API_TICKET}/dashboard`;
  const container = document.getElementById("ticketList");
  refreshBtn.html('<i class="bi bi-arrow-repeat"></i> <span>Đang tải...</span>')
  refreshBtn.prop("disabled", true)

  //defines callback
  const callback = function (response) {

    data = sortDashboardTicket(response.data);
    console.log(data);
    showLoadingElement(container);
    populateData(data, container, renderDashboardTicketItem)

    //after render tickets, start  event
    if (window.startElapsedTimerTicketInterval) {
      clearInterval(window.startElapsedTimerTicketInterval);
      console.log("cleared ticket timer interval")
    }
    window.startElapsedTimerTicketInterval = setInterval(function () {
      $(".time-elapse").each(function () {
        const timestamp = $(this).attr("data-timestamp");
        $(this).text(startElapsedTimer(timestamp));
      })
    });

    //refreshing ticket metrics;
    refreshDashboardTicketMetrics(response.data);

    // Restore refresh button
    refreshBtn.html(originalContent)
    refreshBtn.prop("disabled", false)
  }

  //call API
  openAPIxhr(HTTP_GET_METHOD, url, callback);

}

function refreshDashboardTicketMetrics(data) {
  console.log("..refreshing ticket metrics");
  const totalElem = document.getElementById("totalTickets");
  const inprogressElem = document.getElementById("inProgressTickets");
  const onHoldElem = document.getElementById("onHoldTickets");
  const resolvedElem = document.getElementById("resolvedTickets");

  //resolving data
  total = 0;
  inprogress = 0;
  onhold = 0;
  resolved = 0;
  data.map(ticket => {
    //get status
    let code = ticket.progressStatus.code;
    total += 1;
    if (code == "pending") {
      inprogress += 1;
    } else if (code == "on-hold") {
      onhold += 1;
    } else {
      resolved += 1;
    }
  })
  //animate count
  animateCount(totalElem, total, 1000, 0);
  animateCount(inprogressElem, inprogress, 1000, 0);
  animateCount(onHoldElem, onhold, 1000, 0);
  animateCount(resolvedElem, resolved, 1000, 0);

  // Update last updated time
  const now = new Date()
  $("#lastUpdated").text(formatTime(now))
}

function sortDashboardTicket(data) {
  data.sort((a, b) => {
    const isA3 = a.progressStatus.id === 3;
    const isB3 = b.progressStatus.id === 3;

    // Nếu chỉ A là 3 → A xuống dưới
    if (isA3 && !isB3) return 1;

    // Nếu chỉ B là 3 → B xuống dưới
    if (!isA3 && isB3) return -1;

    // Nếu cả hai đều != 3 → sort theo createdAt DESC
    return new Date(b.createdAt) - new Date(a.createdAt);
  })

  return data;
}

//Populate Dashboard tick
function renderDashboardTicketItem(ticket) {
  const div = document.createElement("div");
  div.innerHTML = `
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
      `;
  const target = div.firstElementChild;
  target.addEventListener("click", function () {
    loadTicketDetail($(this).data("ticket-id"));
  })
  setTimeout(function () {
    target.style.opacity = 1;
    console.log(target);
  }, 300);

  return target;

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
  console.log(searchTerm);
  $("#ticketList .item").each(function () {
    const rowText = $(this).text().toLowerCase();
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
  //get API to get online status
  const statusText = $(this).text().trim();
  const statusValue = statusText.toLowerCase();
  var $indicator = $('.status-dropdown .status-indicator');
  var $statusText = $('.status-dropdown #currentStatusText');

  const xhr = createXHR();
  xhr.open(HTTP_GET_METHOD, `${API_EMPLOYEE}/me/online-status`);
  handleResponse(xhr, function (response) {
    res = JSON.parse(response);
    console.log(res);
    const statusValue = res.status;
    const statusText = toCapital(statusValue);
    $indicator.addClass(statusValue);
    $statusText.text(statusText);
  })
  xhr.send(JSON.stringify(null));
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
  loadTicketSearch(page, pageSize);
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
  const container = document.getElementById("ticket-list-body");
  const data = getTicketSearchData(page, pageSize);
  const url = `${API_TICKET}/search?${buildQueryParam(data)}`;
  callback = function (response) {
    //TODO: populate list
    showLoadingElement(container);

    if (response.totalElements == 0) {
      showNoResult(container);
      return;
    }
    populateData(response.data.content, container, renderTicketSearchItem);
    renderPagination(response.data.page,
      response.data.totalElements,
      response.data.size,
      performTicketSearch);
    successToast(response.message);
    // populateTicketSearchResult(res.data);
  }

  // call API search
  openAPIxhr(HTTP_GET_METHOD, url, callback);

}
function showNoResult(container) {
  container.innerHTML = `
    <div id="no-ticket-result" class="text-center text-muted py-3" style="display: block;">
        <i class="bi bi-inbox me-1"></i> Không có kết quả phù hợp.
      </div>
  `
}
function getTicketSearchData(page, size) {
  //get ticketDetailDTO data
  const ticketSearchCriteria = {
    assignee: $("#ticket-search #assignee").attr("data-username") || null,          // assignee
    facebookId: $("#ticket-search #facebookuser").val() || null,
    title: $("#ticket-search #title").val() || null,
    progressStatus: $("#ticket-search #progress-status").attr("data-progress-status-code") || null,
    fromTime: toTimestamp($("#fromDate").val()),
    toTime: toTimestamp($("#toDate").val()),
    category: $("#ticket-search #category").attr("data-category-code") || null,
    emotion: $("#ticket-search #emotion").attr("data-emotion-code") || null,
    satisfaction: $("#ticket-search #satisfaction").attr("satisfaction") || null,
    page: page,
    size: pageSize,
    sort: "createdAt,DESC"
  }
  console.log("ticketSearchCriteria ", ticketSearchCriteria);
  return ticketSearchCriteria;
}

function renderTicketSearchItem(ticket) {
  const div = document.createElement("div");
  div.innerHTML = `
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
  return div.firstElementChild;
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
      fromTime: toTimestamp($("#fromDate").val()),
      toTime: toTimestamp($("#toDate").val()),
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
// Load ticket list
function loadDashboardTickets() {
  $.ajax({
    url: `${API_TICKET}/dashboard`,
    method: "GET",
    success: function (res) {
      console.log(res);
      //populateDashboard
      showLoadingElement($("#ticketList"));
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

function showLoadingElement(container) {
  content = `
    <div class="d-flex flex-row fs-5 loading-row justify-content-center text-muted py-3">
      <div class="text-center">
        <div class="spinner-border spinner-border-sm me-2 text-primary" role="status"></div>
        Đang tải dữ liệu...
      </div>
    </div>
  `;
  try {
    container.html(content);
  } catch (err) {
    container.innerHTML = content;
  }

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




function sanitizeText(text) {
  if (text == null || text.trim() == "") return ``;
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}

function initReport() {
  let ticketVolumeHourlyChart = null;
  const CHART_CONFIG = {
    padding: {
      sm: 15,
      md: 20,
      lg: 25
    },
    title: "Ticket Distribution Hourly",
    axisTitles: {
      x: "Khung giờ",
      y: "Số lượng ticket"
    },
    colors: {
      font: '#6c757d',
      bar: 'rgba(54, 162, 235, 0.6)',
      hoverBar: 'rgba(54, 162, 235, 1)',
      barSet: ['#4e73df77', '#1cc88a77', '#36b9cc', '#f6c23e'],
      hoverBarSet: ['#2e59d9ffff', '#1cc88aff', '#2c9fafff', '#f4b619ff'],
      lineSet: ['#e74a3b', '#fd7e14', '#20c997', '#6610f2'] // màu đường line
    },
    fontSize: {
      axis: 14,
      title: 18
    },
    barPercentage: 1,
    MAX_DATASETS: 4
  };

  initWidgetCard();
  initChart();
  initAddDataset();


  function initWidgetCard() {
    // Định nghĩa giá trị theo tỷ lệ Max
    const max = 56;
    const avg = 24.5;
    const min = 4;

    const duration = 500; // 0.5s

    // Tính phần trăm
    const avgPercent = (avg / max * 100).toFixed(1);
    const minPercent = (min / max * 100).toFixed(1);

    // Lấy ra thanh progress
    const widgets = document.querySelectorAll(".stat-value");

    // Set width sau 100ms để trigger transition
    setTimeout(() => {

      animateCount(widgets[0], max, duration, 0);
      animateCount(widgets[1], avg, duration, 1);
      animateCount(widgets[2], min, duration, 0);
    }, 100);
  }
  function initChart() {
    const dataset = {
      label: "Hôm nay",
      data: [
        { hour: 0, value: 10 },
        { hour: 1, value: 20 },
        { hour: 2, value: 15 },
        { hour: 3, value: 25 },
        { hour: 4, value: 22 },
        { hour: 5, value: 30 },
        { hour: 6, value: 28 },
        { hour: 7, value: 28 },
        { hour: 8, value: 20 },
        { hour: 9, value: 15 },
        { hour: 10, value: 25 },
        { hour: 11, value: 22 },
        { hour: 12, value: 30 },
        { hour: 13, value: 0 },
        { hour: 14, value: 0 },
        { hour: 15, value: 0 },
        { hour: 16, value: 0 },
        { hour: 17, value: 0 },
        { hour: 18, value: 0 },
        { hour: 19, value: 0 },
        { hour: 20, value: 0 },
        { hour: 21, value: 0 },
        { hour: 22, value: 0 },
        { hour: 23, value: 0 },
      ]

    };

    const newData = {
      label: "3 Tháng",
      type: "bar",
      data: [
        { hour: 0, value: 15 },
        { hour: 1, value: 25 },
        { hour: 2, value: 11 },
        { hour: 3, value: 19 },
        { hour: 4, value: 17 },
        { hour: 5, value: 22 },
        { hour: 6, value: 12 },
        { hour: 7, value: 12 },
        { hour: 8, value: 16 },
        { hour: 9, value: 17 },
        { hour: 10, value: 23 },
        { hour: 11, value: 19 },
        { hour: 12, value: 6 },
        { hour: 13, value: 9 },
        { hour: 14, value: 15 },
        { hour: 15, value: 19 },
        { hour: 16, value: 20 },
        { hour: 17, value: 33 },
        { hour: 18, value: 39 },
        { hour: 19, value: 47 },
        { hour: 20, value: 0 },
        { hour: 21, value: 0 },
        { hour: 22, value: 0 },
        { hour: 23, value: 0 },
      ]
    };
    // Initialize chart
    const canvas = document.getElementById('my-chart-canvas').getContext('2d');
    if (window.ticketVolumeHourlyChart == null) {
      ticketVolumeHourlyChart = createChart('my-chart-canvas', dataset.data.map(row => row.hour), dataset);
    }
    addDataset(ticketVolumeHourlyChart, newData);
  }
  function initAddDataset() {
    console.log("init adad dataset");
    const btn = document.getElementById("add-dataset");

    //init button click
    btn.addEventListener("click", function () {
      const dateOption = document.querySelector(".date-range select").value;
      const [fromDate, toDate] = getDateRangeFromOption(dateOption);
      data = {
        fromTime: toTimestamp(fromDate),
        toTime: toTimestamp(toDate)
      }
      console.log(fromDate + "\n", toDate);
      url = `${API_TICKET}/search-report?${buildQueryParam(data)}`;
      console.log(data, url)
      fetchDataset(url);
    });
  }
  function createChart(canvasId, labels, dataset) {
    const ctx = document.getElementById(canvasId).getContext('2d');
    myChart = new Chart(ctx, {
      data: {
        labels: labels, // ['08h', '09h', '10h']
        datasets: [{
          label: dataset.label || "Không nhãn",
          type: dataset.type || "line",
          data: dataset.data.map(row => row.value),
          borderColor: CHART_CONFIG.colors.lineSet[0],
          barPercentage: CHART_CONFIG.barPercentage,
          pointBackgroundColor: CHART_CONFIG.colors.lineSet[0]
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        layout: {
          padding: CHART_CONFIG.padding.lg
        },
        plugins: {
          legend: {
            display: true,
            position: "bottom",
            align: "center",
            labels: {
              padding: CHART_CONFIG.padding.lg,
              usePointStyle: true,
              pointStyle: 'circle'
            }
          },
          tooltip: {
            enabled: true
          }
        },
        animations: {
          duration: 1000,
          easing: 'easeOutQuart',
          delay: (ctx) => ctx.datasetIndex * 100 + ctx.dataIndex * 50
        },
        scales: {
          x: {
            grid: {
              drawOnChartArea: false,
              drawTicks: true,
              drawBorder: true,
              tickLength: 8
            }
          }
        }
      }
    });
    return myChart;
  }

  function getDateRangeFromOption(optionValue) {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate()); // reset về 00:00:00

    let fromDate = new Date(today);
    let toDate = new Date(today);

    const setEndOfDay = (date) => {
      return new Date(date.getFullYear(), date.getMonth(), date.getDate(), 23, 59, 59, 999);
    };

    const getMonday = (date) => {
      const d = new Date(date);
      const day = d.getDay();
      const diff = d.getDate() - (day === 0 ? 6 : day - 1); // T2 = 1, CN = 0
      return new Date(d.setDate(diff));
    };

    switch (parseInt(optionValue)) {
      case 1: // Hôm nay
        fromDate = new Date(today); // 00:00:00
        toDate = setEndOfDay(today); // 23:59:59
        break;

      case 2: // Tuần này (T2 đến hôm nay)
        fromDate = getMonday(today);
        toDate = setEndOfDay(today);
        break;

      case 3: // Tuần trước (T2 đến CN tuần trước)
        {
          const lastWeekMonday = getMonday(today);
          lastWeekMonday.setDate(lastWeekMonday.getDate() - 7);
          fromDate = new Date(lastWeekMonday);
          toDate = new Date(fromDate);
          toDate.setDate(fromDate.getDate() + 6);
          toDate = setEndOfDay(toDate);
        }
        break;

      case 4: // 4 tuần trước (T2 cách đây 4 tuần đến CN tuần đó)
        {
          const monday = getMonday(today);
          monday.setDate(monday.getDate() - 28);
          fromDate = new Date(monday);
          toDate = new Date(fromDate);
          toDate.setDate(fromDate.getDate() + 27);
          toDate = setEndOfDay(toDate);
        }
        break;

      case 5: // Tháng này
        fromDate = new Date(today.getFullYear(), today.getMonth(), 1);
        toDate = new Date(today.getFullYear(), today.getMonth() + 1, 0);
        toDate = setEndOfDay(toDate);
        break;

      case 6: // 1 tháng gần nhất (dựa trên tháng hiện tại -1)
        {
          const prevMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
          fromDate = new Date(prevMonth);
          toDate = new Date(prevMonth.getFullYear(), prevMonth.getMonth() + 1, 0);
          toDate = setEndOfDay(toDate);
        }
        break;

      case 7: // 3 tháng gần nhất
        {
          const from = new Date(today.getFullYear(), today.getMonth() - 3, 1);
          fromDate = new Date(from);
          toDate = new Date(today.getFullYear(), today.getMonth(), 0); // cuối tháng trước
          toDate = setEndOfDay(toDate);
        }
        break;

      default:
        console.warn("Không xác định kỳ thời gian.");
        fromDate = new Date(today);
        toDate = setEndOfDay(today);
        break;
    }
    return [fromDate, toDate];
  }
  function groupByHourMetric(dataList, metricType = 1) {
    const counts = new Array(24).fill(0);

    // Đếm số lượng theo giờ
    dataList.forEach(item => {
      const hour = new Date(item.createdAt).getHours(); // local time
      counts[hour]++;
    });

    console.log("dataList total: ", dataList.length);

    const total = counts.reduce((sum, val) => sum + val, 0);

    // Tạo array kết quả
    return counts.map((count, hour) => ({
      hour: hour,
      value: metricType === 2
        ? total === 0 ? 0 : Math.round((count / total) * 10000) / 100 // phần trăm, làm tròn 2 chữ số
        : count
    }));
  }
  function fetchDataset(url) {
    //fetch dataz1
    xhr = createXHR();
    xhr.open("GET", url)
    handleResponse(xhr, function (response) {
      //build data
      const dataset = buildDataset(response.data.content, label = "", type = "line");
      console.log("total data: ", response.data.content.length);
      addDataset(ticketVolumeHourlyChart, dataset)

      //refreshChartMetrics
      const avgTickerPerHour = document.getElementById("avgTicketPerHour");
      const maxTickerPerHour = document.getElementById("maxTicketPerHour");
      const minTickerPerHour = document.getElementById("minTicketPerHour");

      arr = new Array(24).fill(0);
      response.data.content.map((ticket) => {
        createdAt = new Date(ticket.createdAt);
        index = createdAt.getHours();
        arr[index] += 1;
      })
      avg = arr.reduce((a, b) => a + b) / 24
      max = arr.reduce((a, b) => Math.max(a, b), arr[0])
      min = arr.reduce((a, b) => Math.min(a, b), arr[0])
      console.log("avg, max, min: ", avg, max, min);
      console.log(arr);

      animateCount(avgTickerPerHour, avg.toFixed(2), 500, 2);
      animateCount(maxTickerPerHour, max, 500, 0);
      animateCount(minTickerPerHour, min, 500, 0);


    })
    xhr.send();
  }
  function buildDataset(rawData, type = "line") {
    const metricType = document.querySelector(".metric-type select").value;
    const data = groupByHourMetric(rawData, parseInt(metricType));
    const select = document.querySelector(".date-range select");
    const label = select.options[select.selectedIndex].text;
    dataset = {};
    dataset.data = data
    dataset.type = document.querySelector(".chart-type select").value
    dataset.label = label
    console.log(dataset);
    return dataset;
  }

  function buildReportUrl(params) {
    return `${API_TICKET}/search?${buildQueryParam(params)}`;
  }
  function addDataset(chart, dataset) {
    if (chart.data.datasets.length >= CHART_CONFIG.MAX_DATASETS) {
      errorToast("Chỉ được so sánh tối đa 4 kỳ!");
      return;
    }

    //append to chart.js
    const index = chart.data.datasets.length;
    const isLine = dataset.line === 'line';
    chart.data.datasets.push({
      label: dataset.label,
      type: dataset.type || "bar",
      data: dataset.data.map(row => row.value),
      backgroundColor: isLine ? 'transparent' : CHART_CONFIG.colors.barSet[index % CHART_CONFIG.colors.barSet.length],
      hoverBackgroundColor: isLine ? 'transparent' : CHART_CONFIG.colors.hoverBarSet[index % CHART_CONFIG.colors.barSet.length],
      borderColor: isLine ? CHART_CONFIG.colors.lineSet[index % CHART_CONFIG.colors.lineSet.length] : undefined,
      borderWidth: isLine ? 2 : undefined,
      fill: false,
      tension: 0.3,
      pointBackgroundColor: isLine ? CHART_CONFIG.colors.lineSet[index % CHART_CONFIG.colors.lineSet.length] : undefined,
      barPercentage: isLine ? undefined : CHART_CONFIG.barPercentage,
    });

    chart.update();

    //add remove button
    const ul = document.querySelector("#datasets .dropdown-menu");
    const li = document.createElement("li");
    li.innerHTML = `
                <a class="dropdown-item d-flex align-items-center" href="#">${dataset.label} <i class="ms-auto bi bi-trash3"></i> </a>
            `
    ul.appendChild(li)
    //add event remove
    li.querySelector("i").addEventListener("click", function () {
      console.log("hello ", dataset.label)
      removeDataset(dataset.label);
      ul.removeChild(li);
    });

    //TODO: add event create dropdown
    li.querySelector("a").addEventListener("click", openOptionModal)
  }
  function removeDataset(label) {
    if (!myChart) return;

    const index = myChart.data.datasets.findIndex(ds => ds.label === label);
    if (index !== -1) {
      myChart.data.datasets.splice(index, 1);
      myChart.update();
    }
  }
}

function animateCount(element, target, duration, decimals = 0) {
  const start = performance.now();
  function update(currentTime) {
    const elapsed = currentTime - start;
    const progress = Math.min(elapsed / duration, 1);
    const current = (target * progress).toFixed(decimals);
    element.textContent = current;
    if (progress < 1) {
      requestAnimationFrame(update);
    }
  }
  requestAnimationFrame(update);
}

function createXHR() {
  return new XMLHttpRequest();
}

function handleResponse(xhr, callback, errorCallback) {
  xhr.onreadystatechange = function () {
    if (this.readyState == 4) {
      const contentType = xhr.getResponseHeader("Content-Type");
      const isJson = contentType && contentType.includes("application/json");

      let response;
      try {
        response = isJson ? JSON.parse(xhr.responseText) : xhr.response;
      } catch (err) {
        if (errorCallback) return errorCallback({ message: "Lỗi phân tích JSON", error: err });
        return errorToast("Lỗi phân tích phản hồi từ server");
      }

      if (this.status == 200) {
        callback(response);
      } else {
        if (errorCallback != null) {
          errorCallback(response);
        } else {
          errorToast(response.message);
        }
      }
    }
  }
}

function openAPIxhr(method, url, callback, errorCallback = null, data = null, headers = {}) {
  xhr = createXHR();
  xhr.open(method, url);
  console.log(url);
  console.log(data);
  //merge GLOBAL_API_HEADERS and headers together
  const allHeaders = { ...GLOBAL_API_HEADERS, ...headers };

  for (const [key, value] of Object.entries(allHeaders)) {
    xhr.setRequestHeader(key, value);
  }

  //add callback handler
  handleResponse(xhr, callback, errorCallback);

  //send request
  if (data != null) {
    xhr.send(JSON.stringify(data))
  } else {
    xhr.send();
  }
}
