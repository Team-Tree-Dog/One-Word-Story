$(document).ready(() => {
    console.log(JSON.parse(localStorage.getItem("gameEndStatData")));
    localStorage.removeItem("gameEndStatData");
    console.log(JSON.parse(localStorage.getItem("gameEndStatData")));
});
