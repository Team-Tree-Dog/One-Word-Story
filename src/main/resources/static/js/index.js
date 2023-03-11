const SortingMethods = {
    LATEST: "latest",
    LIKED: "liked"
};

const simpleGetOption = {method: 'GET'};

const sortingMethodParameterName = "get";
const currentSortingMethodPrefix = `?${sortingMethodParameterName}=`;
let currentSortingMethodSuffix = SortingMethods.LIKED;

let lastStoryId = null;

// Call the function when this javascript file has been loaded
ajaxCall();

let callId = setInterval(ajaxCall, 1000);
function toggleSortingMethod() {
    clearInterval(callId);
    lastStoryId = null;
    currentSortingMethodSuffix = currentSortingMethodSuffix === SortingMethods.LATEST ?
        SortingMethods.LIKED : SortingMethods.LATEST;
    ajaxCall();
    callId = setInterval(ajaxCall, 1000);
}

function ajaxCall() {
    const lastStoryIdUrl = `/api/story/lastStoryId${currentSortingMethodPrefix}${currentSortingMethodSuffix}`;
    fetch(lastStoryIdUrl, simpleGetOption).then(response => response.text()).then(response => {
        if (response !== lastStoryId) {
            console.log("New data");
            lastStoryId = response;
            loadNewStories();
        }
    });
}

function loadNewStories() {
    const restUrl = `/stories${currentSortingMethodPrefix}${currentSortingMethodSuffix}`;
    console.log(`Fetching from: ${restUrl}`);
    fetch(restUrl, simpleGetOption).then(response => response.json()).then(response =>
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