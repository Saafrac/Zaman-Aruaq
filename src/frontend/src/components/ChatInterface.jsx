import { useState, useRef, useEffect } from 'react'
import { MdSend, MdSmartToy, MdPerson, MdMic, MdMicOff } from 'react-icons/md'
import AttachmentMenu from './AttachmentMenu'

const ChatInterface = ({ onFileUpload, onStatementUpload, onShowAnalytics, analyticsData }) => {
  const [messages, setMessages] = useState([
    {
      id: 1,
      text: "Привет! Я ваш персональный финансовый помощник. Чем могу помочь?",
      sender: 'bot',
      timestamp: new Date()
    }
  ])
  const [inputMessage, setInputMessage] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [isRecording, setIsRecording] = useState(false)
  const [mediaRecorder, setMediaRecorder] = useState(null)
  const messagesEndRef = useRef(null)

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const handleSendMessage = async () => {
    if (!inputMessage.trim()) return

    const userMessage = {
      id: Date.now(),
      text: inputMessage,
      sender: 'user',
      timestamp: new Date()
    }

    setMessages(prev => [...prev, userMessage])
    setInputMessage('')
    setIsLoading(true)

    try {
      // Здесь будет запрос к бэкенду
      const response = await fetch('/api/chat', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ message: inputMessage })
      })

      const data = await response.json()
      
      const botMessage = {
        id: Date.now() + 1,
        text: data.response || "Извините, произошла ошибка. Попробуйте еще раз.",
        sender: 'bot',
        timestamp: new Date()
      }

      setMessages(prev => [...prev, botMessage])
    } catch (error) {
      const errorMessage = {
        id: Date.now() + 1,
        text: "Извините, произошла ошибка соединения. Попробуйте еще раз.",
        sender: 'bot',
        timestamp: new Date()
      }
      setMessages(prev => [...prev, errorMessage])
    } finally {
      setIsLoading(false)
    }
  }

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSendMessage()
    }
  }

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      const recorder = new MediaRecorder(stream)
      const chunks = []

      recorder.ondataavailable = (e) => {
        chunks.push(e.data)
      }

      recorder.onstop = async () => {
        const blob = new Blob(chunks, { type: 'audio/wav' })
        // Здесь можно отправить аудио на сервер для обработки
        console.log('Audio recorded:', blob)
        // Пока просто добавляем сообщение о записи
        const audioMessage = {
          id: Date.now(),
          text: "🎤 Голосовое сообщение записано",
          sender: 'user',
          timestamp: new Date()
        }
        setMessages(prev => [...prev, audioMessage])
      }

      recorder.start()
      setMediaRecorder(recorder)
      setIsRecording(true)
    } catch (error) {
      console.error('Error starting recording:', error)
    }
  }

  const stopRecording = () => {
    if (mediaRecorder && isRecording) {
      mediaRecorder.stop()
      mediaRecorder.stream.getTracks().forEach(track => track.stop())
      setIsRecording(false)
      setMediaRecorder(null)
    }
  }

  return (
    <div className="chat-interface">
      <div className="chat-header">
        <h2>💬 Чат с AI помощником</h2>
        <p>Задавайте вопросы о ваших финансах</p>
        
        {analyticsData && (
          <div className="quick-actions">
            <button 
              onClick={onShowAnalytics}
              className="action-button analytics-button"
              title="Показать аналитику"
            >
              📊
              <span>Аналитика</span>
            </button>
          </div>
        )}
      </div>

      <div className="chat-messages">
        {messages.map((message) => (
          <div key={message.id} className={`message ${message.sender}`}>
            <div className="message-avatar">
              {message.sender === 'bot' ? <MdSmartToy size={20} /> : <MdPerson size={20} />}
            </div>
            <div className="message-content">
              <div className="message-text">{message.text}</div>
              <div className="message-time">
                {message.timestamp.toLocaleTimeString()}
              </div>
            </div>
          </div>
        ))}
        
        {isLoading && (
          <div className="message bot">
            <div className="message-avatar">
              <MdSmartToy size={20} />
            </div>
            <div className="message-content">
              <div className="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        )}
        
        <div ref={messagesEndRef} />
      </div>

      <div className="chat-input">
        <div className="input-container">
          <AttachmentMenu 
            onFileUpload={onFileUpload}
            onStatementUpload={onStatementUpload}
          />
          
          <textarea
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Введите ваш вопрос..."
            rows="1"
            disabled={isLoading}
          />
          
          <div className="input-actions">
            <button 
              onClick={isRecording ? stopRecording : startRecording}
              className={`voice-button ${isRecording ? 'recording' : ''}`}
              title={isRecording ? "Остановить запись" : "Записать голос"}
            >
              {isRecording ? <MdMicOff size={20} /> : <MdMic size={20} />}
            </button>
            
            <button 
              onClick={handleSendMessage}
              disabled={!inputMessage.trim() || isLoading}
              className="send-button"
            >
              <MdSend size={20} />
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ChatInterface
