package com.uniwork.service;

import com.uniwork.entity.Employer;
import com.uniwork.entity.User;

import java.util.Optional;

/**
 * Service quản lý thông tin Nhà Tuyển Dụng / Doanh nghiệp
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
public interface EmployerService {

    Optional<Employer> findByUserId(Long userId);

    Employer getOrCreateEmployerForUser(User user);

    Employer getEmployerById(Long id);

    Employer saveEmployer(Employer employer);
}
