document.addEventListener('DOMContentLoaded', function () {
  // 1. Preloader Dismissal (safely hide on page ready)
  const preloader = document.getElementById('preloader');
  function hidePreloader() {
    if (preloader && !preloader.classList.contains('hide')) {
      preloader.classList.add('hide');
      setTimeout(() => {
        preloader.style.display = 'none';
      }, 500);
    }
  }

  // Hide as soon as loaded, with a safety timeout of 400ms so it never hangs
  window.addEventListener('load', hidePreloader);
  setTimeout(hidePreloader, 400);

  // 2. Lucide Icons initialization safely
  if (window.lucide && typeof window.lucide.createIcons === 'function') {
    window.lucide.createIcons();
  }

  // 3. Scroll Progress Indicator
  const scrollProgressBar = document.getElementById('scrollProgress');
  window.addEventListener('scroll', function () {
    if (scrollProgressBar) {
      const scrollTop = window.scrollY || document.documentElement.scrollTop;
      const docHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
      const scrollPercent = docHeight > 0 ? (scrollTop / docHeight) * 100 : 0;
      scrollProgressBar.style.width = scrollPercent + '%';
    }
  }, { passive: true });

  // 4. Header Shadow on Scroll
  const header = document.querySelector('.main-header');
  window.addEventListener('scroll', function () {
    if (header) {
      if (window.scrollY > 10) {
        header.classList.add('header-scrolled');
      } else {
        header.classList.remove('header-scrolled');
      }
    }
  }, { passive: true });

  // 5. Back To Top Button
  const backToTopBtn = document.getElementById('backToTop');
  if (backToTopBtn) {
    window.addEventListener('scroll', function () {
      if (window.scrollY > 300) {
        backToTopBtn.classList.add('visible');
      } else {
        backToTopBtn.classList.remove('visible');
      }
    }, { passive: true });

    backToTopBtn.addEventListener('click', function () {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    });
  }

  // 6. Mobile Menu Toggle
  const mobileToggle = document.getElementById('mobileMenuBtn');
  const mainNav = document.getElementById('mainNav');

  if (mobileToggle && mainNav) {
    mobileToggle.addEventListener('click', function (e) {
      e.stopPropagation();
      const isOpen = mainNav.classList.toggle('nav-open');
      mobileToggle.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
    });

    // Close mobile menu when clicking outside
    document.addEventListener('click', function (e) {
      if (!mainNav.contains(e.target) && !mobileToggle.contains(e.target)) {
        mainNav.classList.remove('nav-open');
        mobileToggle.setAttribute('aria-expanded', 'false');
      }
    });

    // Close when clicking a nav link
    const navLinks = mainNav.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
      link.addEventListener('click', function () {
        mainNav.classList.remove('nav-open');
        mobileToggle.setAttribute('aria-expanded', 'false');
      });
    });
  }

  // 7. Dark Mode Toggle
  const darkModeToggle = document.getElementById('darkModeToggle');
  const savedTheme = localStorage.getItem('jobportal_theme');

  if (savedTheme === 'dark') {
    document.documentElement.setAttribute('data-theme', 'dark');
  }

  if (darkModeToggle) {
    darkModeToggle.addEventListener('click', function () {
      const currentTheme = document.documentElement.getAttribute('data-theme');
      const newTheme = currentTheme === 'dark' ? 'light' : 'dark';

      if (newTheme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
      } else {
        document.documentElement.removeAttribute('data-theme');
      }
      localStorage.setItem('jobportal_theme', newTheme);

      // Re-run lucide icons if needed
      if (window.lucide && typeof window.lucide.createIcons === 'function') {
        window.lucide.createIcons();
      }
    });
  }

  // 8. Job Filter Tabs
  const tabBtns = document.querySelectorAll('.job-tab-btn');
  const jobCards = document.querySelectorAll('.job-card');

  tabBtns.forEach(function (btn) {
    btn.addEventListener('click', function () {
      tabBtns.forEach(function (b) {
        b.classList.remove('active');
        b.setAttribute('aria-selected', 'false');
      });
      this.classList.add('active');
      this.setAttribute('aria-selected', 'true');

      const filter = this.getAttribute('data-filter');
      jobCards.forEach(function (card) {
        if (filter === 'all' || card.getAttribute('data-category') === filter) {
          card.style.display = 'flex';
        } else {
          card.style.display = 'none';
        }
      });
    });
  });

  // 9. Bookmark / Save Job Button
  const saveButtons = document.querySelectorAll('.job-save-btn');
  saveButtons.forEach(btn => {
    btn.addEventListener('click', function (e) {
      e.preventDefault();
      this.classList.toggle('saved');
    });
  });

  // 10. Scroll Reveal Animation (Intersection Observer)
  const revealElements = document.querySelectorAll('.reveal');
  if ('IntersectionObserver' in window && revealElements.length > 0) {
    const revealObserver = new IntersectionObserver((entries, observer) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('revealed');
          observer.unobserve(entry.target);
        }
      });
    }, {
      rootMargin: '0px 0px -40px 0px',
      threshold: 0.1
    });

    revealElements.forEach(el => revealObserver.observe(el));
  } else {
    revealElements.forEach(el => el.classList.add('revealed'));
  }

  // 11. Count-up Animation for Stats
  const statNumbers = document.querySelectorAll('.stat-number[data-count]');
  if ('IntersectionObserver' in window && statNumbers.length > 0) {
    let countersStarted = false;
    const statsSection = document.querySelector('.stats-section');

    const statsObserver = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && !countersStarted) {
          countersStarted = true;
          statNumbers.forEach(stat => {
            const target = parseInt(stat.getAttribute('data-count'), 10);
            const duration = 1500;
            const startTime = performance.now();

            function updateCounter(currentTime) {
              const elapsed = currentTime - startTime;
              const progress = Math.min(elapsed / duration, 1);
              // ease-out cubic
              const easeOut = 1 - Math.pow(1 - progress, 3);
              const currentVal = Math.floor(easeOut * target);

              // Format with dots or plus sign
              if (target >= 1000) {
                stat.textContent = currentVal.toLocaleString('vi-VN') + '+';
              } else {
                stat.textContent = currentVal + '+';
              }

              if (progress < 1) {
                requestAnimationFrame(updateCounter);
              } else {
                if (target >= 1000) {
                  stat.textContent = target.toLocaleString('vi-VN') + '+';
                } else {
                  stat.textContent = target + '+';
                }
              }
            }

            requestAnimationFrame(updateCounter);
          });
          statsObserver.disconnect();
        }
      });
    }, { threshold: 0.2 });

    if (statsSection) {
      statsObserver.observe(statsSection);
    }
  }
});
