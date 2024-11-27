import React, {useEffect, useRef, useState} from "react";
import SockJS from 'sockjs-client';
import {Stomp} from "@stomp/stompjs";
import {authenticatedApi} from "../auth/axiosIntercepter";
import './chat.css';
import {useSelector} from "react-redux";

function Chat() {
    const {isLogin, nickname, userId, email} = useSelector(state => state.loginCheckReducer);
    const [targetUserId, setTargetUserId] = useState(userId);
    const [oldMessage, setOldMessage] = useState([]);
    const [message, setMessage] = useState([]);
    const text = useRef(null);
    const client = useRef(null);

    useEffect(() => {
        authenticatedApi.get('/api/v1/chat',
            {
                params: {
                    targetUserId: targetUserId,
                }
            })
            .then(response => {
                console.log(response.data.data.content)
                if (response.status === 200) {
                    console.log('success');
                    setOldMessage(response.data.data.content);
                }

            })
            .catch(error => {
                console.log(error);
            });
    }, []);

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
    };

    useEffect(() => {
        connectHandler();
    }, []);

    const sendHandler = () => {

        if (!client.current?.connected) {
            console.log('disconnected')
            return;
        }

        const inputValue = text.current.value;
        if (inputValue.trim() === '') {
            return;
        }

        client.current.send(
            `/send/talk.${targetUserId}`,
            {
                // header
            },
            JSON.stringify({
                senderEmail: email,
                message: inputValue
            })
        );
        text.current.value = '';
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

    function getClassNames(senderId, isNotice) {
        if (isNotice) {
            return 'notice-chat'
        }
        return senderId === userId ? 'my-chat' : 'other-user-chat';
    }

    const pressEnter = (e)=>{
        e.stopPropagation();

        if (e.key === 'Enter') {

            if (e.nativeEvent.isComposing) {
                return;
            }
            sendHandler();
        }
    }

    return <>
        <div className={'chat-box'}>
            {oldMessage.map(
                m =>
                    <div className={`chat-log ${getClassNames(m.senderId, m.isNotice)}`} data-chat-id={m.chatId}>
                        <span>{m.senderNickname}</span>
                        <span>{m.message}</span>
                    </div>
            )}
            {message.map(
                m =>
                    <div className={`chat-log ${getClassNames(m.senderId, m.isNotice)}`} data-chat-id={m.chatId}>
                        <span>{m.senderNickname}</span>
                        <span>{m.message}</span>
                    </div>
            )}
        </div>
        <input type={"text"} ref={text} onKeyDown={pressEnter}/>
        <button type={"button"} onClick={sendHandler}>송신</button>
        <button type={"button"} onClick={exitHandler}>나가기</button>
    </>;
}

export default Chat;