/*
 *用户管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var $orgaTree = $('#orgaTree');
    var $roleTable = $('#roleTable');
    var addf = $('#addForm');
    var editf = $('#editForm');
    var changef = $('#changeForm');
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
        //渲染资源树
        renderModal();
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
            field: 'loginName',
            title: '登录名',
            sortable: true
        }, {
            field: 'name',
            title: '姓名',
            sortable: true
        }, {
            field: 'organizations',
            title: '部门',
            formatter: function(value, row, index) {
                var array = [];
                for (var i = 0; i < row.organizations.length; i++) {
                    var item = row.organizations[i];
                    array.push(item.name);
                }
                return array.join(',');
            }
        }, {
            field: 'role',
            title: '角色',
            formatter: function(value, row, index) {
                var array = [];
                for (var i = 0; i < row.roles.length; i++) {
                    var item = row.roles[i];
                    array.push(item.name);
                }
                return array.join(',');
            }
        }];
        $mainTable.fzTable({
            url: obj.urlUserQuery,
            columns: columns,
            height: getTHeight(),
            pageFlg: true,
            queryParams: function(params) {
                //请求参数处理
                var rdata = {};
                rdata.page = params.pageNumber - 1;
                rdata.size = params.pageSize;
                rdata.loginName = $('#loginName_qry').val();
                rdata.name = $('#name_qry').val();
                return rdata;
            }
        });
    }
    //渲染部门树及角色表格
    function renderModal() {
        $orgaTree.fzTree({
            url: obj.urlOrgaQuery,
            keys: ['id', 'name', 'subOrganization'],
            checkedLinkParents: false,
            maxHeight: 400
        });
        $roleTable.fzTable({
            singleSelect: false,
            indexFlg: true,
            url: obj.urlRoleQuery,
            columns: [{
                field: 'checked',
                title: '选择',
                checkbox: true
            }, {
                field: 'id',
                title: 'id',
                visible: false
            }, {
                field: 'name',
                title: '名称'
            }, {
                field: 'description',
                title: '描述'
            }]
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
            changef[0].reset();
        });
        $('button[id$="ModalBtn"]').click(function(e) {
            $(e.currentTarget).prop("disabled", true);
        });
        //主查询
        // obj.doQuery = function() {
        //     $mainTable[0].refresh();
        // };
        $('#queryBtn').click(function(e) {
            $mainTable[0].refresh();
        });
        //新增保存
        $('#addModalBtn').click(function() {
            if (!addf[0].valid()) {
                //启用按钮
                $('button[id$="ModalBtn"]').prop("disabled", false);
                return;
            }
            var addData = $.extend({}, $('#addForm').serializeJson(), {
                organizationIds: $('#orgaModal').data('ids'),
                roleIds: $('#roleModal').data('ids')
            });
            $.Ajax(obj.urlUserAdd, addData, function(data) {
                $mainTable[0].refresh();
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
            var editData = $.extend({}, $mainTable[0].getChooseRows(), $('#editForm').serializeJson(), {
                organizationIds: $('#orgaModal').data('ids'),
                roleIds: $('#roleModal').data('ids')
            });
            $.Ajax(obj.urlUserEdit, editData, function(data) {
                $mainTable[0].refresh();
                $('#editModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        //删除
        $('#deleteModalBtn').click(function(e) {
            $.Ajax(obj.urlUserDelete, {
                id: $mainTable[0].getChooseRows().id
            }, function(data) {
                $mainTable[0].refresh();
                $('#deleteModal').modal('hide');
                $.showSucMsg('删除成功！');
            });
        });
        //修改密码保存
        $('#changePasswdModalBtn').click(function(e) {
            if (!changef[0].valid()) {
                //启用按钮
                $('button[id$="ModalBtn"]').prop("disabled", false);
                return;
            }
            var changeData = $.extend({}, $mainTable[0].getChooseRows(), $('#changeForm').serializeJson());
            $.Ajax(obj.urlUserChangePW, changeData, function(data) {
                $('#changePasswdModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        //选择部门确定
        $('#orgaModalBtn').click(function(e) {
            var $orga = $('#orgaModal');
            //保存已选择部门ID列表
            $orga.data('ids', $orgaTree[0].getDataIdsFromCheckedNodes());
            //展示已选择部门到文本框
            $('#organizationIds_' + $orga.data('type') + '_text').val($orgaTree[0].getDataTextsFromCheckedNodes().join(','));
            $('#orgaModal').modal('hide');
        });
        //选择角色确定
        $('#roleModalBtn').click(function(e) {
            var $role = $('#roleModal');
            //保存已选择角色ID列表
            $role.data('ids', $roleTable[0].getChooseRowsIds());
            // 展示已选择角色到文本框
            $('#roleIds_' + $role.data('type') + '_text').val($roleTable[0].getChooseRowsKeys('name').join(','));
            $('#roleModal').modal('hide');
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable[0].resetView(getTHeight());
        });
    }
    //显示弹框前的事件处理
    function beforeModalShowHandler(e) {
        var modalid = e.currentTarget.id;
        if (modalid != 'addModal' && modalid != 'orgaModal' && modalid != 'roleModal' && !$mainTable[0].isChooseOneRow()) {
            $.showErrMsg('请选择一条记录！');
            return false;
        } else {
            switch (e.currentTarget.id) {
                case 'addModal':
                    {
                        //清空所有输入框
                        $('#addForm').clearFormInput();
                        //设置部门
                        var $orga = $('#orgaModal');
                        $('#orgaModal').data('type', 'add').data('ids', []);
                        $('#organizationIds_' + $orga.data('type') + '_text').val();
                        //设置角色
                        var $role = $('#roleModal');
                        $('#roleModal').data('type', 'add').data('ids', []);
                        $('#roleIds_' + $role.data('type') + '_text').val();
                        break;
                    }
                case 'editModal':
                    {
                        var row = $mainTable[0].getChooseRows();
                        var formJson = $(e.currentTarget).find('form').serializeJson();
                        for (var prop in formJson) {
                            $("#" + prop + '_edit').val(row[prop]);
                        }
                        //设置部门
                        var $orga = $('#orgaModal');
                        $orga.data('type', 'edit').data('ids', $.map(row.organizations, function(n) {
                            return n.id;
                        }));
                        $('#organizationIds_' + $orga.data('type') + '_text').val($.map(row.organizations, function(n) {
                            return n.name;
                        }).join(','));
                        //设置角色
                        var $role = $('#roleModal');
                        $role.data('type', 'edit').data('ids', $.map(row.roles, function(n) {
                            return n.id;
                        }));
                        $('#roleIds_' + $role.data('type') + '_text').val($.map(row.roles, function(n) {
                            return n.name;
                        }).join(','));
                        break;
                    }
                case 'changePasswdModal':
                    {
                        //清空所有输入框
                        $('#changeForm').clearFormInput();
                    }
                case 'orgaModal':
                    {
                        if (!$orgaTree[0].isLoaded()) {
                            $.showErrMsg('部门选择树加载失败！');
                            return false;
                        }
                        $orgaTree[0].setCheckedNodesByIds($('#orgaModal').data('ids'));
                        break;
                    }
                case 'roleModal':
                    {
                        if (!$roleTable[0].isLoaded()) {
                            $.showErrMsg('角色选择列表加载失败！');
                            return false;
                        }
                        $roleTable[0].hideIndexColumn();
                        $roleTable[0].checkTableRowsByIds($('#roleModal').data('ids'));
                        break;
                    }
            }
            return true;
        }
    }
    //验证
    function doValidate() {
        addf.fzValidate({
            type: 'modal',
            rules: {
                loginName: "required",
                passwd: {
                    required: true,
                    minlength: 8
                },
                passwdconfirm_add: {
                    required: true,
                    minlength: 8,
                    equalTo: "#passwd_add"
                },
                name: "required",
                organizationIds_add_text: "required",
                roleIds_add_text: "required"
            },
            messages: {
                loginName: "请输入登录名",
                passwd: {
                    required: "请输入密码",
                    minlength: $.validator.format("密码长度不能小于{0}个字符")
                },
                passwdconfirm_add: {
                    required: "请输入确认密码",
                    minlength: "确认密码长度不能小于8个字符",
                    equalTo: "两次输入密码不一致"
                },
                name: "请输入姓名",
                organizationIds_add_text: "请选择部门",
                roleIds_add_text: "请选择角色"
            }
        });
        editf.fzValidate({
            type: 'modal',
            rules: {
                name: "required",
                organizationIds_edit_text: "required",
                roleIds_edit_text: "required"
            },
            messages: {
                name: "请输入姓名",
                organizationIds_edit_text: "请选择部门",
                roleIds_edit_text: "请选择角色"
            }
        });
        changef.fzValidate({
            type: 'modal',
            rules: {
                passwd: {
                    required: true,
                    minlength: 8
                },
                passwdconfirm_change: {
                    required: true,
                    minlength: 8,
                    equalTo: "#passwd_change"
                }
            },
            messages: {
                passwd: {
                    required: "请输入新密码",
                    minlength: $.validator.format("新密码长度不能小于{0}个字符")
                },
                passwdconfirm_change: {
                    required: "请输入确认密码",
                    minlength: "确认密码长度不能小于8个字符",
                    equalTo: "两次输入密码不一致"
                }
            }
        });
    }
    //获取表格高度
    function getTHeight() {
        return $(window).height() - ($('#u_header').outerHeight(true) + $('#u_toolbar').outerHeight(true));
    }
    init();
}($, userObj));
