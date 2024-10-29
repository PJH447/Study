import axios from "axios";
import {getLocalStorage, setLocalStorage} from "../util";

const setAccessToken = (accessToken) => {
    const expirationDate = new Date().getTime() + (7 * 24 * 60 * 60 * 1000);
    setLocalStorage('accessToken', accessToken, expirationDate);
};

export const login = (email, password) => {
    return axios.post('http://127.0.0.1:9003/api/auth/v1/login',
        {
            email: email,
            password: password,
        },
        {
            headers: {
                'Content-Type': 'application/json',
            },
            withCredentials: true,
        })
        .then(response => {
            console.log(response);
            if (response.status === 200) {
                const accessToken = response.headers.authorization;
                axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
                setAccessToken(accessToken);
            }
        })
        .catch(error => {
            console.error('Login error:', error);
        });
};


export const reissue = () => {
    return axios.post('http://127.0.0.1:9003/api/auth/v1/reissue', {},
        {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            withCredentials: true
        })
        .then(response => {
            if (response.status === 200) {
                const accessToken = response.headers.authorization;
                axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
                setAccessToken(accessToken);
            }
        })
        .catch(error => {
            console.log(error);
            if (error.response.status === 404) {
                window.alert('로그인이 필요합니다.')
                window.location.href = '/login';
            }
            return false;
        });
};

export const logout = () => {

    const accessToken = getLocalStorage("accessToken");
    if (accessToken === null) {
        window.location.href = '/';
        return;
    }

    return axios.post('http://127.0.0.1:9003/api/auth/v1/logout', {},
        {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': `Bearer ${accessToken}`,
            }
        })
        .then(response => {
            console.log(response)
            if (response.status === 200) {
                axios.defaults.headers.common['Authorization'] = `Bearer `;
                localStorage.removeItem('accessToken');
            }

        })
        .catch(error => {
            console.log(error);
            if (error.response && error.response.status === 401) {
                console.log("niiji");
                return reissue()
                    .then(() => logout());
            }
            return false;
        });
};