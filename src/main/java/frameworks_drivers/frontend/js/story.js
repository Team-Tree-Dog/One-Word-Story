function myFunction(){
    let data = "";
    let comment = document.getElementById("userComment").value
    let guest_name = document.getElementById("userName").value

    return comment+guest_name
}

function addTitle(){
    let data = "";
    data = document.getElementById("title1").innerText
    document.getElementById("heading").innerHTML = data
}

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