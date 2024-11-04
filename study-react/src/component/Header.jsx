import {Link} from "react-router-dom";
import {logout} from "../auth/auth";
import {useDispatch} from "react-redux";
import {getUserInfo} from "../auth/user";
import {useEffect, useRef, useState} from "react";

function AuthButton({userInfo, onLogout}) {
    if (userInfo) {
        return <span onClick={onLogout}>로그아웃</span>;
    }
    return <span><Link to='/login'>로그인</Link></span>;
};

function Header() {

    const [userInfo, setUserInfo] = useState("");
    const dispatch = useDispatch();
    useEffect(() => {
        getUserInfo()(dispatch, setUserInfo)
            .then(()=>{
                AuthButton(userInfo, handleLogout);
            });
    }, []);


    const handleLogout = () => {
        logout()
            .then(() => {
                alert('로그아웃 되었습니다.');
                window.location.href = '/';
            });
    };

    return <>
        <header className="App-header">
            <p>
                Edit <code>src/App.js</code> and save to reload.
            </p>
            <div className={"app-head"}>
                <AuthButton userInfo={userInfo} onLogout={handleLogout}/>
            </div>
        </header>
    </>;
}




export default Header;