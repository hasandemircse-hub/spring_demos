import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import PrayerTimes from '../components/PrayerTimes';
import Navbar from '../components/Navbar';

export default function Books() {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(0);
    const [size] = useState(10);
    const [sortBy, setSortBy] = useState('id');
    const [sortDir, setSortDir] = useState('asc');
    const [editBook, setEditBook] = useState(null);
    const [form, setForm] = useState({ title: '', authorId: '' });
    const [showAdd, setShowAdd] = useState(false);
    const [authors, setAuthors] = useState([]);
    const navigate = useNavigate();

    const fetchBooks = () => {
        setLoading(true);
        api.get(`/api/books/paged?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`)
            .then(res => setData(res.data))
            .catch(() => {
                localStorage.removeItem('token');
                navigate('/login');
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        api.get('/api/authors').then(res => setAuthors(res.data)).catch(() => {});
    }, []);

    useEffect(() => { fetchBooks(); }, [page, size, sortBy, sortDir]);

    const handleSort = (column) => {
        if (column === sortBy) {
            setSortDir(d => d === 'asc' ? 'desc' : 'asc');
        } else {
            setSortBy(column);
            setSortDir('asc');
        }
        setPage(0);
    };

    const handleAdd = (e) => {
        e.preventDefault();
        api.post('/api/books', form)
            .then(() => { setForm({ title: '', authorId: '' }); setShowAdd(false); fetchBooks(); })
            .catch(() => alert('Kitap eklenemedi.'));
    };

    const handleUpdate = (e) => {
        e.preventDefault();
        api.put(`/api/books/${editBook.id}`, form)
            .then(() => { setEditBook(null); setForm({ title: '', authorId: '' }); fetchBooks(); })
            .catch(() => alert('Güncelleme başarısız.'));
    };

    const handleDelete = (id) => {
        if (!window.confirm('Kitabı silmek istediğinizden emin misiniz?')) return;
        api.delete(`/api/books/${id}`).then(fetchBooks).catch(() => alert('Silme başarısız.'));
    };

    const loginTime = localStorage.getItem('loginTime');

    if (!data) return <p style={styles.loading}>Yükleniyor...</p>;

    return (
        <div>
            <Navbar />
            <div style={styles.wrapper}>
                <PrayerTimes />
                <div style={styles.container}>
                    <div style={styles.header}>
                        <h2 style={styles.title}>Kitaplar</h2>
                        <div style={styles.headerRight}>
                            <span style={styles.loginTime}>Giriş: {loginTime}</span>
                            <button style={styles.addBtn} onClick={() => { setShowAdd(true); setEditBook(null); setForm({ title: '', authorId: '' }); }}>+ Kitap Ekle</button>
                        </div>
                    </div>

                    {(showAdd || editBook) && (
                        <form onSubmit={editBook ? handleUpdate : handleAdd} style={styles.form}>
                            <input style={styles.input} placeholder="Kitap Adı" value={form.title}
                                onChange={e => setForm({ ...form, title: e.target.value })} required />
                            <select style={styles.input} value={form.authorId}
                                onChange={e => setForm({ ...form, authorId: e.target.value })} required>
                                <option value="">-- Yazar Seç --</option>
                                {authors.map(a => (
                                    <option key={a.id} value={a.id}>{a.name}</option>
                                ))}
                            </select>
                            <button style={styles.submitBtn} type="submit">{editBook ? 'Güncelle' : 'Ekle'}</button>
                            <button style={styles.cancelBtn} type="button" onClick={() => { setShowAdd(false); setEditBook(null); }}>İptal</button>
                        </form>
                    )}

                    <p style={styles.info}>Toplam {data.totalElements} kitap — Sayfa {data.page + 1} / {data.totalPages}</p>

                    <div style={{ minHeight: '520px', opacity: loading ? 0.4 : 1, transition: 'opacity 0.2s' }}>
                        <table style={styles.table}>
                            <colgroup>
                                <col style={{ width: '50px' }} />
                                <col style={{ width: '40%' }} />
                                <col style={{ width: '30%' }} />
                                <col style={{ width: '160px' }} />
                            </colgroup>
                            <thead>
                                <tr>
                                    <th style={{ ...styles.th, ...styles.thClickable }} onClick={() => handleSort('id')}>
                                        # {sortBy === 'id' && (sortDir === 'asc' ? '▲' : '▼')}
                                    </th>
                                    <th style={{ ...styles.th, ...styles.thClickable }} onClick={() => handleSort('title')}>
                                        Başlık {sortBy === 'title' && (sortDir === 'asc' ? '▲' : '▼')}
                                    </th>
                                    <th style={{ ...styles.th, ...styles.thClickable }} onClick={() => handleSort('author')}>
                                        Yazar {sortBy === 'author' && (sortDir === 'asc' ? '▲' : '▼')}
                                    </th>
                                    <th style={styles.th}>İşlemler</th>
                                </tr>
                            </thead>
                            <tbody>
                                {data.content.map(book => (
                                    <tr key={book.id} style={styles.tr}>
                                        <td style={styles.td}>{book.id}</td>
                                        <td style={styles.td}>{book.title}</td>
                                        <td style={styles.td}>{book.authorName}</td>
                                        <td style={styles.td}>
                                            <button style={styles.editBtn} onClick={() => { setEditBook(book); setShowAdd(false); setForm({ title: book.title, authorId: book.authorId }); }}>Düzenle</button>
                                            <button style={styles.deleteBtn} onClick={() => handleDelete(book.id)}>Sil</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    <div style={styles.pagination}>
                        <button style={styles.pageBtn} onClick={() => setPage(p => p - 1)} disabled={page === 0 || loading}>← Önceki</button>
                        <span style={styles.pageNum}>Sayfa {page + 1}</span>
                        <button style={styles.pageBtn} onClick={() => setPage(p => p + 1)} disabled={data.last || loading}>Sonraki →</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

const styles = {
    wrapper: { display: 'flex', gap: '24px', margin: '24px auto', padding: '0 20px', maxWidth: '1100px' },
    container: { flex: 1, minWidth: 0 },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' },
    headerRight: { display: 'flex', alignItems: 'center', gap: '16px' },
    title: { color: '#333', margin: 0 },
    loginTime: { color: '#888', fontSize: '12px' },
    addBtn: { padding: '8px 16px', backgroundColor: '#10b981', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '13px' },
    form: { display: 'flex', gap: '10px', marginBottom: '16px', flexWrap: 'wrap', backgroundColor: '#f9fafb', padding: '16px', borderRadius: '8px', border: '1px solid #e5e7eb' },
    input: { padding: '8px 12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px', flex: 1 },
    submitBtn: { padding: '8px 16px', backgroundColor: '#4f46e5', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    cancelBtn: { padding: '8px 16px', backgroundColor: '#6b7280', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    info: { color: '#666', marginBottom: '16px', fontSize: '14px' },
    table: { width: '100%', tableLayout: 'fixed', borderCollapse: 'collapse', backgroundColor: 'white', borderRadius: '8px', overflow: 'hidden', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' },
    th: { backgroundColor: '#4f46e5', color: 'white', padding: '12px 16px', textAlign: 'left' },
    thClickable: { cursor: 'pointer', userSelect: 'none' },
    tr: { borderBottom: '1px solid #eee' },
    td: { padding: '10px 16px', color: '#333', fontSize: '14px' },
    editBtn: { marginRight: '6px', padding: '4px 10px', backgroundColor: '#f59e0b', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    deleteBtn: { padding: '4px 10px', backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    pagination: { display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '16px', marginTop: '24px' },
    pageBtn: { padding: '8px 20px', backgroundColor: '#4f46e5', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer' },
    pageNum: { color: '#555', fontSize: '14px' },
    loading: { textAlign: 'center', marginTop: '100px', fontSize: '18px', color: '#888' },
};
