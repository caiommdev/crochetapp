import type { Budget, Material, Product, Recipe } from "@/types";

const BASE = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

async function req<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...init,
  });
  if (!res.ok) throw new Error(`Erro na requisição: ${res.status}`);
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

export const recipesApi = {
  list: () => req<Recipe[]>("/api/recipes"),
  create: (data: Omit<Recipe, "id">) =>
    req<Recipe>("/api/recipes", { method: "POST", body: JSON.stringify(data) }),
  update: (id: string, data: Omit<Recipe, "id">) =>
    req<Recipe>(`/api/recipes/${id}`, { method: "PUT", body: JSON.stringify(data) }),
  remove: (id: string) =>
    req<void>(`/api/recipes/${id}`, { method: "DELETE" }),
};

export const materialsApi = {
  list: () => req<Material[]>("/api/materials"),
  create: (data: Omit<Material, "id">) =>
    req<Material>("/api/materials", { method: "POST", body: JSON.stringify(data) }),
  update: (id: number, data: Omit<Material, "id">) =>
    req<Material>(`/api/materials/${id}`, { method: "PUT", body: JSON.stringify(data) }),
  remove: (id: number) =>
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
};
