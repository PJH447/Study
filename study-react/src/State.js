import { configureStore } from '@reduxjs/toolkit';
import setAccessToken from './ClassA';

export default configureStore({
    reducer: {
        accessToken: setAccessToken,
    },
});