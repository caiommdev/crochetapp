"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";

const links = [
  { href: "/", label: "Dashboard" },
  { href: "/recipes", label: "Receitas" },
  { href: "/products", label: "Produtos" },
  { href: "/materials", label: "Materiais" },
  { href: "/budgets", label: "Orçamentos" },
];

export function Nav() {
  const pathname = usePathname();
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
    </aside>
  );
}
