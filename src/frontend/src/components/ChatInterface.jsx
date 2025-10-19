import { useState, useRef, useEffect } from 'react'
import { MdSend, MdSmartToy, MdPerson, MdMic, MdMicOff, MdClose, MdInsertDriveFile, MdPictureAsPdf, MdDescription } from 'react-icons/md'
import AttachmentMenu from './AttachmentMenu'

// Генерация уникального sessionId
const generateSessionId = () => {
  return `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}

// Функция для форматирования текста
const formatMessageText = (text) => {
  if (!text) return text

  // Заменяем **текст** на <strong>текст</strong>
  let formattedText = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
  
  // Заменяем \n на <br/>
  formattedText = formattedText.replace(/\\n/g, '<br/>')
  
  return formattedText
}

// Функция для получения иконки файла по типу
const getFileIcon = (fileName, fileType) => {
  if (fileType?.startsWith('image/')) {
    return null // Для изображений показываем превью
  }
  if (fileType === 'application/pdf' || fileName?.endsWith('.pdf')) {
    return <MdPictureAsPdf size={32} color="#e74c3c" />
  }
  if (fileType?.includes('word') || fileName?.endsWith('.doc') || fileName?.endsWith('.docx')) {
    return <MdDescription size={32} color="#2980b9" />
  }
  if (fileType?.includes('text') || fileName?.endsWith('.txt')) {
    return <MdDescription size={32} color="#27ae60" />
  }
  if (fileType?.includes('spreadsheet') || fileName?.endsWith('.xlsx') || fileName?.endsWith('.xls')) {
    return <MdDescription size={32} color="#16a085" />
  }
  return <MdInsertDriveFile size={32} color="#7f8c8d" />
}

const ChatInterface = ({ onShowAnalytics, onShowRealtimeChat, analyticsData }) => {
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
  const [isListening, setIsListening] = useState(false)
  const [recognition, setRecognition] = useState(null)
  const [sessionId] = useState(generateSessionId())
  const [attachedFile, setAttachedFile] = useState(null)
  const [imagePreview, setImagePreview] = useState(null)
  const messagesEndRef = useRef(null)
  const fileInputRef = useRef(null)

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const handleSendMessage = async () => {
    if (!inputMessage.trim() && !attachedFile) return

    const userMessage = {
      id: Date.now(),
      text: inputMessage,
      sender: 'user',
      timestamp: new Date(),
      file: attachedFile ? {
        name: attachedFile.name,
        type: attachedFile.type,
        size: attachedFile.size
      } : null,
      imagePreview: imagePreview
    }

    setMessages(prev => [...prev, userMessage])
    const messageText = inputMessage
    const fileToSend = attachedFile
    setInputMessage('')
    setAttachedFile(null)
    setImagePreview(null)
    setIsLoading(true)

    try {
      // Отправка данных на n8n webhook
      const formData = new FormData()
      formData.append('chatInput', messageText)
      formData.append('sessionId', sessionId)
      
      // Добавляем файл если есть
      if (fileToSend) {
        formData.append('data', fileToSend, fileToSend.name)
        console.log('Отправка файла:', fileToSend.name, 'тип:', fileToSend.type, 'размер:', fileToSend.size)
      }

      console.log('FormData содержимое:')
      for (let [key, value] of formData.entries()) {
        console.log(key, ':', value instanceof File ? `File: ${value.name}` : value)
      }

      const response = await fetch('https://saafrac.app.n8n.cloud/webhook-test/bank', {
        method: 'POST',
        body: formData
        // НЕ указываем Content-Type - браузер сам установит multipart/form-data с boundary
      })

      const data = await response.json()
      
      // n8n возвращает массив с объектом, содержащим поле output
      let responseText = "Сообщение получено"
      if (Array.isArray(data) && data.length > 0 && data[0].output) {
        responseText = data[0].output
      } else if (data.output) {
        responseText = data.output
      } else if (data.response) {
        responseText = data.response
      } else if (data.message) {
        responseText = data.message
      }
      
      const botMessage = {
        id: Date.now() + 1,
        text: responseText,
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

  const startVoiceRecognition = () => {
    if (!('webkitSpeechRecognition' in window) && !('SpeechRecognition' in window)) {
      alert('Этот браузер не поддерживает распознавание речи.')
      return
    }

    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
    const recognitionInstance = new SpeechRecognition()
    
    recognitionInstance.lang = 'ru-RU'
    recognitionInstance.interimResults = false
    recognitionInstance.continuous = false

    recognitionInstance.onstart = () => {
      setIsListening(true)
    }

    recognitionInstance.onresult = async (event) => {
      const transcript = event.results[0][0].transcript
      
      if (transcript.trim() === "") {
        setIsListening(false)
        return
      }

      // Добавляем сообщение пользователя
      const userMessage = {
        id: Date.now(),
        text: transcript.trim(),
        sender: 'user',
        timestamp: new Date()
      }
      setMessages(prev => [...prev, userMessage])
      setIsLoading(true)

      try {
        // Отправляем на сервер
        const formData = new FormData()
        formData.append('chatInput', transcript.trim())
        formData.append('sessionId', sessionId)

        console.log('Отправка голосового сообщения')
        for (let [key, value] of formData.entries()) {
          console.log(key, ':', value)
        }

        const response = await fetch('https://saafrac.app.n8n.cloud/webhook-test/bank', {
          method: 'POST',
          body: formData
          // НЕ указываем Content-Type - браузер сам установит multipart/form-data с boundary
        })

        const data = await response.json()
        
        let responseText = "Сообщение получено"
        if (Array.isArray(data) && data.length > 0 && data[0].output) {
          responseText = data[0].output
        } else if (data.output) {
          responseText = data.output
        } else if (data.response) {
          responseText = data.response
        } else if (data.message) {
          responseText = data.message
        }
        
        const botMessage = {
          id: Date.now() + 1,
          text: responseText,
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

    recognitionInstance.onerror = (event) => {
      console.error('Speech recognition error:', event.error)
      setIsListening(false)
    }

    recognitionInstance.onend = () => {
      setIsListening(false)
    }

    setRecognition(recognitionInstance)
    recognitionInstance.start()
  }

  const stopVoiceRecognition = () => {
    if (recognition && isListening) {
      recognition.stop()
      setIsListening(false)
    }
  }

  const handleFileSelect = (e) => {
    const file = e.target.files[0]
    if (file) {
      setAttachedFile(file)
      
      // Создаем превью для изображений
      if (file.type.startsWith('image/')) {
        const reader = new FileReader()
        reader.onload = (e) => {
          setImagePreview(e.target.result)
        }
        reader.readAsDataURL(file)
      } else {
        setImagePreview(null)
      }
    }
  }

  const handleAttachmentClick = () => {
    fileInputRef.current?.click()
  }

  const handlePhotoClick = () => {
    // Создаем временный input для выбора только изображений
    const photoInput = document.createElement('input')
    photoInput.type = 'file'
    photoInput.accept = 'image/*'
    photoInput.capture = 'environment' // Для мобильных устройств - открывает камеру
    photoInput.onchange = (e) => {
      const file = e.target.files[0]
      if (file) {
        setAttachedFile(file)
        
        // Создаем превью для изображения
        const reader = new FileReader()
        reader.onload = (e) => {
          setImagePreview(e.target.result)
        }
        reader.readAsDataURL(file)
      }
    }
    photoInput.click()
  }

  const removeAttachedFile = () => {
    setAttachedFile(null)
    setImagePreview(null)
    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
  }

  return (
    <div className="chat-interface">
      <div className="chat-header">
        <h2>💬 Чат с AI помощником</h2>
        <p>Задавайте вопросы о ваших финансах</p>
        
        <div className="quick-actions">
          <button 
            onClick={onShowRealtimeChat}
            className="action-button realtime-button"
            title="Realtime голосовое общение"
          >
            🎤
            <span>Realtime</span>
          </button>
          
          {analyticsData && (
            <button 
              onClick={onShowAnalytics}
              className="action-button analytics-button"
              title="Показать аналитику"
            >
              📊
              <span>Аналитика</span>
            </button>
          )}
        </div>
      </div>

      <div className="chat-messages">
        {messages.map((message) => (
          <div key={message.id} className={`message ${message.sender}`}>
            <div className="message-avatar">
              {message.sender === 'bot' ? <MdSmartToy size={20} /> : <MdPerson size={20} />}
            </div>
            <div className="message-content">
              {message.imagePreview && (
                <div className="message-image">
                  <img src={message.imagePreview} alt="Attached" />
                </div>
              )}
              {message.file && !message.imagePreview && (
                <div className="message-file">
                  <div className="file-icon">
                    {getFileIcon(message.file.name, message.file.type)}
                  </div>
                  <div className="file-info">
                    <div className="file-name">{message.file.name}</div>
                    <div className="file-size">{(message.file.size / 1024).toFixed(2)} KB</div>
                  </div>
                </div>
              )}
              {message.text && (
                <div 
                  className="message-text" 
                  dangerouslySetInnerHTML={{ __html: formatMessageText(message.text) }}
                />
              )}
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
        {attachedFile && (
          <div className="attached-file-preview">
            {imagePreview ? (
              <div className="image-preview-container">
                <img src={imagePreview} alt="Preview" className="image-preview" />
                <span className="file-name">{attachedFile.name}</span>
              </div>
            ) : (
              <span className="file-name">📎 {attachedFile.name}</span>
            )}
            <button 
              onClick={removeAttachedFile}
              className="remove-file-button"
              title="Удалить файл"
            >
              <MdClose size={16} />
            </button>
          </div>
        )}
        
        <div className={`input-container ${isListening ? 'recording' : ''} ${isLoading ? 'loading' : ''}`}>
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileSelect}
            style={{ display: 'none' }}
            accept="image/*,.pdf,.doc,.docx,.txt,.xlsx,.xls"
          />
          
          <AttachmentMenu 
            onAttachmentClick={handleAttachmentClick}
            onPhotoClick={handlePhotoClick}
          />
          
          <textarea
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={
              isLoading 
                ? "⏳ Генерируется ответ, подождите..." 
                : isListening 
                  ? "🎤 Идет запись голоса..." 
                  : "Введите ваш вопрос..."
            }
            rows="1"
            disabled={isLoading || isListening}
          />
          
          <div className="input-actions">
            <div className="voice-control">
              <button 
                onClick={isListening ? stopVoiceRecognition : startVoiceRecognition}
                className={`voice-button ${isListening ? 'listening' : ''}`}
                title={
                  isLoading 
                    ? "Подождите, генерируется ответ" 
                    : isListening 
                      ? "Нажмите еще раз чтобы отправить аудио" 
                      : "Нажмите чтобы начать запись"
                }
                disabled={isLoading}
              >
                {isListening ? <MdMicOff size={20} /> : <MdMic size={20} />}
              </button>
              {isListening && (
                <div className="voice-recording-indicator">
                  <div className="recording-dot"></div>
                  <span>Запись... Нажмите еще раз для отправки</span>
                </div>
              )}
            </div>
            
            <button 
              onClick={handleSendMessage}
              disabled={(!inputMessage.trim() && !attachedFile) || isLoading}
              className="send-button"
              title={isLoading ? "Подождите, генерируется ответ" : "Отправить сообщение"}
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
