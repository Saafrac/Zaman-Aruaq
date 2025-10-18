import { useState, useRef, useEffect } from 'react'
import { MdAttachFile, MdAccountBalance, MdInsertDriveFile } from 'react-icons/md'

const AttachmentMenu = ({ onFileUpload, onStatementUpload }) => {
  const [isOpen, setIsOpen] = useState(false)
  const menuRef = useRef(null)

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setIsOpen(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
    }
  }, [])

  const handleFileClick = () => {
    onFileUpload()
    setIsOpen(false)
  }

  const handleStatementClick = () => {
    onStatementUpload()
    setIsOpen(false)
  }

  return (
    <div className="attachment-menu" ref={menuRef}>
      <button 
        className="attachment-button"
        onClick={() => setIsOpen(!isOpen)}
        title="Прикрепить файл"
      >
        <MdAttachFile size={20} />
      </button>
      
      {isOpen && (
        <div className="attachment-dropdown">
          <button 
            className="dropdown-item"
            onClick={handleFileClick}
          >
            <MdInsertDriveFile size={18} />
            <span>Просто файл</span>
          </button>
          
          <button 
            className="dropdown-item"
            onClick={handleStatementClick}
          >
            <MdAccountBalance size={18} />
            <span>Выписка</span>
          </button>
        </div>
      )}
    </div>
  )
}

export default AttachmentMenu
