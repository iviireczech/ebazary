$( document ).ready(function() {
    $(".dropdown-menu li a").click(function(){
        console.log($(this).prop('nodeName'));
        $(this).parents(".input-group-btn").find('.btn').html($(this).text() + ' <span class="caret"></span>');
        $(this).parents(".input-group-btn").find('.btn').val($(this).data('value'));
    });

    $(".input-group > input").focus(function(e){
        $(this).parent().addClass("input-group-focus");
    }).blur(function(e){
        $(this).parent().removeClass("input-group-focus");
    });

    $("#search-input").focus();

    /*
    $("#filter-collapse-btn").parent().click(function(){
        $(this).parent().toggleClass("filter-enabled");
    });

    $("#filter-collapse").find(".btn").parent().click(function(){
        $(this).toggleClass("filter-enabled");
    });
    */

});