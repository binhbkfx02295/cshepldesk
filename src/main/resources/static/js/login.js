$(function () {
  $('#loginForm').on('submit', function (e) {
    e.preventDefault();
    const username = $('#username').val();
    const password = $('#password').val();

    $.ajax({
      url: '/api/auth/login', // endpoint tương ứng backend bạn đã build
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ username, password }),
      success: function (res) {
        if (res.code === 'MSG_LOGIN_OK') {
          window.location.href = '/dashboard';
        } else {
          $('#loginError').text(res.message).removeClass('d-none');
        }
      },
      error: function () {
        $('#loginError').text('Sai thông tin đăng nhập hoặc lỗi hệ thống.').removeClass('d-none');
      }
    });
  });
});
