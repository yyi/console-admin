(function($) {
    var loginForm = $('#loginForm');
    var btnLoginSubmit = $('#loginSubmit');
    //初始化
    function init() {
        //事件监听
        eventHandler();
        doValidate();
    }
    //事件处理
    function eventHandler() {
        btnLoginSubmit.click(function(e) {
            if (!loginForm[0].valid() || !verifyLoginName()) {
                return false;
            }
            loginForm[0].submit();
        });
        $("input[type='radio']").change(function(event) {
            $('#username').val("");
            $('#password').val("");
            $('#username').attr("placeholder", this.id == 'personLogin' ? "请输入证件号码" : "请输入9位或18位组织机构代码");
        });
    }
    //登录名校验
    function verifyLoginName() {
        var person = $('input:radio[name="personal"]:checked').val();
        var loginName = $('#username').val();
        if (person == "true") {
            if (loginName.length == 18) {
                var isIdentifyCard = identityCodeValid(loginName);
                if (!isIdentifyCard)
                    $.showErrMsg('请输入正确的身份证号');
                return isIdentifyCard;
            }
        } else {
            if (loginName.length != 18 && loginName.length != 9) {
                $.showErrMsg('请输入9位或18位的组织机构代码');
                return false;
            }
            if (loginName.length == 9) {
                var isOrgNo = orgcodevalidate(loginName);
                if (!isOrgNo)
                    $.showErrMsg('请输入正确的组织机构代码');
                return isOrgNo;
            }
        }
        return true;
    }
    //组织机构编码校验
    function orgcodevalidate(value) {
        if (value != "") {
            var values = value.substring(0, 8);
            var ws = [3, 7, 9, 10, 5, 8, 4, 2];
            var str = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
            var reg = /^([0-9A-Z]){8}$/;
            if (!reg.test(values)) {
                return false;
            }
            var sum = 0;
            for (var i = 0; i < 8; i++) {
                sum += str.indexOf(values.charAt(i)) * ws[i];
            }
            var C9 = 11 - (sum % 11);
            var YC9 = value.substring(8).toUpperCase() + '';
            if (C9 == 11) {
                C9 = '0';
            } else if (C9 == 10) {
                C9 = 'X';
            } else {
                C9 = C9 + '';
            }
            return YC9 == C9;
        }
    }
    //验证身份证的合法性
    function identityCodeValid(code) {
        var city = { 11: "北京", 12: "天津", 13: "河北", 14: "山西", 15: "内蒙古", 21: "辽宁", 22: "吉林", 23: "黑龙江 ", 31: "上海", 32: "江苏", 33: "浙江", 34: "安徽", 35: "福建", 36: "江西", 37: "山东", 41: "河南", 42: "湖北 ", 43: "湖南", 44: "广东", 45: "广西", 46: "海南", 50: "重庆", 51: "四川", 52: "贵州", 53: "云南", 54: "西藏 ", 61: "陕西", 62: "甘肃", 63: "青海", 64: "宁夏", 65: "新疆", 71: "台湾", 81: "香港", 82: "澳门", 91: "国外 " };
        var tip = "";
        var pass = true;
        if (!code || !/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[12])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(code)) {
            tip = "身份证号格式错误";
            pass = false;
        } else if (!city[code.substr(0, 2)]) {
            tip = "地址编码错误";
            pass = false;
        } else {
            //18位身份证需要验证最后一位校验位
            if (code.length == 18) {
                code = code.split('');
                //∑(ai×Wi)(mod 11)
                //加权因子
                var factor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
                //校验位
                var parity = [1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2];
                var sum = 0;
                var ai = 0;
                var wi = 0;
                for (var i = 0; i < 17; i++) {
                    ai = code[i];
                    wi = factor[i];
                    sum += ai * wi;
                }
                var last = parity[sum % 11];
                if (parity[sum % 11] != code[17]) {
                    tip = "校验位错误";
                    pass = false;
                }
            }
        }
        return pass;
    }
    //验证
    function doValidate() {
        loginForm.fzValidate({
            type: 'modal',
            rules: {
                username: {
                    required: true,
                    minlength: 6,
                },
                password: {
                    required: true,
                },
                jcaptchaCode: {
                    required: true,
                    minlength: 5,
                },
            },
            messages: {
                password: {
                    required: "请输入密码",
                },
                jcaptchaCode: {
                    required: "请输入5位校验码",
                    minlength: "请输入5位校验码",
                },
                username: {
                    required: "请输入证件号",
                    minlength: "证件号长度至少6位",
                },
            }
        });
    }
    init();
}($));
