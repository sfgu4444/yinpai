<!DOCTYPE html>
<html lang="en">
<#include "../common/header.ftl">
<body>
<div class="x-nav">
      <span class="layui-breadcrumb">
        <a href="">后台</a>
        <a href="">管理</a>
        <a>
          <cite>举报列表</cite></a>
      </span>
    <a class="layui-btn layui-btn-small" style="line-height:1.6em;margin-top:3px;float:right" href="javascript:location.replace(location.href);" title="刷新">
        <i class="layui-icon" style="line-height:30px">ဂ</i></a>
</div>
<div class="x-body">
    <div class="layui-row">
        <form class="layui-form layui-col-md12 x-so">
            <input type="text" name="content"  placeholder="请输入内容" autocomplete="off" class="layui-input" value="${RequestParameters['content']!''}" />
            <button class="layui-btn"  lay-submit="" lay-filter="sreach"><i class="layui-icon">&#xe615;</i></button>
        </form>
    </div>
    <xblock>
        <span class="x-right" style="line-height:40px">共有数据：${info.getTotalElements()} 条</span>
    </xblock>
    <table class="layui-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>昵称</th>
            <th>标题</th>
            <th>类型</th>
            <th>内容</th>
            <th>时间</th>
            <th>作品状态</th>
            <td>操作</td>
        </tr>
        </thead>
        <tbody>
        <#list info.content as v>
            <tr>
                <td>${v.id}</td>
                <td>${v.nickName!'-'}</td>
                <td>${v.title!'-'}</td>
                <td>${v.type!'-'}</td>
                <td>${v.content!'-'}</td>
                <td>${v.createTime!'-'}</td>
                <td class="td-status">
                    <#if v.status == 1 >
                    <span class="layui-btn layui-btn-normal layui-btn-mini">已启用</span>
                    <#else>
                    <span class="layui-btn layui-btn-normal layui-btn-mini layui-btn-disabled">已停用</span>
                    </#if>
                </td>
                <td class="td-manage">
                    <a onclick="member_stop(this, ${v.workId})" href="javascript:;" <#if v.status == 1>title="启用"<#else>title="停用"</#if>>
                    <#if v.status == 1>
                    <i class="layui-icon">&#xe601;</i>
                    <#else>
                    <i class="layui-icon">&#xe62f;</i>
                    </#if>
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

    /*用户-停用*/
    function member_stop(obj,id){
        var title = $(obj).attr('title');
        if(title=='启用'){
            var text = '停用';
        }else{
            var text = '启用';
        }
        layer.confirm('确认'+text+'吗？',function(){
            $.ajax({
                url:"/admin/work/status",
                data:{'id':id},
                type:"post",
                dataType:"json",
                success:function(result){
                    if(result.code==200){
                        if(result.data==1){
                            $(obj).attr('title','启用');
                            $(obj).find('i').html('&#xe601;');
                            $(obj).parents("tr").find(".td-status").find('span').removeClass('layui-btn-disabled').html('已启用');
                            layer.msg('已启用!',{icon: 1,time:1000});
                        }else{
                            //发异步把用户状态进行更改
                            $(obj).attr('title','停用')
                            $(obj).find('i').html('&#xe62f;');
                            $(obj).parents("tr").find(".td-status").find('span').addClass('layui-btn-disabled').html('已停用');
                            layer.msg('已停用!',{icon: 5,time:1000});
                        }
                        location.replace(location.href);
                    }else{
                        layer.msg(result.msg,{time:2000,anim:6});
                    }
                },
                error:function(){
                    layer.msg('网络繁忙',{time:2000,anim:6});
                }
            })
        });
    }

</script>
</body>

</html>