<!DOCTYPE html>
<html>
<#include "../common/header.ftl">

<style>
    .uploader-list {
        margin-left: -15px;
    }

    .uploader-list .info {
        position: relative;
        margin-top: -25px;
        background-color: black;
        color: white;
        filter: alpha(Opacity=80);
        -moz-opacity: 0.5;
        opacity: 0.5;
        width: 100px;
        height: 25px;
        text-align: center;
        display: none;
    }

    .uploader-list .handle {
        position: relative;
        background-color: black;
        color: white;
        filter: alpha(Opacity=80);
        -moz-opacity: 0.5;
        opacity: 1;
        width: 100px;
        text-align: right;
        height: 18px;
        margin-bottom: -18px;
        display: none;
    }

    .uploader-list .handle span {
        margin-right: 5px;
    }

    .uploader-list .handle span:hover {
        cursor: pointer;
    }

    .uploader-list .file-iteme {
        margin: 12px 0 0 15px;
        padding: 1px;
        float: left;
    }
</style>
<body>
<div class="x-body">
    <form class="layui-form">


        <div class="layui-form-item">
            <label for="content" class="layui-form-label">
                <span class="x-red">*</span>消息内容
            </label>
            <div class="layui-input-inline">
                <textarea id="content" class="layui-textarea" name="content">${data.message!''}</textarea>
            </div>
        </div>
    </form>
</div>
<script>

</script>
</body>
</html>