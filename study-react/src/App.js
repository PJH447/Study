import logo from './logo.svg';
import './App.css';
import ClassA from "./ClassA";
import DisplayComponent from "./DisplayComponent";
import {BrowserRouter as Router, Link, Route, Routes} from 'react-router-dom';

function App() {
    return (
        <div className="App">
            <Router>
            <header className="App-header">
                {/*<img src={logo} className="App-logo" alt="logo"/>*/}
                <p>
                    Edit <code>src/App.js</code> and save to reload.
                </p>
                {/*<a*/}
                {/*    className="App-link"*/}
                {/*    href="https://reactjs.org"*/}
                {/*    target="_blank"*/}
                {/*    rel="noopener noreferrer"*/}
                {/*>*/}
                {/*    Learn React*/}
                {/*</a>*/}
                <div> hi</div>
                <Link to='/hi'><li>hi</li></Link>
                <Link to='/hi2'><li>hi2</li></Link>

            </header>
            <section className="App-section">
                <Routes>
                    <Route path='/hi' element={<ClassA/>} />
                    <Route path='/hi2' element={<DisplayComponent/>} />
                </Routes>
                {/*<ClassA/>*/}
                {/*<DisplayComponent/>*/}
            </section>
            </Router>
        </div>
    );
}

export default App;
