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
  console.log(id);
  let actionId = id.split("-");
  let requestData = {};
  if (actionId[0].toLowerCase() === 'approve') {
    requestData.reimb_status = 'approved';
  } else {
    requestData.reimb_status = 'denied';
  }
  console.log(requestData);

  /*
  ev.preventDefault();
  let elemId = ev.target.id;
  let actionId = elemId.split("-");
  let requestData = {};
  if (actionId[0].toLowerCase() === 'approve') {
    requestData.reimb_status = 'approved';
  } else {
    requestData.reimb_status = 'denied';
  }
  */
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
    .then((data) => {
      // console.log(data);
      refreshAllTables();
    });

  // console.log(elemId);
  // console.log(actionId);
  // console.log(requestData);
};

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
      var rowsArr = [];

      if (recordsArr.length > 0) {
        for (var rRow of recordsArr) {
          var receiptStr = '<span class="text-danger">(None)</span>';
          if (rRow.has_receipt) {
            receiptStr = '<a href="/api/employee/reimbursement/receipt/' + rRow.reimb_id + '" download><i class="bi bi-file-earmark-arrow-down-fill me-2"></i>Download</a>';
          }

          var aRow =
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

      /*
      const popoverList = [...reimbAction].map(myElem => new bootstrap.Popover(myElem, popoverOptions));

      console.log(popoverList);

      for (const curE of reimbAction) {
        curE.addEventListener('show.bs.popover', function (param) {
          console.log(param);
        });
        // curE.addEventListener('click', resolveReimbursements);
      }
      */

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
      var rowsArr = [];
      if (recordsArr.length > 0) {
        for (var rRow of recordsArr) {
          var receiptStr = '<span class="text-danger">(None)</span>';
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
      var rowsArr = [];
      if (recordsArr.length > 0) {
        for (var rRow of recordsArr) {
          var receiptStr = '<span class="text-danger">(None)</span>';
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

let refreshAllTables = () => {
  loadPendingReimbursements();
  loadApprovedReimbursements();
  loadDeniedReimbursements();
}

document.addEventListener("DOMContentLoaded", function (event) {
  // Log-out button
  document.getElementById("logoutBtn").addEventListener("click", function () {
    deleteAllCookies();
  });

  // Submit Request
  document.getElementById("submitBtn").addEventListener("click", function () {
    newReimbursement();
  });

  // Refresh list of reimbursements
  document.getElementById("refreshReimbursementTables").addEventListener("click", function () {
    refreshAllTables();
  });

  // Get reimburse types
  loadReimbursementTypes();

  // Get all list of reimbursements
  refreshAllTables();

  // Reload list of reimbursements every minute
  // var intervalID = setInterval(refreshAllTables, 60000);

  document.querySelectorAll('[data-bs-toggle="popover"]')
    .forEach(popover => {
      new bootstrap.Popover(popover)
    })

});
