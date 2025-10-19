import { useState } from 'react';
import StatisticsDashboard from './StatisticsDashboard';
import './StatisticsDashboard.css';

const StatisticsDemo = ({ onClose }) => {
  const [selectedUserId, setSelectedUserId] = useState(1);
  const [showDashboard, setShowDashboard] = useState(false);

  const handleShowStatistics = () => {
    setShowDashboard(true);
  };

  const handleCloseDashboard = () => {
    setShowDashboard(false);
  };

  if (showDashboard) {
    return (
      <StatisticsDashboard 
        userId={selectedUserId} 
        onClose={handleCloseDashboard} 
      />
    );
  }

  return (
    <div className="statistics-demo">
      <button onClick={onClose} className="close-button">
        ×
      </button>
      
      <div className="demo-header">
        <h2>📊 Демо статистики</h2>
        <p>Выберите пользователя для просмотра статистики</p>
      </div>

      <div className="demo-content">
        <div className="user-selection">
          <label htmlFor="userId">ID пользователя:</label>
          <select 
            id="userId"
            value={selectedUserId} 
            onChange={(e) => setSelectedUserId(parseInt(e.target.value))}
            className="user-select"
          >
            <option value={1}>Пользователь 1</option>
            <option value={2}>Пользователь 2</option>
            <option value={3}>Пользователь 3</option>
          </select>
        </div>

        <div className="demo-info">
          <h3>Доступные эндпойнты:</h3>
          <ul>
            <li><code>GET /api/statistics/user/{selectedUserId}</code> - Статистика пользователя</li>
            <li><code>GET /api/statistics/overview</code> - Общая статистика системы</li>
            <li><code>GET /api/statistics/categories/user/{selectedUserId}</code> - Статистика по категориям</li>
          </ul>
        </div>

        <button 
          onClick={handleShowStatistics}
          className="demo-button"
        >
          📈 Показать статистику
        </button>
      </div>
    </div>
  );
};

export default StatisticsDemo;
