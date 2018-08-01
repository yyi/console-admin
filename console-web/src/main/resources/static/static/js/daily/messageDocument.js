/*
 *信息文档管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj, data, organizationList) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var addf = $('#addForm');
    var editf = $('#editForm');
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
        getType();
        renderFileUp(true);
        renderFileUp(false);
        //事件监听
        eventHandler();
        //验证
        doValidate();
    }
    //获取元素类型
    function getType() {
        $("#type_add").fzSelect({
            data: data.MESSAGE_DOCUMENT_TYPE
        });
        $("#type_edit").fzSelect({
            data: data.MESSAGE_DOCUMENT_TYPE
        });
        $("#type_select").fzSelect({
            data: data.MESSAGE_DOCUMENT_TYPE,
            allflg: true,
            allkv: ['', '全部']
        });
        $("#organizationNo_add").fzSelect({
            data: organizationList,
            valueField: 'organizationNo',
            textField: 'name'
        });
        $("#organizationNo_edit").fzSelect({
            data: organizationList,
            valueField: 'organizationNo',
            textField: 'name'
        });
        $("#organizationNo_select").fzSelect({
            data: organizationList,
            valueField: 'organizationNo',
            textField: 'name',
            allflg: true,
            allkv: ['', '全部']
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
            field: 'subject',
            title: '主题',
            sortable: true
        }, {
            field: 'type',
            title: '类型',
            formatter: function(value, row, index) {
                return data['MESSAGE_DOCUMENT_TYPE'][row['type']];
            },
            sortable: true
        }, {
            field: 'organizationName',
            title: '部门',
            sortable: true
        }, {
            field: 'createStaff',
            title: '创建人',
            sortable: true
        }, {
            field: 'createTime',
            title: '创建时间',
            sortable: true
        }, {
            field: 'organizationNo',
            title: '部门',
            formatter: function(value, row, index) {
                return row.organizationNo;
            },
            visible: false
        }, {
            field: 'filePath',
            title: '附件',
            formatter: function(value, row, index) {
                if(value==undefined || value=="" || value==null){
                    return '';
                }else{
                    var str = obj.urlContext + "/commons/download?filePath=" + value;
                    var path = encodeURI(str);
                    return '<a href="' + path + '">下载</a>';
                }
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
                return $.extend(rdata, $('#queryForm').serializeJson());
            }
        });
    }
    //渲染文件上传
    function renderFileUp(isAdd) {
        var type = $('#type_' + (isAdd ? 'add' : 'edit')).find('option:selected').text();
        var orge = $('#organizationNo_' + (isAdd ? 'add' : 'edit')).find('option:selected').text();
        var fileopt = {
            url: commonsUrl + '/upload',
            upBtn: true,
            width: '85%',
            placeHolder: '上传文件名格式为部门-类型-自定义',
            fileNameCheckRegExp: (orge + '\\-' + type + '\\-'),
            fileNameCheckErrMsg: '上传文件名格式为部门-类型-自定义',
            sucCallback: function() {
                $.showSucMsg('文件上传成功！');
            },
            failCallback: function() {
                $.showErrMsg('文件上传失败！');
            }
        };
        if (isAdd) { $('#file_add').fzFile(fileopt); } else { $('#file_edit').fzFile(fileopt); }
    }
    //页面事件监听
    function eventHandler() {
        //工具按钮弹框前事件
        $('div[role="dialog"][id$="Modal"]').on('show.bs.modal', beforeModalShowHandler);
        //查询
        $('#queryBtn').click(function() {
            $mainTable[0].refresh();
        });
        //新增保存
        $('#addModalBtn').click(function() {
            if (!addf[0].valid()) {
                return;
            }
            if (!$('#file_add')[0].nameValidate()) {
                $.showErrMsg('上传文件名格式为部门-类型-自定义');
                return;
            }
            var addData = $.extend({}, $('#addForm').serializeJson());
            $.Ajax(obj.urlUserAdd, addData, function(data) {
                $mainTable[0].refresh();
                $('#addModal').modal('hide');
                $.showSucMsg('添加成功！');
            });
        });
        //修改保存
        $('#editModalBtn').click(function() {
            if (!editf[0].valid()) {
                return;
            }
            if (!$('#file_edit')[0].nameValidate()) {
                $.showErrMsg('上传文件名格式为部门-类型-自定义');
                return;
            }
            var editData = $.extend({}, $mainTable[0].getChooseRows(), $('#editForm').serializeJson());
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
        $("#type_add").change(function() {
            resetOption('file_add', true);
        });
        $("#organizationNo_add").change(function() {
            resetOption('file_add', true);
        });
        $("#type_edit").change(function() {
            resetOption('file_edit', false);
        });
        $("#organizationNo_edit").change(function() {
            resetOption('file_edit', false);
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
                        $('#file_add')[0].reset();
                        break;
                    }
                case 'editModal':
                    {
                        var row = $mainTable[0].getChooseRows();
                        var formJson = $(e.currentTarget).find('form').serializeJson();
                        for (var prop in formJson) {
                            $('#' + prop + '_edit').val(row[prop]);
                        }
                        $('#file_edit')[0].reset();
                        resetOption('file_edit', false);
                        $('#file_edit')[0].setFilePath(row['filePath']);
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
    //重置文件上传组件
    function resetOption(id, isAdd) {
        var type = $('#type_' + (isAdd ? 'add' : 'edit')).find('option:selected').text();
        var orge = $('#organizationNo_' + (isAdd ? 'add' : 'edit')).find('option:selected').text();
        $('#' + id)[0].setOption({
            fileNameCheckRegExp: (orge + '\\-' + type + '\\-')
        });
    }
    //验证
    function doValidate() {
        addf.fzValidate({
            type: 'modal',
            rules: {
                subject: "required",
                content: "required"
            },
            messages: {
                subject: "请输入主题",
                content: "请输入内容"
            }
        });
        editf.fzValidate({
            type: 'modal',
            rules: {
                subject: "required",
                content: "required"
            },
            messages: {
                subject: "请输入主题",
                content: "请输入内容"
            }
        });
    }
    init();
}($, messageDocumentObj, dictionary, organizationList));
