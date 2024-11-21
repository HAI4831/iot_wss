let stompClient = null;
const colors = ['#2196F3', '#32c787', '#00BCD4', '#ff5652', '#ffc107', '#ff85af', '#FF9800', '#39bbb0'];

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
    $("#connect").prop("disabled", true);
    $("#disconnect").prop("disabled", false);
}

function onConnected(frame) {
    console.log("Connected: " + frame);
    stompClient.subscribe("/topic/moving", function (moveResponse) {
        const response = JSON.parse(moveResponse.body);
        showMoving(response.message);
    });
}

function onError(error) {
    console.error("Error with WebSocket connection:", error);
}

function sendMessage() {
    const actionMoveName = $("#action_move_name").val();
    const speed = $("#speed").val();

    if (actionMoveName && speed) {
        stompClient.send("/app/move", {}, JSON.stringify({ actionMoveName: actionMoveName, speed: Number(speed) }));
    }
}

function showMoving(message) {
    const color = getAvatarColor(message);
    $("#MoveControl").append(`<tr><td style="color:${color}">${message}</td></tr>`);
}

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    return colors[Math.abs(hash % colors.length)];
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect(() => {
            console.log("Disconnected");
            $("#connect").prop("disabled", false);
            $("#disconnect").prop("disabled", true);
        });
    }
}

$(function () {
    $("form").on("submit", (e) => e.preventDefault());
    $("#connect").click(connect);
    $("#disconnect").click(disconnect);
    $("#send").click(sendMessage);
});
