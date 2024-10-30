import React, {useEffect, useRef, useState} from "react";
import './signUp.css';
import axios from "axios";

function useInputLengthSlicer(inputRef, maxLength) {
    useEffect(() => {
        const input = inputRef.current;
        if (input) {
            input.addEventListener('input', (event) => {
                if (input.value.length > maxLength) {
                    input.value = input.value.slice(0, maxLength);
                }
            });
        }
    }, [inputRef]);
}


function SignUp() {

    const inputEmail = useRef(null);
    const inputPassword = useRef(null)
    const inputConfirmPassword = useRef(null)
    const inputNickname = useRef(null);
    const inputName = useRef(null);
    const inputPhone1 = useRef(null);
    const inputPhone2 = useRef(null);
    const inputPhone3 = useRef(null);

    useInputLengthSlicer(inputPhone1, 3);
    useInputLengthSlicer(inputPhone2, 4);
    useInputLengthSlicer(inputPhone3, 4);

    const handleSignUp = () => {
        const email = inputEmail.current.value;
        const password = inputPassword.current.value;
        const confirmPassword = inputConfirmPassword.current.value;
        const nickname = inputNickname.current.value;
        const name = inputName.current.value;
        const phone = `${inputPhone1.current.value}${inputPhone2.current.value}${inputPhone3.current.value}`;

        if (password !== confirmPassword) {
            window.alert("다름")
            return;
        }

        return axios.get('http://localhost:9003/v1/signUp', {
            params: {
                email: email,
                password: password,
                name: name,
                nickname: nickname,
                phone: phone,
            }
        }).then(response => {
            console.log(response);
            if (response.status === 200) {
                console.log("success");
            }

        }).catch(error => {

        });
    };

    return <>
        <form>
            <div>
                <span>email : </span>
                <input type={"email"} name={"email"} placeholder={"email"} ref={inputEmail}/>
            </div>
            <div>
                <span>password : </span>
                <input type={"password"} name={"password"} placeholder={"password"} ref={inputPassword}
                       autoComplete={"off"}/>
            </div>
            <div>
                <span>password confirm : </span>
                <input type={"password"} name={"confirmPassword"} placeholder={"password 확인"} ref={inputConfirmPassword}
                       autoComplete={"off"}/>
            </div>
            <div>
                <span>name : </span>
                <input type={"text"} name={"name"} placeholder={"name"} ref={inputName}/>
            </div>
            <div>
                <span>nickname : </span>
                <input type={"text"} name={"nickname"} placeholder={"nickname"} ref={inputNickname}/>
            </div>
            <div>
                <span>phone : </span>
                <input className={"input-phone"} type={"number"} name={"phone1"} placeholder={"010"} ref={inputPhone1}/>
                <span> - </span>
                <input className={"input-phone"} type={"number"} name={"phone2"} placeholder={""} ref={inputPhone2}/>
                <span> - </span>
                <input className={"input-phone"} type={"number"} name={"phone3"} placeholder={""} ref={inputPhone3}/>
            </div>
        </form>
        <button type={"button"} onClick={handleSignUp}>회원가입</button>
    </>;
}

export default SignUp;