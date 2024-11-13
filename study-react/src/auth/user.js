import {delLoginInfo, setLoginInfo} from "../reducer/loginCheckReducer";
import {onceApi} from "./axiosIntercepter";

export const getUserInfo = () => {
    return (dispatch, setUserInfo) => onceApi.get('/api/user/v1/headerInfo',
        {})
        .then(response => {

            console.log(response);
            if (response.status === 200) {
                console.log('success');
                const userInfoForm = response.data.data;
                dispatch(setLoginInfo(userInfoForm));
                setUserInfo(userInfoForm);
                return userInfoForm;
            }
        })
        .catch(error => {
            dispatch(delLoginInfo());
            console.log("Error:", error);
        });

};