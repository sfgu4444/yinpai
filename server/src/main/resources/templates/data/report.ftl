<!DOCTYPE html>
<html lang="en">
<#include "../common/header.ftl">
<body>
<div class="x-nav">
  <span>
    <span style="color: #666;vertical-align: middle;margin-right: 20px;">起止时间</span>
    <input type="button"   value="今日" name="today" style="height: 22px;line-height: 22px;padding: 0 10px;margin-right: 10px;border: none;display: inline-block;background-color: #009688;color: #fff;white-space: nowrap;text-align: center;font-size: 14px;border: none;border-radius: 2px;cursor: pointer;"/>
    <input type="button"  value="近一周" name="week" style="height: 22px;line-height: 22px;padding: 0 10px;margin-right: 10px;border: none;display: inline-block;background-color: #009688;color: #fff;white-space: nowrap;text-align: center;font-size: 14px;border: none;border-radius: 2px;cursor: pointer;"/>
    <input type="button"  value="近一月" name="month" style="height: 22px;line-height: 22px;padding: 0 10px;margin-right: 10px;border: none;display: inline-block;background-color: #009688;color: #fff;white-space: nowrap;text-align: center;font-size: 14px;border: none;border-radius: 2px;cursor: pointer;"/>
    <span style="color: #666;vertical-align: middle;margin-left: 70px;">作品类型</span>
  </span>
</div>
<div style="padding: 20px;display: inline-block;">
<form class="layui-form >
    <div class="layui-form" style="display: inline-block;">
      <div class="layui-form-item">
        <div class="layui-inline" style="margin-right: 0;margin-bottom: 0;">
          <input type="text" name ="startTime" value="${startTime}"  class="layui-input test-item" id="startTime" placeholder="2020-01-01 00:00:00">
        </div>
        <span> — </span>
        <div class="layui-inline" style="margin-right: 0;margin-bottom: 0;">
          <input type="text" name ="endTime" value="${endTime}" class="layui-input test-item" id="endTime" placeholder="2021-12-31 00:00:00">
        </div>
      </div>
    </div>

        <div class="layui-form-item" style="display: inline-flex;">
            <div class="layui-input-inline" style="margin-left: 10px;">
              <select name="type" style="height: 38px;">
                <option value="">请选择</option>
                <option value="1">图片</option>
                <option value="2">视频</option>
              </select>
            </div>
            <div class="layui-form-item" style="display: inline-block;margin-bottom: 0;margin-left: 20px;vertical-align: middle;line-height: 38px;">
                <button class="layui-btn"  lay-submit="" lay-filter="sreach"><i class="layui-icon">&#xe615;</i></button>
                <a href="javascript:void(0)" style="color: #666;">重置</a><i class="layui-icon layui-icon-refresh" style="font-size: 14px;padding-left: 5px;color: #666;"></i>
            </div>
        </div>

    </form>

</div>

               <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
              <div class="layui-row" style="padding: 20px;">
                <div id="reportVoListLogin" class="layui-col-md4" style="height:400px;" ></div>
                <div id="reportVoListOrder" class="layui-col-md4" style="height:400px;" ></div>
                <div id="reportVoListOrderCount" class="layui-col-md4" style="height:400px;"></div>
              </div>
              <div class="layui-row" style="padding: 20px;">
                <div id="reportVoListLook" class="layui-col-md4" style="height:400px;"></div>
                <div id="reportVoListWorks" class="layui-col-md4" style="height:400px;"></div>
                <div id="reportVoListUser" class="layui-col-md4" style="height:400px;"></div>
              </div>
              <div style="width: 100%;padding: 32px 0px 27px 40px;font-size: 18px;">
                  <span style="color: #333;vertical-align: middle;margin-right: 20px;">登陆总数:</span>
                              <span style="color: #009688;vertical-align: middle;margin-right: 20px;">${reportVoLogin}</span>

                  <span style="color: #333;vertical-align: middle;margin-left: 20px;">交易总额:</span>
                              <span style="color: #4B5CDC;vertical-align: middle;margin-left: 20px;">${reportVoOrder}</span>

                  <span style="color: #333;vertical-align: middle;margin-left: 20px;">交易总次数:</span>
                              <span style="color: #ED49D0;vertical-align: middle;margin-left: 20px;">${reportVoOrderCount}</span>

                  <span style="color: #333;vertical-align: middle;margin-left: 20px;">作品点击总量:</span>
                              <span style="color: #35C9F3;vertical-align: middle;margin-left: 20px;">${reportVoLook}</span>

                  <span style="color: #333;vertical-align: middle;margin-left: 20px;">发布总量:</span>
                              <span style="color: #EBD51C;vertical-align: middle;margin-left: 20px;">${reportVoWorks}</span>

                  <span style="color: #333;vertical-align: middle;margin-left: 20px;">注册总量:</span>
                              <span style="color: #F6411E;vertical-align: middle;margin-left: 20px;">${reportVoUser}</span>


                </div>
<script>
layui.use('laydate', function(){
  var laydate = layui.laydate;
  //同时绑定多个
    lay('.test-item').each(function(){
      laydate.render({
        elem: this,
        trigger: 'click',
        type: 'datetime',

      });
    });
});

</script>
<script src="https://s3.pstatp.com/cdn/expire-1-M/jquery/3.3.1/jquery.min.js"></script>
    <!-- 你必须先引入jQuery1.8或以上版本 static/lib/layui/layui.js -->
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
     <!-- 引入 ECharts 文件 -->
<script src="https://cdn.staticfile.org/echarts/4.7.0/echarts.min.js"></script>
<script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('reportVoListUser'));
        // 指定图表的配置项和数据
        var option = {
            title: {
                text: '注册量（个）'
            },
            tooltip: {},
            legend: {
               // data:['注册量（个）']
            },
            xAxis: {
                data: [<#list reportVoListUser  as time>
                                      "${time.time}",
                      </#list>]
            },
            yAxis: {},
            series: [{
                name: '注册量（个）',
                type: 'line',
                data: [<#list reportVoListUser as total>
                "${total.total}",
                </#list>]
            }]
        };

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    </script>
<script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('reportVoListWorks'));
        // 指定图表的配置项和数据
        var option = {
            title: {
                text: '发布量（个）'
            },
            tooltip: {},
            legend: {
                data:['发布量（个）']
            },
            xAxis: {
                data: [<#list reportVoListWorks  as time>
                                      "${time.time}",
                      </#list>]
            },
            yAxis: {},
            series: [{
                name: '发布量（个）',
                type: 'line',
                data: [<#list reportVoListWorks as total>
                "${total.total}",
                </#list>]
            }]
        };

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    </script>
<script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('reportVoListLook'));
        // 指定图表的配置项和数据
        var option = {
            title: {
                text: '作品点击量（次）'
            },
            tooltip: {},
            legend: {
                data:['作品点击量（次）']
            },
            xAxis: {
                data: [<#list reportVoListLook  as time>
                                      "${time.time}",
                      </#list>]
            },
            yAxis: {},
            series: [{
                name: '作品点击量（次）',
                type: 'line',
                data: [<#list reportVoListLook as total>
                "${total.total}",
                </#list>]
            }]
        };

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    </script>
<script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('reportVoListOrderCount'));
        // 指定图表的配置项和数据
        var option = {
            title: {
                text: '交易次数（次）'
            },
            tooltip: {},
            legend: {
                data:['交易次数（次）']
            },
            xAxis: {
                data: [<#list reportVoListOrderCount  as time>
                                      "${time.time}",
                      </#list>]
            },
            yAxis: {},
            series: [{
                name: '交易次数（次）',
                type: 'line',
                data: [<#list reportVoListOrderCount as total>
                "${total.total}",
                </#list>]
            }]
        };

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    </script>
<script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('reportVoListOrder'));
        // 指定图表的配置项和数据
        var option = {
            title: {
                text: '交易额（元）'
            },
            tooltip: {},
            legend: {
                data:['交易额（元）']
            },
            xAxis: {
                data: [<#list reportVoListOrder  as time>
                                      "${time.time}",
                      </#list>]
            },
            yAxis: {},
            series: [{
                name: '交易额（元）',
                type: 'line',
                data: [<#list reportVoListOrder as total>
                "${total.total}",
                </#list>]
            }]
        };

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    </script>
    <script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('reportVoListLogin'));
        // 指定图表的配置项和数据
        var option = {
            title: {
                text: '登陆统计（次）'
            },
            tooltip: {},
            legend: {
                data:['登陆统计']
            },
            xAxis: {
                data: [<#list reportVoListLogin  as time>
                                      "${time.time}",
                      </#list>]
            },
            yAxis: {},
            series: [{
                name: '登陆次数',
                type: 'line',
                data: [<#list reportVoListLogin as total>
                "${total.total}",
                </#list>]
            }]
        };
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    </script>

</body>
</html>