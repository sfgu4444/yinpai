<!doctype html>
<html lang="en">
<#include "../common/header.ftl">
<body class="login-bg">

<div class="login layui-anim layui-anim-up">
    <div class="message">管理登录</div>
    <div id="darkbannerwrap"></div>

    <form method="post" class="layui-form" >
        <input name="adminName" placeholder="用户名"  type="text" lay-verify="required" class="layui-input" >
        <hr class="hr15">
        <input name="adminPass" lay-verify="required" placeholder="密码"  type="password" class="layui-input">
        <hr class="hr15">
        <input value="登录" lay-submit lay-filter="login" style="width:100%;" type="submit">
        <hr class="hr20" >
    </form>
</div>

<script>
    $(function  () {
        layui.use('form', function(){
            var form = layui.form;
            //监听提交
            form.on('submit(login)', function(data){
                $.ajax({
                    url:"/admin/login",
                    data:data.field,
                    type:'post',
                    dataType:'json',
                    success:function(result){
                        if(result.code === 200){
                            layer.msg(result.msg,{anim:1,time:1000,icon:1},function(){
                                location.href = "/index";
                            });
                        }else{
                            layer.msg(result.msg,{anim:6,time:2000});
                        }
                    },
                    error:function(){
                        layer.msg('网络繁忙',{anim:6,time:2000});
                    }
                });
                return false;
            });
        });
    });
</script>
</body>
</html>