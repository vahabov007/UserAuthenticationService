document.addEventListener('DOMContentLoaded', function () {
    const forgotPasswordForm = document.getElementById('forgot-password-form');
    const emailInput = document.getElementById('mail');
    const pinInput = document.getElementById('pin');
    const newPasswordInput = document.getElementById('new-password');
    const confirmNewPasswordInput = document.getElementById('confirm-new-password');

    const emailSection = document.getElementById('email-section');
    const pinVerificationArea = document.getElementById('pin-verification-area');
    const newPasswordArea = document.getElementById('new-password-area');
    const loadingOverlay = document.getElementById('loading-overlay');

    const emailErrorMessage = document.getElementById('email-error-message');
    const pinErrorMessage = document.getElementById('pin-error-message');
    const passwordErrorMessage = document.getElementById('password-error-message');

    const sendPinBtn = document.getElementById('send-pin-btn');
    const verifyPinBtn = document.getElementById('verify-pin-btn');
    const resendPinBtn = document.getElementById('resend-pin-btn');
    const resetPasswordBtn = document.getElementById('reset-password-btn');

    const infoModal = document.getElementById('info-modal');
    const modalTitle = document.getElementById('modal-title');
    const modalMessage = document.getElementById('modal-message');
    const modalCloseBtn = document.querySelector('.info-close-btn');
    const successAnimation = document.getElementById('success-animation');
    const pinTimer = document.getElementById('pin-timer');

    let userEmail = '';
    let timerInterval;
    let timerSeconds = 120;
    let resendCount = 0;

    const passwordToggles = document.querySelectorAll('.password-toggle');
    passwordToggles.forEach(toggle => {
        toggle.addEventListener('click', function() {
            const input = this.parentElement.querySelector('input');
            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);

            const icon = this.querySelector('i');
            if (type === 'password') {
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            } else {
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            }
        });
    });

    function showInfoModal(title, message, isSuccess = true) {
        modalTitle.textContent = title;
        modalMessage.textContent = message;
        if (isSuccess) {
            successAnimation.style.display = 'block';
            modalTitle.style.color = '#5cb85c';
        } else {
            successAnimation.style.display = 'none';
            modalTitle.style.color = '#d9534f';
        }
        infoModal.style.display = 'flex';
    }

    modalCloseBtn.addEventListener('click', function() {
        infoModal.style.display = 'none';
    });

    function showLoading(text) {
        loadingOverlay.style.display = 'flex';
        document.getElementById('loading-text').textContent = text;
    }

    function hideLoading() {
        loadingOverlay.style.display = 'none';
    }

    function startTimer() {
        timerSeconds = 120;
        clearInterval(timerInterval);
        pinTimer.textContent = `02:00`;
        pinTimer.style.display = 'block';
        pinErrorMessage.textContent = '';
        pinErrorMessage.style.display = 'none';

        timerInterval = setInterval(() => {
            let minutes = Math.floor(timerSeconds / 60);
            let seconds = timerSeconds % 60;
            pinTimer.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
            if (timerSeconds <= 0) {
                clearInterval(timerInterval);
                pinTimer.style.display = 'none';
            }
            timerSeconds--;
        }, 1000);
    }

    sendPinBtn.addEventListener('click', async function () {
        userEmail = emailInput.value && emailInput.value.trim();
        console.log('Send PIN clicked for', userEmail);

        if (!userEmail || !userEmail.includes('@')) {
            emailErrorMessage.textContent = "Please enter a valid email address.";
            emailErrorMessage.style.display = 'block';
            return;
        }

        showLoading('Sending PIN...');
        try {
            const response = await fetch('/api/forgot-password/send-pin', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ mail: userEmail })
            });

            const data = await response.json();
            console.log('send-pin response', response.status, data);

            if (response.ok) {
                emailSection.style.display = 'none';
                pinVerificationArea.style.display = 'block';
                hideLoading();
                startTimer();
                resendCount = 0;
            } else {
                emailErrorMessage.textContent = data.message || 'Failed to send PIN.';
                emailErrorMessage.style.display = 'block';
                hideLoading();
            }
        } catch (error) {
            console.error('Error sending PIN:', error);
            emailErrorMessage.textContent = 'An error occurred while connecting to the server.';
            emailErrorMessage.style.display = 'block';
            hideLoading();
        }
    });

    verifyPinBtn.addEventListener('click', async function () {
        const pin = pinInput.value && pinInput.value.trim();
        console.log('Verify PIN clicked for', userEmail, 'pin:', pin);
        if (!pin) {
            pinErrorMessage.textContent = "Please enter the PIN code.";
            pinErrorMessage.style.display = 'block';
            return;
        }

        showLoading('Verifying PIN...');
        try {
            const response = await fetch('/api/forgot-password/verify-pin', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ mail: userEmail, pin: pin })
            });

            const data = await response.json();
            console.log('verify-pin response', response.status, data);

            if (response.ok) {
                pinVerificationArea.style.display = 'none';
                newPasswordArea.style.display = 'block';
                hideLoading();
                clearInterval(timerInterval);
                pinTimer.style.display = 'none';
            } else {
                pinErrorMessage.textContent = data.message;
                pinErrorMessage.style.display = 'block';
                hideLoading();
            }
        } catch (error) {
            console.error('Error verifying PIN:', error);
            pinErrorMessage.textContent = 'An error occurred while connecting to the server.';
            pinErrorMessage.style.display = 'block';
            hideLoading();
        }
    });

    resetPasswordBtn.addEventListener('click', async function () {
        const newPassword = newPasswordInput.value;
        const confirmNewPassword = confirmNewPasswordInput.value;

        console.log('Reset password clicked for', userEmail);

        if (newPassword !== confirmNewPassword) {
            passwordErrorMessage.textContent = "The passwords do not match!";
            passwordErrorMessage.style.display = 'block';
            return;
        }
        if (!newPassword || newPassword.length < 8) {
            passwordErrorMessage.textContent = "Password must be at least 8 characters long.";
            passwordErrorMessage.style.display = 'block';
            return;
        }

        showLoading('Resetting password...');
        try {
            const response = await fetch('/api/forgot-password/reset-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ mail: userEmail, newPassword: newPassword })
            });

            const data = await response.json();
            console.log('reset-password response', response.status, data);

            if (response.ok) {
                showInfoModal("Success", data.message, true);
                setTimeout(() => {
                    window.location.href = '/my-login';
                }, 2000);
            } else {
                passwordErrorMessage.textContent = data.message;
                passwordErrorMessage.style.display = 'block';
                hideLoading();
            }
        } catch (error) {
            console.error('Error resetting password:', error);
            passwordErrorMessage.textContent = 'An error occurred while connecting to the server.';
            passwordErrorMessage.style.display = 'block';
            hideLoading();
        }
    });

    resendPinBtn.addEventListener('click', async function () {
        console.log('Resend PIN clicked. resendCount =', resendCount, 'userEmail =', userEmail);

        if (!userEmail) {
            pinErrorMessage.textContent = "Please go back and enter your email address.";
            pinErrorMessage.style.display = 'block';
            return;
        }

        if (resendCount >= 3) {
            pinErrorMessage.innerHTML = 'You have exceeded the maximum number of resend attempts. Please <a href="https://vahabvahabov.site/#contact" target="_blank">contact support</a> for assistance.';
            pinErrorMessage.style.display = 'block';
            console.log('Resend blocked: Max attempts reached.');
            return;
        }

        showLoading('Resending PIN...');

        try {
            const response = await fetch('/api/forgot-password/send-pin', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ mail: userEmail })
            });

            const data = await response.json();
            console.log('resend send-pin response', response.status, data);

            if (response.ok) {
                showInfoModal("Success", data.message, true);
                resendCount++;
                startTimer();
            } else {
                showInfoModal("Error", data.message || 'Failed to resend PIN.', false);
            }
        } catch (error) {
            console.error('Error resending PIN:', error);
            showInfoModal("Error", 'An error occurred while connecting to the server.', false);
        } finally {
            hideLoading();
        }
    });
});