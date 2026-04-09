import { useEffect, useState } from 'react';
import api from '../api/axiosConfig';
import Navbar from '../components/Navbar';

export default function Users() {
    const [users, setUsers] = useState([]);
    const [form, setForm] = useState({ username: '', password: '', role: 'USER' });
    const [error, setError] = useState('');

    const fetchUsers = () => {
        api.get('/api/users')
            .then(res => setUsers(res.data))
            .catch(() => setError('Kullanıcılar yüklenemedi.'));
    };

    useEffect(() => { fetchUsers(); }, []);

    const handleSubmit = (e) => {
        e.preventDefault();
        api.post('/api/users', form)
            .then(() => {
                setForm({ username: '', password: '', role: 'USER' });
                fetchUsers();
            })
            .catch(() => setError('Kullanıcı eklenemedi.'));
    };

    const handleDelete = (id) => {
        if (!window.confirm('Kullanıcıyı silmek istediğinizden emin misiniz?')) return;
        api.delete(`/api/users/${id}`)
            .then(fetchUsers)
            .catch(() => setError('Silme başarısız.'));
    };

    return (
        <div>
            <Navbar />
            <div style={styles.container}>
                <h2 style={styles.title}>Kullanıcı Yönetimi</h2>
                {error && <p style={styles.error}>{error}</p>}

                <form onSubmit={handleSubmit} style={styles.form}>
                    <input style={styles.input} placeholder="Kullanıcı Adı" value={form.username}
                        onChange={e => setForm({ ...form, username: e.target.value })} required />
                    <input style={styles.input} placeholder="Şifre" type="password" value={form.password}
                        onChange={e => setForm({ ...form, password: e.target.value })} required />
                    <select style={styles.input} value={form.role} onChange={e => setForm({ ...form, role: e.target.value })}>
                        <option value="USER">USER</option>
                        <option value="ADMIN">ADMIN</option>
                    </select>
                    <button style={styles.submitBtn} type="submit">Ekle</button>
                </form>

                <table style={styles.table}>
                    <thead>
                        <tr>
                            <th style={styles.th}>ID</th>
                            <th style={styles.th}>Kullanıcı Adı</th>
                            <th style={styles.th}>Rol</th>
                            <th style={styles.th}>İşlemler</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map(u => (
                            <tr key={u.id} style={styles.tr}>
                                <td style={styles.td}>{u.id}</td>
                                <td style={styles.td}>{u.username}</td>
                                <td style={styles.td}>
                                    <span style={u.role === 'ADMIN' ? styles.adminBadge : styles.userBadge}>{u.role}</span>
                                </td>
                                <td style={styles.td}>
                                    <button style={styles.deleteBtn} onClick={() => handleDelete(u.id)}>Sil</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

const styles = {
    container: { maxWidth: '700px', margin: '0 auto', padding: '0 24px' },
    title: { color: '#4f46e5', marginBottom: '20px' },
    error: { color: '#ef4444', marginBottom: '12px' },
    form: { display: 'flex', gap: '10px', marginBottom: '24px', flexWrap: 'wrap' },
    input: { padding: '8px 12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px' },
    submitBtn: { padding: '8px 16px', backgroundColor: '#4f46e5', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    table: { width: '100%', borderCollapse: 'collapse' },
    th: { textAlign: 'left', padding: '10px', backgroundColor: '#f3f4f6', borderBottom: '2px solid #e5e7eb', fontSize: '13px', color: '#374151' },
    tr: { borderBottom: '1px solid #f0f0f0' },
    td: { padding: '10px', fontSize: '14px' },
    adminBadge: { backgroundColor: '#fcd34d', color: '#92400e', padding: '2px 8px', borderRadius: '12px', fontSize: '12px', fontWeight: 'bold' },
    userBadge: { backgroundColor: '#dbeafe', color: '#1e40af', padding: '2px 8px', borderRadius: '12px', fontSize: '12px', fontWeight: 'bold' },
    deleteBtn: { padding: '4px 10px', backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
};
