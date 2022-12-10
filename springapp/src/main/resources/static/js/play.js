
function pr() {
    document.getElementById("story").innerHTML += " " + document.getElementById('word').value;
    document.getElementById('word').value = "";
}

function exit() {
    console.log("disconnect pressed")
}


/*

 */
let GameAPI = null;


var x = false;


document.getElementById("play_button").
addEventListener("click", (e) => {

    document.getElementById("play_button").disabled = true;

    // TODO: Add websocket connection
    GameAPI = (function(url) {
        function uuidv4() {
            return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
                (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
            );
        }

        const CMD_TRY_JOIN = "try_join";
        const CMD_STATE_UPDATE = "state_update";
        const CMD_SEND_WORD = "send_word";
        const CMD_LEAVE = "leave";

        const RESPONSE_JOIN = "join_response";
        const RESPONSE_STATE = "current_state";

        const SEPARATOR = String.fromCharCode(30);

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
    })("ws://localhost:8080/game");


    console.log("clicked play");


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
                    // Update frontend
                }

                console.log("Current game state: ", updatedGameState);
            }, 1000);

            // GameAPI.sendWord("word1");
            // GameAPI.sendWord("word2");

            // setTimeout(() => {
            //     clearInterval(intervalId);
            //     console.log("Leaving!");
            //     GameAPI.leave();
            // }, 4000);
        } else {
            // Disconnect, display name invalid
            window.location.reload();
        }
    });
})

let hasGameStarted = false;

document.getElementById("cancel-button").
addEventListener("click", (e) => {
    console.log("clicked cancel");

    // DISCONNECT
    window.location.reload();
})


function switchToWaiting () {
    document.getElementById("game_page").style.display = "none";
    document.getElementById("play_page").style.display = "none";

    document.getElementById("waiting_page").style.display = "block";

    document.getElementsByTagName("body")[0].style.background = "#17252a";
}


function switchToGame () {
    document.getElementById("waiting_page").style.display = "none";
    document.getElementById("play_page").style.display = "none";

    document.getElementById("game_page").style.display = "block";

    document.getElementsByTagName("body")[0].style.background = "#3aafa9";
}

