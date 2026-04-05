import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

export default function Books() {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(0);
    const [size] = useState(10);
    const navigate = useNavigate();

    useEffect(() => {
        setLoading(true);
        api.get(`/api/books/paged?page=${page}&size=${size}&sortBy=title`)
            .then(res => setData(res.data))
            .catch(() => {
                localStorage.removeItem('token');
                navigate('/login');
            })
            .finally(() => setLoading(false));
    }, [page, navigate, size]);

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    // İlk yüklemede ekran boşsa bekle
    if (!data) return <p style={styles.loading}>Yükleniyor...</p>;

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <h2 style={styles.title}>Kitaplar</h2>
                <button style={styles.logoutBtn} onClick={handleLogout}>Çıkış Yap</button>
            </div>

            <p style={styles.info}>
                Toplam {data.totalElements} kitap — Sayfa {data.page + 1} / {data.totalPages}
            </p>

            {/* minHeight: sayfa değişince tablonun yüksekliği sabit kalır, kayma olmaz */}
            <div style={{ minHeight: '520px', opacity: loading ? 0.4 : 1, transition: 'opacity 0.2s' }}>
                <table style={styles.table}>
                    <colgroup>
                        <col style={{ width: '60px' }} />   {/* # sütunu sabit */}
                        <col style={{ width: '55%' }} />    {/* Başlık */}
                        <col style={{ width: '45%' }} />    {/* Yazar */}
                    </colgroup>
                    <thead>
                        <tr>
                            <th style={styles.th}>#</th>
                            <th style={styles.th}>Başlık</th>
                            <th style={styles.th}>Yazar</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.content.map(book => (
                            <tr key={book.id} style={styles.tr}>
                                <td style={styles.td}>{book.id}</td>
                                <td style={styles.td}>{book.title}</td>
                                <td style={styles.td}>{book.author}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <div style={styles.pagination}>
                <button
                    style={styles.pageBtn}
                    onClick={() => setPage(p => p - 1)}
                    disabled={page === 0 || loading}>
                    ← Önceki
                </button>
                <span style={styles.pageNum}>Sayfa {page + 1}</span>
                <button
                    style={styles.pageBtn}
                    onClick={() => setPage(p => p + 1)}
                    disabled={data.last || loading}>
                    Sonraki →
                </button>
            </div>
        </div>
    );
}

const styles = {
    container: { maxWidth: '800px', margin: '40px auto', padding: '0 20px' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' },
    title: { color: '#333' },
    logoutBtn: { padding: '8px 16px', backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer' },
    info: { color: '#666', marginBottom: '16px', fontSize: '14px' },
    table: { width: '100%', tableLayout: 'fixed', borderCollapse: 'collapse', backgroundColor: 'white', borderRadius: '8px', overflow: 'hidden', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' },
    th: { backgroundColor: '#4f46e5', color: 'white', padding: '12px 16px', textAlign: 'left' },
    tr: { borderBottom: '1px solid #eee' },
    td: { padding: '12px 16px', color: '#333' },
    pagination: { display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '16px', marginTop: '24px' },
    pageBtn: { padding: '8px 20px', backgroundColor: '#4f46e5', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer' },
    pageNum: { color: '#555', fontSize: '14px' },
    loading: { textAlign: 'center', marginTop: '100px', fontSize: '18px', color: '#888' },
};
