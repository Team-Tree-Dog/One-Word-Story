function createRowWithTwoStories() {
    const stories = document.querySelectorAll('.story');
    
    for(let i = 0; i < stories.length; i += 2) {
        const twoStories = document.createElement('div');
        twoStories.classList.add('twoStories');
        twoStories.appendChild(stories[i]);
        document.querySelector('#allStoriesByTwo').appendChild(twoStories);
    }

    for(let i = 1; i < stories.length; i += 2) {
        const firstStory = stories[i - 1];
        firstStory.insertAdjacentElement("afterend", stories[i]);
    }
}

createRowWithTwoStories();