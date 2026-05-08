import { ref } from 'vue';

export function useAsyncAction() {
  const isRunning = ref(false);

  async function run<T>(action: () => Promise<T>): Promise<T> {
    isRunning.value = true;
    try {
      return await action();
    } finally {
      isRunning.value = false;
    }
  }

  return {
    isRunning,
    run,
  };
}
