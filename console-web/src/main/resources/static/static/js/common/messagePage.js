/*
 *新闻页面的js
 *@Author wx 2017-8-4
 */
$(function($, obj) {
    avalon.filters.mypath = function(a) {
        //取出文件名
        return a.substring((a.lastIndexOf('\\') == -1 ? a.lastIndexOf('/') : a.lastIndexOf('\\')) + 1).substr(15);
    };
    var vm = avalon.define({
        $id: 'messagePage',
        message: {
            id: obj.messageDocumentId
        },
        showfile: false,
        filehref:'',
        type: ''
    });
    //初始化
    function init() {
        $.Ajax(obj.urlMessageQuery, JSON.parse(JSON.stringify(vm.message.$model)), function(data) {
            vm.message = $.clearObj(data);
            vm.showfile = (vm.message.filePath.length > 0);
            vm.type = dictionary.MESSAGE_DOCUMENT_TYPE[vm.message.$model.type];
            avalon.scan(document.body);
            if(vm.showfile){
                vm.filehref = encodeURI(obj.urlContext + "/commons/download?filePath=" + data.filePath);
            }
        });
    }
    init();
}($, messagePageObj));
