import { useState } from 'react'
import ChatInterface from './components/ChatInterface'
import FileUpload from './components/FileUpload'
import BankStatementUpload from './components/BankStatementUpload'
import AnalyticsDashboard from './components/AnalyticsDashboard'
import RealtimeChat from './components/RealtimeChat'
import Header from './components/Header'
import './App.css'

function App() {
  const [analyticsData, setAnalyticsData] = useState(null)
  const [showFileUpload, setShowFileUpload] = useState(false)
  const [showStatementUpload, setShowStatementUpload] = useState(false)
  const [showAnalytics, setShowAnalytics] = useState(false)
  const [showRealtimeChat, setShowRealtimeChat] = useState(false)

  const handleAnalyticsData = (data) => {
    setAnalyticsData(data)
    setShowAnalytics(true)
    setShowFileUpload(false)
    setShowStatementUpload(false)
  }

  const handleFileUpload = () => {
    setShowFileUpload(true)
    setShowStatementUpload(false)
    setShowAnalytics(false)
  }

  const handleStatementUpload = () => {
    setShowStatementUpload(true)
    setShowFileUpload(false)
    setShowAnalytics(false)
  }

  const closeModals = () => {
    setShowFileUpload(false)
    setShowStatementUpload(false)
    setShowAnalytics(false)
    setShowRealtimeChat(false)
  }

  return (
    <div className="app">
      <Header />
      
      <div className="main-container">
        <div className="content">
          <ChatInterface 
            onShowAnalytics={() => setShowAnalytics(true)}
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
          
          {showRealtimeChat && (
            <RealtimeChat onClose={closeModals} />
          )}
        </div>
      </div>
    </div>
  )
}

export default App