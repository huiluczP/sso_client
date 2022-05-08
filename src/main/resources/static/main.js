$(function(){
    getInfo()
    $('#logout').bind('click', logOut)
})

function getInfo(){
    // ajax请求找下数据库中该文件是否存在
    $.ajax({
        url:"/userInfo",
        type:"post",
        cache: false,
        data: {
        },
        dataType: 'json',
        success:function(data){
            var user = data.user
            var role = data.role
            $('#username').html(user)
            $('#role').html(role)
        },
        error:function(){
            console.log("user信息获取错误")
        }
    })
}

function logOut(){
    $.ajax({
        url:"/logout",
        type:"post",
        cache: false,
        data: {
        },
        dataType: 'json',
        success:function(data){
            var url = data.message
            console.log(url)
            window.location.href = url
        },
        error:function(){
            console.log("user信息获取错误")
        }
    })
}