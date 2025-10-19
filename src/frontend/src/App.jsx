import { useState, useEffect, useRef } from 'react'
import { MdPhone } from 'react-icons/md'
import ChatInterface from './components/ChatInterface'
import FileUpload from './components/FileUpload'
import BankStatementUpload from './components/BankStatementUpload'
import AnalyticsDashboard from './components/AnalyticsDashboard'
import StatisticsDashboard from './components/StatisticsDashboard'
import GoalCreator from './components/GoalCreator'
import './components/StatisticsDashboard.css'
import RealtimeChat from './components/RealtimeChat'
import Header from './components/Header'
import './App.css'

function App() {
  const [analyticsData, setAnalyticsData] = useState(null)
  const [showFileUpload, setShowFileUpload] = useState(false)
  const [showStatementUpload, setShowStatementUpload] = useState(false)
  const [showAnalytics, setShowAnalytics] = useState(false)
  const [showRealtimeChat, setShowRealtimeChat] = useState(false)
  const [activeTab, setActiveTab] = useState('chat') // 'chat', 'goals', 'statistics'
  const [currentUserId] = useState(1)
  const [showFloatingPhone, setShowFloatingPhone] = useState(false)
  const phoneNumber = '+7 (777) 123-45-67'
  const floatingPopupRef = useRef(null)
  const floatingButtonRef = useRef(null)

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (showFloatingPhone && 
          floatingPopupRef.current && 
          floatingButtonRef.current &&
          !floatingPopupRef.current.contains(event.target) &&
          !floatingButtonRef.current.contains(event.target)) {
        setShowFloatingPhone(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
    }
  }, [showFloatingPhone])

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
        <div className="tabs-navigation">
          <button 
            className={`tab-button ${activeTab === 'chat' ? 'active' : ''}`}
            onClick={() => setActiveTab('chat')}
          >
            💬 Чат
          </button>
          <button 
            className={`tab-button ${activeTab === 'goals' ? 'active' : ''}`}
            onClick={() => setActiveTab('goals')}
          >
            🎯 Цели
          </button>
          <button 
            className={`tab-button ${activeTab === 'statistics' ? 'active' : ''}`}
            onClick={() => setActiveTab('statistics')}
          >
            📈 Статистика
          </button>
        </div>

        <div className="content">
          {activeTab === 'chat' && (
            <ChatInterface 
              onFileUpload={handleFileUpload}
              onStatementUpload={handleStatementUpload}
              onShowAnalytics={() => setShowAnalytics(true)}
              onShowRealtimeChat={() => setShowRealtimeChat(true)}
              analyticsData={analyticsData}
            />
          )}

          {activeTab === 'goals' && (
            <GoalCreator />
          )}

          {activeTab === 'statistics' && (
            <StatisticsDashboard userId={currentUserId} />
          )}
          
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
          
        </div>
      </div>
      
      {showRealtimeChat && (
        <RealtimeChat onClose={closeModals} />
      )}

      {/* Плавающая кнопка телефона */}
      <button 
        ref={floatingButtonRef}
        className="floating-phone-button"
        onClick={() => setShowFloatingPhone(!showFloatingPhone)}
      >
        <MdPhone size={28} />
      </button>

      {/* Всплывающее окно с номером */}
      {showFloatingPhone && (
        <div ref={floatingPopupRef} className="floating-phone-popup">
          <div className="floating-phone-popup-header">
            <MdPhone size={24} />
            <span>AI Ассистент</span>
          </div>
          <p className="floating-phone-description">
            Позвоните нашему AI ассистенту для помощи по телефону
          </p>
          <a href={`tel:${phoneNumber.replace(/\D/g, '')}`} className="floating-phone-number">
            {phoneNumber}
          </a>
        </div>
      )}
    </div>
  )
}

export default App