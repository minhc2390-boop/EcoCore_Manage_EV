<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp" />

<div class="container py-5 text-center">
    <div class="card border-0 shadow-sm rounded-4 p-5 mx-auto bg-white" style="max-width: 600px;">
        <i class="bi bi-exclamation-triangle-fill fs-1 text-warning mb-3"></i>
        <h3 class="fw-bold mb-2">Đã Xảy Ra Lỗi</h3>
        <p class="text-secondary mb-4">${errorMessage != null ? errorMessage : 'Trang bạn tìm kiếm không tồn tại hoặc hệ thống đang bảo trì.'}</p>
        <div>
            <a href="<c:url value='/' />" class="btn btn-primary rounded-pill px-4 me-2">Về Trang Chủ</a>
            <a href="<c:url value='/jobs' />" class="btn btn-outline-secondary rounded-pill px-4">Tìm Việc Làm</a>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp" />
