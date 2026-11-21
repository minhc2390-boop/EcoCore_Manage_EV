document.addEventListener("DOMContentLoaded", function () {
  var paymentModal = document.getElementById("paymentModal");
  var packageModal = document.getElementById("packageModal");

  var openPaymentBtns = document.querySelectorAll(".btn-open-payment");
  var modalCloseBtns = document.querySelectorAll(".modal-close, .btn-close-modal");

  var modalPackageName = document.getElementById("modalPackageName");
  var modalPackagePrice = document.getElementById("modalPackagePrice");

  openPaymentBtns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      var packageName = btn.getAttribute("data-package");
      var packagePrice = btn.getAttribute("data-price");
      if (modalPackageName) {
        modalPackageName.textContent = packageName;
      }
      if (modalPackagePrice) {
        modalPackagePrice.textContent = packagePrice;
      }
      if (paymentModal) {
        paymentModal.classList.add("show");
      }
    });
  });

  var openPackageBtn = document.getElementById("btnOpenPackageModal");
  if (openPackageBtn && packageModal) {
    openPackageBtn.addEventListener("click", function () {
      packageModal.classList.add("show");
    });
  }

  modalCloseBtns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      if (paymentModal) paymentModal.classList.remove("show");
      if (packageModal) packageModal.classList.remove("show");
    });
  });

  window.addEventListener("click", function (e) {
    if (e.target === paymentModal) paymentModal.classList.remove("show");
    if (e.target === packageModal) packageModal.classList.remove("show");
  });

  var btnConfirmPayment = document.getElementById("btnConfirmPayment");
  if (btnConfirmPayment) {
    btnConfirmPayment.addEventListener("click", function () {
      alert("Yêu cầu thanh toán của bạn đã được gửi. Quản trị viên sẽ kiểm tra và kích hoạt gói trong vòng 15 phút.");
      if (paymentModal) paymentModal.classList.remove("show");
    });
  }

  var btnSavePackage = document.getElementById("btnSavePackage");
  if (btnSavePackage) {
    btnSavePackage.addEventListener("click", function () {
      alert("Đã lưu gói dịch vụ thành công.");
      if (packageModal) packageModal.classList.remove("show");
    });
  }

  var filterBtns = document.querySelectorAll(".filter-btn");
  var tableRows = document.querySelectorAll(".transaction-row");
  var searchInput = document.getElementById("transactionSearch");

  function filterTransactions() {
    var activeFilter = document.querySelector(".filter-btn.active");
    var filterValue = activeFilter ? activeFilter.getAttribute("data-filter") : "ALL";
    var searchText = searchInput ? searchInput.value.trim().toLowerCase() : "";

    tableRows.forEach(function (row) {
      var status = row.getAttribute("data-status");
      var text = row.textContent.toLowerCase();

      var matchFilter = filterValue === "ALL" || status === filterValue;
      var matchSearch = searchText === "" || text.indexOf(searchText) !== -1;

      if (matchFilter && matchSearch) {
        row.style.display = "";
      } else {
        row.style.display = "none";
      }
    });
  }

  filterBtns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      filterBtns.forEach(function (b) {
        b.classList.remove("active");
      });
      btn.classList.add("active");
      filterTransactions();
    });
  });

  if (searchInput) {
    searchInput.addEventListener("input", filterTransactions);
  }

  var approveBtns = document.querySelectorAll(".btn-approve");
  approveBtns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      var row = btn.closest("tr");
      var badge = row.querySelector(".badge");
      if (badge) {
        badge.className = "badge badge-approved";
        badge.textContent = "Đã duyệt";
      }
      row.setAttribute("data-status", "APPROVED");
      var actionCell = btn.parentElement;
      actionCell.innerHTML = '<span style="font-size: 13px; color: #64748B;">Đã xử lý</span>';
      alert("Đã duyệt giao dịch thành công.");
    });
  });

  var rejectBtns = document.querySelectorAll(".btn-reject");
  rejectBtns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      var row = btn.closest("tr");
      var badge = row.querySelector(".badge");
      if (badge) {
        badge.className = "badge badge-rejected";
        badge.textContent = "Từ chối";
      }
      row.setAttribute("data-status", "REJECTED");
      var actionCell = btn.parentElement;
      actionCell.innerHTML = '<span style="font-size: 13px; color: #64748B;">Đã xử lý</span>';
      alert("Đã từ chối giao dịch.");
    });
  });

  var toggleStatusBtns = document.querySelectorAll(".btn-toggle-status");
  toggleStatusBtns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      var row = btn.closest("tr");
      var badge = row.querySelector(".package-status-badge");
      if (badge.textContent.trim() === "Đang bán") {
        badge.textContent = "Tạm ẩn";
        badge.className = "badge badge-inactive package-status-badge";
        btn.textContent = "Mở bán";
      } else {
        badge.textContent = "Đang bán";
        badge.className = "badge badge-active package-status-badge";
        btn.textContent = "Tạm ẩn";
      }
    });
  });
});
