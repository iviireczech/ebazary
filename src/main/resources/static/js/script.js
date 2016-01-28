$( document ).ready(function() {
    $(".dropdown-menu li a").click(function(){
        console.log($(this).prop('nodeName'));
        $(this).parents(".dropdown-inline").find('.btn').html($(this).text() + ' <span class="caret"></span>');
        $(this).parents(".dropdown-inline").find('.btn').val($(this).data('value'));
    });
});