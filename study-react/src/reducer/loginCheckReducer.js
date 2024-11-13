const SET_INFO = "loginCheckReducer/SET";
const DEL_INFO = "loginCheckReducer/DEL";

const initialState = {
    isLogin: false,
    nickname: "",
    userId: null,
    email: "",
};

export const setLoginInfo = userInfoForm => ({type: SET_INFO, userInfoForm});
export const delLoginInfo = accessToken => ({type: DEL_INFO});

const loginCheckReducer = (state = initialState, action) => {
    switch (action.type) {
        case SET_INFO:
            return {
                ...state,
                isLogin: true,
                userId: action.userInfoForm.userId,
                nickname: action.userInfoForm.nickname,
                email: action.userInfoForm.email,
            };

        case DEL_INFO:
            return {
                ...state,
                isLogin: false,
                userId: null,
                nickname: "",
                email: "",
            };

        default: // 현재 상태를 그대로 반환
            return state;
    }

};

export default loginCheckReducer;