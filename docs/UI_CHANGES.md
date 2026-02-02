# AI Resume Analyzer - UI Enhancement Documentation

## Overview

The UI has been completely redesigned to support all features including:
- User authentication (login/register)
- Dashboard with multiple pages
- Resume analyzer with enhanced features
- User profile management
- Job alerts subscription
- Analysis history tracking
- Responsive design supporting all devices

## Architecture

### Files Structure

```
src/main/resources/static/
├── index.html          # Main HTML file with all pages
├── app-auth.js         # Authentication logic
└── app.js              # Main analyzer and dashboard logic
```

## Page Structure

### 1. **Authentication Page** (`authPage`)
- **Login Form**: Email and password login
- **Register Form**: New user registration with email, password, and name
- **Features**:
  - Form validation
  - Error messages with timeout
  - Smooth form transitions
  - Theme toggle available

### 2. **Dashboard Page** (`dashboardPage`)
- Accessible only after authentication
- Main navigation bar with links to all sections
- Four main content areas:

#### A. **Analyzer** (analyzer)
- Resume upload/paste interface
- Job description input
- Job link input support
- Real-time analysis results
- Matched and missing skills display
- AI suggestions
- Rule-based tips
- Full analysis report
- Save to history functionality

#### B. **Profile** (profile)
- User information display
- Profile avatar with user initials
- Email and name display
- User role display
- Notification preferences
- Email notification toggle
- Job alerts toggle
- Weekly digest toggle

#### C. **Job Alerts** (alerts)
- Create new job alerts
- Search by job title
- Filter by company
- Specify required skills
- Set alert frequency (daily, weekly, monthly)
- View active alerts
- Manage subscriptions

#### D. **History** (history)
- View all previous analyses
- Sorted by date (most recent first)
- Match percentage display
- Skill statistics
- View detailed results
- Load previous analyses

## Authentication Flow

```
1. User visits page
2. Check localStorage for accessToken
3. If no token or token expired → Show Auth Page
4. User registers/logs in
5. Server returns JWT tokens (access + refresh)
6. Store tokens in localStorage
7. Store user info in localStorage
8. Redirect to Dashboard
9. Include Bearer token in all API requests
```

## API Integration

### Authorization Header
```javascript
// All authenticated requests include:
{
  'Authorization': `Bearer ${accessToken}`,
  'Content-Type': 'application/json'
}
```

### Endpoints Used

#### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh-token` - Token refresh
- `POST /api/auth/logout` - Logout (client-side)

#### Resume Analysis
- `POST /api/analyze` - Analyze with text input
- `POST /api/analyze-file` - Analyze with file upload

#### Future Endpoints (Task 11+)
- `POST /api/job-alerts` - Create job alert
- `GET /api/job-alerts` - List user alerts
- `DELETE /api/job-alerts/{id}` - Delete alert
- `POST /api/notifications` - Send notification
- `GET /api/notifications` - List notifications

## State Management

### localStorage Keys
```javascript
{
  'accessToken': 'JWT token string',
  'refreshToken': 'Refresh token string',
  'user': {
    id: number,
    email: string,
    fullName: string,
    roles: string[],
    createdAt: ISO datetime,
    expiresIn: milliseconds
  },
  'analysisHistory': [
    {
      id: timestamp,
      timestamp: formatted date,
      matchPercentage: number,
      matchedSkills: string[],
      missingSkills: string[],
      suggestions: string[],
      aiSuggestions: string[],
      report: string
    }
  ],
  'theme': 'light' | 'dark'
}
```

### Session State (Memory)
```javascript
{
  accessToken: string,
  refreshToken: string,
  user: {
    id: number,
    email: string,
    fullName: string,
    roles: string[],
    createdAt: datetime,
    expiresIn: number
  }
}

// App State
{
  selectedFile: File | null,
  useFileUpload: boolean,
  currentPage: string,
  analysisHistory: array,
  lastAnalysisResult: object | null
}
```

## Navigation System

### Tab Navigation (Analyzer Page)
- Upload Tab: File drag-drop or click-to-upload
- Paste Tab: Text area for pasting resume content

### Page Navigation (Dashboard)
```javascript
// Switch between pages using data-page attribute
<button class="navbar-link" data-page="analyzer">Analyzer</button>
<button class="navbar-link" data-page="profile">Profile</button>
<button class="navbar-link" data-page="alerts">Job Alerts</button>
<button class="navbar-link" data-page="history">History</button>
```

## Features

### 1. **Theme Support**
- Light and dark themes
- Toggle button in top-right corner
- Theme preference saved to localStorage
- Consistent across all pages

### 2. **File Upload**
- Drag and drop support
- Click to select files
- Support for PDF and TXT files
- File preview with name, type, and size
- Remove file button

### 3. **Keyboard Shortcuts**
- `Ctrl+Enter` in resume/job description text areas triggers analysis

### 4. **Loading States**
- Button disabled state during analysis
- Spinner animation on analyze button
- FAB pulse animation during loading

### 5. **Error Handling**
- User-friendly error messages
- Auto-hide errors after 5 seconds
- Input validation before API calls
- Network error handling

### 6. **Success Notifications**
- Toast-style notifications
- Auto-dismiss after 3 seconds
- Success checkmark icon

### 7. **Responsive Design**
- Mobile-first approach
- Tablet and desktop optimized
- Flexible grid layout
- FAB visible on mobile, button on desktop

### 8. **Security**
- Tokens stored securely in localStorage
- CORS-ready for different domains
- No sensitive data in console logs
- JWT-based stateless authentication

## Component Structure

### HTML Components

#### Auth Card
```html
<div class="auth-card">
  <div class="card p-8">
    <!-- Form content -->
  </div>
</div>
```

#### Dashboard Card
```html
<div class="card p-5 md:p-6">
  <!-- Content -->
</div>
```

#### Skill Badge
```html
<span class="skill-badge">${skill}</span>
<!-- Or missing -->
<span class="skill-badge missing">${skill}</span>
```

#### Alert Item
```html
<div class="alert-item">
  <!-- Alert content -->
</div>
```

### CSS Classes

#### Layout
- `.main-container` - Main page container
- `.content-grid` - Grid layout for analyzer
- `.responsive-grid` - Auto-fit grid for cards

#### Interactive
- `.btn-primary` - Primary action button
- `.btn-secondary` - Secondary action button
- `.input-field` - Standard input/textarea
- `.form-label` - Form labels
- `.form-group` - Form field wrapper

#### Display
- `.card` - Card container
- `.gradient-text` - Gradient text effect
- `.badge` - Badge styling
- `.loading-spinner` - Loading animation
- `.error-message` - Error styling
- `.success-message` - Success styling
- `.notification` - Notification toast

#### Theme
- `html.light` - Light mode
- `html.dark` - Dark mode

## JavaScript Object Reference

### AUTH Object
```javascript
AUTH = {
  accessToken: string,
  refreshToken: string,
  user: object,
  getAuthHeader(): object,
  isAuthenticated(): boolean,
  isTokenExpired(): boolean
}
```

### DOM Object
```javascript
DOM = {
  dropZone, resumeFile, filePreview,
  tabButtons, tabContents,
  resumeTextInput, jobDescriptionInput,
  analyzeBtn, loadingSpinner,
  resultsContainer, matchScore,
  // ... and many more
}
```

### State Object
```javascript
state = {
  selectedFile: File | null,
  useFileUpload: boolean,
  currentPage: string,
  analysisHistory: array,
  lastAnalysisResult: object | null
}
```

## Key Functions

### Authentication
```javascript
handleLogin(e)           // Handle login form submission
handleRegister(e)        // Handle registration form submission
handleLogout(e)          // Clear auth data and logout
showAuthPage()           // Display authentication page
showDashboard()          // Display dashboard after login
updateUserProfile()      // Update profile info display
```

### Navigation
```javascript
switchPage(page)         // Switch between dashboard pages
setupNavigation()        // Setup page navigation
switchTab(tabName)       // Switch tabs in analyzer
```

### Analysis
```javascript
handleAnalyze()          // Main analyze handler
analyzeWithText()        // Analyze from text input
analyzeWithFile()        // Analyze from file upload
displayResults(data)     // Display analysis results
saveAnalysisToHistory()  // Save analysis to localStorage
loadAnalysisHistory()    // Load and display history
```

### UI
```javascript
showError(message)       // Show error message
hideError()              // Hide error message
showSuccess(message)     // Show success notification
setButtonLoading(isLoading)  // Set loading state
```

### Utilities
```javascript
escapeHtml(text)         // Escape HTML special characters
getAuthHeader()          // Get Authorization header
isAuthenticated()        // Check if user is logged in
toggleTheme()            // Toggle light/dark theme
```

## Integration Points with Backend

### Required Backend Features
1. **Authentication** (Task 10) ✅
   - User registration endpoint
   - User login endpoint
   - JWT token generation
   - Token refresh endpoint

2. **Resume Analysis** (Task 1-8) ✅
   - Analyze text endpoint
   - Analyze file endpoint
   - Returns match percentage, skills, suggestions

3. **Database** (Task 9) ✅
   - User table with authentication
   - Resume storage
   - Analysis history storage

4. **Future Integrations** (Task 11+)
   - Job alerts endpoints
   - Notification service
   - Email sending
   - Schedule processing

## Testing Checklist

### Authentication
- [ ] User can register with new email
- [ ] User can login with correct credentials
- [ ] Invalid credentials show error
- [ ] Token stored in localStorage
- [ ] User can logout
- [ ] Logout clears localStorage
- [ ] Redirects to auth page when not authenticated

### Dashboard
- [ ] Dashboard shows after login
- [ ] Navigation links work
- [ ] User profile displays correctly
- [ ] Theme toggle works
- [ ] All pages load correctly

### Analyzer
- [ ] Resume upload works
- [ ] Drag and drop works
- [ ] Text paste works
- [ ] Job description input works
- [ ] Analysis returns results
- [ ] Results display correctly
- [ ] Save to history works

### History
- [ ] Previous analyses shown
- [ ] Can view detailed results
- [ ] Properly sorted by date
- [ ] Match scores displayed

### Responsive
- [ ] Mobile layout works
- [ ] Tablet layout works
- [ ] Desktop layout works
- [ ] FAB visible on mobile
- [ ] Touch interactions work

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Performance Considerations

1. **Token Management**
   - Access tokens expire in 1 hour
   - Refresh tokens valid for 7 days
   - Tokens checked before each API call

2. **History Storage**
   - localStorage size: ~5MB per domain
   - History cleared on manual logout
   - Old entries can be archived

3. **API Calls**
   - Debouncing on file upload
   - Loading states prevent double-submit
   - Error handling prevents locked UI

## Security Best Practices

1. **Tokens**
   - Stored in localStorage (consider httpOnly cookies for production)
   - Sent only with Authorization header
   - Never included in logs

2. **Input Validation**
   - File type checking (PDF, TXT only)
   - File size limits on frontend
   - Email format validation
   - HTML escaping for display

3. **CORS**
   - API expects CORS headers
   - Credentials in fetch requests

## Migration from Old UI

The old UI (`index.html.bak_*`) is preserved. To use old UI:
1. Rename current `index.html` to `index-new.html`
2. Rename `index.html.bak_*` to `index.html`
3. Keep both `app.js` and `app-auth.js` files

## Future Enhancements

1. **Email Notifications** (Task 11)
   - Job alert emails
   - Match notifications
   - Digest emails

2. **Advanced Features**
   - Resume version management
   - Skill progress tracking
   - Job market analytics
   - Resume recommendations

3. **UI/UX**
   - Dark mode improvements
   - Accessibility (WCAG 2.1)
   - Animation refinements
   - Mobile app version

## Troubleshooting

### "Not authenticated" message
- Check if tokens in localStorage
- Clear localStorage and login again
- Check browser console for errors

### Analysis not showing results
- Verify job description is filled
- Check browser network tab for 200 response
- Ensure file format is PDF or TXT

### Theme not persisting
- Check localStorage is enabled
- Clear browser cache
- Check for browser restrictions

### Page blank after login
- Open browser console for JavaScript errors
- Check if dashboardPage element exists in HTML
- Verify app.js is loaded after app-auth.js

## Support

For issues or questions:
1. Check browser console for errors
2. Review network tab for API responses
3. Check localStorage values
4. Test with sample data

---

**Last Updated**: January 31, 2025
**Version**: 2.0 (Full Dashboard with Auth)
**Compatibility**: All browsers with ES6 support
