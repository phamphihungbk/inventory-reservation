export interface Product {
  id: number;
  name: string;
  stock: number;
  price: number;
  version: number;
  createdAt: string;
}

export interface CreateProductPayload {
  name: string;
  stock: number;
  price: number;
}
