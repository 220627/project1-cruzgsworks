/*
const setupResult = function (response) {
  console.log(response);
  $(".proceedText").css("display", "block");
  $(".proceedLoading").css("display", "none");
  if (response.statusSuccess) {
    $(".login-message .alert-success").addClass("activeStatus");
    // location replace in 3 seconds
    
    setTimeout(function () {
      window.location.replace("./");
    }, 3000);
    
  } else {
    $("#submitBtn").removeAttr("disabled");
    $(".login-message .alert-danger").html(response.responseJSON.statusMessage);
    $(".login-message .alert-danger").addClass("activeStatus");
  }
};

$(function name() {
  $("#submitBtn").on("click", function () {
    $(".login-message .alert").removeClass("activeStatus");
    $("#submitBtn").attr("disabled", " ");
    $(".proceedText").css("display", "none");
    $(".proceedLoading").css("display", "block");
    var setupData = {
      ers_username: $("#inputUsername").val(),
      ers_password: $("#inputPassword").val(),
    };
    $.ajax({
      type: "POST",
      url: "/api/login",
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

let doLogin = async () => {
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
    ers_username: document.getElementById("inputUsername").value,
    ers_password: document.getElementById("inputPassword").value,
  };

  await fetch("/api/login", {
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
        document.getElementById("setupAlertSuccess").classList.add("activeStatus");
        // location replace in 3 seconds
        setTimeout(function () {
          window.location.replace("./");
        }, 1000);
      } else {
        document.getElementById("submitBtn").removeAttribute("disabled");
        document.getElementById("setupAlertFailed").innerHTML = data.statusMessage;
        document.getElementById("setupAlertFailed").classList.add("activeStatus");
      }
    });
};

document.addEventListener("DOMContentLoaded", function (event) {
  /*
  document.getElementById("submitBtn").addEventListener("click", doLogin);
  
  document.getElementById("registerBtn").addEventListener("click", function () {
    window.location.href = "./register";
  });
  
  document.getElementById("inputUsername").addEventListener("keypress", function (event) {
    if (event.key === "Enter") {
      event.preventDefault;
      doLogin();
    }
  });
  document.getElementById("inputPassword").addEventListener("keypress", function (event) {
    if (event.key === "Enter") {
      event.preventDefault;
      doLogin();
    }
  });
  */
  const forms = document.querySelectorAll('.needs-validation');
  Array.from(forms).forEach(form => {
    form.addEventListener('submit', event => {
      event.preventDefault()
      event.stopPropagation()
      if (form.checkValidity()) {
        doLogin();
      }
      form.classList.add('was-validated')
    }, false)
  })
});
