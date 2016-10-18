$(function(){
    var debug = false;
    var url = "/api/comment/" + getFilename();

    function getFilename(){
        return window.location.pathname.substring(window.location.pathname.lastIndexOf('/')+1);
    }

    function getDateTime(d){
        var monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ];
        // padding function
        var s = function(a,b){return(1e15+a+"").slice(-b)};

        // default date parameter
        if (typeof d === 'undefined'){
            d = new Date();
        };

        // return datetime
        return s(d.getDate(),2) + ' ' +
            monthNames[d.getMonth()] + ' ' +
            d.getFullYear() + ' ' +
            s(d.getHours(),2) + ':' +
            s(d.getMinutes(),2) + ':' +
            s(d.getSeconds(),2);
    }

    function addComment(user, text, date){
        return $('<div class="panel panel-default">')
            .append('<div class="panel-heading"><h3 class="panel-title">' + user + '</h3></div>')
            .append('<div class="panel-body" style="word-wrap: break-word">' + text + '</div>')
            .append('<div class="panel-footer">' + getDateTime(new Date(date)) + '</div>')
            .append('</div>');
    }

    function displayAllComments(comments){
        $('form')[0].reset();
        $('#commentList').empty();
        for(var i = 0; i < comments.length; i++){
            var comment = comments[i];
            $('#commentList').append(addComment(comment.username, comment.comment, comment.timestamp));
        }
    }

    function displayCommentsOnPostSuccess(){
        $.ajax({
            type: "GET",
            url: url,
            success: function (response) {
                if(debug){
                    console.log(response);
                }
                displayAllComments(response);
            },
            error: function(response){
                if(debug){
                    console.log(response);
                }
            }   
        });
    }

    $('form').on('submit', function(e) {
        e.preventDefault();
        var comment = $('#comment').val();
        if(debug){
            console.log(comment);
            console.log(url);
        }
        $.ajax({
            type: "POST",
            url: url,
            data: {comment: comment},
            success: function (response) {
                if(debug){
                    console.log(response);
                }
                displayCommentsOnPostSuccess();
            },
            error: function(response){
                if(debug){
                    console.log(response);
                }
            }
        });
        return false;
    });
})