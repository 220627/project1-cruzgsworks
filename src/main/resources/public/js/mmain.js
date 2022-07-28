let deleteAllCookies = () => {
  let cookies = document.cookie.split(";");

  for (let i = 0; i < cookies.length; i++) {
    let cookie = cookies[i];
    let eqPos = cookie.indexOf("=");
    let name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
  }

  window.location.href = "/login";
};

let newReimbursement = async () => {
  console.log("--- Submitting New Reimbursement Request ---");
  // Hide alerts
  let alertElems = document.getElementsByClassName("alert");
  for (let Elems of alertElems) {
    Elems.classList.remove("activeStatus");
  }
  document.getElementById("submitBtn").setAttribute("disabled", "");
  document.getElementById("proceedText").classList.add("hideElem");
  document.getElementById("proceedLoading").classList.remove("hideElem");

  const myForm = document.getElementById("reimbursementForm");
  const formData = new FormData(myForm);

  await fetch("/api/reimbursement/new", {
    method: "POST",
    body: formData,
    headers: {
      Accept: "application/json",
    },
  })
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      document.getElementById("proceedText").classList.remove("hideElem");
      document.getElementById("proceedLoading").classList.add("hideElem");

      if (data.statusSuccess) {
        document
          .getElementById("newRequestAlertSuccess")
          .classList.add("activeStatus");
        document.getElementById("submitBtn").removeAttribute("disabled");
        document.getElementById("reimbursementForm").reset();
        // location replace in 3 seconds
      } else {
        document.getElementById("submitBtn").removeAttribute("disabled");
        document.getElementById("newRequestAlertFailed").innerHTML =
          data.statusMessage;
        document
          .getElementById("newRequestAlertFailed")
          .classList.add("activeStatus");
      }
    });
};

let toTitleCase = (str) => {
  return str.replace(/\w\S*/g, function (txt) {
    return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
  });
};

let formatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',

  // These options are needed to round to whole numbers if that's what you want.
  // minimumFractionDigits: 0, // (this suffices for whole numbers, but will print 2500.10 as $2,500.1)
  // maximumFractionDigits: 0, // (causes 2500.99 to be printed as $2,501)
});

let resolveReimbursements = async (id) => {
  let actionId = id.split("-");
  let requestData = {};
  if (actionId[0].toLowerCase() === 'approve') {
    requestData.reimb_status = 'approved';
  } else {
    requestData.reimb_status = 'denied';
  }

  await fetch(
    "/api/manager/reimbursement/resolve/" + actionId[1],
    {
      method: "PUT",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json; charset=utf-8",
      },
      body: JSON.stringify(requestData),
    }
  )
    .then((res) => {
      return res.json();
    })
    .then(() => {
      loadPagination(curPage);
    });
};

let curPage = 1;
let maxPerPage = 5;

let loadPagination = async (page) => {
  curPage = page;
  // console.log(curPage);
  document.getElementById('rPagination').classList.add('d-none');
  
  const rOrder = document.getElementById('orderBy').value;
  const rColumn = document.getElementById('sortBy').value;
  const rSearch = document.getElementById('searchTerm').value;

  const loadReimb = displayReimbursements(maxPerPage, curPage, rOrder, rColumn, rSearch);
  const rStatus = document.getElementById('filterStatus').value;

  const requestData = {
    reimb_status: rStatus,
    searchterm: (rSearch != undefined ? rSearch.toLowerCase() : '')
  };

  loadReimb.then(async () => {
    await fetch(
      "/api/reimbursement/count?" + new URLSearchParams(requestData),
      {
        method: "GET",
        headers: {
          Accept: "application/json",
        }
      }
    )
      .then((res) => {
        return res.json();
      })
      .then((data) => {
        let nRows = data.statusObject.count_rows;
        let htmlStr = '';
        let nPages = Math.ceil(nRows / maxPerPage);

        if (page == 1) {
          htmlStr += '<li class="page-item prev-page disabled"><a class="page-link">Prev</a></li>';
        } else {
          htmlStr += '<li class="page-item prev-page"><a class="page-link">Prev</a></li>';
        }

        for (let idx = 1; idx <= nPages; idx++) {
          if (idx == page) {
            htmlStr += '<li class="page-item active"><a class="page-link">' + idx + '</a></li>';
          } else {
            htmlStr += '<li class="page-item"><a class="page-link">' + idx + '</a></li>';
          }
        }

        if (nPages == 0) {
          htmlStr += '<li class="page-item disabled"><a class="page-link">1</a></li>';
        }

        if (page == nPages || nPages == 0) {
          htmlStr += '<li class="page-item next-page disabled"><a class="page-link">Next</a></li>';
        } else {
          htmlStr += '<li class="page-item next-page"><a class="page-link">Next</a></li>';
        }

        document.getElementById('rPagination').innerHTML = htmlStr;

        let paginationElem = document.querySelectorAll('#rPagination li');
        for (let idx = 0; idx < paginationElem.length; idx++) {
          if (paginationElem[idx].classList.contains('disabled') || paginationElem[idx].classList.contains('active')) {
            continue;
          }
          if (paginationElem[idx].classList.contains('prev-page')) {
            paginationElem[idx].addEventListener('click', function () {
              loadPagination(curPage - 1)
            });
            continue;
          }
          if (paginationElem[idx].classList.contains('next-page')) {
            paginationElem[idx].addEventListener('click', function () {
              loadPagination(curPage + 1)
            });
            continue;
          }
          paginationElem[idx].addEventListener('click', function () {
            loadPagination(idx)
          });
        }

        // Show table and pagination
        document.getElementById('mainReimbursementsSpinner').classList.add('d-none');
        document.getElementById('mainReimbursementsTBody').classList.remove('d-none');
        document.getElementById('rPagination').classList.remove('d-none');
      });
  });

};

let displayReimbursements = async (limit, pageNum, order, column, searchTerm) => {
  document.getElementById('mainReimbursementsSpinner').classList.remove('d-none');
  document.getElementById('mainReimbursementsTBody').classList.add('d-none');
  console.log("--- Display Reimbursements ---");

  const rStatus = document.getElementById('filterStatus').value;

  const requestData = {
    reimb_status: rStatus,
    limit: (limit != undefined ? limit : 0),
    page: (pageNum != undefined ? pageNum : 0),
    order: (order != undefined ? order.toLowerCase() : 'asc'),
    column: (column != undefined ? column.toLowerCase() : 'reimb_id'),
    searchterm: (searchTerm != undefined ? searchTerm.toLowerCase() : ''),
  };

  let rowsArr = [];

  await fetch(
    "/api/reimbursement/list?" + new URLSearchParams(requestData),
    {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    }
  )
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      if (data.statusObject != undefined) {

        let recordsArr = data.statusObject;

        for (let rRow of recordsArr) {

          let receiptStr = '<span class="text-secondary">(None)</span>';
          if (rRow.has_receipt) {
            receiptStr = '<a href="/api/employee/reimbursement/receipt/' + rRow.reimb_id + '" download><i class="bi bi-file-earmark-arrow-down-fill me-2"></i>Download</a>';
          }

          let statusStr = '';
          let rActions =
            '    <a id="deny-' + rRow.reimb_id + '" class="reimb-action text-danger me-3 fw-bold" tabindex="0"><i class="bi bi-x-lg me-2"></i>Deny</a>' +
            '    <a id="approve-' + rRow.reimb_id + '" class="reimb-action text-success fw-bold" tabindex="0"><i class="bi bi-check-lg me-2"></i>Approve</a>';
          if (rRow.ersReimbursementStatus != undefined) {
            switch (rRow.ersReimbursementStatus.reimb_status) {
              case 'approved':
                statusStr = '<span class="text-success">Approved</span>';
                rActions = '<span class="text-secondary">(Acted Upon)</span>'
                break;
              case 'denied':
                statusStr = '<span class="text-danger">Denied</span>';
                rActions = '<span class="text-secondary">(Acted Upon)</span>'
                break;
              default:
                statusStr = '<span class="text-secondary">Pending</span>';
                break;
            }
          }

          let rType = "";
          switch (rRow.ersReimbursementType.reimb_type) {
            case 'lodging':
              rType = '<i class="bi bi-building me-2"></i>Lodging';
              break;
            case 'travel':
              rType = '<i class="bi bi-car-front-fill me-2"></i>Travel';
              break;
            case 'food':
              rType = '<i class="bi bi-egg-fried me-2"></i>Food';
              break;
            default:
              rType = '<i class="bi bi-question-circle me-2"></i>Other';
              break;
          }

          let aRow =
            "<tr>" +
            '  <td class=\"fit\" scope="row">' + rRow.reimb_id + '</td>' +
            "  <td class=\"fit text-end\">" + formatter.format(rRow.reimb_amount) + "</td>" +
            "  <td class=\"fit\">" + (rRow.reimb_author > 0 ? (rRow.ersAuthor.user_first_name + " " + rRow.ersAuthor.user_last_name) : "<span class=\"text-secondary\">(None)</span>") + "</td>" +
            "  <td class=\"fit\">" + rRow.reimb_submitted + "</td>" +
            "  <td class=\"fit\">" + (rRow.reimb_resolver > 0 ? (rRow.ersResolver.user_first_name + " " + rRow.ersResolver.user_last_name) : "<span class=\"text-secondary\">(None)</span>") + "</td>" +
            '  <td class="fit">' + (rRow.reimb_resolved != undefined ? rRow.reimb_resolved : "<span class=\"text-secondary\">(None)</span>") + '</td>' +
            "  <td class=\"text-start\">" + (rRow.reimb_description != undefined ? rRow.reimb_description : "(No Description)") + "</td>" +
            "  <td class=\"fit\">" + rType + "</td>" +
            '  <td class="fit">' + statusStr + '</td>' +
            '  <td class="fit">' + receiptStr + '</td>' +
            '  <td class="fit">' + rActions + '  </td>' +
            "</tr>";
          rowsArr.push(aRow);
        }

        if (rowsArr.length < 1) {
          let aRow = "<tr><td colspan=\"11\">No Results Found</td></tr>";
          rowsArr.push(aRow);
        };


      } else {
        if (rowsArr.length < 1) {
          let aRow = "<tr><td colspan=\"11\">No Results Found</td></tr>";
          rowsArr.push(aRow);
        };
      }

      document.getElementById('mainReimbursementsTBody').innerHTML = rowsArr.join("\r\n");

      // let reimbAction = document.getElementsByClassName('reimb-action');

      let popoverOptions = {
        html: true,
        sanitize: false,
        placement: 'left',
        title: 'Are you sure?',
        trigger: 'click',
        content: '<div class="container-fluid">' +
          '  <div class="row">' +
          '    <div class="col p-0 me-2"><a type="button" class="btn btn-confirm-no btn-outline-danger pt-0 pb-0">No</a></div>' +
          '    <div class="col p-0"><a type="button" class="btn btn-confirm-yes btn-outline-success pt-0 pb-0">Yes</a></div>' +
          '  </div>' +
          '</div>'
      };

      // Confirm action
      $('.reimb-action').popover(popoverOptions);

      $('.reimb-action').on('click', function () {
        $('.reimb-action').not(this).popover('hide');
      });

      $('.reimb-action').on('shown.bs.popover', function () {
        const popoverInstanceId = '#' + $(this).attr('aria-describedby') + ' .btn';
        const originId = $(this).attr('id');
        const thisElem = $(this);
        $(popoverInstanceId).click(function () {
          if ($(this).hasClass('btn-confirm-yes')) {
            thisElem.popover("hide");
            resolveReimbursements(originId);
          } else {
            thisElem.popover("hide");
          }
        });
      });

    });
};

/*
let loadPendingReimbursements = async () => {
  document.getElementById('reimPendingSpinner').classList.remove('d-none');
  document.getElementById('reimPendingTable').classList.add('d-none');
  console.log("--- Loading Pending Reimbursements ---");

  const requestData = {
    reimb_status: "pending",
  };

  await fetch(
    "/api/manager/reimbursement?" + new URLSearchParams(requestData),
    {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    }
  )
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      let recordsArr = data.statusObject;
      let rowsArr = [];

      if (recordsArr.length > 0) {
        for (let rRow of recordsArr) {
          let receiptStr = '<span class="text-danger">(None)</span>';
          if (rRow.has_receipt) {
            receiptStr = '<a href="/api/employee/reimbursement/receipt/' + rRow.reimb_id + '" download><i class="bi bi-file-earmark-arrow-down-fill me-2"></i>Download</a>';
          }

          let aRow =
            "<tr>" +
            '  <td class=\"fit\" scope="row">' + rRow.reimb_id + '</td>' +
            '  <td class=\"fit\">' + rRow.ersAuthor.user_first_name + " " + rRow.ersAuthor.user_last_name + '</td>' +
            "  <td class=\"fit text-end\">" + formatter.format(rRow.reimb_amount) + "</td>" +
            "  <td class=\"fit\">" + rRow.reimb_submitted + "</td>" +
            "  <td class=\"text-start\">" + (rRow.reimb_description != undefined ? rRow.reimb_description : "(No Description)") + "</td>" +
            "  <td class=\"fit\">" + toTitleCase(rRow.ersReimbursementType.reimb_type) + "</td>" +
            '  <td class="fit">' + receiptStr + '</td>' +
            '  <td class="fit">' +
            '    <a id="deny-' + rRow.reimb_id + '" class="reimb-action text-danger me-3 fw-bold" tabindex="0"><i class="bi bi-x-lg me-2"></i>Deny</a>' +
            '    <a id="approve-' + rRow.reimb_id + '" class="reimb-action text-success fw-bold" tabindex="0"><i class="bi bi-check-lg me-2"></i>Approve</a>' +
            '  </td>' +
            "</tr>";
          rowsArr.push(aRow);

        }
      } else {
        let aRow = "<tr><td colspan=\"8\">No Results Found</td></tr>";
        rowsArr.push(aRow);
      }

      document.getElementById('reimPendingSpinner').classList.add('d-none');
      document.getElementById('reimPendingTBody').innerHTML = rowsArr.join("\r\n");
      document.getElementById('reimPendingTable').classList.remove('d-none');

      let reimbAction = document.getElementsByClassName('reimb-action');

      let popoverOptions = {
        html: true,
        sanitize: false,
        placement: 'left',
        title: 'Are you sure?',
        trigger: 'click',
        content: '<div class="container-fluid">' +
          '  <div class="row">' +
          '    <div class="col p-0 me-2"><a type="button" class="btn btn-confirm-no btn-outline-danger pt-0 pb-0">No</a></div>' +
          '    <div class="col p-0"><a type="button" class="btn btn-confirm-yes btn-outline-success pt-0 pb-0">Yes</a></div>' +
          '  </div>' +
          '</div>'
      };

      $('.reimb-action').popover(popoverOptions);

      $('.reimb-action').on('click', function () {
        $('.reimb-action').not(this).popover('hide');
      });

      $('.reimb-action').on('shown.bs.popover', function () {
        const popoverInstanceId = '#' + $(this).attr('aria-describedby') + ' .btn';
        const originId = $(this).attr('id');
        const thisElem = $(this);
        $(popoverInstanceId).click(function () {
          if ($(this).hasClass('btn-confirm-yes')) {
            thisElem.popover("hide");
            resolveReimbursements(originId);
          } else {
            thisElem.popover("hide");
          }
        });
      });
    });
};

let loadApprovedReimbursements = async () => {
  document.getElementById('reimApprovedSpinner').classList.remove('d-none');
  document.getElementById('reimApprovedTable').classList.add('d-none');
  console.log("--- Loading Approved Reimbursements ---");

  const requestData = {
    reimb_status: "approved",
  };

  await fetch(
    "/api/manager/reimbursement?" + new URLSearchParams(requestData),
    {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    }
  )
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      let recordsArr = data.statusObject;
      let rowsArr = [];
      if (recordsArr.length > 0) {
        for (let rRow of recordsArr) {
          let receiptStr = '<span class="text-danger">(None)</span>';
          if (rRow.has_receipt) {
            receiptStr = '<a href="/api/employee/reimbursement/receipt/' + rRow.reimb_id + '" download><i class="bi bi-file-earmark-arrow-down-fill me-2"></i>Download</a>';
          }

          let aRow =
            "<tr>" +
            '  <td class=\"fit\" scope="row">' + rRow.reimb_id + '</td>' +
            '  <td class=\"fit\">' + rRow.ersAuthor.user_first_name + " " + rRow.ersAuthor.user_last_name + '</td>' +
            "  <td class=\"fit text-end\">" + formatter.format(rRow.reimb_amount) + "</td>" +
            "  <td class=\"fit\">" + rRow.reimb_submitted + "</td>" +
            "  <td class=\"fit\">" + rRow.ersResolver.user_first_name + " " + rRow.ersResolver.user_last_name + "</td>" +
            '  <td class="fit">' + rRow.reimb_resolved + '</td>' +
            "  <td class=\"text-start\">" + (rRow.reimb_description != undefined ? rRow.reimb_description : "(No Description)") + "</td>" +
            "  <td class=\"fit\">" + toTitleCase(rRow.ersReimbursementType.reimb_type) + "</td>" +
            '  <td class="fit">' + receiptStr + '</td>' +
            "</tr>";
          rowsArr.push(aRow);
        }
      } else {
        let aRow = "<tr><td colspan=\"9\">No Results Found</td></tr>";
        rowsArr.push(aRow);
      };

      document.getElementById('reimApprovedSpinner').classList.add('d-none');
      document.getElementById('reimApprovedTBody').innerHTML = rowsArr.join("\r\n");
      document.getElementById('reimApprovedTable').classList.remove('d-none');
    });
};

let loadDeniedReimbursements = async () => {
  document.getElementById('reimDeniedSpinner').classList.remove('d-none');
  document.getElementById('reimDeniedTable').classList.add('d-none');
  console.log("--- Loading Denied Reimbursements ---");

  const requestData = {
    reimb_status: "denied",
  };

  await fetch(
    "/api/manager/reimbursement?" + new URLSearchParams(requestData),
    {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    }
  )
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      let recordsArr = data.statusObject;
      let rowsArr = [];
      if (recordsArr.length > 0) {
        for (let rRow of recordsArr) {
          let receiptStr = '<span class="text-danger">(None)</span>';
          if (rRow.has_receipt) {
            receiptStr = '<a href="/api/employee/reimbursement/receipt/' + rRow.reimb_id + '" download><i class="bi bi-file-earmark-arrow-down-fill me-2"></i>Download</a>';
          }
          let aRow =
            "<tr>" +
            '  <td class=\"fit\" scope="row">' + rRow.reimb_id + '</td>' +
            '  <td class=\"fit\">' + rRow.ersAuthor.user_first_name + " " + rRow.ersAuthor.user_last_name + '</td>' +
            "  <td class=\"fit text-end\">" + formatter.format(rRow.reimb_amount) + "</td>" +
            "  <td class=\"fit\">" + rRow.reimb_submitted + "</td>" +
            "  <td class=\"fit\">" + rRow.ersResolver.user_first_name + " " + rRow.ersResolver.user_last_name + "</td>" +
            '  <td class="fit">' + rRow.reimb_resolved + '</td>' +
            "  <td class=\"text-start\">" + (rRow.reimb_description != undefined ? rRow.reimb_description : "(No Description)") + "</td>" +
            "  <td class=\"fit\">" + toTitleCase(rRow.ersReimbursementType.reimb_type) + "</td>" +
            '  <td class="fit">' + receiptStr + '</td>' +
            "</tr>";
          rowsArr.push(aRow);
        }
      }
      else {
        let aRow = "<tr><td colspan=\"9\">No Results Found</td></tr>";
        rowsArr.push(aRow);
      };

      document.getElementById('reimDeniedSpinner').classList.add('d-none');
      document.getElementById('reimDeniedTBody').innerHTML = rowsArr.join("\r\n");
      document.getElementById('reimDeniedTable').classList.remove('d-none');
    });
};

*/

let refreshAllTables = () => {
  loadPendingReimbursements();
  loadApprovedReimbursements();
  loadDeniedReimbursements();
}

let doUpdate = async (param) => {
  // Hide alerts
  let alertElems = document.getElementsByClassName("alert");
  for (let Elems of alertElems) {
    Elems.classList.remove("activeStatus");
  }
  document.getElementById("updateProfileButton").setAttribute("disabled", "");
  document.getElementById("updateText").classList.add("hideElem");
  document.getElementById("updateLoading").classList.remove("hideElem");

  // get form data
  let updateData = {
    ers_username: document.getElementById("inputUsername").value,
    ers_password: document.getElementById("inputPassword").value,
    user_first_name: document.getElementById("inputFirst").value,
    user_last_name: document.getElementById("inputLast").value,
    user_email: document.getElementById("inputEmail").value,
  };

  await fetch("/api/users/update/" + document.getElementById("ers_users_id").value, {
    method: "PUT",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json; charset=utf-8",
    },
    body: JSON.stringify(updateData),
  })
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      document.getElementById("updateText").classList.remove("hideElem");
      document.getElementById("updateLoading").classList.add("hideElem");

      if (data.statusSuccess) {
        document
          .getElementById("updateAlertSuccess")
          .classList.add("activeStatus");
        // location replace in 3 seconds
        setTimeout(function () {
          window.location.replace("./");
        }, 1000);
      } else {
        document.getElementById("updateProfileButton").removeAttribute("disabled");
        document.getElementById("updateAlertFailed").innerHTML =
          data.statusMessage;
        document
          .getElementById("updateAlertFailed")
          .classList.add("activeStatus");
      }
    });
};

let doChangePassword = async (param) => {
  // Hide alerts
  let alertElems = document.getElementsByClassName("alert");
  for (let Elems of alertElems) {
    Elems.classList.remove("activeStatus");
  }
  document.getElementById("updatePasswordButton").setAttribute("disabled", "");
  document.getElementById("updatePasswordText").classList.add("hideElem");
  document.getElementById("updatePasswordLoading").classList.remove("hideElem");

  // get form data
  let updatePasswordData = {
    ers_password: document.getElementById("inputPassword").value,
  };

  await fetch("/api/users/password/" + document.getElementById("ers_users_id").value, {
    method: "PUT",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json; charset=utf-8",
    },
    body: JSON.stringify(updatePasswordData),
  })
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      document.getElementById("updatePasswordText").classList.remove("hideElem");
      document.getElementById("updatePasswordLoading").classList.add("hideElem");

      if (data.statusSuccess) {
        document
          .getElementById("updatePasswordAlertSuccess")
          .classList.add("activeStatus");
        // location replace in 3 seconds
        setTimeout(function () {
          window.location.replace("./");
        }, 1000);
      } else {
        document.getElementById("updatePasswordButton").removeAttribute("disabled");
        document.getElementById("updatePasswordAlertFailed").innerHTML =
          data.statusMessage;
        document
          .getElementById("updatePasswordAlertFailed")
          .classList.add("activeStatus");
      }
    });
};

document.addEventListener("DOMContentLoaded", function (event) {
  // Log-out button
  document.getElementById("logoutBtn").addEventListener("click", function () {
    deleteAllCookies();
  });
  document.getElementById("refreshReimbursementTables").addEventListener("click", function () {
    loadPagination(curPage);
  });
  document.getElementById("filterStatus").addEventListener("change", function () {
    loadPagination(1);
  });
  document.getElementById("orderBy").addEventListener("change", function () {
    loadPagination(1);
  });
  document.getElementById("sortBy").addEventListener("change", function () {
    loadPagination(1);
  });

  document.getElementById("searchButton").addEventListener("click", function () {
    loadPagination(curPage);
  });
  document.getElementById("searchTerm").addEventListener("keypress", function (event) {
    if (event.key === "Enter") {
      event.preventDefault;
      loadPagination(curPage);
    }
  });

  loadPagination(1);

  document.querySelectorAll('[data-bs-toggle="popover"]')
    .forEach(popover => {
      new bootstrap.Popover(popover)
    })

  document.getElementById('numberOfPages').addEventListener('change', function () {
    maxPerPage = this.value;
    loadPagination(1);
  });

  const profileForms = document.querySelectorAll('.updateProfileForm.needs-validation');
  const passwordForms = document.querySelectorAll('.updatePasswordForm.needs-validation');

  const password1 = document.getElementById('inputPassword');
  const password2 = document.getElementById('confirmPassword');

  Array.from(profileForms).forEach(form => {
    form.addEventListener('submit', event => {
      event.preventDefault();
      event.stopPropagation();
      if (form.checkValidity()) {
        doUpdate();
      }
      form.classList.add('was-validated')
    }, false);
  })

  Array.from(passwordForms).forEach(form => {
    form.addEventListener('submit', event => {
      event.preventDefault();
      event.stopPropagation();
      passwordForms[0].classList.remove('invalid-password');
      passwordForms[0].classList.remove('valid-password');
      if (form.checkValidity()) {
        if (password1.value != password2.value) {
          passwordForms[0].classList.add('invalid-password');
        } else {
          doChangePassword();
        }
      }
      form.classList.add('was-validated')
    }, false);
    form.addEventListener('keyup', event => {
      event.preventDefault();
      event.stopPropagation();
      passwordForms[0].classList.remove('invalid-password');
      passwordForms[0].classList.remove('valid-password');
      if (password1.value != password2.value) {
        passwordForms[0].classList.add('invalid-password');
      } else {
        passwordForms[0].classList.add('valid-password');
      }
    });
  })

});
