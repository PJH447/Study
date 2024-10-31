import React, {useEffect, useRef, useState} from "react";
import './signUp.css';
import axios from "axios";
import {useDebounce, useInputLengthSlicer} from "../util";

function SignUp() {

    const [password, setPassword] = useState();
    const handleChangePassword = (e) => {
        setPassword(e.target.value);
    };

    const [confirmPassword, setConfirmPassword] = useState()
    const handleChangeConfirmPassword = (e) => {
        setConfirmPassword(e.target.value);
    };

    const submitBtnRef = useRef(null);


    useDebounce([password, confirmPassword],
        () => {

            if (!password) {
                return;
            }

            if (!confirmPassword) {
                return;
            }

            if (password !== confirmPassword) {
                submitBtnRef.current.disabled = true;
                submitBtnRef.current.classList.add('blind');
            } else {
                submitBtnRef.current.disabled = false;
                submitBtnRef.current.classList.remove('blind');
            }
        },
        500);


    const inputEmailRef = useRef(null);
    const inputPasswordRef = useRef(null)
    const inputConfirmPasswordRef = useRef(null)
    const inputNicknameRef = useRef(null);
    const inputNameRef = useRef(null);
    const inputPhoneRef1 = useRef(null);
    const inputPhoneRef2 = useRef(null);
    const inputPhoneRef3 = useRef(null);

    useInputLengthSlicer(inputPhoneRef1, 3);
    useInputLengthSlicer(inputPhoneRef2, 4);
    useInputLengthSlicer(inputPhoneRef3, 4);

    const handleSignUp = () => {
        const email = inputEmailRef.current.value;
        const password = inputPasswordRef.current.value;
        const confirmPassword = inputConfirmPasswordRef.current.value;
        const nickname = inputNicknameRef.current.value;
        const name = inputNameRef.current.value;
        const phone = `${inputPhoneRef1.current.value}${inputPhoneRef2.current.value}${inputPhoneRef3.current.value}`;

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
                <input type={"email"} name={"email"} placeholder={"email"} ref={inputEmailRef}/>
            </div>
            <div>
                <span>password : </span>
                <input type={"password"} name={"password"} placeholder={"password"}
                       autoComplete={"off"}
                       ref={inputPasswordRef}
                       onChange={handleChangePassword}
                />
            </div>
            <div>
                <span>password confirm : </span>
                <input type={"password"} name={"confirmPassword"} placeholder={"password 확인"}
                       autoComplete={"off"}
                       ref={inputConfirmPasswordRef}
                       onChange={handleChangeConfirmPassword}
                />
            </div>
            <div>
                <span>name : </span>
                <input type={"text"} name={"name"} placeholder={"name"} ref={inputNameRef}/>
            </div>
            <div>
                <span>nickname : </span>
                <input type={"text"} name={"nickname"} placeholder={"nickname"} ref={inputNicknameRef}/>
            </div>
            <div>
                <span>phone : </span>
                <input className={"input-phone"} type={"number"} name={"phone1"} placeholder={"010"}
                       ref={inputPhoneRef1}
                />
                <span> - </span>
                <input className={"input-phone"} type={"number"} name={"phone2"} placeholder={""}
                       ref={inputPhoneRef2}
                />
                <span> - </span>
                <input className={"input-phone"} type={"number"} name={"phone3"} placeholder={""}
                       ref={inputPhoneRef3}
                />
            </div>
        </form>
        <button className={'blind'} type={"button"} disabled={true}
                ref={submitBtnRef}
                onClick={handleSignUp}
        >회원가입</button>
    </>;
}

export default SignUp;