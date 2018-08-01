/*
 *在线填报页面的js
 *@Author wx 2017-9-5
 */
$(function($, obj) {
    //入口初始化方法
    var $mainTable = $('#maintable');
    var $detailTable = $('#detailTable');
    var detailFlg = false;
    var exportUrl;
    var titleName;
    //初始化
    function init() {
        //渲染表格
        renderMainTable();
        //事件监听
        eventHandler();
    }
    //渲染表格
    function renderMainTable() {
        var columns = [{
            field: 'checked',
            title: '选择',
            checkbox: true
        }, {
            field: 'taskId',
            title: 'id',
            visible: false
        }, {
            field: 'taskName',
            title: '任务名称'

        }, {
            field: 'type',
            title: '任务类型',
            formatter: function(value, row) {
                return value == 'TEMP' ? '临时任务' : '长期任务'
            }
        }, {
            field: 'startDate',
            title: '开始时间'
        }, {
            field: 'endDate',
            title: '截止时间'
        }, {
            field: 'organizationCount',
            title: '参与部门数量'
        }, {
            field: 'organiztionCompleteCount',
            title: '完成部门数量'
        }];
        var objListUrl;
        switch (obj.busiType) {
            case 'DAILY_RUNTIME_LOG':
                {
                    objListUrl = obj.urlSLList;
                    $('#export_btn').hide();
                    break;
                }
            case 'DYNAMIC_STATISTICS':
                {
                    objListUrl = obj.urlDSList;
                    exportUrl = obj.urlContext + 'rest/dynamic/export';
                    break;
                }
            case 'TEST_RESPONSE':
                {
                    objListUrl = obj.urlTRList;
                    exportUrl = obj.urlContext + 'rest/testResponseLogMaintain/export';
                    break;
                }
            case 'EMERGENCY_PRACTICE':
                {
                    objListUrl = obj.urlEPList;
                    exportUrl = obj.urlContext + 'rest/emergencyPracticeLog/export';
                    break;
                }
            case 'EQUIPMENT_MANAGEMENT':
                {
                    objListUrl = obj.urlEMList;
                    exportUrl = obj.urlContext + 'rest/equipmentMaintain/export';
                    break;
                }
        }
        $mainTable.fzTable({
            url: objListUrl,
            columns: columns,
            height: getTHeight(),
            undefinedText: '',
            pageFlg: true,
            queryParams: function(params) {
                //请求参数处理
                var rdata = {};
                rdata.page = params.pageNumber - 1;
                rdata.size = params.pageSize;
                rdata.taskName = $('#taskName_qry').val();
                return rdata;
            }
        });
    }
    //页面事件监听
    function eventHandler() {
        //工具按钮弹框前事件
        $('div[role="dialog"][id$="Modal"]').on('show.bs.modal', beforeModalShowHandler);
        //主查询
        $('#queryBtn').click(function(e) {
            $mainTable[0].refresh();
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable[0].resetView(getTHeight());
        });
        //导出表格
        $('#export_btn').click(function() {
            if (!$mainTable[0].isChooseOneRow()) {
                $.showErrMsg('请选择一条记录！');
                return false;
            }
            var taskId = $mainTable[0].getChooseRows().taskId;
            $(this).attr('href', exportUrl + '?taskId=' + taskId);
        });
    }
    //显示弹框前的事件处理
    function beforeModalShowHandler(e) {
        if (!$mainTable[0].isChooseOneRow()) {
            $.showErrMsg('请选择一条记录！');
            return false;
        } else {
            showDetailTable($mainTable[0].getChooseRows());
            return true;
        }
    }
    //显示详情表格
    function showDetailTable(row) {
        var title = row.taskName;
        if (detailFlg) {
            //如果详情表格已经初始化过了则刷新数据
            $detailTable[0].refresh({
                query: { taskId: row.taskId }
            });
            return;
        }
        var columns = [{
            field: 'checked',
            title: '选择',
            checkbox: true
        }, {
            field: 'id',
            title: 'id',
            visible: false
        }, {
            field: 'organizationName',
            title: '营业部名称',
            width: 280
        }, {
            field: 'organizationNo',
            title: '营业部编号'
        }, {
            field: 'completeStaff',
            title: '填报人'
        }, {
            field: 'status',
            title: '是否完成',
            formatter: function(value, row) {
                return value == 'EXECUTED' ? '已完成' : '未完成'
            }
        }, {
            field: 'completeDate',
            title: '填报时间',
            width: 140
        }, {
            field: 'execDate',
            title: '执行时间',
            width: 140
        }];
        $detailTable.fzTable({
            url: obj.urlDetailList,
            columns: columns,
            undefinedText: '',
            queryParams: function(params) {
                return {
                    taskId: row.taskId
                };
            }
        });
        $detailTable.on('dbl-click-row.bs.table', function(e, row) {
            openTab(row, title);
        });
        $('#detailModalBtn').click(function() {
            if (!$detailTable[0].isChooseOneRow()) {
                $.showErrMsg('请选择一条记录！');
            } else {
                openTab($detailTable[0].getChooseRows(), title);
            }
        });
        detailFlg = true;
    }
    //打开新页签
    function openTab(row, title) {
        if (row.status != 'EXECUTED') {
            $.showErrMsg('任务未完成');
            return;
        }
        var objGotoUrl;
        switch (obj.busiType) {
            case 'DAILY_RUNTIME_LOG':
                {
                    objGotoUrl = obj.urlSLGoto;
                    break;
                }
            case 'DYNAMIC_STATISTICS':
                {
                    objGotoUrl = obj.urlDSGoto;
                    break;
                }
            case 'TEST_RESPONSE':
                {
                    objGotoUrl = obj.urlTRGoto;
                    break;
                }
            case 'EMERGENCY_PRACTICE':
                {
                    objGotoUrl = obj.urlEPGoto;
                    break;
                }
            case 'EQUIPMENT_MANAGEMENT':
                {
                    objGotoUrl = obj.urlEMGoto;
                    break;
                }
        }
        $.openTab(window.top, objGotoUrl + '?operation=update&taskExecutionId=' + row.id, title);
    }
    //获取表格高度
    function getTHeight() {
        return $(window).height() - ($('#u_header').outerHeight(true) + $('#u_toolbar').outerHeight(true));
    }
    init();
}($, onlineFillObj));
