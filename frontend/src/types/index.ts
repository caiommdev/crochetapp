export interface Image {
  url?: string;
  altText?: string;
}

export interface Point {
  name: string;
  centimetersPerPoint: number;
}

export interface RecipeMaterialRequirement {
  material: Material;
  quantityNeeded: number;
}

export interface Recipe {
  id: string;
  name: string;
  description?: string;
  points: Point[];
  materialRequirements: RecipeMaterialRequirement[];
  image?: Image;
}

export type MaterialType = "YARN" | "ACCESSORY" | "METER_ACCESSORY";

export interface Material {
  id: number;
  name: string;
  type: MaterialType;
  price: number;
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

export interface ProfitRange {
  label: string;
  cost: number;
  minPrice: number;
  maxPrice: number;
  minProfit: number;
  maxProfit: number;
}

export interface BudgetQuote {
  budget: Budget;
  profitRanges: ProfitRange[];
}
