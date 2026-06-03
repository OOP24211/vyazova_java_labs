import { useState, useEffect } from "react";
import Auth from "./components/Auth";
import WebSocketTest from "./WebSocketTest";

export default function App() {
  const [currentUser, setCurrentUser] = useState(null);
  const [currentRoom, setCurrentRoom] = useState(null);
  const [roomInput, setRoomInput] = useState("");

  const [activeRooms, setActiveRooms] = useState([]);

  useEffect(() => {
    if (!currentUser || currentRoom) return;

    const fetchRooms = () => {
      fetch("http://localhost:8080/api/rooms")
        .then((res) => res.json())
        .then((data) => {
          setActiveRooms(data);
        })
        .catch((err) => console.error("Ошибка загрузки комнат:", err));
    };

    fetchRooms();
    const interval = setInterval(fetchRooms, 2000);

    return () => clearInterval(interval);
  }, [currentUser, currentRoom]);

  if (!currentUser) {
    return <Auth onLoginSuccess={(username) => setCurrentUser(username)} />;
  }

  if (!currentRoom) {
    return (
      <div style={{
        minHeight: "100vh",
        width: "100vw",
        position: "fixed",
        top: 0,
        left: 0,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",

        backgroundImage: "url('https://i.pinimg.com/736x/93/84/37/938437e03df25081085f1c9990b8ddd1.jpg')",
        backgroundSize: "contain",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        backgroundColor: "#0a0a0a",

        fontFamily: "sans-serif"
      }}>

        {/* центральное окошко */}
        <div style={{
          maxWidth: "550px",
          width: "100%",
          padding: "40px",
          margin: "20px",
          textAlign: "center",
          color: "#f3f4f6",

          background: "rgba(23, 23, 23, 0.82)",
          borderRadius: "20px",
          boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.7)",
          backdropFilter: "blur(10px)",
          border: "1px solid rgba(255, 255, 255, 0.05)"
        }}>

          {/* шапка пользователя */}
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "35px" }}>
            <span style={{ color: "#9ca3af" }}>Привет, <b style={{ color: "#ffffff" }}>{currentUser}</b>! 🐾</span>
            <button
              onClick={() => setCurrentUser(null)}
              style={{
                padding: "6px 14px",
                background: "rgba(220, 38, 38, 0.15)",
                color: "#fca5a5",
                border: "1px solid rgba(220, 38, 38, 0.3)",
                borderRadius: "8px",
                cursor: "pointer",
                fontSize: "13px",
                fontWeight: "500",
                transition: "0.2s"
              }}
              onMouseOver={(e) => e.currentTarget.style.background = "rgba(220, 38, 38, 0.25)"}
              onMouseOut={(e) => e.currentTarget.style.background = "rgba(220, 38, 38, 0.15)"}
            >
              Выйти
            </button>
          </div>

          {/* заголовок */}
          <h1 style={{
            fontSize: "28px",
            fontWeight: "700",
            lineHeight: "1.3",
            marginBottom: "35px",
            color: "#ffffff",
            letterSpacing: "-0.5px"
          }}>
            Создать комнату или войти
          </h1>

          {/* форма ввода */}
          <form
            onSubmit={(e) => {
              e.preventDefault();
              if (roomInput.trim()) {
                setCurrentRoom(roomInput.trim());
                setRoomInput("");
              }
            }}
            style={{ display: "flex", gap: "12px", marginBottom: "35px" }}
          >
            <input
              type="text"
              placeholder="Введите название комнаты..."
              value={roomInput}
              onChange={(e) => setRoomInput(e.target.value)}
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
                transition: "all 0.2s"
              }}
              onMouseOver={(e) => e.currentTarget.style.background = "#e5e7eb"}
              onMouseOut={(e) => e.currentTarget.style.background = "#ffffff"}
            >
              Создать
            </button>
          </form>

          <div style={{ textAlign: "left", margin: "20px 0 12px 4px", color: "#9ca3af", fontSize: "14px", fontWeight: "600" }}>
            Активные комнаты:
          </div>

          {/* список активных комнат */}
          <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
            {activeRooms.length === 0 ? (
              <p style={{ color: "#6b7280", fontStyle: "italic", padding: "15px", fontSize: "14px" }}>
                Активных комнат пока нет. Создайте первую!
              </p>
            ) : (
              activeRooms.map((room) => (
                <button
                  key={room.id}
                  onClick={() => setCurrentRoom(room.id)}
                  style={{
                    padding: "14px 18px",
                    background: "rgba(255, 255, 255, 0.03)",
                    border: "1px solid rgba(255, 255, 255, 0.08)",
                    borderRadius: "10px",
                    textAlign: "left",
                    cursor: "pointer",
                    fontSize: "15px",
                    color: "#e5e7eb",
                    fontWeight: "500",
                    transition: "all 0.2s"
                  }}
                  onMouseOver={(e) => {
                    e.currentTarget.style.background = "rgba(255, 255, 255, 0.08)";
                    e.currentTarget.style.border = "1px solid rgba(255, 255, 255, 0.2)";
                    e.currentTarget.style.color = "#ffffff";
                  }}
                  onMouseOut={(e) => {
                    e.currentTarget.style.background = "rgba(255, 255, 255, 0.03)";
                    e.currentTarget.style.border = "1px solid rgba(255, 255, 255, 0.08)";
                    e.currentTarget.style.color = "#e5e7eb";
                  }}
                >
                  🎬 {room.id}
                </button>
              ))
            )}
          </div>
        </div>
      </div>
    );
  }

  return (
    <WebSocketTest
      roomId={currentRoom}
      userNickname={currentUser}
      onLeaveRoom={() => setCurrentRoom(null)}
    />
  );
}