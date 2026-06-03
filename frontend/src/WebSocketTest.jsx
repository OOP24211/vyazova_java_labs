import { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";
import YouTube from "react-youtube";

export default function WebSocketTest({ roomId, onLeaveRoom, userNickname }) {
  const [client, setClient] = useState(null);
  const [messages, setMessages] = useState([]); 
  const [typedMessage, setTypedMessage] = useState(""); 
  const [username] = useState(userNickname || "Аноним");

  const [onlineCount, setOnlineCount] = useState(1);

  const [currentVideoId, setCurrentVideoId] = useState("");
  const [videoUrlInput, setVideoUrlInput] = useState("");

  const playerRef = useRef(null);
  const isSyncing = useRef(false);

  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
    });

    stompClient.onConnect = () => {
      console.log(`CONNECTED TO ROOM: ${roomId}`);

      stompClient.subscribe(`/topic/room/${roomId}/online`, (msg) => {
        const body = JSON.parse(msg.body);
        setOnlineCount(body.onlineCount);
      });

      stompClient.publish({
        destination: "/app/room.join",
        body: JSON.stringify({ roomId: roomId })
      });

      stompClient.subscribe(`/topic/room/${roomId}`, (msg) => {
        const body = JSON.parse(msg.body);

        if (body.type === "CHANGE_VIDEO") {
          isSyncing.current = true;
          setCurrentVideoId(body.videoId);
          setTimeout(() => {
            if (playerRef.current) {
              playerRef.current.seekTo(0, true);
              playerRef.current.pauseVideo();
            }
            isSyncing.current = false;
          }, 600);
          return;
        }

        if (body.videoId && body.videoId !== "dQw4w9WgXcQ" && !playerRef.current) {
          if (body.videoId !== currentVideoId) {
            setCurrentVideoId(body.videoId);
          }
          return;
        }

        if (!playerRef.current) return;

        const localTime = playerRef.current.getCurrentTime();

        if (body.type === "SYNC") {
          const timeDifference = Math.abs(localTime - body.time);
          if (timeDifference > 2.0) {
            isSyncing.current = true;
            playerRef.current.seekTo(body.time, true);
            setTimeout(() => { isSyncing.current = false; }, 400);
          }
          return;
        }

        isSyncing.current = true;
        if (Math.abs(localTime - body.time) > 1.5) {
          playerRef.current.seekTo(body.time, true);
        }

        if (body.type === "PLAY") {
          playerRef.current.playVideo();
        } else if (body.type === "PAUSE") {
          playerRef.current.pauseVideo();
        }

        setTimeout(() => { isSyncing.current = false; }, 400);
      });

      stompClient.subscribe(`/topic/room/${roomId}/chat`, (msg) => {
        const incomingMessage = JSON.parse(msg.body);
        setMessages((prev) => [...prev, incomingMessage]);
      });

      stompClient.publish({
        destination: "/app/room.getState",
        body: JSON.stringify({ roomId: roomId })
      });
    };

    stompClient.activate();
    setClient(stompClient);

    const handleVisibilityChange = () => {
      if (document.visibilityState === "visible" && stompClient && stompClient.connected) {
        isSyncing.current = true;
        stompClient.publish({
          destination: "/app/room.getState",
          body: JSON.stringify({ roomId: roomId })
        });
      }
    };

    document.addEventListener("visibilitychange", handleVisibilityChange);

    return () => {
      stompClient.deactivate();
      document.removeEventListener("visibilitychange", handleVisibilityChange);
    };
  }, [roomId]);

  const handleReady = (event) => {
    playerRef.current = event.target;

    if (client && client.connected) {
      isSyncing.current = true;
      client.publish({
        destination: "/app/room.getState",
        body: JSON.stringify({ roomId: roomId })
      });
    }
  };

  const handleStateChange = (event) => {
    if (isSyncing.current) return;
    if (!client || !playerRef.current) return;
    if (event.data === 2 && document.hidden) return;

    const currentTime = playerRef.current.getCurrentTime();

    if (event.data === 1) {
      client.publish({
        destination: "/app/room.play",
        body: JSON.stringify({ roomId: roomId, type: "PLAY", time: currentTime }),
      });
    }

    if (event.data === 2) {
      client.publish({
        destination: "/app/room.play",
        body: JSON.stringify({ roomId: roomId, type: "PAUSE", time: currentTime }),
      });
    }
  };

  const sendMessage = (e) => {
    e.preventDefault();
    if (!typedMessage.trim() || !client) return;

    client.publish({
      destination: "/app/room.chat",
      body: JSON.stringify({ roomId: roomId, sender: username, content: typedMessage })
    });
    setTypedMessage("");
  };

  const handleChangeVideoSubmit = (e) => {
    e.preventDefault();
    if (!videoUrlInput.trim() || !client) return;

    let extractedId = videoUrlInput.trim();

    if (extractedId.includes("v=")) {
      extractedId = extractedId.split("v=")[1].split("&")[0];
    } else if (extractedId.includes("youtu.be/")) {
      extractedId = extractedId.split("youtu.be/")[1].split("?")[0];
    }

    client.publish({
      destination: "/app/room.changeVideo",
      body: JSON.stringify({
        roomId: roomId,
        videoId: extractedId
      })
    });

    setVideoUrlInput("");
  };

  return (
    <div style={{
      minHeight: "100vh",
      width: "100vw",
      position: "fixed",
      top: 0,
      left: 0,
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      backgroundImage: "url('https://i.pinimg.com/736x/93/84/37/938437e03df25081085f1c9990b8ddd1.jpg')",
      backgroundSize: "contain",
      backgroundPosition: "center",
      backgroundRepeat: "no-repeat",
      backgroundColor: "#0a0a0a",
      fontFamily: "sans-serif",
      boxSizing: "border-box",
      overflowY: "auto",
      padding: "20px"
    }}>

      <div style={{
        maxWidth: "1220px",
        width: "100%",
        background: "rgba(23, 23, 23, 0.85)",
        backdropFilter: "blur(12px)",
        borderRadius: "20px",
        padding: "30px",
        boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.8)",
        border: "1px solid rgba(255, 255, 255, 0.05)",
        color: "#f3f4f6"
      }}>

        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "25px" }}>
          <button
            onClick={() => {
              if (client && client.connected) {
                client.publish({
                  destination: "/app/room.leave",
                  body: JSON.stringify({ roomId: roomId })
                });
              }
              onLeaveRoom();
            }}
            style={{
              padding: "10px 20px",
              cursor: "pointer",
              borderRadius: "10px",
              border: "1px solid #374151",
              background: "#171717",
              color: "#ffffff",
              fontWeight: "600",
              transition: "0.2s"
            }}
            onMouseOver={(e) => e.currentTarget.style.background = "#262626"}
            onMouseOut={(e) => e.currentTarget.style.background = "#171717"}
          >
             Выйти в лобби
          </button>

          <h2 style={{ display: "flex", alignItems: "center", gap: "12px", margin: 0, fontSize: "22px", fontWeight: "700" }}>
            Комната: <span style={{ color: "#e5e7eb" }}>{roomId}</span>
            <span style={{
              fontSize: "13px",
              background: "#374151",
              color: "#ffffff",
              padding: "4px 12px",
              borderRadius: "20px",
              fontWeight: "600",
              border: "1px solid rgba(255, 255, 255, 0.1)"
            }}>
               В сети: {onlineCount}
            </span>
            (Вы: <span style={{color: "#9ca3af"}}>{username}</span>)
          </h2>
          <div style={{ width: "120px" }}></div>
        </div>

        <div style={{ display: "flex", justifyContent: "center", marginBottom: "25px" }}>
          <form onSubmit={handleChangeVideoSubmit} style={{ display: "flex", width: "100%", gap: "12px" }}>
            <input
              type="text"
              placeholder="Вставьте ссылку на YouTube видео или ID..."
              value={videoUrlInput}
              onChange={(e) => setVideoUrlInput(e.target.value)}
              style={{
                flex: 1,
                padding: "14px 18px",
                borderRadius: "10px",
                border: "1px solid #374151",
                background: "#171717",
                color: "#ffffff",
                fontSize: "15px",
                outline: "none"
              }}
            />
            <button
              type="submit"
              style={{
                padding: "14px 24px",
                background: "#ffffff",
                color: "#171717",
                border: "none",
                borderRadius: "10px",
                cursor: "pointer",
                fontWeight: "600",
                fontSize: "15px",
                transition: "0.2s"
              }}
              onMouseOver={(e) => e.currentTarget.style.background = "#e5e7eb"}
              onMouseOut={(e) => e.currentTarget.style.background = "#ffffff"}
            >
              Поставить видео
            </button>
          </form>
        </div>

        <div style={{ display: "flex", gap: "25px", justifyContent: "center", alignItems: "flex-start" }}>
          <div style={{
            borderRadius: "12px",
            overflow: "hidden",
            boxShadow: "0 10px 30px rgba(0,0,0,0.5)",
            background: "#000000",
            width: "800px",
            height: "450px"
          }}>
            {currentVideoId && currentVideoId !== "dQw4w9WgXcQ" ? (
              <YouTube
                videoId={currentVideoId}
                onReady={handleReady}
                onStateChange={handleStateChange}
                opts={{
                  width: "800",
                  height: "450",
                  playerVars: { autoplay: 0, rel: 0 },
                }}
              />
            ) : (
              <div style={{
                width: "100%",
                height: "100%",
                background: "#111111",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                border: "1px dashed #374151",
                borderRadius: "12px",
                boxSizing: "border-box"
              }}>
                <span style={{
                  fontSize: "20px",
                  color: "#6b7280",
                  fontWeight: "600",
                  letterSpacing: "0.5px"
                }}>
                  Что будем смотреть?
                </span>
              </div>
            )}
          </div>

          <div style={{
            border: "1px solid #374151",
            borderRadius: "12px",
            width: "350px",
            height: "450px",
            display: "flex",
            flexDirection: "column",
            background: "#171717"
          }}>
            <div style={{
              padding: "14px",
              background: "#262626",
              borderBottom: "1px solid #374151",
              fontWeight: "600",
              borderRadius: "12px 12px 0 0",
              fontSize: "15px"
            }}>
              Чат комнаты
            </div>

            <div style={{ flex: 1, padding: "15px", overflowY: "auto", display: "flex", flexDirection: "column", gap: "10px" }}>
              {messages.map((msg, index) => (
                <div key={index} style={{ fontSize: "14px", lineHeight: "1.4", wordBreak: "break-word" }}>
                  <b style={{ color: msg.sender === username ? "#ffffff" : "#9ca3af" }}>{msg.sender}: </b>
                  <span style={{ color: "#d1d5db" }}>{msg.content}</span>
                </div>
              ))}
            </div>

            <form onSubmit={sendMessage} style={{ display: "flex", borderTop: "1px solid #374151", padding: "12px", gap: "8px" }}>
              <input
                type="text"
                placeholder="Напишите сообщение..."
                value={typedMessage}
                onChange={(e) => setTypedMessage(e.target.value)}
                style={{
                  flex: 1,
                  padding: "10px 14px",
                  borderRadius: "8px",
                  border: "1px solid #4b5563",
                  background: "#262626",
                  color: "#ffffff",
                  outline: "none",
                  fontSize: "14px"
                }}
              />
              <button
                type="submit"
                style={{
                  padding: "10px 16px",
                  cursor: "pointer",
                  background: "#ffffff",
                  color: "#171717",
                  border: "none",
                  borderRadius: "8px",
                  fontWeight: "600",
                  fontSize: "14px",
                  transition: "0.2s"
                }}
                onMouseOver={(e) => e.currentTarget.style.background = "#e5e7eb"}
                onMouseOut={(e) => e.currentTarget.style.background = "#ffffff"}
              >
                Отпр.
              </button>
            </form>
          </div>

        </div>
      </div>
    </div>
  );
}