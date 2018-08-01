/*
 *用户管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');

    var tHeight = $(window).height() - ($('#u_header').outerHeight(true) + $('#u_toolbar').outerHeight(true));
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
        //事件监听
        eventHandler();
    }
    //渲染表格
    function renderDatagrid() {
        var columns = [{
            field: 'checked',
            title: 'checked',
            checkbox: true
        }, {
            field: 'id',
            title: 'id',
            visible: false
        }, {
            field: 'name',
            title: '任务名称'
        }, {
            field: 'busiType',
            title: '业务类型'
        }, {
            field: 'type',
            title: '任务类型'
        }, {
            field: 'startDate',
            title: '开始时间',
        }, {
            field: 'endDate',
            title: '结束时间',
        }, {
            field: 'remark',
            title: '备注'
        }];
        $mainTable.bootstrapTable('destroy').bootstrapTable({
            striped: true,
            url: obj.urlTaskQuery,
            dataType: 'json',
            queryParamsType: '',
            pagination: true,
            sidePagination: 'server',
            pageSize: 10,
            pageList: [10, 25, 50, 100, 'All'],
            selectItemName: 'checked',
            idField: 'id',
            columns: columns,
            height: tHeight,
            responseHandler: function(res) {
                //数据处理
                var rdata = {};
                rdata.total = res.totalElements;
                rdata.rows = res.content;
                return rdata;
            },
            queryParams: function(params) {
                //请求参数处理
                var rdata = {};
                rdata.page = params.pageNumber - 1;
                rdata.size = params.pageSize;
                return $.extend(rdata,$('#queryForm').serializeJson());
            }
        });
    }
    //页面事件监听
    function eventHandler() {
        //工具按钮弹框前事件
        $('#editModal').on('show.bs.modal', beforeModalShowHandler);
        $('#deleteModal').on('show.bs.modal', beforeModalShowHandler);
        //查询
        $('#queryBtn').click(function() {
            $mainTable.bootstrapTable('refresh');
        });
        //新增保存
        $('#addModalBtn').click(function() {
            //alert("testAdd");
            var addData = $('#addForm').serializeJson();
            $.Ajax(obj.urlElementAdd, addData, function(data) {
                $mainTable.bootstrapTable('refresh');
                $('#addModal').modal('hide');
                $.showSucMsg('添加成功！');
            });
        });
        //修改保存
        $('#editModalBtn').click(function() {
            //alert("testEdit");
            var editData = $('#editForm').serializeJson();
            $.Ajax(obj.urlElementEdit, editData, function(data) {
                $mainTable.bootstrapTable('refresh');
                $('#editModal').modal('hide');
                $.showSucMsg('修改成功！');
            });
        });
        //删除
        $('#deleteModalBtn').click(function(e) {
            $.Ajax(obj.urlElementDelete, {
                id: getChooseRows($mainTable).id
            }, function(data) {
                $mainTable.bootstrapTable('refresh');
                $('#deleteModal').modal('hide');
                $.showSucMsg('删除成功！');
            });
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable.bootstrapTable('resetView', {
                height: tHeight
            });
        });
    }
    //显示弹框前的事件处理
    function beforeModalShowHandler(e) {
        if (!isChooseOneRow($mainTable)) {
            $.showErrMsg('请选择一条记录！');
            return false;
        } else {
            switch (e.currentTarget.id) {
                case 'editModal':
                {
                    var row = getChooseRows($mainTable);
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
    //获取table选中行的数据数组，如果只有一行数据则直接返回数据
    function getChooseRows(table) {
        var tableobj = (typeof table === 'string') ? $('#' + table) : table;
        var rows = tableobj.bootstrapTable('getSelections');
        return (rows.length == 1) ? rows[0] : rows;
    }
    //判断table表格是否选择了一条记录
    function isChooseOneRow(table) {
        var tableobj = (typeof table === 'string') ? $('#' + table) : table;
        return (tableobj.bootstrapTable('getSelections').length != 1) ? false : true;
    }
    init();
}($, taskObj));
