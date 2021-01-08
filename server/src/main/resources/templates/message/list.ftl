<!DOCTYPE html>
<html lang="en">
<#include "../common/header.ftl">
<body>
<div class="x-nav">
      <span class="layui-breadcrumb">
        <a href="">后台</a>
        <a href="">管理</a>
        <a>
          <cite>消息列表</cite></a>
      </span>
    <a class="layui-btn layui-btn-small" style="line-height:1.6em;margin-top:3px;float:right" href="javascript:location.replace(location.href);" title="刷新">
        <i class="layui-icon" style="line-height:30px">ဂ</i></a>
</div>
<div class="x-body">
    <div class="layui-row">
        <form class="layui-form layui-col-md12 x-so">
            <input type="text" name="message"  placeholder="请输入消息" autocomplete="off" class="layui-input" value="${RequestParameters['message']!''}" />
            <button class="layui-btn"  lay-submit="" lay-filter="sreach"><i class="layui-icon">&#xe615;</i></button>
        </form>
    </div>
 <#if superAdmin == true>
        <xblock>

            <button class="layui-btn" onclick="x_admin_show('新建消息', '/admin/message/add')"><i class="layui-icon"></i>新建消息</button>

            <span class="x-right" style="line-height:40px">共有数据：${info.getTotalElements()} 条</span>
        </xblock>
 </#if>
    <table class="layui-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>发送</th>
            <th>接收</th>
            <th>消息</th>
            <th>时间</th>
            <th>状态</th>
            <td>操作</td>
        </tr>
        </thead>
        <tbody>
        <#list info.content as v>
            <tr>
                <td>${v.id}</td>
                <td>${v.sendName}</td>
                <td>${v.receiveName}</td>
                <td>${v.message}</td>
                <td>${v.createTime}</td>
                <td class="td-status">
                    <#if v.isRead == 0 >
                    <span class="layui-btn layui-btn-normal layui-btn-mini">未读</span>
                    <#else>
                    <span class="layui-btn layui-btn-normal layui-btn-mini layui-btn-disabled">已读</span>
                    </#if>
                </td>
                <td class="td-manage">

                    <a title="阅读" onclick="x_admin_show('阅读', '/admin/message/edit?id=${v.id}')" href="javascript:;">
                      <i class="layui-icon">&#xe618;</i>
                    </a>

                    <a title="删除" onclick="member_del(this,'${v.id}')" href="javascript:;">
                        <i class="layui-icon">&#xe640;</i>
                    </a>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
    <div class="page">
        <div>
            ${html}
        </div>
    </div>

</div>
<script>
    layui.use('laydate', function(){
        var laydate = layui.laydate;

        //执行一个laydate实例
        laydate.render({
            elem: '#start' //指定元素
        });

        //执行一个laydate实例
        laydate.render({
            elem: '#end' //指定元素
        });
    });


    /*用户-删除*/
    function member_del(obj,id){
        layer.confirm('确认要删除吗？',function(){
            $.ajax({
                url:"/admin/message/delete",
                data:{"id":id,},
                type:"post",
                dataType:"json",
                success:function(result){
                    if(result.code==200){
                        layer.msg('删除成功',{icon:1,anim:1,time:1000},function(){
                            $(obj).parents("tr").remove();
                        });
                    }else{
                        layer.msg('删除失败',{anim:6,time:2000});
                    }
                },
                error:function(){
                    layer.msg('网络繁忙',{anim:6});
                }
            });
        });
    }
</script>
</body>

</html>