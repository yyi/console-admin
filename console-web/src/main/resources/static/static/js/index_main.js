/*
 *首页面的js
 *@Author wx 2017-8-3
 */
$(function($, obj) {
    var vm = avalon.define({
        $id: 'indexMain',
        url: obj.urlContext,
        dailyRuntimeLog: [],
        dailyRuntimeLogLength: 0,
        emergencyPractice: [],
        emergencyPracticeLength: 0,
        testPesponse: [],
        testPesponseLength: 0,
        equipmentManagement: [],
        equipmentManagementLength: 0,
        dynamicStatistics: [],
        dynamicStatisticsLength: 0,
        messageDocuments: [],
        messageDocumentsLength: 0,
        dailyRuntimeLogClick: function(o) {
            //点击运行日志打开新标签页面
            $.openTab(window.top, obj.urlContext + 'systemLog?operation=add&taskExecutionId=' + o.id, o.taskName);
            return false;
        },
        emergencyPracticeClick: function(o) {
            //点击应急演练打开新标签页面
            $.openTab(window.top, obj.urlContext + 'emergencyPracticeLogInitCreate?operation=add&taskExecutionId=' + o.id, o.taskName);
            return false;
        },
        testPesponseClick: function(o) {
            //点击测试反馈打开新标签页面
            $.openTab(window.top, obj.urlContext + 'testResponseLog?operation=add&taskExecutionId=' + o.id, o.taskName);
            return false;
        },
        equipmentManagementClick: function(o) {
            //点击设备维护打开新标签页面
            $.openTab(window.top, obj.urlContext + 'equipmentFill?operation=add&taskExecutionId=' + o.id, o.taskName);
            return false;
        },
        dynamicStatisticsClick: function(o) {
            //点击在线填报打开新标签页面
            $.openTab(window.top, obj.urlContext + 'dynamicStat?operation=add&taskExecutionId=' + o.id, o.taskName);
            return false;
        },
        newsClick: function(o) {
            //点击新闻链接打开新标签页面
            $.openTab(window.top, obj.urlContext + 'messagePage?messageDocumentId=' + o.id, o.subject);
            return false
        }
    });
    //入口初始化方法
    var $mainTable = $('#maindatagrid');
    var $orgaTree = $('#orgaTree');
    var $roleTable = $('#roleTable');
    //初始化
    function init() {
        avalon.scan(document.body);
        // 运行日志
        $('#dailyRuntimeLogMore').More({
            allUrl: obj.urlIndexTaskAll,
            moreUrl: obj.urlIndexInitDailyRuntimeLog,
            allQueryType: 'DAILY_RUNTIME_LOG',
            allSucCallback: function(data) {
                vm.dailyRuntimeLog = $.clearObj(data);
            },
            moreSucCallback: function(data) {
                vm.dailyRuntimeLog = vm.dailyRuntimeLog.$model.concat($.clearObj(data.content));
                vm.dailyRuntimeLogLength = data.totalElements;
            }
        });
        // 应急演练
        $('#emergencyPracticeMore').More({
            allUrl: obj.urlIndexTaskAll,
            moreUrl: obj.urlIndexInitEmergencyPractice,
            allQueryType: 'EMERGENCY_PRACTICE',
            allSucCallback: function(data) {
                vm.emergencyPractice = $.clearObj(data);
            },
            moreSucCallback: function(data) {
                vm.emergencyPractice = vm.emergencyPractice.$model.concat($.clearObj(data.content));
                vm.emergencyPracticeLength = data.totalElements;
            }
        });
        // 测试反馈
        $('#testPesponseMore').More({
            allUrl: obj.urlIndexTaskAll,
            moreUrl: obj.urlIndexInitTestResponse,
            allQueryType: 'TEST_RESPONSE',
            allSucCallback: function(data) {
                vm.testPesponse = $.clearObj(data);
            },
            moreSucCallback: function(data) {
                vm.testPesponse = vm.testPesponse.$model.concat($.clearObj(data.content));
                vm.testPesponseLength = data.totalElements;
            }
        });
        // 设备管理
        $('#equipmentManagementMore').More({
            allUrl: obj.urlIndexTaskAll,
            moreUrl: obj.urlIndexInitEquipmentManagement,
            allQueryType: 'EQUIPMENT_MANAGEMENT',
            allSucCallback: function(data) {
                vm.equipmentManagement = $.clearObj(data);
            },
            moreSucCallback: function(data) {
                vm.equipmentManagement = vm.equipmentManagement.$model.concat($.clearObj(data.content));
                vm.equipmentManagementLength = data.totalElements;
            }
        });
        // 动态统计信息
        $('#dynamicStatisticsMore').More({
            allUrl: obj.urlIndexTaskAll,
            moreUrl: obj.urlIndexInitDynamicStatistics,
            allQueryType: 'DYNAMIC_STATISTICS',
            allSucCallback: function(data) {
                vm.dynamicStatistics = $.clearObj(data);
            },
            moreSucCallback: function(data) {
                vm.dynamicStatistics = vm.dynamicStatistics.$model.concat($.clearObj(data.content));
                vm.dynamicStatisticsLength = data.totalElements;
            }
        });
        // 新闻列表
        $('#messageDocumentsMore').More({
            allUrl: obj.urlIndexMessageAll,
            moreUrl: obj.urlIndexInitMessageDocument,
            allSucCallback: function(data) {
                vm.messageDocuments = $.clearObj(data);
            },
            moreSucCallback: function(data) {
                vm.messageDocuments = vm.messageDocuments.$model.concat($.clearObj(data.content));
                vm.messageDocumentsLength = data.totalElements;
            }
        });
        //渲染部门树
        // renderModal();
        //事件监听
        eventHandler();
    }
    //渲染部门树及角色表格
    function renderModal() {
        $orgaTree.fzTree({
            url: obj.urlOrgaQuery,
            keys: ['id', 'name', 'subOrganization'],
            levels: 5,
            showCheckbox: false,
            checkedLink: false,
            maxHeight: 520
        });
    }
    //页面事件监听
    function eventHandler() {
        //页面大小变化重绘表格
        $(window).resize(function() {});
    }
    //加载更多操作封装
    $.fn.More = function(options) {
        return this.each(function() {
            var defaults = {
                allUrl: '',
                moreUrl: '',
                allQueryType: '',
                pageSize: 5,
                allSucCallback: function() {},
                moreSucCallback: function() {}
            };
            var opts = $.extend({}, defaults, options);
            var $this = $(this);
            var that = this;
            var id = $this.attr('id');
            var img, btnall, btnmore, count = 0;
            var $content = $this.parent().find('li:not(:last)');
            //渲染
            function render() {
                $this.addClass('list-group-item list-btn-group');
                $div = $('<div></div>');
                $div.addClass('btn-group');
                $img = $('#loading').clone();
                $div.append($img);
                img = $img;
                $btnall = $('<button type="button"></button');
                $btnmore = $('<button type="button"></button');
                $btnall.html('加载全部');
                $btnmore.html('加载更多');
                $btnall.addClass('btn btn-white');
                $btnmore.addClass('btn btn-white');
                $div.append($btnall);
                $div.append($btnmore);
                btnall = $btnall;
                btnmore = $btnmore;
                $this.append($div);
                eventHand();
            }
            //绑定事件
            function eventHand() {
                $btnmore.click(function() {
                    loadMore();
                });
                $btnall.click(function() {
                    $content.hide();
                    doShow();
                    $.Ajax(opts.allUrl, {
                        type: opts.allQueryType
                    }, function(data) {
                        $content.show();
                        $this.hide();
                        var func = opts.allSucCallback;
                        if (typeof func === 'function') {
                            func.call(this, data);
                        }
                    }, {
                        error: function(err, status) {
                            $content.show();
                            doHide();
                            $.ajaxErr(err, status);
                        }
                    });
                });
            }
            //显示loading
            function doShow() {
                btnall.hide();
                btnmore.hide();
                img.show();
            }
            //隐藏loading
            function doHide() {
                btnall.show();
                btnmore.show();
                img.hide();
            }
            //加载更多
            function loadMore() {
                doShow();
                $.AjaxGet(opts.moreUrl, {
                    page: count++,
                    size: opts.pageSize
                }, function(data) {
                    var func = opts.moreSucCallback;
                    if (typeof func === 'function') {
                        func.call(this, data);
                    }
                    //如果超过总页数则隐藏按钮
                    if (count >= Math.ceil(data.totalElements / opts.pageSize)) {
                        $this.hide();
                    } else {
                        doHide();
                    }
                }, {
                    error: function(err, status) {
                        doHide();
                        $.ajaxErr(err, status);
                    }
                });
            }
            //选项检查
            function checkOpts() {
                if (opts.allUrl.length <= 0) {
                    throw new TypeError('err: allUrl content undefined!');
                }
                if (opts.moreUrl.length <= 0) {
                    throw new TypeError('err: moreUrl content undefined!');
                }
                if ($("#loading").length <= 0) {
                    throw new TypeError('err: loading.gif not exist in page!');
                }
                return true;
            }
            //初始化
            function init() {
                render();
                loadMore();
            }
            if (checkOpts()) init();
        });
    };
    init();
}($, indexObj));
