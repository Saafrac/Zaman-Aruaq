import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts'
import { MdTrendingUp, MdTrendingDown, MdAttachMoney, MdDateRange } from 'react-icons/md'

const AnalyticsDashboard = ({ data, onClose }) => {
  if (!data) return null

  // Подготовка данных для графиков
  const categoryData = data.categoryBreakdown?.map(item => ({
    name: item.category,
    value: item.amount,
    color: item.color || '#2D9A86'
  })) || []

  const monthlyData = data.monthlyTrends?.map(item => ({
    month: item.month,
    income: item.income,
    expenses: item.expenses,
    balance: item.balance
  })) || []

  const topTransactions = data.topTransactions || []

  const COLORS = ['#2D9A86', '#EEFE6D', '#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57']

  return (
      <div className="analytics-dashboard">
        <button onClick={onClose} className="close-button">
          ×
        </button>
        <div className="dashboard-header">
          <h2>📊 Финансовая аналитика</h2>
          <p>Детальный анализ ваших финансовых данных</p>
        </div>

        <div className="dashboard-grid">
          {/* Основные метрики */}
          <div className="metrics-section">
            <h3>📈 Ключевые показатели</h3>
            <div className="metrics-grid">
              <div className="metric-card">
                <div className="metric-icon">
                  <MdAttachMoney size={24} color="#2D9A86" />
                </div>
                <div className="metric-content">
                  <h4>Общий баланс</h4>
                  <p className="metric-value">{data.totalBalance?.toLocaleString()} ₽</p>
                </div>
              </div>

              <div className="metric-card">
                <div className="metric-icon">
                  <MdTrendingUp size={24} color="#2D9A86" />
                </div>
                <div className="metric-content">
                  <h4>Доходы</h4>
                  <p className="metric-value positive">{data.totalIncome?.toLocaleString()} ₽</p>
                </div>
              </div>

              <div className="metric-card">
                <div className="metric-icon">
                  <TrendingDown size={24} color="#ff6b6b" />
                </div>
                <div className="metric-content">
                  <h4>Расходы</h4>
                  <p className="metric-value negative">{data.totalExpenses?.toLocaleString()} ₽</p>
                </div>
              </div>

              <div className="metric-card">
                <div className="metric-icon">
                  <Calendar size={24} color="#EEFE6D" />
                </div>
                <div className="metric-content">
                  <h4>Период</h4>
                  <p className="metric-value">{data.period}</p>
                </div>
              </div>
            </div>
          </div>

          {/* График по категориям */}
          {categoryData.length > 0 && (
              <div className="chart-section">
                <h3>🥧 Расходы по категориям</h3>
                <div className="chart-container">
                  <ResponsiveContainer width="100%" height={300}>
                    <PieChart>
                      <Pie
                          data={categoryData}
                          cx="50%"
                          cy="50%"
                          labelLine={false}
                          label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                          outerRadius={80}
                          fill="#8884d8"
                          dataKey="value"
                      >
                        {categoryData.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                      </Pie>
                      <Tooltip formatter={(value) => [`${value.toLocaleString()} ₽`, 'Сумма']} />
                    </PieChart>
                  </ResponsiveContainer>
                </div>
              </div>
          )}

          {/* Тренд по месяцам */}
          {monthlyData.length > 0 && (
              <div className="chart-section">
                <h3>📈 Динамика доходов и расходов</h3>
                <div className="chart-container">
                  <ResponsiveContainer width="100%" height={300}>
                    <LineChart data={monthlyData}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="month" />
                      <YAxis />
                      <Tooltip formatter={(value) => [`${value.toLocaleString()} ₽`, '']} />
                      <Line
                          type="monotone"
                          dataKey="income"
                          stroke="#2D9A86"
                          strokeWidth={3}
                          name="Доходы"
                      />
                      <Line
                          type="monotone"
                          dataKey="expenses"
                          stroke="#ff6b6b"
                          strokeWidth={3}
                          name="Расходы"
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>
              </div>
          )}

          {/* Топ транзакций */}
          {topTransactions.length > 0 && (
              <div className="transactions-section">
                <h3>💳 Крупные транзакции</h3>
                <div className="transactions-list">
                  {topTransactions.map((transaction, index) => (
                      <div key={index} className="transaction-item">
                        <div className="transaction-info">
                          <div className="transaction-category">
                            {transaction.category}
                          </div>
                          <div className="transaction-description">
                            {transaction.description}
                          </div>
                          <div className="transaction-date">
                            {transaction.date}
                          </div>
                        </div>
                        <div className={`transaction-amount ${transaction.type}`}>
                          {transaction.type === 'expense' ? '-' : '+'}
                          {transaction.amount.toLocaleString()} ₽
                        </div>
                      </div>
                  ))}
                </div>
              </div>
          )}

          {/* Инсайты и рекомендации */}
          <div className="insights-section">
            <h3>💡 Финансовые инсайты</h3>
            <div className="insights-grid">
              {data.insights?.map((insight, index) => (
                  <div key={index} className="insight-card">
                    <div className="insight-icon">
                      {insight.type === 'positive' ? '✅' :
                          insight.type === 'warning' ? '⚠️' : '💡'}
                    </div>
                    <div className="insight-content">
                      <h4>{insight.title}</h4>
                      <p>{insight.description}</p>
                    </div>
                  </div>
              ))}
            </div>
          </div>

          <div className="recommendations-section">
            <h3>🎯 Рекомендации</h3>
            <div className="recommendations-grid">
              {data.recommendations?.map((rec, index) => (
                  <div key={index} className="recommendation-card">
                    <div className="rec-icon">💡</div>
                    <div className="rec-content">
                      <h4>{rec.title}</h4>
                      <p>{rec.description}</p>
                      {rec.impact && (
                          <div className="rec-impact">
                            Потенциальная экономия: {rec.impact}
                          </div>
                      )}
                    </div>
                  </div>
              ))}
            </div>
          </div>
        </div>
      </div>
  )
}

export default AnalyticsDashboard