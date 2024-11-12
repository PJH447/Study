import React, {useRef, useState} from "react";
import SockJS from 'sockjs-client';
import {Stomp} from "@stomp/stompjs";

function Chat() {

    const [message, setMessage] = useState();
    const text = useRef(null);
    const client = useRef(null);

    const connectHandler = () => {

        if (client.current) {
            console.log('already connected')
            return;
        }

        client.current = Stomp.over(() => {
            const sock = new SockJS('http://localhost:9003/webSocket')
            return sock;
        });

        client.current.connect(
            {
                // Authorization: token
            },
            () => {
                client.current.subscribe(
                    `/topic/1`,
                    (newMessage) => {
                        // setMessage(JSON.parse(message.body));
                        // console.log('message', newMessage);
                        // console.log('message.body', newMessage.body);
                        // console.log('JSON.parse(message.body)', JSON.parse(newMessage.body));
                        setMessage(JSON.parse(newMessage.body));
                    },
                    {
                        // header
                    }
                );
            }
        );
    }

    const sendHandler = () => {
        const current = text.current;
        client.current.send(
            "/send/1",
            {
                // header
            },
            JSON.stringify({
                roomId:1,
                senderEmail:'email@naver.com',
                type: "TALK",
                sender: 'user.name',
                message: current.value
            })
        );
    };

    const getMessage = () => {
        console.log(message);
    };

    return <>
        <button type={"button"} onClick={connectHandler}>소켓 연결</button>
        <input type={"text"} ref={text}/>
        <button type={"button"} onClick={sendHandler}>송신</button>
        <button type={"button"} onClick={getMessage}>메세지 출력</button>
    </>;
}

export default Chat;