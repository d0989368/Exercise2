function login() {
    var username = $("#username").val();
    var password = $("#password").val();

    $.ajax({
        type: "POST",
        url: "/demo/login",
        contentType: "application/json", // 设置内容类型为JSON
        dataType: "json", // 期望返回JSON格式
        data: JSON.stringify({ username: username, password: password }),
        success: function(response) {
            if (response.status === "success") {
                window.location.href = "/demo/success";
            } else {
                alert("Invalid credentials: " + response.message);
            }
        },
        error: function(xhr) {
            alert("An error occurred: " + xhr.responseJSON.message);
        }
    });
}