import React, {useState} from 'react';

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

    return (
        <>
            <button onClick={toggleVisibility}>visible button</button>
            {isHidden ? <p>target</p> : null}
            <button onClick={toggleColor}>color button</button>
            <p style={{color: color}}>target2</p>
        </>
    );
}

export default DisplayComponent;