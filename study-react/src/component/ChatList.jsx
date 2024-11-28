import React, {useEffect, useRef, useState} from "react";
import {authenticatedApi} from "../auth/axiosIntercepter";

function ChatList() {
    const [chatList, setChatList] = useState([]);

    useEffect(() => {

        authenticatedApi.get('/api/v1/chat/list',
            {
                params: {
                    size: 10,
                }
            })
            .then(response => {
                console.log(response.data.data);
                if (response.status === 200) {
                    console.log('success');
                    setChatList(response.data.data);
                }
            })
            .catch(error => {
                console.log(error);
            });

    }, []);

    return <>
        {chatList && chatList.map(
            chat =>
                <a href={`/chat-test?targetUserId=${chat.userId}`}>
                    <div>
                        <span>{chat.userNickname}</span> / <span>{chat.message}</span>
                    </div>
                </a>
        )}
    </>;
}

export default ChatList;