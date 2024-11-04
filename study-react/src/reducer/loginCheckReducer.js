const SET_INFO = "loginCheckReducer/SET";
const DEL_INFO = "loginCheckReducer/DEL";

const initialState = {
    isLogin: false,
    nickname: "",
};

export const setLoginInfo = userInfoForm => ({type: SET_INFO, userInfoForm});
export const delLoginInfo = accessToken => ({type: DEL_INFO});

const loginCheckReducer = (state = initialState, action) => {
    switch (action.type) {
        case SET_INFO:
            return {
                ...state,
                isLogin: true,
                nickname: action.userInfoForm.nickname,
            };

        case DEL_INFO:
            return {
                ...state,
                isLogin: false,
                nickname: "",
            };

        default: // 현재 상태를 그대로 반환
            return state;
    }

};

export default loginCheckReducer;