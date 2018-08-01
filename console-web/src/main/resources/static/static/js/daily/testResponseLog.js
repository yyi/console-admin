/*
 *测试反馈的js
 *@Author wx 2017-7-10
 */
$(function($, obj) {
    var vm = avalon.define({
        $id: 'testResponseLog',
        sl: {
            taskExecutionId: obj.taskExecutionId,
            operation: obj.operation,
            organizationChange: function() {},
            inputDateChange: function() {}
        },
        templateUrl: encodeURI(obj.urlContext + "/commons/download?filePath=/template/Excel11.xls"),
        downloadUrl: '',
        slid: 0,
        sld: {},
        saveflg: true,
        logflg: true
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
        $.Ajax(obj.urlSyslogInit, JSON.parse(JSON.stringify(vm.sl.$model)), function(data) {
            vm.saveflg = $.isEmpty(data.testResponseLogDetails) || data.testResponseLogDetails.length <= 0;
            //加载主数据
            vm.sl = $.clearObj(data);
            vm.downloadUrl = encodeURI(obj.urlContext + "/commons/download?filePath=" + vm.sl.filePath);
            avalon.scan(document.body);
            renderFileUp();
        });
    }
    //页面事件监听
    function eventHandler() {
        //保存
        $('#save_btn').click(function() {
            if(vm.sl.$model.testProfile == ''){
                $.showErrMsg('未选择测试概况，无法保存！');
                return;
            }
            vm.saveflg = true;
            $.Ajax(obj.urlSyslogSave, $.extend(JSON.parse(JSON.stringify(vm.sl.$model)), {
                filePath: $('#fileup')[0].getFilePath()
            }), function(data) {
                vm.logflg = false;
                vm.slid = data;
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $.closeTab(window.top);
                    }
                });
            }, {
                error: function(err, status) {
                    $.ajaxErr(err, status);
                    vm.saveflg = false;
                }
            });
        });
    }
    //渲染文件上传
    function renderFileUp() {
        var orga = vm.sl.taskExecution.organizationName;
        $('#fileup').fzFile({
            url: obj.fileUpUrl,
            upBtn: true,
            width: '85%',
            placeHolder: '上传文件名格式为:' + orga + '-测试反馈-自定义',
            fileNameCheckRegExp: (orga + '\\-测试反馈\\-'),
            fileNameCheckErrMsg: '上传文件名格式为:' + orga + '-测试反馈-自定义',
            sucCallback: function() {
                $.showSucMsg('文件上传成功！');
            },
            failCallback: function() {
                $.showErrMsg('文件上传失败！');
            }
        });
        if (obj.operation == 'update')
            $('#fileup')[0].setFilePath(vm.sl.filePath);
    }
    init();
}($, syslogObj));
