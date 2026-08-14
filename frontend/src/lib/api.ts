import type {
  Budget,
  BudgetQuote,
  LoginResponse,
  Material,
  Product,
  Recipe,
  RegisterInput,
  UpdateUserInput,
  User,
} from "@/types";
import { clearStoredToken, getStoredToken } from "@/lib/token";

const BASE = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

async function req<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getStoredToken();
  const res = await fetch(`${BASE}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(init?.headers ?? {}),
    },
  });
  if (res.status === 401) {
    clearStoredToken();
    if (typeof window !== "undefined" && window.location.pathname !== "/login") {
      window.location.href = "/login";
    }
    throw new Error("Sessão expirada. Faça login novamente.");
  }
  if (!res.ok) {
    let message = `Erro na requisição: ${res.status}`;
    try {
      const body = await res.text();
      if (body) message = body;
    } catch { /* ignore */ }
    throw new Error(message);
  }
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

export const recipesApi = {
  list: () => req<Recipe[]>("/api/recipes"),
  create: (data: Omit<Recipe, "id" | "materialRequirements"> & { points: { name: string; centimetersPerPoint: number; quantity: number }[]; materialRequirements: { materialId: string; quantityNeeded: number }[] }) =>
    req<Recipe>("/api/recipes", { method: "POST", body: JSON.stringify(data) }),
  update: (id: string, data: Omit<Recipe, "id" | "materialRequirements"> & { points: { name: string; centimetersPerPoint: number; quantity: number }[]; materialRequirements: { materialId: string; quantityNeeded: number }[] }) =>
    req<Recipe>(`/api/recipes/${id}`, { method: "PUT", body: JSON.stringify(data) }),
  remove: (id: string) =>
    req<void>(`/api/recipes/${id}`, { method: "DELETE" }),
};

export const materialsApi = {
  list: () => req<Material[]>("/api/materials"),
  create: (data: Omit<Material, "id">) =>
    req<Material>("/api/materials", { method: "POST", body: JSON.stringify(data) }),
  update: (id: string, data: Omit<Material, "id">) =>
    req<Material>(`/api/materials/${id}`, { method: "PUT", body: JSON.stringify(data) }),
  remove: (id: string) =>
    req<void>(`/api/materials/${id}`, { method: "DELETE" }),
};

export const productsApi = {
  list: () => req<Product[]>("/api/products"),
  create: (data: Omit<Product, "id">) =>
    req<Product>("/api/products", { method: "POST", body: JSON.stringify(data) }),
  update: (id: string, data: Omit<Product, "id">) =>
    req<Product>(`/api/products/${id}`, { method: "PUT", body: JSON.stringify(data) }),
  remove: (id: string) =>
    req<void>(`/api/products/${id}`, { method: "DELETE" }),
};

export const budgetsApi = {
  list: () => req<Budget[]>("/api/budgets"),
  create: (data: Omit<Budget, "id">) =>
    req<Budget>("/api/budgets", { method: "POST", body: JSON.stringify(data) }),
  update: (id: string, data: Omit<Budget, "id">) =>
    req<Budget>(`/api/budgets/${id}`, { method: "PUT", body: JSON.stringify(data) }),
  remove: (id: string) =>
    req<void>(`/api/budgets/${id}`, { method: "DELETE" }),
  createQuote: (productId: string, materialIds: string[]) =>
    req<BudgetQuote>("/api/budgets/quote", {
      method: "POST",
      body: JSON.stringify({ productId, materialIds }),
    }),
  accept: (id: string) =>
    req<void>(`/api/budgets/${id}/accept`, { method: "POST" }),
  cancel: (id: string) =>
    req<void>(`/api/budgets/${id}/cancel`, { method: "POST" }),
};

export const authApi = {
  login: (username: string, password: string) =>
    req<LoginResponse>("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({ username, password }),
    }),
  register: (data: RegisterInput) =>
    req<User>("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(data),
    }),
};

export const usersApi = {
  get: (id: string) => req<User>(`/api/users/${id}`),
  update: (id: string, data: UpdateUserInput) =>
    req<User>(`/api/users/${id}`, { method: "PUT", body: JSON.stringify(data) }),
  remove: (id: string) =>
    req<void>(`/api/users/${id}`, { method: "DELETE" }),
};
