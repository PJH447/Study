import React, { useState } from 'react';
import axios from "axios";

function ClassA() {
    const [message, setMessage] = useState('hihi');

    const event = () => {
        console.log('set new Message ');
        setMessage('new Message');
    };

    const handleClick = () => {
        console.log(message);
        axios.get('http://127.0.0.1:9003/api/test/v1', {
        }).then(response => {
            console.log('then')
        }).catch(error => {
            console.log('error')
            console.log(error);
            return false;
        });
    };

    return <><div onClick={handleClick}>is ClassA</div>
    <div onClick={event}>is ClassA2</div></>;
}

export default ClassA;