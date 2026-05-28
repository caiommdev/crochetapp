export interface Image {
  url?: string;
  altText?: string;
}

export interface Recipe {
  id: string;
  name: string;
  description?: string;
  points: Record<string, number>;
  image?: Image;
}

export type MaterialType = "YARN" | "ACCESSORY" | "METER_ACCESSORY";

export interface Material {
  id: number;
  name: string;
  type: MaterialType;
  image?: Image;
  // Yarn
  color?: string;
  quantity?: number;
  // MeterAccessory
  meters?: number;
}

export interface Product {
  id: string;
  name: string;
  recipe: Recipe;
  image?: Image;
}

export type BudgetStatus = "IN_VALIDATION" | "IN_PROGRESS" | "CANCELED" | "DONE";

export interface Budget {
  id: string;
  product: Product;
  materials: Material[];
  status: BudgetStatus;
}
