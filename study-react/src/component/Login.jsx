import React, {useRef} from "react";
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
    return <>
        <form>
            <input type={"email"} name={"email"} placeholder={"email"} ref={inputEmail}/><br/>
            <input type={"password"} name={"password"} placeholder={"password"} ref={inputPassword} autoComplete={"off"}/>
        </form>
        <button type={"button"} onClick={handleLogin}>로그인</button>
    </>;

}

export default Login;