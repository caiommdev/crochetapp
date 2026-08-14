"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { usersApi } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export default function ProfilePage() {
  const router = useRouter();
  const { user, logout } = useAuth();
  const userId = user?.userId;

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    if (!userId) return;
    usersApi
      .get(userId)
      .then((u) => {
        setUsername(u.username);
        setEmail(u.email);
      })
      .catch(() => setError("Erro ao carregar o perfil."))
      .finally(() => setLoading(false));
  }, [userId]);

  async function handleUpdate(e: React.FormEvent) {
    e.preventDefault();
    if (!userId) return;
    setError(null);
    setMessage(null);
    try {
      await usersApi.update(userId, { username, email });
      setMessage("Dados atualizados! Faça login novamente para renovar o token.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erro ao atualizar.");
    }
  }

  async function handleDelete() {
    if (!userId) return;
    if (!confirm("Tem certeza que deseja apagar sua conta? Esta ação é irreversível.")) return;
    try {
      await usersApi.remove(userId);
      logout();
      router.replace("/login");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erro ao apagar a conta.");
    }
  }

  if (loading) return <p className="text-zinc-500">Carregando...</p>;

  return (
    <div className="max-w-md">
      <h1 className="text-2xl font-bold mb-6">Meu Perfil</h1>
      <form onSubmit={handleUpdate} className="flex flex-col gap-4">
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="username">Usuário</Label>
          <Input
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="email">E-mail</Label>
          <Input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        {error && <p className="text-red-500 text-sm">{error}</p>}
        {message && <p className="text-green-600 text-sm">{message}</p>}
        <div className="flex gap-2">
          <Button type="submit">Salvar alterações</Button>
          <Button type="button" variant="destructive" onClick={handleDelete}>
            Apagar conta
          </Button>
        </div>
      </form>
    </div>
  );
}
