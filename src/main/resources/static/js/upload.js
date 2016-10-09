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