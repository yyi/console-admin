(function($) {
    var completeInfoForm = $('#completeInfoForm');
    var btnCompleteInfo = $('#btnCompleteInfo');

    function init() {
        //事件监听
        eventHandler();
        doValidate();
    }

    function eventHandler() {
        btnCompleteInfo.click(function(e) {
            if (!completeInfoForm[0].valid()) {
                return false;
            }
            completeInfoForm[0].submit();
        })
    }

    function doValidate() {
        completeInfoForm.fzValidate({
            rules: {
                password: {
                    password_fz: true,
                    minlength: 6,
                },
                confirmedPassword: {
                    required: true,
                    equalTo: "#password",
                }
            },
            messages: {
                password: {
                    required: "请输入密码",
                    minlength: "确认密码长度不能小于6个字符",

                    password_fz: "密码必须是数字及英文字母组合",
                },
                confirmedPassword: {
                    required: "请输入确认密码",
                    equalTo: "两次输入密码不一致",
                }
            }
        });
    }
    init();
}($));
