/*
 *管理岗查询页面的js
 *@Author wx 2017-7-10
 */
$(function($, obj, data) {
    //入口初始化方法
    var $mainTable = $('#managerialPositionGrid');
    var addf = $('#addForm');
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
            field: 'username',
            title: '登录名'
        }, {
            field: 'oaName',
            title: 'OA账户'
        }, {
            field: 'email',
            title: '邮箱'
        }, {
            field: 'name',
            title: '姓名'
        }, {
            field: 'station',
            title: '岗位'
        }, {
            field: 'mobileNo',
            title: '电话'
        }, {
            field: 'landlineNo',
            title: '座机'
        }, {
            field: 'inDate',
            title: '到岗时间'
        }, {
            field: 'outDate',
            title: '离岗时间'
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
    //页面事件监听
    function eventHandler() {
        //工具按钮弹框前事件
        $('#deleteModal').on('show.bs.modal', beforeModalShowHandler);
        $('div[role="dialog"][id$="Modal"]').on('hide.bs.modal', function() {
            //当关闭弹框时启用所有按钮
            $('button[id$="ModalBtn"]').prop("disabled", false);
            //表单验证重置
            addf[0].reset();
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
                $.showSucMsg("新增成功");
            });
        });
        //删除
        $('#deleteModalBtn').click(function(e) {
            $.Ajax(obj.urlElementEdit, {
                id: $mainTable[0].getChooseRows().id
            }, function(data) {
                $mainTable[0].refresh();
                $('#deleteModal').modal('hide');
                $.showSucMsg('离岗成功！');
            });
        });
        $('#export_btn').click(function() {
            var username = $('#username').val();
            var oaName = $('#oaName').val();
            var name = $('#name').val();
            var station = $('#station').val();
            $(this).attr('href', obj.urlElementExport + '/?username=' + username + '&oaName=' + oaName + '&name=' + name + '&station=' + station);
        });
        //页面大小变化重绘表格
        $(window).resize(function() {
            $mainTable[0].resetView(getTHeight());
        });
    }
    //显示弹框前的事件处理
    function beforeModalShowHandler(e) {
        if (!$mainTable[0].isChooseOneRow()) {
            $.showErrMsg('请选择一条记录！');
            return false;
        } else {
            return true;
        }
    }
    //验证
    function doValidate() {
        addf.fzValidate({
            type: 'modal',
            rules: {
                username: "required",
                oaName: "required",
                name: "required"
            },
            messages: {
                username: "请输入登录名",
                oaName: "请输入OA账户",
                name: "请输入姓名"
            }
        });
    }
    //获取表格高度
    function getTHeight() {
        return $(window).height() - ($('#u_header').outerHeight(true) + $('#u_toolbar').outerHeight(true));
    }
    init();
}($, elementObj, dictionary));
