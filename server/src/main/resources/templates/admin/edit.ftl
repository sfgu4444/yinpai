<!DOCTYPE html>
<html>
<#include "../common/header.ftl">
<body>
<div class="x-body">
    <form class="layui-form">
        <div class="layui-form-item">
            <input type="hidden" name="id" value="${data.id!'-'}">
            <label for="adminName" class="layui-form-label">
                <span class="x-red">*</span>用户名
            </label>
            <div class="layui-input-inline">
                <input type="text" id="adminName" name="adminName" required="" lay-verify="required" autocomplete="off" class="layui-input" placeholder="请输入用户名" value="${data.adminName!''}">
            </div>
            <div class="layui-form-mid layui-word-aux">
                用户名为4～16个字符
            </div>
        </div>

        <div class="layui-form-item">
            <label for="adminPass" class="layui-form-label">
                密码
            </label>
            <div class="layui-input-inline">
                <input type="password" id="adminPass" name="adminPass" autocomplete="off" class="layui-input" placeholder="请输入密码">
            </div>
            <div class="layui-form-mid layui-word-aux">
                密码为6~16个字符
            </div>
        </div>

        <div class="layui-form-item">
            <label for="adminCheckPass" class="layui-form-label">
                确认密码
            </label>
            <div class="layui-input-inline">
                <input type="password" id="adminCheckPass" name="adminCheckPass" autocomplete="off" class="layui-input" placeholder="请输入确认密码">
            </div>
            <div class="layui-form-mid layui-word-aux">
                请保持与密码一致
            </div>
        </div>

        <div class="layui-form-item">
            <label for="nickName" class="layui-form-label">
                <span class="x-red">*</span>昵称
            </label>
            <div class="layui-input-inline">
                <input type="text" id="nickName" name="nickName" required="" lay-verify="required" autocomplete="off" class="layui-input" placeholder="请输入昵称" value="${data.nickName!''}">
            </div>
            <div class="layui-form-mid layui-word-aux">
                用户名为4～16个字符
            </div>
        </div>

        <div class="layui-form-item type-2">
            <label class="layui-form-label">
                <span class="x-red">*</span>头像
            </label>
            <div class="add_pic">
                <div id="cover">
                    <input type="hidden" name="avatarUrl" value="${data.avatarUrl!''}">
                    <img src="${data.avatarUrl!'/static/images/camera.png'}" width="200" id="avatarUrl">
                </div>
            </div>
        </div>

        <div class="layui-form-item type-2">
            <label class="layui-form-label">
                <span class="x-red">*</span>背景图
            </label>
            <div class="add_pic">
                <div id="cover_b">
                    <input type="hidden" name="backgroundUrl" value="${data.backgroundUrl!''}">
                    <img src="${data.backgroundUrl!'/static/images/camera.png'}" width="200" id="backgroundUrl">
                </div>
            </div>
        </div>


        <div class="layui-form-item">
            <label for="monthPayPrice" class="layui-form-label">
                月付价格
            </label>
            <div class="layui-input-inline">
                <input type="text" id="monthPayPrice" name="monthPayPrice" autocomplete="off" class="layui-input" placeholder="请输入月付价格" value="${data.monthPayPrice!''}">
            </div>
            <div class="layui-form-mid layui-word-aux">
                不填表示不支持此方式
            </div>
        </div>

        <div class="layui-form-item">
            <label for="quarterPayPrice" class="layui-form-label">
                季付价格
            </label>
            <div class="layui-input-inline">
                <input type="text" id="quarterPayPrice" name="quarterPayPrice" autocomplete="off" class="layui-input" placeholder="请输入季付价格" value="${data.quarterPayPrice!''}">
            </div>
            <div class="layui-form-mid layui-word-aux">
                不填表示不支持此方式
            </div>
        </div>

        <div class="layui-form-item">
            <label for="quarterPayPrice" class="layui-form-label">
                年付价格
            </label>
            <div class="layui-input-inline">
                <input type="text" id="yearPayPrice" name="yearPayPrice" autocomplete="off" class="layui-input" placeholder="请输入年付价格" value="${data.yearPayPrice!''}">
            </div>
            <div class="layui-form-mid layui-word-aux">
                不填表示不支持此方式
            </div>
        </div>

        <div class="layui-form-item">
            <label for="L_repass" class="layui-form-label">
            </label>
            <button  class="layui-btn" lay-filter="add" lay-submit="">
                修改
            </button>
        </div>
    </form>
</div>
<script>
    layui.use(['form', 'layer', "laydate", "upload"], function(){
        $ = layui.jquery;
        var form = layui.form
            ,layer = layui.layer
            ,laydate = layui.laydate
            ,upload = layui.upload;

        laydate.render({
            elem: "#dueTime",
            type: "datetime"
        });

        upload.render({
            elem: '#backgroundUrl' //绑定元素
            ,url: "/oss/upload" //上传接口
            ,field: 'file'
            ,done: function(res){
                layer.closeAll('loading'); //关闭loading
                if(res.code == 200){
                    $('#cover_b img').attr({
                        'id': 'backgroundUrl',
                    });
                    $("input[name=backgroundUrl]").val(res.data);
                }else{
                    layer.msg(res.msg,{anim:6,time:2000});
                }
            }
            ,before: function(obj){
                layer.closeAll('loading'); //关闭loading
                layer.load(); //上传loading

                //预读本地文件示例，不支持ie8
                obj.preview(function(index, file, result){
                    $('#backgroundUrl').attr('src', result); //图片链接（base64）
                });
            }
            ,error: function (){
                layer.closeAll('loading'); //关闭loading
            }
        });

        upload.render({
            elem: '#avatarUrl' //绑定元素
            ,url: "/oss/upload" //上传接口
            ,field: 'file'
            ,done: function(res){
                layer.closeAll('loading'); //关闭loading
                if(res.code == 200){
                    $('#cover img').attr({
                        'id': 'avatarUrl',
                    });
                    $("input[name=avatarUrl]").val(res.data);
                }else{
                    layer.msg(res.msg,{anim:6,time:2000});
                }
            }
            ,before: function(obj){
                layer.closeAll('loading'); //关闭loading
                layer.load(); //上传loading

                //预读本地文件示例，不支持ie8
                obj.preview(function(index, file, result){
                    $('#avatarUrl').attr('src', result); //图片链接（base64）
                });
            }
            ,error: function (){
                layer.closeAll('loading'); //关闭loading
            }
        });

        //监听提交
        form.on('submit(add)', function(data){
            $.ajax({
                url:"/admin/admin/edit",
                data:data.field,
                type:'post',
                dataType:'json',
                success:function(result){
                    if(result.code === 200){
                        layer.msg(result.msg,{anim:1,time:1000,icon:1},function(){
                            // 获得frame索引
                            var index = parent.layer.getFrameIndex(window.name);
                            //关闭当前frame
                            parent.layer.close(index);
                            parent.window.location.reload();
                        });
                    } else if (result.code === 402) {
                        window.top.location.href="/admin/login";
                    } else{
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
</script>
</body>
</html>