function count_keys(obj) {
    let c = 0;
    for (let k in obj) {
        if (obj.hasOwnProperty(k)) {
            c++;
        }
    }
    return c;
}

$(document).ready(() => {
    let stats = JSON.parse(localStorage.getItem("gameEndStatData"));

    console.log(stats);
    localStorage.removeItem("gameEndStatData");
    console.log(JSON.parse(localStorage.getItem("gameEndStatData")));

    /**
     * Generate the div structure from a stat object recursively
     * @param statObj {RecursiveSymboledIntegerHashMap}
     */
    function generate_div (statObj) {
        // If base case
        if (statObj.hasOwnProperty("value") &&
            statObj.hasOwnProperty("suffix") &&
            count_keys(statObj) === 2) {

            // Create a span with the symboled integer
            let new_span = document.createElement("span");
            new_span.innerHTML = `${statObj.value}${statObj.suffix}`

            return {bc: true, html: new_span};
        }

        // Not base case. We know there is a map of key values
        else {
            // New div container
            let new_div = document.createElement('div');
            new_div.classList.add("container");

            // Loop through keys
            for (let k in statObj) {
                if (statObj.hasOwnProperty(k)) {

                    // Generate content for each value, passing the key as title
                    let generated = generate_div(statObj[k]);

                    let new_br = document.createElement("br");

                    // If the resulting content is base case, append a key value row
                    if (generated.bc) {
                        let new_b = document.createElement("b");
                        new_b.innerHTML = k + ": ";

                        new_div.appendChild(new_b);
                        new_div.appendChild(generated.html);
                        new_div.appendChild(new_br);
                    }

                    // Otherwise, we know the generated content is another div so we nest it as a child
                    else {
                        let new_title = document.createElement("h3");
                        new_title.classList.add("stat-title");
                        new_title.innerHTML = k;

                        new_div.appendChild(new_title);
                        new_div.appendChild(generated.html);
                        new_div.appendChild(new_br);
                    }
                }
            }

            return {bc: false, html: new_div};
        }
    }

    if (stats == null) {
        window.location.href = '/play';
    } else {
        stats.stats.forEach((stat) => {
            // Make column
            let new_div = document.createElement("div");
            new_div.classList.add("column");

            // generate stat div and add it as child
            new_div.appendChild(generate_div(stat).html);

            // Add column to row as child
            $(".row").append(new_div);
        });
    }
});
