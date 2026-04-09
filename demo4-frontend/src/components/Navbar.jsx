import { Link, useNavigate } from 'react-router-dom';

const swaggerUrl = (process.env.REACT_APP_API_URL || 'http://localhost:8080') + '/swagger-ui/index.html';

export default function Navbar() {
    const navigate = useNavigate();
    const username = localStorage.getItem('username');
    const isAdmin = username === 'hasan';

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    };

    return (
        <nav style={styles.nav}>
            <div style={styles.links}>
                <Link to="/dashboard" style={styles.link}>Kitaplar</Link>
                <Link to="/authors" style={styles.link}>Yazarlar</Link>
                <Link to="/categories" style={styles.link}>Kategoriler</Link>
                {isAdmin && <Link to="/users" style={styles.adminLink}>Kullanıcılar</Link>}
            </div>
            <div style={styles.right}>
                <a href={swaggerUrl} target="_blank" rel="noreferrer" style={styles.swaggerBtn}>Check With Swagger </a>
                <span style={styles.username}>{username}</span>
                <button style={styles.logoutBtn} onClick={handleLogout}>Çıkış Yap</button>
            </div>
        </nav>
    );
}

const styles = {
    nav: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#4f46e5', padding: '10px 24px', marginBottom: '24px' },
    links: { display: 'flex', gap: '20px' },
    link: { color: 'white', textDecoration: 'none', fontWeight: '500', fontSize: '14px' },
    adminLink: { color: '#fcd34d', textDecoration: 'none', fontWeight: '500', fontSize: '14px' },
    right: { display: 'flex', alignItems: 'center', gap: '12px' },
    username: { color: 'white', fontSize: '13px' },
    swaggerBtn: { padding: '6px 14px', backgroundColor: '#85ea2d', color: '#173647', fontWeight: 'bold', borderRadius: '6px', textDecoration: 'none', fontSize: '13px' },
    logoutBtn: { padding: '6px 14px', backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '13px' },
};
