<!DOCTYPE html>
<html lang="en">
<#include "../common/header.ftl">
<body>
<div class="x-nav">
      <span class="layui-breadcrumb">
        <a href="">后台</a>
        <a href="">管理</a>
        <a>
          <cite>意见反馈</cite></a>
      </span>
    <a class="layui-btn layui-btn-small" style="line-height:1.6em;margin-top:3px;float:right" href="javascript:location.replace(location.href);" title="刷新">
        <i class="layui-icon" style="line-height:30px">ဂ</i></a>
</div>
<div class="x-body">
    <div class="layui-row">
        <form class="layui-form layui-col-md12 x-so">
            <input type="text" name="nickName"  placeholder="请输入昵称" autocomplete="off" class="layui-input" value="${RequestParameters['nickName']!''}" />
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
            <th>账号</th>
            <th>昵称</th>
            <th>头像</th>
            <th>意见</th>
            <th>邮箱</th>
            <th>时间</th>
            <td>操作</td>
        </tr>
        </thead>
        <tbody>
        <#list info.content as v>
            <tr>
                <td>${v.id}</td>
                <td>${v.user.username!'-'}</td>
                <td>${v.user.nickName!'-'}</td>
                <td><img width="100" src="${v.user.avatarUrl}" alt="${v.user.avatarUrl}"></td>
                <td>${v.content!'-'}</td>
                <td>${v.email!'-'}</td>
                <td>${v.createTime!'-'}</td>
                <td class="td-manage">
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
                url:"/admin/admin/status",
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


    /*用户-删除*/
    function member_del(obj,id){
        layer.confirm('确认要删除吗？',function(){
            $.ajax({
                url:"/admin/advice/delete",
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