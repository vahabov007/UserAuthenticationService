document.addEventListener('DOMContentLoaded', function () {
    const registrationForm = document.getElementById('registration-form');
    const mailInput = document.getElementById('mail');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const dateOfBirthInput = document.getElementById('dateOfBirth');
    const pinInput = document.getElementById('pin');
    const emailSection = document.getElementById('email-section');
    const pinVerificationArea = document.getElementById('pin-verification-area');
    const passwordCreationArea = document.getElementById('password-creation-area');
    const sendPinBtn = document.getElementById('send-pin-btn');
    const verifyPinBtn = document.getElementById('verify-pin-btn');
    const completeRegistrationBtn = document.getElementById('complete-registration-btn');
    const loadingOverlay = document.getElementById('loading-overlay');
    const loadingText = document.getElementById('loading-text');
    const emailErrorMessage = document.getElementById('email-error-message');
    const pinErrorMessage = document.getElementById('pin-error-message');
    const passwordErrorMessage = document.getElementById('password-error-message');
    const dateOfBirthError = document.getElementById('date-of-birth-error');
    const usernameErrorMessage = document.getElementById('username-error-message');
    const generalErrorMessage = document.getElementById('general-error-message');
    const existingAccountModal = document.getElementById('existing-account-modal');
    const modalCloseBtn = existingAccountModal ? existingAccountModal.querySelector('.close-btn') : null;
    const goToSigninBtn = document.getElementById('go-to-signin-btn');
    const pinTimer = document.getElementById('pin-timer');
    const resendPinBtn = document.getElementById('resend-pin-btn');

    let resendCount = 0;
    let timerInterval;
    let timerSeconds = 120;

    const passwordToggles = document.querySelectorAll('.password-toggle');
    passwordToggles.forEach(toggle => {
        toggle.addEventListener('click', function() {
            const input = this.parentElement.querySelector('input');
            if (!input) return;
            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);

            const icon = this.querySelector('i');
            if (!icon) return;
            if (type === 'password') {
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            } else {
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            }
        });
    });

    function startTimer() {
        timerSeconds = 120;
        clearInterval(timerInterval);
        if (pinTimer) {
            pinTimer.textContent = '02:00';
            pinTimer.style.display = 'block';
        }
        if (pinErrorMessage) {
            pinErrorMessage.style.display = 'none';
        }

        timerInterval = setInterval(() => {
            let minutes = Math.floor(timerSeconds / 60);
            let seconds = timerSeconds % 60;
            if (pinTimer) {
                pinTimer.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
            }

            if (timerSeconds <= 0) {
                clearInterval(timerInterval);
                if (pinTimer) {
                    pinTimer.style.display = 'none';
                }
            }
            timerSeconds--;
        }, 1000);
    }

    function showInlineMessage(element, message, isError = true) {
        if (element) {
            element.textContent = message;
            element.style.display = 'block';
            element.style.color = isError ? '#d9534f' : '#5cb85c';
        }
    }

    function hideInlineMessage(element) {
        if (element) {
            element.style.display = 'none';
            element.textContent = '';
        }
    }

    function showLoading(text) {
        if (loadingOverlay) {
            loadingText.textContent = text;
            loadingOverlay.style.display = 'flex';
        }
    }

    function hideLoading() {
        if (loadingOverlay) {
            loadingOverlay.style.display = 'none';
        }
    }

    function showInfoModal(title, message, isSuccess = true) {
        const modal = document.createElement('div');
        modal.className = 'modal';
        modal.innerHTML = `
            <div class="modal-content">
                <span class="info-close-btn">&times;</span>
                <div class="success-checkmark" style="display: ${isSuccess ? 'block' : 'none'};">
                    <div class="check-icon">
                        <span class="icon-line line-tip"></span>
                        <span class="icon-line line-long"></span>
                        <div class="icon-circle"></div>
                        <div class="icon-fix"></div>
                    </div>
                </div>
                <h3 style="color: ${isSuccess ? '#5cb85c' : '#d9534f'};">${title}</h3>
                <p>${message}</p>
            </div>
        `;
        document.body.appendChild(modal);
        setTimeout(() => {
            modal.style.display = 'flex';
        }, 10);
        modal.querySelector('.info-close-btn').addEventListener('click', () => {
            modal.style.display = 'none';
            modal.remove();
        });
        window.addEventListener('click', (event) => {
            if (event.target === modal) {
                modal.style.display = 'none';
                modal.remove();
            }
        });
    }

    if(sendPinBtn) {
        sendPinBtn.addEventListener('click', function () {
            const mail = (mailInput?.value || '').trim();
            const emailRegex = /^[a-zA-Z0-9._-]+@gmail\.com$/;

            hideInlineMessage(emailErrorMessage);

            if (!emailRegex.test(mail)) {
                showInlineMessage(emailErrorMessage, 'Please enter a valid Gmail address.');
                return;
            }

            showLoading('Sending PIN code...');

            fetch('/api/register/send-pin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ mail: mail })
            })
            .then(response => response.json())
            .then(data => {
                hideLoading();
                if (data.success) {
                    if (emailSection) emailSection.style.display = 'none';
                    if (sendPinBtn) sendPinBtn.style.display = 'none';
                    if (pinVerificationArea) pinVerificationArea.style.display = 'block';
                    if (verifyPinBtn) verifyPinBtn.style.display = 'block';
                    resendCount = 0;
                    startTimer();
                    showInfoModal('Success', data.message, true);
                } else {
                    if (data.message === 'Email already exists.') {
                        if (existingAccountModal) existingAccountModal.style.display = 'flex';
                    } else {
                        showInlineMessage(emailErrorMessage, data.message);
                    }
                }
            })
            .catch(error => {
                hideLoading();
                console.error('Error:', error);
                showInlineMessage(emailErrorMessage, 'An error occurred. Please try again.');
            });
        });
    }

    if (verifyPinBtn) {
        verifyPinBtn.addEventListener('click', function () {
            const mail = (mailInput?.value || '').trim();
            const pin = (pinInput?.value || '').trim();

            hideInlineMessage(pinErrorMessage);

            if (!pin) {
                showInlineMessage(pinErrorMessage, 'Please enter the PIN code.');
                return;
            }

            showLoading('Verifying PIN...');

            fetch('/api/register/verify-pin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ mail: mail, pin: pin })
            })
            .then(response => response.json())
            .then(data => {
                hideLoading();
                if (data.success) {
                    if (pinVerificationArea) pinVerificationArea.style.display = 'none';
                    if (passwordCreationArea) passwordCreationArea.style.display = 'block';
                    clearInterval(timerInterval);
                    if (pinTimer) pinTimer.style.display = 'none';
                    hideInlineMessage(pinErrorMessage);
                } else {
                    showInlineMessage(pinErrorMessage, data.message);
                }
            })
            .catch(error => {
                hideLoading();
                console.error('Error:', error);
                showInlineMessage(pinErrorMessage, 'An error occurred. Please try again.');
            });
        });
    }

    if (completeRegistrationBtn) {
        completeRegistrationBtn.addEventListener('click', function (event) {
            const username = (usernameInput?.value || '').trim();
            const password = (passwordInput?.value || '');
            const confirmPassword = (confirmPasswordInput?.value || '');
            const mail = (mailInput?.value || '').trim();
            const dateInput = (dateOfBirthInput?.value || '');

            hideInlineMessage(usernameErrorMessage);
            hideInlineMessage(passwordErrorMessage);
            hideInlineMessage(dateOfBirthError);
            hideInlineMessage(generalErrorMessage);

            if (!username || !password || !confirmPassword || !dateInput) {
                showInlineMessage(generalErrorMessage, 'All fields are required.');
                return;
            }

            if (username.length < 3 || username.length > 40) {
                showInlineMessage(usernameErrorMessage, 'Username must be between 3 and 40 characters long.');
                return;
            }

            if (password.length < 8) {
                showInlineMessage(passwordErrorMessage, 'Password must be at least 8 characters long.');
                return;
            }

            if (password !== confirmPassword) {
                showInlineMessage(passwordErrorMessage, 'Passwords do not match.');
                return;
            }

            const today = new Date();
            const birthDate = new Date(dateInput);

            if (isNaN(birthDate.getTime())) {
                showInlineMessage(dateOfBirthError, 'Please enter a valid date of birth.');
                return;
            }

            let minDate = new Date();
            minDate.setFullYear(minDate.getFullYear() - 18);
            if (birthDate > minDate) {
                showInlineMessage(dateOfBirthError, 'You must be at least 18 years old to register.');
                return;
            }

            let maxDate = new Date();
            maxDate.setFullYear(maxDate.getFullYear() - 125);
            if (birthDate < maxDate) {
                showInlineMessage(dateOfBirthError, 'Please enter a valid date of birth.');
                return;
            }

            showLoading('Completing registration...');

            fetch('/api/register/complete-registration', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    mail,
                    username,
                    password,
                    confirmPassword,
                    dateOfBirth: dateInput
                })
            })
            .then(response => response.json())
            .then(data => {
                hideLoading();
                if (data.success) {
                    showInfoModal('Success', data.message, true);
                    setTimeout(() => {
                        window.location.href = '/my-login';
                    }, 3000);
                } else {
                    showInlineMessage(passwordErrorMessage, data.message);
                }
            })
            .catch(error => {
                hideLoading();
                console.error('Error:', error);
                showInlineMessage(passwordErrorMessage, 'An error occurred. Please try again.');
            });
        });
    }

    if (resendPinBtn) {
        resendPinBtn.addEventListener('click', function () {
            const mail = (mailInput?.value || '').trim();

            if (!mail) {
                showInlineMessage(pinErrorMessage, 'Please go back and enter your email address.', true);
                return;
            }

            if (resendCount >= 3) {
                pinErrorMessage.innerHTML = '<span style="color: #d9534f;">You have exceeded the maximum number of resend attempts. Please <a href="https://vahabvahabov.site/#contact" target="_blank">contact support</a> for assistance.</span>';
                pinErrorMessage.style.display = 'block';
                console.log('Resend blocked: Max attempts reached.');
                return;
            }

            resendPinBtn.disabled = true;
            showLoading('Resending PIN...');

            fetch('/api/register/resend-pin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ mail: mail })
            })
            .then(response => response.json())
            .then(data => {
                hideLoading();
                if (data.success) {
                    resendCount++;
                    startTimer();
                    showInfoModal('Success', 'A new PIN has been sent to your email address.', true);
                    resendPinBtn.disabled = false;
                } else {
                    showInlineMessage(pinErrorMessage, data.message, true);
                    resendPinBtn.disabled = false;
                }
            })
            .catch(error => {
                hideLoading();
                console.error('Error:', error);
                showInlineMessage(pinErrorMessage, 'An error occurred. Please try again.', true);
                resendPinBtn.disabled = false;
            });
        });
    }

    if (modalCloseBtn) {
        modalCloseBtn.addEventListener('click', function () {
            if (existingAccountModal) existingAccountModal.style.display = 'none';
        });
    }

    if (goToSigninBtn) {
        goToSigninBtn.addEventListener('click', function () {
            window.location.href = '/my-login';
        });
    }
});