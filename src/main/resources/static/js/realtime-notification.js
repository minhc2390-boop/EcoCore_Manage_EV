/**
 * Hệ thống Thông Báo Thời Gian Thực (Real-time SSE Notification Client)
 * Tương thích Server-Sent Events (SSE) chuẩn HTML5
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
(function () {
  'use strict';

  let eventSource = null;
  let unreadCount = 0;

  // Phát âm thanh bip ngắn êm tai khi có thông báo mới (Web Audio API - không cần file mp3)
  function playNotificationSound() {
    try {
      const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      const osc = audioCtx.createOscillator();
      const gainNode = audioCtx.createGain();
      osc.type = 'sine';
      osc.frequency.setValueAtTime(587.33, audioCtx.currentTime); // Note D5
      osc.frequency.exponentialRampToValueAtTime(880, audioCtx.currentTime + 0.15); // Note A5
      gainNode.gain.setValueAtTime(0.15, audioCtx.currentTime);
      gainNode.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + 0.25);
      osc.connect(gainNode);
      gainNode.connect(audioCtx.destination);
      osc.start();
      osc.stop(audioCtx.currentTime + 0.25);
    } catch (e) {
      // Bỏ qua nếu trình duyệt chặn autoplay
    }
  }

  // Khởi tạo Toast Container góc phải dưới màn hình
  function ensureToastContainer() {
    let container = document.getElementById('realtimeToastContainer');
    if (!container) {
      container = document.createElement('div');
      container.id = 'realtimeToastContainer';
      container.style.cssText = `
        position: fixed;
        bottom: 24px;
        right: 24px;
        z-index: 99999;
        display: flex;
        flex-direction: column;
        gap: 12px;
        max-width: 380px;
        pointer-events: none;
      `;
      document.body.appendChild(container);
    }
    return container;
  }

  // Hiển thị popup Toast nổi góc phải
  function showToast(notif) {
    playNotificationSound();
    const container = ensureToastContainer();

    const toast = document.createElement('div');
    toast.className = 'realtime-toast-card';
    toast.style.cssText = `
      background: #ffffff;
      color: #1e293b;
      border-radius: 12px;
      box-shadow: 0 10px 25px -5px rgba(0,0,0,0.15), 0 8px 10px -6px rgba(0,0,0,0.1);
      border-left: 5px solid #0284c7;
      padding: 14px 16px;
      display: flex;
      gap: 12px;
      align-items: flex-start;
      pointer-events: auto;
      transform: translateX(120%);
      transition: transform 0.35s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.35s ease;
      opacity: 0;
      cursor: pointer;
    `;

    // Màu viền theo loại
    if (notif.type === 'success') {
      toast.style.borderLeftColor = '#10b981';
    } else if (notif.type === 'warning') {
      toast.style.borderLeftColor = '#f59e0b';
    } else if (notif.type === 'primary') {
      toast.style.borderLeftColor = '#3b82f6';
    }

    toast.innerHTML = `
      <div style="font-size: 20px; line-height: 1; color: ${toast.style.borderLeftColor}; margin-top: 2px;">
        <i class="bi bi-bell-fill"></i>
      </div>
      <div style="flex: 1; min-width: 0;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
          <strong style="font-size: 13.5px; font-weight: 700; color: #0f172a;">${escapeHtml(notif.title || 'Thông báo mới')}</strong>
          <span style="font-size: 11px; color: #94a3b8; margin-left: 8px;">Vừa xong</span>
        </div>
        <p style="font-size: 12.5px; margin: 0 0 6px 0; color: #475569; line-height: 1.4;">${escapeHtml(notif.message || '')}</p>
        ${notif.targetUrl ? `<a href="${notif.targetUrl}" style="font-size: 11.5px; font-weight: 600; color: #0284c7; text-decoration: none;">Xem chi tiết &rarr;</a>` : ''}
      </div>
      <button type="button" class="btn-close-toast" style="background: none; border: none; font-size: 16px; color: #94a3b8; cursor: pointer; padding: 0 4px; line-height: 1;" title="Đóng">&times;</button>
    `;

    // Xử lý đóng toast
    const closeBtn = toast.querySelector('.btn-close-toast');
    closeBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      removeToast(toast);
    });

    if (notif.targetUrl) {
      toast.addEventListener('click', () => {
        window.location.href = notif.targetUrl;
      });
    }

    container.appendChild(toast);

    // Kích hoạt animation trượt vào
    requestAnimationFrame(() => {
      toast.style.transform = 'translateX(0)';
      toast.style.opacity = '1';
    });

    // Tự động biến mất sau 6 giây
    setTimeout(() => {
      removeToast(toast);
    }, 6000);
  }

  function removeToast(toast) {
    if (!toast) return;
    toast.style.transform = 'translateX(120%)';
    toast.style.opacity = '0';
    setTimeout(() => {
      if (toast.parentElement) {
        toast.parentElement.removeChild(toast);
      }
    }, 350);
  }

  // Thêm thông báo vào dropdown list
  function prependToDropdown(notif) {
    const list = document.getElementById('notifList');
    if (!list) return;

    const empty = document.getElementById('notifEmpty');
    if (empty) {
      empty.style.display = 'none';
    }

    const item = document.createElement('div');
    item.className = 'notif-item p-2 border-bottom';
    item.style.cssText = `
      transition: background-color 0.2s ease;
      border-radius: 8px;
      margin-bottom: 4px;
      background-color: ${notif.unread ? '#f0f9ff' : 'transparent'};
      cursor: pointer;
    `;

    item.innerHTML = `
      <div class="d-flex align-items-start gap-2">
        <span class="badge bg-primary-subtle text-primary p-1.5 rounded-circle mt-1" style="font-size: 10px;">
          <i class="bi bi-bell"></i>
        </span>
        <div class="flex-grow-1" style="min-width: 0;">
          <div class="d-flex justify-content-between align-items-baseline">
            <span class="fw-bold text-dark text-truncate" style="font-size: 13px;">${escapeHtml(notif.title || '')}</span>
            <small class="text-muted" style="font-size: 10px;">${escapeHtml(notif.createdAt || '')}</small>
          </div>
          <p class="mb-0 text-muted" style="font-size: 12px; line-height: 1.35;">${escapeHtml(notif.message || '')}</p>
        </div>
      </div>
    `;

    if (notif.targetUrl) {
      item.addEventListener('click', () => {
        window.location.href = notif.targetUrl;
      });
    }

    list.insertBefore(item, list.firstChild);
  }

  // Cập nhật số đỏ trên chuông thông báo
  function updateBadge(delta) {
    unreadCount = Math.max(0, unreadCount + delta);
    const badge = document.getElementById('notifBadge');
    if (badge) {
      if (unreadCount > 0) {
        badge.textContent = unreadCount > 99 ? '99+' : unreadCount;
        badge.classList.remove('d-none');
      } else {
        badge.classList.add('d-none');
      }
    }
  }

  // Tải danh sách thông báo gần đây khi tải trang
  function loadRecentNotifications() {
    fetch('/api/notifications/recent')
      .then(res => res.json())
      .then(data => {
        if (Array.isArray(data) && data.length > 0) {
          data.forEach(notif => {
            prependToDropdown(notif);
            if (notif.unread) {
              unreadCount++;
            }
          });
          updateBadge(0);
        }
      })
      .catch(err => console.debug('Không tải được thông báo gần đây:', err));
  }

  // Khởi tạo luồng SSE thời gian thực
  function initSSE() {
    if (typeof EventSource === 'undefined') {
      console.warn('Trình duyệt không hỗ trợ Server-Sent Events.');
      return;
    }

    if (eventSource) {
      eventSource.close();
    }

    eventSource = new EventSource('/api/notifications/stream');

    eventSource.addEventListener('CONNECTED', function (e) {
      console.log('⚡ [SSE] Đã kết nối thành công tới máy chủ Real-time UniWork.');
    });

    eventSource.addEventListener('notification', function (e) {
      try {
        const notif = JSON.parse(e.data);
        console.log('⚡ [SSE Notification]:', notif);
        showToast(notif);
        prependToDropdown(notif);
        updateBadge(1);
      } catch (err) {
        console.error('Lỗi phân giải dữ liệu thông báo:', err);
      }
    });

    eventSource.onerror = function () {
      console.debug('⚡ [SSE] Mất kết nối tạm thời, trình duyệt sẽ tự động kết nối lại...');
    };
  }

  // Thoát ký tự HTML chống XSS
  function escapeHtml(str) {
    return str
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  // Khởi động khi trang web nạp xong
  document.addEventListener('DOMContentLoaded', function () {
    loadRecentNotifications();
    initSSE();

    // Hỗ trợ toggle dropdown tùy biến (dành cho trang index.html không dùng Bootstrap dropdown)
    const notifBtn = document.getElementById('notifDropdownBtn');
    const notifMenu = document.getElementById('notifDropdownMenu');
    const notifClose = document.getElementById('btnCloseNotif');

    function closeNotifDropdown() {
      if (notifMenu) {
        notifMenu.classList.add('d-none');
        notifMenu.style.display = 'none';
      }
    }

    if (notifBtn && notifMenu) {
      notifBtn.addEventListener('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        if (notifMenu.classList.contains('d-none') || notifMenu.style.display === 'none') {
          notifMenu.classList.remove('d-none');
          notifMenu.style.display = 'block';
        } else {
          notifMenu.classList.add('d-none');
          notifMenu.style.display = 'none';
        }
      });

      // Nút Đóng (✕)
      if (notifClose) {
        notifClose.addEventListener('click', function (e) {
          e.preventDefault();
          e.stopPropagation();
          closeNotifDropdown();
        });
      }

      // Đóng khi click ra bên ngoài bảng thông báo
      document.addEventListener('click', function (e) {
        if (!notifMenu.contains(e.target) && !notifBtn.contains(e.target) && e.target !== notifBtn) {
          closeNotifDropdown();
        }
      });

      // Đóng khi nhấn phím ESC
      document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' || e.key === 'Esc') {
          closeNotifDropdown();
        }
      });
    }

    // Nút "Đã đọc hết"
    const btnClear = document.getElementById('btnClearNotif');
    if (btnClear) {
      btnClear.addEventListener('click', function (e) {
        e.preventDefault();
        fetch('/api/notifications/mark-read', { method: 'POST' })
          .then(() => {
            unreadCount = 0;
            updateBadge(0);
            document.querySelectorAll('#notifList .notif-item').forEach(el => {
              el.style.backgroundColor = 'transparent';
            });
          });
      });
    }

    // Nút "Gửi thông báo test"
    const btnTest = document.getElementById('btnTestNotif');
    if (btnTest) {
      btnTest.addEventListener('click', function (e) {
        e.preventDefault();
        btnTest.disabled = true;
        btnTest.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Đang phát...';
        fetch('/api/notifications/test', { method: 'POST' })
          .then(res => res.json())
          .finally(() => {
            setTimeout(() => {
              btnTest.disabled = false;
              btnTest.innerHTML = '<i class="bi bi-lightning-charge-fill me-1"></i>Gửi thông báo test';
            }, 1000);
          });
      });
    }
  });

})();
