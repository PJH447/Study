import logo from './logo.svg';
import './App.css';
import ClassA from "./ClassA";
import DisplayComponent from "./DisplayComponent";
import {BrowserRouter as Router, Link, Route, Routes} from 'react-router-dom';
import Login from "./component/Login";
import {getLocalStorage} from "./util";
import {logout} from "./auth/auth";
import Navigation from "./component/Navigation";
import SignUp from "./component/SignUp";

function App() {
    const accessToken = getLocalStorage("accessToken")
    const hasAccessToken = accessToken !== null;
    const loginBtnBlindClass = hasAccessToken ? '' : '';
    const logoutBtnBlindClass = hasAccessToken ? '' : '';
    const handleLogout = () => {
        logout()
            .then(() => {
                alert('로그아웃 되었습니다.');
                window.location.href = '/';
            });
    };
    return (
        <div className="App">
            <Router>
                <header className="App-header">
                    <p>
                        Edit <code>src/App.js</code> and save to reload.
                    </p>
                    <div className={"app-head"}>
                        <span className={loginBtnBlindClass}><Link to='/login'>로그인</Link></span>
                        <span className={logoutBtnBlindClass} onClick={handleLogout}>로그아웃</span>
                    </div>
                </header>
                <section className="App-section">
                    <Routes>
                        <Route path='/hi' element={<ClassA/>}/>
                        <Route path='/hi2' element={<DisplayComponent/>}/>
                        <Route path='/login' className={"blindClass"} element={<Login/>}/>
                        <Route path='/signUp' className={"blindClass"} element={<SignUp/>}/>
                    </Routes>
                </section>
                <Navigation/>
            </Router>
        </div>
    );
}

export default App;
