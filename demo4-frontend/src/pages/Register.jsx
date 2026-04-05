import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig';

export default function Register() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [success, setSuccess] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await api.post('/auth/register', { username, password });
            setSuccess('Kayıt başarılı! Giriş yapabilirsiniz.');
            setTimeout(() => navigate('/login'), 1500);
        } catch (err) {
            setError('Kayıt başarısız, tekrar deneyin.');
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={styles.title}>Kayıt Ol</h2>
                {error && <p style={styles.error}>{error}</p>}
                {success && <p style={styles.success}>{success}</p>}
                <form onSubmit={handleRegister}>
                    <input
                        style={styles.input}
                        type="text"
                        placeholder="Kullanıcı Adı"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <input
                        style={styles.input}
                        type="password"
                        placeholder="Şifre"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <button style={styles.button} type="submit">Kayıt Ol</button>
                </form>
                <p style={styles.link}>
                    Zaten hesabın var mı? <Link to="/login">Giriş Yap</Link>
                </p>
            </div>
        </div>
    );
}

const styles = {
    container: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', backgroundColor: '#f0f2f5' },
    card: { backgroundColor: 'white', padding: '40px', borderRadius: '12px', boxShadow: '0 4px 20px rgba(0,0,0,0.1)', width: '360px' },
    title: { textAlign: 'center', marginBottom: '24px', color: '#333' },
    input: { width: '100%', padding: '12px', marginBottom: '16px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '14px', boxSizing: 'border-box' },
    button: { width: '100%', padding: '12px', backgroundColor: '#4f46e5', color: 'white', border: 'none', borderRadius: '8px', fontSize: '16px', cursor: 'pointer' },
    error: { color: 'red', textAlign: 'center', marginBottom: '12px' },
    success: { color: 'green', textAlign: 'center', marginBottom: '12px' },
    link: { textAlign: 'center', marginTop: '16px', fontSize: '14px' }
};