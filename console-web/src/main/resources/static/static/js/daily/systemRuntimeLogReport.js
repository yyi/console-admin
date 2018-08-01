/*
 *用户管理页面的js
 *@Author wx 2017-7-10
 */
$(function ($, obj, data,organizationList) {
    //入口初始化方法
    var $mainTable = $('#systemRuntimeLogReportGrid');
    //初始化
    function init() {
        //渲染表格
        renderDatagrid();
        $("#organizationNo_select").fzSelect({
            data: organizationList,
            valueField: 'organizationNo',
            textField: 'name',
            allflg: true,
            allkv: ['', '全部']
        });
        //事件监听
        eventHandler();
    }

    //渲染表格
    function renderDatagrid() {
        var columns = [
            [
                {
                    field: 'organizationName',
                    title: '填报部门',
                    valign: "middle",
                    align: "center",
                    colspan: 1,
                    rowspan: 2
                },
                {
                    field: 'inputDate',
                    title: '填报时间',
                    valign: "middle",
                    align: "center",
                    colspan: 1,
                    rowspan: 2
                },
                {
                    title: "信息系统故障",
                    valign: "middle",
                    align: "center",
                    colspan: 2,
                    rowspan: 1
                },
                {
                    title: "业务问题与需求",
                    valign: "middle",
                    align: "center",
                    colspan: 2,
                    rowspan: 1
                },
                {
                    title: "二级营业部系统故障",
                    valign: "middle",
                    align: "center",
                    colspan: 2,
                    rowspan: 1
                },
                {
                    title: "其他问题",
                    valign: "middle",
                    align: "center",
                    colspan: 2,
                    rowspan: 1
                }
            ],
            [
                {
                    field: 'salesDeptFault',
                    title: '问题填报'
                },
                {
                    field: 'centerOpinionForFault',
                    title: '中心意见'
                },
                {
                    field: 'salesDeptRequest',
                    title: '问题填报'
                },
                {
                    field: 'requestCenterOpinion',
                    title: '中心意见'
                },
                {
                    field: 'secondDeptFault',
                    title: '问题填报'
                },
                {
                    field: 'centerOpinionSecdFault',
                    title: '中心意见'
                },
                {
                    field: 'secondDeptOther',
                    title: '问题填报'
                },
                {
                    field: 'centerOpinionSecdOther',
                    title: '中心意见'
                }
            ]
        ];
        $mainTable.bootstrapTable('destroy').bootstrapTable({
            striped: true,
            url: obj.urlElementQuery,
            dataType: 'json',
            queryParamsType: '',
            pagination: true,
            sidePagination: 'server',
            pageSize: 10,
            pageList: [10, 25, 50, 100, 'All'],
            selectItemName: 'checked',
            columns: columns,
            //height: tHeight,
            responseHandler: function (res) {
                //数据处理
                var rdata = {};
                rdata.total = res.totalElements;
                rdata.rows = res.content;
                return rdata;
            },
            queryParams: function (params) {
                //请求参数处理
                var rdata = {};
                rdata.page = params.pageNumber - 1;
                rdata.size = params.pageSize;
                return $.extend(rdata, $('#queryForm').serializeJson());
            },
            onLoadError: function (status) {
                $mainTable.bootstrapTable('removeAll');
                $.showErrMsg("当前用户没有权限查看这个部门");
            }
        });
    }

    //页面事件监听
    function eventHandler() {
        //查询
        $('#queryBtn').click(function () {
            $mainTable.bootstrapTable('refresh');
        });

        $('#export_btn').click(function () {
            var organizationNo = $('#organizationNo_select').val();
            var inputDate = $('#inputDate').val();
            $(this).attr('href', obj.urlContext + '/export?organizationNo=' + organizationNo + '&inputDate=' + inputDate);
        });
    }

    init();
}($, elementObj, dictionary, organizationList));
