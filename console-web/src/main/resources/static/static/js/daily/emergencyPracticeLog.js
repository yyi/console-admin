/*
 *用户管理页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj,data) {
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var tHeight = $(window).height() - ($('#u_header').outerHeight(true) + $('#u_toolbar').outerHeight(true));
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();

        //事件监听
        eventHandler();

        var isExists = $("#isExists_edit").val();


        changeIsExists();
    }

    function changeIsExists() {
        $("#isExists_edit").change(function () {

            var isExists = $("#isExists_edit").val();

            if(isExists==0){

                $("div[name='isExistsflag']").hide();
            }else{
                $("div[name='isExistsflag']").show();
            }
        });
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
            field: 'element.name',
            title: '项目名'
        },{
            field: 'practiceContent',
            title: '演练内容',
        },{
            field: 'practiceClaim',
            title: '演练要求',
        } , {
            field: 'rpacticeResult',
            title: '演练结果'
        }, {
            field: 'recoveryState',
            title: '恢复情况'
        }];
        $mainTable.bootstrapTable('destroy').bootstrapTable({
            striped: true,
            url: obj.urlQuery,
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

        //修改保存
        $('#editModalBtn').click(function() {

            //alert("testEdit");
            var editData = $('#editForm').serializeJson();
            $.Ajax(obj.urlEdit, editData, function(data) {
                $mainTable.bootstrapTable('refresh');
                $('#editModal').modal('hide');
                $.showSucMsg('修改成功！');
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
                    $("#elementName_edit").val(row.element.name);
                    for (var prop in formJson) {
                    	//alter("type:"+prop+"，value:"+row[prop]);
                        $("#" + prop + '_edit').val(row[prop]);
                    }

                    var isExists = $("#isExists_edit").val();

                    if(isExists==0){

                        $("div[name='isExistsflag']").hide();
                    }else{
                        $("div[name='isExistsflag']").show();
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
}($, emergencyPracticeLogObj));
