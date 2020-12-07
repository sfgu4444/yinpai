<!DOCTYPE html>
<html>
<#include "../common/header.ftl">
<body>
<div class="x-body">
    <form class="layui-form">
        <input type="hidden" name="id" value="${data.id!'-'}">

        <div class="layui-form-item">
            <label for="" class="layui-form-label">
                <span class="x-red">*</span>发布审核
            </label>
            <div class="layui-input-inline">
                <input type="radio" name="isAudit" <#if (data.isAudit!1) == 1>checked="checked"</#if> value="1" title="是">
                <input type="radio" name="isAudit" <#if (data.isAudit!1) == 0>checked="checked"</#if> value="0" title="否">
            </div>
        </div>

        <div class="layui-form-item">
            <label for="L_repass" class="layui-form-label">
            </label>
            <button  class="layui-btn" lay-filter="add" lay-submit="">
                确定
            </button>
        </div>
    </form>
</div>
<script>
    layui.use(['form', 'layer'], function(){
        $ = layui.jquery;
        var form = layui.form
            ,layer = layui.layer;

        //监听提交
        form.on('submit(add)', function(data){
            $.ajax({
                url:"/admin/admin/audit",
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