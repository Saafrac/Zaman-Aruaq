import { useState, useCallback } from 'react'
import { useDropzone } from 'react-dropzone'
import { MdUpload, MdInsertDriveFile, MdClose, MdCheckCircle, MdError } from 'react-icons/md'

const FileUpload = ({ onClose }) => {
  const [uploadedFiles, setUploadedFiles] = useState([])
  const [isUploading, setIsUploading] = useState(false)

  const onDrop = useCallback(async (acceptedFiles) => {
    setIsUploading(true)
    
    for (const file of acceptedFiles) {
      const formData = new FormData()
      formData.append('file', file)
      
      try {
        const response = await fetch('/api/upload', {
          method: 'POST',
          body: formData
        })
        
        const result = await response.json()
        
        setUploadedFiles(prev => [...prev, {
          id: Date.now() + Math.random(),
          file,
          status: 'success',
          result: result,
          uploadedAt: new Date()
        }])
      } catch (error) {
        setUploadedFiles(prev => [...prev, {
          id: Date.now() + Math.random(),
          file,
          status: 'error',
          error: 'Ошибка загрузки файла',
          uploadedAt: new Date()
        }])
      }
    }
    
    setIsUploading(false)
  }, [])

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'application/pdf': ['.pdf'],
      'application/msword': ['.doc'],
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document': ['.docx'],
      'text/plain': ['.txt'],
      'image/*': ['.png', '.jpg', '.jpeg', '.gif']
    },
    multiple: true
  })

  const removeFile = (fileId) => {
    setUploadedFiles(prev => prev.filter(file => file.id !== fileId))
  }

  const getFileIcon = (fileType) => {
    if (fileType.startsWith('image/')) return '🖼️'
    if (fileType === 'application/pdf') return '📄'
    if (fileType.includes('word')) return '📝'
    if (fileType === 'text/plain') return '📃'
    return '📁'
  }

  return (
    <div className="file-upload">
      <button onClick={onClose} className="close-button">
        ×
      </button>
      <div className="upload-header">
        <h2>📁 Загрузка файлов</h2>
        <p>Загрузите документы для анализа AI помощником</p>
      </div>

      <div 
        {...getRootProps()} 
        className={`dropzone ${isDragActive ? 'active' : ''} ${isUploading ? 'uploading' : ''}`}
      >
        <input {...getInputProps()} />
        <div className="dropzone-content">
          <MdUpload size={48} color="#2D9A86" />
          {isDragActive ? (
            <p>Отпустите файлы здесь...</p>
          ) : isUploading ? (
            <p>Загружаем файлы...</p>
          ) : (
            <>
              <p>Перетащите файлы сюда или нажмите для выбора</p>
              <p className="dropzone-hint">
                Поддерживаются: PDF, DOC, DOCX, TXT, изображения
              </p>
            </>
          )}
        </div>
      </div>

      {uploadedFiles.length > 0 && (
        <div className="uploaded-files">
          <h3>Загруженные файлы</h3>
          <div className="files-list">
            {uploadedFiles.map((fileData) => (
              <div key={fileData.id} className="file-item">
                <div className="file-info">
                  <span className="file-icon">
                    {getFileIcon(fileData.file.type)}
                  </span>
                  <div className="file-details">
                    <div className="file-name">{fileData.file.name}</div>
                    <div className="file-size">
                      {(fileData.file.size / 1024 / 1024).toFixed(2)} MB
                    </div>
                    <div className="file-time">
                      {fileData.uploadedAt.toLocaleString()}
                    </div>
                  </div>
                </div>
                
                <div className="file-status">
                  {fileData.status === 'success' ? (
                    <MdCheckCircle size={20} color="#2D9A86" />
                  ) : (
                    <MdError size={20} color="#ff6b6b" />
                  )}
                </div>
                
                <button 
                  onClick={() => removeFile(fileData.id)}
                  className="remove-file"
                >
                  <MdClose size={16} />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {uploadedFiles.some(f => f.status === 'success') && (
        <div className="analysis-section">
          <h3>Результаты анализа</h3>
          <div className="analysis-results">
            {uploadedFiles
              .filter(f => f.status === 'success')
              .map((fileData) => (
                <div key={fileData.id} className="analysis-item">
                  <h4>{fileData.file.name}</h4>
                  <div className="analysis-content">
                    {fileData.result?.summary && (
                      <p><strong>Краткое содержание:</strong> {fileData.result.summary}</p>
                    )}
                    {fileData.result?.keyPoints && (
                      <div>
                        <strong>Ключевые моменты:</strong>
                        <ul>
                          {fileData.result.keyPoints.map((point, index) => (
                            <li key={index}>{point}</li>
                          ))}
                        </ul>
                      </div>
                    )}
                  </div>
                </div>
              ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default FileUpload
