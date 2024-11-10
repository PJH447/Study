// import axios from "axios";
// import {deleteAccessToken, setAccessToken} from "./accessTokenReducer";
//
// export const login = (email, password) => {
//     return async (dispatch) => {
//         try {
//             const response = await axios.post('http://127.0.0.1:9003/api/auth/v1/login', {
//                 email: email,
//                 password: password,
//             }, {
//                 headers: {
//                     'Content-Type': 'application/json',
//                 },
//                 withCredentials: true,
//             });
//
//             console.log(response);
//             if (response.status === 200) {
//                 const accessToken = response.headers.authorization;
//                 axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
//                 dispatch(setAccessToken(accessToken));
//             } else {
//                 console.error('Login failed:', response.data);
//             }
//         } catch (error) {
//             console.error('Login error:', error);
//             dispatch(deleteAccessToken());
//         }
//     };
// };
//
//
// export const reissue = () => {
//     return async (dispatch) => {
//         await axios.post('http://127.0.0.1:9003/api/auth/v1/reissue', {}, {
//             headers: {
//                 'Content-Type': 'application/x-www-form-urlencoded',
//             },
//             withCredentials: true
//         }).then(response => {
//             if (response.status === 200) {
//                 const accessToken = response.headers.authorization;
//                 // axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
//                 // dispatch(setAccessToken(accessToken));
//                 localStorage.setItem('accessToken', accessToken);
//
//                 console.log("reissue token = " + accessToken);
//                 console.log("reissue ok");
//             }
//         }).catch(error => {
//             console.log(error);
//             return false;
//         });
//     };
// };
//
// export const logout = () => {
//     return async (dispatch, selector) => {
//         const accessToken = selector.accessTokenReducer.accessToken;
//         await axios.post('http://127.0.0.1:9003/api/auth/v1/logout', {}, {
//             headers: {
//                 'Content-Type': 'application/x-www-form-urlencoded',
//                 'Authorization': `Bearer ${accessToken}`,
//             }
//         }).then(response => {
//             console.log(response)
//             if (response.status === 200) {
//                 axios.defaults.headers.common['Authorization'] = `Bearer `;
//                 dispatch(deleteAccessToken());
//             }
//
//         }).catch(error => {
//             console.log(error);
//             return false;
//         });
//     }
// };

//