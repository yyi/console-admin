/*
 *设备信息维护页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj, data) {
    var vm = avalon.define({
        $id: 'equipmentMiantain',
        staffName: "",
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
    function baseinfoAvalonInit(taskExecutionId) {
        //首页传来
        var taskExecId = { taskExecutionId: taskExecutionId };
        $.Ajax(obj.urlBaseinfoInit, taskExecId, function(data) {
            vm.baseinfo = data;
            avalon.scan(document.body);
        });
    }
    //整合Avalon
    function internetAvalonInit(taskExecutionId) {
        //首页传来
        var taskExecId = { taskExecutionId: taskExecutionId };
        $.Ajax(obj.urlInternetInit, taskExecId, function(data) {
            vm.internet = data;
            avalon.scan(document.body);
        });
    }
    //整合Avalon
    function acceptAvalonInit(taskExecutionId) {
        //首页传来
        var taskExecId = { taskExecutionId: taskExecutionId };
        $.Ajax(obj.urlAcceptInit, taskExecId, function(data) {
            vm.accept = data;
            avalon.scan(document.body);
        });
    }
    //整合Avalon
    function softwareAvalonInit(taskExecutionId) {
        //首页传来
        var taskExecId = { taskExecutionId: taskExecutionId };
        $.Ajax(obj.urlSoftwareInit, taskExecId, function(data) {
            vm.software = data;
            avalon.scan(document.body);
        });
    }
    //整合Avalon
    function serverAvalonInit(taskExecutionId) {
        //首页传来
        var taskExecId = { taskExecutionId: taskExecutionId };
        $.Ajax(obj.urlServerInit, taskExecId, function(data) {
            vm.server = data;
            avalon.scan(document.body);
        });
    }
    //整合Avalon
    function phoneAvalonInit(taskExecutionId) {
        //首页传来
        var taskExecId = { taskExecutionId: taskExecutionId };
        $.Ajax(obj.urlPhoneInit, taskExecId, function(data) {
            vm.phone = data;
            avalon.scan(document.body);
        });
    }
    //整合Avalon
    function otherAvalonInit(taskExecutionId) {
        //首页传来
        var taskExecId = { taskExecutionId: taskExecutionId };
        $.Ajax(obj.urlOtherInit, taskExecId, function(data) {
            vm.other = data;
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
                return data['ELEMENT_TYPE'][row['busiType']];
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
            url: obj.urlEquipmentMaintainIndex,
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
                case "EQUIPMENT_BASEINFO":
                    {
                        baseinfoAvalonInit(taskExecutionId);
                        $('#businessBaseinfoModal').modal('show');
                        break;
                    }
                case "EQUIPMENT_INTERNET":
                    {
                        //alert(taskExecutionId);
                        internetAvalonInit(taskExecutionId);
                        $('#businessInternetModal').modal('show');
                        break;
                    }
                case "EQUIPMENT_ACCEPT":
                    {
                        //alert("in case taskExecutionId:"+taskExecutionId)
                        acceptAvalonInit(taskExecutionId);
                        $('#businessAcceptModal').modal('show');
                        break;
                    }
                case "EQUIPMENT_SERVER":
                    {
                        serverAvalonInit(taskExecutionId);
                        $('#businessServerModal').modal('show');
                        break;
                    }
                case "EQUIPMENT_SOFTWARE":
                    {
                        softwareAvalonInit(taskExecutionId);
                        $('#businessSoftwareModal').modal('show');
                        break;
                    }
                case "EQUIPMENT_PHONE":
                    {
                        phoneAvalonInit(taskExecutionId);
                        $("#businessPhoneModal").modal('show');
                        break;
                    }
                case "EQUIPMENT_OTHER":
                    {
                        otherAvalonInit(taskExecutionId);
                        $("#businessOtherModal").modal('show');
                        break;
                    }
            }
        });
        //基础信息保存
        $('#businessBaseinfoSaveModalBtn').click(function() {
            $.Ajax(obj.urlBaseinfoUpdate, JSON.parse(JSON.stringify(vm.baseinfo.$model)), function(data) {
                vm.baseinfo = data;
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $('#businessBaseinfoModal').modal('hide');
                    }
                });
            });
        });
        //互联网线路保存
        $('#businessInternetSaveModalBtn').click(function() {
            $.Ajax(obj.urlInternetUpdate, JSON.parse(JSON.stringify(vm.internet.$model)), function(data) {
                vm.internet = data;
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $('#businessInternetModal').modal('hide');
                    }
                });
            });
        });
        //行情接受保存
        $('#businessAcceptSaveModalBtn').click(function() {
            $.Ajax(obj.urlAcceptUpdate, JSON.parse(JSON.stringify(vm.accept.$model)), function(data) {
                vm.accept = data;
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $('#businessAcceptModal').modal('hide');
                    }
                });
            });
        });
        //交易服务器保存
        $('#businessServerSaveModalBtn').click(function() {
            $.Ajax(obj.urlServerUpdate, JSON.parse(JSON.stringify(vm.server.$model)), function(data) {
                vm.server = data;
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $('#businessServerModal').modal('hide');
                    }
                });
            });
        });
        //交易软件保存
        $('#businessSoftwareSaveModalBtn').click(function() {
            $.Ajax(obj.urlSoftwareUpdate, JSON.parse(JSON.stringify(vm.software.$model)), function(data) {
                vm.software = data;
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $('#businessSoftwareModal').modal('hide');
                    }
                });
            });
        });
        //电话坐席保存
        $('#businessPhoneSaveModalBtn').click(function() {
            $.Ajax(obj.urlPhoneUpdate, JSON.parse(JSON.stringify(vm.phone.$model)), function(data) {
                vm.phone = data;
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $('#businessPhoneModal').modal('hide');
                    }
                });
            });
        });
        //其他设备保存
        $('#businessOtherSaveModalBtn').click(function() {
            $.Ajax(obj.urlOtherUpdate, JSON.parse(JSON.stringify(vm.other.$model)), function(data) {
                vm.other = data;
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $('#businessOtherModal').modal('hide');
                    }
                });
            });
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
}($, equipmentMaintainObj, dictionary));
