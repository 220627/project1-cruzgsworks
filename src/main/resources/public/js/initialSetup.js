/*
const setupResult = function (response) {
  console.log(response);
  $(".proceedText").css("display", "block");
  $(".proceedLoading").css("display", "none");
  if (response.statusSuccess) {
    $(".setup-message .alert-success").addClass("activeStatus");
    // location replace in 3 seconds
    setTimeout(function () {
      window.location.replace("./login");
    }, 3000);
  } else {
    $("#submitBtn").removeAttr("disabled");
    $(".setup-message .alert-danger").html(response.responseJSON.statusMessage);
    $(".setup-message .alert-danger").addClass("activeStatus");
  }
};

$(function name() {
  $("#submitBtn").on("click", function () {
    $(".setup-message .alert").removeClass("activeStatus");
    $("#submitBtn").attr("disabled", " ");
    $(".proceedText").css("display", "none");
    $(".proceedLoading").css("display", "block");
    var setupData = {
      postgresDBUrl: $("#inputDatabaseURL").val(),
      postgresDBPort: parseInt($("#inputDatabasePort").val()),
      postgresDBName: $("#inputDatabaseName").val(),
      postgresDBSchema: $("#inputDatabaseSchema").val(),
      postgresUsername: $("#inputDatabaseUsername").val(),
      postgresPassword: $("#inputDatabasePassword").val(),
      ersUsername: $("#inputFMUsername").val(),
      ersPassword: $("#inputFMPassword").val(),
      ersFirstname: $("#inputFMFirst").val(),
      ersLastname: $("#inputFMLast").val(),
      ersEmail: $("#inputFMEmail").val(),
    };
    $.ajax({
      type: "POST",
      url: "/api/setup",
      contentType: "application/json; charset=utf-8",
      data: JSON.stringify(setupData),
      dataType: "json",
      success: function (response) {
        setupResult(response);
      },
      error: function (response) {
        setupResult(response);
      },
    });
  });
});
*/

let deleteAllCookies = () => {
  console.log("clearing cookies");
  var cookies = document.cookie.split(";");

  for (var i = 0; i < cookies.length; i++) {
    var cookie = cookies[i];
    var eqPos = cookie.indexOf("=");
    var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
  }
}

let doSetup = async (param) => {
  // Hide alerts
  let alertElems = document.getElementsByClassName("alert");
  for (let Elems of alertElems) {
    Elems.classList.remove("activeStatus");
  }
  document.getElementById("submitBtn").setAttribute("disabled", "");
  document.getElementById("proceedText").classList.add("hideElem");
  document.getElementById("proceedLoading").classList.remove("hideElem");

  // get form data
  let setupData = {
    postgresDBUrl: document.getElementById("inputDatabaseURL").value,
    postgresDBPort: parseInt(
      document.getElementById("inputDatabasePort").value
    ),
    postgresDBName: document.getElementById("inputDatabaseName").value,
    postgresDBSchema: document.getElementById("inputDatabaseSchema").value,
    postgresUsername: document.getElementById("inputDatabaseUsername").value,
    postgresPassword: document.getElementById("inputDatabasePassword").value,
    ersUsername: document.getElementById("inputFMUsername").value,
    ersPassword: document.getElementById("inputFMPassword").value,
    ersFirstname: document.getElementById("inputFMFirst").value,
    ersLastname: document.getElementById("inputFMLast").value,
    ersEmail: document.getElementById("inputFMEmail").value,
  };

  await fetch("/api/setup", {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json; charset=utf-8",
    },
    body: JSON.stringify(setupData),
  })
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      document.getElementById("proceedText").classList.remove("hideElem");
      document.getElementById("proceedLoading").classList.add("hideElem");

      if (data.statusSuccess) {
        document
          .getElementById("setupAlertSuccess")
          .classList.add("activeStatus");
        // location replace in 3 seconds
        setTimeout(function () {
          window.location.replace("./");
        }, 1000);
      } else {
        document.getElementById("submitBtn").removeAttribute("disabled");
        document.getElementById("setupAlertFailed").innerHTML =
          data.statusMessage;
        document
          .getElementById("setupAlertFailed")
          .classList.add("activeStatus");
      }
    });
};

document.addEventListener("DOMContentLoaded", function (event) {
  // document.getElementById("submitBtn").addEventListener("click", doSetup);
  const forms = document.querySelectorAll('.needs-validation');
  const password1 = document.getElementById('inputFMPassword');
  const password2 = document.getElementById('confirmPassword');
  Array.from(forms).forEach(form => {
    form.addEventListener('submit', event => {
      event.preventDefault();
      event.stopPropagation();
      forms[0].classList.remove('invalid-password');
      forms[0].classList.remove('valid-password');
      if (form.checkValidity()) {
        if(password1.value != password2.value) {
          forms[0].classList.add('invalid-password');
        } else {
          doSetup();
        }
      }
      form.classList.add('was-validated')
    }, false);
    form.addEventListener('keyup', event => {
      event.preventDefault();
      event.stopPropagation();
      if (event.target.type == 'password') {
        forms[0].classList.remove('invalid-password');
        forms[0].classList.remove('valid-password');
        if (password1.value.length > 0 && password2.value.length > 0) {
          if (password1.value != password2.value) {
            forms[0].classList.add('invalid-password');
          } else {
            forms[0].classList.add('valid-password');
          }
        } else {
          forms[0].classList.add('invalid-password');
        }
      }
    });
  })
  deleteAllCookies();
});
