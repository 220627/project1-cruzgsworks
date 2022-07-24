let deleteAllCookies = () => {
  console.log("clearing cookies");
  var cookies = document.cookie.split(";");

  for (var i = 0; i < cookies.length; i++) {
    var cookie = cookies[i];
    var eqPos = cookie.indexOf("=");
    var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
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
        loadPagination(curPage);
      } else {
        document.getElementById("submitBtn").removeAttribute("disabled");
        document.getElementById("newRequestAlertFailed").innerHTML = data.statusMessage;
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
  //minimumFractionDigits: 0, // (this suffices for whole numbers, but will print 2500.10 as $2,500.1)
  //maximumFractionDigits: 0, // (causes 2500.99 to be printed as $2,501)
});

let curPage = 1;
let maxPerPage = 10;

let loadPagination = async (page) => {
  console.log('page ' + page);
  curPage = page;
  document.getElementById('rPagination').classList.add('d-none');

  const loadReimb = displayReimbursements(maxPerPage, page);
  const rStatus = document.getElementById('filterStatus').value;

  const requestData = {
    reimb_status: rStatus
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
          htmlStr += '<li class="page-item prev-page disabled"><a class="page-link" href="#">Prev</a></li>';
        } else {
          htmlStr += '<li class="page-item prev-page"><a class="page-link" href="#">Prev</a></li>';
        }

        for (let idx = 1; idx <= nPages; idx++) {
          if (idx == page) {
            htmlStr += '<li class="page-item active"><a class="page-link" href="#">' + idx + '</a></li>';
          } else {
            htmlStr += '<li class="page-item"><a class="page-link" href="#">' + idx + '</a></li>';
          }
        }

        if (page == nPages) {
          htmlStr += '<li class="page-item next-page disabled"><a class="page-link" href="#">Next</a></li>';
        } else {
          htmlStr += '<li class="page-item next-page"><a class="page-link" href="#">Next</a></li>';
        }

        document.getElementById('rPagination').innerHTML = htmlStr;

        let paginationElem = document.querySelectorAll('#rPagination li');
        // console.log(paginationElem.length);
        for (let idx = 0; idx < paginationElem.length; idx++) {
          console.log('idx ' + idx);
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

        document.getElementById('rPagination').classList.remove('d-none');
      });
  });

};

let displayReimbursements = async (limit, pageNum) => {
  document.getElementById('mainReimbursementsSpinner').classList.remove('d-none');
  document.getElementById('mainReimbursementsTable').classList.add('d-none');
  console.log("--- Loading Pending Reimbursements ---");

  const rStatus = document.getElementById('filterStatus').value;

  const requestData = {
    reimb_status: rStatus,
    limit: (limit != undefined ? limit : 0),
    page: (pageNum != undefined ? pageNum : 0)
  };

  await fetch(
    "/api/reimbursement/pagination?" + new URLSearchParams(requestData),
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
        var rowsArr = [];
        for (var rRow of recordsArr) {
          var receiptStr = '<span class="text-secondary">(None)</span>';
          if (rRow.has_receipt) {
            receiptStr = '<a href="/api/employee/reimbursement/receipt/' + rRow.reimb_id + '" download><i class="bi bi-file-earmark-arrow-down-fill me-2"></i>Download</a>';
          }

          var statusStr = '';
          if (rRow.ersReimbursementStatus != undefined) {
            switch (rRow.ersReimbursementStatus.reimb_status) {
              case 'approved':
                statusStr = '<span class="text-success">Approved</span>';
                break;
              case 'denied':
                statusStr = '<span class="text-danger">Denied</span>';
                break;
              default:
                statusStr = '<span class="text-secondary">Pending</span>';
                break;
            }
          }

          var rType = "";
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

          var aRow =
            "<tr>" +
            '  <td class=\"fit\" scope="row">' + rRow.reimb_id + '</td>' +
            "  <td class=\"fit text-end\">" + formatter.format(rRow.reimb_amount) + "</td>" +
            "  <td class=\"fit\">" + rRow.reimb_submitted + "</td>" +
            "  <td class=\"fit\">" + (rRow.reimb_resolver > 0 ? (rRow.ersResolver.user_first_name + " " + rRow.ersResolver.user_last_name) : "<span class=\"text-secondary\">(None)</span>") + "</td>" +
            '  <td class="fit">' + (rRow.reimb_resolved != undefined ? rRow.reimb_resolved : "<span class=\"text-secondary\">(None)</span>") + '</td>' +
            "  <td class=\"text-start\">" + (rRow.reimb_description != undefined ? rRow.reimb_description : "(No Description)") + "</td>" +
            "  <td class=\"fit\">" + rType + "</td>" +
            '  <td class="fit">' + statusStr + '</td>' +
            '  <td class="fit">' + receiptStr + '</td>' +
            "</tr>";
          rowsArr.push(aRow);
        }

        if (rowsArr.length < 1) {
          let aRow = "<tr><td colspan=\"9\">No Results Found</td></tr>";
          rowsArr.push(aRow);
        };


      } else {
        if (rowsArr.length < 1) {
          let aRow = "<tr><td colspan=\"9\">No Results Found</td></tr>";
          rowsArr.push(aRow);
        };
      }
      document.getElementById('mainReimbursementsSpinner').classList.add('d-none');
      document.getElementById('mainReimbursementsTBody').innerHTML = rowsArr.join("\r\n");
      document.getElementById('mainReimbursementsTable').classList.remove('d-none');
      // console.log(recordsArr);
    });
};

let loadPendingReimbursements = async () => {
  document.getElementById('reimPendingSpinner').classList.remove('d-none');
  document.getElementById('reimPendingTable').classList.add('d-none');
  console.log("--- Loading Pending Reimbursements ---");

  const requestData = {
    reimb_status: "pending",
  };

  await fetch(
    "/api/employee/reimbursement?" + new URLSearchParams(requestData),
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
      var rowsArr = [];
      for (var rRow of recordsArr) {
        var receiptStr = '<span class="text-danger">(None)</span>';
        if (rRow.has_receipt) {
          receiptStr = '<a href="/api/employee/reimbursement/receipt/' + rRow.reimb_id + '" download><i class="bi bi-file-earmark-arrow-down-fill me-2"></i>Download</a>';
        }

        var aRow =
          "<tr>" +
          '  <td class=\"fit\" scope="row">' + rRow.reimb_id + '</td>' +
          "  <td class=\"fit text-end\">" + formatter.format(rRow.reimb_amount) + "</td>" +
          "  <td class=\"fit\">" + rRow.reimb_submitted + "</td>" +
          "  <td class=\"text-start\">" + (rRow.reimb_description != undefined ? rRow.reimb_description : "(No Description)") + "</td>" +
          "  <td class=\"fit\">" + toTitleCase(rRow.ersReimbursementType.reimb_type) + "</td>" +
          '  <td class="fit">' + receiptStr + '</td>' +
          "</tr>";
        rowsArr.push(aRow);
      }

      if (rowsArr.length < 1) {
        let aRow = "<tr><td colspan=\"6\">No Results Found</td></tr>";
        rowsArr.push(aRow);
      };

      document.getElementById('reimPendingSpinner').classList.add('d-none');
      document.getElementById('reimPendingTBody').innerHTML = rowsArr.join("\r\n");
      document.getElementById('reimPendingTable').classList.remove('d-none');
      // console.log(recordsArr);
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
    "/api/employee/reimbursement?" + new URLSearchParams(requestData),
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
      var rowsArr = [];
      for (var rRow of recordsArr) {
        var receiptStr = '<span class="text-danger">(None)</span>';
        if (rRow.has_receipt) {
          receiptStr = '<a href="/api/employee/reimbursement/receipt/' + rRow.reimb_id + '" download><i class="bi bi-file-earmark-arrow-down-fill me-2"></i>Download</a>';
        }

        let aRow =
          "<tr>" +
          '  <td class=\"fit\" scope="row">' + rRow.reimb_id + '</td>' +
          "  <td class=\"fit text-end\">" + formatter.format(rRow.reimb_amount) + "</td>" +
          "  <td class=\"fit\">" + rRow.reimb_submitted + "</td>" +
          "  <td class=\"text-start\">" + (rRow.reimb_description != undefined ? rRow.reimb_description : "(No Description)") + "</td>" +
          "  <td class=\"fit\">" + toTitleCase(rRow.ersReimbursementType.reimb_type) + "</td>" +
          '  <td class="fit">' + receiptStr + '</td>' +
          "</tr>";
        rowsArr.push(aRow);
      }

      if (rowsArr.length < 1) {
        let aRow = "<tr><td colspan=\"6\">No Results Found</td></tr>";
        rowsArr.push(aRow);
      };

      document.getElementById('reimApprovedSpinner').classList.add('d-none');
      document.getElementById('reimApprovedTBody').innerHTML = rowsArr.join("\r\n");
      document.getElementById('reimApprovedTable').classList.remove('d-none');
      // console.log(recordsArr);
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
    "/api/employee/reimbursement?" + new URLSearchParams(requestData),
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
      var rowsArr = [];
      for (var rRow of recordsArr) {
        var receiptStr = '<span class="text-danger">(None)</span>';
        if (rRow.has_receipt) {
          receiptStr = '<a href="/api/employee/reimbursement/receipt/' + rRow.reimb_id + '" download><i class="bi bi-file-earmark-arrow-down-fill me-2"></i>Download</a>';
        }

        let aRow =
          "<tr>" +
          '  <td class=\"fit\" scope="row">' + rRow.reimb_id + '</td>' +
          "  <td class=\"fit text-end\">" + formatter.format(rRow.reimb_amount) + "</td>" +
          "  <td class=\"fit\">" + rRow.reimb_submitted + "</td>" +
          "  <td class=\"text-start\">" + (rRow.reimb_description != undefined ? rRow.reimb_description : "(No Description)") + "</td>" +
          "  <td class=\"fit\">" + toTitleCase(rRow.ersReimbursementType.reimb_type) + "</td>" +
          '  <td class="fit">' + receiptStr + '</td>' +
          "</tr>";
        rowsArr.push(aRow);
      }

      if (rowsArr.length < 1) {
        let aRow = "<tr><td colspan=\"6\">No Results Found</td></tr>";
        rowsArr.push(aRow);
      };

      document.getElementById('reimDeniedSpinner').classList.add('d-none');
      document.getElementById('reimDeniedTBody').innerHTML = rowsArr.join("\r\n");
      document.getElementById('reimDeniedTable').classList.remove('d-none');
      // console.log(recordsArr);
    });
};

let loadReimbursementTypes = async () => {
  console.log("--- Loading Reimbursement Types ---");
  await fetch("/api/reimbursement/types", {
    method: "GET",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json; charset=utf-8",
    },
  })
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      //console.log(data);
      var arrOptions = [];
      var arrOptionsCollection = data.statusObject;

      if (data.statusObject) {
        arrOptionsCollection.forEach((element) => {
          arrOptions.push(
            "<option value='" + element.reimb_type_id + "'>" + toTitleCase(element.reimb_type) + "</option>"
          );
        });

        document.getElementById("rType").innerHTML = arrOptions.join("\r\n");
      }
    });
};
/*
let refreshAllTables = () => {
  loadPendingReimbursements();
  loadApprovedReimbursements();
  loadDeniedReimbursements();
}
*/

document.addEventListener("DOMContentLoaded", function (event) {
  document.getElementById("logoutBtn").addEventListener("click", function () {
    deleteAllCookies();
  });
  document.getElementById("submitBtn").addEventListener("click", function () {
    newReimbursement();
  });
  document.getElementById("refreshReimbursementTables").addEventListener("click", function () {
    loadPagination(curPage);
  });
  document.getElementById("filterStatus").addEventListener("change", function () {
    loadPagination(curPage);
  });
  loadReimbursementTypes();
  loadPagination(1);
  // refreshAllTables();
  // var intervalID = setInterval(refreshAllTables, 60000);

});
