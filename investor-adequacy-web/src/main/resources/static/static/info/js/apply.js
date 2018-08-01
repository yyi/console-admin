$(function($, obj) {
    var vm = avalon.define({
        $id: 'apply',
        three: {
            a: '',
            b: obj.userType == true ? 'person' : 'orga', //person //orga
            c: ''
        },
        stepNum: 1,
        frontBtnFlg: true,
        nextBtnFlg: true,
        comfirmFlg: false,
        comfirmValue: false,
        stockorgaChange: function(e) {
            btnChange('nextBtn', vm.three.v != '');
        },
        stockpersonChange: function(e) {
            btnChange('nextBtn', vm.three.v != '');
        },
        comfirmChange: function(e) {
            var flg = (window.ActiveXObject || "ActiveXObject" in window || navigator.userAgent.indexOf("Edge") > -1);
            btnChange('comfirmBtn', flg ? e.currentTarget.checked : vm.comfirmValue);
        }
    });
    //初始化
    function init() {
        setSetp(vm.stepNum);
        //如果用户是自然人则债券不可用
        if (obj.userType == true) {
            btnChange('bondBtn', false);
        }
        btnChange('nextBtn', false);
        btnChange('comfirmBtn', false);
        eventHandler();
    }
    //页面事件监听
    function eventHandler() {
        $('button[id$="Btn"]').click(function(e) {
            switch (e.currentTarget.id) {
                case 'stockBtn':
                    {
                        vm.three.a = 'stock';
                        vm.three.c = '';
                        btnChange('nextBtn', false);
                        setSetp(++vm.stepNum);
                        break;
                    }
                case 'bondBtn':
                    {
                        vm.three.a = 'bond';
                        vm.nextBtnFlg = false;
                        //如果用户是机构则普通投资者不可用
                        btnChange('commonBtn', false);
                        setSetp(++vm.stepNum);
                        break;
                    }
                case 'profeBtn':
                    {
                        vm.three.c = 'profe';
                        vm.frontBtnFlg = false;
                        vm.nextBtnFlg = false;
                        vm.comfirmFlg = true;
                        btnChange('comfirmBtn', false);
                        break;
                    }
                case 'commonBtn':
                    {
                        vm.three.c = 'common';
                        doSubmit();
                        break;
                    }
                case 'nextBtn':
                    {
                        //告知对话框显示逻辑
                        if (vm.three.a == 'stock' && ((vm.three.b == 'orga' && vm.three.c != 'orgaopt5') || (vm.three.b == 'person' && vm.three.c != 'personopt4'))) {
                            vm.comfirmFlg = true;
                        } else {
                            doSubmit();
                            break;
                        }
                        vm.frontBtnFlg = false;
                        vm.nextBtnFlg = false;
                        vm.comfirmValue = false;
                        btnChange('comfirmBtn', false);
                        break;
                    }
                case 'backBtn':
                    {
                        vm.comfirmFlg = false;
                        vm.frontBtnFlg = true;
                        vm.nextBtnFlg = (vm.three.a == 'bond' && vm.three.b == 'orga') ? false : true;
                        vm.comfirmValue = false;
                        btnChange('comfirmBtn', false);
                        break;
                    }
                case 'comfirmBtn':
                    {
                        doSubmit();
                        break;
                    }
                case 'frontBtn':
                    {
                        //如果从第二步返回
                        if (vm.stepNum == 2)
                            vm.nextBtnFlg = true;
                        setSetp(--vm.stepNum);
                        break;
                    }
            }
        });
    }
    //提交
    function doSubmit() {
        submitHandler(false);
        $.Ajax(obj.urlCreate, {
            serviceType: vm.three.a == 'stock' ? 'SHARES' : 'DEPT',
            investorType: getInvestorType()
        }, function(data) {
            window.top.location.href = 'next?businessId=' + data.id + '&operation=';
        }, {
            error: function(err, status) {
                submitHandler(true);
                $.ajaxErr(err, status);
            }
        });
    }
    //提交前处理
    function submitHandler(flg) {
        if (vm.three.a == 'stock') {
            $('#comfirmchk').prop('disabled', !flg);
            btnChange('backBtn', flg);
            btnChange('comfirmBtn', flg);
        } else {
            //如果是债券
            if (vm.three.c == 'profe') {
                btnChange('profeBtn', flg);
            } else {
                btnChange('commonBtn', flg);
            }
        }
    }
    //获取投资者类型
    function getInvestorType() {
        if (vm.three.a == 'stock') {
            //如果是股票
            if (vm.three.b == 'person') {
                var num = vm.three.c.substr(-1);
                return num < 4 ? 'SHARES_SPECIALTY' : 'SHARES_ORDINARY';
            } else {
                var num = vm.three.c.substr(-1);
                return num < 5 ? (num < 4 ? 'SHARES_A_SPECIALTY' : 'SHARES_B_SPECIALTY') : 'SHARES_ORDINARY';
            }
        } else {
            //如果是债券
            return (vm.three.c == 'profe') ? 'DEPT_SPECIALTY' : 'DEPT_ORDINARY';
        }
    }
    //按钮可用
    function btnChange(id, flg) {
        if (flg) {
            $('#' + id).removeClass('btngray');
            $('#' + id).prop('disabled', false);
        } else {
            $('#' + id).addClass('btngray');
            $('#' + id).prop('disabled', true);
        }
    }
    //导航设置第几步
    function setSetp(num) {
        var _items = $('.c-navbox').find('.nav-item');
        for (var i = 0; i < _items.length; i++) {
            $(_items[i]).removeClass('item-red');
            $(_items[i]).removeClass('item-front');
        }
        if (num > 1) {
            $(_items[num - 2]).addClass('item-front');
        }
        $(_items[num - 1]).addClass('item-red');
    }
    init();
}($, applyObj));
