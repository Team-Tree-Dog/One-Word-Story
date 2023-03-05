// Call the function when this javascript file has been loaded
ajaxCall();

setInterval(ajaxCall, 1000);

function ajaxCall() {
    let lastStoryId = null;
    const lastStoryIdUrl = "/api/story/lastStoryId";
    const options = {method: 'GET'};
    fetch(lastStoryIdUrl, options).then(response => response.text()).then(response => {
        if (response !== lastStoryId) {
            lastStoryId = response;
            loadNewStories();
        }
    });
}

function loadNewStories() {
    const restUrl = "/stories";
    const options = {method: 'GET'};
    fetch(restUrl, options).then(response => response.json()).then(response =>
    {
        // Building HTML
        const html = [];
        // Convert using ``
        response.forEach(element => {
            html.push('<div class="story" id = "story1">');
            html.push(`<a href="/story-${element.id}"><h2>${element.title}</h2></a>`);
            html.push(`<span style="color:darkblue"> ${element.likes}</span>`);
            html.push(`<p>Published: ${element.publishDateString} </p>`);
            html.push(`<h4>Authors: <span> ${element.authorString} </span></h4>`);
            html.push(`<p> ${element.content} </p>`);
            html.push('</div>');
        });
        document.getElementById("stories_wrapper").innerHTML = html.join("");
    });
}