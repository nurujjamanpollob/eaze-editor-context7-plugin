async function loadSettings() {
    try {
        const response = await fetch('api/get-settings');
        const data = await response.json();

        // Map the settings to the UI elements
        document.getElementById('enable_plugin').checked = data.enabled === true;
        document.getElementById('api_key').value = data.apiKey || '';
        document.getElementById('base_url').value = data.baseUrl || 'https://context7.com/api/v2';
        document.getElementById('default_library').value = data.defaultLibrary || 'open-source-libraries';
        document.getElementById('search_timeout').value = data.searchTimeout || '30';
        document.getElementById('max_results').value = data.maxResults || '10';
        
        console.log('Settings loaded successfully');
    } catch (error) {
        console.error('Error loading settings:', error);
        showStatus('Error loading settings', 'text-red-500');
    }
}

async function saveSettings() {
    const saveBtn = document.getElementById('save-btn');
    saveBtn.disabled = true;
    saveBtn.innerText = 'Saving...';
    
    const settings = {
        enabled: document.getElementById('enable_plugin').checked,
        apiKey: document.getElementById('api_key').value,
        baseUrl: document.getElementById('base_url').value,
        defaultLibrary: document.getElementById('default_library').value,
        searchTimeout: parseInt(document.getElementById('search_timeout').value),
        maxResults: parseInt(document.getElementById('max_results').value)
    };

    try {
        const response = await fetch('api/save-settings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(settings)
        });
        
        const result = await response.json();
        if (response.ok && result.status === 'success') {
            showStatus('Settings saved successfully!', 'text-green-500');
        } else {
            showStatus('Failed to save settings: ' + (result.message || 'Unknown error'), 'text-red-500');
        }
    } catch (error) {
        console.error('Error saving settings:', error);
        showStatus('Error saving settings', 'text-red-500');
    } finally {
        saveBtn.disabled = false;
        saveBtn.innerText = 'Save Settings';
    }
}

function showStatus(message, colorClass) {
    const statusMsg = document.getElementById('status-msg');
    statusMsg.innerText = message;
    statusMsg.className = `mt-4 text-sm font-medium ${colorClass}`;
    
    setTimeout(() => {
        statusMsg.innerText = '';
    }, 3000);
}

document.addEventListener('DOMContentLoaded', () => {
    loadSettings();
    document.getElementById('save-btn').addEventListener('click', saveSettings);
});
