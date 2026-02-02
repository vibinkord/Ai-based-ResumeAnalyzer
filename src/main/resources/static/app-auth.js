/**
 * AI Resume Analyzer - Authentication & User Management
 * Handles login, registration, and user session management
 */

const AUTH = {
    // Auth tokens
    accessToken: localStorage.getItem('accessToken'),
    refreshToken: localStorage.getItem('refreshToken'),
    user: JSON.parse(localStorage.getItem('user') || 'null'),

    // Get authorization header
    getAuthHeader() {
        if (this.accessToken) {
            return {
                'Authorization': `Bearer ${this.accessToken}`,
                'Content-Type': 'application/json'
            };
        }
        return { 'Content-Type': 'application/json' };
    },

    // Check if user is authenticated
    isAuthenticated() {
        return !!this.accessToken && !!this.user;
    },

    // Check token expiration
    isTokenExpired() {
        if (!this.user || !this.user.expiresIn) return true;
        const createdTime = new Date(this.user.createdAt).getTime();
        const expiryTime = createdTime + (this.user.expiresIn * 1000);
        return Date.now() > expiryTime;
    }
};

const AUTH_DOM = {
    // Auth pages
    authPage: document.getElementById('authPage'),
    loginForm: document.getElementById('loginForm'),
    registerForm: document.getElementById('registerForm'),

    // Login elements
    loginFormElement: document.getElementById('loginFormElement'),
    loginEmail: document.getElementById('loginEmail'),
    loginPassword: document.getElementById('loginPassword'),
    loginError: document.getElementById('loginError'),
    loginSpinner: document.getElementById('loginSpinner'),
    showRegisterBtn: document.getElementById('showRegisterBtn'),
    showLoginBtn: document.getElementById('showLoginBtn'),

    // Register elements
    registerFormElement: document.getElementById('registerFormElement'),
    registerFullName: document.getElementById('registerFullName'),
    registerEmail: document.getElementById('registerEmail'),
    registerPassword: document.getElementById('registerPassword'),
    registerConfirmPassword: document.getElementById('registerConfirmPassword'),
    registerError: document.getElementById('registerError'),
    registerSpinner: document.getElementById('registerSpinner'),

    // Dashboard
    dashboardPage: document.getElementById('dashboardPage'),
    logoutBtn: document.getElementById('logoutBtn'),

    // Theme toggles
    themeToggle: document.getElementById('themeToggle'),
    themeToggleDashboard: document.getElementById('themeToggleDashboard'),
    htmlElement: document.documentElement
};

// Initialize auth on page load
function initAuth() {
    // Setup theme toggles
    if (AUTH_DOM.themeToggle) {
        AUTH_DOM.themeToggle.addEventListener('click', toggleTheme);
    }
    if (AUTH_DOM.themeToggleDashboard) {
        AUTH_DOM.themeToggleDashboard.addEventListener('click', toggleTheme);
    }

    // Initialize theme
    initTheme();

    // Check if user is authenticated
    if (AUTH.isAuthenticated() && !AUTH.isTokenExpired()) {
        showDashboard();
    } else {
        showAuthPage();
        // Clear invalid tokens
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
    }

    // Setup auth event listeners
    setupAuthListeners();
}

/**
 * Setup authentication event listeners
 */
function setupAuthListeners() {
    // Login form
    if (AUTH_DOM.loginFormElement) {
        AUTH_DOM.loginFormElement.addEventListener('submit', handleLogin);
    }

    // Register form
    if (AUTH_DOM.registerFormElement) {
        AUTH_DOM.registerFormElement.addEventListener('submit', handleRegister);
    }

    // Form toggles
    if (AUTH_DOM.showRegisterBtn) {
        AUTH_DOM.showRegisterBtn.addEventListener('click', showRegisterForm);
    }
    if (AUTH_DOM.showLoginBtn) {
        AUTH_DOM.showLoginBtn.addEventListener('click', showLoginForm);
    }

    // Logout
    if (AUTH_DOM.logoutBtn) {
        AUTH_DOM.logoutBtn.addEventListener('click', handleLogout);
    }
}

/**
 * Show login form
 */
function showLoginForm(e) {
    if (e) e.preventDefault();
    AUTH_DOM.loginForm.classList.remove('hidden');
    AUTH_DOM.registerForm.classList.add('hidden');
    AUTH_DOM.loginError.classList.add('hidden');
}

/**
 * Show register form
 */
function showRegisterForm(e) {
    if (e) e.preventDefault();
    AUTH_DOM.registerForm.classList.remove('hidden');
    AUTH_DOM.loginForm.classList.add('hidden');
    AUTH_DOM.registerError.classList.add('hidden');
}

/**
 * Handle login submission
 */
async function handleLogin(e) {
    e.preventDefault();

    const email = AUTH_DOM.loginEmail.value.trim();
    const password = AUTH_DOM.loginPassword.value.trim();

    if (!email || !password) {
        showAuthError('email', 'Please enter both email and password');
        return;
    }

    AUTH_DOM.loginSpinner.classList.remove('hidden');

    try {
        const response = await fetch('api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            showAuthError('email', data.message || 'Login failed. Please check your credentials.');
            return;
        }

        // Store auth data
        AUTH.accessToken = data.accessToken;
        AUTH.refreshToken = data.refreshToken;
        AUTH.user = {
            id: data.userId,
            email: data.email,
            fullName: data.fullName,
            roles: data.roles,
            createdAt: data.createdAt,
            expiresIn: data.expiresIn
        };

        localStorage.setItem('accessToken', AUTH.accessToken);
        localStorage.setItem('refreshToken', AUTH.refreshToken);
        localStorage.setItem('user', JSON.stringify(AUTH.user));

        showSuccess('Login successful! Redirecting...');
        setTimeout(() => {
            showDashboard();
        }, 500);

    } catch (error) {
        console.error('Login error:', error);
        showAuthError('email', 'Network error. Please try again.');
    } finally {
        AUTH_DOM.loginSpinner.classList.add('hidden');
    }
}

/**
 * Handle registration submission
 */
async function handleRegister(e) {
    e.preventDefault();

    const fullName = AUTH_DOM.registerFullName.value.trim();
    const email = AUTH_DOM.registerEmail.value.trim();
    const password = AUTH_DOM.registerPassword.value.trim();
    const confirmPassword = AUTH_DOM.registerConfirmPassword.value.trim();

    // Validation
    if (!fullName || !email || !password) {
        showAuthError('register', 'Please fill in all fields');
        return;
    }

    if (password !== confirmPassword) {
        showAuthError('register', 'Passwords do not match');
        return;
    }

    if (password.length < 6) {
        showAuthError('register', 'Password must be at least 6 characters');
        return;
    }

    AUTH_DOM.registerSpinner.classList.remove('hidden');

    try {
        const response = await fetch('api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password, fullName })
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            showAuthError('register', data.message || 'Registration failed');
            return;
        }

        // Store auth data
        AUTH.accessToken = data.accessToken;
        AUTH.refreshToken = data.refreshToken;
        AUTH.user = {
            id: data.userId,
            email: data.email,
            fullName: data.fullName,
            roles: data.roles,
            createdAt: data.createdAt,
            expiresIn: data.expiresIn
        };

        localStorage.setItem('accessToken', AUTH.accessToken);
        localStorage.setItem('refreshToken', AUTH.refreshToken);
        localStorage.setItem('user', JSON.stringify(AUTH.user));

        showSuccess('Registration successful! Welcome!');
        setTimeout(() => {
            showDashboard();
        }, 500);

    } catch (error) {
        console.error('Registration error:', error);
        showAuthError('register', 'Network error. Please try again.');
    } finally {
        AUTH_DOM.registerSpinner.classList.add('hidden');
    }
}

/**
 * Handle logout
 */
function handleLogout(e) {
    if (e) e.preventDefault();

    // Clear auth data
    AUTH.accessToken = null;
    AUTH.refreshToken = null;
    AUTH.user = null;

    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');

    showAuthPage();
    showSuccess('Logged out successfully');
}

/**
 * Show auth page
 */
function showAuthPage() {
    AUTH_DOM.authPage.classList.add('active');
    AUTH_DOM.dashboardPage.classList.remove('active');
}

/**
 * Show dashboard page
 */
function showDashboard() {
    AUTH_DOM.authPage.classList.remove('active');
    AUTH_DOM.dashboardPage.classList.add('active');
    updateUserProfile();
}

/**
 * Update user profile information
 */
function updateUserProfile() {
    if (!AUTH.user) return;

    const userName = document.getElementById('userName');
    const userEmail = document.getElementById('userEmail');
    const userRole = document.getElementById('userRole');
    const profileFullName = document.getElementById('profileFullName');
    const profileEmail = document.getElementById('profileEmail');
    const userAvatar = document.getElementById('userAvatar');

    if (userName) userName.textContent = AUTH.user.fullName || 'User';
    if (userEmail) userEmail.textContent = AUTH.user.email;
    if (userRole) userRole.textContent = (AUTH.user.roles && AUTH.user.roles[0]) || 'User';
    if (profileFullName) profileFullName.value = AUTH.user.fullName || '';
    if (profileEmail) profileEmail.value = AUTH.user.email || '';
    
    // Set avatar with user initials
    if (userAvatar && AUTH.user.fullName) {
        const initials = AUTH.user.fullName.split(' ')
            .map(n => n[0])
            .join('')
            .toUpperCase()
            .substring(0, 2);
        userAvatar.textContent = initials;
    }
}

/**
 * Show auth error message
 */
function showAuthError(formType, message) {
    let errorElement;
    if (formType === 'register') {
        errorElement = AUTH_DOM.registerError;
    } else {
        errorElement = AUTH_DOM.loginError;
    }

    if (errorElement) {
        errorElement.textContent = message;
        errorElement.classList.remove('hidden');
        setTimeout(() => {
            errorElement.classList.add('hidden');
        }, 5000);
    }
}

/**
 * Theme management
 */
let currentTheme = localStorage.getItem('theme') || 'light';

function initTheme() {
    applyTheme(currentTheme);
}

function applyTheme(theme) {
    AUTH_DOM.htmlElement.classList.remove('light', 'dark');
    AUTH_DOM.htmlElement.classList.add(theme);

    if (AUTH_DOM.themeToggle) {
        AUTH_DOM.themeToggle.classList.remove('light', 'dark');
        AUTH_DOM.themeToggle.classList.add(theme);
    }
    if (AUTH_DOM.themeToggleDashboard) {
        AUTH_DOM.themeToggleDashboard.classList.remove('light', 'dark');
        AUTH_DOM.themeToggleDashboard.classList.add(theme);
    }

    localStorage.setItem('theme', theme);
    currentTheme = theme;
}

function toggleTheme() {
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    applyTheme(newTheme);
}

/**
 * Show success notification
 */
function showSuccess(message) {
    const notification = document.createElement('div');
    notification.className = 'notification success';
    notification.innerHTML = `<div style="color: #86efac;">✓ ${message}</div>`;
    document.body.appendChild(notification);
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAuth);
} else {
    initAuth();
}
