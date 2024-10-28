import {} from "react-redux";
import { createSlice } from '@reduxjs/toolkit';

const initialStateValue = {
    accessToken: '',
}
const accessToken = createSlice({
    name: 'accessToken',
    initialState:{value: initialStateValue},
    reducers:{
        setAccessToken: (state, action) => {
            console.log(state);
            console.log(action);
            console.log(action.payload);

            state.value = action.payload;
        },
        delAccessToken: state =>{
            state.value = initialStateValue;
        }
    }
});

export const {setAccessToken, delAccessToken} = accessToken.actions;
export default accessToken.reducer;