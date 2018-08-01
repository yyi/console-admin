/*
 *系统运行日志维护页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj, data) {
    var vm = avalon.define({
        $id: 'systemLogMaintain',
        staffName: "",
        sl: {},
        downloadUrl:encodeURI(obj.urlContext + "/commons/download?filePath=/template/Excel11.xls"),
        baseinfo: {},
        accept: {},
        server: {},
        internet: {},
        software: {},
        phone: {},
        other: {}
    });
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
        //事件监听
        eventHandler();
    }
    //整合Avalon
    function baseinfoAvalonInit(tastExecId) {
        //首页传来
        var taskExecId = { taskExecutionId: tastExecId };
        $.Ajax(obj.urlBaseinfoInit, taskExecId, function(data) {
            vm.sl = data;
            avalon.scan(document.body);
        });
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
            field: 'busiType',
            title: '任务类型',
            formatter: function(value, row, index) {
                return data['LARGE_ELEMENT_TYPE'][row['busiType']];
            }
        }, {
            field: 'startDate',
            title: '开始时间',
        }, {
            field: 'endDate',
            title: '结束时间',
        }, {
            field: 'execDate',
            title: '填报时间'
        }, {
            field: 'organizationName',
            title: '填报部门'
        }];
        $mainTable.bootstrapTable('destroy').bootstrapTable({
            striped: true,
            url: obj.urlSystemLogMaintainIndex,
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
    //页面事件监听
    function eventHandler() {
        //$('#editModal').on('show.bs.modal', beforeModalShowHandler);
        //查询
        $('#queryBtn').click(function() {
            $mainTable.bootstrapTable('refresh');
        });
        $('#edit_btn').click(function() {
            //0808接入，根据类型弹不同的窗口
            if (!isChooseOneRow($mainTable)) {
                $.showErrMsg('请选择一条记录！');
                return false;
            }
            var taskId = getChooseRows($mainTable).taskId;
            var taskExecutionId = getChooseRows($mainTable).taskExecutionId;
            var taskType = getChooseRows($mainTable).busiType;
            //alert("taskid:"+taskId+",taskExecutionId:"+taskExecutionId+",taskType:"+taskType);
            switch (taskType) {
                case "DAILY_RUNTIME_LOG":
                    {
                        baseinfoAvalonInit(taskExecutionId);
                        $('#businessBaseinfoModal').modal('show');
                        break;
                    }

            }
            $('#fileup').fzFile({
                url: obj.fileUpUrl,
                upBtn: true,
                width: '85%',
                sucCallback: function() {
                    $.showSucMsg('文件上传成功！');
                },
                failCallback: function() {
                    $.showErrMsg('文件上传失败！');
                }
            });
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable.bootstrapTable('resetView', {
                height: getTHeight()
            });
        });
        //保存
        $('#businessBaseinfoSaveModalBtn').click(function() {
            // vm.saveflg = true;
            vm.sl.uploadFilePath = $('#fileup')[0].getFilePath();
            $.Ajax(obj.urlBaseinfoUpdate, JSON.parse(JSON.stringify(vm.sl.$model)), function(data) {
                // vm.logflg = false;
                // vm.slid = data;
                $('#businessBaseinfoModal').modal('hide');
                $.showSucMsg('保存成功！');
            }, {
                error: function(err, status) {
                    $.ajaxErr(err, status);
                    // vm.saveflg = false;
                }
            });
        });
    }
    //显示弹框前的事件处理
    function beforeModalShowHandler(e) {
        if (!isChooseOneRow($mainTable)) {
            $.showErrMsg('请选择一条记录！');
            return false;
        } else {
            switch (e.currentTarget.id) {
                case 'editModal':
                    {
                        var row = getChooseRows($mainTable);
                        var formJson = $(e.currentTarget).find('form').serializeJson();
                        for (var prop in formJson) {
                            $("#" + prop + '_edit').val(row[prop]);
                        }
                        break;
                    }
            }
            return true;
        }
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
    //获取表格高度
    function getTHeight() {
        return $(window).height() - ($('#u_header').outerHeight(true) + $('#u_toolbar').outerHeight(true));
    }
    init();
}($, systemLogMaintainObj, dictionary));
