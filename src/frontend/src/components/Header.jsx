import { MdMessage } from 'react-icons/md'

const Header = () => {
  return (
    <header className="header">
      <div className="header-content">
        <div className="logo">
          <MdMessage size={24} color="#2D9A86" />
          <h1>Zaman Bank AI Assistant</h1>
        </div>
        <div className="header-subtitle">
          Ваш персональный финансовый помощник
        </div>
      </div>
    </header>
  )
}

export default Header
