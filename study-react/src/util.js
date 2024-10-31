import {useEffect} from "react";

export const setLocalStorage = (key, value, expirationDate) => {
    let item = {
        value: value,
        expirationDate: expirationDate,
    }
    localStorage.setItem(key, JSON.stringify(item));
}

export const getLocalStorage = (key) => {
    const item = JSON.parse(localStorage.getItem(key));
    if (item === null) {
        return null;
    }

    const now = new Date();
    if (now.getTime() > item.expirationDate) {
        localStorage.removeItem(key);
        return null;
    }
    return item.value;
}

export const useInputLengthSlicer = (inputRef, maxLength) => {
    useEffect(() => {
        if (!inputRef?.current || typeof maxLength !== 'number') {
            return;
        }

        const input = inputRef.current;
        const limitInputLength = ({target}) => {
            const currentValue = target.value;

            if (currentValue.length > maxLength) {
                target.value = currentValue.slice(0, maxLength);
            }
        };
        input.addEventListener('input', limitInputLength);
        return () => {
            input.removeEventListener('input', limitInputLength);
        };
    }, [inputRef, maxLength]);
}


export function useDebounce(detectTarget, doAction, waitTime) {
    useEffect(() => {
        const timeout = setTimeout(() => {
            doAction();
        }, waitTime);
        return () => clearTimeout(timeout);
    }, [detectTarget]);
}
