import { useState } from "react";

export default function Auth({ onLoginSuccess }) {
  const [isRegister, setIsRegister] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");

    if (!username.trim() || !password.trim()) {
      setError("Заполните все поля!");
      return;
    }

    const endpoint = isRegister ? "/api/auth/register" : "/api/auth/login";

    try {
      const response = await fetch(`http://localhost:8080${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      const data = await response.text();

      if (!response.ok) {
        setError(data);
        return;
      }

      if (isRegister) {
        setMessage("Успешная регистрация! Теперь войдите.");
        setIsRegister(false);
        setPassword("");
      } else {
        onLoginSuccess(username);
      }
    } catch (err) {
      setError("Не удалось связаться с сервером");
    }
  };

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

      backgroundImage: "url('https://i.pinimg.com/1200x/df/dc/d4/dfdcd4c20356fc338c35fbf53842febd.jpg')",
      backgroundSize: "contain",
      backgroundPosition: "center",
      backgroundRepeat: "no-repeat",
      backgroundColor: "#0a0a0a",

      fontFamily: "sans-serif"
    }}>

      <div style={{
        maxWidth: "360px",
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

        {/* заголовок */}
        <h2 style={{
          fontSize: "26px",
          fontWeight: "700",
          lineHeight: "1.3",
          marginBottom: "30px",
          color: "#ffffff",
          letterSpacing: "-0.5px"
        }}>
          {isRegister ? "Регистрация" : "Вход в WatchTogether"}
        </h2>

        {/* плашка об ошибке */}
        {error && (
          <div style={{
            background: "rgba(220, 38, 38, 0.15)",
            color: "#fca5a5",
            border: "1px solid rgba(220, 38, 38, 0.2)",
            padding: "12px",
            borderRadius: "10px",
            marginBottom: "20px",
            fontSize: "14px",
            textAlign: "center"
          }}>
            {error}
          </div>
        )}

        {/* плашка успеха */}
        {message && (
          <div style={{
            background: "rgba(16, 185, 129, 0.15)",
            color: "#a7f3d0",
            border: "1px solid rgba(16, 185, 129, 0.2)",
            padding: "12px",
            borderRadius: "10px",
            marginBottom: "20px",
            fontSize: "14px",
            textAlign: "center"
          }}>
            {message}
          </div>
        )}

        {/* форма авторизации */}
        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "15px" }}>
          <input
            type="text"
            placeholder="Имя пользователя"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            style={{
              padding: "14px 18px",
              borderRadius: "10px",
              border: "1px solid #374151",
              background: "#171717",
              color: "#ffffff",
              fontSize: "15px",
              outline: "none"
            }}
          />
          <input
            type="password"
            placeholder="Пароль"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{
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
              padding: "14px",
              background: "#ffffff",
              color: "#171717",
              border: "none",
              borderRadius: "10px",
              cursor: "pointer",
              fontWeight: "600",
              fontSize: "15px",
              marginTop: "10px",
              transition: "all 0.2s"
            }}
            onMouseOver={(e) => e.currentTarget.style.background = "#e5e7eb"}
            onMouseOut={(e) => e.currentTarget.style.background = "#ffffff"}
          >
            {isRegister ? "Создать аккаунт" : "Войти"}
          </button>
        </form>

        {/* переключатель режимов */}
        <div style={{ marginTop: "25px", textAlign: "center", fontSize: "14px" }}>
          <span style={{ color: "#9ca3af" }}>
            {isRegister ? "Уже есть аккаунт? " : "Впервые тут? "}
          </span>
          <button
            onClick={() => { setIsRegister(!isRegister); setError(""); setMessage(""); }}
            style={{
              background: "none",
              border: "none",
              color: "#ffffff",
              cursor: "pointer",
              textDecoration: "underline",
              fontWeight: "600",
              padding: 0
            }}
          >
            {isRegister ? "Войти" : "Зарегистрироваться"}
          </button>
        </div>
      </div>
    </div>
  );
}