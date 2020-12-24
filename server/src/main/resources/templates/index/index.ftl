<!doctype html>
<html lang="en">
<#include "../common/header.ftl">
<body>
<!-- 顶部开始 -->
<div class="container">
    <div class="logo"><a href="/">首页</a></div>
    <div class="left_open">
        <i title="展开左侧栏" class="iconfont">&#xe699;</i>
    </div>
    <ul class="layui-nav left fast-add" lay-filter="">
        <#--<li class="layui-nav-item">-->
            <#--<a href="javascript:;">+新增</a>-->
            <#--<dl class="layui-nav-child"> <!-- 二级菜单 &ndash;&gt;-->
                <#--<dd><a onclick="x_admin_show('资讯','http://www.baidu.com')"><i class="iconfont">&#xe6a2;</i>资讯</a></dd>-->
                <#--<dd><a onclick="x_admin_show('图片','http://www.baidu.com')"><i class="iconfont">&#xe6a8;</i>图片</a></dd>-->
                <#--<dd><a onclick="x_admin_show('用户','http://www.baidu.com')"><i class="iconfont">&#xe6b8;</i>用户</a></dd>-->
            <#--</dl>-->
        <#--</li>-->
    </ul>
    <ul class="layui-nav right" lay-filter="">
        <li class="layui-nav-item">
            <a href="javascript:;">个人中心</a>
            <dl class="layui-nav-child"> <!-- 二级菜单 -->
                <#--<dd><a onclick="x_admin_show('个人信息','http://www.baidu.com')">个人信息</a></dd>-->
                <dd><a href="/admin/logout">退出</a></dd>
            </dl>
        </li>
        <li class="layui-nav-item to-index"><a href="/index">前台首页</a></li>
    </ul>
</div>
<!-- 顶部结束 -->
<!-- 中部开始 -->
<!-- 左侧菜单开始 -->
<div class="left-nav">
    <div id="side-nav">
        <ul id="nav">
            <li>
                <a href="javascript:;">
                    <i class="iconfont"></i>
                    <cite>商家管理</cite>
                    <i class="iconfont nav_right">&#xe697;</i>
                </a>
                <ul class="sub-menu">
                    <li>
                        <a _href="/admin/admin/list">
                            <i class="iconfont"></i>
                            <cite>商家列表</cite>
                        </a>
                    </li>
                </ul>
            </li>
        </ul>
        <ul id="nav">
            <li>
                <a href="javascript:;">
                    <i class="iconfont"></i>
                    <cite>作品管理</cite>
                    <i class="iconfont nav_right">&#xe697;</i>
                </a>
                <ul class="sub-menu">
                    <li>
                        <a _href="/admin/work/list">
                            <i class="iconfont"></i>
                            <cite>作品列表</cite>
                        </a>
                    </li>
                    <li>
                        <a _href="/admin/workComment/list">
                            <i class="iconfont"></i>
                            <cite>评论列表</cite>
                        </a>
                    </li>
                </ul>
            </li>
        </ul>
        <ul id="nav">
            <li>
                <a href="javascript:;">
                    <i class="iconfont"></i>
                    <cite>用户管理</cite>
                    <i class="iconfont nav_right">&#xe697;</i>
                </a>
                <ul class="sub-menu">
                    <li>
                        <a _href="/admin/user/list">
                            <i class="iconfont"></i>
                            <cite>用户列表</cite>
                        </a>
                    </li>
                </ul>
            </li>
        </ul>
        <#if superAdmin == true>
        <ul id="nav">
            <li>
                <a href="javascript:;">
                    <i class="iconfont"></i>
                    <cite>运营管理</cite>
                    <i class="iconfont nav_right">&#xe697;</i>
                </a>
                <ul class="sub-menu">
                    <li>
                        <a _href="/admin/report/list">
                            <i class="iconfont"></i>
                            <cite>举报列表</cite>
                        </a>
                    </li>
                </ul>
            </li>
        </ul>
        </#if>
                <#if superAdmin == true>
                <ul id="nav">
                    <li>
                        <a href="javascript:;">
                            <i class="iconfont"></i>
                            <cite>数据统计</cite>
                            <i class="iconfont nav_right">&#xe697;</i>
                        </a>
                        <ul class="sub-menu">
                            <li>
                                <a _href="/admin/data/report">
                                    <i class="iconfont"></i>
                                    <cite>数据统计</cite>
                                </a>
                            </li>
                        </ul>
                    </li>
                </ul>
                </#if>
    </div>
</div>
<!-- <div class="x-slide_left"></div> -->
<!-- 左侧菜单结束 -->
<!-- 右侧主体开始 -->
<div class="page-content">
    <div class="layui-tab tab" lay-filter="xbs_tab" lay-allowclose="false">
        <ul class="layui-tab-title">
            <li class="home"><i class="layui-icon">&#xe68e;</i>首页</li>
        </ul>
        <div class="layui-tab-content">
            <div class="layui-tab-item layui-show">
                <iframe src='/index/welcome' frameborder="0" scrolling="yes" class="x-iframe"></iframe>
            </div>
        </div>
    </div>
</div>
<div class="page-content-bg"></div>
<!-- 右侧主体结束 -->
<!-- 中部结束 -->
<!-- 底部开始 -->
<div class="footer">
    <div class="copyright">copyright</div>
</div>
<!-- 底部结束 -->

<audio preload="auto" src="/static/mp3/new_order_remind.mp3" id="new_order_remind"></audio>
</body>
</html>