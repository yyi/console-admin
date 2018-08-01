$(function($, obj) {
    var vm = avalon.define({
        $id: 'dynamicStat',
        dynamicSt: {
            taskExecutionId: obj.taskExecutionId,
            operation: obj.operation
        },
        downloadUrl: ''
    });
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    //初始化
    function init() {
        avalonInit();
        eventHandler();
    }
    //整合Avalon
    function avalonInit() {
        //首页传来
        $.Ajax(obj.urlDynamicInit, JSON.parse(JSON.stringify(vm.dynamicSt.$model)), function(data) {
            vm.dynamicSt = $.clearObj(data);
            vm.downloadUrl = encodeURI(obj.urlContext + "/commons/download?filePath=" + vm.dynamicSt.uploadFilePath);
            avalon.scan(document.body);
            renderFileUp(true);
            $('#fileup')[0].setFilePath(vm.dynamicSt.uploadFilePath);
        });
    }
    //渲染文件上传
    function renderFileUp(isAdd) {
        var type = '在线填报';
        var orge = vm.dynamicSt.organizationName;
        var fileopt = {
            url: obj.fileUpUrl,
            upBtn: true,
            width: '85%',
            placeHolder: '上传文件名格式为部门-在线填报-自定义',
            fileNameCheckRegExp: (orge + '\\-' + type + '\\-'),
            fileNameCheckErrMsg: '上传文件名格式为部门-在线填报-自定义',
            sucCallback: function() {
                $.showSucMsg('文件上传成功！');
            },
            failCallback: function() {
                $.showErrMsg('文件上传失败！');
            }
        };
        if (isAdd) { $('#fileup').fzFile(fileopt); } else { $('#fileup').fzFile(fileopt); }
    }
    //页面事件监听
    function eventHandler() {
        //保存
        $('#save_btn').click(function() {
            vm.dynamicSt.uploadFilePath = $('#fileup')[0].getFilePath();
            $.Ajax(obj.urlDynamicAdd, JSON.parse(JSON.stringify(vm.dynamicSt.$model)), function(data) {
                vm.dynamicSt = data;
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $.closeTab(window.top);
                    }
                });
            });
        });
    }
    init();
}($, dynamicObj));
