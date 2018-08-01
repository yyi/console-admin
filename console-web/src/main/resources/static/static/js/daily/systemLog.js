/*
 *用户管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj) {
    avalon.filters.inputStatusF = function(a) {
        if (a === 'YES')
            return '已完成';
        if (a === 'NO')
            return '未完成';
        else
            return a;
    };
    avalon.filters.isNomalF = function(a) {
        if (a === 'YES')
            return '正常';
        if (a === 'NO')
            return '异常';
        else
            return a;
    };
    var vm = avalon.define({
        $id: 'systemLog',
        sl: {
            taskExecutionId: obj.taskExecutionId,
            operation: obj.operation
        },
        slid: 0,
        sld: {},
        templateUrl: encodeURI(obj.urlContext + "/commons/download?filePath=/template/Excel11.xls"),
        downloadUrl: '',
        saveflg: true,
        saveTempFlg:true,
        orgaText: '',
        orgaTextFlg: obj.operation == 'add',
        organizationChange: function() {},
        inputDateChange: function() {}
    });
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var $logTable = $('#logTable');
    //初始化
    function init() {
        avalonInit();
        eventHandler();
    }
    //整合Avalon
    function avalonInit() {
        //加载数据
        loadData(false);
    }
    //页面事件监听
    function eventHandler() {
        //工具按钮弹框前事件
        $('div[role="dialog"][id$="Modal"]').on('show.bs.modal', function(e) {
            if (e.currentTarget.id == 'logModal') {
                $('#logLoadTip').html($.tips.loadTip).show();
                $logTable.hide();
                $.Ajax(obj.urlSyslogFind, {
                    systemRuntimeLogId: vm.slid
                }, function(data) {
                    //加载填写数据
                    vm.sld = $.clearObj(data);
                    $('#logLoadTip').hide();
                    renderFileUp();
                    if (vm.sld.uploadFilePath != '')
                        $('#fileup')[0].setFilePath(vm.sld.uploadFilePath);
                    vm.downloadUrl = encodeURI(obj.urlContext + "/commons/download?filePath=" + vm.sld.uploadFilePath);
                    $logTable.show();
                });
            }
        });
        $('div[role="dialog"][id$="Modal"]').on('hide.bs.modal', function(e) {
            //当关闭弹框时启用所有按钮
            $('button[id$="ModalBtn"]').prop("disabled", false);
            if (e.currentTarget.id == 'logModal') {
                //关闭日志填写弹框给出确认提示
                $('#cancelModal').modal('show');
                return false;
            }
        });
        $('button[id$="ModalBtn"]').click(function(e) {
            $(e.currentTarget).prop("disabled", true);
        });
        vm.inputDateChange = function(e) {
            vm.sl.inputDate = $(e.target).val();
            //重新加载数据
            loadData(true);
        };
        vm.organizationChange = function(e) {
            vm.sl.organizationN = $(e.target).val();
            //重新加载数据
            loadData(true);
        };
        //点击一键正常
        $('#one_btn').click(function() {
            $.each(vm.sl.systemRuntimeLogDetails, function(i, n) {
                for (var j = 1; j <= 4; j++) {
                    vm.sl.systemRuntimeLogDetails[i]['checkStatus' + j] = 'NORM';
                }
            });
        });

        //保存临时按钮
        $('#save_temp_btn').click(function() {

            $.Ajax(obj.urlSyslogTempSave, JSON.parse(JSON.stringify(vm.sl.$model)), function(data) {
                vm.slid = data;
                $.showSucMsg('保存成功！');
            }, {
                error: function(err, status) {
                    $.ajaxErr(err, status);
                    vm.saveflg = false;
                }
            });
        });

        //保存并填写运行日志
        $('#save_btn').click(function() {
            vm.saveflg = true;
            $.Ajax(obj.urlSyslogSave, JSON.parse(JSON.stringify(vm.sl.$model)), function(data) {
                vm.slid = data;
                //显示弹框
                $('#logModal').modal('show');
            }, {
                error: function(err, status) {
                    $.ajaxErr(err, status);
                    vm.saveflg = false;
                }
            });
        });
        //保存当日运行日志
        $('#logModalBtn').click(function() {
            $.Ajax(obj.urlSyslogEdit, $.extend(JSON.parse(JSON.stringify(vm.sld.$model)), {
                uploadFilePath: $('#fileup')[0].getFilePath()
            }), function(data) {
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $.closeTab(window.top);
                    }
                });
            }, {
                error: function(err, status) {
                    $.ajaxErr(err, status);
                    $('#logModalBtn').prop("disabled", false);
                }
            });
        });
        //确定关闭当前标签页
        $('#cancelBtn').click(function() {
            $.closeTab(window.top);
        });
    }
    //加载数据
    function loadData(flg) {
        var data = JSON.parse(JSON.stringify(vm.sl.$model));
        if (flg && data.taskExecutionId) {
            delete data.taskExecutionId;
        }
        $.Ajax(obj.urlSyslogInit, data, function(data) {
            //保存按钮是否可用
            vm.saveflg = $.isEmpty(data.systemRuntimeLogDetails) || data.systemRuntimeLogDetails.length <= 0;
            vm.saveTempFlg = vm.saveflg;
            //加载主数据
            vm.sl = $.clearObj(data);
            if (vm.saveflg)
                vm.sl.systemRuntimeLogDetails = [];
            avalon.scan(document.body);
            //如果是编辑则无法选择部门
            $.map(vm.sl.organizationValues.$model, function(n) {
                if (vm.sl.organizationN === n.organizationNo)
                    vm.orgaText = n.name;
            });
        });
    }
    //渲染文件上传
    function renderFileUp() {
        var orga = vm.orgaText;
        $('#fileup').fzFile({
            url: obj.fileUpUrl,
            upBtn: true,
            width: '85%',
            placeHolder: '上传文件名格式为:' + orga + '-系统运行日志-自定义',
            fileNameCheckRegExp: (orga + '\\-系统运行日志\\-'),
            fileNameCheckErrMsg: '上传文件名格式为:' + orga + '-系统运行日志-自定义',
            sucCallback: function() {
                $.showSucMsg('文件上传成功！');
            },
            failCallback: function() {
                $.showErrMsg('文件上传失败！');
            }
        });
    }
    init();
}($, syslogObj));
