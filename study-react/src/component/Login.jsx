import React, {useEffect, useRef, useState} from "react";
import {login} from "../auth/auth";


function Login() {
    const inputEmail = useRef(null); // Use null for initial value
    const inputPassword = useRef(null);

    const handleLogin = async () => {
        const email = inputEmail.current.value;
        const password = inputPassword.current.value;

        if (!email || !password) {
            alert('이메일과 비밀번호를 입력해주세요.');
            return;
        }

        login(email, password)
            .then(() => {
                window.location.href = '/';
            });
    };

    let webSocket = null;
    useEffect(() => {
        if (webSocket == null) {
            webSocket = new WebSocket('ws://localhost:9003/webSocket');
            webSocket.onopen = function (e) {
                console.log('open now');
            };
        }
    }, []);


    const closeSocket = () => {
        webSocket.close()
    };

    const sendMessage = () => {
        webSocket.send("send Message~~");
    };

    return <>
        <form>
            <input type={"email"} name={"email"} placeholder={"email"} ref={inputEmail}/><br/>
            <input type={"password"} name={"password"} placeholder={"password"} ref={inputPassword} autoComplete={"off"}/>
        </form>
        <button type={"button"} onClick={handleLogin}>로그인</button>
        <button type={"button"} onClick={closeSocket}>소켓 종료</button>
        <button type={"button"} onClick={sendMessage}>메세지 보내기</button>
    </>;

}

export default Login;