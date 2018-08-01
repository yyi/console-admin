/*
 *资源管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj) {
    //入口初始化方法
    var $mainTT = $('#maintreetable');
    var addf = $('#addForm');
    var editf = $('#editForm');
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
        //渲染增删改弹框
        renderModal();
        //事件监听
        eventHandler();
        //验证
        doValidate();
    }
    //渲染表格
    function renderDatagrid() {
        var columns = [{
            field: 'name',
            title: '名称'
        }, {
            field: 'resourceType',
            title: '类型',
            formatter: function(val) {
                return val;
            }
        }, {
            field: 'url',
            title: 'URL'
        }, {
            field: 'permission',
            title: '权限'
        }];
        $mainTT.fzTreeTable({
            url: obj.urlResQuery,
            columns: columns,
            keys: ['id', 'parentId', 'subResources'],
            height: getTTHeight()
        });
    }
    //渲染增删改弹框
    function renderModal() {
        $("#resourceType_add").fzSelect({
            data: {
                'MENU': 'MENU-菜单',
                'ENTRY': 'ENTRY-入口',
                'BUTTON': 'BUTTON-功能按钮'
            }
        });
    }
    //页面事件监听
    function eventHandler() {
        //工具按钮弹框前事件
        $('div[role="dialog"][id$="Modal"]').on('show.bs.modal', beforeModalShowHandler);
        $('div[role="dialog"][id$="Modal"]').on('hide.bs.modal', function() {
            //当关闭弹框时启用所有按钮
            $('button[id$="ModalBtn"]').prop("disabled", false);
            //表单验证重置
            addf[0].reset();
            editf[0].reset();
        });
        $('button[id$="ModalBtn"]').click(function(e) {
            $(e.currentTarget).prop("disabled", true);
        });
        //下拉列表值改变事件监听
        $('#resourceType_add').change(function() {
            var $p = $('#url_add').parent();
            if ($(this).val() == 'MENU') {
                $p.hide();
                $('#url_add').val('');
                $('#url_add').prop("disabled", true);
            } else {
                $p.show();
                $('#url_add').prop("disabled", false);
            }
        });
        //新增保存
        $('#addModalBtn').click(function() {
            if (!addf[0].valid()) {
                //启用按钮
                $('button[id$="ModalBtn"]').prop("disabled", false);
                return;
            }
            //新增节点数据的parentId是当前选中节点的id
            var addData = $.extend({}, getChooseNodeData($mainTT), {
                parentId: getChooseNodeData($mainTT).id
            }, $('#addForm').serializeJson());
            $.Ajax(obj.urlResAdd, addData, function(data) {
                $mainTT.reloadTreeTable(getTTHeight());
                $('#addModal').modal('hide');
                $.showSucMsg('添加成功！');
            });
        });
        //修改保存
        $('#editModalBtn').click(function() {
            if (!editf[0].valid()) {
                //启用按钮
                $('button[id$="ModalBtn"]').prop("disabled", false);
                return;
            }
            var editData = $.extend({}, getChooseNodeData($mainTT), $('#editForm').serializeJson());
            $.Ajax(obj.urlResEdit, editData, function(data) {
                $mainTT.reloadTreeTable(getTTHeight());
                $('#editModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        //删除保存
        $('#deleteModalBtn').click(function() {
            var id = $mainTT.getChooseNodeId();
            $.Ajax(obj.urlResDelete, {
                id: id
            }, function(data) {
                $mainTT.removeNode(id);
                $mainTT.refreshTreeTableView();
                $('#deleteModal').modal('hide');
                $.showSucMsg('删除成功！');
            });
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTT.resetHeight(getTTHeight());
        });
    }
    //显示弹框前的事件处理
    function beforeModalShowHandler(e) {
        if (!isChooseOneNode($mainTT)) {
            $.showErrMsg('请选择一个节点！');
            return false;
        } else {
            switch (e.currentTarget.id) {
                case 'addModal':
                    {
                        //清空所有输入框
                        $('#addForm').clearFormInput();
                        var row = getChooseNodeData($mainTT);
                        var data;
                        $('#url_add').prop("disabled", false);
                        switch (row.resourceType) {
                            case 'MENU':
                                {
                                    data = (row.deep == 2) ? { 'ENTRY': 'ENTRY-入口' } : { 'MENU': 'MENU-菜单', 'ENTRY': 'ENTRY-入口' };
                                    $('#url_add').parent().hide(); //菜单隐藏URL
                                    $('#url_add').prop("disabled", true);
                                    break;
                                }
                            case 'ENTRY':
                                { data = { 'BUTTON': 'BUTTON-功能按钮' }; break; }
                            case 'BUTTON':
                                { $.showErrMsg('按钮节点无法新增！'); return false; }
                        }
                        $("#resourceType_add")[0].refreshSelect(data);
                        break;
                    }
                case 'editModal':
                    {
                        var row = getChooseNodeData($mainTT);
                        var formJson = $(e.currentTarget).find('form').serializeJson();
                        var $p = $('#url_edit').parent();
                        $('#url_edit').prop("disabled", false);
                        (row.resourceType == 'MENU') ? ($p.hide(), $('#url_edit').prop("disabled", true)) : $p.show();
                        for (var prop in formJson) {
                            $("#" + prop + '_edit').val(row[prop]);
                        }
                        break;
                    }
                case 'deleteModal':
                    {
                        var id = $mainTT.getChooseNodeId()
                        if (!$mainTT.isLeafNode(id)) {
                            $.showErrMsg('仅叶子节点可以删除！');
                            return false;
                        }
                        if ($mainTT.isRootNode(id)) {
                            $.showSucMsg('根节点无法删除！');
                            return false;
                        }
                        break;
                    }
            }
            return true;
        }
    }
    //获取treetable树表选中行的数据数组
    function getChooseNodeData(treetable) {
        var ttobj = (typeof treetable === 'string') ? $('#' + treetable) : treetable;
        return ttobj.getNodeData(ttobj.getChooseNodeId());
    }
    //判断treetable树表是否选择了一条记录
    function isChooseOneNode(treetable) {
        var ttobj = (typeof treetable === 'string') ? $('#' + treetable) : treetable;
        return ttobj.getChooseNodeId() === null ? false : true;
    }
    //获取树表高度
    function getTTHeight() {
        return $(window).height() - $('#u_toolbar').outerHeight(true);
    }
    //验证
    function doValidate() {
        addf.fzValidate({
            type: 'modal',
            rules: {
                name: "required",
                resourceType: "required",
                url: "required",
                permission: "required"
            },
            messages: {
                name: "请输入名称",
                resourceType: "请选择类型",
                url: "请输入URL",
                permission: "请输入权限"
            }
        });
        editf.fzValidate({
            type: 'modal',
            rules: {
                name: "required",
                url: "required",
                permission: "required"
            },
            messages: {
                name: "请输入名称",
                url: "请输入URL",
                permission: "请输入权限"
            }
        });
    }
    init();
}($, resourceObj));
