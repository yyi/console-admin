/*
 *用户管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj, data) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var $orgaTree = $('#orgaTree');
    var addf = $('#addForm');
    var editf = $('#editForm');
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
        //下拉列表
        getElementType();
        //事件监听
        eventHandler();
        //验证
        doValidate();
    }
    //下拉列表
    function getElementType() {
        $("#busiType_add").fzSelect({
            data: data.LARGE_ELEMENT_TYPE
        });
        $("#busiType_edit").fzSelect({
            data: data.LARGE_ELEMENT_TYPE
        });
        $("#busiType_query").fzSelect({
            data: data.LARGE_ELEMENT_TYPE,
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
            field: 'name',
            title: '任务名称',
            sortable: true
        }, {
            field: 'busiType',
            title: '业务类型',
            sortable: true,
            formatter: function(value, row, index) {
                return data['LARGE_ELEMENT_TYPE'][row['busiType']];
            }
        }, {
            field: 'type',
            title: '任务类型',
            sortable: true,
            formatter: function(value, row, index) {
                switch (value) {
                    case 'TEMP':
                        return '临时任务';
                        break;
                    case 'LONGTIME':
                        return '长期任务';
                        break;
                    case 'AUTOMATIC':
                        return '自动任务';
                        break;
                }
            }
        }, {
            field: 'startDate',
            title: '开始时间',
            sortable: true
        }, {
            field: 'endDate',
            title: '结束时间',
            sortable: true
        }, {
            field: 'status',
            title: '状态',
            sortable: true,
            formatter: function(value, row, index) {
                switch (value) {
                    case 'AVAILIABLE':
                        return '已启动';
                        break;
                    case 'DISABLE':
                        return '未启动';
                        break;
                }
            }
        }, {
            field: 'remark',
            title: '备注'
        }];
        $mainTable.fzTable({
            url: obj.urlTaskQuery,
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
            $.Ajax(obj.urlTaskAdd, addData, function(data) {
                $mainTable[0].refresh();
                $('#addModal').modal('hide');
                $.showSucMsg('添加成功！');
            });
        });
        //修改保存
        $('#editModalBtn').click(function() {
            var statusValueForEdit = $mainTable[0].getChooseRows().status;
            if (statusValueForEdit == "AVAILIABLE") {
                $.showErrMsg('任务已经启动，无法修改');
                return;
            }
            if (!editf[0].valid()) {
                //启用按钮
                $('button[id$="ModalBtn"]').prop("disabled", false);
                return;
            }
            var editData = $('#editForm').serializeJson();
            $.Ajax(obj.urlTaskEdit, editData, function(data) {
                $mainTable[0].refresh();
                $('#editModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        //启动
        $('#actionModalBtn').click(function(e) {
            var statusValue = $mainTable[0].getChooseRows().status;
            if (statusValue == "AVAILIABLE") {
                $.showErrMsg('任务已经启动，无法执行启动');
                return;
            }
            $.Ajax(obj.urlTaskAction, {
                id: $mainTable[0].getChooseRows().id
            }, function(data) {
                $mainTable[0].refresh();
                $('#actionModal').modal('hide');
                $.showSucMsg('任务已启动');
            });
        });
        //停止
        $('#disableModalBtn').click(function(e) {
            var statusValue = $mainTable[0].getChooseRows().status;
            if (statusValue == "DISABLE") {
                $.showErrMsg('任务不是启动状态，无法执行停止');
                return;
            }
            $.Ajax(obj.urlTaskDisable, {
                id: $mainTable[0].getChooseRows().id
            }, function(data) {
                $mainTable[0].refresh();
                $('#disableModal').modal('hide');
                $.showSucMsg('任务已停止');
            });
        });
        //删除
        $('#deleteModalBtn').click(function(e) {
            var statusValueForDelete = $mainTable[0].getChooseRows().status;
            if (statusValueForDelete == "AVAILIABLE") {
                $.showErrMsg('任务已经启动，无法删除');
                return;
            }
            $.Ajax(obj.urlTaskDelete, {
                id: $mainTable[0].getChooseRows().id
            }, function(data) {
                $mainTable[0].refresh();
                $('#deleteModal').modal('hide');
                $.showSucMsg('删除成功！');
            });
        });
        //新增时类型变化
        $('#type_add').change(function(e) {
            var typeValue = $(e.target).val();
            if (typeValue == 'TEMP') {
                $('#endDateAdddiv').hide();
                $('#endDate_add').value = "";
                $('#endDate_add').prop("disabled", true);
            } else {
                $('#endDateAdddiv').show();
                $('#endDate_add').prop("disabled", false);
            }
        });
        //修改时类型变化
        $('#type_edit').change(function(e) {
            var typeValue = $(e.target).val();
            if (typeValue == 'TEMP') {
                $('#endDateEditdiv').hide();
                $('#endDate_edit').value = "";
                $('#endDate_edit').prop("disabled", true);
            } else {
                $('#endDateEditdiv').show();
                $('#endDate_edit').prop("disabled", false);
            }
        });
        //配置模板
        $('#templateModalBtn').click(function(e) {
            var array = [];
            $('#templateRight option').each(function() {
                array.push($(this).attr('value'));
            });
            var updateData = { id: $mainTable[0].getChooseRows()["id"], templateIds: array };
            $.Ajax(obj.urlTaskTemplateUpdate, updateData, function(data) {
                $('#templateModal').modal('hide');
                $.showSucMsg('配置成功！');
            });
        });
        //配置模板
        $('#organizationModalBtn').click(function(e) {
            var array = [];
            $('#organizationRight option').each(function() {
                array.push($(this).attr('value'));
            });
            var updateData = { id: $mainTable[0].getChooseRows()["id"], organizationNos: array };
            $.Ajax(obj.urlTaskOrganizationUpdate, updateData, function(data) {
                $('#organizationModal').modal('hide');
                $.showSucMsg('分配成功！');
            });
        });
        //配置部门
        $('#orgaModalBtn').click(function(e) {
            var array = $orgaTree[0].getDataIdsFromCheckedNodes();
            if (array.length == 0) {
                $.showSucMsg('未勾选任何部门！');
                $(e.currentTarget).prop('disabled', false);
                return;
            }
            var updateData = {
                id: $mainTable[0].getChooseRows()["id"],
                organizationNos: array
            };
            $.Ajax(obj.urlTaskOrganizationUpdate, updateData, function(data) {
                $('#orgaModal').modal('hide');
                $.showSucMsg('分配成功！');
            });
        });
        $('#leftBtn').click(function(e) {
            if ($('#templateLeft option:selected').length > 0)
                $('#templateLeft option:selected').appendTo('#templateRight');
            else
                $.showErrMsg('请选择可用模板');
        })
        $('#rightBtn').click(function(e) {
            if ($('#templateRight option:selected').length > 0)
                $('#templateRight option:selected').appendTo('#templateLeft');
            else
                $.showErrMsg('请选择已关联模板');
        })
        $('#leftOrganizationBtn').click(function(e) {
            if ($('#organizationLeft option:selected').length > 0)
                $('#organizationLeft option:selected').appendTo('#organizationRight');
            else
                $.showErrMsg('请选择可分配部门');
        })
        $('#rightOrganizationBtn').click(function(e) {
            if ($('#organizationRight option:selected').length > 0)
                $('#organizationRight option:selected').appendTo('#organizationLeft');
            else
                $.showErrMsg('请选择已分配部门');
        })
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable[0].resetView(getTHeight());
        });
    }
    //清空下拉列表
    function clearTemplateSelect() {
        $("#templateLeft").empty();
        $("#templateRight").empty();

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
                case 'actionModal':
                    {
                        var statusValue = $mainTable[0].getChooseRows().status;
                        if (statusValue == "AVAILIABLE") {
                            $.showErrMsg('任务已经启动，无法执行启动');
                            return false;
                        }
                        break;
                    }
                case 'disableModal':
                    {
                        var statusValue = $mainTable[0].getChooseRows().status;
                        if (statusValue == "DISABLE") {
                            $.showErrMsg('任务不是启动状态，无法执行停止');
                            return false;
                        }
                        break;
                    }
                case 'editModal':
                    {
                        var statusValue = $mainTable[0].getChooseRows().status;
                        if (statusValue == "AVAILIABLE") {
                            $.showErrMsg('任务已经启动，无法修改');
                            return false;
                        } else {
                            var row = $mainTable[0].getChooseRows();
                            var formJson = $('#editForm').serializeJson();
                            $('#endDate_edit').prop("disabled", false);
                            for (var prop in formJson) {
                                $("#" + prop + '_edit').val(row[prop]);
                                if (prop == "type") {
                                    if ($('#type_edit').val() == "LONGTIME") {
                                        $('#endDateEditdiv').show();
                                    } else {
                                        $('#endDateEditdiv').hide();
                                        $('#endDate_edit').prop("disabled", true);
                                    }
                                }
                            }
                            break;
                        }
                        break;
                    }
                case 'deleteModal':
                    {
                        var statusValue = $mainTable[0].getChooseRows().status;
                        if (statusValue == "AVAILIABLE") {
                            $.showErrMsg('任务已经启动，无法删除');
                            return false;
                        }
                        break;
                    }
                case 'templateModal':
                    {
                        clearTemplateSelect();
                        var selectedRow = $mainTable[0].getChooseRows();
                        $.Ajax(obj.urlTaskTemplateQuery, selectedRow, function(data) {
                            var associatedTemplate = data['associatedTemplate'];
                            var availableTemplate = data['availableTemplate'];
                            for (var i = 0; i < associatedTemplate.length; ++i) {
                                if (associatedTemplate[i]['key'] in availableTemplate)
                                    delete availableTemplate[associatedTemplate[i]['key']];
                            }
                            $("#templateRight").fzSelect({ data: associatedTemplate });
                            $("#templateLeft").fzSelect({ data: availableTemplate });
                        });
                        break;
                    }
                case 'organizationModal':
                    {
                        clearOrganizationSelect();
                        var selectedRow = $mainTable[0].getChooseRows();
                        $.Ajax(obj.urlTaskOrganizationQuery, selectedRow, function(data) {
                            var associatedOrganization = data['associatedOrganization'];
                            var availableOrganization = data['availableOrganization'];
                            for (var i = 0; i < associatedOrganization.length; ++i) {
                                if (associatedOrganization[i]['key'] in availableOrganization)
                                    delete availableOrganization[associatedOrganization[i]['key']];
                            }
                            $("#organizationRight").fzSelect({ data: associatedOrganization });
                            $("#organizationLeft").fzSelect({ data: availableOrganization });
                        });
                        break;
                    }
                case 'orgaModal':
                    {
                        $('#treeLoadTip').html($.tips.loadTip).show();
                        $orgaTree.hide();
                        var selectedRow = $mainTable[0].getChooseRows();
                        $.Ajax(obj.urlTaskOrganizationQuery, selectedRow, function(data) {
                            //渲染部门树
                            $orgaTree.fzTree({
                                levels: 3,
                                treeData: data['organizationDto'],
                                keys: ['organizationNo', 'name', 'subOrganization'],
                                textFormatter: function(node, val, index) {
                                    return node[index]['organizationNo'] + '-' + node[index]['name'];
                                },
                                checkedLinkParents: false,
                                maxHeight: 400
                            });
                            $orgaTree[0].setCheckedNodesByIds($.map(data['associatedOrganization'], function(n) {
                                return n.key;
                            }));
                            $('#treeLoadTip').hide();
                            $orgaTree.show();
                        });
                        break;
                    }
            }
            return true;
        }
    }
    //清空下拉列表
    function clearOrganizationSelect() {
        $("#organizationLeft").empty();
        $("#organizationRight").empty();
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
                type: "required",
                startDate: "required",
                endDate: "required",
                busiType: "required"
            },
            messages: {
                name: "请输入任务名",
                type: "请选择任务类型",
                startDate: "请输入开始时间",
                endDate: "请输入结束时间",
                busiType: "选择业务类型"
            }
        };
        addf.fzValidate(vOpt);
        editf.fzValidate(vOpt);
    }
    init();
}($, taskObj, dictionary));
