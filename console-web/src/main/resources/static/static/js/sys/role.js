/*
 *角色管理页面的js
 *@Author wx 2017-7-20
 */
$(function($, obj) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var $tree = $('#resourceTree');
    var addf = $('#addForm');
    var editf = $('#editForm');
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
        //渲染资源树
        renderResourceTree();
        //事件监听
        eventHandler();
        //验证
        doValidate();
    }
    //渲染表格
    function renderDatagrid() {
        var columns = [{
            field: 'checked',
            title: '选择',
            checkbox: true
        }, {
            field: 'id',
            title: 'id',
            visible: false
        }, {
            field: 'name',
            title: '名称',
            sortable: true
        }, {
            field: 'description',
            title: '描述'
        }];
        $mainTable.fzTable({
            url: obj.urlRoleQuery,
            columns: columns,
            height: getTHeight()
        });
    }
    //渲染资源树
    function renderResourceTree() {
        $tree.fzTree({
            url: obj.urlResQuery,
            keys: ['id', 'name', 'subResources'],
            maxHeight: 400
        });
    }
    //页面事件监听
    function eventHandler() {
        //工具按钮弹框前事件
        $('div[role="dialog"][id$="Modal"]').on('show.bs.modal', beforeModalShowHandler);
        $('div[role="dialog"][id$="Modal"]').on('hide.bs.modal', function() {
            //当关闭弹框时启用所有按钮
            $('button[id$="ModalBtn"]').prop('disabled', false);
            //表单验证重置
            addf[0].reset();
            editf[0].reset();
        });
        $('button[id$="ModalBtn"]').click(function(e) {
            $(e.currentTarget).prop('disabled', true);
        });
        //新增保存
        $('#addModalBtn').click(function() {
            if (!addf[0].valid()) {
                //启用按钮
                $('button[id$="ModalBtn"]').prop("disabled", false);
                return;
            }
            $.Ajax(obj.urlRoleAdd, $('#addForm').serializeJson(), function(data) {
                $mainTable.bootstrapTable('refresh');
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
            var editData = $.extend({}, $mainTable[0].getChooseRows(), $('#editForm').serializeJson());
            $.Ajax(obj.urlRoleEdit, editData, function(data) {
                $mainTable[0].refresh();
                $('#editModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        //删除
        $('#deleteModalBtn').click(function(e) {
            $.Ajax(obj.urlRoleDelete, {
                id: $mainTable[0].getChooseRows().id
            }, function(data) {
                $mainTable[0].refresh();
                $('#deleteModal').modal('hide');
                $.showSucMsg('删除成功！');
            });
        });
        //选择资源保存
        $('#roleRmModalBtn').click(function(e) {
            var ids = $tree[0].getDataIdsFromCheckedNodes();
            if (ids.length == 0) {
                $.showSucMsg('未勾选任何资源！');
                $(e.currentTarget).prop('disabled', false);
                return;
            }
            $.Ajax(obj.urlRoleResUpdate, {
                id: $mainTable[0].getChooseRows().id,
                resourceIds: ids
            }, function(data) {
                $('#roleRmModal').modal('hide');
                $.showSucMsg('保存成功！');
            });
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable[0].resetView(getTHeight());
        });
    }
    //显示弹框前的事件处理
    function beforeModalShowHandler(e) {
        if (e.currentTarget.id != 'addModal' && !$mainTable[0].isChooseOneRow()) {
            $.showErrMsg('请选择一条记录！');
            return false;
        } else {
            switch (e.currentTarget.id) {
                case 'addModal':
                    {
                        //清空所有输入框
                        $('#addForm').clearFormInput();
                        break;
                    }
                case 'editModal':
                    {
                        var row = $mainTable[0].getChooseRows();
                        var formJson = $(e.currentTarget).find('form').serializeJson();
                        for (var prop in formJson) {
                            $('#' + prop + '_edit').val(row[prop]);
                        }
                        break;
                    }
                case 'roleRmModal':
                    {
                        //初始化树表
                        showTreeView();
                        break;
                    }
            }
            return true;
        }
    }
    //加载树及数据
    function showTreeView() {
        $('#treeLoadTip').html($.tips.loadTip).show();
        $tree.hide();
        $.Ajax(obj.urlRoleResQuery, { id: $mainTable[0].getChooseRows().id }, function(data) {
            $tree[0].setCheckedNodesByData(data);
            $('#treeLoadTip').hide();
            $tree.show();
        });
    }
    //获取表格高度
    function getTHeight() {
        return $(window).height() - $('#u_toolbar').outerHeight(true) - 1;
    }
    //验证
    function doValidate() {
        var vOpt = {
            type: 'modal',
            rules: {
                name: "required",
                description: "required"
            },
            messages: {
                name: "请输入名称",
                description: "请输入描述"
            }
        };
        addf.fzValidate(vOpt);
        editf.fzValidate(vOpt);
    }
    init();
}($, roleObj));
