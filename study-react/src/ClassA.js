import React, {useEffect, useRef, useState} from 'react';
import axios from "axios";

function login(inputName, inputPassword) {

    return () => {
        axios.post('http://127.0.0.1:9003/api/auth/v1/login', {
            // "email": "test@naver.com",
            // "password": "password"
            "email": inputName.current.value,
            "password": inputPassword.current.value
        }, {
            headers: {
                'Content-Type': 'application/json'
            },
            withCredentials: true
        }).then(response => {
            console.log(response);
            if (response.status === 200) {
                const authorizationHeader = response.headers.authorization;
                axios.defaults.headers.common[
                    'Authorization'
                    ] = `Bearer ${authorizationHeader}`;
            }

        }).catch(error => {
            console.log(error);
            return false;
        });
    };
}

function ClassA() {

    const inputName = useRef();
    const inputPassword = useRef();

    const doLogin = login(inputName, inputPassword);

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
            }
        }).then(response => {
            console.log(response)
            if (response.status === 200) {
                axios.defaults.headers.common[
                    'Authorization'
                    ] = `Bearer `;
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
        <a href="/hi2">hi2</a>
    </>;
}

export default ClassA;