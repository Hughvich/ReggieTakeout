function loginApi(data) {
  // 通过axios向后端发送异步请求，data数据存在loginForm，
  return $axios({
    'url': '/employee/login',
    'method': 'post',
    data
  })
}

function logoutApi(){
  return $axios({
    'url': '/employee/logout',
    'method': 'post',
  })
}
