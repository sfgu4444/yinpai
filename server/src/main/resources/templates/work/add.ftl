<!DOCTYPE html>
<html>
<#include "../common/header.ftl">
<#setting number_format="#">
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
            <label for="title" class="layui-form-label">
                <span class="x-red">*</span>标题
            </label>
            <div class="layui-input-inline">
                <input type="text" id="title" name="title" required="" lay-verify="required" autocomplete="off" class="layui-input" placeholder="请输入title">
            </div>
            <div class="layui-form-mid layui-word-aux">
                用户名为4～255个字符
            </div>
        </div>

        <div class="layui-form-item">
            <label for="content" class="layui-form-label">
                <span class="x-red">*</span>内容
            </label>
            <div class="layui-input-inline">
                <textarea id="content" class="layui-textarea" name="content"></textarea>
            </div>
            <div class="layui-form-mid layui-word-aux">
                用户名为4～255个字符
            </div>
        </div>

        <div class="layui-form-item type-2">
            <label class="layui-form-label">
                <span class="x-red">*</span>封面
            </label>
            <div class="add_pic">
                <div id="cover">
                    <input type="hidden" name="coverImageUrl" value="">
                    <img src="/static/images/camera.png" width="200" id="coverImageUrl">
                    <span class="layui-word-aux">
                  上传的图片尺寸大小为380px * 260px
                </span>
                </div>
            </div>
        </div>

        <div class="layui-form-item">
            <label for="" class="layui-form-label">
                <span class="x-red">*</span>是否免费
            </label>
            <div class="layui-input-inline">
                <input type="radio" name="isFree" value="1" title="是">
                <input type="radio" name="isFree" value="0" title="否">
            </div>
        </div>

        <div class="layui-form-item">
            <label for="price" class="layui-form-label">
                价格
            </label>
            <div class="layui-input-inline">
                <input type="text" id="price" name="price" autocomplete="off" class="layui-input" placeholder="请输入价格">
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">
                <span class="x-red">*</span>资源类型
            </label>
            <div class="layui-input-block">
                <button class="layui-btn type-btn dan" type="button">图片</button>
                <button class="layui-btn layui-btn-primary type-btn san" type="button">视频</button>
            </div>
            <input type="hidden" name="type" value="1">
        </div>

        <div class="layui-form-item type-2">
            <label class="layui-form-label">
                资源图片上传
            </label>
            <div class="layui-input-inline">

            </div>
            <div class="layui-form-mid layui-word-aux">
                <button type="button" class="layui-btn" id="imagesResources">
                    <i class="layui-icon">&#xe67c;</i>上传图片
                </button>
            </div>
            <div class="layui-row">
                <div class='layui-input-block layui-col-sm12 uploader-list' id='uploaderDetail' title=''>
                    <div id="goodsDetail">

                    </div>
                </div>
                <input type="hidden" id="goodsImagesDetail" name="goodsImagesDetail" value="">
            </div>
        </div>

        <div class="layui-form-item type-1" style="display: none;">
            <label class="layui-form-label">
                资源视频上传
            </label>
            <div class="layui-input-inline">

            </div>
            <div class="layui-form-mid layui-word-aux">
                <button type="button" class="layui-btn" id="videoResources">
                    <i class="layui-icon">&#xe67c;</i>上传视频
                </button>
            </div>
            <div class="layui-row">
                <div class='layui-input-block layui-col-sm12 uploader-list' title=''>
                    <div>
                        <video id="videoDetailPlay" src="" controls autoplay></video>
                    </div>
                </div>
                <input type="hidden" id="videoDetail" name="videoDetail" value="">
            </div>
        </div>

        <input type="hidden" name="folder" value="${folder}">

        <div class="layui-form-item">
            <label for="L_repass" class="layui-form-label">
            </label>
            <button  class="layui-btn" lay-filter="add" lay-submit="">
                添加
            </button>
        </div>
    </form>
</div>
<script>

    (function(){
        var type = "1";
        if (type == "1") {
            $('.type-2').show();
            $('.type-1').hide();
            $('#dan').show();
            $('#san').hide();
            $('.san').addClass('layui-btn-primary');
            $('.dan').removeClass('layui-btn-primary');
        } else {
            $('.type-1').show();
            $('.type-2').hide();
            $('#dan').hide();
            $('#san').show();
            $('.dan').addClass('layui-btn-primary');
            $('.san').removeClass('layui-btn-primary');
        }
    }());

    $('.type-btn').click(function(event) {
        if($(this).html() == "图片"){
            $('.type-2').show();
            $('.type-1').hide();
            $('#dan').show();
            $('#san').hide();
            $('input[name=type]').val(1);
        }else{
            $('.type-1').show();
            $('.type-2').hide();
            $('#dan').hide();
            $('#san').show();
            $('input[name=type]').val(2);
        }
        $('.type-btn').addClass('layui-btn-primary');
        $(this).removeClass('layui-btn-primary');
    });

    var goodsDetailNum;

    var imageResourceList = [];

    layui.use(['form', 'layer', "laydate", "upload"], function(){
        $ = layui.jquery;

        var form = layui.form
            ,layer = layui.layer
            ,laydate = layui.laydate
            ,upload = layui.upload;


        var folder = $('input[name=folder]').val();

        laydate.render({
            elem: "#dueTime",
            type: "datetime"
        });

        upload.render({
            elem: '#videoResources' //绑定元素
            ,url: "/oss/works/upload?folder=" + folder //上传接口
            ,accept: 'video'
            ,field: 'file'
            ,done: function(res){
                layer.closeAll('loading'); //关闭loading
                if(res.code == 200){
                    $('#cover img').attr({
                        'id': 'videoDetail',
                    });
                    $("input[name=videoDetail]").val(res.data);
                }else{
                    layer.msg(res.msg,{anim:6,time:2000});
                }
            }
            ,before: function(obj){
                layer.closeAll('loading'); //关闭loading
                layer.load(); //上传loading

                //预读本地文件示例，不支持ie8
                obj.preview(function(index, file, result){
                    $('#videoDetailPlay').attr('src', result); //图片链接（base64）
                });
            }
            ,error: function (){
                layer.closeAll('loading'); //关闭loading
            }
        });

        upload.render({
            elem: '#coverImageUrl' //绑定元素
            ,url: "/oss/works/upload?folder=" + folder //上传接口
            ,field: 'file'
            ,done: function(res){
                layer.closeAll('loading'); //关闭loading
                if(res.code == 200){
                    $('#cover img').attr({
                        'id': 'coverImageUrl',
                    });
                    $("input[name=coverImageUrl]").val(res.data.data);
                }else{
                    layer.msg(res.msg,{anim:6,time:2000});
                }
            }
            ,before: function(obj){
                layer.closeAll('loading'); //关闭loading
                layer.load(); //上传loading

                //预读本地文件示例，不支持ie8
                obj.preview(function(index, file, result){
                    $('#coverImageUrl').attr('src', result); //图片链接（base64）
                });
            }
            ,error: function (){
                layer.closeAll('loading'); //关闭loading
            }
        });

        upload.render({
            elem: '#imagesResources', //绑定元素
            accept: 'images', //允许上传的文件类型
            multiple: true, //允许多文件上传
            auto: true, //选完文件后不要自动上传
            url: "/oss/works/upload?folder=" + folder, //上传接口
            field: 'file',
            before: function () {
                layer.closeAll('loading');
                layer.load();
            },
            done: function (res) {
                $('#uploaderDetail').append(
                    '<div id="" class="file-iteme">' +
                    '<div class="handle"><i class="layui-icon">&#x1006;</i></div>' +
                    '<img style="width: 100px;height: 100px;" src="' + res.data.data + '">' +
                    '</div>'
                );
                goodsDetailNum += 1;
                if (goodsDetailNum > 10) {
                    $('#imagesResources').hide();
                }
                imageResourceList.push(res.data.data);
                $('#goodsImagesDetail').val(JSON.stringify(imageResourceList));
                layer.closeAll('loading');
                layer.msg('上传成功！', {icon: 1, time: 1000});
            },
            error: function () {
                layer.closeAll('loading');
                layer.msg('上传失败！', {icon: 2, time: 1000});
            }
        });

        // 显示删除按钮
        $(document).on("mouseenter mouseleave", ".file-iteme", function (event) {
            if (event.type === "mouseenter") {
                //鼠标悬浮
                $(this).children(".info").fadeIn("fast");
                $(this).children(".handle").fadeIn("fast");
            } else if (event.type === "mouseleave") {
                //鼠标离开
                $(this).children(".info").hide();
                $(this).children(".handle").hide();
            }
        });

        // 执行删除操作
        $(document).on("click", ".file-iteme .handle", function (event) {
            var deleteURL = $(this).parent().children('img').attr('src');
            var that = $(this);
            $.ajax({
                url: '/oss/delete',
                type: 'POST',
                dataType: 'json',
                data: {
                    url: deleteURL
                },
                success: function (res) {
                    if (res.code == 200) {
                        that.parent().remove();
                        for (var i = 0; i < imgList.length; i++) {
                            if (imgList[i] == deleteURL) {
                                imgList.splice(i, 1)
                            }
                        }
                        if (goodsData.length > 0) {
                            for (var i = 0; i < goodsData.length; i++) {
                                if (goodsData[i] === deleteURL) {
                                    goodsData.splice(i, 1)
                                }
                            }
                        }
                        if (goodsDetailNum > 0) {
                            goodsDetailNum -= 1;
                            if (goodsDetailNum < 4) {
                                $('#test3').show();
                            }
                        }
                        $('#goodsImages').val(JSON.stringify(imgList));
                        $('#goodsImagesDetail').val(JSON.stringify(goodsData));
                        layer.msg(res.msg, {icon: 1, time: 1000})
                    } else if (res.code === 402) {
                        window.top.location.href = "/admin/admin/login";
                    } else {
                        layer.msg(res.msg, {icon: 2, time: 1000})
                    }
                },
                error: function () {
                    layer.msg('数据异常！', {icon: 2, time: 1000})
                }
            });
        });


        //监听提交
        form.on('submit(add)', function(data){
            $.ajax({
                url:"/admin/work/add",
                data: JSON.stringify(data.field),
                type:'post',
                dataType:'json',
                contentType: 'application/json',
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