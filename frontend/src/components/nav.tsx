"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { LogOut, User as UserIcon } from "lucide-react";
import { cn } from "@/lib/utils";
import { useAuth } from "@/lib/auth";

const links = [
  { href: "/", label: "Dashboard" },
  { href: "/recipes", label: "Receitas" },
  { href: "/products", label: "Produtos" },
  { href: "/materials", label: "Materiais" },
  { href: "/budgets", label: "Orçamentos" },
];

export function Nav() {
  const pathname = usePathname();
  const { user, logout } = useAuth();

  return (
    <aside className="w-56 min-h-screen border-r bg-zinc-50 p-4 flex flex-col gap-1 shrink-0">
      <p className="font-bold text-lg mb-6 px-3">🧶 CrochetApp</p>
      {links.map((link) => (
        <Link
          key={link.href}
          href={link.href}
          className={cn(
            "px-3 py-2 rounded-md text-sm font-medium transition-colors",
            pathname === link.href
              ? "bg-zinc-200 text-zinc-900"
              : "text-zinc-600 hover:bg-zinc-100 hover:text-zinc-900"
          )}
        >
          {link.label}
        </Link>
      ))}

      <div className="mt-auto flex flex-col gap-1 border-t pt-3">
        <Link
          href="/profile"
          className={cn(
            "flex items-center gap-2 px-3 py-2 rounded-md text-sm font-medium transition-colors",
            pathname === "/profile"
              ? "bg-zinc-200 text-zinc-900"
              : "text-zinc-600 hover:bg-zinc-100 hover:text-zinc-900"
          )}
        >
          <UserIcon className="h-4 w-4" />
          {user?.username ?? "Perfil"}
        </Link>
        <button
          type="button"
          onClick={logout}
          className="flex items-center gap-2 px-3 py-2 rounded-md text-sm font-medium text-zinc-600 hover:bg-zinc-100 hover:text-zinc-900 transition-colors"
        >
          <LogOut className="h-4 w-4" />
          Sair
        </button>
      </div>
    </aside>
  );
}
