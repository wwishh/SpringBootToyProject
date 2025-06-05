let socket = new SockJS("/ws-chat");
let stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    stompClient.subscribe("/topic/messages", function (messageOutput) {
        const msg = JSON.parse(messageOutput.body);
        const li = document.createElement("li");
        li.textContent = msg.sender + ": " + msg.content;
        document.getElementById("messages").appendChild(li);
    });
});

function sendMessage() {
    const content = document.getElementById("messageInput").value;
    stompClient.send("/app/chat/send", {}, JSON.stringify({
        sender: "익명", // 또는 로그인 사용자 이름
        content: content
    }));
    document.getElementById("messageInput").value = '';
}
