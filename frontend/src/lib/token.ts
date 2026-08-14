export const TOKEN_KEY = "crochet_token";

export function getStoredToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(TOKEN_KEY);
}

export function setStoredToken(token: string) {
  if (typeof window !== "undefined") localStorage.setItem(TOKEN_KEY, token);
}

export function clearStoredToken() {
  if (typeof window !== "undefined") localStorage.removeItem(TOKEN_KEY);
}

export type JwtUser = { userId?: string; username?: string; email?: string };

export function parseJwt(token: string): JwtUser | null {
  try {
    const payload = token.split(".")[1];
    const json = JSON.parse(
      decodeURIComponent(
        atob(payload.replace(/-/g, "+").replace(/_/g, "/"))
          .split("")
          .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
          .join("")
      )
    );
    return { userId: json.userId, username: json.sub, email: json.email };
  } catch {
    return null;
  }
}
