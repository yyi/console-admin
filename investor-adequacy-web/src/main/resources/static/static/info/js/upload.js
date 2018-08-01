$(function($, obj) {
    var vm = avalon.define({
        $id: 'uploadObj',
        info: {},
        fileType: obj.fileType
    });
    var cropObj = null;
    //初始化
    function init() {
        //设置导航条显示
        setNav();
        loadData();
        eventHandler();
    }
    //页面事件监听
    function eventHandler() {
        $('#picCropModal').on('hide.bs.modal', function() {
            //当关闭弹框时销毁裁剪控件
            if (cropObj != null) {
                $('#' + cropObj.id)[0].destroy();
                cropObj = null;
            }
        });
        //提交
        $('#nextBtn').click(function(e) {
            //上传前检查
            if (!beforeSubmit()) return;
            layer.confirm('一旦提交将无法再更改！确定提交吗？', { icon: 5, title: '确定' }, function(index) {
                layer.close(index);
                //获取文件裁剪组件中数据到vm
                saveData();
                //执行更新
                btnChange('nextBtn', false);
                vm.info.questionsDtoMap = {};
                vm.info.selectAnswers = [];
                vm.info.operation = obj.fileType;
                $.Ajax(obj.urlUpdate, vm.info.$model, function(data) {
                    //如果更新成功则调用完成接口
                    $.Ajax(obj.urlComplete + '?businessId=' + data.id, {}, function(data) {
                        $.showSucMsg('提交成功！', {
                            yes: function() {
                                //跳首页
                                window.top.location.href = obj.urlContext;
                            },
                            cancel: function() {
                                //跳首页
                                window.top.location.href = obj.urlContext;
                            }
                        });
                    }, {
                        error: function(err, status) {
                            btnChange('nextBtn', true);
                            $.ajaxErr(err, status);
                        }
                    });
                }, {
                    error: function(err, status) {
                        btnChange('nextBtn', true);
                        $.ajaxErr(err, status);
                    }
                });
            });
        });
        //返回上一步
        $('#frontBtn').click(function(e) {
            window.top.location.href = 'back?businessId=' + obj.businessId + '&operation=' + obj.fileType;
        });
    }
    //加载数据
    function loadData() {
        $.Ajax(obj.urlBaseValue + '?businessId=' + obj.businessId, null, function(data) {
            vm.info = data;
            avalon.scan(document.body);
            //初始化裁剪
            initPicCrop();
            //设置附件下载地址
            setDownloadUrl();
        });
        // vm.info = {
        //     valueDtoMap: {
        //         FILE_BASE_INFO: [{
        //             id: 100,
        //             key: "FILE_BASE_INFO",
        //             displayName: "投资者基本信息表（机构）",
        //             val: ""
        //         }],
        //         FILE_BUSINESS_LICENSE: [{
        //             id: 101,
        //             key: "FILE_BUSINESS_LICENSE",
        //             displayName: "营业执照（扫描件）",
        //             val: ""
        //         }],
        //         FILE_OTHER: [{
        //             id: 107,
        //             key: "FILE_OTHER",
        //             displayName: "承销商要求的其他材料（由审核反馈决定）",
        //             val: ""
        //         }]
        //     }
        // };
        // avalon.scan(document.body);
        // //初始化裁剪
        // initPicCrop();
        // //设置附件下载地址
        // setDownloadUrl();
    }
    //设置数据
    function setData() {
        var arrs = getArrayByType();
        for (var i = 0; i < arrs.length; i++) {
            $('#' + arrs[i] + '_picupload')[0].setPaths(vm.info.valueDtoMap[arrs[i]][0].val);
        }
    }
    //设置附件下载地址
    function setDownloadUrl() {
        var url = '';
        switch (obj.fileType) {
            case 'FILE_DEPT_SPECIALTY':
                {
                    url = obj.urlContext + 'tab/deptOrganizationSpecialtyBase';
                    $('#' + obj.fileType + '_download').attr('href', url + '?businessId=' + obj.businessId);
                    break;
                }
            case 'FILE_DEPT_ORDINARY':
                {
                    url = obj.urlContext + 'tab/deptOrganizationOrdinaryBase';
                    $('#' + obj.fileType + '_download').attr('href', url + '?businessId=' + obj.businessId);
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_A_SPECIALTY':
                {
                    url = obj.urlContext + 'tab/deptOrganizationBase';
                    $('#' + obj.fileType + '_download').attr('href', url + '?businessId=' + obj.businessId);
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_B_SPECIALTY':
                {
                    url = obj.urlContext + 'tab/shareSpecialtyApply';
                    $('#' + obj.fileType + '_a_download').attr('href', url + '?businessId=' + obj.businessId);
                    url = obj.urlContext + 'tab/deptOrganizationBase';
                    $('#' + obj.fileType + '_b_download').attr('href', url + '?businessId=' + obj.businessId);
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_ORDINARY':
                {
                    url = obj.urlContext + 'tab/deptOrganizationBase';
                    $('#' + obj.fileType + '_download').attr('href', url + '?businessId=' + obj.businessId);
                    break;
                }
            case 'FILE_SHARES_EVERYMAN_SPECIALTY':
                {
                    url = obj.urlContext + 'tab/shareSpecialtyApply';
                    $('#' + obj.fileType + '_a_download').attr('href', url + '?businessId=' + obj.businessId);
                    url = obj.urlContext + 'tab/shareEverymanBase';
                    $('#' + obj.fileType + '_b_download').attr('href', url + '?businessId=' + obj.businessId);
                    break;
                }
            case 'FILE_SHARES_EVERYMAN_ORDINARY':
                {
                    url = obj.urlContext + 'tab/shareEverymanBase';
                    $('#' + obj.fileType + '_download').attr('href', url + '?businessId=' + obj.businessId);
                    break;
                }
        }
    }
    //保存数据
    function saveData() {
        var arrs = getArrayByType();
        for (var i = 0; i < arrs.length; i++) {
            vm.info.valueDtoMap[arrs[i]][0].val = $('#' + arrs[i] + '_picupload')[0].getPaths();
        }
    }
    //初始化上传组件
    function initPicCrop() {
        var cropOption = {
            url: obj.picUploadUrl,
            urlContext: obj.urlContext,
            onPicReady: function(obj) {
                cropObj = obj;
            }
        };
        switch (obj.fileType) {
            case 'FILE_DEPT_SPECIALTY': //债务专业上传
                {
                    //债务增加pdfFlg参数
                    cropOption = $.extend({}, cropOption, { url: obj.uploadUrl, pdfFlg: true });
                    $('#FILE_BASE_INFO_picupload').picUpload($.extend({}, cropOption, { single: true }));
                    $('#FILE_BUSINESS_LICENSE_picupload').picUpload($.extend({}, cropOption, { single: true }));
                    $('#FILE_SECURITY_FUND_FUTURES_LICENSE_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_FOUNDATION_PERSON_LICENSE_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_QFII_RQFII_FUND_ADMIN_LOGIN_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_FINANCIAL_PRODUCTS_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_OTHER_picupload').picUpload($.extend({}, cropOption, {}));
                    break;
                }
            case 'FILE_DEPT_ORDINARY': //债务普通上传
                {
                    //债务增加pdfFlg参数
                    cropOption = $.extend({}, cropOption, { url: obj.uploadUrl, pdfFlg: true });
                    $('#FILE_BASE_INFO_picupload').picUpload($.extend({}, cropOption, { single: true }));
                    $('#FILE_BUSINESS_LICENSE_picupload').picUpload($.extend({}, cropOption, { single: true }));
                    $('#FILE_FINANCIAL_STATEMENTS_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_FINANCIAL_ASSET_PROOF_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_INVESTMENT_EXPERIENCE_PROOF_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_AGENT_CARD_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_AGENT_PROXY_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_OTHER_picupload').picUpload($.extend({}, cropOption, {}));
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_A_SPECIALTY': //股票A类专业上传
                {
                    $('#FILE_BASE_INFO_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_BUSINESS_LICENSE_picupload').picUpload($.extend({}, cropOption, { single: true }));
                    $('#FILE_SECURITY_FUND_FUTURES_LICENSE_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_OTHER_FINANCE_LICENSE_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_FOUNDATION_PERSON_LICENSE_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_QFII_RQFII_FUND_ADMIN_LOGIN_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_FINANCIAL_PRODUCTS_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_PROVE_OTHER_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_AGENT_CARD_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_AGENT_PROXY_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_OTHER_picupload').picUpload($.extend({}, cropOption, {}));
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_B_SPECIALTY': //股票B类专业上传
                {
                    $('#FILE_INVESTOR_APPLICATION_picupload').picUpload($.extend({}, cropOption, { single: true }));
                    $('#FILE_BASE_INFO_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_BUSINESS_LICENSE_picupload').picUpload($.extend({}, cropOption, { single: true }));
                    $('#FILE_FINANCIAL_STATEMENTS_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_FINANCIAL_ASSET_PROOF_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_INVESTMENT_EXPERIENCE_PROOF_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_AGENT_CARD_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_AGENT_PROXY_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_OTHER_picupload').picUpload($.extend({}, cropOption, {}));
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_ORDINARY': //股票普通上传
                {
                    $('#FILE_BASE_INFO_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_BUSINESS_LICENSE_picupload').picUpload($.extend({}, cropOption, { single: true }));
                    $('#FILE_AGENT_CARD_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_AGENT_PROXY_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_OTHER_picupload').picUpload($.extend({}, cropOption, {}));
                    break;
                }
            case 'FILE_SHARES_EVERYMAN_SPECIALTY': //股票自然人专业上传
                {
                    $('#FILE_INVESTOR_APPLICATION_picupload').picUpload($.extend({}, cropOption, { single: true }));
                    $('#FILE_BASE_INFO_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_FINANCIAL_ASSET_PROOF_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_INVESTMENT_EXPERIENCE_PROOF_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_INVESTMENT_CARD_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_OTHER_picupload').picUpload($.extend({}, cropOption, {}));
                    break;
                }
            case 'FILE_SHARES_EVERYMAN_ORDINARY': //股票自然人普通上传
                {
                    $('#FILE_BASE_INFO_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_INVESTMENT_CARD_picupload').picUpload($.extend({}, cropOption, {}));
                    $('#FILE_OTHER_picupload').picUpload($.extend({}, cropOption, {}));
                    break;
                }
        }
        //设置数据
        setData();
    }
    //检查文件是否上传
    function beforeSubmit() {
        var arrs = [];
        switch (obj.fileType) {
            case 'FILE_DEPT_SPECIALTY': //债务专业上传
                {
                    arrs = [
                        'FILE_BASE_INFO',
                        'FILE_BUSINESS_LICENSE',
                        'FILE_SECURITY_FUND_FUTURES_LICENSE'
                    ];
                    break;
                }
            case 'FILE_DEPT_ORDINARY': //债务普通上传
                {
                    arrs = [
                        'FILE_BASE_INFO',
                        'FILE_BUSINESS_LICENSE',
                        'FILE_FINANCIAL_STATEMENTS',
                        'FILE_FINANCIAL_ASSET_PROOF',
                        'FILE_INVESTMENT_EXPERIENCE_PROOF',
                        'FILE_AGENT_CARD',
                        'FILE_AGENT_PROXY',
                        'FILE_OTHER'
                    ];
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_A_SPECIALTY': //股票A类专业上传
                {
                    arrs = [
                        'FILE_BASE_INFO',
                        'FILE_BUSINESS_LICENSE',
                        'FILE_AGENT_CARD'
                    ];
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_B_SPECIALTY': //股票B类专业上传
                {
                    arrs = [
                        'FILE_INVESTOR_APPLICATION',
                        'FILE_BASE_INFO',
                        'FILE_BUSINESS_LICENSE',
                        'FILE_FINANCIAL_STATEMENTS',
                        'FILE_FINANCIAL_ASSET_PROOF',
                        'FILE_INVESTMENT_EXPERIENCE_PROOF',
                        'FILE_AGENT_CARD'
                    ];
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_ORDINARY': //股票普通上传
                {
                    arrs = [
                        'FILE_BASE_INFO',
                        'FILE_BUSINESS_LICENSE',
                        'FILE_AGENT_CARD',
                        'FILE_OTHER'
                    ];
                    break;
                }
            case 'FILE_SHARES_EVERYMAN_SPECIALTY': //股票自然人专业上传
                {
                    arrs = [
                        'FILE_INVESTOR_APPLICATION',
                        'FILE_BASE_INFO',
                        'FILE_FINANCIAL_ASSET_PROOF',
                        'FILE_INVESTMENT_EXPERIENCE_PROOF',
                        'FILE_INVESTMENT_CARD',
                    ];
                    break;
                }
            case 'FILE_SHARES_EVERYMAN_ORDINARY': //股票自然人普通上传
                {
                    arrs = [
                        'FILE_BASE_INFO',
                        'FILE_INVESTMENT_CARD',
                    ];
                    break;
                }
        }
        for (var i = 0; i < arrs.length; i++) {
            if (!$('#' + arrs[i] + '_picupload')[0].isUploadSuc()) {
                $.showErrMsg('请上传' + $('#' + arrs[i] + '_picupload').prev().children().eq(1).text());
                return false;
            }
        }
        return true;
    }
    //获取KEY数组
    function getArrayByType() {
        var arrs = [];
        switch (obj.fileType) {
            case 'FILE_DEPT_SPECIALTY': //债务专业上传
                {
                    arrs = [
                        'FILE_BASE_INFO',
                        'FILE_BUSINESS_LICENSE',
                        'FILE_SECURITY_FUND_FUTURES_LICENSE',
                        'FILE_FOUNDATION_PERSON_LICENSE',
                        'FILE_QFII_RQFII_FUND_ADMIN_LOGIN',
                        'FILE_FINANCIAL_PRODUCTS',
                        'FILE_OTHER'
                    ];
                    break;
                }
            case 'FILE_DEPT_ORDINARY': //债务普通上传
                {
                    arrs = [
                        'FILE_BASE_INFO',
                        'FILE_BUSINESS_LICENSE',
                        'FILE_FINANCIAL_STATEMENTS',
                        'FILE_FINANCIAL_ASSET_PROOF',
                        'FILE_INVESTMENT_EXPERIENCE_PROOF',
                        'FILE_AGENT_CARD',
                        'FILE_AGENT_PROXY',
                        'FILE_OTHER'
                    ];
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_A_SPECIALTY': //股票A类专业上传
                {
                    arrs = [
                        'FILE_BASE_INFO',
                        'FILE_BUSINESS_LICENSE',
                        'FILE_SECURITY_FUND_FUTURES_LICENSE',
                        'FILE_OTHER_FINANCE_LICENSE',
                        'FILE_FOUNDATION_PERSON_LICENSE',
                        'FILE_QFII_RQFII_FUND_ADMIN_LOGIN',
                        'FILE_FINANCIAL_PRODUCTS',
                        'FILE_PROVE_OTHER',
                        'FILE_AGENT_CARD',
                        'FILE_AGENT_PROXY',
                        'FILE_OTHER'
                    ];
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_B_SPECIALTY': //股票B类专业上传
                {
                    arrs = [
                        'FILE_INVESTOR_APPLICATION',
                        'FILE_BASE_INFO',
                        'FILE_BUSINESS_LICENSE',
                        'FILE_FINANCIAL_STATEMENTS',
                        'FILE_FINANCIAL_ASSET_PROOF',
                        'FILE_INVESTMENT_EXPERIENCE_PROOF',
                        'FILE_AGENT_CARD',
                        'FILE_AGENT_PROXY',
                        'FILE_OTHER'
                    ];
                    break;
                }
            case 'FILE_SHARES_ORGANIZATION_ORDINARY': //股票普通上传
                {
                    arrs = [
                        'FILE_BASE_INFO',
                        'FILE_BUSINESS_LICENSE',
                        'FILE_AGENT_CARD',
                        'FILE_AGENT_PROXY',
                        'FILE_OTHER'
                    ];
                    break;
                }
            case 'FILE_SHARES_EVERYMAN_SPECIALTY': //股票自然人专业上传
                {
                    arrs = [
                        'FILE_INVESTOR_APPLICATION',
                        'FILE_BASE_INFO',
                        'FILE_FINANCIAL_ASSET_PROOF',
                        'FILE_INVESTMENT_EXPERIENCE_PROOF',
                        'FILE_INVESTMENT_CARD',
                        'FILE_OTHER'
                    ];
                    break;
                }
            case 'FILE_SHARES_EVERYMAN_ORDINARY': //股票自然人普通上传
                {
                    arrs = [
                        'FILE_BASE_INFO',
                        'FILE_INVESTMENT_CARD',
                        'FILE_OTHER'
                    ];
                    break;
                }
        }
        return arrs;
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
    //设置导航条显示
    function setNav() {
        var _items = $('.c-navbox').find('.nav-item');
        if (obj.isQuestions) {
            setSetp(5);
        } else {
            for (var i = 0; i < _items.length; i++) {
                $(_items[i]).removeClass('item-min');
            }
            $(_items[4]).find('.item-num').text(4);
            $(_items[3]).remove();
            setSetp(4);
        }
        //显示导航条
        $(_items[0]).parent().show();
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
}($, uploadObj));
