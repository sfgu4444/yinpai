<!DOCTYPE html>
<html>
<#include "../common/header.ftl">
<body>
<div class="x-body">
    <form class="layui-form">

        <div class="layui-form-item">
            <label for="message" class="layui-form-label">
                <span class="x-red">${info.money}</span>余额
            </label>
            <div class="layui-input-inline">
                <input type="money" id="money" name="money" required="" lay-verify="required" autocomplete="off" class="layui-input" placeholder="输入提现金额">
            </div>

        </div>

        <div class="layui-form-item">
            <label for="L_repass" class="layui-form-label">
            </label>
            <button  class="layui-btn" lay-filter="add" lay-submit="">
                提交
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


        //监听提交
        form.on('submit(add)', function(data){
            $.ajax({
                url:"/admin/withdrawal/add",
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