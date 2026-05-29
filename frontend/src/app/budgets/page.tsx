"use client";

import { useEffect, useState } from "react";
import { budgetsApi, materialsApi, productsApi } from "@/lib/api";
import type { Budget, BudgetQuote, BudgetStatus, Material, Product } from "@/types";
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
import { CheckCircle, Plus, Trash2, XCircle } from "lucide-react";

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

function formatBRL(value: number) {
  return value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

export default function BudgetsPage() {
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [materials, setMaterials] = useState<Material[]>([]);
  const [open, setOpen] = useState(false);
  const [step, setStep] = useState<"form" | "quote">("form");
  const [productId, setProductId] = useState("");
  const [selectedMaterialIds, setSelectedMaterialIds] = useState<(string | number)[]>([]);
  const [quote, setQuote] = useState<BudgetQuote | null>(null);
  const [loading, setLoading] = useState(false);
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
    setStep("form");
    setProductId("");
    setSelectedMaterialIds([]);
    setQuote(null);
    setError(null);
    setOpen(true);
  }

  function toggleMaterial(id: string | number) {
    setSelectedMaterialIds((prev) =>
      prev.includes(id) ? prev.filter((m) => m !== id) : [...prev, id]
    );
  }

  async function handleGenerateQuote() {
    setLoading(true);
    setError(null);
    try {
      const result = await budgetsApi.createQuote(
        productId,
        selectedMaterialIds.map(String)
      );
      setQuote(result);
      setStep("quote");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Não foi possível gerar o orçamento.");
    } finally {
      setLoading(false);
    }
  }

  async function handleAcceptQuote() {
    if (!quote) return;
    setLoading(true);
    try {
      await budgetsApi.accept(quote.budget.id);
      setOpen(false);
      load();
    } catch {
      setError("Erro ao aceitar orçamento.");
    } finally {
      setLoading(false);
    }
  }

  async function handleCancelQuote() {
    if (!quote) return;
    setLoading(true);
    try {
      await budgetsApi.cancel(quote.budget.id);
      setOpen(false);
      load();
    } catch {
      setError("Erro ao recusar orçamento.");
    } finally {
      setLoading(false);
    }
  }

  async function handleAccept(id: string) {
    try {
      await budgetsApi.accept(id);
      load();
    } catch {
      setError("Erro ao aceitar orçamento.");
    }
  }

  async function handleCancel(id: string) {
    if (
      !confirm(
        "Confirma o cancelamento? Os materiais reservados serão devolvidos ao estoque."
      )
    )
      return;
    try {
      await budgetsApi.cancel(id);
      load();
    } catch {
      setError("Erro ao cancelar orçamento.");
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
              <TableCell
                colSpan={4}
                className="text-center text-zinc-400 py-8"
              >
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
                {b.status === "IN_VALIDATION" && (
                  <>
                    <Button
                      size="icon"
                      variant="ghost"
                      title="Aceitar orçamento"
                      onClick={() => handleAccept(b.id)}
                    >
                      <CheckCircle className="h-4 w-4 text-green-600" />
                    </Button>
                    <Button
                      size="icon"
                      variant="ghost"
                      title="Cancelar orçamento"
                      onClick={() => handleCancel(b.id)}
                    >
                      <XCircle className="h-4 w-4 text-red-500" />
                    </Button>
                  </>
                )}
                {b.status === "IN_PROGRESS" && (
                  <Button
                    size="icon"
                    variant="ghost"
                    title="Cancelar orçamento"
                    onClick={() => handleCancel(b.id)}
                  >
                    <XCircle className="h-4 w-4 text-red-500" />
                  </Button>
                )}
                {(b.status === "CANCELED" || b.status === "DONE") && (
                  <Button
                    size="icon"
                    variant="ghost"
                    onClick={() => handleDelete(b.id)}
                  >
                    <Trash2 className="h-4 w-4 text-red-500" />
                  </Button>
                )}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-lg">
          {step === "form" ? (
            <>
              <DialogHeader>
                <DialogTitle>Novo Orçamento</DialogTitle>
              </DialogHeader>
              <div className="space-y-4">
                <div className="space-y-1">
                  <Label>Produto</Label>
                  <Select value={productId} onValueChange={(v) => setProductId(v ?? "")}>
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
                          checked={selectedMaterialIds.includes(m.id)}
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
                {error && <p className="text-red-500 text-sm">{error}</p>}
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setOpen(false)}>
                  Fechar
                </Button>
                <Button
                  onClick={handleGenerateQuote}
                  disabled={
                    !productId || selectedMaterialIds.length === 0 || loading
                  }
                >
                  {loading ? "Gerando..." : "Gerar Cotação"}
                </Button>
              </DialogFooter>
            </>
          ) : (
            <>
              <DialogHeader>
                <DialogTitle>Cotação do Orçamento</DialogTitle>
              </DialogHeader>
              <div className="space-y-4">
                {quote && (
                  <>
                    <p className="text-sm text-zinc-500">
                      Produto:{" "}
                      <span className="font-medium text-zinc-900">
                        {quote.budget.product.name}
                      </span>
                    </p>
                    <div className="space-y-2">
                      {quote.profitRanges.map((range) => (
                        <div
                          key={range.label}
                          className="border rounded-md p-3"
                        >
                          <p className="font-medium text-sm mb-2">
                            {range.label}
                          </p>
                          <div className="grid grid-cols-2 gap-x-4 gap-y-1 text-sm text-zinc-600">
                            <span>Custo dos materiais:</span>
                            <span className="font-medium text-zinc-900">
                              {formatBRL(range.cost)}
                            </span>
                            <span>Preço sugerido:</span>
                            <span>
                              {formatBRL(range.minPrice)} –{" "}
                              {formatBRL(range.maxPrice)}
                            </span>
                            <span>Lucro estimado:</span>
                            <span>
                              {formatBRL(range.minProfit)} –{" "}
                              {formatBRL(range.maxProfit)}
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </>
                )}
                {error && <p className="text-red-500 text-sm">{error}</p>}
              </div>
              <DialogFooter>
                <Button
                  variant="outline"
                  onClick={handleCancelQuote}
                  disabled={loading}
                >
                  Recusar
                </Button>
                <Button onClick={handleAcceptQuote} disabled={loading}>
                  {loading ? "Processando..." : "Aceitar"}
                </Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}

