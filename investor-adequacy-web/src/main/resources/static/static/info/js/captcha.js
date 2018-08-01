function refreshCaptcha(acho, imgSrc) {
    $(acho).find('img:first-child').attr('src', imgSrc
        + '?' + new Date().getTime());
}