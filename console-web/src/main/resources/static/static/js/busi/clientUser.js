/*
 *用户管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var addf = $('#addForm');
    var editf = $('#editForm');
    var changef = $('#changeForm');
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
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
            title: '证件号',
            sortable: true
        }, {
            field: 'name',
            title: '姓名',
            sortable: true
        }, {
            field: 'mobileNo',
            title: '手机号码',
            sortable: true
        }, {
            field: 'emailAddr',
            title: '邮箱',
            sortable: true
        }, {
            field: 'personal',
            title: '用户类型',
            formatter: function(value, row, index) {
                switch (value) {
                    case true:
                        return '个人';
                    case false:
                        return '机构';
                    default:
                        return '';
                }
            }
        }, {
            field: 'initialized',
            title: '是否初始化',
            formatter: function(value, row, index) {
                switch (value) {
                    case true:
                        return '是';
                    case false:
                        return '否';
                    default:
                        return '';
                }
            }
        }, {
            field: 'status',
            title: '状态',
            formatter: function(value, row, index) {
                switch (value) {
                    case 'AVAILIABLE':
                        return '正常';
                    case 'DISABLE':
                        return '停用';
                }
            }
        }];
        $mainTable.fzTable({
            url: obj.urlClientUserQuery,
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
            var addData = $('#addForm').serializeJson();
            $.Ajax(obj.urlClientUserAdd, addData, function(data) {
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
            var editData = $.extend({}, $mainTable[0].getChooseRows(), $('#editForm').serializeJson());
            $.Ajax(obj.urlClientUserEdit, editData, function(data) {
                $mainTable[0].refresh();
                $('#editModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        //删除
        $('#deleteModalBtn').click(function(e) {
            $.Ajax(obj.urlClientUserDelete, {
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
            $.Ajax(obj.urlClientUserChangePW, changeData, function(data) {
                $('#changePasswdModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
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
                        break;
                    }
                case 'editModal':
                    {
                        //清空所有输入框
                        $('#editForm').clearFormInput();
                        var row = $mainTable[0].getChooseRows();
                        var formJson = $(e.currentTarget).find('form').serializeJson();
                        for (var prop in formJson) {
                            $("#" + prop + '_edit').val(row[prop]);
                        }
                        $("#personal_edit").val(row.personal + "");
                        $("#initialized_edit").val(row.initialized + "");
                        break;
                    }
                case 'changePasswdModal':
                    {
                        //清空所有输入框
                        $('#changeForm').clearFormInput();
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
                personal: "required",
                mobileNo: {
                    required: true,
                    minlength: 11
                },
                emailAddr: "required"
            },
            messages: {
                loginName: "请输入登录名",
                passwd: {
                    required: "请输入密码",
                    minlength: $.validator.format("密码长度不能小于{0}个字符")
                },
                passwdconfirm_add: {
                    required: "请输入确认密码",
                    minlength: "确认密码长度不能小于{0}个字符",
                    equalTo: "两次输入密码不一致"
                },
                name: "请输入姓名",
                personal: "请选择用户类型",
                mobileNo:  {
                    required: "请输入手机号码",
                    minlength: $.validator.format("手机号码长度不能小于{0}个字符")
                },
                emailAddr: "请输入邮箱"
            }
        });
        editf.fzValidate({
            type: 'modal',
            rules: {
                name: "required",
                personal: "required",
                initialized: "required",
                mobileNo: {
                    required: true,
                    minlength: 11
                },
                emailAddr: "required"
            },
            messages: {
                name: "请输入姓名",
                personal: "请选择用户类型",
                initialized:"请选择是否初始化",
                mobileNo:  {
                    required: "请输入手机号码",
                    minlength: $.validator.format("手机号码长度不能小于{0}个字符")
                },
                emailAddr: "请输入邮箱"
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
                    minlength: "确认密码长度不能小于{0}个字符",
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
}($, clientUserObj));
