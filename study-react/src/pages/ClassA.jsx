import React, {useEffect, useRef, useState} from 'react';
import {login, logout, reissue} from "../auth/auth";
import {authenticatedApi} from "../auth/axiosIntercepter";
import {useSelector} from "react-redux";
import '../styles/pages/ClassA.css';

function ClassA() {

    const {isLogin, nickname} = useSelector(state => state.loginCheckReducer);
    console.log(isLogin, nickname);

    const inputName = useRef(null); // Use null for initial value
    const inputPassword = useRef(null);

    const handleLogin = () => {
        const email = inputName.current.value;
        const password = inputPassword.current.value;

        if (!email || !password) {
            console.error('Email and password are required');
            return;
        }

        login(email, password)
    };

    const handleReissue = () => {
        reissue();
    }

    const handleLogout = () => {
        logout();
    };

    // const getTest = async () => {
    //     try {
    //         const response = await axios.get('http://127.0.0.1:9003/api/test/v2', {
    //             headers: {
    //                 'Content-Type': 'application/json',
    //             },
    //             withCredentials: true,
    //         });
    //
    //         if (response.status === 200) {
    //             console.log("success");
    //         }
    //
    //     } catch (error) {
    //         console.log("Error:", error);
    //     }
    // };

    const getTest = async () => {
        return authenticatedApi.get('http://localhost:9003/api/test/v2', {
                headers: {
                    'Content-Type': 'application/json',
                },
                withCredentials: true,
            })
            .then(response => {
                if (response.status === 200) {
                    console.log("success");
                }
            }).catch(error=>{
                console.log("Error:", error);
            })
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
        <div onClick={handleLogin}>is login</div>
        <div onClick={handleReissue}>is reissue</div>
        <div onClick={handleLogout}>is logout</div>
        <div onClick={getTest}>is ClassA2</div>
        <div onClick={getTest2}>add el</div>
        <div onClick={getTest3}>del el</div>
        <a href="/hi2">hi2</a>
    </>;
}

export default ClassA;