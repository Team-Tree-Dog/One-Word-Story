const SortingMethods = {
    LATEST: "latest",
    LIKED: "liked"
};

const simpleGetOption = {method: 'GET'};

const sortingMethodParameterName = "get";
const currentSortingMethodPrefix = `?${sortingMethodParameterName}=`;
let currentSortingMethodSuffix = SortingMethods.LIKED;

let lastStory = {};

// Call the function when this javascript file has been loaded
loadNewStories();

let callId = setInterval(loadNewStories, 1000);
function toggleSortingMethod() {
    clearInterval(callId);
    lastStory = null;
    currentSortingMethodSuffix = currentSortingMethodSuffix === SortingMethods.LATEST ?
        SortingMethods.LIKED : SortingMethods.LATEST;
    loadNewStories();
    callId = setInterval(loadNewStories, 1000);
}
function loadNewStories() {
    const restUrl = `/stories${currentSortingMethodPrefix}${currentSortingMethodSuffix}`;
    console.log(`Fetching from: ${restUrl}`);
    fetch(restUrl, simpleGetOption).then(response => response.json()).then(response =>
    {
        // Building HTML
        const html = [];
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