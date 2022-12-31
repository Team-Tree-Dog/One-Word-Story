$(document).ready(() => {
    let stats = JSON.parse(localStorage.getItem("gameEndStatData"));

    console.log(stats);
    localStorage.removeItem("gameEndStatData");
    console.log(JSON.parse(localStorage.getItem("gameEndStatData")));

    if (stats == null) {
        window.location.href = '/';
    } else {
        stats.stats.forEach((stat) => {
            stat.entries.forEach((entry) => {
                // TODO: Make a recursive function which builds a game end stat visual screen. If a key leads
                // to another stat object, make a new inner div box with a bolded title of the key. Otherwise,
                // make a new small row bolded title with a colon link to the value
            });
        });
    }
});
