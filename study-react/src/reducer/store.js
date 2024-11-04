import {applyMiddleware, combineReducers, createStore} from "redux";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage";
import reducer from "./reducer";
import accessTokenReducer from "./accessTokenReducer";
import {thunk} from "redux-thunk";
import loginCheckReducer from "./loginCheckReducer";

const persistConfig = {
    key: "root",
    storage,
    whitelist: ["reducer", "accessTokenReducer", "loginCheckReducer"],
};

export const rootReducer = combineReducers({
    reducer: reducer,
    accessTokenReducer: accessTokenReducer,
    loginCheckReducer: loginCheckReducer,

});

const persistedReducer = persistReducer(persistConfig, rootReducer);

const store = createStore(persistedReducer, applyMiddleware(thunk));
const persistor = persistStore(store);

export { store, persistor };
