import { useState, useRef, useEffect } from 'react'
import { MdAttachFile, MdInsertDriveFile, MdCameraAlt, MdAccountBalance } from 'react-icons/md'

const AttachmentMenu = ({ onAttachmentClick, onPhotoClick, onBankStatementClick }) => {
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

  const handleAttachClick = () => {
    onAttachmentClick()
    setIsOpen(false)
  }

  const handlePhotoAttachClick = () => {
    onPhotoClick()
    setIsOpen(false)
  }

  const handleBankStatementClick = () => {
    onBankStatementClick()
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
            onClick={handleAttachClick}
          >
            <MdInsertDriveFile size={18} />
            <span>Файл / Документ</span>
          </button>
          
          <button 
            className="dropdown-item"
            onClick={handlePhotoAttachClick}
          >
            <MdCameraAlt size={18} />
            <span>Фото / Камера</span>
          </button>

          <button 
            className="dropdown-item"
            onClick={handleBankStatementClick}
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
