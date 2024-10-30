import axios from "axios";
import {getLocalStorage, setLocalStorage} from "../util";

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
                console.log('success');
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
                console.log('success')
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

    return axios.post('http://127.0.0.1:9003/api/auth/v1/logout', {},
        {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            withCredentials: true
        })
        .then(response => {
            console.log(response)
            if (response.status === 200) {
                console.log('success');
            }

        })
        .catch(error => {
            console.log(error);
            if (error.response && error.response.status === 401) {
                return reissue()
                    .then(() => logout());
            }
            return false;
        });
};