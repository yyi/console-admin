$(function($, obj) {
    //状态过滤器
    avalon.filters.statusFilter = function(status) {
        switch (status) {
            case 'COMPLETE':
                {
                    return '待审核';
                }
            case 'UNFINISHED':
                {
                    return '未完成';
                }
            case 'AUDIT_NO':
            {
                return '审核不通过';
            }
            case 'AUDIT_YES':
            {
                return '审核通过';
            }
        }
    };
    //操作过滤器
    avalon.filters.funcFilter = function(o) {
        switch (o.status) {
            case 'AUDIT_YES':
            case 'COMPLETE':
                {
                    return '<a href="detail?businessId=' + o.id + '">查看</a>';
                }

            case 'UNFINISHED':
                {
                    return '<a class="m-r-xs" href="next?businessId=' + o.id + '&operation=">继续</a><a onclick="indexObj.delete(' + o.id + ')">删除</a>';
                }
            case 'AUDIT_NO':
            {
                return '<a class="m-r-xs" href="next?businessId=' + o.id + '&operation=">修改</a><a onclick="indexObj.delete(' + o.id + ')">删除</a>';
            }

        }
    };
    var vm = avalon.define({
        $id: 'indexObj',
        apply: []
    });
    //初始化
    function init() {
        loadData();
        eventHandler();
    }
    //页面事件监听
    function eventHandler() {
        $('#applyBtn').click(function(e) {
            window.top.location.href = 'businessFill';
        });
    }
    obj.delete = function(id) {
        layer.confirm('确定删除吗？', { icon: 5, title: '删除' }, function(index) {
            layer.close(index);
            $.Ajax(obj.urlDelete + '?businessId=' + id, null, function(data) {
                loadData();
            });
        });
    }
    //加载数据
    function loadData() {
        $.Ajax(obj.urlFind, null, function(data) {
            vm.apply = data;
            avalon.scan(document.body);
        });
    }
    init();
}($, indexObj));
