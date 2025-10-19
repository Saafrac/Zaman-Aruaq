import { useState, useEffect, useRef, useMemo } from 'react';
import { 
  PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, 
  ResponsiveContainer, LineChart, Line, Area, AreaChart, Legend 
} from 'recharts';
import { 
  MdTrendingUp, MdTrendingDown, MdAttachMoney, MdDateRange, 
  MdAccountBalance, MdCategory, MdReceipt, MdChat, MdClose, MdSend, MdSmartToy, MdPerson 
} from 'react-icons/md';
import { statisticsApi } from '../services/statisticsApi';

const generateSessionId = () => {
  return `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}

const formatMessageText = (text) => {
  if (!text) return text
  
  let formattedText = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
  formattedText = formattedText.replace(/\\n/g, '<br/>')
  formattedText = formattedText.replace(/\s(\d+)\.\s/g, '<br/>$1. ')

  return formattedText
}

const StatisticsDashboard = ({ userId, onClose = null }) => {
  const [userStats, setUserStats] = useState(null);
  const [overviewStats, setOverviewStats] = useState(null);
  const [categoryStats, setCategoryStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [usingMockData, setUsingMockData] = useState(false);
  
  // Состояние для чата
  const [showChat, setShowChat] = useState(false);
  const [chatMessages, setChatMessages] = useState([]);
  const [chatInput, setChatInput] = useState('');
  const [chatLoading, setChatLoading] = useState(false);
  const chatMessagesEndRef = useRef(null);
  const sessionId = useMemo(() => generateSessionId(), []);

  useEffect(() => {
    const fetchAllStatistics = async () => {
      try {
        setLoading(true);
        setError(null);
        setUsingMockData(false);
        
        // Hardcoded userId
        const userIdParam = '5a27be9d-beef-4112-9466-277312593d62';
        
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

  useEffect(() => {
    if (showChat) {
      scrollChatToBottom();
    }
  }, [chatMessages, showChat]);

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
          {onClose && <button onClick={onClose} className="close-button">Закрыть</button>}
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

  const handleOpenChat = () => {
    setShowChat(!showChat);
    if (!showChat && chatMessages.length === 0) {
      setChatMessages([{
        id: 1,
        text: "Привет! Я помогу вам разобраться со статистикой. Задавайте вопросы!",
        sender: 'bot',
        timestamp: new Date()
      }]);
    }
  };

  const scrollChatToBottom = () => {
    chatMessagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const handleSendChatMessage = async () => {
    if (!chatInput.trim() || chatLoading) return;

    const userMessage = {
      id: Date.now(),
      text: chatInput,
      sender: 'user',
      timestamp: new Date()
    };

    setChatMessages(prev => [...prev, userMessage]);
    const messageText = chatInput;
    setChatInput('');
    setChatLoading(true);

    try {
      // Формируем контекст со всеми данными статистики
      const statisticsContext = `
Статистика пользователя:
- Период: ${userStats?.statementPeriod || 'N/A'}
- Доходы: ${userStats?.totalIncome?.toLocaleString() || 0} ₸
- Расходы: ${userStats?.totalExpenses?.toLocaleString() || 0} ₸
- Чистый капитал: ${userStats?.netWorth?.toLocaleString() || 0} ₸
- Количество транзакций: ${userStats?.transactionsCount || 0}

Общая статистика системы:
- Общие транзакции: ${overviewStats?.totalTransactions || 0}
- Общие доходы: ${overviewStats?.totalIncome?.toLocaleString() || 0} ₸
- Общие расходы: ${overviewStats?.totalExpenses?.toLocaleString() || 0} ₸
- Общий капитал: ${overviewStats?.netWorth?.toLocaleString() || 0} ₸

Статистика по категориям:
${categoryStats?.categories?.map(cat => 
  `- ${cat.categoryName}: ${cat.totalAmount.toLocaleString()} ₸ (${cat.percentage.toFixed(1)}%, транзакций: ${cat.transactionCount})`
).join('\n') || 'Нет данных'}

Вопрос пользователя: ${messageText}
`;

      const formData = new FormData();
      formData.append('chatInput', statisticsContext);
      formData.append('sessionId', sessionId);

      const response = await fetch('https://saafrac.app.n8n.cloud/webhook/bank', {
        method: 'POST',
        body: formData
      });

      const data = await response.json();

      let responseText = "Сообщение получено";
      if (Array.isArray(data) && data.length > 0 && data[0].output) {
        responseText = data[0].output;
      } else if (data.output) {
        responseText = data.output;
      } else if (data.response) {
        responseText = data.response;
      } else if (data.message) {
        responseText = data.message;
      }

      const botMessage = {
        id: Date.now() + 1,
        text: responseText,
        sender: 'bot',
        timestamp: new Date()
      };

      setChatMessages(prev => [...prev, botMessage]);
    } catch (error) {
      const errorMessage = {
        id: Date.now() + 1,
        text: "Извините, произошла ошибка соединения. Попробуйте еще раз.",
        sender: 'bot',
        timestamp: new Date()
      };
      setChatMessages(prev => [...prev, errorMessage]);
    } finally {
      setChatLoading(false);
    }
  };

  const handleChatKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendChatMessage();
    }
  };

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
      {onClose && (
        <button onClick={onClose} className="close-button">
          ×
        </button>
      )}

      {!showChat && (
        <button onClick={handleOpenChat} className="floating-chat-button">
          <MdChat size={24} />
        </button>
      )}

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
                <p className="metric-value">{userStats?.netWorth?.toLocaleString()} ₸</p>
              </div>
            </div>

            <div className="metric-card">
              <div className="metric-icon">
                <MdTrendingUp size={24} color="#2D9A86" />
              </div>
              <div className="metric-content">
                <h4>Доходы</h4>
                <p className="metric-value positive">{userStats?.totalIncome?.toLocaleString()} ₸</p>
              </div>
            </div>

            <div className="metric-card">
              <div className="metric-icon">
                <MdTrendingDown size={24} color="#ff6b6b" />
              </div>
              <div className="metric-content">
                <h4>Расходы</h4>
                <p className="metric-value negative">{userStats?.totalExpenses?.toLocaleString()} ₸</p>
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
                      `${value.toLocaleString()} ₸`, 
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
                    formatter={(value) => [`${value.toLocaleString()} ₸`, 'Сумма']}
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
                  formatter={(value) => [`${value.toLocaleString()} ₸`, '']}
                  labelFormatter={(label) => `Метрика: ${label}`}
                />
                <Legend />
                <Bar dataKey="user" fill="#2D9A86" name="Ваши показатели" />
                <Bar dataKey="system" fill="#4ecdc4" name="Общие показатели" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* График средних сумм и количества транзакций */}
        {categoryChartData.length > 0 && (
          <div className="chart-section">
            <h3>📉 Средний чек и активность по категориям</h3>
            <div className="chart-container">
              <ResponsiveContainer width="100%" height={300} style={{ overflow: 'visible' }}>
                <AreaChart data={categoryChartData} margin={{ top: 20, right: 30, left: 20, bottom: 60 }}>
                  <defs>
                    <linearGradient id="colorAvg" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#2D9A86" stopOpacity={0.8}/>
                      <stop offset="95%" stopColor="#2D9A86" stopOpacity={0}/>
                    </linearGradient>
                    <linearGradient id="colorCount" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#EEFE6D" stopOpacity={0.8}/>
                      <stop offset="95%" stopColor="#EEFE6D" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="name" 
                    angle={-45}
                    textAnchor="end"
                    height={100}
                    fontSize={12}
                  />
                  <YAxis yAxisId="left" />
                  <YAxis yAxisId="right" orientation="right" />
                  <Tooltip 
                    formatter={(value, name) => {
                      if (name === "Средний чек") return [`${value.toLocaleString()} ₸`, name];
                      return [value, name];
                    }}
                    labelFormatter={(label) => `Категория: ${label}`}
                  />
                  <Legend />
                  <Area 
                    yAxisId="left"
                    type="monotone" 
                    dataKey="averageAmount" 
                    stroke="#2D9A86" 
                    fillOpacity={1} 
                    fill="url(#colorAvg)" 
                    name="Средний чек"
                  />
                  <Area 
                    yAxisId="right"
                    type="monotone" 
                    dataKey="transactionCount" 
                    stroke="#EEFE6D" 
                    fillOpacity={1} 
                    fill="url(#colorCount)" 
                    name="Количество транзакций"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

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
                      <td>{category.totalAmount.toLocaleString()} ₸</td>
                      <td>{category.transactionCount}</td>
                      <td>{category.averageAmount.toLocaleString()} ₸</td>
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
              <p>{overviewStats?.totalIncome?.toLocaleString()} ₸</p>
            </div>
            <div className="system-stat-card">
              <h4>Общие расходы</h4>
              <p>{overviewStats?.totalExpenses?.toLocaleString()} ₸</p>
            </div>
            <div className="system-stat-card">
              <h4>Общий капитал</h4>
              <p>{overviewStats?.netWorth?.toLocaleString()} ₸</p>
            </div>
          </div>
        </div>
      </div>

      {/* Чат для обсуждения статистики */}
      {showChat && (
        <div className="statistics-chat-container">
          <div className="statistics-chat-header">
            <h3>💬 Обсудить статистику</h3>
            <button onClick={() => setShowChat(false)} className="statistics-chat-close">
              <MdClose />
            </button>
          </div>

          <div className="statistics-chat-messages">
            {chatMessages.map((message) => (
              <div key={message.id} className={`statistics-chat-message ${message.sender}`}>
                <div className="statistics-chat-avatar">
                  {message.sender === 'bot' ? <MdSmartToy /> : <MdPerson />}
                </div>
                <div 
                  className="statistics-chat-bubble"
                  dangerouslySetInnerHTML={{ __html: formatMessageText(message.text) }}
                />
              </div>
            ))}

            {chatLoading && (
              <div className="statistics-chat-message bot">
                <div className="statistics-chat-avatar">
                  <MdSmartToy />
                </div>
                <div className="statistics-chat-bubble">
                  <div className="statistics-chat-typing">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                </div>
              </div>
            )}

            <div ref={chatMessagesEndRef} />
          </div>

          <div className="statistics-chat-input-container">
            <textarea
              value={chatInput}
              onChange={(e) => setChatInput(e.target.value)}
              onKeyPress={handleChatKeyPress}
              placeholder="Задайте вопрос о статистике..."
              rows="2"
              disabled={chatLoading}
              className="statistics-chat-input"
            />
            <button
              onClick={handleSendChatMessage}
              disabled={!chatInput.trim() || chatLoading}
              className="statistics-chat-send"
            >
              <MdSend size={18} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default StatisticsDashboard;
