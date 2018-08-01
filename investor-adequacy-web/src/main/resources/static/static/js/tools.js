/*
 *JQuery扩展，该JS放在页面全局JS之后，用户JS之前
 */
(function($) {
    //常用公共提示语
    $.tips = {
        //公共错误
        err: '系统开小差了，请稍后再试！',
        timeoutErr: '网络超时，请稍后再试！',
        //网络异常
        networkErr: '网络异常，请稍后再试！',
        //公共加载提示
        loadTip: '正在加载数据...'
    };
    $.fn.serializeNewArray = function() {
        var rCRLF = /\r?\n/g,
            rsubmitterTypes = /^(?:submit|button|image|reset|file)$/i,
            rsubmittable = /^(?:input|select|textarea|keygen)/i,
            rcheckableType = (/^(?:checkbox|radio)$/i);
        return this.map(function() {
                var elements = $.prop(this, "elements");
                return elements ? $.makeArray(elements) : this;
            })
            .filter(function() {
                var type = this.type;
                return this.name && rsubmittable.test(this.nodeName) && !rsubmitterTypes.test(type) &&
                    (this.checked || !rcheckableType.test(type));
            })
            .map(function(i, elem) {
                var val = $(this).val();
                return val == null ? null : $.isArray(val) ?
                    $.map(val, function(val) {
                        return { name: elem.name, value: val.replace(rCRLF, "\r\n") };
                    }) : { name: elem.name, value: val.replace(rCRLF, "\r\n") };
            }).get();
    };
    //表单序列化成JSON格式数据
    $.fn.serializeJson = function() {
        var o = {};
        var a = this.serializeNewArray();
        $.each(a, function() {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };
    //将JS对象或数组中null或undefined值转为''空串
    $.extend({
        clearObj: function(o) {
            if (typeof o === 'undefined' || o === null) {
                o = '';
                return o;
            } else {
                if (o.constructor === Array || o.constructor === Object) {
                    for (i in o) {
                        o[i] = this.clearObj(o[i]);
                    }
                }
                return o;
            }
        }
    });
    $.extend({
        //是否为空判断
        isEmpty: function(obj) {
            if (typeof obj === 'undefined') return true;
            else if (obj === null) return true;
            else if (obj === '') return true;
            else return false;
        }
    });
    $.extend({
        //是否为0判断
        isZero: function(obj) {
            if ($.isEmpty(obj)) return true;
            else if (obj === 0) return true;
            else return false;
        }
    });
    //数学计算相关
    $.extend({
        //将数字转换为金额大写
        moneyUppers: function(v) {
            var MAXIMUM_NUMBER = 99999999999.99;
            var CN_ZERO = "零";
            var CN_ONE = "壹";
            var CN_TWO = "贰";
            var CN_THREE = "叁";
            var CN_FOUR = "肆";
            var CN_FIVE = "伍";
            var CN_SIX = "陆";
            var CN_SEVEN = "柒";
            var CN_EIGHT = "捌";
            var CN_NINE = "玖";
            var CN_TEN = "拾";
            var CN_HUNDRED = "佰";
            var CN_THOUSAND = "仟";
            var CN_TEN_THOUSAND = "万";
            var CN_HUNDRED_MILLION = "亿";
            var CN_SYMBOL = "人民币";
            var CN_DOLLAR = "元";
            var CN_TEN_CENT = "角";
            var CN_CENT = "分";
            var CN_INTEGER = "整";
            var integral;
            var decimal;
            var outputCharacters;
            var parts;
            var digits, radices, bigRadices, decimals;
            var zeroCount;
            var i, p, d;
            var quotient, modulus;
            v = v.toString();
            if (v == "") {
                return "";
            }
            if (v.match(/[^,.\d]/) != null) {
                return "";
            }
            if ((v).match(/^((\d{1,3}(,\d{3})*(.((\d{3},)*\d{1,3}))?)|(\d+(.\d+)?))$/) == null) {
                return "";
            }
            v = v.replace(/,/g, "");
            v = v.replace(/^0+/, "");
            if (Number(v) > MAXIMUM_NUMBER) {
                return "";
            }
            parts = v.split(".");
            if (parts.length > 1) {
                integral = parts[0];
                decimal = parts[1];
                decimal = decimal.substr(0, 2);
            } else {
                integral = parts[0];
                decimal = "";
            }
            digits = new Array(CN_ZERO, CN_ONE, CN_TWO, CN_THREE, CN_FOUR, CN_FIVE, CN_SIX, CN_SEVEN, CN_EIGHT, CN_NINE);
            radices = new Array("", CN_TEN, CN_HUNDRED, CN_THOUSAND);
            bigRadices = new Array("", CN_TEN_THOUSAND, CN_HUNDRED_MILLION);
            decimals = new Array(CN_TEN_CENT, CN_CENT);
            outputCharacters = "";
            if (Number(integral) > 0) {
                zeroCount = 0;
                for (i = 0; i < integral.length; i++) {
                    p = integral.length - i - 1;
                    d = integral.substr(i, 1);
                    quotient = p / 4;
                    modulus = p % 4;
                    if (d == "0") {
                        zeroCount++;
                    } else {
                        if (zeroCount > 0) {
                            outputCharacters += digits[0];
                        }
                        zeroCount = 0;
                        outputCharacters += digits[Number(d)] + radices[modulus];
                    }
                    if (modulus == 0 && zeroCount < 4) {
                        outputCharacters += bigRadices[quotient];
                        zeroCount = 0;
                    }
                }
                outputCharacters += CN_DOLLAR;
            }
            if (decimal != "") {
                for (i = 0; i < decimal.length; i++) {
                    d = decimal.substr(i, 1);
                    if (d != "0") {
                        outputCharacters += digits[Number(d)] + decimals[i];
                    }
                }
            }
            if (outputCharacters == "") {
                outputCharacters = CN_ZERO + CN_DOLLAR;
            }
            if (decimal == "") {
                outputCharacters += CN_INTEGER;
            }
            return outputCharacters;
        }
    });
    //日期对象格式化
    Date.prototype.format = function(fmt) {
        if ($.isEmpty(fmt))
            fmt = 'yyyy-MM-dd hh:mm:ss';
        var o = {
            'M+': this.getMonth() + 1, //月份
            'd+': this.getDate(), //日
            'h+': this.getHours(), //小时
            'm+': this.getMinutes(), //分
            's+': this.getSeconds(), //秒
            'q+': Math.floor((this.getMonth() + 3) / 3), //季度
            'S': this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        }
        for (var k in o) {
            if (new RegExp("(" + k + ")").test(fmt)) {
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            }
        }
        return fmt;
    };
    //日期相关工具
    $.extend({
        DateFormat: function(date, fmt) {
            return date.format(fmt);
        },
        DateNow: function() {
            return new Date().format();
        }
    });
    //操作顶层窗口标签页
    $.extend({
        //打开标签页
        openTab: function(window, href, title) {
            thisw = window;
            $link = $('<a></a>');
            $link.addClass('J_menuItem');
            $link.attr({ 'href': href, 'data-index': window.tabsObj.maxIndex++ });
            $link.html(title);
            $(thisw.document).find('body').append($link);
            $link.hide();
            window.tabsObj.menuItem.call($link);
        },
        //关闭标签页
        closeTab: function(window) {
            window.tabsObj.closetab.call($(window.document).find('.active.J_menuTab i')[0]);
        }
    });
    //一些小工具方法合集，依赖Layer插件
    $.extend({
        showSucMsg: function(msg, option) {
            if ($.isPlainObject(option) && 'yes' in option) {
                var func = option.yes;
                if (typeof func === 'function') {
                    function funcc(i, l) {
                        func.call(this, i, l);
                        layer.close(i);
                    }
                    //覆盖原始yes回调
                    $.extend(option, {
                        yes: funcc
                    });
                }
            }
            layer.alert('' + msg, $.extend({}, {
                icon: 1,
                title: '成功'
            }, option));
        },
        showErrMsg: function(msg, option) {
            this.showSucMsg(msg, $.extend({}, {
                icon: 5,
                title: '提示'
            }, option));
        }
    });
    $.extend({
        ajaxErr: function(err, status) {
            if (err.status != 200) {
                if (err.status == 0 || err.status == 404) {
                    $.showErrMsg((status == 'timeout') ? $.tips.timeoutErr : $.tips.networkErr);
                } else {
                    if ($.isEmpty(err.responseJSON)) $.showErrMsg($.tips.err);
                    else $.showErrMsg('message:' + err.responseJSON.message);
                }
            } else {
                window.location.href = 'logout';
            }
        }
    });
    $.extend({
        AjaxGet: function(url, dataObj, sucCallback, option) {
            $.ajax($.extend({
                type: 'GET',
                timeout: 30000,
                url: url,
                data: dataObj,
                dataType: 'json',
                success: sucCallback,
                error: $.ajaxErr
            }, option));
        }
    });
    $.extend({
        Ajax: function(url, jsonObj, sucCallback, option) {
            $.AjaxGet(url, {}, sucCallback, $.extend({
                type: 'POST',
                data: JSON.stringify(jsonObj),
                headers: {
                    Accept: "application/json; charset=utf-8"
                },
                contentType: "application/json; charset=utf-8",
            }, option));
        }
    });
    /*
     *表单验证，依赖JQValidate插件、Layer插件
     *@Author wx 2017.08.16
     */
    $.fn.fzValidate = function(options) {
        return this.each(function() {
            var $this = $(this);
            var that = this;
            var defaults = {
                type: 'form',
                errmsgId: null
            };
            var opts = $.extend({}, defaults, options);
            var vObj;
            var errboxid = (opts.errmsgId || $this.attr('id')) + 'ErrMsg';
            //渲染
            function render() {
                if (opts.type === 'modal') {
                    vObj = $this.validate($.extend({
                        focusInvalid: false,
                        focusCleanup: true,
                        showErrors: function(errorMap, errorList) {
                            $('#' + errboxid).empty();
                            //仅显示第一个错误信息
                            for (var i = errorList.length - 1; i >= 1; i--) {
                                errorList[i].message = '';
                            }
                            this.defaultShowErrors();
                        },
                        submitHandler: function() {},
                        errorLabelContainer: $('#' + errboxid)
                    }, opts));
                } else {
                    //从表单项的validate属性取得验证规则
                    $.metadata.setType('attr', 'validate');
                    $('body').append('<div id=' + errboxid + ' style="display:none;"><div>');
                    vObj = $this.validate($.extend({
                        focusInvalid: false,
                        focusCleanup: true,
                        showErrors: function(errorMap, errorList) {
                            layer.closeAll('tips');
                            if (errorList.length) {
                                //仅显示第一个错误信息
                                var ele = errorList[0].element;
                                var msg = errorList[0].message;
                                if ($(ele).is(":hidden") || ele.name == 'SEX') {
                                    //如果被验证表单元素被隐藏则弹框提示
                                    $.showErrMsg(msg);
                                } else {
                                    //否则tips提示
                                    layer.tips(msg, ele, {
                                        tips: [2, '#cc5965'],
                                        time: 0
                                    });
                                }
                            }
                            for (var i = 0; i < errorList.length; i++) {
                                errorList[i].message = '';
                            }
                            this.defaultShowErrors();
                        },
                        submitHandler: function() {},
                        errorLabelContainer: $('#' + errboxid)
                    }, opts));
                }
            }
            //选项检查
            function checkOpts() {
                if (opts.type != 'modal' && opts.type != 'form') {
                    throw new TypeError('err: type error!only \'form\' or \'modal\'');
                }
                return true;
            }
            //初始化
            function init() {
                $.validator.setDefaults({
                    ignore: ''
                });
                render();
                //执行验证
                that.valid = function() {
                    $this.submit();
                    return vObj.valid();
                };
                that.reset = function() {
                    vObj.resetForm();
                };
            }
            if (checkOpts()) init();
        });
    };
    //常用组件工具方法
    /*
     *清空表单所有输入框内容
     *@Author wx 2017.07.19
     */
    $.fn.clearFormInput = function(id) {
        return this.each(function() {
            if (this.nodeName.toLowerCase() != 'form') {
                throw new TypeError('err: not a form element!');
            }
            $(this).find('input:text,input:password,input:file,textarea').each(function() {
                var nodename = this.nodeName.toLowerCase();
                if (nodename == 'input' || nodename == 'textarea') $(this).val('');
            });
        });
    };
    /*
     *表格组件
     *@Author wx 2017.07.27
     */
    $.fn.fzTable = function(options) {
        return this.each(function() {
            var loaded = false;
            var $this = $(this);
            var that = this;
            var defaults = {
                url: '',
                columns: [],
                pageFlg: false,
                indexFlg: false,
                striped: true,
                singleSelect: true,
                clickToSelect: true,
                dataType: 'json',
                selectItemName: 'checked',
                idField: 'id'
            };
            var opts = $.extend({}, defaults, options);
            //表格的id与index的查找数组
            var roleIdIndexArray = [];
            //渲染
            function render() {
                $this.bootstrapTable(opts);
                $this.bootstrapTable('uncheckAll');
            }
            //选项检查
            function checkOpts() {
                if (opts.url.length <= 0) {
                    throw new TypeError('err: url content undefined!');
                }
                if (opts.columns.length <= 0) {
                    throw new TypeError('err: columns content undefined!');
                }
                return true;
            }
            //用于fzTable多列偏序时转换请求参数
            function getSortQueryUrl(url, sorts) {
                var count = 0;
                var arr = $.map(sorts, function(n) {
                    return (n.sortOrder == 'both') ? (count++, null) : 'sort=' + n.sortName + ',' + n.sortOrder;
                });
                var idx = url.indexOf('?');
                return (idx == -1 ? url : url.substr(0, idx)) + (count == sorts.length ? '' : '?' + arr.join('&'));
            }
            //处理多列排序请求参数URL
            function multiSortOpts() {
                opts.sortPriority = $.map(opts.columns, function(n) {
                    return (('sortable' in n) && n.sortable == true) ? {
                        sortName: n.field,
                        sortOrder: ('sortorder' in n) ? ((n.sortorder == 'asc' || n.sortorder == 'desc') ? n.sortorder : 'both') : 'both'
                    } : null;
                });
                if (opts.sortPriority.length != 0) {
                    opts.url = getSortQueryUrl(opts.url, opts.sortPriority);
                    $this.on('sort.bs.table', function(e, name, order, opt) {
                        $.each(opt.sortPriority, function(i, n) {
                            if (name == n.sortName)
                                n.sortOrder = order;
                        });
                        opt.url = getSortQueryUrl(opt.url, opt.sortPriority);
                    });
                }
            }
            //处理选项
            function initOptions() {
                //处理多列排序请求参数URL
                multiSortOpts();
                if (opts.indexFlg)
                    opts.columns.unshift({
                        field: 'index',
                        title: '行',
                        formatter: function(value, row, index) {
                            //准备表格的id与index的查找数组
                            roleIdIndexArray.push({
                                key: row.id,
                                value: index
                            });
                            return index + 1;
                        }
                    });
                if (opts.pageFlg)
                    opts = $.extend({}, {
                        queryParamsType: '',
                        pagination: opts.pageFlg,
                        sidePagination: 'server',
                        pageSize: 10,
                        pageList: [10, 25, 50, 100, 'All'],
                        responseHandler: function(res) {
                            //数据处理
                            var rdata = {};
                            rdata.total = res.totalElements;
                            $.map(res.content, function(n) {
                                n.index = 0;
                                return n;
                            });
                            rdata.rows = res.content;
                            //加载完成
                            loaded = true;
                            return rdata;
                        },
                        queryParams: function(params) {
                            //请求参数处理
                            var rdata = {};
                            rdata.page = params.pageNumber - 1;
                            rdata.size = params.pageSize;
                            return rdata;
                        }
                    }, opts);
                else
                    opts = $.extend({}, {
                        responseHandler: function(res) {
                            var res = $.map(res, function(n) {
                                n.index = 0;
                                return n;
                            });
                            //加载完成
                            loaded = true;
                            return res;
                        }
                    }, opts);
            }
            //初始化
            function init() {
                initOptions();
                render();
                that.isLoaded = function() {
                    return loaded;
                };
                that.refresh = function(params) {
                    $this.bootstrapTable('refresh', params);
                };
                that.hideHeader = function() {
                    $this.parent().parent().find('.fixed-table-header').hide();
                    $this.removeAttr('style');
                }
                //获取选中行的数据数组，如果只有一行数据则直接返回数据
                that.getChooseRows = function(isArr) {
                    var rows = $this.bootstrapTable('getSelections');
                    return ((!(isArr || false) && rows.length == 1) || opts.singleSelect) ? rows[0] : rows;
                };
                //获取选中行的数据中指定KEY数组
                that.getChooseRowsKeys = function(key) {
                    return $.map(that.getChooseRows(true), function(n) {
                        return n[key];
                    });
                };
                //获取选中行的数据ID数组
                that.getChooseRowsIds = function() {
                    return that.getChooseRowsKeys('id');
                };
                //判断表格是否选择了一条记录
                that.isChooseOneRow = function() {
                    return ($this.bootstrapTable('getSelections').length != 1) ? false : true;
                };
                //通过ID列表选中表格行
                that.checkTableRowsByIds = function(ids) {
                    if (roleIdIndexArray.length == 0 || opts.indexFlg == false) return;
                    if (opts.indexFlg)
                        $this.bootstrapTable('hideColumn', 'index');
                    $this.bootstrapTable('uncheckAll');
                    $.each($.map(ids, function(n) {
                        var index = -1;
                        $.each(roleIdIndexArray, function(i, m) {
                            if (n == m.key) index = m.value;
                        });
                        return index;
                    }), function(i, n) {
                        $this.bootstrapTable('check', n);
                    });
                };
                //全部取消勾选
                that.uncheckAll = function() {
                    $this.bootstrapTable('uncheckAll');
                };
                //隐藏index列
                that.hideIndexColumn = function() {
                    $this.bootstrapTable('hideColumn', 'index');
                };
                //刷新视图
                that.resetView = function(height) {
                    $this.bootstrapTable('resetView', {
                        height: height
                    });
                };
            }
            if (checkOpts()) init();
        });
    };
    /*
     *树表组件
     *@Author wx 2017.07.17
     */
    $.fn.fzTreeTable = function(options) {
        var defaults = {
            url: '',
            columns: [],
            keys: ['', '', ''],
            height: 0,
            expandable: true,
            initialState: 'expanded',
            selectedRowHandler: function(id, deep, data) {},
            unSelectedRowHandler: function(id, deep, data) {},
            trHandler: function(node) {}
        };
        var treeData = []; //存储后台加载的数据
        var ttDatas = {}; //存储所有节点数据的map，id为KEY
        var opts = $.extend({}, defaults, options);
        var $this = $(this);
        var that = this;
        var selectedId = null; //缓存已经点击的行的ID
        //渲染表格一行
        function addTr(jqDom, node, deep) {
            var $tr = $('<tr></tr>');
            var id = node[opts.keys[0]];
            var pid = node[opts.keys[1]];
            if (node[opts.keys[0]] != null) $tr.attr('data-tt-id', id);
            if (node[opts.keys[1]] != null) $tr.attr('data-tt-parent-id', pid);
            ttDatas[id] = { id: id, parentId: pid, deep: deep, nodeDataObj: node };
            for (var i = 0; i < opts.columns.length; i++) {
                var $td = $('<td></td>');
                var text = null;
                var value = node[opts.columns[i].field];
                if ('formatter' in opts.columns[i]) {
                    var func = opts.columns[i].formatter;
                    if (typeof func === 'function') {
                        text = func.call($this, value);
                    }
                }
                $td.html((text != null) ? text : (value != null) ? value : '');
                if ('visible' in opts.columns[i]) {
                    var flg = opts.columns[i].visible;
                    if (typeof flg === 'boolean') {
                        flg ? ($tr.append($td), !0) : '';
                    } else {
                        $tr.append($td);
                    }
                } else {
                    $tr.append($td);
                }
                ttDatas[id][opts.columns[i].field] = value;
            }
            if (typeof opts.trHandler === 'function') {
                opts.trHandler.call($tr, node);
            }
            jqDom.append($tr);
        }
        //先序遍历JSON树
        function traverse(ttDom, node, deep) {
            addTr(ttDom, node, deep++);
            var sub = node[opts.keys[2]];
            for (var i in sub) {
                traverse(ttDom, sub[i], deep);
            }
        }
        //选项检查
        function checkOpts() {
            if (opts.url.length <= 0) {
                throw new TypeError('err: url content undefined!');
            }
            if (opts.columns.length <= 0) {
                throw new TypeError('err: columns content undefined!');
            }
            var keysFlg = true;
            for (var i = 0; i < opts.keys.length; i++) {
                keysFlg = keysFlg && (!$.isEmpty(opts.keys[i] && opts.keys[i].length > 0));
            }
            if (!keysFlg) {
                throw new TypeError('err: keys content undefined!');
            }
            return true;
        }
        //重新渲染
        function reRender(height) {
            if ($.isZero(height)) return;
            $this.parent().css({ 'height': height + 'px', 'overflow-y': 'auto' });
        }
        //渲染表头
        function renderHeader() {
            var $thead = $('<thead><tr></tr></thead>');
            for (var i = 0; i < opts.columns.length; i++) {
                var $th = $('<th></th>');
                var html = opts.columns[i].title;
                $th.html((html != null) ? html : '');
                if ('visible' in opts.columns[i]) {
                    var flg = opts.columns[i].visible;
                    if (typeof flg === 'boolean') {
                        flg ? ($thead.children().append($th), !0) : '';
                    } else {
                        $thead.children().append($th);
                    }
                } else {
                    $thead.children().append($th);
                }
            }
            $this.append($thead);
        }
        //渲染表格内容
        function renderBody() {
            //树深度deep从0开始
            traverse($this, treeData[0], 0);
        }
        //从原始树数据中删除指定ID的内容
        function deleteNodeDataByID(id) {
            var findFlg = false;
            var delFlg = false;
            //先序遍历JSON树
            function traverse(node, deep) {
                if (findFlg) return;
                if (id == node.id) {
                    if (deep == 0) throw new TypeError('err: can\'t delete root node!');
                    findFlg = true;
                    return;
                }
                var sub = node[opts.keys[2]];
                for (var i in sub) {
                    traverse(sub[i], ++deep);
                    if (findFlg && !delFlg) {
                        sub.splice(i, 1);
                        delFlg = true;
                        return;
                    }
                }
            }
            traverse(treeData[0], 0);
        }
        //渲染后
        function afterRender(reflg) {
            $this.treetable(opts, reflg);
            if (!reflg) {
                //增加父容器
                $this.wrap("<div></div>");
                $this.on("mousedown", "tbody tr", function(e) {
                    $(".selected").not(this).removeClass("selected");
                    $(this).toggleClass("selected");
                    //如果选中了一条数据则触发selected事件
                    if (that.isChooseOneNode()) {
                        if (typeof opts.selectedRowHandler === 'function') {
                            selectedId = that.getChooseNodeId();
                            opts.selectedRowHandler.call($this, selectedId, that.getNodeDeep(selectedId), that.getChooseNodeData());
                        }
                    } else {
                        //否则触发unselected事件
                        if (typeof opts.unSelectedRowHandler === 'function') {
                            opts.unSelectedRowHandler.call($this, selectedId, that.getNodeDeep(selectedId), that.getNodeData(selectedId));
                        }
                    }
                });
                //根据id返回节点的Data数据
                that.getNodeData = function(id) {
                    var ret = $.extend(true, {}, ttDatas[id]);
                    delete ret.nodeDataObj;
                    return ret;
                };
                //根据id返回节点的原始Data数据
                that.getNodeDataObj = function(id) {
                    return ttDatas[id].nodeDataObj;
                };
                //返回当前选中节点ID
                that.getChooseNodeId = function() {
                    return ($this.find('.selected').length != 1) ? null : $this.find('.selected')[0].dataset.ttId
                };
                //获取treetable树表选中行的数据数组
                that.getChooseNodeData = function() {
                    return that.getNodeData(that.getChooseNodeId());
                };
                //判断treetable树表是否选择了一条记录
                that.isChooseOneNode = function() {
                    return that.getChooseNodeId() === null ? false : true;
                };
                //返回当前节点的父节点ID
                that.getNodeParentId = function(id) {
                    return (that.getNodeData(id).parentId == 1) ? null : that.getNodeData(id).parentId;
                };
                //返回当前节点的深度
                that.getNodeDeep = function(id) {
                    return that.getNodeData(id).deep;
                };
                //是否叶子节点
                that.isLeafNode = function(id) {
                    return $('tr[data-tt-id="' + id + '"]').attr("class").match('leaf') ? true : false;
                };
                //是否根节点
                that.isRootNode = function(id) {
                    return (that.getNodeDeep(id) == 0) ? true : false;
                };
                //重绘表格高度
                that.resetHeight = function(height) {
                    reRender(height);
                };
                //刷新树表数据
                that.reloadTreeTable = function(height) {
                    height ? opts.height = height : '';
                    $this.empty();
                    init(true);
                };
                //刷新树表视图
                that.refreshTreeTableView = function(height) {
                    height ? opts.height = height : '';
                    $this.empty();
                    render(true);
                };
                //删除树表节点
                that.removeNode = function(id) {
                    $this.treetable("removeNode", id);
                    deleteNodeDataByID(id);
                    that.refreshTreeTableView();
                };
            }
            reRender(opts.height);
        }
        //主渲染
        function render(flg) {
            renderHeader();
            renderBody();
            afterRender(flg);
        }
        //初始化
        function init(flg) {
            $.Ajax(opts.url, {}, function(data) {
                treeData = [data];
                render(flg);
            });
        }
        if (checkOpts()) init(false);
    };
    /*
     *下拉列表组件
     *@Author wx 2017.07.19
     */
    $.fn.fzSelect = function(options) {
        return this.each(function() {
            var defaults = {
                data: {},
                valueField: 'key',
                textField: 'value',
                allflg: false,
                allkv: ['0', '全部']
            };
            var opts = $.extend({}, defaults, options);
            var $this = $(this);
            var that = this;
            //渲染
            function render() {
                $this.empty();
                if (opts.allflg) {
                    var $all = $('<option></option>');
                    $all.attr('value', opts.allkv[0]);
                    $all.html(!$.isEmpty(opts.allkv[1]) ? opts.allkv[1] : '');
                    $this.append($all);
                }
                if (opts.data instanceof Array) {
                    for (var i = 0; i < opts.data.length; i++) {
                        var v = opts.data[i];
                        var key = v[opts.valueField];
                        var txt = v[opts.textField];
                        var $option = $('<option></option>');
                        !$.isEmpty(key) ? $option.attr('value', key) : $option.attr('value', ($.isEmpty(v) ? '' : v));
                        $option.html(!$.isEmpty(txt) ? txt : ($.isEmpty(v) ? '' : v));
                        $this.append($option);
                    }
                } else {
                    for (var key in opts.data) {
                        var $option = $('<option></option>');
                        !$.isEmpty(key) ? $option.attr('value', key) : '';
                        $option.html(!$.isEmpty(opts.data[key]) ? opts.data[key] : '');
                        $this.append($option);
                    }
                }
            }
            //选项检查
            function checkOpts() {
                if (opts.allflg) {
                    if (opts.allkv.length != 2) {
                        throw new TypeError('err: allkv content undefined!');
                    }
                }
                return true;
            }
            //初始化
            function init() {
                render();
                that.refreshSelect = function(data) {
                    opts.data = data;
                    render();
                };
                that.getVal = function() {
                    return $this.find("option:selected").val();
                };
                that.getText = function() {
                    return $this.find("option:selected").text();
                };
            }
            if (checkOpts()) init();
        });
    };
    /*
     *树组件
     *@Author wx 2017.07.21
     */
    $.fn.fzTree = function(options) {
        return this.each(function() {
            var defaults = {
                url: '',
                keys: ['', '', ''],
                maxHeight: 0,
                singleSelect: false,
                checkedLink: true, //是否级联勾选
                checkedLinkParents: true, //是否级联勾选父节点
                checkedLinkChildrens: true, //是否级联勾选子节点
                checkLinkParents: true, //勾选时是否级联勾选父节点
                chechLinkChildrens: true, //勾选时是否级联勾选子节点
                unCheckLinkParents: true, //取消勾选时是否级联取消勾选父节点
                unChechLinkChildrens: true, //取消勾选时是否级联取消勾选子节点
                queryParams: {}, //查询参数
                treeData: null,
                textFormatter: function(node, val, index) {
                    return val;
                },
                ajaxSucCallback: function() {}
            };
            var opts = $.extend({}, defaults, options);
            var checkedLinkOption = opts.checkedLink;
            var loaded = false;
            var $this = $(this);
            var that = this;
            //选择事件
            function selectedEventHandler(node) {
                $this.treeview('toggleNodeExpanded', node.nodeId, { silent: true });
            }
            //取消选中事件
            function nodeSelectedEventHandler(e, node) {
                selectedEventHandler(node);
            }
            //取消选中事件
            function nodeUnselectedEventHandler(e, node) {
                selectedEventHandler(node);
            }
            var nodeCheckedSilent = false,
                nodeUncheckedSilent = false;
            //选中事件
            function nodeCheckedEventHandler(e, node) {
                if (nodeCheckedSilent) {
                    return;
                }
                nodeCheckedSilent = true;
                if (opts.singleSelect) {
                    //取消勾选其他节点
                    $this.treeview('uncheckAll', { silent: true });
                    //勾选当前节点
                    $this.treeview('checkNode', node.nodeId, { silent: true });
                    nodeCheckedSilent = false;
                    return;
                }
                if (opts.checkedLink && opts.checkedLinkParents && opts.checkLinkParents)
                    checkAllParent(node);
                if (opts.checkedLink && opts.checkedLinkChildrens && opts.chechLinkChildrens)
                    checkAllSon(node);
                showHalfCheckedIcon($this.treeview('getNode', 0));
                nodeCheckedSilent = false;
            }
            //取消选中事件
            function nodeUncheckedEventHandler(e, node) {
                if (nodeUncheckedSilent)
                    return;
                nodeUncheckedSilent = true;
                if (opts.checkedLink && opts.checkedLinkParents && opts.unCheckLinkParents)
                    unCheckAllParent(node);
                if (opts.checkedLink && opts.checkedLinkChildrens && opts.unChechLinkChildrens)
                    unCheckAllSon(node);
                showHalfCheckedIcon($this.treeview('getNode', 0));
                nodeUncheckedSilent = false;
            }
            //同步半选择状态
            function syncHalfCheckedIcon(node) {
                var parentNode = $this.treeview('getParent', node.nodeId);
                var checkedCount = 0; //子节点勾选个数
                var halfChecked = false;
                var childrens = parentNode.nodes;
                for (var i in childrens) {
                    if (childrens[i].state.checked) {
                        checkedCount++;
                    }
                    if (childrens[i].state.halfChecked)
                        halfChecked = true;
                }
                if (!("state" in parentNode)) return;
                parentNode.state.halfChecked = ((checkedCount == 0 || checkedCount == childrens.length) && !halfChecked) ? false : true;
            }
            //显示半选择Icon
            function showHalfCheckedIcon(node) {
                for (var i in node.nodes) {
                    showHalfCheckedIcon(node.nodes[i]);
                }
                syncHalfCheckedIcon(node);
            }
            //选中全部父节点
            function checkAllParent(node) {
                $this.treeview('checkNode', node.nodeId, { silent: true });
                var parentNode = $this.treeview('getParent', node.nodeId);
                if (!("nodeId" in parentNode)) {
                    return;
                } else {
                    checkAllParent(parentNode);
                }
            }
            //取消全部父节点
            function unCheckAllParent(node) {
                $this.treeview('uncheckNode', node.nodeId, { silent: true });
                var siblings = $this.treeview('getSiblings', node.nodeId);
                var parentNode = $this.treeview('getParent', node.nodeId);
                if (!("nodeId" in parentNode)) {
                    return;
                }
                var isAllUnchecked = true; //是否全部没选中
                for (var i in siblings) {
                    if (siblings[i].state.checked) {
                        isAllUnchecked = false;
                        break;
                    }
                }
                if (isAllUnchecked) {
                    unCheckAllParent(parentNode);
                }
            }
            //选中所有子节点
            function checkAllSon(node) {
                $this.treeview('checkNode', node.nodeId, { silent: true });
                if (node.nodes != null && node.nodes.length > 0) {
                    for (var i in node.nodes) {
                        checkAllSon(node.nodes[i]);
                    }
                }
            }
            //取消选中所有子节点
            function unCheckAllSon(node) {
                $this.treeview('uncheckNode', node.nodeId, { silent: true });
                if (node.nodes != null && node.nodes.length > 0) {
                    for (var i in node.nodes) {
                        unCheckAllSon(node.nodes[i]);
                    }
                }
            }
            //选项检查
            function checkOpts() {
                // throw new TypeError('err: allkv content undefined!');
                return true;
            }
            /*
             *获取BootstrapTreeViewDataJson
             *@Param data(JsonObj) 待转换的JSON数据
             *@Param id(String) 转换时id键值
             *@Param text(String) 转换时text键值
             *@Param children(String) 转换时子节点nodes键值
             *@Param func(Function(node)) 转换时text格式化函数，参数为当前node节点
             */
            function treeViewDataJson(data, id, text, children, func) {
                if ($.isEmpty(data) || $.isEmpty(text) || $.isEmpty(children))
                    throw new TypeError('err: no content in data or text or children!');
                //先序遍历转换
                function traverse(node) {
                    var nodeArray = [];
                    for (var i = 0; i < node.length; i++) {
                        var subNode = [];
                        if (node[i][children].length != 0) {
                            subNode = traverse(node[i][children]);
                        }
                        var txt = node[i][text];
                        if (typeof func === 'function') {
                            txt = func.call(node, node, txt, i);
                        }
                        var nodeObj = { dataId: node[i][id], text: txt };
                        if (subNode.length != 0) nodeObj.nodes = subNode;
                        nodeArray.push(nodeObj);
                    }
                    return nodeArray;
                }
                return traverse(data);
            }
            //建立nodeId(key)与dataId的关联Array用于后续的查找
            function treeViewDataArray(data) {
                var arr = [];
                //先序遍历JSON树
                function traverse(node) {
                    if ('dataId' in node && 'nodeId' in node) {
                        arr.push({
                            key: node.dataId,
                            value: node.nodeId
                        });
                        var sub = node.nodes;
                        for (var i in sub) {
                            traverse(sub[i]);
                        }
                    }
                }
                traverse(data);
                return arr;
            }
            //根据dataId查找nodeId
            function findNodeIdByDataId(id) {
                var nodeId;
                $.each(opts.idArray, function(i, n) {
                    if (id == n.key) nodeId = n.value;
                });
                return nodeId;
            }
            //渲染
            function render(data) {
                $this.treeview($.extend({
                    data: opts.data,
                    levels: 2,
                    collapseIcon: 'glyphicon glyphicon-chevron-down',
                    expandIcon: 'glyphicon glyphicon-chevron-right',
                    halfCheckedIcon: 'glyphicon glyphicon-modal-window',
                    showIcon: false,
                    highlightSelected: false,
                    multiSelect: true,
                    showBorder: false,
                    showCheckbox: true,
                    onNodeChecked: nodeCheckedEventHandler,
                    onNodeUnchecked: nodeUncheckedEventHandler,
                    onNodeSelected: nodeSelectedEventHandler,
                    onNodeUnselected: nodeUnselectedEventHandler
                }, opts));
                (opts.maxHeight > 0) ? ($this.css({ 'max-height': opts.maxHeight + 'px', 'overflow-y': 'auto' }), !0) : '';
            }
            //处理treeData数据
            function handledata(data) {
                if ($.isEmpty(data))
                    throw new TypeError('err: err treeData!');
                opts.data = treeViewDataJson([data], opts.keys[0], opts.keys[1], opts.keys[2], opts.textFormatter);;
                render();
                opts.idArray = treeViewDataArray($this.treeview('getNode', 0));
                var func = opts.ajaxSucCallback;
                if (typeof func === 'function') {
                    func.call(this, data);
                }
                loaded = true;
            }
            //初始化
            function init() {
                if (opts.url != '')
                    $.Ajax(opts.url, opts.queryParams, function(data) {
                        handledata(data);
                    });
                else
                    handledata(opts.treeData)
                //获取选中节点的dataIds
                that.getDataIdsFromCheckedNodes = function() {
                    var ids = [];
                    $.each($this.treeview('getChecked'), function(i, n) {
                        ids.push(n.dataId);
                    });
                    return ids;
                };
                //获取选中节点的dataTexts
                that.getDataTextsFromCheckedNodes = function() {
                    var tests = [];
                    $.each($this.treeview('getChecked'), function(i, n) {
                        tests.push(n.text);
                    });
                    return tests;
                };
                //根据指定id列表选中树节点
                that.setCheckedNodesByIds = function(ids) {
                    if ($.isEmpty(ids)) return;
                    if (opts.idArray.length == 0) throw new TypeError('err: not set treeview data!');
                    $this.treeview('uncheckAll', { silent: true });
                    $this.treeview('expandAll', { levels: 1, silent: true });
                    $.each(ids, function(i, n) {
                        var id = findNodeIdByDataId(n);
                        if (!$.isEmpty(id)) {
                            opts.checkedLink = false;
                            $this.treeview('checkNode', id, { silent: true });
                            opts.checkedLink = checkedLinkOption;
                        }
                    });
                };
                //根据指定data列表选中树节点
                that.setCheckedNodesByData = function(datas) {
                    if ($.isEmpty(datas)) return;
                    var ids = [];
                    $.each(datas, function(i, n) {
                        ids.push(n[opts.keys[0]]);
                    });
                    that.setCheckedNodesByIds(ids);
                };
                //全部取消勾选
                that.uncheckAll = function() {
                    $this.treeview('uncheckAll', { silent: true });
                };
                //树数据是否加载完成
                that.isLoaded = function() {
                    return loaded;
                };
            }
            if (checkOpts()) init();
        });
    };
    /*
    *文件上传组件，依赖common-page.css及ajaxfileupload.js及md5.js
    @Author wx 2017.08.10
    */
    $.fn.fzFile = function(options) {
        return this.each(function() {
            var defaults = {
                url: '',
                upBtn: true,
                width: '100%',
                pathName: 'filePath',
                name: 'file',
                maxSize: 0,
                placeHolder: '未选择任何文件',
                fileNameCheckRegExp: '',
                fileNameCheckErrMsg: '选择的文件名格式不正确',
                sucCallback: function() {},
                failCallback: function() {}
            };
            var opts = $.extend({}, defaults, options);
            var $this = $(this);
            var that = this;
            var content = null;
            var id = $this.attr('id');
            var path, hide, btnup, file;
            var upSucFlg = false;
            //渲染
            function render() {
                var $filebox = $('<div id="' + id + md5($.DateNow()) + '"></div>');
                $filebox.addClass('fzfilebox');
                $filebox.css({ width: opts.width });
                var $path = $('<input id="' + id + md5($.DateNow()) + 'path" type="text" placeholder="' + opts.placeHolder + '" readonly>');
                $path.addClass('form-control fzfiletxt');
                $filebox.append($path);
                path = $path;
                var $btnfun = $('<button id="' + id + md5($.DateNow()) + 'btnfun" type="button"><i class="fa fa-folder-open-o"></i>选择</button>')
                $btnfun.addClass('btn btn-success fzfilebtn fzfilefun');
                $filebox.append($btnfun);
                if (opts.upBtn) {
                    var $btnup = $('<button id="' + id + md5($.DateNow()) + 'btnup" type="button"><i class="fa fa-upload"></i>上传</button>')
                    $btnup.addClass('btn btn-success fzfilebtn fzfileup');
                    $filebox.append($btnup);
                    $btnup.click(function() {
                        doupload();
                    });
                    btnup = $btnup;
                }
                var $file = $('<input id="' + id + md5($.DateNow()) + 'file" type="file">');
                $file.addClass('fzfile');
                $file.attr('name', opts.name);
                $filebox.append($file);
                $file.change(function() {
                    $path.val($file.val());
                    if (!opts.upBtn) {
                        doupload();
                    }
                });
                file = $file;
                var $hide = $('<input id="' + id + md5($.DateNow()) + 'hide" type="hidden" value="">');
                $hide.attr('name', opts.pathName);
                $filebox.append($hide);
                hide = $hide;
                return $filebox;
            }
            //检查文件名
            function pathCheck(str, isSave) {
                //取出文件名
                var filename = str.substring((str.lastIndexOf('\\') == -1 ? str.lastIndexOf('/') : str.lastIndexOf('\\')) + 1, str.lastIndexOf("."));
                isSave ? (filename = filename.substr(15), 0) : 0;
                return new RegExp('^' + opts.fileNameCheckRegExp + '.*').test(filename) ? true : false;
            }
            //重置组件
            function fix() {
                $this.empty();
                $this.append(render());
                upSucFlg = false;
            }
            //执行上传请求
            function doupload() {
                if ($.isEmpty(file.val())) {
                    $.showErrMsg('请选择文件！');
                    return;
                }
                var maxSize = opts.maxSize * 1024 * 1024;
                if (maxSize > 0 && file[0].files[0].size > maxSize) {
                    $.showErrMsg('文件大小超过' + opts.maxSize + 'M');
                    return;
                }
                if (!pathCheck(file.val(), false)) {
                    $.showErrMsg(opts.fileNameCheckErrMsg);
                    return;
                }
                if (opts.upBtn) btnup.prop("disabled", true);
                $.ajaxFileUpload($.extend({
                    url: opts.url,
                    secureuri: false,
                    fileElementId: file.attr('id'),
                    dataType: 'json',
                    type: 'post',
                    success: function(data) {
                        fix();
                        if (typeof(data.success) != 'undefined') {
                            if ($.isEmpty(data.success) || data.success == 'false') {
                                //上传失败
                                var func = opts.failCallback;
                                if (typeof func === 'function') {
                                    func.call(this, data);
                                }
                            } else {
                                //上传成功
                                upSucFlg = true;
                                path.val(data.path);
                                hide.val(data.path);
                                var func = opts.sucCallback;
                                if (typeof func === 'function') {
                                    func.call(this, data);
                                }
                            }
                        }
                    },
                    error: function(err, status) {
                        fix();
                        $.ajaxErr(err, status);
                    }
                }, opts));
            }
            //选项检查
            function checkOpts() {
                if (opts.url.length <= 0) {
                    throw new TypeError('err: url content undefined!');
                }
                return true;
            }
            //初始化
            function init() {
                $this.append(render());
                //获取文件路径
                that.getFilePath = function() {
                    return upSucFlg ? hide.val() : '';
                };
                //设置文件路径
                that.setFilePath = function(pathstr) {
                    path.val(pathstr || '');
                    hide.val(path.val());
                    upSucFlg = true;
                };
                //重置组件
                that.reset = function() {
                    fix();
                }
                //重设选项
                that.setOption = function(option) {
                    opts = $.extend({}, opts, option);
                };
                //校验文件名
                that.nameValidate = function() {
                    //如果未上传则不校验
                    if (!upSucFlg) return true;
                    return pathCheck(hide.val(), true);
                };
            }
            if (checkOpts()) init();
        });
    };
})(jQuery);
