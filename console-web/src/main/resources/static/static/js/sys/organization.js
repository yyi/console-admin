/*
 *部门管理页面的js
 *@Author wx 2017-7-26
 */
$(function($, obj) {
    //入口初始化方法
    var $mainTT = $('#maintreetable');
    var $orgaTree = $('#orgaTree');
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
            title: '部门名称'
        }, {
            field: 'organizationNo',
            title: '部门代码'
        }, {
            field: 'organizationType',
            title: '部门类型',
            formatter: function(value) {
                return obj.dictionary['ORGANIZATION_TYPE'][value];
            }
        }, {
            field: 'organizationLevel',
            title: '部门级别',
            formatter: function(value) {
                return obj.dictionary['ORGANIZATION_LEVEL'][value];
            }
        }, {
            field: 'address',
            title: '地址'
        }, {
            field: 'zipCode',
            title: '邮编'
        }];
        $mainTT.fzTreeTable({
            url: obj.urlOrgaQuery,
            columns: columns,
            keys: ['id', 'parentId', 'subOrganization'],
            height: getTTHeight()
        });
    }
    //渲染增删改弹框
    function renderModal() {
        var typedata = obj.dictionary['ORGANIZATION_TYPE'];
        var leveldata = obj.dictionary['ORGANIZATION_LEVEL'];
        $("#organizationType_add").fzSelect({
            data: typedata
        });
        $("#organizationType_edit").fzSelect({
            data: typedata
        });
        $("#organizationLevel_add").fzSelect({
            data: leveldata
        });
        $("#organizationLevel_edit").fzSelect({
            data: leveldata
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
        //新增保存
        $('#addModalBtn').click(function() {
            if (!addf[0].valid()) {
                //启用按钮
                $('button[id$="ModalBtn"]').prop("disabled", false);
                return;
            }
            //新增节点数据的parentId是当前选中节点的id
            var addData = $.extend({}, $mainTT.getChooseNodeData(), {
                parentId: $mainTT.getChooseNodeData().id
            }, $('#addForm').serializeJson());
            $.Ajax(obj.urlOrgaAdd, addData, function(data) {
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
            var editData = $.extend({}, $mainTT.getChooseNodeData(), $('#editForm').serializeJson());
            $.Ajax(obj.urlOrgaEdit, editData, function(data) {
                $mainTT.reloadTreeTable(getTTHeight());
                $('#editModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        //删除保存
        $('#deleteModalBtn').click(function() {
            var id = $mainTT.getChooseNodeId();
            $.Ajax(obj.urlOrgaDelete, {
                id: id
            }, function(data) {
                $mainTT.removeNode(id);
                $mainTT.refreshTreeTableView();
                $('#deleteModal').modal('hide');
                $.showSucMsg('删除成功！');
            });
        });
        //变更部门保存
        $('#changeOrgaModalBtn').click(function() {
            var id = $mainTT.getChooseNodeId();
            var nodes = $orgaTree[0].getDataIdsFromCheckedNodes();
            if (nodes.length != 1) {
                $.showErrMsg('请选择一个节点！');
                //启用按钮
                $('button[id$="ModalBtn"]').prop("disabled", false);
                return;
            }
            $.Ajax(obj.urlOrgaChange, {
                id: id,
                parentId: nodes[0]
            }, function(data) {
                $mainTT.reloadTreeTable(getTTHeight());
                $('#changeOrgaModal').modal('hide');
                $.showSucMsg('变更上级部门成功！');
            });
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTT.resetHeight(getTTHeight());
        });
    }
    //显示弹框前的事件处理
    function beforeModalShowHandler(e) {
        if (!$mainTT.isChooseOneNode()) {
            $.showErrMsg('请选择一个节点！');
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
                        var row = $mainTT.getChooseNodeData();
                        var formJson = $(e.currentTarget).find('form').serializeJson();
                        for (var prop in formJson) {
                            $("#" + prop + '_edit').val(row[prop]);
                        }
                        break;
                    }
                case 'deleteModal':
                    {
                        var id = $mainTT.getChooseNodeId()
                        if ($mainTT.isRootNode(id)) {
                            $.showErrMsg('根节点无法删除！');
                            return false;
                        }
                        if (!$mainTT.isLeafNode(id)) {
                            $.showErrMsg('仅叶子节点可以删除！');
                            return false;
                        }
                        break;
                    }
                case 'changeOrgaModal':
                    {
                        var id = $mainTT.getChooseNodeId()
                        if ($mainTT.isRootNode(id)) {
                            $.showErrMsg('根节点无法变更！');
                            return false;
                        }
                        //初始化树
                        showTreeView(id);
                        break;
                    }
            }
            return true;
        }
    }
    //加载树及数据
    function showTreeView(id) {
        $('#treeLoadTip').html($.tips.loadTip).show();
        $orgaTree.hide();
        $orgaTree.fzTree({
            url: obj.urlOrgaQuery,
            singleSelect: true,
            checkedLink: false,
            queryParams: {
                id: id
            },
            keys: ['id', 'name', 'subOrganization'],
            ajaxSucCallback: function(data) {
                $('#treeLoadTip').hide();
                $orgaTree.show();
            },
            maxHeight: 400
        });
    }
    //获取树表高度
    function getTTHeight() {
        return $(window).height() - $('#u_toolbar').outerHeight(true);
    }
    //验证
    function doValidate() {
        var vOpt = {
            type: 'modal',
            rules: {
                name: "required",
                organizationNo: "required",
                organizationType: "required",
                organizationLevel: "required",
                address: "required",
                zipCode: {
                    required: true,
                    zipcode_fz: true
                }
            },
            messages: {
                name: "请输入名称",
                organizationNo: "请输入部门代码",
                organizationType: "请选择部门类型",
                organizationLevel: "请选择部门级别",
                address: "请输入地址",
                zipCode: {
                    required: "请输入邮编",
                    zipcode_fz: "请输入正确的邮政编码"
                }
            }
        };
        addf.fzValidate(vOpt);
        editf.fzValidate(vOpt);
    }
    init();
}($, organizationObj));
