import React, { useState } from "react";
import { Navigate } from "react-router-dom";
import { accessTokenState, refreshTokenState } from "../store/auth";
import { userInfoState } from "../store/user";
import { useRecoilValue, useSetRecoilState } from "recoil";
import { api } from "../axios-instance/api";
import { Fragment } from "react";
import { scrapFolderListState } from "../store/sidebar-global-state";

const KakaoRedirectHandler = () => {
  const params = new URLSearchParams(window.location.search);
  const setAccessToken = useSetRecoilState(accessTokenState); // 액세스 토큰 세터를 Recoil로 부터 얻어온다
  const setRefreshToken = useSetRecoilState(refreshTokenState); // 리프레시 토큰 세터를 Recoil로 부터 얻어온다
  const setUserInfo = useSetRecoilState(userInfoState); // 유저 정보 세터를 Recoil로 부터 얻어온다

  setAccessToken(params.get("access-token"));
  setRefreshToken(params.get("refresh-token"));

  const [isLoading, setLoading] = useState(true);
  const initialize = async (accessToken) => {
    try {
      // set user info
      const r = await api.get("/user");
      const { userInfo } = r.data;
      setUserInfo(userInfo);
    } catch (error) {
      console.log(error);
    } finally {
      setLoading(false);
    }
  };
  initialize(useRecoilValue(accessTokenState));

  return <Fragment>{isLoading ? <div /> : <Navigate to={"/"} />}</Fragment>;
};

export default KakaoRedirectHandler;
