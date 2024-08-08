function login() {
	var username = $("#username").val();
	var password = $("#password").val();

	$.ajax({
		type: "POST",
		url: "/demo/login",
		contentType: "application/json",
		dataType: "json",
		data: JSON.stringify({ username: username, password: password }),
		success: function(response) {
			if (response.status === "success") {
				window.location.href = "/demo/success";
			}
		},
		error: function(xhr) {
			alert("An error occurred: " + xhr.responseJSON.message);
			document.querySelector('input[name="username"]').value = '';
			document.querySelector('input[name="password"]').value = '';
		}
	});
}

document.querySelectorAll('[data-bs-theme-value]').forEach(function(themeButton) {
	themeButton.addEventListener('click', function() {
		var theme = themeButton.getAttribute('data-bs-theme-value');
		
		document.documentElement.setAttribute('data-bs-theme', theme);

		
		document.querySelectorAll('[data-bs-theme-value]').forEach(function(btn) {
			btn.setAttribute('aria-pressed', 'false');
			btn.classList.remove('active');
		});
		themeButton.setAttribute('aria-pressed', 'true');
		themeButton.classList.add('active');

		
		updateThemeIcon(theme);
	});
});

function updateThemeIcon(theme) {
	var iconHref;
	switch (theme) {
		case 'light':
			iconHref = '#sun-fill';
			break;
		case 'dark':
			iconHref = '#moon-stars-fill';
			break;
	}
	document.querySelector('.theme-icon-active use').setAttribute('href', iconHref);
}
