$(function($, obj) {
    var vm = avalon.define({
        $id: 'baseInfo',
        info: {},
        formType: obj.formType,
        ORGANIZATION_TYPE: '', //机构类型
        OCCUPATION: '', //职业
        CREDIT_RECORD_RADIO: '', //诚信记录
        CREDIT_RECORD_VALUE: [],
        IS_ORGANIZATION_TYPE_VALUE: [], //是否符合类型
        comfirmValue: false,
        comfirmChange: function(e) {
            var flg = (window.ActiveXObject || "ActiveXObject" in window || navigator.userAgent.indexOf("Edge") > -1);
            btnChange('nextBtn', flg ? e.currentTarget.checked : vm.comfirmValue);
        }
    });
    var biForm = $('#biForm');
    //初始化
    function init() {
        setSetp(3);
        btnChange('nextBtn', false);
        loadData();
        eventHandler();
    }
    //页面事件监听
    function eventHandler() {
        $('#nextBtn').click(function(e) {
            if (!biForm[0].valid()) return;
            if (!emptyCheck()) return;
            btnChange('nextBtn', false);
            saveData();
            vm.info.questionsDtoMap = {};
            vm.info.selectAnswers = [];
            vm.info.operation = obj.formType;
            $.Ajax(obj.urlUpdate, vm.info.$model, function(data) {
                window.top.location.href = 'next?businessId=' + data.id + '&operation=' + obj.formType;
            }, {
                error: function(err, status) {
                    btnChange('nextBtn', true);
                    $.ajaxErr(err, status);
                }
            });
        });
    }
    //加载数据
    function loadData() {
        $.Ajax(obj.urlBaseValue + '?businessId=' + obj.businessId, null, function(data) {
            vm.info = data;
            showData(data);
            avalon.scan(document.body);
            //验证
            doValidate();
        });
    }
    //还原数据
    function showData(data) {
        var fields = [
            { key: 'ORGANIZATION_TYPE', tip: '其他组织' }, //机构类型
            { key: 'OCCUPATION', tip: '其他' } //职业
        ];
        $.each(fields, function(i, n) {
            if (!$.isEmpty(data.valueDtoMap[n.key])) {
                var o_type = data.valueDtoMap[n.key][0].val || '';
                if (o_type == '') data.valueDtoMap[n.key][0].val = '';
                if (o_type.substr(0, 3) == '>_<') {
                    vm[n.key] = data.valueDtoMap[n.key][0].val;
                    data.valueDtoMap[n.key][0].val = '';
                } else {
                    vm[n.key] = n.tip;
                }
            }
        });
        //是否符合类型
        if (!$.isEmpty(vm.info.valueDtoMap.IS_ORGANIZATION_TYPE)) {
            var i_value = data.valueDtoMap.IS_ORGANIZATION_TYPE[0].val || '';
            vm.IS_ORGANIZATION_TYPE_VALUE = (i_value.length > 0) ? i_value.split('-') : [];
        }
        //诚信记录
        if (!$.isEmpty(data.valueDtoMap.CREDIT_RECORD)) {
            var c_value = data.valueDtoMap.CREDIT_RECORD[0].val || '';
            if (c_value == '') {
                vm.CREDIT_RECORD_RADIO = '';
            } else if (c_value == '无') {
                vm.CREDIT_RECORD_RADIO = c_value;
            } else {
                vm.CREDIT_RECORD_RADIO = '有';
                vm.CREDIT_RECORD_VALUE = (c_value == '有') ? [] : ((c_value.length > 0) ? c_value.split('-') : []);
            }
        }
    }
    //保存数据
    function saveData() {
        var fields = [
            { key: 'ORGANIZATION_TYPE', tip: '其他组织' }, //机构类型
            { key: 'OCCUPATION', tip: '其他' } //职业
        ];
        $.each(fields, function(i, n) {
            if (!$.isEmpty(vm.info.valueDtoMap[n.key])) {
                if (vm[n.key] != n.tip) {
                    vm.info.valueDtoMap[n.key][0].val = vm[n.key];
                }
            }
        });
        //是否符合类型
        if (!$.isEmpty(vm.info.valueDtoMap.IS_ORGANIZATION_TYPE)) {
            var _length = vm.IS_ORGANIZATION_TYPE_VALUE.length;
            vm.info.valueDtoMap.IS_ORGANIZATION_TYPE[0].val = (_length > 0) ? vm.IS_ORGANIZATION_TYPE_VALUE.join('-') : '';
        }
        //诚信记录
        if (!$.isEmpty(vm.info.valueDtoMap.CREDIT_RECORD)) {
            if (vm.CREDIT_RECORD_RADIO == '无') {
                vm.info.valueDtoMap.CREDIT_RECORD[0].val = vm.CREDIT_RECORD_RADIO;
            } else {
                var _length = vm.CREDIT_RECORD_VALUE.length;
                vm.info.valueDtoMap.CREDIT_RECORD[0].val = (_length > 0) ? vm.CREDIT_RECORD_VALUE.join('-') : '';
            }
        }
    }
    //非空提示
    function emptyCheck() {
        var fields = [
            { key: 'ORGANIZATION_TYPE', tip: '其他组织', msg: '请选择或填写机构类型' }, //机构类型
            { key: 'OCCUPATION', tip: '其他', msg: '请选择职业' } //职业
        ];
        for (var i = 0; i < fields.length; i++) {
            var n = fields[i];
            if (!$.isEmpty(vm.info.valueDtoMap[n.key])) {
                if (vm[n.key] == '') {
                    $.showErrMsg(n.msg);
                    return false;
                }
                if (vm[n.key] == n.tip && vm.info.valueDtoMap[n.key][0].val.length == 0) {
                    $.showErrMsg(n.msg);
                    return false;
                }
            }
        }
        //是否符合类型
        if (!$.isEmpty(vm.info.valueDtoMap.IS_ORGANIZATION_TYPE)) {
            if (vm.IS_ORGANIZATION_TYPE_VALUE.length == 0) {
                $.showErrMsg('请选择是否符合类型');
                return false;
            }
        }
        //诚信记录
        if (!$.isEmpty(vm.info.valueDtoMap.CREDIT_RECORD)) {
            if (vm.CREDIT_RECORD_RADIO == '') {
                $.showErrMsg('请选择诚信记录');
                return false;
            }
            if (vm.CREDIT_RECORD_RADIO == '有' && vm.CREDIT_RECORD_VALUE.length == 0) {
                $.showErrMsg('请选择诚信记录');
                return false;
            }
        }
        return true;
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
    //验证
    function doValidate() {
        var opts = null;
        switch (obj.formType) {
            case 'FILL_DEPT_ORGANIZATION_SPECIALTY':
                {
                    opts = {
                        rules: {
                            ORGANIZATION_NAME: 'required',
                            REPRESENTATIVE: 'required',
                            CREATE_TIME: 'required',
                            BOSS: 'required',
                            BENEFICIARY: 'required',
                            ABODE: 'required',
                            ADDRESS: 'required',
                            CREDIT_CODE: 'required',
                            AGENT_NAME: 'required',
                            AGENT_CARD_TYPE: 'required',
                            AGENT_CARD_NUMBER: 'required',
                            AGENT_TELEPHONE: 'required',
                            AGENT_IPHONE: 'required',
                            AGENT_ADDRESS: 'required',
                            POSTAL_CODE: 'required',
                            EMAIL: {
                                required: false,
                                email: true
                            }
                        },
                        messages: {
                            ORGANIZATION_NAME: '请输入产品名称',
                            REPRESENTATIVE: '请输入法定代表人姓名',
                            CREATE_TIME: '请输入开户时间',
                            BOSS: '请输入控股股东或实际控制人',
                            BENEFICIARY: '请输入交易的实际受益人',
                            ABODE: '请输入住所',
                            ADDRESS: '请输入联系地址',
                            CREDIT_CODE: '请输入社会统一信用代码',
                            AGENT_NAME: '请输入经办人姓名',
                            AGENT_CARD_TYPE: '请输入经办人身份证件类型',
                            AGENT_CARD_NUMBER: '请输入经办人身份证件号码',
                            AGENT_TELEPHONE: '请输入经办人电话',
                            AGENT_IPHONE: '请输入经办人手机号码',
                            AGENT_ADDRESS: '请输入经办人联系地址',
                            POSTAL_CODE: '请输入邮政编码'
                        }
                    };
                    break;
                }
            case 'FILL_DEPT_ORGANIZATION_ORDINARY':
                {
                    opts = {
                        rules: {
                            ORGANIZATION_NAME: 'required',
                            BOSS: 'required',
                            ABODE: 'required',
                            SCOPE_OF_OPERATION: 'required',
                            ORGANIZATION_CODE: 'required',
                            TAX_REGISTRATION_CARD_NUMBER: 'required',
                            ORGANIZATION_CARD_TYPE: 'required',
                            ORGANIZATION_CARD_NUMBER: 'required',
                            ORGANIZATION_CARD_DATE: 'required',
                            ORGANIZATION_TELEPHONE: 'required',
                            ORGANIZATION_ADDRESS: 'required',
                            POSTAL_CODE: 'required',
                            REPRESENTATIVE: 'required',
                            REPRESENTATIVE_CARD_TYPE: 'required',
                            REPRESENTATIVE_CARD_NUMBER: 'required',
                            REPRESENTATIVE_CARD_DATE: 'required',
                            AUTHORIZATION: 'required',
                            AUTHORIZATION_CARD_TYPE: 'required',
                            AUTHORIZATION_CARD_NUMBER: 'required',
                            AUTHORIZATION_CARD_DATE: 'required',
                            AUTHORIZATION_TELEPHONE: 'required',
                            AUTHORIZATION_IPHONE: 'required',
                            AUTHORIZATION_ADDRESS: 'required',
                            AUTHORIZATION_POSTAL_CODE: 'required',
                            AUTHORIZATION_EMAIL: {
                                required: true,
                                email: true
                            },
                            INVESTOR_PERSON: 'required',
                            BENEFICIARY: 'required'
                        },
                        messages: {
                            ORGANIZATION_NAME: '请输入机构名称',
                            BOSS: '请输入控股股东或实际控制人',
                            ABODE: '请输入住所地',
                            SCOPE_OF_OPERATION: '请输入经营范围',
                            ORGANIZATION_CODE: '请输入组织机构代码',
                            TAX_REGISTRATION_CARD_NUMBER: '请输入税务登记证号码',
                            ORGANIZATION_CARD_TYPE: '请输入证明该机构依法设立或者可依法开展经营、社会活动的证照类型',
                            ORGANIZATION_CARD_NUMBER: '请输入证明该机构依法设立或者可依法开展经营、社会活动的证照号码',
                            ORGANIZATION_CARD_DATE: '请输入证明该机构依法设立或者可依法开展经营、社会活动的证照有效期限',
                            ORGANIZATION_TELEPHONE: '请输入机构联系电话',
                            ORGANIZATION_ADDRESS: '请输入联系地址',
                            POSTAL_CODE: '请输入邮政编码',
                            REPRESENTATIVE: '请输入法定代表人姓名',
                            REPRESENTATIVE_CARD_TYPE: '请输入法定代表人身份证件类型',
                            REPRESENTATIVE_CARD_NUMBER: '请输入法定代表人身份证件号码',
                            REPRESENTATIVE_CARD_DATE: '请输入法定代表人身份证件有效期',
                            AUTHORIZATION: '请输入授权代表人姓名',
                            AUTHORIZATION_CARD_TYPE: '请输入授权代表人身份证件类型',
                            AUTHORIZATION_CARD_NUMBER: '请输入授权代表人身份证件号码',
                            AUTHORIZATION_CARD_DATE: '请输入授权代表人身份证件有效期',
                            AUTHORIZATION_TELEPHONE: '请输入授权代表人电话',
                            AUTHORIZATION_IPHONE: '请输入授权代表人手机号码',
                            AUTHORIZATION_ADDRESS: '请输入授权代表人联系地址',
                            AUTHORIZATION_POSTAL_CODE: '请输入授权代表人邮政编码',
                            AUTHORIZATION_EMAIL: {
                                required: '请输入授权代表人Email地址'
                            },
                            INVESTOR_PERSON: '请输入实际控制投资者的自然人',
                            BENEFICIARY: '请输入交易的实际受益人'
                        }
                    };
                    break;
                }
            case 'FILL_SHARES_ORGANIZATION':
                {
                    opts = {
                        rules: {
                            ORGANIZATION_NAME: 'required',
                            BOSS: 'required',
                            ABODE: 'required',
                            SCOPE_OF_OPERATION: 'required',
                            ORGANIZATION_CODE: 'required',
                            TAX_REGISTRATION_CARD_NUMBER: 'required',
                            ORGANIZATION_CARD_TYPE: 'required',
                            ORGANIZATION_CARD_NUMBER: 'required',
                            ORGANIZATION_CARD_DATE: 'required',
                            ORGANIZATION_TELEPHONE: 'required',
                            ORGANIZATION_ADDRESS: 'required',
                            POSTAL_CODE: 'required',
                            REPRESENTATIVE: 'required',
                            REPRESENTATIVE_CARD_TYPE: 'required',
                            REPRESENTATIVE_CARD_NUMBER: 'required',
                            REPRESENTATIVE_CARD_DATE: 'required',
                            AUTHORIZATION: 'required',
                            AUTHORIZATION_CARD_TYPE: 'required',
                            AUTHORIZATION_CARD_NUMBER: 'required',
                            AUTHORIZATION_CARD_DATE: 'required',
                            AUTHORIZATION_TELEPHONE: 'required',
                            AUTHORIZATION_IPHONE: 'required',
                            AUTHORIZATION_ADDRESS: 'required',
                            AUTHORIZATION_POSTAL_CODE: 'required',
                            AUTHORIZATION_EMAIL: {
                                required: true,
                                email: true
                            },
                            INVESTOR_PERSON: 'required',
                            BENEFICIARY: 'required'
                        },
                        messages: {
                            ORGANIZATION_NAME: '请输入机构名称',
                            BOSS: '请输入控股股东或实际控制人',
                            ABODE: '请输入住所地',
                            SCOPE_OF_OPERATION: '请输入经营范围',
                            ORGANIZATION_CODE: '请输入组织机构代码',
                            TAX_REGISTRATION_CARD_NUMBER: '请输入税务登记证号码',
                            ORGANIZATION_CARD_TYPE: '请输入证明该机构依法设立或者可依法开展经营、社会活动的证照类型',
                            ORGANIZATION_CARD_NUMBER: '请输入证明该机构依法设立或者可依法开展经营、社会活动的证照号码',
                            ORGANIZATION_CARD_DATE: '请输入证明该机构依法设立或者可依法开展经营、社会活动的证照有效期限',
                            ORGANIZATION_TELEPHONE: '请输入机构联系电话',
                            ORGANIZATION_ADDRESS: '请输入联系地址',
                            POSTAL_CODE: '请输入邮政编码',
                            REPRESENTATIVE: '请输入法定代表人姓名',
                            REPRESENTATIVE_CARD_TYPE: '请输入法定代表人身份证件类型',
                            REPRESENTATIVE_CARD_NUMBER: '请输入法定代表人身份证件号码',
                            REPRESENTATIVE_CARD_DATE: '请输入法定代表人身份证件有效期',
                            AUTHORIZATION: '请输入授权代表人姓名',
                            AUTHORIZATION_CARD_TYPE: '请输入授权代表人身份证件类型',
                            AUTHORIZATION_CARD_NUMBER: '请输入授权代表人身份证件号码',
                            AUTHORIZATION_CARD_DATE: '请输入授权代表人身份证件有效期',
                            AUTHORIZATION_TELEPHONE: '请输入授权代表人电话',
                            AUTHORIZATION_IPHONE: '请输入授权代表人手机号码',
                            AUTHORIZATION_ADDRESS: '请输入授权代表人联系地址',
                            AUTHORIZATION_POSTAL_CODE: '请输入授权代表人邮政编码',
                            AUTHORIZATION_EMAIL: {
                                required: '请输入授权代表人Email地址'
                            },
                            INVESTOR_PERSON: '请输入实际控制投资者的自然人',
                            BENEFICIARY: '请输入交易的实际受益人'
                        }
                    };
                    break;
                }
            case 'FILL_SHARES_EVERYMAN':
                {
                    opts = {
                        rules: {
                            NAME: "required",
                            SEX: 'required',
                            BIRTH_DATE: 'required',
                            NATIONALITY: 'required',
                            INVESTOR_PERSON: 'required',
                            BENEFICIARY: 'required',
                            WORK_UNIT: 'required',
                            post: 'required',
                            CARD_TYPE: 'required',
                            CARD_NUMBER: 'required',
                            CARD_DATE: 'required',
                            TELEPHONE: 'required',
                            IPHONE: 'required',
                            ADDRESS: 'required',
                            POSTAL_CODE: 'required',
                            EMAIL: {
                                required: true,
                                email: true
                            }
                        },
                        messages: {
                            NAME: '请输入姓名',
                            SEX: '请输入性别',
                            BIRTH_DATE: '请输入出生年月',
                            NATIONALITY: '请输入国籍',
                            INVESTOR_PERSON: '请输入实际控制投资者的自然人',
                            BENEFICIARY: '请输入交易的实际受益人',
                            WORK_UNIT: '请输入工作单位',
                            post: '请输入职务',
                            CARD_TYPE: '请输入身份证件类型',
                            CARD_NUMBER: '请输入身份证件号码',
                            CARD_DATE: '请输入身份证件有效期',
                            TELEPHONE: '请输入固定电话',
                            IPHONE: '请输入手机号码',
                            ADDRESS: '请输入联系地址',
                            POSTAL_CODE: '请输入邮政编码',
                            EMAIL: {
                                required: '请输入Email地址'
                            }
                        }
                    };
                    break;
                }
        }
        biForm.fzValidate(opts);
    }
    init();
}($, baseInfoObj));
