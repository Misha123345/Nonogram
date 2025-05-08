import React, { useContext } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Logo from '../Logo'
import style from './Header.module.css'
import { AuthContext } from '../../contexts/AuthContext'

const Header = () => {
  const { currentUser, logout, isAuthenticated } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className={style.header}>
      <Link to="/" className={style.logoLink}>
        <Logo />
      </Link>
      <nav className={style.navigation}>
        <ul className={style.navList}>
          <li className={style.navItem}>
            <Link to="/play" className={style.navLink}>Play</Link>
          </li>
          <li className={style.navItem}>
            <Link to="/scores" className={style.navLink}>Scores</Link>
          </li>
          <li className={style.navItem}>
            <Link to="/rating" className={style.navLink}>Rating</Link>
          </li>
          <li className={style.navItem}>
            <Link to="/comments" className={style.navLink}>Comments</Link>
          </li>
          
          {isAuthenticated() ? (
            <>
              <li className={style.navItem}>
                <Link to="/profile" className={style.navLink}>Profile</Link>
              </li>
              <li className={style.navItem}>
                <button onClick={handleLogout} className={style.logoutButton}>Logout ({currentUser.username})</button>
              </li>
            </>
          ) : (
            <>
              <li className={style.navItem}>
                <Link to="/login" className={style.navLink}>Login</Link>
              </li>
              <li className={style.navItem}>
                <Link to="/register" className={style.navLink}>Register</Link>
              </li>
            </>
          )}
        </ul>
      </nav>
    </header>
  )
}

export default Header