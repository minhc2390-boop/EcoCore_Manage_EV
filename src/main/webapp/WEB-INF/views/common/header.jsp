<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EVCSMS - Quản Lý Trạm Sạc Xe Điện</title>
    <!-- Bootstrap 5 CSS CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css' />">
</head>
<body class="bg-light">
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold" href="<c:url value='/stations' />">
            <i class="bi bi-ev-station-fill me-2"></i>EVCSMS - Quản Lý Trạm Sạc
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link active" href="<c:url value='/stations' />"><i class="bi bi-list-ul me-1"></i>Danh sách</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white fw-semibold" href="<c:url value='/stations/new' />"><i class="bi bi-plus-circle me-1"></i>Thêm mới</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<div class="container">

