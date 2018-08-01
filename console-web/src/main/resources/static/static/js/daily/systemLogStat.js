/*
 *运行日志完成情况统计页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj, data) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    //初始化
    function init() {
        //渲染下拉列表
        renderSelect();
        //渲染表格
        renderDatagrid();
        //事件监听
        eventHandler();
    }
    //渲染下拉列表
    function renderSelect() {
        $("#year_query").fzSelect({
            data: obj.yearMonthObj,
            valueField: 'year',
            textField: 'year'
        });
        //保存月份数据
        for (var i = 0; i < obj.yearMonthObj.length; i++) {
            var fobj = obj.yearMonthObj[i];
            $("#year_query").find('option[value=\'' + fobj.year + '\']').data('months', fobj.months);
        }
        $("#month_query").fzSelect({
            data: obj.yearMonthObj[0].months
        });
    }
    //渲染表格
    function renderDatagrid() {
        var columns = [{
            field: 'id',
            title: 'id',
            visible: false
        }, {
            field: 'organizationName',
            title: '营业部名称'
        }, {
            field: 'organizationNo',
            title: '营业部编号'
        }];
        for (var i = 0; i < 31; i++) {
            columns.push({
                field: 'day' + (i + 1),
                title: (i + 1) + '日',
                formatter: function(value, row, index) {
                    if (value == null) return '';
                    return '<p class="btn btn-circle btn-outline" type="button"><i class="fa fa-' + ((value == '未完成') ? 'close' : 'check') + '"></i></p>';
                }
            });
        }
        columns.push({
            field: 'total',
            title: '完成统计',
            formatter: function(value, row, index) {
                return [row.complete + '', row.total + ''].join('/');
            }
        });
        $mainTable.fzTable({
            url: obj.urlSystemLogStatIndex,
            columns: columns,
            indexFlg: true,
            undefinedText: '',
            height: getTHeight(),
            queryParams: function(params) {
                //请求参数处理
                return $('#queryForm').serializeJson();
            }
        });
        //隐藏未对其的表格头部
        $mainTable.on('post-header.bs.table', function(name, args) {
            $mainTable[0].hideHeader();
        });
    }
    //页面事件监听
    function eventHandler() {
        //查询
        $('#queryBtn').click(function() {
            $mainTable[0].refresh();
        });
        //年份改变
        $('#year_query').change(function(e) {
            var target = $(e.currentTarget);
            $("#month_query").fzSelect({
                data: target.find('option[value=\'' + target.val() + '\']').data('months')
            });
        });

        //导出表格
        $('#export_btn').click(function() {
            var year = $('#year_query').val();
            var month = $('#month_query').val();
            $(this).attr('href', obj.urlContext + '/export?year=' + year + '&month=' + month);
        });

        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable[0].resetView(getTHeight());
        });
    }
    //获取表格高度
    function getTHeight() {
        return $(window).height() - ($('#u_header').outerHeight(true) + $('#u_toolbar').outerHeight(true));
    }
    init();
}($, systemLogStatObj));
