/*
 *用户管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var $detailTable = $('#detaildatagrid');
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
            title: '序号',
        },{
            field: 'organizationNo',
            title: '营业部代码'
        }, {
            field: 'organizationName',
            title: '部门名称'
        }, {
            field: 'testProfile',
            title: '测试概况',
        }, {
            field: 'faultDescription',
            title: '主要故障描述',
        }, {
            field: 'faultSolveStatus',
            title: '故障解决情况'
        }, {
            field: 'rsrvStr1',
            title: '备用1'
        }, {
            field: 'rsrvStr2',
            title: '备用2'
        }, {
            field: 'rsrvStr3',
            title: '备用3'
        }, {
            field: 'completeDate',
            title: '完成时间'
        }, {
            field: 'completeStaff',
            title: '填报人'
        }];

        $mainTable.bootstrapTable('destroy').bootstrapTable({
            striped: true,
            url: obj.urlTaskExecutionStatInit,
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
        $('#queryBtn').click(function() {
            $mainTable.bootstrapTable('refresh');
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable.bootstrapTable('resetView', {
                height: getTHeight()
            });
        });

        //导出
        $('#export_btn').click(function() {
            var taskName = $('#taskName_query').val();
            $(this).attr('href',obj.contextUrl + 'testResponseLogStatExport?taskName='+taskName);
        });
    }


    //获取表格高度
    function getTHeight() {
        return $(window).height() - $('#u_header').outerHeight(true);
    }

    //获取table选中行的数据数组，如果只有一行数据则直接返回数据
    function getChooseRows(table) {
        var tableobj = (typeof table === 'string') ? $('#' + table) : table;
        var rows = tableobj.bootstrapTable('getSelections');
        return (rows.length == 1) ? rows[0] : rows;
    }

    //判断table表格是否选择了一条记录
    function isChooseOneRow(table) {
        var tableobj = (typeof table === 'string') ? $('#' + table) : table;
        return (tableobj.bootstrapTable('getSelections').length != 1) ? false : true;
    }
    init();
}($, testResponseLogStatObj ));
