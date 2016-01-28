$( document ).ready(function() {
    $(".dropdown-menu li a").click(function(){
        console.log($(this).prop('nodeName'));
        $(this).parents(".input-group-btn").find('.btn').html($(this).text() + ' <span class="caret"></span>');
        $(this).parents(".input-group-btn").find('.btn').val($(this).data('value'));
    });
});