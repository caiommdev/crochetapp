"use client";

import { useEffect, useState } from "react";
import { materialsApi } from "@/lib/api";
import type { Material, MaterialType } from "@/types";
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

const TYPE_LABELS: Record<MaterialType, string> = {
  YARN: "Fio",
  ACCESSORY: "Acessório",
  METER_ACCESSORY: "Acessório por Metro",
};

type FormData = {
  name: string;
  type: MaterialType;
  price: string;
  color: string;
  quantity: string;
  meters: string;
};

const EMPTY: FormData = {
  name: "",
  type: "YARN",
  price: "",
  color: "",
  quantity: "",
  meters: "",
};

export default function MaterialsPage() {
  const [materials, setMaterials] = useState<Material[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Material | null>(null);
  const [form, setForm] = useState<FormData>(EMPTY);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try {
      setMaterials(await materialsApi.list());
    } catch {
      setError("Erro ao carregar materiais.");
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

  function openEdit(m: Material) {
    setEditing(m);
    setForm({
      name: m.name,
      type: m.type,
      price: String(m.price),
      color: m.color ?? "",
      quantity: m.quantity != null ? String(m.quantity) : "",
      meters: m.meters != null ? String(m.meters) : "",
    });
    setOpen(true);
  }

  async function handleSubmit() {
    try {
      const payload: Omit<Material, "id"> = {
        name: form.name,
        type: form.type,
        price: Number(form.price),
        ...(form.type === "YARN" && {
          color: form.color || undefined,
          quantity: form.quantity ? Number(form.quantity) : undefined,
          meters: form.meters ? Number(form.meters) : undefined,
        }),
        ...(form.type === "ACCESSORY" && {
          quantity: form.quantity ? Number(form.quantity) : undefined,
        }),
        ...(form.type === "METER_ACCESSORY" && {
          meters: form.meters ? Number(form.meters) : undefined,
        }),
      };
      if (editing) {
        await materialsApi.update(editing.id, payload);
      } else {
        await materialsApi.create(payload);
      }
      setOpen(false);
      load();
    } catch {
      setError("Erro ao salvar material.");
    }
  }

  async function handleDelete(id: string) {
    if (!confirm("Confirma a exclusão deste material?")) return;
    try {
      await materialsApi.remove(id);
      load();
    } catch {
      setError("Erro ao excluir material.");
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Materiais</h1>
        <Button onClick={openCreate}>
          <Plus className="mr-2 h-4 w-4" /> Novo Material
        </Button>
      </div>

      {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Nome</TableHead>
            <TableHead>Tipo</TableHead>
            <TableHead>Detalhes</TableHead>
            <TableHead className="text-right">Ações</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {materials.length === 0 && (
            <TableRow>
              <TableCell colSpan={4} className="text-center text-zinc-400 py-8">
                Nenhum material cadastrado.
              </TableCell>
            </TableRow>
          )}
          {materials.map((m) => (
            <TableRow key={m.id}>
              <TableCell className="font-medium">{m.name}</TableCell>
              <TableCell>
                <Badge variant="secondary">{TYPE_LABELS[m.type]}</Badge>
              </TableCell>
              <TableCell className="text-zinc-500 text-sm">
                {m.type === "YARN" &&
                  [
                    m.color && `Cor: ${m.color}`,
                    m.quantity && `Qtd: ${m.quantity}`,
                    m.meters && `${m.meters}m`,
                  ]
                    .filter(Boolean)
                    .join(" · ") || "—"}
                {m.type === "ACCESSORY" &&
                  (m.quantity ? `Qtd: ${m.quantity}` : "—")}
                {m.type === "METER_ACCESSORY" &&
                  (m.meters ? `${m.meters}m` : "—")}
              </TableCell>
              <TableCell className="text-right space-x-1">
                <Button size="icon" variant="ghost" onClick={() => openEdit(m)}>
                  <Pencil className="h-4 w-4" />
                </Button>
                <Button
                  size="icon"
                  variant="ghost"
                  onClick={() => handleDelete(m.id)}
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
              {editing ? "Editar Material" : "Novo Material"}
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
                placeholder="Nome do material"
              />
            </div>
            <div className="space-y-1">
              <Label>Preço (R$)</Label>
              <Input
                type="number"
                step="0.01"
                value={form.price}
                onChange={(e) =>
                  setForm((f) => ({ ...f, price: e.target.value }))
                }
                placeholder="0,00"
              />
            </div>
            <div className="space-y-1">
              <Label>Tipo</Label>
              <Select
                value={form.type}
                onValueChange={(v) =>
                  setForm((f) => ({ ...f, type: v as MaterialType }))
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="YARN">Fio</SelectItem>
                  <SelectItem value="ACCESSORY">Acessório</SelectItem>
                  <SelectItem value="METER_ACCESSORY">
                    Acessório por Metro
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>

            {form.type === "YARN" && (
              <>
                <div className="space-y-1">
                  <Label>Cor</Label>
                  <Input
                    value={form.color}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, color: e.target.value }))
                    }
                    placeholder="Ex: Azul royal"
                  />
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-1">
                    <Label>Quantidade</Label>
                    <Input
                      type="number"
                      value={form.quantity}
                      onChange={(e) =>
                        setForm((f) => ({ ...f, quantity: e.target.value }))
                      }
                    />
                  </div>
                  <div className="space-y-1">
                    <Label>Metros</Label>
                    <Input
                      type="number"
                      value={form.meters}
                      onChange={(e) =>
                        setForm((f) => ({ ...f, meters: e.target.value }))
                      }
                    />
                  </div>
                </div>
              </>
            )}

            {form.type === "ACCESSORY" && (
              <div className="space-y-1">
                <Label>Quantidade</Label>
                <Input
                  type="number"
                  value={form.quantity}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, quantity: e.target.value }))
                  }
                />
              </div>
            )}

            {form.type === "METER_ACCESSORY" && (
              <div className="space-y-1">
                <Label>Metros</Label>
                <Input
                  type="number"
                  value={form.meters}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, meters: e.target.value }))
                  }
                />
              </div>
            )}
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
