// for accessing data
import axios from "axios";
import authHeader from "./auth-header";
const API_URL = "http://localhost:8080/api/test/";
const getPublicContent = () => {
    return axios.get(API_URL + "all");
}

const getUserBoard = () => {
    return axios.get(API_URL + "user", { header: authHeader() });
}

const getModeratorBoard = () => {
    return axios.get(API_URL + "mod", { header: authHeader() });
}

const getAdminBoard = () => {
    return axios.get(API_URL + "admin", { header: authHeader() });
}
const UserService = {
    getPublicContent,
    getUserBoard,
    getModeratorBoard,
    getAdminBoard,
}
export default UserService;