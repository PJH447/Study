import React, {useEffect, useRef, useState} from 'react';
import axios from "axios";
import {useDispatch, useSelector} from "react-redux";
import {deleteAccessToken, setAccessToken} from "./AccessTokenReducer";

function login(email, password) {
    return async (dispatch) => {
        try {
            const response = await axios.post('http://127.0.0.1:9003/api/auth/v1/login', {
                email: email,
                password: password,
            }, {
                headers: {
                    'Content-Type': 'application/json',
                },
                withCredentials: true,
            });

            if (response.status === 200) {
                const accessToken = response.headers.authorization;
                axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
                dispatch(setAccessToken(accessToken));
            } else {
                console.error('Login failed:', response.data);
            }
        } catch (error) {
            console.error('Login error:', error);
            dispatch(deleteAccessToken());
            // Dispatch an error action if needed for state management
        }
    };
}

function ClassA() {
    const {accessToken, counter} = useSelector(state => ({
        accessToken: state.accessTokenReducer.accessToken,
    }));


    console.log(counter);

    const dispatch = useDispatch();
    const inputName = useRef(null); // Use null for initial value
    const inputPassword = useRef(null);

    const doLogin = async () => {
        const email = inputName.current.value;
        const password = inputPassword.current.value;

        if (!email || !password) {
            console.error('Email and password are required');
            return;
        }

        await login(email, password)(dispatch);
    };

    const reissue = () => {
        axios.post('http://127.0.0.1:9003/api/auth/v1/reissue', {}, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            withCredentials: true
        }).then(response => {
            console.log(response);
            if (response.status === 200) {
            }
        }).catch(error => {
            console.log(error);
            return false;
        });
    };


    const logout = () => {
        axios.post('http://127.0.0.1:9003/api/auth/v1/logout', {}, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': `Bearer ${accessToken}`,
            }
        }).then(response => {
            console.log(response)
            if (response.status === 200) {
                axios.defaults.headers.common['Authorization'] = `Bearer `;
                dispatch(deleteAccessToken());
            }

        }).catch(error => {
            console.log(error);
            return false;
        });
    };

    const getTest = () => {
        axios.get('http://127.0.0.1:9003/api/test/v2', {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => {
            console.log(response)

        }).catch(error => {
            console.log(error);
            return false;
        });
    };

    const [items, setItems] = useState([])

    const getTest2 = () => {
        setItems([...items, "isNewItem"]);
    };

    const getTest3 = () => {
        setItems(items.slice(0, -1));
    };

    const resetAccessToken = () => {
        dispatch(setAccessToken(""));
    }

    return <>
        <form>
            <input type={"text"} name={"email"} placeholder={"email"} ref={inputName}/><br/>
            <input type={"password"} name={"password"} placeholder={"password"} ref={inputPassword}
                   autoComplete={"off"}/>
        </form>
        <div onClick={doLogin}>is login</div>
        <div onClick={reissue}>is reissue</div>
        <div onClick={logout}>is logout</div>
        <div onClick={getTest}>is ClassA2</div>
        <div onClick={getTest2}>add el</div>
        <div onClick={getTest3}>del el</div>
        <div onClick={resetAccessToken}>resetAccessToken</div>
        <a href="/hi2">hi2</a>
    </>;
}

export default ClassA;