"use client";

import { useEffect } from "react";
import { usePathname, useRouter } from "next/navigation";
import { useAuth } from "@/lib/auth";
import { Nav } from "@/components/nav";

export function AppShell({ children }: { children: React.ReactNode }) {
  const { token, isReady } = useAuth();
  const pathname = usePathname();
  const router = useRouter();
  const isLoginPage = pathname === "/login";

  useEffect(() => {
    if (!isReady) return;
    if (!token && !isLoginPage) router.replace("/login");
    if (token && isLoginPage) router.replace("/");
  }, [isReady, token, isLoginPage, router]);

  if (!isReady) return null;

  if (isLoginPage) {
    return (
      <main className="flex-1 flex items-center justify-center p-8">{children}</main>
    );
  }

  if (!token) return null;

  return (
    <>
      <Nav />
      <main className="flex-1 p-8">{children}</main>
    </>
  );
}
