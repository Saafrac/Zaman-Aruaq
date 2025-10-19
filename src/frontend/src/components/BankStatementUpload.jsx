import { useState, useCallback } from 'react'
import { useDropzone } from 'react-dropzone'
import { MdUpload, MdDescription, MdTrendingUp, MdAttachMoney, MdDateRange } from 'react-icons/md'

const BankStatementUpload = ({ onAnalyticsData, onClose }) => {
  const [uploadedStatement, setUploadedStatement] = useState(null)
  const [isProcessing, setIsProcessing] = useState(false)
  const [analysisResult, setAnalysisResult] = useState(null)

  const onDrop = useCallback(async (acceptedFiles) => {
    if (acceptedFiles.length === 0) return

    const file = acceptedFiles[0]
    setIsProcessing(true)

    const formData = new FormData()
    formData.append('statement', file)

    try {
      const response = await fetch('/api/analyze-statement', {
        method: 'POST',
        body: formData
      })

      const result = await response.json()

      setUploadedStatement({
        file,
        uploadedAt: new Date()
      })

      setAnalysisResult(result)

      // Передаем данные аналитики в родительский компонент
      if (onAnalyticsData) {
        onAnalyticsData(result)
      }

    } catch (error) {
      console.error('Ошибка обработки выписки:', error)
    } finally {
      setIsProcessing(false)
    }
  }, [onAnalyticsData])

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'application/pdf': ['.pdf'],
      'text/csv': ['.csv'],
      'application/vnd.ms-excel': ['.xls'],
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': ['.xlsx']
    },
    multiple: false
  })

  const resetUpload = () => {
    setUploadedStatement(null)
    setAnalysisResult(null)
  }

  return (
      <div className="bank-statement-upload">
        <button onClick={onClose} className="close-button">
          ×
        </button>
        <div className="upload-header">
          <h2>🏦 Анализ банковской выписки</h2>
          <p>Загрузите выписку для получения детального финансового анализа</p>
        </div>

        {!uploadedStatement ? (
            <div
                {...getRootProps()}
                className={`statement-dropzone ${isDragActive ? 'active' : ''} ${isProcessing ? 'processing' : ''}`}
            >
              <input {...getInputProps()} />
              <div className="dropzone-content">
                <MdDescription size={64} color="#2D9A86" />
                {isDragActive ? (
                    <p>Отпустите выписку здесь...</p>
                ) : isProcessing ? (
                    <>
                      <p>Обрабатываем выписку...</p>
                      <div className="processing-spinner"></div>
                    </>
                ) : (
                    <>
                      <p>Перетащите банковскую выписку сюда</p>
                      <p className="dropzone-hint">
                        Поддерживаются: PDF, CSV, Excel файлы
                      </p>
                    </>
                )}
              </div>
            </div>
        ) : (
            <div className="statement-analysis">
              <div className="statement-header">
                <div className="file-info">
                  <MdDescription size={24} color="#2D9A86" />
                  <div>
                    <h3>{uploadedStatement.file.name}</h3>
                    <p>Загружено: {uploadedStatement.uploadedAt.toLocaleString()}</p>
                  </div>
                </div>
                <button onClick={resetUpload} className="reset-button">
                  Загрузить новую выписку
                </button>
                <button className="zaman-button">
                  Установите приложение
                </button>
              </div>

              {analysisResult && (
                  <div className="analysis-results">
                    <div className="summary-cards">
                      <div className="summary-card">
                        <div className="card-icon">
                          <MdAttachMoney size={24} color="#2D9A86" />
                        </div>
                        <div className="card-content">
                          <h4>Общий баланс</h4>
                          <p className="card-value">
                            {analysisResult.totalBalance?.toLocaleString()} ₽
                          </p>
                        </div>
                      </div>

                      <div className="summary-card">
                        <div className="card-icon">
                          <MdTrendingUp size={24} color="#EEFE6D" />
                        </div>
                        <div className="card-content">
                          <h4>Доходы за период</h4>
                          <p className="card-value">
                            {analysisResult.totalIncome?.toLocaleString()} ₽
                          </p>
                        </div>
                      </div>

                      <div className="summary-card">
                        <div className="card-icon">
                          <MdTrendingUp size={24} color="#ff6b6b" />
                        </div>
                        <div className="card-content">
                          <h4>Расходы за период</h4>
                          <p className="card-value">
                            {analysisResult.totalExpenses?.toLocaleString()} ₽
                          </p>
                        </div>
                      </div>

                      <div className="summary-card">
                        <div className="card-icon">
                          <MdDateRange size={24} color="#2D9A86" />
                        </div>
                        <div className="card-content">
                          <h4>Период анализа</h4>
                          <p className="card-value">
                            {analysisResult.period}
                          </p>
                        </div>
                      </div>
                    </div>

                    <div className="insights-section">
                      <h3>💡 Финансовые инсайты</h3>
                      <div className="insights-list">
                        {analysisResult.insights?.map((insight, index) => (
                            <div key={index} className="insight-item">
                              <div className="insight-icon">
                                {insight.type === 'positive' ? '✅' :
                                    insight.type === 'warning' ? '⚠️' : '💡'}
                              </div>
                              <div className="insight-content">
                                <h4>{insight.title}</h4>
                                <p>{insight.description}</p>
                              </div>
                            </div>
                        ))}
                      </div>
                    </div>

                    <div className="recommendations-section">
                      <h3>🎯 Рекомендации</h3>
                      <div className="recommendations-list">
                        {analysisResult.recommendations?.map((rec, index) => (
                            <div key={index} className="recommendation-item">
                              <div className="rec-icon">💡</div>
                              <div className="rec-content">
                                <h4>{rec.title}</h4>
                                <p>{rec.description}</p>
                                {rec.impact && (
                                    <span className="rec-impact">
                            Потенциальная экономия: {rec.impact}
                          </span>
                                )}
                              </div>
                            </div>
                        ))}
                      </div>
                    </div>
                  </div>
              )}
            </div>
        )}
      </div>
  )
}

export default BankStatementUpload