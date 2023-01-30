function addVote(e){
    let count = Number(e.nextElementSibling.innerText) + 1;
    e.nextElementSibling.innerText = count;
}

function changeHeartColor(){
    const heartButton = document.getElementById('heartButton');
    heartButton.style.color === "red" ? heartButton.style.color = "#985b26" : heartButton.style.color = "red";
}