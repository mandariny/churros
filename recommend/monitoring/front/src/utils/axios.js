import axios from 'axios';

const baseURL = "http://localhost:4444/database"

export const api = {
    success:(params) => axios.get(`${baseURL}/crawling`, {params}),
    dataset:() => axios.get(`${baseURL}/dataset`),
    error:(params) => axios.get(`${baseURL}/error`, {params})
}