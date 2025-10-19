import { useState, useEffect } from 'react';
import { 
  PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, 
  ResponsiveContainer, LineChart, Line, Area, AreaChart, Legend 
} from 'recharts';
import { 
  MdTrendingUp, MdTrendingDown, MdAttachMoney, MdDateRange, 
  MdAccountBalance, MdCategory, MdReceipt 
} from 'react-icons/md';
import { statisticsApi } from '../services/statisticsApi';

const StatisticsDashboard = ({ userId, onClose }) => {
  const [userStats, setUserStats] = useState(null);
  const [overviewStats, setOverviewStats] = useState(null);
  const [categoryStats, setCategoryStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [usingMockData, setUsingMockData] = useState(false);

  useEffect(() => {
    const fetchAllStatistics = async () => {
      try {
        setLoading(true);
        setError(null);
        setUsingMockData(false);
        
        // Convert userId to UUID format if it's a number
        const userIdParam = typeof userId === 'number' 
          ? `00000000-0000-0000-0000-${userId.toString().padStart(12, '0')}` 
          : userId;
        
        const [userData, overviewData, categoryData] = await Promise.all([
          statisticsApi.getUserStatistics(userIdParam),
          statisticsApi.getOverviewStatistics(),
          statisticsApi.getCategoryStatistics(userIdParam)
        ]);
        
        setUserStats(userData);
        setOverviewStats(overviewData);
        setCategoryStats(categoryData);
        
        // Check if we're using mock data by checking if the data matches our mock data
        if (userData && userData.statementPeriod === "2025-09-02 - 2025-10-19") {
          setUsingMockData(true);
        }
        
      } catch (err) {
        setError('Ошибка загрузки данных');
        console.error('Error fetching statistics:', err);
      } finally {
        setLoading(false);
      }
    };

    if (userId) {
      fetchAllStatistics();
    }
  }, [userId]);

  if (loading) {
    return (
      <div className="statistics-dashboard">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Загрузка статистики...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="statistics-dashboard">
        <div className="error-container">
          <p>❌ {error}</p>
          <button onClick={onClose} className="close-button">Закрыть</button>
        </div>
      </div>
    );
  }

  // Подготовка данных для графиков
  const categoryChartData = categoryStats?.categories?.map(cat => ({
    name: cat.categoryName,
    value: cat.totalAmount,
    percentage: cat.percentage,
    transactionCount: cat.transactionCount,
    averageAmount: cat.averageAmount
  })) || [];

  const COLORS = ['#2D9A86', '#EEFE6D', '#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#a8e6cf'];

  // Данные для сравнения пользователя с общей статистикой
  const comparisonData = [
    {
      metric: 'Доходы',
      user: userStats?.totalIncome || 0,
      system: overviewStats?.totalIncome || 0
    },
    {
      metric: 'Расходы',
      user: userStats?.totalExpenses || 0,
      system: overviewStats?.totalExpenses || 0
    },
    {
      metric: 'Чистый капитал',
      user: userStats?.netWorth || 0,
      system: overviewStats?.netWorth || 0
    }
  ];

  return (
    <div className="statistics-dashboard">
      <button onClick={onClose} className="close-button">
        ×
      </button>
      
      <div className="dashboard-header">
        <h2>📊 Финансовая статистика</h2>
        <p>Детальный анализ ваших финансовых данных</p>
        
        {usingMockData && (
          <div className="mock-data-notification">
            <div className="notification-icon">⚠️</div>
            <div className="notification-content">
              <strong>Демо режим:</strong> Отображаются тестовые данные. 
              Запустите backend сервер для получения реальных данных.
            </div>
          </div>
        )}
      </div>

      <div className="dashboard-grid">
        {/* Основные метрики пользователя */}
        <div className="metrics-section">
          <h3>👤 Ваши показатели</h3>
          <div className="metrics-grid">
            <div className="metric-card">
              <div className="metric-icon">
                <MdAttachMoney size={24} color="#2D9A86" />
              </div>
              <div className="metric-content">
                <h4>Чистый капитал</h4>
                <p className="metric-value">{userStats?.netWorth?.toLocaleString()} ₽</p>
              </div>
            </div>

            <div className="metric-card">
              <div className="metric-icon">
                <MdTrendingUp size={24} color="#2D9A86" />
              </div>
              <div className="metric-content">
                <h4>Доходы</h4>
                <p className="metric-value positive">{userStats?.totalIncome?.toLocaleString()} ₽</p>
              </div>
            </div>

            <div className="metric-card">
              <div className="metric-icon">
                <MdTrendingDown size={24} color="#ff6b6b" />
              </div>
              <div className="metric-content">
                <h4>Расходы</h4>
                <p className="metric-value negative">{userStats?.totalExpenses?.toLocaleString()} ₽</p>
              </div>
            </div>

            <div className="metric-card">
              <div className="metric-icon">
                <MdReceipt size={24} color="#EEFE6D" />
              </div>
              <div className="metric-content">
                <h4>Транзакции</h4>
                <p className="metric-value">{userStats?.transactionsCount}</p>
              </div>
            </div>

            <div className="metric-card">
              <div className="metric-icon">
                <MdDateRange size={24} color="#4ecdc4" />
              </div>
              <div className="metric-content">
                <h4>Период</h4>
                <p className="metric-value">{userStats?.statementPeriod}</p>
              </div>
            </div>
          </div>
        </div>

        {/* График расходов по категориям */}
        {categoryChartData.length > 0 && (
          <div className="chart-section">
            <h3>🥧 Расходы по категориям</h3>
            <div className="chart-container">
              <ResponsiveContainer width="100%" height={400} style={{ overflow: 'visible' }}>
                <PieChart margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
                  <Pie
                    data={categoryChartData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percentage }) => `${name} ${percentage.toFixed(1)}%`}
                    outerRadius={120}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {categoryChartData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip 
                    formatter={(value, name, props) => [
                      `${value.toLocaleString()} ₽`, 
                      'Сумма'
                    ]}
                    labelFormatter={(label, payload) => {
                      if (payload && payload[0]) {
                        return `${payload[0].payload.name} (${payload[0].payload.percentage.toFixed(1)}%)`;
                      }
                      return label;
                    }}
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {/* Столбчатая диаграмма по категориям */}
        {categoryChartData.length > 0 && (
          <div className="chart-section">
            <h3>📊 Суммы по категориям</h3>
            <div className="chart-container">
              <ResponsiveContainer width="100%" height={300} style={{ overflow: 'visible' }}>
                <BarChart data={categoryChartData} margin={{ top: 20, right: 30, left: 20, bottom: 60 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="name" 
                    angle={-45}
                    textAnchor="end"
                    height={100}
                    fontSize={12}
                  />
                  <YAxis />
                  <Tooltip 
                    formatter={(value) => [`${value.toLocaleString()} ₽`, 'Сумма']}
                    labelFormatter={(label) => `Категория: ${label}`}
                  />
                  <Bar dataKey="value" fill="#2D9A86" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {/* Сравнение с общей статистикой */}
        <div className="chart-section">
          <h3>📈 Сравнение с общими показателями</h3>
          <div className="chart-container">
            <ResponsiveContainer width="100%" height={300} style={{ overflow: 'visible' }}>
              <BarChart data={comparisonData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="metric" />
                <YAxis />
                <Tooltip 
                  formatter={(value) => [`${value.toLocaleString()} ₽`, '']}
                  labelFormatter={(label) => `Метрика: ${label}`}
                />
                <Legend />
                <Bar dataKey="user" fill="#2D9A86" name="Ваши показатели" />
                <Bar dataKey="system" fill="#4ecdc4" name="Общие показатели" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Детальная таблица по категориям */}
        {categoryStats?.categories && (
          <div className="table-section">
            <h3>📋 Детальная статистика по категориям</h3>
            <div className="table-container">
              <table className="categories-table">
                <thead>
                  <tr>
                    <th>Категория</th>
                    <th>Общая сумма</th>
                    <th>Количество транзакций</th>
                    <th>Средняя сумма</th>
                    <th>Процент</th>
                  </tr>
                </thead>
                <tbody>
                  {categoryStats.categories.map((category, index) => (
                    <tr key={index}>
                      <td>
                        <div className="category-cell">
                          <div 
                            className="category-color" 
                            style={{ backgroundColor: COLORS[index % COLORS.length] }}
                          ></div>
                          {category.categoryName}
                        </div>
                      </td>
                      <td>{category.totalAmount.toLocaleString()} ₽</td>
                      <td>{category.transactionCount}</td>
                      <td>{category.averageAmount.toLocaleString()} ₽</td>
                      <td>{category.percentage.toFixed(1)}%</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Общая статистика системы */}
        <div className="system-stats-section">
          <h3>🌐 Общая статистика системы</h3>
          <div className="system-stats-grid">
            <div className="system-stat-card">
              <h4>Общие транзакции</h4>
              <p>{overviewStats?.totalTransactions}</p>
            </div>
            <div className="system-stat-card">
              <h4>Общие доходы</h4>
              <p>{overviewStats?.totalIncome?.toLocaleString()} ₽</p>
            </div>
            <div className="system-stat-card">
              <h4>Общие расходы</h4>
              <p>{overviewStats?.totalExpenses?.toLocaleString()} ₽</p>
            </div>
            <div className="system-stat-card">
              <h4>Общий капитал</h4>
              <p>{overviewStats?.netWorth?.toLocaleString()} ₽</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StatisticsDashboard;
