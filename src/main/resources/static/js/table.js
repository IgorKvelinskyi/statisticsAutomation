<!-- language: lang-js -->
$(function() {
    var header_height = 0;
    $('.rotate-table-grid th span').each(function() {
        if ($(this).outerWidth() > header_height) header_height = $(this).outerWidth();
    });

    $('.rotate-table-grid th').height(header_height);
});
