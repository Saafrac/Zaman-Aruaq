import { useState, useMemo, useRef, useEffect } from 'react'
import { MdSend, MdEdit, MdChat, MdClose, MdSmartToy, MdPerson } from 'react-icons/md'
import './GoalCreator.css'

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

const GoalCreator = () => {
  const [input, setInput] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [goalData, setGoalData] = useState(null)
  const [error, setError] = useState(null)
  const [previousInput, setPreviousInput] = useState('')
  const [previousOutput, setPreviousOutput] = useState('')
  const [isEditing, setIsEditing] = useState(false)
  const sessionId = useMemo(() => generateSessionId(), [])
  
  // Состояние для чата
  const [showChat, setShowChat] = useState(false)
  const [chatMessages, setChatMessages] = useState([])
  const [chatInput, setChatInput] = useState('')
  const [chatLoading, setChatLoading] = useState(false)
  const chatMessagesEndRef = useRef(null)

  const generateImage = async (goalTitle) => {
    try {
      const imageResponse = await fetch('https://saafrac.app.n8n.cloud/webhook-test/image-gen', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          prompt: goalTitle
        })
      })

      const imageData = await imageResponse.json()
      
      // Проверяем различные возможные форматы ответа
      if (imageData.imageUrl) {
        return imageData.imageUrl
      } else if (imageData.url) {
        return imageData.url
      } else if (imageData.image) {
        return imageData.image
      } else if (typeof imageData === 'string') {
        return imageData
      }
      
      return null
    } catch (err) {
      console.error('Ошибка генерации изображения:', err)
      return null
    }
  }

  const handleSubmit = async () => {
    if (!input.trim()) return

    setIsLoading(true)
    setError(null)

    try {
      // Если редактируем, добавляем контекст предыдущего запроса
      let finalInput = input
      if (isEditing && previousInput && previousOutput) {
        finalInput = `Предыдущий запрос: ${previousInput}\nПредыдущий результат: ${previousOutput}\nНовый запрос (изменение цели): ${input}`
      }

      const response = await fetch('https://saafrac.app.n8n.cloud/webhook/goal', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          chatInput: finalInput,
          sessionId: sessionId
        })
      })

      const data = await response.json()
      
      // Обработка ответа: берем output и убираем \n
      if (Array.isArray(data) && data.length > 0 && data[0].output) {
        let outputStr = data[0].output
        
        // Убираем все \n и лишние пробелы
        outputStr = outputStr.replace(/\\n/g, '').replace(/\n/g, '')
        
        // Убираем markdown блоки кода
        outputStr = outputStr.replace(/```json/g, '').replace(/```/g, '')
        
        // Парсим JSON
        const parsedData = JSON.parse(outputStr)
        
        if (parsedData.success) {
          // Сохраняем предыдущие данные для возможного редактирования
          setPreviousInput(input)
          setPreviousOutput(outputStr)
          
          // Генерируем изображение для цели
          const imageUrl = await generateImage(parsedData.goal.title)
          
          // Добавляем imageUrl в данные цели
          const goalWithImage = {
            ...parsedData.goal,
            imageUrl: imageUrl
          }
          
          setGoalData(goalWithImage)
          setInput('')
          setIsEditing(false)
        } else {
          setError(parsedData.message || 'Недостаточно данных для создания цели')
        }
      } else {
        setError('Неверный формат ответа от сервера')
      }
    } catch (err) {
      console.error('Ошибка:', err)
      setError('Произошла ошибка при создании цели')
    } finally {
      setIsLoading(false)
    }
  }

  const handleEditGoal = () => {
    setIsEditing(true)
    setGoalData(null)
  }

  const handleCreateNewGoal = () => {
    setGoalData(null)
    setError(null)
    setPreviousInput('')
    setPreviousOutput('')
    setIsEditing(false)
    setShowChat(false)
    setChatMessages([])
  }

  const handleDiscussGoal = () => {
    setShowChat(true)
    if (chatMessages.length === 0) {
      setChatMessages([{
        id: 1,
        text: "Привет! Я помогу вам с вашей финансовой целью. Задавайте вопросы!",
        sender: 'bot',
        timestamp: new Date()
      }])
    }
  }

  const scrollChatToBottom = () => {
    chatMessagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }

  useEffect(() => {
    if (showChat) {
      scrollChatToBottom()
    }
  }, [chatMessages, showChat])

  const handleSendChatMessage = async () => {
    if (!chatInput.trim() || chatLoading) return

    const userMessage = {
      id: Date.now(),
      text: chatInput,
      sender: 'user',
      timestamp: new Date()
    }

    setChatMessages(prev => [...prev, userMessage])
    const messageText = chatInput
    setChatInput('')
    setChatLoading(true)

    try {
      // Формируем контекст с данными цели
      const goalContext = `
Контекст цели пользователя:
- Цель: ${goalData.title}
- Целевая сумма: ${goalData.targetAmount} тенге
- Уже накоплено: ${goalData.currentAmount} тенге
- Срок накопления: ${goalData.durationMonths} месяцев

Вопрос пользователя: ${messageText}
`

      const formData = new FormData()
      formData.append('chatInput', goalContext)
      formData.append('sessionId', sessionId)

      const response = await fetch('https://saafrac.app.n8n.cloud/webhook/bank', {
        method: 'POST',
        body: formData
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

      setChatMessages(prev => [...prev, botMessage])
    } catch (error) {
      const errorMessage = {
        id: Date.now() + 1,
        text: "Извините, произошла ошибка соединения. Попробуйте еще раз.",
        sender: 'bot',
        timestamp: new Date()
      }
      setChatMessages(prev => [...prev, errorMessage])
    } finally {
      setChatLoading(false)
    }
  }

  const handleChatKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSendChatMessage()
    }
  }

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSubmit()
    }
  }

  const progress = goalData ? (goalData.currentAmount / goalData.targetAmount) * 100 : 0
  const monthlyRecommended = goalData ? Math.ceil((goalData.targetAmount - goalData.currentAmount) / goalData.durationMonths) : 0
  const monthlyAggressive = goalData ? Math.ceil(monthlyRecommended * 1.5) : 0
  const aggressiveMonths = goalData ? Math.ceil((goalData.targetAmount - goalData.currentAmount) / monthlyAggressive) : 0

  const formatAmount = (amount) => {
    return new Intl.NumberFormat('ru-RU').format(amount)
  }

  return (
    <div className="goal-creator-full">
      <div className="goal-header">
        <h2>🎯 Создать финансовую цель</h2>
      </div>

      {!goalData ? (
        <div className="goal-input-section">
          <p className="goal-description">
            {isEditing 
              ? 'Опишите, что хотите изменить в вашей цели (сумму, срок, или добавьте дополнительную информацию)'
              : 'Опишите свою цель: на что хотите накопить, сколько зарабатываете, сколько можете откладывать'
            }
          </p>
          
          <textarea
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={
              isEditing
                ? "Например: Хочу уменьшить срок до 10 месяцев или Хочу увеличить сумму до 600 000 тенге"
                : "Например: Хочу накопить на новый iPhone за 500 000 тенге. Зарабатываю 300к, остаётся 20к в месяц, хочу за 12 месяцев накопить"
            }
            rows="4"
            disabled={isLoading}
            className="goal-textarea"
          />

          {error && (
            <div className="error-message">
              ⚠️ {error}
            </div>
          )}

          <button
            onClick={handleSubmit}
            disabled={!input.trim() || isLoading}
            className="submit-button"
          >
            {isLoading ? (
              <>
                <div className="spinner"></div>
                {isEditing ? 'Обновление...' : 'Создание...'}
              </>
            ) : (
              <>
                <MdSend size={20} />
                {isEditing ? 'Обновить цель' : 'Создать цель'}
              </>
            )}
          </button>
        </div>
      ) : (
        <div className="goal-display">
          <div className="goal-card">
            <div className="goal-image">
              {goalData.imageUrl && (
                <img src={goalData.imageUrl} alt={goalData.title} />
              )}
              <div className="goal-title-overlay">
                <h3>{goalData.title}</h3>
              </div>
            </div>

            <div className="goal-info">
              <div className="goal-amounts">
                <div className="amount-item">
                  <span className="amount-label">Накоплено</span>
                  <span className="amount-value current">
                    {formatAmount(goalData.currentAmount)} ₸
                  </span>
                </div>
                <div className="amount-item">
                  <span className="amount-label">Цель</span>
                  <span className="amount-value target">
                    {formatAmount(goalData.targetAmount)} ₸
                  </span>
                </div>
              </div>

              <div className="progress-section">
                <div className="progress-header">
                  <span>Прогресс накопления</span>
                  <span className="progress-percent">{progress.toFixed(1)}%</span>
                </div>
                <div className="progress-bar">
                  <div 
                    className="progress-fill" 
                    style={{ width: `${Math.min(progress, 100)}%` }}
                  >
                    <div className="progress-shine"></div>
                  </div>
                </div>
                <div className="progress-amounts">
                  <span>{formatAmount(goalData.currentAmount)} ₸</span>
                  <span>{formatAmount(goalData.targetAmount)} ₸</span>
                </div>
              </div>

              <div className="goal-stats">
                <div className="stat-item">
                  <div className="stat-icon">📅</div>
                  <div className="stat-content">
                    <span className="stat-label">Срок накопления</span>
                    <span className="stat-value">{goalData.durationMonths} месяцев</span>
                  </div>
                </div>
                <div className="stat-item">
                  <div className="stat-icon">💰</div>
                  <div className="stat-content">
                    <span className="stat-label">Осталось накопить</span>
                    <span className="stat-value">
                      {formatAmount(goalData.targetAmount - goalData.currentAmount)} ₸
                    </span>
                  </div>
                </div>
              </div>

              <div className="predictions">
                <h4>📊 Рекомендации по накоплению</h4>
                
                <div className="prediction-card recommended">
                  <div className="prediction-header">
                    <span className="prediction-badge">Рекомендуется</span>
                    <span className="prediction-amount">{formatAmount(monthlyRecommended)} ₸/мес</span>
                  </div>
                  <p className="prediction-description">
                    Откладывайте по {formatAmount(monthlyRecommended)} ₸ каждый месяц, 
                    чтобы достичь цели за {goalData.durationMonths} месяцев
                  </p>
                </div>

                <div className="prediction-card aggressive">
                  <div className="prediction-header">
                    <span className="prediction-badge">Быстрое накопление</span>
                    <span className="prediction-amount">{formatAmount(monthlyAggressive)} ₸/мес</span>
                  </div>
                  <p className="prediction-description">
                    Откладывайте по {formatAmount(monthlyAggressive)} ₸ каждый месяц, 
                    чтобы достичь цели за {aggressiveMonths} месяцев
                  </p>
                </div>
              </div>

              <div className="goal-actions">
                <button 
                  onClick={handleEditGoal}
                  className="edit-goal-button"
                >
                  <MdEdit size={20} />
                  Редактировать цель
                </button>
                <button 
                  onClick={handleDiscussGoal}
                  className="discuss-goal-button"
                >
                  <MdChat size={20} />
                  Обсудить это
                </button>
                <button 
                  onClick={handleCreateNewGoal}
                  className="create-another-button"
                >
                  Создать другую цель
                </button>
              </div>
            </div>
          </div>

          {showChat && (
            <div className="goal-chat-container">
              <div className="goal-chat-header">
                <h3>💬 Обсудить цель</h3>
                <button onClick={() => setShowChat(false)} className="goal-chat-close">
                  <MdClose />
                </button>
              </div>

              <div className="goal-chat-messages">
                {chatMessages.map((message) => (
                  <div key={message.id} className={`goal-chat-message ${message.sender}`}>
                    <div className="goal-chat-avatar">
                      {message.sender === 'bot' ? <MdSmartToy /> : <MdPerson />}
                    </div>
                    <div 
                      className="goal-chat-bubble"
                      dangerouslySetInnerHTML={{ __html: formatMessageText(message.text) }}
                    />
                  </div>
                ))}

                {chatLoading && (
                  <div className="goal-chat-message bot">
                    <div className="goal-chat-avatar">
                      <MdSmartToy />
                    </div>
                    <div className="goal-chat-bubble">
                      <div className="goal-chat-typing">
                        <span></span>
                        <span></span>
                        <span></span>
                      </div>
                    </div>
                  </div>
                )}

                <div ref={chatMessagesEndRef} />
              </div>

              <div className="goal-chat-input-container">
                <textarea
                  value={chatInput}
                  onChange={(e) => setChatInput(e.target.value)}
                  onKeyPress={handleChatKeyPress}
                  placeholder="Задайте вопрос о вашей цели..."
                  rows="2"
                  disabled={chatLoading}
                  className="goal-chat-input"
                />
                <button
                  onClick={handleSendChatMessage}
                  disabled={!chatInput.trim() || chatLoading}
                  className="goal-chat-send"
                >
                  <MdSend size={18} />
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default GoalCreator

