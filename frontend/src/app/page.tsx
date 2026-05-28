import Link from "next/link";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";

const sections = [
  {
    href: "/recipes",
    title: "Receitas",
    description: "Gerencie as receitas de crochê com pontos e descrições",
  },
  {
    href: "/products",
    title: "Produtos",
    description: "Cadastre produtos vinculados às receitas",
  },
  {
    href: "/materials",
    title: "Materiais",
    description: "Controle fios, acessórios e acessórios por metro",
  },
  {
    href: "/budgets",
    title: "Orçamentos",
    description: "Acompanhe o status dos orçamentos de produção",
  },
];

export default function Home() {
  return (
    <div>
      <h1 className="text-3xl font-bold mb-2">Dashboard</h1>
      <p className="text-zinc-500 mb-8">Bem-vindo ao CrochetApp</p>
      <div className="grid grid-cols-2 gap-4 max-w-2xl">
        {sections.map((s) => (
          <Link key={s.href} href={s.href}>
            <Card className="hover:shadow-md transition-shadow cursor-pointer h-full">
              <CardHeader>
                <CardTitle>{s.title}</CardTitle>
                <CardDescription>{s.description}</CardDescription>
              </CardHeader>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}


