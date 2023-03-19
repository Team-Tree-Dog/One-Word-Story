const likeButton = document.getElementById('btn1');
const simpleGetOption = {method: 'GET'};

const currentUrl = window.location.href;
const storyId = currentUrl.substring(currentUrl.indexOf("-") + 1);
const storyMetadataUrl = `/api/story/metadata/${storyId}`;
const storyCommentsUrl = `/api/story/${storyId}/comments`

let currentStoryMetadata = {
    "likes": -1,
    "numberOfComments": -1,
    "numberOfSuggestedTitles": -1
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
    fetch(storyMetadataUrl, simpleGetOption).then(response => response.json()).then(response =>
    {
        if (response.likes !== currentStoryMetadata.likes) {
            console.log("Likes should be updated")
            updateLikes(response.likes)
        }
        if (response.numberOfComments !== currentStoryMetadata.numberOfComments) {
            console.log("Comments should be updated")
            updateComments()
        }
        if (response.numberOfSuggestedTitles !== currentStoryMetadata.numberOfSuggestedTitles) {
            console.log("Titles and the title should be updated")
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