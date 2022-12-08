function pr() {
    document.getElementById("story").innerHTML += " " + document.getElementById('word').value;
    document.getElementById('word').value = "";
}

function exit() {
    console.log("disconnect pressed")
}