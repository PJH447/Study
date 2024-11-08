export const SET = "accessTokenReducer/SET"
export const DEL = "accessTokenReducer/DEL"

const initialState = {
    accessToken: "",
};

export const setAccessToken = accessToken => ({type: SET, accessToken});
export const deleteAccessToken = accessToken => ({type: DEL});

const accessTokenReducer = (state = initialState, action) => {

    switch (action.type) {
        case SET:
            return {
                ...state,
                accessToken: action.accessToken,
            };

        case DEL:
            return {
                ...state,
                accessToken: "",
            };

        default: // 현재 상태를 그대로 반환
            return state;
    }

};

export default accessTokenReducer;