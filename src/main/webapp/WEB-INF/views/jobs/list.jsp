<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp" />

<div class="container py-4">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="<c:url value='/' />" class="text-decoration-none">Trang Chủ</a></li>
            <li class="breadcrumb-item active" aria-current="page">Danh Sách Việc Làm</li>
        </ol>
    </nav>

    <!-- Filter Card Bar -->
    <div class="card border-0 shadow-sm rounded-4 p-3 mb-4 bg-white">
        <form action="<c:url value='/jobs' />" method="get" class="row g-2 align-items-center">
            <div class="col-md-4">
                <input type="text" name="keyword" class="form-control rounded-3" 
                       placeholder="Từ khóa (Chức danh, kỹ năng, địa điểm)..." value="${searchDTO.keyword}">
            </div>
            <div class="col-md-3">
                <select name="categoryId" class="form-select rounded-3">
                    <option value="">-- Tất cả ngành nghề --</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${searchDTO.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-3">
                <select name="jobType" class="form-select rounded-3">
                    <option value="">-- Hình thức làm việc --</option>
                    <option value="Part-time" ${searchDTO.jobType == 'Part-time' ? 'selected' : ''}>Bán thời gian (Part-time)</option>
                    <option value="Internship" ${searchDTO.jobType == 'Internship' ? 'selected' : ''}>Thực tập sinh (Internship)</option>
                    <option value="Remote" ${searchDTO.jobType == 'Remote' ? 'selected' : ''}>Việc làm từ xa (Remote)</option>
                </select>
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-primary w-100 rounded-3 fw-semibold">
                    <i class="bi bi-funnel me-1"></i>Lọc Tin
                </button>
            </div>
        </form>
    </div>

    <!-- Job Results Count -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="fw-bold mb-0">Tìm thấy <span class="text-primary">${jobs.size()}</span> việc làm phù hợp cho sinh viên</h5>
    </div>

    <!-- Job List -->
    <div class="row g-3">
        <c:choose>
            <c:when test="${empty jobs}">
                <div class="col-12 text-center py-5">
                    <div class="p-5 bg-white rounded-4 shadow-sm">
                        <i class="bi bi-inbox fs-1 text-muted"></i>
                        <h5 class="fw-bold mt-3 text-secondary">Không tìm thấy công việc nào phù hợp!</h5>
                        <p class="text-muted small">Hãy thử tìm kiếm với từ khóa khác hoặc xóa bớt bộ lọc.</p>
                        <a href="<c:url value='/jobs' />" class="btn btn-outline-primary btn-sm rounded-pill px-4">Xem tất cả việc làm</a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="job" items="${jobs}">
                    <div class="col-12">
                        <div class="card job-list-card border-0 shadow-sm rounded-4 p-4 transition bg-white">
                            <div class="row align-items-center g-3">
                                <div class="col-md-8">
                                    <div class="d-flex gap-2 mb-2">
                                        <span class="badge bg-primary bg-opacity-10 text-primary px-3 py-1 rounded-pill fw-semibold">
                                            ${job.jobType}
                                        </span>
                                        <span class="badge bg-info bg-opacity-10 text-info px-3 py-1 rounded-pill fw-semibold">
                                            ${job.category != null ? job.category.name : 'Chung'}
                                        </span>
                                    </div>
                                    <h5 class="fw-bold mb-1">
                                        <a href="<c:url value='/jobs/${job.id}' />" class="text-decoration-none text-dark hover-primary">
                                            ${job.title}
                                        </a>
                                    </h5>
                                    <p class="text-secondary small mb-2">
                                        <i class="bi bi-building me-1 text-primary"></i><strong>${job.employer != null ? job.employer.companyName : 'Doanh nghiệp'}</strong>
                                        <span class="mx-2">•</span>
                                        <i class="bi bi-geo-alt me-1 text-danger"></i>${job.location}
                                    </p>
                                    <div class="text-muted small">
                                        <i class="bi bi-clock-history me-1 text-warning"></i>${job.shiftSchedule}
                                    </div>
                                </div>
                                <div class="col-md-4 text-md-end">
                                    <div class="text-success fw-bold fs-5 mb-2">${job.salaryRange}</div>
                                    <a href="<c:url value='/jobs/${job.id}' />" class="btn btn-primary rounded-pill px-4 fw-medium shadow-sm">
                                        Xem & Ứng Tuyển <i class="bi bi-arrow-right ms-1"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
