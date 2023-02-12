
setInterval(ajaxCall, 1000);

function ajaxCall() {
    const url = "/";
    const options = {method: 'GET'};
    fetch(url, options).
    then(response => response.text()).
    then(
        response => {
            // Definitely not the best way
            let newStories = document.createElement("div");
            newStories.innerHTML = response;
            const result = newStories.getElementsByClassName("stories").item(0);
            document.getElementById("stories_wrapper").innerHTML = result.innerHTML;
        }
    ).catch(error => console.log(error));
}