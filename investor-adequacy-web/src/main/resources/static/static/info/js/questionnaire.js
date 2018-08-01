$(function($, obj) {
    //标题过滤器
    avalon.filters.titleFilter = function(txt) {
        switch (txt) {
            case 'QUESTIONS_SHARES_EVERYMAN_FINANCIAL':
                {
                    return '一、财务状况';
                }
            case 'QUESTIONS_SHARES_EVERYMAN_EXPERIENCE':
                {
                    return '二、投资经验及知识';
                }
            case 'QUESTIONS_SHARES_EVERYMAN_TARGET':
                {
                    return '三、投资目标';
                }
            case 'QUESTIONS_SHARES_EVERYMAN_RISK':
                {
                    return '四、风险偏好';
                }
        }
    };
    //选项过滤器
    avalon.filters.optFilter = function(txt) {
        return String.fromCharCode(64 + parseInt(txt));
    };
    var vm = avalon.define({
        $id: 'qusObj',
        qusKeys: [],
        data: {}
    });
    //初始化
    function init() {
        setSetp(4);
        loadData();
        eventHandler();
    }
    //页面事件监听
    function eventHandler() {
        $('#nextBtn').click(function(e) {
            var answers = getSelectAnswers($('#qusForm').serializeJson());
            if (answers.length < getQusCount()) {
                $.showErrMsg('您还有问题没有填写答案');
                return;
            }
            btnChange('nextBtn', false);
            vm.data.valueDtoMap = {};
            vm.data.questionsDtoMap = {};
            vm.data.operation = obj.formType;
            vm.data.selectAnswers = answers;
            $.Ajax(obj.urlUpdate, vm.data.$model, function(data) {
                window.top.location.href = 'next?businessId=' + data.id + '&operation=' + obj.formType;
            }, {
                error: function(err, status) {
                    btnChange('nextBtn', true);
                    $.ajaxErr(err, status);
                }
            });
        });
        //返回上一步
        $('#frontBtn').click(function(e) {
            window.top.location.href = 'back?businessId=' + obj.businessId + '&operation=' + obj.formType;
        });
    }
    //加载数据
    function loadData() {
        $.Ajax(obj.urlBaseValue + '?businessId=' + obj.businessId, null, function(data) {
            vm.data = data;
            vm.qusKeys = getQusKeys(data.questionsDtoMap);
            avalon.scan(document.body);
            setSelectAnswers(data.selectAnswers || []);
        });
    }
    //获取map属性
    function getQusKeys(data) {
        var props = [];
        for (prop in data) {
            props.push(prop);
        }
        if (obj.formType == 'QUESTIONS_SHARES_EVERYMAN') {
            props = ['QUESTIONS_SHARES_EVERYMAN_FINANCIAL',
                'QUESTIONS_SHARES_EVERYMAN_EXPERIENCE',
                'QUESTIONS_SHARES_EVERYMAN_TARGET',
                'QUESTIONS_SHARES_EVERYMAN_RISK'
            ];
        }
        return props;
    }
    //获取问题数量
    function getQusCount() {
        var count = 0;
        $.each(vm.qusKeys.$model, function(i, k) {
            $.each(vm.data.questionsDtoMap[k], function(j, e) {
                count++;
            });
        });
        return count;
    }
    //获取选中答案
    function getSelectAnswers(form) {
        var arr = [];
        for (prop in form) {
            var val = form[prop];
            arr.push((val instanceof Array) ? val : [val]);
        }
        return arr;
    }
    //设置答案选中
    function setSelectAnswers(data) {
        for (var i = 0; i < data.length; i++) {
            for (var j = 0; j < data[i].length; j++) {
                $('#ans' + data[i][j]).prop('checked', true);
            }
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
}($, qusObj));
