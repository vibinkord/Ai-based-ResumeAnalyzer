/**
 * AI Resume Analyzer - Frontend Application
 * Main analyzer logic with authentication support
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

    // Navigation elements
    navLinks: document.querySelectorAll('[data-page]'),
    dashboardContents: document.querySelectorAll('.dashboard-content'),
    saveAnalysisBtn: document.getElementById('saveAnalysisBtn'),

    // History elements
    historyListContainer: document.getElementById('historyListContainer'),
    alertsListContainer: document.getElementById('alertsListContainer'),

    // Job alerts elements
    alertJobTitle: document.getElementById('alertJobTitle'),
    alertCompany: document.getElementById('alertCompany'),
    alertSkills: document.getElementById('alertSkills'),
    alertFrequency: document.getElementById('alertFrequency'),
};

// State management
let state = {
    selectedFile: null,
    useFileUpload: true,
    currentPage: 'analyzer',
    analysisHistory: JSON.parse(localStorage.getItem('analysisHistory') || '[]'),
    lastAnalysisResult: null
};

/**
 * Initialize the application
 */
function initApp() {
    if (!AUTH.isAuthenticated()) {
        return; // User not authenticated, auth page will be shown
    }

    // Setup analyzer event listeners
    setupAnalyzerListeners();

    // Setup navigation
    setupNavigation();

    // Load history
    loadAnalysisHistory();

    // Show FAB for analyzer
    const fabAnalyze = document.getElementById('fabAnalyze');
    if (fabAnalyze) {
        fabAnalyze.classList.remove('hidden');
        fabAnalyze.addEventListener('click', handleAnalyze);
    }
}

/**
 * Setup analyzer event listeners
 */
function setupAnalyzerListeners() {
    // Analyze button
    if (DOM.analyzeBtn) {
        DOM.analyzeBtn.addEventListener('click', handleAnalyze);
    }
    
    // Tab navigation
    DOM.tabButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            const tabName = e.target.dataset.tab;
            switchTab(tabName);
        });
    });
    
    // File upload handlers
    if (DOM.dropZone) {
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
    }
    
    // Keyboard shortcuts
    if (DOM.resumeTextInput) {
        DOM.resumeTextInput.addEventListener('keydown', (e) => {
            if (e.ctrlKey && e.key === 'Enter') handleAnalyze();
        });
    }
    
    if (DOM.jobDescriptionInput) {
        DOM.jobDescriptionInput.addEventListener('keydown', (e) => {
            if (e.ctrlKey && e.key === 'Enter') handleAnalyze();
        });
    }

    // Save analysis button
    if (DOM.saveAnalysisBtn) {
        DOM.saveAnalysisBtn.addEventListener('click', saveAnalysisToHistory);
    }
}

/**
 * Setup navigation between pages
 */
function setupNavigation() {
    DOM.navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const page = e.target.dataset.page;
            if (page) {
                switchPage(page);
            }
        });
    });
}

/**
 * Switch between dashboard pages
 */
function switchPage(page) {
    // Update active nav link
    DOM.navLinks.forEach(link => {
        if (link.dataset.page === page) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });

    // Update content visibility
    DOM.dashboardContents.forEach(content => {
        if (content.id === page + 'Content') {
            content.classList.add('active');
        } else {
            content.classList.remove('active');
        }
    });

    state.currentPage = page;

    // Refresh page-specific content
    if (page === 'history') {
        loadAnalysisHistory();
    } else if (page === 'alerts') {
        loadJobAlerts();
    }
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
        headers: AUTH.getAuthHeader(),
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
        headers: AUTH.getAuthHeader(),
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
 */
function displayResults(data) {
    // Store the result
    state.lastAnalysisResult = data;

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
 * Save analysis to history
 */
function saveAnalysisToHistory() {
    if (!state.lastAnalysisResult) {
        showError('No analysis to save');
        return;
    }

    const analysis = {
        id: Date.now(),
        timestamp: new Date().toLocaleString(),
        matchPercentage: state.lastAnalysisResult.matchPercentage,
        matchedSkills: state.lastAnalysisResult.matchedSkills,
        missingSkills: state.lastAnalysisResult.missingSkills,
        suggestions: state.lastAnalysisResult.suggestions,
        aiSuggestions: state.lastAnalysisResult.aiSuggestions,
        report: state.lastAnalysisResult.report
    };

    state.analysisHistory.push(analysis);
    localStorage.setItem('analysisHistory', JSON.stringify(state.analysisHistory));

    showSuccess('Analysis saved to history!');
}

/**
 * Load analysis history
 */
function loadAnalysisHistory() {
    if (!DOM.historyListContainer) return;

    if (state.analysisHistory.length === 0) {
        DOM.historyListContainer.innerHTML = '<p class="text-slate-400">No analysis history yet</p>';
        return;
    }

    DOM.historyListContainer.innerHTML = state.analysisHistory
        .reverse()
        .map(analysis => `
            <div class="card p-5 md:p-6">
                <div class="flex justify-between items-start mb-3">
                    <div>
                        <p class="text-sm text-slate-400">${analysis.timestamp}</p>
                        <p class="text-2xl font-bold mt-2">
                            <span class="${analysis.matchPercentage >= 80 ? 'score-green' : analysis.matchPercentage >= 50 ? 'score-yellow' : 'score-red'}" style="background-clip: text; -webkit-background-clip: text; -webkit-text-fill-color: transparent;">
                                ${parseFloat(analysis.matchPercentage).toFixed(1)}%
                            </span>
                        </p>
                    </div>
                    <button class="btn-secondary px-3 py-2 text-sm" onclick="loadAnalysisDetails(${analysis.id})">
                        View Details
                    </button>
                </div>
                <div class="text-sm text-slate-400">
                    <p>Matched: ${analysis.matchedSkills.length} skills | Missing: ${analysis.missingSkills.length} skills</p>
                </div>
            </div>
        `)
        .join('');
}

/**
 * Load job alerts
 */
function loadJobAlerts() {
    if (!DOM.alertsListContainer) return;

    // This would load alerts from the server
    // For now, show placeholder
    DOM.alertsListContainer.innerHTML = '<p class="text-slate-400">No active alerts yet. Create your first alert above!</p>';
}

/**
 * Show error message
 */
function showError(message) {
    if (DOM.errorMessage && DOM.errorContainer) {
        DOM.errorMessage.textContent = message;
        DOM.errorContainer.classList.remove('hidden');
    }
}

/**
 * Hide error message
 */
function hideError() {
    if (DOM.errorContainer) {
        DOM.errorContainer.classList.add('hidden');
    }
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

/**
 * Set button loading state
 */
function setButtonLoading(isLoading) {
    if (!DOM.analyzeBtn) return;

    DOM.analyzeBtn.disabled = isLoading;
    
    if (isLoading) {
        DOM.loadingSpinner.classList.remove('hidden');
    } else {
        DOM.loadingSpinner.classList.add('hidden');
    }

    // Sync FAB loading/disabled state
    const fabAnalyze = document.getElementById('fabAnalyze');
    if (fabAnalyze) {
        fabAnalyze.disabled = isLoading;
        if (isLoading) {
            fabAnalyze.classList.add('loading');
        } else {
            fabAnalyze.classList.remove('loading');
        }
    }
}

/**
 * Escape HTML special characters to prevent XSS
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Load analysis details from history
 */
function loadAnalysisDetails(id) {
    const analysis = state.analysisHistory.find(a => a.id === id);
    if (analysis) {
        // Scroll to analyzer
        switchPage('analyzer');
        
        // Display the analysis
        displayResults(analysis);
        state.lastAnalysisResult = analysis;
    }
}

// Initialize the application when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initApp);
} else {
    initApp();
}
