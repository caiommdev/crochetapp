"use client";

import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import {
  clearStoredToken,
  getStoredToken,
  parseJwt,
  setStoredToken,
  type JwtUser,
} from "@/lib/token";

type AuthContextValue = {
  token: string | null;
  user: JwtUser | null;
  isReady: boolean;
  login: (token: string) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    setToken(getStoredToken());
    setIsReady(true);
  }, []);

  function login(newToken: string) {
    setStoredToken(newToken);
    setToken(newToken);
  }

  function logout() {
    clearStoredToken();
    setToken(null);
  }

  const user = token ? parseJwt(token) : null;

  return (
    <AuthContext.Provider value={{ token, user, isReady, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth deve ser usado dentro de <AuthProvider>");
  return ctx;
}
