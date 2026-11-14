<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp" />

<!-- Hero Banner Search Section -->
<section class="hero-section text-white py-5 position-relative overflow-hidden">
    <div class="container py-4 position-relative z-1">
        <div class="row align-items-center g-4">
            <div class="col-lg-8 text-center text-lg-start">
                <span class="badge bg-warning text-dark px-3 py-2 rounded-pill fw-semibold mb-3">
                    <i class="bi bi-stars me-1"></i>Việc Làm Đã Xác Thực Dành Riêng Cho Sinh Viên
                </span>
                <h1 class="display-4 fw-extrabold mb-3">
                    Tìm Việc Làm Thêm & Thực Tập <br class="d-none d-md-block">
                    <span class="text-warning">Linh Hoạt Theo Lịch Học</span> Của Bạn
                </h1>
                <p class="lead text-white-50 mb-4 pe-lg-5">
                    Hơn 1.500+ cơ hội việc làm part-time, ca xoay, thực tập sinh doanh nghiệp với mức lương minh bạch và đảm bảo quyền lợi.
                </p>

                <!-- Search Card Form -->
                <div class="card p-3 shadow-lg border-0 rounded-4 bg-white text-dark">
                    <form action="<c:url value='/jobs' />" method="get" class="row g-2 align-items-center">
                        <div class="col-md-5">
                            <div class="input-group">
                                <span class="input-group-text bg-transparent border-0 text-primary"><i class="bi bi-search"></i></span>
                                <input type="text" name="keyword" class="form-control border-0 shadow-none ps-0" 
                                       placeholder="Tên công việc (vd: Barista, Thực tập Java, Gia sư)...">
                            </div>
                        </div>
                        <div class="col-md-4 border-start border-light-subtle">
                            <div class="input-group">
                                <span class="input-group-text bg-transparent border-0 text-primary"><i class="bi bi-grid"></i></span>
                                <select name="categoryId" class="form-select border-0 shadow-none ps-0">
                                    <option value="">-- Tất cả ngành nghề --</option>
                                    <c:forEach var="cat" items="${categories}">
                                        <option value="${cat.id}">${cat.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-primary w-100 rounded-3 py-2 fw-semibold shadow-sm">
                                <i class="bi bi-search me-1"></i>Tìm Kiếm
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            <div class="col-lg-4 d-none d-lg-block text-center">
                <div class="hero-stats-card p-4 rounded-4 shadow-lg text-start">
                    <h5 class="fw-bold text-white mb-3"><i class="bi bi-lightning-charge-fill text-warning me-2"></i>Vì Sao Sinh Viên Chọn StudentJob?</h5>
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <div class="icon-circle bg-primary bg-opacity-25 text-warning"><i class="bi bi-calendar-check fs-5"></i></div>
                        <div>
                            <h6 class="mb-0 fw-semibold text-white">Đăng ký ca theo lịch học</h6>
                            <small class="text-white-50">Linh động đổi ca khi bận lịch thi cử</small>
                        </div>
                    </div>
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <div class="icon-circle bg-primary bg-opacity-25 text-warning"><i class="bi bi-shield-check fs-5"></i></div>
                        <div>
                            <h6 class="mb-0 fw-semibold text-white">Xác thực doanh nghiệp 100%</h6>
                            <small class="text-white-50">Không lo bẫy đa cấp hay lừa đảo</small>
                        </div>
                    </div>
                    <div class="d-flex align-items-center gap-3">
                        <div class="icon-circle bg-primary bg-opacity-25 text-warning"><i class="bi bi-cash-stack fs-5"></i></div>
                        <div>
                            <h6 class="mb-0 fw-semibold text-white">Lương thưởng minh bạch</h6>
                            <small class="text-white-50">Rõ ràng theo giờ hoặc theo tháng</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Job Categories Section -->
<section class="py-5 bg-white">
    <div class="container py-2">
        <div class="d-flex justify-content-between align-items-end mb-4">
            <div>
                <span class="text-primary fw-bold text-uppercase small">Khám Phá Theo Nhóm</span>
                <h3 class="fw-bold mb-0">Ngành Nghề Phổ Biến Cho Sinh Viên</h3>
            </div>
            <a href="<c:url value='/jobs' />" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                Xem tất cả <i class="bi bi-arrow-right ms-1"></i>
            </a>
        </div>
        <div class="row g-3">
            <c:forEach var="cat" items="${categories}">
                <div class="col-lg-3 col-md-6">
                    <a href="<c:url value='/jobs?categoryId=${cat.id}' />" class="text-decoration-none">
                        <div class="card category-card h-100 border-0 shadow-sm p-3 rounded-4 transition">
                            <div class="d-flex align-items-center gap-3">
                                <div class="category-icon p-3 rounded-3 bg-light text-primary fs-3">
                                    <i class="bi ${cat.icon != null ? cat.icon : 'bi-briefcase'}"></i>
                                </div>
                                <div>
                                    <h6 class="fw-bold text-dark mb-1">${cat.name}</h6>
                                    <small class="text-muted">${cat.description}</small>
                                </div>
                            </div>
                        </div>
                    </a>
                </div>
            </c:forEach>
        </div>
    </div>
</section>

<!-- Featured Jobs Section -->
<section class="py-5">
    <div class="container py-2">
        <div class="d-flex justify-content-between align-items-end mb-4">
            <div>
                <span class="text-primary fw-bold text-uppercase small">Tin Tuyển Dụng Nổi Bật</span>
                <h3 class="fw-bold mb-0">Việc Làm Mới Nhất Dành Cho Sinh Viên</h3>
            </div>
            <a href="<c:url value='/jobs' />" class="btn btn-primary btn-sm rounded-pill px-3">
                Xem thêm việc làm <i class="bi bi-chevron-right ms-1"></i>
            </a>
        </div>
        <div class="row g-4">
            <c:forEach var="job" items="${latestJobs}">
                <div class="col-lg-4 col-md-6">
                    <div class="card job-card h-100 border-0 shadow-sm rounded-4 p-4 d-flex flex-column justify-content-between">
                        <div>
                            <div class="d-flex justify-content-between align-items-start mb-3">
                                <span class="badge bg-primary bg-opacity-10 text-primary px-3 py-1.5 rounded-pill fw-semibold">
                                    <i class="bi bi-clock me-1"></i>${job.jobType}
                                </span>
                                <span class="text-success fw-bold small">
                                    <i class="bi bi-cash me-1"></i>${job.salaryRange}
                                </span>
                            </div>
                            <h5 class="fw-bold text-dark mb-2">
                                <a href="<c:url value='/jobs/${job.id}' />" class="text-decoration-none text-dark hover-primary">
                                    ${job.title}
                                </a>
                            </h5>
                            <p class="text-secondary small mb-2">
                                <i class="bi bi-building me-1 text-primary"></i>
                                <strong>${job.employer != null ? job.employer.companyName : 'Doanh nghiệp đối tác'}</strong>
                            </p>
                            <p class="text-muted small mb-3">
                                <i class="bi bi-geo-alt me-1 text-danger"></i>${job.location}
                            </p>
                            <div class="p-2.5 bg-light rounded-3 mb-3 small text-secondary">
                                <i class="bi bi-calendar2-range me-1 text-info"></i>
                                <strong>Lịch ca:</strong> ${job.shiftSchedule}
                            </div>
                        </div>
                        <div class="d-flex justify-content-between align-items-center pt-3 border-top">
                            <span class="text-muted small"><i class="bi bi-eye me-1"></i>${job.viewCount} lượt xem</span>
                            <a href="<c:url value='/jobs/${job.id}' />" class="btn btn-outline-primary btn-sm rounded-pill px-3 fw-medium">
                                Chi tiết <i class="bi bi-arrow-right ms-1"></i>
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</section>

<jsp:include page="common/footer.jsp" />
