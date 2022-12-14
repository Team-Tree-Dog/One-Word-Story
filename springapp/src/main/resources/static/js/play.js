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

/**
 * Callback for play button, initiates websocket
 * @param e event (ignored)
 */
function onPlay(e) {
    // Disable button so we don't start two connections if someone clicks it again quickly
    document.getElementById("play_button").disabled = true;

    // Show loading icon
    $("#load-icon-container").show();

    // Create API which initiates socket connection
    GameAPI = createAPI(SOCKET_URL);

    // Wait for socket to be ready and then proceed with logic
    GameAPI.onReady(socketLogic);
}

// SOCKET LOGIC
// ================================================

let GameAPI = null;
let hasGameStarted = false;

const SEPARATOR = String.fromCharCode(30);

const CMD_TRY_JOIN = "try_join";
const CMD_STATE_UPDATE = "state_update";
const CMD_SEND_WORD = "send_word";
const CMD_LEAVE = "leave";

const RESPONSE_JOIN = "join_response";
const RESPONSE_STATE = "current_state";

/**
 * Initiates and runs the socket logic once the connection has been established
 */
async function socketLogic () {
    // Call JPL
    const joinResult = await GameAPI.tryJoin(document.getElementById("name").value);
    console.log("Join result: " + joinResult);

    if (joinResult.code === "SUCCESS") {

        switchToWaiting()

        const intervalId = setInterval(async () => {
            // Get the state periodically and run game logic

            const updatedGameState = await GameAPI.getGameState();

            if (!hasGameStarted && !!updatedGameState) {
                hasGameStarted = true;

                switchToGame();
            }

            if (hasGameStarted) {
                // Update frontend with new game data


                // Clear players list
                let ply_list = document.getElementById("players-list")
                ply_list.innerHTML = "";

                // BUILD PLAYER LIST
                updatedGameState.players.forEach(e => {
                    // Build <li> element for player, with pencil icon if it's the player's turn
                    let new_li = document.createElement("li")
                    new_li.innerHTML = `<li>
                            <div>
                                `+ e.displayName +
                        (e.isCurrentTurnPlayer ? `<img style="vertical-align:middle" src="media/pencil.png" width="20" alt="Pencil">` : "") + `
                            </div>
                        </li>`

                    ply_list.appendChild(new_li)
                })

                // POPULATE STORY
                document.getElementById("story").innerHTML = updatedGameState.storyString

                // POPULATE TIME
                document.getElementById("seconds-left").innerHTML = updatedGameState.secondsLeftInTurn


            }

            console.log("Current game state: ", updatedGameState);
        }, 500);
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
                    // Get all message handlers
                    (Object.values(this.messageHandlers) || [])
                        .filter(x => typeof x == 'function')
                        .forEach(x => x(data));
                };

                this.wsHandle.onopen = () => {
                    console.log("%c[Socket] %cConnection Established",
                        "color:#8c1aff;font-weight:bold", "color:#ff99e6");

                    this.isConnected = true
                };

                this.wsHandle.onclose = () => {
                    console.log("%c[Socket] %cConnection Closed",
                        "color:#8c1aff;font-weight:bold", "color:#ff99e6");
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

        /** Logs messages in a pretty colored css format */
        log: (m) => console.log("%c[Socket] %c" + m, "color:#8c1aff;font-weight:bold", "color:#ff99e6"),

        /**
         * Ensures that websocket is open before calling callback
         * To safely use the socket, call this method and include all
         * of your socket logic in the callback
         * @param callback {function}
         */
        onReady: function(callback) {
            const interval = setInterval(() => {
                this.log("Checking connection...");
                if (this._ws.isConnected) {
                    this.log("Ready!");
                    clearInterval(interval);
                    callback();
                }
            }, 50);
        },

        /**
         * JPL (displayName, uniqueId = socket session ID)
         *
         * Adds a callback to the messageHandlers which resolves the promise with
         * the server's response. Then sends command to server. Awaiting this method
         * call will give you the server response corresponding to this message sent
         *
         * @param playerName {string} desired display name
         * @returns {Promise<Object>}
         */
        tryJoin: async function(playerName) {
            return new Promise((resolve, reject) => {
                const waiterGuid = uuidv4();

                // If server hasn't responded to try join in a while, reload with failure
                let tryJoinCounter = 0
                let tryJoinTimeout = setInterval(()=>{
                    tryJoinCounter++
                    if (tryJoinCounter === 5) {
                        window.location.search = "errorMessage=Server%20connection%20timed%20out"
                    }
                },1000)

                this._ws.messageHandlers[waiterGuid] = (msg) => {
                    const decoded = this._ws.decode(msg.data);

                    if(decoded[0] === RESPONSE_JOIN) {
                        clearInterval(tryJoinTimeout)

                        delete this._ws.messageHandlers[waiterGuid];
                        console.log(decoded[1])
                        resolve(JSON.parse(decoded[1]));
                    }
                }

                this._ws.send(this._ws.encode(CMD_TRY_JOIN, playerName));
            });
        },

        getGameState: async function(playerName) {
            return new Promise((resolve, reject) => {
                const waiterGuid = uuidv4();

                this._ws.messageHandlers[waiterGuid] = (msg) => {
                    const decoded = this._ws.decode(msg.data);

                    if(decoded[0] === RESPONSE_STATE) {
                        delete this._ws.messageHandlers[waiterGuid];
                        resolve(JSON.parse(decoded[1]));
                    }
                }

                this._ws.send(this._ws.encode(CMD_STATE_UPDATE, playerName));
            });
        },

        sendWord: function(word) {
            this._ws.send(this._ws.encode(CMD_SEND_WORD, word));
        },

        leave: function() {
            this._ws.send(this._ws.encode(CMD_LEAVE));
        }
    };

    apiObj._ws.init(url);

    return apiObj;
}

// ================================================


document.getElementById("play_button").addEventListener("click", onPlay)
document.getElementById("cancel-button").addEventListener("click", exit)

/**
 * Switch screens to waiting page
 */
function switchToWaiting () {
    document.getElementById("game_page").style.display = "none";
    document.getElementById("play_page").style.display = "none";

    document.getElementById("waiting_page").style.display = "block";

    document.getElementsByTagName("body")[0].style.background = "#17252a";
}

/**
 * Switch screens to game page
 */
function switchToGame () {
    document.getElementById("waiting_page").style.display = "none";
    document.getElementById("play_page").style.display = "none";

    document.getElementById("game_page").style.display = "block";

    document.getElementsByTagName("body")[0].style.background = "#3aafa9";
}

/**
 * Submit word button callback
 */
function submitWord() {
    let word = document.getElementById("word").value
    document.getElementById("word").value = ""
    if (word !== "") {
        GameAPI.sendWord(word)
    }
}

/**
 * Disconnect button & Cancel button callback
 */
function exit() {
    // DISCONNECT
    window.location.search = ""
}
