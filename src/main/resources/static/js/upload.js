var selDiv = "";
document.addEventListener("DOMContentLoaded", init, false);

function init() {
    document.querySelector('#file-selector').addEventListener('change', handleFileSelect, false);
    selDiv = document.querySelector("#file-list");
}

function handleFileSelect(e) {
    if(!e.target.files) return;
    selDiv.innerHTML = "";

    var files = e.target.files;
    for(var i=0; i<files.length; i++) {
        var f = files[i];
        var file = document.createElement("span");
        file.setAttribute("class", "label label-info");
        file.appendChild(document.createTextNode(f.name))
        selDiv.appendChild(file);
        selDiv.appendChild(document.createElement("br"));
    }
}

$(function(){

    // File upload ajax
    $('form').on('submit', function(e){
        e.preventDefault();
        // Checking if input is empty on upload
        if($('#file-selector').val() === ""){
            $('.alert').remove();
            $('<div class="alert alert-danger"><span class="sr-only">Error:</span>No files selected.</div>').insertBefore('form');
        }
        else{
            var file = new FormData(this);

            $.ajax({
                url: 'upload',
                type: 'POST',
                xhr: function() {
                    var myXhr = $.ajaxSettings.xhr();
                    return myXhr;
                },
                success: function (data) {
                    console.log(data);
                    $('form')[0].reset();
                    $('#file-list').empty();
                    $('.alert').remove();
                    $('<div class="alert alert-success"><span class="sr-only">Success:</span>' + data + '</div>').insertBefore('form');

                },
                error: function(data){
                    console.log(data);
                    $('form')[0].reset();
                    $('#file-list').empty();
                    $('.alert').remove();
                    $('<div class="alert alert-danger"><span class="sr-only">Error:</span>' + data.responseText + '</div>').insertBefore('form');
                },
                data: file,
                cache: false,
                contentType: false,
                processData: false
            });
            return false;
        }
    })
});
