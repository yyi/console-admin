$(function($, obj) {
    var vm = avalon.define({
        $id: 'busiAccept',
        busiAccept: {}
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
        var taskExecId={taskExecutionId:7};
        $.Ajax(obj.urlBusinessAcceptInit,taskExecId, function(data) {
            vm.busiAccept = data;
            avalon.scan(document.body);
        });
    }
    //整合Avalon
    function submitDynamicValue() {
        $.Ajax(obj.urlDynamicInit,JSON.parse(JSON.stringify(vm.busiAccept.$model)), function(data) {
            vm.busiAccept = data;
            avalon.scan(document.body);
        });
    }
    //页面事件监听
    function eventHandler() {

        //保存
        $('#save_btn').click(function() {
            $.Ajax(obj.urlBusinessAcceptAdd, JSON.parse(JSON.stringify(vm.busiAccept.$model)), function(data) {
                vm.busiAccept = data;
                $.showSucMsg('保存成功！');

                //$.closeTab(window.top);
            });
        });


    }
    init();
}($, busiAcceptObj));
