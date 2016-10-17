$(function(){
    function addComment(user, text, date){
        return $('<div class="panel panel-default">')
            .append($('<div class="panel-heading">').append('<h3 class="panel-title">'+user+'</h3></div>'))
            .append('<div class="panel-body" style="text:word-wrap">'+text+'</div>')
            .append('<div class="panel-footer">'+new Date(date)+'</div>')
            .append('</div>');
    }

    function displayAllComments(comments){
        $('form')[0].reset();
        $('#comments').empty();
        for(i = 0; i < comments.length; i++){
            var comment = comments[i];
            $('#comments').append(addComment(comment.username, comment.comment, comment.timestamp));
        }
    }

    $('form').on('submit', function(e) {
        e.preventDefault();
        var comment = $('#comment').val();
        console.log(comment);
        var url = "/api/comment/"+ window.location.pathname.substring(window.location.pathname.lastIndexOf('/')+1);
        console.log(url);

        $.ajax({
            type: "POST",
            url: url,
            data: {comment: comment},
            success: function (response) {
                console.log(response);
                $.ajax({
                    type: "GET",
                    url: url,
                    success: function (response) {
                        console.log(response);
                        displayAllComments(response);
                    },
                    error: function(response){
                        console.log(response);
                    }   
                });
            },
            error: function(response){
                console.log(response);
            }
        });
        return false;
    });
})