function addVote(e){
    let count = Number(e.nextElementSibling.innerText) + 1;
    e.nextElementSibling.innerText = count;
}

function changeHeartColor(){
    const btn1 = document.getElementById('btn1');
    btn1.style.color === "red" ? btn1.style.color = "grey" : btn1.style.color = "red";
}