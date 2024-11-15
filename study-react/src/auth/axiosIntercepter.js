import axios from "axios";
import {reissue} from "./auth";

export const onceApi = axios.create({
    baseURL: 'http://localhost:9003',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true,
})

export const authenticatedApi = axios.create({
    baseURL: 'http://localhost:9003',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true,
});

authenticatedApi.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const originalRequest = error.config;
        console.log(originalRequest);

        if (error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                // 토큰 재발급 요청
                const response = await reissue();
                return authenticatedApi(originalRequest);
            } catch (reissueError) {
                window.location.href = '/login';
                return Promise.reject(reissueError);
            }
        }

        return Promise.reject(error);
    }
);