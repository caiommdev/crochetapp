"use client";

import { useEffect, useState } from "react";
import { recipesApi } from "@/lib/api";
import type { Recipe } from "@/types";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Pencil, Plus, Trash2 } from "lucide-react";

type PointEntry = { key: string; value: string };
type FormData = { name: string; description: string; points: PointEntry[] };

const EMPTY: FormData = { name: "", description: "", points: [] };

export default function RecipesPage() {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Recipe | null>(null);
  const [form, setForm] = useState<FormData>(EMPTY);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try {
      setRecipes(await recipesApi.list());
    } catch {
      setError("Erro ao carregar receitas.");
    }
  }

  useEffect(() => {
    load();
  }, []);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY);
    setOpen(true);
  }

  function openEdit(r: Recipe) {
    setEditing(r);
    setForm({
      name: r.name,
      description: r.description ?? "",
      points: Object.entries(r.points).map(([key, value]) => ({
        key,
        value: String(value),
      })),
    });
    setOpen(true);
  }

  async function handleSubmit() {
        console.log("TESTE");
    try {
      const payload = {
        name: form.name,
        description: form.description || undefined,
        points: Object.fromEntries(
          form.points
            .filter((p) => p.key.trim())
            .map((p) => [p.key, Number(p.value)])
        ),
      };
      if (editing) {
        await recipesApi.update(editing.id, payload);
      } else {
        await recipesApi.create(payload);
      }
      setOpen(false);
      load();
    } catch {
      setError("Erro ao salvar receita.");
    }
  }

  async function handleDelete(id: string) {
    if (!confirm("Confirma a exclusão desta receita?")) return;
    try {
      await recipesApi.remove(id);
      load();
    } catch {
      setError("Erro ao excluir receita.");
    }
  }

  function addPoint() {
    setForm((f) => ({ ...f, points: [...f.points, { key: "", value: "0" }] }));
  }

  function updatePoint(i: number, field: "key" | "value", val: string) {
    setForm((f) => {
      const points = [...f.points];
      points[i] = { ...points[i], [field]: val };
      return { ...f, points };
    });
  }

  function removePoint(i: number) {
    setForm((f) => ({ ...f, points: f.points.filter((_, idx) => idx !== i) }));
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Receitas</h1>
        <Button onClick={openCreate}>
          <Plus className="mr-2 h-4 w-4" /> Nova Receita
        </Button>
      </div>

      {error && (
        <p className="text-red-500 text-sm mb-4">{error}</p>
      )}

      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Nome</TableHead>
            <TableHead>Descrição</TableHead>
            <TableHead>Pontos</TableHead>
            <TableHead className="text-right">Ações</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {recipes.length === 0 && (
            <TableRow>
              <TableCell colSpan={4} className="text-center text-zinc-400 py-8">
                Nenhuma receita cadastrada.
              </TableCell>
            </TableRow>
          )}
          {recipes.map((r) => (
            <TableRow key={r.id}>
              <TableCell className="font-medium">{r.name}</TableCell>
              <TableCell className="text-zinc-500 max-w-xs truncate">
                {r.description ?? "—"}
              </TableCell>
              <TableCell>{Object.keys(r.points).length} ponto(s)</TableCell>
              <TableCell className="text-right space-x-1">
                <Button size="icon" variant="ghost" onClick={() => openEdit(r)}>
                  <Pencil className="h-4 w-4" />
                </Button>
                <Button
                  size="icon"
                  variant="ghost"
                  onClick={() => handleDelete(r.id)}
                >
                  <Trash2 className="h-4 w-4 text-red-500" />
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>
              {editing ? "Editar Receita" : "Nova Receita"}
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-1">
              <Label>Nome</Label>
              <Input
                value={form.name}
                onChange={(e) =>
                  setForm((f) => ({ ...f, name: e.target.value }))
                }
                placeholder="Nome da receita"
              />
            </div>
            <div className="space-y-1">
              <Label>Descrição</Label>
              <Input
                value={form.description}
                onChange={(e) =>
                  setForm((f) => ({ ...f, description: e.target.value }))
                }
                placeholder="Descrição opcional"
              />
            </div>
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <Label>Pontos</Label>
                <Button size="sm" variant="outline" onClick={addPoint}>
                  <Plus className="h-3 w-3 mr-1" /> Adicionar ponto
                </Button>
              </div>
              {form.points.map((p, i) => (
                <div key={i} className="flex gap-2 items-center">
                  <Input
                    placeholder="Nome do ponto"
                    value={p.key}
                    onChange={(e) => updatePoint(i, "key", e.target.value)}
                  />
                  <Input
                    placeholder="Qtd"
                    type="number"
                    value={p.value}
                    onChange={(e) => updatePoint(i, "value", e.target.value)}
                    className="w-24"
                  />
                  <Button
                    size="icon"
                    variant="ghost"
                    onClick={() => removePoint(i)}
                  >
                    <Trash2 className="h-4 w-4 text-red-500" />
                  </Button>
                </div>
              ))}
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button onClick={handleSubmit} disabled={!form.name.trim()}>
              Salvar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
