import axios from "axios";
//  axios instances
// to api server
export const api = axios.create({
  baseURL: "https://churros.site/api",
});

api.interceptors.request.use((config) => {
  const accessToken = JSON.parse(
    localStorage.getItem("recoil-persist")
  )?.accessToken;
  if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`;
  else {
    console.log("accessToken not exists");
  }
  return config;
});
