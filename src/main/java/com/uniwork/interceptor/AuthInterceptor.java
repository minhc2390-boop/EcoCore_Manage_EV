package com.uniwork.interceptor;

import com.uniwork.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//Bộ lọc kiểm tra phiên đăng nhập của người dùng
//author-Cao Trần-Duy-Minh-PC11166

@Component
@Slf4j

public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        // chưa đăng nhập, chặn lại và đưa về login
        if (currentUser == null) {
            String requestUri = request.getRequestURI();
            log.warn("Truy cập trái phép vào URI: {} -> Chuyển hướng về /login", requestUri);

            // lưu lại đường dẫn người dùng đang muốn vào sau khi login xong có thể mở lại
            session.setAttribute("redirectAfterLogin", requestUri);

            // chuyển hướng về trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/login");
            return false; // chặn không cho request đi tiếp vào controller
        }
        // nếu đăng nhập rồi cho phép đi tiếp vào controller
        return true;
    }
}