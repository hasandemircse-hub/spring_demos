import { useEffect, useState } from 'react';
import api from '../api/axiosConfig';
import Navbar from '../components/Navbar';

export default function Categories() {
    const [categories, setCategories] = useState([]);
    const [name, setName] = useState('');
    const [editId, setEditId] = useState(null);
    const [error, setError] = useState('');

    const fetchCategories = () => {
        api.get('/api/categories')
            .then(res => setCategories(res.data))
            .catch(() => setError('Kategoriler yüklenemedi.'));
    };

    useEffect(() => { fetchCategories(); }, []);

    const handleSubmit = (e) => {
        e.preventDefault();
        const req = editId
            ? api.put(`/api/categories/${editId}`, name, { headers: { 'Content-Type': 'text/plain' } })
            : api.post('/api/categories', name, { headers: { 'Content-Type': 'text/plain' } });

        req.then(() => {
            setName('');
            setEditId(null);
            fetchCategories();
        }).catch(() => setError('İşlem başarısız.'));
    };

    const handleDelete = (id) => {
        if (!window.confirm('Kategoriyi silmek istediğinizden emin misiniz?')) return;
        api.delete(`/api/categories/${id}`)
            .then(fetchCategories)
            .catch(() => setError('Silme başarısız.'));
    };

    return (
        <div>
            <Navbar />
            <div style={styles.container}>
                <h2 style={styles.title}>Kategoriler</h2>
                {error && <p style={styles.error}>{error}</p>}

                <form onSubmit={handleSubmit} style={styles.form}>
                    <input style={styles.input} placeholder="Kategori adı" value={name}
                        onChange={e => setName(e.target.value)} required />
                    <button style={styles.submitBtn} type="submit">{editId ? 'Güncelle' : 'Ekle'}</button>
                    {editId && <button style={styles.cancelBtn} type="button" onClick={() => { setEditId(null); setName(''); }}>İptal</button>}
                </form>

                <table style={styles.table}>
                    <thead>
                        <tr>
                            <th style={styles.th}>ID</th>
                            <th style={styles.th}>Kategori Adı</th>
                            <th style={styles.th}>İşlemler</th>
                        </tr>
                    </thead>
                    <tbody>
                        {categories.map(c => (
                            <tr key={c.id} style={styles.tr}>
                                <td style={styles.td}>{c.id}</td>
                                <td style={styles.td}>{c.name}</td>
                                <td style={styles.td}>
                                    <button style={styles.editBtn} onClick={() => { setEditId(c.id); setName(c.name); }}>Düzenle</button>
                                    <button style={styles.deleteBtn} onClick={() => handleDelete(c.id)}>Sil</button>
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
    form: { display: 'flex', gap: '10px', marginBottom: '24px' },
    input: { flex: 1, padding: '8px 12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px' },
    submitBtn: { padding: '8px 16px', backgroundColor: '#4f46e5', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    cancelBtn: { padding: '8px 16px', backgroundColor: '#6b7280', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    table: { width: '100%', borderCollapse: 'collapse' },
    th: { textAlign: 'left', padding: '10px', backgroundColor: '#f3f4f6', borderBottom: '2px solid #e5e7eb', fontSize: '13px', color: '#374151' },
    tr: { borderBottom: '1px solid #f0f0f0' },
    td: { padding: '10px', fontSize: '14px' },
    editBtn: { marginRight: '8px', padding: '4px 10px', backgroundColor: '#f59e0b', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    deleteBtn: { padding: '4px 10px', backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
};
