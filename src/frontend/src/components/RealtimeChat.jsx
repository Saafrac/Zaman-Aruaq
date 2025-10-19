import { useState, useRef, useEffect } from 'react'
import { MdClose } from 'react-icons/md'
import './RealtimeChat.css'

const RealtimeChat = ({ onClose }) => {
  const [isRecording, setIsRecording] = useState(false)
  const [isPlaying, setIsPlaying] = useState(false)
  const [statusText, setStatusText] = useState('Нажмите чтобы говорить')
  
  const orbRef = useRef(null)
  const ringRef = useRef(null)
  const beepRef = useRef(null)
  const audioRef = useRef(null)
  const mediaRecorderRef = useRef(null)
  const audioChunksRef = useRef([])

  useEffect(() => {
    const createBeep = () => {
      const audioContext = new (window.AudioContext || window.webkitAudioContext)()
      const oscillator = audioContext.createOscillator()
      const gainNode = audioContext.createGain()
      
      oscillator.connect(gainNode)
      gainNode.connect(audioContext.destination)
      
      oscillator.frequency.value = 800
      oscillator.type = 'sine'
      
      gainNode.gain.setValueAtTime(0.3, audioContext.currentTime)
      gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.1)
      
      oscillator.start(audioContext.currentTime)
      oscillator.stop(audioContext.currentTime + 0.1)
    }
    
    beepRef.current = { play: createBeep }

    return () => {
      if (mediaRecorderRef.current && mediaRecorderRef.current.state === 'recording') {
        mediaRecorderRef.current.stop()
      }
      if (audioRef.current) {
        audioRef.current.pause()
        audioRef.current = null
      }
    }
  }, [])

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      
      if (beepRef.current) {
        try {
          beepRef.current.play()
        } catch (e) {
          console.log('Beep play failed:', e)
        }
      }

      audioChunksRef.current = []
      const mediaRecorder = new MediaRecorder(stream)
      mediaRecorderRef.current = mediaRecorder

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunksRef.current.push(event.data)
        }
      }

      mediaRecorder.onstart = () => {
        setIsRecording(true)
        setStatusText('Запись...')
        if (orbRef.current) orbRef.current.classList.add('listening')
        if (ringRef.current) ringRef.current.classList.add('listening')
      }

      mediaRecorder.onstop = async () => {
        setIsRecording(false)
        if (orbRef.current) orbRef.current.classList.remove('listening')
        if (ringRef.current) ringRef.current.classList.remove('listening')
        
        stream.getTracks().forEach(track => track.stop())

        if (audioChunksRef.current.length > 0) {
          await processAudio()
        } else {
          setStatusText('Нажмите чтобы говорить')
        }
      }

      mediaRecorder.start()
    } catch (err) {
      console.error('Error accessing microphone:', err)
      setStatusText('Ошибка доступа к микрофону')
    }
  }

  const stopRecording = () => {
    if (mediaRecorderRef.current && mediaRecorderRef.current.state === 'recording') {
      mediaRecorderRef.current.stop()
    }
  }

  const processAudio = async () => {
    setStatusText('Обрабатываю...')

    try {
      const audioBlob = new Blob(audioChunksRef.current, { type: 'audio/webm' })
      
      const formData = new FormData()
      formData.append('data', audioBlob, 'audio.webm')

      const response = await fetch('https://saafrac.app.n8n.cloud/webhook/process-audio', {
        method: 'POST',
        body: formData
      })

      if (!response.ok) {
        throw new Error(`Server response error: ${response.status}`)
      }

      const contentType = response.headers.get('content-type')
      console.log('Response content-type:', contentType)

      if (contentType && contentType.includes('audio')) {
        const blob = await response.blob()
        const audioURL = URL.createObjectURL(blob)
        const audio = new Audio(audioURL)
        audioRef.current = audio

        audio.onplay = () => {
          setIsPlaying(true)
          setStatusText('Отвечаю...')
          if (orbRef.current) orbRef.current.classList.add('playing')
          if (ringRef.current) ringRef.current.classList.add('playing')
        }

        audio.onended = () => {
          setIsPlaying(false)
          setStatusText('Нажмите чтобы говорить')
          if (orbRef.current) orbRef.current.classList.remove('playing')
          if (ringRef.current) ringRef.current.classList.remove('playing')
          URL.revokeObjectURL(audioURL)
          audioRef.current = null
        }

        audio.onerror = () => {
          setIsPlaying(false)
          setStatusText('Ошибка воспроизведения')
          if (orbRef.current) orbRef.current.classList.remove('playing')
          if (ringRef.current) ringRef.current.classList.remove('playing')
          URL.revokeObjectURL(audioURL)
          audioRef.current = null
        }

        await audio.play()
      } else {
        const data = await response.json()
        console.log('JSON response:', data)
        
        if (data.audioUrl) {
          const audio = new Audio(data.audioUrl)
          audioRef.current = audio
          
          audio.onplay = () => {
            setIsPlaying(true)
            setStatusText('Отвечаю...')
            if (orbRef.current) orbRef.current.classList.add('playing')
            if (ringRef.current) ringRef.current.classList.add('playing')
          }

          audio.onended = () => {
            setIsPlaying(false)
            setStatusText('Нажмите чтобы говорить')
            if (orbRef.current) orbRef.current.classList.remove('playing')
            if (ringRef.current) ringRef.current.classList.remove('playing')
            audioRef.current = null
          }

          audio.onerror = () => {
            setIsPlaying(false)
            setStatusText('Ошибка воспроизведения')
            if (orbRef.current) orbRef.current.classList.remove('playing')
            if (ringRef.current) ringRef.current.classList.remove('playing')
            audioRef.current = null
          }

          await audio.play()
        } else {
          setStatusText(`Ответ: ${data.response || data.message || 'Получен ответ'}`)
          setTimeout(() => {
            setStatusText('Нажмите чтобы говорить')
          }, 3000)
        }
      }

    } catch (err) {
      console.error('Error sending or processing response:', err)
      setStatusText('Ошибка связи с AI')
    }
  }

  const stopAudio = () => {
    if (audioRef.current) {
      audioRef.current.pause()
      audioRef.current.currentTime = 0
      audioRef.current = null
    }
    setIsPlaying(false)
    if (orbRef.current) orbRef.current.classList.remove('playing')
    if (ringRef.current) ringRef.current.classList.remove('playing')
  }

  const handleClose = () => {
    // Останавливаем запись если она идёт
    if (mediaRecorderRef.current && mediaRecorderRef.current.state === 'recording') {
      mediaRecorderRef.current.stop()
      // Останавливаем все треки
      const stream = mediaRecorderRef.current.stream
      if (stream) {
        stream.getTracks().forEach(track => track.stop())
      }
    }
    
    // Останавливаем воспроизведение
    stopAudio()
    
    // Сбрасываем состояния
    setIsRecording(false)
    setIsPlaying(false)
    setStatusText('Нажмите чтобы говорить')
    if (orbRef.current) {
      orbRef.current.classList.remove('listening')
      orbRef.current.classList.remove('playing')
    }
    if (ringRef.current) {
      ringRef.current.classList.remove('listening')
      ringRef.current.classList.remove('playing')
    }
    
    // Закрываем окно
    onClose()
  }

  const handleOrbClick = () => {
    if (isPlaying) {
      stopAudio()
      setStatusText('Нажмите чтобы говорить')
      startRecording()
      return
    }
    
    if (isRecording) {
      stopRecording()
    } else {
      startRecording()
    }
  }

  return (
    <div className="realtime-chat-overlay">
      <div className="realtime-chat-container">
        <button className="close-button" onClick={handleClose}>
          <MdClose size={24} />
        </button>
        
        <div className="orb-container">
          <div 
            ref={ringRef}
            className="ring"
          ></div>
          <div 
            ref={orbRef}
            className="orb"
            onClick={handleOrbClick}
            title={isPlaying ? "Нажмите чтобы прервать и говорить" : isRecording ? "Нажмите чтобы остановить" : "Нажмите чтобы говорить с AI"}
          ></div>
        </div>
        
        <div className="status-text">
          {statusText}
        </div>
      </div>
    </div>
  )
}

export default RealtimeChat
