import type { Config } from 'tailwindcss';

export default {
  content: ['./index.html', './src/**/*.{vue,ts}'],
  theme: {
    extend: {
      colors: {
        ink: '#172033',
        muted: '#667085',
        surface: '#f6f7fb',
        brand: '#2563eb',
      },
      boxShadow: {
        panel: '0 12px 30px rgba(23, 32, 51, 0.08)',
      },
    },
  },
  plugins: [],
} satisfies Config;
