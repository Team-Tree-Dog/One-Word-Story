function addVote(e){
    let count = Number(e.nextElementSibling.innerText) + 1;
    e.nextElementSibling.textContent = count.text();
}

function changeHeartColor(){
    const heartButton = document.getElementById('heartButton');
    heartButton.style.color === "red" ? heartButton.style.color = "#985b26" : heartButton.style.color = "red";
}

function changeThumbsUpColor(){
    const heartButton = document.getElementById('thumbsUpButton');
    heartButton.style.color === "#f3e3c5" ? heartButton.style.color = "#985b26" : heartButton.style.color = "#f3e3c5";
}