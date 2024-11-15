import React, {useRef, useState} from "react";
import SockJS from 'sockjs-client';
import {Stomp} from "@stomp/stompjs";
import {authenticatedApi} from "../auth/axiosIntercepter";
import './chat.css';
import {useSelector} from "react-redux";

function Chat() {
    const {isLogin, nickname, userId, email} = useSelector(state => state.loginCheckReducer);
    const [targetUserId, setTargetUserId] = useState(userId);
    const [message, setMessage] = useState([]);
    const text = useRef(null);
    const client = useRef(null);

    const [chatLog, setChatLog] = useState([]);

    const connectHandler = () => {

        if (client.current?.connected) {
            console.log('already connected')
            return;
        }

        authenticatedApi.get('/api/auth/v1/socket', {})
            .then(response => {
                const accessToken = response.headers.get("Authorization");
                client.current = Stomp.over(() => {
                    const sock = new SockJS('http://localhost:9003/webSocket')
                    return sock;
                });
                client.current.connect(
                    {
                        Authorization: `Bearer ${accessToken}`,
                    },
                    () => {
                        client.current.subscribe(
                            `/exchange/chat.exchange/*.user.${targetUserId}`,
                            // `/queue/chat.queue`,
                            (newMessage) => {
                                setMessage(preMessages => [...preMessages, JSON.parse(newMessage.body)]);
                            },
                            {
                                // header
                            }
                        );

                        client.current.send(
                            `/send/enter.${targetUserId}`,
                            {
                                // header
                            },
                        );
                    }
                );
            }).catch(error => {
            console.log(error);

        });
    }

    const sendHandler = () => {

        if (!client.current?.connected) {
            console.log('disconnected')
            return;
        }

        client.current.send(
            `/send/talk.${targetUserId}`,
            {
                // header
            },
            JSON.stringify({
                senderEmail: email,
                message: text.current.value
            })
        );
    };

    const exitHandler = () => {

        if (!client.current?.connected) {
            console.log('disconnected')
            return;
        }

        client.current.send(
            `/send/exit.${targetUserId}`,
            {
                // header
            },
            JSON.stringify({

            }),
            () =>{
                console.log("Exit message sent");
            }
        );
        client.current?.deactivate();
    };

    const getMessage = () => {
        console.log(message);
    };

    function getClassNames(message) {
        return message === 'hi' ? 'blind' : '';
    }

    return <>
        <button type={"button"} onClick={connectHandler}>소켓 연결</button>
        <div>
            {message.map(
                m => <div
                    className={`chat-log ${getClassNames(m.message)}`}>{m.chatId} / {m.senderNickname} / {m.message} / {m.createdAt}</div>
            )}
        </div>
        <input type={"text"} ref={text}/>
        <button type={"button"} onClick={sendHandler}>송신</button>
        <button type={"button"} onClick={exitHandler}>나가기</button>
    </>;
}

export default Chat;