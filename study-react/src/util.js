export const setLocalStorage = (key, value, expirationDate) =>{
    let item = {
        value: value,
        expirationDate: expirationDate,
    }
    localStorage.setItem(key, JSON.stringify(item));
}

export const getLocalStorage = (key) =>{
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