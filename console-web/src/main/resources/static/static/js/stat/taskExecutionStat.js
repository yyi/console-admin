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
        //日期查询
        $('#statDate_query').val($.DateFormat(new Date(), 'yyyy-MM-dd'));
        laydate({
            elem: '#statDate_query',
            istime: false,
            format: 'YYYY-MM-DD'
        });
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
            field: 'taskId',
            title: 'taskId',
            visible: false
        }, {
            field: 'taskName',
            title: '填报名称'
        }, {
            field: 'startDate',
            title: '开始时间'
        }, {
            field: 'endDate',
            title: '截止时间',
        }, {
            field: 'statDate',
            title: '报表时间',
        }, {
            field: 'execOrganizationCount',
            title: '参与营业部数量'
        }, {
            field: 'completeOrganizationCount',
            title: '完成营业部数量'
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
                return $.extend(rdata, $('#queryForm').serializeJson());
            }
        });
    }

    function renderDetailDatagrid(taskid) {
        var detailcolumns = [{
            field: 'id',
            title: 'id',
            visible: false
        }, {
            field: 'organizationName',
            title: '营业部名称',
        }, {
            field: 'organizationNo',
            title: '营业部编号'
        }, {
            field: 'inputStaff',
            title: '填报人'
        }, {
            field: 'isDone',
            title: '是否完成'
        }, {
            field: 'inputDate',
            title: '填报时间'
        }];

        $detailTable.bootstrapTable('destroy').bootstrapTable({
            striped: true,
            url: obj.urlDetailQuery,
            dataType: 'json',
            queryParamsType: '',
            pagination: true,
            sidePagination: 'server',
            pageSize: 10,
            pageList: [10, 25, 50, 100, 'All'],
            selectItemName: 'checked',
            idField: 'id',
            columns: detailcolumns,
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
                rdata.taskId = taskid;
                return $.extend(rdata, $('#queryForm').serializeJson());
            }
        });
    }
    //页面事件监听
    function eventHandler() {
        //弹框前处理
        $('#detailModal').on('show.bs.modal', beforeModalShowHandler);
        //查询
        $('#queryBtn').click(function() {
            $mainTable.bootstrapTable('refresh');
        });
        //导出
        $('#export_btn').click(function() {
            var taskName = $('#taskName_query').val();
            var statDate = $('#statDate_query').val();
            $(this).attr('href', obj.contextUrl + 'taskExecutionStatExport?taskName=' + taskName+'&statDate='+statDate);
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable.bootstrapTable('resetView', {
                height: getTHeight()
            });
        });
    }
    //显示弹框前的事件处理
    function beforeModalShowHandler(e) {
        if (!isChooseOneRow($mainTable)) {
            $.showErrMsg('请选择一条记录！');
            return false;
        } else {
            // alert("taskId:"+getChooseRows($mainTable).taskId);
            renderDetailDatagrid(getChooseRows($mainTable).taskId);
            return true;
        }
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
}($, taskExecutionStatObj));
