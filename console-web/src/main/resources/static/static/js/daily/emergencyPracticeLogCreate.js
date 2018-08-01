/*
 *应急演练填报页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj) {
    var vm = avalon.define({
        $id: 'emergencyPracticeLogCreate',
        sl: {
            currentUserName: '',
            taskExecutionId: '',
            currentDate: '',
            emergencyPracticeLogElement: [{
                id:null,
                elementId: '',
                elementName: '',
                isExists: '',
                practiceContent: '',
                practiceClaim: '',
                practiceStep: '',
                practiceTime: '',
                rpacticeResult: '正常',
                checkProcess: '',
                recoveryState: '',
                recoveryStateRemark: ''
            }]
        }
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
        $.Ajax(obj.urlSyncElement, {
            id: obj.taskExecutionId,
            operation: obj.operation
        }, function(data) {
            vm.sl = $.clearObj(data);
            avalon.scan(document.body);
            laydateInit(data);
        });
    }
    //页面日期初始化
    function laydateInit(data) {
        var dto = data.emergencyPracticeLogElement;
        if (dto) {
            for (var i = 0; i < dto.length; i++) {
                laydate({ elem: '#practiceTime' + i, istime: false, format: 'YYYYMMDD' });
            }
        }
    }
    //页面事件监听
    function eventHandler() {
        //保存
        $('#save_btn').click(function() {
            var sobj = vm.sl.emergencyPracticeLogElement.$model;
            var status = $.map(sobj, function(n) {
                return n.isExists + '';
            }).join('');
            if (status.length != sobj.length) {
                $.showErrMsg('存在未填写项目，无法保存！');
                return;
            }
            $.Ajax(obj.urlCreate, JSON.parse(JSON.stringify(vm.sl.$model)), function(data) {
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $.closeTab(window.top);
                    }
                });
            });
        });
    }
    init();
}($, syslogObj));
