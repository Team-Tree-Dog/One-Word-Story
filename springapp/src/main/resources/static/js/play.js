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
 * Callback for play button
 * @param e event (ignored)
 */
function onPlay(e) {
    document.getElementById("play_button").disabled = true;

    GameAPI = createAPI("ws://localhost:8080/game");

    GameAPI.onReady(async function() {
        GameAPI.log("TEST API READY");

        const joinResult = await GameAPI.tryJoin(document.getElementById("name").value);
        console.log("Join result: " + joinResult);

        if (joinResult) {

            switchToWaiting()

            const intervalId = setInterval(async () => {
                // Get the state every second

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
            }, 1000);
        } else {
            // Disconnect, display name invalid
            window.location.reload();
        }
    });
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
 * Create and return the API object for interacting with
 * the websocket
 * @param url The websocket endpoint URL string
 */
function createAPI (url) {
    const apiObj = {

        _ws: {
            isConnected: false,
            wsHandle: null,
            messageHandlers: {},

            init: function(url, mainHandler) {
                this.messageHandlers = {
                    "root": mainHandler
                };

                this.wsHandle = new WebSocket(url);
                this.wsHandle.onmessage = (data) => {
                    // console.log("Active handlers: ", this.messageHandlers);

                    (Object.values(this.messageHandlers) || []).filter(x => typeof x == 'function').forEach(x => x(data));
                };
                this.wsHandle.onopen = () => {
                    console.log("CONNECTED!");
                    this.isConnected = true
                };
                this.wsHandle.onclose = () => {
                    console.log("DISCONNECTED!");
                };
            },

            encode: (...elements) => (elements || []).join(SEPARATOR),
            decode: data => (data || "").split(SEPARATOR),

            send: function(message) {
                if(!this.isConnected) {
                    throw "Invalid state";
                }

                this.wsHandle.send(message);
            }
        },

        apiUrl: url,
        pendingCb: null,
        log: function(message) { console.log(message) },

        onReady: function(callback) {
            const interval = setInterval(() => {
                console.log("Checking connection");
                if(this._ws.isConnected) {
                    console.log("Connection established");
                    clearInterval(interval);
                    callback();
                }
            }, 50);
        },

        tryJoin: async function(playerName) {
            return await new Promise((resolve, reject) => {
                const waiterGuid = uuidv4();

                this._ws.messageHandlers[waiterGuid] = (msg) => {
                    // console.log("Got message: ", msg);

                    const decoded = this._ws.decode(msg.data);

                    if(decoded[0] == RESPONSE_JOIN) {
                        delete this._ws.messageHandlers[waiterGuid];
                        resolve(decoded[1] === "true");
                    }
                }

                this._ws.send(this._ws.encode(CMD_TRY_JOIN, playerName));
            });
        },

        getGameState: async function(playerName) {
            return await new Promise((resolve, reject) => {
                const waiterGuid = uuidv4();

                this._ws.messageHandlers[waiterGuid] = (msg) => {
                    // console.log("Got message: ", msg);

                    const decoded = this._ws.decode(msg.data);

                    if(decoded[0] == RESPONSE_STATE) {
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

    apiObj._ws.init(
        url,
        (incoming) => {} //console.log("incoming:", incoming.data)
    );

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
    window.location.reload()
}
