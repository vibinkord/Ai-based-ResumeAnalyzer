# AI Resume Analyzer - UI Quick Start Guide

## 🚀 Getting Started in 5 Minutes

### Step 1: Start the Application
```bash
cd "/mnt/d/College project/Resume analyser"
mvn spring-boot:run
```

### Step 2: Open in Browser
```
http://localhost:8080
```

You'll see the **Login/Register Page**

### Step 3: Create an Account
1. Click "Create one" link
2. Enter:
   - Full Name: "John Doe"
   - Email: "john@example.com"
   - Password: "password123"
   - Confirm: "password123"
3. Click "Create Account"

### Step 4: Dashboard Access
After login, you'll see the **Dashboard** with 4 tabs:
- **Analyzer** - Resume analysis tool
- **Profile** - User information
- **Job Alerts** - Alert management
- **History** - Previous analyses

### Step 5: Analyze a Resume
1. Go to **Analyzer** tab
2. Choose upload method:
   - **Upload**: Drag & drop or click to upload PDF/TXT
   - **Paste**: Copy-paste resume text
3. Enter job description or URL
4. Click **"Analyze Resume"**
5. View results instantly!

## 📊 What's in Each Page?

### 🔐 Authentication Page
```
┌─────────────────────────────────────┐
│   AI Resume Analyzer                │
│   Master Your Career                │
├─────────────────────────────────────┤
│                                     │
│  LOGIN FORM          REGISTER FORM  │
│  ┌──────────────┐    ┌────────────┐│
│  │ Email        │    │Full Name   ││
│  │ Password     │    │Email       ││
│  │ [Sign In]    │    │Password    ││
│  │ Create one → │    │Confirm Pwd ││
│  │              │    │[Create]    ││
│  │              │    │← Sign in   ││
│  └──────────────┘    └────────────┘│
│                                     │
└─────────────────────────────────────┘
```

### 📄 Analyzer Page
```
┌─────────────────────────────────────┐
│   Analyze Your Resume               │
├─────────────────────────────────────┤
│ INPUT SECTION:                      │
│ ┌─────────────────────────────────┐ │
│ │ [Upload] [Paste]                │ │
│ │                                 │ │
│ │ Drag & Drop Resume Here         │ │
│ │ or click to select              │ │
│ │                                 │ │
│ │ Job Description:                │ │
│ │ [__________________________]     │ │
│ │                                 │ │
│ │ Job Link:                       │ │
│ │ [__________________________]     │ │
│ │                                 │ │
│ │         [Analyze Resume]        │ │
│ └─────────────────────────────────┘ │
│                                     │
│ RESULTS SECTION:                    │
│ ┌──────────────┬──────────────────┐│
│ │ Match Score  │ Matched Skills   ││
│ │     85%      │ ✓ Python         ││
│ │              │ ✓ JavaScript     ││
│ │ Missing:     │ ✗ React          ││
│ │ ✗ TypeScript │ ✗ Docker         ││
│ └──────────────┴──────────────────┘│
│                                     │
│ AI Suggestions:                     │
│ • Add more specific project details │
│ • Highlight leadership experience   │
│                                     │
└─────────────────────────────────────┘
```

### 👤 Profile Page
```
┌─────────────────────────────────────┐
│   My Profile                        │
├─────────────────────────────────────┤
│ AVATAR NAME                         │
│   JD   John Doe                     │
│        john@example.com             │
│        User Role                    │
│                                     │
│ Full Name: John Doe                 │
│ Email: john@example.com             │
│                                     │
│ PREFERENCES:                        │
│ ☑ Receive email notifications      │
│ ☑ Subscribe to job alerts          │
│ ☑ Receive weekly digest             │
│                                     │
│      [Save Preferences]             │
│                                     │
└─────────────────────────────────────┘
```

### 🔔 Job Alerts Page
```
┌─────────────────────────────────────┐
│   Job Alerts                        │
├─────────────────────────────────────┤
│ CREATE JOB ALERT:                   │
│                                     │
│ Job Title:    [________________]    │
│ Company:      [________________]    │
│ Skills:       [________________]    │
│ Frequency:    [Daily ▼]             │
│               [Create Alert]        │
│                                     │
│ ACTIVE ALERTS:                      │
│ ┌────────────────────────────────┐  │
│ │ Senior Developer               │  │
│ │ Google | Python, JavaScript    │  │
│ │ Frequency: Daily               │  │
│ │              [Edit] [Delete]   │  │
│ └────────────────────────────────┘  │
│                                     │
└─────────────────────────────────────┘
```

### 📚 History Page
```
┌─────────────────────────────────────┐
│   Analysis History                  │
├─────────────────────────────────────┤
│ ┌────────────────────────────────┐  │
│ │ Jan 31, 2025, 2:45 PM          │  │
│ │ Match Score: 85%                │  │
│ │ Matched: 8 skills               │  │
│ │ Missing: 3 skills               │  │
│ │              [View Details]    │  │
│ └────────────────────────────────┘  │
│                                     │
│ ┌────────────────────────────────┐  │
│ │ Jan 30, 2025, 10:15 AM          │  │
│ │ Match Score: 72%                │  │
│ │ Matched: 6 skills               │  │
│ │ Missing: 5 skills               │  │
│ │              [View Details]    │  │
│ └────────────────────────────────┘  │
│                                     │
└─────────────────────────────────────┘
```

## 🎨 Features You'll Love

### 🌙 Dark Mode Toggle
- Click the ☀️/🌙 button in top-right
- Your preference is saved
- Applies to all pages

### ⚡ Keyboard Shortcuts
- **Ctrl+Enter** in resume/job description fields = Analyze!

### 📱 Mobile Friendly
- Works great on phones, tablets, and desktops
- Touch-friendly buttons
- Responsive layouts

### 💾 Auto-Save
- Analysis results saved automatically
- History persists across sessions
- Preferences remembered

### 🚀 Floating Action Button
- On mobile: Large floating button for quick analysis
- Pulse animation when analyzing
- Always accessible

## 📋 Sample Data for Testing

### Sample Resume
```
JOHN DOE
john@example.com | 555-1234

EXPERIENCE
Senior Software Developer - Tech Corp (2020-2025)
- Developed Python and JavaScript applications
- Led team of 5 developers
- Improved performance by 40%

SKILLS
Python, JavaScript, React, Node.js, SQL, Docker

EDUCATION
B.S. Computer Science - State University
```

### Sample Job Description
```
SENIOR DEVELOPER - TECH CORP

We're hiring a Senior Developer to join our team!

Requirements:
- 5+ years of experience
- Python and JavaScript
- React and Node.js expertise
- Docker and Kubernetes
- AWS cloud experience
- Leadership experience

Nice to have:
- TypeScript
- GraphQL
- CI/CD pipelines
```

## 🔧 Troubleshooting

### "Not authenticated" message
**Solution**: Clear localStorage and login again
```javascript
// Open DevTools (F12) → Console → paste:
localStorage.clear();
location.reload();
```

### Analysis showing empty results
**Solution**: 
1. Make sure job description is filled
2. Check browser console for errors (F12)
3. Verify API endpoint is running

### Page looks broken
**Solution**:
1. Hard refresh: Ctrl+Shift+R (Windows) or Cmd+Shift+R (Mac)
2. Clear browser cache
3. Try different browser

### Can't upload file
**Solution**: 
- Only PDF and TXT files supported
- File must be < 10MB
- Try pasting text instead

## 🎯 Common Tasks

### How to change my password?
- Currently only available through admin panel
- Contact support or wait for Task 11 implementation

### How to download my analysis report?
- Analysis report is displayed on screen
- Copy/paste into your preferred format
- Export feature coming in Task 15

### How to delete my account?
- Feature coming in Task 11+
- Contact support for immediate assistance

### How to manage job alerts?
- Go to **Job Alerts** tab
- Create, edit, or delete alerts
- Full management coming in Task 11

## 📞 Need Help?

### Check the Documentation
- **UI Guide**: `/docs/UI_CHANGES.md`
- **API Docs**: Swagger at `http://localhost:8080/swagger-ui.html`
- **Architecture**: `/docs/ARCHITECTURE.md`

### Debug Mode
Open browser DevTools (F12) and check:
1. **Console** - Any JavaScript errors
2. **Network** - API calls and responses
3. **Application** - localStorage and cookies

### Contact Support
- Email: support@resumeanalyzer.io
- GitHub Issues: Issues tab
- Slack: #support channel

## ⌨️ Keyboard Navigation

| Key | Action |
|-----|--------|
| Tab | Move between fields |
| Enter | Submit form (if focused) |
| Ctrl+Enter | Analyze resume |
| Escape | Close dialogs (future) |
| / | Search (future) |

## 🔐 Login Tips

### For Development
```
Email: test@example.com
Password: password123
```

### For Testing Errors
```
Email: invalid@example.com (will fail)
Password: short (will fail)
```

## 📊 What Gets Saved?

### In Browser
- Tokens (accessToken, refreshToken)
- User info (name, email, roles)
- Analysis history (last 50 analyses)
- Theme preference (light/dark)

### On Server
- User credentials
- Resume content (future)
- Analysis results (future)
- Job alert subscriptions (future)

## 🚀 Performance Tips

1. **Faster Upload**: Use Paste tab instead of file upload
2. **Better Results**: Be specific in job description
3. **Quicker Analysis**: Keep resumes under 2 pages
4. **Smooth Experience**: Close other browser tabs

## 🎓 Learning Resources

- [JWT Authentication Guide](https://jwt.io)
- [REST API Best Practices](https://restfulapi.net)
- [Modern JavaScript](https://javascript.info)
- [Spring Boot Guide](https://spring.io/projects/spring-boot)

## 📈 What's Coming Next?

### Task 11 (Email Notifications)
- Email alerts for job matches
- Weekly digest emails
- Notification preferences
- Email templates

### Task 12-14 (DevOps)
- Docker containerization
- Kubernetes deployment
- CI/CD automation
- Cloud hosting

### Task 15-16 (Advanced)
- Mobile app
- Advanced analytics
- ML recommendations
- Resume optimization

## ✨ Pro Tips

1. **Bookmark the app** for quick access
2. **Use dark mode** for reduced eye strain
3. **Save analyses** to compare offers later
4. **Check history** before applying to similar roles
5. **Update resume** regularly as you grow

## 🎉 You're All Set!

You now have:
- ✅ Working AI Resume Analyzer
- ✅ Full user authentication
- ✅ Resume analysis with AI suggestions
- ✅ Analysis history tracking
- ✅ Mobile-friendly interface
- ✅ Dark mode support

**Start analyzing your resume now!**

---

**Version**: 2.0  
**Last Updated**: January 31, 2025  
**Status**: Production Ready (Auth & Analyzer)
