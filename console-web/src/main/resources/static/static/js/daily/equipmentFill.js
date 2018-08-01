$(function($, obj) {
    var vm = avalon.define({
        $id: 'equipmentFill',
        saveflg: false,
        seatCountFlg: 0,
        hideinternetLine: 'tab-2',
        showbusinessPhone: 'tab-2',
        ef: {},
        showTitle: function(a, b, c) {
            var o = a[b];
            if (o) {
                return o[c];
            }
            return '';
        },
        saveFunc: function() {},
        inputInt: function(e) {
            $(e.currentTarget).val($(e.currentTarget).val().replace(/\D/g, ''));
        },
        seatCountChange: function(e) {
            if (e.currentTarget.value == 0)
                this.ef.businessPhoneItemDto.seatCount = 0;
        }
    });
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var eff = $('#efForm');
    //初始化
    function init() {
        avalonInit();
        eventHandler();
    }
    //整合Avalon
    function avalonInit() {
        $.Ajax(obj.urlEuipmentFillInit, {
            taskExecutionId: obj.taskExecutionId,
            operation: obj.operation
        }, function(data) {
            vm.ef = $.clearObj(data);
            avalon.scan(document.body);
            //默认显示第一个Tab页
            $('#tab a:first').tab('show');
            //处理电话委托数量
            if (vm.ef.businessPhoneItemDto.seatCount > 0)
                vm.seatCountFlg = 1;
            laydateInit(data);
            //验证
            doValidate();
        });
    }
    //页面日期初始化
    function laydateInit(data) {
        var array = [
            //行情接收
            ['businessAcceptItemDto',
                'businessAcceptDtos', [
                    'receiverDate',
                    'gatewayDate',
                    'extranetDate',
                    'intranetDate'
                ]
            ],
            //行情软件
            ['businessSoftwareItemDto',
                'businessSoftwareDtos', [
                    'usingTime'
                ]
            ],
            //主服务器
            ['businessQuotesServerItemDto',
                'businessQuotesServerDtos', [
                    'purchaseTime'
                ]
            ],
            //电话委托
            ['businessPhoneItemDto',
                'businessPhoneDtos', [
                    'purchaseTime'
                ]
            ],
            //其他设备
            ['businessOtherEquipmentItemDto',
                'businessOtherEquipmentDtos', [
                    'purchaseTime'
                ]
            ]
        ];
        for (var i = 0; i < array.length; i++) {
            var arr = array[i];
            var obj = data[arr[0]];
            if (obj) {
                var dto = obj[arr[1]];
                if (dto) {
                    for (var j = 0; j < dto.length; j++) {
                        for (var k = 0; k < arr[2].length; k++) {
                            laydate({ elem: '#ld' + arr[0] + arr[2][k] + j, istime: true, format: 'YYYY-MM-DD hh:mm:ss' });
                        }
                    }
                }
            }
        }
    }
    //页面事件监听
    function eventHandler() {
        $('#tab').on('shown.bs.tab', function(e) {
            var tabid = $(e.target).attr('href').slice(1);
            vm.hideinternetLine = tabid;
            vm.showbusinessPhone = tabid;
            //切换tap时隐藏tips信息
            layer.closeAll('tips');
        });
        vm.saveFunc = function() {
            if (!eff[0].valid()) return;
            $.Ajax(obj.urlEuipmentFillSave, JSON.parse(JSON.stringify(saveHandler(vm.ef.$model))), function(data) {
                $.showSucMsg('保存成功！', {
                    yes: function() {
                        $.closeTab(window.top);
                    }
                });
            });
        };
    }
    //处理空对象
    function saveHandler(obj) {
        for (var i in obj) {
            if (i.substr(-3) == 'Dto' && obj[i] === '')
                obj[i] = {};
        }
        return obj;
    }
    //验证
    function doValidate() {
        eff.fzValidate();
    }
    init();
}($, equipmentFillObj));
