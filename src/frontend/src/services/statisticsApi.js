import axios from 'axios';

const API_BASE_URL = 'https://outwardly-phytocidal-ola.ngrok-free.dev/api';

// Default test user ID (UUID format)
const DEFAULT_USER_ID = '5a27be9d-beef-4112-9466-277312593d62';

// Mock data for development when backend is not available
const mockUserStatistics = {
  statementPeriod: "2025-09-02 - 2025-10-19",
  totalIncome: 98409.2,
  netWorth: 26816.2,
  totalExpenses: 71593.0,
  message: "Выписка успешно обработана",
  transactionsCount: 553
};

const mockOverviewStatistics = {
  totalTransactions: 553,
  totalIncome: 98409.2,
  netWorth: 26816.2,
  totalExpenses: 71593.0,
  message: "Общая статистика системы"
};

const mockCategoryStatistics = {
  totalAmount: 170002.2,
  totalTransactions: 553,
  categories: [
    {
      totalAmount: 157880.2,
      averageAmount: 301.87,
      percentage: 92.87,
      transactionCount: 523,
      category: "OTHER_EXPENSE",
      categoryName: "Прочие расходы"
    },
    {
      totalAmount: 10045.0,
      averageAmount: 386.35,
      percentage: 5.91,
      transactionCount: 26,
      category: "FOOD",
      categoryName: "Еда и напитки"
    },
    {
      totalAmount: 2077.0,
      averageAmount: 519.25,
      percentage: 1.22,
      transactionCount: 4,
      category: "HEALTHCARE",
      categoryName: "Здоровье"
    }
  ],
  message: "Статистика по категориям успешно получена"
};

export const statisticsApi = {
  // Получить статистику пользователя
  getUserStatistics: async (userId) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/statistics/user/5a27be9d-beef-4112-9466-277312593d62`, {
        timeout: 5000, // 5 second timeout
        headers: {
          'ngrok-skip-browser-warning': 'true'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching user statistics:', error);
      
      // Check if it's a connection error
      if (error.code === 'ERR_NETWORK' || error.code === 'ECONNREFUSED') {
        console.warn('Backend server not available, using mock data for user statistics');
        return mockUserStatistics;
      }
      
      throw error;
    }
  },

  // Получить общую статистику системы
  getOverviewStatistics: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/statistics/overview`, {
        timeout: 5000, // 5 second timeout
        headers: {
          'ngrok-skip-browser-warning': 'true'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching overview statistics:', error);
      
      // Check if it's a connection error
      if (error.code === 'ERR_NETWORK' || error.code === 'ECONNREFUSED') {
        console.warn('Backend server not available, using mock data for overview statistics');
        return mockOverviewStatistics;
      }
      
      throw error;
    }
  },

  // Получить статистику по категориям для пользователя
  getCategoryStatistics: async (userId) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/statistics/categories/user/5a27be9d-beef-4112-9466-277312593d62`, {
        timeout: 5000, // 5 second timeout
        headers: {
          'ngrok-skip-browser-warning': 'true'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching category statistics:', error);
      
      // Check if it's a connection error
      if (error.code === 'ERR_NETWORK' || error.code === 'ECONNREFUSED') {
        console.warn('Backend server not available, using mock data for category statistics');
        return mockCategoryStatistics;
      }
      
      throw error;
    }
  }
};
