// reducer.js
const initialState = {
    counter: 20,
};
export const ADD = "COUNT/ADD"
export const increaseCount = counter => ({type: ADD, counter});

const reducer = (state = initialState, action) => { //현재의 상태, 액션을 받아온다.

    switch (action.type) {
        case ADD: // 이 타입일때 리턴이 실행된다.
            return { // 새로운 상태로 반환한다. // 현재상태를 복사하고 payload 값으로 업데이트한 새로운 상태를 반환한다.
                ...state,
                count: action.counter,
            };
        default: // 현재 상태를 그대로 반환
            return state;
    }
};

export default reducer;