IS_SECURE = false
DOMAIN = window.location.hostname + (!!window.location.port ? ":" + window.location.port : "")
SOCKET_URL = "ws"+(IS_SECURE ? "s" : "")+"://"+DOMAIN+"/game"

$(document).ready(() => {
    let urlParams = new URLSearchParams(window.location.search);
    let errMessage = urlParams.get("errorMessage")
    let errTitle = urlParams.get("errorTitle")
    if (!!errMessage) {
        Swal.fire({
            title: errTitle || "Error",
            text: errMessage,
            icon: "error"
        })
    }
});

// print pretty ;)
nicelog = (header, content, header_col="#9933ff", content_col="#ff99e6") =>
    console.log(`%c[${header}] %c ${content}`, `color:${header_col};font-weight:bold`, `color:${content_col}`)

/**
 * Generate UUID
 * @returns A new UUID string
 */
function uuidv4() {
    // Lol thank you stackoverflow
    return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
        (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );
}

// SOCKET LOGIC
// ================================================

let GameAPI = null;
let hasGameStarted = false;

const SEPARATOR = String.fromCharCode(30);

const CMD_TRY_JOIN = "JPL";
const CMD_SEND_WORD = "SW";

const RESPONSE_JOIN = "JPL:out:in_pool";
const RESPONSE_SUBMIT_WORD = "SW:out";
const RESPONSE_STATE = "current_state";

/**
 * Initiates and runs the socket logic once the connection has been established
 */
async function socketLogic () {
    // Call JPL
    const joinResult = await GameAPI.joinPublicLobby(document.getElementById("name").value);
    nicelog("Socket Logic", "Join result: " + joinResult);

    if (joinResult.code === "SUCCESS") {

        switchToWaiting()

        // Subscribe a handler which will listen for game updates
        const game_update_handler_guid = uuidv4()
        GameAPI._ws.messageHandlers[game_update_handler_guid] = (servRes) => {
            const decoded = GameAPI._ws.decode(servRes.data);

            // Game logic inside this if statement
            if(decoded[0] === RESPONSE_STATE) {
                const updatedGameState = JSON.parse(decoded[1]);

                if (!hasGameStarted && !!updatedGameState) {
                    hasGameStarted = true;

                    switchToGame();
                }

                if (hasGameStarted) {
                    // Update frontend with new game data
                    updateGameState(updatedGameState);
                }

                nicelog("Socket Logic", "Current game state: " + updatedGameState);
            }
        }

    } else {
        // Disconnect, display name invalid
        let mess = encodeURIComponent(joinResult.message);
        window.location.search = `errorTitle=${joinResult.code}&errorMessage=${mess}`;
    }
}

/**
 * Create and return the API object for interacting with
 * the websocket
 * @param url The websocket endpoint URL string
 */
function createAPI (url) {
    const apiObj = {

        /**
         * private websocket attribute
         */
        _ws: {
            isConnected: false, // Has connection been established
            wsHandle: null,
            /**
             * Stores a map of UUID to callback method.
             * A client command adds a callback defining what to do when
             * the server responds. Then it sends the message to the server. The
             * callback will be called and deleted once the server replies
             */
            messageHandlers: {},

            /**
             * Initialize websocket object and define its event callbacks
             * @param url socket url
             */
            init: function(url) {
                this.wsHandle = new WebSocket(url);

                this.wsHandle.onmessage = (data) => {
                    // Call all message handlers with message
                    (Object.values(this.messageHandlers) || [])
                        .filter(x => typeof x == 'function')
                        .forEach(x => x(data));
                };

                this.wsHandle.onopen = () => {
                    nicelog("Socket", "Connection Established");

                    this.isConnected = true
                };

                this.wsHandle.onclose = () => {
                    nicelog("Socket", "Connection Closed");

                    // Makes popup on the spot with a reload. This is so that
                    // when the server shuts down, you can read the popup an not just
                    // get a web error screen
                    Swal.fire({
                        title: "Disconnected",
                        text: "You have been disconnected from the game",
                        icon: "error",
                        confirmButtonText: "Leave",
                        allowOutsideClick: false
                    }).then((result) => {
                        if (result.isConfirmed) {window.location.search = ``}
                    })
                };
            },

            /**
             * takes in elements of a desired message and joins them with
             * agreed separator
             */
            encode: (...elements) => (elements || []).join(SEPARATOR),
            /**
             * @param data Raw string payload from websocket
             * @returns {string[]} Individual message elements split by separator
             */
            decode: data => (data || "").split(SEPARATOR),

            /**
             * Send a raw payload to the server.
             * @param message Raw payload to send
             */
            send: function(message) {
                if(!this.isConnected) {
                    throw "Invalid state";
                }

                this.wsHandle.send(message);
            }
        },

        /**
         * Ensures that websocket is open before calling callback
         * To safely use the socket, call this method and include all
         * of your socket logic in the callback
         * @param callback {function}
         */
        onReady: function(callback) {
            const interval = setInterval(() => {
                nicelog("Socket", "Checking connection...");
                if (this._ws.isConnected) {
                    nicelog("Socket", "Ready!");
                    clearInterval(interval);
                    callback();
                }
            }, 50);
        },

        /**
         * JPL (displayName, playerId = passed in backend)
         *
         * Adds a callback to the messageHandlers which resolves the promise with
         * the server's response. Then sends command to server. Awaiting this method
         * call will give you the server response corresponding to this message sent
         *
         * @param playerName {string} desired display name
         * @returns {Promise<Object>}
         */
        joinPublicLobby: async function(playerName) {
            return new Promise((resolve, reject) => {
                const waiterGuid = uuidv4();

                // If server hasn't responded to try join in a while, reload with failure
                let jplCounter = 0
                let jplTimeout = setInterval(()=>{
                    jplCounter++
                    if (jplCounter === 5) {
                        window.location.search = "errorMessage=Server%20connection%20timed%20out"
                    }
                },1000)

                this._ws.messageHandlers[waiterGuid] = (msg) => {
                    const decoded = this._ws.decode(msg.data);

                    if(decoded[0] === RESPONSE_JOIN) {
                        clearInterval(jplTimeout)

                        delete this._ws.messageHandlers[waiterGuid];
                        nicelog("Socket: joinPublicLobby", "Serv Res: " + decoded[1])
                        resolve(JSON.parse(decoded[1]));
                    }
                }

                this._ws.send(this._ws.encode(CMD_TRY_JOIN, playerName));
            });
        },

        /**
         * SW (word, playerId = passed in backend)
         *
         * Adds a callback to the messageHandlers which resolves the promise with
         * the server's response. Then sends command to server. Awaiting this method
         * call will give you the server response corresponding to this message sent
         *
         * @param word {string} desired word and punctuation to submit
         * @returns {Promise<Object>}
         */
        submitWord: function(word) {
            return new Promise((resolve, reject) => {
                const waiterGuid = uuidv4();

                this._ws.messageHandlers[waiterGuid] = (servRes) => {
                    const decoded = this._ws.decode(servRes.data);

                    if (decoded[0] === RESPONSE_SUBMIT_WORD) {
                        delete this._ws.messageHandlers[waiterGuid]

                        resolve({
                            "response": JSON.parse(decoded[1]),
                            "game_data": JSON.parse(decoded[2]) // Can be null
                        })
                    }
                }

                this._ws.send(this._ws.encode(CMD_SEND_WORD, word));
            });
        }
    };

    apiObj._ws.init(url);

    return apiObj;
}

// ================================================


/**
 * Given new information from server, update the game state
 * @param updatedGameState {Object<GameDisplayData>} a JSON-converted GameDisplayData object
 */
function updateGameState(updatedGameState) {
    // Clear players list
    let ply_list = document.getElementById("players-list")
    ply_list.innerHTML = "";

    // BUILD PLAYER LIST
    updatedGameState.players.forEach(e => {
        // Build <li> element for player, with pencil icon if it's the player's turn
        let new_li = document.createElement("div")

        new_li.classList.add("player")
        new_li.innerHTML = e.displayName +
            (e.isCurrentTurnPlayer ?
                `<img style="vertical-align:middle" src="media/pencil.png" width="20" alt="Pencil">` : "")

        ply_list.appendChild(new_li)
    })

    // POPULATE STORY
    document.getElementById("story").innerHTML = updatedGameState.storyString

    // POPULATE TIME
    document.getElementById("seconds-left").innerHTML = updatedGameState.secondsLeftInTurn
}

/**
 * Display a temporary error message under the submit word text field
 * @param errorText {string} error message
 */
function showSubmitWordError(errorText) {
    $("#submit-word-error-text").html(errorText);
    $("#submit-word-error").css("opacity", "1");
}

/**
 * Display a temporary error message under the submit word text field
 * @param errorText {string} error message
 */
function hideSubmitWordError() {
    $("#submit-word-error").css("opacity", "0");
}

/**
 * Switch screens to waiting page
 */
function switchToWaiting() {
    document.getElementById("game_page").style.display = "none";
    document.getElementById("play_page").style.display = "none";

    document.getElementById("waiting_page").style.display = "block";

    document.getElementsByTagName("body")[0].style.background = "#17252a";
}

/**
 * Switch screens to game page
 */
function switchToGame() {
    document.getElementById("waiting_page").style.display = "none";
    document.getElementById("play_page").style.display = "none";

    document.getElementById("game_page").style.display = "block";

    document.getElementsByTagName("body")[0].style.background = "#3aafa9";
}


/*
  ____        _   _                 ____      _ _ _                _
 | __ ) _   _| |_| |_ ___  _ __    / ___|__ _| | | |__   __ _  ___| | _____
 |  _ \| | | | __| __/ _ \| '_ \  | |   / _` | | | '_ \ / _` |/ __| |/ / __|
 | |_) | |_| | |_| || (_) | | | | | |__| (_| | | | |_) | (_| | (__|   <\__ \
 |____/ \__,_|\__|\__\___/|_| |_|  \____\__,_|_|_|_.__/ \__,_|\___|_|\_\___/
 */

document.getElementById("play-button").addEventListener("click", onPlay)
document.getElementById("cancel-button").addEventListener("click", onExit)
document.getElementById("exit-game-button").addEventListener("click", onExit)
document.getElementById("submit-word-button").addEventListener("click", onSubmitWord)
$("#word").focus(() => {
    hideSubmitWordError()
})

/**
 * Submit word button callback
 */
async function onSubmitWord() {
    let word_element = document.getElementById("word");
    let word = word_element.value;

    if (word !== "") {
        let data = await GameAPI.submitWord(word)
        console.log(data)

        if (data.response.code === "SUCCESS") {
            // Clear text area and update game data
            word_element.value = "";
            updateGameState(data.game_data);
        }
        // Show the error for 3.5 seconds
        else {
            showSubmitWordError(data.response.message);
            setTimeout(() => {
                hideSubmitWordError()
            }, 3500)
        }
    }
}

/**
 * Disconnect button & Cancel button callback
 */
function onExit() {
    // DISCONNECT
    window.location.search = ""
}

/**
 * Callback for play button, initiates websocket
 */
function onPlay() {
    // Disable button so we don't start two connections if someone clicks it again quickly
    document.getElementById("play-button").disabled = true;

    // Show loading icon
    $("#load-icon-container").show();

    // Create API which initiates socket connection
    GameAPI = createAPI(SOCKET_URL);

    // Wait for socket to be ready and then proceed with logic
    GameAPI.onReady(socketLogic);
}
