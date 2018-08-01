/*
 *修改用户密码页面的js
 *@Author wx 2017-10-17
 */
$(function($, obj) {
    var changef = $('#changeForm');
    //初始化
    function init() {
        //事件监听
        eventHandler();
        //验证
        doValidate();
    }
    //页面事件监听
    function eventHandler() {
        //修改密码保存
        $('#saveBtn').click(function(e) {
            $('#saveBtn').prop('disabled', true);
            if (!changef[0].valid()) {
                //启用按钮
                $('#saveBtn').prop('disabled', false);
                return;
            }
            var changeData = $.extend({ id: obj.userId }, $('#changeForm').serializeJson());
            $.Ajax(obj.urlUserChangePW, changeData, function(data) {
                $.showSucMsg('修改成功！', {
                    yes: function() {
                        window.top.location.href='logout';
                    }
                });
            }, {
                error: function(err, status) {
                    $.ajaxErr(err, status);
                    //启用按钮
                    $('#saveBtn').prop('disabled', false);
                }
            });
        });
    }
    //验证
    function doValidate() {
        changef.fzValidate({
            type: 'modal',
            rules: {
                passwd: {
                    required: true,
                    minlength: 8
                },
                passwdconfirm_change: {
                    required: true,
                    minlength: 8,
                    equalTo: "#passwd_change"
                }
            },
            messages: {
                passwd: {
                    required: "请输入新密码",
                    minlength: $.validator.format("新密码长度不能小于{0}个字符")
                },
                passwdconfirm_change: {
                    required: "请输入确认密码",
                    minlength: "确认密码长度不能小于8个字符",
                    equalTo: "两次输入密码不一致"
                }
            }
        });
    }
    init();
}($, pwdObj));
