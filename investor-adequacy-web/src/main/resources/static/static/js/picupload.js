(function($) {
    /*
     *图片裁剪上传组件，依赖common-page.css及md5.js及ajaxfileupload.js
     */
    $.fn.picUpload = function(options) {
        return this.each(function() {
            var defaults = {
                url: '',
                urlContext: '',
                width: '100%',
                single: false,
                pdfFlg: false,
                pdfMaxSize: 2,
                picCropModalID: 'picCropModal',
                maxSize: 500,
                defaultImgUrl: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQYV2P4////fwAJ+wP9BUNFygAAAABJRU5ErkJggg==',
                btnTip: '点击上传',
                onPicReady: function(domObj) {}
            };
            var opts = $.extend({}, defaults, options);
            var $this = $(this);
            var that = this;
            var id = $this.attr('id');
            var domObjs = [];
            //保存事件处理时的公用参数
            var publicObj = {
                index: null,
                croping: false, //是否在crop中
                base64: ''
            };
            //渲染
            function boxRender(index) {
                //创建文件选择盒子
                var $picbox = $('<div></div>')
                $picbox.attr('id', id + MD5() + 'picbox_' + index);
                $picbox.addClass('picupbox m-b-xs m-r-xs');
                $pdfimg = $('<img style="display:none;" src="' + opts.urlContext + 'static/info/images/pdfup.jpg" class="picupimg">');
                $pdfimg.attr('id', id + MD5() + 'pdfimg_' + index);
                $picbox.append($pdfimg);
                $picimg = $('<img src="' + opts.defaultImgUrl + '" class="picupimg">');
                $picimg.attr('id', id + MD5() + 'picimg_' + index);
                $picbox.append($picimg);
                $picbox.append($('<div class="picuptipbg"></div>'));
                $picbox.append($('<div class="picuptip">' + opts.btnTip + '</div>'));
                var $file = $('<input type="file" name="file" unselectable="on">');
                $file.attr('id', id + MD5() + 'file_' + index);
                $file.addClass('picupfile');
                $file.change(fileChangeHandler);
                $picbox.append($file);
                var $btndel = $('<button type="button" title="删除"><i class="fa fa-times"></i></button>');
                $btndel.attr('id', id + MD5() + 'btndel_' + index);
                $btndel.addClass('btn btn-default btn-circle btn-outline picupdel');
                $btndel.hide();
                $btndel.click(delHandler);
                $picbox.append($btndel);
                var $path = $('<input type="hidden" value="">');
                $path.attr('id', id + MD5() + 'path_' + index);
                $picbox.append($path);
                return {
                    picbox: $picbox,
                    pdfimg: $pdfimg,
                    picimg: $picimg,
                    file: $file,
                    btndel: $btndel,
                    path: $path,
                    id: id,
                    sucFlg: false
                }
            }
            //文件改变事件
            function fileChangeHandler(e) {
                var fileObj = e.currentTarget.files[0];
                if (fileObj == undefined) return;
                if (/[\+\s/\?%#&=]/.test(fileObj.name)) {
                    $.showErrMsg('文件名包含特殊字符(+/?%#&=或空格)');
                    //将文件选择置空
                    e.currentTarget.value = '';
                    return;
                }
                //文件类型过滤
                if (!opts.pdfFlg && ($.isEmpty(fileObj.type) || !/^(image)/.test(fileObj.type))) {
                    $.showErrMsg('仅支持图片格式(jpg/jpeg/png/bmp等)');
                    //将文件选择置空
                    e.currentTarget.value = '';
                    return;
                }
                if (opts.pdfFlg && ($.isEmpty(fileObj.type) || fileObj.type != 'application/pdf')) {
                    $.showErrMsg('仅支持PDF格式');
                    //将文件选择置空
                    e.currentTarget.value = '';
                    return;
                }
                if (opts.pdfFlg && fileObj.size > (opts.pdfMaxSize * 1024 * 1024)) {
                    $.showErrMsg('文件大小超过' + opts.pdfMaxSize + 'M');
                    //将文件选择置空
                    e.currentTarget.value = '';
                    return;
                }
                var index = getIndexById(e.currentTarget.id);
                publicObj.index = index;
                //保存文件对象
                domObjs[index]['fileObj'] = fileObj;
                if (!opts.pdfFlg) {
                    //设置预览
                    var cropimgSrc = getImgUrl(fileObj);
                    $('#cropImg').attr('src', cropimgSrc);
                    //初始化裁剪控件
                    $('#cropImg').cropper('destroy').cropper({
                        minContainerWidth: 630,
                        minContainerHeight: 450, //891
                        minCropBoxWidth: 30,
                        aspectRatio: 210 / 297,
                        autoCropArea: 1,
                        dragMode: 'move',
                        toggleDragModeOnDblclick: false,
                        crop: function(e) {
                            //显示当前截取数据
                            $('#cropWidth').html(Math.round(e.width));
                            $('#cropHeight').html(Math.round(e.height));
                            $('#cropRotate').html(e.rotate);
                            //根据是否裁剪判断是否要计算尺寸
                            $('#cropSize').html(publicObj.croping ? '0.00' : getBase64Size());
                        },
                        cropstart: function(e) {
                            publicObj.croping = true;
                        },
                        cropmove: function(e) {
                            publicObj.croping = true;
                        },
                        cropend: function(e) {
                            $('#cropSize').html(!publicObj.croping ? '0.00' : getBase64Size());
                            publicObj.croping = false;
                        },
                        zoom: function(e) {
                            //当放大30倍后不再缩放
                            if (e.ratio > 30) {
                                e.preventDefault();
                            }
                        },
                        ready: function(e) {
                            toolsEventHandler();
                            //点击裁剪按钮
                            $('#' + opts.picCropModalID + 'Btn').click(function(e) {
                                var size = $('#cropSize').text();
                                if (size > opts.maxSize) {
                                    $.showErrMsg('文件大小不超过' + opts.maxSize + 'KB');
                                    return;
                                }
                                var base64 = getBase64();
                                $('#cropedImg').attr('src', base64);
                                publicObj.base64 = base64;
                                $.Ajax(opts.url, {
                                    imgBase64: base64.split(',')[1],
                                    fileName: domObjs[publicObj.index].fileObj.name
                                }, function(data) {
                                    if (data.success) {
                                        domObjs[publicObj.index].sucFlg = data.success;
                                        $.showSucMsg('裁剪并上传成功');
                                        domObjs[publicObj.index].path.val(data.path);
                                        domObjs[publicObj.index].picimg.attr('src', publicObj.base64);
                                        //显示删除按钮
                                        domObjs[publicObj.index].btndel.show();
                                        $('#' + opts.picCropModalID).modal('hide');
                                    } else {
                                        $.showSucMsg('裁剪上传失败');
                                    }
                                });
                            });
                            //打开弹框
                            $('#' + opts.picCropModalID).modal({ backdrop: 'static', keyboard: false });
                            $('#' + opts.picCropModalID).modal('show');
                            if (typeof opts.onPicReady === 'function') {
                                opts.onPicReady.call($this, domObjs[publicObj.index]);
                            }
                        }
                    });
                    //将文件选择置空
                    e.currentTarget.value = '';
                } else {
                    //执行上传
                    $.ajaxFileUpload({
                        url: opts.url,
                        secureuri: false,
                        fileElementId: domObjs[index].file.attr('id'),
                        dataType: 'json',
                        type: 'post',
                        success: function(data) {
                            $('form[name^="jUpload"]').remove();
                            $('iframe[name^="jUpload"]').remove();
                            resetBox();
                            if (typeof(data.success) != 'undefined' && !$.isEmpty(data.success) && data.success == 'true') {
                                //上传成功
                                domObjs[publicObj.index].sucFlg = true;
                                domObjs[publicObj.index].path.val(data.path);
                                domObjs[publicObj.index].picimg.hide();
                                domObjs[publicObj.index].pdfimg.show();
                                //显示删除按钮
                                domObjs[publicObj.index].btndel.show();
                                $.showSucMsg('PDF上传成功');
                            } else {
                                //上传失败
                                $.showSucMsg('PDF上传失败');
                            }
                        },
                        error: function(err, status) {
                            $('form[name^="jUpload"]').remove();
                            $('iframe[name^="jUpload"]').remove();
                            resetBox();
                            $.ajaxErr(err, status);
                        }
                    });
                }
            }
            //重置box
            function resetBox(data) {
                //将this还原
                $this = $('#' + id);
                that = $this[0];
                $this.empty();
                //缓存数据
                var tempObjs = $.map(domObjs, function(n) {
                    return {
                        sucFlg: n.sucFlg,
                        pathVal: n.path.val()
                    };
                });
                domObjs = [];
                init();
                if (opts.single || tempObjs.length == 1) {
                    domObjs[0].path.val(tempObjs[0].pathVal);
                    domObjs[0].sucFlg = tempObjs[0].sucFlg;
                    if (domObjs[0].sucFlg) {
                        domObjs[0].picimg.hide();
                        domObjs[0].pdfimg.show();
                    }
                } else {
                    for (var i = 0; i < tempObjs.length; i++) {
                        if (i != 0) {
                            addBox(i - 1);
                        }
                        domObjs[i].path.val(tempObjs[i].pathVal);
                        domObjs[i].sucFlg = tempObjs[i].sucFlg;
                        if (domObjs[i].sucFlg) {
                            domObjs[i].picimg.hide();
                            domObjs[i].pdfimg.show();
                        }
                    }
                    showDelBtn();
                }
            }
            //获取Canvas
            function getBase64() {
                return $('#cropImg').cropper('getCroppedCanvas', {
                    fillColor: '#fff',
                    imageSmoothingEnabled: false,
                    imageSmoothingQuality: 'high'
                }).toDataURL(domObjs[publicObj.index].fileObj.type);
            }
            //获取Canvas尺寸大小
            function getBase64Size() {
                return Number(getBase64().length / (1.3 * 1024)).toFixed(2);
            }
            //工具按钮事件
            function toolsEventHandler() {
                $('#crop-tool-reset').click(function() {
                    $('#cropImg').cropper('reset');
                });
                $('#crop-tool-zoomup').click(function() {
                    $('#cropImg').cropper('zoom', 0.1);
                });
                $('#crop-tool-zoomdown').click(function() {
                    $('#cropImg').cropper('zoom', -0.1);
                });
                $('#crop-tool-moveleft').click(function() {
                    $('#cropImg').cropper('move', -10, 0);
                });
                $('#crop-tool-moveright').click(function() {
                    $('#cropImg').cropper('move', 10, 0);
                });
                $('#crop-tool-moveup').click(function() {
                    $('#cropImg').cropper('move', 0, -10);
                });
                $('#crop-tool-movedown').click(function() {
                    $('#cropImg').cropper('move', 0, 10);
                });
                $('#crop-tool-rotate').click(function() {
                    $('#cropImg').cropper('rotate', 90);
                });
                $('#crop-tool-rotateleft').click(function() {
                    $('#cropImg').cropper('rotate', -3);
                });
                $('#crop-tool-rotateright').click(function() {
                    $('#cropImg').cropper('rotate', 3);
                });
            }
            //增加一个Box
            function addBox(index) {
                //往dom中添加一个box
                var addbox = boxRender(index + 1);
                domObjs[index].picbox.after(addbox.picbox);
                //保存到domObjs
                domObjs.splice(index + 1, 0, addbox);
                //重置索引
                resetIndex();
            }
            //增加按钮事件
            function addHandler(e) {
                addBox(domObjs.length - 1);
                showDelBtn();
            }
            //删除按钮事件
            function delHandler(e) {
                //如果是仅剩一个box
                if (domObjs.length == 1) {
                    //删除数据
                    domObjs[0].sucFlg = false;
                    domObjs[0].path.val('');
                    //还原图像
                    domObjs[0].pdfimg.hide();
                    domObjs[0].picimg.show();
                    domObjs[0].picimg.attr('src', opts.defaultImgUrl);
                    //隐藏删除按钮
                    domObjs[0].btndel.hide();
                    return;
                }
                var index = getIndexById(e.currentTarget.id);
                //从dom中删除一个box
                domObjs[index].picbox.remove();
                //保存到domObjs
                domObjs.splice(index, 1);
                //重置索引
                resetIndex();
            }
            //显示删除按钮
            function showDelBtn() {
                //显示删除按钮
                for (var i = 0; i < domObjs.length; i++) {
                    domObjs[i].btndel.show();
                }
            }
            //重置索引
            function resetIndex() {
                for (var i = 0; i < domObjs.length; i++) {
                    changeDomId(domObjs[i].picbox, i);
                    changeDomId(domObjs[i].pdfimg, i);
                    changeDomId(domObjs[i].picimg, i);
                    changeDomId(domObjs[i].file, i);
                    changeDomId(domObjs[i].btndel, i);
                    changeDomId(domObjs[i].path, i);
                }
            }
            //修改对象ID
            function changeDomId(dom, index) {
                dom.attr('id', getIdPref(dom.attr('id')) + index);
            }
            //获取md5
            function MD5() {
                return '_' + md5($.DateNow()) + '_';
            }
            //根据id获取index
            function getIndexById(id) {
                return parseInt(id.substr(id.lastIndexOf('_') + 1));
            }
            //获取id的前缀
            function getIdPref(id) {
                return id.substring(0, id.lastIndexOf('_') + 1);
            }
            //获取图片路径
            function getImgUrl(file) {
                var imgUrl = '';
                try {
                    imgUrl = file.getAsDataURL();
                } catch (e) {
                    imgUrl = window.URL.createObjectURL(file);
                }
                return imgUrl;
            }
            //主渲染
            function mainRender() {
                $this.css({ width: opts.width });
                var box = boxRender(0);
                domObjs[0] = box;
                $this.append(box.picbox);
                if (!opts.single) {
                    var $btnadd = $('<button type="button" title="添加"><i class="fa fa-plus"></i></button>');
                    $btnadd.addClass('btn btn-default btn-circle btn-outline picupadd');
                    $btnadd.click(addHandler);
                    $this.append($btnadd);
                }
                $this.append($('<div class="fclear"></div>'));
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
                mainRender();
                that.isUploadSuc = function() {
                    var flg = domObjs[0].sucFlg;
                    for (var i = 1; i < domObjs.length; i++) {
                        flg = flg && domObjs[i].sucFlg;
                    }
                    return flg;
                };
                //获取文件路径
                that.getPaths = function() {
                    return that.isUploadSuc() ? $.map(domObjs, function(n) {
                        return n.path.val();
                    }).join('>_<') : '';
                };
                //设置文件路径
                that.setPaths = function(paths) {
                    if ($.isEmpty(paths)) return;
                    var strs = paths.split('>_<');
                    if (opts.pdfFlg) {
                        if (opts.single || strs.length == 1) {
                            domObjs[0].path.val(strs[0]);
                            domObjs[0].picimg.hide();
                            domObjs[0].pdfimg.show();
                            domObjs[0].sucFlg = true;
                        } else {
                            for (var i = 0; i < strs.length; i++) {
                                if (i != 0) {
                                    addBox(i - 1);
                                    showDelBtn();
                                }
                                domObjs[i].path.val(strs[i]);
                                domObjs[i].picimg.hide();
                                domObjs[i].pdfimg.show();
                                domObjs[i].sucFlg = true;
                            }
                        }
                    } else {
                        if (opts.single || strs.length == 1) {
                            domObjs[0].path.val(strs[0]);
                            domObjs[0].picimg.attr('src', encodeURI(opts.urlContext + "rest/commons/download?filePath=" + strs[0]));
                            domObjs[0].sucFlg = true;
                        } else {
                            for (var i = 0; i < strs.length; i++) {
                                if (i != 0) {
                                    addBox(i - 1);
                                    showDelBtn();
                                }
                                domObjs[i].path.val(strs[i]);
                                domObjs[i].picimg.attr('src', encodeURI(opts.urlContext + "rest/commons/download?filePath=" + strs[i]));
                                domObjs[i].sucFlg = true;
                            }
                        }
                    }
                };
                that.destroy = function() {
                    $('#cropSize').html('');
                    $('#cropImg').cropper('destroy');
                    $('a[id^="crop-tool-"]').unbind("click");
                    $('#' + opts.picCropModalID + 'Btn').unbind("click");
                };
            }
            if (checkOpts()) init();
        });
    };
})(jQuery);
