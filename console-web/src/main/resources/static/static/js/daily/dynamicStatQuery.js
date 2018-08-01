/*
 *用户管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
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
            field: 'id',
            title: 'id',
            visible: false
        }, {
            field: 'taskName',
            title: '任务名称'
        }, {
            field: 'elementName',
            title: '动态统计项目'
        }, {
            field: 'elementValue',
            title: '动态统计结果',
        },{
            field: 'updateStaff',
            title: '填报人',
        } , {
            field: 'updateTime',
            title: '填报时间'
        }];
        $mainTable.bootstrapTable('destroy').bootstrapTable({
            striped: true,
            url: obj.urlDynamicStatQuery,
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
            responseHandler: function(res) {
                //数据处理
                var rdata = {};
                rdata.total = res.totalElements;
                rdata.rows = res.content;
                return rdata;
            },
            queryParams: function(params) {
                //请求参数处理
                var rdata = {};
                rdata.page = params.pageNumber - 1;
                rdata.size = params.pageSize;
                return $.extend(rdata,$('#queryForm').serializeJson());
            }
        });
    }
    //页面事件监听
    function eventHandler() {
        //查询
        $('#queryBtn').click(function() {
            $mainTable.bootstrapTable('refresh');
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable.bootstrapTable('resetView', {
                height: getTHeight()
            });
        });
    }
    //获取表格高度
    function getTHeight() {
        return $(window).height() - $('#u_header').outerHeight(true);
    }
    init();
}($, dynamicStatQueryObj ));
