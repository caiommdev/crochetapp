"use client";

import { useEffect, useState } from "react";
import { budgetsApi, materialsApi, productsApi } from "@/lib/api";
import type { Budget, BudgetStatus, Material, Product } from "@/types";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Pencil, Plus, Trash2 } from "lucide-react";

const STATUS_LABELS: Record<BudgetStatus, string> = {
  IN_VALIDATION: "Em Validação",
  IN_PROGRESS: "Em Andamento",
  CANCELED: "Cancelado",
  DONE: "Concluído",
};

const STATUS_VARIANTS: Record<
  BudgetStatus,
  "default" | "secondary" | "destructive" | "outline"
> = {
  IN_VALIDATION: "secondary",
  IN_PROGRESS: "default",
  CANCELED: "destructive",
  DONE: "outline",
};

type FormData = {
  productId: string;
  status: BudgetStatus;
  materialIds: number[];
};

const EMPTY: FormData = {
  productId: "",
  status: "IN_VALIDATION",
  materialIds: [],
};

export default function BudgetsPage() {
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [materials, setMaterials] = useState<Material[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Budget | null>(null);
  const [form, setForm] = useState<FormData>(EMPTY);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try {
      const [b, p, m] = await Promise.all([
        budgetsApi.list(),
        productsApi.list(),
        materialsApi.list(),
      ]);
      setBudgets(b);
      setProducts(p);
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

  function openEdit(b: Budget) {
    setEditing(b);
    setForm({
      productId: b.product.id,
      status: b.status,
      materialIds: b.materials.map((m) => m.id),
    });
    setOpen(true);
  }

  function toggleMaterial(id: number) {
    setForm((f) => ({
      ...f,
      materialIds: f.materialIds.includes(id)
        ? f.materialIds.filter((m) => m !== id)
        : [...f.materialIds, id],
    }));
  }

  async function handleSubmit() {
    const product = products.find((p) => p.id === form.productId);
    if (!product) return;
    const selectedMaterials = materials.filter((m) =>
      form.materialIds.includes(m.id)
    );
    try {
      const payload = {
        product,
        status: form.status,
        materials: selectedMaterials,
      };
      if (editing) {
        await budgetsApi.update(editing.id, payload);
      } else {
        await budgetsApi.create(payload);
      }
      setOpen(false);
      load();
    } catch {
      setError("Erro ao salvar orçamento.");
    }
  }

  async function handleDelete(id: string) {
    if (!confirm("Confirma a exclusão deste orçamento?")) return;
    try {
      await budgetsApi.remove(id);
      load();
    } catch {
      setError("Erro ao excluir orçamento.");
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Orçamentos</h1>
        <Button onClick={openCreate}>
          <Plus className="mr-2 h-4 w-4" /> Novo Orçamento
        </Button>
      </div>

      {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Produto</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Materiais</TableHead>
            <TableHead className="text-right">Ações</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {budgets.length === 0 && (
            <TableRow>
              <TableCell colSpan={4} className="text-center text-zinc-400 py-8">
                Nenhum orçamento cadastrado.
              </TableCell>
            </TableRow>
          )}
          {budgets.map((b) => (
            <TableRow key={b.id}>
              <TableCell className="font-medium">{b.product.name}</TableCell>
              <TableCell>
                <Badge variant={STATUS_VARIANTS[b.status]}>
                  {STATUS_LABELS[b.status]}
                </Badge>
              </TableCell>
              <TableCell className="text-zinc-500">
                {b.materials.length === 0
                  ? "—"
                  : `${b.materials.length} material(is)`}
              </TableCell>
              <TableCell className="text-right space-x-1">
                <Button size="icon" variant="ghost" onClick={() => openEdit(b)}>
                  <Pencil className="h-4 w-4" />
                </Button>
                <Button
                  size="icon"
                  variant="ghost"
                  onClick={() => handleDelete(b.id)}
                >
                  <Trash2 className="h-4 w-4 text-red-500" />
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {editing ? "Editar Orçamento" : "Novo Orçamento"}
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-1">
              <Label>Produto</Label>
              <Select
                value={form.productId}
                onValueChange={(v) => setForm((f) => ({ ...f, productId: v ?? "" }))}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Selecione um produto" />
                </SelectTrigger>
                <SelectContent>
                  {products.map((p) => (
                    <SelectItem key={p.id} value={p.id}>
                      {p.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-1">
              <Label>Status</Label>
              <Select
                value={form.status}
                onValueChange={(v) =>
                  setForm((f) => ({ ...f, status: (v ?? "IN_VALIDATION") as BudgetStatus }))
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {(
                    Object.entries(STATUS_LABELS) as [BudgetStatus, string][]
                  ).map(([value, label]) => (
                    <SelectItem key={value} value={value}>
                      {label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label>Materiais</Label>
              <div className="max-h-48 overflow-y-auto border rounded-md p-2 space-y-1">
                {materials.length === 0 && (
                  <p className="text-zinc-400 text-sm text-center py-2">
                    Nenhum material disponível
                  </p>
                )}
                {materials.map((m) => (
                  <label
                    key={m.id}
                    className="flex items-center gap-2 cursor-pointer hover:bg-zinc-50 rounded px-2 py-1"
                  >
                    <input
                      type="checkbox"
                      checked={form.materialIds.includes(m.id)}
                      onChange={() => toggleMaterial(m.id)}
                      className="accent-zinc-900"
                    />
                    <span className="text-sm">{m.name}</span>
                    <span className="text-xs text-zinc-400 ml-auto">
                      {m.type === "YARN"
                        ? "Fio"
                        : m.type === "ACCESSORY"
                          ? "Acessório"
                          : "Acessório/m"}
                    </span>
                  </label>
                ))}
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button onClick={handleSubmit} disabled={!form.productId}>
              Salvar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
