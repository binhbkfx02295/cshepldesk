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
const API_REPORT = `${BASE}/api/report`
const USERS = []
const CATEGORIES = []
const PROGRESS_STATUS = [];
const EMOTIONS = [];
const SATISFACTIONS = [];
const USERGROUPS = []
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
var customerViewDetailModal = null;
var ticketVolumeHourlyChart = null;
var appendDatasetCallback = null;
var charts = {};
var cache = {};
var chatbox;

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
    mainColors: ['#4e73df89', '#ff0000b0', '#36b9cc', '#f6c23e'], // dùng làm borderColor cho line, backgroundColor cho bar
    hoverColors: ['#2e59d9', '#ff0000', '#2c9faf', '#f4b619'], // dùng hover cho cả line và bar nếu cần
  },
  fontSize: {
    axis: 14,
    title: 18
  },
  barPercentage: 1,
  MAX_DATASETS: 4
};

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
    initPerformance()
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(4).classList.add("active");
  } else if (window.location.href.includes("report")) {
    initReport();
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(5).classList.add("active");
  } else if (window.location.href.includes("setting")) {
    //check coi co data table khong
    initSetting();
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(6).classList.add("active");
  } else if (window.location.href.includes("user-group")) {
    $(".sidebar-menu li").removeClass("active");
    $(".sidebar-menu li").get(7).classList.add("active");
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
      console.log("Kết quả trả về danh sách employee:", response);
      showLoadingElement(employeeList);
      populateData(response.data, employeeList, renderDashboardEmployeeItem);
      // Update employee count
      countElem.innerText = response.data.length;

      //add event interval
      bindDashboardEmployeeItem();
    }
    openAPIxhr(HTTP_GET_METHOD, url, callback);

  }

}

function bindDashboardEmployeeItem() {
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

function renderDashboardEmployeeItem(employee) {
  console.log("..rendering items");
  const tr = document.createElement("tr");
  tr.classList.add("show");
  tr.setAttribute("data-username", employee.username);
  tr.innerHTML = `
            <td>${sanitizeText(employee.name)}</td>
            <td>${sanitizeText(employee.userGroup.name)}</td>
            <td class="ticket-count">${employee.ticketCount || 0}</td>
            <td style="text-transform: capitalize;">
              <span class="status-indicator ${employee.statusLog.status.name}"></span>
              <span class="status-text">${sanitizeText(employee.statusLog.status.name)}</span>
            </td>
            ${employee.statusLog.status == "offline" ? "" : `<td class="time-elapse" data-timestamp="${employee.statusLog.from}">${startElapsedTimer(employee.statusLog.from)}</td>`}
        `;
  console.log(tr);
  return tr;
}
function populateData(data, container, renderItemCallback) {
  container.innerHTML = "";
  const fragment = document.createDocumentFragment();
  if (data.length > 0) {
    data.forEach((item) => {
      fragment.append(renderItemCallback(item));
    })
  } else {
    fragment.append(renderNoResultElement());
  }

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

  openAPIxhr(HTTP_GET_METHOD, `${API_EMPLOYEE}/me`, function (response) {
    window.me = response.data;
  })

}

function refreshTodayTicketMetrics() {
  console.log("init ticket metrics")
}
//customer/html
function initCustomer() {
  console.log("init customer page");
  const tables = document.querySelectorAll(".data-table");
  tables.forEach(table => initSortingByIndex(table))
  initCustomerDeleteModal();
  initCustomerExport();
  initCustomerViewModal();
  bindItemClickEvents();
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
        //get array of ids
        const checkboxes = document.querySelectorAll("input[type=checkbox]");
        arr = []
        checkboxes.forEach(checkbox => {
          if (checkbox != confirmBtn) {
            value = checkbox.closest(".row[data-id]")?.getAttribute("data-id");
            if (checkbox.checked == true && value != "" && value != null) {
              arr.push(value);
            }
          }
        })
        console.log("arr ", arr);
        openAPIxhr(HTTP_DELETE_METHOD, `${API_FACEBOOK_USER}/delete-all`, function () {
          window.customerDeleteModal.hide();
          setTimeout(function () {
            checkboxes.forEach(checkbox => {
              checkbox.checked = false;
              checkbox.dispatchEvent(new Event("change", { bubbles: true }));
            })
            performSearchCustomer(page = 0, size = $("#pageSize").val());
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

  function performSearchCustomer(page = 0, size = 10) {
    data = getCustomerSearchData();
    data.page = page;
    data.size = size;
    console.log(data)
    const container = document.getElementById("customer-list-body");
    const loading = $($("#loading-result"));
    const _url = `${API_FACEBOOK_USER}/search?${buildQueryParam(data)}`;
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
    console.log(response.data);
    if (data.content != 0) {
      renderCustomerDetail(data.content, container);

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
        <div class="col facebookId" title="${sanitizeText(item.facebookId)}>${item.facebookId || "- -"}</div>
        <div class="col facebookProfile">
          <img class="avt" src="${item.facebookProfilePic || "- -"}">
          ${item.facebookName || "- -"}</div>
        <div class="col" title="${sanitizeText(item.facebookName)}">${sanitizeText(item.facebookName) || "- -"}</div>
        <div class="col" title="${sanitizeText(item.phone)}">${sanitizeText(item.phone) || "- -"}</div>
        <div class="col" title="${sanitizeText(item.email)}">${sanitizeText(item.email) || "- -"}</div>
        <div class="col" title="${sanitizeText(item.zalo)}">${sanitizeText(item.zalo) || "- -"}</div>
        <div class="col options overflow-visible">
            <div class="customer-dropdown">
                <i class="bi bi-three-dots-vertical"></i>
                <ul class="dropdown-menu" style="right: 0">
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
    const deleteButton = $("#delete-checkbox");
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
  //check coi co data table khong
  const dataTable = document.querySelectorAll(".data-table");
  if (dataTable.length > 0) {
    dataTable.forEach(table => initSortingByIndex(table));
  }
  initTicketCreate();
  initTicketDetailModal();
}


function initHeader() {
  //side-bar controller
  const zz = document.querySelectorAll(".sidebar-heading .wrapper");
  zz.forEach(item => {
    item.addEventListener("click", function () {
      console.log(this);
      const inone = this.querySelector("i:not(.d-none)");
      const idisplay = this.querySelector("i.d-none");
      inone.classList.add("d-none");
      idisplay.classList.remove("d-none");
      if (!this.classList.contains("show")) {
        this.classList.add("show");

        this.parentElement.querySelector("ul").classList.remove("d-none");
      } else {
        this.classList.remove("show");
        this.parentElement.querySelector("ul").classList.add("d-none");
      }
    })
  })


  // connect websocket
  fetch(`${API_EMPLOYEE}/me`)
    .then(response => response.json())
    .then(data => initWeksocketConnection(data))
  //init current date
  const now = new Date()
  $("#currentDate").text(formatDate(now))
  $("#lastUpdated").text(formatTime(now))


  // fetch online status
  const statusIndicator = document.querySelector('.status-dropdown .status-indicator');
  const statusText = document.querySelector('.status-dropdown #currentStatusText');
  const url = `${API_EMPLOYEE}/me/online-status`;
  console.log(url);
  const callback = function (response) {
    console.log(response);
    statusIndicator.classList.add(response.data.status.name);
    statusText.innerHTML = toCapital(sanitizeText(response.data.status.name))
  }
  openAPIxhr(HTTP_GET_METHOD, url, callback);

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

  const toggleBtn = document.getElementById("showSidebar");
  const pageContent = document.querySelector(".page-content");
  const sidebar = document.getElementById("sidebar");

  toggleBtn.addEventListener("click", function () {
    if (sidebar.classList.contains("show")) {
      sidebar.classList.remove("show");
      pageContent.style.marginLeft = "0px";
    } else {
      sidebar.classList.add("show");
      pageContent.style.marginLeft = "230px";
    }
  })



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
    const statusId = $(this).data("status-id");
    const statusValue = statusText.toLowerCase();
    var $indicator = $('.status-dropdown .status-indicator');
    var $statusText = $('.status-dropdown #currentStatusText');
    // Gửi request đến backend để cập nhật trạng thái
    $.ajax({
      url: `${API_EMPLOYEE}/me/online-status`,
      method: 'PUT',
      contentType: 'application/json',
      data: JSON.stringify({ status: { id: statusId }, from: new Date().getTime() }),
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

function initWeksocketConnection(data) {
  const me = data.data;
  console.log(me);
  const socket = new SockJS('/ws');
  const stompClient = new StompJs.Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    onConnect: (frame) => {
      console.log(frame) // debug
      // Đăng ký nhận tin nhắn trên topic /queue/messages
      if (me.authorities.some(auth => auth.authority === "ROLE_SUPERVISOR")) {
        subscribeForSupervisor(stompClient);
      } else {
        subscribeForStaff(stompClient);
      }
    },
    onStompError: (frame) => console.error('STOMP error:', frame.headers['message'])
  });

  stompClient.activate();
}

function subscribeForSupervisor(stompClient) {
  stompClient.subscribe('/topic/admin/tickets', handleWSTicket) //cho today's ticket+today's staff
  stompClient.subscribe('/topic/admin/messages', handleWSMessage) // cho today's ticket
  stompClient.subscribe('/topic/admin/employees', handleWSEmployee); //cho today staff
}

function subscribeForStaff(stompClient) {
  stompClient.subscribe('/user/queue/tickets', handleWSTicket) //cho today's ticket
  stompClient.subscribe('/user/queue/messages', handleWSMessage) //cho today's ticket
  stompClient.subscribe('/topic/admin/employees', handleWSEmployee); //cho today staff
}

function handleWSTicket(response) {
  response = JSON.parse(response.body);
  let ticket = response.data;

  if (window.location.href.includes("today-ticket")) {
    const container = document.getElementById("ticketList");
    if (container != null) {
      const item = renderDashboardTicketItem(ticket);
      switch (response.action) {
        case "CREATED":
          playNewTicketNotificationSound();
          showPopupNotification("Có ticket mới! ID: ", ticket.id);
          container.firstChild != null
            ? container.insertBefore(item, container.firstChild)
            : container.appendChild(item);
          break;
        case "UPDATED":
          container.replaceChild(item, container.querySelector(`*[data-ticket-id="${ticket.id}"]`));
          break;
        case "ASSIGNED":
          if (ticket.assignee != null && ticket.assignee.username != me.username) {
            let ticketElem = container.querySelector(`*[data-ticket-id="${ticket.id}"]`);
            container.removeChild(ticketElem);
          }
          break;
        case "CLOSED":
          const target = container.querySelector(`.progress-status.resolved`)?.closest(".item");
          let oldItem = container.querySelector(`*[data-ticket-id="${ticket.id}"]`);
          container.removeChild(oldItem);
          if (target != null) {
            container.insertBefore(item, target);
            //xóa luôn "Có tin nhắn" nếu đang hiển thị
            const s = container.querySelector(".new-message");
            if (s != null && !s.classList.contains("d-none")) {
              s.classList.add("d-none");
            }

          } else {
            container.appendChild(item);
          }
          break;
      }
      //rebind interval
      bindDashboardTicketItem();
    }

  }

  if (window.location.href.includes("today-staff")) {
    const container = document.getElementById("employeeList2");
    const employeeElem = container.querySelector(`*[data-username="${ticket.assignee.username}"]`)
    if (employeeElem != null) {
      const td = employeeElem.querySelector(".ticket-count");
      if (response.action == "CREATED") {

        td.innerText = parseInt(td.innerText.trim()) + 1;
      } else if (response.action == "CLOSED") {
        td.innerText = parseInt(td.innerText.trim()) - 1;
      }
    }

  }
}




function handleWSMessage(response) {
  response = JSON.parse(response.body);
  console.log("..handling websocket message", response)
  let message = response.data;
  if (window.location.href.includes("today-ticket")) {
    const ticketId = message.ticket.id;
    const ticketElem = document.querySelector(`*[data-ticket-id="${ticketId}"]`);
    //TODO:
    //1. check message.senderEmployee: neu la false, render "Có tin nhắn" + bật âm thanh
    if (message.senderEmployee == false || message.senderSystem == true) {
      ticketElem.querySelector(".new-message").classList.remove("d-none");
      playNewMessageNotificationSound()
    } else {
      //2. Nếu không phải, tắt thông báo "Có tin nhắn" (vì staff đã đọc và đã trả lời)
      const classList = ticketElem.querySelector(".new-message").classList;
      if (classList.contains("d-none") == false) {
        classList.add("d-none");
      }
    }

    //TODO: nếu detail modal đang mở, thì add tin nhắn vào đó, tự scroll xuống
    handleIfModalOpen(message);


  }
}

function handleIfModalOpen(message) {
  if (window.fullModal._isShown == true) {
    //try to append this message
    const messageElement = renderMessageItem(message);
    if (chatbox == null) {
      chatbox = document.querySelector("#chatBox");
    }
    chatbox.querySelector("#messageList").append(messageElement);
    scrollToBottomMessageList(chatbox.querySelector("#messageList"));
  }
}

function handleWSEmployee(response) {
  response = JSON.parse(response.body);
  console.log("..handling websocket employee", response);
  let employee = response.data;
  if (window.location.href.includes("today-staff")) {
    //1. Tìm row item chứa thằng employee
    console.log(employee);
    const container = document.getElementById("employeeList2");
    const statusLog = employee.statusLogs[0];
    console.log("This is container", container);
    const tr = container.querySelector(`*[data-username=${employee.username}]`);
    console.log("This is tr", tr);
    const statusIndicator = tr.querySelector(`.status-indicator`);
    const statusText = tr.querySelector(`.status-text`);
    //2. sửa cột status log;
    const oldStatus = statusIndicator.classList[1]
    statusIndicator.classList.remove(oldStatus);
    statusIndicator.classList.add(statusLog.status.name);
    statusText.innerText = toCapital(statusLog.status.name);

    //3. set timer cua no ve 00:00:00
    const dataTimestamp = tr.querySelector("*[data-timestamp]");
    dataTimestamp.setAttribute("data-timestamp", statusLog.from);
    //4. unbind rồi bind lại timer;
    bindDashboardEmployeeItem();

  }
}

// Hàm popup thông báo nhỏ
function showPopupNotification(title, content) {
  const popup = document.createElement('div');
  popup.className = 'ws-popup-notification';
  popup.innerHTML = `<strong>${title}</strong><div>${content}</div>`;
  Object.assign(popup.style, {
    position: 'fixed',
    right: '20px',
    bottom: '20px',
    background: '#333',
    color: '#fff',
    padding: '16px 24px',
    borderRadius: '12px',
    boxShadow: '0 4px 24px #0008',
    zIndex: 9999,
    opacity: 0.95,
    fontSize: '1rem',
    pointerEvents: 'none',
    transition: 'all 0.4s'
  });
  document.body.appendChild(popup);
  setTimeout(() => {
    popup.style.opacity = 0;
    setTimeout(() => popup.remove(), 500);
  }, 3000);
}

// Hàm phát âm thanh
function playNewMessageNotificationSound() {
  // Dùng file mp3 ở public hoặc url bất kỳ
  const audio = new Audio('/audio/new-message.wav');
  audio.play().catch(e => {
    console.log("Lỗi không thể phát âm thanh \"Có tin nhắn mới\"", e)
  });
}

// Hàm phát âm thanh
function playNewTicketNotificationSound() {
  // Dùng file mp3 ở public hoặc url bất kỳ
  const audio = new Audio('/audio/new-ticket.wav');
  audio.play().catch(e => {
    console.log("Lỗi không thể phát âm thanh \"Có Ticket mới\"", e)
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
    showLoadingElement(container);
    populateData(data, container, renderDashboardTicketItem)

    //after render tickets, start  event
    bindDashboardTicketItem()

    //refreshing ticket metrics;
    refreshDashboardTicketMetrics(response.data);

    // Restore refresh button
    refreshBtn.html(originalContent)
    refreshBtn.prop("disabled", false)
  }

  //call API
  openAPIxhr(HTTP_GET_METHOD, url, callback);

}
function bindDashboardTicketItem() {
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



    // 1. Đầu tiên nếu chỉ A là 3 → A xuống dưới
    if (isA3 && !isB3) return 1;

    // 2. Nếu chỉ B là 3 → B xuống dưới
    if (!isA3 && isB3) return -1;

    //3. Nếu A là Has new Mesage
    if (a.hasNewMessage && !b.hasNewMessage) return -1;

    //4. New B là Has new Message
    if (!a.hasNewMessage && b.hasNewMessage) return 1;

    // Nếu cả hai đều != 3 → sort theo createdAt DESC
    return new Date(b.createdAt) - new Date(a.createdAt);
  })
  return data;
}

//Populate Dashboard tick
function renderDashboardTicketItem(ticket) {
  if (ticket == null) return null;
  const div = document.createElement("div");
  div.innerHTML = `
        <div class="item mb-2" data-ticket-id="${ticket.id}">
            <div class="d-flex flex-row">
                <div class=" w-100 d-flex flex-column me-2">
                    <div class="messages mb-1"></div>
                    <div class="title mb-1">
                        <span class="ticket-id me-2">#${ticket.id}</span> - ${ticket.title || "Chưa có tiêu đề"}
                        <span class="ms-2 text-white new-message bg-danger rounded br-sm py-1 px-2 ${ticket.hasNewMessage ? "" : "d-none"}" style="
                              font-size: 13px;
                          "> Có tin nhắn</span>
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
                      <i class="bi bi-hourglass me-2"></i><span class="duration ${ticket.progressStatus.id != 3 ? "time-elapse" : ""}" data-timestamp=${ticket.createdAt}>${ticket.progressStatus.id != 3 ? startElapsedTimer(ticket.createdAt) : "- -"}</span>
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
  $("#editNote").val(ticket.description || "");


  $("#editCategory").attr("data-category-id", ticket.category?.id || null);
  $("#editProgressStatus").attr("data-progress-id", ticket.progressStatus.id || null);
  $("#editEmotion").attr("data-emotion-id", ticket.emotion?.id || null);
  $("#editSatisfaction").attr("data-satisfaction-id", ticket.satisfaction?.id || null);
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

function formatTimestampToDate(timestamp) {
  let date = new Date(timestamp);
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

function startElapsedTimer(startTimestamp) {
  let ms = Date.now() - startTimestamp;
  ms = ms < 0 ? 0 : ms;

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
        "category-id",
        "name",
        "id"
      );
    } else if (container.attr("class").includes("progress-status")) {
      loadDropdownField(
        ul,
        input,
        loadProgressStatus,
        PROGRESS_STATUS,
        "progress-id",
        "name",
        "id"
      );
    } else if (container.attr("class").includes("emotion")) {
      loadDropdownField(
        ul,
        input,
        loadEmotions,
        EMOTIONS,
        "emotion-id",
        "name",
        "id"
      );
    } else if (container.attr("class").includes("satisfaction")) {
      loadDropdownField(
        ul,
        input,
        loadSatisfaction,
        SATISFACTIONS,
        "satisfaction-id",
        "name",
        "id"
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
    $("#editDescription").val(originalTicketData.description);

    disableEditButtons();
  });

  // Save edit
  $("#saveEdit").click(function () {
    const category = $("#editCategory").attr("data-category-id") ?
      { id: $("#editCategory").attr("data-category-id") } : null

    const progressStatus = $("#editProgressStatus").attr("data-progress-id") ?
      { id: $("#editProgressStatus").attr("data-progress-id") } : null

    const ticketData = {
      title: $("#editTitle").val(),
      category: category,
      progressStatus: progressStatus,
    };
    updateTicketData(ticketData);
  });
}

function updateTicketData(ticketData) {
  const url = `${API_TICKET}/${$("#editTicketId").val()}`;
  const callback = function (response) {
    console.log("update ticket thahnf công, response nè:", response);
    successToast(response.message);
    loadTicketDetail($("#editTicketId").val())

  }
  disableEditButtons();
  openAPIxhr(HTTP_PUT_METHOD, url, callback, null, ticketData);
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
        const dayOfWeek = today.getDay();

        const diffToMonday = (dayOfWeek + 6) % 7;
        from.setDate(today.getDate() - diffToMonday);
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
    }
  });

  // Khởi tạo lại tooltip (nếu dùng Bootstrap tooltip)
  $('[data-bs-toggle="tooltip"]').tooltip?.();
}
function loadTicketSearch(page = null, pageSize = null) {

  const container = document.getElementById("ticket-list-body");
  const data = getTicketSearchData(page, pageSize);
  const url = `${API_TICKET}/search?${buildQueryParam(data)}`;
  showLoadingElement(container);
  callback = function (response) {
    //TODO: populate list
    populateData(response.data.content, container, renderTicketSearchItem);
    renderPagination(response.data.page,
      response.data.totalElements,
      response.data.size,
      performTicketSearch);
    successToast(response.message);
  }

  // call API search
  openAPIxhr(HTTP_GET_METHOD, url, callback);

}

function renderNoResultElement() {
  const div = document.createElement("div");
  div.innerHTML =
    `
<div id="no-ticket-result" class="text-center text-muted py-3" style="display: block;">
        <i class="bi bi-inbox me-1"></i> Không có kết quả phù hợp.
      </div>
  `
  return div.firstElementChild;
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
    progressStatus: $("#ticket-search #progress-status").attr("data-progress-id") || null,
    fromTime: toTimestampLocal($("#fromDate").val()),
    toTime: toTimestampLocal($("#toDate").val()),
    category: $("#ticket-search #category").attr("data-category-id") || null,
    emotion: $("#ticket-search #emotion").attr("data-emotion-id") || null,
    satisfaction: $("#ticket-search #satisfaction").attr("satisfaction") || null,
    page: page,
    size: size,
    sort: "createdAt,DESC"
  }
  console.log("ticketSearchCriteria ", ticketSearchCriteria);
  return ticketSearchCriteria;
}

function renderTicketSearchItem(ticket) {
  const div = document.createElement("div");
  div.className = "row border-bottom item";
  div.setAttribute("data-ticket-id", ticket.id);
  div.setAttribute("data-facebookId", ticket.facebookUser.id);
  div.innerHTML = `
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
          ${ticket.category?.name || '- -'}
        </div>
        <div class="col text-truncate" title="${formatEpochTimestamp(ticket.createdAt)}" data-timestamp="${ticket.createdAt}">
          ${formatEpochTimestamp(ticket.createdAt)}
        </div>
        <div class="col text-truncate emotion-${ticket.emotion?.code || ''}"
             title="${ticket.emotion?.name || ''}">
          ${ticket.emotion?.name || '- -'}
        </div>
        <div class="col text-truncate satisfaction-${ticket.satisfaction?.code || ''}"
             title="${ticket.satisfaction?.name || ''}">
          ${ticket.satisfaction?.name || '- -'}
        </div>
    `;
  div.addEventListener("click", function () {
    loadTicketDetail($(this).data("ticket-id"));
  })
  return div;
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




function initSortingByIndex(container) {
  console.log("test");
  const headerSelector = ".col.sortable";
  const bodySelector = ".data-body";

  const headers = container.querySelectorAll(headerSelector);
  const body = container.querySelector(bodySelector);

  if (!headers.length || !body) return;

  headers.forEach(col => {
    // Tạo icon nếu chưa có
    if (!col.querySelector(".sort-icons")) {
      const iconWrapper = document.createElement("div");
      iconWrapper.className = "sort-icons d-flex flex-column";
      iconWrapper.style.fontSize = "12px";
      iconWrapper.innerHTML = `
        <i class="bi bi-caret-up-fill arrow-up text-gray-400"></i>
        <i class="bi bi-caret-down-fill arrow-down text-gray-400"></i>
      `;
      col.appendChild(iconWrapper);
    }

    col.addEventListener("click", () => {
      const index = Array.from(col.parentNode.children).indexOf(col);
      let direction = col.getAttribute("data-sort-direction") || "none";

      // Reset tất cả header khác
      headers.forEach(h => {
        h.setAttribute("data-sort-direction", "none");
        const up = h.querySelector(".arrow-up");
        const down = h.querySelector(".arrow-down");
        if (up && down) {
          up.classList.remove("text-dark");
          up.classList.add("text-gray-400");
          down.classList.remove("text-dark");
          down.classList.add("text-gray-400");
        }
      });

      // Toggle hướng
      direction = direction === "asc" ? "desc" : "asc";
      col.setAttribute("data-sort-direction", direction);

      // Highlight arrow đúng hướng
      const up = col.querySelector(".arrow-up");
      const down = col.querySelector(".arrow-down");
      if (up && down) {
        if (direction === "asc") {
          up.classList.replace("text-gray-400", "text-dark");
          down.classList.replace("text-dark", "text-gray-400");
        } else {
          up.classList.replace("text-dark", "text-gray-400");
          down.classList.replace("text-gray-400", "text-dark");
        }
      }

      sortByIndex(body, index, direction);
    });
  });
}





function sortByIndex(container, colIndex, direction) {
  const rows = Array.from(container.querySelectorAll(".row"));

  const sorted = rows.sort((a, b) => {
    //kiếm tra nếu là date thì sort theo date
    const aTimestamp = a.children[colIndex]?.getAttribute("data-timestamp");
    const bTimestamp = b.children[colIndex]?.getAttribute("data-timestamp");
    console.log(aTimestamp, bTimestamp);
    if (aTimestamp != null && bTimestamp != null) {
      console.log("sort timestamp");
      return direction === "asc" ? aTimestamp - bTimestamp : bTimestamp - aTimestamp;
    }

    const aText = a.children[colIndex]?.textContent.trim() || "";
    const bText = b.children[colIndex]?.textContent.trim() || "";

    //Kiểm tra nếu là số thì sort theo số
    const isNumeric = !isNaN(aText) && !isNaN(bText);
    if (isNumeric) {
      return direction === "asc" ? aText - bText : bText - aText;
    }

    //if tra nếu là text thì sort theo text

    return direction === "asc"
      ? aText.localeCompare(bText)
      : bText.localeCompare(aText);
  });


  container.innerHTML = "";
  const fragment = document.createDocumentFragment();
  sorted.forEach(row => fragment.appendChild(row));
  container.innerHTML = "";
  container.appendChild(fragment);
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
      fromTime: toTimestampLocal($("#fromDate").val()),
      toTime: toTimestampLocal($("#toDate").val()),
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

function toTimestampLocal(dateString) {
  return new Date(dateString).getTime();
}
function toTimestampLocal(dateString) {
  let date = new Date(dateString);
  date.getTime() + date.getTimezoneOffset()*60*1000;
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
  const container = document.getElementById("messageList");
  showLoadingElement(container);
  const url = `${API_MESSAGE}?ticketId=${ticketId}`
  const callback = function (response) {
    console.log(response);
    populateData(response.data, container, renderMessageItem);
    scrollToBottomMessageList(container);
  }
  openAPIxhr(HTTP_GET_METHOD, url, callback);
}

function getExtension(filename) {
  const pos = filename.lastIndexOf('.');
  return pos > 0 ? filename.slice(pos + 1).toLowerCase() : '';
}

function renderMessageItem(msg) {
  const div = document.createElement("div");
  const innerDiv = document.createElement("div");
  let attachments = document.createElement("div");
  attachments.className = "message-attachments"
  if (msg.attachments.length != 0) {
    for (let i = 0; i < msg.attachments.length; i++) {
      let attachment = msg.attachments[i];
      let element;
      if (attachment.type == "audio") {
        element = document.createElement("audio");
        element.controls = true;
        const source = document.createElement("source");
        source.src = attachment.url;
        element.appendChild(source);
        element.append("Trình duyệt không hỗ trợ audio.");

      } else if (attachment.type == "image") {
        element = document.createElement("img");
        element.className = "d-inline-block"
        element.src = attachment.url;
        element.style.maxWidth = attachment.stickerId == null ? "120px" : "240px";
        element.style.cursor = "pointer";
        element.addEventListener("click", function () { openImgModal(element) })
      }
      attachments.append(element);
    }
    innerDiv.append(attachments);
  }
  div.innerHTML =
    `<div class="d-flex mb-2 ${msg.senderEmployee ? "justify-content-end" : "justify-content-start"}">
        <div class="chat-bubble ${msg.senderEmployee ? "staff" : "user"}">
          ${innerDiv.innerHTML}
          <div class="message-text">${sanitizeText(msg.text)}</div>
          <div class="message-timestamp text-muted small mt-1" title="Timestamp: ${msg.timestamp}">${formatEpochTimestamp(msg.timestamp)}</div>
        </div>
      </div>`
  return div.firstElementChild;
}

function openImgModal(imgElement) {
  return function () {
    const modalImg = document.getElementById("modal-img");
    modalImg.src = imgElement.src;

    const modal = new bootstrap.Modal(document.getElementById('imgModal'));
    modal.show();
  };
}

// Auto scroll to bottom
function scrollToBottomMessageList(container) {
  if (container) {
    messageList.scrollTop = messageList.scrollHeight;
  }
}



// Load ticket history
function loadTicketHistory(facebookId) {
  const container = document.getElementById("historyList");
  const url = `${API_TICKET}/get-by-facebook-id?id=${facebookId}`;
  const callback = function (response) {
    populateData(response.data, container, renderTicketHistoryItem)
  }
  openAPIxhr(HTTP_GET_METHOD, url, callback);
}

function renderTicketHistoryItem(item) {
  const li = document.createElement("li");
  li.className = "list-group-item d-flex flex-column"
  li.innerHTML =
    `
  <strong>#${item.id}</strong>
  <small>${item.title || "Chưa có tiêu đề"}</small>
  <small>${formatEpochTimestamp(item.createdAt)}</small>
  `
  li.style.cursor = "pointer";
  li.addEventListener("click", function () { loadTicketDetail(item.id) });
  return li;
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
  //init some button
  setDateOptionRanges();
  const tables = document.querySelectorAll(".data-table");
  tables.forEach(table => initSortingByIndex(table))

  const ticketByHourContainer = document.querySelector(".chart-item#ticketByHour")
  const ticketByWeekdayContainer = document.querySelector(".chart-item#ticketByWeekday")
  const ticketByDayContainer = document.querySelector(".chart-item#ticketByDay")
  initChart(ticketByHourContainer, `${API_REPORT}/ticket-by-hour`);
  initChart(ticketByWeekdayContainer, `${API_REPORT}/ticket-by-weekday`);
  initChart(ticketByDayContainer, `${API_REPORT}/ticket-by-day`);



  function initChart(container, api) {
    console.log(container)
    const id = container.id;
    const canvas = container.querySelector("canvas").getContext("2d");
    const chart = createDefaultChart(canvas);
    window.charts[id] = chart;
    initChartController(container, api);
  }

  function initChartController(container, api) {
    // ========= DATE RANGE OPTION ====
    flatpickr(".dateRangeOption", {
      mode: "range",
      dateFormat: "Y-m-d",
      onChange: function (selectedDates, dateStr, instance) {

        if (selectedDates.length === 2) {
          const startTimestamp = selectedDates[0].getTime();
          const endTimestamp = selectedDates[1].getTime();

          console.log("Start:", startTimestamp, "End:", endTimestamp);
          instance.element.setAttribute("data-fromTime", startTimestamp);
          instance.element.setAttribute("data-toTime", endTimestamp);
        }
      }
    });

    // ========== INIT DATE RANGE OPTION =======
    container.querySelectorAll(".dateOption").forEach(option => {
      option.addEventListener("click", function () {
        // ==== styling ===
        container.querySelectorAll(".dateOption").forEach(item => {
          item.classList.remove("active");
        })
        option.classList.add("active");
        // === calling fetch dataset
        params = {
          label: option.getAttribute("data-label"),
          fromTime: option.getAttribute("data-fromTime"),
          toTime: option.getAttribute("data-toTime"),
          type: "bar",
          timezone: Intl.DateTimeFormat().resolvedOptions().timeZone
        }
        fetchMainDataset(container, params, api);
      })
    })


    // ========== ADD DATASET BUTTON =============
    const appendDatasetBtn = container.querySelector(".add-dataset");
    appendDatasetBtn.addEventListener("click", function () {
      createDatasetModal(container, api);
    })

    // ========== FETCH DEFAULT DATASET ==========
    setTimeout(function () {
      // appendDatasetBtn.dispatchEvent(new Event("click"));
      container.querySelector(".dateOption").dispatchEvent(new Event("click"));

    }, 100)


    setTimeout(function () {
      const id = container.id;
      appendDatasetBtn.dispatchEvent(new Event("click"));
      let dataset = container.querySelector(".dataset");
      dataset.querySelector(".chart-type a[data-value=\"line\"]").dispatchEvent(new Event("click"));
      if (id == "ticketByHour") {
        dataset.querySelector(".dataset-name").value = "Hôm nay";
        dataset.querySelector("#dateRangeOption").value = "1";
      } else if (id == "ticketByWeekday") {
        dataset.querySelector(".dataset-name").value = "Tuần này";
        dataset.querySelector("#dateRangeOption").value = "2";
      } else if (id == "ticketByDay") {
        dataset.querySelector(".dataset-name").value = "Tháng này";
        dataset.querySelector("#dateRangeOption").value = "5";
      }
      dataset.querySelector(".confirm-add").dispatchEvent(new Event("click"));
    }, 300)


  }

  function fetchMainDataset(container, params, api) {
    if (params.main == null) {
      params.main = true;
    }
    fetchDataset(container, params, api);
  }
  function setDateOptionRanges() {
    console.log("heer");

    const now = new Date();
    now.setHours(0, 0, 0, 0); // reset về đầu ngày

    document.querySelectorAll('.dateOption').forEach(option => {
      const type = option.getAttribute('data-dateoption');
      let from, to;
      console.log("hello")
      to = new Date(now);

      switch (type) {
        case '1w':
          lastWeekMonday = getMonday(now);
          lastWeekMonday.setDate(lastWeekMonday.getDate() - 7);
          from = new Date(lastWeekMonday);
          to = new Date(from);
          to.setDate(from.getDate() + 6);
          to = setEndOfDay(to);
          break;
        case '4w':
          lastWeekMonday = getMonday(now);
          lastWeekMonday.setDate(lastWeekMonday.getDate() - 7 * 4);
          from = new Date(lastWeekMonday);
          to = new Date(from);
          to.setDate(from.getDate() + 7 * 4 - 1);
          to = setEndOfDay(to);
          break;
        case '12w':
          lastWeekMonday = getMonday(now);
          lastWeekMonday.setDate(lastWeekMonday.getDate() - 7 * 12);
          from = new Date(lastWeekMonday);
          to = new Date(from);
          to.setDate(from.getDate() + 7 * 12 - 1);
          to = setEndOfDay(to);
          break;
        case '1m':
          to.setDate(to.getDate() - to.getDate());
          from = new Date(to);
          from.setDate(from.getDate() - from.getDate() + 1)
          to.setHours(23, 59, 59);
          break;
        case '3m':
          to.setDate(to.getDate() - to.getDate());
          from = new Date(to);
          for (let i = 0; i < 3; i++) {
            if (i == 2) {
              from.setDate(from.getDate() - from.getDate() + 1);
            } else {
              from.setDate(from.getDate() - from.getDate());
            }
          }
          to.setHours(23, 59, 59);
          break;
        case '6m':
          to.setDate(to.getDate() - to.getDate());
          from = new Date(to);
          for (let i = 0; i < 6; i++) {
            if (i == 5) {
              from.setDate(from.getDate() - from.getDate() + 1);
            } else {
              from.setDate(from.getDate() - from.getDate());
            }
          }
          to.setHours(23, 59, 59);
          break;
        default:
          return;
      }

      // set lại attribute: lưu dạng timestamp ms
      option.setAttribute('data-fromtime', from.getTime());
      option.setAttribute('data-totime', to.getTime());
    });
  }


  function createDatasetModal(container, api) {
    console.log("..creating dataset modal..");
    const template = document.querySelector(".template-add-dataset");
    let datasetModal = template.content.cloneNode(true).children[0];
    datasetModal.setAttribute("data-draft", true);
    container.querySelector(".datasets").append(datasetModal);
    enableMovable(datasetModal);
    const chart = window.charts[container.id];

    //bind confirm add dataset;
    datasetModal.querySelector(".confirm-add").addEventListener("click", function () {
      const params = getDatasetProperty(datasetModal);
      const url = `${api}?${buildQueryParam(params)}`
      console.log("...calling report api", url);
      callback = function (response) {
        console.log(response);
        //populate datasets
        const dataset = response.data.dataset;

        if (datasetModal.getAttribute("data-draft") == "true") {
          //set nonce when first call
          const nonce = crypto.randomUUID();
          datasetModal.setAttribute("data-nonce", nonce);
          dataset.nonce = nonce;

          const result = appendDataset(chart, dataset);
          if (!result) return;
          renderDatasetController(datasetModal, params.label);
          datasetModal.removeAttribute("data-draft");
        } else {
          //update dataset
          //update controller
          const nonce = datasetModal.getAttribute("data-nonce");
          dataset.nonce = nonce;
          datasetModal.closest(".chart-item").querySelector(`ul li[data-nonce="${nonce}"] a`).innerText = dataset.label;

          updateDataset(chart, dataset);
        }
        datasetModal.classList.add("d-none");
      }
      openAPIxhr(HTTP_GET_METHOD, url, callback);
    });

    //bind choosing chart-type
    bindDatasetChartTypeBtn(datasetModal);
    //bind canceling-datasetModal
    bindCandelDatasetModal(datasetModal)

    return datasetModal;

  }

  function bindDatasetChartTypeBtn(datasetModal) {
    datasetModal.querySelectorAll(".chart-type ul a").forEach(function (item) {
      item.addEventListener("click", function () {
        const child = item.firstElementChild;
        const target = item.closest(".dropdown").firstElementChild;
        target.replaceChildren(child.cloneNode(true));
        target.closest(".chart-type").setAttribute("data-value", item.getAttribute("data-value"));
      })
    })
  }
  function bindCandelDatasetModal(datasetModal) {
    datasetModal.querySelector(".cancel-add").addEventListener("click", function () {
      if (datasetModal.getAttribute("data-draft") == true) {
        datasetModal.parentElement.removeChild(datasetModal);
      } else {
        datasetModal.classList.add("d-none");
      }

    });
  }


  function getDatasetProperty(datasetModal) {
    const label = datasetModal.querySelector("input").value;
    const type = datasetModal.querySelector(".chart-type").getAttribute("data-value");
    const dateRange = datasetModal.querySelector("select").value;
    const [fromTime, toTime] = getDateRangeFromOption(dateRange);
    return {
      label: label || "Không có tiêu đề",
      type: type || "bar",
      fromTime: fromTime.getTime(),
      toTime: toTime.getTime(),
      timezone: Intl.DateTimeFormat().resolvedOptions().timeZone
    }

  }
  function getDateRangeFromOption(optionValue) {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate()); // reset về 00:00:00

    let fromDate = new Date(today);
    let toDate = new Date(today);



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

  function renderDatasetController(datasetModal, datasetName) {
    const id = datasetModal.closest(".chart-item").id;
    const chart = charts[id]
    const ul = datasetModal.parentElement.firstElementChild.querySelector("ul");
    const li = document.createElement("li");
    const nonce = datasetModal.getAttribute("data-nonce");
    li.setAttribute("data-nonce", nonce);
    li.className = "d-flex flex-row align-items-center"
    li.innerHTML = `
                        <a class="dropdown-item d-flex align-items-center" href="#">${datasetName || "Không tên"} </a>
                        <i class="me-2 bi bi-trash3"></i>
                    `
    //bind the a to reopen this datasetModal
    li.querySelector("a").addEventListener("click", function () {
      //repoen this datasetModal
      datasetModal.classList.remove("d-none");
    })


    //add event remove
    li.querySelector("i").addEventListener("click", function () {
      datasetModal.parentElement.removeChild(datasetModal); //removing datasets from chart
      removeDataset(chart, nonce); //completely delete this modal
      ul.removeChild(li); //remove button from controller

    });

    //add to ul
    ul.appendChild(li)
  }

}

function fetchDataset(container, params, api) {
  //fetch dataz1
  const url = `${api}?${buildQueryParam(params)}`
  const chart = charts[container.id];
  const metricContainer = container.querySelector(".chart-metrics");
  const tableContainer = container.querySelector(".chart-tabular-data");
  const callback = function (response) {
    console.log(response);
    if (params.main == true) {
      //update or add new?
      populateDataset(chart, response.data.dataset);
      populateMetrics(metricContainer, response.data.summary);
      populateTabularData(tableContainer, response.data.tabularData);
    } else {
      appendDataset(chart, response.data.dataset);
    }
  }

  openAPIxhr(HTTP_GET_METHOD, url, callback)
}
function populateDataset(chart, dataset) {
  console.log("populate dataset", dataset);
  if (chart.data.datasets.length > 0) {
    chart.data.datasets.forEach(ds => {
      if (ds.main == true) {
        //replace
        let index = chart.data.datasets.indexOf(ds);
        chart.data.datasets.splice(index, 1, formatDataset(dataset, 0));
      }
    })
  } else {
    chart.data.labels = dataset.labels;
    chart.data.datasets.push(formatDataset(dataset, 0));
  }
  chart.update()
}

function populateMetrics(container, metrics) {
  let avg = metrics.avg;
  let max = metrics.max.value;
  let min = metrics.min.value;

  animateCount(container.querySelector(".avgTicketPerHour"), avg, 500, 2);
  animateCount(container.querySelector(".maxTicketPerHour"), max, 500, 2);
  animateCount(container.querySelector(".minTicketPerHour"), min, 500, 2);
}

function populateTabularData(container, tabularData) {
  let header = container.querySelector(".data-header")
  let row = document.createElement("div");
  row.className = "row"
  tabularData.columns.forEach(column => {
    let div = document.createElement("div");
    div.className = "col sortable";
    div.textContent = column
    row.append(div);
  })
  header.innerHTML = "";
  header.append(row);
  let body = container.querySelector(".data-body");
  body.innerHTML = "";
  tabularData.rows.forEach(row => {
    let rowElem = document.createElement("div");
    rowElem.className = "row";
    row.forEach(col => {
      let div = document.createElement("div");
      div.className = "col";
      let value = col
      if (typeof col === "number" && isFinite(col) && !Number.isInteger(col)) {
        value = col.toFixed(2);
      }
      div.textContent = value;
      rowElem.append(div);
    })
    body.append(rowElem);
  })



}

function enableMovable(movable) {
  // Tìm phần tử con với class 'move-trigger'
  const trigger = movable.querySelector('.move-trigger');
  movable.style.zIndex = 9999;
  const parent = movable.closest(".chart-item");
  console.log(movable, parent, movable.closest(".chart-item"));
  if (!trigger) return;

  let offsetX = 0, offsetY = 0, isDragging = false;

  // Mouse down on trigger
  trigger.addEventListener('mousedown', function (e) {
    e.preventDefault();
    isDragging = true;

    // Đưa phần tử về position: absolute (nếu chưa có)
    movable.style.position = 'absolute';

    // Tính vị trí tương đối của chuột trong movable
    const rect = movable.getBoundingClientRect();
    offsetX = e.clientX - rect.left;
    offsetY = e.clientY - rect.top;

    // Đưa lên trên cùng (optional)
    movable.style.zIndex = 9999;
  });

  // Mouse move trên document
  document.addEventListener('mousemove', function (e) {
    if (!isDragging) return;
    // Cập nhật vị trí mới
    movable.style.left = (e.clientX - offsetX - parent.getBoundingClientRect().left) + 'px';
    movable.style.top = (e.clientY - offsetY - parent.getBoundingClientRect().top) + 'px';
  });

  // Mouse up trên document
  document.addEventListener('mouseup', function () {
    if (isDragging) {
      isDragging = false;
    }
  });
}


function appendDataset(chart, dataset) {
  if (chart.data.datasets.length >= CHART_CONFIG.MAX_DATASETS) {
    errorToast("Chỉ được so sánh tối đa 4 kỳ!");
    return false;
  }

  //append to chart.js
  if (chart.data.labels.length == 0 && dataset.data.length > 0) {
    console.log("... thay labels");
    chart.data.labels = dataset.labels;
  }

  const index = chart.data.datasets.length;
  chart.data.datasets.splice(0, 0, formatDataset(dataset, index));
  chart.update();
  return true;
}

function formatDataset(dataset, index) {

  const mainColor = CHART_CONFIG.colors.mainColors[index % CHART_CONFIG.colors.mainColors.length];
  const hoverColor = CHART_CONFIG.colors.hoverColors[index % CHART_CONFIG.colors.hoverColors.length];
  const isLine = dataset.type == "line";

  let formattedDataset = {
    main: dataset.main,
    nonce: dataset.nonce,
    label: dataset.label || "Không có tiêu đề",
    type: dataset.type,
    data: dataset.data,
    backgroundColor: isLine ? 'transparent' : mainColor,
    hoverBackgroundColor: hoverColor,
    borderColor: isLine ? mainColor : undefined,
    pointBackgroundColor: isLine ? mainColor : undefined,
    borderWidth: isLine ? 2 : undefined,
    barPercentage: isLine ? undefined : CHART_CONFIG.barPercentage,
    fill: false,
    // tension: 0.3,
    pointRadius: 2
  }

  return formattedDataset
}
function updateDataset(chart, dataset) {
  //replace dataset
  for (index in chart.data.datasets) {
    let current = chart.data.datasets[index];
    if (current.nonce == dataset.nonce) {
      // console.log("..replacing index", index, current.nonce, dataset.nonce)
      // chart.data.datasets.splice(index, 1) //remove the "index"th place;
      chart.data.datasets.splice(index, 1, formatDataset(dataset, chart.data.datasets.length - 1)) //add element in the "index"th place
      break;
    }
  }
  console.log(chart);
  chart.update();
}

function removeDataset(chart, nonce) {
  if (!chart) return;

  const index = chart.data.datasets.findIndex(ds => ds.nonce === nonce);
  if (index !== -1) {
    chart.data.datasets.splice(index, 1);
    chart.update();
  }
}

function createDefaultChart(ctx) {
  myChart = new Chart(ctx, {
    data: {
      labels: [],
      datasets: []
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      layout: {
        padding: CHART_CONFIG.padding.lg
      },
      interaction: {
        mode: 'index',
        intersect: false
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
          enabled: true,
          mode: 'index',
          intersect: false,
          axis: 'x'
        }
      },
      animations: {
        duration: 1000,
        easing: 'easeOutQuart',
        delay: (ctx) => ctx.datasetIndex * 100 + ctx.dataIndex * 50
      },
      scales: {
        x: {
          type: "category",
          offset: true,
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

function initPerformance() {

}

function renderAddDadasetModal(nonce) {
  console.log("..rendering modal")
  const container = ``;
  const confirmBtn = ``;
  container.classList.remove("d-none");
  container.setAttribute("data-nonce", nonce);
  container.addEventListener("click", callback);
  container.addEventListener("click", function () {

  })
}



//setting.html
function initSetting() {
  console.log("init settings");
  const employeeDetailModal = new bootstrap.Modal(document.getElementById('employeeDetailModal'));
  const modalId = document.getElementById('confirmResetPwModal');
  const confirmResetPwModal = new bootstrap.Modal(modalId);
  const defaultPWInput = modalId.querySelector("#defaultPassword")
  const defaultPwError = modalId.querySelector(".error");
  const exportBtn = document.getElementById("employee-export-excel");
  const employeeAddModal = new bootstrap.Modal(document.getElementById('addEmployeeModal'));
  initCreateEmployeeModal();
  initViewEmployeeDetailModal();

  modalId.querySelector("button[type=\"submit\"]").addEventListener("click", (e) => {
    e.preventDefault();
    let username = modalId.getAttribute("data-username");
    console.log(`Submit reset password for ${username}`);
    let defaultPassword = defaultPWInput.value;
    let validateResult = validateDefaultPassword(defaultPassword);
    if (validateResult == null) {
      resetPasswordEmployee({
        username: username,
        defaultPassword: defaultPassword,
      })
      defaultPWInput.style.border = '';
      defaultPwError.classList.add("d-none");

    } else {
      defaultPWInput.style.border = "1px solid red";
      defaultPwError.classList.remove("d-none");
      defaultPwError.innerText = validateResult;
    }



  })
  const dataTable = document.querySelectorAll(".data-table");
  if (dataTable.length > 0) {
    dataTable.forEach(table => initSortingByIndex(table));
  }

  const searchBtn = document.querySelector("#search-keyword").nextElementSibling;
  searchBtn.addEventListener("click", searchEmployees);


  exportBtn.addEventListener("click", exportEmployeeData);

  function searchEmployees() {
    const container = document.getElementById("employee-list");
    showLoadingElement(container);
    openAPIxhr(HTTP_GET_METHOD, `${API_EMPLOYEE}/get-all-user`, function (response) {
      console.log("fetch all employees", response);
      successToast(response.message);
      if (response.data.length != 0) {
        populateData(response.data, container, renderEmployeeItem);
      } else {
        showNoResult(container);
      }

    });
  }
  function initViewEmployeeDetailModal() {
    console.log("init view detail modal");
    const modalId = document.getElementById("employeeDetailModal");
    const cancelEdit = modalId.querySelector("#cancel-edit");
    const submitEdit = modalId.querySelector("#submit-edited");
    const userGroupField = modalId.querySelector(".field-group.userGroup");
    const activeField = modalId.querySelector(".field-group.active");
    userGroupField.querySelector("i").addEventListener("click", function () {
      loadUsergroupField(userGroupField);
    })
    activeField.querySelector("i").addEventListener("click", function () {
      console.log("...chose active");
      this.nextElementSibling.style.display == "block" ?
        this.nextElementSibling.style.display = "none" :
        this.nextElementSibling.style.display = "block";
    })
    activeField.querySelectorAll("[data-active]").forEach(item => {
      item.addEventListener("click", function () {
        let input = activeField.querySelector("input");
        let value = item.getAttribute("data-active");
        input.value = value == "true" ? "Hoạt động" : "Hủy kích hoạt";
        input.setAttribute("data-active", value);
        item.parentElement.style.display = "none";

      })

    })



    cancelEdit.addEventListener("click", function () {
      console.log("...cancel edit employee");
      modalId.querySelectorAll("input:not(disabled)").forEach(item => {
        item.value = item.getAttribute("data-original");
        employeeDetailModal.hide();
      })
    })

    submitEdit.addEventListener("click", function () {
      console.log("...submit edit employee");
      let data = getUpdateEmployeeDataField(modalId);
      updateEmployee(data);
      fetchDetailEmployee(modalId.getAttribute("data-username"));
    })

    modalId.querySelectorAll("input:not(disabled)").forEach(item => {
      item.addEventListener("keyup", function () {
        if (item.value != "" && item.value != item.getAttribute("data-original")) {
          modalId.querySelector("#submit-edited").disabled = false;
        } else {
          modalId.querySelector("#submit-edited").disabled = true;
        }
      })
    })


  }
  function getUpdateEmployeeDataField(container) {
    return {
      username: container.querySelector("#username").value,
      userGroup: { groupId: container.querySelector(".userGroup").getAttribute("data-usergroup-id") },
      active: container.querySelector("#edit_active").getAttribute("data-active"),
      name: container.querySelector("#name").value || null,
      email: container.querySelector("#email").value || null,
      phone: container.querySelector("#phone").value || null,
      description: container.querySelector("#description").value || null,
    }
  }

  function initCreateEmployeeModal() {
    const modalId = document.getElementById("addEmployeeModal")
    const addBtn = document.getElementById("employee-create");
    const submitCreateEmployee = modalId.querySelector("#submit-create-employee");
    const userGroupField = modalId.querySelector(".field-group.userGroup");
    userGroupField.querySelector("i").addEventListener("click", function () {
      console.log("..fetching field-group userGroup");
      loadUsergroupField(userGroupField);
    })

    addBtn.addEventListener("click", () => {
      employeeAddModal.show();
      modalId.querySelectorAll("input").forEach(item => {
        item.value = "";
      })
    })
    submitCreateEmployee.addEventListener("click", function () {
      data = {
        username: modalId.querySelector("#create-username").value || null,
        password: modalId.querySelector("#create-password").value || null,
        userGroup: { groupId: userGroupField.getAttribute("data-usergroup-id") },
        name: modalId.querySelector("#create-name").value || null,
        phone: modalId.querySelector("#create-phone").value || null,
        email: modalId.querySelector("#create-email").value || null,
        description: modalId.querySelector("#create-description").value || null,
        active: true
      }
      createEmployee(data);
    })
  }

  function loadUsergroupField(userGroupField) {
    console.log("..loading usergroup field", userGroupField);
    let ul = userGroupField.querySelector("ul");
    if (USERGROUPS.length == 0) {
      openAPIxhr(HTTP_GET_METHOD, `${API_USERGROUP}`, function (response) {
        response.data.forEach(item => { USERGROUPS.push(item) });
        console.log(response.message);
        populateUsergroupField(userGroupField);
      })
    } else if (ul.children.length == 0) {
      populateUsergroupField(userGroupField);
    } else {
      ul.style.display == "block" ? ul.style.display = "none" :
        ul.style.display = "block";
    }
  }

  function populateUsergroupField(userGroupField) {
    let ul = userGroupField.querySelector("ul");
    let liDivider = document.createElement("li");
    let liRemove = document.createElement("li");
    let input = userGroupField.querySelector("input");
    liRemove.innerHTML = `<a class="dropdown-item">Xóa</a>`;
    liDivider.innerHTML = `<hr class="dropdown-divider">`;
    liRemove.addEventListener("click", function () {
      console.log("xoas");
      input.value = "";
      userGroupField.removeAttribute("data-usergroup-id");
      ul.style.display = "none";
    })
    ul.append(liRemove);
    ul.append(liDivider);
    USERGROUPS.forEach(usergroup => {
      let li = document.createElement("li");
      li.setAttribute("data-usergroup-id", usergroup.groupId);
      li.innerHTML = `<a class="dropdown-item">${usergroup.name}</a>`
      li.addEventListener("click", function () {
        console.log(".. chọn cái này");
        input.value = usergroup.name;
        userGroupField.setAttribute("data-usergroup-id", usergroup.groupId);
        ul.style.display = "none";
      })
      ul.append(li);
    })
    ul.style.display = "block";
  }
  function renderEmployeeItem(employee) {
    console.log("render employee:", employee);
    const div = document.createElement("div");
    div.className = "row item border-bottom align-items-center";
    div.setAttribute("data-username", employee.username);
    div.innerHTML =
      `
    <div class="col" title=${employee.username}>${employee.username}</div>
    <div class="col" title=${employee.userGroup?.name}>${employee.userGroup?.name || "- -"}</div>
    <div class="col active-true">${employee.active ? "Hoạt động" : "Chưa kích hoạt"}</div>
    <div class="col" title=${employee.email}>${employee.email || "- -"}</div>
    <div class="col" title=${employee.phone}>${employee.phone || "- -"}</div>
    <div class="col" title=${formatTimestampToDate(employee.createdAt)}>${formatTimestampToDate(employee.createdAt) || "- -"}</div>
    <div class="col options overflow-visible">
        <div class="employee-dropdown">
            <i class="bi bi-three-dots-vertical"></i>
            <ul class="dropdown-menu" style="right: 0">
                <li><a class="employee-view-detail dropdown-item" href="#"><i class="bi bi-eye me-2"></i>Xem chi tiết</a></li>
                <li><a class="employee-activate dropdown-item" href="#"><i class="bi-slash-circle me-2"></i>${employee.active ? "Hủy kích hoạt" : "Kích hoạt"}</a></li>
                <li><a class="employee-reset-password dropdown-item" href="#"><i class="bi-arrow-repeat me-2"></i>Đặt lại mật khẩu</a></li>
                <li><a class="employee-delete dropdown-item" href="#"><i class="bi bi-trash3 me-2"></i>Xóa nhân viên</a></li>
            </ul>
        </div>
    </div>
    `;
    div.querySelector(".employee-dropdown > i").addEventListener("click", function () {
      let ul = div.querySelector("ul");
      ul.classList.contains("d-block") ? ul.classList.remove("d-block") : ul.classList.add("d-block");
    })
    div.querySelector(".employee-view-detail").addEventListener("click", function () {
      fetchDetailEmployee(employee.username);
      div.querySelector("ul").classList.remove("d-block");
    });

    div.querySelector(".employee-activate")?.addEventListener("click", () => {
      updateEmployee({
        username: employee.username,
        active: !employee.active
      })
      div.querySelector("ul").classList.remove("d-block");
    });
    div.querySelector(".employee-delete")?.addEventListener("click", () => {
      deleteEmployee(employee.username);
      div.querySelector("ul").classList.remove("d-block");
    });

    div.querySelector(".employee-reset-password").addEventListener("click", () => {
      openResetPasswordModal(employee.username);
      div.querySelector("ul").classList.remove("d-block");
    });

    return div;
  }

  function fetchDetailEmployee(username) {
    console.log("..fetching employee details");
    openAPIxhr(HTTP_GET_METHOD, `${API_EMPLOYEE}?username=${username}`, function (response) {
      successToast(response.message);
      console.log("... employee detail here: ", response.data)
      employeeDetailModal.show();
      const container = document.getElementById("employeeDetailModal")
      const usernameField = container.querySelector(".username input");
      const usergroupField = container.querySelector(".userGroup input");
      const activeField = container.querySelector(".active input");
      const nameField = container.querySelector("#name");
      const phoneField = container.querySelector("#phone");
      const emailField = container.querySelector("#email");
      const descriptionField = container.querySelector("#description");
      const createdAtField = container.querySelector("#createdAt");

      container.setAttribute("data-username", username);
      usernameField.value = response.data.username;
      createdAtField.value = formatEpochTimestamp(response.data.createdAt);

      usergroupField.value = response.data.userGroup.name;
      usergroupField.closest(".userGroup").setAttribute("data-usergroup-id", response.data.userGroup.groupId);
      usergroupField.setAttribute("data-original", response.data.userGroup.groupId);

      activeField.value = response.data.active == true ? "Hoạt động" : "Hủy kích hoạt";
      activeField.setAttribute("data-active", response.data.active);
      activeField.setAttribute("data-original", response.data.active);

      nameField.value = response.data.name;
      nameField.setAttribute("data-original", response.data.name);

      phoneField.value = response.data.phone;
      phoneField.setAttribute("data-original", response.data.phone);

      emailField.value = response.data.email;
      emailField.setAttribute("data-original", response.data.email);

      descriptionField.value = response.data.description;
      descriptionField.setAttribute("data-original", response.data.description)

    })
  }
  function createEmployee(data) {
    console.log("..creating employee", data);
    openAPIxhr(HTTP_POST_METHOD, `${API_EMPLOYEE}`, function (response) {
      successToast(response.message);
      console.log("create employee succesfully", response);
      employeeAddModal.hide();
      let employeeElem = renderEmployeeItem(response.data);
      let container = document.getElementById("employee-list");
      container.append(employeeElem);
    }, null, data)
  }
  function updateEmployee(data) {
    console.log("..updating following employee:", data);
    openAPIxhr(HTTP_PUT_METHOD, `${API_EMPLOYEE}`, function (response) {
      console.log("..update thanh cong", response);
      successToast(response.message);
      const container = document.getElementById("employee-list");
      const oldChild = container.querySelector(`.row[data-username=\"${response.data.username}\"]`);
      //render lai, replaceChild
      console.log("oldchild", oldChild);
      let item = renderEmployeeItem(response.data);
      container.replaceChild(item, oldChild);

    }, null, data)
  }
  function deleteEmployee(username) {
    console.log("..deleting employee");
    let data = {
      username: username
    }
    openAPIxhr(HTTP_DELETE_METHOD, `${API_EMPLOYEE}`, function (response) {
      successToast(response.message);
      const container = document.getElementById("employee-list");
      container.removeChild(container.querySelector(`.row[data-username=\"${username}\"]`));
    }, null, data)
  }

  function openResetPasswordModal(username) {
    confirmResetPwModal.show();
    modalId.setAttribute("data-username", username);

  }
  function validateDefaultPassword(defaultPassword) {
    console.log("validate reset pw");
    if (defaultPassword.trim() === "") {
      return "Không được để trống";
    }

    if (!/^.*(?=.{6,})(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).*$/.test(defaultPassword)) {
      return "Mật khẩu phải từ 6 ký tự, gồm A-Z, a-z, 0-9 và ký tự đặc biệt (!@#$%^&*...)";
    }
  }
  function resetPasswordEmployee(data) {
    console.log("...resetting password...")
    openAPIxhr(HTTP_PUT_METHOD, `${API_EMPLOYEE}/reset-password`, function (response) {
      console.log(response);
      successToast(response.message);
      confirmResetPwModal.hide();
    }, null, data)
  }

  function exportEmployeeData() {
    console.log("..exporting employee data");
  }


}
const setEndOfDay = (date) => {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate(), 23, 59, 59, 999);
};

const getMonday = (date) => {
  const d = new Date(date);
  const day = d.getDay();
  const diff = d.getDate() - (day === 0 ? 6 : day - 1); // T2 = 1, CN = 0
  return new Date(d.setDate(diff));
};