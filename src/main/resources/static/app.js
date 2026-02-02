/**
 * AI Resume Analyzer - Frontend Application
 * Vanilla JavaScript, no frameworks or build tools
 * Supports file upload (PDF, TXT) and text input
 * Includes light/dark theme toggle
 */

const DOM = {
    // File upload elements
    dropZone: document.getElementById('dropZone'),
    resumeFile: document.getElementById('resumeFile'),
    filePreview: document.getElementById('filePreview'),
    
    // Tab elements
    tabButtons: document.querySelectorAll('.tab-button'),
    tabContents: document.querySelectorAll('.tab-content'),
    
    // Text input elements
    resumeTextInput: document.getElementById('resumeText'),
    jobDescriptionInput: document.getElementById('jobDescriptionText'),
    jobUrlInput: document.getElementById('jobUrlInput'),
    analyzeBtn: document.getElementById('analyzeBtn'),
    loadingSpinner: document.getElementById('loadingSpinner'),
    
    // Results elements
    resultsContainer: document.getElementById('resultsContainer'),
    detailsContainer: document.getElementById('detailsContainer'),
    errorContainer: document.getElementById('errorContainer'),
    errorMessage: document.getElementById('errorMessage'),
    aiContainer: document.getElementById('aiContainer'),
    suggestionsContainer2: document.getElementById('suggestionsContainer2'),
    reportContainer2: document.getElementById('reportContainer2'),
    
    // Score and skills display
    matchScore: document.getElementById('matchScore'),
    matchedSkillsContainer: document.getElementById('matchedSkillsContainer'),
    missingSkillsContainer: document.getElementById('missingSkillsContainer'),
    suggestionsContainer: document.getElementById('suggestionsContainer'),
    aiSuggestionsContainer: document.getElementById('aiSuggestionsContainer'),
    reportContainer: document.getElementById('reportContainer'),
    
    // Theme toggle
    themeToggle: document.getElementById('themeToggle'),
    htmlElement: document.documentElement
};

// State management
let state = {
    selectedFile: null,
    useFileUpload: true,
    currentTheme: 'light'
};

/**
 * Initialize theme on page load
 */
function initTheme() {
    // Get saved theme from localStorage, default to 'light'
    const savedTheme = localStorage.getItem('theme') || 'light';
    state.currentTheme = savedTheme;
    applyTheme(savedTheme);
}

/**
 * Apply theme to the page
 */
function applyTheme(theme) {
    // Remove both classes
    DOM.htmlElement.classList.remove('light', 'dark');
    
    // Add the selected theme class
    DOM.htmlElement.classList.add(theme);
    
    // Update toggle button appearance
    DOM.themeToggle.classList.remove('light', 'dark');
    DOM.themeToggle.classList.add(theme);
    
    // Save to localStorage
    localStorage.setItem('theme', theme);
    state.currentTheme = theme;
}

/**
 * Toggle between light and dark themes
 */
function toggleTheme() {
    const newTheme = state.currentTheme === 'light' ? 'dark' : 'light';
    applyTheme(newTheme);
}

/**
 * Initialize event listeners
 */
function init() {
    // Theme toggle
    DOM.themeToggle.addEventListener('click', toggleTheme);

    // Attach FAB after DOM is ready (in case markup is added after the script)
    DOM.fabAnalyze = document.getElementById('fabAnalyze');
    if (DOM.fabAnalyze) {
        DOM.fabAnalyze.addEventListener('click', (e) => {
            // Prefer triggering the existing analyze button (ensures same UX and validation).
            if (DOM.analyzeBtn) {
                DOM.analyzeBtn.click();
                return;
            }

            // Fallback: call the handler directly
            handleAnalyze();
        });
    }
    
    // Analyze button
    DOM.analyzeBtn.addEventListener('click', handleAnalyze);
    
    // Tab navigation
    DOM.tabButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            const tabName = e.target.dataset.tab;
            switchTab(tabName);
        });
    });
    
    // File upload handlers
    DOM.dropZone.addEventListener('click', () => DOM.resumeFile.click());
    DOM.resumeFile.addEventListener('change', handleFileSelect);
    
    // Drag and drop
    DOM.dropZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        DOM.dropZone.classList.add('drag-over');
    });
    
    DOM.dropZone.addEventListener('dragleave', () => {
        DOM.dropZone.classList.remove('drag-over');
    });
    
    DOM.dropZone.addEventListener('drop', (e) => {
        e.preventDefault();
        DOM.dropZone.classList.remove('drag-over');
        
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            DOM.resumeFile.files = files;
            handleFileSelect({ target: { files: files } });
        }
    });
    
    // Keyboard shortcuts
    if (DOM.resumeTextInput) {
        DOM.resumeTextInput.addEventListener('keydown', (e) => {
            if (e.ctrlKey && e.key === 'Enter') handleAnalyze();
        });
    }
    
    DOM.jobDescriptionInput.addEventListener('keydown', (e) => {
        if (e.ctrlKey && e.key === 'Enter') handleAnalyze();
    });
}

/**
 * Switch between upload and paste tabs
 */
function switchTab(tabName) {
    // Update button states
    DOM.tabButtons.forEach(btn => {
        if (btn.dataset.tab === tabName) {
            btn.classList.add('active');
            state.useFileUpload = tabName === 'upload';
        } else {
            btn.classList.remove('active');
        }
    });
    
    // Update content visibility
    DOM.tabContents.forEach(content => {
        if (content.id === `${tabName}-tab`) {
            content.classList.add('active');
        } else {
            content.classList.remove('active');
        }
    });
}

/**
 * Handle file selection
 */
function handleFileSelect(e) {
    const files = e.target.files;
    
    if (!files || files.length === 0) {
        return;
    }
    
    const file = files[0];
    const validTypes = ['application/pdf', 'text/plain'];
    const validExtensions = ['.pdf', '.txt'];
    
    // Validate file type
    const isValidType = validTypes.includes(file.type) || 
                       validExtensions.some(ext => file.name.toLowerCase().endsWith(ext));
    
    if (!isValidType) {
        showError('Please upload a PDF or TXT file.');
        state.selectedFile = null;
        return;
    }
    
    state.selectedFile = file;
    showFilePreview(file);
    hideError();
}

/**
 * Display file preview
 */
function showFilePreview(file) {
    const fileIcon = file.type === 'application/pdf' ? '📄' : '📝';
    const fileType = file.type === 'application/pdf' ? 'PDF Document' : 'Text File';
    const fileSize = (file.size / 1024).toFixed(1);
    
    DOM.filePreview.innerHTML = `
        <div class="file-preview">
            <div class="file-icon">${fileIcon}</div>
            <div class="file-info">
                <div class="file-name">${escapeHtml(file.name)}</div>
                <div class="file-type">${fileType} • ${fileSize} KB</div>
            </div>
            <button class="remove-file-btn" id="removeFileBtn">Remove</button>
        </div>
    `;
    DOM.filePreview.classList.remove('hidden');
    
    // Add remove button handler
    document.getElementById('removeFileBtn').addEventListener('click', () => {
        state.selectedFile = null;
        DOM.filePreview.classList.add('hidden');
        DOM.resumeFile.value = '';
    });
}

/**
 * Handle analyze button click
 */
async function handleAnalyze() {
    const jobDescriptionText = DOM.jobDescriptionInput.value.trim();
    const jobDescriptionUrl = DOM.jobUrlInput.value.trim();
    
    if (!jobDescriptionText && !jobDescriptionUrl) {
        showError('Provide a job description or a LinkedIn/Internshala job link.');
        return;
    }
    
    hideError();
    setButtonLoading(true);
    
    try {
        if (state.useFileUpload && state.selectedFile) {
            // Analyze with file upload
            await analyzeWithFile(jobDescriptionText, jobDescriptionUrl);
        } else {
            // Analyze with text input
            const resumeText = DOM.resumeTextInput.value.trim();
            if (!resumeText) {
                showError('Please enter resume text or upload a file.');
                setButtonLoading(false);
                return;
            }
            await analyzeWithText(resumeText, jobDescriptionText, jobDescriptionUrl);
        }
    } catch (error) {
        console.error('Analysis error:', error);
        showError(`Failed to analyze resume: ${error.message}`);
    } finally {
        setButtonLoading(false);
    }
}

/**
 * Analyze with uploaded file
 */
async function analyzeWithFile(jobDescriptionText, jobDescriptionUrl) {
    const formData = new FormData();
    formData.append('resumeFile', state.selectedFile);
    formData.append('jobDescriptionText', jobDescriptionText);
    formData.append('jobDescriptionUrl', jobDescriptionUrl);
    
    const response = await fetch('api/analyze-file', {
        method: 'POST',
        body: formData
    });
    
    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }
    
    const data = await response.json();
    displayResults(data);
}

/**
 * Analyze with text input
 */
async function analyzeWithText(resumeText, jobDescriptionText, jobDescriptionUrl) {
    const response = await fetch('api/analyze', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            resumeText: resumeText,
            jobDescriptionText: jobDescriptionText,
            jobDescriptionUrl: jobDescriptionUrl
        })
    });
    
    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }
    
    const data = await response.json();
    displayResults(data);
}

/**
 * Display analysis results in the UI
 * @param {Object} data - API response data
 */
function displayResults(data) {
    // Show results and containers
    DOM.resultsContainer.classList.remove('hidden');
    DOM.aiContainer.classList.remove('hidden');
    DOM.suggestionsContainer2.classList.remove('hidden');
    DOM.reportContainer2.classList.remove('hidden');
    
    // Scroll to results
    DOM.resultsContainer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    
    // Display match score with color coding
    const matchPercentage = parseFloat(data.matchPercentage).toFixed(1);
    DOM.matchScore.textContent = `${matchPercentage}%`;
    
    // Remove previous color classes
    DOM.matchScore.classList.remove('score-red', 'score-yellow', 'score-green');
    
    // Apply color based on score
    const percentage = parseFloat(matchPercentage);
    if (percentage < 50) {
        DOM.matchScore.classList.add('score-red');
    } else if (percentage < 80) {
        DOM.matchScore.classList.add('score-yellow');
    } else {
        DOM.matchScore.classList.add('score-green');
    }
    
    // Display matched skills
    if (data.matchedSkills && data.matchedSkills.length > 0) {
        DOM.matchedSkillsContainer.innerHTML = data.matchedSkills
            .map(skill => `<span class="skill-badge">${escapeHtml(skill)}</span>`)
            .join('');
    } else {
        DOM.matchedSkillsContainer.innerHTML = '<span class="text-slate-400">None</span>';
    }
    
    // Display missing skills
    if (data.missingSkills && data.missingSkills.length > 0) {
        DOM.missingSkillsContainer.innerHTML = data.missingSkills
            .map(skill => `<span class="skill-badge missing">${escapeHtml(skill)}</span>`)
            .join('');
    } else {
        DOM.missingSkillsContainer.innerHTML = '<span class="text-slate-400">None</span>';
    }
    
    // Display rule-based suggestions
    if (data.suggestions && data.suggestions.length > 0) {
        DOM.suggestionsContainer.innerHTML = data.suggestions
            .map(suggestion => `<div class="suggestion-item">${escapeHtml(suggestion)}</div>`)
            .join('');
    } else {
        DOM.suggestionsContainer.innerHTML = '<p class="text-slate-400">No suggestions available</p>';
    }
    
    // Display AI suggestions
    if (data.aiSuggestions && data.aiSuggestions.length > 0) {
        DOM.aiSuggestionsContainer.innerHTML = data.aiSuggestions
            .map(suggestion => `<div class="ai-suggestion-item">${escapeHtml(suggestion)}</div>`)
            .join('');
    } else {
        DOM.aiSuggestionsContainer.innerHTML = '<p class="text-slate-400">AI suggestions not available (check API key)</p>';
    }
    
    // Display report
    if (data.report) {
        DOM.reportContainer.textContent = data.report;
    } else {
        DOM.reportContainer.textContent = 'No report available';
    }
}

/**
 * Show error message
 * @param {string} message - Error message to display
 */
function showError(message) {
    DOM.errorMessage.textContent = message;
    DOM.errorContainer.classList.remove('hidden');
}

/**
 * Hide error message
 */
function hideError() {
    DOM.errorContainer.classList.add('hidden');
}

/**
 * Set button loading state
 * @param {boolean} isLoading - Loading state
 */
function setButtonLoading(isLoading) {
    DOM.analyzeBtn.disabled = isLoading;
    
    if (isLoading) {
        DOM.loadingSpinner.classList.remove('hidden');
    } else {
        DOM.loadingSpinner.classList.add('hidden');
    }

    // Sync FAB loading/disabled state
    if (DOM.fabAnalyze) {
        DOM.fabAnalyze.disabled = isLoading;
        if (isLoading) {
            DOM.fabAnalyze.classList.add('loading');
        } else {
            DOM.fabAnalyze.classList.remove('loading');
        }
    }
}

/**
 * Escape HTML special characters to prevent XSS
 * @param {string} text - Text to escape
 * @returns {string} Escaped text
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize the application when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        initTheme();
        init();
    });
} else {
    initTheme();
    init();
}
