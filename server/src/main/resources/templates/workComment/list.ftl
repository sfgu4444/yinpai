<!DOCTYPE html>
<html lang="en">
<#include "../common/header.ftl">
<body>
<div class="x-nav">
      <span class="layui-breadcrumb">
        <a href="">后台</a>
        <a href="">管理</a>
        <a>
          <cite>评论列表</cite></a>
      </span>
    <a class="layui-btn layui-btn-small" style="line-height:1.6em;margin-top:3px;float:right" href="javascript:location.replace(location.href);" title="刷新">
        <i class="layui-icon" style="line-height:30px">ဂ</i></a>
</div>
<div class="x-body">
    <div class="layui-row">
    </div>
    <xblock>
        <span class="x-right" style="line-height:40px">共有数据：${info.getTotalElements()} 条</span>
    </xblock>
    <table class="layui-table">
        <thead>
        <tr>
            <th>评论ID</th>
            <th>所属作品标题</th>
            <th>所属作品封面</th>
            <th>用户昵称</th>
            <th>用户头像</th>
            <th>评论内容</th>
            <th>评论时间</th>
            <th>回复内容</th>
            <th>回复时间</th>
            <td>操作</td>
        </tr>
        </thead>
        <tbody>
        <#list info.content as v>
            <tr>
                <td>${v.id}</td>
                <td>${v.title!'-'}</td>
                <td><img src="${v.coverImageUrl!'-'}" alt="${v.coverImageUrl!'-'}" /></td>
                <td>${v.nickName!'-'}</td>
                <td><img src="${v.avatarUrl!''}" width="100" alt="${v.avatarUrl!''}" /></td>
                <td>${v.content!'-'}</td>
                <td>${v.createTime!'-'}</td>
                <td>${v.replyContent!'-'}</td>
                <td>${v.replyTime!'-'}</td>
                <td class="td-manage">
                    <a title="回复" onclick="x_admin_show('回复', '/admin/workComment/reply?id=${v.id}')" href="javascript:;">
                        <i class="layui-icon">&#xe642;</i>
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
                url:"/admin/workComment/delete",
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