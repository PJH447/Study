import './App.css';
import ClassA from "./pages/ClassA";
import DisplayComponent from "./DisplayComponent";
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Login from "./component/Login";
import Header from "./component/Header";
import Navigation from "./component/Navigation";
import SignUp from "./component/SignUp";
import Chat from "./component/Chat";
import ChatList from "./component/ChatList";

function App() {
    return (
        <div className="App">
            <Router>
                <Header/>
                <section className="App-section">
                    <Routes>
                        <Route path='/class-example' element={<ClassA/>}/>
                        <Route path='/display-example' element={<DisplayComponent/>}/>
                        <Route path='/login' element={<Login/>}/>
                        <Route path='/signUp' element={<SignUp/>}/>
                        <Route path='/chat' element={<Chat/>}/>
                        <Route path='/chat-list' element={<ChatList/>}/>
                    </Routes>
                </section>
                <Navigation/>
            </Router>
        </div>
    );
}

export default App;
