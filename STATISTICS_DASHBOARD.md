# 📊 Statistics Dashboard

## Overview
The Statistics Dashboard is a comprehensive visualization system that displays financial data from three backend endpoints. It provides interactive charts, detailed metrics, and comparative analysis for financial data.

## Features

### 🎯 Core Functionality
- **User Statistics**: Personal financial metrics for individual users
- **System Overview**: Aggregate statistics across all users
- **Category Breakdown**: Detailed analysis of spending by categories
- **Interactive Charts**: Multiple chart types for different data visualization needs
- **Responsive Design**: Works on desktop, tablet, and mobile devices

### 📈 Visualizations
1. **Pie Charts**: Category-wise spending distribution
2. **Bar Charts**: Comparative analysis and category amounts
3. **Line Charts**: Trend analysis over time
4. **Tables**: Detailed tabular data with sorting and filtering
5. **Metrics Cards**: Key performance indicators

## API Endpoints

### 1. User Statistics
```
GET /api/statistics/user/{userId}
```
**Response:**
```json
{
    "statementPeriod": "2025-09-02 - 2025-10-19",
    "totalIncome": 98409.2,
    "netWorth": 26816.2,
    "totalExpenses": 71593.0,
    "message": "Выписка успешно обработана",
    "transactionsCount": 553
}
```

### 2. System Overview
```
GET /api/statistics/overview
```
**Response:**
```json
{
    "totalTransactions": 553,
    "totalIncome": 98409.2,
    "netWorth": 26816.2,
    "totalExpenses": 71593.0,
    "message": "Общая статистика системы"
}
```

### 3. Category Statistics
```
GET /api/statistics/categories/user/{userId}
```
**Response:**
```json
{
    "totalAmount": 170002.2,
    "totalTransactions": 553,
    "categories": [
        {
            "totalAmount": 157880.2,
            "averageAmount": 301.87,
            "percentage": 92.87,
            "transactionCount": 523,
            "category": "OTHER_EXPENSE",
            "categoryName": "Прочие расходы"
        }
    ],
    "message": "Статистика по категориям успешно получена"
}
```

## Components

### StatisticsDashboard.jsx
Main dashboard component that fetches and displays all statistics data.

**Props:**
- `userId` (number): ID of the user to display statistics for
- `onClose` (function): Callback to close the dashboard

**Features:**
- Fetches data from all three endpoints
- Displays loading states and error handling
- Responsive grid layout
- Interactive charts with tooltips

### StatisticsDemo.jsx
Demo component for testing the dashboard with different user IDs.

**Props:**
- `onClose` (function): Callback to close the demo

**Features:**
- User ID selection dropdown
- API endpoint information display
- Easy testing interface

### statisticsApi.js
API service for making requests to the backend endpoints.

**Methods:**
- `getUserStatistics(userId)`: Fetch user-specific statistics
- `getOverviewStatistics()`: Fetch system-wide statistics
- `getCategoryStatistics(userId)`: Fetch category breakdown for user

## Usage

### Basic Usage
```jsx
import StatisticsDashboard from './components/StatisticsDashboard';

function App() {
  const [showStats, setShowStats] = useState(false);
  const [userId, setUserId] = useState(1);

  return (
    <div>
      {showStats && (
        <StatisticsDashboard 
          userId={userId} 
          onClose={() => setShowStats(false)} 
        />
      )}
    </div>
  );
}
```

### Demo Usage
```jsx
import StatisticsDemo from './components/StatisticsDemo';

function App() {
  const [showDemo, setShowDemo] = useState(false);

  return (
    <div>
      {showDemo && (
        <StatisticsDemo onClose={() => setShowDemo(false)} />
      )}
    </div>
  );
}
```

## Styling

### CSS Classes
- `.statistics-dashboard`: Main dashboard container
- `.dashboard-grid`: Grid layout for dashboard sections
- `.metrics-section`: User metrics display
- `.chart-section`: Chart containers
- `.table-section`: Data tables
- `.system-stats-section`: System-wide statistics

### Color Scheme
- Primary: `#2D9A86` (Green)
- Accent: `#EEFE6D` (Yellow)
- Error: `#ff6b6b` (Red)
- Success: `#28a745` (Green)
- Warning: `#ffc107` (Orange)

## Chart Types

### 1. Pie Chart (Categories)
- Shows spending distribution by category
- Interactive tooltips with percentages
- Color-coded segments

### 2. Bar Chart (Amounts)
- Displays category amounts
- Rotated labels for readability
- Responsive design

### 3. Comparison Chart
- User vs system statistics
- Side-by-side comparison
- Legend for data identification

### 4. Data Table
- Detailed category information
- Sortable columns
- Color-coded category indicators

## Responsive Design

### Breakpoints
- **Desktop**: Full grid layout with all sections
- **Tablet**: Adjusted grid with stacked sections
- **Mobile**: Single column layout with optimized spacing

### Mobile Optimizations
- Touch-friendly buttons
- Optimized chart sizes
- Readable text sizes
- Simplified navigation

## Error Handling

### Loading States
- Spinner animation during data fetch
- Loading messages for user feedback
- Graceful fallbacks for missing data

### Error States
- Network error handling
- API error messages
- User-friendly error displays
- Retry mechanisms

## Performance

### Optimizations
- Parallel API calls using Promise.all
- Lazy loading of chart components
- Memoized calculations
- Efficient re-renders

### Data Management
- Cached API responses
- Optimized chart rendering
- Minimal DOM updates
- Efficient state management

## Browser Support

### Supported Browsers
- Chrome 80+
- Firefox 75+
- Safari 13+
- Edge 80+

### Features Used
- CSS Grid
- Flexbox
- CSS Custom Properties
- ES6+ JavaScript
- React Hooks

## Development

### Setup
1. Install dependencies: `npm install`
2. Start development server: `npm run dev`
3. Backend should be running on `localhost:8080`

### Testing
1. Use the Demo component to test with different user IDs
2. Check browser console for any errors
3. Test responsive design on different screen sizes

### Customization
- Modify colors in CSS custom properties
- Add new chart types in StatisticsDashboard.jsx
- Extend API service for additional endpoints
- Customize responsive breakpoints

## Future Enhancements

### Planned Features
- Export functionality (PDF, Excel)
- Date range filtering
- Advanced filtering options
- Real-time data updates
- Custom chart configurations
- Data drill-down capabilities

### Technical Improvements
- WebSocket integration for real-time updates
- Advanced caching strategies
- Performance monitoring
- Accessibility improvements
- Internationalization support
