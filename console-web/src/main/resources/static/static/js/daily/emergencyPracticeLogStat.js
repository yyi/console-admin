$(function ($, obj, data) {
    //入口初始化方法
    var $mainTable = $('#emergencyPracticeLogStatGrid');
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
        //事件监听
        eventHandler();
    }

    //渲染表格
    function renderDatagrid() {
        var columns = [{
            field: 'checked',
            title: 'checked',
            checkbox: true
        }, {
            field: 'organizationName',
            title: '营业部名称'
        }, {
            field: 'organizationNo',
            title: '营业部编号'
        }, {
            field: 'quarterCount',
            title: '季-数量'
        }, {
            field: 'monthCount',
            title: '月-数量'
        }, {
            field: 'weekCount',
            title: '周-数量'
        }, {
            field: 'quarterTime',
            title: '季-演练项目'
        }, {
            field: 'monthTime',
            title: '月-演练项目'
        }, {
            field: 'weekTime',
            title: '周-演练项目'
        }];
        $mainTable.bootstrapTable('destroy').bootstrapTable({
            striped: true,
            url: obj.urlElementQuery,
            dataType: 'json',
            queryParamsType: '',
            pagination: true,
            sidePagination: 'server',
            pageSize: 10,
            pageList: [10, 25, 50, 100, 'All'],
            selectItemName: 'checked',
            idField: 'id',
            columns: columns,
            height: getTHeight(),
            responseHandler: function (res) {
                //数据处理
                var rdata = {};
                rdata.total = res.totalElements;
                rdata.rows = res.content;
                return rdata;
            },
            queryParams: function (params) {
                //请求参数处理
                var rdata = {};
                rdata.page = params.pageNumber - 1;
                rdata.size = params.pageSize;
                return $.extend(rdata, $('#queryForm').serializeJson());
            }
        });
    }

    //页面事件监听
    function eventHandler() {
        //查询
        $('#queryBtn').click(function () {
            $mainTable.bootstrapTable('refresh');
        });
        $('#export_btn').click(function() {
            var quarter = $('#quarter_query').val();
            var inputDate = $('#inputDate').val();
            $(this).attr('href',contextUrl + '/emergencyPracticeLogStat/export?quarter='+quarter+'&inputDate='+inputDate);
        });
        //页面大小变化重绘表格
        $(window).resize(function () {
            $mainTable.bootstrapTable('resetView', {
                height: getTHeight()
            });
        });
    }

    //获取表格高度
    function getTHeight() {
        return $(window).height() - ($('#u_header').outerHeight(true) + $('#u_toolbar').outerHeight(true));
    }

    init();
}($, elementObj, dictionary));
