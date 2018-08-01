(function($, obj) {
    var resetForm = $('#resetForm');
    var btnSubmit = $('#btnSubmit');
    var btnSendVerificationCode = $('#btnSendVerificationCode');
    var expMail = /^(\w)+(\.\w+)*@(\w)+((\.\w{2,3}){1,3})$/;
    var expMobile = /^1[0-9]{10}$/;

    function init() {
        //事件监听
        eventHandler();
        doValidate();
    }

    function eventHandler() {
        btnSendVerificationCode.click(function() {
            sendVerifyCode();
        });
        btnSubmit.click(function(e) {
            if (!resetForm[0].valid()) {
                return false;
            }
            resetForm[0].submit();
        });
    }

    function doValidate() {
        resetForm.fzValidate({
            rules: {
                mobileNoOrEmail: {
                    required: true,
                    mobileOrEmail: true,
                },
                password: {
                    password_fz: true,
                    minlength: 6,
                },
                jcaptchaCode: {
                    minlength: 5,
                },
                verifyCode: {
                    minlength: 6,
                },
                confirmedPassword:{
                    required: true,
                    equalTo: "#password",
                },
            },
            messages: {
                userName: {
                    required: "请输入证件号",
                },
                mobileNoOrEmail: {
                    required: "请输入邮箱或手机号",
                },
                password: {
                    required: "请输入密码",
                    minlength: "确认密码长度不能小于6个字符",
                    password_fz: "密码必须是数字及英文字母组合",

                },
                jcaptchaCode: {
                    required: "请输入5位校验码",
                    minlength: "请输入5位校验码",
                },
                verifyCode: {
                    required: "请输入6位验证码",
                    minlength: "请输入6位验证码",
                },
                confirmedPassword:{
                    required: "请再次输入密码",
                    equalTo: "两次输入密码不一致",
                }
            }
        });
    }

    function buttonCountdown($el, msNum, timeFormat) {
        var text = $el.data("text") || $el.text(),
            timer = 0;
        $el.prop("disabled", true).addClass("disabled")
            .on("bc.clear", function() {
                clearTime();
            });
        (function countdown() {
            var time = showTime(msNum)[timeFormat];
            $el.text(time + '后重试');
            if (msNum <= 0) {
                msNum = 0;
                clearTime();
            } else {
                msNum -= 1000;
                timer = setTimeout(arguments.callee, 1000);
            }
        })();

        function clearTime() {
            clearTimeout(timer);
            $el.prop("disabled", false).removeClass("disabled").text(text);
        }

        function showTime(ms) {
            var d = Math.floor(ms / 1000 / 60 / 60 / 24),
                h = Math.floor(ms / 1000 / 60 / 60 % 24),
                m = Math.floor(ms / 1000 / 60 % 60),
                s = Math.floor(ms / 1000 % 60),
                ss = Math.floor(ms / 1000);
            return {
                d: d + "天",
                h: h + "小时",
                m: m + "分",
                ss: ss + "秒",
                "d:h:m:s": d + "天" + h + "小时" + m + "分" + s + "秒",
                "h:m:s": h + "小时" + m + "分" + s + "秒",
                "m:s": m + "分" + s + "秒"
            };
        }
        return this;
    }

    function sendVerifyCode() {
        var verificationData = {};
        var loginName = $('#userName').val();
        var token = $('#mobileNoOrEmail').val();
        if (!emailOrMobileVerify(token) || !loginNameVerify(loginName)) return;
        verificationData['loginName'] = loginName;
        verificationData['type'] = getSendType(token);
        verificationData['operationType'] = 'RESET_PASSWORD';
        verificationData['token'] = token;
        $.Ajax(obj.urlSendVerificationCode, verificationData, function(data) {
            buttonCountdown(btnSendVerificationCode, 90 * 1000, 'ss');
        });
    }

    function emailOrMobileVerify(token) {
        if (expMail.test(token) || expMobile.test(token)) {
            return true;
        }
        $.showErrMsg('请输入正确的邮箱或手机号');
        return false;
    }

    function loginNameVerify(loginName) {
        if (!loginName) {
            $.showErrMsg('请输入证件号');
            return false;
        }
        return true;
    }

    function getSendType(token) {
        if (expMail.test(token)) {
            return 'MAIL';
        } else if (expMobile.test(token))
            return 'SMS';
        $.showErrMsg('请输入正确的邮箱或手机号');
        return false;
    }
    init();
}($, userObj));
