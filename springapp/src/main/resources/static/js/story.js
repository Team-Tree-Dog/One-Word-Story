function addVote(e){
    let count = Number(e.nextElementSibling.innerText) + 1;
    e.nextElementSibling.innerText = count;
}

function Toggle1(){
    const btnvar1 = document.getElementById('btn1');

    if (btnvar1.style.color ==="red") {
        btnvar1.style.color = "grey"
    }
    else{
        btnvar1.style.color = "red"
    }
}