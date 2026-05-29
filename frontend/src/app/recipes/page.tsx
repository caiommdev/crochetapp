"use client";

import { useEffect, useState } from "react";
import { materialsApi, recipesApi } from "@/lib/api";
import type { Material, Recipe } from "@/types";
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
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
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
type MaterialReqEntry = { materialId: string; quantityNeeded: string };
type FormData = {
  name: string;
  description: string;
  points: PointEntry[];
  materialRequirements: MaterialReqEntry[];
};

const EMPTY: FormData = {
  name: "",
  description: "",
  points: [],
  materialRequirements: [],
};

export default function RecipesPage() {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [materials, setMaterials] = useState<Material[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Recipe | null>(null);
  const [form, setForm] = useState<FormData>(EMPTY);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try {
      const [r, m] = await Promise.all([recipesApi.list(), materialsApi.list()]);
      setRecipes(r);
      setMaterials(m);
    } catch {
      setError("Erro ao carregar dados.");
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
      points: r.points.map((p) => ({
        key: p.name,
        value: String(p.centimetersPerPoint),
      })),
      materialRequirements: r.materialRequirements.map((req) => ({
        materialId: String(req.material.id),
        quantityNeeded: String(req.quantityNeeded),
      })),
    });
    setOpen(true);
  }

  async function handleSubmit() {
    try {
      const payload = {
        name: form.name,
        description: form.description || undefined,
        points: form.points
          .filter((p) => p.key.trim())
          .map((p) => ({ name: p.key, centimetersPerPoint: Number(p.value) })),
        materialRequirements: form.materialRequirements
          .filter((r) => r.materialId)
          .map((r) => ({
            materialId: r.materialId,
            quantityNeeded: Number(r.quantityNeeded) || 1,
          })),
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

  function addRequirement() {
    setForm((f) => ({
      ...f,
      materialRequirements: [
        ...f.materialRequirements,
        { materialId: "", quantityNeeded: "1" },
      ],
    }));
  }

  function updateRequirement(
    i: number,
    field: "materialId" | "quantityNeeded",
    val: string
  ) {
    setForm((f) => {
      const reqs = [...f.materialRequirements];
      reqs[i] = { ...reqs[i], [field]: val };
      return { ...f, materialRequirements: reqs };
    });
  }

  function removeRequirement(i: number) {
    setForm((f) => ({
      ...f,
      materialRequirements: f.materialRequirements.filter((_, idx) => idx !== i),
    }));
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Receitas</h1>
        <Button onClick={openCreate}>
          <Plus className="mr-2 h-4 w-4" /> Nova Receita
        </Button>
      </div>

      {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Nome</TableHead>
            <TableHead>Descrição</TableHead>
            <TableHead>Pontos</TableHead>
            <TableHead>Materiais</TableHead>
            <TableHead className="text-right">Ações</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {recipes.length === 0 && (
            <TableRow>
              <TableCell colSpan={5} className="text-center text-zinc-400 py-8">
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
              <TableCell>{r.points.length} ponto(s)</TableCell>
              <TableCell>{r.materialRequirements.length} material(is)</TableCell>
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
        <DialogContent className="max-w-lg max-h-[85vh] overflow-y-auto">
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
                <Label>Pontos (tipo de ponto → cm/ponto)</Label>
                <Button size="sm" variant="outline" onClick={addPoint}>
                  <Plus className="h-3 w-3 mr-1" /> Adicionar
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
                    placeholder="cm"
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

            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <Label>Materiais necessários</Label>
                <Button size="sm" variant="outline" onClick={addRequirement}>
                  <Plus className="h-3 w-3 mr-1" /> Adicionar
                </Button>
              </div>
              {form.materialRequirements.map((req, i) => (
                <div key={i} className="flex gap-2 items-center">
                  <Select
                    value={req.materialId}
                    onValueChange={(v) => updateRequirement(i, "materialId", v)}
                  >
                    <SelectTrigger className="flex-1">
                      <SelectValue placeholder="Selecione o material" />
                    </SelectTrigger>
                    <SelectContent>
                      {materials.map((m) => (
                        <SelectItem key={m.id} value={String(m.id)}>
                          {m.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <Input
                    placeholder="Qtd"
                    type="number"
                    value={req.quantityNeeded}
                    onChange={(e) =>
                      updateRequirement(i, "quantityNeeded", e.target.value)
                    }
                    className="w-24"
                  />
                  <Button
                    size="icon"
                    variant="ghost"
                    onClick={() => removeRequirement(i)}
                  >
                    <Trash2 className="h-4 w-4 text-red-500" />
                  </Button>
                </div>
              ))}
              {form.materialRequirements.length === 0 && (
                <p className="text-sm text-zinc-400">
                  Nenhum material adicionado. Os materiais definem o custo do orçamento.
                </p>
              )}
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
