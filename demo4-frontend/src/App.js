import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Books from './pages/Books';
import Authors from './pages/Authors';
import Categories from './pages/Categories';
import Users from './pages/Users';

function PrivateRoute({ children }) {
    const token = localStorage.getItem('token');
    return token ? children : <Navigate to="/login" />;
}

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/dashboard" element={<PrivateRoute><Books /></PrivateRoute>} />
                <Route path="/authors" element={<PrivateRoute><Authors /></PrivateRoute>} />
                <Route path="/categories" element={<PrivateRoute><Categories /></PrivateRoute>} />
                <Route path="/users" element={<PrivateRoute><Users /></PrivateRoute>} />
                <Route path="/" element={<Navigate to="/login" />} />
            </Routes>
        </BrowserRouter>
    );
}

