import { useState } from 'react'
import ChatInterface from './components/ChatInterface'
import FileUpload from './components/FileUpload'
import BankStatementUpload from './components/BankStatementUpload'
import AnalyticsDashboard from './components/AnalyticsDashboard'
import StatisticsDashboard from './components/StatisticsDashboard'
import StatisticsDemo from './components/StatisticsDemo'
import './components/StatisticsDashboard.css'
import RealtimeChat from './components/RealtimeChat'
import Header from './components/Header'
import './App.css'

function App() {
  const [analyticsData, setAnalyticsData] = useState(null)
  const [showFileUpload, setShowFileUpload] = useState(false)
  const [showStatementUpload, setShowStatementUpload] = useState(false)
  const [showAnalytics, setShowAnalytics] = useState(false)
  const [showStatistics, setShowStatistics] = useState(false)
  const [showStatisticsDemo, setShowStatisticsDemo] = useState(false)
  const [showRealtimeChat, setShowRealtimeChat] = useState(false)
  const [currentUserId, setCurrentUserId] = useState(null)

  const handleAnalyticsData = (data) => {
    setAnalyticsData(data)
    setShowAnalytics(true)
    setShowFileUpload(false)
    setShowStatementUpload(false)
    setShowStatistics(false)
  }

  const handleShowStatistics = (userId) => {
    setCurrentUserId(userId)
    setShowStatistics(true)
    setShowFileUpload(false)
    setShowStatementUpload(false)
    setShowAnalytics(false)
    setShowStatisticsDemo(false)
  }

  const handleShowStatisticsDemo = () => {
    setShowStatisticsDemo(true)
    setShowFileUpload(false)
    setShowStatementUpload(false)
    setShowAnalytics(false)
    setShowStatistics(false)
  }

  const handleFileUpload = () => {
    setShowFileUpload(true)
    setShowStatementUpload(false)
    setShowAnalytics(false)
    setShowStatistics(false)
  }

  const handleStatementUpload = () => {
    setShowStatementUpload(true)
    setShowFileUpload(false)
    setShowAnalytics(false)
    setShowStatistics(false)
  }

  const closeModals = () => {
    setShowFileUpload(false)
    setShowStatementUpload(false)
    setShowAnalytics(false)
    setShowStatistics(false)
    setShowStatisticsDemo(false)
    setShowRealtimeChat(false)
  }

  return (
    <div className="app">
      <Header />
      
      <div className="main-container">
        <div className="content">
          <ChatInterface 
            onFileUpload={handleFileUpload}
            onStatementUpload={handleStatementUpload}
            onShowAnalytics={() => setShowAnalytics(true)}
            onShowStatistics={handleShowStatistics}
            onShowStatisticsDemo={handleShowStatisticsDemo}
            onShowRealtimeChat={() => setShowRealtimeChat(true)}
            analyticsData={analyticsData}
          />
          
          {showFileUpload && (
            <div className="modal-overlay" onClick={closeModals}>
              <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <FileUpload onClose={closeModals} />
              </div>
            </div>
          )}
          
          {showStatementUpload && (
            <div className="modal-overlay" onClick={closeModals}>
              <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <BankStatementUpload onAnalyticsData={handleAnalyticsData} onClose={closeModals} />
              </div>
            </div>
          )}
          
          {showAnalytics && analyticsData && (
            <div className="modal-overlay" onClick={closeModals}>
              <div className="modal-content analytics-modal" onClick={(e) => e.stopPropagation()}>
                <AnalyticsDashboard data={analyticsData} onClose={closeModals} />
              </div>
            </div>
          )}
          
          {showStatistics && currentUserId && (
            <div className="modal-overlay" onClick={closeModals}>
              <div className="modal-content statistics-modal" onClick={(e) => e.stopPropagation()}>
                <StatisticsDashboard userId={currentUserId} onClose={closeModals} />
              </div>
            </div>
          )}
          
          {showStatisticsDemo && (
            <div className="modal-overlay" onClick={closeModals}>
              <div className="modal-content statistics-demo-modal" onClick={(e) => e.stopPropagation()}>
                <StatisticsDemo onClose={closeModals} />
              </div>
            </div>
          )}
        </div>
      </div>
      
      {showRealtimeChat && (
        <RealtimeChat onClose={closeModals} />
      )}
    </div>
  )
}

export default App