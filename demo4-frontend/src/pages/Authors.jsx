import { useEffect, useState } from 'react';
import api from '../api/axiosConfig';
import Navbar from '../components/Navbar';

export default function Authors() {
    const [authors, setAuthors] = useState([]);
    const [form, setForm] = useState({ name: '' });
    const [editId, setEditId] = useState(null);
    const [error, setError] = useState('');

    const fetchAuthors = () => {
        api.get('/api/authors')
            .then(res => setAuthors(res.data))
            .catch(() => setError('Yazarlar yüklenemedi.'));
    };

    useEffect(() => { fetchAuthors(); }, []);

    const handleSubmit = (e) => {
        e.preventDefault();
        const req = editId
            ? api.put(`/api/authors/${editId}`, form)
            : api.post('/api/authors', form);

        req.then(() => {
            setForm({ name: '' });
            setEditId(null);
            setError('');
            fetchAuthors();
        }).catch(() => setError('İşlem başarısız.'));
    };

    const handleEdit = (author) => {
        setEditId(author.id);
        setForm({ name: author.name });
    };

    const handleDelete = (id) => {
        if (!window.confirm('Yazarı silmek istediğinizden emin misiniz?')) return;
        api.delete(`/api/authors/${id}`)
            .then(fetchAuthors)
            .catch(() => setError('Silme başarısız.'));
    };

    return (
        <div>
            <Navbar />
            <div style={styles.container}>
                <h2 style={styles.title}>Yazarlar</h2>
                {error && <p style={styles.error}>{error}</p>}

                <form onSubmit={handleSubmit} style={styles.form}>
                    <input style={styles.input} placeholder="Yazar Adı" value={form.name}
                        onChange={e => setForm({ name: e.target.value })} required />
                    <button style={styles.submitBtn} type="submit">{editId ? 'Güncelle' : 'Ekle'}</button>
                    {editId && <button style={styles.cancelBtn} type="button" onClick={() => { setEditId(null); setForm({ name: '' }); }}>İptal</button>}
                </form>

                <table style={styles.table}>
                    <thead>
                        <tr>
                            <th style={styles.th}>#</th>
                            <th style={styles.th}>Ad</th>
                            <th style={styles.th}>İşlemler</th>
                        </tr>
                    </thead>
                    <tbody>
                        {authors.map(a => (
                            <tr key={a.id} style={styles.tr}>
                                <td style={styles.td}>{a.id}</td>
                                <td style={styles.td}>{a.name}</td>
                                <td style={styles.td}>
                                    <button style={styles.editBtn} onClick={() => handleEdit(a)}>Düzenle</button>
                                    <button style={styles.deleteBtn} onClick={() => handleDelete(a.id)}>Sil</button>
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
    input: { padding: '8px 12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px', flex: 1 },
    submitBtn: { padding: '8px 16px', backgroundColor: '#4f46e5', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    cancelBtn: { padding: '8px 16px', backgroundColor: '#6b7280', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    table: { width: '100%', borderCollapse: 'collapse' },
    th: { textAlign: 'left', padding: '10px', backgroundColor: '#f3f4f6', borderBottom: '2px solid #e5e7eb', fontSize: '13px', color: '#374151' },
    tr: { borderBottom: '1px solid #f0f0f0' },
    td: { padding: '10px', fontSize: '14px' },
    editBtn: { marginRight: '8px', padding: '4px 10px', backgroundColor: '#f59e0b', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    deleteBtn: { padding: '4px 10px', backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
};
