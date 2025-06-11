import React, { useEffect, useRef, useState, useCallback } from "react";
import SockJS from 'sockjs-client';
import { Stomp } from "@stomp/stompjs";
import { authenticatedApi } from "../auth/axiosIntercepter";
import './chat.css';
import { useSelector } from "react-redux";
import { useLocation } from "react-router-dom";

// Constants
const API_ENDPOINTS = {
  CHAT: '/api/v1/chat',
  SOCKET: '/api/auth/v1/socket',
  WEBSOCKET_URL: 'http://localhost:9003/webSocket'
};

const STOMP_ENDPOINTS = {
  SUBSCRIBE: (targetUserId) => `/exchange/chat.exchange/*.user.${targetUserId}`,
  ENTER: (targetUserId) => `/send/enter.${targetUserId}`,
  TALK: (targetUserId) => `/send/talk.${targetUserId}`,
  EXIT: (targetUserId) => `/send/exit.${targetUserId}`
};

// Message component for better readability
const ChatMessage = ({ message, getClassNames, userId }) => (
  <div className={`chat-log ${getClassNames(message.senderId, message.isNotice)}`} data-chat-id={message.chatId}>
    <span>{message.senderNickname}</span>
    <span>{message.message}</span>
  </div>
);

function Chat() {
  const { userId, email } = useSelector(state => state.loginCheckReducer);
  const [oldMessage, setOldMessage] = useState([]);
  const [message, setMessage] = useState([]);
  const text = useRef(null);
  const client = useRef(null);
  const messageEndRef = useRef(null);
  const [pageNumber, setPageNumber] = useState(1);

  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const targetUserIdParam = queryParams.get('targetUserId');
  const [targetUserId, setTargetUserId] = useState(targetUserIdParam || userId);

  // Scroll to bottom when messages change
  useEffect(() => {
    messageEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [message]);

  // Function to fetch chat messages
  const fetchChatMessages = useCallback(async (page = 0, append = false) => {
    try {
      const response = await authenticatedApi.get(API_ENDPOINTS.CHAT, {
        params: {
          targetUserId: targetUserId,
          page: page
        }
      });

      if (response.status === 200) {
        const newMessages = response.data.data.content;
        if (append) {
          setOldMessage(prevMessages => [...newMessages, ...prevMessages]);
          setPageNumber(prevPage => prevPage + 1);
        } else {
          setOldMessage(newMessages);
        }
      }
    } catch (error) {
      console.error('Error fetching chat messages:', error);
    }
  }, [targetUserId]);

  // Initial message load
  useEffect(() => {
    fetchChatMessages();
  }, [fetchChatMessages]);

  // Load more messages
  const readMore = useCallback(() => {
    fetchChatMessages(pageNumber, true);
  }, [fetchChatMessages, pageNumber]);

  // Connect to WebSocket
  const connectHandler = useCallback(() => {
    if (client.current?.connected) {
      return;
    }

    authenticatedApi.get(API_ENDPOINTS.SOCKET, {})
      .then(response => {
        const accessToken = response.headers.get("Authorization");

        client.current = Stomp.over(() => {
          return new SockJS(API_ENDPOINTS.WEBSOCKET_URL);
        });

        client.current.connect(
          {
            Authorization: `Bearer ${accessToken}`,
          },
          () => {
            // Subscribe to messages
            client.current.subscribe(
              STOMP_ENDPOINTS.SUBSCRIBE(targetUserId),
              (newMessage) => {
                setMessage(prevMessages => [...prevMessages, JSON.parse(newMessage.body)]);
              },
              {}
            );

            // Send enter message
            client.current.send(
              STOMP_ENDPOINTS.ENTER(targetUserId),
              {},
            );
          }
        );
      }).catch(error => {
        console.error('Error connecting to WebSocket:', error);
      });
  }, [targetUserId]);

  // Connect on component mount
  useEffect(() => {
    connectHandler();

    // Cleanup function to disconnect when component unmounts
    return () => {
      if (client.current?.connected) {
        exitHandler();
      }
    };
  }, [connectHandler]);

  // Send message
  const sendHandler = useCallback(() => {
    if (!client.current?.connected) {
      console.error('Cannot send message: disconnected');
      return;
    }

    const inputValue = text.current.value;
    if (inputValue.trim() === '') {
      return;
    }

    client.current.send(
      STOMP_ENDPOINTS.TALK(targetUserId),
      {},
      JSON.stringify({
        senderEmail: email,
        message: inputValue
      })
    );
    text.current.value = '';
  }, [targetUserId, email]);

  // Exit chat
  const exitHandler = useCallback(() => {
    if (!client.current?.connected) {
      return;
    }

    try {
      client.current.send(
        STOMP_ENDPOINTS.EXIT(targetUserId),
        {},
        JSON.stringify({})
      );
      client.current.deactivate();
    } catch (error) {
      console.error('Error during chat exit:', error);
    }
  }, [targetUserId]);

  // Get CSS class names for chat messages
  const getClassNames = useCallback((senderId, isNotice) => {
    if (isNotice) {
      return 'notice-chat';
    }
    return senderId === userId ? 'my-chat' : 'other-user-chat';
  }, [userId]);

  // Handle Enter key press
  const pressEnter = useCallback((e) => {
    e.stopPropagation();

    if (e.key === 'Enter' && !e.nativeEvent.isComposing) {
      sendHandler();
    }
  }, [sendHandler]);

  return (
    <>
      <div className="chat-box">
        {oldMessage.map((m, index) => (
          <ChatMessage 
            key={`old-${m.chatId || index}`} 
            message={m} 
            getClassNames={getClassNames} 
            userId={userId} 
          />
        ))}
        {message.map((m, index) => (
          <ChatMessage 
            key={`new-${m.chatId || index}`} 
            message={m} 
            getClassNames={getClassNames} 
            userId={userId} 
          />
        ))}
        <div ref={messageEndRef}></div>
      </div>
      <div className="input-util-box">
        <input type="text" ref={text} onKeyDown={pressEnter} />
        <button type="button" onClick={sendHandler}>송신</button>
        <button type="button" onClick={exitHandler}>나가기</button>
        <button type="button" onClick={readMore}>더보기</button>
      </div>
    </>
  );
}

export default Chat;
