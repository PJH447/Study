import React, {useState} from 'react';
import {useDispatch, useSelector} from "react-redux";

function DisplayComponent() {
    const [isHidden, setIsHidden] = useState(true);

    const toggleVisibility = () => {
        setIsHidden(!isHidden);
    };

    const [color, setColor] = useState('red');

    const toggleColor = () => {
        if (color === 'red') {
            setColor('blue');
        } else {
            setColor('red');
        }
    };

    const dispatch = useDispatch();

    const {count} = useSelector(state => ({
        count: state.reducer.counter,
    }));
    console.log(count);

    const {accessToken} = useSelector(state => ({
        accessToken: state.accessTokenReducer.accessToken,
    }));

    console.log(accessToken);

    const [names, setNames] = useState([
        { id: 1, text: "눈사람" },
        { id: 2, text: "얼음" },
        { id: 3, text: "눈" },
        { id: 4, text: "바람" },
    ]);

    return (
        <>
            <button onClick={toggleVisibility}>visible button</button>
            {isHidden ? <p>target</p> : null}
            <button onClick={toggleColor}>color button</button>
            <p style={{color: color}}>target2</p>
            <br/>
            <div>
                {names.map(name => <div>{name.id} hi {name.text}</div>)}
                {/*{count}*/}
            </div>
        </>
    );
}

export default DisplayComponent;