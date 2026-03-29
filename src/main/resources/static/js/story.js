const likeButton = document.getElementById('btn1');
const simpleGetOption = {method: 'GET'};

const currentUrl = window.location.href;

// TODO: Change the endpoints and refactor the code
let storyId = currentUrl.substring(currentUrl.indexOf("-") + 1);
if (storyId.indexOf('?') !== -1) {
    storyId = storyId.substring(0, storyId.indexOf('?'))
}
const storyMetadataUrl = `/api/story/metadata/${storyId}`;
const storyCommentsUrl = `/api/story/comments/${storyId}`
const storyTitlesUrl = `/api/story/titles/${storyId}`
const storyTitleUrl = `/api/story/title/${storyId}`

let currentStoryMetadata = {
    "likes": -1,
    "numberOfComments": -1,
    "numberOfSuggestedTitles": -1,
    "likesInTotal": -1
};

let callId = setInterval(fetchStoryData, 1000);
function addVote(e) {
    e.nextElementSibling.innerText = Number(e.nextElementSibling.innerText) + 1;
}

function Toggle1() {
    if (likeButton.style.color === "red") {
        likeButton.style.color = "grey";
    } else {
        likeButton.style.color = "red";
    }
}

function fetchStoryData() {
    if (document.visibilityState === "hidden") {
        return;
    }
    fetch(storyMetadataUrl, simpleGetOption).then(response => response.json()).then(response =>
    {
        console.log(response)
        if (response.likes !== currentStoryMetadata.likes) {
            // TODO: Add the number of likes
            console.log("Likes should be updated")
            updateLikes(response.likes)
        }
        console.log(`Number of comments (server): ${response.numberOfComments}`)
        console.log(`Number of comments (client): ${currentStoryMetadata.numberOfComments}`)
        if (response.numberOfComments !== currentStoryMetadata.numberOfComments) {
            console.log("Comments should be updated")
            updateComments()
        }
        if (response.numberOfSuggestedTitles !== currentStoryMetadata.numberOfSuggestedTitles ||
            response.titlesLikesInTotal !== currentStoryMetadata.likesInTotal) {
            console.log("Titles and the title should be updated")
            updateTitles()
        }
        currentStoryMetadata = response;
    });
}

function updateLikes(numberOfLikes) {
    // We don't display the number of likes at the moment
}

function updateComments() {
    fetch(storyCommentsUrl, simpleGetOption).then(response => response.json()).then(response =>
    {
        console.log("Updating comments")
        if (response.length === 0) {
            return;
        }
        console.log(response)
        // Building HTML
        const html = [];
        response.forEach(comment => {
            html.push('<div class="comments" id="comment">')
            html.push('<img src="media/user.jpeg" class="user_profile" alt="not available">');
            html.push(`<p  class="comment">`);
            html.push(`<b>${comment.displayName}</b> <br>`);
            html.push(`<p>${comment.content}</p>`);
            html.push('</p>')
            html.push('</div>')
        });
        document.getElementById("all_comments").innerHTML = html.join("")
    });
}

function updateTitles() {
    fetch(storyTitlesUrl, simpleGetOption).then(response => response.json()).then(response =>
    {
        if (response.length === 0) {
            return;
        }
        console.log(`Id: ${storyId}`);
        // Building HTML
        const html = [];
        response.forEach(suggestion => {
            html.push(`<form method="POST" action="upvote-suggestion/suggestion?id=${suggestion.title}&storyId=${storyId}"
            class="titles">`)
            html.push(`<p>${suggestion.title}</p>`)
            html.push('<br>');
            html.push(`<button type="submit" onclick="addVote(this)">Upvote</button>`);
            html.push(`<span class="count">${suggestion.numUpvotes}</span>`);
            html.push("</form>")
        });
        document.getElementById("all_votes").innerHTML = html.join("")
    });
    fetch(storyTitleUrl, simpleGetOption).then(response => response.text()).then(response => {
            console.log(response)
            document.getElementById("heading").innerText = response
        }
    )
}