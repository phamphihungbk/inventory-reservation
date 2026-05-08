import { defineStore } from 'pinia';

import { createProduct, fetchProducts } from '@/api/products';
import { toFriendlyError } from '@/api/http';
import type { CreateProductPayload, Product } from '@/types/product';

interface ProductState {
  products: Product[];
  isLoading: boolean;
  error: string | null;
}

export const useProductStore = defineStore('productStore', {
  state: (): ProductState => ({
    products: [],
    isLoading: false,
    error: null,
  }),
  getters: {
    totalStock: (state) => state.products.reduce((sum, product) => sum + product.stock, 0),
    lowStockCount: (state) => state.products.filter((product) => product.stock < 5).length,
  },
  actions: {
    async loadProducts() {
      this.isLoading = true;
      this.error = null;

      try {
        this.products = await fetchProducts();
      } catch (error) {
        this.error = toFriendlyError(error);
      } finally {
        this.isLoading = false;
      }
    },

    async addProduct(payload: CreateProductPayload) {
      this.error = null;

      try {
        const product = await createProduct(payload);
        this.products = [product, ...this.products];
        return product;
      } catch (error) {
        this.error = toFriendlyError(error);
        throw error;
      }
    },
  },
});
