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
  let registerData = {
    ers_username: document.getElementById("inputUsername").value,
    ers_password: document.getElementById("inputPassword").value,
    user_first_name: document.getElementById("inputFirst").value,
    user_last_name: document.getElementById("inputLast").value,
    user_email: document.getElementById("inputEmail").value,
  };

  await fetch("/api/register", {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json; charset=utf-8",
    },
    body: JSON.stringify(registerData),
  })
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      document.getElementById("proceedText").classList.remove("hideElem");
      document.getElementById("proceedLoading").classList.add("hideElem");

      if (data.statusSuccess) {
        document
          .getElementById("registerAlertSuccess")
          .classList.add("activeStatus");
        // location replace in 3 seconds
        setTimeout(function () {
          window.location.replace("./");
        }, 1000);
      } else {
        document.getElementById("submitBtn").removeAttribute("disabled");
        document.getElementById("registerAlertFailed").innerHTML =
          data.statusMessage;
        document
          .getElementById("registerAlertFailed")
          .classList.add("activeStatus");
      }
    });
};

document.addEventListener("DOMContentLoaded", function (event) {
  document.getElementById("submitBtn").addEventListener("click", doSetup);
});
