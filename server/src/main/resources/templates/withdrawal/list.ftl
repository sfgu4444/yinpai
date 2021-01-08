<!DOCTYPE html>
<html lang="en">
<#include "../common/header.ftl">
<body>
<div class="x-nav">
      <span class="layui-breadcrumb">
        <a href="">后台</a>
        <a href="">管理</a>
        <a>
          <cite>提现列表</cite></a>
      </span>
    <a class="layui-btn layui-btn-small" style="line-height:1.6em;margin-top:3px;float:right" href="javascript:location.replace(location.href);" title="刷新">
        <i class="layui-icon" style="line-height:30px">ဂ</i></a>
</div>
<div class="x-body">
    <div class="layui-row">
     <#if superAdmin == true>
        <div class="layui-form-item" style="display: inline-flex;">
            <form style="display: flex;flex-direction: row;">
                <select  value="adminName" name="adminName" lay-verify="required" style="height: 38px;margin-right: 10px;">
                    <#list adminList as v>
                    <option id="${v.id}">${v.adminName}</option>
                    </#list>
                </select>
               <button class="layui-btn"  lay-submit="" lay-filter="sreach"><i class="layui-icon">&#xe615;</i></button>

                </form>

        </div>

     </#if>
    </div>

        <xblock>
          <#if superAdmin != true>
            <button class="layui-btn" onclick="x_admin_show('提现', '/admin/withdrawal/add')"><i class="layui-icon"></i>提现</button>
           </#if>
          <span class="x-right" style="line-height:40px">共有数据：${info.getTotalElements()} 条</span>
        </xblock>

    <table class="layui-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>提现人</th>
            <th>银行卡号</th>
            <th>金额</th>
            <th>时间</th>
            <th>状态</th>
            <td>操作</td>
        </tr>
        </thead>
        <tbody>
        <#list info.content as v>
            <tr>
                <td>${v.id}</td>
                <td>${v.adminName}</td>
                <td>${v.bankAccount}
                <td>${v.money}</td>
                <td>${v.createTime}</td>
                <td class="td-status">
                    <#if v.state == 0 >
                    <span class="layui-btn layui-btn-normal layui-btn-mini">提现中</span>
                    <#else>
                    <span class="layui-btn layui-btn-normal layui-btn-mini layui-btn-disabled ">提现成功</span>
                    </#if>
                </td>

                <td class="td-manage">
                    <#if superAdmin == true>
                    <#if v.state == 0>
                    <a title="审批" onclick="member_edit(this,'${v.id}')" href="javascript:;">
                     <i class="layui-icon">&#xe618;</i>
                    </a>
                    </#if>
                    </#if>
                   <#if superAdmin != true>
                   <a title="删除" onclick="member_del(this,'${v.id}')" href="javascript:;">
                       <i class="layui-icon">&#xe640;</i>
                   </a>
                   </#if>

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

        /*审批*/
        function member_edit(obj,id){
            layer.confirm('确认提现吗？',function(){
                $.ajax({
                    url:"/admin/withdrawal/edit",
                    data:{"id":id,},
                    type:"post",
                    dataType:"json",
                    success:function(result){
                     if(result.code==200){

                         $(obj).parents("tr").find(".td-status").find('span').addClass('layui-btn-disabled').html('提现成功');
                         $(obj).parents("tr").find(".td-manage").find('title').remove();
                         $(obj).parents("tr").find(".td-manage").find('i').remove();
                         layer.msg('提现成功!',{icon: 1,time:1000});
                    }else{
                        layer.msg(result.msg,{time:2000,anim:6});
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