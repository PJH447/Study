import React, { useState } from 'react';
import axios from "axios";

function ClassA() {
    const [message, setMessage] = useState('hihi');

    const event = () => {
        console.log('set new Message ');
        setMessage('new Message');
    };

    const login = () => {
        axios.post('http://127.0.0.1:9003/api/auth/v1/login', {
            "email": "test@naver.com",
            "password": "password"
        },{
            headers:{
                'Content-Type': 'application/json'
            },
            withCredentials: true
        }).then(response => {

            const authorizationHeader = response.headers.authorization;
            axios.defaults.headers.common[
                'Authorization'
                ] = `Bearer ${authorizationHeader}`;

        }).catch(error => {
            console.log(error);
            return false;
        });
    };

    const reissue = () => {
        console.log(message);
        axios.post('http://127.0.0.1:9003/api/auth/v1/reissue', {
        },{
            headers:{
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            withCredentials: true
        }).then(response => {
            console.log(response)
        }).catch(error => {
            console.log(error);
            return false;
        });
    };

    const logout = () => {
        console.log(message);
        axios.post('http://127.0.0.1:9003/api/auth/v1/logout', {
        },{
            headers:{
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }).then(response => {
            console.log(response)

            axios.defaults.headers.common[
                'Authorization'
                ] = `Bearer `;

        }).catch(error => {
            console.log(error);
            return false;
        });
    };

    const getTest = () => {
        axios.get('http://127.0.0.1:9003/api/test/v2',{
            headers:{
                'Content-Type': 'application/json'
            }
        }).then(response => {
            console.log(response)

        }).catch(error => {
            console.log(error);
            return false;
        });
    };

    return <>
        <div onClick={login}>is login</div>
        <div onClick={reissue}>is reissue</div>
        <div onClick={logout}>is logout</div>
        <div onClick={getTest}>is ClassA2</div>
    </>;
}

export default ClassA;