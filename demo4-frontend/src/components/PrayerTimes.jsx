import { useEffect, useState } from 'react';
import api from '../api/axiosConfig';

const CITIES = [
    'Adana','Adıyaman','Afyonkarahisar','Ağrı','Amasya','Ankara','Antalya','Artvin',
    'Aydın','Balıkesir','Bilecik','Bingöl','Bitlis','Bolu','Burdur','Bursa','Çanakkale',
    'Çankırı','Çorum','Denizli','Diyarbakır','Edirne','Elazığ','Erzincan','Erzurum',
    'Eskişehir','Gaziantep','Giresun','Gümüşhane','Hakkari','Hatay','Isparta','Mersin',
    'İstanbul','İzmir','Kars','Kastamonu','Kayseri','Kırklareli','Kırşehir','Kocaeli',
    'Konya','Kütahya','Malatya','Manisa','Kahramanmaraş','Mardin','Muğla','Muş',
    'Nevşehir','Niğde','Ordu','Rize','Sakarya','Samsun','Siirt','Sinop','Sivas',
    'Tekirdağ','Tokat','Trabzon','Tunceli','Şanlıurfa','Uşak','Van','Yozgat',
    'Zonguldak','Aksaray','Bayburt','Karaman','Kırıkkale','Batman','Şırnak','Bartın',
    'Ardahan','Iğdır','Yalova','Karabük','Kilis','Osmaniye','Düzce'
];

const PRAYER_LABELS = [
    { key: 'Fajr',    label: 'İmsak'  },
    { key: 'Sunrise', label: 'Güneş'  },
    { key: 'Dhuhr',   label: 'Öğle'   },
    { key: 'Asr',     label: 'İkindi' },
    { key: 'Maghrib', label: 'Akşam'  },
    { key: 'Isha',    label: 'Yatsı'  },
];

export default function PrayerTimes() {
    const [city, setCity]     = useState('');
    const [times, setTimes]   = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError]   = useState('');

    // IP'den şehri otomatik bul
    useEffect(() => {
        fetch('https://ipapi.co/json/')
            .then(r => r.json())
            .then(geo => {
                const ipCity = geo.city || '';
                // IP şehri listede varsa seç, yoksa İstanbul'u default al
                const match = CITIES.find(c =>
                    c.toLocaleLowerCase('tr') === ipCity.toLocaleLowerCase('tr')
                );
                setCity(match || 'İstanbul');
            })
            .catch(() => setCity('İstanbul'));
    }, []);

    // Şehir seçilince vakitleri çek
    useEffect(() => {
        if (!city) return;
        setLoading(true);
        setError('');
        api.get(`/api/prayer-times/${encodeURIComponent(city)}`)
            .then(res => setTimes(res.data?.data?.timings || null))
            .catch(() => setError('Vakitler alınamadı.'))
            .finally(() => setLoading(false));
    }, [city]);

    return (
        <div style={styles.panel}>
            <h3 style={styles.title}>Namaz Vakitleri</h3>

            <select
                style={styles.select}
                value={city}
                onChange={e => setCity(e.target.value)}>
                <option value="" disabled>Şehir seçin</option>
                {CITIES.map(c => (
                    <option key={c} value={c}>{c}</option>
                ))}
            </select>

            {loading && <p style={styles.info}>Yükleniyor...</p>}
            {error   && <p style={styles.error}>{error}</p>}

            {times && !loading && (
                <div style={styles.timesContainer}>
                    {PRAYER_LABELS.map(({ key, label }) => (
                        <div key={key} style={styles.row}>
                            <span style={styles.label}>{label}</span>
                            <span style={styles.time}>{times[key]}</span>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

const styles = {
    panel: {
        width: '200px',
        minWidth: '200px',
        backgroundColor: 'white',
        borderRadius: '8px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        padding: '16px',
        alignSelf: 'flex-start',
    },
    title:  { color: '#4f46e5', marginBottom: '12px', fontSize: '15px', margin: '0 0 12px 0' },
    select: { width: '100%', padding: '6px 8px', marginBottom: '12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '13px', color: '#333' },
    timesContainer: { marginTop: '4px' },
    row:   { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '7px 0', borderBottom: '1px solid #f0f0f0' },
    label: { fontSize: '13px', color: '#555' },
    time:  { fontSize: '14px', fontWeight: 'bold', color: '#4f46e5' },
    info:  { fontSize: '13px', color: '#888', textAlign: 'center', margin: '8px 0' },
    error: { fontSize: '13px', color: '#ef4444', textAlign: 'center', margin: '8px 0' },
};
