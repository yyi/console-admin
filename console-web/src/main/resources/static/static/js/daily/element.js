/*
 *用户管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj, data) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var addf = $('#addForm');
    var editf = $('#editForm');
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
        //获取元素类型
        getElementType();
        //事件监听
        eventHandler();
        //验证
        doValidate();
    }
    //获取元素类型
    function getElementType() {
        $("#type_add").fzSelect({
            data: data.ELEMENT_TYPE
        });
        $("#type_edit").fzSelect({
            data: data.ELEMENT_TYPE
        });
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
            title: '元素名',
            sortable: true
        }, {
            field: 'type',
            title: '类型',
            formatter: function(value, row, index) {
                return data['ELEMENT_TYPE'][row['type']];
            },
            sortable: true
        }, {
            field: 'createDate',
            title: '创建时间',
            sortable: true
        }, {
            field: 'updateDate',
            title: '更新时间',
            sortable: true
        }, {
            field: 'updateStaff',
            title: '更新人',
        }, {
            field: 'remark',
            title: '备注'
        }];
        $mainTable.fzTable({
            url: obj.urlElementQuery,
            columns: columns,
            height: getTHeight(),
            pageFlg: true,
            queryParams: function(params) {
                //请求参数处理
                var rdata = {};
                rdata.page = params.pageNumber - 1;
                rdata.size = params.pageSize;
                return $.extend(rdata, $('#queryForm').serializeJson());
            }
        });
    }
    //下拉数据选择
    function selectDate() {
        $("#templateType_add").fzSelect({
            data: tempObj.selectDS.ELEMENT_TYPE
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
        //查询
        $('#queryBtn').click(function() {
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
            $.Ajax(obj.urlElementAdd, addData, function(data) {
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
            var editData = $('#editForm').serializeJson();
            $.Ajax(obj.urlElementEdit, editData, function(data) {
                $mainTable[0].refresh();
                $('#editModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        //删除
        $('#deleteModalBtn').click(function(e) {
            $.Ajax(obj.urlElementDelete, {
                id: $mainTable[0].getChooseRows().id
            }, function(data) {
                $mainTable[0].refresh();
                $('#deleteModal').modal('hide');
                $.showSucMsg('删除成功！');
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
                case 'editModal':
                    {
                        var row = $mainTable[0].getChooseRows();
                        var formJson = $(e.currentTarget).find('form').serializeJson();
                        for (var prop in formJson) {
                            $("#" + prop + '_edit').val(row[prop]);
                        }
                        break;
                    }
            }
            return true;
        }
    }
    //获取表格高度
    function getTHeight() {
        return $(window).height() - ($('#u_header').outerHeight(true) + $('#u_toolbar').outerHeight(true));
    }
    //验证
    function doValidate() {
        var vOpt = {
            type: 'modal',
            rules: {
                name: "required",
                type: "required"
            },
            messages: {
                name: "请输入元素名",
                type: "请选择类型"
            }
        };
        addf.fzValidate(vOpt);
        editf.fzValidate(vOpt);
    }
    init();
}($, elementObj, dictionary));
