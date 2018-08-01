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
        //渲染下拉列表
        renderSelect();
        //渲染表格
        renderDatagrid();
        //事件监听
        eventHandler();
        //验证
        doValidate();
    }
    //渲染下拉列表
    function renderSelect() {
        $("#type_add").fzSelect({
            data: data.ELEMENT_TYPE
        });
        $("#type_edit").fzSelect({
            data: data.ELEMENT_TYPE
        });
    }
    //清空下拉列表
    function clearElementSelect() {
        $("#elementLeft").empty();
        $("#elementRight").empty();
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
            title: '模板名',
            sortable: true
        }, {
            field: 'type',
            title: '类型',
            formatter: function(value, row, index) {
                return data['ELEMENT_TYPE'][row['type']];
            },
            sortable: true
        }];
        $mainTable.fzTable({
            url: obj.urlQuery,
            columns: columns,
            height: getTHeight(),
            pageFlg: true,
            queryParams: function(params) {
                //请求参数处理
                var rdata = {};
                rdata.page = params.pageNumber - 1;
                rdata.size = params.pageSize;
                if ($.trim($('#templateName').val()) != '')
                    rdata['name'] = $('#templateName').val();
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
            var addData = $.extend({}, $('#addForm').serializeJson());
            $.Ajax(obj.urlAdd, addData, function(data) {
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
            $.Ajax(obj.urlEdit, editData, function(data) {
                $mainTable[0].refresh();
                $('#editModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        //删除
        $('#deleteModalBtn').click(function(e) {
            $.Ajax(obj.urlDelete, {
                id: $mainTable[0].getChooseRows().id
            }, function(data) {
                $mainTable[0].refresh();
                $('#deleteModal').modal('hide');
                $.showSucMsg('删除成功！');
            });
        });
        $('#elemntModalBtn').click(function(e) {
            var array = [];
            $('#elementRight option').each(function() {
                array.push($(this).attr('value'));
            })
            var updateData = {
                id: $mainTable[0].getChooseRows().id,
                elmentsIds: array
            }
            $.Ajax(obj.urlTemplateElementUpdate, updateData, function(data) {
                $('#elementModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        $('#leftBtn').click(function(e) {
            if ($('#elementLeft option:selected').length > 0)
                $('#elementLeft option:selected').appendTo('#elementRight');
            else
                $.showErrMsg('请选择可用元素');
        })
        $('#rightBtn').click(function(e) {
            if ($('#elementRight option:selected').length > 0)
                $('#elementRight option:selected').appendTo('#elementLeft');
            else
                $.showErrMsg('请选择已关联元素');
        })
        $.fn.reverse = [].reverse;
        $("#upBtn,#downBtn").click(function() {
            var $opt = $("#elementRight option:selected");
            if (!$opt.length) return;
            var bGoUp = (this.id == "upBtn");
            if (!bGoUp) $opt.reverse();
            $opt.each(function(i) {
                var $src = $(this);
                var $dst = $src[bGoUp ? "prev" : "next"]();
                if ($dst.length && $dst[0].selected) return;
                $dst[bGoUp ? "before" : "after"]($src);
            });
        });
        $('#queryBtn').click(function(e) {
            $mainTable[0].refresh();
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
                case 'elementModal':
                    {
                        clearElementSelect();
                        var selectedRow = $mainTable[0].getChooseRows();
                        $.Ajax(obj.urlTemplateElementQuery, selectedRow, function(data) {
                            var associatedElements = data['associatedElement'];
                            var availableElements = data['availableElement'];
                            for (var i = 0; i < associatedElements.length; ++i) {
                                if (associatedElements[i]['key'] in availableElements)
                                    delete availableElements[associatedElements[i]['key']];
                            }
                            $("#elementRight").fzSelect({ data: associatedElements });
                            $("#elementLeft").fzSelect({ data: availableElements });
                        });
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
                name: "请输入模板名",
                type: "请选择类型"
            }
        };
        addf.fzValidate(vOpt);
        editf.fzValidate(vOpt);
    }
    init();
}($, userObj, dictionary));
