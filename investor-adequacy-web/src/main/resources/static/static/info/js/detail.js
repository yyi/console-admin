$(function($, obj) {
    //文本过滤
    avalon.filters.valFilter = function(o) {
        var flg = (o.k == 'CREDIT_RECORD' || o.k == 'IS_ORGANIZATION_TYPE');
        return o.v.replace(flg ? /\-/g : /(>_<)/g, flg ? '、' : '');
    };
    //文件审核过滤
    avalon.filters.auditFilter = function(v) {
        switch (v) {
            case 'AGREED':
                return '通过';
            case 'REJECTIVE':
                return '不通过';
            case 'NOT_APPLICABLE':
                return '此项不适用';
        }
        return '';
    };
    //图片过滤
    avalon.filters.imgFilter = function(o) {
        if ($.isEmpty(o.v)) return '';
        var imgs = o.v.split('>_<');
        var html = '';
        $.each(imgs, function(i, e) {
            var url = encodeURI(o.u + "rest/commons/download?filePath=" + e);
            var name = e.substr((e.lastIndexOf('\\') == -1 ? e.lastIndexOf('/') : e.lastIndexOf('\\')) + 1);
            if(o.t=='DEPT')
                //如果是债务则显示下载PDF图标
                html += ('<a href="' + url + '"><img src="' + o.u + 'static/info/images/pdfdn.jpg" title="' + name + '"><a>');
            else
                html += ('<a href="' + url + '"><img src="' + url + '" title="' + name + '"><a>');
        });
        return html;
    };
    var vm = avalon.define({
        $id: 'detailObj',
        detail: [],
        url: obj.urlContext,
        baseInfoKey: function(e, i) {
            return !/^(FILE_)/.test(e);
        },
        fileKey: function(e, i) {
            return /^(FILE_)/.test(e);
        }
    });
    //初始化
    function init() {
        loadData();
        eventHandler();
    }
    //页面事件监听
    function eventHandler() {
        $('#backBtn').click(function(e) {
            window.top.location.href = obj.urlContext;
        });
    }
    //加载数据
    function loadData() {
        $.Ajax(obj.urlInit + '?businessId=' + obj.businessId, null, function(data) {
            vm.detail = data;
            avalon.scan(document.body);
        });
        // vm.detail = toJson();
        // console.log(vm.detail.$model);
        // avalon.scan(document.body);
    }

    function toJson() {
        var str = '{"businessDto":{"id":53826,"name":"股票业务申请","createDate":"2018-01-09 10:51:31","status":"COMPLETE","serviceType":"SHARES","investorType":"SHARES_SPECIALTY","organizationName":null,"feedback":null,"totalScope":null,"riskLevel":null},"valueDtoMap":{"NATIONALITY":[{"id":82,"key":"NATIONALITY","displayName":"国籍","val":"4"}],"CARD_DATE":[{"id":99,"key":"CARD_DATE","displayName":"身份证有效期","val":"11"}],"SEX":[{"id":80,"key":"SEX","displayName":"性别","val":"2"}],"CARD_TYPE":[{"id":90,"key":"CARD_TYPE","displayName":"身份证件类型","val":"9"}],"EDUCATION":[{"id":88,"key":"EDUCATION","displayName":"学历","val":"初中及以下"}],"EMAIL":[{"id":97,"key":"EMAIL","displayName":"Email","val":"16"}],"FILE_INVESTOR_APPLICATION":[{"id":141,"key":"FILE_INVESTOR_APPLICATION","displayName":"专业投资者申请书：","val":"\\\\201801\\\\20180110155840_IMG_20170114_220130.jpg"}],"FILE_INVESTMENT_CARD":[{"id":145,"key":"FILE_INVESTMENT_CARD","displayName":"投资者身份证（正反面彩色扫描件）","val":"\\\\201801\\\\20180110160009_QQ截图20170726091510.png"}],"CARD_NUMBER":[{"id":91,"key":"CARD_NUMBER","displayName":"身份证件号码","val":"10"}],"CREDIT_RECORD":[{"id":89,"key":"CREDIT_RECORD","displayName":"诚信记录","val":"中国人民银行征信中心-监管机构、自律组织-其他组织"}],"post":[{"id":87,"key":"post","displayName":"职务","val":"8"}],"POSTAL_CODE":[{"id":95,"key":"POSTAL_CODE","displayName":"邮政编码","val":"15"}],"BIRTH_DATE":[{"id":81,"key":"BIRTH_DATE","displayName":"出生年月","val":"3"}],"BENEFICIARY":[{"id":84,"key":"BENEFICIARY","displayName":"交易的实际受益人","val":"6"}],"FILE_INVESTMENT_EXPERIENCE_PROOF":[{"id":144,"key":"FILE_INVESTMENT_EXPERIENCE_PROOF","displayName":"2年以上证券、基金、期货、黄金、外汇等投资经历证明（首次交易截图需要加盖中结算营业部开户专用章）（扫描件）","val":"\\\\201801\\\\20180110155946_搜狗截图20170809180653.png"}],"TELEPHONE":[{"id":92,"key":"TELEPHONE","displayName":"固定电话","val":"12"}],"WORK_UNIT":[{"id":86,"key":"WORK_UNIT","displayName":"工作单位","val":"7"}],"FILE_BASE_INFO":[{"id":142,"key":"FILE_BASE_INFO","displayName":"投资者基本信息表（个人）","val":"\\\\201801\\\\20180110155853_ico.png"}],"NAME":[{"id":79,"key":"NAME","displayName":"姓名","val":"1"}],"FILE_OTHER":[{"id":146,"key":"FILE_OTHER","displayName":"其他材料（扫描件，此项用于投资者补充审核反馈中说明的材料）","val":"\\\\201801\\\\20180110160028_QQ截图20170726091510.png"}],"INVESTOR_PERSON":[{"id":83,"key":"INVESTOR_PERSON","displayName":"实际控制投资者的自然人","val":"5"}],"FILE_FINANCIAL_ASSET_PROOF":[{"id":143,"key":"FILE_FINANCIAL_ASSET_PROOF","displayName":"金融资产证明包括：银行存款、股票、债券、基金份额、资产管理计划、银行理财产品、信托计划、保险产品、期货及其他衍生产品等（最近1年末金融资产不低于1000万元）（扫描件）","val":"\\\\201801\\\\20180110155903_搜狗截图20170809180653.png>_<\\\\201801\\\\20180110155922_QQ图片20180108111428.jpg"}],"ADDRESS":[{"id":96,"key":"ADDRESS","displayName":"联系地址","val":"14"}],"OCCUPATION":[{"id":85,"key":"OCCUPATION","displayName":"职业","val":"sdfgergdfg"}],"IPHONE":[{"id":93,"key":"IPHONE","displayName":"手机号码","val":"13"}]},"questionsDtoMap":null,"selectAnswers":null,"keyOrders":["NAME","SEX","BIRTH_DATE","NATIONALITY","INVESTOR_PERSON","BENEFICIARY","OCCUPATION","WORK_UNIT","post","EDUCATION","CREDIT_RECORD","CARD_TYPE","CARD_NUMBER","CARD_DATE","TELEPHONE","IPHONE","ADDRESS","POSTAL_CODE","ADDRESS","EMAIL","FILE_INVESTOR_APPLICATION","FILE_BASE_INFO","FILE_FINANCIAL_ASSET_PROOF","FILE_INVESTMENT_EXPERIENCE_PROOF","FILE_INVESTMENT_CARD","FILE_OTHER"],"operation":null,"map":{}}';
        return JSON.parse(str);
    }
    init();
}($, detailObj));
